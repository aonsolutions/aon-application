package com.esferalia.aon.file.payroll.fan;

import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.payroll.EnterpriseCCC;

public interface IFanFactory {

	
	
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
			
	public void createEDTCa01Segment(Double cgcTotalEnterprise, Double cgcTotalEmployee, EMP emp);
	public void createEDTCa02Segment(EMP emp);
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
	public void createEDTCa51Segment(EMP emp);
//	public void createEDTCa52Segment(EMP emp);
	public void createEDTCa52Segment(
			Double desmplEnterpriseTotal, Double fogasaEnterpriseTotal, Double fpEnterpriseTotal, 
			Double desmplEmployeeTotal, Double fpEmployeeTotal, EMP emp);
	public void createEDTCa53Segment(EMP emp);
	public void createEDTCa54Segment(EMP emp);
	public void createEDTCa55Segment(EMP emp);
	public void createEDTCa56Segment(EMP emp);
	public void createEDTCa57Segment(EMP emp);
	public void createEDTCa60Segment(EMP emp);
	public void createEDTCa80Segment(Double continuousFormationTotal, EMP emp);
	public void createEDTCa90Segment(EMP emp);
	
	public void createEDTTt10Segment(EMP emp);
	public void createEDTTt20Segment(EMP emp);
	public void createEDTTt30Segment(EMP emp);
	public void createEDTTt9XSegment(EMP emp);
}
