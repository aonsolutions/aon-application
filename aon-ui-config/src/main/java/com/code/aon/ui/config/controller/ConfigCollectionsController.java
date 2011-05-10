package com.code.aon.ui.config.controller;

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
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.Tariff;
import com.code.aon.config.Tax;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public class ConfigCollectionsController {
	
	private List<SelectItem> taxTypes;
	private List<SelectItem> vatDeductionTypes;
	private List<SelectItem> withholdingTypes;
	private List<SelectItem> payMethodTypes;
	private List<SelectItem> invoiceTransactionTypes;
	private List<SelectItem> workGroupStatuses;
	private List<SelectItem> administrations;

	public List<SelectItem> getTaxTypes() {
		if (taxTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			taxTypes = new LinkedList<SelectItem>();
			for(TaxType type : TaxType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				taxTypes.add(item);
			}
		}
		return taxTypes;
	}

	public List<SelectItem> getVatDeductionTypes() {
		if (vatDeductionTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatDeductionTypes = new LinkedList<SelectItem>();
			for (VatDeductionType type : VatDeductionType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				vatDeductionTypes.add(item);
			}
		}
		return vatDeductionTypes;
	}
	
	public List<SelectItem> getWithholdingTypes() {
		if (withholdingTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			withholdingTypes = new LinkedList<SelectItem>();
			for (WithholdingType type : WithholdingType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				withholdingTypes.add(item);
			}
		}
		return withholdingTypes;
	}

	public List<SelectItem> getPayMethodTypes() {
		if (payMethodTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			payMethodTypes = new LinkedList<SelectItem>();
			for (PayMethodType type : PayMethodType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				payMethodTypes.add(item);
			}
		}
		return payMethodTypes;
	}

	public InvoiceTransactionType getInvoiceTransactionType() {
		return null;
	}

	public void setInvoiceTransactionType( InvoiceTransactionType invoiceTransactionType ) {
	}

	public List<SelectItem> getInvoiceTransactionTypes() {
		if (invoiceTransactionTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			invoiceTransactionTypes = new LinkedList<SelectItem>();
			for (InvoiceTransactionType type : InvoiceTransactionType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				invoiceTransactionTypes.add(item);
			}
		}
		return invoiceTransactionTypes;
	}

	public List<SelectItem> getWorkGroupStatuses() {
		if (workGroupStatuses == null) {
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

	public List<SelectItem> getAdministrations() {
		if (administrations == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			administrations = new LinkedList<SelectItem>();
			administrations.add(new SelectItem(Administration.COMMON_TERRITORY, Administration.COMMON_TERRITORY.getName(locale)));
			administrations.add(new SelectItem(Administration.ALAVA, Administration.ALAVA.getName(locale)));
			administrations.add(new SelectItem(Administration.BIZKAIA, Administration.BIZKAIA.getName(locale)));
			administrations.add(new SelectItem(Administration.GIPUZKOA, Administration.GIPUZKOA.getName(locale)));
			administrations.add(new SelectItem(Administration.NAVARRA, Administration.NAVARRA.getName(locale)));
		}
		return administrations;
	}
	
	public List<SelectItem> getTaxes() throws ManagerBeanException {
		List<SelectItem> taxes = new LinkedList<SelectItem>();
		for (ITransferObject ito : BeanManager.getManagerBean(Tax.class).getList(null)) {
			Tax tax = (Tax)ito;
			SelectItem item = new SelectItem(tax, tax.getName());
			taxes.add(item);
		}
		return taxes;
	}

	public List<SelectItem> getVatTaxes() throws ManagerBeanException {
		List<SelectItem> vatTaxes = new LinkedList<SelectItem>();
		IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(taxBean.getFieldName(IConfigAlias.TAX_TYPE), TaxType.VAT);
		for (ITransferObject ito : taxBean.getList(criteria)) {
			Tax tax = (Tax)ito;
			SelectItem item = new SelectItem(tax, tax.getName());
			vatTaxes.add(item);
		}
		return vatTaxes;
	}
	
	public List<SelectItem> getVatTaxesIds() throws ManagerBeanException {
		List<SelectItem> vatTaxes = new LinkedList<SelectItem>();
		IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(taxBean.getFieldName(IConfigAlias.TAX_TYPE), TaxType.VAT);
		for (ITransferObject ito : taxBean.getList(criteria)) {
			Tax tax = (Tax)ito;
			SelectItem item = new SelectItem(tax.getId(), tax.getName());
			vatTaxes.add(item);
		}
		return vatTaxes;
	}

	public List<SelectItem> getRetentionTaxes() throws ManagerBeanException {
		List<SelectItem> retentionTaxes = new LinkedList<SelectItem>();
		IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(taxBean.getFieldName(IConfigAlias.TAX_TYPE), TaxType.RETENTION);
		for (ITransferObject ito : taxBean.getList(criteria)) {
			Tax tax = (Tax)ito;
			SelectItem item = new SelectItem(tax, tax.getName());
			retentionTaxes.add(item);
		}
		return retentionTaxes;
	}

	public List<SelectItem> getRetentionTaxesIds() throws ManagerBeanException {
		List<SelectItem> retentionTaxes = new LinkedList<SelectItem>();
		IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(taxBean.getFieldName(IConfigAlias.TAX_TYPE), TaxType.RETENTION);
		for (ITransferObject ito : taxBean.getList(criteria)) {
			Tax tax = (Tax)ito;
			SelectItem item = new SelectItem(tax.getId(), tax.getName());
			retentionTaxes.add(item);
		}
		return retentionTaxes;
	}

	public List<SelectItem> getOfferSeries() throws ManagerBeanException{
		return getSeries(false, IConfigAlias.SERIES_OFFER);
	}

	public List<SelectItem> getOfferSeriesIds() throws ManagerBeanException {
		return getSeries(true, IConfigAlias.SERIES_OFFER);
	}	

	public List<SelectItem> getSalesSeries() throws ManagerBeanException{
		return getSeries(false, IConfigAlias.SERIES_SALES);
	}

	public List<SelectItem> getSalesSeriesIds() throws ManagerBeanException {
		return getSeries(true, IConfigAlias.SERIES_SALES);
	}	

	public List<SelectItem> getDeliverySeries() throws ManagerBeanException{
		return getSeries(false, IConfigAlias.SERIES_DELIVERY);
	}

	public List<SelectItem> getDeliverySeriesIds() throws ManagerBeanException {
		return getSeries(true, IConfigAlias.SERIES_DELIVERY);
	}	

	public List<SelectItem> getInvoiceSeries() throws ManagerBeanException{
		return getSeries(false, IConfigAlias.SERIES_INVOICE);
	}

	public List<SelectItem> getInvoiceSeriesIds() throws ManagerBeanException {
		return getSeries(true, IConfigAlias.SERIES_INVOICE);
	}	

	public List<SelectItem> getRectificationSeries() throws ManagerBeanException{
		return getSeries(false, IConfigAlias.SERIES_RECTIFICATION);
	}

	public List<SelectItem> getRectificationSeriesIds() throws ManagerBeanException {
		return getSeries(true, IConfigAlias.SERIES_RECTIFICATION);
	}	

	public List<SelectItem> getSeries(boolean onlyId, String typeAlias) throws ManagerBeanException {
		List<SelectItem> series = new LinkedList<SelectItem>();
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(typeAlias), new Boolean(true));
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ACTIVE), new Boolean(true));
		if (!AonUtil.getRoleManager().isConfidentiality()) {
			criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		}
		criteria.addOrder(seriesBean.getFieldName(IConfigAlias.SERIES_ID));
		for (ITransferObject ito : seriesBean.getList(criteria)) {
			Series serie = (Series)ito;
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
	
	public List<SelectItem> getScopes() throws ManagerBeanException {
		List<SelectItem> scopes = new LinkedList<SelectItem>();
		IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(scopeBean.getFieldName(IConfigAlias.SCOPE_DESCRIPTION));
		for (ITransferObject ito : scopeBean.getList(criteria)) {
			Scope scope = (Scope)ito;
			SelectItem item = new SelectItem(scope, scope.getDescription());
			scopes.add(item);
		}
		return scopes;
	}

	public List<SelectItem> getCurrentUserScopes() {
		List<SelectItem> currentUserScopes = new LinkedList<SelectItem>();
		for (Scope scope : UserUtils.getInstance().getCurrentUserScopes()) {
			SelectItem item = new SelectItem(scope, scope.getDescription());
			currentUserScopes.add(item);
		}
		return currentUserScopes;
	}

	public List<SelectItem> getWorkgroups() throws ManagerBeanException {
		List<SelectItem> workgroups = new LinkedList<SelectItem>(); 
		IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class); 
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
		criteria.addOrder(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_DESCRIPTION));
		for (ITransferObject ito : workGroupBean.getList(criteria)) {
			WorkGroup workGroup = (WorkGroup)ito;
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
		for (ITransferObject ito : workGroupBean.getList(criteria)) {
			WorkGroup workGroup = (WorkGroup)ito;
			SelectItem item = new SelectItem(workGroup, workGroup.getDescription());
			workgroups.add(item);
		}
		return workgroups;
	}

	public PayMethod getPayMethod() {
		return null;
	}

	public void setPayMethod( PayMethod payMethod ) {
	}

	public List<SelectItem> getPayMethods() throws ManagerBeanException {
		List<SelectItem> payMethods = new LinkedList<SelectItem>();
		IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(payMethodBean.getFieldName(IConfigAlias.PAY_METHOD_NAME));
		for (ITransferObject ito : payMethodBean.getList(criteria)) {
			PayMethod pMethod = (PayMethod)ito;
			SelectItem item = new SelectItem(pMethod, pMethod.getName());
			payMethods.add(item);
		}
		return payMethods;
	}

	public List<SelectItem> getPayMethodTypeDetails() throws ManagerBeanException {
		List<SelectItem> payMethodTypeDetails = new LinkedList<SelectItem>();
		IManagerBean payMethodTypeDetailBean = BeanManager.getManagerBean(PayMethodTypeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(payMethodTypeDetailBean.getFieldName(IConfigAlias.PAY_METHOD_TYPE_DETAIL_DESCRIPTION));
		for (ITransferObject ito : payMethodTypeDetailBean.getList(criteria)) {
			PayMethodTypeDetail p = (PayMethodTypeDetail)ito;
			SelectItem item = new SelectItem(p, p.getDescription());
			payMethodTypeDetails.add(item);
		}
		return payMethodTypeDetails;
	}

	public List<SelectItem> getTariffs() throws ManagerBeanException {
		List<SelectItem> tariffs = new LinkedList<SelectItem>();
		IManagerBean tariffBean = BeanManager.getManagerBean(Tariff.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(tariffBean.getFieldName(IConfigAlias.TARIFF_NAME));
		for (ITransferObject ito : tariffBean.getList(criteria)) {
			Tariff tariff = (Tariff)ito;
			SelectItem item = new SelectItem(tariff, tariff.getName());
			tariffs.add(item);
		}
		return tariffs;
	}

	public List<SelectItem> getCommissionTypes() throws ManagerBeanException {
		List<SelectItem> commissionTypes = new LinkedList<SelectItem>();
		IManagerBean commissionTypeBean = BeanManager.getManagerBean(CommissionType.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(commissionTypeBean.getFieldName(IConfigAlias.COMMISSION_TYPE_NAME));
		for (ITransferObject ito : commissionTypeBean.getList(criteria)) {
			CommissionType commissionType = (CommissionType)ito;
			SelectItem item = new SelectItem(commissionType, commissionType.getName());
			commissionTypes.add(item);
		}
		return commissionTypes;
	}

}