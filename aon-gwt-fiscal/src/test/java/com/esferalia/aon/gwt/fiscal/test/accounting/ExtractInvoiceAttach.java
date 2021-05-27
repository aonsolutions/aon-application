package com.esferalia.aon.gwt.fiscal.test.accounting;


import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;


public class ExtractInvoiceAttach {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "udapa.ecastellano.euk";
	private static int DOMAIN_ID = 3049;
	private static String USER = "montse";
	
	
	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException {
		
		Class.forName( org.mariadb.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER);
		
		DomainGserviceaccount g = AON.getDomainGserviceaccount(DOMAIN_NAME, DOMAIN_ID, USER);
		Drive drive = AonDrive.getInstace().serviceInitialize(g);
		
		
		Date start = AonDateUtils.toSql( AonDateUtils.getDate(2018, 7, 1));
		Date end = AonDateUtils.toSql( AonDateUtils.getDate(2018, 7, 30));
		ctx.getDslContext().select( INVOICE.ID, INVOICE_ATTACH.ID, INVOICE_ATTACH.DRIVEID, INVOICE_ATTACH.MIMETYPE )
			.from(INVOICE)
			.innerJoin(INVOICE_ATTACH).on(INVOICE.ID.eq(INVOICE_ATTACH.INVOICE))
			.where(INVOICE.DOMAIN.eq(DOMAIN_ID))
			.and(INVOICE.TYPE.eq( InvoiceType.EXPENSES.value()))
			.and(INVOICE.ISSUE_DATE.ge(start))
			.and(INVOICE.ISSUE_DATE.le(end))
			.and(INVOICE_ATTACH.DRIVEID.isNotNull())
			.limit(20)
		.fetch()
		.stream()
		.forEach(rec -> {
			try {
				MimeType mime = MimeType.safeValueOf(rec.getValue(INVOICE_ATTACH.MIMETYPE)); 
				Integer id = rec.getValue(INVOICE_ATTACH.ID);
				// AttachmentDAO.getInvoiceAttachStream(ctx, filter, true);
				Attach attach = AON.getAttach(DOMAIN_NAME, DOMAIN_ID, USER
						, f -> f.getIdProperty().eq(id), AttachType.getAttachType(AttachType.INVOICE.getName()), true);
				System.out.println( " --> " + attach.getId() );
				attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
				FileOutputStream output = new FileOutputStream("/home/ecastellano/TRABAJO/Facturas2/" + id + "." + mime.getExtension());
				AonIOUtils.write(attach.getData(), output);
				output.flush();
				output.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}


