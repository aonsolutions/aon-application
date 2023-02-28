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
		
		String header;
		String sintaxIndent;
		String authKey;
		String reserved8;
		String dateCreation;
		String fileName;
		String sufix;
		String priority;
		String test;
		String registryIdent;
		String reserved2;
		
		public ETI(String authKey, String payrollProvider, String fileName, String priorityCode) {
			super();
			
			this.header = "ETI";
			this.sintaxIndent = "AFI93W0000";
			this.authKey = AonStringUtils.leftPad(authKey, 8, '0');
			this.reserved8 = AonStringUtils.leftPad("", 8, ' ');
			this.dateCreation = AonStringUtils.leftPad(year + month + day + hour + minute, 12, '0');;
			this.fileName = AonStringUtils.leftPad(AonStringUtils.isBlank(fileName) ? day + month + hour + minute : fileName, 8, '0');
			this.sufix = "AFI";
			this.priority = "N";
			this.test = AonStringUtils.leftPad("", 1, ' ');
			this.registryIdent = AonStringUtils.leftPad("", 14, '0');
			this.reserved2 = AonStringUtils.leftPad("", 2, ' ');
		}

		public String getHeader() {
			return header;
		}
		public String getSintaxIndent() {
			return sintaxIndent;
		}
		public String getAuthKey() {
			return authKey;
		}
		public String getReserved8() {
			return reserved8;
		}
		public String getDateCreation() {
			return dateCreation;
		}
		public String getFileName() {
			return fileName;
		}
		public String getSufix() {
			return sufix;
		}
		public String getPriority() {
			return priority;
		}
		public String getTest() {
			return test;
		}
		public String getRegistryIdent() {
			return registryIdent;
		}
		public String getReserved2() {
			return reserved2;
		}
		
	}
	
	public static class EMP{
		
		String header;
		String fullCcc;
		String identType;
		String identCountry;
		String ident;
		String reserved2;
		String fullPrincipalCcc;
		String collection;
		String action;
		String reserved1;
		
		public EMP(String fullCcc, String identType, String identCountry, String ident, String fullPrincipalCcc) {
			
			super();
			
			this.header = "EMP";
			this.fullCcc = fullCcc;
			this.identType = AonStringUtils.leftPad(identType, 1, '0');
			this.identCountry = AonStringUtils.leftPad(identCountry, 3, '0');
			this.ident = AonStringUtils.leftPad(ident, 14, '0');
			this.reserved2 = AonStringUtils.leftPad("", 2, ' ');
			this.fullPrincipalCcc = fullPrincipalCcc;
			this.collection = AonStringUtils.leftPad("", 13, ' ');
			this.action = AonStringUtils.leftPad("", 3, ' ');
			this.reserved1 = AonStringUtils.leftPad("", 1, ' ');
		
		}
		
		public String getHeader() {
			return header;
		}
		public String getFullCcc() {
			return fullCcc;
		}
		public String getIdentType() {
			return identType;
		}
		public String getIdentCountry() {
			return identCountry;
		}
		public String getIdent() {
			return ident;
		}
		public String getReserved2() {
			return reserved2;
		}
		public String getFullPrincipalCcc() {
			return fullPrincipalCcc;
		}
		public String getCollection() {
			return collection;
		}
		public String getAction() {
			return action;
		}
		public String getReserved1() {
			return reserved1;
		}
		
	}
	
	public static class RZS{
		
		String header;
		String identRzs;
		String businessman;
		String rzs;
		String authKey;
		String reserved2;
		
		public RZS(String rzs) {
			super();
			
			this.header = "RZS";
			this.identRzs = "0";
			this.businessman = "2";
			this.rzs = AonStringUtils.rightPad(rzs, 55, ' ');
			this.authKey = AonStringUtils.leftPad("", 8, '0');
			this.reserved2 = AonStringUtils.leftPad("", 2, ' ');
		}

		public String getHeader() {
			return header;
		}
		public String getIdentRzs() {
			return identRzs;
		}
		public String getBusinessman() {
			return businessman;
		}
		public String getRzs() {
			return rzs;
		}
		public String getAuthKey() {
			return authKey;
		}
		public String getReserved2() {
			return reserved2;
		}
		
	}

	public static class TRA{
		
		String header;
		String nss;
		String documentType;
		String documentCountry;
		String document;
		String reserved3;
		String decodeControl;
		String nationality;
		String employeeIndic;
		String reserved5;
		
		public TRA(String nss, String documentType, String documentCountry, String document,
				String nationality) {
			
			super();
			
			this.header = "TRA";
			this.nss = AonStringUtils.rightPad(nss, 12, '0');
			
			this.documentType = documentType;
			this.documentCountry = AonStringUtils.leftPad(documentCountry, 3, '0');
			this.document = AonStringUtils.leftPad(document, 14, '0');
			
			this.reserved3 = AonStringUtils.leftPad("", 3, ' ');
			this.decodeControl = AonStringUtils.leftPad("", 25, ' ');
			
			this.nationality = AonStringUtils.leftPad(nationality, 3, '0');
			
			this.employeeIndic = AonStringUtils.leftPad("", 1, ' ');
			this.reserved5 = AonStringUtils.leftPad("", 5, ' ');
		}
		
		public String getHeader() {
			return header;
		}
		public String getNss() {
			return nss;
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
		String header;
		String firstSurname;
		String secondSurname;
		String name;
		String reserved12;
		
		public AYN(String firstSurname, String secondSurname, String name) {
			super();
			
			this.header = "AYN";
			this.firstSurname = AonStringUtils.rightPad(firstSurname, 20, ' ');
			this.secondSurname = AonStringUtils.rightPad(secondSurname, 20, ' ');
			this.name = AonStringUtils.rightPad(name, 15, ' ');
			this.reserved12 = AonStringUtils.rightPad("", 12, ' ');
		}

		public String getHeader() {
			return header;
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
		String header;
		String action;
		String situation;
		String realDate;
		String quoteGroup;
		String daylyQG;
		String disability;
		String tc2;
		String reserved1;
		String subWomen;
		String partialityCoef;
		String employeeColective;
		String printInd;
		
		String profesionalCat; // Only for regime 0911
		String birthDate;
		
		String gender;
		String reserved5;
		String reWomen;
		String disabilityAdmited;
		String freelancer;
		String reserved2;
		String rlce;
		String reserved5Str;
		String rent;
		String worker;
		
		public FAB(String action, String situation, String realDate, String quoteGroup, String tc2, String partialityCoef, String employeeColective, String birthDate, String gender ) {
			super();
			this.header = "FAB";
			this.action = AonStringUtils.rightPad(action, 3, ' ');
			this.situation = AonStringUtils.leftPad(situation, 2, '0');
			
			this.realDate = AonStringUtils.leftPad(realDate, 8, '0');
			
			this.quoteGroup = AonStringUtils.leftPad(quoteGroup, 2, '0');
			this.daylyQG = "N";
			this.disability = AonStringUtils.leftPad("", 2, '0');
			this.tc2 = AonStringUtils.leftPad(tc2, 3, '0');
			
			this.reserved1 = StringUtils.leftPad("", 1, ' ');
			this.subWomen = "N";
			this.partialityCoef = AonStringUtils.leftPad(partialityCoef, 3, '0');
			this.employeeColective = AonStringUtils.leftPad(employeeColective, 3, '0');
			this.printInd = " ";

			this.profesionalCat = AonStringUtils.leftPad("", 7, '0');
			this.birthDate = AonStringUtils.leftPad(birthDate, 8, '0');
			
			this.gender = AonStringUtils.leftPad(gender, 1, '1');
			this.reserved5 = AonStringUtils.leftPad("", 5, '0');
			this.reWomen = " ";
			this.disabilityAdmited = "N";
			this.freelancer = " ";
			this.reserved2 = AonStringUtils.leftPad("", 2, ' ');
			
			this.rlce = AonStringUtils.leftPad("", 4, '0');
			
			this.reserved5Str = AonStringUtils.leftPad("", 5, ' ');
			this.rent = "N";
			this.worker = "N";
		}
		
		public String getHeader() {
			return header;
		}
		public String getAction() {
			return action;
		}
		public String getSituation() {
			return situation;
		}
		public String getRealDate() {
			return realDate;
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
		public String getProfesionalCat() {
			return profesionalCat;
		}
		public String getBirthDate() {
			return birthDate;
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
		public String getRlce() {
			return rlce;
		}
		public String getReserved5Str() {
			return reserved5Str;
		}
		public String getRent() {
			return rent;
		}
		public String getWorker() {
			return worker;
		}
		
	}
	
	public static class ODL{
		String header;
		String convCollective;
		String reserved6;
		String cno;
		String reservedN6;
		String contribution;
		String reserved31;
		
		public ODL( String convCollective,  String cno ) {
			this.header = "ODL";
			this.convCollective = convCollective;
			this.reserved6 = AonStringUtils.rightPad("", 6, ' ');
			this.cno = AonStringUtils.leftPad(cno, 4, '0');;
			this.reservedN6 = AonStringUtils.rightPad("", 6, '0');
			this.contribution = AonStringUtils.rightPad("", 6, '0');
			this.reserved31 = AonStringUtils.rightPad("", 31, ' ');
		}

		public String getHeader() {
			return header;
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
		String header;
		String reserved14;
		String excedencia;
		String excedenciaCcc;
		String notifDate;
		String readmited;
		String endDate;
		String boatType;
		String reserved12;
		
		public OTD( String endDate ) {
			this.header = "OTD";
			this.reserved14 = AonStringUtils.rightPad("", 14, ' ');
			this.excedencia = AonStringUtils.rightPad("", 8, '0');
			this.excedenciaCcc = AonStringUtils.rightPad("", 15, '0');
			this.notifDate = AonStringUtils.rightPad("", 8, '0');
			this.readmited = "N";
			this.endDate = AonStringUtils.rightPad(endDate, 8, '0');
			this.boatType = AonStringUtils.rightPad("", 1, '0');
			this.reserved12 = AonStringUtils.rightPad("", 12, ' ');
		}
		
		public String getHeader() {
			return header;
		}
		public String getReserved14() {
			return reserved14;
		}
		public String getExcedencia() {
			return excedencia;
		}
		public String getExcedenciaCcc() {
			return excedenciaCcc;
		}
		public String getNotifDate() {
			return notifDate;
		}
		public String getReadmited() {
			return readmited;
		}
		public String getEndDate() {
			return endDate;
		}
		public String getBoatType() {
			return boatType;
		}
		public String getReserved12() {
			return reserved12;
		}
		
	}
	
	public static class DAM{
		String header;
		String contractStartDate;
		String fic;
		String reserved19;
		String relevo;
		String reserved11;
		String familyVinc;
		String reserved16;
		String ocupation;
		String reserved8;
		
		public DAM (String ocupation) {
			this.header = "DAM";
			this.contractStartDate = AonStringUtils.leftPad("", 8, '0');
			this.fic = " ";
			this.reserved19 = AonStringUtils.leftPad("", 19, '0');
			this.relevo = " ";
			this.reserved11 = AonStringUtils.leftPad("", 11, '0');
			this.familyVinc = " ";
			this.reserved16 = AonStringUtils.leftPad("", 16, '0');
			this.ocupation = AonStringUtils.leftPad(ocupation, 2, ' ');
			this.reserved8 = AonStringUtils.leftPad("", 8, '0');
		}

		public String getHeader() {
			return header;
		}
		public String getContractStartDate() {
			return contractStartDate;
		}
		public String getFic() {
			return fic;
		}
		public String getReserved19() {
			return reserved19;
		}
		public String getRelevo() {
			return relevo;
		}
		public String getReserved11() {
			return reserved11;
		}
		public String getFamilyVinc() {
			return familyVinc;
		}
		public String getReserved16() {
			return reserved16;
		}
		public String getOcupation() {
			return ocupation;
		}
		public String getReserved8() {
			return reserved8;
		}
		
	}
	
	public static class FCT{
		String header;
		String reserved6;
		String holidayDate;
		String reserved32;
		String reserved16;
		String saa;
		String reserved2;
		
		public FCT ( String holidayDate, String saa ) {
			this.header = "FCT";
			this.reserved6 = AonStringUtils.leftPad("", 6, '0');
			this.holidayDate = AonStringUtils.leftPad(holidayDate, 8, '0');
			this.reserved32 = AonStringUtils.leftPad("", 32, '0');
			this.reserved16 = AonStringUtils.leftPad("", 16, ' ');
			this.saa = AonStringUtils.leftPad(saa, 3, '0');
			this.reserved2 = AonStringUtils.leftPad("", 2, ' ');
		}

		public String getHeader() {
			return header;
		}
		public String getReserved6() {
			return reserved6;
		}
		public String getHolidayDate() {
			return holidayDate;
		}
		public String getReserved32() {
			return reserved32;
		}
		public String getReserved16() {
			return reserved16;
		}
		public String getSaa() {
			return saa;
		}
		public String getReserved2() {
			return reserved2;
		}
		
	}
	
	public static class ETF{
		String header;
		String sintaxIndent;
		String authKey;
		String reserved8;
		String dateCreation;
		String fileName;
		String sufixAFI;
		String priorityCode;
		String test;
		String countEmp;
		String countLines;
		String reserved3;
		
		public ETF(String authKey, String payrollProvider, String fileName, String countEmployees, String countLines) {
			super();
			this.header = "ETF";
			this.sintaxIndent = "AFI93W0000";
			this.authKey = AonStringUtils.leftPad(authKey, 8, '0');
			this.reserved8 = AonStringUtils.leftPad("", 8, ' ');
			this.dateCreation = AonStringUtils.leftPad(year + month + day + hour + minute, 12, '0');;
			this.fileName = AonStringUtils.leftPad(AonStringUtils.isBlank(fileName) ? day + month + hour + minute : fileName, 8, '0');
			this.sufixAFI = "AFI";
			this.priorityCode = "N";
			
			this.test = " ";
			this.countEmp = StringUtils.leftPad(countEmployees, 5, '0');
			this.countLines = StringUtils.leftPad(countLines, 8, '0');
			this.reserved3 = StringUtils.leftPad("", 3, ' ');
		}

		public String getHeader() {
			return header;
		}
		public String getSintaxIndent() {
			return sintaxIndent;
		}
		public String getAuthKey() {
			return authKey;
		}
		public String getReserved8() {
			return reserved8;
		}
		public String getDateCreation() {
			return dateCreation;
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
				empJson.get("fullCcc").toString(),
				empJson.get("identType").toString(),
				empJson.get("country").toString(),
				empJson.get("ident").toString(),
				empJson.get("fullPrincipalCcc").toString());
		
		//RZS
		JSONObject rzsJson = (JSONObject) employeeData.get("RZS");
		RZS rzs = new RZS(rzsJson.get("rzsName").toString());
		
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
					fabJson.get("realDate").toString(),
					fabJson.get("quoteGroup").toString(),
					fabJson.get("tc2").toString(),
					fabJson.get("partialityCoef") == null ? "000" : fabJson.get("partialityCoef").toString(),
					fabJson.get("employeeColective") == null ? "" : fabJson.get("employeeColective").toString(),
					fabJson.get("birthDate") == null ? "" : fabJson.get("birthDate").toString(),
					fabJson.get("gender").toString());
			
			JSONObject odlJson = (JSONObject) sdcJson.get("ODL");
			ODL odl = new ODL(
					odlJson.get("convCollective").toString(),
					odlJson.get("cno") == null ? "" : odlJson.get("cno").toString());
			
			JSONObject otdJson = (JSONObject) sdcJson.get("OTD");
			OTD otd = null;
			if(null != otdJson)
				otd = new OTD(
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
					fabJson.get("realDate").toString(),
					fabJson.get("quoteGroup").toString(),
					fabJson.get("tc2").toString(),
					fabJson.get("partialityCoef") == null ? "000" : fabJson.get("partialityCoef").toString(),
					fabJson.get("employeeColective") == null ? "" : fabJson.get("employeeColective").toString(),
					fabJson.get("birthDate") == null ? "" : fabJson.get("birthDate").toString(),
					fabJson.get("gender").toString());
			
			DAM dam = new DAM(""); // 	JSONObject damJson = (JSONObject) edcJson.get("DAM");
			
			JSONObject fctJson = (JSONObject) edcJson.get("FCT");
			if(null != fctJson) {
				FCT fct = new FCT(
						fctJson.get("holidayDate").toString(),
						fctJson.get("saa").toString());
				
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
					fabJson.get("realDate").toString(),
					fabJson.get("quoteGroup").toString(),
					fabJson.get("tc2").toString(),
					fabJson.get("partialityCoef") == null ? "000" : fabJson.get("partialityCoef").toString(),
					fabJson.get("employeeColective") == null ? "" : fabJson.get("employeeColective").toString(),
					fabJson.get("birthDate") == null ? "" : fabJson.get("birthDate").toString(),
					fabJson.get("gender").toString());
			
			JSONObject damJson = (JSONObject) chcJson.get("DAM");
			DAM dam = new DAM(damJson.get("ocupation") == null ? "" : damJson.get("ocupation").toString());
			
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
				numEmployees+"",
				totalLines+"");
		
		employeeAFI = createEmployeeAFI(eti, emp, rzs, tra, ayn, ma, mb, mc, etf);
		
		return employeeAFI;
		
	}
	
	private static String createEmployeeAFI(ETI eti, EMP emp, RZS rzs, TRA tra, AYN ayn, MA ma, MB mb, MC mc, ETF etf) {
		
		String employeeAFI = "";
		
		employeeAFI +=
				eti.getHeader() +
				eti.getSintaxIndent() +
				eti.getAuthKey() +
				eti.getReserved8() + 
				eti.getDateCreation() +
				eti.getFileName() + 
				eti.getSufix() +
				eti.getPriority() +
				eti.getTest() +
				eti.getRegistryIdent() +
				eti.getReserved2() +
				"\r\n";
		
		employeeAFI +=
				emp.getHeader() + 
				emp.getFullCcc() +
				emp.getIdentType() + 
				emp.getIdentCountry() + 
				emp.getIdent() + 
				emp.getReserved2() + 
				emp.getFullPrincipalCcc() +
				emp.getCollection() +
				emp.getAction() +
				emp.getReserved1() +
				"\r\n";
		
		employeeAFI +=
				rzs.getHeader() +
				rzs.getIdentRzs() +
				rzs.getBusinessman() +
				rzs.getRzs() +
				rzs.getAuthKey() +
				rzs.getReserved2() +
				"\r\n";
		
		if(null != ma) {
			employeeAFI +=
					tra.getHeader() +
					tra.getNss() +
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
					ayn.getHeader() +
					ayn.getFirstSurname() +
					ayn.getSecondSurname() +
					ayn.getName() +
					ayn.getReserved12() +
					"\r\n";
			
			employeeAFI +=
					ma.getFab().getHeader() +
					ma.getFab().getAction() +
					ma.getFab().getSituation() +
					ma.getFab().getRealDate() +
					ma.getFab().getQuoteGroup() +
					ma.getFab().getDaylyQG() +
					ma.getFab().getDisability() +
					ma.getFab().getTc2() +
					ma.getFab().getReserved1() +
					ma.getFab().getSubWomen() +
					ma.getFab().getPartialityCoef() +
					ma.getFab().getEmployeeColective() +
					ma.getFab().getPrintInd() +
					ma.getFab().getProfesionalCat() +
					ma.getFab().getBirthDate() +
					ma.getFab().getGender() +
					ma.getFab().getReserved5() +
					ma.getFab().getReWomen() +
					ma.getFab().getDisabilityAdmited() + 
					ma.getFab().getFreelancer() +
					ma.getFab().getReserved2() +
					ma.getFab().getRlce() +
					ma.getFab().getReserved5Str() +
					ma.getFab().getRent() +
					ma.getFab().getWorker() + 
					"\r\n";
			
			employeeAFI +=
					ma.getOdl().getHeader() +
					ma.getOdl().getConvCollective() +
					ma.getOdl().getReserved6() +
					ma.getOdl().getCno() +
					ma.getOdl().getReservedN6() +
					ma.getOdl().getContribution() +
					ma.getOdl().getReserved31() +
					"\r\n";
			
			if(null != ma.getOtd())
				employeeAFI +=
						ma.getOtd().getHeader() +
						ma.getOtd().getReserved14() +
						ma.getOtd().getExcedencia() +
						ma.getOtd().getExcedenciaCcc() +
						ma.getOtd().getNotifDate() +
						ma.getOtd().getReadmited() +
						ma.getOtd().getEndDate() +
						ma.getOtd().getBoatType() +
						ma.getOtd().getReserved12() +
						"\r\n";
		}
			
		if(null != mb) {
			employeeAFI +=
					tra.getHeader() +
					tra.getNss() +
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
					ayn.getHeader() +
					ayn.getFirstSurname() +
					ayn.getSecondSurname() +
					ayn.getName() +
					ayn.getReserved12() +
					"\r\n";
			
			employeeAFI +=
					mb.getFab().getHeader() +
					mb.getFab().getAction() +
					mb.getFab().getSituation() +
					mb.getFab().getRealDate() +
					mb.getFab().getQuoteGroup() +
					mb.getFab().getDaylyQG() +
					mb.getFab().getDisability() +
					mb.getFab().getTc2() +
					mb.getFab().getReserved1() +
					mb.getFab().getSubWomen() +
					mb.getFab().getPartialityCoef() +
					mb.getFab().getEmployeeColective() +
					mb.getFab().getPrintInd() +
					mb.getFab().getProfesionalCat() +
					mb.getFab().getBirthDate() +
					mb.getFab().getGender() +
					mb.getFab().getReserved5() +
					mb.getFab().getReWomen() +
					mb.getFab().getDisabilityAdmited() + 
					mb.getFab().getFreelancer() +
					mb.getFab().getReserved2() +
					mb.getFab().getRlce() +
					mb.getFab().getReserved5Str() +
					mb.getFab().getRent() +
					mb.getFab().getWorker() + 
					"\r\n";
			
			employeeAFI +=
					mb.getDam().getHeader() +
					mb.getDam().getContractStartDate() +
					mb.getDam().getFic() +
					mb.getDam().getReserved19() +
					mb.getDam().getRelevo() +
					mb.getDam().getReserved11() +
					mb.getDam().getFamilyVinc() +
					mb.getDam().getReserved16() +
					mb.getDam().getOcupation() +
					mb.getDam().getReserved8() +
					"\r\n";
			
			if(null != mb.getFct())
				employeeAFI +=
						mb.getFct().getHeader() +
						mb.getFct().getReserved6() +
						mb.getFct().getHolidayDate() +
						mb.getFct().getReserved32() +
						mb.getFct().getReserved16() +
						mb.getFct().getSaa() +
						mb.getFct().getReserved2() +
						"\r\n";

		}
			
		if(null != mc) {
			employeeAFI +=
					tra.getHeader() +
					tra.getNss() +
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
					ayn.getHeader() +
					ayn.getFirstSurname() +
					ayn.getSecondSurname() +
					ayn.getName() +
					ayn.getReserved12() +
					"\r\n";
			
			employeeAFI +=
					mc.getFab().getHeader() +
					mc.getFab().getAction() +
					mc.getFab().getSituation() +
					mc.getFab().getRealDate() +
					mc.getFab().getQuoteGroup() +
					mc.getFab().getDaylyQG() +
					mc.getFab().getDisability() +
					mc.getFab().getTc2() +
					mc.getFab().getReserved1() +
					mc.getFab().getSubWomen() +
					mc.getFab().getPartialityCoef() +
					mc.getFab().getEmployeeColective() +
					mc.getFab().getPrintInd() +
					mc.getFab().getProfesionalCat() +
					mc.getFab().getBirthDate() +
					mc.getFab().getGender() +
					mc.getFab().getReserved5() +
					mc.getFab().getReWomen() +
					mc.getFab().getDisabilityAdmited() + 
					mc.getFab().getFreelancer() +
					mc.getFab().getReserved2() +
					mc.getFab().getRlce() +
					mc.getFab().getReserved5Str() +
					mc.getFab().getRent() +
					mc.getFab().getWorker() + 
					"\r\n";
			
			employeeAFI +=
					mc.getDam().getHeader() +
					mc.getDam().getContractStartDate() +
					mc.getDam().getFic() +
					mc.getDam().getReserved19() +
					mc.getDam().getRelevo() +
					mc.getDam().getReserved11() +
					mc.getDam().getFamilyVinc() +
					mc.getDam().getReserved16() +
					mc.getDam().getOcupation() +
					mc.getDam().getReserved8() +
					"\r\n";

		}
		
		employeeAFI +=
				etf.getHeader() +
				etf.getSintaxIndent() +
				etf.getAuthKey() +
				etf.getReserved8() +
				etf.getDateCreation() +
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
