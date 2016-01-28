package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE_PATTERN;
import static com.code.aon.ui.common.ICommonMessages.DECIMAL_2_PATTERN;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_CHARGED;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.PayMethod;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.Pos;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosShiftController extends BasicController implements IFinanceConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private WorkPlace workPlace;
	private Department department;
	private CashCalculator calculator;
	private DataModel totalShiftCountModel;
	private DataModel financeModel;
	private boolean showBindInvoiceWindow;
	private String invoiceReferenceCode;

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
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

	public DataModel getTotalShiftCountModel() {
		if (totalShiftCountModel == null) {
			totalShiftCountModel = new SerializableListDataModel(getTotalShiftCountList((PosShift)getTo()));
		}
		return totalShiftCountModel;
	}

	public void setTotalShiftCountModel(DataModel totalShiftCountModel) {
		this.totalShiftCountModel = totalShiftCountModel;
	}

	public DataModel getFinanceModel() {
		if (financeModel == null) {
			financeModel = new SerializableListDataModel(getFinanceList((PosShift)getTo()));
		}
		return financeModel;
	}

	public void setFinanceModel(DataModel financeModel) {
		this.financeModel = financeModel;
	}

	public boolean isShowBindInvoiceWindow() {
		return showBindInvoiceWindow;
	}

	public void setShowBindInvoiceWindow(boolean showBindInvoiceWindow) {
		this.showBindInvoiceWindow = showBindInvoiceWindow;
	}

	public String getInvoiceReferenceCode() {
		return invoiceReferenceCode;
	}

	public void setInvoiceReferenceCode(String invoiceReferenceCode) {
		this.invoiceReferenceCode = invoiceReferenceCode;
	}

	public void resetTotalShiftCount() {
		((PosShift)getTo()).setTotalShiftCountMap(null);
		setTotalShiftCountModel(null);
	}

	private List<PayMethodCount> getTotalShiftCountList(PosShift posShift) {
		List<PayMethodCount> totalShiftCountList = new LinkedList<PayMethodCount>();
		for (PayMethod payMethod : posShift.getTotalShiftCountMap().keySet()) {
			double[] totals = posShift.getTotalShiftCountMap().get(payMethod);

			PayMethodCount payMethodCount = new PayMethodCount();
			payMethodCount.setPayMethod(payMethod);
			payMethodCount.setCountAmount(totals[0]);
			payMethodCount.setFinanceAmount(totals[1]);
			
			totalShiftCountList.add(payMethodCount);
		}
		Collections.sort(totalShiftCountList);
		return totalShiftCountList;
	}

	private List<ITransferObject> getFinanceList(PosShift posShift) {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAYMENT), Boolean.FALSE);
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_ID), posShift.getId());
			criteria.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_NAME));
			criteria.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_REFERENCE_CODE));
			return financeBean.getList(criteria);
		} catch (ManagerBeanException ex) {
			String msg = "Error al cargar los datos de Cobros.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public boolean isCountCashOk() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			return isCountCashOk((PosShift)getModel().getRowData());
		}
		return true;
	}

	public boolean isCountCashOk(PosShift posShift) throws ManagerBeanException {
		return isCountOk(posShift, PayMethodType.CASH_BASIS);
	}

	public boolean isCountCardOk() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			return isCountCardOk((PosShift)getModel().getRowData());
		}
		return true;
	}

	public boolean isCountCardOk(PosShift posShift) throws ManagerBeanException {
		return isCountOk(posShift, PayMethodType.CREDIT_CARD) && isCountOk(posShift, PayMethodType.DEBIT_CARD);
	}

	public boolean isCountChequeOk() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			return isCountChequeOk((PosShift)getModel().getRowData());
		}
		return true;
	}

	public boolean isCountChequeOk(PosShift posShift) throws ManagerBeanException {
		return isCountOk(posShift, PayMethodType.CHEQUE);
	}

	public boolean isCountTransferOk() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			return isCountTransferOk((PosShift)getModel().getRowData());
		}
		return true;
	}

	public boolean isCountTransferOk(PosShift posShift) throws ManagerBeanException {
		return isCountOk(posShift, PayMethodType.BANK_TRANSFER);
	}

	private boolean isCountOk(PosShift posShift, PayMethodType type) throws ManagerBeanException {
		if (posShift.isClosed()) {
			for (PayMethod payMethod : posShift.getTotalShiftCountMap().keySet()) {
				if (type == payMethod.getType()) {
					double[] totals = posShift.getTotalShiftCountMap().get(payMethod);
					if (totals[0] != totals[1]) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public List<SelectItem> getWorkPlacePos() throws ManagerBeanException {
		List<SelectItem> posList = new LinkedList<SelectItem>();
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			IManagerBean posBean = BeanManager.getManagerBean(Pos.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_WORK_PLACE_ID), getWorkPlace().getId());
			if (getDepartment() != null && getDepartment().getId() != null) {
				criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_DEPARTMENT_ID), getDepartment().getId());
			}
			criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_ACTIVE), Boolean.TRUE);
			criteria.addOrder(posBean.getFieldName(IEntityAlias.POS_NAME));
			for (ITransferObject ito : posBean.getList(criteria)) {
				Pos pos = (Pos)ito;
				SelectItem posItem = new SelectItem(pos, pos.getName());
				posList.add(posItem);
			}
		}
		return posList;
	}

	public String getUserName() throws ManagerBeanException {
		String userName = ((PosShift)getTo()).getUsername();
		IManagerBean userBean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(userBean.getFieldName(IEntityAlias.USER_LOGIN), userName);
		for (ITransferObject ito : userBean.getList(criteria)) {
			userName = userName + " (" + ((User)ito).getName() + ")";
			break;
		}
		return userName;
	}

	public void onInitialAmountChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			resetTotalShiftCount();
		}
	}

	public void onShowCalculatorWindow(ActionEvent event) {
		getCalculator().initialize();
	}

	public void onAcceptCalculatorWindow(ActionEvent event) {
		resetTotalShiftCount();
	}

	public String getFinancePayMethod() throws ManagerBeanException {
		Finance finance = (Finance)getFinanceModel().getRowData();
		String invoicePayMethodName = (finance.getPayMethod().getName().equals(finance.getInvoice().getPayMethod())) ? "" : finance.getInvoice().getPayMethod();
		return (invoicePayMethodName.equals("") ? "" : invoicePayMethodName + "/") + finance.getPayMethod().getName();
	}

	public void onAcceptCount(ActionEvent event) {
		FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME).onAccept(event);
		resetTotalShiftCount();
	}

	public void onRemoveCount(ActionEvent event) {
		FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME).onRemove(event);
		resetTotalShiftCount();
	}

	public void onLoadReservation(ActionEvent event) throws ManagerBeanException {
		if (getFinanceModel().isRowAvailable()) {
			Finance finance = (Finance)getFinanceModel().getRowData();
			BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
			reservationController.onLoad(event, finance.getInvoice().getProject().getId(), POS_SHIFT_FORM_NAME, POS_SHIFT_CONTROLLER_NAME + ".onBackPosShift");
		}
	}

	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		if (getFinanceModel().isRowAvailable()) {
			Finance finance = (Finance)getFinanceModel().getRowData();
			BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.onLoad(event, finance.getInvoice().getId(), POS_SHIFT_FORM_NAME, POS_SHIFT_CONTROLLER_NAME + ".onBackPosShift");
		}
	}

	public void onBackPosShift(ActionEvent event) throws ManagerBeanException {
		resetTotalShiftCount();
		setFinanceModel(null);
	}

	public void onBindInvoiceShow(ActionEvent event) throws ManagerBeanException {
		setInvoiceReferenceCode(null);
	}

	public void onBindInvoice(ActionEvent event) throws ManagerBeanException {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_REFERENCE_CODE), getInvoiceReferenceCode());
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
			UserUtils.getInstance().addScopeFilterToCriteria(criteria, invoiceBean.getFieldName(IEntityAlias.INVOICE_SCOPE_ID));
			List<ITransferObject> invoiceList = invoiceBean.getList(criteria);
			if (invoiceList.size() == 0) {
				String msg = "No se ha encontrado ninguna Factura con esos datos.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else if (invoiceList.size() > 1) {
				String msg = "Se han encontrado varias Facturas con esos datos. Revisar posibles errores.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else {
				Invoice invoice = (Invoice)invoiceList.get(0);
				double financeTotal = invoice.getFinanceTotal();
				if (financeTotal != 0 || invoice.getTotal() == 0) {
					NumberFormat numberFormat = new DecimalFormat(AonUtil.getMessage(DECIMAL_2_PATTERN));
					DateFormat dateFormat = new SimpleDateFormat(AonUtil.getMessage(DATE_PATTERN));
					String comments = StringUtils.isNotBlank(invoice.getComments()) ? invoice.getComments() + "\n" : "";
					comments += dateFormat.format(invoice.getDate()) + " - " + AonUtil.getMessage(FINANCE_CHARGED) + ": " + numberFormat.format(financeTotal) + "\n";
					invoice.setComments(comments);
				}
				invoice.setPosShift(((PosShift)getTo()));
				invoice.setUpdateEnabled(false);
				BeanManager.getManagerBean(Invoice.class).update(invoice);
			} 
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
		accept(event);
		onBackPosShift(event);
	}

	public void onUnbindInvoice(ActionEvent event) throws ManagerBeanException {
		if (getFinanceModel().isRowAvailable()) {
			Finance finance = (Finance)getFinanceModel().getRowData();
			finance.getInvoice().setPosShift(null);
			finance.getInvoice().setUpdateEnabled(false);
			BeanManager.getManagerBean(Invoice.class).update(finance.getInvoice());
		}
		accept(event);
		onBackPosShift(event);
	}

	public void onPrintInvoice(ActionEvent event) throws ManagerBeanException {
		if (getFinanceModel().isRowAvailable()) {
			SaleInvoiceController invoiceController = (SaleInvoiceController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.load(event, ((Finance)getFinanceModel().getRowData()).getInvoice().getId());
		}
	}


	public static class PayMethodCount implements Comparable<Object>, Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private PayMethod payMethod;
		private double countAmount;
		private double financeAmount;
		
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

		public double getFinanceAmount() {
			return financeAmount;
		}

		public void setFinanceAmount(double financeAmount) {
			this.financeAmount = financeAmount;
		}

		public double getDifference() {
			return CommonUtil.round(countAmount - financeAmount);
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
