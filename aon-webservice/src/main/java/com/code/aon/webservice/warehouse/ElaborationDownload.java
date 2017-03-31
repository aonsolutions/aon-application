package com.code.aon.webservice.warehouse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.itextpdf.text.BadElementException;
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
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

@WebServlet(name = "elaborationProjection", urlPatterns = { "/aon_gwt_aio/download_elaboration/*" })
public class ElaborationDownload extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger
			.getLogger(ElaborationDownload.class.getName());

	private Integer elaborationId = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HashMap<String, String> parameters = SecurityUtils.getInstance()
				.getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Domain domain = AON.getDomain(domainName, 1, login, f -> f
				.getNameProperty().eq(domainName));
		String _id = parameters.get("id");
		if (_id != null && !"".equals(_id)) {
			elaborationId = Integer.parseInt(_id);
		}

		List<Elaboration> elaborationList = null;
		if (elaborationId != null) {
			elaborationList = AON.getElaborationList(domain.getName(),
					domain.getId(), login,
					f -> f.getIdProperty().eq(elaborationId));
		} else {
			elaborationList = AON.getElaborationList(domain.getName(),
					domain.getId(), login,
					f -> f.getDomainProperty().eq(domain.getId()));
		}

		Company company = AON.getCompanyForDomain(domain.getName(),
				domain.getId(), login);
		RAddress address = AON.getRAddres(domain.getName(), domain.getId(),
				login, company.getId());

		// --------------- //

		Attach logoAttach = AON.getAttach(
				domain.getName(),
				domain.getId(),
				login,
				f -> f.getTypeProperty()
						.eq(RegistryAttachmentType.LOGO.value())
						.and(f.getDomainProperty().eq(domain.getId())),
				AttachType.REGISTRY);

		Elaboration elaboration = elaborationList.get(0);
		SalesDetail salesDetail = null;
		Sales sales = null;
		if(elaboration.getSourceId()!=null){
			ElaborationSource source = ElaborationSource.safeValueOf(elaboration.getSource());
			if(source==ElaborationSource.SALES){
				salesDetail = AON.getSalesDetailStream(domain.getName(), domain.getId(), login, 
						f -> f.getIdProperty().eq(elaboration.getSourceId()))
						.findFirst().orElse(null);
				if(salesDetail!=null && salesDetail.getSales()>0){
					int salesId = salesDetail.getSales(); 
					sales = AON.getSales(domain.getName(), domain.getId(), login, 
							f -> f.getIdProperty().eq(salesId));
				}
			} else if(source==ElaborationSource.PURCHASE){
				// TODO purchase source of elaboration
			}
		}
		
		File file = createPdf(elaboration, sales, salesDetail, company, address,
				logoAttach.getData());

		Utils.addCorsHeader(resp);
		resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition",
				"inline; filename=\"" + file.getName() + ".pdf\";");
		FileInputStream fileInpurOs = new FileInputStream(file);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		resp.flushBuffer();

		fileInpurOs.close();
	}
	
	private Optional<Item> getItem(Domain domain, String login, Integer itemId,
			Integer productId) {
		Optional<Item> optional = AON.getItemOptional(
				domain.getName(),
				domain.getId(),
				login,
				f -> f.getIdProperty().eq(itemId)
						.and(f.getPackFormatTagProperty().isNotNull())
						.and(f.getPackMeasurementTagProperty().isNotNull())
						.and(f.getPackMeasurementProperty().isNotNull())
						.and(f.getPackUnitsProperty().isNotNull()));
		return optional.isPresent() ? optional : AON.getItemOptional(
				domain.getName(),
				domain.getId(),
				login,
				f -> f.getProductProperty().eq(productId)
						.and(f.getPackFormatTagProperty().isNotNull())
						.and(f.getPackMeasurementTagProperty().isNotNull())
						.and(f.getPackMeasurementProperty().isNotNull())
						.and(f.getPackUnitsProperty().isNotNull()));
	}

	/*
	 * PDF METHODS
	 */
	public static File createPdf(Elaboration elaboration,
			Sales sales, SalesDetail salesDetail,
			Company company, RAddress address, byte[] image) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("elaboration", "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}

		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));

			document.open();
			document.add(getHeader(elaboration, company, address,
					image));
			document.add(new Paragraph(" "));
			document.add(getSubHeader(elaboration));
			document.add(new Paragraph(" "));

			if(salesDetail!=null){
				Paragraph salesPdf = getSales(sales, salesDetail);
				document.add(getSeparator());
				document.add(salesPdf);
				document.add(getSeparator());
			}

			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
		} catch (DocumentException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		document.close();
		return archivoPDF;
	}

	private static PdfPTable getHeader(Elaboration elaboration,
			Company company, RAddress address, byte[] image)
			throws BadElementException, MalformedURLException, IOException {
		PdfPTable header = new PdfPTable(3);
		float[] medidaCeldas = { 0.75f, 1.25f, 1f };
		try {
			header.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		header.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		header.setWidthPercentage(100);

		header.addCell(getHeaderLogo(image));
		header.addCell(getHeaderCompany(company, address));
		header.addCell(getHeaderElaboration(elaboration));
		return header;
	}

	private static PdfPCell getHeaderLogo(byte[] image)
			throws BadElementException, MalformedURLException, IOException {
		Image i1 = Image.getInstance(image);

		float percentage = 0;
		if (i1.getWidth() > i1.getHeight()) {
			percentage = 100 / i1.getWidth();
		} else
			percentage = 100 / i1.getHeight();

		Float width = i1.getWidth() * percentage;
		Float height = i1.getHeight() * percentage;

		BufferedImage img = ImageIO.read(new ByteArrayInputStream(image));

		Image logo = Image.getInstance(img, null);
		logo.scaleAbsolute(width, height);
		PdfPCell headerLogo = new PdfPCell(logo, false);
		headerLogo.setBorder(PdfPCell.NO_BORDER);
		return headerLogo;
	}

	private static PdfPTable getHeaderCompany(Company company, RAddress address) {
		PdfPTable table = new PdfPTable(1);
		PdfPCell ca = new PdfPCell(new Phrase(company.getName(), getFont1()));
		ca.setBorder(PdfPCell.NO_BORDER);
		table.addCell(ca);

		PdfPCell cX = new PdfPCell(new Phrase("NIF: " + company.getDocument(),
				getFont2()));
		cX.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cX);

		PdfPCell cb = new PdfPCell(new Phrase(address.getAddress(), getFont2()));
		cb.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cb);

		PdfPCell cc = new PdfPCell(new Phrase(address.getZip() + " "
				+ address.getCity() + " " + address.getGeozoneName(),
				getFont2()));
		cc.setBorder(PdfPCell.NO_BORDER);
		table.addCell(cc);
		return table;
	}

	private static PdfPTable getHeaderElaboration(Elaboration elaboration) {
		PdfPTable header3 = new PdfPTable(2);
		PdfPCell c4 = new PdfPCell(new Phrase("Número:", getFont1()));
		c4.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c4);

		PdfPCell c5 = new PdfPCell(new Phrase(elaboration.getSeries() + "/"
				+ elaboration.getNumber(), getFont2()));
		c5.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c5);

		PdfPCell c2 = new PdfPCell(new Phrase("Fecha:", getFont1()));
		c2.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c2);

		PdfPCell c3 = new PdfPCell(new Phrase(elaboration.getDate().toString(),
				getFont2()));
		c3.setBorder(PdfPCell.NO_BORDER);
		header3.addCell(c3);

		return header3;
	}

	// ------------------- SUB-HEADER

	private static PdfPTable getSubHeader(Elaboration elaboration)
			throws BadElementException, MalformedURLException, IOException {
		PdfPTable subHeader = new PdfPTable(1);
		subHeader.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		subHeader.setWidthPercentage(100);
		subHeader.addCell(getSubHeaderPackingListType(elaboration));

		PdfPCell space = new PdfPCell(new Phrase("", getFont1()));
		space.setBorder(PdfPCell.NO_BORDER);
		subHeader.addCell(space);

		return subHeader;
	}

	private static PdfPCell getSubHeaderPackingListType(Elaboration elaboration) {
		Paragraph title = new Paragraph("elaboration", getTitleFont());
		title.setAlignment(Element.ALIGN_CENTER);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.addElement(title);
		return cell;
	}

	private static Paragraph getSales(Sales sales, SalesDetail salesDetail){
		Paragraph paragraph = new Paragraph();
		
		PdfPTable tableM = new PdfPTable(1);
		tableM.setWidthPercentage(100);
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		

		table.addCell(getDottedSeparator());
		
		PdfPCell cell1 = new PdfPCell(new Phrase("Articulo",getFont1()));
		cell1.setBorder(PdfPCell.NO_BORDER);
		PdfPCell cell2 = new PdfPCell(new Phrase("Formato",getFont1()));
		cell2.setBorder(PdfPCell.NO_BORDER);
		PdfPCell cell3 = new PdfPCell(new Phrase("Cantidad",getFont1()));
		cell3.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable detail = new PdfPTable(3);
		float[] medidaCeldas2 = {4f, 1f, 1f};
		try {
			detail.setWidths(medidaCeldas2);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		detail.addCell(cell1);
		detail.addCell(cell2);
		detail.addCell(cell3);
		detail.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

		
		PdfPTable reception = new PdfPTable(2);
		PdfPCell sign = new PdfPCell(new Phrase("Firma", getFont1()));
		sign.setBorder(PdfPCell.NO_BORDER);
		reception.addCell(sign);

		PdfPCell date = new PdfPCell(new Phrase("Fecha", getFont1()));
		date.setBorder(PdfPCell.NO_BORDER);
		reception.addCell(date);
		
		table.addCell(detail);
		table.addCell(reception);
		
		tableM.addCell(table);
		paragraph.add(tableM);
		return paragraph;
	}
	
	
	/*
	 * SEPARATORS
	 */
	private static Paragraph getSeparator() {
		Paragraph separator = new Paragraph();
		LineSeparator line = new LineSeparator();
		line.setOffset(-2);
		separator.add(line);
		return separator;
	}

	private static Paragraph getDottedSeparator() {
		Paragraph p = new Paragraph();
		DottedLineSeparator dottedline = new DottedLineSeparator();
		dottedline.setOffset(5);
		dottedline.setGap(2f);
		p.add(dottedline);
		return p;
	}

	// ------------------- FONTS
	private static Font getTitleFont() {
		Font font = new Font();
		font.setSize(16);
		font.setStyle(Font.BOLD);
		return font;
	}

	private static Font getBoeInfoFont() {
		Font font = new Font();
		font.setSize(13);
		font.setStyle(Font.BOLD);
		return font;
	}

	private static Font getFont1() {
		Font font1 = new Font();
		font1.setSize(8);
		font1.setStyle(Font.BOLD);
		return font1;
	}

	private static Font getFont2() {
		Font font2 = new Font();
		font2.setSize(8);
		return font2;
	}

	private static Font getFont3() {
		Font font1 = new Font();
		font1.setSize(12);
		font1.setStyle(Font.BOLD);
		return font1;
	}

}
