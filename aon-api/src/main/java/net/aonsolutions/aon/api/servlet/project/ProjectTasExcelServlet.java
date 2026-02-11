package net.aonsolutions.aon.api.servlet.project;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.project.ProjectTas;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiProjectTasExcelServlet", urlPatterns = {"/ms/api/projectTasExcel/*"})
public class ProjectTasExcelServlet extends AonApiHttpServlet{
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	private void get(HttpServletRequest request, HttpServletResponse response) {
		try {
			//Get Request Parametrers
			String domainName = request.getParameter("domainName");
			Integer domainId = Integer.parseInt(request.getParameter("domainId"));
			String login = request.getParameter("login");
			
			Occam occam = new Occam().setDomain(domainId).setDomainName(domainName).setUser(login);
			
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			
			Date start = AonDateUtils.simpleParse(startDate);
			Date end = AonDateUtils.simpleParse(endDate);
			
			List<ProjectTas> projectTas = AON.getProjectTasList(occam, 
					f -> f.getDomainProperty().eq(domainId)
						.and(f.getDateProperty().ge(AonDateUtils.toSql(start)))
						.and(f.getDateProperty().le(AonDateUtils.toSql(null == end ? new Date() : end)))
					);
			
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=\"Ordenes_Reparacion.xls\"");
			
			ServletOutputStream output = response.getOutputStream();
			
			setProjectTasExcel(output, projectTas);
			
			response.flushBuffer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private XSSFColor AON_BLUE = new XSSFColor(new java.awt.Color(0,114,207), new DefaultIndexedColorMap());
	private int rowCount = 0;
	private int cellCount = 0;
	private Font headerFont;
	private SXSSFWorkbook wb;
	private SXSSFSheet sheet;
	private XSSFCellStyle headerCellStyle;
	private XSSFCellStyle rowCellStyle;
	private XSSFCellStyle rowCenterCellStyle;
	
	private void setProjectTasExcel(ServletOutputStream output, List<ProjectTas> projectTas) throws Exception {
	    wb = new SXSSFWorkbook(1);
	    sheet = (SXSSFSheet) wb.createSheet("Ordenes Reparacion");

	    headerFont = wb.createFont();
	    headerFont.setBold(true);
	    headerFont.setColor(IndexedColors.WHITE.index);

	    headerCellStyle = (XSSFCellStyle) wb.createCellStyle();
	    headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
	    headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerCellStyle.setFillForegroundColor(AON_BLUE);
	    headerCellStyle.setFont(headerFont);

	    rowCellStyle = (XSSFCellStyle) wb.createCellStyle();
	    rowCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

	    rowCenterCellStyle = (XSSFCellStyle) wb.createCellStyle();
	    rowCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    rowCenterCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

	    rowCount = 0;
	    cellCount = 0;

	    printProjectTasHeader();
	    fillProjectTasRows(projectTas);

	    wb.write(output);
	    output.close();
	    wb.close();
	}
	
	private static final XSSFColor AON_DARK_BLUE = new XSSFColor(new java.awt.Color(0,80,160), new DefaultIndexedColorMap());

	private void printProjectTasHeader() {
	    // ==== Estilo especial para la primera fila agrupada ====
	    XSSFCellStyle groupHeaderCellStyle = (XSSFCellStyle) wb.createCellStyle();
	    groupHeaderCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    groupHeaderCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    groupHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    groupHeaderCellStyle.setFillForegroundColor(AON_DARK_BLUE);
	    groupHeaderCellStyle.setBorderBottom(BorderStyle.NONE);
	    groupHeaderCellStyle.setBorderTop(BorderStyle.NONE);
	    groupHeaderCellStyle.setBorderLeft(BorderStyle.NONE);
	    groupHeaderCellStyle.setBorderRight(BorderStyle.NONE);

	    Font groupFont = wb.createFont();
	    groupFont.setBold(true);
	    groupFont.setColor(IndexedColors.WHITE.index);
	    groupFont.setFontHeightInPoints((short)12); // un poco más grande
	    groupHeaderCellStyle.setFont(groupFont);

	    // ===== Primera fila: agrupaciones =====
	    SXSSFRow groupedRow = sheet.createRow(rowCount);
	    groupedRow.setHeightInPoints(28); // más alto

	    SXSSFCell ordCell = groupedRow.createCell(0);
	    ordCell.setCellValue("Ordenes de reparación");
	    ordCell.setCellStyle(groupHeaderCellStyle);

	    SXSSFCell vehCell = groupedRow.createCell(9);
	    vehCell.setCellValue("Vehículos");
	    vehCell.setCellStyle(groupHeaderCellStyle);

	    // Fusionar celdas correctamente
	    sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 8));   // cols 0..8
	    sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 9, 13));  // cols 9..13

	    rowCount++;
	    cellCount = 0;

	    // ===== Segunda fila: cabeceras individuales =====
	    SXSSFRow row = sheet.createRow(rowCount);
	    row.setHeightInPoints(20);

	    CellUtil.createCell(row, cellCount++, "Serie/numero de resguardo", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Matricula", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Marca", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Modelo", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Numero de cliente", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "DNI", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Nombre cliente", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Cuentakilometros", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Descripcion", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Matricula", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Marca", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Modelo", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Bastidor", headerCellStyle);
	    CellUtil.createCell(row, cellCount++, "Descripcion", headerCellStyle);

	    // Anchos (igual que antes)
	    sheet.setColumnWidth(0, 22 * 256);  // Serie
	    sheet.setColumnWidth(1, 15 * 256);  // Matricula
	    sheet.setColumnWidth(2, 15 * 256);  // Marca
	    sheet.setColumnWidth(3, 18 * 256);  // Modelo
	    sheet.setColumnWidth(4, 18 * 256);  // Numero cliente
	    sheet.setColumnWidth(5, 15 * 256);  // DNI
	    sheet.setColumnWidth(6, 40 * 256);  // Nombre cliente
	    sheet.setColumnWidth(7, 15 * 256);  // Cuentakilometros
	    sheet.setColumnWidth(8, 60 * 256);  // Descripcion
	    sheet.setColumnWidth(9, 15 * 256);  // Matricula 2
	    sheet.setColumnWidth(10, 15 * 256); // Marca 2
	    sheet.setColumnWidth(11, 18 * 256); // Modelo 2
	    sheet.setColumnWidth(12, 30 * 256); // Bastidor
	    sheet.setColumnWidth(13, 60 * 256); // Descripcion 2
	}

	private void fillProjectTasRows(List<ProjectTas> projectTas) {
	    rowCount++;
	    cellCount = 0;
	    
	    projectTas.sort(
	    	    Comparator.comparing(ProjectTas::getSeries)
	    	              .thenComparing(ProjectTas::getNumber).reversed()
	    	);

	    projectTas.forEach(pt -> {
	        SXSSFRow row = sheet.createRow(rowCount);
	        row.setHeightInPoints(18); // un poco más alto

	        // Centramos los códigos, matriculas, serie, dni, bastidor...
	        CellUtil.createCell(row, cellCount++, pt.getSeries() + "/" + AonStringUtils.leftPad(pt.getNumber().toString(), 6, "0"), rowCenterCellStyle);
	        CellUtil.createCell(row, cellCount++, pt.getTasItem().getPublicCode(), rowCellStyle);
	        CellUtil.createCell(row, cellCount++, pt.getTasItem().getMakeName(), rowCellStyle);
	        CellUtil.createCell(row, cellCount++, pt.getTasItem().getModelName(), rowCellStyle);

	        CellUtil.createCell(row, cellCount++, pt.getTarget().getId().toString(), rowCellStyle);
	        CellUtil.createCell(row, cellCount++, pt.getTarget().getDocument(), rowCellStyle);
	        CellUtil.createCell(row, cellCount++, pt.getTarget().getName(), rowCellStyle);

	        CellUtil.createCell(row, cellCount++, pt.getCounter().toString(), rowCellStyle);
	        CellUtil.createCell(row, cellCount++, pt.getComments(), rowCellStyle);

	        CellUtil.createCell(row, cellCount++, pt.getTasItem().getPublicCode(), rowCellStyle);
	        CellUtil.createCell(row, cellCount++, pt.getTasItem().getMakeName(), rowCellStyle);
	        CellUtil.createCell(row, cellCount++, pt.getTasItem().getModelName(), rowCellStyle);
	        CellUtil.createCell(row, cellCount++, pt.getTasItem().getPrivateCode(), rowCellStyle);
	        CellUtil.createCell(row, cellCount++, pt.getTasItem().getDescription(), rowCellStyle);

	        rowCount++;
	        cellCount = 0;
	    });
	}

	
}
