package com.code.aon.webservice.warehouse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.json.JSONException;

import com.code.aon.webservice.common.PdfUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.server.warehouse.CarrierPackingParams;
import com.esferalia.aon.occam.server.warehouse.XMLUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class Deca extends PdfUtils{
	
	private static final Logger LOGGER  = Logger.getLogger(Deca.class.getName());
	
	private Deca() {
	    throw new IllegalAccessError("Utility class");
	}
	
	public static File createPdf(Domain domain, PackingListContext context, byte [] image) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("DeCA-", "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			
			PageEvent pageEvent = new PageEvent(context, image);
			writer.setPageEvent(pageEvent);
			// Se reserva en los márgenes el espacio que ocupan la cabecera y el pie,
			// que el evento de página pinta en todas las hojas.
			document.setMargins(document.leftMargin(), document.rightMargin(),
					document.topMargin() + pageEvent.getHeaderHeight(document) + HEADER_GAP,
					document.bottomMargin() + pageEvent.getFooterHeight(document) + FOOTER_GAP);
			
			document.open();			
			document.add(getSubHeader(context));
			document.add(new Paragraph(" "));

	
			Double totalPackages = 0.0;
			Double totalWeight = 0.0;
			
			for(Delivery delivery : context.getDeliveries()) {
				document.add(order(delivery));
				totalPackages = totalPackages + delivery.getTotalPackages();
				totalWeight = totalWeight + delivery.getTotalWeight();
				document.add(new Paragraph(" "));
			}
			
			document.add(totalQuantity(context.getDeliveries(), totalPackages, totalWeight));

			Paragraph order = new Paragraph(" ");
			order.add(getSeparator());
			document.add(order);

			String observation = context.getCarrierPacking().getObservation() != null ? context.getCarrierPacking().getObservation() : "";
			CarrierPackingParams params = XMLUtils.readXml(context.getCarrierPacking().getParams());
			if(params.getParam() == null){
				params.setParam(new LinkedList<>());
			}
			document.add(new Paragraph(" "));
			document.add(getObservations(observation, params));		
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));

			document.add(new Paragraph(new Phrase("Firma Transportista", getFont1())));					
		} catch (DocumentException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		document.close();
		return archivoPDF;
	}
	
	// ------------------- HEADER
	
	/** Separación entre la cabecera y el contenido de la página. */
	private static final float HEADER_GAP = 10f;
	
	/** Separación entre el contenido de la página y el pie. */
	private static final float FOOTER_GAP = 10f;
	
	private static final String FOOTER_TEXT = "Este documento puede verificarse escaneando el código QR o a través del siguiente enlace ";
	
	/** Pinta la cabecera y el pie en todas las páginas del documento. */
	private static class PageEvent extends PdfPageEventHelper {
		
		private final PackingListContext context;
		private final byte [] image;
		private float headerHeight = 0f;
		private float footerHeight = 0f;
		
		private PageEvent(PackingListContext context, byte [] image){
			this.context = context;
			this.image = image;
		}
		
		private float getHeaderHeight(Document document){
			PdfPTable header = buildHeader(document, 1);
			headerHeight = header != null ? header.getTotalHeight() : 0f;
			return headerHeight;
		}
		
		private float getFooterHeight(Document document){
			footerHeight = buildFooter(document).getTotalHeight();
			return footerHeight;
		}
		
		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfPTable header = buildHeader(document, writer.getPageNumber());
			if(header != null){
				float y = document.getPageSize().getHeight() - document.topMargin() + headerHeight + HEADER_GAP;
				header.writeSelectedRows(0, -1, document.leftMargin(), y, writer.getDirectContent());
			}
			
			PdfPTable footer = buildFooter(document);
			footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin() - FOOTER_GAP, writer.getDirectContent());
		}
		
		private PdfPTable buildHeader(Document document, int page){
			try {
				PdfPTable header = getHeader(context, image, page);
				lockWidth(header, document);
				return header;
			} catch (DocumentException | IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
				return null;
			}
		}
		
		private PdfPTable buildFooter(Document document){
			PdfPTable footer = getFooter(context.getCarrierPacking());
			lockWidth(footer, document);
			return footer;
		}
		
		private void lockWidth(PdfPTable table, Document document){
			table.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
			table.setLockedWidth(true);
		}
	}
	
	private static PdfPTable getHeader(PackingListContext context, byte [] image, int page) throws BadElementException, MalformedURLException, IOException{
		PdfPTable header1 = new PdfPTable(1);
		header1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		header1.setWidthPercentage(100);
   

		
		PdfPTable header = new PdfPTable(3);
        float[] medidaCeldas = {0.75f, 1.25f, 1f};
		try {
			header.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
        header.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        header.setWidthPercentage(100);
      
        header.addCell(getHeaderLogo(image));
//		header.addCell(getHeaderCompany(context.getCompany()));
		header.addCell(getHeaderQR(context.getCarrierPacking()));
		header.addCell(getHeaderPackingList(context.getCarrierPacking(), page));
	
		header1.addCell(header);
		header1.addCell(getSubHeaderBoeInfo());
		return header1;
	}
	
	private static PdfPCell getHeaderLogo(byte [] image) throws BadElementException, MalformedURLException, IOException{
		if(image != null) {
			Image i1 = Image.getInstance(image);
		
			float percentage = 0;
			if(i1.getWidth() > i1.getHeight()){
				percentage = 100 / i1.getWidth();
			} else percentage = 100 / i1.getHeight();
		
			Float width = i1.getWidth() * percentage;
			Float height = i1.getHeight() * percentage;
		
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(image));
		
			Image logo = Image.getInstance(img, null);
			logo.scaleAbsolute(width, height);
			
			PdfPCell headerLogo = new PdfPCell(logo, false);
			headerLogo.setBorder(PdfPCell.NO_BORDER);
			headerLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
			return headerLogo;
		} else return new PdfPCell();
	}

	private static PdfPTable buildCargadorContractual(CompanyFull company){
		PdfPTable table = new PdfPTable(1);
		
		PdfPCell c1 = new PdfPCell(new Phrase("Cargador Contractual", getFont1()));
		c1.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c1);
		
		PdfPCell ca = new PdfPCell(new Phrase(company.getRegistry().getName(), getFont1()));
		ca.setBorder(PdfPCell.NO_BORDER);
		ca.setPaddingLeft(5);
		table.addCell(ca);
		
		PdfPCell cX = new PdfPCell(new Phrase("NIF: " + company.getRegistry().getDocument(), getFont2()));
		cX.setBorder(PdfPCell.NO_BORDER);
		cX.setPaddingLeft(5);
		table.addCell(cX);
		
		String streetType = company.getMainAddress().getStreetType() != null ? company.getMainAddress().getStreetType().getDescription() + " " : "";
		String address = company.getMainAddress().getAddress() != null ? company.getMainAddress().getAddress() + " " : "";
		String number = company.getMainAddress().getNumber() != null ? company.getMainAddress().getNumber() + " " : "";
		String address2 = company.getMainAddress().getAddress2() != null ? company.getMainAddress().getAddress2() + " " : "";
		String address3 = company.getMainAddress().getAddress3() != null ? company.getMainAddress().getAddress3() + " " : "";
		String fullAddress = streetType + address + number + address2 + address3;
		
		PdfPCell cb = new PdfPCell(new Phrase(fullAddress, getFont2()));
		cb.setBorder(PdfPCell.NO_BORDER);
		cb.setPaddingLeft(5);
		table.addCell(cb);
		
		String zip = company.getMainAddress().getZip() != null ? company.getMainAddress().getZip() + " " : "";
		String city = company.getMainAddress().getCity() != null ? company.getMainAddress().getCity() + " " : "";
		String province = company.getMainAddress().getProvince() != null ? company.getMainAddress().getProvince() + " " : "";
		String country = company.getMainAddress().getCountry() != null ? company.getMainAddress().getCountry().getName() + " " : "";
		
		PdfPCell cc = new PdfPCell(new Phrase(zip + city + province + country, getFont2()));
		cc.setBorder(PdfPCell.NO_BORDER);
		cc.setPaddingLeft(5);
		table.addCell(cc);
		return table;
	}
	
	private static PdfPTable buildCarrier(Carrier carrier, CarrierPacking carrierPacking){
		PdfPTable table = new PdfPTable(1);
		
		PdfPCell c1 = new PdfPCell(new Phrase("Empresa de Transporte", getFont1()));
		c1.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c1);
		
		PdfPCell ca = new PdfPCell(new Phrase(carrier.getName(), getFont1()));
		ca.setBorder(PdfPCell.NO_BORDER);
		ca.setPaddingLeft(5);
		table.addCell(ca);
		
		PdfPCell cX = new PdfPCell(new Phrase("NIF: " + carrier.getDocument(), getFont2()));
		cX.setBorder(PdfPCell.NO_BORDER);
		cX.setPaddingLeft(5);
		table.addCell(cX);
		
		String streetType = carrier.getMainAddress().getStreet_type() != null ? carrier.getMainAddress().getStreet_type() + " " : "";
		String address = carrier.getMainAddress().getAddress() != null ? carrier.getMainAddress().getAddress() + " " : "";
		String number = carrier.getMainAddress().getNumber() != null ? carrier.getMainAddress().getNumber() + " " : "";
		String address2 = carrier.getMainAddress().getAddress2() != null ? carrier.getMainAddress().getAddress2() + " " : "";
		String address3 = carrier.getMainAddress().getAddress3() != null ? carrier.getMainAddress().getAddress3() + " " : "";
		String fullAddress = streetType + address + number + address2 + address3;

		
		PdfPCell cb = new PdfPCell(new Phrase(fullAddress, getFont2()));
		cb.setBorder(PdfPCell.NO_BORDER);
		cb.setPaddingLeft(5);
		table.addCell(cb);
		
		String zip = carrier.getMainAddress().getZip() != null ? carrier.getMainAddress().getZip() + " " : "";
		String city = carrier.getMainAddress().getCity() != null ? carrier.getMainAddress().getCity() + " " : "";
