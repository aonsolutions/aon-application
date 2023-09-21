package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos;

public class Idc {
	
	private static final class IdcCretaListener extends CretaListener {
//		@Override
//		protected String getIpf(String naf) {
//			return cb.getIpf(naf);
//		}
//
//		@Override
//		protected TipoIpf getTipoIpf(String naf) {
//			return cb.getTipoIpf(naf);
//		}
//
//		@Override
//		protected boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
//			return cb.isPartTimeEmployee(ssNum, ccc, start, end);
//		}
//
//		@Override
//		protected boolean isScholarEmployee(String ssNum, String ccc, Date start, Date end) {
//			return cb.isScholarEmployee(ssNum, ccc, start, end);
//		}
//
//		@Override
//		protected boolean isTraining421Employee(String ssNum, String ccc, Date start, Date end) {
//			return cb.isTraining421Employee(ssNum, ccc, start, end);
//		}
	
		// -------------------------------------------------- IdcParserListener
		
		@Override
		public void onEmployeePerido(String ssNum, String ccc, Date startDate, Date endDate) {
		}
	}

	public static interface IdcListener{
		void onSSPECs(Date startDate, Date endDate, Collection<PEC> SSPecs);
		void onContractData( Date startDate, Date endDate, Map<ContextVariable,Object> contractData);
	}
	
	private static class ContractDataListener implements IdcParserListener{
		
		private Date endDate;
		private Date startDate;
		
		private Map<ContextVariable,Object> contractData = new HashMap<ContextVariable, Object>();
		
		@Override
		public void onContractType(String contractType) {
			contractData.put(ContextVariable.TC2, contractType);
		}
		
		@Override
		public void onRlce(String rlce) {
			if(AonStringUtils.containsIgnoreCase(rlce, "PRACT. NO LAB. EMP"))
				contractData.put(ContextVariable.TC2, "000");
		}

		@Override
		public void onContractOcupation(String ocupation) {
			contractData.put(ContextVariable.OCCUPATION, ocupation.toLowerCase());
		}
		
		@Override
		public void onContractPartialCoeficient(String coeficient) {
			contractData.put(ContextVariable.PARTIAL_FACTOR, Integer.parseInt(coeficient.trim())/1000.00);
		}
		
		@Override
		public void onContractQuoteGroup(String quoteGroup) {
			contractData.put(ContextVariable.QUOTE_GROUP, quoteGroup);
		}
				
		@Override
		public void onEmployeeQuoteTypes(Double it, Double ims, Double unemployment) {
			if (Objects.nonNull(it)) {
				contractData.put(ContextVariable.IT_PERCENT, it);
			}
			if (Objects.nonNull(ims)) {
				contractData.put(ContextVariable.IMS_PERCENT, ims);
			}
			
			if (Objects.nonNull(unemployment)) {
				contractData.put(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, unemployment == 7.05 ? 1.55 : 1.60);
				contractData.put(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT, unemployment == 7.05 ? 5.50 : 6.70);
			}
		}
		
		@Override
		public void onEmployeePerido(String ssNum, String ccc, Date startDate, Date endDate) {
			this.startDate = startDate;
			this.endDate = endDate;
		}
		
		public Date getEndDate() {
			return endDate;
		}

		public Date getStartDate() {
			return startDate;
		}
		
		public Map<ContextVariable, Object> getContractData() {
			return contractData;
		}
		
	}

	public static void parse(byte [] data, IdcListener idcListener ) throws IOException, UnknownPDFException {
		try ( InputStream is = new ByteArrayInputStream(data)) {
			parse(is, idcListener);
		}
	}

	public static void parse(InputStream is, IdcListener idcListener ) throws IOException, UnknownPDFException {
		PECListener  ssPecListener = new PECListener();
		ContractDataListener contractDataListener = new ContractDataListener();
		IdcCompositeParserListener idcCompositeParserListener = new IdcCompositeParserListener().add(ssPecListener).add(contractDataListener);
		IdcParser.parse(is, idcCompositeParserListener );
		idcListener.onSSPECs(contractDataListener.getStartDate(), contractDataListener.getEndDate(), ssPecListener.getSSBonuses());
		idcListener.onContractData(contractDataListener.getStartDate(), contractDataListener.getEndDate(), contractDataListener.getContractData());
	}

	public static  Map<ContextVariable,Object> getContractData(byte data []) throws IOException, UnknownPDFException {
		try ( InputStream is = new ByteArrayInputStream(data)) {
			return getContractData(is);
		}
	}

	public static  Map<ContextVariable,Object> getContractData(InputStream is) throws IOException, UnknownPDFException {
		ContractDataListener contractDataListener = new ContractDataListener();
		IdcParser.parse(is, contractDataListener);
		return contractDataListener.getContractData();
	}
	
	
	public static Collection<PEC> getSSPECs (byte pdf []) throws IOException, UnknownPDFException {
		try (InputStream is = new ByteArrayInputStream(pdf)){
			return getSSPECs(is);
		}
	}
	
	public static Collection<PEC> getSSPECs (InputStream is) throws IOException, UnknownPDFException {	
		PECListener  ssBonusListener = new PECListener();
		IdcParser.parse(is, ssBonusListener );
		return ssBonusListener.getSSBonuses();
	}
	
	public static TrabajadoresTramos getTrabajadoresTramos (InputStream is, Date startDate, Date endDate) throws IOException, UnknownPDFException {	
		CretaListener  cretaListener = new IdcCretaListener();
		IdcParser.parse(is, cretaListener );
		return cretaListener.getTrabajadoresTramos();
		
	}

	public static TrabajadoresTramos getTrabajadoresTramos (byte idcplnss [], Date startDate, Date endDate) throws IOException, UnknownPDFException {
		try ( ByteArrayInputStream is = new ByteArrayInputStream(idcplnss) ) {
			return getTrabajadoresTramos(is, startDate , endDate);
		}  
	}
	
	public static boolean isBonus(String code, String quota) {
		return PECListener.PEC_BONUS_MAP.containsKey(code) && PECListener.BONUS_QUOTA_EXPRESSION_MAP.containsKey(quota); 
	}

	public static boolean isDeduction(String code, String quota) {
		return ( PECListener.PEC_BONUS_MAP.containsKey(code) && PECListener.DEDUCTION_QUOTA_EXPRESSION_MAP.containsKey(quota))
				|| (PECListener.PEC_DEDUCTION_MAP.containsKey(code) && PECListener.DEDUCTION_QUOTA_PROVIDER_MAP.containsKey(quota)); 
	}

	public static String getBonusExpression(String code, String tipo, String quota) throws ParseException {
		return PECListener.getBonusFormula(code, tipo, quota, null, null);
	}
	
	public static String getDeductionExpression(String code, String tipo, String quota) throws ParseException {
		return PECListener.getDeductionFormula(code, tipo, quota, null, null);
	}
	
	
}
