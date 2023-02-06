package com.esferalia.aon.gwt.payroll.server;

import java.util.Calendar;

import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public final class EmployeeAFIGeneration {
	
	private static Calendar actualCalendar = Calendar.getInstance();
	private static String year = AonStringUtils.leftPad(actualCalendar.get(Calendar.YEAR)+"", 4, '0');
	private static String month = AonStringUtils.leftPad((actualCalendar.get(Calendar.MONTH)+1)+"", 2, '0');
	private static String day = AonStringUtils.leftPad(actualCalendar.get(Calendar.DAY_OF_MONTH)+"", 2, '0');
	private static String hour = AonStringUtils.leftPad(actualCalendar.get(Calendar.HOUR)+"", 2, '0');
	private static String minute = AonStringUtils.leftPad(actualCalendar.get(Calendar.MINUTE)+"", 2, '0');
	
	protected EmployeeAFIGeneration() {
		super();
	}
	
	public static class ETI{
		
		String etiHeader;
		String sintaxIndent;
		String authKey;
		String payrollProvider;
		String reserved5;
		String fileName; //Length 8
		String sufixAFI;
		String priorityCode;
		String test;
		String registryIdent;
		String reservedTGSS;
		String reserved1;
		
		public ETI(String authKey, String payrollProvider, String fileName, String priorityCode) {
			super();
			this.etiHeader = "ETI";
			this.sintaxIndent = AonStringUtils.rightPad("AFI90W0000", 10, '0');
			this.authKey = AonStringUtils.leftPad(authKey, 8, '0');
			this.payrollProvider = AonStringUtils.leftPad(payrollProvider, 3, '0');
			this.reserved5 = AonStringUtils.leftPad("", 5, '0');
			
			this.fileName = AonStringUtils.isBlank(fileName) ? day + month + hour + minute : fileName;
			
			this.sufixAFI = "AFI";
			this.priorityCode = priorityCode;
			
			this.test = " ";
			
			this.registryIdent = AonStringUtils.leftPad("", 14, '0');
			this.reservedTGSS = AonStringUtils.leftPad("", 1, ' ');
			this.reserved1 = AonStringUtils.leftPad("", 1, ' ');
		}
		
		public String getEtiHeader() {
			return etiHeader;
		}
		public String getSintaxIndent() {
			return sintaxIndent;
		}
		public String getAuthKey() {
			return authKey;
		}
		public String getPayrollProvider() {
			return payrollProvider;
		}
		public String getReserved5() {
			return reserved5;
		}
		public String getYear() {
			return year;
		}
		public String getMonth() {
			return month;
		}
		public String getDay() {
			return day;
		}
		public String getHour() {
			return hour;
		}
		public String getMinute() {
			return minute;
		}
		public String getFileName() {
			return fileName;
		}
		public String getSufixAFI() {
			return sufixAFI;
		}
		public String getPriorityCode() {
			return priorityCode;
		}
		public String getTest() {
			return test;
		}
		public String getRegistryIdent() {
			return registryIdent;
		}
		public String getReservedTGSS() {
			return reservedTGSS;
		}
		public String getReserved1() {
			return reserved1;
		}
		
	}
	
	public static class EMP{
		String empHeader;
		String cccRegime;
		String cccProvince;
		String ccc;
		String identType;
		String country;
		String indet;
		String reserved2;
		String cccRegimePrincipal;
		String cccProvincePrincipal;
		String cccPrincipal;
		String reservedCollection;
		String action;
		String reserved1;
		
		public EMP(String cccRegime, String cccProvince, String ccc, String identType, String country, 
				String ident, String cccRegimePrincipal, String cccProvincePrincipal, String cccPrincipal) {
			
			super();
			this.empHeader = "EMP";
			
			this.cccRegime = AonStringUtils.leftPad(cccRegime, 4, '0');
			this.cccProvince = AonStringUtils.leftPad(cccProvince, 2, '0');
			this.ccc = AonStringUtils.leftPad(ccc, 9, '0');
			
			this.identType = identType;
			this.country = AonStringUtils.leftPad(country, 3, '0');
			this.indet = AonStringUtils.leftPad(ident, 14, '0');
			
			this.reserved2 = StringUtils.leftPad("", 2, ' ');
			
			this.cccRegimePrincipal = AonStringUtils.leftPad(cccRegimePrincipal, 4, '0');
			this.cccProvincePrincipal = AonStringUtils.leftPad(cccProvincePrincipal, 2, '0');
			this.cccPrincipal = AonStringUtils.leftPad(cccPrincipal, 9, '0');
			
			this.reservedCollection = AonStringUtils.leftPad("", 13, ' ');
			this.action = AonStringUtils.leftPad("", 3, ' ');
			this.reserved1 = AonStringUtils.leftPad("", 1, ' ');
		}
		
		public String getEmpHeader() {
			return empHeader;
		}
		public String getCccRegime() {
			return cccRegime;
		}
		public String getCccProvince() {
			return cccProvince;
		}
		public String getCcc() {
			return ccc;
		}
		public String getIdentType() {
			return identType;
		}
		public String getCountry() {
			return country;
		}
		public String getIndet() {
			return indet;
		}
		public String getReserved2() {
			return reserved2;
		}
		public String getCccRegimePrincipal() {
			return cccRegimePrincipal;
		}
		public String getCccProvincePrincipal() {
			return cccProvincePrincipal;
		}
		public String getCccPrincipal() {
			return cccPrincipal;
		}
		public String getReservedCollection() {
			return reservedCollection;
		}
		public String getAction() {
			return action;
		}
		public String getReserved1() {
			return reserved1;
		}
		
	}
	
	public static class RZS{
		String rzsHeader;
		String rzsIndicator;
		String businessmanType;
		String rzsName;
		String authKey;
		String reserved2;
		
		public RZS(String businessmanType, String rzsName) {
			super();
			
			this.rzsHeader = "RZS";
			this.rzsIndicator = "0";
			this.businessmanType = businessmanType;
			
			this.rzsName = AonStringUtils.rightPad(rzsName, 55, ' ');
			this.authKey = AonStringUtils.leftPad("", 8, '0');
			this.reserved2 = AonStringUtils.leftPad("", 2, ' ');
		}

		public String getRzsHeader() {
			return rzsHeader;
		}
		public String getRzsIndicator() {
			return rzsIndicator;
		}
		public String getBusinessmanType() {
			return businessmanType;
		}
		public String getRzsName() {
			return rzsName;
		}
		public String getAuthKey() {
			return authKey;
		}
		public String getReserved2() {
			return reserved2;
		}
		
	}

	public static class TRA{
		String traHeader;
		String numAfilicion;
		String documentType;
		String documentCountry;
		String document;
		String reserved3;
		String decodeControl;
		String nationality;
		String employeeIndic;
		String reserved5;
		
		public TRA(String numAfilicion, String documentType, String documentCountry, String document,
				String nationality) {
			
			super();
			
			this.traHeader = "TRA";
			this.numAfilicion = AonStringUtils.rightPad(numAfilicion, 12, '0');
			
			this.documentType = documentType;
			this.documentCountry = AonStringUtils.leftPad(documentCountry, 3, '0');
			this.document = AonStringUtils.leftPad(document, 14, '0');
			
			this.reserved3 = AonStringUtils.leftPad("", 3, ' ');
			this.decodeControl = AonStringUtils.leftPad("", 25, ' ');
			
			this.nationality = AonStringUtils.leftPad(nationality, 3, '0');
			
			this.employeeIndic = AonStringUtils.leftPad("", 1, ' ');
			this.reserved5 = AonStringUtils.leftPad("", 5, ' ');
		}
		
		public String getTraHeader() {
			return traHeader;
		}
		public String getNumAfilicion() {
			return numAfilicion;
		}
		public String getDocumentType() {
			return documentType;
		}
		public String getDocumentCountry() {
			return documentCountry;
		}
		public String getDocument() {
			return document;
		}
		public String getReserved3() {
			return reserved3;
		}
		public String getDecodeControl() {
			return decodeControl;
		}
		public String getNationality() {
			return nationality;
		}
		public String getEmployeeIndic() {
			return employeeIndic;
		}
		public String getReserved5() {
			return reserved5;
		}
		
	}
	
	public static class AYN{
		String aynHeader;
		String firstSurname;
		String secondSurname;
		String name;
		String reserved12;
		
		public AYN(String firstSurname, String secondSurname, String name) {
			super();
			this.aynHeader = "AYN";
			this.firstSurname = AonStringUtils.rightPad(firstSurname, 20, ' ');
			this.secondSurname = AonStringUtils.rightPad(secondSurname, 20, ' ');
			this.name = AonStringUtils.rightPad(name, 15, ' ');
			this.reserved12 = AonStringUtils.rightPad("", 12, ' ');
		}

		public String getAynHeader() {
			return aynHeader;
		}
		public String getFirstSurname() {
			return firstSurname;
		}
		public String getSecondSurname() {
			return secondSurname;
		}
		public String getName() {
			return name;
		}
		public String getReserved12() {
			return reserved12;
		}
		
	}
	
	public static class MA{
		FAB fab;
		ODL odl;
		OTD otd;
		
		public MA(FAB fab, OTD otd, ODL odl) {
			super();
			this.fab = fab;
			this.otd = otd;
			this.odl = odl;
		}

		public FAB getFab() {
			return fab;
		}
		public void setFab(FAB fab) {
			this.fab = fab;
		}
		public OTD getOtd() {
			return otd;
		}
		public void setOtd(OTD otd) {
			this.otd = otd;
		}
		public ODL getOdl() {
			return odl;
		}
		public void setOdl(ODL odl) {
			this.odl = odl;
		}
		
	}
	
	public static class MB{
		FAB fab;
		DAM dam;
		FCT fct;
		
		public MB(FAB fab, DAM dam) {
			super();
			this.fab = fab;
			this.dam = dam;
		}
		
		public MB(FAB fab, DAM dam, FCT fct) {
			super();
			this.fab = fab;
			this.dam = dam;
			this.fct = fct;
		}

		public FAB getFab() {
			return fab;
		}
		public void setFab(FAB fab) {
			this.fab = fab;
		}
		public DAM getDam() {
			return dam;
		}
		public void setDam(DAM dam) {
			this.dam = dam;
		}
		public FCT getFct() {
			return fct;
		}
		public void setFct(FCT fct) {
			this.fct = fct;
		}
		
	}
	
	public static class MC{
		FAB fab;
		DAM dam;
		
		public MC(FAB fab, DAM dam) {
			super();
			this.fab = fab;
			this.dam = dam;
		}

		public FAB getFab() {
			return fab;
		}
		public void setFab(FAB fab) {
			this.fab = fab;
		}
		public DAM getDam() {
			return dam;
		}
		public void setDam(DAM dam) {
			this.dam = dam;
		}
		
	}
	
	public static class FAB{
		String fabHeader;
		String action;
		String situation;
		String day;
		String month;
		String year;
		String quoteGroup;
		String daylyQG;
		String disability;
		String tc2;
		String reserved1;
		String subWomen;
		String partialityCoef;
		String employeeColective;
		String printInd;
		String reserved15;
		String gender;
		String reserved5;
		String reWomen;
		String disabilityAdmited;
		String freelancer;
		String reserved11;
		String rent;
		String worker;
		
		public FAB(String action, String situation, String day, String month, String year, String quoteGroup, String tc2, String partialityCoef, String employeeColective, String gender ) {
			super();
			this.fabHeader = "FAB";
			this.action = AonStringUtils.rightPad(action, 3, ' ');
			this.situation = AonStringUtils.leftPad(situation, 2, '0');
			
			this.day = AonStringUtils.leftPad(day, 2, '0');
			this.month = AonStringUtils.leftPad(month, 2, '0');
			this.year = AonStringUtils.leftPad(year, 4, '0');
			
			this.quoteGroup = AonStringUtils.leftPad(quoteGroup, 2, '0');
			this.daylyQG = "N";
			this.disability = AonStringUtils.leftPad("", 2, '0');
			this.tc2 = AonStringUtils.leftPad(tc2, 3, '0');
			
			this.reserved1 = StringUtils.leftPad("", 1, ' ');
			this.subWomen = "N";
			this.partialityCoef = AonStringUtils.leftPad(partialityCoef, 3, '0');
			this.employeeColective = AonStringUtils.leftPad(employeeColective, 3, '0');
			this.printInd = " ";

			this.reserved15 = AonStringUtils.leftPad("", 15, '0');
			this.gender = AonStringUtils.leftPad(gender, 1, '1');
			this.reserved5 = AonStringUtils.leftPad("", 5, '0');
			this.reWomen = " ";
			this.disabilityAdmited = "N";
			this.freelancer = " ";
			
			this.reserved11 = AonStringUtils.leftPad("", 11, ' ');
			this.rent = "N";
			this.worker = "N";
		}
		
		public String getFabHeader() {
			return fabHeader;
		}
		public String getAction() {
			return action;
		}
		public String getSituation() {
			return situation;
		}
		public String getDay() {
			return day;
		}
		public String getMonth() {
			return month;
		}
		public String getYear() {
			return year;
		}
		public String getQuoteGroup() {
			return quoteGroup;
		}
		public String getDaylyQG() {
			return daylyQG;
		}
		public String getDisability() {
			return disability;
		}
		public String getTc2() {
			return tc2;
		}
		public String getReserved1() {
			return reserved1;
		}
		public String getSubWomen() {
			return subWomen;
		}
		public String getPartialityCoef() {
			return partialityCoef;
		}
		public String getEmployeeColective() {
			return employeeColective;
		}
		public String getPrintInd() {
			return printInd;
		}
		public String getReserved15() {
			return reserved15;
		}
		public String getGender() {
			return gender;
		}
		public String getReserved5() {
			return reserved5;
		}
		public String getReWomen() {
			return reWomen;
		}
		public String getDisabilityAdmited() {
			return disabilityAdmited;
		}
		public String getFreelancer() {
			return freelancer;
		}
		public String getReserved11() {
			return reserved11;
		}
		public String getRent() {
			return rent;
		}
		public String getWorker() {
			return worker;
		}
		
	}
	
	public static class ODL{
		String odlHeader;
		String convCollective;
		String reserved6;
		String cno;
		String reservedN6;
		String contribution;
		String reserved31;
		
		public ODL( String cno ) {
			this.odlHeader = "ODL";
			this.convCollective = AonStringUtils.leftPad("", 14, '0');
			this.reserved6 = AonStringUtils.rightPad("", 6, ' ');
			this.cno = AonStringUtils.leftPad(cno, 4, '0');;
			this.reservedN6 = AonStringUtils.rightPad("", 6, '0');
			this.contribution = AonStringUtils.rightPad("", 6, '0');
			this.reserved31 = AonStringUtils.rightPad("", 31, ' ');
		}

		public String getOdlHeader() {
			return odlHeader;
		}

		public String getConvCollective() {
			return convCollective;
		}

		public String getReserved6() {
			return reserved6;
		}

		public String getCno() {
			return cno;
		}

		public String getReservedN6() {
			return reservedN6;
		}

		public String getContribution() {
			return contribution;
		}

		public String getReserved31() {
			return reserved31;
		}
		
	}
	
	public static class OTD{
		String otdHeader;
		String convCollective;
		String excedencia;
		String reserved15;
		String reserved8;
		String readmited;
		String endDate;
		String reserved13;
		
		public OTD( String convCollective, String endDate ) {
			this.otdHeader = "OTD";
			this.convCollective = AonStringUtils.leftPad(convCollective, 14, '0');
			this.excedencia = AonStringUtils.rightPad("", 8, '0');
			this.reserved15 = AonStringUtils.rightPad("", 15, '0');
			this.reserved8 = AonStringUtils.rightPad("", 8, '0');
			this.readmited = "N";
			this.endDate = AonStringUtils.rightPad(endDate, 8, '0');
			this.reserved13 = AonStringUtils.rightPad("", 13, ' ');
		}
		
		public String getOtdHeader() {
			return otdHeader;
		}
		public String getConvCollective() {
			return convCollective;
		}
		public String getExcedencia() {
			return excedencia;
		}
		public String getReserved15() {
			return reserved15;
		}
		public String getReserved8() {
			return reserved8;
		}
		public String getReadmited() {
			return readmited;
		}
		public String getEndDate() {
			return endDate;
		}
		public String getReserved13() {
			return reserved13;
		}
		
	}
	
	public static class DAM{
		String damHeader;
		String reserved8;
		String fic;
		String reserved48;
		String ocupation;
		
		public DAM ( String ocupation, String startDate ) {
			this.damHeader = "DAM";
			this.reserved8 = AonStringUtils.leftPad(startDate, 8, '0');
			this.fic = "N";
			this.reserved48 = AonStringUtils.leftPad("", 48, '0');
			this.ocupation = AonStringUtils.leftPad(ocupation, 2, ' ');
		}
		
		public String getDamHeader() {
			return damHeader;
		}
		public String getReserved8() {
			return reserved8;
		}
		public String getFic() {
			return fic;
		}
		public String getReserved48() {
			return reserved48;
		}
		public String getOcupation() {
			return ocupation;
		}
		
	}
	
	public static class FCT{
		String fctHeader;
		String reserved6;
		String holidayDay;
		String holidayMonth;
		String holidayYear;
		String reserved53;
		
		public FCT ( String holidayDay, String holidayMonth, String holidayYear ) {
			this.fctHeader = "FCT";
			this.reserved6 = AonStringUtils.leftPad("", 6, ' ');
			this.holidayDay = AonStringUtils.leftPad(holidayDay, 2, '0');
			this.holidayMonth = AonStringUtils.leftPad(holidayMonth, 2, '0');
			this.holidayYear = AonStringUtils.leftPad(holidayYear, 4, '0');
			this.reserved53 = AonStringUtils.leftPad("", 53, ' ');
		}

		public String getFctHeader() {
			return fctHeader;
		}
		public void setFctHeader(String fctHeader) {
			this.fctHeader = fctHeader;
		}
		public String getReserved6() {
			return reserved6;
		}
		public void setReserved6(String reserved6) {
			this.reserved6 = reserved6;
		}
		public String getHolidayDay() {
			return holidayDay;
		}
		public void setHolidayDay(String holidayDay) {
			this.holidayDay = holidayDay;
		}
		public String getHolidayMonth() {
			return holidayMonth;
		}
		public void setHolidayMonth(String holidayMonth) {
			this.holidayMonth = holidayMonth;
		}
		public String getHolidayYear() {
			return holidayYear;
		}
		public void setHolidayYear(String holidayYear) {
			this.holidayYear = holidayYear;
		}
		public String getReserved53() {
			return reserved53;
		}
		public void setReserved53(String reserved53) {
			this.reserved53 = reserved53;
		}
		
	}
	
	public static class ETF{
		String etfHeader;
		String sintaxIndent;
		String authKey;
		String payrollProvider;
		String reserved5;
		String fileName; //Length 8
		String sufixAFI;
		String priorityCode;
		String test;
		String countEmp;
		String countLines;
		String reserved3;
		
		public ETF(String authKey, String payrollProvider, String fileName, String priorityCode, String countEmployees, String countLines) {
			super();
			this.etfHeader = "ETF";
			this.sintaxIndent = "AFI90W0000";
			this.authKey = StringUtils.leftPad(authKey, 8, '0');
			this.payrollProvider = payrollProvider;
			this.reserved5 = StringUtils.leftPad("", 5, '0');
			
			this.fileName = (null == fileName) ? day + month + hour + minute : fileName;
			
			this.sufixAFI = "AFI";
			this.priorityCode = priorityCode;
			this.test = " ";
			this.countEmp = StringUtils.leftPad(countEmployees, 5, '0');
			this.countLines = StringUtils.leftPad(countLines, 8, '0');
			this.reserved3 = StringUtils.leftPad("", 3, ' ');
		}
		
		public String getEtiHeader() {
			return etfHeader;
		}
		public String getSintaxIndent() {
			return sintaxIndent;
		}
		public String getAuthKey() {
			return authKey;
		}
		public String getPayrollProvider() {
			return payrollProvider;
		}
		public String getReserved5() {
			return reserved5;
		}
		public String getYear() {
			return year;
		}
		public String getMonth() {
			return month;
		}
		public String getDay() {
			return day;
		}
		public String getHour() {
			return hour;
		}
		public String getMinute() {
			return minute;
		}
		public String getFileName() {
			return fileName;
		}
		public String getSufixAFI() {
			return sufixAFI;
		}
		public String getPriorityCode() {
			return priorityCode;
		}
		public String getTest() {
			return test;
		}
		public String getEtfHeader() {
			return etfHeader;
		}
		public String getCountEmp() {
			return countEmp;
		}
		public String getCountLines() {
			return countLines;
		}
		public String getReserved3() {
			return reserved3;
		}
		
	}
	
	// ********************************************************************************************************************************************
	//													GENERATE EMPLOYE AFI
	// ********************************************************************************************************************************************

	public static String generateEmployeeAFI (JSONObject employeeData) {
		//RESULT
		String employeeAFI = null;
		
		//ETI
		JSONObject etiJson = (JSONObject) employeeData.get("ETI");
		ETI eti = new ETI(
				etiJson.get("authkey").toString(), 
				etiJson.get("payrollProvider").toString(), 
				null == etiJson.get("fileName") ? null : etiJson.get("fileName").toString(), 
				etiJson.get("prorityCode").toString());
				
		//EMP
		JSONObject empJson = (JSONObject) employeeData.get("EMP");
		EMP emp = new EMP(
				empJson.get("cccRegime").toString(),
				empJson.get("cccProvince").toString(),
				empJson.get("ccc").toString(),
				empJson.get("identType").toString(),
				empJson.get("country").toString(),
				empJson.get("ident").toString(),
				empJson.get("cccRegimePrincipal").toString(),
				empJson.get("cccProvincePrincipal").toString(),
				empJson.get("cccPrincipal").toString());
		
		//RZS
		JSONObject rzsJson = (JSONObject) employeeData.get("RZS");
		RZS rzs = new RZS(
				rzsJson.get("businessmanType").toString(), 
				rzsJson.get("rzsName").toString());
		
		//TRA
		JSONObject traJson = (JSONObject) employeeData.get("TRA");
		TRA tra = new TRA(
				traJson.get("numAfiliacion").toString(),
				traJson.get("documentType").toString(), 
				traJson.get("documentCountry").toString(),
				traJson.get("document").toString(),
				traJson.get("nationality").toString());
		
		//AYN
		JSONObject aynJson = (JSONObject) employeeData.get("AYN");
		AYN ayn = new AYN(
				aynJson.get("firstSurname").toString(), 
				aynJson.get("secondSurname").toString(), 
				aynJson.get("name").toString());
		
		//SDC
		JSONObject sdcJson = (JSONObject) employeeData.get("MA");
		MA ma = null;
		if(null != sdcJson) {
			JSONObject fabJson = (JSONObject) sdcJson.get("FAB");
			FAB fab = new FAB(
					fabJson.get("action").toString(), 
					fabJson.get("situation").toString(),
					fabJson.get("day").toString(),
					fabJson.get("month").toString(),
					fabJson.get("year").toString(),
					fabJson.get("quoteGroup").toString(),
					fabJson.get("tc2").toString(),
					fabJson.get("partialityCoef") == null ? "000" : fabJson.get("partialityCoef").toString(),
					fabJson.get("employeeColective") == null ? "" : fabJson.get("employeeColective").toString(),
					fabJson.get("gender").toString());
			
			JSONObject odlJson = (JSONObject) sdcJson.get("ODL");
			ODL odl = new ODL(
					odlJson.get("cno") == null ? "" : odlJson.get("cno").toString());
			
			JSONObject otdJson = (JSONObject) sdcJson.get("OTD");
			OTD otd = new OTD(
					otdJson.get("convCollective").toString(),
					otdJson.get("endDate") == null ? "" : otdJson.get("endDate").toString());
			
			ma = new MA(fab, otd, odl);
		}
		
		//EDC
		JSONObject edcJson = (JSONObject) employeeData.get("MB");
		MB mb = null;
		if(null != edcJson) {
			JSONObject fabJson = (JSONObject) edcJson.get("FAB");
			FAB fab = new FAB(
					fabJson.get("action").toString(), 
					fabJson.get("situation").toString(),
					fabJson.get("day").toString(),
					fabJson.get("month").toString(),
					fabJson.get("year").toString(),
					fabJson.get("quoteGroup").toString(),
					fabJson.get("tc2").toString(),
					fabJson.get("partialityCoef").toString(),
					fabJson.get("employeeColective") == null ? "" : fabJson.get("employeeColective").toString(),
					fabJson.get("gender").toString());
			
			DAM dam = new DAM("", ""); // 	JSONObject damJson = (JSONObject) edcJson.get("DAM");
			
			JSONObject fctJson = (JSONObject) edcJson.get("FCT");
			if(null != fctJson) {
				FCT fct = new FCT(
						fctJson.get("dayHoliday").toString(),
						fctJson.get("monthHoliday").toString(),
						fctJson.get("yearHoliday").toString());
				
				mb = new MB(fab, dam, fct);
			} else
				mb = new MB(fab, dam);
		}
		
		//CHC
		JSONObject chcJson = (JSONObject) employeeData.get("MC");
		MC mc = null;
		if(null != chcJson) {
			JSONObject fabJson = (JSONObject) chcJson.get("FAB");
			FAB fab = new FAB(
					fabJson.get("action").toString(), 
					fabJson.get("situation").toString(),
					fabJson.get("day").toString(),
					fabJson.get("month").toString(),
					fabJson.get("year").toString(),
					fabJson.get("quoteGroup").toString(),
					fabJson.get("tc2").toString(),
					fabJson.get("partialityCoef") == null ? "000" : fabJson.get("partialityCoef").toString(),
					fabJson.get("employeeColective") == null ? "" : fabJson.get("employeeColective").toString(),
					fabJson.get("gender").toString());
			
			JSONObject damJson = (JSONObject) chcJson.get("DAM");
			DAM dam = new DAM(damJson.get("ocupation") == null ? "" : damJson.get("ocupation").toString(), damJson.get("startDate").toString());
			
			mc = new MC(fab, dam);
		}
	
		//CONF
		JSONObject confJson = (JSONObject) employeeData.get("CONF");
		Integer staticLines = Integer.parseInt(confJson.get("staticLines").toString());
		Integer employeeLines = Integer.parseInt(confJson.get("employeeLines").toString());
		Integer numEmployees = Integer.parseInt(confJson.get("numEmployees").toString());
		String settleHolidaysLine = confJson.get("settleHolidaysLine").toString();
		Integer holidaysLine = AonStringUtils.isBlank(settleHolidaysLine) ? 0 : Integer.parseInt(settleHolidaysLine);
		Integer totalLines = numEmployees * employeeLines + staticLines + holidaysLine;
				
		//ETF
		JSONObject etfJson = (JSONObject) employeeData.get("ETF");
		ETF etf = new ETF(
				etfJson.get("authkey").toString(), 
				etfJson.get("payrollProvider").toString(), 
				null == etfJson.get("fileName") ? null : etfJson.get("fileName").toString(), 
				etfJson.get("priorityCode").toString(),
				numEmployees+"",
				totalLines+"");
		
		employeeAFI = createEmployeeAFI(eti, emp, rzs, tra, ayn, ma, mb, mc, etf);
		
		return employeeAFI;
		
	}
	
	private static String createEmployeeAFI(ETI eti, EMP emp, RZS rzs, TRA tra, AYN ayn, MA ma, MB mb, MC mc, ETF etf) {
		
		String employeeAFI = "";
		
		employeeAFI +=
				eti.getEtiHeader() +
				eti.getSintaxIndent() +
				eti.getAuthKey() +
				eti.getPayrollProvider() +
				eti.getReserved5() +
				eti.getYear() +
				eti.getMonth() +
				eti.getDay() +
				eti.getHour() +
				eti.getMinute() +
				eti.getFileName() +
				eti.getSufixAFI() +
				eti.getPriorityCode() +
				eti.getTest() +
				eti.getRegistryIdent() +
				eti.getReservedTGSS() +
				eti.getReserved1() +
				"\r\n";
		
		employeeAFI +=
				emp.getEmpHeader() +
				emp.getCccRegime() +
				emp.getCccProvince() +
				emp.getCcc() +
				emp.getIdentType() +
				emp.getCountry() +
				emp.getIndet() +
				emp.getReserved2() +
				emp.getCccRegimePrincipal() +
				emp.getCccProvincePrincipal() +
				emp.getCccPrincipal() +
				emp.getReservedCollection() +
				emp.getAction() +
				emp.getReserved1() +
				"\r\n";
		
		employeeAFI +=
				rzs.getRzsHeader() +
				rzs.getRzsIndicator() +
				rzs.getBusinessmanType() +
				rzs.getRzsName() +
				rzs.getAuthKey() +
				rzs.getReserved2() +
				"\r\n";
		
		if(null != ma) {
			employeeAFI +=
					tra.getTraHeader() +
					tra.getNumAfilicion() +
					tra.getDocumentType() +
					tra.getDocumentCountry() +
					tra.getDocument() +
					tra.getReserved3() +
					tra.getDecodeControl() +
					tra.getNationality() +
					tra.getEmployeeIndic() +
					tra.getReserved5() +
					"\r\n";
			
			employeeAFI +=
					ayn.getAynHeader() +
					ayn.getFirstSurname() +
					ayn.getSecondSurname() +
					ayn.getName() +
					ayn.getReserved12() +
					"\r\n";
			
			employeeAFI +=
					ma.getFab().getFabHeader() +
					ma.getFab().getAction() +
					ma.getFab().getSituation() +
					ma.getFab().getYear() +
					ma.getFab().getMonth() +
					ma.getFab().getDay() +
					ma.getFab().getQuoteGroup() +
					ma.getFab().getDaylyQG() +
					ma.getFab().getDisability() +
					ma.getFab().getTc2() +
					ma.getFab().getReserved1() +
					ma.getFab().getSubWomen() +
					ma.getFab().getPartialityCoef() +
					ma.getFab().getEmployeeColective() +
					ma.getFab().getPrintInd() +
					ma.getFab().getReserved15() +
					ma.getFab().getGender() +
					ma.getFab().getReserved5() +
					ma.getFab().getReWomen() +
					ma.getFab().getDisabilityAdmited() + 
					ma.getFab().getFreelancer() +
					ma.getFab().getReserved11() +
					ma.getFab().getRent() +
					ma.getFab().getWorker() + 
					"\r\n";
			
			employeeAFI +=
					ma.getOdl().getOdlHeader() +
					ma.getOdl().getConvCollective() +
					ma.getOdl().getReserved6() +
					ma.getOdl().getCno() +
					ma.getOdl().getReservedN6() +
					ma.getOdl().getContribution() +
					ma.getOdl().getReserved31() +
					"\r\n";
			
			employeeAFI +=
					ma.getOtd().getOtdHeader() +
					ma.getOtd().getConvCollective() +
					ma.getOtd().getExcedencia() +
					ma.getOtd().getReserved15() +
					ma.getOtd().getReserved8() +
					ma.getOtd().getReadmited() +
					ma.getOtd().getEndDate() +
					ma.getOtd().getReserved13() +
					"\r\n";
		}
			
		if(null != mb) {
			employeeAFI +=
					tra.getTraHeader() +
					tra.getNumAfilicion() +
					tra.getDocumentType() +
					tra.getDocumentCountry() +
					tra.getDocument() +
					tra.getReserved3() +
					tra.getDecodeControl() +
					tra.getNationality() +
					tra.getEmployeeIndic() +
					tra.getReserved5() +
					"\r\n";
			
			employeeAFI +=
					ayn.getAynHeader() +
					ayn.getFirstSurname() +
					ayn.getSecondSurname() +
					ayn.getName() +
					ayn.getReserved12() +
					"\r\n";
			
			employeeAFI +=
					mb.getFab().getFabHeader() +
					mb.getFab().getAction() +
					mb.getFab().getSituation() +
					mb.getFab().getYear() +
					mb.getFab().getMonth() +
					mb.getFab().getDay() +
					mb.getFab().getQuoteGroup() +
					mb.getFab().getDaylyQG() +
					mb.getFab().getDisability() +
					mb.getFab().getTc2() +
					mb.getFab().getReserved1() +
					mb.getFab().getSubWomen() +
					mb.getFab().getPartialityCoef() +
					mb.getFab().getEmployeeColective() +
					mb.getFab().getPrintInd() +
					mb.getFab().getReserved15() +
					mb.getFab().getGender() +
					mb.getFab().getReserved5() +
					mb.getFab().getReWomen() +
					mb.getFab().getDisabilityAdmited() + 
					mb.getFab().getFreelancer() +
					mb.getFab().getReserved11() +
					mb.getFab().getRent() +
					mb.getFab().getWorker() + 
					"\r\n";
			
			employeeAFI +=
					mb.getDam().getDamHeader() +
					mb.getDam().getReserved8() +
					mb.getDam().getFic() +
					mb.getDam().getReserved48() +
					mb.getDam().getOcupation() +
					mb.getDam().getReserved8() +
					"\r\n";
			
			if(null != mb.getFct())
				employeeAFI +=
						mb.getFct().getFctHeader() +
						mb.getFct().getReserved6() +
						mb.getFct().getHolidayDay() +
						mb.getFct().getHolidayMonth() +
						mb.getFct().getHolidayYear() +
						mb.getFct().getReserved53() +
						"\r\n";

		}
			
		if(null != mc) {
			employeeAFI +=
					tra.getTraHeader() +
					tra.getNumAfilicion() +
					tra.getDocumentType() +
					tra.getDocumentCountry() +
					tra.getDocument() +
					tra.getReserved3() +
					tra.getDecodeControl() +
					tra.getNationality() +
					tra.getEmployeeIndic() +
					tra.getReserved5() +
					"\r\n";
			
			employeeAFI +=
					ayn.getAynHeader() +
					ayn.getFirstSurname() +
					ayn.getSecondSurname() +
					ayn.getName() +
					ayn.getReserved12() +
					"\r\n";
			
			employeeAFI +=
					mc.getFab().getFabHeader() +
					mc.getFab().getAction() +
					mc.getFab().getSituation() +
					mc.getFab().getYear() +
					mc.getFab().getMonth() +
					mc.getFab().getDay() +
					mc.getFab().getQuoteGroup() +
					mc.getFab().getDaylyQG() +
					mc.getFab().getDisability() +
					mc.getFab().getTc2() +
					mc.getFab().getReserved1() +
					mc.getFab().getSubWomen() +
					mc.getFab().getPartialityCoef() +
					mc.getFab().getEmployeeColective() +
					mc.getFab().getPrintInd() +
					mc.getFab().getReserved15() +
					mc.getFab().getGender() +
					mc.getFab().getReserved5() +
					mc.getFab().getReWomen() +
					mc.getFab().getDisabilityAdmited() + 
					mc.getFab().getFreelancer() +
					mc.getFab().getReserved11() +
					mc.getFab().getRent() +
					mc.getFab().getWorker() + 
					"\r\n";
			
			employeeAFI +=
					mc.getDam().getDamHeader() +
					mc.getDam().getReserved8() +
					mc.getDam().getFic() +
					mc.getDam().getReserved48() +
					mc.getDam().getOcupation() +
					mc.getDam().getReserved8() +
					"\r\n";

		}
		
		employeeAFI +=
				etf.getEtfHeader() +
				etf.getSintaxIndent() +
				etf.getAuthKey() +
				etf.getPayrollProvider() +
				etf.getReserved5() +
				etf.getYear() +
				etf.getMonth() +
				etf.getDay() +
				etf.getHour() +
				etf.getMinute() +
				etf.getFileName() +
				etf.getSufixAFI() +
				etf.getPriorityCode() +
				etf.getTest() +
				etf.getCountEmp() +
				etf.getCountLines() +
				etf.getReserved3() +
				"\r\n";
		
		return employeeAFI;
	}
	
}