//		String province = carrier.getMainAddress().getProvince() != null ? carrier.getMainAddress().getProvince() + " " : "";
		String country = carrier.getMainAddress().getCountry() != null ? carrier.getMainAddress().getCountry().getName() + " " : "";
		
		PdfPCell cc = new PdfPCell(new Phrase(zip + city + /*province +*/ country, getFont2()));
		cc.setBorder(PdfPCell.NO_BORDER);
		cc.setPaddingLeft(5);
		table.addCell(cc);
		
		
		PdfPTable table2 = new PdfPTable(2);
		PdfPCell driver = new PdfPCell(new Phrase("Conductor:", getFont1()));
		driver.setBorder(PdfPCell.NO_BORDER);
		table2.addCell(driver);
		
		PdfPCell driver2 = new PdfPCell(new Phrase(carrierPacking.getDriverName() + " - " + carrierPacking.getDriverDocument(), getFont2()));
		driver2.setBorder(PdfPCell.NO_BORDER);
		table2.addCell(driver2);

		PdfPCell numberPlate = new PdfPCell(new Phrase("Matrícula:", getFont1()));
		numberPlate.setBorder(PdfPCell.NO_BORDER);
		table2.addCell(numberPlate);
		
		PdfPCell numberPlate2 = new PdfPCell(new Phrase(carrierPacking.getNumberPlate(), getFont2()));
		numberPlate2.setBorder(PdfPCell.NO_BORDER);
		table2.addCell(numberPlate2);
		
		PdfPCell table2Cell = new PdfPCell(table2);
		table2Cell.setBorder(PdfPCell.NO_BORDER);
		table.addCell(table2Cell);
		
		return table;
	}
	
	private static PdfPTable getHeaderQR(CarrierPacking carrierPacking) throws BadElementException{
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		
		if(carrierPacking.getQr() != null) {
			BarcodeQRCode qrcode = new BarcodeQRCode(carrierPacking.getQr(), 100, 100, null);
			PdfPCell qr = new PdfPCell(qrcode.getImage());
			qr.setBorder(PdfPCell.NO_BORDER);
			qr.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(qr);
		}
		
		return table;
	}
	
	private static PdfPCell getHeaderPackingList(CarrierPacking carrierPacking, int page){
		PdfPTable header3 = new PdfPTable(2);
		header3.setWidthPercentage(100);
		PdfPCell c4 = new PdfPCell(new Phrase("Número:",getFont1()));
		c4.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c4);
			
		PdfPCell c5 = new PdfPCell(new Phrase(carrierPacking.getReferenceCode(), getFont2()));
		c5.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c5);
			
		PdfPCell c2 = new PdfPCell(new Phrase("Fecha Carga:",getFont1()));
		c2.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c2);
				
		PdfPCell c3 = new PdfPCell(new Phrase(AonDateUtils.simpleFormat(carrierPacking.getIssueDate()), getFont2()));
		c3.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c3);
		
		PdfPCell c10 = new PdfPCell(new Phrase("Fecha Entrega: ", getFont1()));
		c10.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c10);
		
		PdfPCell c11 = new PdfPCell(new Phrase(AonDateUtils.simpleFormat(carrierPacking.getDeliveryDate()), getFont2()));
		c11.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c11);
			
		PdfPCell c6 = new PdfPCell(new Phrase("Su Referencia:", getFont1()));
		c6.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c6);
			
		PdfPCell c7 = new PdfPCell(new Phrase(carrierPacking.getCarrierReference(), getFont2()));
		c7.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c7);		
		
		PdfPCell c8 = new PdfPCell(new Phrase("Hoja:",getFont1()));
		c8.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c8);
			
		PdfPCell c9 = new PdfPCell(new Phrase(String.valueOf(page),getFont2()));
		c9.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c9);
		
		PdfPCell cell = new PdfPCell();
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setPadding(0);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.addElement(header3);
		return cell;
	}	
	
	// ------------------- FOOTER
	
	private static PdfPTable getFooter(CarrierPacking carrierPacking){
		StringBuilder text = new StringBuilder(FOOTER_TEXT);
		if(carrierPacking.getQr() != null){
			text.append(carrierPacking.getQr());
		}
		
		Paragraph paragraph = new Paragraph(text.toString(), getFont6());
		paragraph.setAlignment(Element.ALIGN_CENTER);
		
		PdfPCell cell = new PdfPCell();
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.addElement(paragraph);
		
		PdfPTable footer = new PdfPTable(1);
		footer.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		footer.addCell(cell);
		return footer;
	}
	
	// ------------------- SUB-HEADER

	private static PdfPTable getSubHeader(PackingListContext context) throws BadElementException, MalformedURLException, IOException{
		PdfPTable subHeader = new PdfPTable(1);
        subHeader.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        subHeader.setWidthPercentage(100);
        
        subHeader.addCell(getSubHeaderPackingListCarrier(context));
		return subHeader;
	}

	private static PdfPCell getSubHeaderBoeInfo() {
		String boeInfo = "DOCUMENTO ELECTRÓNICO DE CONTROL ADMINISTRATIVO ";
		Paragraph title = new Paragraph(boeInfo, getFont5());
		title.setAlignment(Element.ALIGN_CENTER);
	
		String bi = "Orden FOM/2861/2012 (Ley 9/2025, D.T. 8.ª)";
		Paragraph title2 = new Paragraph(bi, getFont2());
		title2.setAlignment(Element.ALIGN_CENTER);

		PdfPCell cell = new PdfPCell();
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.addElement(title);
		cell.addElement(title2);
		return cell;
	}

	private static PdfPTable getSubHeaderPackingListCarrier(PackingListContext context) {
		CarrierPacking carrierPacking = context.getCarrierPacking();
		Carrier carrier = context.getCarrier();
		
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);		
		
		PdfPTable table1 = new PdfPTable(2);
		table1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table1.addCell(buildCargadorContractual(context.getCompany()));
		
		table1.addCell(buildCarrier(carrier, carrierPacking));
		
		table.addCell(table1);
		
		return table;
	}

	private static Paragraph getSeparator(){
        return getSeparator(-2);
	}
	
	private static Paragraph getSeparator(float offset){
		Paragraph separator = new Paragraph();
		LineSeparator line = new LineSeparator();
        line.setOffset(offset);
        separator.add(line);
        return separator;
	}
	
	private static Paragraph getDottedSeparator(){
		Paragraph p = new Paragraph();
	    DottedLineSeparator dottedline = new DottedLineSeparator();
	    dottedline.setOffset(5);
	    dottedline.setGap(2f);
	    p.add(dottedline);
        return p;
	}
	
	private static Paragraph order(Delivery delivery){
		Paragraph paragraph = new Paragraph();
		
		PdfPTable tableM = new PdfPTable(1);
		tableM.setWidthPercentage(100);
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		
		PdfPTable destinatario = new PdfPTable(4);
		destinatario.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		float[] medidaCeldas = {1f, 3f, 1f, 1f};
		try {
			destinatario.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	
		PdfPCell c = new PdfPCell(new Phrase("Destinatario", getFont1()));
		c.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(c);

		PdfPCell ca = new PdfPCell(new Phrase(delivery.getCustomer().getName(), getFont1()));
		ca.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(ca);
		String descr = "Albarán:";
		String val = delivery.getReferenceCode();

		destinatario.addCell(new Phrase(descr,getFont1()));
		destinatario.addCell(new Phrase(val,getFont2()));
		
		destinatario.addCell("");
		PdfPCell cbc = new PdfPCell(new Phrase("NIF: " + delivery.getCustomer().getDocument(), getFont2()));
		cbc.setBorder(PdfPCell.NO_BORDER);
 		destinatario.addCell(cbc);
		destinatario.addCell(new Phrase("Fecha:",getFont1()));
		String dstr = "";
		try {
			dstr = AonDateUtils.simpleFormat(delivery.getDate());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		destinatario.addCell(new Phrase(dstr,getFont2()));
		
		destinatario.addCell("");
		String streetType = delivery.getAddress().getStreetType() != null ? delivery.getAddress().getStreetType().getDescription() + " " : "";
		String address = delivery.getAddress().getAddress() != null ? delivery.getAddress().getAddress() + " " : "";
		String number = delivery.getAddress().getNumber() != null ? delivery.getAddress().getNumber() + " " : "";
		String address2 = delivery.getAddress().getAddress2() != null ? delivery.getAddress().getAddress2() + " " : "";
		String address3 = delivery.getAddress().getAddress3() != null ? delivery.getAddress().getAddress3() + " " : "";
		String fullAddress = streetType + address + number + address2 + address3;
		PdfPCell cb = new PdfPCell(new Phrase(fullAddress, getFont2()));
		cb.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(cb);
		destinatario.addCell(new Phrase("Su Referencia:", getFont1()));
		destinatario.addCell(new Phrase("", getFont2())); //TODO ¿?
		
		destinatario.addCell("");
		
		String zip = delivery.getAddress().getZip() != null ? delivery.getAddress().getZip() + " " : "";
		String city = delivery.getAddress().getCity() != null ? delivery.getAddress().getCity() + " " : "";
		String province = delivery.getAddress().getProvince() != null ? delivery.getAddress().getProvince() + " " : "";
		String country = delivery.getAddress().getCountry() != null ? delivery.getAddress().getCountry().getName() + " " : "";
		
		PdfPCell cc = new PdfPCell(new Phrase(zip + city + province + country, getFont2()));
		cc.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(cc);
		destinatario.addCell("");
		destinatario.addCell("");
		table.addCell(destinatario);

		table.addCell(getDottedSeparator());
		
		PdfPCell cell1 = new PdfPCell(new Phrase("Artículo",getFont1()));
		cell1.setBorder(PdfPCell.NO_BORDER);
		PdfPCell cell2 = new PdfPCell(new Phrase("Formato",getFont1()));
		cell2.setBorder(PdfPCell.NO_BORDER);
		PdfPCell cell3 = new PdfPCell(new Phrase("Cantidad",getFont1()));
		cell3.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable detailTable = new PdfPTable(3);
		float[] medidaCeldas2 = {4f, 1f, 1f};
		try {
			detailTable.setWidths(medidaCeldas2);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		detailTable.addCell(cell1);
		detailTable.addCell(cell2);
		detailTable.addCell(cell3);
		detailTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

		
		HashMap<String, Double> measurementMap = new HashMap<>();
		HashMap<String, Double> formatMap = new HashMap<>();

		delivery.getDetails().stream().forEach(detail -> {
 			StringBuilder description = new StringBuilder();
			description.append(detail.getDescription());
			
			PdfPCell c1 = new PdfPCell(new Phrase(description.toString(),getFont2()));
			c1.setBorder(PdfPCell.NO_BORDER);

			Double measurements = detail.getItem().getPackMeasurement();;
			Integer units = detail.getItem().getPackUnits();
			Double quantity = detail.getQuantity();
			Double format = (quantity / (units != null && units != 0.0 ? units : 1))
					/ (measurements != null && measurements != 0.0 ? measurements : 1);
		
			String formatTag = "";
			if(detail.getItem().getPackFormatTag() != null && detail.getItem().getPackFormatTag().getName() != null) {
				formatTag = detail.getItem().getPackFormatTag().getName();
				if(formatMap.containsKey(formatTag)){
					formatMap.put(formatTag, formatMap.get(formatTag) + format);
				} else formatMap.put(formatTag, format);
			}
			String measurementTag = "";
			
			if(detail.getItem().getPackMeasurementTag() != null && detail.getItem().getPackMeasurementTag().getName() != null) {
				measurementTag = detail.getItem().getPackMeasurementTag().getName();
				if(measurementMap.containsKey(measurementTag)){
					measurementMap.put(measurementTag, measurementMap.get(measurementTag) + quantity);
				} else measurementMap.put(measurementTag, quantity);
			}
			String formatStr = AonMathUtils.round(format) + " " + formatTag;
			String quantityStr = AonMathUtils.round(quantity) + " " + measurementTag;
			if(quantityStr.equals(formatStr)){
				formatStr = "";
			}
			PdfPCell c2 = new PdfPCell(new Phrase(formatStr, getFont2()));
			c2.setBorder(PdfPCell.NO_BORDER);
			PdfPCell c3 = new PdfPCell(new Phrase(quantityStr, getFont2()));
			c3.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(c1);
			detailTable.addCell(c2);
			detailTable.addCell(c3);
		});
		
		LinkedList<String> formatList = new LinkedList<>(formatMap.keySet());
		Integer formatCont = 0;
		LinkedList<String> measurementList = new LinkedList<>(measurementMap.keySet());
		Integer measurementCont = 0;
		Double totalPackages = delivery.getTotalPackages();
		Double totalWeight = delivery.getTotalWeight();
		
		if(totalPackages!= null && totalPackages != 0){
			PdfPCell cz1 = new PdfPCell(new Phrase("", getFont2()));
			cz1.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(cz1);

			PdfPCell cy1 = new PdfPCell(new Phrase("Bultos: "+ AonMathUtils.round(totalPackages), getFont1()));
			cy1.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(cy1);
			
			String str1 = "";
			if(formatList.size() > 0){
				str1 = formatList.get(0) + " " + AonMathUtils.round(formatMap.get(formatList.get(0))); 
				formatCont++;
			} else if(measurementList.size() > 0){
				str1 = measurementList.get(0) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(0))); 
				measurementCont++;
			}
			PdfPCell cx1 = new PdfPCell(new Phrase(str1, getFont1()));
			cx1.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(cx1);
		}
			
		if(totalWeight != null && totalWeight != 0.0){
			PdfPCell cz2 = new PdfPCell(new Phrase("", getFont2()));
			cz2.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(cz2);
				
			PdfPCell cy2 = new PdfPCell(new Phrase("Peso: " + AonMathUtils.round(totalWeight), getFont1()));
			cy2.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(cy2);
			
			String str2 = "";
			if(formatList.size() > 1){
				str2 = formatList.get(1) + " " + AonMathUtils.round(formatMap.get(formatList.get(1))); 
				formatCont++;
			} else if(measurementList.size() > measurementCont){
				str2 = measurementList.get(measurementCont) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(measurementCont))); 
				measurementCont++;
			}
			PdfPCell cx2 = new PdfPCell(new Phrase(str2, getFont1()));
			cx2.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(cx2);
		}
			
		for(Integer i = formatCont; i < formatList.size(); i++){
			PdfPCell czi = new PdfPCell(new Phrase("", getFont2()));
			czi.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(czi);
			
			PdfPCell cyi = new PdfPCell(new Phrase("", getFont1()));
			cyi.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(cyi);

			String stri = formatList.get(i) + " " + AonMathUtils.round(formatMap.get(formatList.get(i))); 
			PdfPCell cxi = new PdfPCell(new Phrase(stri, getFont1()));
			cxi.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(cxi);
		}
			
		for(Integer j = measurementCont; j < measurementList.size(); j++){
			PdfPCell czj = new PdfPCell(new Phrase("", getFont2()));
			czj.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(czj);
				
			PdfPCell cyj = new PdfPCell(new Phrase("", getFont1()));
			cyj.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(cyj);

			String strj = measurementList.get(j) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(j))); 
			PdfPCell cxj = new PdfPCell(new Phrase(strj, getFont1()));
			cxj.setBorder(PdfPCell.NO_BORDER);
			detailTable.addCell(cxj);
		}		
		
		PdfPTable reception = new PdfPTable(2);
		PdfPCell sign = new PdfPCell(new Phrase("Firma", getFont1()));
		sign.setBorder(PdfPCell.NO_BORDER);
		reception.addCell(sign);

		PdfPCell date = new PdfPCell(new Phrase("Fecha", getFont1()));
		date.setBorder(PdfPCell.NO_BORDER);
		reception.addCell(date);
		
		table.addCell(detailTable);
		table.addCell(reception);
		
		tableM.addCell(table);
		paragraph.add(tableM);
		return paragraph;
	}

	public static PdfPTable getObservations(String observation, com.esferalia.aon.occam.server.warehouse.CarrierPackingParams params){
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		table.addCell(new Paragraph(new Phrase("Observaciones: ", getFont1())));
		table.addCell(new Paragraph(new Phrase(observation, getFont2())));
		
		if(params.getParam().size() > 0){
			PdfPTable parameters = new PdfPTable(2);
			PdfPCell cp1 = new PdfPCell(new Phrase("Parámetro",getFont1()));
			cp1.setBorder(PdfPCell.NO_BORDER);
			parameters.addCell(cp1);
		
			PdfPCell cr1 = new PdfPCell(new Phrase("Resultado",getFont1()));
			cr1.setBorder(PdfPCell.NO_BORDER);
			parameters.addCell(cr1);
			
			params.getParam().stream().forEach(p -> {
				PdfPCell cp2 = new PdfPCell(new Phrase(p.getName(),getFont2()));
				cp2.setBorder(PdfPCell.NO_BORDER);
				parameters.addCell(cp2);
			
				PdfPCell cr2 = new PdfPCell(new Phrase(p.getValue(),getFont2()));
				cr2.setBorder(PdfPCell.NO_BORDER);
				parameters.addCell(cr2);
			});
			
			table.addCell(parameters);
		}
	
		return table;
	}
	
	private static HashMap<String, Double> getMeasurementMap(List<Delivery> orders) {
		HashMap<String, Double> measurementMap = new HashMap<>();
		for(Integer j = 0 ; j < orders.size() ; j++){
			List<DeliveryDetail> details  = orders.get(j).getDetails();	
			for(Integer i = 0 ; i < details.size() ; i++){
				Double quantity = details.get(i).getQuantity();
				String measurementTag = "";
				if(details.get(i).getItem().getPackMeasurementTag() != null && details.get(i).getItem().getPackMeasurementTag().getName() != null) {
					measurementTag = details.get(i).getItem().getPackMeasurementTag().getName();
					if(measurementMap.containsKey(measurementTag)){
						measurementMap.put(measurementTag, measurementMap.get(measurementTag) + quantity);
					} else measurementMap.put(measurementTag, quantity);
				}
			}
		}
		return measurementMap;
	}
	 
	private static HashMap<String, Double> getFormatMap(List<Delivery> deliveries) {
		HashMap<String, Double> formatMap = new HashMap<>();
		for(Integer j = 0 ; j < deliveries.size() ; j++){
			List<DeliveryDetail> details  = deliveries.get(j).getDetails();	
			for(Integer i = 0 ; i < details.size() ; i++){
				Double measurements = details.get(i).getItem().getPackMeasurement();
				Integer units = details.get(i).getItem().getPackUnits();
				Double quantity = details.get(i).getQuantity();
				Double format = (quantity / (units != null && units != 0.0 ? units : 1))
					/ (measurements != null && measurements != 0.0 ? measurements : 1);
			
				String formatTag = "";
				if(details.get(i).getItem().getPackFormatTag() != null && details.get(i).getItem().getPackFormatTag().getName() != null) {
					formatTag = details.get(i).getItem().getPackFormatTag().getName();
					if(formatMap.containsKey(formatTag)){
						formatMap.put(formatTag, formatMap.get(formatTag) + format);
					} else formatMap.put(formatTag, format);
				}
			}
		}
		return formatMap;
	}
	
	private static PdfPTable totalQuantity(List<Delivery> orders, Double totalPackages,Double totalWeight){
		HashMap<String, Double> formatMap = getFormatMap(orders);
		HashMap<String, Double> measurementMap = getMeasurementMap(orders);

		LinkedList<String> formatList = new LinkedList<>(formatMap.keySet());
		Integer formatCont = 0;
		LinkedList<String> measurementList = new LinkedList<>(measurementMap.keySet());
		Integer measurementCont = 0;

		PdfPTable t = new PdfPTable(3);
		float[] medidaCeldas = {4f, 1f, 1f};
		try {
			t.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		t.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		t.setWidthPercentage(100);
		
	
		if(totalPackages != null && totalPackages != 0.0){
			t.addCell("");
			t.addCell(new Phrase("Bultos: " + AonMathUtils.round(totalPackages), getFont1()));
			String str1 = "";
			if(formatList.size() > 0){
				str1 = formatList.get(0) + " " + AonMathUtils.round(formatMap.get(formatList.get(0))); 
				formatCont++;
			} else if(measurementList.size() > 0){
				str1 = measurementList.get(0) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(0))); 
				measurementCont++;
			}
			PdfPCell cx1 = new PdfPCell(new Phrase(str1, getFont1()));
			cx1.setBorder(PdfPCell.NO_BORDER);
			t.addCell(cx1);
		}
		if(totalWeight != null && totalWeight != 0.0){
			t.addCell("");
			t.addCell(new Phrase("Peso: " +  AonMathUtils.round(totalWeight), getFont1()));
			String str2 = "";
			if(formatList.size() > 1){
				str2 = formatList.get(1) + " " + AonMathUtils.round(formatMap.get(formatList.get(1))); 
				formatCont++;
			} else if(measurementList.size() > measurementCont){
				str2 = measurementList.get(measurementCont) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(measurementCont))); 
				measurementCont++;
			}
			PdfPCell cx2 = new PdfPCell(new Phrase(str2, getFont1()));
			cx2.setBorder(PdfPCell.NO_BORDER);
			t.addCell(cx2);
		}
		for(Integer i = formatCont; i < formatList.size(); i++){
			t.addCell("");
			t.addCell("");
			String stri = formatList.get(i) + " " + AonMathUtils.round(formatMap.get(formatList.get(i))); 
			PdfPCell cxi = new PdfPCell(new Phrase(stri, getFont1()));
			cxi.setBorder(PdfPCell.NO_BORDER);
			t.addCell(cxi);
		}
		for(Integer j = measurementCont; j < measurementList.size(); j++){
			t.addCell("");
			t.addCell("");

			String strj = measurementList.get(j) + " " + AonMathUtils.round(measurementMap.get(measurementList.get(j))); 
			PdfPCell cxj = new PdfPCell(new Phrase(strj, getFont1()));
			cxj.setBorder(PdfPCell.NO_BORDER);
			t.addCell(cxj);
		}	
		return t;
	}

}
