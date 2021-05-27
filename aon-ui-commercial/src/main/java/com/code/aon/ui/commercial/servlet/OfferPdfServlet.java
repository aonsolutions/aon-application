package com.code.aon.ui.commercial.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


@SuppressWarnings("serial")
@WebServlet(name = "DownloadOfferPdf", urlPatterns = {"/aon_gwt_aio/download_offer_pdf/*"})
public class OfferPdfServlet extends HttpServlet {

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String domainName = req.getParameter("domain");
		Integer domainId = Integer.parseInt(req.getParameter("domain_id"));
		String userName = req.getParameter("username");
		Integer offerId = Integer.parseInt(req.getParameter("id"));
		
		Domain domain = AON.getDomain(domainName, domainId, userName, f->f.getNameProperty().eq(domainName));		

		Company company = AON.getCompany(domainName, domainId, userName, f -> f.getDomainProperty().eq(domain.getId()));
		company.setMainAddress(AON.getRAddres(domain.getName(), domain.getId(), userName, company.getId()));
		Offer offer = AON.getOffer(domainName, domainId, userName, f -> f.getIdProperty().eq(offerId));
		Target target = AON.getTarget(domain.getName(), domain.getId(),userName, f -> f.getIdProperty().eq(offer.getTarget().getId())).get();
		target.setMainAddress(AON.getRAddres(domain.getName(), domain.getId(), userName, target.getId()));
		offer.setTarget(target);

		Attach attach = AON.getAttach(domain.getName(), domain.getId(), userName, 
				f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
				.and(f.getDomainProperty().eq(domain.getId())),
			AttachType.REGISTRY);
		
