package net.aonsolutions.aon.api.servlet.booking;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

public class BookingUtils {
	
	public static BookingUtils getInstance() {
		return new BookingUtils();
	}
	
	public void sendMail(Domain domain, User user, Booking oldBooking, Booking newBooking) {
		String subject = "Modificación de Contratación en " + domain.getName();
		String body = content(domain, user, newBooking);

		SESMessage msg = new SESMessage()
				.setAlias("AON Solutions | Contrataciones")
				.setTo(domain.getOwner())
				.addBcc("admin@aonsolutions.es")
				.addBcc("administracion@aonsolutions.es")
				.addBcc("asignacion@aonsolutions.es")
				.setFrom("booking@aon.solutions")
				.setReplyTo("asignacion@aonsolutions.es")
				.setSubject(subject)
				.setBody(body)
				.setFiles(getFiles(oldBooking, newBooking));

		SES.sendEmailWithAttachment(msg);
	}
	
	private List<File> getFiles(Booking oldBooking, Booking newBooking) {
		LinkedList<File> list = new LinkedList<>();
		File file = getDiffFile(oldBooking, newBooking);
		if(file != null) list.add(file);
		return list;
	}

	
	private File getDiffFile(Booking oldBooking, Booking newBooking) {
		File file = null;
		String diff = getDifferences(oldBooking, newBooking);
		if (! StringUtils.isEmpty(diff) ) {
			try {
				file = File.createTempFile( "diff", "." + MimeType.MIME_TXT.getExtension() );
				AonFileUtils.writeStringToFile(file, diff);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	private String content(Domain domain, User user, Booking b) {
		BookingMail booking = new BookingMail()
				.setUser(user.getName())
				.setCompanyName(domain.getDescription())
				.setDomainName(domain.getName())
				.setDomainType(getDomainTypeStr(domain.getDomainType()))
				.setNumberOfUsers(b.getNumberOfUsers())
				.setApps(b.getApps().stream().map(app -> app.getDescription())
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
	
	public String getDifferences(Booking b1, Booking b2) {
		StringBuilder sb = new StringBuilder();	
		if(!b1.getDomain().getDomainType().equals(b2.getDomain().getDomainType())){
			diff(sb, "Tipo de Dominio", getDomainTypeStr(b1.getDomain().getDomainType()),
					getDomainTypeStr(b2.getDomain().getDomainType()));
		}
		
		if (!ObjectUtils.equals(b1.getNumberOfUsers(), b2.getNumberOfUsers())) {
			diff(sb, "Número de usuarios contratados", b1.getNumberOfUsers(), b2.getNumberOfUsers());
		}
		if (!ObjectUtils.equals(b1.getDomain().getMaxTotalDocumentSize(), b2.getDomain().getMaxTotalDocumentSize())) {
			diff(sb, "Tamaño contratado", b1.getDomain().getMaxTotalDocumentSize(), b2.getDomain().getMaxTotalDocumentSize());
		}
		if (b1.getDomain().isDomainManagement() != b2.getDomain().isDomainManagement()) {
			diff(sb, "Multidominio", b1.getDomain().isDomainManagement(), b2.getDomain().isDomainManagement());
		}
		
		if (!b1.getApps().equals(b2.getApps())) {
			diffList(sb, "Aplicaciones y servicios contratados", b1.getApps(), b2.getApps());
		}
		
		if (!AonStringUtils.equals(b1.getPayer(), b2.getPayer())) {
			diff(sb, "Dominio Pagador", (b2.getPayer() != null ? b2.getPayer() : "-"));
		} 
		return sb.toString();
	}

	private void diff( StringBuilder sb, String message, Object oldValue, Object newValue ) {
		diff( sb, message, oldValue + " -> "+ newValue );
	}

	private void diff( StringBuilder sb, String message, String differences ) {
		if ( sb.length() > 0 ) {
			sb.append(", ");
		}
		sb.append(message).append(": ").append( differences );
		sb.append("\n");
	}

	private void diff( StringBuilder sb, char sign, List<AonApp> list1, List<AonApp> list2 ) {
		for( AonApp app : list1 ) {
			if (! list2.contains(app) ) {
				if ( sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(sign).append(app.getDescription());
			}
		}				
	}

	private void diffList( StringBuilder sb, String message, List<AonApp> list1, List<AonApp> list2 ) {
		StringBuilder differences = new StringBuilder();
		diff( differences, '-', list1, list2 );
		diff( differences, '+', list2, list1 );
		diff(sb, message, differences.toString());
	}	
	
}
