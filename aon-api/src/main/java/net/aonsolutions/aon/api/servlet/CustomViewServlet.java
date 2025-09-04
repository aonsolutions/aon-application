package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonNumberUtils;

@SuppressWarnings("serial")
@WebServlet(name = "APICustomViewServlet", urlPatterns = {"/ms/api/customview/*"})
public class CustomViewServlet extends HttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomViewServlet.class.getName());
	
	public static final String CUSTOM_VIEW = "/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		AonApiHttpServlet api = new AonApiHttpServlet();
		try {
			String path = req.getPathInfo();
			if(path == null) api.response(req, resp, getCustomViewValues(req));
			else api.responseFile(resp, downloadImage(req));
		} catch (Exception e) {
			api.error(req, resp, e);
		}
	}
	
	private static Attach downloadImage(HttpServletRequest req) throws Exception {
		String domainName = req.getServerName();
		String id = req.getPathInfo().replace("/", "");
		Domain domain = AON.getDomain(domainName, 1, "", f -> f.getNameProperty().eq(domainName));
		Attach attach = AON.getAttach(domain.getName(), domain.getParentId() != null ? domain.getParentId() : domain.getId(), "", 
				f -> f.getIdProperty().eq(AonNumberUtils.toInteger(id)), AttachType.REGISTRY, true);
		if(attach.getData() != null)
			return attach;
		else
			throw new Exception("No se encuentra el documento");
	}
	
	private static JSONObject getCustomViewValues(HttpServletRequest req) {
		String domainName = req.getServerName();
		Domain domain = AON.getDomain(domainName, 1, "", f -> f.getNameProperty().eq(domainName));
		Integer domainId = domain.getParentId() != null ? domain.getParentId() : domain.getId();
		JSONObject json = new JSONObject();
		JSONObject params = new JSONObject();
		JSONObject images = new JSONObject();
		AON.getApplicationParameterStream(domain.getName(), domainId, "", 
		        f -> f.getDomainProperty().eq(domainId)
	            .and((f.getNameProperty().eq(AppParam.AON_CUSTOMIZE_SUPPORT_EMAIL.toString()))
	             .or(f.getNameProperty().eq(AppParam.AON_CUSTOMIZE_SUPPORT_PHONE.toString())) 
	             .or(f.getNameProperty().eq(AppParam.AON_CUSTOMIZE_ID.toString())) 
	             .or(f.getNameProperty().eq(AppParam.AON_CUSTOMIZE_HERITABLE_ID.toString()))
	             .or(f.getNameProperty().eq(AppParam.AON_CUSTOMIZE_LOGIN_SEPARATOR.toString()))
	             .or(f.getNameProperty().eq(AppParam.AON_CUSTOMIZE_SEPARATOR.toString()))
	             .or(f.getNameProperty().eq(AppParam.AON_CUSTOMIZE_TITLE.toString()))
	             .or(f.getNameProperty().eq(AppParam.AON_HIDE_VERSION.toString())))
				).forEach(p -> params.put(p.getName(), p.getValue()));
		AON.getAttachList(domain.getName(), domainId, "", 
				f -> f.getDomainProperty().eq(domainId)
						.and(
								f.getAttachModuleProperty().eq(params.getInt(AppParam.AON_CUSTOMIZE_ID.toString()))
								.or(f.getAttachModuleProperty().eq(params.getInt(AppParam.AON_CUSTOMIZE_HERITABLE_ID.toString())))
						)
						.and(f.getTypeProperty().eq((byte)2))
						.or(f.getDescriptionProperty().like(ICommonConstantsAPI.FAVICON_NAME))
						.or(f.getDescriptionProperty().like(ICommonConstantsAPI.FAVICON_NAME_DARK))
						.or(f.getDescriptionProperty().like(ICommonConstantsAPI.LOGIN_LOGO_NAME))
						.or(f.getDescriptionProperty().like(ICommonConstantsAPI.LOGIN_LOGO_NAME_DARK))
						.or(f.getDescriptionProperty().like(ICommonConstantsAPI.HEADER_LOGO_NAME))
						.or(f.getDescriptionProperty().like(ICommonConstantsAPI.HEADER_LOGO_NAME_DARK))
						.or(f.getDescriptionProperty().like(ICommonConstantsAPI.TOOLBAR_LOGO_NAME))
						.or(f.getDescriptionProperty().like(ICommonConstantsAPI.STATUS_START_NAME))
						.or(f.getDescriptionProperty().like(ICommonConstantsAPI.STATUS_STOP_NAME))
						.or(f.getDescriptionProperty().like(ICommonConstantsAPI.STATUS_FAILED_NAME))
				, AttachType.REGISTRY)
				.forEach(r -> {
					images.put(r.getDescription().replace(".", ""), "customview/" + r.getId());
				});
		json.put("params", params);
		json.put("images", images);
		return json;
	}
	
	public interface ICommonConstantsAPI {
		String FAVICON_NAME = "favicon.svg";
		String FAVICON_NAME_DARK = "favicon-dark.svg";
		String LOGIN_LOGO_NAME = "aon-login-logo";
		String LOGIN_LOGO_NAME_DARK = "login-logo-dark";
		String HEADER_LOGO_NAME = "aon-header-logo";
		String HEADER_LOGO_NAME_DARK = "header-logo-dark";
		String TOOLBAR_LOGO_NAME = "aon-toolbar-logo";
		String STATUS_START_NAME = "aon-status-start";
		String STATUS_STOP_NAME = "aon-status-stop";
		String STATUS_FAILED_NAME = "aon-status-failed";
	}
}
