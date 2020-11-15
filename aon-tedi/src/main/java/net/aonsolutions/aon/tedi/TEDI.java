package net.aonsolutions.aon.tedi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RawdocDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.pdf.InvoicePDFParser;
import solutions.aon.in.invoice.tedi.TediInvoiceBuilder;
import solutions.aon.in.invoice.tedi.TediInvoiceContext;

public class TEDI {
	private static Logger LOGGER = Logger.getLogger(TEDI.class.getName());

	private TEDI() {
	}
	
	public static TediResult validateInvoice(TediContext tctx, TediResult result) throws TediException {
		if (tctx == null) throw new IllegalArgumentException("TediContext can not be null");
		boolean mustCloseCtx =  tctx.getAONContext() != null; 
		AONContext ctx = tctx.getAONContext();
		try {
			result.clearMessages();
			if (ctx == null) {
				if (tctx.getDomainName() == null) throw new IllegalArgumentException("TediContext.domainName can not be null");		
				if (tctx.getDomain() == null) throw new IllegalArgumentException("TediContext.domain can not be null");
				if (tctx.getUser() == null) throw new IllegalArgumentException("TediContext.user can not be null");
				ctx = AONContext.getAONContext(tctx.getDomainName(), tctx.getDomain(), tctx.getUser());
			}
			TediValidator.validateInvoice(ctx,result);
		} catch (Throwable t) {
			throw new TediException(t.getMessage());
		} finally {
			if (ctx != null && mustCloseCtx)
				ctx.close();
		}
		return result;
	}

	public static TediResult parse(TediContext tctx, InputStream input) throws TediException {
		if (tctx == null) throw new IllegalArgumentException("TediContext can not be null");
		boolean mustCloseCtx =  tctx.getAONContext() == null; 
		AONContext ctx = tctx.getAONContext();
		try {
			if (ctx == null) {
				if (tctx.getDomainName() == null) throw new IllegalArgumentException("TediContext.domainName can not be null");		
				if (tctx.getDomain() == null) throw new IllegalArgumentException("TediContext.domain can not be null");
				if (tctx.getUser() == null) throw new IllegalArgumentException("TediContext.user can not be null");
				ctx = AONContext.getAONContext(tctx.getDomainName(), tctx.getDomain(), tctx.getUser());
			}
			AonConfiguration aonCtx = (tctx.getAonConfiguration() == null)
				?aonCtx = ConfigurationDAO.getConfiguration(ctx)
				:tctx.getAonConfiguration();
			if (aonCtx == null || aonCtx.getCompany() == null) {
				throw new TediException("No se ha encontrado una compa\u00F1ia v\u00E1lida " 
					+ "para el dominio " + "( " + ctx.getDomainId() + " - " + ctx.getDomainName() +")");
			}
			TediInvoiceContext tictx = new TediInvoiceContext()
					.setDocument(aonCtx.getCompany().getDocument())
					.setName(aonCtx.getCompany().getName());
			LOGGER.info("[TEDI] Attempt to parse document for [" + tictx.getDocument() + ", " + tictx.getName() + "]");
			TediInvoiceBuilder tediInvoiceBuilder = new TediInvoiceBuilder( tictx );
			InvoicePDFParser.parse(input, tediInvoiceBuilder);
			TediResultBuilder.build( tediInvoiceBuilder.getInvoice() );
			TediResult result = TediParser.toFullInvoice(ctx, aonCtx, tediInvoiceBuilder.getInvoice()); 
			return result;
		} catch (IOException e) {
			throw new TediException(e.getMessage());
		} catch (UnknownInvoiceException e) {
			throw new TediException(e.getMessage());
		} finally {
			if (ctx != null && mustCloseCtx)
				ctx.close();
		}
	}
	