		Attach signature = AON.getAttach(domain.getName(), domain.getId(), userName, 
				f -> f.getTypeProperty().eq(RegistryAttachmentType.SIGNATURE.value())
				.and(f.getDomainProperty().eq(domain.getId())),
			AttachType.REGISTRY);
		File file = createPdf(domain, company, offer, attach.getData(), signature.getData());
		
		
        addCorsHeader(resp);
        resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition", "inline; filename=\"" + file.getName() + ".pdf\";");
		FileInputStream fileInpurOs =  new FileInputStream(file);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		resp.flushBuffer();

		fileInpurOs.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	
	private static final Logger LOGGER  = Logger.getLogger(OfferPdfServlet.class.getName());
	
	public OfferPdfServlet() {

	}
	
	private static final BaseColor DARK_BLUE = new BaseColor(0,0,140);
	
	private static final String ONE_TITLE = "1.- Datos del Proveedor";
	
	private static String getOneText(Domain domain, Company company) {
		RecordData rd = AON.getRecordData(domain.getName(), domain.getId(), "", f -> f.getRegistryProperty().eq(company.getId()));
		return "El proveedor de los productos y/o servicios objeto del presente contrato es la Sociedad Mercantil " +
	      company.getName() + ", con N.I.F. " + company.getDocument() + " y docimicilio en "+ company.getMainAddress().getFullAddress() + ", "+
	      company.getMainAddress().getZip() + " de " + company.getMainAddress().getCity() + " ("+ company.getMainAddress().getGeozoneName() + ")."  +
	      "[Constituida el " + AonDateUtils.simpleFormat(rd.getCreationDate()) + " e " + rd.getRegistration() + " el " +
	      AonDateUtils.simpleFormat(rd.getRecordDate()) + " en el Tomo " + rd.getVolume() +", Folio " + rd.getPage() +
	      ", Hoja " + rd.getSheet() + "]";
	}

	private static final String TWO_TITLE = "2.- Datos del Cliente";
	
	private static final String THREE_TITLE = "3.- Objeto del Contrato";
	
	private static String getThreeText(Company company) {
		return "El cliente identificado en el punto 2(Datos del cliente) está interesado en la contratación a " +
		      company.getName() + " de los productos y/o servicios reseñados en el punto 4 (Detalle del contrato)" +
		      "del presente documento.";
	}
	private static final String FOUR_TITLE = "4.- Detalle del Contrato";
	private static final String FIVE_TITLE = "5.- Condiciones Económicas";
	private static String getFiveText() {
		return "Todos los precios indicados se entienden en Euros y se incrementarán con el IVA correspondiente " + 
				"al momento de emisión de la factura. El cliente se obliga a pagar los importes indicados en el \"Importa total\"" + 
				"mediante domiciliación bancaria recurrente en los plazos indicados como \"Periocidad\" que serán cargados" + 
				"a través de la cuenta que se detalla a continuación:";
	}
	private static final String SIX_TITLE = "6.- Aceptación del Contrato por el Cliente";
	private static String getSixText() {
		return "La persona firmante, actuando en nombre y representación del Cliente descrito en el punto DOS (2.- Datos del Cliente), " + 
				"asegura la vigencia y suficiencia de sus facultades para la aceptación del presente contrato y mandato SEPA así como anexos incluídos, " + 
				"y en prueba de conformidad con todos los puntos descritos con anterioridad, firma electrónicamente el presente documento";
	}
	private static final String SEVEN_TITLE = "7.- Aceptación del Contrato por el Proveedor";
	private static String getSevenText(Company company) {
		return "La persona firmante, actuando en nombre y representación de " + company.getName() + ", asegura la vigencia y suficiencia " + 
				"de las facultades para la aceptación del presente contrato, y en prueba de conformidad con todos los puntos descritos con anterioridad, " + 
				"firma el presente documento.";
	}
	
	public static File createPdf(com.code.aon.commercial.Offer o) {
		String login = "";
		Domain domain = AON.getDomain(AonUtil.getDomainName(), o.getDomain(), login, f-> f.getIdProperty().eq(o.getDomain()));
		
		Company company = AON.getCompany(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));
		company.setMainAddress(AON.getRAddres(domain.getName(), domain.getId(), login, company.getId()));
		Offer offer = AON.getOffer(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(o.getId()));
		Target target = AON.getTarget(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(offer.getTarget().getId())).get();
		target.setMainAddress(AON.getRAddres(domain.getName(), domain.getId(), login, target.getId()));
		offer.setTarget(target);
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, 
				f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
				.and(f.getDomainProperty().eq(domain.getId())),
			AttachType.REGISTRY);
		
		Attach signature = AON.getAttach(domain.getName(), domain.getId(), login, 
				f -> f.getTypeProperty().eq(RegistryAttachmentType.SIGNATURE.value())
				.and(f.getDomainProperty().eq(domain.getId())),
			AttachType.REGISTRY);
		return createPdf(domain, company, offer, attach.getData(), signature.getData());
	}
	
	public static File createPdf(Domain domain, Company company, Offer offer, byte [] image, byte [] sign) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("packingList", "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			
			document.open();			
			document.add(getHeader(domain, offer, company, image));
		
			document.add(new Paragraph(" "));
			document.add(getOne(domain, company));
			document.add(getTwo(domain, offer));
			document.add(getThree(company));		
			document.add(getFour(domain, offer));		
			document.add(getFive(offer));
			document.add(getSix(offer));
			document.add(getSeven(domain, company, sign));			
		} catch (DocumentException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		document.close();
		return archivoPDF;
	}
	
	// ------------------- HEADER
	
	private static PdfPTable getHeader(Domain domain, Offer offer, Company company, byte [] image) throws BadElementException, MalformedURLException, IOException{
        PdfPTable header = new PdfPTable(3);
        float[] medidaCeldas = {1.5f, 1f, 0.75f};
		try {
			header.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
        header.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        header.setWidthPercentage(100);
      
        header.addCell(getHeaderLogo(image));
		header.addCell(getHeaderOffer(offer));
		header.addCell(getHeaderCompany(domain, company));
		return header;
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
			return headerLogo;
		} else {
			PdfPCell headerLogo = new PdfPCell();
			headerLogo.setBorder(PdfPCell.NO_BORDER);
			return headerLogo;
		}
	}
	
	private static PdfPCell getHeaderOffer(Offer offer){
		PdfPTable table = new PdfPTable(2);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		PdfPCell ca = new PdfPCell(new Phrase("CONTRATO Nº.: " , getFont(9)));
		ca.setBorder(PdfPCell.NO_BORDER);
		table.addCell(ca);
		table.addCell(new Paragraph(offer.getReferenceCode(), getColorFont(9, DARK_BLUE)));
		
		PdfPCell cX = new PdfPCell(new Phrase("DE PRESTACIÓN DE SERVICIOS", getFont(9)));
		cX.setBorder(PdfPCell.NO_BORDER);
		cX.setColspan(2);
		table.addCell(cX);
		
		PdfPCell cb = new PdfPCell(new Phrase("de fecha ", getFont(9)));
		cb.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cb);
		
		table.addCell(new Paragraph(AonDateUtils.simpleFormat(offer.getIssueDate()), getColorFont(9, DARK_BLUE)));
		
		PdfPCell cell = new PdfPCell(table);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	private static PdfPCell getHeaderCompany(Domain domain, Company company){
		RegistryMedia cellular = AON.getRMedia(domain.getName(), domain.getId(), "", f-> f.getRegistryProperty().eq(company.getId()).and(f.getMediaProperty().eq(MediaType.CELLULAR.value())));
        RegistryMedia fixed = AON.getRMedia(domain.getName(), domain.getId(), "", f-> f.getRegistryProperty().eq(company.getId()).and(f.getMediaProperty().eq(MediaType.FIXED_PHONE.value())));
        String phone = cellular != null && cellular.getValue() != null ? cellular.getValue(): "";
        phone = phone + (fixed != null && fixed.getValue() != null ? (phone != "" ? " - " + fixed.getValue(): fixed.getValue()) : "");
        RegistryMedia email = AON.getRMedia(domain.getName(), domain.getId(), "", f-> f.getRegistryProperty().eq(company.getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
        RegistryMedia web = AON.getRMedia(domain.getName(), domain.getId(), "", f-> f.getRegistryProperty().eq(company.getId()).and(f.getMediaProperty().eq(MediaType.WEB.value())));

		PdfPTable header3 = new PdfPTable(1);
		header3.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		header3.addCell(new Phrase(phone, getFont(9)));
		header3.addCell(new Phrase(web != null && web.getValue() != null ? web.getValue() : "", getFont(9)));
		header3.addCell(new Phrase(email != null && web.getValue() != null ? email.getValue() : "", getFont(9)));
		
		PdfPCell cell = new PdfPCell(header3);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}	
	
	// ONE
	private static PdfPTable getTitle(String title) throws BadElementException, MalformedURLException, IOException{
	    PdfPTable table  = new PdfPTable(1);
        table.setWidthPercentage(100);
        
        PdfPCell cell = new PdfPCell(new Paragraph(title, getColorFont(9, BaseColor.WHITE) ));
        cell.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell);	
        return table;
	}
	
	private static PdfPTable getOne(Domain domain, Company company) throws BadElementException, MalformedURLException, IOException{
		PdfPTable table = getTitle(ONE_TITLE);
        PdfPCell cell = new PdfPCell(new Paragraph(getOneText(domain, company), getFont(9) ));
        table.addCell(cell);
        return table;
	}
	
	private static PdfPTable getTwo(Domain domain, Offer offer) throws BadElementException, MalformedURLException, IOException{
        PdfPTable table  = getTitle(TWO_TITLE);
        PdfPTable t1 = new PdfPTable(6);
        t1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

		float[] medidaCeldas = {0.6f, 1f, 0.6f, 1f, 0.6f, 1f};
		try {
			t1.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
        
        t1.addCell(new Paragraph("Razón Social:", getFont(9)));
        PdfPCell cell1 = new PdfPCell(new Paragraph(offer.getTarget().getName(), getColorFont(9, DARK_BLUE)));
        cell1.setColspan(3);
        cell1.setBorder(PdfPCell.NO_BORDER);
        t1.addCell(cell1);
        t1.addCell(new Paragraph("N.I.F.:", getFont(9)));
        t1.addCell(new Paragraph(offer.getTarget().getDocument(), getColorFont(9, DARK_BLUE)));
        
        t1.addCell(new Paragraph("Dirección:", getFont(9)));
        PdfPCell cell = new PdfPCell(new Paragraph(offer.getTarget().getMainAddress().getFullAddress(), getColorFont(9, DARK_BLUE)));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(5);
        t1.addCell(cell);

        t1.addCell(new Paragraph("C. Postal:", getFont(9)));
        t1.addCell(new Paragraph(offer.getTarget().getMainAddress().getZip(), getColorFont(9, DARK_BLUE)));
        t1.addCell(new Paragraph("Población:", getFont(9)));
        t1.addCell(new Paragraph(offer.getTarget().getMainAddress().getCity(), getColorFont(9, DARK_BLUE)));
        t1.addCell(new Paragraph("Provincia:", getFont(9)));
        t1.addCell(new Paragraph(offer.getTarget().getMainAddress().getGeozoneName(), getColorFont(9, DARK_BLUE)));
        
        RegistryMedia email = AON.getRMedia(domain.getName(), domain.getId(), "", f-> f.getRegistryProperty().eq(offer.getTarget().getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
        RegistryMedia cellular = AON.getRMedia(domain.getName(), domain.getId(), "", f-> f.getRegistryProperty().eq(offer.getTarget().getId()).and(f.getMediaProperty().eq(MediaType.CELLULAR.value())));
        RegistryMedia fixed = AON.getRMedia(domain.getName(), domain.getId(), "", f-> f.getRegistryProperty().eq(offer.getTarget().getId()).and(f.getMediaProperty().eq(MediaType.FIXED_PHONE.value())));
        String phone = cellular != null && cellular.getValue() != null ? cellular.getValue(): "";
        phone = phone + (fixed != null && fixed.getValue() != null ? (phone != "" ? " - " + fixed.getValue(): fixed.getValue()) : "");
        t1.addCell(new Paragraph("Teléfono:", getFont(9)));
        t1.addCell(new Paragraph(phone, getColorFont(9, DARK_BLUE)));
        t1.addCell(new Paragraph("Email:", getFont(9)));
        
        PdfPCell mail = new PdfPCell(new Paragraph(email != null && email.getValue() != null ? email.getValue() : "", getColorFont(9, DARK_BLUE)));
        mail.setColspan(3);
        mail.setBorder(PdfPCell.NO_BORDER);
        t1.addCell(mail);
        
        table.addCell(t1);
        return table;
	}
	
	private static PdfPTable getThree(Company company) throws BadElementException, MalformedURLException, IOException{
        PdfPTable table  = getTitle(THREE_TITLE);
        PdfPCell cell = new PdfPCell(new Paragraph(getThreeText(company), getFont(9)));
        table.addCell(cell);
        return table;
	}
	
	private static PdfPTable getFour(Domain domain, Offer offer) throws BadElementException, MalformedURLException, IOException{
        PdfPTable table  = getTitle(FOUR_TITLE);
        PdfPTable t1 = new PdfPTable(5);
//        t1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

		float[] medidaCeldas = {2f, 0.5f, 0.75f, 1f,  1f};
		try {
			t1.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
        
        PdfPCell cell1 = new PdfPCell(new Paragraph("Descripción del producto o servicio a contratar", getColorFont(9, BaseColor.WHITE)));
		cell1.setBackgroundColor(BaseColor.GRAY);
        t1.addCell(cell1);
        PdfPCell cell2 = new PdfPCell(new Paragraph("Cantidad", getColorFont(9, BaseColor.WHITE)));
		cell2.setBackgroundColor(BaseColor.GRAY);
        t1.addCell(cell2);
        PdfPCell cell3 = new PdfPCell(new Paragraph("Precio", getColorFont(9, BaseColor.WHITE)));
		cell3.setBackgroundColor(BaseColor.GRAY);
        t1.addCell(cell3);
        PdfPCell cell4 = new PdfPCell(new Paragraph("Periodicidad", getColorFont(9, BaseColor.WHITE)));
		cell4.setBackgroundColor(BaseColor.GRAY);
        t1.addCell(cell4);
        PdfPCell cell5 = new PdfPCell(new Paragraph("Importe", getColorFont(9, BaseColor.WHITE)));
		cell5.setBackgroundColor(BaseColor.GRAY);
        t1.addCell(cell5);
        Double total = 0.0;
        Double quota = 0.0;
        LinkedList<OfferDetail> details = AON.getOfferDetails(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(offer.getId())).collect(Collectors.toCollection(LinkedList::new));
        for (OfferDetail detail : details) {
        	//Item item = AON.getItem(domain.getName(), domain.getId(), "", f-> f.getIdProperty().eq(detail.getItem().getId()));
        	OldProduct product = AON.getProduct(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(detail.getItem().getProductId()));
        	Tax tax = AON.getTax(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(product.getVat()));
        	t1.addCell(new Paragraph(detail.getDescription(), getColorFont(9, DARK_BLUE)));
            t1.addCell(getRightCell(new Paragraph(Double.toString(AonMathUtils.round(detail.getQuantity())), getColorFont(9, DARK_BLUE))));
            t1.addCell(getRightCell(new Paragraph(Double.toString(AonMathUtils.round(detail.getPrice())) + " \u20AC", getColorFont(9, DARK_BLUE))));
            t1.addCell(new Paragraph("", getColorFont(9, DARK_BLUE)));
            t1.addCell(getRightCell(new Paragraph(Double.toString(AonMathUtils.round(detail.getQuantity() * detail.getPrice()))  + " \u20AC", getColorFont(9, DARK_BLUE))));
            Double detailAmount = AonMathUtils.round(detail.getQuantity() * detail.getPrice());
            quota = quota + (tax.getPercentage() * detailAmount / 100);
            total = total + detailAmount ;			
		}

        PdfPCell cell = new PdfPCell(new Paragraph("Observaciones: ", getFont(9)));

        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(3);
        t1.addCell(cell);
        t1.addCell(new Paragraph("Base Imponible", getFont(9)));
        
        t1.addCell(getRightCell(new Paragraph(Double.toString(AonMathUtils.round(total))  + " \u20AC", getColorFont(9, DARK_BLUE))));
        
        PdfPCell cellX = new PdfPCell(new Paragraph(offer.getComments(), getColorFont(9, DARK_BLUE)));
        cellX.setColspan(3);
        cellX.setRowspan(2);
        cellX.setBorder(PdfPCell.NO_BORDER);
        t1.addCell(cellX);
        
        t1.addCell(new Paragraph("Cuota IVA", getFont(9)));
        t1.addCell(getRightCell(new Paragraph(Double.toString(AonMathUtils.round(quota))  + " \u20AC", getColorFont(9, DARK_BLUE))));
        t1.addCell(new Paragraph("Importe Total", getFont(9)));
        t1.addCell(getRightCell(new Paragraph(Double.toString(AonMathUtils.round(total + quota))  + " \u20AC", getColorFont(9, DARK_BLUE))));
        table.addCell(t1);
        
        return table;
	}
	
	private static PdfPCell getRightCell(Paragraph p) {
		PdfPCell cell = new PdfPCell(p);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		return cell;
	}
	private static PdfPTable getFive(Offer offer) throws BadElementException, MalformedURLException, IOException{
        PdfPTable table  = getTitle(FIVE_TITLE);

 		PdfPTable t1 = new PdfPTable(4);
        PdfPCell cell = new PdfPCell( new Paragraph(getFiveText(), getFont(9)));
 		cell.setColspan(4);
 		cell.setBorder(PdfPCell.NO_BORDER);
 		t1.addCell(cell);
 		t1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
 		t1.addCell(new Paragraph("Número de Cuenta - IBAN: ", getFont(9)));
 		t1.addCell(new Paragraph(offer.getBankAccount(), getColorFont(9, DARK_BLUE)));
 		t1.addCell(new Paragraph("BIC/Swift: ", getFont(9)));
 		t1.addCell(new Paragraph(offer.getBic(), getColorFont(9, DARK_BLUE)));
 		table.addCell(t1);
        return table;
	}
	
	private static PdfPTable getSix(Offer offer) throws BadElementException, MalformedURLException, IOException{
        PdfPTable table  = getTitle(SIX_TITLE);
        PdfPCell cell = new PdfPCell( new Paragraph(getSixText(), getFont(9) ));
        table.addCell(cell);
        return table;
	}
	
	private static PdfPTable getSeven(Domain domain, Company company, byte[] sign) throws BadElementException, MalformedURLException, IOException{
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
        float[] medidaCeldas = {4f, 1f};
		try {
			table.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		PdfPCell cell = new PdfPCell(new Paragraph(SEVEN_TITLE , getColorFont(9, BaseColor.WHITE) ));
        cell.setBackgroundColor(BaseColor.BLACK);
        cell.setColspan(2);
        table.addCell(cell);

        RDirStaff rdirStaff = AON.getRDirStaff(domain.getName(), domain.getId(), "", f -> f.getRegistryProperty().eq(company.getId()).and(f.getRepresentativeProperty().eq((byte) 1)));
        if(rdirStaff.getId() != null) {
        	rdirStaff = AON.getRDirStaff(domain.getName(), domain.getId(), "", f -> f.getRegistryProperty().eq(company.getId()));
        }
      	table.addCell(new Paragraph(getSevenText(company) 
       		+ (rdirStaff.getId() != null ? ("\n\nFdo.: " + rdirStaff.getName()+ " con D.N.I. " + rdirStaff.getDocument()) : "")
       		, getFont(9)));

        BufferedImage img = ImageIO.read(new ByteArrayInputStream(sign));
		Image logo = Image.getInstance(img, null);
		
		PdfPCell headerLogo = new PdfPCell(logo, false);
		headerLogo.setBorder(PdfPCell.NO_BORDER);
        
        table.addCell(logo);
        return table;
	}

	public static Font getFont(Integer size){
		Font font2 = new Font();
		font2.setSize(size);
		return font2;
	}
	
	public static Font getColorFont(Integer size, BaseColor color){
		Font font2 = new Font();
		font2.setSize(size);
		font2.setColor(color);
		return font2;
	}
	
    public static void addCorsHeader(HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
    }
}
