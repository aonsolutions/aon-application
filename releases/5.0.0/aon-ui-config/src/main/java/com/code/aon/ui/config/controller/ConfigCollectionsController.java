package com.code.aon.ui.config.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.CommissionType;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.Tariff;
import com.code.aon.config.Tax;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public class ConfigCollectionsController {
	
	private List<SelectItem> workGroupStatuses;
	
	private List<SelectItem> taxTypes;
	
	private List<SelectItem> payMethodTypes;

	private List<SelectItem> invoiceTransactionTypes;

	public List<SelectItem> getSeries() throws ManagerBeanException{
		return getSeries(false);		
	}

	public List<SelectItem> getSeriesIds() throws ManagerBeanException {
		return getSeries(true);
	}	

	@SuppressWarnings("unchecked")
	public List<SelectItem> getSeries(boolean onlyId) throws ManagerBeanException{
		List<SelectItem> series = new LinkedList<SelectItem>();
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ACTIVE), new Boolean(true));
		if (!AonUtil.getRoleManager().isConfidentiality()) {
			criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		}
		criteria.addOrder(seriesBean.getFieldName(IConfigAlias.SERIES_ID));
		Iterator iter = seriesBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Series serie = (Series)iter.next();
			SelectItem item;
			if (onlyId) {
				item = new SelectItem(serie.getId(), serie.getId()); 
			} else {
				item = new SelectItem(serie, serie.getId());
			} 
			series.add(item);
		}
		return series;
	}		
	
	public Scope getScope() {
		return null;
	}

	public void setScope( Scope scope ) {
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getScopes() throws ManagerBeanException {
		List<SelectItem> scopes = new LinkedList<SelectItem>();
		IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(scopeBean.getFieldName(IConfigAlias.SCOPE_DESCRIPTION));
		Iterator iter = scopeBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Scope scope = (Scope)iter.next();
			SelectItem item = new SelectItem(scope, scope.getDescription());
			scopes.add(item);
		}
		return scopes;
	}
	
	public List<SelectItem> getWorkgroups() throws ManagerBeanException {
		List<SelectItem> workgroups = new LinkedList<SelectItem>(); 
		IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class); 
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
		criteria.addOrder(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_DESCRIPTION));
		Iterator<ITransferObject> iter = workGroupBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			WorkGroup workGroup = (WorkGroup) iter.next();
			SelectItem item = new SelectItem(workGroup, workGroup.getDescription());
			workgroups.add(item);
		}
		return workgroups;
	}

	public List<SelectItem> getWorkgroupEntities() throws ManagerBeanException {
		List<SelectItem> workgroups = new LinkedList<SelectItem>(); 
		IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class); 
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
		criteria.addOrder(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_DESCRIPTION));
		Iterator<ITransferObject> iter = workGroupBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			WorkGroup workGroup = (WorkGroup) iter.next();
			SelectItem item = new SelectItem(workGroup, workGroup.getDescription());
			workgroups.add(item);
		}
		return workgroups;
	}

	public List<SelectItem> getWorkGroupStatuses() {
		if(workGroupStatuses == null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			workGroupStatuses = new LinkedList<SelectItem>();
			for (WorkGroupStatus status : WorkGroupStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				workGroupStatuses.add(item);
			}
		}
		return workGroupStatuses;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getCurrentUserScopes() {
		List<SelectItem> currentUserScopes = new LinkedList<SelectItem>();
		Iterator iter = UserUtils.getInstance().getCurrentUserScopes().iterator();
		while(iter.hasNext()){
			Scope scope = (Scope)iter.next();
			SelectItem item = new SelectItem(scope, scope.getDescription());
			currentUserScopes.add(item);
		}
		return currentUserScopes;
	}

	/** The taxes list. */
	private List<SelectItem> taxes;

	/**
	 * Gets the taxes.
	 * 
	 * @return the taxes
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getTaxes() throws ManagerBeanException {
		if (taxes == null) {
			taxes = new LinkedList<SelectItem>();
			List<ITransferObject> c = BeanManager.getManagerBean(Tax.class).getList(null);
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Tax tax = (Tax) iter.next();
				SelectItem item = new SelectItem(tax, tax.getName());
				taxes.add(item);
			}
		}
		return taxes;
	}

	/** The vat taxes list. */
	private List<SelectItem> vatTaxes;

	/**
	 * Gets the vat taxes.
	 * 
	 * @return the vat taxes
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getVatTaxes() throws ManagerBeanException {
		if (vatTaxes == null) {
			vatTaxes = new LinkedList<SelectItem>();
			IManagerBean managerBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.TAX_TYPE), TaxType.VAT);
			List<ITransferObject> c = managerBean.getList(criteria);  
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Tax tax = (Tax) iter.next();
				SelectItem item = new SelectItem(tax, tax.getName());
				vatTaxes.add(item);
			}
		}
		return vatTaxes;
	}
	
	/**
	 * Gets the vat taxes.
	 * 
	 * @return the vat taxes
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getVatTaxesIds() throws ManagerBeanException {
		if (vatTaxes == null) {
			vatTaxes = new LinkedList<SelectItem>();
			IManagerBean managerBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.TAX_TYPE), TaxType.VAT);
			List<ITransferObject> c = managerBean.getList(criteria);  
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Tax tax = (Tax) iter.next();
				SelectItem item = new SelectItem(tax.getId(), tax.getName());
				vatTaxes.add(item);
			}
		}
		return vatTaxes;
	}

	/** The retention taxes list. */
	private List<SelectItem> retentionTaxes;
	
	/**
	 * Gets the retention taxes.
	 * 
	 * @return the retention taxes
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getRetentionTaxes() throws ManagerBeanException {
		if (retentionTaxes == null) {
			retentionTaxes = new LinkedList<SelectItem>();
			IManagerBean managerBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.TAX_TYPE), TaxType.RETENTION);
			List<ITransferObject> c = managerBean.getList(criteria);  
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Tax tax = (Tax) iter.next();
				SelectItem item = new SelectItem(tax, tax.getName());
				retentionTaxes.add(item);
			}
		}
		return retentionTaxes;
	}
	public List<SelectItem> getRetentionTaxesIds() throws ManagerBeanException {
		if (retentionTaxes == null) {
			retentionTaxes = new LinkedList<SelectItem>();
			IManagerBean managerBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.TAX_TYPE), TaxType.RETENTION);
			List<ITransferObject> c = managerBean.getList(criteria);  
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Tax tax = (Tax) iter.next();
				SelectItem item = new SelectItem(tax.getId(), tax.getName());
				retentionTaxes.add(item);
			}
		}
		return retentionTaxes;
	}

	/**
	 * Gets the tax types
	 * 
	 * @return the tax types
	 */
	public List<SelectItem> getTaxTypes() {
		if ( taxTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			taxTypes = new LinkedList<SelectItem>();
			for( TaxType type : TaxType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				taxTypes.add(item);
			}
		}
		return taxTypes;
	}

	/**
	 * Fills and Returns PayMethod collection
	 * 
	 * @return PayMethod collection
	 * @throws ManagerBeanException
	 */
	public List<SelectItem> getPayMethods() throws ManagerBeanException {
		List<SelectItem> payMethods = new LinkedList<SelectItem>();
		IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(payMethodBean.getFieldName(IConfigAlias.PAY_METHOD_NAME));
		Iterator<?> iter = payMethodBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			PayMethod pMethod = (PayMethod) iter.next();
			SelectItem item = new SelectItem(pMethod, pMethod.getName());
			payMethods.add(item);
		}
		return payMethods;
	}

	/**
	 * Gets the tariffs.
	 * 
	 * @return the tariffs
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getTariffs() throws ManagerBeanException {
		List<SelectItem> tariffs = new LinkedList<SelectItem>();
		IManagerBean tariffBean = BeanManager.getManagerBean(Tariff.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(tariffBean.getFieldName(IConfigAlias.TARIFF_NAME));
		Iterator<ITransferObject> iter = tariffBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Tariff tariff = (Tariff) iter.next();
			SelectItem item = new SelectItem(tariff, tariff.getName());
			tariffs.add(item);
		}
		return tariffs;
	}

	/**
	 * Returns a list of PayMethodType
	 * 
	 * @return list of PayMethodType
	 */
	public List<SelectItem> getPayMethodTypes() {
		if ( payMethodTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			payMethodTypes = new LinkedList<SelectItem>();
			for ( PayMethodType type : PayMethodType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				payMethodTypes.add(item);
			}
		}
		return payMethodTypes;
	}
	
	public PayMethod getPayMethod() {
		return null;
	}

	public void setPayMethod( PayMethod payMethod ) {
	}

	public List<SelectItem> getInvoiceTransactionTypes() {
		if (invoiceTransactionTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			invoiceTransactionTypes = new LinkedList<SelectItem>();
			for (InvoiceTransactionType type:InvoiceTransactionType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				invoiceTransactionTypes.add(item);
			}
		}
		return invoiceTransactionTypes;
	}

	public InvoiceTransactionType getInvoiceTransactionType() {
		return null;
	}

	public void setInvoiceTransactionType( InvoiceTransactionType invoiceTransactionType ) {
	}

	public List<SelectItem> getCommissionTypes() throws ManagerBeanException {
		List<SelectItem> commissionTypes = new LinkedList<SelectItem>();
		IManagerBean commissionTypeBean = BeanManager.getManagerBean(CommissionType.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(commissionTypeBean.getFieldName(IConfigAlias.COMMISSION_TYPE_NAME));
		Iterator<ITransferObject> iter = commissionTypeBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			CommissionType commissionType = (CommissionType) iter.next();
			SelectItem item = new SelectItem(commissionType, commissionType.getName());
			commissionTypes.add(item);
		}
		return commissionTypes;
	}

}