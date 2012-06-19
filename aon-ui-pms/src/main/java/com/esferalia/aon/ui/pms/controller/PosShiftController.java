package com.esferalia.aon.ui.pms.controller;

import java.util.ArrayList;
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
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Pos;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.PosShiftCount;


public class PosShiftController extends BasicController {
	
	private final String CASH_CALCULATOR_CONTROLLER_NAME = "cashCalculator";
	private final String POS_SHIFT_COUNT_CONTROLLER_NAME = "posShiftCount";
	private final String POS_SHIFT_CONTROLLER_NAME = "posShift";
	
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
	
	private List<ITransferObject> getInvoiceFinancesList(PosShift posShift) {
		try {
			IManagerBean financesBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addBetweenExpression(financesBean.getFieldName(IEntityAlias.FINANCE_INVOICE_CREATION_DATE),posShift.getStartTime(), posShift.getEndTime()!=null?posShift.getEndTime():new Date());
			criteria.addEqualExpression(financesBean.getFieldName(IEntityAlias.FINANCE_INVOICE_CREATION_USER), posShift.getUser().getLogin());
			criteria.addOrder(financesBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_NAME));
			return financesBean.getList(criteria);
		} catch (ManagerBeanException ex) {
			String msg = "Error al cargar los datos de Facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}
	
	private List<ITransferObject> getInvoiceList(PosShift posShift) {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addBetweenExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_CREATION_DATE),posShift.getStartTime(), posShift.getEndTime()!=null?posShift.getEndTime():new Date());
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_CREATION_USER), posShift.getUser().getLogin());
			criteria.addOrder(invoiceBean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE));
			return invoiceBean.getList(criteria);
		} catch (ManagerBeanException ex) {
			String msg = "Error al cargar los datos de Facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}
	
	private List<PaymethodCount> getComparedPaymethodCashList(PosShift posShift) {
		List<ITransferObject> financeList = getInvoiceFinancesList(posShift);
		try {
			Map<String, PaymethodCount> map = new HashMap<String, PosShiftController.PaymethodCount>();
			if(!financeList.isEmpty()){
				for(ITransferObject to: financeList){
					Finance finance = (Finance) to;
					if(finance.getPayMethod().getType()==PayMethodType.CASH_BASIS){
						if(map.containsKey(PayMethodType.CASH_BASIS.name())){
							map.get(PayMethodType.CASH_BASIS.name()).setInvoiceAmount(map.get(PayMethodType.CASH_BASIS.name()).getInvoiceAmount()+finance.getAmount());
						} else {
							PaymethodCount pc = new PaymethodCount();
							pc.setPayMethod(finance.getPayMethod());
							pc.setInvoiceAmount(finance.getAmount());
							map.put(PayMethodType.CASH_BASIS.name(), pc);
						}
					} else if(map.containsKey(finance.getPayMethod().getName())){
						map.get(finance.getPayMethod().getName()).setInvoiceAmount(map.get(finance.getPayMethod().getName()).getInvoiceAmount()+finance.getAmount());
					} else {
						PaymethodCount pc = new PaymethodCount();
						pc.setPayMethod(finance.getPayMethod());
						pc.setInvoiceAmount(finance.getAmount());
						map.put(finance.getPayMethod().getName(), pc);
					}
				}
			}
			IManagerBean countBean = BeanManager.getManagerBean(PosShiftCount.class);
			Criteria countCriteria = new Criteria();
			countCriteria.addEqualExpression(countBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), posShift.getId());
			countCriteria.addOrder(countBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_PAY_METHOD_NAME));
			for(ITransferObject to: countBean.getList(countCriteria)){
				PosShiftCount psc = (PosShiftCount) to;
				if(psc.getPayMethod().getType()==PayMethodType.CASH_BASIS){
					if(map.containsKey(PayMethodType.CASH_BASIS.name())){
						map.get(PayMethodType.CASH_BASIS.name()).setPosAmount(map.get(PayMethodType.CASH_BASIS.name()).getPosAmount()+psc.getAmount());
					} else {
						PaymethodCount pc = new PaymethodCount();
						pc.setPayMethod(psc.getPayMethod());
						pc.setPosAmount(psc.getAmount());
						map.put(PayMethodType.CASH_BASIS.name(), pc);
					}
				} else if(map.containsKey(psc.getPayMethod().getName())){
					map.get(psc.getPayMethod().getName()).setPosAmount(map.get(psc.getPayMethod().getName()).getPosAmount()+psc.getAmount());
				} else {
					PaymethodCount pc = new PaymethodCount();
					pc.setPayMethod(psc.getPayMethod());
					pc.setPosAmount(psc.getAmount());
					map.put(psc.getPayMethod().getName(), pc);
				}
			}
			if(map.containsKey(PayMethodType.CASH_BASIS.name())){
				// se resta al metalico el importe inicial de la caja
				map.get(PayMethodType.CASH_BASIS.name()).setPosAmount(map.get(PayMethodType.CASH_BASIS.name()).getPosAmount()-getPosShift().getInitialAmount());
			}
			return new ArrayList<PaymethodCount>(map.values());
		} catch (ManagerBeanException ex) {
			String msg = "Error al cargar los datos de Facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}
	
	public Double getTotalCashAmount() {
		String sqlSelect;
		try {
			sqlSelect = "SELECT sum(amount)"
					+ " FROM pos_shift_count"
					+ " WHERE " + DomainManager.getSQLWhereClause("pos_shift_count.domain") 
					+ " AND pos_shift = " + ((PosShift)getTo()).getId()
					+ getCashPayMethodClause()
					+ " GROUP BY pos_shift";
		} catch (ManagerBeanException e) {
			return null;
		}
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query sqlQuery = session.createSQLQuery(sqlSelect);
		return (Double) sqlQuery.uniqueResult();
	}
	
	private String getCashPayMethodClause() throws ManagerBeanException {
		String clause = "";
		IManagerBean bean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), PayMethodType.CASH_BASIS);
		for(ITransferObject to: bean.getList(criteria)){
			PayMethod pm = (PayMethod) to;
			clause += (clause.isEmpty()?" AND (":" OR ") + " pay_method = " + pm.getId();
		}
		clause += " )";
		return clause;
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
		CashCalculatorController controller = (CashCalculatorController) AonUtil.getRegisteredBean(CASH_CALCULATOR_CONTROLLER_NAME);
		controller.init();
		setCalculator(controller);
		setHotel(null);
		setPosShift(new PosShift());
		getPosShift().setStartTime(new Date());
	}
	public void initInvoiceData( ){
		setInvoiceFinancesModel(new ListDataModel(getInvoiceFinancesList((PosShift)getTo())));
		setComparedCashModel(new ListDataModel(getComparedPaymethodCashList((PosShift)getTo())));
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
			IController controller = FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME);
			getCalculator().setCashAmount(getCalculator().getCalcTotal());
			((PosShiftCount)controller.getTo()).setAmount(getCalculator().getCalcTotal());
		} else {
			IController controller = FormUtil.getController(POS_SHIFT_CONTROLLER_NAME);
			PosShift c = (PosShift) controller.getTo();
			c.setInitialAmount(getCalculator().getCalcTotal());
		} 
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
