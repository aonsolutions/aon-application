package com.esferalia.aon.gwt.payroll.server;

import java.util.Date;

import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.payroll.shared.StringUtils;

public final class EmployeeAFIGeneration {
	public static class ETI{
		String etiHeader;
		String sintaxIndent;
		String authKey;
		String payrollProvider;
		String reserved5;
		String year;
		String month;
		String day;
		String hour;
		String minute;
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
			this.sintaxIndent = StringUtils.rightPad("AFI90", 10, ' ');
			this.authKey = StringUtils.leftPad(authKey, 8, '0');
			this.payrollProvider = payrollProvider;
			this.reserved5 = StringUtils.leftPad("", 5, ' ');
			Date actualDate = new Date();
			this.year = (actualDate.getYear() + 1900) + "";
			this.month = StringUtils.leftPad((actualDate.getMonth()+1)+"", 2, '0');
			this.day = StringUtils.leftPad(actualDate.getDate()+"", 2, '0');
			this.hour = StringUtils.leftPad(actualDate.getHours()+"", 2, '0');
			this.minute = StringUtils.leftPad(actualDate.getMinutes()+"", 2, '0');
			this.fileName = (null == fileName) ? this.day + this.month + this.hour + this.minute : fileName;
			this.sufixAFI = "AFI";
			this.priorityCode = priorityCode;
			this.test = " ";
			this.registryIdent = StringUtils.leftPad("", 14, '0');
			this.reservedTGSS = StringUtils.leftPad("", 1, ' ');
			this.reserved1 = StringUtils.leftPad("", 1, ' ');
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
			this.cccRegime = cccRegime;
			this.cccProvince = cccProvince;
			this.ccc = ccc;
			this.identType = identType;
			this.country = country;
			this.indet = StringUtils.leftPad(ident, 14, '0');
			this.reserved2 = StringUtils.leftPad("", 2, ' ');
			this.cccRegimePrincipal = cccRegimePrincipal;
			this.cccProvincePrincipal = cccProvincePrincipal;
			this.cccPrincipal = cccPrincipal;
			this.reservedCollection = StringUtils.leftPad("", 13, ' ');
			this.action = StringUtils.leftPad("", 3, ' ');
			this.reserved1 = StringUtils.leftPad("", 1, ' ');
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
			this.rzsName = StringUtils.rightPad(rzsName, 55, ' ');
			this.authKey = StringUtils.rightPad("", 8, '0');
			this.reserved2 = StringUtils.rightPad("", 2, ' ');
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
			this.numAfilicion = numAfilicion;
			this.documentType = documentType;
			this.documentCountry = StringUtils.leftPad(documentCountry, 3, ' ');
			this.document = StringUtils.leftPad(document, 14, '0');
			this.reserved3 = StringUtils.leftPad("", 3, ' ');
			this.decodeControl = StringUtils.leftPad("", 25, ' ');
			this.nationality = nationality;
			this.employeeIndic = StringUtils.leftPad("", 1, ' ');
			this.reserved5 = StringUtils.leftPad("", 5, ' ');
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
			this.firstSurname = StringUtils.rightPad(firstSurname, 20, ' ');
			this.secondSurname = StringUtils.rightPad(secondSurname, 20, ' ');
			this.name = StringUtils.rightPad(name, 15, ' ');
			this.reserved12 = StringUtils.rightPad("", 12, ' ');
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
	
	public static class SDC{
		FAB fab;
		OTD otd;
		
		public SDC(FAB fab, OTD otd) {
			super();
			this.fab = fab;
			this.otd = otd;
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
		
	}
	
	public static class EDC{
		FAB fab;
		DAM dam;
		
