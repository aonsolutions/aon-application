package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.PMS_RESERVATION_FINANCE_BATCH_PROCESS_END;
import static com.code.aon.ui.common.ICommonMessages.PMS_RESERVATION_FINANCE_BATCH_PROCESS_INFO;
import static com.code.aon.ui.common.ICommonMessages.PMS_RESERVATION_FINANCE_BATCH_PROCESS_START;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICommonConstants;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.AonFile;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.PayMethod;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;

public class ReservationFinanceBatchController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private AonFile reservationFile;
	private FinanceBatch financeBatch;
	private String batchDescription;
	private PayMethod payMethod;

	public AonFile getReservationFile() {
		return reservationFile;
	}
	public void setReservationFile(AonFile reservationFile) {
		this.reservationFile = reservationFile;
	}

	public FinanceBatch getFinanceBatch() {
		return financeBatch;
	}
	public void setFinanceBatch(FinanceBatch financeBatch) {
		this.financeBatch = financeBatch;
	}

	public String getBatchDescription() {
		return batchDescription;
	}
	public void setBatchDescription(String batchDescription) {
		this.batchDescription = batchDescription;
	}

	public PayMethod getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public void onEditSearch(ActionEvent event) throws ManagerBeanException {
		setReservationFile(null);
		setFinanceBatch(null);
		setBatchDescription(null);
		setPayMethod(null);
		try {
			ApplicationParameter payMethodParam = AppParamUtil.getParameter(AppParam.PMS_CONEXFLOW_PAY_METHOD);
			if (payMethodParam != null && StringUtils.isNotBlank(payMethodParam.getValue())) {
				setPayMethod((PayMethod)BeanManager.getManagerBean(PayMethod.class).get(Integer.parseInt(payMethodParam.getValue())));
			}
		} catch (ManagerBeanException ex) {
			setPayMethod(null);
		}
	}

	public void fileUploaded(UploadEvent event) {
		setReservationFile(AttachmentUtil.fileUploaded(event));
	}

	public void generateFinanceBatch(ActionEvent event) {
		generateFinanceBatch();
	}

	public void generateFinanceBatch() {
		LogPanelController log = LogPanelController.getInstance();
		log.reset();
		log.info(AonUtil.getMessage(PMS_RESERVATION_FINANCE_BATCH_PROCESS_START));

		try {
			List<String> reservationCodeList = importReservationFile();

			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			Criteria subCriteria = new Criteria();
			subCriteria.addInExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CODE), reservationCodeList);
			ProjectionList projectIdList = new ProjectionList(Projection.property(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_PROJECT_ID)));

			setFinanceBatch(null);
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_ID), getPayMethod().getId());
			criteria.addInExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_PROJECT_ID), ExpressionUtilities.getSubQueryExpression(ProjectReservation.class, subCriteria, projectIdList));
			for (ITransferObject ito : financeBean.getList(criteria)) {
				Finance finance = (Finance)ito;
				log.info(AonUtil.getMessage(PMS_RESERVATION_FINANCE_BATCH_PROCESS_INFO, finance.getReferenceCode(), finance.getInvoice().getProject().getId()));

				if (getFinanceBatch() == null) {
					setFinanceBatch(createFinanceBatch());
				}
				FinanceBatchDetail fBatchDetail = new FinanceBatchDetail();
				fBatchDetail.setFinance(finance);
				fBatchDetail.setFinanceBatch(getFinanceBatch());
				fBatchDetail.setAmount(finance.getTotalAmount());
				fBatchDetail.setStatus(FinanceStatus.BATCHED);
				fBatchDetail.setCreationUser(ICommonConstants.SYSTEM_USER);
				fBatchDetailBean.insert(fBatchDetail);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			log.info(AonUtil.getMessage(PMS_RESERVATION_FINANCE_BATCH_PROCESS_END));
			log.finish();
		}
	}

	private List<String> importReservationFile() throws IOException {
		List<String> reservationCodeList = new LinkedList<String>();

		LineNumberReader reader = new LineNumberReader(new InputStreamReader(getReservationFile().openStream()));
		String code = reader.readLine();
		while (code != null) {
			if (StringUtils.isNotBlank(code)) {
				reservationCodeList.add(code);
			}
			code = reader.readLine();
		}
		reader.close();

		return reservationCodeList;
	}

	private FinanceBatch createFinanceBatch() throws ManagerBeanException {
		RegistryBank rBank = null;
		ApplicationParameter autoFBatchBankParam = AppParamUtil.getParameter(AppParam.PMS_AUTO_FBATCH_BANK);
		if (autoFBatchBankParam != null && StringUtils.isNotBlank(autoFBatchBankParam.getValue())) {
			rBank = (RegistryBank)BeanManager.getManagerBean(RegistryBank.class).get(Integer.parseInt(autoFBatchBankParam.getValue()));
		}

		FinanceBatch financeBatch = new FinanceBatch();
		financeBatch.setDescription(getBatchDescription());
		financeBatch.setIssueDate(new Date());
		financeBatch.setPayment(false);
		financeBatch.setFinanceBatchType(FinanceBatchType.NONE);
		financeBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
		financeBatch.setRegistryBank(rBank);
		financeBatch.setConfidential(false);
		financeBatch.setSecurityLevel(SecurityLevel.OFFICIAL);
		financeBatch.setCreationUser(ICommonConstants.SYSTEM_USER);
		return (FinanceBatch)BeanManager.getManagerBean(FinanceBatch.class).insert(financeBatch);
	}

	public void onLoadFinanceBatch(ActionEvent event) throws ManagerBeanException {
		if (getFinanceBatch() != null && getFinanceBatch().getId() != null) {
			BasicController controller = (BasicController)AonUtil.getRegisteredBean(IPmsConstants.FINANCE_BATCH_CONTROLLER_NAME);
			controller.onLoad(event, getFinanceBatch().getId(), IPmsConstants.RESERVATION_FINANCE_BATCH_FORM_NAME, null);
		}
	}

}