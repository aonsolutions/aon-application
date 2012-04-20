package com.esferalia.aon.entity.hibernate.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.MappingException;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import com.code.aon.common.enumeration.IStringEnum;

public class AonExporter extends GenericExporter{

	public static Map<String, String> map;
	
	public static String ENTITY_PACKAGE = "com.esferalia.aon.entity.master";
	public static String CLASS_SUFFIX= "DB";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void exportPersistentClass(Map additionalContext, POJOClass pojo) {
		boolean isConfidentialable = false;
		boolean isDomainContainer = false;
		Iterator<?> iterator = pojo.getAllPropertiesIterator();
		while (iterator.hasNext()) {
			Property property = (Property)iterator.next();

			// En este pnto se evalúa si la propiedad es un ManyToOne, si la clase
			// del pojo devuelto por la propiedad, es un pojo generado, se sustituye 
			// por la clase a sobreescribir el los proyectos entity.
			Value value = property.getValue();
			if (value instanceof org.hibernate.mapping.ManyToOne) {
				ManyToOne v = (ManyToOne) value;
				if (v.getReferencedEntityName().startsWith(ENTITY_PACKAGE)) {
					String aonEntity = getAonEntity(v.getReferencedEntityName());
					if (aonEntity != null) {
						v.setReferencedEntityName(aonEntity);	
					}
				}
			}
			// --------------------------------------------------
			
			if (property.getName().equals("securityLevel")) {
				isConfidentialable = true;
			}
			if (property.getName().equals("domain")) {
				isDomainContainer = true;
			}
		}
		if ( "ProfileDB".equals(pojo.getDeclarationName()) ) {
			isDomainContainer = false;
		}
		additionalContext.put("hasProjectPrimaryKeyJoinColumn", hasProjectPrimaryKeyJoinColumn(pojo) );	
		additionalContext.put("hasRegistryPrimaryKeyJoinColumn", hasRegistryPrimaryKeyJoinColumn(pojo));	
		additionalContext.put("hasAssetPrimaryKeyJoinColumn", hasAssetPrimaryKeyJoinColumn(pojo));
		additionalContext.put("isConfidentialable", isConfidentialable);
		additionalContext.put("isDomainContainer", isDomainContainer);
		additionalContext.put("aonExporter", this);
		super.exportPersistentClass(additionalContext, pojo);
	}
	
	public boolean hasRegistryPrimaryKeyJoinColumn(POJOClass pojo) {
		Property idProperty = pojo.getIdentifierProperty();
		Iterator<?> iter = idProperty.getColumnIterator();
		Column id = (Column) iter.next();
		return "registry".equals(id.getName());
	}

	public boolean hasProjectPrimaryKeyJoinColumn(POJOClass pojo) {
		Property idProperty = pojo.getIdentifierProperty();
		Iterator<?> iter = idProperty.getColumnIterator();
		Column id = (Column) iter.next();
		return "project".equals(id.getName());
	}

