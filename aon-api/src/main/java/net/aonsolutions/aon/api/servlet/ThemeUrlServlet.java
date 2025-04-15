package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "ThemeUrlServlet", urlPatterns = {"/ms/api/themeurl/*"})
public class ThemeUrlServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ThemeUrlServlet.class.getName());
	
	public static final String THEME = "/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
		
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		
		try {
			
			String domainStr = req.getParameter(IJsonNames.DOMAIN);
			String paramTheme = req.getParameter("paramTheme");
			String lsTheme = req.getParameter("lsTheme");
			String cookieTheme = req.getParameter("cookieTheme");
			String lsAonTheme = req.getParameter("lsAonTheme");
			
			String url = "";
			resp.setContentType(MimeType.CSS.getName());
			Domain domain = AON.getDomain(domainStr, 1, "", f -> f.getNameProperty().eq(domainStr));
			Integer parentId = domain.getParentId();
			if(parentId != null) {
				boolean customView = AON_SOLUTIONS.getDomainApp(domainStr, parentId, "", f-> f.getDomainProperty().eq(parentId)
					.and(f.getAppProperty().eq(AonApp.CUSTOM_VIEW.value())
					.and(f.getActiveProperty().eq((byte) 1)))).count() > 0;
					if(customView) {
						url = "/customview";
					}else if(AonStringUtils.isNotBlank(paramTheme)) {
						url = paramTheme;
					}else if(AonStringUtils.isNotBlank(lsTheme)) {
						url = lsTheme;
					}else if(AonStringUtils.isNotBlank(cookieTheme)) {
						url = cookieTheme;
					}else if(AonStringUtils.isNotBlank(lsAonTheme)) {
						url = lsAonTheme;
					}
			}else if(AonStringUtils.isNotBlank(paramTheme)) {
				url = paramTheme;
			}else if(AonStringUtils.isNotBlank(lsTheme)) {
				url = lsTheme;
			}else if(AonStringUtils.isNotBlank(cookieTheme)) {
				url = cookieTheme;
			}else if(AonStringUtils.isNotBlank(lsAonTheme)) {
				url = lsAonTheme;
			}
			JSONObject json = new  JSONObject();
			json.put(IJsonNames.URL, url);
			response(req, resp, json);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	
}
