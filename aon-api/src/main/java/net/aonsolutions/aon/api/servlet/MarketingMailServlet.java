package net.aonsolutions.aon.api.servlet;

import java.io.StringWriter;
import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;

import net.aonsolutions.aon.api.model.mail.Data;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "MarketingMailServlet", urlPatterns = {"/ms/api/marketing/mail/*"})
public class MarketingMailServlet extends AonApiHttpServlet{
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		JSONObject json = getRequestJSON(req);
	
		String to = json.opt(IJsonNames.SERVICE) != null 
				? JsonUtils.getString(json, IJsonNames.SERVICE) + "@aonsolutions.es"
				: "info@aonsolutions.es";

		String serviceName = JsonUtils.getString(json, IJsonNames.SERVICE_NAME);
		String name = JsonUtils.getString(json, IJsonNames.NAME);
		String mail = JsonUtils.getString(json, IJsonNames.EMAIL);

		LinkedList<Data> list = new LinkedList<>();
		json.toMap().keySet().stream().forEach(r -> {
			Data data = new Data(r, json.optString(r));
			list.add(data);
		});
		
		SESMessage sesMessage = new SESMessage()
				.setSubject(serviceName)
				.setTo(to)
				.setReplyTo(mail)
				.setAlias(name)
				.setBody(dataContent(list));
		
		SES.sendEmail(sesMessage);
		
		SESMessage responseMessage = new SESMessage()
				.setSubject(serviceName)
				.setTo(mail)
				.setBody(responseContent());
		
		SES.sendEmail(responseMessage);
	}
	
	private String dataContent(LinkedList<Data> list) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("datas", list);
		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/marketing.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private String responseContent() {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/marketing_response.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(new VelocityContext(), writer);

		return writer.toString();
	}
}
