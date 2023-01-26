package com.code.aon.ui.config.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Catalogue;
import com.code.aon.config.CommissionType;
import com.code.aon.config.PayMethod;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.Tag;
import com.code.aon.config.Tariff;
import com.code.aon.config.TariffAddInfo;
import com.code.aon.config.Tax;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.enumeration.TagType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.Toolbar;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.config.enumeration.WithholdingTypeGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ConfigCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<SelectItem> taxTypes;
	private List<SelectItem> vatDeductionTypes;
	private List<SelectItem> withholdingTypes;
	private List<SelectItem> payMethodTypes;
	private List<SelectItem> invoiceTransactionTypes;
	private List<SelectItem> workGroupStatuses;
	private List<SelectItem> administrations;
	private List<SelectItem> toolbars;
	private List<SelectItem> tagTypes;
	private List<SelectItem> payMethodTypeForDetails;
	private Scope emptyScope;
	private Tag emptyTag;

	public List<SelectItem> getTaxTypes() {
		if (taxTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			taxTypes = new LinkedList<SelectItem>();
			for(TaxType type : TaxType.values()) {
				if (type != TaxType.UNKNOWN) {
					String name = type.getName(locale);
					SelectItem item = new SelectItem(type, name);
					taxTypes.add(item);
				}
			}
		}
		return taxTypes;
	}

	public List<SelectItem> getVatDeductionTypes() {
		if (vatDeductionTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
			withholdingTypes = new LinkedList<>();
			for (WithholdingTypeGroup typeGroup : WithholdingTypeGroup.values()) {
				List<SelectItem> wtList = new LinkedList<>();
				for (WithholdingType type : WithholdingType.ORDERED_VALUES) {
					if ( type.getGroup() == typeGroup) {
						String name = type.getAbbreviatedDescription() + ". " + type.getName(locale);
						SelectItem item = new SelectItem(type, name);
						wtList.add(item);
					}
				}
				if (!wtList.isEmpty()) {
					SelectItemGroup itemGroup = new SelectItemGroup( typeGroup.getDescription() );
					itemGroup.setSelectItems( wtList.toArray( new SelectItem[wtList.size()] ) );
					withholdingTypes.add(itemGroup);
				}
			}
		}
		return withholdingTypes;
	}

	public List<SelectItem> getPayMethodTypes() {
		if (payMethodTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
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
		criteria.addEqualExpression(taxBean.getFieldName(IEntityAlias.TAX_TYPE), TaxType.VAT);
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
		criteria.addEqualExpression(taxBean.getFieldName(IEntityAlias.TAX_TYPE), TaxType.VAT);
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
		criteria.addEqualExpression(taxBean.getFieldName(IEntityAlias.TAX_TYPE), TaxType.RETENTION);
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
		criteria.addEqualExpression(taxBean.getFieldName(IEntityAlias.TAX_TYPE), TaxType.RETENTION);
		for (ITransferObject ito : taxBean.getList(criteria)) {
			Tax tax = (Tax)ito;
			SelectItem item = new SelectItem(tax.getId(), tax.getName());
			retentionTaxes.add(item);
		}
		return retentionTaxes;
	}

	public List<SelectItem> getTasSeries() throws ManagerBeanException{
		return getSeries(false, IEntityAlias.SERIES_TAS);
	}

	public List<SelectItem> getTasSeriesIds() throws ManagerBeanException {
		return getSeries(true, IEntityAlias.SERIES_TAS);
	}	

	public List<SelectItem> getOfferSeries() throws ManagerBeanException{
		return getSeries(false, IEntityAlias.SERIES_OFFER);
	}

	public List<SelectItem> getOfferSeriesIds() throws ManagerBeanException {
		return getSeries(true, IEntityAlias.SERIES_OFFER);
	}	

	public List<SelectItem> getSalesSeries() throws ManagerBeanException{
		return getSeries(false, IEntityAlias.SERIES_SALES);
	}

	public List<SelectItem> getSalesSeriesIds() throws ManagerBeanException {
		return getSeries(true, IEntityAlias.SERIES_SALES);
	}	

	public List<SelectItem> getDeliverySeries() throws ManagerBeanException{
		return getSeries(false, IEntityAlias.SERIES_DELIVERY);
	}

	public List<SelectItem> getDeliverySeriesIds() throws ManagerBeanException {
		return getSeries(true, IEntityAlias.SERIES_DELIVERY);
	}	

	public List<SelectItem> getInvoiceSeries() throws ManagerBeanException{
		return getSeries(false, IEntityAlias.SERIES_INVOICE);
	}

	public List<SelectItem> getInvoiceSeriesIds() throws ManagerBeanException {
		return getSeries(true, IEntityAlias.SERIES_INVOICE);
	}	

	public List<SelectItem> getRectificationSeries() throws ManagerBeanException{
		return getSeries(false, IEntityAlias.SERIES_RECTIFICATION);
	}

	public List<SelectItem> getRectificationSeriesIds() throws ManagerBeanException {
		return getSeries(true, IEntityAlias.SERIES_RECTIFICATION);
	}	

	public List<SelectItem> getPosSeries() throws ManagerBeanException{
		return getSeries(false, IEntityAlias.SERIES_POS);
	}

	public List<SelectItem> getPosSeriesIds() throws ManagerBeanException {
		return getSeries(true, IEntityAlias.SERIES_POS);
	}	

	public List<SelectItem> getSeries(boolean onlyCode, String typeAlias) throws ManagerBeanException {
		List<SelectItem> series = new LinkedList<SelectItem>();
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(typeAlias), Boolean.TRUE);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), Boolean.TRUE);
		if (!AonUtil.getRoleManager().isConfidentiality()) {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		}
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID));
		criteria.addOrder(seriesBean.getFieldName(IEntityAlias.SERIES_CODE));
		for (ITransferObject ito : seriesBean.getList(criteria)) {
			Series serie = (Series)ito;
			SelectItem item = new SelectItem(onlyCode?serie.getCode():serie, serie.getCode());
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
		criteria.addOrder(scopeBean.getFieldName(IEntityAlias.SCOPE_DESCRIPTION));
		for (ITransferObject ito : scopeBean.getList(criteria)) {
			Scope scope = (Scope)ito;
			SelectItem item = new SelectItem(scope, scope.getDescription());
			scopes.add(item);
		}
		return scopes;
	}

	public List<SelectItem> getCurrentUserScopes() {
		return getScopeList(UserUtils.getInstance().getCurrentUserScopes());
	}
	
	public static List<SelectItem> getScopeList( List<Scope> scopes ) {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (Scope scope : scopes) {
			list.add( AonUtil.getSelectItem(scope, scope.getDescription()));
		}
		return list;
	}

	public static List<SelectItem> getTagList( List<Tag> tags ) {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (Tag tag : tags) {
			list.add( AonUtil.getSelectItem(tag, tag.getName()));
		}
		return list;
	}
	
	public List<SelectItem> getProductTypeTags() throws ManagerBeanException {
		IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.PRODUCT);
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (ITransferObject to : tagBean.getList(criteria)) {
			Tag tag = (Tag) to;
			list.add( AonUtil.getSelectItem(tag, tag.getName()));
		}
		return list;
	}
	
	public int getProductTypeTagsCount() throws ManagerBeanException {
		IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.PRODUCT);
		return tagBean.getCount(criteria);
	}
	
	public List<SelectItem> getPackingTypeTags() throws ManagerBeanException {
		IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.PACKING);
		criteria.addOrder(tagBean.getFieldName(IEntityAlias.TAG_NAME));
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (ITransferObject to : tagBean.getList(criteria)) {
			Tag tag = (Tag) to;
			list.add( AonUtil.getSelectItem(tag, tag.getName()));
		}
		return list;
	}
	
	public int getPackingTypeTagsCount() throws ManagerBeanException {
		IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.PACKING);
		return tagBean.getCount(criteria);
	}
	
	public List<SelectItem> getWorkgroups() throws ManagerBeanException {
		List<SelectItem> workgroups = new LinkedList<SelectItem>(); 
		IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class); 
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workGroupBean.getFieldName(IEntityAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
		criteria.addOrder(workGroupBean.getFieldName(IEntityAlias.WORK_GROUP_DESCRIPTION));
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
		criteria.addEqualExpression(workGroupBean.getFieldName(IEntityAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
		criteria.addOrder(workGroupBean.getFieldName(IEntityAlias.WORK_GROUP_DESCRIPTION));
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
		return getPayMethods(null);
	}

	public List<SelectItem> getDirectPayMethods() throws ManagerBeanException {
		List<PayMethodType> directPayMethods = new LinkedList<PayMethodType>();
		directPayMethods.add(PayMethodType.CASH_BASIS);
		directPayMethods.add(PayMethodType.DEBIT_CARD);
		directPayMethods.add(PayMethodType.CREDIT_CARD);
		return getPayMethods(directPayMethods);
	}

	public List<SelectItem> getNoCashDirectPayMethods() throws ManagerBeanException {
		List<PayMethodType> noCashDirectPayMethods = new LinkedList<PayMethodType>();
		noCashDirectPayMethods.add(PayMethodType.DEBIT_CARD);
		noCashDirectPayMethods.add(PayMethodType.CREDIT_CARD);
		return getPayMethods(noCashDirectPayMethods);
	}

	public List<SelectItem> getExtendedDirectPayMethods() throws ManagerBeanException {
		List<PayMethodType> directPayMethods = new LinkedList<PayMethodType>();
		directPayMethods.add(PayMethodType.CASH_BASIS);
		directPayMethods.add(PayMethodType.DEBIT_CARD);
		directPayMethods.add(PayMethodType.CREDIT_CARD);
		directPayMethods.add(PayMethodType.CHEQUE);
		directPayMethods.add(PayMethodType.BANK_TRANSFER);
		return getPayMethods(directPayMethods);
	}

	private List<SelectItem> getPayMethods(List<PayMethodType> payMethodTypes) throws ManagerBeanException {
		List<SelectItem> payMethods = new LinkedList<SelectItem>();
		IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		if (payMethodTypes != null && payMethodTypes.size() > 0) {
			criteria.addExpression(ExpressionUtilities.getInExpression(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), payMethodTypes));
		}
		criteria.addOrder(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_NAME));
		for (ITransferObject ito : payMethodBean.getList(criteria)) {
			PayMethod payMethod = (PayMethod)ito;
			SelectItem item = new SelectItem(payMethod, payMethod.getName());
			payMethods.add(item);
		}
		return payMethods;
	}

	public List<SelectItem> getPayMethodTypeDetails() throws ManagerBeanException {
		List<SelectItem> payMethodTypeDetails = new LinkedList<SelectItem>();
		IManagerBean payMethodTypeDetailBean = BeanManager.getManagerBean(PayMethodTypeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(payMethodTypeDetailBean.getFieldName(IEntityAlias.PAY_METHOD_TYPE_DETAIL_DESCRIPTION));
		for (ITransferObject ito : payMethodTypeDetailBean.getList(criteria)) {
			PayMethodTypeDetail p = (PayMethodTypeDetail)ito;
			SelectItem item = new SelectItem(p, p.getDescription());
			payMethodTypeDetails.add(item);
		}
		return payMethodTypeDetails;
	}

	public Tariff getTariff() {
		return null;
	}

	public void setTariff( Tariff tariff ) {
	}
	
	public List<SelectItem> getAllTariffs() throws ManagerBeanException {
		return getTariffs(null);
	}

	public List<SelectItem> getPurchaseTariffs() throws ManagerBeanException {
		return getTariffs(Boolean.TRUE);
	}

	public List<SelectItem> getSalesTariffs() throws ManagerBeanException {
		return getTariffs(Boolean.FALSE);
	}

	public List<SelectItem> getTariffs(Boolean purchaseType) throws ManagerBeanException {
		List<SelectItem> tariffs = new LinkedList<SelectItem>();
		IManagerBean tariffBean = BeanManager.getManagerBean(Tariff.class);
		Criteria criteria = new Criteria();
		if (purchaseType != null) {
			criteria.addEqualExpression(tariffBean.getFieldName(IEntityAlias.TARIFF_PURCHASE), purchaseType);
		}
		criteria.addOrder(tariffBean.getFieldName(IEntityAlias.TARIFF_NAME));
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
		criteria.addOrder(commissionTypeBean.getFieldName(IEntityAlias.COMMISSION_TYPE_NAME));
		for (ITransferObject ito : commissionTypeBean.getList(criteria)) {
			CommissionType commissionType = (CommissionType)ito;
			SelectItem item = new SelectItem(commissionType, commissionType.getName());
			commissionTypes.add(item);
		}
		return commissionTypes;
	}

	public List<SelectItem> getToolbars() {
		if (toolbars == null) {
			Locale locale = AonUtil.getCurrentLocale();
			toolbars = new LinkedList<SelectItem>();
			for (Toolbar toolbar : Toolbar.values()) {
				if ( toolbar != Toolbar.ESFERALIA_WEBMAIL ) {
					String name = toolbar.getName(locale);
					SelectItem item = new SelectItem(toolbar, name);
					toolbars.add(item);
				}
			}
		}
		return toolbars;
	}	

	public List<SelectItem> getTagTypes() {
		if (tagTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			tagTypes = new LinkedList<SelectItem>();
			for(TagType type : TagType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				tagTypes.add(item);
			}
		}
		return tagTypes;
	}

	public List<SelectItem> getPayMethodTypeForDetails() {
		if ( payMethodTypeForDetails == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			payMethodTypeForDetails = new LinkedList<SelectItem>();
			String name = PayMethodType.CASH_BASIS.getName(locale);
			SelectItem item = new SelectItem(PayMethodType.CASH_BASIS, name);
			payMethodTypeForDetails.add(item);
			name = PayMethodType.OTHER.getName(locale);
			item = new SelectItem(PayMethodType.OTHER, name);
			payMethodTypeForDetails.add(item);
		}
		return payMethodTypeForDetails;
	}

	public List<String> getAddInfoAttributes() throws ManagerBeanException{
    	List<String> addInfos = new LinkedList<String>();
    	IManagerBean addInfoBean = BeanManager.getManagerBean(TariffAddInfo.class);
    	Criteria criteria = new Criteria();
    	criteria.addOrder(addInfoBean.getFieldName(IEntityAlias.TARIFF_ADD_INFO_ATTRIBUTE));
		Projection projection = Projection.group(addInfoBean.getFieldName(IEntityAlias.TARIFF_ADD_INFO_ATTRIBUTE));
		for (Object ito : addInfoBean.getList(new ProjectionList(projection), criteria)) {
    		String addInfo = (String)ito;
    		addInfos.add(addInfo);
    	}
    	return addInfos;
    }

	public List<SelectItem> getAllCatalogues() throws ManagerBeanException {
		return getCatalogues(null);
	}

	public List<SelectItem> getPurchaseCatalogues() throws ManagerBeanException {
		return getCatalogues(Boolean.TRUE);
	}

	public List<SelectItem> getSalesCatalogues() throws ManagerBeanException {
		return getCatalogues(Boolean.FALSE);
	}

	private List<SelectItem> getCatalogues(Boolean purchaseType) throws ManagerBeanException {
		List<SelectItem> catalogues = new LinkedList<SelectItem>();
		IManagerBean catalogueBean = BeanManager.getManagerBean(Catalogue.class);
		Criteria criteria = new Criteria();
		if (purchaseType != null) {
			criteria.addEqualExpression(catalogueBean.getFieldName(IEntityAlias.CATALOGUE_PURCHASE), purchaseType);
		}
		criteria.addOrder(catalogueBean.getFieldName(IEntityAlias.CATALOGUE_NAME));
		for (ITransferObject ito : catalogueBean.getList(criteria)) {
			Catalogue catalogue = (Catalogue)ito;
			SelectItem item = new SelectItem(catalogue,catalogue.getName());
			catalogues.add(item);
		}
		return catalogues;
	}

	public Scope getEmptyScope() {
		if ( this.emptyScope == null ) {
			this.emptyScope = new Scope();
			this.emptyScope.setDescription("-");
			this.emptyScope.setDomain(DomainManager.getCurrentDomain());			
		}
		return this.emptyScope;
	}

	public Tag getEmptyTag() {
		if ( this.emptyScope == null ) {
			this.emptyTag = new Tag();
			this.emptyTag.setName("-");
			this.emptyTag.setDomain(DomainManager.getCurrentDomain());			
		}
		return this.emptyTag;
	}
	
}