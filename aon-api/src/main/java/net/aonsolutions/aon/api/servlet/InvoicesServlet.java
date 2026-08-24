package net.aonsolutions.aon.api.servlet;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.InvoiceCounterJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RawdocInvoiceCounterJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.InvoiceCounter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;

@SuppressWarnings("serial")
@WebServlet(name = "AonInvoicesServlet", urlPatterns = { "/ms/api/invoices/*" })
public class InvoicesServlet extends AonApiHttpServlet {

    private static final Logger LOGGER = Logger.getLogger(InvoicesServlet.class.getName());

    public static final String INVOICES = "/";
    public static final String INVOICE = "/:id";
    public static final String INVOICE_RECORD = "/:id/record";
    public static final String RAWDOC = "/rawdoc/:id";
    public static final String ACCEPT = "/accept";
    public static final String COMMUNICATE = "/communicate";
    public static final String COUNT = "/count";
    public static final String CHART = "/chart";
    public static final String CHART_PERIOD = "/chart/period";
    
    public static final String INVOICE_DUPLICATE_FIX = "/invoiceduplicatefix";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        get(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        put(req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) {
        put(req, resp);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        delete(req, resp);
    }

    private void get(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);
 
            Object object = new AonRouting(api)
            		.addRoute(RAWDOC, InvoicesServlet::getRawdoc)
            		.addRoute(COUNT, InvoicesServlet::getCount)
            		.addRoute(CHART, InvoicesServlet::getChartInvoices)
                    .addRoute(CHART_PERIOD, InvoicesServlet::getChartInvoicesPeriod)
            		.addRoute(INVOICES, InvoicesServlet::getInvoices)
                    .addRoute(INVOICE, InvoicesServlet::getInvoice)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }

