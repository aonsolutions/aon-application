package com.code.aon.aio.servlet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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
		try {
			get(req, resp);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			get(req, resp);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) throws SQLException {
		String domainStr = req.getServerName();
		
		resp.setContentType(MimeType.CSS.getName());

		Domain domain = AON.getDomain(domainStr, 1, "", f -> f.getNameProperty().eq(domainStr));
		Integer parentId = domain.getParentId();
		if(parentId != null) {
			boolean customView = AON_SOLUTIONS.getDomainApp(domainStr, parentId, "", f-> f.getDomainProperty().eq(parentId)
				.and(f.getAppProperty().eq(AonApp.CUSTOM_VIEW.value())
				.and(f.getActiveProperty().eq((byte) 1)))).count() > 0;
			if(customView == true) {
				try ( InputStream is = req.getServletContext().getResourceAsStream("/css/theme/customView.css")
						) {
					Map<String, String> paramsMap = new HashMap<>();

					AON.getApplicationParameterStream(
					        domainStr, 
					        parentId, 
					        "", 
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
							domainStr, 
						    parentId, 
						    "", 
						    p -> (p.getAttachModuleProperty().eq(id).or(p.getAttachModuleProperty().eq(heritableId)))
						         .and(p.getTypeProperty().eq((byte) 2))
						         .and(p.getDescriptionProperty().eq("favicon.svg")), 
						    AttachType.REGISTRY);
					Attach headerLogoAttach = AON.getAttach(
							domainStr, 
						    parentId, 
						    "", 
						    p -> (p.getAttachModuleProperty().eq(id).or(p.getAttachModuleProperty().eq(heritableId)))
						         .and(p.getTypeProperty().eq((byte) 2))
						         .and(p.getDescriptionProperty().eq("aon-header-logo")), 
						    AttachType.REGISTRY);
					Attach loginLogoAttach = AON.getAttach(
							domainStr, 
						    parentId, 
						    "", 
						    p -> (p.getAttachModuleProperty().eq(id).or(p.getAttachModuleProperty().eq(heritableId)))
						         .and(p.getTypeProperty().eq((byte) 2))
						         .and(p.getDescriptionProperty().eq("aon-login-logo")), 
						    AttachType.REGISTRY);
					
					String headerLogoMd5 = getMd5(headerLogoAttach.getData());
					String faviconMd5 = getMd5(faviconAttach.getData());
					String loginLogoMd5 = getMd5(loginLogoAttach.getData());
					byte [] bytes = is.readAllBytes();
					String css = new String(bytes, StandardCharsets.UTF_8);
				
					css = AonStringUtils.replace(css, "phoneCustom", phone==null ? "" : phone);
					if (phone==null) {
						css = AonStringUtils.replace(css, "phoneIcon", "none");
					}
					css = AonStringUtils.replace(css, "emailCustom", email==null ? "" : email);
					if (email==null) {
						css = AonStringUtils.replace(css, "emailIcon", "none");
					}
					css = AonStringUtils.replace(css, "titleCustom", title==null ? "" : title);
					if (title==null) {
						css = AonStringUtils.replace(css, "titleIcon", "none");
					}
					css = AonStringUtils.replace(css, "logoCustom", "aonDocuments/" + headerLogoAttach.getId() + "-" + headerLogoMd5);
					css = AonStringUtils.replace(css, "faviconCustom", "aonDocuments/" + faviconAttach.getId() + "-" + faviconMd5);
					css = AonStringUtils.replace(css, "loginLogoCustom", "aonDocuments/" + loginLogoAttach.getId() + "-" + loginLogoMd5);
					
					byte[] finalCssBytes = css.getBytes(StandardCharsets.UTF_8);
					resp.setContentLength(finalCssBytes.length);  
					resp.getOutputStream().write(finalCssBytes);
				} catch (IOException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
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
