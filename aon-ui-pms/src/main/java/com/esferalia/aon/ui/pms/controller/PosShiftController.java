package com.esferalia.aon.ui.pms.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Pos;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.PosShiftCount;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;

public class PosShiftController extends BasicController {
	
	private PosShift posShift;
	private Hotel hotel;
	private CashCalculatorController calculator;
	private boolean cashCalculator;
	
	private DataModel invoiceFinancesModel;
	private DataModel comparedCashModel;
	
	public DataModel getInvoiceFinancesModel() {
		return invoiceFinancesModel;
	}

	public void setInvoiceFinancesModel(DataModel invoiceFinancesModel) {
		this.invoiceFinancesModel = invoiceFinancesModel;
	}

	public DataModel getComparedCashModel() {
		return comparedCashModel;
	}

	public void setComparedCashModel(DataModel comparedCashModel) {
		this.comparedCashModel = comparedCashModel;
	}
	
	public CashCalculatorController getCalculator() {
		return calculator;
	}
	public void setCalculator(CashCalculatorController calculator) {
		this.calculator = calculator;
	}
	public boolean isCashCalculator() {
		return cashCalculator;
	}
	public void setCashCalculator(boolean cashCalculator) {
		this.cashCalculator = cashCalculator;
	}
	public PosShift getPosShift() {
		return posShift;
	}
	public void setPosShift(PosShift posShift) {
		this.posShift = posShift;
	}
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	
	public boolean getUniquePayMethod() throws ManagerBeanException{
		Finance finance = (Finance) getInvoiceFinancesModel().getRowData();
		return finance.getPayMethod().getName().equals(finance.getInvoice().getPayMethod());
	}

	public String getPayMethod() throws ManagerBeanException{
		Finance finance = (Finance) getInvoiceFinancesModel().getRowData();
		return (finance.getPayMethod().getName().equals(finance.getInvoice().getPayMethod())?"":finance.getInvoice().getPayMethod()+"/")+finance.getPayMethod().getName();
	}
	
	private List<ITransferObject> invoiceFinancesList;
	
	public List<ITransferObject> getInvoiceFinancesList() {
		if( invoiceFinancesList==null ){
			searchInvoiceFinancesList();
		}
		return invoiceFinancesList;
	}
	
