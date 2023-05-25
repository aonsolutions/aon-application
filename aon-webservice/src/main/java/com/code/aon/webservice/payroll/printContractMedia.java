package com.code.aon.webservice.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

@SuppressWarnings("serial")
@WebServlet(name = "PrintContractMedia", urlPatterns = {"/aon_gwt_aio/print_contract_media/*"})
public class printContractMedia extends HttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(printContractMedia.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Print Salaried Staff - GET METHOD");
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Integer year = Integer.parseInt(parameters.get("year"));
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		Boolean resume = parameters.get("resume").equalsIgnoreCase("true");
		Boolean detail = parameters.get("detail").equalsIgnoreCase("true");
		
		JSONObject json = new JSONObject();
		JSONArray arrayAct = ContractServlet.getContractMediaList(domain, login, year);
		JSONArray arrayAnt = ContractServlet.getContractMediaList(domain, login, year-1);

		json.put("contract_act", arrayAct);
		json.put("contract_ant", arrayAnt);
		
		Company company = AON.getCompanyForDomain(domain.getName(), domain.getId(), login);
		JSONObject jsonCompany = ToJSON.objectToJSON(company.getId(), company.getName());
		jsonCompany.put("document", company.getDocument());
		json.put("company", jsonCompany);
		json.put("year", year);
		
		JSONArray categoryArray = new JSONArray();
		PAYROLL.getAgreementLevelCategoryStream(domain.getName(), domain.getId(), login, f ->
		f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
		.forEach(r -> {
			categoryArray.put(ToJSON.objectToJSON(r.getId(), r.getDescription()));
		});
		json.put("categories", categoryArray);
		File file = createPdf(json, resume, detail);
		
        Utils.addCorsHeader(resp);
        resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition", "inline; filename=\"" + file.getName() + ".pdf\";");
		FileInputStream fileInpurOs =  new FileInputStream(file);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		resp.flushBuffer();

		fileInpurOs.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Print Salaried Staff - POST METHOD");
	}
	
	public static File createPdf(JSONObject json, Boolean resume, Boolean detail) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("Listado Media contratos " + json.getInt("year"), "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		Document document = new Document();
		if(resume){
			document.setPageSize(PageSize.A4);
		} else document.setPageSize(PageSize.A4.rotate());
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			document.open();
			if(resume && detail){
				printContractMediaResume.writeDocument(document, json);
				document.setPageSize(PageSize.A4.rotate());
				document.newPage();
				printContractMediaList.writeDocument(document, json);

			}else if(resume){
				printContractMediaResume.writeDocument(document, json);
			} else if(detail){
				printContractMediaList.writeDocument(document, json);
			}
		} catch (DocumentException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		document.close();
		return archivoPDF;
	}
}
