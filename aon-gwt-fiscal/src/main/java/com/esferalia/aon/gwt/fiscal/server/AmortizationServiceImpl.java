package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.accounting.AccountingAmortization;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetailFlat;
import com.esferalia.aon.occam.api.model.accounting.AmortizationInvoice;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Amortization Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Amortization" })
public class AmortizationServiceImpl extends AonStatelessRemoteServiceServlet implements AmortizationService {

	private static final long serialVersionUID = -7723849685842294618L;

	@Override
	public Amortization get(Occam occam, Integer domain, Integer id) throws AonCoreException {
		return ACCOUNTING.getAmortization(occam, domain, id)
			.orElseThrow(() -> new AonCoreException("Fiche de amortizaci\u00F3n no encontrada "));
	}

	@Override
	public LinkedList<Amortization> get(Occam occam, AmortizationParams params) throws AonCoreException {
		return ACCOUNTING.getAmortizations(occam, params);
	}

	@Override
	public LinkedList<AmortizationDetailFlat> getFlat(Occam occam, AmortizationParams params) throws AonCoreException {
		return ACCOUNTING.getFlatAmortizations(occam, params);
	}

	@Override
	public Amortization saveFiscalAllocation(Occam occam, AmortizationDetail detail) throws AonCoreException {
		return ACCOUNTING.saveFiscalAllocation(occam, detail);
	}
	
	@Override
	public Amortization save(Occam occam, Amortization am) throws AonCoreException {
		return ACCOUNTING.saveAmortization(occam, am);
	}
	
	@Override
	public Amortization sale(Occam occam, Amortization am) throws AonCoreException {
		return ACCOUNTING.saleAmortization(occam, am);
	}

	@Override
	public Amortization calculate(Occam occam, Amortization am) throws AonCoreException {
		return ACCOUNTING.calculateAmortization(occam, am);
	}
	
	@Override
	public void delete(Occam occam, Amortization am) throws AonCoreException {
		ACCOUNTING.deleteAmortization(occam, am);
	}
	
	@Override
	public AmortizationDetail recordAllocation(Occam occam, Amortization am, AmortizationDetail detail) throws AonCoreException {
		return ACCOUNTING.recordAmortizationAllocation(occam, am, detail);
	}
	
	@Override
	public AmortizationDetail unrecordAllocation(Occam occam, AmortizationDetail detail) throws AonCoreException {
		return ACCOUNTING.unrecordAmortizationAllocation(occam, detail);
	}
	
	@Override
	public AmortizationDetail blockDetail(Occam occam, AmortizationDetail detail) throws AonCoreException {
		return ACCOUNTING.blockAmortizationDetail(occam, detail);
	}

	@Override
	public AmortizationDetail unblockDetail(Occam occam, AmortizationDetail detail) throws AonCoreException {
		return ACCOUNTING.unblockAmortizationDetail(occam, detail);
	}

	@Override
	public LinkedList<AmortizationInvoice> getInvoices(Occam occam, Integer domain, Integer amortizationId) throws AonCoreException {
		return ACCOUNTING.getAmortizationInvoices(occam, domain, amortizationId);
	}

	@Override
	public void linkInvoices(Occam occam, Integer domain, Integer amortizationId, Integer[] invoiceIds) throws AonCoreException {
		ACCOUNTING.linkAmortizationInvoices(occam, domain, amortizationId, invoiceIds);
	}
	
	@Override
	public void unlinkInvoice(Occam occam, Integer domain, Integer amortizationId, Integer invoiceId) throws AonCoreException {
		ACCOUNTING.unlinkAmortizationInvoice(occam, domain, amortizationId, invoiceId);
	}

	@Override
	public Invoice changeInvestment(Occam occam, Integer domain, Integer invoiceId) throws AonCoreException {
		return ACCOUNTING.changeInvestment(occam, domain, invoiceId);
	}

	@Override
	public LinkedList<AccountingAmortization> getAccountingAmortizations(Occam occam, Integer domain, AmortizationParams params) throws AonCoreException {
		return ACCOUNTING.getAccountingAmortizations(occam, domain, params);
	}

	@Override
	public void recordAmortizationDetails(Occam occam, Integer domain, Integer[] ids) throws AonCoreException {
		ACCOUNTING.recordAmortizationDetails(occam, domain, ids);
	}

	@Override
	public void unrecordAmortizationDetails(Occam occam, Integer domain, Integer[] ids) throws AonCoreException {
		ACCOUNTING.unrecordAmortizationDetails(occam, domain, ids);
	}
	
}
