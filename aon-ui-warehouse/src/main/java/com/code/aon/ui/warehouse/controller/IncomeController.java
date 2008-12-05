package com.code.aon.ui.warehouse.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.report.OutputFormat;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.dao.ISupplierAlias;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

/**
 * Controller for Income
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class IncomeController extends BasicController {
	
	/**
	 * The name of the controller of the company 
	 */
	private static final String COMPANY_CONTROLLER_NAME = "company";
	
	/**
	 * The price strategy linked
	 */
	private IPriceStrategy priceStrategy;
	
	/**
	 * Warehouse ident
	 */
	private Integer warehouseId;
	
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(IncomeController.class.getName());

	/**
	 * A list of incomes currently checked
	 */
	private ArrayList<Income> checkList= new ArrayList<Income>();
	
	/**
	 * Returns warehouse ident
	 * 
	 * @return warehouse ident
	 */
	public Integer getWarehouseId() {
		return warehouseId;
	}

	/**
	 * Assigns warehouse ident
	 * 
	 * @param warehouseId warehouse ident
	 */
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	
	/**
	 * Returns if the transfer object income can be modified
	 * If is closed can not be modified
	 * 
	 * @return if is closed returns false
	 */
	public boolean isEditable(){
		if((Income)this.getTo() != null && ((Income)this.getTo()).getIncomeStatus()!= null){
			if(((Income)this.getTo()).getIncomeStatus().equals(IncomeStatus.CLOSED)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns if the row data income can be modified
	 * If is closed can not be modified
	 * 
	 * @return if is closed returns false
	 * @throws ManagerBeanException
	 */
	public boolean isClosed() throws ManagerBeanException{
		return ((Income)this.getModel().getRowData()).getIncomeStatus().equals(IncomeStatus.CLOSED);
	}
	
	/**
	 * Returns if the Income is already in the checklist
	 * 
	 * @return true if exists in the checklist
	 */ 
	public boolean getRowChecked() {
		Income to = (Income) model.getRowData();
		return checkList.contains( to );
	}
	
	/**
	 * Add or remove current row from the checklist
	 * if param is true adds to the list else removes
	 * 
	 * @param rowChecked true to add and false to remove
	 */
	public void setRowChecked(boolean rowChecked) {
		if ( rowChecked ) {
			Income to = (Income) model.getRowData();
			if (!checkList.contains( to )) {
				checkList.add( to );
			}
		} else {
			Income to = (Income) model.getRowData();
			if (checkList.contains( to )) {
				checkList.remove( to );
			}
		}
	}
	
	/**
	 * Adds the transfer object to the checklist 
	 * 
	 * @param to transfer object
	 */
	public void addToCheckList(ITransferObject to){
		if(!checkList.contains( to )){
			checkList.add( (Income)to );
		}
	}
	
	/**
	 * Changes current row chacked status
	 * 
	 * @param event event containing the value
	 */
	public void rowSelected(ValueChangeEvent event){
		if(event.getNewValue() != null){
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	/**
	 * Returns if checklist contains this trnasfer object 
	 * 
	 * @param to transfer object
	 * @return true if exists
	 */
	public boolean isChecked(ITransferObject to){
		return checkList.contains( to );
	}
	
	/**
	 * Returns the size of the checklist
	 * 
	 * @return the size
	 */
	public int getCheckListSize(){
		return checkList.size();
	}
	
	/**
	 * Inits the check list
	 */
	public void clearCheckList(){
		this.checkList = new ArrayList<Income>();
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onReset(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onReset(ActionEvent event) {
		try {
			this.setModel(new PageDataModel(this,0,20));
			this.clearCriteria();
			super.onReset(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error getting model onSelect", e);
		}
	}

	/**
	 * Load the income related to this parameters value
	 * 
	 * @param event the event that contains the value
	 * @throws ManagerBeanException
	 */
	public void loadIncome(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null){
			loadIncome((Integer)event.getNewValue());
		}
	}
	
	/**
	 * Load the income for this suppliers ident and status pending
	 * 
	 * @param supplierId suppliers ident
	 * @throws ManagerBeanException
	 */
	public void loadIncome(Integer supplierId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBean().getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), supplierId);
		criteria.addEqualExpression(getManagerBean().getFieldName(IWarehouseAlias.INCOME_INCOME_STATUS),IncomeStatus.PENDING);
		this.setCriteria(criteria);
		super.onSearch(null);
	}
	
	/**
	 * Returns rows base price for this price strategy
	 * 
	 * @return base price
	 * @throws ManagerBeanException
	 */
	public double getIncomeBasePrice() throws ManagerBeanException{
		return getPriceStrategy().getTaxableBase((ICalculableContainer)this.getModel().getRowData());
	}
	
	/**
	 * Returns rows total price for this price strategy
	 * 
	 * @return total price
	 * @throws ManagerBeanException
	 */
	public double getIncomeTotalPrice() throws ManagerBeanException{
		CompanyController companyController = (CompanyController)FormUtil.getController(COMPANY_CONTROLLER_NAME);
		return getPriceStrategy().getTotalPrice((ICalculableContainer)this.getModel().getRowData(),companyController.obtainCompany());
	}
	
	/**
	 * Returns total tax rate for this price strategy
	 * 
	 * @return total tax rate
	 * @throws ManagerBeanException
	 */
	public double getIncomeTotalTaxRate() throws ManagerBeanException {
		CompanyController companyController = (CompanyController)FormUtil.getController(COMPANY_CONTROLLER_NAME);
		Income income = (Income)this.getModel().getRowData();
		Iterator<TaxBreakDown> iter = getPriceStrategy().getTaxBreakDowns(income,companyController.obtainCompany()).iterator();
		double total = 0;
		while(iter.hasNext()){
			total += iter.next().getTaxQuota();
		}
		return total;
	}
	
	/**
	 * Returns total surcharge rate for this price strategy
	 * 
	 * @return total surcharge rate
	 * @throws ManagerBeanException
	 */
	public double getIncomeTotalSurchargeRate() throws ManagerBeanException {
		CompanyController companyController = (CompanyController)FormUtil.getController(COMPANY_CONTROLLER_NAME);
		Income income = (Income)this.getModel().getRowData();
		Iterator<TaxBreakDown> iter = getPriceStrategy().getTaxBreakDowns(income,companyController.obtainCompany()).iterator();
		double total = 0;
		while(iter.hasNext()){
			total += iter.next().getSurchargeQuota();
		}
		return total;
	}
	
	/**
	 * Assigns the supplier searched instead of events value
	 * and assigns it to the income
	 * 
	 * @param event contains suppliers ident
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public void supplierData(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(supplierBean.getFieldName(ISupplierAlias.SUPPLIER_ID),event.getNewValue());
			Iterator iter = supplierBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Supplier supplier = (Supplier)iter.next();
				((Income)this.getTo()).setSupplier(supplier);
			}
		}
	}
	
	/**
	 * Returns price strategy for this class
	 * 
	 * @return price strategy
	 */
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#getCollection()
	 */
	@SuppressWarnings("unchecked")
	public Collection getCollection(){
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		l.add(obtainIncome(((Income)this.getTo()).getId()));
		return l;
	}

	/**
	 * Returns the income with this income ident
	 * 
	 * @param incomeId income ident
	 * @return the income searched
	 */
	@SuppressWarnings("unchecked")
	private ITransferObject obtainIncome(Integer incomeId) {
		try {
			IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_ID), incomeId);
			Iterator iter = incomeBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Income)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining income with id= " + incomeId, e);
		}
		return null;
	}
	
	/**
	 * Adds a criteria greater than or equal to the issue date
	 * 
	 * @param event contains the value
	 * @throws ManagerBeanException
	 */
	public void addIssueDateGreaterThanExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(IWarehouseAlias.INCOME_ISSUE_TIME), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds a criteria less than or equal to the issue date
	 * 
	 * @param event contains the value
	 * @throws ManagerBeanException
	 */
	public void addIssueDateLessThanExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addLessThanOrEqualExpression(getFieldName(IWarehouseAlias.INCOME_ISSUE_TIME), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Searchs instead of the suppliers's document
	 * 
	 * @param event constains the suppliers document
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */
	@SuppressWarnings("unchecked")
	public void addSupplierExpression(ValueChangeEvent event)
		throws ManagerBeanException, ExpressionException {
	    if ((event.getNewValue() != null)
	    		&& (!"".equals(event.getNewValue().toString().trim()))
	    		) {
			Criteria criteria = new Criteria();
			IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
			String identifier = bean.getFieldName(ISupplierAlias.SUPPLIER_REGISTRY_DOCUMENT);
			criteria.addEqualExpression(identifier, event.getNewValue());
			Iterator iter = bean.getList(criteria).iterator();
	    	Criteria c = getCriteria();
	    	if (iter.hasNext()){
				while (iter.hasNext()){
					Supplier t = (Supplier)iter.next();
					Object value = t.getId(); 
					c.addEqualExpression(getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), value);
					setCriteria(c);
				}
	    	}else{
				Object value = new Integer(-1); 
				c.addEqualExpression(getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), value);
				setCriteria(c);
	    	}
		}
	}
	
	/**
	 * Adds equal expression
	 * 
	 * @param event constains the value
	 * @throws ManagerBeanException
	 */
	public void addEqualExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addEqualExpression(getFieldName(event.getComponent().getId()), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Searchs instead of the supplier's ident
	 * 
	 * @param event constains the suppliers ident
	 */
	public void addSupplierEqualExpression(ValueChangeEvent event){
		if(event.getNewValue() != null && !((String)event.getNewValue()).trim().equals("")){
			try{
				Integer id = new Integer((String)event.getNewValue());
				Criteria criteria = getCriteria();
				criteria.addEqualExpression(getManagerBean().getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), id);
				setCriteria(criteria);
			} catch (Exception e) {
			}
		}
	}

    /**
     * Sets default parameters to report.
     * 
     * @param event that launched report
     */
	@SuppressWarnings("unused")
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("income");
        manager.setOutputFormat(OutputFormat.PDF);
    }

}