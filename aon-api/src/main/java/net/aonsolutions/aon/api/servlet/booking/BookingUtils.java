package net.aonsolutions.aon.api.servlet.booking;

import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.servlet.Utils;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

public class BookingUtils {
	
	private static final String AON_LOGO = "https://aon.solutions/assets/aon-logo.png";
	
	public static BookingUtils getInstance() {
		return new BookingUtils();
	}
	
	public void sendMail(Domain domain, User user, Booking oldBooking, Booking newBooking, boolean console) {
		String subject = "Modificación de Contratación en " + domain.getName();
		String body = content(domain, user, newBooking, oldBooking);
		Set<String> mails = new HashSet<>();
		
		if(!console) {
			Integer[] domains = domain.isChild() ? new Integer[] {domain.getId(), domain.getParentId()} : new Integer[] {domain.getId()};
			Integer[] companies = AON.getCompanyStream(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().in(domains))
				.map(Company::getId).toArray(Integer[]::new);
		
			List<String> rmediaMails = AON.getRegistryMediaStream(domain, user, f -> f.getRegistryProperty().in(companies).and(f.getDomainProperty().in(domains)).and(f.getMediaProperty().eq((byte)4)))
					.map(RegistryMedia::getValue).toList();
			
			mails.addAll(rmediaMails);
			
			if(Utils.isEmail(domain.getOwner()))
				mails.add(domain.getOwner());
			
			if(domain.isChild()) {
				Domain parent = AON.getDomain(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(domain.getParentId()));
				if(Utils.isEmail(parent.getOwner()))
					mails.add(parent.getOwner());
			}
		} else mails.add("admin@aonsolutions.es");
		
		String from = getFromMessage(domain, user);
		String alias = AonStringUtils.isBlank(from)  ? formatUT8B("AON Solutions | Contrataciones") : formatUT8B(domain.getDescription() + " | Contrataciones");
		
		SESMessage msg = new SESMessage()
				.setAlias(alias)
				.setTo(mails.stream().toList())
				.addBcc("admin@aonsolutions.es")
				.addBcc("administracion@aonsolutions.es")
				.addBcc("asignacion@aonsolutions.es")
				.setFrom(AonStringUtils.isBlank(from) ? "booking@aon.solutions" : from)
				.setReplyTo(AonStringUtils.isBlank(from) ? "asignacion@aonsolutions.es" : from)
				.setSubject(subject)
				.setBody(body);
		
//		if(AonStringUtils.isBlank(from))
//			msg.setFiles(getFiles(oldBooking, newBooking));

		SES.sendEmail(msg);
	}
	
