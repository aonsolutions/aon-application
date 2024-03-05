package com.esferalia.aon.payroll.tgss.afi;

import java.util.ArrayList;
import java.util.Date;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.payroll.tgss.cra.StringUtils;

public class MainPensionPlanAFIGenerator {
	
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
//			this.sintaxIndent = "AFI90WS881";
			this.sintaxIndent = "AFI95W0352";
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
			this.registryIdent = StringUtils.leftPad("", 14, '0');
			this.reservedTGSS = "A";
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
		String businessmanCode;
		String cccRegimePrincipal;
		String cccProvincePrincipal;
		String cccPrincipal;
		String reservedCollection;
		String action;
		String reserved1;
		
		public EMP(String regime, String cccProvince, String ccc, String cccRegimePrincipal, String cccProvincePrincipal,
				String cccPrincipal) {
			super();
			this.empHeader = "EMP";
			this.cccRegime = regime;
			this.cccProvince = cccProvince;
			this.ccc = ccc;
			this.businessmanCode = StringUtils.leftPad("", 20, ' ');
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
		public String getBusinessmanCode() {
			return businessmanCode;
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
	
	public static class DRA{
		String draHeader;
		String year;
		String month;
		char[] days;
		String cosolidateDate;
		String newDate;
		String reserved14;
		
		public DRA(String year, String month, JSONArray days ) {
			super();
			this.draHeader = "DRA";
			this.year = year;
			this.month = StringUtils.leftPad(month, 2, '0');
			char[] day = new char[days.size()];
			for(int i=0; i< days.size(); i++) {
//				System.out.print(days.get(i).asString().getString());
				day[i] = days.get(i).toString().charAt(0);
			}
			this.days = day;
			this.cosolidateDate = StringUtils.rightPad("", 8, '0');
			this.newDate = StringUtils.rightPad("", 8, '0');
			this.reserved14 = StringUtils.rightPad("", 14, ' ');
		}
		
		public String getDraHeader() {
			return draHeader;
		}
		public String getYear() {
			return year;
		}
		public String getMonth() {
			return month;
		}
		public String getDays() {
			String daysStr = "";
			for(int i=0; i<this.days.length; i++) {
				if (this.days[i] == ' ')
					daysStr+= " ";
				else
					daysStr += "S";
			}
				
			return daysStr;
		}
		public String getCosolidateDate() {
			return cosolidateDate;
		}
		public String getNewDate() {
			return newDate;
		}
		public String getReserved14() {
			return reserved14;
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
		
		public ETF(String authKey, String payrollProvider, String fileName, String priorityCode, String countEmp, String countLines) {
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
			this.countEmp = StringUtils.leftPad(countEmp, 5, '0');
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
	
	public static class EMPL{
		TRA tra;
		AYN ayn;
		String fab;
		String odl;
		String fct;
		
		public EMPL(TRA tra, AYN ayn, String fab, String odl, String fct) {
			super();
			this.tra = tra;
			this.ayn = ayn;
			this.fab = fab;
			this.odl = odl;
			this.fct = fct;
		}
		
		public TRA getTra() {
			return tra;
		}
		
		public void setTra(TRA tra) {
			this.tra = tra;
		}
		
		public AYN getAyn() {
			return ayn;
		}
		
		public void setAyn(AYN ayn) {
			this.ayn = ayn;
		}
		
		public String getFab() {
			return fab;
		}
		
		public void setFab(String fab) {
			this.fab = fab;
		}

		public String getOdl() {
			return odl;
		}

		public void setOdl(String odl) {
			this.odl = odl;
		}

		public String getFct() {
			return fct;
		}

		public void setFct(String fct) {
			this.fct = fct;
		}
		
	}
	
	public static class EMPLS{
		ArrayList<EMPL> empls;

		public EMPLS() {
			super();
			this.empls = new ArrayList<EMPL>();
		}
		
		public void add(EMPL empl) {
			this.empls.add(empl);
		}
		
		public ArrayList<EMPL> getEmpls() {
			return this.empls;
		}	
	}
	
	public static class ACTIVITES{
		ArrayList<ACTIVITY> activities;

		public ACTIVITES() {
			super();
			this.activities = new ArrayList<ACTIVITY>();
		}
		
		public void addActivity(ACTIVITY activity) {
			this.activities.add(activity);
		}
		public ArrayList<ACTIVITY> getActivities() {
			return activities;
		}
	}
	
	public static class ACTIVITY{
		EMPLS empls;
		EMP emp;
		RZS rzs;

		public ACTIVITY(EMPLS empls, EMP emp, RZS rzs) {
			super();
			this.empls = empls;
			this.emp = emp;
			this.rzs = rzs;
		}

		public EMPLS getEmpls() {
			return empls;
		}
		public EMP getEmp() {
			return emp;
		}
		public RZS getRzs() {
			return rzs;
		}
		
	}
	
	public static String generatePensionPlanAFI (JSONObject pensionPlanData) {
		//RESULT
		String agrarianAFI = null;
		
		//ETI
		JSONObject etiJson = (JSONObject) pensionPlanData.get("ETI");
		ETI eti = new ETI(
				etiJson.get("authkey").toString(), 
				etiJson.get("payrollProvider").toString(), 
				null == etiJson.get("fileName") ? null : etiJson.get("fileName").toString(), 
				etiJson.get("prorityCode").toString());
		
		JSONArray activitiesJSON = (JSONArray) pensionPlanData.get("ACTIVITIES");
		ACTIVITES activities = new ACTIVITES();
		for(int i=0; i<activitiesJSON.size(); i++) {
			//EMP
			JSONObject empJson = (JSONObject) ((JSONObject)activitiesJSON.get(i)).get("EMP");
			EMP emp = new EMP(
					empJson.get("regime").toString(),
					empJson.get("cccProvince").toString(),
					empJson.get("ccc").toString(),
					empJson.get("cccRegimePrincipal").toString(),
					empJson.get("cccProvincePrincipal").toString(),
					empJson.get("cccPrincipal").toString());
			
			//RZS
			JSONObject rzsJson = (JSONObject) ((JSONObject)activitiesJSON.get(i)).get("RZS");
			RZS rzs = new RZS(
					rzsJson.get("businessmanType").toString(), 
					rzsJson.get("rzsName").toString());
			
			//EMPLOYEES
			JSONArray employees = (JSONArray) ((JSONObject)activitiesJSON.get(i)).get("EMPS");
			EMPLS empls = new EMPLS();
			for(int j=0; j<employees.size(); j++) {
				JSONObject emplJson = (JSONObject) employees.get(j);
				
				//TRA
				JSONObject traJson = (JSONObject) emplJson.get("TRA");
				TRA tra = new TRA(
						traJson.get("numAfiliacion").toString(),
						traJson.get("documentType").toString(), 
						traJson.get("documentCountry").toString(),
						traJson.get("document").toString(),
						traJson.get("nationality").toString());
				
				//AYN
				JSONObject aynJson = (JSONObject) emplJson.get("AYN");
				AYN ayn = new AYN(
						aynJson.get("firstSurname").toString(), 
						aynJson.get("secondSurname").toString(), 
						aynJson.get("name").toString());
				
				//FAB
				String fab = emplJson.get("FAB").toString();
				
				//ODL
				String odl = emplJson.get("ODL").toString();
				
				//FCT
				String fct = emplJson.get("FCT").toString();
				
				//CREATE EMPL
				EMPL empl = new EMPL(tra, ayn, fab, odl, fct);
				empls.add(empl);
			}
			
			ACTIVITY activity = new ACTIVITY(empls, emp, rzs);
			activities.addActivity(activity);
		}
		
		//CONF
		JSONObject confJson = (JSONObject) pensionPlanData.get("CONF");
		Integer staticLines = Integer.parseInt(confJson.get("staticLines").toString());
		Integer employeeLines = Integer.parseInt(confJson.get("employeeLines").toString());
		Integer numEmployees = Integer.parseInt(confJson.get("numEmployees").toString());
		Integer activityLines = Integer.parseInt(confJson.get("activityLines").toString());
		Integer numCCCs = Integer.parseInt(confJson.get("numCCCs").toString());
		Integer totalLines = numEmployees * employeeLines + activityLines * numCCCs + staticLines;
		
		//ETF
		JSONObject etfJson = (JSONObject) pensionPlanData.get("ETF");
		ETF etf = new ETF(
				etfJson.get("authkey").toString(), 
				etfJson.get("payrollProvider").toString(), 
				 null == etfJson.get("fileName") ? null : etfJson.get("fileName").toString(), 
						 etfJson.get("priorityCode").toString(),
				numCCCs+"", 
				totalLines+"");
		
		agrarianAFI = createAgrarianAFI(eti, activities, etf);
		
		return agrarianAFI;
		
	}
	
	private static String createAgrarianAFI(ETI eti, ACTIVITES activities, ETF etf) {
		String agrarianAFI = "";
		
		agrarianAFI +=
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
		
		for(ACTIVITY activity : activities.getActivities()) {
			EMP emp = activity.getEmp();
			agrarianAFI +=
					emp.getEmpHeader() +
					emp.getCccRegime() +
					emp.getCccProvince() +
					emp.getCcc() +
					emp.getBusinessmanCode() +
					emp.getCccRegimePrincipal() +
					emp.getCccProvincePrincipal() +
					emp.getCccPrincipal() +
					emp.getReservedCollection() +
					emp.getAction() +
					emp.getReserved1() +
					"\r\n";
			
			RZS rzs = activity.getRzs();
			agrarianAFI +=
					rzs.getRzsHeader() +
					rzs.getRzsIndicator() +
					rzs.getBusinessmanType() +
					rzs.getRzsName() +
					rzs.getAuthKey() +
					rzs.getReserved2() +
					"\r\n";
			
			EMPLS empls = activity.getEmpls();
			for(EMPL empl : empls.getEmpls()) {
				TRA tra = empl.getTra();
				AYN ayn = empl.getAyn();
				String fab = empl.getFab();
				String odl = empl.getOdl();
				String fct = empl.getFct();
				
				agrarianAFI +=
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
				
				agrarianAFI +=
						ayn.getAynHeader() +
						ayn.getFirstSurname() +
						ayn.getSecondSurname() +
						ayn.getName() +
						ayn.getReserved12() +
						"\r\n";
				
				agrarianAFI +=
						fab +
						"\r\n";
				
				agrarianAFI +=
						odl +
						"\r\n";
				
				agrarianAFI +=
						fct +
						"\r\n";
			}
		}
		
		agrarianAFI +=
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
				etf.countLines +
				etf.getReserved3() +
				"\r\n";
		
		return agrarianAFI;
	}
}
