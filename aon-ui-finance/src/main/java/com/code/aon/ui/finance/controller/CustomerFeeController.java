package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.NO_FEE_CUSTOMER_REPORT;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.server.AonDateUtils;

public class CustomerFeeController extends LinesController implements IFinanceConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean longDescription;
	private IPriceStrategy priceStrategy;
	private DataScrollerState noFeeCustomersState;
	private List<Customer> noFeeCustomersList;
	
	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public DataModel getNoFeeCustomersModel() {
		return getNoFeeCustomersState().getDirectModel();
	}

	public void setNoFeeCustomersModel(DataModel noFeeCustomersModel) {
		if ( noFeeCustomersModel == null ) {
			setNoFeeCustomersState(null);
		} else {
			getNoFeeCustomersState().setModel(noFeeCustomersModel);
		}					
	}
	
	public DataScrollerState getNoFeeCustomersState() {
		if (noFeeCustomersState == null) {
			noFeeCustomersState = new DataScrollerState(new SerializableListDataModel(getNoFeeCustomersList()), "yearsStats");
		}				
		return noFeeCustomersState;
	}

	public void setNoFeeCustomersState(DataScrollerState noFeeCustomersState) {
		this.noFeeCustomersState = noFeeCustomersState;
	}

	public List<Customer> getNoFeeCustomersList() {
		return noFeeCustomersList;
	}

	public void setNoFeeCustomersList(List<Customer> noFeeCustomersList) {
		this.noFeeCustomersList = noFeeCustomersList;
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		CustomerFee customerFee = (CustomerFee)getTo();
		if (StringUtils.equals(customerFee.getItem().getFullName().trim(), customerFee.getDescription().trim())) {
			String longDescription = customerFee.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				customerFee.setDescription(customerFee.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public void onItemChanged(LookupChangeEvent event) {
		CustomerFee fee = (CustomerFee)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			fee.setItem(item);
			fee.setDescription(item.getFullName());

			Customer customer = (Customer)getMasterController().getTo();
			price = getPriceStrategy().getUnitPrice(fee, fee.getInitialDate(), customer);
		}
		fee.setPrice(price);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		CustomerFee fee = (CustomerFee)getTo();
		if (fee.getItem() != null && fee.getItem().getId() != null) {
			double price = 0;
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				fee.setQuantity((Double)event.getNewValue());
	
				Customer customer = (Customer)getMasterController().getTo();
				price = getPriceStrategy().getUnitPrice(fee, fee.getInitialDate(), customer);
			}
			fee.setPrice(price);
		}
	}

	public List<SelectItem> getProjects() throws ManagerBeanException {
		Customer customer = (Customer)getMasterController().getTo();
		List<SelectItem> projects = new LinkedList<SelectItem>();
		IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), customer.getId());
		criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_ACTIVE), Boolean.TRUE);
		criteria.addOrder(projectBean.getFieldName(IEntityAlias.PROJECT_NAME));
		for (ITransferObject ito : projectBean.getList(criteria)) {
			Project project = (Project)ito;
			SelectItem item = new SelectItem(project, project.getName());
			projects.add(item);
		}
		return projects;
	}

	public boolean isInvoicingGroupInMyScopes() {
		CustomerFee fee = (CustomerFee)getTo();
		return UserUtils.getInstance().isScopeInUserScopes(fee.getInvoicingGroup().getCustomer().getScope());
	}

	public boolean isModelInvoicingGroupInMyScopes() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			CustomerFee fee = (CustomerFee)getModel().getRowData();
			return UserUtils.getInstance().isScopeInUserScopes(fee.getInvoicingGroup().getCustomer().getScope());
		}
		return false;
	}

	public void removeProject(ActionEvent event) throws ManagerBeanException {
		CustomerFee customerFee = (CustomerFee)getTo();
		customerFee.setProject(null);
	}

	public String getReportTitle(){
		return AonUtil.getMessage(NO_FEE_CUSTOMER_REPORT);
	}

	@SuppressWarnings("unchecked")
	public void onNoFeeCustomers(ActionEvent event) {
		String select = "select distinct(customer) "
			+ " from Customer as customer "
			+ " where " + DomainManager.getSQLWhereClause("customer.domain") 
			+ " and customer.status= 0 AND ( "
			+ " customer.id NOT IN (select customerFee.customer.id from CustomerFee as customerFee) "
			+ " AND "
			+ "customer.id NOT IN (select customerFee.customer.id from CustomerFee as customerFee where finalDate < '"
			+ AonDateUtils.format(new Date(), "yyyy-MM-dd") + "')))";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		noFeeCustomersList = query.list();
	}

	public void onLoadPrepayment(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			CustomerFee fee = (CustomerFee)getModel().getRowData();
			BasicController prepaymentController = (BasicController)AonUtil.getRegisteredBean(PREPAYMENT_CONTROLLER_NAME);
			prepaymentController.onLoad(event, fee.getPrepaymentId(), CUSTOMER_FORM_NAME, CUSTOMER_FEE_CONTROLLER_NAME + ".onSearch");
		}
	}

}
