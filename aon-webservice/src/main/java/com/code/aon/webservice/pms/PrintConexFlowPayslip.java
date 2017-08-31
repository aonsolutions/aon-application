package com.code.aon.webservice.pms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.google.apis.DriveUtils;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.ProjectAttachmentType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;

@WebServlet(name = "printConexFlowPayslip", urlPatterns = {"/print_conexflowpayslip/*"})
public class PrintConexFlowPayslip extends HttpServlet{
	
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
	
		Domain domain = new Domain().setName(domainName).setId(domainId);
		User user = new User().setLogin(login);
		LinkedList<Attach>  attachList = AON.getAttachList(domainName, domainId, login, f -> 
			f.getTypeProperty().eq(ProjectAttachmentType.PAYSLIP.value())
			.and(f.getAttachModuleProperty().eq(projectId))
		, AttachType.PROJECT);
		
		
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("payslip", "pdf");
		} catch (IOException e) {
		}
		
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.open();
		
		attachList.stream().forEach(r ->{
			if(r.getData() == null){
				byte[] b = DriveUtils.getByteFile(domain, user, r.getDriveId(), r.getId());
				r.setData(b);
			}
			
			if(r.getData() != null){
				String html = new String(r.getData());				
				
				HTMLWorker htmlWorker = new HTMLWorker(document);
				try {
					htmlWorker.parse(new StringReader(html));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				document.newPage();		
			}

		});

        document.close();
        
        Utils.addCorsHeader(resp);
        resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition", "inline; filename=\"" + archivoPDF.getName() + ".pdf\";");
		FileInputStream fileInpurOs =  new FileInputStream(archivoPDF);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		resp.flushBuffer();

		fileInpurOs.close();
		
	}
	
}
