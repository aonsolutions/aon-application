package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ContractInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//Contract Table
	private Integer contractId;
	private Integer workplaceId;
	private String workplaceName;
	private String workplaceZIP;
	private String workplaceFullAddress;
	private Integer cccId;
	private String completeCCC;
	private String startDate;
	private String originalStartDate; // For trasnformations
	private String originalEndDate; // For trasnformations
	private String endDate;
	private String seniorityDate;
	private Integer activityId;
	private String enterpriseCIF;
	private Byte ssRegimen;
	private String agreementCategory;
	private Integer agreementLevelId;
	private Integer agreementId; //¿Innecesario? Posiblemente por que tenemos el nivel
	
	//Enterprise CCC Table
	private Byte cccType;
	
	//Contract Data Table
	private Integer contracttypeId;
	private String contractType;
	private Integer quotegroupId;
	private String quoteGroup;
	private Integer quoteGroupIdxMonthId;
	private boolean quoteGroupIdxMonth;
	private Integer ocupationId;
	private String ocupation;
	private Integer rlceId;
	private String rlce;
	private Integer journeytypeId;
	private Byte journeyType;
	
	//Contract Info Table
	private Integer contractmodelId;
	private Integer contractModel;
	private Integer retaId;
	
	//Contract Journey Duration
	private ContractJourneyDuration contractJourneyDuration;
	
	private String oldStartDate;
	private String oldEndDate;
	
	//Has Payroll
	private Boolean hasPayroll;
	private String payrollDate;
	
	// Comunic@ Fields
	private String colectiveAgreement;
	private String colectiveEmployees;
	
	private Integer mdCtzId;
	private String mdCtz;
	
	private Integer partialityCoefId;
	private Double partialityCoef;
	
	private Integer salariesCount;
	private ArrayList<ContractSalaryInfo> contractSalariesInfo;
	
	private String settleReason;
	private boolean hasSettle;
	private Date holidaysDate;
	private String saa;
	private boolean hasCertifica2;
	
	private boolean isTGSSActive;
	private String sepeId;
	
	private boolean hasExtension;
	private boolean hasTransformation;
	
	private boolean hasCto;
	private boolean hasCbc;
	
	private boolean discontinuos;
	
	public ContractInfo() {
		super();
		this.contractId = null;
		this.workplaceId = null;
		this.workplaceName = null;
		this.workplaceFullAddress = null;
		this.workplaceZIP = null;
		this.cccId = null;
		this.completeCCC = null;
		this.startDate = null;
		this.originalStartDate = null;
		this.originalEndDate = null;
		this.endDate = null;
		this.seniorityDate = null;
		this.activityId = null;
		this.enterpriseCIF = null;
		this.ssRegimen = null;
		this.agreementCategory = null;
		this.agreementLevelId = null;
		this.agreementId = null;
		this.cccType = null;
		this.contracttypeId = null;
		this.contractType = null;
		this.quotegroupId = null;
		this.quoteGroup = null;
		this.quoteGroupIdxMonthId = null;
		this.quoteGroupIdxMonth = false;
		this.ocupationId = null;
		this.ocupation = null;
		this.rlceId = null;
		this.rlce = null;
		this.journeytypeId = null;
		this.journeyType = null;
		this.contractmodelId = null;
		this.contractModel = null;
		this.retaId = null;
		this.contractJourneyDuration = new ContractJourneyDuration();
		this.oldStartDate = null;
		this.oldEndDate = null;
		this.hasPayroll = false;
		this.payrollDate = null;
		
		this.colectiveAgreement = null;
		this.colectiveEmployees = null;
		this.mdCtz = null;
		this.partialityCoef = null;
		
		this.salariesCount = null;
		this.contractSalariesInfo = new ArrayList<>();
		
		this.hasExtension = false;
		this.hasTransformation = false;
		
		this.hasCto = false;
		this.hasCbc = false;
		
		this.discontinuos = false;
	}
	
	// ------------- GETTERS / SETTERS -------------

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public String getEnterpriseCIF() {
		return enterpriseCIF;
	}

	public void setEnterpriseCIF(String enterpriseCIF) {
		this.enterpriseCIF = enterpriseCIF;
	}

	public Integer getCccId() {
		return cccId;
	}

	public void setCccId(Integer cccId) {
		this.cccId = cccId;
	}

	public String getCompleteCCC() {
		return completeCCC;
	}

	public void setCompleteCCC(String completeCCC) {
		this.completeCCC = completeCCC;
	}

	public Byte getCccType() {
		return cccType;
	}

	public void setCccType(Byte cccType) {
		this.cccType = cccType;
	}

	public Integer getWorkplaceId() {
		return workplaceId;
	}

	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}
	
	public String getWorkplaceName() {
		return workplaceName;
	}

	public void setWorkplaceName(String workplaceName) {
		this.workplaceName = workplaceName;
	}

	public String getWorkplaceZIP() {
		return workplaceZIP;
	}

	public void setWorkplaceZIP(String workplaceZIP) {
		this.workplaceZIP = workplaceZIP;
	}

	public String getWorkplaceFullAddress() {
		return workplaceFullAddress;
	}

	public void setWorkplaceFullAddress(String workplaceFullAddress) {
		this.workplaceFullAddress = workplaceFullAddress;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		if(AonStringUtils.isNotBlank(contractType) && contractType.contains("\""))
			try {
				this.contractType = contractType.split("\"")[1];
			} catch (IndexOutOfBoundsException e) {
				this.contractType = contractType;
			}	
		else
			this.contractType = contractType;
	}
	
