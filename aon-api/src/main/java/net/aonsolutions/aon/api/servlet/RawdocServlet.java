package net.aonsolutions.aon.api.servlet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.json.TediInvoiceJSON;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import solutions.aon.aws.s3.S3;

@SuppressWarnings("serial")
@WebServlet(name = "RawdocServlet", urlPatterns = {"/ms/api/rawdoc/*"})
public class RawdocServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(RawdocServlet.class.getName());
	
	public static final String RAWDOCS = "/";
	public static final String RAWDOC = "/:id";
	
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
				.addRoute(RAWDOCS, RawdocServlet::getRawdocs)
				.addRoute(RAWDOC, RawdocServlet::getRawdoc)
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
				.addRoute(RAWDOCS, RawdocServlet::putRawdoc)
				.addRoute(RAWDOC, RawdocServlet::putRawdoc)
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
				.addRoute(RAWDOCS, RawdocServlet::deleteRawdocs)
				.addRoute(RAWDOC, RawdocServlet::deleteRawdoc)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	public static JSONArray getRawdocs(AonApiData api) {
		String status = JsonUtils.getString(api.getData(), IJsonNames.STATUS);
		String description = JsonUtils.getString(api.getData(), IJsonNames.DESCRIPTION);
		JSONArray jsArray = new JSONArray();
		RawdocStatus rs = RawdocStatus.safeValueOf(status);
		AON.getRawdocStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getStatusProperty().eq(rs.value())))
		.forEach(r -> { 
			JSONObject json = rawdocToJson(api, r);			
			String reference = description != null && json.opt(IJsonNames.REFERENCE) != null ? json.optString(IJsonNames.REFERENCE) : "";
			String registryName = RawdocType.OUTPUT.equals(r.getType()) 
				? (description != null && json.opt(IJsonNames.RECEIVER) != null ? json.getJSONObject(IJsonNames.RECEIVER).optString(IJsonNames.NAME) : "")
				: (description != null && json.opt(IJsonNames.SENDER) != null ? json.getJSONObject(IJsonNames.SENDER).optString(IJsonNames.NAME) : "");
					
			if(description == null || (description != null && 
					(AonStringUtils.containsIgnoreCase(reference, description) 
							|| AonStringUtils.containsIgnoreCase(registryName, description)))) {
				jsArray.put(json);
			}
		});
		return jsArray;
	}
	
	private static JSONObject getRawdoc(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer rawdocId = vars.getInt(IJsonNames.ID);
		Rawdoc rawdoc =AON.getRawdocFull(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), rawdocId);
		return rawdocToJson(api, rawdoc);
	}
	
	public static JSONObject putRawdoc(AonApiData api) {
		return setInvoice(api);
	}
	
	public static JSONObject deleteRawdocs(AonApiData api) {
		List<Integer> invoiceIds = toList(api.getData().optJSONArray(IConstants.ID));
		if(!invoiceIds.isEmpty()) {
			Integer[] idsArray = invoiceIds.toArray(new Integer[invoiceIds.size()]);
			AON.rawdocDelete(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getIdProperty().in(idsArray)));
		}
		return new JSONObject();
	}
	
	private static JSONObject deleteRawdoc(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer rawdocId = vars.getInt(IJsonNames.ID);
		
		AON.rawdocDelete(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getIdProperty().eq(rawdocId)));
		return new JSONObject();
	}
	
	public static JSONObject setInvoice(AonApiData api) {
		Domain domain = api.getDomain();
		String login = api.getUser().getLogin();
		JSONObject json = api.getData();
		JSONObject file = null;
		
		if(json.opt("file")!= null && json.opt(IJsonNames.INVOICE) != null) { 
			file = json.optJSONObject("file");
			json.remove("file");
			json = json.opt(IJsonNames.INVOICE) != null ? json.optJSONObject(IJsonNames.INVOICE) : json;
			if(json.opt(IJsonNames.STATUS) == null) {
				json = initInvoice();
			}
		}
		
		Integer id = json.opt("id") !=null ? json.optInt("id") : null;
		RawdocStatus status = RawdocStatus.safeValueOf(json.optString("status")); 
		RawdocType type = json.opt("type") != null && json.optString("type").equalsIgnoreCase("emitida") 
				? RawdocType.OUTPUT : RawdocType.INPUT;

		Rawdoc rawdoc = new Rawdoc()
				.setId(id)
				.setDomain(domain.getId())
				.setNature(RawdocNature.INVOICE)
				.setType(type)
				.setStatus(status);
		
		if(JsonUtils.has(json, IJsonNames.FILE)) {
			JSONObject fileJSON =  JsonUtils.getJSONObject(json, IJsonNames.FILE);
			rawdoc.setS3Key(JsonUtils.getString(fileJSON, IJsonNames.S3_KEY));
		}
		
		if(file != null) {
			String base64 = file.optString("content");
			String contentType = file.optString("contentType");
			byte[] fileData = Base64.getDecoder().decode(base64);
			rawdoc.setData(fileData)
				.setMimeType(MimeType.get(contentType));

			// TEDI PARSER!!!		    
		    if(api.getDur().isOcr()) {
		    	InputStream input = new ByteArrayInputStream(fileData);
		    	TediContext tctx = new TediContext()
		    		.setDomainName(domain.getName())
		    		.setDomain(domain.getId())
		    		.setUser(login);
		    	try {
		    		TediResult r = TEDI.parse(tctx, input, MimeType.get(contentType));
		    		json = tediParse(TediInvoiceJSON.toJSON(r.getTedi()), json);
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		}
    	rawdoc.setJson(json.toString());
    	rawdoc.setLog(json.opt("remarks") != null? json.optJSONArray("remarks").toString(): "[]");

		rawdoc = AON.rawdocSave(domain.getName(), domain.getId(), login, rawdoc);
		json.put("id", rawdoc.getId()); 
		if(rawdoc.getMimeType() != null){
			JSONObject data = new JSONObject();
			data.put("domain_name", domain.getName());
			data.put("domain_id", domain.getId());
			data.put("id", rawdoc.getId());
			data.put("attach_type", AttachType.RAWDOC.getName());
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			String url =  "ms/api/file/" +  result;
								
			JSONObject f = new JSONObject();
		    f.put("url", url);
		    f.put("content_type", rawdoc.getMimeType().getName());
		    json.put("file", f);
		}
		return json;
	}
	
	private static JSONObject tediParse(JSONObject tedi, JSONObject json) {
		tedi.keySet().stream().forEach(key -> {
			json.put(key, tedi.get(key));
		});
		return json;
	}
	
	private static JSONObject initInvoice() {
		JSONObject json = new JSONObject();
		json.put("type", "recibida");
		json.put("series", "");
		json.put("number", 0);
		json.put("reference", "");
		json.put("date", AonDateUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		json.put("total", 0);
		json.put("sender", initRegistry());
		json.put("receiver", initRegistry());
		json.put("category", "");
		json.put("transaction", "NAC");
		json.put("taxes", new JSONArray());
		json.put("details", new JSONArray());
		json.put("finances", new JSONArray());
		json.put("suplidos", initSuplidos());
		json.put("status", "inbox");
		json.put("comments", "");
		json.put("remarks", new JSONArray());
		return json;
	}
	
	private static JSONObject initRegistry() {
		JSONObject json = new JSONObject();
		json.put("document", "");
		json.put("name", "");
		json.put("address", initAddress());			
		return json;
	}
	
	private static JSONObject initAddress() {
		JSONObject json = new JSONObject();
		json.put("country", "ES");
		json.put("address", "");
		json.put("zip", "");
		json.put("city", "");
		json.put("province", "");
		return json;
	}

	private static JSONObject initSuplidos() {
		JSONObject json = new JSONObject();
		json.put("active", false);
		json.put("description", "");
		json.put("total", 0);
		return json;
	}

	public static List<Integer> toList(JSONArray array) {
	    if(array==null || array.isEmpty())
	        return new LinkedList<>();
	    LinkedList<Integer> list = new LinkedList<>();
	    for(int i=0; i<array.length(); i++) {
	    	list.add(array.optInt(i));
	    }
	    return list;
	}
	
	
	private static JSONObject rawdocToJson(AonApiData api, Rawdoc rawdoc) {
		JSONObject json = new JSONObject(rawdoc.getJson());
		json.put(IJsonNames.ID, rawdoc.getId());
		json.put(IJsonNames.STATUS, rawdoc.getStatus() != null ? rawdoc.getStatus().getName() : IConstants.INBOX);
		if(!AonStringUtils.isBlank(rawdoc.getS3Key())) {
			URL url = S3.getURL(rawdoc.getS3Bucket(), rawdoc.getS3Key());
			JSONObject f = new JSONObject();
			f.put("url", url.toExternalForm());
			f.put("path", url.toExternalForm());
			String contentType = S3.getContentType(rawdoc.getS3Bucket(), rawdoc.getS3Key());
			f.put("content_type", contentType);
			f.put("s3Bucket", rawdoc.getS3Bucket());
			f.put("s3Key", rawdoc.getS3Key());
		    json.put("file", f);
		} else if(rawdoc.getMimeType() != null){
			JSONObject data = new JSONObject();
			data.put("domain_name", api.getDomain().getName());
			data.put("domain_id", api.getDomain().getId());
			data.put("id", rawdoc.getId());
			data.put("attach_type", AttachType.RAWDOC.getName());
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			String url =  "ms/api/file/" +  result;
								
			JSONObject f = new JSONObject();
		    f.put("url", url);
		    f.put("path", url);
		    f.put("content_type", rawdoc.getMimeType().getName());
		    json.put("file", f);
		}
		JSONArray log = new JSONArray(rawdoc.getLog() != null ? rawdoc.getLog() : "[]");
		json.put("remarks", log);
		return json;
	}
}