    private void put(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);
            Object object = new AonRouting(api)
                    .addRoute(ACCEPT, InvoicesServlet::acceptInvoice)
                    .addRoute(COMMUNICATE, InvoicesServlet::communicateInvoice)
                    .addRoute(INVOICE_DUPLICATE_FIX, InvoicesServlet::invoiceDuplicateFix)
            		.addRoute(INVOICES, InvoicesServlet::saveInvoice)
                    .addRoute(INVOICE, InvoicesServlet::saveInvoice)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);

            Object object = new AonRouting(api)
                    .addRoute(INVOICE, InvoicesServlet::deleteInvoice)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }

    private static JSONArray getInvoices(AonApiData api) {
        JSONArray array = new JSONArray();
        String status = JsonUtils.getString(api.getData(), IJsonNames.STATUS);
        if (isRawdoc(status)) {
            RawdocStatus rs = getRawdocStatus(status);
            AON.getRawdocStream(api.getDomain().getName(), api.getDomain().getId(),
                    api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId())
                            .and(f.getStatusProperty().eq(rs.value()))
                            .and(f.getNatureProperty().eq(RawdocNature.INVOICE.value())))
                    .forEach(r -> {
                        JSONObject json = new JSONObject(r.getJson());
                        json.put(IJsonNames.ID, r.getId());
                        json.put(IJsonNames.STATUS, r.getStatus() != null ? r.getStatus().getName() : IConstants.INBOX);
                        if (r.getMimeType() != null) {
                            JSONObject data = new JSONObject();
                            data.put("domain_name", api.getDomain().getName());
                            data.put("domain_id", api.getDomain().getId());
                            data.put("id", r.getId());
                            data.put("attach_type", AttachType.RAWDOC.getName());
                            String result = Base64.getEncoder()
                                    .encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
                            String url = "ms/api/file/" + result;

                            JSONObject f = new JSONObject();
                            f.put("url", url);
                            f.put("content_type", r.getMimeType().getName());
                            json.put("file", f);
                        }
                        JSONArray log = new JSONArray(r.getLog() != null ? r.getLog() : "[]");
                        json.put("remarks", log);
                        String description = JsonUtils.getString(api.getData(), IJsonNames.DESCRIPTION);
                        String reference = !AonStringUtils.isBlank(description)
                                && json.opt(IJsonNames.REFERENCE) != null ? json.optString(IJsonNames.REFERENCE) : "";
                        String registryName = RawdocType.OUTPUT.equals(r.getType())
                                ? (!AonStringUtils.isBlank(description) && json.opt(IJsonNames.RECEIVER) != null
                                        ? json.getJSONObject(IJsonNames.RECEIVER).optString(IJsonNames.NAME)
                                        : "")
                                : (!AonStringUtils.isBlank(description) && json.opt(IJsonNames.SENDER) != null
                                        ? json.getJSONObject(IJsonNames.SENDER).optString(IJsonNames.NAME)
                                        : "");

                        if (AonStringUtils.isBlank(description) || (!AonStringUtils.isBlank(description) &&
                                (AonStringUtils.containsIgnoreCase(reference, description)
                                        || AonStringUtils.containsIgnoreCase(registryName, description)))) {
                            array.put(json);
                        }
                    });
        } else {
            AON_SOLUTIONS
                    .getInvoices(api.getDomain().getName(), api.getDomain().getId(), "api", f -> invoiceFilter(api, f))
                    .forEach(invoice -> array.put(invoiceList2JSON(invoice)));
        }
        return array;
    }
    
    private static JSONArray getChartInvoices(AonApiData api) {
        JSONArray array = new JSONArray();
        AON_SOLUTIONS
        	.getChartInvoices(api.getDomain().getName(), api.getDomain().getId(), "api", f -> invoiceFilter(api, f))
        	.forEach(invoice -> array.put(invoiceList2JSON(invoice)));
        return array;
    }
    
    private static JSONObject getChartInvoicesPeriod(AonApiData api) {
    	JSONObject result = new JSONObject();
        Pair<Date, Date> period = AON_SOLUTIONS.getInvoicesChartPeriod(api.getDomain().getName(), api.getDomain().getId(), "api", f -> invoiceFilter(api, f));
        if(null != period) { 
		    result.put("min", AonDateUtils.getYear(period.getKey()));
		    result.put("max", AonDateUtils.getYear(period.getValue()));
        }
        return result;
    }

    private static JSONObject getInvoice(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer invoiceId = vars.getInt(IJsonNames.ID);
		Invoice invoice = AON_SOLUTIONS.getInvoice(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoiceId);
		JSONObject json = InvoiceJSON.toJSON(invoice);
		json.put(IJsonNames.FILE, buildInvoiceFileJSON(api.getDomain(), api.getUser().getLogin(), invoice));
		return json;
    }
    
	/**
	 * @deprecated
	 * @use com.esferalia.aon.occam.impl.jooq.dao.InvoiceJSONUtils.buildInvoiceFileJSON
	 */
	@Deprecated
	private static JSONObject buildInvoiceFileJSON(Domain domain, String login, Invoice invoice) {
		JSONObject json = new JSONObject();

		InvoiceDoc invoiceDoc = invoice.getDoc().orElse(null);
		Occam occam = new Occam().setDomain(domain.getId()).setDomainName(domain.getName()).setUser(login);
		if(invoiceDoc == null) invoiceDoc = AON.getInvoiceDoc(occam, invoice.getDomain(), invoice.getId()).orElse(null);
		
		if(invoiceDoc != null) {
		    json.put(IJsonNames.URL, invoiceDoc.getUrl());
		    json.put(IConstants.CONTENT_TYPE, invoiceDoc.getMimeType().getName());
			return json;
		} else {
			Attach invoiceAttach = AON.getAttach(domain.getName(), domain.getId(), login,
					f -> f.getAttachModuleProperty().eq(invoice.getId())
					, AttachType.INVOICE);

			if(invoiceAttach != null && invoiceAttach.getId() != null) {
				JSONObject data = new JSONObject();
				data.put(IConstants.DOMAIN_NAME, domain.getName());
				data.put(IConstants.DOMAIN_ID, domain.getId());
				data.put(IJsonNames.ID, invoiceAttach.getId());
				data.put(IConstants.ATTACH_TYPE, AttachType.INVOICE.getName());
				String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
				String path = "/ms/api/file/" +  result;	
				String url = "https://" + domain.getName() + path; 
			    json.put(IJsonNames.URL, url);
			    json.put(IJsonNames.PATH, path);
			    json.put(IConstants.CONTENT_TYPE, invoiceAttach.getMimeType().getName());
				return json;
			} else if(invoice.isSales()){
				JSONObject data = new JSONObject();
				data.put(IConstants.DOMAIN_NAME, domain.getName());
				data.put(IConstants.DOMAIN_ID, domain.getId());
				data.put(IJsonNames.ID, invoice.getId());
				data.put(IConstants.SOURCE, "invoice");
				data.put(IJsonNames.LOGIN, login);
			
				String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
				String path = "/ms/api/download_invoice_pdf?json=" +  result;
				String url = "https://" + domain.getName() + path;
			    json.put(IJsonNames.URL, url);
			    json.put(IJsonNames.PATH, path);
			    json.put(IConstants.CONTENT_TYPE, MimeType.PDF.getName());
				return json;
			}	
		}
		return null;
	}
    
    
    private static JSONObject getRawdoc(AonApiData api) {
  		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
  		Integer invoiceId = vars.getInt(IJsonNames.ID);
   		Rawdoc rawdoc = AON.getRawdocFull(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoiceId)
   				.orElse(null);
  		return new JSONObject(rawdoc.getJson());
    }
    
    private static JSONObject getCount(AonApiData api) {
    	JSONObject json = new JSONObject();
    	RawdocInvoiceCounter rawdocCounter = AON.getRawdocInvoiceCounter(api.getDomain(), api.getUser());
    	json.put(IJsonNames.RAWDOC, RawdocInvoiceCounterJSON.toJSON(rawdocCounter));
    	
    	InvoiceCounter invoiceCounter = AON.getInvoiceCounter(api.getDomain(), api.getUser());
    	json.put(IJsonNames.INVOICE, InvoiceCounterJSON.toJSON(invoiceCounter));
    	
    	return json;
    }
    
    private static JSONObject saveInvoice(AonApiData api) {
        return RawdocServlet.setInvoice(api);
    }

    private static JSONObject acceptInvoice(AonApiData api) {
    	try {
			return InvoiceServlet.acceptInvoice(api);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonApiException(e);
		}
    }
    
    private static JSONObject communicateInvoice(AonApiData api) {
    	try (CloseableAONContext ctx = AONContext.getAONContext(api.getOccam())) {
    		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
    		Integer invoiceId = JsonUtils.getInteger(api.getData(), IJsonNames.INVOICE);
    		Integer certificateId = JsonUtils.getInteger(api.getData(), IJsonNames.CERTIFICATE);
    		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, api.getDomain().getId(), false);
    		icc.setCertificate(checkCertificate(api, certificateId));
    		
    		Invoice invoice = InvoiceDAO.getFullInvoice(ctx, invoiceId);

    		if(icc.isVerifactu() || icc.isNoVerifactu() || icc.isSif()) {
    			InvoiceCommunicatorContext communicator = new InvoiceCommunicatorContext(api.getDomain(), api.getUser(), certificateId, AonCollectionUtils.toList(invoice))
    					.setConfig(icc)
    					.setCompany(company);
    			try {
    				InvoiceCommunicator.issueInvoice(communicator);
    			} catch (Exception e) {
    				InvoiceCommunicator.throwRightException( e, invoice  );
    			}    			
    		} else if(icc.isTbai() || icc.isLroe()) {
    			InvoiceServlet.acceptTbai(icc, company, invoice);
    		} else if(icc.isSii()) {
    			InvoiceServlet.acceptSii(api, icc, company, invoice);
    		}
    		return new JSONObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonApiException(e);
		}
    }
    
    private static JSONObject deleteInvoice(AonApiData api) {
        return new JSONObject();
    }

    private static Filter invoiceFilter(AonApiData api, InvoiceProperties f) {
        Filter filter = f.getDomainProperty().eq(api.getDomain().getId())
			.and(f.getAnnulledProperty().eq((byte) 0).or(f.getAnnulledProperty().isNull()));

        if (api.getData().has(IJsonNames.DESCRIPTION)) {
            String description = JsonUtils.getString(api.getData(), IJsonNames.DESCRIPTION);
            filter = filter.and(
                    f.getReferenceCodeProperty().like("%" + description + "%")
                            .or(f.getRegistryNameProperty().like("%" + description + "%")));
        }

        if (api.getData().has(IJsonNames.TYPE)) {
            String[] types = JsonUtils.getString(api.getData(), IJsonNames.TYPE).split(",");
            Filter filter2 = f.getTypeProperty().eq(InvoiceType.safeValueOf(types[0]).value())
                    .or(f.getTypeProperty().eq(InvoiceType.safeValueOf(types[0].toUpperCase()).value()));
            for (Integer i = 1; i < types.length; i++) {
                filter2 = filter2.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(types[i]).value()))
                        .or(f.getTypeProperty().eq(InvoiceType.safeValueOf(types[i].toUpperCase()).value()));
            }
            filter = filter.and(filter2);
        }

        if (api.getData().has(IJsonNames.FROM)) {
            Date from = JsonUtils.getDate(api.getData(), IJsonNames.FROM);
            filter = filter.and(f.getStartIssueDateProperty().ge(from));
        }

        if (api.getData().has(IJsonNames.TO)) {
            Date to = JsonUtils.getDate(api.getData(), IJsonNames.TO);
            filter = filter.and(f.getEndIssueDateProperty().le(to));
        }

        if (api.getData().has(IJsonNames.PAGE)) {
            filter.page(JsonUtils.getInteger(api.getData(), IJsonNames.PAGE));
        }

        if (api.getData().has(IJsonNames.PER_PAGE)) {
            filter.perPage(JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE));
        }

        return filter;
    }

    private static RawdocStatus getRawdocStatus(String status) {
        return RawdocStatus.safeValueOf(status);
    }

    private static boolean isRawdoc(String status) {
        return getRawdocStatus(status) != null;
    }

    @Deprecated
    private static JSONObject invoiceList2JSON(Invoice invoice) {
        String referenceAux = "";
        if (!AonStringUtils.isBlank(invoice.getSeries())) {
            referenceAux = referenceAux + invoice.getSeries() + "/";
        }
        referenceAux = referenceAux + "PROFORMA";
        JSONObject json = new JSONObject();
        json.put(IJsonNames.ID, invoice.getId());
        json.put(IJsonNames.DATE, invoice.getIssueDate());
        json.put(IJsonNames.REFERENCE, invoice.getNumber() > 0
                ? invoice.getReferenceCode()
                : referenceAux);
        json.put(IJsonNames.NAME, invoice.getRegistryName());
        json.put(IJsonNames.TOTAL, invoice.getTotal());
        json.put(IJsonNames.STATUS, invoice.isRecorded()
                ? InvoiceStatus.SCORED.name().toLowerCase()
                : InvoiceStatus.PENDING.name().toLowerCase());
        json.put(IJsonNames.TYPE, invoice.getType().value());
        json.put(IJsonNames.TAXABLE_BASE, invoice.getTaxableBase());
        return json;
    }
    
	private static JSONObject invoiceDuplicateFix(AonApiData api) {
		AON_SOLUTIONS.invoiceDuplicateFix(api.getDomain(), api.getUser());
		return new JSONObject();
	}
	
}
