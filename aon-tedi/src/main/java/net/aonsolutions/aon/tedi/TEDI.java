package net.aonsolutions.aon.tedi;

import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.baloo.Tedi;
import es.translogia.tedi.baloo.TediException;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceStatus;

public class TEDI {
	private static Logger LOGGER = Logger.getLogger(TEDI.class.getName());

	private TEDI() {
	}

	public static Tedi getTedi(AONContext ctx, boolean snapshot) throws TediException {
		String tediTokenParam = snapshot?AppParam.TEDI_SNAPSHOT_TOKEN.getValue():AppParam.TEDI_TOKEN.getValue();
		Domain domain = AON.getDomain(ctx.getDomainName(), ctx.getDomainId(),ctx.getUser());
		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), "", DataResponseSource.TEDI_INVOICE,
				f -> f.getCodeProperty().eq(tediTokenParam)
				.and(f.getDomainProperty().eq(domain.getId())));
		if(dr != null && dr.getId() != null) {
			DataResponseDetail drd = AON.getDataResponseDetail(domain.getName(), domain.getId(), "", 
				f -> f.getDataResponseProperty().eq(dr.getId())
					.and(f.getDataVariableProperty().eq(tediTokenParam)))
				.orElse(new DataResponseDetail());
		
			return Tedi.login(drd.getDataValue(),snapshot);
		}
		throw new TediException(
				"No se han definido parámetros válidos para conectarse a tEDI Center");
	}
	
	public static LinkedList<TediResult> getVerifiedInvoices(String domainName, int domain, boolean snapshot, String user)
			throws TediException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getVerifiedInvoices(ctx , snapshot);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<TediResult> getVerifiedInvoices(AONContext ctx, boolean snapshot) throws TediException {
		Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
		if (company == null) {
			throw new TediException(
					"No se ha encontrado una compa\u00F1ia v\u00E1lida para el dominio " + ctx.getDomainId());
		}
		if (AonStringUtils.isEmpty(company.getDocument())) {
			throw new TediException(
					"No se ha indicado un NIF/CIF/DNI v\u00E1lido para la compa\u00F1ia (Configuraci\u00F3n global)");
		}
		Tedi tedi = getTedi(ctx, snapshot);
		LOGGER.info("[TEDI] Attempt to recover verified invoices for [" + company.getDocument() + "]");
		LinkedList<TediInvoice> invoices = tedi.getVerifiedInvoices(company.getDocument());
		if (invoices != null && invoices.size() > 0) {
			final AonConfiguration aonCtx = ConfigurationDAO.getConfiguration(ctx);
			return invoices.stream().map(inv -> TediParser.toFullInvoice(ctx, aonCtx, inv))
					.collect(Collectors.toCollection(LinkedList::new));
		}
		return null;
	}

	public static TediResult getInvoice(String domainName, int domain, boolean snapshot, String user, String uuid) throws TediException {
		if (AonStringUtils.isEmpty(uuid)) {
			throw new TediException("No se ha indicado un identificador de factura que recuperar");
		}
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			Company company = CompanyDAO.getCompany(ctx, domain);
			if (company == null) {
				throw new TediException("No se ha encontrado una compa\u00F1ia v\u00E1lida para el dominio " + domain);
			}
			if (AonStringUtils.isEmpty(company.getDocument())) {
				throw new TediException(
						"No se ha indicado un NIF/CIF/DNI v\u00E1lido para la compa\u00F1ia (Configuraci\u00F3n global)");
			}
			Tedi tedi = getTedi(ctx, snapshot);
			LOGGER.info("[TEDI] Attempt to recover invoice [" + company.getDocument() + "," + uuid + "]");
			TediInvoice invoice = tedi.getInvoice(company.getDocument(), uuid);
			if (invoice != null) {
				final AonConfiguration aonCtx = ConfigurationDAO.getConfiguration(ctx);
				return TediParser.toFullInvoice(ctx, aonCtx, invoice);
			}
			return null;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getInvoiceAttach(String domainName, int domain, boolean snapshot, String user, String uuid) throws TediException {
		if (AonStringUtils.isEmpty(uuid)) {
			throw new TediException("No se ha indicado un identificador de factura que recuperar");
		}
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			Company company = CompanyDAO.getCompany(ctx, domain);
			if (company == null) {
				throw new TediException("No se ha encontrado una compa\u00F1ia v\u00E1lida para el dominio " + domain);
			}
			if (AonStringUtils.isEmpty(company.getDocument())) {
				throw new TediException(
						"No se ha indicado un NIF/CIF/DNI v\u00E1lido para la compa\u00F1ia (Configuraci\u00F3n global)");
			}
			Tedi tedi = getTedi(ctx, snapshot);
			LOGGER.info("[TEDI] Attempt to recover invoice [" + company.getDocument() + "," + uuid + "]");
			String url = tedi.getInvoiceAttach(company.getDocument(), uuid);
			if (url != null) {
				return url;
			}
			return null;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static TediResult putInvoice(String domainName, int domain, boolean snapshot, String user, TediInvoice invoice)
			throws TediException {
		if (invoice == null) {
			throw new TediException("No se ha indicado una factura");
		}
		if (AonStringUtils.isBlank(invoice.getCompany())) {
			throw new TediException("La factura no tiene el atributo compa\u00F1ia");
		}
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			final AonConfiguration aonCtx = ConfigurationDAO.getConfiguration(ctx);
			Tedi tedi = getTedi(ctx, snapshot);
			invoice.setOldStatus( invoice.getStatus() );
			invoice.setStatus( TediInvoiceStatus.accepted );
			
			LOGGER.info("[TEDI] Attempt to put invoice [" + invoice.getCompany() + "," + invoice.getUuid() + "]");
			return TediParser.toFullInvoice(ctx, aonCtx, tedi.putInvoice(invoice));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static TediResult validateInvoice(String domainName, int domain, String user, TediResult result) {
		result.clearMessages();
		TediValidator.validateInvoice(result);
		return result;
	}
	

	
	public static LinkedList<TediResult> putInvoices(String domainName, int domain, boolean snapshot, String user,
			LinkedList<TediResult> invoices) throws TediException {
		if (invoices == null) {
			throw new TediException("No se han indicado una facturas");
		}
		AONContext ctx = null;
		LinkedList<TediResult> returned = new LinkedList<TediResult>();	
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			Tedi tedi = getTedi(ctx,snapshot);
			final AonConfiguration aonCtx = ConfigurationDAO.getConfiguration(ctx);
			for (TediResult result : invoices) {
				TediInvoice inv = result.getTedi();
				if (AonStringUtils.isBlank(inv.getCompany())) {
					throw new TediException("La factura no tiene el atributo compa\u00F1ia");
				}
				inv.setOldStatus( inv.getStatus() );
				inv.setStatus( TediInvoiceStatus.accepted );
				if ( result.getAccountingInvoice() != null) {
					Invoice aonInvoice = result.getAccountingInvoice().getInvoice();
					LOGGER.info("[TEDI] Attempt to put invoice [" + aonInvoice.getType() 
						+ "," + aonInvoice.getSeries()
						+ "," + aonInvoice.getNumber()
						+ "," + aonInvoice.getReferenceCode()
						+ "," + aonInvoice.getRegistryDocument()
						+ "," + aonInvoice.getRegistryName()
						+ "]");
					AccountingInvoiceDAO.save(ctx, aonCtx, result.getAccountingInvoice());
				}
				tedi.putInvoice(inv);
				returned.add(result);
			}
		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
		return returned;
	}
}
