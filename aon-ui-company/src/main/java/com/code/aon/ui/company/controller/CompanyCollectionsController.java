package com.code.aon.ui.company.controller;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.Department;
import com.code.aon.company.Enterprise;
import com.code.aon.company.InvestAsset;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.enumeration.EnterpriseSalaryTemplate;
import com.code.aon.company.enumeration.FinancePaymentTemplate;
import com.code.aon.company.enumeration.InvestAssetRegime;
import com.code.aon.company.enumeration.InvestAssetType;
import com.code.aon.company.enumeration.ItemTagTemplate;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.company.enumeration.SalarySendingMethod;
import com.code.aon.company.enumeration.SalaryTemplate;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseActivity;

public class CompanyCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> salarySendingMethods;
	private List<SelectItem> enterpriseSalaryTemplates;
	private List<SelectItem> salaryTemplates;
	private List<SelectItem> saleInvoiceTemplates;
	private List<SelectItem> reportPrintOptions;
	private List<SelectItem> simpleReportPrintOptions;
	private List<SelectItem> financePaymentTemplate;
	private List<SelectItem> itemTagTemplate;
	private List<SelectItem> investAssetTypes;
	private List<SelectItem> investAssetRegimes;
	
	public List<SelectItem> getItemTagTemplate(){
		if (itemTagTemplate == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			itemTagTemplate = new LinkedList<SelectItem>();
			ItemTagTemplate[] list = ItemTagTemplate.values();
			for (ItemTagTemplate o : list) {
				String name = o.getName(locale);
				SelectItem item = new SelectItem(o, name);
				itemTagTemplate.add(item);
			}
		}
		return itemTagTemplate;
	}
	public List<SelectItem> getFinancePaymentTemplates(){
		if (financePaymentTemplate == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financePaymentTemplate = new LinkedList<SelectItem>();
			FinancePaymentTemplate[] list = FinancePaymentTemplate.values();
			for (FinancePaymentTemplate o : list) {
				String name = o.getName(locale);
				SelectItem item = new SelectItem(o, name);
				financePaymentTemplate.add(item);
			}
		}
		return financePaymentTemplate;
	}
	
	public List<SelectItem> getShortReportPrintOptions() {
		if (simpleReportPrintOptions == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			simpleReportPrintOptions = new LinkedList<SelectItem>();
			ReportPrintOption[] list = ReportPrintOption.values();
			for (ReportPrintOption o : list) {
				if(o!=ReportPrintOption.LEFT_SIDE){
					String name = o.getName(locale);
					SelectItem item = new SelectItem(o, name);
					simpleReportPrintOptions.add(item);
				}
			}
		}
		return simpleReportPrintOptions;
	}
	
	public List<SelectItem> getReportPrintOptions() {
		if (reportPrintOptions == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			reportPrintOptions = new LinkedList<SelectItem>();
			ReportPrintOption[] list = ReportPrintOption.values();
			for (ReportPrintOption o : list) {
				String name = o.getName(locale);
				SelectItem item = new SelectItem(o, name);
				reportPrintOptions.add(item);
			}
		}
		return reportPrintOptions;
	}
	
	public List<SelectItem> getSaleInvoiceTemplates() {
		if (saleInvoiceTemplates == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
			saleInvoiceTemplates = new LinkedList<SelectItem>();
			SaleInvoiceTemplate[] st = SaleInvoiceTemplate.values();
			for (SaleInvoiceTemplate template : st) {
				boolean skip = false;
				
				Integer domainId = ds.getDomainId();
				if ( domainId != null ) {
					Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
					try {
						if ( template == SaleInvoiceTemplate.GTA && !AuditManager.hasModule(domainId, appId, Module.GARAGE)){
							skip = true;
						} else if ( template == SaleInvoiceTemplate.HOTEL && !AuditManager.hasModule(domainId, appId, Module.HOTEL)){
							skip = true;
						}
					} catch (Throwable e) {
						skip = true;
					}								
				}
				
				if (! skip ) {
					String name = template.getName(locale);
					SelectItem item = new SelectItem(template, name);
					saleInvoiceTemplates.add(item);					
				}
			}
		}
		return saleInvoiceTemplates;
	}
	
	@Deprecated
	public List<SelectItem> getSalaryTemplates() {
		if (salaryTemplates == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			salaryTemplates = new LinkedList<SelectItem>();
			SalaryTemplate[] st = SalaryTemplate.values();
			for (SalaryTemplate c : st) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c.getValue(), name);
				salaryTemplates.add(item);
			}
		}
		return salaryTemplates;
	}
	
	public List<SelectItem> getSalarySendingMethods() {
		if (salarySendingMethods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			salarySendingMethods = new LinkedList<SelectItem>();
			for( SalarySendingMethod type : SalarySendingMethod.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type.name(), name);
				salarySendingMethods.add(item);			
			}
		}
		return salarySendingMethods;
	}	
	
	public List<SelectItem> getEnterpriseSalaryTemplates() {
		if (enterpriseSalaryTemplates == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			enterpriseSalaryTemplates = new LinkedList<SelectItem>();
			for( EnterpriseSalaryTemplate type : EnterpriseSalaryTemplate.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type.getValue(), name);
				enterpriseSalaryTemplates.add(item);			
			}
		}
		return enterpriseSalaryTemplates;
	}	
	
    public List<SelectItem> getCompanyAddresses() throws ManagerBeanException {
    	LinkedList<SelectItem> addresses = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if(iterator.hasNext()) {
    		Company company = (Company)iterator.next();
    		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), company.getId());
    		criteria.addOrder(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS));
    		List<ITransferObject> list = registryAddressBean.getList(criteria);
    		for (ITransferObject to : list) {
    			RegistryAddress address = (RegistryAddress)to;
				String addressLabel = address.getFullAddress();
				addressLabel = ((addressLabel.length()>30)?addressLabel.substring(0,27)+"...":addressLabel);
				if ( address.getGeozone() != null ) {
					addressLabel += " - " + address.getGeozone().getName();
				}
				addressLabel = ((addressLabel.length()>48)?addressLabel.substring(0,45)+"...":addressLabel);
				SelectItem item = new SelectItem(address, addressLabel);
				addresses.add(item);
    		}
    	}
    	return addresses;
    }

    public List<SelectItem> getAllCompanyBanks() throws ManagerBeanException {
    	LinkedList<SelectItem> banks = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if(iterator.hasNext()) {
    		Company company = (Company)iterator.next();
    		IManagerBean registryBankBean = BeanManager.getManagerBean(RegistryBank.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), company.getId());
    		criteria.addOrder(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_ID));
    		List<ITransferObject> list = registryBankBean.getList(criteria);
    		for (ITransferObject to : list) {
    			RegistryBank rBank = (RegistryBank)to;
    			banks.add(new SelectItem(rBank, rBank.getFullName()));
    		}
    	}
    	return banks;
    }

    public List<SelectItem> getActiveCompanyBanks() throws ManagerBeanException {
    	LinkedList<SelectItem> banks = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if(iterator.hasNext()) {
    		Company company = (Company)iterator.next();
    		IManagerBean registryBankBean = BeanManager.getManagerBean(RegistryBank.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), company.getId());
    		criteria.addEqualExpression(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_ACTIVE), true);
    		criteria.addOrder(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_ID));
    		List<ITransferObject> list = registryBankBean.getList(criteria);
    		for (ITransferObject to : list) {
    			RegistryBank rBank = (RegistryBank)to;
    			banks.add(new SelectItem(rBank, rBank.getFullName()));
    		}
    	}
    	return banks;
    }

    public Enterprise getEnterprise() {
		return null;
	}

	public void setEnterprise(Enterprise enterprise) {
	}

	public List<SelectItem> getCurrentUserEnterprises() throws ManagerBeanException {
		List<SelectItem> enterprises = new LinkedList<SelectItem>();
   		IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
   		Criteria criteria = new Criteria();
   		UserUtils.getInstance().addScopeFilterToCriteria(criteria, enterpriseBean.getFieldName(IEntityAlias.ENTERPRISE_SCOPE_ID));
    	criteria.addOrder(enterpriseBean.getFieldName(IEntityAlias.ENTERPRISE_REGISTRY_NAME));
    	List<ITransferObject> list = enterpriseBean.getList(criteria);
    	for (ITransferObject to : list) {
    		Enterprise enterprise = (Enterprise)to;
    		enterprises.add(new SelectItem(enterprise, enterprise.getRegistry().getFullName()));
    	}
		return enterprises;
	}	

	public int getCurrentUserEnterprisesCount() throws ManagerBeanException {
		IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
		Criteria criteria = new Criteria();
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, enterpriseBean.getFieldName(IEntityAlias.ENTERPRISE_SCOPE_ID));
		return enterpriseBean.getCount(criteria);
	}
	
    public WorkPlace getWorkPlace() {
		return null;
	}

	public void setWorkPlace(WorkPlace workPlace) {
	}

	public int getWorkPlacesCount() throws ManagerBeanException {
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), true);
		return workPlaceBean.getCount(criteria);
	}
	
	public List<SelectItem> getCurrentUserWorkPlaces() throws ManagerBeanException {
		List<SelectItem> workPlaces = new LinkedList<SelectItem>();
    	for(ITransferObject to: getCurrentUserWorkPlaceList()){
    		WorkPlace workPlace = (WorkPlace)to;
    		workPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
    	}
		return workPlaces;
	}	

	public int getCurrentUserWorkPlacesCount() throws ManagerBeanException {
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), true);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_SCOPE_ID));
		return workPlaceBean.getCount(criteria);
	}
	
	public List<ITransferObject> getCurrentUserWorkPlaceList() throws ManagerBeanException {
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), new Boolean(true));
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_SCOPE_ID));
		criteria.addOrder(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_DESCRIPTION));
		return workPlaceBean.getList(criteria);
	}
	
	public List<Integer> getCurrentUserWorkPlacesIds() throws ManagerBeanException {
		List<Integer> list = new LinkedList<Integer>();
		for(ITransferObject to: getCurrentUserWorkPlaceList()){
			WorkPlace wp = (WorkPlace) to;
			list.add(wp.getId());
		}
		return list;
	}
	
	public List<SelectItem> getDepartments() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Department.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IEntityAlias.DEPARTMENT_NAME));
		for (ITransferObject ito : bean.getList(criteria)) {
			Department d = (Department)ito;
			SelectItem item = new SelectItem(d, d.getName());
			list.add(item);
		}
		return list;
	}
	
	public int getDepartmentsCount() throws ManagerBeanException {
		return BeanManager.getManagerBean(Department.class).getCount(null);
	}
	
	public List<SelectItem> getCompanyActivities() throws ManagerBeanException {
		List<SelectItem> activities = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if (iterator.hasNext()) {
    		Company company = (Company)iterator.next();
			IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), company.getId());
			criteria.addOrder(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_PRINCIPAL), Boolean.FALSE);
			criteria.addOrder(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_END_DATE));
			criteria.addOrder(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_DESCRIPTION));
			for (ITransferObject ito : activityBean.getList(criteria)) {
				EnterpriseActivity activity = (EnterpriseActivity)ito;
				SelectItem item = new SelectItem(activity, activity.getDescription());
				activities.add(item);
			}
    	}
		return activities;
	}

	public int getCompanyActivitiesCount() throws ManagerBeanException {
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if (iterator.hasNext()) {
    		Company company = (Company)iterator.next();
			IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), company.getId());
			return activityBean.getCount(criteria);
    	}
		return 0;
	}

	public List<SelectItem> getActiveCompanyActivities() throws ManagerBeanException {
		List<SelectItem> activities = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if (iterator.hasNext()) {
    		Company company = (Company)iterator.next();
			IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), company.getId());
			criteria.addNullExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_END_DATE));
			criteria.addOrder(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_PRINCIPAL), Boolean.FALSE);
			criteria.addOrder(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_DESCRIPTION));
			for (ITransferObject ito : activityBean.getList(criteria)) {
				EnterpriseActivity activity = (EnterpriseActivity)ito;
				SelectItem item = new SelectItem(activity, activity.getDescription());
				activities.add(item);
			}
    	}
		return activities;
	}

	public int getActiveCompanyActivitiesCount() throws ManagerBeanException {
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if (iterator.hasNext()) {
    		Company company = (Company)iterator.next();
			IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), company.getId());
			criteria.addNullExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_END_DATE));
			return activityBean.getCount(criteria);
    	}
		return 0;
	}

	public List<SelectItem> getCompanyInvestAssets() throws ManagerBeanException {
		List<SelectItem> investAssets = new LinkedList<SelectItem>();
		IManagerBean investAssetBean = BeanManager.getManagerBean(InvestAsset.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(investAssetBean.getFieldName(IEntityAlias.INVEST_ASSET_DESCRIPTION));
		for (ITransferObject ito : investAssetBean.getList(criteria)) {
			InvestAsset investAsset = (InvestAsset)ito;
			SelectItem item = new SelectItem(investAsset, investAsset.getDescription());
			investAssets.add(item);
		}
		return investAssets;
	}

	public int getCompanyInvestAssetsCount() throws ManagerBeanException {
		IManagerBean investAssetBean = BeanManager.getManagerBean(InvestAsset.class);
		return investAssetBean.getCount(null);
	}

	public List<SelectItem> getActiveCompanyInvestAssets() throws ManagerBeanException {
		List<SelectItem> investAssets = new LinkedList<SelectItem>();
		IManagerBean investAssetBean = BeanManager.getManagerBean(InvestAsset.class);
		Criteria criteria = new Criteria();
		criteria.addNullExpression(investAssetBean.getFieldName(IEntityAlias.INVEST_ASSET_END_DATE));
		criteria.addOrder(investAssetBean.getFieldName(IEntityAlias.INVEST_ASSET_DESCRIPTION));
		for (ITransferObject ito : investAssetBean.getList(criteria)) {
			InvestAsset investAsset = (InvestAsset)ito;
			SelectItem item = new SelectItem(investAsset, investAsset.getDescription());
			investAssets.add(item);
		}
		return investAssets;
	}

	public int getActiveCompanyInvestAssetsCount() throws ManagerBeanException {
		IManagerBean investAssetBean = BeanManager.getManagerBean(InvestAsset.class);
		Criteria criteria = new Criteria();
		criteria.addNullExpression(investAssetBean.getFieldName(IEntityAlias.INVEST_ASSET_END_DATE));
		return investAssetBean.getCount(criteria);
	}

	public List<SelectItem> getInvestAssetTypes() {
		if (investAssetTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			investAssetTypes = new LinkedList<SelectItem>();
			for (InvestAssetType type : InvestAssetType.values()) {
				SelectItem item = new SelectItem(type, type.getName(locale));
				investAssetTypes.add(item);
			}
		}
		return investAssetTypes;
	}

	public List<SelectItem> getInvestAssetRegimes() {
		if (investAssetRegimes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			investAssetRegimes = new LinkedList<SelectItem>();
			for (InvestAssetRegime regime : InvestAssetRegime.values()) {
				SelectItem item = new SelectItem(regime, regime.getName(locale));
				investAssetRegimes.add(item);
			}
		}
		return investAssetRegimes;
	}

}

