package net.aonsolutions.aon.api.servlet;

import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@WebServlet(name = "MagicLinkServlet", urlPatterns = {"/ms/api/magicLink/*"})
public class MagicLinkServlet extends AonApiHttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER  = Logger.getLogger(MagicLinkServlet.class.getName());
	
	private static final String AON_LOGO = "https://aon.solutions/assets/aon-logo.png";
	private static final String AON_FROM = "booking@aon.solutions";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		JSONObject json = getParamsJSON(req);
		get(req, resp, json);

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		JSONObject json = getRequestJSON(req);
		get(req, resp, json);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp, JSONObject json) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			magicLink(json, req.getServerName());
			response(req, resp);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}	
	
	private void magicLink(JSONObject json, String url) {
		String email = JsonUtils.getString(json, IJsonNames.EMAIL);
		if(Utils.isEmail(email)) {
			Auth auth = AON_SOLUTIONS.getAuth(email);
			
			if(auth.isEmpty()) throw new AonApiException(AonApiError.NOT_EXIST_USER.getMessage());
			
			Date expireDate = AonDateUtils.addDays(new Date(), 1);
			String token = AonToken.build(auth, expireDate);
			String magicLink = "https://" + url + "?token=" + token; 
			
			
			List<User> users = getUsersByEmail(email);
			if(!users.isEmpty() && users.size() == 1) {
				User user = users.get(0);
				Company cp = AON.getCompany(user.getDomain().getName(), user.getDomain().getId(), "", 
						f -> f.getDomainProperty().eq(user.getDomain().getId()));
				String companyName = cp.getName().toUpperCase();
				
				sendGmail(auth, magicLink, expireDate, companyName, user);
			} else
				sendGmail(auth, magicLink, expireDate);
		} else throw new AonApiException(AonApiError.NOT_VALID_EMAIL.getMessage());
	}
	
	private List<User> getUsersByEmail(String email){
		List<User> users = new ArrayList<User>();
		List<String> schemas = AONContext.getSchemas();
		
		for(String schema : schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(!AonStringUtils.isBlank(domain)){
				users.addAll( AON_SOLUTIONS.getUsersByEmail(domain, 0, email) );
			}
		}
		
		return users;
	}
	
	public void sendGmail(Auth auth, String magicLink, Date expireDate) {
		SESMessage msg = new SESMessage()
			.setTo(auth.getEmail())
			.setSubject("MAGIC LINK | " + "AON SOLUTIONS")
			.setBody(getContent(auth, magicLink, expireDate));
		SES.sendEmail(msg);
	}
	
	public void sendGmail(Auth auth, String magicLink, Date expireDate, String companyName, User user ) {
		SESMessage msg = new SESMessage()
			.setTo(auth.getEmail())
			.setSubject("MAGIC LINK | " + companyName)
			.setBody(getContent(user, auth, magicLink, expireDate));
		SES.sendEmail(msg);
	}
	
	private String getContent(Auth auth, String magicLink, Date expireDate) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        
        String from = AON_FROM;
        
        VelocityContext context = new VelocityContext();
        context.put("logo", AON_LOGO);
		context.put("parentName", "AON SOLUTIONS");
		context.put("name", auth.getName());
        context.put("expireDate", AonDateUtils.format(expireDate, "dd/MM/yyyy HH:mm"));
        context.put("magicLink", magicLink);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);
        
        Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/magic_link.vm");
        
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }
	
	private String getContent(User user, Auth auth, String magicLink, Date expireDate) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        
        String from = getFromMessage(user);
        
        VelocityContext context = new VelocityContext();
		context.put("logo", getLogoUrl(user.getDomain()));
		context.put("parentName", user.getDomain().getDescription());
		context.put("name", auth.getName());
        context.put("expireDate", AonDateUtils.format(expireDate, "dd/MM/yyyy HH:mm"));
        context.put("magicLink", magicLink);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);
        
        Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/magic_link.vm");
        
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }
	
	private static String getLogoUrl(Domain domain) {
		String logoUrl = AON_LOGO;
		
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain.getName(), 0, "")) {
			Company company = AON.getCompany(domain, new User().setLogin(""), f -> f.getDomainProperty().eq(domain.getId()));

			Attach attach = getLogoAttach(aonContext, company.getId());

			String str = "domain=" + attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
			String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));

			Domain attachDomain = DomainDAO.getDomain(aonContext, attach.getDomain().getId());
			logoUrl = "https://" + domain.getName() + "/ms/download_attachment/" + attachDomain.getName() + "/" + attach.getCreationUser() + "/"
					+ result;
			
		} catch (Exception e) {
			e.printStackTrace();
			logoUrl = AON_LOGO;
		}

		return logoUrl;
	}

	private static Attach getLogoAttach(AONContext aonContext, Integer enterpriseId) {
		Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
				f -> f.getTypeProperty().eq(SIGNATURE.value()).and(f.getAttachModuleProperty().eq(enterpriseId)),
				REGISTRY);

		if (attach1 == null || attach1.getData() == null)
			attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(LOGO.value()).and(f.getAttachModuleProperty().eq(enterpriseId)),
					REGISTRY);

		return attach1;
	}

	private static String getFromMessage(User user) {
		String from = null;
		
		DomainUserRoles dur = SECURITY.getDomainUserRoles(user.getDomain(), user.getLogin(), user.getId());

		Domain parentDomain = dur.getDomain().isParent() ? dur.getDomain() : dur.getParentDomain();
		
		if(dur.hasCustomView() || dur.hasParentCustomView()) {
			RegistryMedia emailMedia = AON.getRegistryMedia(user.getDomain(), user,
					f -> f.getDomainProperty().eq(parentDomain.getId()).and(f.getMediaProperty().eq((byte) 4)));
			if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
				from = emailMedia.getValue();
		}
		if(AonStringUtils.isBlank(from)) {
			from = AON_FROM;
		}
		return from;
	}
}
