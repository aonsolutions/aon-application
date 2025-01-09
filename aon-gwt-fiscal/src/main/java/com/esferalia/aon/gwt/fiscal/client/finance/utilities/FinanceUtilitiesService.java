package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/FinanceUtilities")
public interface FinanceUtilitiesService extends RemoteService {
	
	Domain getDomain(Occam occam) throws AonCoreException;
	LinkedList<Domain> getChildDomains(Occam occam) throws AonCoreException;
	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;

	// Chequeo de facturas sin vencimientos
	FinanceUtilitiesResult missingFinanceInvoices(Occam occam, Domain domain, FinanceUtilitiesParams params) throws AonCoreException;
	Invoice missingFinanceInvoicesFix(Occam occam, Integer invoice) throws AonCoreException;

	// Chequeo de integridad de vencimientos en facturas 
	FinanceUtilitiesResult financeInvoiceIntegrity(Occam occam, Domain domain) throws AonCoreException;
	Finance financeInvoiceIntegrityFix(Occam occam, Finance finance) throws AonCoreException;

	// Chequeo de tipos de retenci\u00F3n en facturas.
	void updateWithholdingType(Occam occam, Integer invoiceId, WithholdingType newType ) throws AonCoreException;
	
	// Modificación de actividades en facturas.
	void updateActivity(Occam occam, Integer invoiceId, Integer activity) throws AonCoreException;

	//Integridad del dato actividad en facturas y asientos.
	FinanceUtilitiesResult activityIntegrity(Occam occam, Integer domain) throws AonCoreException;
	void activityIntegrityFix(Occam occam, Integer invoiceId, boolean useInvoiceActivity) throws AonCoreException;
	
}
