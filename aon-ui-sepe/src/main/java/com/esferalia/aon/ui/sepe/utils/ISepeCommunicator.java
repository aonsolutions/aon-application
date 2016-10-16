package com.esferalia.aon.ui.sepe.utils;

import com.esferalia.aon.payroll.Contract;



public interface ISepeCommunicator {
	
	public String BGCOLOR_PROCESSED = "#E0F8E0";
	public String BGCOLOR_PROCESSED_PARTIALLY = "#F6E3CE";
	public String BGCOLOR_REFUSED = "#F8E0E0";
	public String BGCOLOR_OTHER = "#E4E4E4";
	
	public void initialize();
	
	public boolean isLoginRequired();
	
	public void setDataCommunication(boolean dataCommunication);
	
	public void setDataQuery(boolean dataCommunication);
	
	public void setDocument(String document);
	
	public String communicate();

	public String obtainCommunicationNumber(byte[] data);
		
	public String obtainCommunicationStatus(byte[] data, Contract contract);
	
	public boolean isCommunicationAccepted(byte[] data);

	public boolean isCommunicationFinished(byte[] data);
	
}