	private void searchInvoiceFinancesList() {
		try {
			IManagerBean financesBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addBetweenExpression(financesBean.getFieldName(IEntityAlias.FINANCE_INVOICE_CREATION_DATE),((PosShift)getTo()).getStartTime(), ((PosShift)getTo()).getEndTime()!=null?((PosShift)getTo()).getEndTime():new Date());
			criteria.addEqualExpression(financesBean.getFieldName(IEntityAlias.FINANCE_INVOICE_CREATION_USER), ((PosShift)getTo()).getUser().getLogin());
			criteria.addOrder(financesBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_NAME));
			invoiceFinancesList = financesBean.getList(criteria);
		} catch (ManagerBeanException ex) {
			String msg = "Error al cargar los vencimientos de Facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}
	
	private List<PaymethodCount> getComparedPaymethodCashList() {
		List<ITransferObject> financeList = getInvoiceFinancesList();
		try {
			Map<Integer, PaymethodCount> map = new HashMap<Integer, PosShiftController.PaymethodCount>();
			if(!financeList.isEmpty()){
				for(ITransferObject to: financeList){
					Finance finance = (Finance) to;
					if(map.containsKey(finance.getPayMethod().getId())){
						map.get(finance.getPayMethod().getId()).setInvoiceAmount(map.get(finance.getPayMethod().getId()).getInvoiceAmount()+finance.getAmount());
					} else {
						PaymethodCount pc = new PaymethodCount();
						pc.setPayMethod(finance.getPayMethod());
						pc.setInvoiceAmount(finance.getAmount());
						map.put(finance.getPayMethod().getId(), pc);
					}
				}
			}
			IManagerBean countBean = BeanManager.getManagerBean(PosShiftCount.class);
			Criteria countCriteria = new Criteria();
			countCriteria.addEqualExpression(countBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), posShift.getId());
			countCriteria.addOrder(countBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_PAY_METHOD_NAME));
			for(ITransferObject to: countBean.getList(countCriteria)){
				PosShiftCount psc = (PosShiftCount) to;
				if(map.containsKey(psc.getPayMethod().getId())){
					map.get(psc.getPayMethod().getId()).setPosAmount(map.get(psc.getPayMethod().getId()).getPosAmount()+psc.getAmount());
				} else {
					PaymethodCount pc = new PaymethodCount();
					pc.setPayMethod(psc.getPayMethod());
					pc.setPosAmount(psc.getAmount());
					map.put(psc.getPayMethod().getId(), pc);
				}
			}
			PayMethod cashBasis = getPayMethodCashBasis();
			if(cashBasis!=null && map.containsKey(cashBasis.getId())){
				// se resta al efectivo (metalico) el importe inicial de la caja
				map.get(cashBasis.getId()).setPosAmount(map.get(cashBasis.getId()).getPosAmount()-getPosShift().getInitialAmount());
			}
			List<PaymethodCount> list = new LinkedList<PosShiftController.PaymethodCount>(map.values());
			Collections.sort(list, new Comparator<PaymethodCount>() {  
				@Override
				public int compare(PaymethodCount o1, PaymethodCount o2) {
					return o1.getPayMethod().getName().compareToIgnoreCase(o2.getPayMethod().getName());
				}  
		    });  
			return list;
		} catch (ManagerBeanException ex) {
			String msg = "Error al cargar los datos de Facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}
	
	private PayMethod getPayMethodCashBasis() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), PayMethodType.CASH_BASIS);
		criteria.addOrder(bean.getFieldName(IEntityAlias.PAY_METHOD_ID));
		List<ITransferObject> list = bean.getList(criteria);
		return (!list.isEmpty())?(PayMethod) list.get(0):null;
	}
	
	public List<SelectItem> getPosList() throws ManagerBeanException {
		String sqlSelect = "SELECT Pos.*"
			+ " FROM pos as Pos"
			+ " LEFT JOIN pos_shift as PosShift on PosShift.pos = Pos.id"
			+ " WHERE " + DomainManager.getSQLWhereClause("Pos.domain") 
			+ " AND " + (getHotel() == null ? " Pos.id is null " : " Pos.workplace = " + getHotel().getWorkPlace().getId()) 
			+ " GROUP BY Pos.name"
			+ " ORDER BY Pos.name";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query sqlQuery = session.createSQLQuery(sqlSelect);
		List<SelectItem> list = new LinkedList<SelectItem>();
		for(Object to: sqlQuery.list()){
			Pos pos = (Pos) BeanManager.getManagerBean(Pos.class).get((Integer)(((Object[])to)[0]));
			String name = pos.getName() + ((getHotel()!=null && getHotel().getId()!=null)?"":" ("+pos.getWorkPlace().getDescription()+")");
			SelectItem item = new SelectItem(pos, name);
			list.add(item);
		}
		return list;
	}
	
	public void onInit( ActionEvent event ){
		init();
	}
	
	public void init( ){
		CashCalculatorController controller = (CashCalculatorController) AonUtil.getRegisteredBean(IPmsConstants.CASH_CALCULATOR_CONTROLLER_NAME);
		controller.init();
		setCalculator(controller);
		setHotel(null);
		setPosShift(new PosShift());
		getPosShift().setStartTime(new Date());
	}
	public void initInvoiceData( ){
		invoiceFinancesList = null;
		setInvoiceFinancesModel(new ListDataModel(getInvoiceFinancesList()));
		setComparedCashModel(new ListDataModel(getComparedPaymethodCashList()));
	}
	
	public void onShowCalculatorWindow( ActionEvent event ){
		setCashCalculator(false);
		getCalculator().setAmounts( new int[15] );
		getCalculator().setInitialAmount(true);
		getCalculator().setPosShift(getPosShift());
	}
	
	public void onShowCashCalculatorWindow( ActionEvent event ){
		setCashCalculator(true);
		getCalculator().setAmounts( new int[15] );
		getCalculator().setInitialAmount(false);
		getCalculator().setPosShift(getPosShift());
	}
	
	public void onAcceptCalculatorAmount( ActionEvent event ){
		if( isCashCalculator() ){
			IController controller = FormUtil.getController(IPmsConstants.POS_SHIFT_COUNT_CONTROLLER_NAME);
			getCalculator().setCashAmount(getCalculator().getCalcTotal());
			((PosShiftCount)controller.getTo()).setAmount(getCalculator().getCalcTotal());
		} else {
			PosShift posShift = (PosShift) this.getTo();
			posShift.setInitialAmount(getCalculator().getCalcTotal());
		} 
	}
	
	public void onAcceptPosShiftCount( ActionEvent event ){
		IController controller = FormUtil.getController(IPmsConstants.POS_SHIFT_COUNT_CONTROLLER_NAME);
		controller.onAccept(event);
		setComparedCashModel(new ListDataModel(getComparedPaymethodCashList()));
	}

	public void onRemovePosShiftCount( ActionEvent event ){
		IController controller = FormUtil.getController(IPmsConstants.POS_SHIFT_COUNT_CONTROLLER_NAME);
		controller.onRemove(event);
		setComparedCashModel(new ListDataModel(getComparedPaymethodCashList()));
	}
	
	public void onPrintShiftInvoice(ActionEvent event) throws ManagerBeanException {
		PosShift ps = (PosShift) getTo();
		if (ps!=null && ps.getInvoice()!=null) {
			BasicController controller = (BasicController) ((IController)AonUtil.getRegisteredBean(IPmsConstants.SALE_INVOICE_CONTROLLER_NAME));
			controller.select(event, ps.getInvoice().getId());
		}
	}
	
	public void onPrintInvoice(ActionEvent event) throws ManagerBeanException {
		SelectedInvoiceController controller = (SelectedInvoiceController) AonUtil.getRegisteredBean(IPmsConstants.SELECTED_INVOICE_CONTROLLER_NAME);
		controller.setTo(((Finance)getInvoiceFinancesModel().getRowData()).getInvoice());
	}
	
	
	public void onLoadReservation(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance)this.getInvoiceFinancesModel().getRowData();
		BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_CONTROLLER_NAME);
		reservationController.onLoad(event, finance.getInvoice().getProject().getId(), IPmsConstants.POS_SHIFT_FORM_NAME, IPmsConstants.POS_SHIFT_CONTROLLER_NAME + ".onBackPosShift");
	}
	
	public void onBackPosShift(ActionEvent event) throws ManagerBeanException {
		initInvoiceData();
	}
	
	public class PaymethodCount {
		private PayMethod payMethod;
		private Double posAmount;
		private Double invoiceAmount;
		
		public PaymethodCount() {
			posAmount = 0.0;
			invoiceAmount = 0.0;
		}
		public PayMethod getPayMethod() {
			return payMethod;
		}
		public void setPayMethod(PayMethod payMethod) {
			this.payMethod = payMethod;
		}
		public Double getPosAmount() {
			return posAmount;
		}
		public void setPosAmount(Double posAmount) {
			this.posAmount = posAmount;
		}
		public Double getInvoiceAmount() {
			return invoiceAmount;
		}
		public void setInvoiceAmount(Double invoiceAmount) {
			this.invoiceAmount = invoiceAmount;
		}
		
	}
	
}
