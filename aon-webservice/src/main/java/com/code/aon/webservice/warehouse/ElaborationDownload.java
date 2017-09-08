package com.code.aon.webservice.warehouse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
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

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	private Integer elaborationId = null;
	
	private String domainName;
	private Integer domainId;
	private String login;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HashMap<String, String> parameters = SecurityUtils.getInstance()
				.getParameters(req.getPathInfo().substring(1));
		domainName = parameters.get("domain");
		login = parameters.get("login");
		domainId = 1;
		Domain domain = AON.getDomain(domainName, domainId, login, f -> f
				.getNameProperty().eq(domainName));
		String _id = parameters.get("id");
		if (_id != null && !"".equals(_id)) {
			elaborationId = Integer.parseInt(_id);
		}

		List<Elaboration> elaborationList = null;
		if (elaborationId != null) {
			elaborationList = new LinkedList<>();
			elaborationList.add(AON.getFullElaboration(domain.getName(),
					domain.getId(), login,
					elaborationId));
		} else {
			elaborationList = AON.getElaborationStream(domain.getName(),
					domain.getId(), login,
					f -> f.getDomainProperty().eq(domain.getId())).collect(
					Collectors.toList());
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
		List<ElaborationDetail> elaborationDetailList = AON
				.getElaborationDetailList(domain.getName(), domain.getId(),
						login, elaboration.getId());
		Map<Integer, List<ElaborationDetailComposition>> compositionMap = new HashMap<>();
		elaborationDetailList.forEach(detail -> {
			List<ElaborationDetailComposition> compositionList = AON
					.getElaborationDetailCompositionList(domain.getName(), domain.getId(),
							login, detail.getId());
			compositionMap.put(detail.getId(), compositionList);
		});
		
		SalesDetail salesDetail = null;
		Sales sales = null;
		Customer customer = null;
		if(elaboration.getSourceId()!=null){
			ElaborationSource source = ElaborationSource.safeValueOf(elaboration.getSource());
			if(source==ElaborationSource.SALES){
				salesDetail = AON.getSalesDetailStream(domain.getName(), domain.getId(), login, 
						f -> f.getIdProperty().eq(elaboration.getSourceId()))
						.findFirst().orElse(null);
				if(salesDetail!=null && salesDetail.getSales().getId()>0){
					int salesId = salesDetail.getSales().getId(); 
					sales = AON.getSales(domain.getName(), domain.getId(), login, 
							f -> f.getIdProperty().eq(salesId));
					if(sales!=null && sales.getId()!=null){
						customer = sales.getCustomer();
					}
				}
			} else if(source==ElaborationSource.PURCHASE){
				// TODO purchase source of elaboration
			}
		}
		
		File file = createPdf(elaboration, elaborationDetailList,
				compositionMap, sales, customer, company, address,
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
	

	/*
	 * PDF METHODS
	 */
	public File createPdf(Elaboration elaboration, 
			List<ElaborationDetail> elaborationDetailList,
			Map<Integer, List<ElaborationDetailComposition>> compositionMap,
			Sales sales, Customer customer, Company company, RAddress address,
			byte[] image) {
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
			document.add(getHeader(elaboration, company, address, image));
			document.add(new Paragraph(" "));
			document.add(getSubHeader(elaboration));
			document.add(getSeparator());
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));

			Paragraph elaborationHeaderParagraph = getElaborationHeaderParagraph(elaboration);
			document.add(elaborationHeaderParagraph);
			document.add(new Paragraph(" "));
			
			if (sales != null) {
				Paragraph salesParagraph = getSalesParagraph(sales, customer);
				document.add(salesParagraph);
				document.add(new Paragraph(" "));
			}
			
			List<ItemComposition> compositionList = AON.getItemCompositionList(domainName, domainId, login, elaboration.getItem().getId());
			
			Paragraph compositionParagraph = getCompositionParagraph(elaboration, compositionList);
			document.add(compositionParagraph);
			document.add(new Paragraph(" "));
			
			if(elaborationDetailList!=null && !elaborationDetailList.isEmpty()){
				Paragraph elaborationDetailParagraph = getElaborationDetailParagraph(elaborationDetailList, compositionMap);
				document.add(elaborationDetailParagraph);
				document.add(new Paragraph(" "));
			}

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

		header.addCell(image!=null?getHeaderLogo(image):new PdfPCell());
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

		PdfPCell c3 = new PdfPCell(new Phrase(dateFormat.format(elaboration.getDate()),
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
		Paragraph title = new Paragraph("Orden de elaboración", getTitleFont());
		title.setAlignment(Element.ALIGN_CENTER);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.addElement(title);
		return cell;
	}

	private static Paragraph getElaborationHeaderParagraph(Elaboration elaboration){
		Paragraph paragraph = new Paragraph();

		PdfPTable tableM = new PdfPTable(1);
		tableM.setWidthPercentage(100);
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		
		PdfPTable header = new PdfPTable(4);
		header.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		float[] medidaCeldas = {1f, 1f, 0.5f, 3.5f};
		try {
			header.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		PdfPCell referenceCode = new PdfPCell(new Phrase("Serie/Número",getFont1()));
		referenceCode.setBorder(PdfPCell.NO_BORDER);
		header.addCell(referenceCode);
		header.addCell(new Phrase(elaboration.getSeries()+"/"+elaboration.getNumber(),getFont2()));
		PdfPCell date = new PdfPCell(new Phrase("Fecha",getFont1()));
		date.setBorder(PdfPCell.NO_BORDER);
		header.addCell(date);
		header.addCell(new Phrase(dateFormat.format(elaboration.getDate()),getFont2()));
		
		PdfPCell quantity = new PdfPCell(new Phrase("Cantidad",getFont1()));
		quantity.setBorder(PdfPCell.NO_BORDER);
		header.addCell(quantity);
		header.addCell(new Phrase(String.valueOf(elaboration.getQuantity()),getFont2()));
		
		PdfPCell item = new PdfPCell(new Phrase("Producto",getFont1()));
		item.setBorder(PdfPCell.NO_BORDER);
		header.addCell(item);
		String description = elaboration.getDescription();
		description = description!=null && !"".equals(description)?description
				:(elaboration.getItem().getProduct()!=null?elaboration.getItem().getProduct().getName():"");
		header.addCell(new Phrase(description,getFont2()));
		
		PdfPCell warehouse = new PdfPCell(new Phrase("Almacén",getFont1()));
		warehouse.setBorder(PdfPCell.NO_BORDER);
		header.addCell(warehouse);
		header.addCell(new Phrase(elaboration.getWarehouse()!=null?elaboration.getWarehouse().getName():"",getFont2()));
		header.addCell("");
		header.addCell("");
		table.addCell(header);
		
		if(elaboration.getComments()!=null && !"".equals(elaboration.getComments().trim())){
			table.addCell(getDottedSeparator());
			
			PdfPCell cell1 = new PdfPCell(new Phrase("Observaciones",getFont1()));
			cell1.setBorder(PdfPCell.NO_BORDER);
			PdfPCell cell2 = new PdfPCell(new Phrase(elaboration.getComments(),getFont2()));
			cell2.setBorder(PdfPCell.NO_BORDER);
			PdfPTable detail = new PdfPTable(2);
			float[] medidaCeldas2 = {1f, 5f};
			try {
				detail.setWidths(medidaCeldas2);
			} catch (DocumentException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
			detail.addCell(cell1);
			detail.addCell(cell2);
			detail.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			table.addCell(detail);
		}
		
		
		tableM.addCell(table);
		paragraph.add(tableM);
		return paragraph;
	}
	
	private Paragraph getCompositionParagraph(
			Elaboration elaboration,
			List<ItemComposition> compositionList) {
		Paragraph paragraph = new Paragraph();
		
		PdfPTable tableM = new PdfPTable(1);
		tableM.setWidthPercentage(100);
		
		
		String groupHeader = " COMPOSICION:  ";
		groupHeader += elaboration.getItem().getProduct().getName();
		tableM.addCell(new Phrase(groupHeader,getFont2()));

		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		
		if(compositionList==null || compositionList.isEmpty()){
			PdfPCell label = new PdfPCell(new Phrase("Composicion no definida",getFont2()));
			table.addCell(label);
		} else {
			compositionList.forEach(composition -> {
				Item compositionItem = AON.getItem(domainName, domainId, login, composition.getCompositionItemId());
				
				PdfPTable detail = new PdfPTable(6);
				detail.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
				float[] medidaCeldas = {0.5f, 0.5f, 0.5f, 2.5f, 0.5f, 1.5f};
				try {
					detail.setWidths(medidaCeldas);
				} catch (DocumentException e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				}
				
				PdfPCell quantity = new PdfPCell(new Phrase("Cantidad",getFont1()));
				quantity.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(quantity);
				detail.addCell(new Phrase(String.valueOf(composition.getQuantity()),getFont2()));
				
				PdfPCell item = new PdfPCell(new Phrase("Producto",getFont1()));
				item.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(item);
				// TODO
				String detailText = compositionItem.getDetail()!=null && !"".equals(compositionItem.getDetail().trim()) ? compositionItem.getDetail() : "";
				detailText += detailText!=null && !"".equals(detailText) 
						&& compositionItem.getDetail2()!=null && !"".equals(compositionItem.getDetail2().trim()) ? " / " : "";
				detailText += compositionItem.getDetail2()!=null && !"".equals(compositionItem.getDetail2().trim()) ? compositionItem.getDetail2() : "";
				detailText += detailText!=null && !"".equals(detailText) 
						&& compositionItem.getDetail3()!=null && !"".equals(compositionItem.getDetail3().trim()) ? " / " : "";
				detailText += compositionItem.getDetail3()!=null && !"".equals(compositionItem.getDetail3().trim()) ? compositionItem.getDetail3() : "";
				detailText = detailText!=null && !"".equals(detailText) ? " [" +detailText + "]" : "";
				String itemDescription = compositionItem.getProduct().getName() + detailText + " (" + compositionItem.getProduct().getCode() + ")";
				detail.addCell(new Phrase(String.valueOf(compositionItem.getProduct()!=null?itemDescription:""),getFont2()));
				
				PdfPCell warehouse = new PdfPCell(new Phrase("",getFont1()));
				warehouse.setBorder(PdfPCell.NO_BORDER);
				detail.addCell(warehouse);
				detail.addCell(new Phrase("",getFont2()));
				
				table.addCell(detail);
			});
		}
		tableM.addCell(table);
		
		
		paragraph.add(tableM);
		return paragraph;
	}
	
	private static Paragraph getElaborationDetailParagraph(
			ElaborationDetail elaborationDetail,
			List<ElaborationDetailComposition> compositionList) {
		Paragraph paragraph = new Paragraph();
		
		PdfPTable tableM = new PdfPTable(1);
		tableM.setWidthPercentage(100);
		
		
		String groupHeader = "ELABORADO: " + elaborationDetail.getQuantity() + " uds. ";
		groupHeader += "(" + elaborationDetail.getItem().getSerialNumber() + ") ";
		groupHeader += dateFormat.format(elaborationDetail.getDate());
		tableM.addCell(new Phrase(groupHeader,getFont2()));
		
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		
		compositionList.forEach(composition -> {
			PdfPTable detail = new PdfPTable(6);
			detail.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			float[] medidaCeldas = {0.5f, 0.5f, 0.5f, 2.5f, 0.5f, 1.5f};
			try {
				detail.setWidths(medidaCeldas);
			} catch (DocumentException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
			
			PdfPCell quantity = new PdfPCell(new Phrase("Cantidad",getFont1()));
			quantity.setBorder(PdfPCell.NO_BORDER);
			detail.addCell(quantity);
			detail.addCell(new Phrase(String.valueOf(composition.getQuantity()),getFont2()));
			
			PdfPCell item = new PdfPCell(new Phrase("Producto",getFont1()));
			item.setBorder(PdfPCell.NO_BORDER);
			detail.addCell(item);
			detail.addCell(new Phrase(String.valueOf(composition.getItem().getProduct()!=null?composition.getItem().getProduct().getName():""),getFont2()));
			
			PdfPCell warehouse = new PdfPCell(new Phrase("Almacén",getFont1()));
			warehouse.setBorder(PdfPCell.NO_BORDER);
			detail.addCell(warehouse);
			detail.addCell(new Phrase(composition.getWarehouse()!=null?composition.getWarehouse().getName():"",getFont2()));
			
			table.addCell(detail);
		});
		
		tableM.addCell(table);
		
		paragraph.add(tableM);
		return paragraph;
	}
	
	private static Paragraph getElaborationDetailParagraph(
			List<ElaborationDetail> elaborationDetailList,
			Map<Integer, List<ElaborationDetailComposition>> compositionMap) {
		
		Paragraph paragraph = new Paragraph();
		
		elaborationDetailList.forEach(elaborationDetail -> {
			List<ElaborationDetailComposition> compositionList = compositionMap.get(elaborationDetail.getId());
			paragraph.add(
					getElaborationDetailParagraph(elaborationDetail, compositionList)
					);
		});
		
		
//		PdfPTable tableM = new PdfPTable(1);
//		tableM.setWidthPercentage(100);
//		
//		elaborationDetailList.forEach(elaborationDetail -> {
//			String groupHeader = elaborationDetail.getQuantity() + " uds. ";
//			groupHeader += "(" + elaborationDetail.getItem().getSerialNumber() + ") ";
//			groupHeader += dateFormat.format(elaborationDetail.getDate());
//			tableM.addCell(new Phrase(groupHeader,getFont2()));
//
//			PdfPTable table = new PdfPTable(1);
//			table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
//			table.setWidthPercentage(100);
//			
//			List<ElaborationDetailComposition> list = compositionMap.get(elaborationDetail.getId());
//			list.forEach(composition -> {
//				PdfPTable detail = new PdfPTable(6);
//				detail.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
//				float[] medidaCeldas = {0.5f, 0.5f, 0.5f, 2.5f, 0.5f, 1.5f};
//				try {
//					detail.setWidths(medidaCeldas);
//				} catch (DocumentException e) {
//					LOGGER.log(Level.SEVERE, e.getMessage());
//				}
//				
//				PdfPCell quantity = new PdfPCell(new Phrase("Cantidad",getFont1()));
//				quantity.setBorder(PdfPCell.NO_BORDER);
//				detail.addCell(quantity);
//				detail.addCell(new Phrase(String.valueOf(composition.getQuantity()),getFont2()));
//				
//				PdfPCell item = new PdfPCell(new Phrase("Producto",getFont1()));
//				item.setBorder(PdfPCell.NO_BORDER);
//				detail.addCell(item);
//				detail.addCell(new Phrase(String.valueOf(composition.getItem().getProduct()!=null?composition.getItem().getProduct().getName():""),getFont2()));
//				
//				PdfPCell warehouse = new PdfPCell(new Phrase("Almacén",getFont1()));
//				warehouse.setBorder(PdfPCell.NO_BORDER);
//				detail.addCell(warehouse);
//				detail.addCell(new Phrase(composition.getWarehouse()!=null?composition.getWarehouse().getName():"",getFont2()));
//				
//				table.addCell(detail);
//			});
//			
//			tableM.addCell(table);
//		});
//		
//		paragraph.add(tableM);
		return paragraph;
	}
	
	
	private static Paragraph getSalesParagraph(Sales sales, Customer customer){
		Paragraph paragraph = new Paragraph();
		
		PdfPTable tableM = new PdfPTable(1);
		tableM.setWidthPercentage(100);
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		
		PdfPTable destinatario = new PdfPTable(4);
		destinatario.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		float[] medidaCeldas = {1f, 1f, 0.5f, 3.5f};
		try {
			destinatario.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	
		destinatario.addCell(new Phrase("Pedido:",getFont1()));
		destinatario.addCell(new Phrase(sales.getSeries()+"/"+sales.getNumber(),getFont2()));
		PdfPCell c = new PdfPCell(new Phrase("Cliente",getFont1()));
		c.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(c);
		PdfPCell ca = new PdfPCell(new Phrase(customer.getName(),getFont1()));
		ca.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(ca);
		
		destinatario.addCell(new Phrase("Fecha pedido:",getFont1()));
		destinatario.addCell(new Phrase(sales.getIssueDate()!=null?dateFormat.format(sales.getIssueDate()):"",getFont2()));
		PdfPCell cn = new PdfPCell(new Phrase("NIF",getFont1()));
		cn.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(cn);
		PdfPCell cd = new PdfPCell(new Phrase(customer.getDocument(),getFont1()));
		cd.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(cd);
		
		destinatario.addCell(new Phrase("Fecha entrega:",getFont1()));
		destinatario.addCell(new Phrase(sales.getDeliveryDate()!=null?dateFormat.format(sales.getDeliveryDate()):"",getFont2()));
		destinatario.addCell("");
		PdfPCell cbc2 = new PdfPCell(new Phrase("",getFont2()));
		cbc2.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(cbc2);
		
		destinatario.addCell(new Phrase("Referencia compra:",getFont1()));
		destinatario.addCell(new Phrase(sales.getPurchaseReference(),getFont2()));
		destinatario.addCell("");
		PdfPCell cb = new PdfPCell(new Phrase("",getFont2()));
		cb.setBorder(PdfPCell.NO_BORDER);
		destinatario.addCell(cb);
		
		table.addCell(destinatario);

		if(sales.getComments()!=null && !"".equals(sales.getComments().trim())){
			table.addCell(getDottedSeparator());
			
			PdfPCell cell1 = new PdfPCell(new Phrase("Observaciones",getFont1()));
			cell1.setBorder(PdfPCell.NO_BORDER);
			PdfPCell cell2 = new PdfPCell(new Phrase(sales.getComments(),getFont2()));
			cell2.setBorder(PdfPCell.NO_BORDER);
			PdfPTable detail = new PdfPTable(2);
			float[] medidaCeldas2 = {1f, 5f};
			try {
				detail.setWidths(medidaCeldas2);
			} catch (DocumentException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
			detail.addCell(cell1);
			detail.addCell(cell2);
			detail.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			table.addCell(detail);
		}
		
		tableM.addCell(table);
		paragraph.add(tableM);
		return paragraph;
	}
	
	
	/*
	 * 
	 */
	
	
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

}
