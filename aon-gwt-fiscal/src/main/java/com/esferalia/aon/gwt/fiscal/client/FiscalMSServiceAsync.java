package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalMSServiceAsync {

	// ---------------------------------- COMPANY
	void getCompanyBanks(String domainName, String user, int domain, AsyncCallback<LinkedList<CompanyBank>> callback);
	// -------------------------------------------------------------- CREDITOR
	void getBasicCreditors(Occam occam, String query, AsyncCallback<LinkedList<Creditor>> callback);
	
	// Para carga del PDF del modelo 
	void savePDFModel(Occam occam, IFiscalModel model, String data, AsyncCallback<Void> callback);
	
	// Para envio de email
	void sendEmail(Occam occam, IFiscalModel model, AsyncCallback<Void> callback);
	
	// Para obtener informacion del certificado 
	void getCertificateInfo(String domainName, int domainId, String user, Integer certificateId, AsyncCallback<CertificateInfo> callback);

}
