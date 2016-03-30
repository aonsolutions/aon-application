package com.esferalia.aon.file.payroll.fan;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.file.payroll.fan.data.DAT;
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.Salary;

public interface IFanFactory {

	
//	public void createEMPRecord(EnterpriseCCC ccc, EMP emp);
//	
//	// TRABAJADOR
//	
//	public void createDATSegments(EnterpriseCCC ccc, EMP emp);
//	
//	public void createEDLBaSegments(EnterpriseCCC ccc, EMP emp);
//	
//	public void createEDLCdSegments(EnterpriseCCC ccc, EMP emp);
//	
//	
//	
//	// TOTALES
//	
//	public void createEDTBaSegments(EnterpriseCCC ccc, EMP emp);
//	
//	public void createEDTCdSegments(EnterpriseCCC ccc, EMP emp);
//	
//	public void createEDTCaSegments(EnterpriseCCC ccc, EMP emp);
//	
//	public void createEDTTtSegments(EnterpriseCCC ccc, EMP emp);

	public String getQuoteIndicator(List<ITransferObject> salaryDataList, Map<String, String> contractDataMap);
	
	public String getQuoteMode(List<ITransferObject> list);
	
	public Integer getContractDaysOrHours(Salary salary, List<ITransferObject> salaryDataList, Map<String, String> contractDataMap, Integer itDays, Date startDate, Date endDate);
	
	// *********************************************
	// TRABAJADOR
	// *********************************************
	
	public void createEDLBa00Segment(Double commonBase, DAT dat);
	public void createEDLBa01Segment(Double commonBase, DAT dat);
	public void createEDLBa02Segment(Double professionalBase, DAT dat);
	public void createEDLBa05Segment();
	public void createEDLBa06Segment();
	public void createEDLBa07Segment();
	public void createEDLBa08Segment();
	public void createEDLBa09Segment();
	public void createEDLBa10Segment(Double overtimeBase, DAT dat);
	public void createEDLBa11Segment(Double nonEstructuralOvertimeBase, DAT dat);
	public void createEDLBa20Segment(Double base, DAT dat);
	public void createEDLBa21Segment(Double base, DAT dat);
	public void createEDLBa22Segment(Double base, DAT dat);
	public void createEDLBa23Segment();
	public void createEDLBa28Segment();
	public void createEDLBa30Segment();
	public void createEDLBa31Segment();
	public void createEDLBa32Segment();
	public void createEDLBa33Segment();
	public void createEDLBa34Segment();
	public void createEDLBa35Segment();	
	public void createEDLBa36Segment();
	public void createEDLBa37Segment();
	public void createEDLBa38Segment();
	public void createEDLBa41Segment();
	public void createEDLBa42Segment();
	
	
	public void createEDLCd01Segment(Double ecssAmount, DAT dat);
	public void createEDLCd03Segment(Double atepAmount, DAT dat);
	public void createEDLCd05Segment(DAT dat);
	public void createEDLCd06Segment(Double bonusAmount, DAT dat);
	public void createEDLCd07Segment(Double bonusAmount, DAT dat);
	public void createEDLCd10Segment(Integer formationDays, Double bonusAmount, DAT dat);
	public void createEDLCd11Segment(Integer formationDays, Double bonusAmount, DAT dat);
	public void createEDLCd12Segment(Double bonusAmount, DAT dat);
	public void createEDLCd13Segment(Double bonusAmount, DAT dat);
	public void createEDLCd16Segment(DAT dat);
	public void createEDLCd17Segment(Double bonusAmount, DAT dat);
	public void createEDLCd18Segment(DAT dat);
	public void createEDLCd20Segment(Double bonusAmount, DAT dat);
	public void createEDLCd21Segment(DAT dat);
	public void createEDLCd22Segment(Integer bonusDays, Double bonusAmount, DAT dat);
	public void createEDLCd23Segment(Double bonusAmount, DAT dat);
	public void createEDLCd24Segment(DAT dat);
	public void createEDLCd25Segment(Double bonusAmonut, DAT dat);
	public void createEDLCd26Segment(DAT dat);
	public void createEDLCd27Segment(DAT dat);
	public void createEDLCd28Segment(Double bonusAmount, DAT dat);
	public void createEDLCd29Segment(Double cgcTotalEnterprise, Double cgcTotalEmployee, DAT dat, List<ITransferObject> salaryDataList);
	public void createEDLCd30Segment(DAT dat);
	public void createEDLCd31Segment(Double bonusAmount, DAT dat);
	public void createEDLCd34Segment(Integer bonusDays, Double bonusAmount, DAT dat);
	
	
	// *********************************************
	// TOTALES
	// *********************************************
	