		public EDC(FAB fab, DAM dam) {
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
	
	public static class CHC{
		FAB fab;
		DAM dam;
		
		public CHC(FAB fab, DAM dam) {
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
	
	public static class QGC{
		FAB fab;
		DAM dam;
		
		public QGC(FAB fab, DAM dam) {
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
	
	public static class OCC{
		FAB fab;
		DAM dam;
		
		public OCC(FAB fab, DAM dam) {
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
		String reserved6;
		String printInd;
		String reserved15;
		String gender;
		String reserved5;
		String reWomen;
		String disabilityAdmited;
		String freelancer;
		String reserved2;
		String actualDay;
		String actualMonth;
		String actualYear;
		String reserved2N;
		
		public FAB(String action, String situation, String day, String month, String year, String quoteGroup, String tc2, String gender ) {
			super();
			this.fabHeader = "FAB";
			this.action = StringUtils.rightPad(action, 3, ' ');
			this.situation = StringUtils.leftPad(situation, 2, '0');
			this.day = StringUtils.leftPad(day, 2, '0');
			this.month = StringUtils.leftPad(month, 2, '0');
			this.year = StringUtils.leftPad(year, 2, '0');
			this.quoteGroup = quoteGroup;
			this.daylyQG = "N";
			this.disability = StringUtils.leftPad("", 2, '0');
			this.tc2 = tc2;
			this.reserved1 = StringUtils.leftPad("", 1, ' ');
			this.subWomen = "N";
			this.reserved6 = StringUtils.leftPad("", 6, '0');
			this.printInd = "C";
			this.reserved15 = StringUtils.leftPad("", 15, '0');
			this.gender = gender;
			this.reserved5 = StringUtils.leftPad("", 5, '0');
			this.reWomen = " ";
			this.disabilityAdmited = "N";
			this.freelancer = "N";
			this.reserved2 = StringUtils.leftPad("", 2, ' ');
			Date actualDate = new Date();
			this.actualYear = (actualDate.getYear() + 1900) + "";
			this.actualMonth = StringUtils.leftPad((actualDate.getMonth()+1)+"", 2, '0');
			this.actualDay = StringUtils.leftPad(actualDate.getDate()+"", 2, '0');
			this.reserved2N = StringUtils.leftPad("", 2, 'N');
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
		public String getReserved6() {
			return reserved6;
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
		public String getReserved2() {
			return reserved2;
		}
		public String getActualDay() {
			return actualDay;
		}
		public String getActualMonth() {
			return actualMonth;
		}
		public String getActualYear() {
			return actualYear;
		}
		public String getReserved2N() {
			return reserved2N;
		}
		
		
	}
	
	public static class OTD{
		String otdHeader;
		String convCollective;
		String reserved31;
		String reAdmited;
		String reserved8;
		String reserved13;
		
		public OTD( String convCollective ) {
			this.otdHeader = "OTD";
			this.convCollective = convCollective;
			this.reserved31 = StringUtils.rightPad("", 31, '0');
			this.reAdmited = " ";
			this.reserved8 = StringUtils.rightPad("", 8, '0');
			this.reserved13 = StringUtils.rightPad("", 13, ' ');
		}
		
		public String getOtdHeader() {
			return otdHeader;
		}
		public String getConvCollective() {
			return convCollective;
		}
		public String getReserved31() {
			return reserved31;
		}
		public String getReAdmited() {
			return reAdmited;
		}
		public String getReserved8() {
			return reserved8;
		}
		public String getReserved13() {
			return reserved13;
		}
		
	}
	
	public static class DAM{
		String damHeader;
		String reserved8;
		String fic;
		String reserved21;
		String reserved6;
		String reserved2;
		String reserved1;
		String reserved7;
		String reservedW2;
		String reserved4;
		String family;
		String ocupation;
		
		public DAM ( String ocupation ) {
			this.damHeader = "DAM";
			this.reserved8 = StringUtils.leftPad("", 8, '0');
			this.fic = "N";
			this.reserved21 = StringUtils.leftPad("", 21, '0');
			this.reserved6 = StringUtils.leftPad("", 6, ' ');
			this.reserved2 = StringUtils.leftPad("", 2, '0');
			this.reserved1 = StringUtils.leftPad("", 1, ' ');
			this.reserved7 = StringUtils.leftPad("", 7, '0');
			this.reservedW2 = StringUtils.leftPad("", 2, ' ');
			this.reserved4 = StringUtils.leftPad("", 4, '0');
			this.family = " ";
			this.ocupation = StringUtils.leftPad(ocupation, 2, ' ');
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
		public String getReserved21() {
			return reserved21;
		}
		public String getReserved6() {
			return reserved6;
		}
		public String getReserved2() {
			return reserved2;
		}
		public String getReserved1() {
			return reserved1;
		}
		public String getReserved7() {
			return reserved7;
		}
		public String getReservedW2() {
			return reservedW2;
		}
		public String getReserved4() {
			return reserved4;
		}
		public String getFamily() {
			return family;
		}
		public String getOcupation() {
			return ocupation;
		}
		
	}
	
	public static class ETF{
		String etfHeader;
		String sintaxIndent;
		String authKey;
		String payrollProvider;
		String reserved5;
		String year;
		String month;
		String day;
		String hour;
		String minute;
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
			this.sintaxIndent = "AFI90WS881";
			this.authKey = StringUtils.leftPad(authKey, 8, '0');
			this.payrollProvider = payrollProvider;
			this.reserved5 = StringUtils.leftPad("", 5, '9');
			Date actualDate = new Date();
			this.year = actualDate.getYear() + 1900 + "";
			this.month = StringUtils.leftPad(actualDate.getMonth()+1+"", 2, '0');
			this.day = StringUtils.leftPad(actualDate.getDate()+"", 2, '0');
			this.hour = StringUtils.leftPad(actualDate.getHours()+"", 2, '0');
			this.minute = StringUtils.leftPad(actualDate.getMinutes()+"", 2, '0');
			this.fileName = (null == fileName) ? this.day + this.month + this.hour + this.minute : fileName;
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
		JSONObject sdcJson = (JSONObject) employeeData.get("SDC");
		SDC sdc = null;
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
					fabJson.get("gender").toString());
			
			JSONObject otdJson = (JSONObject) sdcJson.get("OTD");
			OTD otd = new OTD(
					otdJson.get("convCollective").toString());
			
			sdc = new SDC(fab, otd);
		}
		
		//EDC
		JSONObject edcJson = (JSONObject) employeeData.get("EDC");
		EDC edc = null;
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
					fabJson.get("gender").toString());
			
			JSONObject damJson = (JSONObject) edcJson.get("DAM");
			DAM dam = new DAM("");
			
			edc = new EDC(fab, dam);
		}
		
		//CHC
		JSONObject chcJson = (JSONObject) employeeData.get("CHC");
		CHC chc = null;
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
					fabJson.get("gender").toString());
			
			JSONObject damJson = (JSONObject) chcJson.get("DAM");
			DAM dam = new DAM("");
			
			chc = new CHC(fab, dam);
		}
		
		//QGC
		JSONObject qgcJson = (JSONObject) employeeData.get("QGC");
		QGC qgc = null;
		if(null != qgcJson) {
			JSONObject fabJson = (JSONObject) qgcJson.get("FAB");
			FAB fab = new FAB(
					fabJson.get("action").toString(), 
					fabJson.get("situation").toString(),
					fabJson.get("day").toString(),
					fabJson.get("month").toString(),
					fabJson.get("year").toString(),
					fabJson.get("quoteGroup").toString(),
					fabJson.get("tc2").toString(),
					fabJson.get("gender").toString());
			
			JSONObject damJson = (JSONObject) qgcJson.get("DAM");
			DAM dam = new DAM("");
			
			qgc = new QGC(fab, dam);
		}

		//QGC
		JSONObject occJson = (JSONObject) employeeData.get("OCC");
		OCC occ = null;
		if(null != occJson) {
			JSONObject fabJson = (JSONObject) occJson.get("FAB");
			FAB fab = new FAB(
					fabJson.get("action").toString(), 
					fabJson.get("situation").toString(),
					fabJson.get("day").toString(),
					fabJson.get("month").toString(),
					fabJson.get("year").toString(),
					fabJson.get("quoteGroup").toString(),
					fabJson.get("tc2").toString(),
					fabJson.get("gender").toString());
			
			JSONObject damJson = (JSONObject) occJson.get("DAM");
			DAM dam = new DAM(
					damJson.get("ocupation").toString());
			
			occ = new OCC(fab, dam);
		}
	
		//CONF
		JSONObject confJson = (JSONObject) employeeData.get("CONF");
		Integer staticLines = Integer.parseInt(confJson.get("staticLines").toString());
		Integer employeeLines = Integer.parseInt(confJson.get("employeeLines").toString());
		Integer numEmployees = Integer.parseInt(confJson.get("numEmployees").toString());
		Integer totalLines = numEmployees * employeeLines + staticLines;
				
		//ETF
		JSONObject etfJson = (JSONObject) employeeData.get("ETF");
		ETF etf = new ETF(
				etfJson.get("authkey").toString(), 
				etfJson.get("payrollProvider").toString(), 
				null == etfJson.get("fileName") ? null : etfJson.get("fileName").toString(), 
				etfJson.get("priorityCode").toString(),
				numEmployees+"",
				totalLines+"");
		
		employeeAFI = createEmployeeAFI(eti, emp, rzs, tra, ayn, sdc, edc, chc, qgc, occ, etf);
		
		return employeeAFI;
		
	}
	
	private static String createEmployeeAFI(ETI eti, EMP emp, RZS rzs, TRA tra, AYN ayn, SDC sdc, EDC edc, CHC chc,
			QGC qgc, OCC occ, ETF etf) {
		
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
		
		if(null != sdc) {
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
					sdc.getFab().getFabHeader() +
					sdc.getFab().getAction() +
					sdc.getFab().getSituation() +
					sdc.getFab().getYear() +
					sdc.getFab().getMonth() +
					sdc.getFab().getDay() +
					sdc.getFab().getQuoteGroup() +
					sdc.getFab().getDaylyQG() +
					sdc.getFab().getDisability() +
					sdc.getFab().getTc2() +
					sdc.getFab().getReserved1() +
					sdc.getFab().getSubWomen() +
					sdc.getFab().getReserved6() +
					sdc.getFab().getPrintInd() +
					sdc.getFab().getReserved15() +
					sdc.getFab().getGender() +
					sdc.getFab().getReserved5() +
					sdc.getFab().getReWomen() +
					sdc.getFab().getReserved2N() +
					sdc.getFab().getReserved2() +
					sdc.getFab().getActualYear() +
					sdc.getFab().getActualMonth() +
					sdc.getFab().getActualDay() +
					sdc.getFab().getReserved1() +
					sdc.getFab().getReserved2N() +
					"\r\n";
			
			employeeAFI +=
					sdc.getOtd().getOtdHeader() +
					sdc.getOtd().getConvCollective() +
					sdc.getOtd().getReserved31() +
					sdc.getOtd().getReAdmited() +
					sdc.getOtd().getReserved8() +
					sdc.getOtd().getReserved13() +
					"\r\n";
		}
			
		if(null != edc) {
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
					edc.getFab().getFabHeader() +
					edc.getFab().getAction() +
					edc.getFab().getSituation() +
					edc.getFab().getYear() +
					edc.getFab().getMonth() +
					edc.getFab().getDay() +
					edc.getFab().getQuoteGroup() +
					edc.getFab().getDaylyQG() +
					edc.getFab().getDisability() +
					edc.getFab().getTc2() +
					edc.getFab().getReserved1() +
					edc.getFab().getSubWomen() +
					edc.getFab().getReserved6() +
					edc.getFab().getPrintInd() +
					edc.getFab().getReserved15() +
					edc.getFab().getGender() +
					edc.getFab().getReserved5() +
					edc.getFab().getReWomen() +
					edc.getFab().getReserved2N() +
					edc.getFab().getReserved2() +
					edc.getFab().getActualYear() +
					edc.getFab().getActualMonth() +
					edc.getFab().getActualDay() +
					edc.getFab().getReserved1() +
					edc.getFab().getReserved2N() +
					"\r\n";
			
			employeeAFI +=
					edc.getDam().getDamHeader() +
					edc.getDam().getReserved8() +
					edc.getDam().getFic() +
					edc.getDam().getReserved21() +
					edc.getDam().getReserved6() +
					edc.getDam().getReserved2() +
					edc.getDam().getReserved1() +
					edc.getDam().getReserved7() +
					edc.getDam().getReservedW2() +
					edc.getDam().getReserved4() +
					edc.getDam().getFamily() +
					edc.getDam().getReserved4() +
					edc.getDam().getOcupation() +
					edc.getDam().getReserved8() +
					"\r\n";

		}
			
		if(null != chc) {
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
					chc.getFab().getFabHeader() +
					chc.getFab().getAction() +
					chc.getFab().getSituation() +
					chc.getFab().getYear() +
					chc.getFab().getMonth() +
					chc.getFab().getDay() +
					chc.getFab().getQuoteGroup() +
					chc.getFab().getDaylyQG() +
					chc.getFab().getDisability() +
					chc.getFab().getTc2() +
					chc.getFab().getReserved1() +
					chc.getFab().getSubWomen() +
					chc.getFab().getReserved6() +
					chc.getFab().getPrintInd() +
					chc.getFab().getReserved15() +
					chc.getFab().getGender() +
					chc.getFab().getReserved5() +
					chc.getFab().getReWomen() +
					chc.getFab().getReserved2N() +
					chc.getFab().getReserved2() +
					chc.getFab().getActualYear() +
					chc.getFab().getActualMonth() +
					chc.getFab().getActualDay() +
					chc.getFab().getReserved1() +
					chc.getFab().getReserved2N() +
					"\r\n";
			
			employeeAFI +=
					chc.getDam().getDamHeader() +
					chc.getDam().getReserved8() +
					chc.getDam().getFic() +
					chc.getDam().getReserved21() +
					chc.getDam().getReserved6() +
					chc.getDam().getReserved2() +
					chc.getDam().getReserved1() +
					chc.getDam().getReserved7() +
					chc.getDam().getReservedW2() +
					chc.getDam().getReserved4() +
					chc.getDam().getFamily() +
					chc.getDam().getReserved4() +
					chc.getDam().getOcupation() +
					chc.getDam().getReserved8() +
					"\r\n";

		}
		
		if(null != qgc) {
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
					qgc.getFab().getFabHeader() +
					qgc.getFab().getAction() +
					qgc.getFab().getSituation() +
					qgc.getFab().getYear() +
					qgc.getFab().getMonth() +
					qgc.getFab().getDay() +
					qgc.getFab().getQuoteGroup() +
					qgc.getFab().getDaylyQG() +
					qgc.getFab().getDisability() +
					qgc.getFab().getTc2() +
					qgc.getFab().getReserved1() +
					qgc.getFab().getSubWomen() +
					qgc.getFab().getReserved6() +
					qgc.getFab().getPrintInd() +
					qgc.getFab().getReserved15() +
					qgc.getFab().getGender() +
					qgc.getFab().getReserved5() +
					qgc.getFab().getReWomen() +
					qgc.getFab().getReserved2N() +
					qgc.getFab().getReserved2() +
					qgc.getFab().getActualYear() +
					qgc.getFab().getActualMonth() +
					qgc.getFab().getActualDay() +
					qgc.getFab().getReserved1() +
					qgc.getFab().getReserved2N() +
					"\r\n";
			
			employeeAFI +=
					qgc.getDam().getDamHeader() +
					qgc.getDam().getReserved8() +
					qgc.getDam().getFic() +
					qgc.getDam().getReserved21() +
					qgc.getDam().getReserved6() +
					qgc.getDam().getReserved2() +
					qgc.getDam().getReserved1() +
					qgc.getDam().getReserved7() +
					qgc.getDam().getReservedW2() +
					qgc.getDam().getReserved4() +
					qgc.getDam().getFamily() +
					qgc.getDam().getReserved4() +
					qgc.getDam().getOcupation() +
					qgc.getDam().getReserved8() +
					"\r\n";

		}

		if(null != occ) {
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
					occ.getFab().getFabHeader() +
					occ.getFab().getAction() +
					occ.getFab().getSituation() +
					occ.getFab().getYear() +
					occ.getFab().getMonth() +
					occ.getFab().getDay() +
					occ.getFab().getQuoteGroup() +
					occ.getFab().getDaylyQG() +
					occ.getFab().getDisability() +
					occ.getFab().getTc2() +
					occ.getFab().getReserved1() +
					occ.getFab().getSubWomen() +
					occ.getFab().getReserved6() +
					occ.getFab().getPrintInd() +
					occ.getFab().getReserved15() +
					occ.getFab().getGender() +
					occ.getFab().getReserved5() +
					occ.getFab().getReWomen() +
					occ.getFab().getReserved2N() +
					occ.getFab().getReserved2() +
					occ.getFab().getActualYear() +
					occ.getFab().getActualMonth() +
					occ.getFab().getActualDay() +
					occ.getFab().getReserved1() +
					occ.getFab().getReserved2N() +
					"\r\n";
			
			employeeAFI +=
					occ.getDam().getDamHeader() +
					occ.getDam().getReserved8() +
					occ.getDam().getFic() +
					occ.getDam().getReserved21() +
					occ.getDam().getReserved6() +
					occ.getDam().getReserved2() +
					occ.getDam().getReserved1() +
					occ.getDam().getReserved7() +
					occ.getDam().getReservedW2() +
					occ.getDam().getReserved4() +
					occ.getDam().getFamily() +
					occ.getDam().getReserved4() +
					occ.getDam().getOcupation() +
					occ.getDam().getReserved8() +
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
