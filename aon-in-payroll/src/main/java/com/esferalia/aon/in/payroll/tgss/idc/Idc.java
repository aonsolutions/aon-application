package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonNumberUtils;
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
		void onContractData( Date startDate, Date endDate, Map<ContextVariable,Collection<IdcContractData>> contractData);
	}

	public static record IdcContractData (Object data, Date startDate, Date endDate) {}; 
	
	private static class ContractDataListener implements IdcParserListener{
		
		private static final NumberFormat NUMBER_FORMAT = DecimalFormat.getNumberInstance(Locale.of("es", "ES"));

		private Date endDate;
		private Date startDate;
		
		
		private Map<ContextVariable,Collection<IdcContractData>> contractData = new HashMap<>();
		
		@Override
		public void onContractType(String contractType) {
			contractData.computeIfAbsent(ContextVariable.TC2, k -> new ArrayList<>()).add( new IdcContractData(contractType, startDate, endDate));
		}
		
		@Override
		public void onRlce(String rlce) {
			if(AonStringUtils.containsIgnoreCase(rlce, "PRACT. NO LAB. EMP"))
				contractData.computeIfAbsent(ContextVariable.TC2, k -> new ArrayList<>()).add( new IdcContractData("000", startDate, endDate));
		}

		@Override
		public void onContractOcupation(String ocupation) {
			contractData.computeIfAbsent(ContextVariable.OCCUPATION, k -> new ArrayList<>()).add( new IdcContractData(ocupation.toLowerCase(), startDate, endDate));
		}
		
		@Override
		public void onContractPartialCoeficient(String coeficient) {
			contractData.computeIfAbsent(ContextVariable.PARTIAL_FACTOR, k -> new ArrayList<>()).add( new IdcContractData(AonNumberUtils.todouble(coeficient)/1000.00, startDate, endDate) );
		}
		
		@Override
		public void onContractQuoteGroup(String quoteGroup) {
			contractData.computeIfAbsent(ContextVariable.QUOTE_GROUP, k -> new ArrayList<>()).add( new IdcContractData(quoteGroup, startDate, endDate) );
		}
				
		@Override
		public void onEmployeeQuoteTypes(Double it, Double ims, Double unemployment) {
			if (Objects.nonNull(it)) {
				contractData.computeIfAbsent(ContextVariable.IT_PERCENT, k -> new ArrayList<>()).add( new IdcContractData(it, startDate, endDate) );
			}
			if (Objects.nonNull(ims)) {
				contractData.computeIfAbsent(ContextVariable.IMS_PERCENT, k -> new ArrayList<>()).add( new IdcContractData(ims, startDate, endDate) );
			}
			
			if (Objects.nonNull(unemployment) && unemployment == 7.05 ) {
				contractData.computeIfAbsent(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, k -> new ArrayList<>()).add( new IdcContractData(1.55, startDate, endDate) );
				contractData.computeIfAbsent(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT, k -> new ArrayList<>()).add(new IdcContractData( 5.50 , startDate, endDate));
			} else if (Objects.nonNull(unemployment) && unemployment == 8.30 ) {
				contractData.computeIfAbsent(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, k -> new ArrayList<>()).add( new IdcContractData( 1.60, startDate, endDate) );
				contractData.computeIfAbsent(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT, k -> new ArrayList<>()).add( new IdcContractData(6.70, startDate, endDate));
			} else if (Objects.nonNull(unemployment) && unemployment == 0.00 ) {
				contractData.computeIfAbsent(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, k -> new ArrayList<>()).add( new IdcContractData( 0.00, startDate, endDate) );
				contractData.computeIfAbsent(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT, k -> new ArrayList<>()).add( new IdcContractData(0.00, startDate, endDate));
			}
		}
		
		@Override
		public void onEmployeePerido(String ssNum, String ccc, Date startDate, Date endDate) {
			this.startDate = startDate;
			this.endDate = endDate;
		}
		
		@Override
		public void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo,
				String quota, Date start, Date end) {
			// 06 DECREMENTO DE TIPOS  03 CONT.COMUN-C.EMPRESA
			if ( AonStringUtils.equals(code, "06") && AonStringUtils.equals(quota, "03") ) {
					try {
						double percent = NUMBER_FORMAT.parse(portTipo).doubleValue();
						contractData.computeIfAbsent(ContextVariable.CGC_ENTERPRISE_PERCENT, k -> new ArrayList<>()).add(
						new IdcContractData(new Object() { 
							@Override
							public String toString() {
								return String.format(Locale.ROOT,"%s - %.2f", ContextVariable.CGC_ENTERPRISE_PERCENT, percent );
							} 
						}, start, end));
					} catch (ParseException e) {
					}
			}
		}

		
		public Date getEndDate() {
			return endDate;
		}

		public Date getStartDate() {
			return startDate;
		}
		
		public Map<ContextVariable, Collection<IdcContractData>> getContractData() {
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

	public static  Map<ContextVariable,Collection<IdcContractData>> getContractData(byte data []) throws IOException, UnknownPDFException {
		try ( InputStream is = new ByteArrayInputStream(data)) {
			return getContractData(is);
		}
	}

	public static  Map<ContextVariable,Collection<IdcContractData>> getContractData(InputStream is) throws IOException, UnknownPDFException {
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
	
	public static Collection<EmployeeIT> getEmployeeITs (byte pdf []) throws IOException, UnknownPDFException {
		try (InputStream is = new ByteArrayInputStream(pdf)){
			return getEmployeeITs(is);
		}
	}
	
	public static Collection<EmployeeIT> getEmployeeITs (InputStream is) throws IOException, UnknownPDFException {	
		EmployeeITListener  itListener = new EmployeeITListener();
		
		IdcParser.parse(is, itListener );
		
		return itListener.getEmployeeITs();
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
