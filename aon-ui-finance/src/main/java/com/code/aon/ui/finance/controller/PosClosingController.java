package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE_FROM;
import static com.code.aon.ui.common.ICommonMessages.DATE_TO;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_POS_SHIFT_IMBALANCE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.TICKET;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICommonConstants;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.Pos;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.finance.invoicing.PosInvoicing;
import com.code.aon.finance.util.PosBalanceUtils;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class PosClosingController implements IFinanceConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private PosShift posShift;
	private String fromTicket;
	private String toTicket;
	private CashCalculator calculator;
	private boolean showConfirmWindow;

	public PosShift getPosShift() {
		return posShift;
	}

	public void setPosShift(PosShift posShift) {
		this.posShift = posShift;
	}
	
	public String getFromTicket() {
		return fromTicket;
	}

	public void setFromTicket(String fromTicket) {
		this.fromTicket = fromTicket;
	}
	
	public String getToTicket() {
		return toTicket;
	}

	public void setToTicket(String toTicket) {
		this.toTicket = toTicket;
	}
	
	public CashCalculator getCalculator() {
		if (calculator == null) {
			calculator = new CashCalculator();
		}
		return calculator;
	}

	public void setCalculator(CashCalculator calculator) {
		this.calculator = calculator;
	}

	public boolean isShowConfirmWindow() {
		return showConfirmWindow;
	}

	public void setShowConfirmWindow(boolean showConfirmWindow) {
		this.showConfirmWindow = showConfirmWindow;
	}

    public void onLoad(ActionEvent event) {    	
    	setFromTicket(null);
    	setToTicket(null);
    	setPosShift(PosUtils.getUserPosShift());
    	if (getPosShift() == null || getPosShift().getId() == null) {
			String msg = "No hay ninguna Caja abierta por el Usuario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} else {
			try {
		        ((PosShiftController)FormUtil.getController(POS_SHIFT_CONTROLLER_NAME)).load(event, getPosShift().getId());
			} catch (ManagerBeanException ex) {
				String msg = "Error en el proceso de Cierre de Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
    }   

    public ITransferObject getTo() {
    	return getPosShift();
    }

    public void onShowCalculatorWindow(ActionEvent event) {
		getCalculator().initialize();
	}

	public void onAcceptCalculatorWindow(ActionEvent event) {
		getPosShift().setTotalShiftCountMap(null);
	}

	public void onAcceptCount(ActionEvent event) {
		FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME).onAccept(event);
		getPosShift().setTotalShiftCountMap(null);
	}

	public void onRemoveCount(ActionEvent event) {
		FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME).onRemove(event);
		getPosShift().setTotalShiftCountMap(null);
	}

	public void onConfirmClose(ActionEvent event) {
		if (!getPosShift().isClosed() && !getPosShift().getPos().isInvoiceable() && PosBalanceUtils.isPosShiftImbalance(getPosShift())) {
			setShowConfirmWindow(true);
		} else if (!getPosShift().isClosed()) {
			onClose(event);
		} else {
			onSave(event);
		}
	}

	public String getImbalancePayMethodsError() {
		String imbalance = "";
		for (PayMethod payMethod : getPosShift().getTotalShiftCountMap().keySet()) {
			double[] totals = posShift.getTotalShiftCountMap().get(payMethod);
			if (totals[0] != totals[1]) {
				imbalance += ", " + payMethod.getName();
			}
		}
		return AonUtil.getMessage(FINANCE_POS_SHIFT_IMBALANCE_ERROR, imbalance.replaceFirst(", ", ""));
	}

	public void onClose(ActionEvent event) {
		try {
			String ticketInfo = AonUtil.getMessage(TICKET) + " " + 
									AonUtil.getMessage(DATE_FROM) + ": " + getFromTicket() + " " + 
									AonUtil.getMessage(DATE_TO) + ": " + getToTicket();
			if (!getPosShift().isClosed() && validateClosing(getPosShift().getPos(), getPosShift().getShift())) {
				getPosShift().setEndTime(new Date());
				if (getPosShift().getPos().isInvoiceable()) {
					getPosShift().setRemarks(ticketInfo + "\n" + getPosShift().getRemarks());
				}
				onSave(event);
				onInvoice(ticketInfo);
				onAutoFbatch();
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error en el proceso de Cierre de Caja. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private boolean validateClosing(Pos pos, Shift shift) {
		if (!PosUtils.isPosShiftAlreadyOpened(pos, shift)) {
			String msg = "No es posible cerrar la Caja. Ya ha sido cerrada por otro Usuario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	public void onSave(ActionEvent event) {
		try {
			setPosShift((PosShift)BeanManager.getManagerBean(PosShift.class).update(getPosShift()));
		} catch (ManagerBeanException ex) {
			String msg = "Error en el proceso de Cierre de Caja. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onInvoice(String ticketInfo) throws ManagerBeanException {
		if (getPosShift().getPos().isInvoiceable()) {
            PosInvoicing posInvoicing = new PosInvoicing();
            Invoice invoice = posInvoicing.completeInvoice(getPosShift(), getPosShift().getShift().getName(AonUtil.getCurrentLocale()), ticketInfo);
			onSave(null);

    		AccountEntryInvoiceWriter entryWriter = new AccountEntryInvoiceWriter();
    		entryWriter.recordAndUpdateInvoice(invoice);
        }
	}

	public void onAutoFbatch() throws ManagerBeanException {
		RegistryBank rBank = obtainAutoRegistryBank();
		if (rBank != null) {
			PosFinanceController posFinanceController = (PosFinanceController)AonUtil.getRegisteredBean(IFinanceConstants.POS_FINANCE_CONTROLLER_NAME);
			ReservationUtils reservationUtils = new ReservationUtils(getPosShift().getDomain());
			Date issueDate = reservationUtils.obtainProductionDate(getPosShift().getEndTime());

			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
			Criteria criteria = new Criteria();
			PayMethodType[] payMethodTypes = obtainAutoPayMethodType();
			if (payMethodTypes != null) {
				criteria.addExpression(ExpressionUtilities.getInExpression(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), payMethodTypes));
			}
			criteria.addOrder(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_TYPE));
			criteria.addOrder(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_NAME));
			for (ITransferObject ito : payMethodBean.getList(criteria)) {
				PayMethod payMethod = (PayMethod)ito;
				FinanceBatch financeBatch = null;

				criteria = new Criteria();
				criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_ID), payMethod.getId());
				criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
				criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_ID), getPosShift().getId());
				for (ITransferObject itr : financeBean.getList(criteria)) {
					Finance finance = (Finance)itr;
					if (financeBatch == null) {
						String description = posFinanceController.obtainFbatchDescription(issueDate, payMethod, getPosShift().getPos().getWorkPlace());
						financeBatch = obtainFinanceBatch(rBank, description, issueDate);
					}

					FinanceBatchDetail fBatchDetail = new FinanceBatchDetail();
					fBatchDetail.setFinance(finance);
					fBatchDetail.setFinanceBatch(financeBatch);
					fBatchDetail.setAmount(finance.getTotalAmount());
					fBatchDetail.setStatus(FinanceStatus.BATCHED);
					fBatchDetail.setCreationUser(ICommonConstants.SYSTEM_USER);
					fBatchDetailBean.insert(fBatchDetail);
				}
			}
		}
	}

	private RegistryBank obtainAutoRegistryBank() throws ManagerBeanException {
		ApplicationParameter autoFBatchBankParam = AppParamUtil.getParameter(AppParam.PMS_AUTO_FBATCH_BANK);
		if (autoFBatchBankParam != null && StringUtils.isNotBlank(autoFBatchBankParam.getValue())) {
			return (RegistryBank)BeanManager.getManagerBean(RegistryBank.class).get(Integer.parseInt(autoFBatchBankParam.getValue()));
		}
		return null;
	}

	private PayMethodType[] obtainAutoPayMethodType() {
		ApplicationParameter autoFBatchPayMethodParam = AppParamUtil.getParameter(AppParam.PMS_AUTO_FBATCH_PAY_METHOD);
		if (autoFBatchPayMethodParam != null) { 
			autoFBatchPayMethodParam.setValue(StringUtils.replace(autoFBatchPayMethodParam.getValue(), "[]", ""));
			if (StringUtils.isNotBlank(autoFBatchPayMethodParam.getValue())) {
				String[] payMethodTypeIds = StringUtils.substringsBetween(autoFBatchPayMethodParam.getValue(), "[", "]");
				if (payMethodTypeIds.length > 0) {
					PayMethodType[] payMethodTypes = new PayMethodType[payMethodTypeIds.length];
					for (int i=0; i<payMethodTypeIds.length; i++) {
						payMethodTypes[i] = PayMethodType.values()[Integer.parseInt(payMethodTypeIds[i])];
					}
					return payMethodTypes;
				}
			}
		}
		return null;
	}

	private FinanceBatch obtainFinanceBatch(RegistryBank rBank, String description, Date issueDate) throws ManagerBeanException {
		IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(ExpressionUtilities.getLikeExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_DESCRIPTION), description + "%"));
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE), issueDate);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_PAYMENT), Boolean.FALSE);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_TYPE), FinanceBatchType.NONE);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_REGISTRY_BANK_ID), rBank.getId());
		criteria.addOrder(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_DESCRIPTION));
		for (ITransferObject ito : fBatchBean.getList(criteria)) {
			FinanceBatch fBatch = (FinanceBatch)ito;
			if (fBatch.isTodo()) {
				return fBatch;
			} else {
				String suffix = StringUtils.substring(fBatch.getDescription(), fBatch.getDescription().length()-2);
				if (StringUtils.substring(suffix, 0, 1).equals("_") && NumberUtils.isNumber(StringUtils.substring(suffix, -1))) {
					description = fBatch.getDescription() + "_" + (NumberUtils.toInt(StringUtils.substring(suffix, -1)) + 1);
				} else {
					description = fBatch.getDescription() + "_2";
				}
			}
		}

		FinanceBatch financeBatch = new FinanceBatch();
		financeBatch.setDescription(description);
		financeBatch.setIssueDate(issueDate);
		financeBatch.setPayment(false);
		financeBatch.setFinanceBatchType(FinanceBatchType.NONE);
		financeBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
		financeBatch.setRegistryBank(rBank);
		financeBatch.setConfidential(false);
		financeBatch.setSecurityLevel(SecurityLevel.OFFICIAL);
		financeBatch.setCreationUser(ICommonConstants.SYSTEM_USER);
		return (FinanceBatch)fBatchBean.insert(financeBatch);
	}


	public class PayMethodCount implements Comparable<Object> {
		private PayMethod payMethod;
		private double countAmount;
		
		public PayMethod getPayMethod() {
			return payMethod;
		}

		public void setPayMethod(PayMethod payMethod) {
			this.payMethod = payMethod;
		}

		public double getCountAmount() {
			return countAmount;
		}

		public void setCountAmount(double countAmount) {
			this.countAmount = countAmount;
		}

		@Override
		public int compareTo(Object obj) {
			if (obj instanceof PayMethodCount) {
				PayMethodCount payMethodCount = (PayMethodCount)obj;
				return this.getPayMethod().getName().compareTo(payMethodCount.getPayMethod().getName());
			}
			return 0;
		}

	}

}