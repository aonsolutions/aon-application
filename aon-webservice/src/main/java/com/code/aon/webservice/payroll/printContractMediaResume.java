package com.code.aon.webservice.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

@SuppressWarnings("serial")
@WebServlet(name = "PrintContractMediaResume", urlPatterns = {"/aon_gwt_aio/print_contract_media_resume/*"})
public class printContractMediaResume extends HttpServlet{
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private static final Logger LOGGER  = Logger.getLogger(printContractMediaResume.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Print Salaried Staff - GET METHOD");
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Integer year = Integer.parseInt(parameters.get("year"));
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		
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
		File file = createPdf(json);
		
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
	
	public static File createPdf(JSONObject json) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("Listado Media contratos " + json.getInt("year"), "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));

			document.open();
			
			document.add(getTitle());
			
			document.add(new Paragraph(" "));
			
			JSONObject company = json.getJSONObject("company");
			document.add(getCompany(company));
			
			document.add(new Paragraph(" "));
			
			document.add(getSalariedStaff(json));
			
			document.add(new Paragraph(" "));
			
			document.add(getSalariedStaffCategory(json));
			
			
			
		} catch (DocumentException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		document.close();
		return archivoPDF;
	}
	
	private static PdfPTable getTitle(){
		String boeInfo = "INFORME DE PERSONAL ASALARIADO";
		Paragraph title = new Paragraph(boeInfo, getTitleFont());
		title.setAlignment(Element.ALIGN_CENTER);
		title.setIndentationRight(20);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.addElement(title);
		
		PdfPTable titleTable = new PdfPTable(1);
		titleTable.addCell(cell);
		return titleTable;
	}
	
	private static PdfPTable getCompany(JSONObject json){
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		PdfPCell empresa = new PdfPCell(new Phrase("EMPRESA",getFont1()));
		empresa.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(empresa);
		
		table.addCell(emptyCell());
		
		PdfPCell empresaName = new PdfPCell(new Phrase(json.getString("name"),getFont1()));
		empresaName.setColspan(2);
		table.addCell(empresaName);
		
		return table;
	}
	
	private static PdfPTable getSalariedStaff(JSONObject json){
		JSONArray arrayAct = json.getJSONArray("contract_act");
		Double fixedAct = 0.0, fixedAnt = 0.0, unfixedAct = 0.0, unfixedAnt = 0.0, 
			   fixedActH = 0.0, fixedAntH = 0.0, unfixedActH = 0.0, unfixedAntH = 0.0,
			   fixedActM = 0.0, fixedAntM = 0.0, unfixedActM = 0.0, unfixedAntM = 0.0,
			   discapAct = 0.0, discapAnt = 0.0 ;
		for(Integer i = 0; i < arrayAct.length(); i++){
			fixedAct = fixedAct + arrayAct.getJSONObject(i).getDouble("fixed");
			unfixedAct = unfixedAct + arrayAct.getJSONObject(i).getDouble("unfixed");
			if(arrayAct.getJSONObject(i).getJSONObject("disability").getInt("id") != -1){
				discapAct = discapAct + arrayAct.getJSONObject(i).getDouble("fixed") 
						+ arrayAct.getJSONObject(i).getDouble("unfixed");
			}
			if(arrayAct.getJSONObject(i).getJSONObject("gender").getInt("id") == 1){
				fixedActM = fixedActM + arrayAct.getJSONObject(i).getDouble("fixed");
				unfixedActM = unfixedActM + arrayAct.getJSONObject(i).getDouble("unfixed");
			} else {
				fixedActH = fixedActH + arrayAct.getJSONObject(i).getDouble("fixed");
				unfixedActH = unfixedActH + arrayAct.getJSONObject(i).getDouble("unfixed");
			}
		}
		JSONArray arrayAnt = json.getJSONArray("contract_ant");
		for(Integer i = 0; i < arrayAnt.length(); i++){
			fixedAnt = fixedAnt + arrayAnt.getJSONObject(i).getDouble("fixed");
			unfixedAnt = unfixedAnt + arrayAnt.getJSONObject(i).getDouble("unfixed");
			if(arrayAnt.getJSONObject(i).getJSONObject("disability").getInt("id") != -1){
				discapAnt = discapAnt + arrayAnt.getJSONObject(i).getDouble("fixed") 
						+ arrayAnt.getJSONObject(i).getDouble("unfixed");
			}
			if(arrayAnt.getJSONObject(i).getJSONObject("gender").getInt("id") == 1){
				fixedAntM = fixedAntM + arrayAnt.getJSONObject(i).getDouble("fixed");
				unfixedAntM = unfixedAntM + arrayAnt.getJSONObject(i).getDouble("unfixed");
			} else {
				fixedAntH = fixedAntH + arrayAnt.getJSONObject(i).getDouble("fixed");
				unfixedAntH = unfixedAntH + arrayAnt.getJSONObject(i).getDouble("unfixed");
			}
		}
		
		
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		PdfPCell empresa = new PdfPCell(new Phrase("PERSONAL ASALARIADO",getFont1()));
		empresa.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(empresa);
		table.addCell(emptyCell());
		
		
		PdfPTable table2 = new PdfPTable(1);
		table2.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

		String a = "	a) Número medio de personas empleadas en el curso del ejercicio, por tipo de contrato y empleo con discapacidad:";
		PdfPCell cellA = new PdfPCell(new Phrase(a,getFont2()));
		cellA.setBorder(PdfPCell.NO_BORDER);
		table2.addCell(cellA);
		
		PdfPTable table3 = new PdfPTable(4);
		PdfPCell emptyCel = emptyCell();
		emptyCel.setColspan(2);
		table3.addCell(emptyCel);
		
		PdfPCell ejAct = new PdfPCell(new Phrase("EJERCICIO ACTUAL",getFont1()));
		ejAct.setBorder(PdfPCell.NO_BORDER);
		table3.addCell(ejAct);
		
		PdfPCell ejAnt = new PdfPCell(new Phrase("EJERCICIO ANTERIOR",getFont1()));
		ejAnt.setBorder(PdfPCell.NO_BORDER);
		table3.addCell(ejAnt);
		
		table3.addCell(emptyCell());
		
		PdfPCell fix = new PdfPCell(new Phrase("FIJO",getFont1()));
		fix.setBorder(PdfPCell.NO_BORDER);
		table3.addCell(fix);
		
		PdfPCell ejAct2 = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedAct)),getFont2()));
		table3.addCell(ejAct2);
		
		PdfPCell ejAnt2 = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedAnt)),getFont2()));
		table3.addCell(ejAnt2);
		
		table3.addCell(emptyCell());
		
		PdfPCell unfix = new PdfPCell(new Phrase("NO FIJO",getFont1()));
		unfix.setBorder(PdfPCell.NO_BORDER);
		table3.addCell(unfix);
		
		PdfPCell ejAct3 = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedAct)),getFont2()));
		table3.addCell(ejAct3);
		
		PdfPCell ejAnt3 = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedAnt)),getFont2()));
		table3.addCell(ejAnt3);
		
		table2.addCell(table3);
		
		String a2 = "			Del cual: Personas empleadas con discapacidad mayor o igual al 33% (o calificación equivalente local):";
		PdfPCell cellA2 = new PdfPCell(new Phrase(a2,getFont2()));
		cellA2.setBorder(PdfPCell.NO_BORDER);
		table2.addCell(cellA2);
		
		PdfPTable table4 = new PdfPTable(4);
		
		table4.addCell(emptyCell());
		table4.addCell(emptyCell());
	
		PdfPCell ejAct4 = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(discapAct)),getFont2()));
		table4.addCell(ejAct4);
		
		PdfPCell ejAnt4 = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(discapAnt)),getFont2()));
		table4.addCell(ejAnt4);
		
		table2.addCell(table4);
		
		
		String b = "	b) Personal asalariado al término del ejercicio, por tipo de contrato y por sexo:";
		PdfPCell cellB = new PdfPCell(new Phrase(b,getFont2()));
		cellB.setBorder(PdfPCell.NO_BORDER);
		table2.addCell(cellB);
		
		PdfPTable table5 = new PdfPTable(5);
		
		table5.addCell(emptyCell());
		
		PdfPCell ejAct5 = new PdfPCell(new Phrase("EJERCICIO ACTUAL",getFont1()));
		ejAct5.setBorder(PdfPCell.NO_BORDER);
		ejAct5.setColspan(2);
		table5.addCell(ejAct5);
		
		PdfPCell ejAnt5 = new PdfPCell(new Phrase("EJERCICIO ANTERIOR",getFont1()));
		ejAnt5.setBorder(PdfPCell.NO_BORDER);
		ejAnt5.setColspan(2);
		table5.addCell(ejAnt5);
		
		
		table5.addCell(emptyCell());
		
		PdfPCell ejAct6H = new PdfPCell(new Phrase("HOMBRE",getFont2()));
		table5.addCell(ejAct6H);
		
		PdfPCell ejAct6M = new PdfPCell(new Phrase("MUJERES",getFont2()));
		table5.addCell(ejAct6M);
		
		PdfPCell ejAnt6H = new PdfPCell(new Phrase("HOMBRES",getFont2()));
		table5.addCell(ejAnt6H);
		
		PdfPCell ejAnt6M = new PdfPCell(new Phrase("MUJERES",getFont2()));
		table5.addCell(ejAnt6M);
		
		PdfPCell fix2 = new PdfPCell(new Phrase("FIJO",getFont1()));
		fix2.setBorder(PdfPCell.NO_BORDER);
		table5.addCell(fix2);
		
		PdfPCell ejAct7H = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedActH)),getFont2()));
		table5.addCell(ejAct7H);
		
		PdfPCell ejAct7M = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedActM)),getFont2()));
		table5.addCell(ejAct7M);
		
		PdfPCell ejAnt7H = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedAntH)),getFont2()));
		table5.addCell(ejAnt7H);
		
		PdfPCell ejAnt7M = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedAntM)),getFont2()));
		table5.addCell(ejAnt7M);
		
		PdfPCell unfix2 = new PdfPCell(new Phrase("NO FIJO",getFont1()));
		unfix2.setBorder(PdfPCell.NO_BORDER);
		table5.addCell(unfix2);
		
		PdfPCell ejAct8H = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedActH)),getFont2()));
		table5.addCell(ejAct8H);
		
		PdfPCell ejAct8M = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedActM)),getFont2()));
		table5.addCell(ejAct8M);
		
		PdfPCell ejAnt8H = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedAntH)),getFont2()));
		table5.addCell(ejAnt8H);
		
		PdfPCell ejAnt8M = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedAntM)),getFont2()));
		table5.addCell(ejAnt8M);
		
		table2.addCell(table5);
		

		table2.addCell(emptyCell());
		
		PdfPCell c = new PdfPCell(table2);
		c.setColspan(2);
		table.addCell(c);
		
		return table;
	}
	
	private static PdfPTable getSalariedStaffCategory(JSONObject json){
		JSONArray arrayAct = json.getJSONArray("contract_act");
		JSONArray arrayAnt = json.getJSONArray("contract_ant");
		
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		PdfPCell empresa = new PdfPCell(new Phrase("PERSONAL ASALARIADO POR CATEGORIAS",getFont1()));
		empresa.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(empresa);
		
		table.addCell(emptyCell());

		PdfPTable table1 = new PdfPTable(3);
		table1.setWidthPercentage(100);
		float[] medidaCeldas1 = {3.8f, 1.1f, 1.1f};
		try {
			table1.setWidths(medidaCeldas1);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		String x ="Número medio de personas empleadas en el curso del ejercicio, por categorías";
		PdfPCell a = new PdfPCell(new Phrase(x,getFont1()));
		table1.addCell(a);
		
		PdfPCell ejAct = new PdfPCell(new Phrase("EJERCICIO ACTUAL",getFont1()));
		table1.addCell(ejAct);
		
		PdfPCell ejAnt = new PdfPCell(new Phrase("EJERCICIO ANTERIOR",getFont1()));
		table1.addCell(ejAnt);
		
		String x1 ="Directores generales y presidentes ejecutivos";
		PdfPCell a1 = new PdfPCell(new Phrase(x1,getFont2()));
		a1.setBorder(PdfPCell.NO_BORDER);
		table1.addCell(a1);
		
		PdfPCell ejAct1 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAct1);
		
		PdfPCell ejAnt1 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAnt1);
		
		String x2 ="Resto de directores y gerentes";
		PdfPCell a2 = new PdfPCell(new Phrase(x2,getFont2()));
		a2.setBorder(PdfPCell.NO_BORDER);
		table1.addCell(a2);
		
		PdfPCell ejAct2 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAct2);
		
		PdfPCell ejAnt2 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAnt2);
		
		String x3 ="Técnicos y profesionales científicos e intelectuales y profesionales de apoyo";
		PdfPCell a3 = new PdfPCell(new Phrase(x3,getFont2()));
		a3.setBorder(PdfPCell.NO_BORDER);
		table1.addCell(a3);
		
		PdfPCell ejAct3 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAct3);
		
		PdfPCell ejAnt3 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAnt3);
		
		String x4 ="Empleados contables, administrativos y otros empleados de oficina";
		PdfPCell a4 = new PdfPCell(new Phrase(x4,getFont2()));
		a4.setBorder(PdfPCell.NO_BORDER);
		table1.addCell(a4);
		
		PdfPCell ejAct4 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAct4);
		
		PdfPCell ejAnt4 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAnt4);
		
		String x5 ="Comerciales, vendedores y similares";
		PdfPCell a5 = new PdfPCell(new Phrase(x5,getFont2()));
		a5.setBorder(PdfPCell.NO_BORDER);
		table1.addCell(a5);
		
		PdfPCell ejAct5 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAct5);
		
		PdfPCell ejAnt5 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAnt5);
		
		String x6 ="Resto de personal cualificado";
		PdfPCell a6 = new PdfPCell(new Phrase(x6,getFont2()));
		a6.setBorder(PdfPCell.NO_BORDER);
		table1.addCell(a6);
		
		PdfPCell ejAct6 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAct6);
		
		PdfPCell ejAnt6 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAnt6);
		
		String x7 ="Operaciones elementales";
		PdfPCell a7 = new PdfPCell(new Phrase(x7,getFont2()));
		a7.setBorder(PdfPCell.NO_BORDER);
		table1.addCell(a7);
		
		PdfPCell ejAct7 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAct7);
		
		PdfPCell ejAnt7 = new PdfPCell(new Phrase("0.0",getFont2()));
		table1.addCell(ejAnt7);
		
		String xN ="TOTAL EMPLEO MEDIO";
		PdfPCell aN = new PdfPCell(new Phrase(xN,getFont1()));
		table1.addCell(aN);
		
		PdfPCell ejActN = new PdfPCell(new Phrase("0.0",getFont1()));
		table1.addCell(ejActN);
		
		PdfPCell ejAntN = new PdfPCell(new Phrase("0.0",getFont1()));
		table1.addCell(ejAntN);
		
		table1.addCell(emptyCell());
		
		PdfPCell c = new PdfPCell(table1);
		c.setColspan(2);
		table.addCell(c);
		
		return table;
	}
	
	private static PdfPCell emptyCell() {
		PdfPCell cell = new PdfPCell(new Phrase("",getFont2()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	private static Font getFont1(){
		Font font1 = new Font();
		font1.setSize(8);
		font1.setStyle(Font.BOLD);
		return font1;
	}
	
	private static Font getFont2(){
		Font font2 = new Font();
		font2.setSize(8);
		return font2;
	}
	
	private static Font getTitleFont(){
		Font font = new Font();
		font.setSize(16);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	private static Paragraph getSeparator(){
		Paragraph separator = new Paragraph();
		LineSeparator line = new LineSeparator();
        line.setOffset(-2);
        separator.add(line);
        return separator;
	}
}
