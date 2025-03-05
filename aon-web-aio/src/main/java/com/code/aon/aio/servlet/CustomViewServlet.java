package com.code.aon.aio.servlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "CustomViewServlet", urlPatterns = {"/customview/*"})
public class CustomViewServlet extends HttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomViewServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		String user = req.getParameter(IJsonNames.USER);
		String domain = req.getParameter(IJsonNames.DOMAIN);
		String parentIdStr = req.getParameter(IJsonNames.PARENT_ID);
		Integer parentId = AonNumberUtils.toInteger(parentIdStr);
		
		resp.setContentType(MimeType.CSS.getName());
		User usr = AON.getUser(domain, parentId, user);
		DomainUserRoles domainUserRoles = SECURITY.getDomainUserRoles(domain, parentId, user, usr.getId());
		
		domainUserRoles.getDomainApps().stream()
		.filter( app -> app == AonApp.CUSTOM_VIEW)
		.findFirst().ifPresentOrElse(app -> {
			try ( InputStream is = req.getServletContext().getResourceAsStream("/css/theme/customView.css")
					) {
				Map<String, String> paramsMap = new HashMap<>();

				AON.getApplicationParameterStream(
				        domain, 
				        parentId, 
				        user, 
				        p -> p.getDomainProperty().eq(parentId)
			            .and((p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_SUPPORT_EMAIL.toString()))
			             .or(p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_SUPPORT_PHONE.toString())) 
			             .or(p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_ID.toString())) 
			             .or(p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_HERITABLE_ID.toString())) 
			             .or(p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_TITLE.toString())))
						).forEach(p -> paramsMap.put(p.getName(), p.getValue()));

				String email = paramsMap.get(AppParam.AON_CUSTOMIZE_SUPPORT_EMAIL.toString());
				String phone = paramsMap.get(AppParam.AON_CUSTOMIZE_SUPPORT_PHONE.toString());
				String title = paramsMap.get(AppParam.AON_CUSTOMIZE_TITLE.toString());
				String idStr = paramsMap.get(AppParam.AON_CUSTOMIZE_ID.toString());
				Integer id = AonNumberUtils.toInteger(idStr);
				String heritableIdStr = paramsMap.get(AppParam.AON_CUSTOMIZE_HERITABLE_ID.toString());
				Integer heritableId = AonNumberUtils.toInteger(heritableIdStr);

				Attach faviconAttach = AON.getAttach(
					    domain, 
					    parentId, 
					    user, 
					    p -> (p.getAttachModuleProperty().eq(id).or(p.getAttachModuleProperty().eq(heritableId)))
					         .and(p.getTypeProperty().eq((byte) 2))
					         .and(p.getDescriptionProperty().eq("favicon.svg")), 
					    AttachType.REGISTRY);
				Attach headerLogoAttach = AON.getAttach(
					    domain, 
					    parentId, 
					    user, 
					    p -> (p.getAttachModuleProperty().eq(id).or(p.getAttachModuleProperty().eq(heritableId)))
					         .and(p.getTypeProperty().eq((byte) 2))
					         .and(p.getDescriptionProperty().eq("aon-header-logo")), 
					    AttachType.REGISTRY);
				Attach loginLogoAttach = AON.getAttach(
					    domain, 
					    parentId, 
					    user, 
					    p -> (p.getAttachModuleProperty().eq(id).or(p.getAttachModuleProperty().eq(heritableId)))
					         .and(p.getTypeProperty().eq((byte) 2))
					         .and(p.getDescriptionProperty().eq("aon-login-logo")), 
					    AttachType.REGISTRY);
				
				String headerLogoMd5 = getMd5(headerLogoAttach.getData());
				String faviconMd5 = getMd5(faviconAttach.getData());
				String loginLogoMd5 = getMd5(loginLogoAttach.getData());
				byte [] bytes = is.readAllBytes();
				String css = new String(bytes, StandardCharsets.UTF_8);
				
				css = css.replaceAll("phoneCustom", phone);
				css = css.replaceAll("emailCustom", email);
				css = css.replaceAll("titleCustom", title);
				css = css.replaceAll("logoCustom", "aonDocuments/" + headerLogoAttach.getId() + "-" + headerLogoMd5);
				css = css.replaceAll("faviconCustom", "aonDocuments/" + faviconAttach.getId() + "-" + faviconMd5);
				css = css.replaceAll("loginLogoCustom", "aonDocuments/" + loginLogoAttach.getId() + "-" + loginLogoMd5);
				
				byte[] finalCssBytes = css.getBytes(StandardCharsets.UTF_8);
				resp.setContentLength(finalCssBytes.length);  
				resp.getOutputStream().write(finalCssBytes);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		},
		() -> {
			resp.setContentLength(0);
		});
		
		
		
		
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
	}
	
	private static String getMd5(byte[] data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    md.update(data);
	    byte byteData[] = md.digest();
	    //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
	     	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }       
        return sb.toString();
	}
	
	
	
}
