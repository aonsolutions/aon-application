package net.aonsolutions.aon.api.servlet.marketing;

import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

public class MarketingEmailTemplates {
	
	private static boolean isLocal = false;

	public static String createEnterpriseDuplicateBody(CloseableAONContext ctx, AonApiData api, Domain domain, Domain parentDomain) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		String url = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
				+ "/app";
		
		if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "app.leevy.es"))
			url = "https://leevy.aon.solutions";
		else if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "infoautonomos.aonsolutions.net"))
			url = "https://infoautonomos.aon.solutions";
		
		String logoUrl = getLogoUrl(parentDomain, api.getUser());
		String from = getFromMessage(ctx, api, parentDomain);
		
		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", domain.getDescription());
		context.put("url", url);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_duplicate.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	
	
	public static String getFinishCreationHtml(AonApiData api, Domain parentDomain, Domain newDomain, String urlMail, String userMail) {
		JSONObject data = api.getData();

		// Enterprise Data
		String name = JsonUtils.getString(data, "name");
		
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		String url = (isLocal ? "http" : "https") + "://" + newDomain.getName() + (isLocal ? ":8080" : "") + "/";
		
		String logoUrl = getLogoUrl(parentDomain, api.getUser());

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", name);
		context.put("url", url);
		context.put("domainName", urlMail);
		context.put("mail", userMail);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_created_response_ayudat.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	public static String getLogoUrl(Domain parentDomain, User user) {
		String logoUrl = null;
		try (CloseableAONContext aonParentContext = AONContext.getAONContext(parentDomain.getName(), user.getLogin())) {
			
			Company company = AON.getCompany(parentDomain, user, f -> f.getDomainProperty().eq(parentDomain.getId()));

			Attach attach = getLogoAttach(aonParentContext, company.getId());

			String str = "domain=" + attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
			String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));

			Domain attachDomain = DomainDAO.getDomain(aonParentContext, attach.getDomain().getId());
			logoUrl = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
					+ "/ms/download_attachment/" + attachDomain.getName() + "/" + attach.getCreationUser() + "/"
					+ result;
		}

		return logoUrl;
	}

	public static Attach getLogoAttach(AONContext aonContext, Integer enterpriseId) {
		Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
				f -> f.getTypeProperty().eq(SIGNATURE.value()).and(f.getAttachModuleProperty().eq(enterpriseId)),
				REGISTRY);

		if (attach1 == null || attach1.getData() == null)
			attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(LOGO.value()).and(f.getAttachModuleProperty().eq(enterpriseId)),
					REGISTRY);

		return attach1;
	}
	
	public static String getFromMessage(CloseableAONContext ctx, AonApiData api, Domain parent) {
		DomainUserRoles domainUserRoles = SecurityDAO.getDomainUserRoles(ctx, api.getUser().getId()); 
		
		String from = null;

		if (domainUserRoles.hasParentCustomView() || domainUserRoles.hasCustomView()) {
			
			// Ya es el dominio padre el que hay en api.getDomain()
			if (null == api.getDomain().getParentId() && (null == parent || null == parent.getId())) {
				
				Enterprise enterprise = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(api.getDomain().getId()));
				
				RegistryMedia emailMedia = RegistryMediaDAO.get(ctx, f -> f.getRegistryProperty().eq(enterprise.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
				
			// Se busca el dominio padre
			
			} else if (null != parent && null != parent.getId()) {
				
				Enterprise enterprise = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(parent.getId()));
				RegistryMedia emailMedia = RegistryMediaDAO.get(ctx, f -> f.getRegistryProperty().eq(enterprise.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
				
			}
		}

		return from;
	}
	
}
