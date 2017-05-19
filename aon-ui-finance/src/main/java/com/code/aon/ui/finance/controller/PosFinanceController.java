package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.PRICE_PATTERN;
import static com.code.aon.ui.common.ICommonMessages.SIMPLE_DATE2_PATTERN;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.finance.event.PosFinanceSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;

public class PosFinanceController extends FinanceListController implements IFinanceConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean showFinanceBatchWindow;
	private boolean newBatch;
	private FinanceBatch financeBatch;
	
	public boolean isShowFinanceBatchWindow() {
		return showFinanceBatchWindow;
	}

	public void setShowFinanceBatchWindow(boolean showFinanceBatchWindow) {
		this.showFinanceBatchWindow = showFinanceBatchWindow;
	}
	
	public boolean isNewBatch() {
		return newBatch;
	}

	public void setNewBatch(boolean newBatch) {
		this.newBatch = newBatch;
	}
	
	public FinanceBatch getFinanceBatch() {
		return financeBatch;
	}

	public void setFinanceBatch(FinanceBatch financeBatch) {
		this.financeBatch = financeBatch;
	}

	public void onFinanceBatchShow(ActionEvent event) throws ManagerBeanException {
		setNewBatch(true);
		setFinanceBatch(createFinanceBatch());
	}

	public void onBatchModeChanged(ActionEvent event) {
		if (isNewBatch()) {
			setFinanceBatch(createFinanceBatch());
		}
	}

	private FinanceBatch createFinanceBatch() {
		PosFinanceSearchListener searchListener = (PosFinanceSearchListener)AonUtil.getRegisteredBean(POS_FINANCE_SEARCH_LISTENER_NAME);
		Date issueDate = new Date();
		FinanceBatch fBatch = new FinanceBatch();
		fBatch.setDescription(obtainFbatchDescription(issueDate, searchListener.getPayMethod(), searchListener.getWorkPlace()));
		fBatch.setIssueDate(issueDate);
		return fBatch;
	}

	public String obtainFbatchDescription(Date issueDate, PayMethod payMethod, WorkPlace workPlace) {
		String date = new SimpleDateFormat(AonUtil.getMessage(SIMPLE_DATE2_PATTERN)).format(issueDate);
		String payMethodName = StringUtils.substring(payMethod.getName(), 0, 8);
		String workPlaceName = obtainWorkPlaceDescription(workPlace);
		return (date + "_" + payMethodName + "_" + workPlaceName);
	}

	private String obtainWorkPlaceDescription(WorkPlace workPlace) {
		String description = workPlace.getDescription();
		try {
			Hotel hotel = null;
			IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_ID), workPlace.getId());
			for (ITransferObject ito : hotelBean.getList(criteria)) {
				hotel = (Hotel)ito;
			}
			description = (hotel != null) ? hotel.getAlias() : description;
		} catch(ManagerBeanException ex) {
		}
		return StringUtils.substring(description, 0, 14).trim();
	}
	
	public List<SelectItem> getFinanceBatchList() throws ManagerBeanException {
		IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_PAYMENT), false);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.TODO);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_TYPE), FinanceBatchType.NONE);
		criteria.addOrder(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ID), false);
		List<SelectItem> fBatchList = new LinkedList<SelectItem>();
		for (ITransferObject ito : fBatchBean.getList(criteria)) {
			FinanceBatch fBatch = (FinanceBatch)ito;
			String id = StringUtils.leftPad("(" + fBatch.getId() + ")", 6, "0");
			String amount = new DecimalFormat(AonUtil.getMessage(PRICE_PATTERN)).format(fBatch.getFinanceBatchTotalAmount());
			SelectItem item = new SelectItem(fBatch, id + " - " + fBatch.getDescription() + StringUtils.leftPad(amount, 50 - fBatch.getDescription().length()-amount.length(), "·") + "EUR.");
			fBatchList.add(item);
		}
		return fBatchList;
	}

	public void onFinanceBatch(ActionEvent event) throws ManagerBeanException {
		FinanceBatch financeBatch = getFinanceBatch();
		if (isNewBatch()) {
			IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
			financeBatch.setPayment(false);
			financeBatch.setFinanceBatchType(FinanceBatchType.NONE);
			financeBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
			financeBatch.setConfidential(false);
			financeBatch.setSecurityLevel(SecurityLevel.OFFICIAL);
			financeBatch = (FinanceBatch)fBatchBean.insert(getFinanceBatch());
			setFinanceBatch(financeBatch);
		}

		if (financeBatch.getFinanceBatchStatus() == FinanceBatchStatus.TODO) {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			for (Finance finance : getCheckedFinances()) {
				finance = (Finance)financeBean.get(finance.getId());
				if (finance.isPending() || finance.isReturned()) {
					FinanceBatchDetail fBatchDetail = new FinanceBatchDetail();
					fBatchDetail.setFinance(finance);
					fBatchDetail.setFinanceBatch(financeBatch);
					fBatchDetail.setAmount(finance.getTotalAmount());
					fBatchDetail.setStatus(FinanceStatus.BATCHED);
					fBatchDetailBean.insert(fBatchDetail);
				}
			}
		}

		onLoadFinanceBatch(event);
	}

	public String getReservationCode() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			Finance finance = (Finance)getModel().getRowData();
			if (finance.getInvoice() != null && finance.getInvoice().getProject() != null && finance.getInvoice().getProject().getId() != null) {
				IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ID), finance.getInvoice().getProject().getId());
				Projection prjCode = Projection.property(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CODE));
				List<?> resultList = reservationBean.getList(new ProjectionList(prjCode), criteria);
				if (resultList.size() > 0 && resultList.get(0) != null) {
					return (String)resultList.get(0);
				}
			}
		}
		return null;
	}

	public void onLoadFinanceBatch(ActionEvent event) throws ManagerBeanException {
		if (getFinanceBatch() != null && getFinanceBatch().getId() != null) {
			BasicController controller = (BasicController)AonUtil.getRegisteredBean(FINANCE_BATCH_CONTROLLER_NAME);
			controller.onLoad(event, getFinanceBatch().getId(), POS_FINANCE_LIST_NAME, POS_FINANCE_CONTROLLER_NAME + ".onSearch");
		}
	}

	public void onLoadReservation(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			Finance finance = (Finance)getModel().getRowData();
			BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
			reservationController.onLoad(event, finance.getInvoice().getProject().getId(), POS_FINANCE_LIST_NAME, POS_FINANCE_CONTROLLER_NAME + ".onSearch");
		}
	}

	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			Finance finance = (Finance)getModel().getRowData();
			BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.onLoad(event, finance.getInvoice().getId(), POS_FINANCE_LIST_NAME, POS_FINANCE_CONTROLLER_NAME + ".onSearch");
		}
	}

}
