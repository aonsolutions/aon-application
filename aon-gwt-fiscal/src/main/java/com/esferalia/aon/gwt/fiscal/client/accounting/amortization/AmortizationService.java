package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.accounting.AmortizationInvoice;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Amortization")
public interface AmortizationService extends RemoteService {

	Amortization get(Occam occam, Integer domain, Integer id) throws AonCoreException;
	LinkedList<Amortization> get(Occam occam, AmortizationParams params) throws AonCoreException;
	Amortization save(Occam occam, Amortization am) throws AonCoreException;
	Amortization saveFiscalAllocation (Occam occam, AmortizationDetail detail) throws AonCoreException;
	void delete(Occam occam, Amortization am) throws AonCoreException;
	Amortization sale(Occam occam, Amortization am) throws AonCoreException;
	Amortization calculate(Occam occam, Amortization am) throws AonCoreException;
	
	AmortizationDetail recordAllocation(Occam occam, Amortization am, AmortizationDetail detail) throws AonCoreException;
	AmortizationDetail unrecordAllocation(Occam occam, AmortizationDetail detail) throws AonCoreException;
	
	AmortizationDetail blockDetail(Occam occam, AmortizationDetail detail) throws AonCoreException;
	AmortizationDetail unblockDetail(Occam occam, AmortizationDetail detail) throws AonCoreException;
	
	LinkedList<AmortizationInvoice> getInvoices(Occam occam, Integer domain, Integer amortizationId) throws AonCoreException;
}
