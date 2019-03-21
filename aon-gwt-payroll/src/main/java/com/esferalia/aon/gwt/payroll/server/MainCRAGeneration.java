package com.esferalia.aon.gwt.payroll.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

public final class MainCRAGeneration {
	public static class ETI{
		String etiHeader;
		String sintaxIndent;
		String authKey;
		String reserved8;
		String year;
		String month;
		String day;
		String hour;
		String minute;
		String fileName; //Length 8
		String sufixCRA;
		String priorityCode;
		String test;
		String registryIdent;
		String reservedTGSS;
		String reserved1;
		
		public ETI(String authKey, String fileName, String priorityCode) {
			super();
			this.etiHeader = "ETI";
			this.sintaxIndent = "CRA12WS000";
			this.authKey = StringUtils.leftPad(authKey, 8, '0');
			this.reserved8 = StringUtils.leftPad("", 8, ' ');
			Date actualDate = new Date();
			this.year = actualDate.getYear() + 1900 + "";
			this.month = StringUtils.leftPad(actualDate.getMonth()+1+"", 2, '0');
			this.day = StringUtils.leftPad(actualDate.getDate()+"", 2, '0');
			this.hour = StringUtils.leftPad(actualDate.getHours()+"", 2, '0');
			this.minute = StringUtils.leftPad(actualDate.getMinutes()+"", 2, '0');
			this.fileName = (null == fileName) ? this.day + this.month + this.hour + this.minute : fileName;
			this.sufixCRA = "CRA";
			this.priorityCode = priorityCode;
			this.test = " ";
			this.registryIdent = StringUtils.leftPad("", 14, '0');
			this.reservedTGSS = " ";
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
		public String getReserved8() {
			return reserved8;
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
		public String getSufixCRA() {
			return sufixCRA;
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
	
	public static class DDE{
		String empHeader;
		String cccRegime;
		String cccProvince;
		String ccc;
		String year;
		String month;
		String reserved46;
		
		public DDE(String cccProvince, String cccRegime, String ccc) {
			super();
			this.empHeader = "DDE";
			this.cccRegime = cccRegime;
			this.cccProvince = cccProvince;
			this.ccc = ccc;
			Date actualDate = new Date();
			this.year = actualDate.getYear() + 1900 + "";
			this.month = StringUtils.leftPad(actualDate.getMonth()+1+"", 2, '0');
			this.reserved46 = StringUtils.leftPad("", 46, ' ');
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
		public String getReserved46() {
			return reserved46;
		}
		
	}
	
	public static class TRB{
		
		String trbHeader;
		String numAfilicion;
		String reserved55;
		
		public TRB(String numAfilicion) {
			super();
			this.trbHeader = "TRB";
			this.numAfilicion = numAfilicion;
			this.reserved55 = StringUtils.leftPad("", 55, ' ');
		}
		
		public String getTrbHeader() {
			return trbHeader;
		}
		public String getNumAfilicion() {
			return numAfilicion;
		}
		public String getReserved55() {
			return reserved55;
		}
		
	}
	
	public static class CRE{
		
		String creHeader;
		String concept;
		String include_exclude;
		String amount;
		String action; // M, B, C
		String reserved52;
		
		public CRE(String numAfilicion, String concept, String include_exclude, String amount, String action) {
			super();
			this.creHeader = "CRE";
			this.concept = concept;
			this.include_exclude = include_exclude;
			this.amount = StringUtils.leftPad(amount, 9, '0');
			this.action = action;
			this.reserved52 = StringUtils.leftPad("", 52, ' ');
		}
		
		public String getCreHeader() {
			return creHeader;
		}
		public String getConcept() {
			return concept;
		}
		public String getInclude_exclude() {
			return include_exclude;
		}
		public String getAmount() {
			return amount;
		}
		public String getAction() {
			return action;
		}
		public String getReserved52() {
			return reserved52;
		}
		
		
	}
		
	public static class EMPL{
		TRB trb;
		ArrayList<CRE> cres;
		
		public EMPL(TRB trb, ArrayList<CRE> cres) {
			super();
			this.trb = trb;
			this.cres = cres;
		}

		public TRB getTrb() {
			return trb;
		}

		public void setTrb(TRB trb) {
			this.trb = trb;
		}

		public ArrayList<CRE> getCres() {
			return cres;
		}

		public void setCres(ArrayList<CRE> cres) {
			this.cres = cres;
		}
	}
	
	public static class EMPRE{
		Map<DDE,ArrayList<EMPL>> empls;

		public EMPRE() {
			super();
			this.empls = new HashMap<DDE,ArrayList<EMPL>>();
		}
		
		public void add(DDE dde, ArrayList<EMPL> empls) {
			this.empls.put(dde, empls);
		}
	}
	
	public static String generateMainCRA (JSONObject agrarianData) {
		//RESULT
		String agrarianAFI = null;
		
		//ETI
		JSONObject etiJson = (JSONObject) agrarianData.get("ETI");
		ETI eti = new ETI(
				etiJson.get("authkey").toString(), 
				null == etiJson.get("fileName") ? null : etiJson.get("fileName").toString(),
				etiJson.get("prorityCode").toString());
		
		//EMP
		JSONObject empJson = (JSONObject) agrarianData.get("DDE");
		DDE dde = new DDE(
				empJson.get("cccProvince").toString(), 
				empJson.get("cccRegime").toString(), 
				empJson.get("ccc").toString());
		
		//RZS
//		JSONObject rzsJson = (JSONObject) agrarianData.get("RZS");
//		RZS rzs = new RZS(
//				rzsJson.get("businessmanType").toString(), 
//				rzsJson.get("rzsName").toString());
//		
//		//EMPLOYEES
//		JSONArray employees = (JSONArray) agrarianData.get("EMPS");
//		EMPLS empls = new EMPLS();
//		for(int i=0; i<employees.size(); i++) {
//			JSONObject emplJson = (JSONObject) employees.get(i);
//			
//			//TRA
//			JSONObject traJson = (JSONObject) emplJson.get("TRA");
//			TRA tra = new TRA(
//					traJson.get("numAfiliacion").toString(),
//					traJson.get("documentType").toString(), 
//					traJson.get("documentCountry").toString(),
//					traJson.get("document").toString(),
//					traJson.get("nationality").toString());
//			
//			//AYN
//			JSONObject aynJson = (JSONObject) emplJson.get("AYN");
//			AYN ayn = new AYN(
//					aynJson.get("firstSurname").toString(), 
//					aynJson.get("secondSurname").toString(), 
//					aynJson.get("name").toString());
//			
//			//FAB
//			String fab = emplJson.get("FAB").toString();
//			
//			//DRA
//			JSONObject draJson = (JSONObject) emplJson.get("DRA");
//			DRA dra = new DRA(
//					draJson.get("year").toString(), 
//					draJson.get("month").toString(), 
//					(JSONArray) draJson.get("days"));
//			
//			//CREATE EMPL
//			EMPL empl = new EMPL(tra, ayn, fab, dra);
//			empls.add(empl);
//		}
//		
//		//CONF
//		JSONObject confJson = (JSONObject) agrarianData.get("CONF");
//		Integer staticLines = Integer.parseInt(confJson.get("staticLines").toString());
//		Integer employeeLines = Integer.parseInt(confJson.get("employeeLines").toString());
//		Integer numEmployees = Integer.parseInt(confJson.get("numEmployees").toString());
//		Integer totalLines = numEmployees * employeeLines + staticLines;
//		
//		//ETF
//		JSONObject etfJson = (JSONObject) agrarianData.get("ETF");
//		ETF etf = new ETF(
//				etfJson.get("authkey").toString(), 
//				etfJson.get("payrollProvider").toString(), 
//				 null == etfJson.get("fileName") ? null : etfJson.get("fileName").toString(), 
//						 etfJson.get("priorityCode").toString(),
//				 totalLines+"");
//		
//		agrarianAFI = createAgrarianAFI(eti, emp, rzs, empls, etf);
		
		return agrarianAFI;
		
	}
	
//	private static String createAgrarianAFI(ETI eti, EMP emp, RZS rzs, EMPLS empls, ETF etf) {
//		String agrarianAFI = "";
//		
//		agrarianAFI +=
//				eti.getEtiHeader() +
//				eti.getSintaxIndent() +
//				eti.getAuthKey() +
//				eti.getPayrollProvider() +
//				eti.getReserved5() +
//				eti.getYear() +
//				eti.getMonth() +
//				eti.getDay() +
//				eti.getHour() +
//				eti.getMinute() +
//				eti.getFileName() +
//				eti.getSufixAFI() +
//				eti.getPriorityCode() +
//				eti.getTest() +
//				eti.getRegistryIdent() +
//				eti.getReservedTGSS() +
//				eti.getReserved1() +
//				"\r\n";
//		
//		agrarianAFI +=
//				emp.getEmpHeader() +
//				emp.getCccRegime() +
//				emp.getCccProvince() +
//				emp.getCcc() +
//				emp.getBusinessmanCode() +
//				emp.getCccRegimePrincipal() +
//				emp.getCccProvincePrincipal() +
//				emp.getCccPrincipal() +
//				emp.getReservedCollection() +
//				emp.getAction() +
//				emp.getReserved1() +
//				"\r\n";
//		
//		agrarianAFI +=
//				rzs.getRzsHeader() +
//				rzs.getRzsIndicator() +
//				rzs.getBusinessmanType() +
//				rzs.getRzsName() +
//				rzs.getAuthKey() +
//				rzs.getReserved2() +
//				"\r\n";
//		
//		for(EMPL empl : empls.getEmpls()) {
//			TRA tra = empl.getTra();
//			AYN ayn = empl.getAyn();
//			String fab = empl.getFab();
//			DRA dra = empl.getDra();
//			
//			agrarianAFI +=
//					tra.getTraHeader() +
//					tra.getNumAfilicion() +
//					tra.getDocumentType() +
//					tra.getDocumentCountry() +
//					tra.getDocument() +
//					tra.getReserved3() +
//					tra.getDecodeControl() +
//					tra.getNationality() +
//					tra.getEmployeeIndic() +
//					tra.getReserved5() +
//					"\r\n";
//			
//			agrarianAFI +=
//					ayn.getAynHeader() +
//					ayn.getFirstSurname() +
//					ayn.getSecondSurname() +
//					ayn.getName() +
//					ayn.getReserved12() +
//					"\r\n";
//			
//			agrarianAFI +=
//					fab +
//					"\r\n";
//			
//			agrarianAFI +=
//					dra.getDraHeader() +
//					dra.getYear() +
//					dra.getMonth();
//			
//			agrarianAFI += dra.getDays();
//			
//			agrarianAFI +=
//					dra.cosolidateDate +
//					dra.newDate +
//					dra.reserved14 +
//					"\r\n";
//		}
//		
//		agrarianAFI +=
//				etf.getEtfHeader() +
//				etf.getSintaxIndent() +
//				etf.getAuthKey() +
//				etf.getPayrollProvider() +
//				etf.getReserved5() +
//				etf.getYear() +
//				etf.getMonth() +
//				etf.getDay() +
//				etf.getHour() +
//				etf.getMinute() +
//				etf.getFileName() +
//				etf.getSufixAFI() +
//				etf.getPriorityCode() +
//				etf.getTest() +
//				etf.getCountEmp() +
//				etf.countLines +
//				etf.getReserved3() +
//				"\r\n";
//		
//		return agrarianAFI;
//	}

	
}