	public static TediResult fromRawdoc(TediContext tctx, int rawdocId) throws TediException {
		if (tctx == null) throw new IllegalArgumentException("TediContext can not be null");
		boolean mustCloseCtx =  tctx.getAONContext() == null; 
		AONContext ctx = tctx.getAONContext();
		try {
			if (ctx == null) {
				if (tctx.getDomainName() == null) throw new IllegalArgumentException("TediContext.domainName can not be null");		
				if (tctx.getDomain() == null) throw new IllegalArgumentException("TediContext.domain can not be null");
				if (tctx.getUser() == null) throw new IllegalArgumentException("TediContext.user can not be null");
				ctx = AONContext.getAONContext(tctx.getDomainName(), tctx.getDomain(), tctx.getUser());
				tctx.setAONContext(ctx);
			}
			if (tctx.getAonConfiguration() == null) {
				tctx.setAonConfiguration(ConfigurationDAO.getConfiguration(ctx));
			} 
			if (tctx.getAonConfiguration() == null || tctx.getAonConfiguration().getCompany() == null) {
				throw new TediException("No se ha encontrado una compa\u00F1ia v\u00E1lida "
					+ "para el dominio " + "( " + ctx.getDomainId() + " - " + ctx.getDomainName() +")");
			}
			Rawdoc rawdoc = RawdocDAO.get(ctx, rawdocId);
			if (rawdoc == null) {
				throw new TediException("No se ha encontrado el documento " + rawdocId + "en el dominio " + "( " + ctx.getDomainId() + " - " + ctx.getDomainName() +")");
			}
			TediResult result = null;
			if ( AonStringUtils.isBlank( rawdoc.getJson() )) {
				rawdoc = RawdocDAO.getFull(ctx, rawdocId);
				result = parse(tctx, new ByteArrayInputStream(rawdoc.getData()));
			} else {
				result = TediParser.toFullInvoice(ctx, tctx.getAonConfiguration(), rawdoc.getTediInvoice());
			}
			Attach attach = new Attach();
			attach.setId(rawdoc.getId());
			attach.setAttachType(AttachType.INVOICE);
			attach.setMimeType( rawdoc.getMimeType());
			attach.setData(rawdoc.getData());
			attach.setAttachURL("RAWDOC");
			result.getAccountingInvoice().setAttach(attach);
			return result;
		} catch (Throwable e) {
			throw new TediException(e.getMessage());
		} finally {
			if (ctx != null && mustCloseCtx)
				ctx.close();
		}
	}
	
/*
	public static Tedi getTedi(AONContext ctx, boolean snapshot) throws TediException {
		String tediTokenParam = snapshot?AppParam.TEDI_SNAPSHOT_TOKEN.getValue():AppParam.TEDI_TOKEN.getValue();
		Domain domain = AON.getDomain(ctx.getDomainName(), ctx.getDomainId(),ctx.getUser());
		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), "", DataResponseSource.TEDI_INVOICE,
				f -> f.getCodeProperty().eq(tediTokenParam)
				.and(f.getDomainProperty().eq(0)));
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
	
	public static LinkedList<TediResult> getVerifiedInvoices(String domainName, int domain, boolean snapshot, String user, Company company)
			throws TediException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return company != null ? getVerifiedInvoices(ctx , snapshot, company) :getVerifiedInvoices(ctx , snapshot);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Integer getCountInvoices(String domainName, int domain, boolean snapshot, String user, Company company, TediInvoiceStatus status)
			throws TediException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return company != null ? getCountInvoices(ctx , snapshot, company, status) : 0;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Integer getCountInvoices(AONContext ctx, boolean snapshot, Company company, TediInvoiceStatus status) throws TediException {
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
		Integer count = tedi.getCountInvoices(company.getDocument(), status);
		return count;
	}
	
	public static LinkedList<TediResult> getVerifiedInvoices(AONContext ctx, boolean snapshot) throws TediException {
		Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
		return getVerifiedInvoices(ctx, snapshot, company);
	}
	
	public static LinkedList<TediResult> getVerifiedInvoices(AONContext ctx, boolean snapshot, Company company) throws TediException {
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
	
	public static TediResult getInvoice(String domainName, int domain, boolean snapshot, String user, String uuid, String status) throws TediException {
		if (AonStringUtils.isEmpty(uuid)) {
			throw new TediException("No se ha indicado un identificador de factura que recuperar");
		}
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			if (status == null) {
				throw new TediException("No se ha indicado el Status de la factura");
			}
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
			TediInvoice invoice = tedi.getInvoice(status,company.getDocument(), uuid);
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
			LOGGER.info("[TEDI] Attempt to recover invoice attach [" + company.getDocument() + "," + uuid + "]");
			String url = tedi.getInvoiceAttach(company.getDocument(), uuid);
			LOGGER.info("[TEDI] URL for "+ uuid +" [" + url + "]");
			if (url != null) {
				return url;
			}
			return null;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	private static void ensureInvoice(TediInvoice invoice) throws TediException {
		if (invoice == null) {
			throw new TediException("No se ha indicado una factura");
		}
		if (AonStringUtils.isBlank(invoice.getCompany())) {
			throw new TediException("La factura no tiene el atributo compa\u00F1ia");
		}
	}

	private static TediResult putInvoice(String domainName, int domain, boolean snapshot, String user, TediInvoice invoice)
			throws TediException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			final AonConfiguration aonCtx = ConfigurationDAO.getConfiguration(ctx);
			Tedi tedi = getTedi(ctx, snapshot);
			LOGGER.info("[TEDI] Attempt to put invoice [" + invoice.getCompany() + "," + invoice.getUuid() + "]");
			return TediParser.toFullInvoice(ctx, aonCtx, tedi.putInvoice(invoice));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static TediResult rejectInvoice(String domainName, int domain, boolean snapshot, String user, TediInvoice invoice)
			throws TediException {
		ensureInvoice( invoice );
		invoice.setOldStatus( invoice.getStatus() );
		invoice.setStatus( TediInvoiceStatus.refused);
		LOGGER.info("[TEDI] Attempt to reject invoice [" + invoice.getCompany() + "," + invoice.getUuid() + "]");
		return putInvoice(domainName, domain, snapshot, user, invoice);
	}

	public static LinkedList<TediResult> rejectInvoices(String domainName, int domain, boolean snapshot, String user,
			LinkedList<TediResult> invoices) throws TediException {
		if (invoices == null) {
			throw new TediException("No se han indicado una facturas");
		}
		AONContext ctx = null;
		LinkedList<TediResult> returned = new LinkedList<TediResult>();	
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			Tedi tedi = getTedi(ctx,snapshot);
			for (TediResult result : invoices) {
				TediInvoice inv = result.getTedi();
				if (AonStringUtils.isBlank(inv.getCompany())) {
					throw new TediException("La factura no tiene el atributo compa\u00F1ia");
				}
				inv.setOldStatus( inv.getStatus() );
				inv.setStatus( TediInvoiceStatus.refused);
				if ( result.getAccountingInvoice() != null) {
					Invoice aonInvoice = result.getAccountingInvoice().getInvoice();
					LOGGER.info("[TEDI] Attempt to reject invoice [" + aonInvoice.getType() 
						+ "," + aonInvoice.getSeries()
						+ "," + aonInvoice.getNumber()
						+ "," + aonInvoice.getReferenceCode()
						+ "," + aonInvoice.getRegistryDocument()
						+ "," + aonInvoice.getRegistryName()
						+ "]");
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
	
	public static TediResult acceptInvoice(String domainName, int domain, boolean snapshot, String user, TediInvoice invoice)
			throws TediException {
		ensureInvoice( invoice );
		invoice.setOldStatus( invoice.getStatus() );
		invoice.setStatus( TediInvoiceStatus.accepted );
		LOGGER.info("[TEDI] Attempt to accept invoice [" + invoice.getCompany() + "," + invoice.getUuid() + "]");
		return putInvoice(domainName, domain, snapshot, user, invoice);
	}

	public static LinkedList<TediResult> acceptInvoices(String domainName, int domain, boolean snapshot, String user,
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
			final AONContext dupCtx = ctx;
			LOGGER.info("[TEDI] Attempt to accept " + invoices.size() + " invoices ");
			LinkedList<TediInvoice> acceptedInvoices = new LinkedList<TediInvoice>();
			try {
				ctx.getDslContext().transaction( (config) -> {
					int x = 1;
					for (TediResult result : invoices) {
						TediInvoice invoice = result.getTedi();
						ensureInvoice(invoice);
						if ( result.getAccountingInvoice() == null) {
							String msg = "[TEDI] \t (" +x+ ") Invoice [" + invoice.getCompany() + "," + invoice.getUuid() + "] has no valid AON invoice";
							LOGGER.severe(msg);
							throw new TediException(msg);			
						}
						invoice.setOldStatus( invoice.getStatus() );
						invoice.setStatus( TediInvoiceStatus.accepted );
						LOGGER.info("[TEDI] \t (" +x+ ") Attempt to accept invoice [" + invoice.getCompany() + "," + invoice.getUuid() + "]");
						TediInvoice accepted = tedi.putInvoice(invoice);
						acceptedInvoices.add(accepted);		
						Invoice aonInvoice = result.getAccountingInvoice().getInvoice();
						LOGGER.info("[TEDI] \t (" +x+ ") Attempt to save AON invoice [" + aonInvoice.getType() 
							+ "," + aonInvoice.getSeries()
							+ "," + aonInvoice.getNumber()
							+ "," + aonInvoice.getReferenceCode()
							+ "," + aonInvoice.getRegistryDocument()
							+ "," + aonInvoice.getRegistryName()
							+ "]");
						AccountingInvoiceDAO.save(dupCtx, aonCtx, result.getAccountingInvoice());
						returned.add(result);
						x++;
					}
				});
			} catch ( RuntimeException e) {
				int x = 1;
				for (TediInvoice invoice : acceptedInvoices ) {
					invoice.setOldStatus( invoice.getStatus() );
					invoice.setStatus( TediInvoiceStatus.inbox);
					LOGGER.info("[TEDI] \t\t (" +x+ ") Attempt to rollback provious accepted invoice [" + invoice.getCompany() + "," + invoice.getUuid() + "]");
					tedi.putInvoice(invoice);
					x++;
				}
				throw e;
			}
		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
		return returned;
	}
	
	public static LinkedList<TediCompany> getCompanies(String domainName, int domain, boolean snapshot, String user)
			throws TediException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getCompanies(ctx , snapshot);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<TediCompany> getCompanies(AONContext ctx, boolean snapshot) throws TediException {
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
		LOGGER.info("[TEDI] Attempt to recover companies for [" + company.getDocument() + "]");
		return tedi.getCompanies(company.getDocument());
		
	}

	public static TediCompany createCompany(String domainName, int domain, boolean snapshot, String user, TediCompany tediCompany) throws TediException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return createCompany(ctx , snapshot, tediCompany);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static TediCompany createCompany(AONContext ctx, boolean snapshot, TediCompany tediCompany) throws TediException {
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
		LOGGER.info("[TEDI] Attempt to recover companies for [" + company.getDocument() + "]");
		tediCompany.setCompany(company.getDocument());
		return tedi.createCompany(tediCompany);
	}
	
	public static TediCompany getRegistry(String domainName, int domain, String user, boolean snapshot, String document) throws TediException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry(ctx , snapshot, document);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static TediCompany getRegistry(AONContext ctx, boolean snapshot, String document) throws TediException {
		Tedi tedi = getTedi(ctx, snapshot);
		return tedi.getRegistry(document);
		
	}

*/	

}
