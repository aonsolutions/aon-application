package com.esferalia.aon.occam.impl.jooq.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jooq.tools.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

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
	
	public static Booking save(AONContext ctx, Booking booking) {
		if(booking.getNumberOfUsers() != null) {
			SecurityDAO.saveDomainMaxDefinedUser(ctx, booking.getNumberOfUsers());
		}
		
		saveBookingHistory(ctx, booking);
		
		AonApp.getValues().stream().forEach(app -> {
			DomainApp domainApp = SecurityDAO.getDomainAppStream(ctx, f -> 
				f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getAppProperty().eq(app.value())))
				.findFirst().orElse(new DomainApp());
	
			if(booking.getApps().contains(app)) {
				domainApp.setDomain(ctx.getDomainId())
					.setApp(app)
					.setActive(true);
			} else if(!domainApp.isEmpty()) {
				domainApp.setActive(false);
			}
			
			if(!domainApp.isEmpty())
				SecurityDAO.saveDomainApp(ctx, domainApp);
		});
		return booking;
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
