package com.code.aon.webservice.documental;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.CategoryType;
import com.esferalia.aon.occam.api.model.type.TagType;

@SuppressWarnings("serial")
@WebServlet(name = "DocumentalServlet", urlPatterns = { "/aon_gwt_aio/attachment/*" })
public class DocumentalServlet extends HttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(DocumentalServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Documental Servlet - GET METHOD");
	
		String domainName = req.getServerName();
		Integer domainId = Integer.parseInt(req.getParameter(MSG.DOMAIN));
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String userName = req.getRemoteUser();
		Domain domain = AON.getDomain(domainName, domainId, userName);
	
		Object object = new Object();
		JSONObject meta = new JSONObject();

		String md5 = Utils.getMd5(userName+domain.getName());
		if(accessToken.equals(md5)){
			switch (req.getPathInfo()) {
			case "/files":
				object = getAttachJSON(domain, userName);
				break;
			case "/certificates":
				object = getCertificateAttachJSON(domain, userName);
				break;
			case "/quality":
				object = getQualityImagesJSON(domain, userName, req.getParameter(MSG.ID));
				break;
			case "/category":
				object = getCategoryJSON(domain, userName);
				break;
			case "/tag":
				object = getTagJSON(domain, userName);
				break;
			case "/scope":
				object = getScopeJSON(domain, userName);
				break;
			default:
				break;
			}				
			
			Utils.giveBack(req, resp, object, meta);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Documental Servlet - POST METHOD");
		
		JSONObject json = Utils.getRequestJSON(req);
		
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1]; 
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		
		if(pathInfo.length > 3){				
			Object object = new Object();
			switch (pathInfo[3]) {
			case "remove": case "delete":
				object = removeAttach(domain, userName, json);
				break;
			default:
				break;
			}
			
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}
	
	private JSONObject removeAttach(Domain domain, String login, JSONObject json) {
		Integer id = json.getInt("id");
		AttachType attachType = AttachType.getAttachType(json.getString("attach_type"));
		AON.deleteAttach(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id), attachType);
		return json;
	}
	
	private JSONArray getAttachJSON(Domain domain, String login) {
		JSONArray array = new JSONArray();
		AON.getAttachStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value())
				.page(1)
				.perPage(30)
			), AttachType.REGISTRY, false).forEach(a -> {
				array.put(ToJSON.attachToJSON(a));
			});
		return array;
	}
	
	private JSONArray getCertificateAttachJSON(Domain domain, String login) {
		JSONArray array = new JSONArray();
		AON.getAttachStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getTypeProperty().eq(RegistryAttachmentType.DIGITAL_CERTIFICATE.value())
				.page(1)
				.perPage(30)
			), AttachType.REGISTRY, false).forEach(a -> {
				array.put(ToJSON.attachToJSON(a));
			});
		return array;
	}
	
	private JSONArray getQualityImagesJSON(Domain domain, String login, String idStr) {
		Integer id = Integer.parseInt(idStr);
		JSONArray array = new JSONArray();
		AON.getAttachStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getSourceTypeProperty().eq((byte)0)).and(f.getSourceBatchProperty().eq(id))
				.page(1)
				.perPage(30)
			, AttachType.DATA, false).forEach(a -> {
				JSONObject json = ToJSON.attachToJSON(a);
				String url = "/aon_gwt_aio/image_servlet?type=data&id="+ a.getId() +"&domain="+ domain.getName() +"&login="+ login;
				json.put("attach_type", "data");
				json.put("url", url);
				array.put(json);
			});
		return array;
	}
	
	private JSONArray getCategoryJSON(Domain domain, String login) {
		JSONArray array = new JSONArray();
		AON.getCategoryStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getTypeProperty().eq(CategoryType.REGISTRY_ATTACHMENT.value())
			)).forEach(a -> array.put(ToJSON.categoryToJSON(a)));
		return array;
	}
	private JSONArray getTagJSON(Domain domain, String login) {
		JSONArray array = new JSONArray();
		AON.getTagStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getTypeProperty().eq(TagType.RATTACH.value())
			)).forEach(a -> array.put(ToJSON.tagToJSON(a)));
		return array;
	}
	
	private JSONArray getScopeJSON(Domain domain, String login) {
		JSONArray array = new JSONArray();
		AON.getScopeStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()))
		.forEach(s -> array.put(ToJSON.scopeToJSON(s)));
		return array;
	}
}
