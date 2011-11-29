package com.esferalia.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.apache.commons.lang.StringUtils;

import com.code.aon.registry.enumeration.StreetType;
import com.esferalia.aon.payroll.enumeration.AgeCollective;
import com.esferalia.aon.payroll.enumeration.AgeGroup;
import com.esferalia.aon.payroll.enumeration.BasicCopySignatureType;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.CNO;
import com.esferalia.aon.payroll.enumeration.Certifica2BatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.CollectiveReductionCode;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractBatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCalendarEventType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractDuration;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.DisabilityCode;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.DischargeCause;
import com.esferalia.aon.payroll.enumeration.DismissCause;
import com.esferalia.aon.payroll.enumeration.DismissalCollective;
import com.esferalia.aon.payroll.enumeration.EducationalLevel;
import com.esferalia.aon.payroll.enumeration.EmbargableType;
import com.esferalia.aon.payroll.enumeration.EmployeeType;
import com.esferalia.aon.payroll.enumeration.EmploymentProgram;
import com.esferalia.aon.payroll.enumeration.EnterpriseActivityType;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.payroll.enumeration.FanBatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.InactiveLastPeriod;
import com.esferalia.aon.payroll.enumeration.InterimCause;
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;
import com.esferalia.aon.payroll.enumeration.LeaveBatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.LiquidationType;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.OtherLaws;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.ResearchEmployee;
import com.esferalia.aon.payroll.enumeration.ResearchEmployer;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.SchoolWorkshop;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.payroll.enumeration.TaxationType;
import com.esferalia.aon.payroll.enumeration.WorkingDayType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class PayrollCollectionsController {

	private List<SelectItem> contractDurations;
	private List<SelectItem> contractWorkingDays;
	private List<SelectItem> contractCalendarEventTypes;
	private List<SelectItem> paymentTypes;
	private List<SelectItem> deductionTypes;
	private List<SelectItem> salaryTypes;
	
	private List<SelectItem> contractCodes;
	private Map<ContractType,List<SelectItem>> contractCodesMap;
	private List<SelectItem> contractModels;
	private List<SelectItem> contractOptions;
	private Map<ContractOption,List<SelectItem>> contractTypesMap;
	private List<SelectItem> quoteGroups;
	private List<SelectItem> occupationTypes;
	private List<SelectItem> ssRegimes;
	
	private List<SelectItem> streetTypes;
	private List<SelectItem> leaveReportTypes;
	private List<SelectItem> leaveTypes;
	private List<SelectItem> dischargeCauses;
	private List<SelectItem> suspensionCauses;
	private List<SelectItem> fileStatus;
	private List<SelectItem> cnoList;
	private List<SelectItem> employmentProgramList;
	private List<SelectItem> otherLawsList;
	private List<SelectItem> educationalLevelList;
	private List<SelectItem> disabilityCodeList;
	private List<SelectItem> dismissalCollectiveList;
	private List<SelectItem> employeeTypeList;
	private List<SelectItem> schoolWorkshopList;
	private List<SelectItem> basicCopySignatureTypeList;
	private List<SelectItem> ageGroupList;
	private List<SelectItem> embargableTypeList;
	private List<SelectItem> dismissCauseList;
	private List<SelectItem> contractAttachTypes;
	private List<SelectItem> inactiveLastPeriod;
	
	private List<SelectItem> cccTypes;
	private List<SelectItem> enterpriseActivityTypes;

	private List<SelectItem> familySityations;
	private List<SelectItem> disabilityLevels;
	private List<SelectItem> irpfRegularizationReasons;
	private List<SelectItem> taxationTypes;
	private List<SelectItem> quoteTypes;
	private List<SelectItem> reportTypes;
	private List<SelectItem> leaveBatchAttachTypes;
	private List<SelectItem> contractBatchAttachTypes;
	private List<SelectItem> fanBatchAttachTypes;
	private List<SelectItem> certifica2BatchAttachTypes;

	private List<SelectItem> liquidationTypes;
	private List<SelectItem> interimCauses;
	private List<SelectItem> researchEmployers;
	private List<SelectItem> researchEmployees;
	private List<SelectItem> collectiveReductionCodes;
	private List<SelectItem> workingDayTypes;
	private List<SelectItem> ageCollectives;
	
	public List<SelectItem> getPaymentTypes() {
		if (paymentTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			paymentTypes = new LinkedList<SelectItem>();
			for( PaymentType paymentType : PaymentType.values() ) {
				String name = paymentType.getName(locale);
				SelectItem item = new SelectItem(paymentType, name);
				paymentTypes.add(item);			
			}
		}
		return paymentTypes;
	}
	
	public List<SelectItem> getDeductionTypes() {
		if (deductionTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			deductionTypes = new LinkedList<SelectItem>();
			for( DeductionType deductionType : DeductionType.values() ) {
				String name = deductionType.getName(locale);
				SelectItem item = new SelectItem(deductionType, name);
				deductionTypes.add(item);			
			}
		}
		return deductionTypes;
	}
	
	public List<SelectItem> getSalaryTypes() {
		if (salaryTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			salaryTypes = new LinkedList<SelectItem>();
			for( SalaryType salaryType : SalaryType.values() ) {
				String name = salaryType.getName(locale);
				SelectItem item = new SelectItem(salaryType, name);
				salaryTypes.add(item);			
			}
		}
		return salaryTypes;
	}

	public List<SelectItem> getContractDurations() {
		if (contractDurations == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractDurations = new LinkedList<SelectItem>();
			for( ContractDuration contractDuration : ContractDuration.values() ) {
				String name = contractDuration.getName(locale);
				SelectItem item = new SelectItem(contractDuration, name);
				contractDurations.add(item);			
			}
		}
		return contractDurations;
	}

	public List<SelectItem> getContractWorkingDays() {
		if (contractWorkingDays == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractWorkingDays = new LinkedList<SelectItem>();
			for( ContractWorkingDay contractWorkingDay : ContractWorkingDay.values() ) {
				String name = contractWorkingDay.getName(locale);
				SelectItem item = new SelectItem(contractWorkingDay, name);
				contractWorkingDays.add(item);			
			}
		}
		return contractWorkingDays;
	}
	
	public List<SelectItem> getContractCalendarEventTypes() {
		if (contractCalendarEventTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractCalendarEventTypes = new LinkedList<SelectItem>();
			for( ContractCalendarEventType type : ContractCalendarEventType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				contractCalendarEventTypes.add(item);			
			}
		}
		return contractCalendarEventTypes;
	}
	
	public List<SelectItem> getContractOptions() {
		if (contractOptions == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractOptions = new LinkedList<SelectItem>();
			ContractOption[] options = ContractOption.values();
			for (ContractOption o : options) {
				if(o==ContractOption.INDEFINITE){
					String name = o.getName(locale);
					SelectItem item = new SelectItem(o, name);
					contractOptions.add(item);
				}
			}
		}
		return contractOptions;
	}
	
	public List<SelectItem> getContractCodes() {
		if (contractCodes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			contractCodes = new LinkedList<SelectItem>();
			ContractCode[] codes = ContractCode.values();
			for (ContractCode cc : codes) {
				if(cc==ContractCode.C100){
					String name = cc.getName(locale);
					SelectItem item = new SelectItem(cc, name);
					contractCodes.add(item);
				}
			}
		}
		return contractCodes;
	}
	
	public List<SelectItem> getContractModels() {
		if (contractModels == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			contractModels = new LinkedList<SelectItem>();
			ContractModel[] models = ContractModel.values();
			for (ContractModel cm : models) {
				if(cm==ContractModel.PE170){
					String name = cm.getDescription(locale);
					SelectItem item = new SelectItem(cm, name);
					contractModels.add(item);
				}
			}
		}
		return contractModels;
	}
	
	public Map<ContractType,List<SelectItem>> getContractCodesMap() {
		if (contractCodesMap == null) {
			contractCodesMap = new HashMap<ContractType, List<SelectItem>>();
			List<SelectItem> list = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(null, "-");
			list.add(item);
			contractCodesMap.put(null, list);
			for (ContractType contractType:ContractType.values()) {
				list = new LinkedList<SelectItem>();
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				for (ContractCode c : contractType.getCodes()) {
					String name = c.getName(locale);
					item = new SelectItem(c, StringUtils.abbreviate(name,100));
					list.add(item);
				}
				contractCodesMap.put(contractType, list);
			}
		}
		return contractCodesMap;
	}
	
	public Map<ContractOption, List<SelectItem>> getContractTypesMap() {
		if (contractTypesMap == null) {
			contractTypesMap = new HashMap<ContractOption, List<SelectItem>>();
			List<SelectItem> list = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(null, "-");
			list.add(item);
			contractTypesMap.put(null, list);
			for (ContractOption contractOption:ContractOption.values()) {
				list = new LinkedList<SelectItem>();
				if(contractOption!=null){
					Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
					for (ContractType t : contractOption.getTypes()) {
						String name = t.getName(locale);
						item = new SelectItem(t, StringUtils.abbreviate(name,100));
						list.add(item);
					}
				}
				contractTypesMap.put(contractOption, list);
			}
		}
		return contractTypesMap;
	}

	public List<SelectItem> getStreetTypes() {
		if (streetTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			streetTypes = new LinkedList<SelectItem>();
			StreetType[] models = StreetType.values();
			for (StreetType cm : models) {
				String name = cm.getName(locale);
				SelectItem item = new SelectItem(cm, name);
				streetTypes.add(item);
			}
		}
		return streetTypes;
	}
	
	public List<SelectItem> getQuoteGroups() {
		if (quoteGroups == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			quoteGroups = new LinkedList<SelectItem>();
			QuoteGroup[] models = QuoteGroup.values();
			for (QuoteGroup cm : models) {
				String name = cm.getFullName(locale);
				SelectItem item = new SelectItem(cm, name);
				quoteGroups.add(item);
			}
		}
		return quoteGroups;
	}
	
	public List<SelectItem> getOccupationTypes() {
		if (occupationTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			occupationTypes = new LinkedList<SelectItem>();
			OccupationType[] types = OccupationType.values();
			for (OccupationType t : types) {
				String name = t.getFullName(locale);
				SelectItem item = new SelectItem(t, name);
				occupationTypes.add(item);
			}
		}
		return occupationTypes;
	}
	
	public List<SelectItem> getSsRegimes() {
		if (ssRegimes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			ssRegimes = new LinkedList<SelectItem>();
			SSRegimeType[] models = SSRegimeType.values();
			for (SSRegimeType ss : models) {
				String name = ss.getName(locale);
				SelectItem item = new SelectItem(ss, name);
				ssRegimes.add(item);
			}
		}
		return ssRegimes;
	}
	
	public List<SelectItem> getLeaveReportTypes() {
		if (leaveReportTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			leaveReportTypes = new LinkedList<SelectItem>();
			LeaveReportType[] reports = LeaveReportType.values();
			for (LeaveReportType r : reports) {
				String name = r.getName(locale);
				SelectItem item = new SelectItem(r, name);
				leaveReportTypes.add(item);
			}
		}
		return leaveReportTypes;
	}
	
	public List<SelectItem> getLeaveTypes() {
		if (leaveTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			leaveTypes = new LinkedList<SelectItem>();
			LeaveType[] leaves = LeaveType.values();
			for (LeaveType l : leaves) {
				String name = l.getName(locale);
				SelectItem item = new SelectItem(l, name);
				leaveTypes.add(item);
			}
		}
		return leaveTypes;
	}
	
	public List<SelectItem> getDischargeCauses() {
		if (dischargeCauses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			dischargeCauses = new LinkedList<SelectItem>();
			DischargeCause[] causes = DischargeCause.values();
			for (DischargeCause l : causes) {
				String name = l.getName(locale);
				SelectItem item = new SelectItem(l, name);
				dischargeCauses.add(item);
			}
		}
		return dischargeCauses;
	}
	
	public List<SelectItem> getSuspensionCauses() {
		if (suspensionCauses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			suspensionCauses = new LinkedList<SelectItem>();
			SuspensionCause[] causes = SuspensionCause.values();
			for (SuspensionCause c : causes) {
				String name = c.getFullName(locale);
				SelectItem item = new SelectItem(c, name);
				suspensionCauses.add(item);
			}
		}
		return suspensionCauses;
	}
	
	public List<SelectItem> getFileStatus() {
		if (fileStatus == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			fileStatus = new LinkedList<SelectItem>();
			FileStatus[] estados = FileStatus.values();
			for (FileStatus c : estados) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				fileStatus.add(item);
			}
		}
		return fileStatus;
	}
	
	public List<SelectItem> getCnoList() {
		if (cnoList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			cnoList = new LinkedList<SelectItem>();
			CNO[] cno = CNO.values();
			for (CNO c : cno) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				cnoList.add(item);
			}
		}
		return cnoList;
	}
	
	public List<SelectItem> getEmploymentProgramList() {
		if (employmentProgramList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			employmentProgramList = new LinkedList<SelectItem>();
			EmploymentProgram[] ep = EmploymentProgram.values();
			for (EmploymentProgram c : ep) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				employmentProgramList.add(item);
			}
		}
		return employmentProgramList;
	}
	
	public List<SelectItem> getOtherLawsList() {
		if (otherLawsList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			otherLawsList = new LinkedList<SelectItem>();
			OtherLaws[] ol = OtherLaws.values();
			for (OtherLaws c : ol) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				otherLawsList.add(item);
			}
		}
		return otherLawsList;
	}
	
	public List<SelectItem> getEducationalLevelList() {
		if (educationalLevelList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			educationalLevelList = new LinkedList<SelectItem>();
			EducationalLevel[] el = EducationalLevel.values();
			for (EducationalLevel c : el) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, StringUtils.abbreviate(name, 50));
				educationalLevelList.add(item);
			}
		}
		return educationalLevelList;
	}
	
	public List<SelectItem> getDisabilityCodeList() {
		if (disabilityCodeList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			disabilityCodeList = new LinkedList<SelectItem>();
			DisabilityCode[] el = DisabilityCode.values();
			for (DisabilityCode c : el) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				disabilityCodeList.add(item);
			}
		}
		return disabilityCodeList;
	}
	
	public List<SelectItem> getDismissalCollectiveList() {
		if (dismissalCollectiveList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			dismissalCollectiveList = new LinkedList<SelectItem>();
			DismissalCollective[] el = DismissalCollective.values();
			for (DismissalCollective c : el) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				dismissalCollectiveList.add(item);
			}
		}
		return dismissalCollectiveList;
	}
	
	public List<SelectItem> getEmployeeTypeList() {
		if (employeeTypeList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			employeeTypeList = new LinkedList<SelectItem>();
			EmployeeType[] el = EmployeeType.values();
			for (EmployeeType c : el) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				employeeTypeList.add(item);
			}
		}
		return employeeTypeList;
	}
	
	public List<SelectItem> getSchoolWorkshopList() {
		if (schoolWorkshopList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			schoolWorkshopList = new LinkedList<SelectItem>();
			SchoolWorkshop[] el = SchoolWorkshop.values();
			for (SchoolWorkshop c : el) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				schoolWorkshopList.add(item);
			}
		}
		return schoolWorkshopList;
	}
	
	public List<SelectItem> getBasicCopySignatureTypeList() {
		if (basicCopySignatureTypeList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			basicCopySignatureTypeList = new LinkedList<SelectItem>();
			BasicCopySignatureType[] el = BasicCopySignatureType.values();
			for (BasicCopySignatureType c : el) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				basicCopySignatureTypeList.add(item);
			}
		}
		return basicCopySignatureTypeList;
	}
	
	public List<SelectItem> getAgeGroupList() {
		if (ageGroupList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			ageGroupList = new LinkedList<SelectItem>();
			AgeGroup[] el = AgeGroup.values();
			for (AgeGroup c : el) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				ageGroupList.add(item);
			}
		}
		return ageGroupList;
	}
	
	public List<SelectItem> getEmbargableTypeList() {
		if (embargableTypeList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			embargableTypeList = new LinkedList<SelectItem>();
			EmbargableType[] el = EmbargableType.values();
			for (EmbargableType c : el) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				embargableTypeList.add(item);
			}
		}
		return embargableTypeList;
	}
	
	public List<SelectItem> getDismissCauseList() {
		if (dismissCauseList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			dismissCauseList = new LinkedList<SelectItem>();
			DismissCause[] el = DismissCause.values();
			for (DismissCause c : el) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				dismissCauseList.add(item);
			}
		}
		return dismissCauseList;
	}
	
	public List<SelectItem> getContractAttachTypes() {
		if (contractAttachTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractAttachTypes = new LinkedList<SelectItem>();
			ContractAttachmentType[] cat = ContractAttachmentType.values();
			for (ContractAttachmentType c : cat) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				contractAttachTypes.add(item);
			}
		}
		return contractAttachTypes;
	}
	
	public List<SelectItem> getCCCTypes() {
		if (cccTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			cccTypes = new LinkedList<SelectItem>();
			for( CCCType cccType : CCCType.values() ) {
				String name = cccType.getName(locale);
				SelectItem item = new SelectItem(cccType, name);
				cccTypes.add(item);			
			}
		}
		return cccTypes;
	}	

	public List<SelectItem> getEnterpriseActivityTypes() {
		if (enterpriseActivityTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			enterpriseActivityTypes = new LinkedList<SelectItem>();
			for( EnterpriseActivityType type : EnterpriseActivityType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				enterpriseActivityTypes.add(item);			
			}
		}
		return enterpriseActivityTypes;
	}
	
	public List<SelectItem> getInactiveLastPeriods() {
		if (inactiveLastPeriod == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			inactiveLastPeriod = new LinkedList<SelectItem>();
			for( InactiveLastPeriod p : InactiveLastPeriod.values() ) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				inactiveLastPeriod.add(item);			
			}
		}
		return inactiveLastPeriod;
	}
	
	public List<SelectItem> getFamilySituations() {
		if (familySityations == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			familySityations = new LinkedList<SelectItem>();
			for( FamilySituation p : FamilySituation.values() ) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				familySityations.add(item);			
			}
		}
		return familySityations;
	}
	
	public List<SelectItem> getDisabilityLevels() {
		if (disabilityLevels == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			disabilityLevels = new LinkedList<SelectItem>();
			for( DisabilityLevel p : DisabilityLevel.values() ) {
				if(p!=DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE){
					String name = p.getName(locale);
					SelectItem item = new SelectItem(p, name);
					disabilityLevels.add(item);			
				}
			}
		}
		return disabilityLevels;
	}
	
	public List<SelectItem> getIrpfRegularizationReasons() {
		if (irpfRegularizationReasons == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			irpfRegularizationReasons = new LinkedList<SelectItem>();
			for( IrpfRegularizationReason p : IrpfRegularizationReason.values() ) {
					String name = p.getName(locale);
					SelectItem item = new SelectItem(p, name);
					irpfRegularizationReasons.add(item);			
			}
		}
		return irpfRegularizationReasons;
	}
	
	public List<?> getTc2List() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItemGroup> list = new LinkedList<SelectItemGroup>();
		for( ContractType p : ContractType.values() ) {
			List<SelectItem> subList = new ArrayList<SelectItem>();
			for( ContractCode c : p.getCodes() ) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				subList.add(item);			
			}
			SelectItemGroup group = new SelectItemGroup(p.getName(locale), p.getName(locale), false, subList.toArray(new SelectItem[0]));
			list.add(group);
		}
		return list;
	}
	
	public List<SelectItem> getTaxationTypes() {
		if (taxationTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			taxationTypes = new LinkedList<SelectItem>();
			for( TaxationType p : TaxationType.values() ) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				taxationTypes.add(item);			
			}
		}
		return taxationTypes;
	}
	
	public List<SelectItem> getQuoteTypes() {
		if (quoteTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			quoteTypes = new LinkedList<SelectItem>();
			for( QuoteType p : QuoteType.values() ) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				quoteTypes.add(item);			
			}
		}
		return quoteTypes;
	}
	
	public List<SelectItem> getReportTypesList() {
		if (reportTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			reportTypes = new LinkedList<SelectItem>();
			for( LeaveReportType type : LeaveReportType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				reportTypes.add(item);			
			}
		}
		return reportTypes;
	}
	
	public List<SelectItem> getLeaveBatchAttachTypes() {
		if (leaveBatchAttachTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			leaveBatchAttachTypes = new LinkedList<SelectItem>();
			for( LeaveBatchAttachmentType type : LeaveBatchAttachmentType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				leaveBatchAttachTypes.add(item);			
			}
		}
		return leaveBatchAttachTypes;
	}
	
	public List<SelectItem> getContractBatchAttachTypes() {
		if (contractBatchAttachTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractBatchAttachTypes = new LinkedList<SelectItem>();
			for( ContractBatchAttachmentType type : ContractBatchAttachmentType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				contractBatchAttachTypes.add(item);			
			}
		}
		return contractBatchAttachTypes;
	}
	
	public List<SelectItem> getFanBatchAttachTypes() {
		if (fanBatchAttachTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			fanBatchAttachTypes = new LinkedList<SelectItem>();
			for( FanBatchAttachmentType type : FanBatchAttachmentType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				fanBatchAttachTypes.add(item);			
			}
		}
		return fanBatchAttachTypes;
	}
	
	public List<SelectItem> getCertifica2BatchAttachTypes() {
		if (certifica2BatchAttachTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			certifica2BatchAttachTypes = new LinkedList<SelectItem>();
			for( Certifica2BatchAttachmentType type : Certifica2BatchAttachmentType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				certifica2BatchAttachTypes.add(item);			
			}
		}
		return certifica2BatchAttachTypes;
	}
	
	public List<SelectItem> getLiquidationTypes() {
		if (liquidationTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			liquidationTypes = new LinkedList<SelectItem>();
			for( LiquidationType type : LiquidationType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				liquidationTypes.add(item);			
			}
		}
		return liquidationTypes;
	}
	
	public List<SelectItem> getInterimCauses() {
		if (interimCauses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			interimCauses = new LinkedList<SelectItem>();
			for( InterimCause type : InterimCause.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				interimCauses.add(item);			
			}
		}
		return interimCauses;
	}
	
	public List<SelectItem> getResearchEmployers() {
		if (researchEmployers == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			researchEmployers = new LinkedList<SelectItem>();
			for( ResearchEmployer type : ResearchEmployer.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				researchEmployers.add(item);			
			}
		}
		return researchEmployers;
	}
	
	public List<SelectItem> getResearchEmployees() {
		if (researchEmployees == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			researchEmployees = new LinkedList<SelectItem>();
			for( ResearchEmployee type : ResearchEmployee.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				researchEmployees.add(item);			
			}
		}
		return researchEmployees;
	}
	
	public List<SelectItem> getCollectiveReductionCodes() {
		if (collectiveReductionCodes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			collectiveReductionCodes = new LinkedList<SelectItem>();
			for( CollectiveReductionCode type : CollectiveReductionCode.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				collectiveReductionCodes.add(item);			
			}
		}
		return collectiveReductionCodes;
	}
	
	public List<SelectItem> getWorkingDayTypes() {
		if (workingDayTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			workingDayTypes = new LinkedList<SelectItem>();
			for( WorkingDayType type : WorkingDayType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				workingDayTypes.add(item);			
			}
		}
		return workingDayTypes;
	}
	
	public List<SelectItem> getAgeCollectives() {
		if (ageCollectives == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			ageCollectives = new LinkedList<SelectItem>();
			for( AgeCollective type : AgeCollective.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				ageCollectives.add(item);			
			}
		}
		return ageCollectives;
	}
	
		
}