package net.aonsolutions.aon.tedi.test;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RawdocDAO;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

import es.translogia.tedi.json.TediInvoiceJSON;
import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;


public class FillRawdoc {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "queserialascortas.ecastellano.euk";
	private static int DOMAIN_ID = 18539;
	private static String USER = "admin";
	private static Faker FAKER = new Faker(new Locale("es"));
	
	private static String[] FILES = new String[]{
//			"/home/ecastellano/TRABAJO/RAWDOC/192300.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/192677.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/192678.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/192682.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/192683.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/192684.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/192777.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/192993.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193174.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193176.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193179.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193180.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193181.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193183.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193263.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193265.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193266.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193394.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193396.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/193397.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/20191211FacturaBipDrive005420175264.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/AON.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/AON.png"
//			,"/home/ecastellano/TRABAJO/RAWDOC/ARALAB.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/AYSER.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/AYSER.png"
//			,"/home/ecastellano/TRABAJO/RAWDOC/b-font.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/EMBALAJES_BASKONIA.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Europool.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Gas Natural I.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Iberdrola I.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura Iberdrola II.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Movistar_Fijo II.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Movistar_Fijo I-UDAPA.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Movistar_Fusion I.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Movistar_Fusion II.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Movistar_Fusion III.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Movistar_Fusion IV.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Movistar_Fusion V.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Naturgy I.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Naturgy II.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura_Naturgy (Negativa) I.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura Aon solutions ORTUN.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura Orange I.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura Orange II.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Factura Orange III.pdf"
//			"/home/ecastellano/TRABAJO/RAWDOC/FRUTAS IRU.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/IMQ.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/LABORAL_KUTXA.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/LOGIFRUIT.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/NOTAR1.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/NOTAR2.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/NOTARIO_SUPLIDOS.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-174538.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-174539.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-174540.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-174628.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-174764.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-174773.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-174884.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175517.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175519.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175543.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175544.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175546.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175547.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175559.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175551.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175560.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175562.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/OK-175601.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/ORONA.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/RETEN.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/SIMA.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Ticket1.jpg"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Ticket1_AON.jpg"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Ticket2.jpg"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Ticket2_AON.jpg"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Ticket3.jpg"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Ticket4.png"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Ticket5.jpg"
//			,"/home/ecastellano/TRABAJO/RAWDOC/Ticket_translogia.jpg"
//			,"/home/ecastellano/TRABAJO/RAWDOC/UDAPA1.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/UDAPA2.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/UDAPA3.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/UDAPA4.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/ULMA.pdf"
//			,"/home/ecastellano/TRABAJO/RAWDOC/ULMA2.pdf"
//			,"/home/ecastellano/Pictures/SIMA.png"
//			,"/home/ecastellano/Pictures/FRUTAS.png"
//			"/home/ecastellano/TRABAJO/Facturas/Factura_OSCAR.xlsx"
			"/home/ecastellano/TRABAJO/Facturas/Modelo-factura.docx"
	};
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, TediException {
		
		Class.forName( org.mariadb.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER);
		AonConfiguration aonConfiguration = ConfigurationDAO.getConfiguration(ctx);
		
//		for (int i = 0; i < 10; i++) {
			
			for (String file : FILES) {
				System.out.print( "Reading file " + file);
				int index = AonStringUtils.lastIndexOf(file, ".");
				System.out.print( " -- " + index  );
				String extension = AonStringUtils.substring(file, index+1);
				System.out.print( " -- " + extension  );
				MimeType mimetype = MimeType.safeValueFromExtension(extension);
				System.out.print( " -- " + mimetype.getName());
				
				FileInputStream input = new FileInputStream(file);
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				AonIOUtils.copy(input, output);
				TediContext tctx = new TediContext()
						.setDomain(DOMAIN_ID)
						.setDomainName(DOMAIN_NAME)
						.setUser(USER)
						.setAONContext(ctx)
						.setAonConfiguration(aonConfiguration);
				Rawdoc rawdoc = new Rawdoc()
						.setDomain(DOMAIN_ID)
						.setNature(RawdocNature.INVOICE)
						.setType(  randomType() )
						.setStatus( randomStatus() )
						.setMimeType( mimetype )
						.setData(output.toByteArray())
						.setJson( (mimetype != MimeType.PDF) ? null :TediInvoiceJSON.toJSON(TEDI.parse(tctx, new FileInputStream(file)).getTedi()).toString());
				System.out.println( );
				RawdocDAO.insert(ctx, rawdoc);
			}
//		}
		
		
		
	}

	private static RawdocType randomType() {
		return (98 >= FAKER.random().nextInt(0,100)) ? RawdocType.INPUT : RawdocType.OUTPUT;
	}

	private static RawdocStatus randomStatus() {
		int i = FAKER.random().nextInt(0,100);
		if (i > 98) return RawdocStatus.DRAFT;
		if (i > 90) return RawdocStatus.REJECTED;
		return RawdocStatus.INBOX;
	}
	
}