//	public void setContractType(String contractType) {
//		this.contractType = contractType;
//	}

	public Integer getContractModel() {
		return contractModel;
	}

	public void setContractModel(Integer ordinal) {
		this.contractModel = ordinal;
	}

	public Date getStartDate() {
		return parse(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = format(startDate);
	}
	
	public Date getOriginalStartDate() {
		return parse(originalStartDate);
	}

	public void setOriginalStartDate(Date originalStartDate) {
		this.originalStartDate = format(originalStartDate);
	}
	
	public Date getOriginalEndDate() {
		return parse(originalEndDate);
	}

	public void setOriginalEndDate(Date originalEndDate) {
		this.originalEndDate = format(originalEndDate);
	}

	public Date getEndDate() {
		return Shared.parse(endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = format(endDate);
	}

	public Date getSeniorityDate() {
		return parse(seniorityDate);
	}

	public void setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = format(seniorityDate);
	}

	public Integer getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(Integer agreementId) {
		this.agreementId = agreementId;
	}

	public Integer getAgreementLevelId() {
		return agreementLevelId;
	}

	public void setAgreementLevelId(Integer agreementLevelId) {
		this.agreementLevelId = agreementLevelId;
	}

	public String getAgreementCategory() {
		return agreementCategory;
	}

	public void setAgreementCategory(String agreementCategory) {
		this.agreementCategory = agreementCategory;
	}

	public String getQuoteGroup() {
		return quoteGroup;
	}

	public void setQuoteGroup(String quoteGroup) {
		if(AonStringUtils.isNotBlank(quoteGroup) && quoteGroup.contains("\""))
			try {
				this.quoteGroup = quoteGroup.split("\"")[1];
			} catch (IndexOutOfBoundsException e) {
				this.quoteGroup = quoteGroup;
			}	
		else
			this.quoteGroup = quoteGroup;
	}
	
	public void setQuoteGroupIdxMonthId(Integer quoteGroupIdxMonthId) {
		this.quoteGroupIdxMonthId = quoteGroupIdxMonthId;
	}
	
	public Integer getQuoteGroupIdxMonthId() {
		return this.quoteGroupIdxMonthId;
	}
	
	public void setQuoteGroupIdxMonth(boolean quoteGroupIdxMonth) {
		this.quoteGroupIdxMonth = quoteGroupIdxMonth;
	}
	
	public boolean getQuoteGroupIdxMonth() {
		return this.quoteGroupIdxMonth;
	}

	public String getOcupation() {
		return ocupation;
	}

	public void setOcupation(String ocupation) {
		if(AonStringUtils.isNotBlank(ocupation) && ocupation.contains("\""))
			try {
				this.ocupation = ocupation.split("\"")[1];
			} catch (IndexOutOfBoundsException e) {
				this.ocupation = ocupation;
			}
		else
			this.ocupation = ocupation;
	}
	
	public String getRlce() {
		return rlce;
	}

	public void setRlce(String rlce) {
		if(null != rlce && rlce.contains("\""))
			this.rlce = rlce.split("\"")[1];
		else
			this.rlce = rlce;
	}

	public Byte getJourneyType() {
		return journeyType;
	}

	public void setJourneyType(Byte journeyType) {
		this.journeyType = journeyType;
	}

	public Byte getSsRegimen() {
		return ssRegimen;
	}

	public void setSsRegimen(Byte ssRegimen) {
		this.ssRegimen = ssRegimen;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	public Integer getContracttypeId() {
		return contracttypeId;
	}

	public void setContracttypeId(Integer contracttypeId) {
		this.contracttypeId = contracttypeId;
	}

	public Integer getQuotegroupId() {
		return quotegroupId;
	}

	public void setQuotegroupId(Integer quotegroupId) {
		this.quotegroupId = quotegroupId;
	}

	public Integer getOcupationId() {
		return ocupationId;
	}

	public void setOcupationId(Integer ocupationId) {
		this.ocupationId = ocupationId;
	}
	
	public Integer getRlceId() {
		return rlceId;
	}

	public void setRlceId(Integer rlceId) {
		this.rlceId = rlceId;
	}

	public Integer getJourneytypeId() {
		return journeytypeId;
	}

	public void setJourneytypeId(Integer journeytypeId) {
		this.journeytypeId = journeytypeId;
	}

	public Integer getContractmodelId() {
		return contractmodelId;
	}

	public void setContractmodelId(Integer contractmodelId) {
		this.contractmodelId = contractmodelId;
	}
	
	public Integer getRetaId() {
		return retaId;
	}

	public void setRetaId(Integer retaId) {
		this.retaId = retaId;
	}

	public ContractJourneyDuration getContractJourneyDuration() {
		return contractJourneyDuration;
	}

	public void setContractJourneyDuration(Map<Date, ArrayList<JourneyDuration>> journies) {
		this.contractJourneyDuration.setContractJourneyDuration(journies);
	}
	
	public Date getOldStartDate() {
		return parse(oldStartDate);
	}

	public void setOldStartDate(Date oldStartDate) {
		this.oldStartDate = format(oldStartDate);
	}

	public Date getOldEndDate() {
		return parse(oldEndDate);
	}

	public void setOldEndDate(Date oldEndDate) {
		this.oldEndDate = format(oldEndDate);
	}

	public Boolean hasPayroll() {
		return hasPayroll;
	}

	public void setHasPayroll(Boolean hasPayroll) {
		this.hasPayroll = hasPayroll;
	}

	public Date getPayrollDate() {
		return parse(payrollDate);
	}

	public void setPayrollDate(Date payrollDate) {
		this.payrollDate = format(payrollDate);
	}
	
	public String getAgreementColective() {
		return colectiveAgreement;
	}

	public void setAgreementColective(String colectiveAgreement) {
		this.colectiveAgreement = colectiveAgreement;
	}
	
	public String getEmployeesColective() {
		return colectiveEmployees;
	}

	public void setEmployeesColective(String colectiveEmployees) {
		if(AonStringUtils.isNotBlank(colectiveEmployees) && colectiveEmployees.contains("\""))
			try {
				this.colectiveEmployees = colectiveEmployees.split("\"")[1];
			} catch (IndexOutOfBoundsException e) {
				this.colectiveEmployees = colectiveEmployees;
			}	
		else
			this.colectiveEmployees = colectiveEmployees;
	}

	public Integer getMdctzId() {
		return mdCtzId;
	}

	public void setMdctzId(Integer mdCtzId) {
		this.mdCtzId = mdCtzId;
	}
	
	public String getMdctz() {
		return mdCtz;
	}

	public void setMdctz(String mdCtz) {
		this.mdCtz = mdCtz;
	}

	public Double getPartialityCoef() {
		return partialityCoef;
	}

	public void setPartialityCoef(Double coef) {
		this.partialityCoef = coef;
	}

	public Integer getPartialityCoefId() {
		return partialityCoefId;
	}

	public void setPartialityCoefId(Integer partialityCoefId) {
		this.partialityCoefId = partialityCoefId;
	}

	public void setSalariesCount(Integer salariesCount) {
		this.salariesCount = salariesCount;
	}
	
	public Integer getSalariesCount() {
		return this.salariesCount;
	}
	
	public ArrayList<ContractSalaryInfo> getContractSalariesInfo() {
		return contractSalariesInfo;
	}

	public void setContractSalariesInfo(ArrayList<ContractSalaryInfo> contractSalariesInfo) {
		this.contractSalariesInfo = contractSalariesInfo;
	}
	
	public String getSettleReason() {
		return this.settleReason;
	}
	
	public void setSettleReason(String settleReason) {
		this.settleReason = settleReason;
	}
	
	public boolean hasSettle() {
		return hasSettle;
	}
	
	public void setHasSettle(boolean hasSettle) {
		this.hasSettle = hasSettle;
	}
	
	public void setHolidaysDate(Date holidaysDate) {
		this.holidaysDate = holidaysDate;
	}
	
	public Date getHolidaysDate() {
		return holidaysDate;
	}
	
	public void setSAA(String saa) {
		this.saa = saa;
	}
	
	public String getSAA() {
		return saa;
	}

	public boolean hasCertifica2() {
		return hasCertifica2;
	}

	public void setHasCertifica2(boolean hasCertifica2) {
		this.hasCertifica2 = hasCertifica2;
	}
	
	public boolean isTGSSActive() {
		return this.isTGSSActive;
	}
	
	public void setIsTGSSActive(boolean isTGSSActive) {
		this.isTGSSActive = isTGSSActive;
	}
	
	public String getSepeId() {
		return this.sepeId;
	}

	public void setSepeId(String sepeId) {
		this.sepeId = sepeId;
	}
	
	public boolean isHasExtension() {
		return hasExtension;
	}

	public void setHasExtension(boolean hasExtension) {
		this.hasExtension = hasExtension;
	}
	
	public boolean isHasTransformation() {
		return hasTransformation;
	}

	public void setHasTransformation(boolean hasTransformation) {
		this.hasTransformation = hasTransformation;
	}
	
	public boolean hasCto() {
		return hasCto;
	}

	public void setHasCto(boolean hasCto) {
		this.hasCto = hasCto;
	}
	
	public boolean hasCbc() {
		return hasCbc;
	}

	public void setHasCbc(boolean hasCbc) {
		this.hasCbc = hasCbc;
	}
	
	public boolean isDiscontinuos() {
		return discontinuos;
	}

	public void setDiscontinuos(boolean discontinuos) {
		this.discontinuos = discontinuos;
	}
	
	public boolean isPartial() {
		if(contractType!=null) {
			int type = Integer.parseInt(contractType);
			return AonNumberUtils.between(type, 200, 300) ||  Arrays.asList(309,330,350,389).contains(type) || AonNumberUtils.between(type, 500, 599) || AonNumberUtils.equals(type, 0) ;
		}
		return false;
	}

	@Override
	public String toString() {
		return "ContractInfo [contractId=" + contractId + ", workplaceId=" + workplaceId + ", workplaceName="
				+ workplaceName + ", workplaceZIP=" + workplaceZIP + ", workplaceFullAddress=" + workplaceFullAddress
				+ ", cccId=" + cccId + ", completeCCC=" + completeCCC + ", startDate=" + startDate
				+ ", originalStartDate=" + originalStartDate + ", originalEndDate=" + originalEndDate + ", endDate="
				+ endDate + ", seniorityDate=" + seniorityDate + ", activityId=" + activityId + ", enterpriseCIF="
				+ enterpriseCIF + ", ssRegimen=" + ssRegimen + ", agreementCategory=" + agreementCategory
				+ ", agreementLevelId=" + agreementLevelId + ", agreementId=" + agreementId + ", cccType=" + cccType
				+ ", contracttypeId=" + contracttypeId + ", contractType=" + contractType + ", quotegroupId="
				+ quotegroupId + ", quoteGroup=" + quoteGroup + ", quoteGroupIdxMonthId=" + quoteGroupIdxMonthId
				+ ", quoteGroupIdxMonth=" + quoteGroupIdxMonth + ", ocupationId=" + ocupationId + ", ocupation="
				+ ocupation + ", rlceId=" + rlceId + ", rlce=" + rlce + ", journeytypeId=" + journeytypeId
				+ ", journeyType=" + journeyType + ", contractmodelId=" + contractmodelId + ", contractModel="
				+ contractModel + ", retaId=" + retaId + ", contractJourneyDuration=" + contractJourneyDuration
				+ ", oldStartDate=" + oldStartDate + ", oldEndDate=" + oldEndDate + ", hasPayroll=" + hasPayroll
				+ ", payrollDate=" + payrollDate + ", colectiveAgreement=" + colectiveAgreement
				+ ", colectiveEmployees=" + colectiveEmployees + ", mdCtzId=" + mdCtzId + ", mdCtz=" + mdCtz
				+ ", partialityCoefId=" + partialityCoefId + ", partialityCoef=" + partialityCoef + ", salariesCount="
				+ salariesCount + ", contractSalariesInfo=" + contractSalariesInfo + ", settleReason=" + settleReason
				+ ", hasSettle=" + hasSettle + ", holidaysDate=" + holidaysDate + ", hasCertifica2=" + hasCertifica2
				+ ", isTGSSActive=" + isTGSSActive + ", sepeId=" + sepeId + ", hasExtension=" + hasExtension
				+ ", hasTransformation=" + hasTransformation + ", hasCto=" + hasCto + ", hasCbc=" + hasCbc + "]";
	}

}
