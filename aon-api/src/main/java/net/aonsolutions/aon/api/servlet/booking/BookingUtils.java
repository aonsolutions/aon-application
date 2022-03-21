package net.aonsolutions.aon.api.servlet.booking;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.server.io.AonFileUtils;

import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

public class BookingUtils {

	private static final String LEGAL_WARNING_PATH = "/net/aonsolutions/aon/api/servlet/avisoLegal.pdf";
	
	public static BookingUtils getInstance() {
		return new BookingUtils();
	}
	
	public void sendMail(Domain domain, User user, Booking b) {
		String subject = "Contratación Aon Solutions";
		String body = content(domain, user, b);
		
		File file = null;
		try {
			file = File.createTempFile("avisoLegal", ".pdf");
			AonFileUtils.writeByteArrayToFile(file, getTermsOfServiceData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SESMessage msg = new SESMessage()
				.setTo(domain.getOwner())
				.setBcc("admin@aonsolutions.es")
				.setFrom("booking@aon.solutions")
				.setReplyTo("admin@aonsolutions.es")
				.setSubject(subject)
				.setBody(body)
				.setFile(file);
		SES.sendEmailWithAttachment(msg);
	}
	
	public static byte[] getTermsOfServiceData() {
		InputStream in = null;
		byte[] data = null;
		try {
			in = BookingUtils.class.getResourceAsStream(LEGAL_WARNING_PATH);
			data = IOUtils.toByteArray(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}	
		return data;
	}
	
	private String content(Domain domain, User user, Booking b) {
		BookingMail booking = new BookingMail()
				.setUser(user.getName())
				.setCompanyName(domain.getDescription())
				.setDomainName(domain.getName())
				.setDomainType(getDomainTypeStr(domain.getDomainType()))
				.setNumberOfUsers(b.getNumberOfUsers())
				.setApps(b.getApps().stream().map(app -> getAppStr(app))
						.collect(Collectors.toCollection(LinkedList::new)));
				
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
	
	private String getAppStr(AonApp app) {
		if(AonApp.ACCOUNTING.equals(app)) return "Contabilidad";
		else if(AonApp.AULA.equals(app)) return "Aula";
		else if(AonApp.BANK.equals(app)) return "Bank";
		else if(AonApp.BASIC_MANAGEMENT.equals(app)) return "Gestión Básica";
		else if(AonApp.STANDAR_MANAGEMENT.equals(app)) return "Gestión Estándar";
		else if(AonApp.PROFESSIONAL_MANAGEMENT.equals(app)) return "Gestión Profesional";
		else if(AonApp.COMUNICA.equals(app)) return "Comunic@";
		else if(AonApp.CONVENIOS.equals(app)) return "Convenios";
		else if(AonApp.DOCUMENTAL.equals(app)) return "Documental";
		else if(AonApp.FISCAL.equals(app)) return "Fiscal";
		else if(AonApp.INVOICE.equals(app)) return "Facturas";
		else if(AonApp.MESSENGER.equals(app)) return "Solicitudes";
		else if(AonApp.OCR.equals(app)) return "OCR";
		else if(AonApp.PACK_FISCAL_ACCOUNTING.equals(app)) return "Pack Tributación";
		else if(AonApp.PACK_PAYROLL.equals(app)) return "Pack Cotización";
		else if(AonApp.PACK_PORTAL.equals(app)) return "Pack Portal";
		else if(AonApp.PACK_SUITE.equals(app)) return "Suite Completa";
		else if(AonApp.PAYROLL.equals(app)) return "Laboral";
		else if(AonApp.SALTRA.equals(app)) return "Saltra";
		else if(AonApp.TIMECONTROL.equals(app)) return "Control Horario";
		return "";
	}
	
	private String getDomainTypeStr(DomainType type){
		if(DomainType.GARAGE.equals(type)) return "Taller";
		else if(DomainType.ENTERPRISE.equals(type)) return "Empresa";
		else if(DomainType.ACADEMY.equals(type)) return "Academia";
		else if(DomainType.COMMERCE.equals(type)) return "Comercio";
		else if(DomainType.CONSULTANCY.equals(type)) return "Asesoría";
		else if(DomainType.HOTEL.equals(type)) return "Hotel";
		else if(DomainType.OFFICE.equals(type)) return "Despacho";
		else if(DomainType.KIT_DIGITAL.equals(type)) return "Kit Digital";
		return "";
	}
}
