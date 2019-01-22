package com.code.aon.webservice.expedient;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONObject;

import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Expedient;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.ProjectProperties;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "PrintFullExpedient", urlPatterns = {"/aon_gwt_aio/print_full_expedient/*"})
public class printFullExpedient extends HttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(printFullExpedient.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Print Full Expedient - GET METHOD");
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));

		String domainName = parameters.get("domainName");
		Integer domainId = Integer.parseInt(parameters.get("domainId"));
		String login = parameters.get("login");
		JSONObject filter = new JSONObject(parameters.get("filter"));
		Stream<Expedient> exStream = AON.getFullExpedientStream(domainName, domainId, login, f -> projectFilter(domainId, filter, f));

		byte[] data = createExcel(exStream, filter.opt("id") != null);
		giveBackData(resp, data, "expediente.xls");
	}
	
	  private Filter projectFilter(Integer domain, JSONObject filterJson, ProjectProperties f) {
		Filter filter = f.getDomainProperty().eq(domain);
		
		if(filterJson.opt("id") != null) {
			filter = filter.and(f.getIdProperty().eq(filterJson.getInt("id")));
		}
		
		if(filterJson.opt("name") != null) {
			filter = filter.and(f.getNameProperty().like("%" + filterJson.getString("name") + "%"));
		}		
		
		if(filterJson.opt("alias") != null) {
			filter = filter.and(f.getAliasProperty().like("%" + filterJson.getString("alias") + "%"));
		}
		
		if(filterJson.opt("registry") != null) {
			filter = filter.and(f.getRegistryProperty().eq(filterJson.getInt("registry")));
		}
		
		if(filterJson.opt("type") != null) {
			filter = filter.and(f.getProjectTypeProperty().eq(filterJson.getInt("type")));
		}	
		
		if(filterJson.opt("active") != null) {
			filter = filter.and(f.getActiveProperty().eq(filterJson.getString("active").equals("true")? (byte) 1 : (byte) 0));
		}
		
		if(filterJson.opt("from") != null) {
			filter = filter.and(f.getDateProperty().ge(AonDateUtils.toSql(AonDateUtils.simpleParse(filterJson.getString("from")))));
		}
		
		if(filterJson.opt("to") != null) {
			filter = filter.and(f.getDateProperty().le(AonDateUtils.toSql(AonDateUtils.simpleParse(filterJson.getString("to")))));
		}

		return filter;
	}	
	
	  public static void giveBackData(HttpServletResponse resp, byte[] data, String name) throws ServletException, IOException{
		Integer length = data.length;
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
	        
		resp.addHeader("Content-Disposition","attachment; filename=\""+name +"\"");
		resp.setContentType("application/msexcel");
		
		if (length > 0 && length <= Integer.MAX_VALUE)
        	resp.setContentLength((int)length);
        ServletOutputStream out = resp.getOutputStream();
        resp.setBufferSize(32768);
        int bufSize = resp.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
        	out.write(buffer, 0, bytes);
    
        bis.close();
        bais.close();
        out.flush();
        out.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}
	
	
	Integer cont = 0;
	public byte[] createExcel(Stream<Expedient> exStream, Boolean onlyOne) {
		byte[] data = null; 
		try {
			ByteArrayOutputStream archivo = new ByteArrayOutputStream();
			HSSFWorkbook libro = new HSSFWorkbook();		
			
			
			CellStyle style = getStyle(libro);
			CellStyle style3 = getStyle3(libro);
			
			HSSFSheet hoja = libro.createSheet("Expendientes");
			
			Row fila = hoja.createRow(onlyOne ? 1 : 0);
			Integer column = onlyOne ? -2 : 0;
			if(!onlyOne) {
				boldCell(libro, fila, style, 0, "Nombre");
				boldCell(libro, fila, style, 1, "Alias");
			}
			boldCell(libro, fila, style, column + 2, "Año");
			boldCell(libro, fila, style, column + 3, "Tipo");
			boldCell(libro, fila, style, column + 4, "Documento");
			boldCell(libro, fila, style, column + 5, "Fecha");
			boldCell(libro, fila, style, column + 6, "Base Imponible");
			boldCell(libro, fila, style, column + 7, "Concepto");

			cont = onlyOne ? 2 : 1;
			exStream.forEach(r -> {
				Row row = hoja.createRow(cont++);
				if(onlyOne) {
					Row f = hoja.createRow(0);
					boldCell(libro, f, style, 0, r.getName());
					boldCell(libro, f, style, 1, r.getAlias());
					boldCell(libro, f, style, 2, "");
					boldCell(libro, f, style, 3, "");
					boldCell(libro, f, style, 4, "");
					boldCell(libro, f, style, 5, "");
				} else {
					cell(libro, row, style3, 0, r.getName());
					cell(libro, row, style3, 1, r.getAlias());
				}
				cell(libro, row, style3, column + 2, r.getYear().toString());
				cell(libro, row, style3, column + 3, r.getType());
				cell(libro, row, style3, column + 4, r.getDocument());
				cell(libro, row, style3, column + 5, AonDateUtils.simpleFormat(r.getDate()));
				cell(libro, row, style3, column + 6, r.getBase());
				cell(libro, row, style3, column + 7, r.getConcept());
			});
			
			for(int i = 0; i < column + 8; i++) {
	            hoja.autoSizeColumn(i);
			}
			
			libro.write(archivo);

			data = archivo.toByteArray();
			archivo.close();
	        libro.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return data;
	}

	// -------------------- EXCEL UTILS
	
	private Cell boldCell(HSSFWorkbook libro, Row row, CellStyle style, Integer index, String str) {
		Cell cell = row.createCell(index);
		cell.setCellValue(str);
		row.setHeightInPoints(16);
		cell.setCellStyle(style);
	
		return cell;
	}
	
	private Cell cell(HSSFWorkbook libro, Row row, CellStyle style, Integer index, String str) {
		Cell cell = row.createCell(index);
		cell.setCellValue(str);
		row.setHeightInPoints(16);
		cell.setCellStyle(style);	
		return cell;
	}
	
	private Cell cell(HSSFWorkbook libro, Row row, CellStyle style, Integer index, Double dbl) {
		Cell cell = row.createCell(index);
		cell.setCellValue(dbl);
		row.setHeightInPoints(16);
		cell.setCellStyle(style);	
		return cell;
	}
	
	private CellStyle getStyle(HSSFWorkbook libro){		
		CellStyle style = libro.createCellStyle();
		
		HSSFFont font = libro.createFont();
		font.setFontHeightInPoints((short)10);
		font.setBold(true);
		
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.DARK_GREEN.getIndex());
		style.setLeftBorderColor(IndexedColors.DARK_GREEN.getIndex());
		style.setRightBorderColor(IndexedColors.DARK_GREEN.getIndex());
		style.setTopBorderColor(IndexedColors.DARK_GREEN.getIndex());
 
		style.setWrapText(true);
		return style;
	}
	
	private CellStyle getStyle3(HSSFWorkbook libro){	
		CellStyle style3 = libro.createCellStyle();
     	HSSFFont font2 = libro.createFont();
     	font2.setFontHeightInPoints((short)12);
		style3.setFont(font2);
		style3.setAlignment(HorizontalAlignment.LEFT);
		style3.setBorderBottom(BorderStyle.THIN);
		style3.setBorderRight(BorderStyle.THIN);
		style3.setBorderLeft(BorderStyle.THIN);
		style3.setBorderTop(BorderStyle.THIN);	
		return style3;
	}
}
