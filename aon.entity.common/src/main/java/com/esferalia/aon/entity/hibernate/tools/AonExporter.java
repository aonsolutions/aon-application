package com.esferalia.aon.entity.hibernate.tools;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.MappingException;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import com.code.aon.common.enumeration.IStringEnum;

public class AonExporter extends GenericExporter{

	public static String ENTITY_PACKAGE = "com.esferalia.aon.entity.master";
	public static String CLASS_SUFFIX= "DB";
	
	public static Map<String, String> map;
	public static Map<String, List<String>> propertyMap;
	
	public Map<String,String> getMap() {
		return map;
	}

	public Map<String,List<String>> getPropertyMap() {
		if (propertyMap == null) {
			propertyMap = new HashMap<String, List<String>>();
		}
		return propertyMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void exportPersistentClass(Map additionalContext, POJOClass pojo) {
		boolean isConfidentialable = false;
		boolean isDomainContainer = false;
		Iterator<?> iterator = pojo.getAllPropertiesIterator();
		while (iterator.hasNext()) {
			Property property = (Property)iterator.next();

			// En este punto se evalúa si la propiedad es un ManyToOne, si la clase
			// del pojo devuelto por la propiedad, es un pojo generado, se sustituye 
			// por la clase a sobreescribir el los proyectos entity.
			Value value = property.getValue();
			if (value instanceof org.hibernate.mapping.ToOne) {
				ToOne v = (ToOne) value;
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
		if ( "ProfileDB".equals(pojo.getDeclarationName()) || "ProfileRoleDB".equals(pojo.getDeclarationName()) || "ProfileModuleDeniedDB".equals(pojo.getDeclarationName())) {
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
			String pojoClass = pojo.getDeclarationName();
			String propertyClass = ClassUtils.getShortClassName(property.getType().getName()) + CLASS_SUFFIX;
			if (StringUtils.equals(pojoClass, propertyClass)) {
				return true;
			} else {
				List<String> propertyList = getPropertyMap().containsKey(pojoClass) ? getPropertyMap().get(pojoClass) : new LinkedList<String>();
				propertyList.add(propertyClass);
				getPropertyMap().put(pojoClass, propertyList);

				if (getPropertyMap().containsKey(propertyClass)) {
					for (String subProperty : getPropertyMap().get(propertyClass)) {
						if (StringUtils.equals(pojoClass, subProperty)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	static {
		map = new HashMap<String, String>();

		// AON ACADEMY
		map.put("Absence","com.code.aon.academy.Absence");
		map.put("AlumnLoan","com.code.aon.academy.AlumnLoan");
		map.put("AcademicSkill","com.code.aon.academy.AcademicSkill");
		map.put("AcademicYear","com.code.aon.academy.AcademicYear");	
		map.put("Course","com.code.aon.academy.Course");
		map.put("CourseAcademicSkill","com.code.aon.academy.CourseAcademicSkill");
		map.put("CourseAlumn","com.code.aon.academy.CourseAlumn");
		map.put("CourseEvaluation","com.code.aon.academy.CourseEvaluation");
		map.put("CourseInstructor","com.code.aon.academy.CourseInstructor");
		map.put("CourseLevel","com.code.aon.academy.CourseLevel");		
		map.put("CourseObservation","com.code.aon.academy.CourseObservation");
		map.put("CourseSchedule","com.code.aon.academy.CourseSchedule");
		map.put("CourseSubject","com.code.aon.academy.CourseSubject");
		map.put("EvaluationObservation","com.code.aon.academy.EvaluationObservation");
		map.put("Mark","com.code.aon.academy.Mark");
		map.put("Observation","com.code.aon.academy.Observation");
		map.put("Qualification","com.code.aon.academy.Qualification");
		map.put("QualitySkill","com.code.aon.academy.QualitySkill");
		
		// AON ACCOUNT
		map.put("Account","com.code.aon.account.Account");
		
		//AON ACCOUNTING 
		map.put("AccountEntry","com.code.aon.accounting.AccountEntry");
		map.put("AccountEntryDetail","com.code.aon.accounting.AccountEntryDetail");
		map.put("Amortization","com.code.aon.accounting.Amortization");
		map.put("AmortizationDetail","com.code.aon.accounting.AmortizationDetail");
		map.put("AmortizationInvoice","com.code.aon.accounting.AmortizationInvoice");
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
		map.put("InvoiceDetailAccount","com.code.aon.account.bridge.InvoiceDetailAccount");
		map.put("InvoiceTaxAccount","com.code.aon.account.bridge.InvoiceTaxAccount");

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
		map.put("DomainApplicationModule","com.code.aon.audit.DomainApplicationModule");
		map.put("ProfileActionDenied","com.code.aon.audit.ProfileActionDenied");
		map.put("ProfileModuleDenied","com.code.aon.audit.ProfileModuleDenied");		
		map.put("Session","com.code.aon.audit.Session");
		
		//AON CARRIER
		map.put("Carrier","com.esferalia.aon.carrier.Carrier");
		
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

		//AON COMPANY 
		map.put("Company","com.code.aon.company.Company");
		map.put("Department","com.code.aon.company.Department");
		map.put("Enterprise","com.code.aon.company.Enterprise");
		map.put("EnterpriseData","com.code.aon.company.EnterpriseData");
		map.put("EnterpriseUser","com.code.aon.company.EnterpriseUser");
		map.put("InvestAsset","com.code.aon.company.InvestAsset");
		map.put("WorkPlace","com.code.aon.company.WorkPlace");
		map.put("WorkplaceDepartment","com.code.aon.company.WorkplaceDepartment");

		//AON CONFIG 
		map.put("Application","com.code.aon.config.Application");
		map.put("ApplicationParameter","com.code.aon.config.ApplicationParameter");
		map.put("ApplicationUser","com.code.aon.config.ApplicationUser");		
		map.put("Catalogue","com.code.aon.config.Catalogue");
		map.put("CNAE","com.code.aon.config.CNAE");
		map.put("CNAE2009","com.code.aon.config.CNAE2009");
		map.put("CNAE2009Rate","com.code.aon.config.CNAE2009Rate");
		map.put("CommissionType","com.code.aon.config.CommissionType");
		map.put("Domain","com.code.aon.config.Domain");    
		map.put("DomainApplication","com.code.aon.config.DomainApplication");    
		map.put("IAE","com.code.aon.config.IAE");
		map.put("PayMethodTypeDetail","com.code.aon.config.PayMethodTypeDetail");
		map.put("PayMethod","com.code.aon.config.PayMethod");
		map.put("Series","com.code.aon.config.Series");
		map.put("Scope","com.code.aon.config.Scope");
		map.put("Tag","com.code.aon.config.Tag");
		map.put("Tariff","com.code.aon.config.Tariff");
		map.put("TariffAddInfo","com.code.aon.config.TariffAddInfo");
		map.put("TariffCatalogue","com.code.aon.config.TariffCatalogue");
		map.put("Tax","com.code.aon.config.Tax");
		map.put("TaxDetail","com.code.aon.config.TaxDetail");
		map.put("User","com.code.aon.config.User");
		map.put("UserScope","com.code.aon.config.UserScope");
		map.put("UserWorkGroup","com.code.aon.config.UserWorkGroup");
		map.put("WorkGroup","com.code.aon.config.WorkGroup");
		
		//AON CUSTOMER 
		map.put("Customer","com.code.aon.customer.Customer");
		map.put("InvoicingGroup","com.code.aon.customer.InvoicingGroup");
		
		//AON DATA 
		map.put("DataAttachment","com.code.aon.data.DataAttachment");    
		map.put("DataResponse","com.code.aon.data.DataResponse");    
		map.put("DataResponseDetail","com.code.aon.data.DataResponseDetail");    

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
        map.put("Pos","com.code.aon.finance.Pos");
        map.put("PosCatalogue","com.code.aon.finance.PosCatalogue");
        map.put("PosShift","com.code.aon.finance.PosShift");
        map.put("PosShiftCount","com.code.aon.finance.PosShiftCount");
        map.put("Prepayment","com.code.aon.finance.Prepayment");

		//AON-FISCAL  	
		map.put("FiscalActivity","com.code.aon.fiscal.FiscalActivity");
		map.put("FiscalActivityInfo","com.code.aon.fiscal.FiscalActivityInfo");
		map.put("FiscalModel","com.code.aon.fiscal.FiscalModel");
		map.put("FiscalModelDetail","com.code.aon.fiscal.FiscalModelDetail");

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
        map.put("News","com.code.aon.marketing.News");
        map.put("Newsletter","com.code.aon.marketing.Newsletter");
        map.put("NewsletterDetail","com.code.aon.marketing.NewsletterDetail");
		map.put("Survey","com.code.aon.marketing.Survey");
		map.put("SurveyQuestion","com.code.aon.marketing.SurveyQuestion");
		map.put("SurveyResponse","com.code.aon.marketing.SurveyResponse");
		map.put("SurveyResponseDetail","com.code.aon.marketing.SurveyResponseDetail");
		map.put("SurveyWorkflow","com.code.aon.marketing.SurveyWorkflow");
		map.put("Template","com.code.aon.marketing.Template");
		
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
		map.put("Certifica2BatchDetail","com.esferalia.aon.payroll.Certifica2BatchDetail");
		map.put("CNO","com.esferalia.aon.payroll.CNO");
		map.put("Contract","com.esferalia.aon.payroll.Contract");
		map.put("ContractAttachment","com.esferalia.aon.payroll.ContractAttachment");
		map.put("ContractBatch","com.esferalia.aon.payroll.ContractBatch");
		map.put("ContractBatchDetail","com.esferalia.aon.payroll.ContractBatchDetail");
		map.put("ContractBonus","com.esferalia.aon.payroll.ContractBonus");
		map.put("ContractCalendarEvent","com.esferalia.aon.payroll.ContractCalendarEvent");
		map.put("ContractClause","com.esferalia.aon.payroll.ContractClause");
		map.put("ContractData","com.esferalia.aon.payroll.ContractData");
		map.put("ContractDeduction","com.esferalia.aon.payroll.ContractDeduction");
		map.put("ContractEmbargo","com.esferalia.aon.payroll.ContractEmbargo");
		map.put("ContractInfo","com.esferalia.aon.payroll.ContractInfo");
		map.put("ContractLeave","com.esferalia.aon.payroll.ContractLeave");
		map.put("ContractLeaveDetail","com.esferalia.aon.payroll.ContractLeaveDetail");
		map.put("ContractPayment","com.esferalia.aon.payroll.ContractPayment");
		map.put("ContrataBatch","com.esferalia.aon.payroll.ContrataBatch");
		map.put("ContrataBatchDetail","com.esferalia.aon.payroll.ContrataBatchDetail");
		map.put("CraBatch","com.esferalia.aon.payroll.CraBatch");
		map.put("CraBatchDetail","com.esferalia.aon.payroll.CraBatchDetail");
		map.put("DeductionConcept","com.esferalia.aon.payroll.DeductionConcept");
		map.put("EnterpriseActivity","com.esferalia.aon.payroll.EnterpriseActivity");
		map.put("EnterpriseCCC","com.esferalia.aon.payroll.EnterpriseCCC");
		map.put("FanBatch","com.esferalia.aon.payroll.FanBatch");
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
		map.put("LeaveBatchDetail","com.esferalia.aon.payroll.LeaveBatchDetail");
		map.put("PaymentConcept","com.esferalia.aon.payroll.PaymentConcept");
		map.put("PayrollBatchAttachment","com.esferalia.aon.payroll.PayrollBatchAttachment");
		map.put("PayrollWorkPlace","com.esferalia.aon.payroll.PayrollWorkPlace");
		map.put("Salary","com.esferalia.aon.payroll.Salary");
		map.put("SalaryBonus","com.esferalia.aon.payroll.SalaryBonus");
		map.put("SalaryData","com.esferalia.aon.payroll.SalaryData");
		map.put("SalaryCost","com.esferalia.aon.payroll.SalaryCost");
		map.put("SalaryDeduction","com.esferalia.aon.payroll.SalaryDeduction");
		map.put("SalaryEmbargo","com.esferalia.aon.payroll.SalaryEmbargo");
		map.put("SalaryPayment","com.esferalia.aon.payroll.SalaryPayment");
		map.put("SepeBatchAttachment","com.esferalia.aon.payroll.SepeBatchAttachment");
		map.put("SystemCost","com.esferalia.aon.payroll.SystemCost");
		map.put("SystemData","com.esferalia.aon.payroll.SystemData");
		map.put("SystemDeduction","com.esferalia.aon.payroll.SystemDeduction");
		map.put("SystemPayment","com.esferalia.aon.payroll.SystemPayment");
		map.put("TrainingCenter","com.esferalia.aon.payroll.TrainingCenter");
		map.put("TrainingCourse","com.esferalia.aon.payroll.TrainingCourse");

		//AON PRODUCT 
		map.put("Brand","com.code.aon.product.Brand");
		map.put("CatalogueCategory","com.code.aon.product.CatalogueCategory");
		map.put("CatalogueItem","com.code.aon.product.CatalogueItem");
		map.put("Item","com.code.aon.product.Item");
		map.put("ItemAddInfo","com.code.aon.product.ItemAddInfo");
		map.put("ItemAlternative","com.code.aon.product.ItemAlternative");
		map.put("ItemAttachment","com.code.aon.product.ItemAttachment");
		map.put("ItemComposition","com.code.aon.product.ItemComposition");
		map.put("ItemTariff","com.code.aon.product.ItemTariff");
		map.put("Product","com.code.aon.product.Product");
		map.put("ProductCategory","com.code.aon.product.ProductCategory");
		map.put("ProductTag","com.code.aon.product.ProductTag");

		//AON PROJECT 
		map.put("ActivityType","com.code.aon.project.ActivityType");
		map.put("Project","com.code.aon.project.Project");
		map.put("ProjectActivity","com.code.aon.project.ProjectActivity");
		map.put("ProjectAttachment","com.code.aon.project.ProjectAttachment");
		map.put("ProjectType","com.code.aon.project.ProjectType");

		//AON-PMS
        map.put("Allotment","com.esferalia.aon.pms.Allotment");
        map.put("AllotmentItem","com.esferalia.aon.pms.AllotmentItem");
        map.put("AllotmentTariff","com.esferalia.aon.pms.AllotmentTariff");
        map.put("Hotel","com.esferalia.aon.pms.Hotel");
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
        map.put("StopSales","com.esferalia.aon.pms.StopSales");
        map.put("StopSalesItem","com.esferalia.aon.pms.StopSalesItem");
        map.put("StopSalesTariff","com.esferalia.aon.pms.StopSalesTariff");
        
		//AON-PURCHASE  
        map.put("Proposal","com.code.aon.purchase.Proposal");
        map.put("ProposalDetail","com.code.aon.purchase.ProposalDetail");
		map.put("Purchase","com.code.aon.purchase.Purchase");
		map.put("PurchaseDetail","com.code.aon.purchase.PurchaseDetail");

		//AON REGISTRY 
		map.put("Person","com.code.aon.person.Person");
		map.put("Category","com.code.aon.registry.Category");
		map.put("Question","com.code.aon.registry.Question");
		map.put("QuestionValue","com.code.aon.registry.QuestionValue");		
		map.put("RecordData","com.code.aon.registry.RecordData");
		map.put("Registry","com.code.aon.registry.Registry");
		map.put("RegistryAddInfo","com.code.aon.registry.RegistryAddInfo");
		map.put("RegistryAddress","com.code.aon.registry.RegistryAddress");
		map.put("RegistryAttachment","com.code.aon.registry.RegistryAttachment");
		map.put("RegistryAttachmentTag","com.code.aon.registry.RegistryAttachmentTag");
		map.put("RegistryDirStaff","com.code.aon.registry.RegistryDirStaff");
		map.put("RegistryBank","com.code.aon.registry.RegistryBank");
		map.put("RegistryItem","com.code.aon.registry.RegistryItem");
		map.put("RegistryMedia","com.code.aon.registry.RegistryMedia");
		map.put("RegistryNote","com.code.aon.registry.RegistryNote");
		map.put("RegistryPayMethod","com.code.aon.registry.RegistryPayMethod");
		map.put("RegistryProfile","com.code.aon.registry.RegistryProfile");
		map.put("RegistryRelationship","com.code.aon.registry.RegistryRelationship");
		map.put("RegistrySegment","com.code.aon.registry.RegistrySegment");
		map.put("RegistrySeller","com.code.aon.registry.RegistrySeller");
		map.put("RegistrySupplier","com.code.aon.registry.RegistrySupplier");		
		map.put("RegistryTax","com.code.aon.registry.RegistryTax");		
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
		map.put("ContactData","com.code.aon.webmail.db.ContactData");
		map.put("ContactDetail","com.code.aon.webmail.db.ContactDetail");
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
		
		//AON-DOCUMENTAL
		map.put("Rdoc","com.code.aon.documental.Rdoc");
		map.put("NordigenCallLog", "com.code.aon.nordigen.NordigenCallLog");
		
		//AON-MESSAGING
		//map.put("Message","com.code.aon.messaging.Message");
		//map.put("MessageContent","com.code.aon.messaging.MessageContent");
		//map.put("FiscalBatch","com.code.aon.fiscal.FiscalBatch");
		//map.put("FiscalBatchDetail","com.code.aon.fiscal.FiscalBatchDetail");
	}

}