    /**
     * Returns the parameter formated to UTF8 & Base64
     */
    private static String formatUT8B(final String text) {
    	if(AonStringUtils.isBlank(text)) return text;
        return "=?UTF-8?B?" + Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)) + "?=" ;
    }
	
	private static String getFromMessage(Domain domain, User user) {
		DomainUserRoles domainUserRoles = SECURITY.getDomainUserRoles(domain, user.getLogin(), user.getId());
		String from = null;

		if (domainUserRoles.hasParentCustomView() || domainUserRoles.hasCustomView()) {
			Domain parentDomain = AON.getDomain(domain.getName(), domain.getId(),
					user.getLogin(), f -> f.getIdProperty().eq(domain.getParentId()));
			// Ya es el dominio padre el que hay en api.getDomain()
			if (null == domain.getParentId() && (null == parentDomain || null == parentDomain.getId())) {
				RegistryMedia emailMedia = AON.getRegistryMedia(domain,user,
						f -> f.getDomainProperty().eq(domain.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
			// Se busca el dominio padre
			} else if (null != parentDomain && null != parentDomain.getId()) {
				RegistryMedia emailMedia = AON.getRegistryMedia(domain, user,
						f -> f.getDomainProperty().eq(parentDomain.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
			}
		}

		return from;
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
	
	private String content(Domain domain, User user, Booking b, Booking oldBooking) {
		BookingMail booking = new BookingMail()
				.setUser(user.getName())
				.setCompanyName(domain.getDescription())
				.setDomainName(domain.getName())
				.setDomainType(getDomainTypeStr(domain.getDomainType()))
				.setNumberOfUsers(b.getNumberOfUsers())
				.setApps(b.getApps().stream().map(app -> app.getDescription())
						.collect(Collectors.toCollection(LinkedList::new)));
		
		String logo = "";
		String parentName = domain.getDescription();
		if(domain.getParentId() == null) logo = getLogoUrl(domain, user);
		else {
			Domain parentDomain = AON.getDomain(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(domain.getParentId()));
			logo = getLogoUrl(parentDomain, user);
			parentName = parentDomain.getDescription();
		}
		
		DomainUserRoles domainUserRoles = SECURITY.getDomainUserRoles(domain, user.getLogin(), user.getId());
		
		String domainChange = null;
		if(!oldBooking.getDomain().getDomainType().equals(b.getDomain().getDomainType()))
			domainChange = "El tipo de dominio ha cambiado de " + getDomainTypeStr(oldBooking.getDomain().getDomainType()) + " a " + getDomainTypeStr(b.getDomain().getDomainType());
		
		String usersChange = null;
		if (!ObjectUtils.equals(oldBooking.getNumberOfUsers(), b.getNumberOfUsers()))
			usersChange = "El numero de usuarios ha cambiado de " + oldBooking.getNumberOfUsers() + " a " + b.getNumberOfUsers();
		
		String sizeChange = null;
		if (!ObjectUtils.equals(oldBooking.getDomain().getMaxTotalDocumentSize(), b.getDomain().getMaxTotalDocumentSize()))
			sizeChange = "El tamañano contratado ha cambiado de " + oldBooking.getDomain().getMaxTotalDocumentSize() + " a " + b.getDomain().getMaxTotalDocumentSize();
		
		String multiDomain = null;
		if (oldBooking.getDomain().isDomainManagement() != b.getDomain().isDomainManagement())
			multiDomain = b.getDomain().isDomainManagement() ? "La empresa ahora es multidominio" : "La empresa ha dejado de ser multidominio";
		
		String payerDomain = null;
		if (!AonStringUtils.equals(oldBooking.getPayer(), b.getPayer())) 
			payerDomain = b.getPayer() != null ? "La empresa ahora es dominio pagador" : "La empresa ha dejado de ser dominio pagador";
		
		Set<String> currentApps = b.getApps().stream()
                .map(AonApp::getDescription)
                .collect(Collectors.toSet());
                
		Set<String> oldApps = oldBooking.getApps().stream()
                .map(AonApp::getDescription)
                .collect(Collectors.toSet());
		
		Set<String> addedApps = new HashSet<>(currentApps);
		addedApps.removeAll(oldApps);

        Set<String> removeApps = new HashSet<>(oldApps);
        removeApps.removeAll(currentApps); 
		
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("booking", booking);
		context.put("logo", logo);
		context.put("parentName", parentName);
		context.put("customView", domainUserRoles.hasCustomView() || domainUserRoles.hasParentCustomView());
		context.put("domainChange", domainChange);
		context.put("usersChange", usersChange);
		context.put("sizeChange", sizeChange);
		context.put("multiDomain", multiDomain);
		context.put("payerDomain", payerDomain);
		context.put("addedApps", addedApps);
		context.put("removeApps", removeApps);
		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private static String getLogoUrl(Domain parentDomain, User user) {
		boolean isLocal = false;
		String logoUrl = null;
		try (CloseableAONContext aonContext = AONContext.getAONContext(parentDomain.getName(), user.getLogin())) {
			Company company = AON.getCompany(parentDomain, user, f -> f.getDomainProperty().eq(parentDomain.getId()));

			Attach attach = getLogoAttach(aonContext, company.getId());

			String str = "domain=" + attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
			String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));

			Domain attachDomain = DomainDAO.getDomain(aonContext, attach.getDomain().getId());
			logoUrl = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
					+ "/ms/download_attachment/" + attachDomain.getName() + "/" + attach.getCreationUser() + "/"
					+ result;
		} catch (Exception e) {
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
