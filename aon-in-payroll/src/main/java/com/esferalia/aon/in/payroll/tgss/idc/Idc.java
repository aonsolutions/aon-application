package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class Idc {
	
	public static interface IdcListener{
		void onSSPECs(Collection<PEC> SSPecs);
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
				contractData.put(ContextVariable.IT_RATE, it);
			}
			if (Objects.nonNull(ims)) {
				contractData.put(ContextVariable.IMS_RATE, ims);
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
		PECListener  ssBonusListener = new PECListener();
		ContractDataListener contractDataListener = new ContractDataListener();
		IdcCompositeParserListener idcCompositeParserListener = new IdcCompositeParserListener().add(ssBonusListener).add(contractDataListener);
		IdcParser.parse(is, idcCompositeParserListener );
		idcListener.onSSPECs(ssBonusListener.getSSBonuses());
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
	
}
