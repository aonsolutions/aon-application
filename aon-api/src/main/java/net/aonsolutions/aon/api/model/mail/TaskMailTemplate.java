package net.aonsolutions.aon.api.model.mail;

import java.io.StringWriter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class TaskMailTemplate {
	
	public static String taskWorkflowContent(TaskMail tm) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();	
		
		VelocityContext context = new VelocityContext();
		context.put("task", tm);
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/task-historic.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
	}
	
	
	public static String taskContent(TaskMail tm) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();	
		
		VelocityContext context = new VelocityContext();
		context.put("task", tm);
		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/task.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
}
