package com.esferalia.aon.payroll.tgss.cra;

import java.util.ArrayList;
import java.util.Date;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.watson.util.AonStringUtils;

public class MainCRAGenerator {
	
	private MainCRAGenerator() {
		super();
	}
	
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
		
		@SuppressWarnings("deprecation")
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
	
	public static class CCCs{
		
		ArrayList<CCC> cccs;
		
		public CCCs() {
			super();
			this.cccs = new ArrayList<>();			
		}
		
		public void addCCC(CCC ccc) {
			this.cccs.add(ccc);
		}
		public ArrayList<CCC> getCccs() {
			return cccs;
		}
		
	}
	
	public static class CCC{
		
		DDE dde;
		DDEAS ddeas;
		FINIQ finiq;
		
		public CCC( DDE dde, DDEAS ddeas, FINIQ finiq) {
			super();
			this.dde = dde;
			this.ddeas = ddeas;
			this.finiq = finiq;
		}

		public DDE getDde() {
			return dde;
		}
		public DDEAS getDdeas() {
			return ddeas;
		}
		public FINIQ getFiniq() {
			return finiq;
		}
		
	}
	
	public static class DDE{
		String empHeader;
		String cccRegime;
		String ccc;
		String year;
		String month;
		String cccRegimeConcert;
		String cccConcert;
		String reserved31;
		ArrayList<TRB> trbs;
		
		public DDE(String cccRegime, String ccc, String year, String month) {
			super();
			this.empHeader = "DDE";
			this.cccRegime = cccRegime;
			this.ccc = ccc;
			this.year = year;
			this.month = StringUtils.leftPad(month, 2, '0');
			this.cccRegimeConcert = StringUtils.leftPad("", 4, '0');
			this.cccConcert = StringUtils.leftPad("", 11, '0');
			this.reserved31 = StringUtils.leftPad("", 31, ' ');
			this.trbs = new ArrayList<>();
		}
		
		public String getEmpHeader() {
			return empHeader;
		}
		public String getCccRegime() {
			return cccRegime;
		}
		public String getCcc() {
			return ccc;
		}
		public String getReserved31() {
			return reserved31;
		}
		public ArrayList<TRB> getTrbs() {
			return trbs;
		}
		public void setTrbs(ArrayList<TRB> trbs) {
			this.trbs = trbs;
		}
		public void addTRB(TRB trb) {
			this.trbs.add(trb);
		}
		public String getYear() {
			return year;
		}
		public String getMonth() {
			return month;
		}
		public String getCccRegimeConcert() {
			return cccRegimeConcert;
		}
		public String getCccConcert() {
			return cccConcert;
		}
		
	}
	
	public static class TRB{
		
		String trbHeader;
		String numAfilicion;
		String reserved55;
		ArrayList<CRE> cres;
		
		public TRB(String numAfilicion) {
			super();
			this.trbHeader = "TRB";
			this.numAfilicion = AonStringUtils.leftPad(numAfilicion, 12, ' ');
			this.reserved55 = AonStringUtils.leftPad("", 55, ' ');
			this.cres = new ArrayList<>();
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
		public ArrayList<CRE> getCres() {
			return cres;
		}
		public void setCres(ArrayList<CRE> cres) {
			this.cres = cres;
		}
		public void addCRE(CRE cre) {
			this.cres.add(cre);
		}
		
	}
	
	public static class CRE{
		
		String creHeader;
		String concept;
		String includeExclude;
		String amount;
		String action; // M, B, C
		String reserved52;
		
