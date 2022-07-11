package net.aonsolutions.aon.api.servlet.invoice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.facturae.nuevo.FacturaeWriter2;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediRegistry;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;


@SuppressWarnings("serial")
@WebServlet(name = "FaceServlet", urlPatterns = {"/ms/api/face/*",
														"/aon_gwt_aio/face/*"
														})
public class FaceServlet extends AonApiHttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API DOWNLOAD INVOICE PDF AK");
		try {
			String idStr = req.getParameter("id");
			Integer id = Integer.parseInt(idStr);
			String domainName = req.getParameter(IJsonNames.DOMAIN_NAME);
			String domainIdStr = req.getParameter(IJsonNames.DOMAIN_ID);
			Integer domainId = Integer.parseInt(domainIdStr);
			String login = "";

			Domain domain = new Domain()
				.setName(domainName)
				.setId(domainId);
			
			User user = new User()
					.setLogin(login);
		
			CompanyFull company = AON.getCompanyFull(domainName, domainId, login);
			Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, domainId, login, id);
			Workplace workplace = new Workplace();
			if(invoice.getDetails().getFirst().getWorkplace() != null &&
					invoice.getDetails().getFirst().getWorkplace().getId() != null) {
				Integer wId = invoice.getDetails().getFirst().getWorkplace().getId();
				workplace = AON.getWorkplace(domainName, domainId, login, f -> f.getIdProperty().eq(wId));				
			} else {
				workplace = AON.getWorkplace(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId)
						.and(f.getActiveProperty().eq((byte)1)));
			}

			FacturaeWriter2 facturae = new FacturaeWriter2(domain, user, company, workplace, invoice);
			byte[] data = facturae.generate();
			responseFile(resp, "FACTURAE", data, MimeType.XML);
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	private static final Logger LOGGER  = Logger.getLogger(FaceServlet.class.getName());
	
	public static File createPdf(TediInvoice invoice) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("invoice", "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			
			document.open();			
			document.add(buildHeader(invoice));
			document.add(new Paragraph(" "));
			document.add(buildBody(invoice));
			document.add(new Paragraph(" "));
			document.add(buildFooter(invoice));
		} catch (DocumentException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		document.close();
		return archivoPDF;
	}
	
	// ------------------- HEADER
	
	private static PdfPTable buildHeader(TediInvoice invoice) throws BadElementException {
        PdfPTable header = new PdfPTable(3);
        float[] medidaCeldas = {1f, 1f, 2f};
		try {
			header.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
        header.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        header.setWidthPercentage(100);
        
        header.addCell(buildHeaderInvoiceInfo(invoice));
        header.addCell(new Paragraph(" "));
		header.addCell(buildRegistry(invoice));
		
		return header;
	}
	
	private static PdfPTable buildBody(TediInvoice invoice) {
		PdfPTable body = new PdfPTable(1);
		body.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
	    body.setWidthPercentage(100);
	    body.addCell(buildDetails(invoice));
	    body.addCell(buildTaxes(invoice));
	    body.addCell(buildFinances(invoice));
	    
	    return body;
	}
	
	private static PdfPTable buildFooter(TediInvoice invoice) throws BadElementException {
		PdfPTable footer = new PdfPTable(1);
		footer.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		footer.setWidthPercentage(100);
		footer.addCell(buildQR(invoice));
		
		return footer;
	}
	private static PdfPCell buildHeaderInvoiceInfo(TediInvoice invoice) {
		PdfPTable table = new PdfPTable(2);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		
		PdfPCell title = new PdfPCell(new Phrase("FACTURA" , getFont(9)));
		title.setColspan(2);
		title.setBorder(PdfPCell.NO_BORDER);
		table.addCell(title);
		
		PdfPCell ca = new PdfPCell(new Phrase("Número: " , getFont(9)));
		ca.setBorder(PdfPCell.NO_BORDER);
		table.addCell(ca);
		table.addCell(new Paragraph(invoice.getReference(), getFont(9)));
		
		PdfPCell date = new PdfPCell(new Phrase("Fecha: " , getFont(9)));
		date.setBorder(PdfPCell.NO_BORDER);
		table.addCell(date);
		table.addCell(new Paragraph(AonDateUtils.simpleFormat(invoice.getDate()), getFont(9)));
		
		PdfPCell nif = new PdfPCell(new Phrase("NIF: " , getFont(9)));
		nif.setBorder(PdfPCell.NO_BORDER);
		table.addCell(nif);
		String document = invoice.isEmitida() ? invoice.getReceiver().getDocument() : invoice.getSender().getDocument(); 
		table.addCell(new Paragraph(document, getFont(9)));


		PdfPCell cell = new PdfPCell(table);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	

	private static PdfPCell buildRegistry(TediInvoice invoice) {
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		TediRegistry registry = invoice.isEmitida() ? invoice.getReceiver() : invoice.getSender();
		PdfPCell name = new PdfPCell(new Phrase(registry.getName() , getFont(9)));
		name.setBorder(PdfPCell.NO_BORDER);
		table.addCell(name);

		PdfPCell address = new PdfPCell(new Phrase(registry.getAddress().getAddress() , getFont(9)));
		address.setBorder(PdfPCell.NO_BORDER);
		table.addCell(address);
		
		PdfPCell zipCity = new PdfPCell(new Phrase(registry.getAddress().getPostalCode() + " " + registry.getAddress().getCity() , getFont(9)));
		zipCity.setBorder(PdfPCell.NO_BORDER);
		table.addCell(zipCity);
		
		PdfPCell provinceCountry = new PdfPCell(new Phrase(registry.getAddress().getProvince() + " " + registry.getAddress().getCountry() , getFont(9)));
		provinceCountry.setBorder(PdfPCell.NO_BORDER);
		table.addCell(provinceCountry);

		PdfPCell cell = new PdfPCell(table);
		return cell;
	}
	
	private static PdfPCell buildDetails(TediInvoice invoice) {
		PdfPTable table = new PdfPTable(10);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		
		PdfPCell description = new PdfPCell(new Phrase("Descripción" , getTitleFont(10)));
		description.setBorder(PdfPCell.NO_BORDER);
		description.setBackgroundColor(BaseColor.DARK_GRAY);
		description.setColspan(5);
		table.addCell(description);
	
		PdfPCell quantity = new PdfPCell(new Phrase("Cant." , getTitleFont(10)));
		quantity.setBorder(PdfPCell.NO_BORDER);
		quantity.setBackgroundColor(BaseColor.DARK_GRAY);
		table.addCell(quantity);
		
		PdfPCell price = new PdfPCell(new Phrase("Precio" , getTitleFont(10)));
		price.setBorder(PdfPCell.NO_BORDER);
		price.setBackgroundColor(BaseColor.DARK_GRAY);
		table.addCell(price);
		
		PdfPCell discount = new PdfPCell(new Phrase("%Dto" , getTitleFont(10)));
		discount.setBorder(PdfPCell.NO_BORDER);
		discount.setBackgroundColor(BaseColor.DARK_GRAY);
		table.addCell(discount);
		
		PdfPCell amount = new PdfPCell(new Phrase("Importe" , getTitleFont(10)));
		amount.setBorder(PdfPCell.NO_BORDER);	
		amount.setBackgroundColor(BaseColor.DARK_GRAY);
		amount.setColspan(2);
		table.addCell(amount);
		if(invoice.getDetails().isEmpty()) {
			PdfPCell descriptionValue = new PdfPCell(new Phrase("" , getFont(9)));
			descriptionValue.setBorder(PdfPCell.NO_BORDER);
			descriptionValue.setColspan(5);
			table.addCell(descriptionValue);
		
			PdfPCell quantityValue = new PdfPCell(new Phrase("", getFont(9)));
			quantityValue.setBorder(PdfPCell.NO_BORDER);
			table.addCell(quantityValue);
			
			PdfPCell priceValue = new PdfPCell(new Phrase("", getFont(9)));
			priceValue.setBorder(PdfPCell.NO_BORDER);
			table.addCell(priceValue);
			
			PdfPCell discountValue = new PdfPCell(new Phrase("", getFont(9)));
			discountValue.setBorder(PdfPCell.NO_BORDER);
			table.addCell(discountValue);
			
			PdfPCell amountValue = new PdfPCell(new Phrase("", getFont(9)));
			amountValue.setBorder(PdfPCell.NO_BORDER);
			amountValue.setColspan(2);
			table.addCell(amountValue);
		}
		invoice.getDetails().stream().forEach(detail -> {
			PdfPCell descriptionValue = new PdfPCell(new Phrase(str(detail.getDescription()) , getFont(9)));
			descriptionValue.setBorder(PdfPCell.NO_BORDER);
			descriptionValue.setColspan(5);
			table.addCell(descriptionValue);
		
			PdfPCell quantityValue = new PdfPCell(new Phrase(str(detail.getQuantity()), getFont(9)));
			quantityValue.setBorder(PdfPCell.NO_BORDER);
			table.addCell(quantityValue);
			
			PdfPCell priceValue = new PdfPCell(new Phrase(str(detail.getPrice()), getFont(9)));
			priceValue.setBorder(PdfPCell.NO_BORDER);
			table.addCell(priceValue);
			
			PdfPCell discountValue = new PdfPCell(new Phrase(str(detail.getDiscount()), getFont(9)));
			discountValue.setBorder(PdfPCell.NO_BORDER);
			table.addCell(discountValue);
			
			PdfPCell amountValue = new PdfPCell(new Phrase(str(detail.getAmount()), getFont(9)));
			amountValue.setBorder(PdfPCell.NO_BORDER);
			amountValue.setColspan(2);
			table.addCell(amountValue);
		});
		
		PdfPCell details = new PdfPCell(table);
		details.setBorder(PdfPCell.NO_BORDER);
		details.setFixedHeight(400f);
		return details; 
	}
	
	private static Boolean total = true;
	private static PdfPCell buildTaxes(TediInvoice invoice) {
		PdfPTable table = new PdfPTable(10);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		
		PdfPCell base = new PdfPCell(new Phrase("Base Imponible" , getTitleFont(10)));
		base.setBackgroundColor(BaseColor.DARK_GRAY);
		base.setBorder(PdfPCell.NO_BORDER);
		base.setColspan(2);
		table.addCell(base);
	
		PdfPCell percent = new PdfPCell(new Phrase("%" , getTitleFont(10)));
		percent.setBackgroundColor(BaseColor.DARK_GRAY);
		percent.setBorder(PdfPCell.NO_BORDER);
		percent.setColspan(2);
		table.addCell(percent);
		
		PdfPCell type = new PdfPCell(new Phrase("Tipo" , getTitleFont(10)));
		type.setBackgroundColor(BaseColor.DARK_GRAY);
		type.setBorder(PdfPCell.NO_BORDER);
		type.setColspan(2);
		table.addCell(type);
		
		PdfPCell quota = new PdfPCell(new Phrase("Cuota" , getTitleFont(10)));
		quota.setBackgroundColor(BaseColor.DARK_GRAY);
		quota.setBorder(PdfPCell.NO_BORDER);
		quota.setColspan(2);
		table.addCell(quota);
		
		PdfPCell amount = new PdfPCell(new Phrase("Total Factura" , getTitleFont(10)));
		amount.setBackgroundColor(BaseColor.BLACK);
		amount.setBorder(PdfPCell.NO_BORDER);
		amount.setColspan(2);
		table.addCell(amount);

		if(invoice.getTaxes().isEmpty()) {
			PdfPCell baseValue = new PdfPCell(new Phrase("", getFont(9)));
			baseValue.setBorder(PdfPCell.NO_BORDER);
			baseValue.setColspan(2);
			table.addCell(baseValue);
		
			PdfPCell percentValue = new PdfPCell(new Phrase("" , getFont(9)));
			percentValue.setBorder(PdfPCell.NO_BORDER);
			percentValue.setColspan(2);
			table.addCell(percentValue);
			
			PdfPCell typeValue = new PdfPCell(new Phrase("" , getFont(9)));
			typeValue.setBorder(PdfPCell.NO_BORDER);
			typeValue.setColspan(2);
			table.addCell(typeValue);
			
			PdfPCell quotaValue = new PdfPCell(new Phrase("", getFont(9)));
			quotaValue.setBorder(PdfPCell.NO_BORDER);
			quotaValue.setColspan(2);
			table.addCell(quotaValue);
			
			PdfPCell amountValue = new PdfPCell(new Phrase(str(invoice.getTotal()), getFont(9)));
			amountValue.setBorder(PdfPCell.NO_BORDER);
			amountValue.setColspan(2);
			table.addCell(amountValue);
		}
		total = true;
		invoice.getTaxes().stream().forEach(tax -> {
			PdfPCell baseValue = new PdfPCell(new Phrase(str(tax.getBase()), getFont(9)));
			baseValue.setBorder(PdfPCell.NO_BORDER);
			baseValue.setColspan(2);
			table.addCell(baseValue);
		
			PdfPCell percentValue = new PdfPCell(new Phrase(str(tax.getPercentage()) , getFont(9)));
			percentValue.setBorder(PdfPCell.NO_BORDER);
			percentValue.setColspan(2);
			table.addCell(percentValue);
			
			PdfPCell typeValue = new PdfPCell(new Phrase(str(tax.getTaxType()) , getFont(9)));
			typeValue.setBorder(PdfPCell.NO_BORDER);
			typeValue.setColspan(2);
			table.addCell(typeValue);
			
			PdfPCell quotaValue = new PdfPCell(new Phrase(str(tax.getQuota()), getFont(9)));
			quotaValue.setBorder(PdfPCell.NO_BORDER);
			quotaValue.setColspan(2);
			table.addCell(quotaValue);
			
			PdfPCell amountValue = new PdfPCell(new Phrase(total ? str(invoice.getTotal()) : "", getFont(9)));
			amountValue.setBorder(PdfPCell.NO_BORDER);
			amountValue.setColspan(2);
			table.addCell(amountValue);
			total = false;
		});
		
		PdfPCell taxes = new PdfPCell(table);
		taxes.setBorder(PdfPCell.NO_BORDER);
		taxes.setFixedHeight(75f);
		return taxes; 
	}
	
	private static PdfPCell buildFinances(TediInvoice invoice) {
		PdfPTable table = new PdfPTable(10);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		
		PdfPCell date = new PdfPCell(new Phrase("Fecha" , getTitleFont(10)));
		date.setBorder(PdfPCell.NO_BORDER);
		date.setBackgroundColor(BaseColor.DARK_GRAY);
		date.setColspan(2);
		table.addCell(date);
	
		PdfPCell paymethod = new PdfPCell(new Phrase("Forma de Pago" , getTitleFont(10)));
		paymethod.setBorder(PdfPCell.NO_BORDER);
		paymethod.setBackgroundColor(BaseColor.DARK_GRAY);
		paymethod.setColspan(2);
		table.addCell(paymethod);
		
		PdfPCell bank = new PdfPCell(new Phrase("Cuenta Bancaria" , getTitleFont(10)));
		bank.setBorder(PdfPCell.NO_BORDER);
		bank.setBackgroundColor(BaseColor.DARK_GRAY);
		bank.setColspan(4);
		table.addCell(bank);
		
		PdfPCell amount = new PdfPCell(new Phrase("Importe" , getTitleFont(10)));
		amount.setBorder(PdfPCell.NO_BORDER);
		amount.setBackgroundColor(BaseColor.DARK_GRAY);
		amount.setColspan(2);
		table.addCell(amount);
		
		if(invoice.getFinances().isEmpty()) {
			PdfPCell dateValue = new PdfPCell(new Phrase("" , getFont(9)));
			dateValue.setBorder(PdfPCell.NO_BORDER);
			dateValue.setColspan(2);
			table.addCell(dateValue);
		
			PdfPCell paymethodValue = new PdfPCell(new Phrase("" , getFont(9)));
			paymethodValue.setBorder(PdfPCell.NO_BORDER);
			paymethodValue.setColspan(2);
			table.addCell(paymethodValue);
			
			PdfPCell bankValue = new PdfPCell(new Phrase("", getFont(9)));
			bankValue.setBorder(PdfPCell.NO_BORDER);
			bankValue.setColspan(4);
			table.addCell(bankValue);
			
			PdfPCell amountValue = new PdfPCell(new Phrase("", getFont(9)));
			amountValue.setBorder(PdfPCell.NO_BORDER);
			amountValue.setColspan(2);
			table.addCell(amountValue);
		}
		
		invoice.getFinances().stream().forEach(finance -> {
			PdfPCell dateValue = new PdfPCell(new Phrase(AonDateUtils.simpleFormat(finance.getDueDate()) , getFont(9)));
			dateValue.setBorder(PdfPCell.NO_BORDER);
			dateValue.setColspan(2);
			table.addCell(dateValue);
		
			PdfPCell paymethodValue = new PdfPCell(new Phrase(str(finance.getPayMethod()) , getFont(9)));
			paymethodValue.setBorder(PdfPCell.NO_BORDER);
			paymethodValue.setColspan(2);
			table.addCell(paymethodValue);
			
			PdfPCell bankValue = new PdfPCell(new Phrase(str(finance.getIban()), getFont(9)));
			bankValue.setBorder(PdfPCell.NO_BORDER);
			bankValue.setColspan(4);
			table.addCell(bankValue);
			
			PdfPCell amountValue = new PdfPCell(new Phrase(str(finance.getAmount()), getFont(9)));
			amountValue.setBorder(PdfPCell.NO_BORDER);
			amountValue.setColspan(2);
			table.addCell(amountValue);
		});
		
		PdfPCell finances = new PdfPCell(table);
		finances.setBorder(PdfPCell.NO_BORDER);
		finances.setFixedHeight(75f);
		return finances; 
	}
	
	private static PdfPCell buildQR(TediInvoice invoice) throws BadElementException {
		JSONObject json = new JSONObject();
		json.put("reference", invoice.getReference());
		json.put("date", invoice.getDate());
		String document = invoice.isEmitida() ? invoice.getReceiver().getDocument() : invoice.getSender().getDocument(); 
		json.put("nif", document);
		json.put("total", invoice.getTotal());
		BarcodeQRCode qrcode = new BarcodeQRCode(json.toString(), 100, 100, null);
	
		PdfPCell qr = new PdfPCell(qrcode.getImage());
		qr.setBorder(PdfPCell.NO_BORDER);
		qr.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		return qr;
	}
	
	
	public static String str(Object value) {
		return value != null ? value.toString() : "";
	}
	
	
	public static Font getTitleFont(Integer size){
		Font font2 = new Font();
		font2.setSize(size);
		font2.setColor(BaseColor.WHITE);
		return font2;
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
}
