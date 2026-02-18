package com.code.aon.aio.servlet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "CustomViewServlet", urlPatterns = {"/css/theme/customview.css","/css/theme/customview.gwt.css"})
public class CustomViewServlet extends HttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomViewServlet.class.getName());
	
	private static final Pattern CUSTOMIZABLE_VARIABLE_PATTERN = Pattern.compile("--(?<name>aonCustomize[a-zA-Z0-9]+):");
	
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
		Integer customViewdomainId = domain.getParentId() != null ? domain.getParentId() : domain.getId();
		boolean customView = AON_SOLUTIONS.getDomainApp(domainStr, customViewdomainId, "", f-> f.getDomainProperty().eq(customViewdomainId)
			.and(f.getAppProperty().eq(AonApp.CUSTOM_VIEW.value())
			.and(f.getActiveProperty().eq((byte) 1)))).count() > 0;
		if(customView == true) {
			
			try ( InputStream is = req.getServletContext().getResourceAsStream(req.getServletPath())
					) {
				Map<String, String> paramsMap = new HashMap<>();

				AON.getApplicationParameterStream(
				        domainStr, 
				        customViewdomainId, 
				        "", 
				        p -> p.getDomainProperty().eq(customViewdomainId)
			            .and((p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_SUPPORT_EMAIL.toString()))
			             .or(p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_SUPPORT_PHONE.toString())) 
			             .or(p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_ID.toString())) 
			             .or(p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_HERITABLE_ID.toString())) 
			             .or(p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_TITLE.toString()))
			             .or(p.getNameProperty().eq(AppParam.AON_CUSTOMIZE_THEME.toString())))
						).forEach(p -> paramsMap.put(p.getName(), p.getValue()));

				String theme = paramsMap.get(AppParam.AON_CUSTOMIZE_THEME.toString());
				String email = paramsMap.get(AppParam.AON_CUSTOMIZE_SUPPORT_EMAIL.toString());
				String phone = paramsMap.get(AppParam.AON_CUSTOMIZE_SUPPORT_PHONE.toString());
				String title = paramsMap.get(AppParam.AON_CUSTOMIZE_TITLE.toString());
				String idStr = paramsMap.get(AppParam.AON_CUSTOMIZE_ID.toString());
				Integer id = AonNumberUtils.toInteger(idStr);
				String heritableIdStr = paramsMap.get(AppParam.AON_CUSTOMIZE_HERITABLE_ID.toString());
				Integer heritableId = AonNumberUtils.toInteger(heritableIdStr);

				byte [] bytes = is.readAllBytes();
				String css = new String(bytes, StandardCharsets.UTF_8);
			
				css = AonStringUtils.replace(css, "themeCustomGwt", theme == null ? "/css/theme/aon.gwt.css" : theme.replace(".css", ".gwt.css"));
				css = AonStringUtils.replace(css, "themeCustom", theme== null ? "/css/theme/aon.css" : theme);

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
				css = AonStringUtils.replace(css, "faviconCustom", "cvDocument/faviconCustom");
				
				String[] customizableVariables = getThemeCustomizableVariables(req, theme == null ? "/css/theme/aon.css" : theme);
				String customizableVariablesCss =
				AON.getApplicationParameterStream(
				        domainStr, 
				        customViewdomainId, 
				        "", 
				        p -> p.getDomainProperty().eq(customViewdomainId)
			            .and(p.getNameProperty().in(customizableVariables))
						).map(appParam -> String.format("--%s: %s;", fromUpperUnderscoreToCamelCase(appParam.getName()), appParam.getValue()))
						.collect(Collectors.joining("\n"));
				css = AonStringUtils.replace(css, "/*aonCustomizableVariables*/", customizableVariablesCss);
				
				byte[] finalCssBytes = css.getBytes(StandardCharsets.UTF_8);
				resp.setContentLength(finalCssBytes.length);  
				resp.getOutputStream().write(finalCssBytes);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
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
	
	private static String[] getThemeCustomizableVariables(HttpServletRequest req, String theme) {
		try ( InputStream is = req.getServletContext().getResourceAsStream(theme) ) {
			byte [] bytes  = is.readAllBytes();
			 // Assuming the CSS file is not too large to fit into memory
			String css = new String(bytes, StandardCharsets.UTF_8);
			// Extract customizable variables from the CSS content
			return CUSTOMIZABLE_VARIABLE_PATTERN.matcher(css).results()
					.map(m -> m.group("name"))
					.map(CustomViewServlet::fromCamelCaseToUpperUnderscore)
					.toArray(String[]::new);
			
		} catch (IOException e) {
			return new String[0];
		}
	}
	
	private static String fromCamelCaseToUpperUnderscore(String camelCase) {
	    return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
	}
	
	private static String fromUpperUnderscoreToCamelCase(String upperUnderscore) {
	    StringBuilder result = new StringBuilder();
	    boolean nextUpper = false;
	    for (char c : upperUnderscore.toCharArray()) {
	        if (c == '_') {
	            nextUpper = true;
	        } else {
	            if (nextUpper) {
	                result.append(Character.toUpperCase(c));
	                nextUpper = false;
	            } else {
	                result.append(Character.toLowerCase(c));
	            }
	        }
	    }
	    return result.toString();
	}
}
