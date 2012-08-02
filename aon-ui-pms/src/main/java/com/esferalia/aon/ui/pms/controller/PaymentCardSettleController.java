package com.esferalia.aon.ui.pms.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.BankAccount;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.finance.controller.FinanceListController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.event.FinanceListSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class PaymentCardSettleController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PaymentCardSettleController.class);
	
	private String selectedTab;

	private ArrayList<AgencyFinance> checks = new ArrayList<AgencyFinance>();
	
	private Customer agency;
	private Date startDate;
	private Date endDate;
	
	private DataModel financeModel;
	
	private List<AgencyFinance> financeList;
	
	public Customer getAgency() {
		return agency;
	}
	public void setAgency(Customer agency) {
		this.agency = agency;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = DateUtils. addMilliseconds(endDate, 24*60*60*1000 - 1000);
	}
	
	public DataModel getFinanceModel() {
		if(financeModel == null){
			financeModel = new ListDataModel(getFinanceList());
		}
		return financeModel;
	}

	public void setFinanceModel(DataModel financeModel) {
		this.financeModel = financeModel;
	}

	public List<AgencyFinance> getFinanceList() {
		if(financeList==null){
			financeList = new LinkedList<AgencyFinance>();
		}
		return financeList;
	}

	public void setFinanceList(List<AgencyFinance> financeList) {
		this.financeList = financeList;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public void rowSelected(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() throws ManagerBeanException {
		AgencyFinance to = (AgencyFinance) getFinanceModel().getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) throws ManagerBeanException {
		AgencyFinance to = (AgencyFinance) getFinanceModel().getRowData();
		if (rowChecked) {
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<AgencyFinance> getCheckedFinances() {
		return checks;
	}

	public void clearCheckedFinances() {
		checks = new ArrayList<AgencyFinance>();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		for (AgencyFinance af : getFinanceList()) {
//			Finance detail = (Finance)ito;
			if (!checks.contains(af)) {
				checks.add( af );
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedFinances();
	}

	public int getCheckedCount() {
		return getCheckedFinances().size();
	}
	
	public void onExecuteReport(){
//		ReportManager report = (ReportManager) AonUtil.getRegisteredBean("report");
//		report.onExecute();
//		batchFinances();
//		clearCheckedFinances();
	}
	
	public void onInit(ActionEvent event){
		
	}
	
	public void onEditSearch(ActionEvent event) {
		try {
			setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
			onEditSearchFinance(event);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onSearch(ActionEvent event) {
		
		try {
			onSearchFinance(event);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void onEditSearchFinance(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
		financeList.onEditSearch(event);
        financeList.setCriteria(getAvailableFinancesCriteria());
	}

	public void onSearchFinance(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
		financeList.clearCriteria();
		financeList.getCriteria().addEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_PAYMENT), false);
		financeList.getCriteria().addEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_REGISTRY_ID), getAgency().getId());
		financeList.getCriteria().addBetweenExpression(financeList.getFieldName(IEntityAlias.FINANCE_INVOICE_ISSUE_DATE), getStartDate(), getEndDate());
		financeList.onSearch(event);
	}
	
	private Criteria getAvailableFinancesCriteria() throws ManagerBeanException {

        FinanceListSearchListener financeSearch = (FinanceListSearchListener)AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_LIST_SEARCH_LISTENER_NAME);
		FinanceStatus[] financeStatuses = {FinanceStatus.PENDING, FinanceStatus.RETURNED};
		financeSearch.setFinanceStatuses(financeStatuses);

        FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_PAYMENT), true);
        for(AgencyFinance af: getFinanceList()){
        	criteria.addNotEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_ID), af.getFinance().getId());
        }
        
        
    	criteria.addOrder(financeList.getFieldName(IEntityAlias.FINANCE_DUE_DATE));
        criteria.addOrder(financeList.getFieldName(IEntityAlias.FINANCE_CONCEPT));
		return criteria;
	}
	
	public void onAddSelected(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
        Iterator<Finance> iterator = financeList.getCheckedFinances().iterator();
        while(iterator.hasNext()){
        	Finance finance = iterator.next();
        	AgencyFinance af = new AgencyFinance();
        	af.setFinance(finance);
        	af.setAmount(finance.getTotalAmount());
        	getFinanceList().add(af);
        }
        searchFinanceList(event);
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		for(AgencyFinance af: getCheckedFinances()){
			getFinanceList().remove(af);
		}
		clearCheckedFinances();
		searchFinanceList(event);
	}
	

	public void searchFinanceList(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
//		financeList.onEditSearch(event);
        financeList.setCriteria(getAvailableFinancesCriteria());
        financeList.onSearch(event);
        financeList.clearCheckedFinances();
	}

	private void batchFinances() {
//		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
//		boolean mustCloseSession = HibernateUtil.mustCloseSession();
//		String sessionName = HibernateUtil.getSessionFactoryName(FinancePaymentPrintController.class.getName());
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(Finance.class);
//
//			HibernateUtil.setBeginTransaction(false);
//			HibernateUtil.setCloseSession(false);
//			HibernateUtil.beginTransaction(sessionName);
//
//			for(Finance finance: getCheckedFinances()){
//				finance.setFinanceStatus(FinanceStatus.BATCHED);
//				bean.update(finance);
//				createFinanceTracking(finance);
//			}
//
//			HibernateUtil.getSession(sessionName).flush();
//			HibernateUtil.commitTransaction(sessionName);
//		} catch (Exception e) {
//			try {
//				HibernateUtil.rollbackTransaction(sessionName);
//			} catch (DAOException daoe) {
//				String msg =  "Unable to rollback transaction!";
//				throw new AbortProcessingException(msg);
//			}
//			String msg =  "Error batching finance. " + e.getMessage();
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg);
//		} finally {
//			HibernateUtil.closeSession(sessionName);
//			HibernateUtil.setCloseSession(mustCloseSession);
//			HibernateUtil.setBeginTransaction(mustBeginTransaction);
//		}
	}
	
	private void createFinanceTracking(Finance finance){
//		String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_PAYMENT_PRINT);
//		FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.BATCHED, message);
	}
	
	public class AgencyFinance{
		private Finance finance;
		private Double amount;
		public Finance getFinance() {
			return finance;
		}
		public void setFinance(Finance finance) {
			this.finance = finance;
		}
		public Double getAmount() {
			return amount;
		}
		public void setAmount(Double amount) {
			this.amount = amount;
		}
	}
		
}