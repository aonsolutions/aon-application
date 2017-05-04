package com.code.aon.webservice.documental;

import java.io.IOException;
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

@SuppressWarnings("serial")
@WebServlet(name = "DocumentalServlet", urlPatterns = { "/aon_gwt_aio/attachment/*" })
public class DocumentalServlet extends HttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(DocumentalServlet.class.getName());

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Documental Servlet - GET METHOD");
	
		String scheme = req.getParameter("scheme");
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1]; 
		String md5 = Utils.getMd5(userName+domainName);
		String url = Utils.getUrl(scheme, domainName, req.getRequestURL().toString().contains("aon-aio"));
		if(accessToken.equals(md5)){
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));

			if(pathInfo.length > 3){
				Object object = new Object();
				JSONObject meta = new JSONObject();
				switch (pathInfo[3]) {
				case "files":
					object = getAttachJSON(domain, userName);
					break;
				default:
						break;
				}
				
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Documental Servlet - POST METHOD");
	}
	
	private JSONArray getAttachJSON(Domain domain, String login) {
		JSONArray array = new JSONArray();
		AON.getAttachStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value())
				.page(1)
				.perPage(30)
			), AttachType.REGISTRY).forEach(a -> {
				array.put(ToJSON.attachToJSON(a));
			});
		return array;
	}
}