	public void createEDTBa01Segment(EMP emp);
	public void createEDTBa02Segment(EMP emp);
	public void createEDTBa05Segment(EMP emp);
	public void createEDTBa06Segment(EMP emp);
	public void createEDTBa07Segment(EMP emp);
	public void createEDTBa08Segment(EMP emp);
	public void createEDTBa09Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTBa10Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTBa11Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTBa21Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTBa22Segment(EMP emp);
	public void createEDTBa23Segment(EMP emp);
	public void createEDTBa28Segment(EMP emp);
	public void createEDTBa30Segment(EMP emp);
	public void createEDTBa31Segment(EMP emp);
	public void createEDTBa32Segment(EMP emp);
	public void createEDTBa33Segment(EMP emp);
	public void createEDTBa34Segment(EMP emp);
	public void createEDTBa35Segment(EMP emp);
	public void createEDTBa36Segment(EMP emp);
	public void createEDTBa37Segment(EMP emp);
	public void createEDTBa38Segment(EMP emp);
	public void createEDTBa41Segment(EMP emp);
	public void createEDTBa42Segment(EMP emp);
			
	public void createEDTCd01Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTCd03Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTCd05Segment(EMP emp);
	public void createEDTCd06Segment(EMP emp);
	public void createEDTCd07Segment(EMP emp);
	public void createEDTCd10Segment(EMP emp);
	public void createEDTCd11Segment(EMP emp);
	public void createEDTCd12Segment(EMP emp);
	public void createEDTCd13Segment(EMP emp);
	public void createEDTCd16Segment(EMP emp);
	public void createEDTCd17Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTCd18Segment(EMP emp);
	public void createEDTCd20Segment(EMP emp);
	public void createEDTCd21Segment(EMP emp);
	public void createEDTCd22Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTCd23Segment(EMP emp);
	public void createEDTCd24Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTCd25Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTCd26Segment(EMP emp);
	public void createEDTCd27Segment(EMP emp);
	public void createEDTCd28Segment(EMP emp);
	public void createEDTCd29Segment(EMP emp);
	public void createEDTCd30Segment(EMP emp);
	public void createEDTCd31Segment(EnterpriseCCC ccc, EMP emp);
	public void createEDTCd34Segment(EnterpriseCCC ccc, EMP emp);
			
	public void createEDTCa01Segment(Double cgcTotalEnterprise, Double cgcTotalEmployee, EMP emp);
	public void createEDTCa02Segment(Double cgcOnlyEnterprise, EMP emp);
	public void createEDTCa03Segment(EMP emp);
	public void createEDTCa11Segment(Double lessThanSevenDaysContractAmount, EMP emp);
	public void createEDTCa12Segment(EMP emp);
	public void createEDTCa20Segment(EMP emp);
	public void createEDTCa21Segment(EMP emp);
	public void createEDTCa22Segment(EMP emp);
	public void createEDTCa31Segment(Double itTotal, EMP emp);
	public void createEDTCa32Segment(Double imsTotal, EMP emp);
	public void createEDTCa30Segment(EMP emp);
//	public void createEDTCa50Segment(Double otherEnterpriseTotal, Double otherEmployeeTotal, EMP emp);
	public void createEDTCa50Segment(
			Double desmplEnterpriseTotal, Double fogasaEnterpriseTotal, Double fpEnterpriseTotal, 
			Double desmplEmployeeTotal, Double fpEmployeeTotal, EMP emp);	
	public void createEDTCa51Segment(Double desmplEnterpriseTotal, EMP emp);	
//	public void createEDTCa52Segment(EMP emp);
	public void createEDTCa52Segment(
			Double desmplEnterpriseTotal, Double fogasaEnterpriseTotal, Double fpEnterpriseTotal, 
			Double desmplEmployeeTotal, Double fpEmployeeTotal, EMP emp);
	public void createEDTCa53Segment(Double fogasaEnterpriseTotal, Double fpEnterpriseTotal, EMP emp);
	public void createEDTCa54Segment(EMP emp);
	public void createEDTCa55Segment(EMP emp);
	public void createEDTCa56Segment(EMP emp);
	public void createEDTCa57Segment(Double desmplOnlyEnterpriseTotal, Double fogasaOnlyEnterpriseTotal,
			Double fpOnlyEnterpriseTotal, EMP emp);
	
	public void createEDTCa60Segment(EMP emp);
	public void createEDTCa80Segment(Double continuousFormationTotal, EMP emp);
	public void createEDTCa90Segment(EMP emp);
	
	public void createEDTTt10Segment(EMP emp);
	public void createEDTTt20Segment(EMP emp);
	public void createEDTTt30Segment(EMP emp);
	public void createEDTTt9XSegment(EMP emp);
}
