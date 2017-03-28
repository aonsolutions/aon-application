package com.code.aon.webservice.pms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.ProjectAttachmentType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@WebServlet(name = "printConexFlowPayslip", urlPatterns = {"/print_conexflow_log/*"})
public class PrintConexFlowLog extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	       		
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain_name");
		String domainIdStr = parameters.get("domain_id");
		Integer domainId = Integer.parseInt(domainIdStr);
		String login = parameters.get("login");

		String projectIdStr = parameters.get("project");
		Integer projectId = Integer.parseInt(projectIdStr);
	
		
		
		LinkedList<Attach>  attachList = AON.getAttachList(domainName, domainId, login, f -> 
			f.getTypeProperty().eq(ProjectAttachmentType.PAYSLIP.value())
			.and(f.getAttachModuleProperty().eq(projectId))
			.and(f.getDescriptionProperty().like("CONEXFLOW%PAYSLIP#%"))
		, AttachType.PROJECT);
		
		
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("conexflow_log", "pdf");
		} catch (IOException e) {
		}
		
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.open();
		
		
		PdfPTable table = new PdfPTable(3);
		PdfPCell date = new PdfPCell(new Phrase("Fecha", getFont()));
		date.setBorder(PdfPCell.NO_BORDER);
		table.addCell(date);
		
		PdfPCell text = new PdfPCell(new Phrase("Descripcion", getFont()));
		text.setBorder(PdfPCell.NO_BORDER);
		table.addCell(text);
		
		PdfPCell amount = new PdfPCell(new Phrase("Importe", getFont()));
		amount.setBorder(PdfPCell.NO_BORDER);
		table.addCell(amount);
		
		attachList.stream().sorted((e1, e2) -> e2.getCreationDate().compareTo(e1.getCreationDate()))
		.forEach(r ->{
			
			PdfPCell d = new PdfPCell(new Phrase(r.getCreationDate().toString(), getFont2()));
			d.setBorder(PdfPCell.NO_BORDER);
			table.addCell(d);
			
			PdfPCell t = new PdfPCell(new Phrase(getDescription(r.getDescription()), getFont2()));
			t.setBorder(PdfPCell.NO_BORDER);
			table.addCell(t);
			
			PdfPCell a = new PdfPCell(new Phrase(getAmount(r.getDescription()), getFont2()));
			a.setBorder(PdfPCell.NO_BORDER);
			table.addCell(a);
							
		});
		try {
			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
        document.close();
        
        Utils.addCorsHeader(resp);
        resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition", "inline; filename=\"" + archivoPDF.getName() + ".pdf\";");
		FileInputStream fileInpurOs =  new FileInputStream(archivoPDF);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		resp.flushBuffer();

		fileInpurOs.close();
		
	}
	
	private static String getDescription(String description){
		if (description.contains("T#")) return "Obtener Token / Get Token";
		else if(description.contains("-CHECK#")) return "Tarjeta correcta / Card Ok";
		else if(description.contains("-CHECK-FAIL#")) return "Tarjeta No válida / Card Fail";
		else if(description.contains("P#")) return "Preautorización correcta / Preauthorization OK";
		else if(description.contains("P-FAIL#")) return "Preautorización fallida / Preauthorization Fail";
		else if(description.contains("ANT_TNR")) return "Cobro No Reembolsable / Not refundable Sale";
		else if(description.contains("V#")) return "Cobro Anticipo / Advance Sale";
		else if(description.contains("V-FAIL#")) return "Cobro fallido / Sale Fail";
		else if(description.contains("A#")) return "Cancelación correcta / Cancel OK";
		else if(description.contains("A-FAIL#")) return "Cancelación fallida / Cancel Fail";
		else if(description.contains("D#")) return "Reembolso correcto / Refund Fail";
		else if(description.contains("D-FAIL#")) return "Reembolso fallido / Refund Fail";
		else return "Otra Operacion  / Another operation";	
	}
	
	private static String getAmount(String description){
		Integer index = description.lastIndexOf("#");
		return description.substring(index + 1);
	}
	
	private static Font getFont2(){
		Font font2 = new Font();
		font2.setSize(12);
		return font2;
	}

	private static Font getFont(){
		Font font1 = new Font();
		font1.setSize(12);
		font1.setStyle(Font.BOLD);
		return font1;
	}
}
