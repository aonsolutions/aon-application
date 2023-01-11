package net.aonsolutions.aon.invoice.communication;

public interface ICommunicationInvoiceVisitor {

	void alta(CommunicationInvoice invoice) throws Exception;
	void modificacion(CommunicationInvoice invoice) throws Exception;
	void baja(CommunicationInvoice invoice) throws Exception;
	
	public static final String LOGIN = "system";
}
