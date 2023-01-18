package net.aonsolutions.aon.api.servlet.documental;

import java.util.Base64;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.AttachJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AttachServlet", urlPatterns = { "/ms/api/attach/*" })
public class AttachServlet extends AonApiHttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(AttachServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[GET] /ms/api/attach/ - Attach Servlet");
		AonApiData api = initialize(req);
		response(req, resp, getFile(api));
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[POST] /ms/api/attach/ - Attach Servlet");
		AonApiData api = initialize(req);
		uploadFile(api);
		response(req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[DELETE] /ms/api/attach/ - Attach Servlet");
		AonApiData api = initialize(req);
		deleteFile(api);
		response(req, resp);
	}
	
	private JSONObject getFile(AonApiData api) {
		AttachType attachType = AttachType.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.ATTACH_TYPE));	
		
		Attach attach = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> attachFilter(api.getDomain().getId(), api.getData(), f), attachType);
		
		JSONObject attachJSON = AttachJSON.toJSON(attach);
		
		setContent(api, attach, attachJSON);
		
		return attachJSON;
	}
	
	private Filter attachFilter(Integer domainId, JSONObject json, AttachProperties f) {
		domainId = json.opt(IJsonNames.DOMAIN_ID) != null ? json.optInt(IJsonNames.DOMAIN_ID) : domainId;
		
		Filter filter = f.getDomainProperty().eq(domainId);
		
		if(json.opt(IJsonNames.ATTACH_MODULE) != null) 
			filter = filter.and(f.getAttachModuleProperty().eq(JsonUtils.getInteger(json, IJsonNames.ATTACH_MODULE)));

		if(json.opt(IJsonNames.TYPE) != null) 
			filter = filter.and(f.getTypeProperty().eq(JsonUtils.getByte(json, IJsonNames.TYPE)));
		
		if(json.opt(IJsonNames.SOURCE) != null) 
			filter = filter.and(f.getSourceTypeProperty().eq(JsonUtils.getByte(json, IJsonNames.SOURCE)));
		
		if(json.opt(IJsonNames.SOURCE_ID) != null) 
			filter = filter.and(f.getSourceBatchProperty().eq(JsonUtils.getInt(json, IJsonNames.SOURCE_ID)));
		
		if(json.opt(IJsonNames.ID) != null) 
			filter = filter.and(f.getIdProperty().eq(JsonUtils.optInteger(json, IJsonNames.ID)));

		return filter;
	}
	
	private void deleteFile(AonApiData api) {
		AttachType attachType = AttachType.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.ATTACH_TYPE));	
		AON.deleteAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> attachFilter(api.getDomain().getId(), api.getData(), f), attachType);	
	}
	
	private void uploadFile(AonApiData api) {		
		Attach attach = AttachJSON.fromJSON(api.getData());
		if(attach.getId() != null)
			AON.updateAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), attach);
		else 
			AON.insertAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), attach);
	}
	
	
	private void setContent(AonApiData api, Attach attach, JSONObject attachJSON) {
		if(api.getData().optBoolean(IJsonNames.FILE) && attach.getId()!=null && attach.getData() != null) {
			attachJSON.put(IJsonNames.CONTENT, Base64.getEncoder().encodeToString(attach.getData()));
		}
	}
}