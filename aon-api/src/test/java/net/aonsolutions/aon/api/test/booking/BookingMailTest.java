package net.aonsolutions.aon.api.test.booking;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import net.aonsolutions.aon.api.servlet.booking.BookingMail;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

public class BookingMailTest {
	Faker faker = new Faker();

	@Test
	public void test() {
		String to = "aibanez@aonsolutions.es";
		String subject = "BOOKING MAIL TEST";
		String body = content();
	
		SESMessage msg = new SESMessage()
				.setTo(to)
				.setFrom("booking@aon.solutions")
				.setReplyTo("admin@aonsolutions.es")
				.setSubject(subject)
				.setBody(body);
		SES.sendEmailWithAttachment(msg);
	}
	
	private String content() {
		BookingMail booking = new BookingMail()
				.setUser(faker.artist().name())
				.setCompanyName(faker.rockBand().name())
				.setDomainName(faker.internet().domainName())
				.setDomainType(faker.superhero().name())
				.setNumberOfUsers(faker.number().randomDigit())
				.setParentName(faker.rockBand().name());
		List<String> list = new LinkedList<>();
		for (int i = 0; i < 5; i++) {
			list.add(faker.pokemon().name());
		}
		booking.setApps(list);
		
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
	
		
		VelocityContext context = new VelocityContext();
		context.put("booking", booking);
		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
}
