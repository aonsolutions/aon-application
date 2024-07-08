package net.aonsolutions.aon.api.servlet;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.InvoiceCounterJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RawdocInvoiceCounterJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.InvoiceCounter;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounter;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;

@SuppressWarnings("serial")
@WebServlet(name = "AonInvoicesServlet", urlPatterns = { "/ms/api/invoices/*" })
public class InvoicesServlet extends AonApiHttpServlet {

    private static final Logger LOGGER = Logger.getLogger(InvoicesServlet.class.getName());

    public static final String INVOICES = "/";
    public static final String INVOICE = "/:id";
    public static final String INVOICE_RECORD = "/:id/record";
    public static final String RAWDOC = "/rawdoc/:id";
    public static final String ACCEPT = "/accept";
    public static final String RECORD = "/record";
    public static final String COUNT = "/count";
    
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
                    .addRoute(RECORD, InvoicesServlet::recordInvoices)
                    .addRoute(INVOICE_DUPLICATE_FIX, InvoicesServlet::invoiceDuplicateFix)
            		.addRoute(INVOICES, InvoicesServlet::saveInvoice)
                    .addRoute(INVOICE, InvoicesServlet::saveInvoice)
                    .addRoute(INVOICE_RECORD, InvoicesServlet::recordInvoice)
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
                            .and(f.getStatusProperty().eq(rs.value())))
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

    private static JSONObject getInvoice(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer invoiceId = vars.getInt(IJsonNames.ID);
		Invoice invoice = AON_SOLUTIONS.getInvoice(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoiceId);
		JSONObject json = InvoiceJSON.toJSON(invoice);
		json.put(IJsonNames.FILE, InvoiceServlet.buildInvoiceFileJSON(api.getDomain(), api.getUser().getLogin(), invoice));
		return json;
    }
    
    private static JSONObject getRawdoc(AonApiData api) {
  		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
  		Integer invoiceId = vars.getInt(IJsonNames.ID);
   		Rawdoc rawdoc = AON.getRawdocFull(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoiceId);
  		return new JSONObject(rawdoc.getJson());
    }
    
    private static JSONObject getCount(AonApiData api) {
    	JSONObject json = new JSONObject();
    	RawdocInvoiceCounter rawdocCounter = AON.getRawdocInvoiceCounter(api.getDomain(), api.getUser());
    	json.put(IJsonNames.RAWDOC, RawdocInvoiceCounterJSON.toJSON(rawdocCounter));
    	
    	InvoiceCounter invoiceCounter = AON.getInvoiceCounter(api.getDomain(), api.getUser());
    	json.put(IJsonNames.INVOICE, InvoiceCounterJSON.toJSON(invoiceCounter));
    	System.out.println( json.toString(1)  );
    	return json;
    }
    
    private static JSONObject saveInvoice(AonApiData api) {
        return InvoiceServlet.setInvoice(api);
    }

    private static JSONObject acceptInvoice(AonApiData api) {
    	try {
			return InvoiceServlet.acceptInvoice(api);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonApiException(e);
		}
    }
    
    private static JSONObject deleteInvoice(AonApiData api) {
        return new JSONObject();
    }

    private static Filter invoiceFilter(AonApiData api, InvoiceProperties f) {
        Filter filter = f.getDomainProperty().eq(api.getDomain().getId());

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
        return json;
    }
    
    private static JSONObject recordInvoices(AonApiData api) {
//    	JsonUtils.getJSONArray(api.getData(), IJsonNames.INVOICES).toList().stream().forEach(object -> {
//    		System.out.println(object);
//    		System.out.println(object.toString());
//    		JSONObject document = InvofoxServlet.getDocument(api.getDomain(), api.getUser(), object.toString());
//    		recordInvoice(api.getDomain(), api.getUser(), document);
//    	});
    	return new JSONObject();
    }
    
    private static JSONObject recordInvoice(AonApiData api) {
    	return recordInvoice(api.getDomain(), api.getUser(), api.getData());
    }
    
    private static JSONObject recordInvoice(Domain domain, User user, JSONObject invoice) {
//    	AccountingInvoice ai = Rawdoc2AccountingInvoice.getAccountingInvoice(domain, user, invoice);
//    	try {
//    		ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), ai);
//    	} catch (Exception e) {
//    		e.printStackTrace();
//		}
    	return new JSONObject();    	
    }
    
	private static JSONObject invoiceDuplicateFix(AonApiData api) {
		AON_SOLUTIONS.invoiceDuplicateFix(api.getDomain(), api.getUser());
		return new JSONObject();
	}
	
}
