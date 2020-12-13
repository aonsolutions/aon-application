package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class Idc {
	
	private static class ContractDataListener implements IdcListener{
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
				
		public Map<ContextVariable, Object> getContractData() {
			return contractData;
		}
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
}
