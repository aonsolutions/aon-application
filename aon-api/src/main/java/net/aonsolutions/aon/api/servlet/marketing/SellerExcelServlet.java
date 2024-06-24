package net.aonsolutions.aon.api.servlet.marketing;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.SellerParams;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "SellerExcelServlet", urlPatterns = { "/ms/api/seller-excel/*"})
public class SellerExcelServlet extends AonApiHttpServlet {
	
	private String domainName;
	private Integer domainId;
	private String login;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
		
		try {
			//Get Request Parametrers
			domainName = request.getParameter("domainName");
			domainId = Integer.parseInt(request.getParameter("domainId"));
			login = request.getParameter("login");
			
			SellerParams params = new SellerParams()
					.setDomainName(domainName)
					.setDomain(domainId)
					.setUser(login)
					.setDescription(request.getParameter("description"))
					.setScope(AonStringUtils.isBlank(request.getParameter("scope")) ? null : Integer.parseInt(request.getParameter("scope")))
					.setActive(AonStringUtils.isBlank(request.getParameter("active")) ? null : Byte.parseByte(request.getParameter("active")))
					.setOrderBy(request.getParameter("orderBy"))
					.setAsc(Boolean.parseBoolean(request.getParameter("asc")))
					.setLimit(Integer.MAX_VALUE)
					;
			
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=\"AgentesComerciales.xls\"");
			
			ServletOutputStream output = response.getOutputStream();
			
			List<Seller> sellers = AON.getSellerList(params);
			
			setSellerExcel(output, sellers);
			
			response.flushBuffer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private XSSFColor AON_BLUE = new XSSFColor(new java.awt.Color(0,114,207));
	private int rowCount = 0;
	private int cellCount = 0;
	private Font headerFont;
	private SXSSFWorkbook wb;
	private SXSSFSheet sheet;
	private XSSFCellStyle headerCellStyle;
	private XSSFCellStyle rowCellStyle;
	private XSSFCellStyle rowCenterCellStyle;
	
	private void setSellerExcel(ServletOutputStream output, List<Seller> sellers) throws Exception {
		wb = new SXSSFWorkbook(1);
		sheet = (SXSSFSheet) wb.createSheet("Agentes Comerciales");
		
		headerFont = wb.createFont();
		headerFont.setBold(true);
		headerFont.setColor( IndexedColors.WHITE.index );
		
		headerCellStyle = (XSSFCellStyle) wb.createCellStyle();
		headerCellStyle.setAlignment( HorizontalAlignment.CENTER );
		headerCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
	    headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerCellStyle.setFillForegroundColor(AON_BLUE);
	    headerCellStyle.setFont(headerFont);
	    
	    rowCellStyle = (XSSFCellStyle) wb.createCellStyle();
	    rowCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    
	    rowCenterCellStyle = (XSSFCellStyle) wb.createCellStyle();
	    rowCenterCellStyle.setAlignment( HorizontalAlignment.CENTER );
	    rowCenterCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
		
	    rowCount = 0;
		cellCount = 0;
	    
		printSellerHeader();
		fillSellerRows(sellers);
		
		wb.write(output);
		output.close();
		wb.close();
	}

	private void printSellerHeader() {
		SXSSFRow row = sheet.createRow(rowCount);
		
		CellUtil.createCell(row, cellCount++, "Nombre", headerCellStyle);		
		CellUtil.createCell(row, cellCount++, "Alias", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Documento", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Ambito", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Estado", headerCellStyle); 
		CellUtil.createCell(row, cellCount++, "Tipo Comision", headerCellStyle); 
		CellUtil.createCell(row, cellCount++, "Operario", headerCellStyle); 
		CellUtil.createCell(row, cellCount++, "Grupo Tabrajo", headerCellStyle); 
		
		// Definir ancho para las distintas columnas
		
		sheet.setColumnWidth(0, 40 * 256);  // Ancho para la columna Nombre
		sheet.setColumnWidth(1, 30 * 256);  // Ancho para la columna Alias
		sheet.setColumnWidth(2, 15 * 256);  // Ancho para la columna Documento
		sheet.setColumnWidth(3, 20 * 256);  // Ancho para la columna Ambito
		sheet.setColumnWidth(4, 12 * 256);  // Ancho para la columna Estado
		sheet.setColumnWidth(5, 20 * 256);  // Ancho para la columna Tipo Comision
		sheet.setColumnWidth(6, 20 * 256);  // Ancho para la columna Operario
		sheet.setColumnWidth(7, 50 * 256);  // Ancho para la columna Grupo Tabrajo
	}
	
	private void fillSellerRows(List<Seller> sellers) {
		rowCount++;
		cellCount = 0;
		
		for(Seller seller : sellers) {
			
			List<Workgroup> workgroups = AON.getTaskHolderWorkgroupStream(domainName, domainId, login, f -> f.getTaskHolderProperty().eq(seller.getTaskHolder().getRegistry())).collect(Collectors.toList());
			
			SXSSFRow row = sheet.createRow(rowCount);
			
			CellUtil.createCell(row, cellCount++, seller.getName(), rowCellStyle);		
			CellUtil.createCell(row, cellCount++, seller.getAlias(), rowCellStyle);
			CellUtil.createCell(row, cellCount++, seller.getDocument(), rowCellStyle);
			CellUtil.createCell(row, cellCount++, seller.getScope() == null ? null : seller.getScope().getDescription(), rowCellStyle);
			CellUtil.createCell(row, cellCount++, seller.isActive() ? "Activo" : "Inactivo", rowCenterCellStyle);
			
			CellUtil.createCell(row, cellCount++, seller.getCommissionType() == null ? null :  seller.getCommissionType().getName(), rowCellStyle);
			CellUtil.createCell(row, cellCount++, seller.getTaskHolder() == null ? null :  seller.getTaskHolder().getName(), rowCellStyle);
			CellUtil.createCell(row, cellCount++, null == workgroups || workgroups.isEmpty() ? null :  String.join(", ", workgroups.stream().map(workgroupIt -> workgroupIt.getDescription()).collect(Collectors.toList())), rowCellStyle);
			
			rowCount++;
			cellCount = 0;
		}
	}
	
}
