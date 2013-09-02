package com.esferalia.aon.ui.sepe.utils;


public interface ISepeCommunicator {
	
	public void initialize();
	
	public boolean isLoginRequired();
	
	public void setDataCommunication(boolean dataCommunication);
	
	public void setDataQuery(boolean dataCommunication);
	
	public void setDocument(String document);
	
	public String communicate();

	public String obtainCommunicationNumber(byte[] data);
		
	
}
