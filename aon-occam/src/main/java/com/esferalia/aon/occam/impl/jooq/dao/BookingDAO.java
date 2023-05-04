package com.esferalia.aon.occam.impl.jooq.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class BookingDAO {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final String MAX_TOTAL_DOCUMENT_SIZE = "maxTotalDocumentSize";
	private static final String DOMAIN_MANAGEMENT = "domainManagement";
	private static final String APPS = "apps";
	private static final String NUMBER_OF_USERS = "numberOfUsers";
	private static final String TYPE = "type";
	private static final String USER = "user";
	private static final String OCR = "ocr";
	private static final String NAME = "name";
//	private static final String PARENT = "parent";
//	private static final String AUTO_UPDATE = "autoUpdate";
//	private static final String PAYER = "payer";
	
	private BookingDAO() {
	
	}
	
	public static Booking get(AONContext ctx, Domain domain) {
		Company company = CompanyDAO.getCompanyStream(ctx, f -> 
			f.getDomainProperty().eq(domain.getId()))
			.findFirst().orElse(new Company());
		
		Booking booking = new Booking()
				.setDomain(domain)
				.setCompany(company)
				.setNumberOfUsers(domain.getMaxDefinedUsers())
				.setPayer("");
		
		booking.setApps(SecurityDAO.getDomainAppStream(ctx, f -> 
			f.getDomainProperty().eq(domain.getId()).and(f.getActiveProperty().eq((byte) 1))
		).map(r -> r.getApp()).collect(Collectors.toCollection(LinkedList::new)));
		
		if(domain.isChild()) {
			booking.setParentApps(SecurityDAO.getDomainAppStream(ctx, f -> 
				f.getDomainProperty().eq(domain.getParentId()).and(f.getActiveProperty().eq((byte) 1))
			).map(r -> r.getApp()).collect(Collectors.toCollection(LinkedList::new)));
		}
		
		return booking;
	}
		
	public static Booking save(AONContext ctx, Booking booking) {
		if(booking.getNumberOfUsers() != null) {
			SecurityDAO.saveDomainMaxDefinedUser(ctx, booking.getNumberOfUsers());
		}
		ApplicationParameter domainPayer = AppParamDAO.fetchOne(ctx, AppParam.AON_DOMAIN_PAYER);
		if(!AonStringUtils.isBlank(booking.getPayer())) {
			if(domainPayer == null) {
				AppParamDAO.saveApplicationParameter(ctx, new ApplicationParameter()
					.setDomain(booking.getDomain().getId())
					.setName(AppParam.AON_DOMAIN_PAYER)
					.setValue(booking.getPayer()));
			}
		} else if(domainPayer != null) {
			AppParamDAO.deleteApplicationParameter(ctx, f -> 
				f.getIdProperty().eq(domainPayer.getId()));
		}
		
		saveBookingHistory(ctx, booking);
		
		AonApp.getValues().stream().filter(f -> !booking.getApps().contains(f))
			.forEach(app -> saveBookingApp(ctx, app, false));
		
		booking.getApps().forEach(app -> saveBookingApp(ctx, app, true));
		if(!DomainType.CONSULTANCY.equals(booking.getDomain().getDomainType())
				&& !booking.getApps().contains(AonApp.BASIC_MANAGEMENT)
				&& !booking.getApps().contains(AonApp.STANDAR_MANAGEMENT)
				&& !booking.getApps().contains(AonApp.PROFESSIONAL_MANAGEMENT)) {
			SecurityDAO.insertDomainModule(ctx, Module.AON_FINANCE);
		} else SecurityDAO.deleteDomainModule(ctx, Module.AON_FINANCE);
	
		return booking;
	}
	
	private static void saveBookingApp(AONContext ctx, AonApp app, boolean active) {
		DomainApp domainApp = SecurityDAO.getDomainAppStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getAppProperty().eq(app.value())))
			.findFirst().orElse(new DomainApp());
		
		if(active) {
			domainApp.setDomain(ctx.getDomainId())
				.setApp(app)
				.setActive(true);
		} else if(!domainApp.isEmpty()) {
			domainApp.setActive(false);
		}
		
		if(!domainApp.isEmpty())
			SecurityDAO.saveDomainApp(ctx, domainApp);
	}

	private static void saveBookingHistory(AONContext ctx, Booking booking) {
		Date date = new Date();
		Attach attach = new Attach(AttachType.REGISTRY)
			.setDomain(booking.getCompany().getDomain())
			.setAttachModule(booking.getCompany().getId())
			.setType(RegistryAttachmentType.DOMAIN_BOOK_HISTORY.value())
			.setDescription(AonDateUtils.format(date, "yyyyMMddHHmmss"))
			.setMimeType(MimeType.TXT)
			.setDate(date)
			.setData(getData(ctx, booking));
		
		AttachmentDAO.insertRegistryAttach(ctx, attach);	
	}
	
	private static byte[] getData(AONContext ctx, Booking booking) {
		Properties properties = new Properties();
		properties.setProperty(NAME, booking.getDomain().getName());	
		
//		if(! StringUtils.isEmpty(parent) ) {
//			properties.setProperty(PARENT, parent);	
//		}
//		if (! StringUtils.isEmpty(payer) ) {
//			properties.setProperty(PAYER, payer);	
//		}
		properties.setProperty(TYPE, booking.getDomain().getDomainType().toString());	
		properties.setProperty(NUMBER_OF_USERS, booking.getNumberOfUsers().toString());	
		properties.setProperty(MAX_TOTAL_DOCUMENT_SIZE, booking.getDomain().getMaxTotalDocumentSize().toString());	
	
		properties.setProperty(DOMAIN_MANAGEMENT, Boolean.toString(booking.getDomain().isDomainManagement()));
		properties.setProperty(USER, ctx.getUser());	
		
//		if (! bookinModules.isEmpty() ) {
//			String modulesValue = StringUtils.join(bookinModules, " ");
//			properties.setProperty(MODULES, modulesValue);			
//		}
//		if (! displayModules.isEmpty() ) {
//			String modulesValue = StringUtils.join(displayModules, " ");
//			properties.setProperty(DISPLAY_MODULES, modulesValue);			
//		}
		
		String appsValue = StringUtils.join(booking.getApps(), " ");
		properties.setProperty(APPS, appsValue);

		if(booking.getApps().contains(AonApp.OCR) ) {
			properties.setProperty(OCR, Boolean.TRUE.toString());
		}
		
//		if ( autoUpdate ) {
//			properties.setProperty(AUTO_UPDATE, Boolean.TRUE.toString());
//		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			properties.store(bos, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}
	
}
