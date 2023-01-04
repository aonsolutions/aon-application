package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.enumeration.SalaryTemplate;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.enumeration.AfiLeaveType;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.CNO;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCalendarEventType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractDuration;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractModelCode;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.DischargeCause;
import com.esferalia.aon.payroll.enumeration.DismissCause;
import com.esferalia.aon.payroll.enumeration.EmbargableType;
import com.esferalia.aon.payroll.enumeration.EnterpriseActivityType;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.InactiveLastPeriod;
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.LiquidationType;
import com.esferalia.aon.payroll.enumeration.Mutual;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.PayrollBatchAttachmentType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.TaxationType;
import com.esferalia.aon.payroll.enumeration.TrainingModality;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.SepeAppParamsController;

public class PayrollCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final int NAME_LENGHT_80 = 80;	
	private final int NAME_LENGHT_100 = 100;	

	private List<SelectItem> contractDurations;
	private List<SelectItem> contractWorkingDays;
	private List<SelectItem> contractCalendarEventTypes;
	private List<SelectItem> paymentTypes;
	private List<SelectItem> deductionTypes;
	private List<SelectItem> bonusTypes;
	private List<SelectItem> salaryTypes;
	
	private List<SelectItem> contractCodes;
	private List<SelectItem> contractTransformCodes;
	private Map<ContractType,List<SelectItem>> contractCodesMap;
	private List<SelectItem> contractModels;
	private List<SelectItem> contractOptions;
	private Map<ContractOption,List<SelectItem>> contractTypesMap;
	private List<SelectItem> quoteGroups;
	private List<SelectItem> occupationTypes;
	private List<SelectItem> ssRegimes;
	private List<SelectItem> trainingModalities;
	
	private List<SelectItem> streetTypes;
	private List<SelectItem> leaveReportTypes;
	private List<SelectItem> leaveTypes;
	private List<SelectItem> dischargeCauses;
	private List<SelectItem> fileStatus;
	private List<SelectItem> cnoList;
	
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
	private List<SelectItem> payrollBatchAttachTypes;
	
	
	private List<SelectItem> liquidationTypes;
	private List<SelectItem> mutualList;
	private List<SelectItem> salaryTemplates;
	private List<SelectItem> afiLeaveTypes;
	
	
	private String getAbbreviatedSelectItemLabel(String name, int lenght) {
		if(name.length()>lenght){
			return name.substring(0, lenght)+"...";
		}
		return name;
	}
	
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
	
	public List<SelectItem> getBonusTypes() {
		if (bonusTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			bonusTypes = new LinkedList<SelectItem>();
			for( BonusType bonusType : BonusType.values() ) {
				String name = bonusType.getName(locale);
				SelectItem item = new SelectItem(bonusType, name);
				bonusTypes.add(item);			
			}
		}
		return bonusTypes;
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
	
	public List<SelectItem> getAllContractCodes() {
		String INTERNSHIP = "BECARIOS";
		List<SelectItem> list;
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		list = new LinkedList<SelectItem>();
		ContractCode[] codes = ContractCode.values();
		for (ContractCode code : codes) {
			String name = code.getValue() +" - "+ code.getName(locale);
			SelectItem item = new SelectItem(code.getValue(), name);
			list.add(item);
		}
		// CONTRATOS NO NORMALIZADOS
		list.add(new SelectItem("000", INTERNSHIP));
		return list;
	}
	
	public List<SelectItem> getContractCodes() {
		if (contractCodes == null) {
			String[] TRANSFORM_CODES = {"189", "109", "139", "289", "209", "239", "309", "339", "389"};
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractCodes = new LinkedList<SelectItem>();
			ContractCode[] codes = ContractCode.values();
			for (ContractCode code : codes) {
				if( !ArrayUtils.contains(TRANSFORM_CODES, code.getValue()) ){
					String name = code.getValue() +" - "+ code.getName(locale);
					SelectItem item = new SelectItem(code.getValue(), name);
					contractCodes.add(item);
				}
			}
			// CONTRATOS NO NORMALIZADOS
			contractCodes.add(new SelectItem("000", "BECARIOS"));
		}
		return contractCodes;
	}

	public List<SelectItem> getContractTransformCodes() {
		if (contractTransformCodes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractTransformCodes = new LinkedList<SelectItem>();
			ContractCode[] codes = ContractCode.values();
			for (ContractCode code : codes) {
				if( ArrayUtils.contains(ISepeConstants.AVAILABLE_TRANSFORM_CODE_COMMUNICATION, code.getValue()) ){
					String name = code.getValue() +" - "+ code.getName(locale);
					if(StringUtils.contains(name,", TRANSFORMACIÓN CONTRATO TEMPORAL")){
						name = StringUtils.replace(name, ", TRANSFORMACIÓN CONTRATO TEMPORAL", "");
					}
					SelectItem item = new SelectItem(code.getValue(), name);
					contractTransformCodes.add(item);
				}
			}
		}
		return contractTransformCodes;
	}
	
	public List<SelectItem> getContractModels() {
		if (contractModels == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractModels = new LinkedList<SelectItem>();
			ContractModel[] models = ContractModel.values();
			for (ContractModel model : models) {
				String name = model.name() + " - " + model.getName(locale);
				SelectItem item = new SelectItem(model, name);
				contractModels.add(item);
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
				SelectItem item = new SelectItem(cm, getAbbreviatedSelectItemLabel(name, NAME_LENGHT_80));
				quoteGroups.add(item);
			}
		}
		return quoteGroups;
	}
	
	public List<SelectItem> getOccupationTypes() {
		if (occupationTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			occupationTypes = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(OccupationType.A, getAbbreviatedSelectItemLabel(OccupationType.A.getFullName(locale), NAME_LENGHT_80));
			occupationTypes.add(item);
			item = new SelectItem(OccupationType.B, getAbbreviatedSelectItemLabel(OccupationType.B.getFullName(locale), NAME_LENGHT_80));
			occupationTypes.add(item);
			item = new SelectItem(OccupationType.D, getAbbreviatedSelectItemLabel(OccupationType.D.getFullName(locale), NAME_LENGHT_80));
			occupationTypes.add(item);
			item = new SelectItem(OccupationType.E, getAbbreviatedSelectItemLabel(OccupationType.E.getFullName(locale), NAME_LENGHT_80));
			occupationTypes.add(item);
			item = new SelectItem(OccupationType.F, getAbbreviatedSelectItemLabel(OccupationType.F.getFullName(locale), NAME_LENGHT_80));
			occupationTypes.add(item);
			item = new SelectItem(OccupationType.G, getAbbreviatedSelectItemLabel(OccupationType.G.getFullName(locale), NAME_LENGHT_80));
			occupationTypes.add(item);
			item = new SelectItem(OccupationType.H, getAbbreviatedSelectItemLabel(OccupationType.H.getFullName(locale), NAME_LENGHT_80));
			occupationTypes.add(item);
		}
		return occupationTypes;
	}
	
	public List<SelectItem> getAvailableSsRegimes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		LinkedList<SelectItem> ssRegimes = new LinkedList<SelectItem>();
		ssRegimes.add(new SelectItem(SSRegimeType.GENERAL, SSRegimeType.GENERAL
				.getName(locale)));
		ssRegimes.add(new SelectItem(SSRegimeType.COAL_MINING,
				SSRegimeType.COAL_MINING.getName(locale), null, true));
		ssRegimes.add(new SelectItem(SSRegimeType.SEA_WORKERS,
				SSRegimeType.SEA_WORKERS.getName(locale), null, true));
		ssRegimes.add(new SelectItem(SSRegimeType.ISFAS,
				SSRegimeType.ISFAS.getName(locale)));
		ssRegimes.add(new SelectItem(SSRegimeType.MUFACE,
				SSRegimeType.MUFACE.getName(locale)));
//		ssRegimes.add(new SelectItem(SSRegimeType.AGRICULTURAL,
//				 SSRegimeType.AGRICULTURAL.getName(locale), null, true));
//		ssRegimes.add(new SelectItem(SSRegimeType.ARTIST, SSRegimeType.ARTIST
//				.getName(locale), null, true));
//		ssRegimes.add(new SelectItem(SSRegimeType.DOMESTIC_EMPLOYEES,
//				SSRegimeType.DOMESTIC_EMPLOYEES.getName(locale), null, true));
//		ssRegimes.add(new SelectItem(SSRegimeType.SELF_EMPLOYED,
//				SSRegimeType.SELF_EMPLOYED.getName(locale), null, true));
//		ssRegimes.add(new SelectItem(SSRegimeType.STUDENT_INSURANCE,
//				SSRegimeType.STUDENT_INSURANCE.getName(locale), null, true));
		return ssRegimes;
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
	
	public List<SelectItem> getTrainingModalities() {
		if (trainingModalities == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			trainingModalities = new LinkedList<SelectItem>();
			TrainingModality[] types = TrainingModality.values();
			for (TrainingModality type : types) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				trainingModalities.add(item);
			}
		}
		return trainingModalities;
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
	
	public List<SelectItem> getFileStatus() {
		if (fileStatus == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			fileStatus = new LinkedList<SelectItem>();
			for (FileStatus status : FileStatus.values()) {
				SelectItem item = new SelectItem(status, status.getName(locale));
				fileStatus.add(item);
			}
		}
		return fileStatus;
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
		SepeAppParamsController paramsController = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		contractAttachTypes = new LinkedList<SelectItem>();
		ContractAttachmentType[] cat = ContractAttachmentType.values();
		for (ContractAttachmentType t : cat) {
			if (paramsController.getDevelopmentMode() || AonUtil.getRoleManager().isSysAdmin()
					|| (!paramsController.getDevelopmentMode() 
							&& t != ContractAttachmentType.SEPE_CONTRACT_FILE
							&& t != ContractAttachmentType.SEPE_CONTRACT_COMMUNICATION_ID
							&& t != ContractAttachmentType.SEPE_CONTRACT_RESPONSE
							&& t != ContractAttachmentType.SEPE_EXTENSION_FILE
							&& t != ContractAttachmentType.SEPE_EXTENSION_COMMUNICATION_ID
							&& t != ContractAttachmentType.SEPE_EXTENSION_RESPONSE
							&& t != ContractAttachmentType.SEPE_CERTIFICADOS_FILE
							&& t != ContractAttachmentType.SEPE_CERTIFICADOS_COMMUNICATION_ID
							&& t != ContractAttachmentType.SEPE_CERTIFICADOS_RESPONSE 
							&& t != ContractAttachmentType.SEPE_TRANSFORM_FILE 
							&& t != ContractAttachmentType.SEPE_TRANSFORM_COMMUNICATION_ID 
							&& t != ContractAttachmentType.SEPE_TRANSFORM_RESPONSE 
							&& t != ContractAttachmentType.CONTRACT_CLAUSES)) {
				String name = t.getName(locale);
				SelectItem item = new SelectItem(t, name);
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
		
		PayrollAppParamsController appParams = (PayrollAppParamsController) AonUtil.getRegisteredBean(IPayrollConstants.PAYROLL_APP_PARAMS_CONTROLLER_NAME);
		List<ContractCode> availableCodes = appParams.getAvailableNewContracts();
		
		for( ContractType type : ContractType.values() ) {
			if(type.getModel()==ContractModel.PE151
					|| type.getModel()==ContractModel.PE170
					|| type.getModel()==ContractModel.PE176
					|| type.getModel()==ContractModel.PE177
					|| type.getModel()==ContractModel.PE179
					|| type.getModel()==ContractModel.PE183
					|| type.getModel()==ContractModel.PE187
					|| type.getModel()==ContractModel.PE226
					){
				List<SelectItem> subList = new ArrayList<SelectItem>();
				for( ContractModelCode o : ContractModelCode.values() ) {
					if ( o.getModel() == type.getModel() ) {
						if ( availableCodes!=null && !availableCodes.isEmpty()) {
							if ( availableCodes.contains(o.getCode())) {
								String name = getAbbreviatedSelectItemLabel(o.getCode().getValue()+" - "+o.getCode().getName(locale), NAME_LENGHT_80);
								SelectItem item = new SelectItem(o, name);
								subList.add(item);
							}
						} else {
							String name = getAbbreviatedSelectItemLabel(o.getCode().getValue()+" - "+o.getCode().getName(locale), NAME_LENGHT_80);
							SelectItem item = new SelectItem(o, name);
							subList.add(item);
						}
					}
				}
				if(!subList.isEmpty()){
					SelectItemGroup group = new SelectItemGroup(getAbbreviatedSelectItemLabel(type.getName(locale), NAME_LENGHT_100), type.getName(locale), false, subList.toArray(new SelectItem[0]));
					group.setValue(type);
					list.add(group);
				}
			}
		}
		return list;
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
	
	public List<SelectItem> getCategoryList() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		return list;
	}
	
	public List<SelectItem> getQuoteGroupList() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( QuoteGroup p : QuoteGroup.values() ) {
			String name = p.getName(locale);
			SelectItem item = new SelectItem(p, getAbbreviatedSelectItemLabel(name, NAME_LENGHT_80));
			list.add(item);			
		}
		return list;
	}

	public List<SelectItem> getOccupationList() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( OccupationType p : OccupationType.values() ) {
			String name = p.getName(locale);
			SelectItem item = new SelectItem(p, getAbbreviatedSelectItemLabel(name, NAME_LENGHT_80));
			list.add(item);			
		}
		return list;
	}

	public List<SelectItem> getQuoteItList() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		return list;
	}
	
	public List<SelectItem> getTaxationTypes() {
		if (taxationTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			taxationTypes = new LinkedList<SelectItem>();
			for( TaxationType p : TaxationType.values() ) {
				if(!(AonUtil.getRoleManager().isConfig() && !AonUtil.getRoleManager().isAdmin() && p==TaxationType.MANUAL)){
					String name = p.getName(locale);
					SelectItem item = new SelectItem(p, name);
					taxationTypes.add(item);			
				}
			}
		}
		return taxationTypes;
	}
	
	public List<SelectItem> getQuoteTypes() {
		if (quoteTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			quoteTypes = new LinkedList<SelectItem>();
			for( QuoteType p : QuoteType.values() ) {
				if(!(AonUtil.getRoleManager().isConfig() && !AonUtil.getRoleManager().isAdmin() && (p==QuoteType.IPREM_EXCESS || p==QuoteType.MANUAL))){
					String name = p.getName(locale);
					SelectItem item = new SelectItem(p, name);
					quoteTypes.add(item);			
				}
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
	
	public List<SelectItem> getPayrollBatchAttachTypes() {
		if (payrollBatchAttachTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			payrollBatchAttachTypes = new LinkedList<SelectItem>();
			for( PayrollBatchAttachmentType type : PayrollBatchAttachmentType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				payrollBatchAttachTypes.add(item);			
			}
		}
		return payrollBatchAttachTypes;
	}
	
	public List<SelectItem> getLiquidationTypes() {
		if (liquidationTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			liquidationTypes = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(LiquidationType.L00, LiquidationType.L00.getValue()+ " - " + LiquidationType.L00.getName(locale));
			liquidationTypes.add(item);			
			item = new SelectItem(LiquidationType.L13, LiquidationType.L13.getValue()+ " - " + LiquidationType.L13.getName(locale));
			liquidationTypes.add(item);			
		}
		return liquidationTypes;
	}

	public List<SelectItem> getMutualList() {
		if (mutualList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			mutualList = new LinkedList<SelectItem>();
			for( Mutual mutual : Mutual.values() ) {
				SelectItem item = new SelectItem(mutual.getValue(), mutual.getName(locale));
				mutualList.add(item);			
			}
		}
		return mutualList;
	}
	
	public Long getTrainingCenterTotalCount(){
		 String select = "select count(*) " 
                 +" from TrainingCenter as trainingCenter " 
                 +" where " + DomainManager.getSQLWhereClause("trainingCenter.domain", true);
 		String name = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(name);
		Query query = session.createQuery(select);
		Iterator<?> iterator = query.list().iterator();
		if(iterator.hasNext()) {
			return (Long) iterator.next();
		}
		return null;
	}
	
	public List<SelectItem> getSalaryTemplates() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		salaryTemplates = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(SalaryTemplate.DEFAULT.getValue(), SalaryTemplate.DEFAULT.getName(locale), null, true);
		salaryTemplates.add(item);
		item = new SelectItem(SalaryTemplate.STANDARD_DUAL_COLUMN.getValue(), SalaryTemplate.STANDARD_DUAL_COLUMN.getName(locale), null, true);
		salaryTemplates.add(item);
		item = new SelectItem(SalaryTemplate.INVOICE_SIMPLE.getValue(), SalaryTemplate.INVOICE_SIMPLE.getName(locale), null, true);
		salaryTemplates.add(item);
		item = new SelectItem(SalaryTemplate.INVOICE_CRA_GROUP.getValue(), SalaryTemplate.INVOICE_CRA_GROUP.getName(locale), null, true);
		salaryTemplates.add(item);
		item = new SelectItem(SalaryTemplate.AON_SOLUTIONS_MACLEOD.getValue(), SalaryTemplate.AON_SOLUTIONS_MACLEOD.getName(locale));
		salaryTemplates.add(item);
		item = new SelectItem(SalaryTemplate.AON_SOLUTIONS_DEFAULT.getValue(), SalaryTemplate.AON_SOLUTIONS_DEFAULT.getName(locale));
		salaryTemplates.add(item);
			
		EnterpriseParamsController paramsController = (EnterpriseParamsController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_PARAMS_CONTROLLER_NAME);
		if(paramsController.getEnabledSalaryTemplates()!=null){
			for(SalaryTemplate template: paramsController.getEnabledSalaryTemplates()){
				if(template!=null){
					item = new SelectItem(template.getValue(), template.getName(locale));
					salaryTemplates.add(item);
				}
			}
		}
		return salaryTemplates;
	}
	
	public List<SelectItem> getAfiLeaveTypes() {
		if (afiLeaveTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			afiLeaveTypes = new LinkedList<SelectItem>();
			for( AfiLeaveType type : AfiLeaveType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				afiLeaveTypes.add(item);			
			}
		}
		return afiLeaveTypes;
	}

}