	public boolean hasAssetPrimaryKeyJoinColumn(POJOClass pojo) {
		Property idProperty = pojo.getIdentifierProperty();
		Iterator<?> iter = idProperty.getColumnIterator();
		Column id = (Column) iter.next();
		return "asset".equals(id.getName());
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void exportPOJO(Map additionalContext, POJOClass pojo) {
		Iterator<?> iterator = pojo.getAllPropertiesIterator();
		while (iterator.hasNext()) {
			Property property = (Property)iterator.next();
			if (property.getValue() instanceof ToOne) {
				ToOne toOne = (ToOne)property.getValue();
				toOne.setLazy(false);
			}
			else if (property.getValue() instanceof Collection) {
				Collection collection = (Collection)property.getValue();
				collection.setLazy(true);
			}
			else {
				property.setLazy(true);
			}
		}
		super.exportPOJO(additionalContext, pojo);
	}
	
	private String getAonEntity(String entity) {
		if (entity.startsWith( ENTITY_PACKAGE )) {
			String entitySimpleName = ClassUtils.getShortClassName(entity);
			if (entitySimpleName.endsWith(CLASS_SUFFIX)) {
				entitySimpleName = StringUtils.chomp(entitySimpleName,CLASS_SUFFIX);
			}
			String newClass = getMap().get(entitySimpleName);
			if (StringUtils.isEmpty(newClass)) {
				throw new IllegalStateException( " No sé en que paquete generar la clase " + entity + "!. Define la asociación.");
			}
			return newClass;
		}
		return null;
	}

	public Map<String,String> getMap() {
		return map;
	}

	@SuppressWarnings("rawtypes")
	public boolean isStringEnum(Property property) {
		try {
			Class clazz = Class.forName(property.getType().getName());
			Class[] interfaces = clazz.getInterfaces();
			for (Class interfaz : interfaces) {
				if (IStringEnum.class.equals(interfaz)) {
					return true;
				}
			}
		} catch (MappingException e) {
			// ignore hibernate tools will fail
		} catch (ClassNotFoundException e) {
			// ignore hibernate tools will fail
		}
		return false;
	}
	
	public boolean isRecursiveProperty(POJOClass pojo, Property property) {
		if (property.getType().isEntityType()) {
			String propertyClass = ClassUtils.getShortClassName(property.getType().getName()) + CLASS_SUFFIX;
			String pojoClass = pojo.getDeclarationName();
			return StringUtils.equals(propertyClass, pojoClass);
		}
		return false;
	}

	static {
		map = new HashMap<String, String>();
		// AON ACCOUNT
		map.put("Account","com.code.aon.account.Account");
		
		//AON ACCOUNTING 
		map.put("AccountEntry","com.code.aon.accounting.AccountEntry");
		map.put("AccountEntryDetail","com.code.aon.accounting.AccountEntryDetail");
		map.put("AccountHelper","com.code.aon.accounting.AccountHelper");
		map.put("Amortization","com.code.aon.accounting.Amortization");
		map.put("AmortizationDetail","com.code.aon.accounting.AmortizationDetail");
		map.put("AmortizationType","com.code.aon.accounting.AmortizationType");
		map.put("AutoConcept","com.code.aon.accounting.AutoConcept");
		map.put("Balance","com.code.aon.accounting.Balance");
		map.put("BalanceDetail","com.code.aon.accounting.BalanceDetail");
		map.put("Loan","com.code.aon.accounting.Loan");
		map.put("Period","com.code.aon.accounting.Period");

		//AON ACCOUNT-BRIDGE 
		map.put("AccountEntryBankStatement","com.code.aon.account.bridge.AccountEntryBankStatement");
		map.put("AccountEntryFinanceBatch","com.code.aon.account.bridge.AccountEntryFinanceBatch");
		map.put("AccountEntryFinanceTracking","com.code.aon.account.bridge.AccountEntryFinanceTracking");
		map.put("AccountEntryInvoice","com.code.aon.account.bridge.AccountEntryInvoice");
		map.put("BankConceptAccount","com.code.aon.account.bridge.BankConceptAccount");		
		map.put("CreditorAccount","com.code.aon.account.bridge.CreditorAccount");
		map.put("CustomerAccount","com.code.aon.account.bridge.CustomerAccount");
		map.put("InvoiceDetailAccount","com.code.aon.account.bridge.InvoiceDetailAccount");
		map.put("InvoiceTaxAccount","com.code.aon.account.bridge.InvoiceTaxAccount");
		map.put("LoanAccount","com.code.aon.account.bridge.LoanAccount");
		map.put("PayMethodTypeDetailAccount","com.code.aon.account.bridge.PayMethodTypeDetailAccount");		
		map.put("ProductAccount","com.code.aon.account.bridge.ProductAccount");		
		map.put("RegistryBankAccount","com.code.aon.account.bridge.RegistryBankAccount");
		map.put("SupplierAccount","com.code.aon.account.bridge.SupplierAccount");		
		map.put("TaxAccount","com.code.aon.account.bridge.TaxAccount");		

		//AON-ASSET   
		map.put("Asset","com.code.aon.asset.Asset");
		map.put("AssetActivity","com.code.aon.asset.AssetActivity");
		map.put("AssetFeature","com.code.aon.asset.AssetFeature");
		map.put("Feature","com.code.aon.asset.Feature");

		//AON-AUDIT   
		map.put("Action","com.code.aon.audit.Action");
		map.put("ActionDenied","com.code.aon.audit.ActionDenied");
		map.put("ActionEntry","com.code.aon.audit.ActionEntry");
		map.put("ActionFavorite","com.code.aon.audit.ActionFavorite");
		map.put("Session","com.code.aon.audit.Session");

		//AON CALENDAR 
		map.put("Calendar","com.esferalia.aon.calendar.Calendar");
		map.put("CalendarHoliday","com.esferalia.aon.calendar.CalendarHoliday");
		map.put("CalendarPeriod","com.esferalia.aon.calendar.CalendarPeriod");
		map.put("Holiday","com.esferalia.aon.calendar.Holiday");
		map.put("HolidayDetail","com.esferalia.aon.calendar.HolidayDetail");

		//AON-COMMERCIAL  
		map.put("CommercialActivity","com.code.aon.commercial.CommercialActivity");
		map.put("CommercialTracking","com.code.aon.commercial.CommercialTracking");
		map.put("CommercialTerm","com.code.aon.commercial.CommercialTerm");
		map.put("Commission","com.code.aon.commercial.Commission");
		map.put("CommissionCategory","com.code.aon.commercial.CommissionCategory");
		map.put("CommissionItem","com.code.aon.commercial.CommissionItem");
		map.put("CommissionTypeCommission","com.code.aon.commercial.CommissionTypeCommission");
		map.put("Offer","com.code.aon.commercial.Offer");
		map.put("OfferAttachment","com.code.aon.commercial.OfferAttachment");
		map.put("OfferDetail","com.code.aon.commercial.OfferDetail");
		map.put("OfferDetailCommission","com.code.aon.commercial.OfferDetailCommission");
		map.put("OfferTerm","com.code.aon.commercial.OfferTerm");
		map.put("ProjectCommercial","com.code.aon.commercial.ProjectCommercial");		
		map.put("Target","com.code.aon.commercial.Target");
		map.put("TargetItem","com.code.aon.commercial.TargetItem");
		map.put("TargetSeller","com.code.aon.commercial.TargetSeller");
		map.put("TargetSupplier","com.code.aon.commercial.TargetSupplier");

		//AON COMPANY 
		map.put("Company","com.code.aon.company.Company");
		map.put("Department","com.code.aon.company.Department");
		map.put("Enterprise","com.code.aon.company.Enterprise");
		map.put("EnterpriseData","com.code.aon.company.EnterpriseData");
		map.put("WorkPlace","com.code.aon.company.WorkPlace");
		map.put("EnterpriseUser","com.code.aon.company.EnterpriseUser");
		map.put("WorkplaceDepartment","com.code.aon.company.WorkplaceDepartment");

		//AON CONFIG 
		map.put("Application","com.code.aon.config.Application");
		map.put("ApplicationParameter","com.code.aon.config.ApplicationParameter");
		map.put("ApplicationUser","com.code.aon.config.ApplicationUser");		
		map.put("Bank","com.code.aon.config.Bank");
		map.put("CNAE","com.code.aon.config.CNAE");
		map.put("CNAE2009","com.code.aon.config.CNAE2009");
		map.put("CNAE2009Rate","com.code.aon.config.CNAE2009Rate");
		map.put("CommissionType","com.code.aon.config.CommissionType");
		map.put("Domain","com.code.aon.config.Domain");    
		map.put("DomainApplication","com.code.aon.config.DomainApplication");    
		map.put("PayMethodTypeDetail","com.code.aon.config.PayMethodTypeDetail");
		map.put("PayMethod","com.code.aon.config.PayMethod");
		map.put("Series","com.code.aon.config.Series");
		map.put("Scope","com.code.aon.config.Scope");
		map.put("Tariff","com.code.aon.config.Tariff");
		map.put("Tax","com.code.aon.config.Tax");
		map.put("TaxDetail","com.code.aon.config.TaxDetail");
		map.put("User","com.code.aon.config.User");
		map.put("UserScope","com.code.aon.config.UserScope");
		map.put("UserWorkGroup","com.code.aon.config.UserWorkGroup");
		map.put("WorkGroup","com.code.aon.config.WorkGroup");
		
		//AON CUSTOMER 
		map.put("Customer","com.code.aon.customer.Customer");
		
		//AON FINANCE 
		map.put("BankConcept","com.code.aon.finance.BankConcept");
		map.put("BankStatement","com.code.aon.finance.BankStatement");
		map.put("BankStatementLink","com.code.aon.finance.BankStatementLink");
		map.put("CashFlowForecast","com.code.aon.finance.CashFlowForecast");
		map.put("CustomerFee","com.code.aon.finance.CustomerFee");
		map.put("Creditor","com.code.aon.finance.Creditor");
		map.put("Finance","com.code.aon.finance.Finance");
		map.put("FinanceBatch","com.code.aon.finance.FinanceBatch");
		map.put("FinanceBatchDetail","com.code.aon.finance.FinanceBatchDetail");
		map.put("FinanceTracking","com.code.aon.finance.FinanceTracking");
		map.put("Invoice","com.code.aon.finance.Invoice");
		map.put("InvoiceAddress","com.code.aon.finance.InvoiceAddress");
		map.put("InvoiceAttachment","com.code.aon.finance.InvoiceAttachment");
		map.put("InvoiceDetail","com.code.aon.finance.InvoiceDetail");
		map.put("InvoiceTax","com.code.aon.finance.InvoiceTax");
		map.put("InvoicingGroup","com.code.aon.finance.InvoicingGroup");
		map.put("InvoicingGroupDetail","com.code.aon.finance.InvoicingGroupDetail");

		//AON-FISCAL  	
		map.put("Mod347","com.code.aon.fiscal.Mod347");
		map.put("Mod347Detail","com.code.aon.fiscal.Mod347Detail");
		map.put("ProfessionalRetention","com.code.aon.fiscal.ProfessionalRetention");
		map.put("Renting","com.code.aon.fiscal.Renting");
		map.put("RentingDetail","com.code.aon.fiscal.RentingDetail");
		map.put("VatTax","com.code.aon.fiscal.VatTax");
		map.put("VatTaxDeclaration","com.code.aon.fiscal.VatTaxDeclaration");
		map.put("VatTaxDetail","com.code.aon.fiscal.VatTaxDetail");

		//AON GEOZONE 
		map.put("GeoZone","com.code.aon.geozone.GeoZone");
		map.put("GeoTree","com.code.aon.geozone.GeoTree");
		
		//AON GROUPWARE 
		map.put("Alarm","com.code.aon.groupware.Alarm");
		map.put("Campaign","com.code.aon.groupware.Campaign");
		map.put("CampaignProject","com.code.aon.groupware.CampaignProject");
		map.put("CampaignType","com.code.aon.groupware.CampaignType");
		map.put("CostProfile","com.code.aon.groupware.CostProfile");
		map.put("DailyTracking","com.code.aon.groupware.DailyTracking");
		map.put("Favorite","com.code.aon.groupware.Favorite");
		map.put("FavoriteCategory","com.code.aon.groupware.FavoriteCategory");
		map.put("JobType","com.code.aon.groupware.JobType");
		map.put("Note","com.code.aon.groupware.Note");
		map.put("Notice","com.code.aon.groupware.Notice");
		map.put("Process","com.code.aon.groupware.Process");
		map.put("ProcessDetail","com.code.aon.groupware.ProcessDetail");
		map.put("ProcessDetailTransition","com.code.aon.groupware.ProcessDetailTransition");
		map.put("ProcessTask","com.code.aon.groupware.ProcessTask");
		map.put("ProcessTransitionType","com.code.aon.groupware.ProcessTransitionType");
		map.put("Task","com.code.aon.groupware.Task");
		map.put("TaskHolder","com.code.aon.groupware.TaskHolder");
		map.put("TaskHolderWorkgroup","com.code.aon.groupware.TaskHolderWorkgroup");

		//AON INFOWEB 
		map.put("WebInfo","com.code.aon.infoweb.WebInfo");
		map.put("WebInfoPage","com.code.aon.infoweb.WebInfoPage");
		map.put("WebInfoPageDetail","com.code.aon.infoweb.WebInfoPageDetail");
		map.put("WebInfoPageResource","com.code.aon.infoweb.WebInfoPageResource");
		map.put("WebInfoStyle","com.code.aon.infoweb.WebInfoStyle");

		//AON-ADMIN   
		map.put("ApplicationRole","com.code.aon.admin.ApplicationRole");		
		map.put("ApplicationUserProfile","com.code.aon.admin.ApplicationUserProfile");
		map.put("Profile","com.code.aon.admin.Profile");
		map.put("ProfileRole","com.code.aon.admin.ProfileRole");
		map.put("Role","com.code.aon.admin.Role");
		
		//AON-MARKETING  
		map.put("ActionTarget","com.code.aon.marketing.ActionTarget");
		map.put("MarketingCampaign","com.code.aon.marketing.MarketingCampaign");
		map.put("MarketingAction","com.code.aon.marketing.MarketingAction");
		map.put("Question","com.code.aon.marketing.Question");
		map.put("QuestionValue","com.code.aon.marketing.QuestionValue");
		map.put("Survey","com.code.aon.marketing.Survey");
		map.put("SurveyQuestion","com.code.aon.marketing.SurveyQuestion");
		map.put("SurveyResponse","com.code.aon.marketing.SurveyResponse");
		map.put("SurveyResponseDetail","com.code.aon.marketing.SurveyResponseDetail");
		map.put("SurveyWorkflow","com.code.aon.marketing.SurveyWorkflow");
		map.put("TargetProfile","com.code.aon.marketing.TargetProfile");
		map.put("Template","com.code.aon.marketing.Template");
		
		//AON-MESSAGING
		map.put("Message","com.code.aon.messaging.Message");
		map.put("MessageContent","com.code.aon.messaging.MessageContent");
		
		//AON-PAYROLL 
		map.put("Agreement","com.esferalia.aon.payroll.Agreement");
		map.put("AgreementData","com.esferalia.aon.payroll.AgreementData");
		map.put("AgreementExtra","com.esferalia.aon.payroll.AgreementExtra");
		map.put("AgreementLevel","com.esferalia.aon.payroll.AgreementLevel");
		map.put("AgreementLevelCategory","com.esferalia.aon.payroll.AgreementLevelCategory");
		map.put("AgreementLevelData","com.esferalia.aon.payroll.AgreementLevelData");
		map.put("AgreementPayment","com.esferalia.aon.payroll.AgreementPayment");
		map.put("BonusConcept","com.esferalia.aon.payroll.BonusConcept");
		map.put("Certifica2Batch","com.esferalia.aon.payroll.Certifica2Batch");
		map.put("Certifica2BatchAttachment","com.esferalia.aon.payroll.Certifica2BatchAttachment");
		map.put("Certifica2BatchData","com.esferalia.aon.payroll.Certifica2BatchData");
		map.put("Certifica2BatchDetail","com.esferalia.aon.payroll.Certifica2BatchDetail");
		map.put("CNO","com.esferalia.aon.payroll.CNO");
		map.put("Contract","com.esferalia.aon.payroll.Contract");
		map.put("ContractAttachment","com.esferalia.aon.payroll.ContractAttachment");
		map.put("ContractBatch","com.esferalia.aon.payroll.ContractBatch");
		map.put("ContractBatchAttachment","com.esferalia.aon.payroll.ContractBatchAttachment");
		map.put("ContractBatchDetail","com.esferalia.aon.payroll.ContractBatchDetail");
		map.put("ContractBonus","com.esferalia.aon.payroll.ContractBonus");
		map.put("ContractCalendarEvent","com.esferalia.aon.payroll.ContractCalendarEvent");
		map.put("ContractData","com.esferalia.aon.payroll.ContractData");
		map.put("ContractDeduction","com.esferalia.aon.payroll.ContractDeduction");
		map.put("ContractEmbargo","com.esferalia.aon.payroll.ContractEmbargo");
		map.put("ContractLeave","com.esferalia.aon.payroll.ContractLeave");
		map.put("ContractLeaveDetail","com.esferalia.aon.payroll.ContractLeaveDetail");
		map.put("ContractPayment","com.esferalia.aon.payroll.ContractPayment");
		map.put("DeductionConcept","com.esferalia.aon.payroll.DeductionConcept");
		map.put("EnterpriseActivity","com.esferalia.aon.payroll.EnterpriseActivity");
		map.put("EnterpriseCCC","com.esferalia.aon.payroll.EnterpriseCCC");
		map.put("FanBatch","com.esferalia.aon.payroll.FanBatch");
		map.put("FanBatchAttachment","com.esferalia.aon.payroll.FanBatchAttachment");
		map.put("FanBatchDetail","com.esferalia.aon.payroll.FanBatchDetail");
		map.put("GeozoneIrpf","com.esferalia.aon.payroll.GeozoneIrpf");
		map.put("GeozoneIrpfDescendant","com.esferalia.aon.payroll.GeozoneIrpfDescendant");
		map.put("GeozoneIrpfHandicap","com.esferalia.aon.payroll.GeozoneIrpfHandicap");
		map.put("IrpfData","com.esferalia.aon.payroll.IrpfData");
		map.put("IrpfDataAscendants","com.esferalia.aon.payroll.IrpfDataAscendants");
		map.put("IrpfDataDescendients","com.esferalia.aon.payroll.IrpfDataDescendients");
		map.put("IrpfRegularization","com.esferalia.aon.payroll.IrpfRegularization");
		map.put("IrpfResult","com.esferalia.aon.payroll.IrpfResult");
		map.put("LeaveBatch","com.esferalia.aon.payroll.LeaveBatch");
		map.put("LeaveBatchAttachment","com.esferalia.aon.payroll.LeaveBatchAttachment");
		map.put("LeaveBatchDetail","com.esferalia.aon.payroll.LeaveBatchDetail");
		map.put("PaymentConcept","com.esferalia.aon.payroll.PaymentConcept");
		map.put("PayrollWorkPlace","com.esferalia.aon.payroll.PayrollWorkPlace");
		map.put("Salary","com.esferalia.aon.payroll.Salary");
		map.put("SalaryBonus","com.esferalia.aon.payroll.SalaryBonus");
		//map.put("SalaryData","com.esferalia.aon.payroll.SalaryData");
		map.put("SalaryCost","com.esferalia.aon.payroll.SalaryCost");
		map.put("SalaryDeduction","com.esferalia.aon.payroll.SalaryDeduction");
		map.put("SalaryEmbargo","com.esferalia.aon.payroll.SalaryEmbargo");
		map.put("SalaryPayment","com.esferalia.aon.payroll.SalaryPayment");
		map.put("SystemCost","com.esferalia.aon.payroll.SystemCost");
		map.put("SystemData","com.esferalia.aon.payroll.SystemData");
		map.put("SystemDeduction","com.esferalia.aon.payroll.SystemDeduction");
		map.put("SystemPayment","com.esferalia.aon.payroll.SystemPayment");

		//AON PRODUCT 
		map.put("Brand","com.code.aon.product.Brand");
		map.put("Catalogue","com.code.aon.product.Catalogue");
		map.put("CatalogueCategory","com.code.aon.product.CatalogueCategory");
		map.put("CatalogueItem","com.code.aon.product.CatalogueItem");
		map.put("Item","com.code.aon.product.Item");
		map.put("ItemAlternative","com.code.aon.product.ItemAlternative");
		map.put("ItemAttachment","com.code.aon.product.ItemAttachment");
		map.put("ItemComposition","com.code.aon.product.ItemComposition");
		map.put("ItemSupplier","com.code.aon.product.ItemSupplier");
		map.put("ItemTariff","com.code.aon.product.ItemTariff");
		map.put("Product","com.code.aon.product.Product");
		map.put("ProductCategory","com.code.aon.product.ProductCategory");
		map.put("ProductCategoryGroup","com.code.aon.product.ProductCategoryGroup");
		map.put("ProductCategoryTree","com.code.aon.product.ProductCategoryTree");
		map.put("TariffCatalogue","com.code.aon.product.TariffCatalogue");

		//AON PROJECT 
		map.put("ActivityType","com.code.aon.project.ActivityType");
		map.put("Project","com.code.aon.project.Project");
		map.put("ProjectActivity","com.code.aon.project.ProjectActivity");
		map.put("ProjectAttachment","com.code.aon.project.ProjectAttachment");
		map.put("ProjectType","com.code.aon.project.ProjectType");

		//AON-PMS
        map.put("Hotel","com.esferalia.aon.pms.Hotel");
        map.put("Pos","com.esferalia.aon.pms.Pos");
        map.put("PosShift","com.esferalia.aon.pms.PosShift");
        map.put("PosShiftCount","com.esferalia.aon.pms.PosShiftCount");
        map.put("ProjectReservation","com.esferalia.aon.pms.ProjectReservation");
        map.put("ProjectReservationDivert","com.esferalia.aon.pms.ProjectReservationDivert");
        map.put("ProjectReservationGuest","com.esferalia.aon.pms.ProjectReservationGuest");
        map.put("ProjectReservationRoom","com.esferalia.aon.pms.ProjectReservationRoom");
        map.put("ProjectReservationRoomDetail","com.esferalia.aon.pms.ProjectReservationRoomDetail");
        map.put("ProjectReservationService","com.esferalia.aon.pms.ProjectReservationService");
        map.put("ProjectReservationServiceDetail","com.esferalia.aon.pms.ProjectReservationServiceDetail");
        map.put("ReservationRequest","com.esferalia.aon.pms.ReservationRequest");
        map.put("ReservationRequestGuest","com.esferalia.aon.pms.ReservationRequestGuest");
        map.put("ReservationRequestRoom","com.esferalia.aon.pms.ReservationRequestRoom");
        map.put("Room","com.esferalia.aon.pms.Room");
        
		//AON-PURCHASE  
        map.put("Proposal","com.code.aon.purchase.Proposal");
        map.put("ProposalDetail","com.code.aon.purchase.ProposalDetail");
		map.put("Purchase","com.code.aon.purchase.Purchase");
		map.put("PurchaseDetail","com.code.aon.purchase.PurchaseDetail");

		//AON REGISTRY 
		map.put("Person","com.code.aon.person.Person");
		map.put("Category","com.code.aon.registry.Category");
		map.put("RecordData","com.code.aon.registry.RecordData");
		map.put("Registry","com.code.aon.registry.Registry");
		map.put("RegistryAddInfo","com.code.aon.registry.RegistryAddInfo");
		map.put("RegistryAddress","com.code.aon.registry.RegistryAddress");
		map.put("RegistryAttachment","com.code.aon.registry.RegistryAttachment");
		map.put("RegistryDirStaff","com.code.aon.registry.RegistryDirStaff");
		map.put("RegistryBank","com.code.aon.registry.RegistryBank");
		map.put("RegistryMedia","com.code.aon.registry.RegistryMedia");
		map.put("RegistryNote","com.code.aon.registry.RegistryNote");
		map.put("RegistryPayMethod","com.code.aon.registry.RegistryPayMethod");
		map.put("RegistryRelationship","com.code.aon.registry.RegistryRelationship");
		map.put("RegistrySegment","com.code.aon.registry.RegistrySegment");
		map.put("Relationship","com.code.aon.registry.Relationship");
		map.put("Segment","com.code.aon.registry.Segment");

		//AON-SALES  
		map.put("Sales","com.code.aon.sales.Sales");
		map.put("SalesDetail","com.code.aon.sales.SalesDetail");

		//AON-SUPPLIER 	
		map.put("Supplier","com.code.aon.supplier.Supplier");

		//AON-SELLER 	
		map.put("Seller","com.code.aon.seller.Seller");
		
		//AON-TAS 
		map.put("Make","com.code.aon.tas.Make");
		map.put("Model","com.code.aon.tas.Model");
		map.put("ProjectTas","com.code.aon.tas.ProjectTas");
		map.put("TasItem","com.code.aon.tas.TasItem");

		//AON-WEBMAIL 
		map.put("Contact","com.code.aon.webmail.db.Contact");
		map.put("ContactGroup","com.code.aon.webmail.db.ContactGroup");
		map.put("ContactGroupDetail","com.code.aon.webmail.db.ContactGroupDetail");
		map.put("MailAccount","com.code.aon.webmail.db.MailAccount");
		map.put("Signature","com.code.aon.webmail.db.Signature");

		//AON-WAREHOUSE 
		map.put("Delivery","com.code.aon.warehouse.Delivery");
		map.put("DeliveryDetail","com.code.aon.warehouse.DeliveryDetail");
		map.put("Income","com.code.aon.warehouse.Income");
		map.put("IncomeDetail","com.code.aon.warehouse.IncomeDetail");		
		map.put("Inventory","com.code.aon.warehouse.Inventory");		
		map.put("InventoryDetail","com.code.aon.warehouse.InventoryDetail");
		map.put("ItemWarehouse","com.code.aon.warehouse.ItemWarehouse");
		map.put("Stock","com.code.aon.warehouse.Stock");
		map.put("Warehouse","com.code.aon.warehouse.Warehouse");
		map.put("WarehouseTransfer","com.code.aon.warehouse.WarehouseTransfer");
		map.put("WarehouseTransferDetail","com.code.aon.warehouse.WarehouseTransferDetail");
		
	}
		
}