		public CRE(String concept, String includeExclude, String amount, String action) {
			super();
			this.creHeader = "CRE";
			this.concept = concept;
			this.includeExclude = includeExclude;
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
		public String getIncludeExclude() {
			return includeExclude;
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
	
	public static class DDEA{
		String empHeader;
		String cccRegime;
		String ccc;
		String year;
		String month;
		String cccRegimeConcert;
		String cccConcert;
		String reserved31;
		ArrayList<TRB> trbs;
		
		public DDEA(String cccRegime, String ccc, String year, String month) {
			super();
			this.empHeader = "DDE";
			this.cccRegime = cccRegime;
			this.ccc = ccc;
			this.year = year;
			this.month = StringUtils.leftPad(month, 2, '0');
			this.cccRegimeConcert = StringUtils.leftPad("", 4, '0');
			this.cccConcert = StringUtils.leftPad("", 11, '0');
			this.reserved31 = StringUtils.leftPad("", 31, ' ');
			this.trbs = new ArrayList<>();
		}
		
		public String getEmpHeader() {
			return empHeader;
		}
		public String getCccRegime() {
			return cccRegime;
		}
		public String getCcc() {
			return ccc;
		}
		public String getReserved31() {
			return reserved31;
		}
		public ArrayList<TRB> getTrbs() {
			return trbs;
		}
		public void setTrbs(ArrayList<TRB> trbs) {
			this.trbs = trbs;
		}
		public void addTRB(TRB trb) {
			this.trbs.add(trb);
		}
		public String getYear() {
			return year;
		}
		public String getMonth() {
			return month;
		}
		public String getCccRegimeConcert() {
			return cccRegimeConcert;
		}
		public String getCccConcert() {
			return cccConcert;
		}
		
	}
	
	public static class DDEAS{
		ArrayList<DDEA> ddeas;
		
		public DDEAS() {
			super();
			this.ddeas = new ArrayList<>();
		}
		
		public ArrayList<DDEA> getDdeas() {
			return ddeas;
		}
		public void setTrbs(ArrayList<DDEA> ddeas) {
			this.ddeas = ddeas;
		}
		public void addDDEA(DDEA ddea) {
			this.ddeas.add(ddea);
		}
		
	}
	
	public static class FINIQ{
		String empHeader;
		String cccRegime;
		String ccc;
		String year;
		String month;
		String cccRegimeConcert;
		String cccConcert;
		String reserved31;
		ArrayList<TRB> trbs;
		
		public FINIQ(String cccRegime, String ccc, String year, String month) {
			super();
			this.empHeader = "DDE";
			this.cccRegime = cccRegime;
			this.ccc = ccc;
			this.year = year;
			this.month = StringUtils.leftPad(month, 2, '0');
			this.cccRegimeConcert = StringUtils.leftPad("", 4, '0');
			this.cccConcert = StringUtils.leftPad("", 11, '0');
			this.reserved31 = StringUtils.leftPad("", 31, ' ');
			this.trbs = new ArrayList<>();
		}
		
		public String getEmpHeader() {
			return empHeader;
		}
		public String getCccRegime() {
			return cccRegime;
		}
		public String getCcc() {
			return ccc;
		}
		public String getReserved31() {
			return reserved31;
		}
		public ArrayList<TRB> getTrbs() {
			return trbs;
		}
		public void setTrbs(ArrayList<TRB> trbs) {
			this.trbs = trbs;
		}
		public void addTRB(TRB trb) {
			this.trbs.add(trb);
		}
		public String getYear() {
			return year;
		}
		public String getMonth() {
			return month;
		}
		public String getCccRegimeConcert() {
			return cccRegimeConcert;
		}
		public String getCccConcert() {
			return cccConcert;
		}
		
	}
		
	public static String generateMainCRA (JSONObject mainCRAData) {
		
		//RESULT
		String mainCRA = null;
		
		//ERROR
		JSONArray cccsArr = (JSONArray) mainCRAData.get("CCCs");
		if(cccsArr.isEmpty()) {
			return ((JSONObject)((JSONArray)((JSONObject)cccsArr.get(0)).get("ERRS")).get(0)).get("ERR").toString();
		}
		
		//ETI
		JSONObject etiJson = (JSONObject) mainCRAData.get("ETI");
		ETI eti = new ETI(
				etiJson.get("authkey").toString(), 
				null == etiJson.get("fileName") ? null : etiJson.get("fileName").toString(),
				etiJson.get("prorityCode").toString());
		
		CCCs cccs = new CCCs();
		
		for(int h=0; h<cccsArr.size(); h++) {
			
			//DDE
			JSONObject ddeJson = (JSONObject) ((JSONObject)cccsArr.get(h)).get("DDE");
			DDE dde = null;
			if(null != ddeJson) {
				dde = new DDE(
						ddeJson.get("cccRegime").toString(), 
						ddeJson.get("ccc").toString(),
						ddeJson.get("year").toString(),
						ddeJson.get("month").toString());
				
				//TRBs
				JSONArray employees = (JSONArray) ddeJson.get("TRBS");
				for(int i=0; i<employees.size(); i++) {
					JSONObject emplJson = (JSONObject) employees.get(i);
					
					//TRB
					TRB trb = new TRB(
							emplJson.get("numAfilicion").toString());
					
					//CREs
					JSONArray cres = (JSONArray) emplJson.get("CRES");
					for(int j=0; j<cres.size(); j++) {
						JSONObject creJson = (JSONObject) cres.get(j);
						
						//CRE
						CRE cre = new CRE(
								creJson.get("concept").toString(), 
								creJson.get("include_exclude").toString(), 
								creJson.get("amount").toString(), 
								creJson.get("action").toString());
						
						trb.addCRE(cre);
					}
					
					dde.addTRB(trb);
				}
			}
			
			//DDEA
			DDEAS ddeas = null;
			JSONArray ddeaArray = (JSONArray) ((JSONObject)cccsArr.get(h)).get("DDEAS");
			if(null != ddeaArray) {
				ddeas = new DDEAS();
				for(int i=0; i<ddeaArray.size(); i++){
					JSONObject ddeaJson = (JSONObject) ddeaArray.get(i);
					DDEA ddea = new DDEA(
							ddeaJson.get("cccRegime").toString(), 
							ddeaJson.get("ccc").toString(),
							ddeaJson.get("year").toString(),
							ddeaJson.get("month").toString());
					
					//TRBs
					JSONArray employeesAtrasos = (JSONArray) ddeaJson.get("TRBS");
					for(int j=0; j<employeesAtrasos.size(); j++) {
						JSONObject emplJson = (JSONObject) employeesAtrasos.get(j);
						
						//TRB
						TRB trb = new TRB(
								emplJson.get("numAfilicion").toString());
						
						//CREs
						JSONArray cres = (JSONArray) emplJson.get("CRES");
						for(int k=0; k<cres.size(); k++) {
							JSONObject creJson = (JSONObject) cres.get(k);
							
							//CRE
							CRE cre = new CRE(
									creJson.get("concept").toString(), 
									creJson.get("include_exclude").toString(), 
									creJson.get("amount").toString(), 
									creJson.get("action").toString());
							
							trb.addCRE(cre);
						}
								
						ddea.addTRB(trb);
					}
					ddeas.addDDEA(ddea);
				}
			}
			
			//FINIQ
			JSONObject finiqJson = (JSONObject) ((JSONObject)cccsArr.get(h)).get("FINIQ");
			FINIQ finiq = null;
			if(null != finiqJson){
				finiq = new FINIQ(
						finiqJson.get("cccRegime").toString(), 
						finiqJson.get("ccc").toString(),
						finiqJson.get("year").toString(),
						finiqJson.get("month").toString());
				
				//TRBs
				JSONArray employeesFiniq = (JSONArray) finiqJson.get("TRBS");
				for(int i=0; i<employeesFiniq.size(); i++) {
					JSONObject emplJson = (JSONObject) employeesFiniq.get(i);
					
					//TRB
					TRB trb = new TRB(
							emplJson.get("numAfilicion").toString());
					
					//CREs
					JSONArray cres = (JSONArray) emplJson.get("CRES");
					for(int j=0; j<cres.size(); j++) {
						JSONObject creJson = (JSONObject) cres.get(j);
						
						//CRE
						CRE cre = new CRE(
								creJson.get("concept").toString(), 
								creJson.get("include_exclude").toString(), 
								creJson.get("amount").toString(), 
								creJson.get("action").toString());
						
						trb.addCRE(cre);
					}
					
					finiq.addTRB(trb);
				}
			}
			
			// Create CCC
			CCC ccc = new CCC(dde, ddeas, finiq);
			cccs.addCCC(ccc);
		}
		
		if(cccs.getCccs().isEmpty() || (cccs.getCccs().size() == 1 && cccs.getCccs().get(0).getDde() == null && cccs.getCccs().get(0).getDdeas() == null && cccs.getCccs().get(0).getFiniq() == null)) {
			String errorMessage = "";
			
			JSONArray errorsArr = (JSONArray) ((JSONObject)cccsArr.get(0)).get("ERRS");
			if(null != errorsArr) {
				for(int i = 0; i < errorsArr.size(); i++) {
					JSONObject error = (JSONObject) errorsArr.get(i);
					for (Object key : error.keySet()) {
			            String e = error.get(key).toString();
			            errorMessage += e + "\n";
			        }
				}
			}
			
			throw new IllegalArgumentException(errorMessage);
		} else {
			mainCRA = createMainCRA(eti, cccs);
			
			return mainCRA;
		}
		
	}

	private static String createMainCRA(ETI eti, CCCs cccs) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(
				eti.getEtiHeader() +
				eti.getSintaxIndent() +
				eti.getAuthKey() +
				eti.getReserved8() + 
				eti.getYear() +
				eti.getMonth() +
				eti.getDay() +
				eti.getHour() +
				eti.getMinute() +
				eti.getFileName() +
				eti.getSufixCRA() + 
				eti.getPriorityCode() +
				eti.getTest() +
				eti.getRegistryIdent() +
				eti.getReservedTGSS() +
				eti.getReserved1() +
				"\r\n"
			);
		
			for(CCC ccc: cccs.getCccs()) {
			
			DDE dde = ccc.getDde();
				
			if(null != dde) {
				builder.append(
						dde.getEmpHeader() +
						dde.getCccRegime() +
						dde.getCcc() +
						dde.getYear() +
						dde.getMonth() +
						dde.getCccRegimeConcert() +
						dde.getCccConcert() +
						dde.getReserved31() +
						"\r\n"
				);
				
				for(TRB trb : dde.getTrbs()) {
					builder.append(
							trb.getTrbHeader() +
							trb.getNumAfilicion() +
							trb.getReserved55() +
							"\r\n"
					);
					
					for( CRE cre : trb.getCres()) {
						builder.append(
								cre.getCreHeader() +
								cre.getConcept() +
								cre.getIncludeExclude() +
								cre.getAmount() +
								cre.getAction() +
								cre.getReserved52() +
								"\r\n"
						);
					}
				}
			}
			
			FINIQ finiq = ccc.getFiniq();
			
			if(null != finiq){
				builder.append(
						finiq.getEmpHeader() +
						finiq.getCccRegime() +
						finiq.getCcc() +
						finiq.getYear() +
						finiq.getMonth() +
						finiq.getCccRegimeConcert() +
						finiq.getCccConcert() +
						finiq.getReserved31() +
						"\r\n"
				);
				
				for(TRB trb : finiq.getTrbs()) {
					builder.append(
							trb.getTrbHeader() +
							trb.getNumAfilicion() +
							trb.getReserved55() +
							"\r\n"
					);
					
					for( CRE cre : trb.getCres()) {
						builder.append(
								cre.getCreHeader() +
								cre.getConcept() +
								cre.getIncludeExclude() +
								cre.getAmount() +
								cre.getAction() +
								cre.getReserved52() +
								"\r\n"
						);
					}
				}
			}
			
			DDEAS ddeas = ccc.getDdeas();
			
			if(null != ddeas){
				for(DDEA ddea: ddeas.getDdeas()){
					builder.append(
							ddea.getEmpHeader() +
							ddea.getCccRegime() +
							ddea.getCcc() +
							ddea.getYear() +
							ddea.getMonth() +
							ddea.getCccRegimeConcert() +
							ddea.getCccConcert() +
							ddea.getReserved31() +
							"\r\n"
					);
					
					for(TRB trb : ddea.getTrbs()) {
						builder.append(
								trb.getTrbHeader() +
								trb.getNumAfilicion() +
								trb.getReserved55() +
								"\r\n"
						);
						
						for( CRE cre : trb.getCres()) {
							builder.append(
									cre.getCreHeader() +
									cre.getConcept() +
									cre.getIncludeExclude() +
									cre.getAmount() +
									cre.getAction() +
									cre.getReserved52() +
									"\r\n"
							);
						}
					}
				}
			}
		}
		
		return builder.toString();
	}
}
