package net.aonsolutions.aon.api.servlet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CustomerBookingResumeExcelServlet", urlPatterns = { "/ms/api/customers-booking-resume-excel/*"})
public class CustomerBookingResumeExcelServlet extends AonApiHttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
		
		try {
			//Get Request Parametrers
			String domainName = request.getParameter("domainName");
			Integer domainId = Integer.parseInt(request.getParameter("domainId"));
			String login = request.getParameter("login");
			
			Domain domain = AON.getDomain(domainName, domainId, login);
			User user = AON.getUser(domainName, domainId, login);
			
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=\"ResumenContratacion.xls\"");
			
			ServletOutputStream output = response.getOutputStream();
			
			Booking booking = AON.getBooking(domain, user);
			
			setBookingExcel(output, booking);
			
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
	
	private void setBookingExcel(ServletOutputStream output, Booking booking) throws Exception {
		wb = new SXSSFWorkbook(1);
		sheet = (SXSSFSheet) wb.createSheet("Resumen Contrataci\u00f3n");
		
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
	    
		printCustomerHeader();
		fillCustomerRow(booking);
		
		if(null != booking.getResume() && null != booking.getResume().getChilds() && !booking.getResume().getChilds().isEmpty()) {
			printChildHeader();
			fillChildRow(booking);
		}
		
		wb.write(output);
		output.close();
		wb.close();
	}

	private void printCustomerHeader() {
		SXSSFRow row = sheet.createRow(rowCount);
		
		CellUtil.createCell(row, cellCount++, "Dominio", headerCellStyle);		
		CellUtil.createCell(row, cellCount++, "Extensiones contratadas", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Empresas", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Numero Ext.", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Estado", headerCellStyle);  
		CellUtil.createCell(row, cellCount++, "F. Expiraci\u00f3n", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Usr.", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Usr Portal.", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Tipo", headerCellStyle);
		
		// Definir ancho para las distintas columnas
		
		sheet.setColumnWidth(0, 80 * 256);  // Ancho para la columna Dominio
		sheet.setColumnWidth(1, 120 * 256);  // Ancho para la columna Extensiones contratadas
		sheet.setColumnWidth(2, 120 * 256);  // Ancho para la columna Empresas
		sheet.setColumnWidth(3, 12 * 256);  // Ancho para la columna Numero Ext.
		sheet.setColumnWidth(4, 12 * 256);  // Ancho para la columna Estado
		sheet.setColumnWidth(5, 15 * 256);  // Ancho para la columna F. Expiraci\u00f3n
		sheet.setColumnWidth(6, 12 * 256);  // Ancho para la columna Usr.
		sheet.setColumnWidth(7, 13 * 256);  // Ancho para la columna Usr. Portal
		sheet.setColumnWidth(8, 15 * 256);  // Ancho para la columna Tipo
	}
	
	private void fillCustomerRow(Booking booking) {
		rowCount++;
		cellCount = 0;
		
		SXSSFRow row = sheet.createRow(rowCount);
		
		List<String> apps = booking.getApps().stream().map(aonApp -> aonApp.getDescription()).collect(Collectors.toList());
		apps.sort((o1, o2) -> o1.compareTo(o2));
		
		List<Domain> activeChilds = null == booking.getResume() || null == booking.getResume().getChilds() ? new ArrayList<Domain>() : booking.getResume().getChilds().stream().filter(domain -> domain.isActive()).collect(Collectors.toList());
		
		CellUtil.createCell(row, cellCount++, booking.getDomain().getDescription(), rowCellStyle);		
		CellUtil.createCell(row, cellCount++, String.join(", ", apps), rowCellStyle);
		CellUtil.createCell(row, cellCount++, activeChilds.size() + " / " + (null == booking.getResume() || null == booking.getResume().getTotalChilds() ? "0" : booking.getResume().getTotalChilds()), rowCenterCellStyle);
		CellUtil.createCell(row, cellCount++, booking.getApps().size() + "", rowCenterCellStyle);
		CellUtil.createCell(row, cellCount++, booking.getDomain().getExpirationDate() != null && booking.getDomain().getExpirationDate().before(new Date()) ? "Expirado" : (booking.getDomain().isActive() ? "Activo" : "Inactivo"), rowCenterCellStyle);  
		CellUtil.createCell(row, cellCount++, formatDate(booking.getDomain().getExpirationDate()), rowCenterCellStyle);
		CellUtil.createCell(row, cellCount++, booking.getNumberOfUsers() + " / " + booking.getDomain().getMaxDefinedUsers(), rowCenterCellStyle);
		CellUtil.createCell(row, cellCount++, null == booking.getDomain().getUsers() ? "0" : ((int) booking.getDomain().getUsers().stream().filter(user -> user.isActive() && user.isPortal()).count() + ""), rowCenterCellStyle);
		CellUtil.createCell(row, cellCount++, booking.getType().getName(), rowCenterCellStyle);
	}
	
	private void printChildHeader() {
		rowCount++;
		rowCount++;
		cellCount = 0;
		
		SXSSFRow row = sheet.createRow(rowCount);
		
		CellUtil.createCell(row, cellCount++, "Empresa", headerCellStyle);		
		CellUtil.createCell(row, cellCount++, "Extensiones contratadas", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Extensiones heredadas", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Numero Ext.", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Estado", headerCellStyle);  
		CellUtil.createCell(row, cellCount++, "F. Expiraci\u00f3n", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Usr.", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Usr Portal.", headerCellStyle);
		CellUtil.createCell(row, cellCount++, "Tipo", headerCellStyle);
	}
	
	private void fillChildRow(Booking booking) {
		rowCount++;
		cellCount = 0;
		
		List<String> parentApps = booking.getApps().stream().filter(aonApp -> AonStringUtils.isNotBlank(aonApp.getDescription())).map(aonApp -> aonApp.getDescription()).collect(Collectors.toList());
		
		List<Domain> childsDomain = booking.getResume().getChilds().stream().filter(child -> AonStringUtils.isNotBlank(child.getDescription())).collect(Collectors.toList());
		childsDomain.sort((o1, o2) -> {
			  // Sort by type, prioritizing type 4
			  if (o1.getDomainType().ordinal() == 6 && o2.getDomainType().ordinal() != 6) return -1;
			  if (o1.getDomainType().ordinal() != 6 && o2.getDomainType().ordinal() == 6) return 1;
			  if (o1.getDomainType().ordinal() < o2.getDomainType().ordinal()) return -1;
			  if (o1.getDomainType().ordinal() > o2.getDomainType().ordinal()) return 1;

			  // If types are equal, sort by name
			  return o1.getDescription().compareTo(o2.getDescription());
		});
		
		for(Domain domainChild : childsDomain) {

			SXSSFRow row = sheet.createRow(rowCount);
			
			
			// Get child and parent diff apps
			List<String> childApps = domainChild.getApps().stream().filter(domainApp -> null != domainApp.getApp() && AonStringUtils.isNotBlank(domainApp.getApp().getDescription())).map(domainApp -> domainApp.getApp().getDescription()).collect(Collectors.toList());
			List<String> parentAppsDiff = childApps.stream().filter(app -> parentApps.contains(app)).collect(Collectors.toList());
			parentAppsDiff.sort((o1, o2) -> o1.compareTo(o2));
			
			List<String> childAppsDiff = childApps.stream().filter(app -> !parentApps.contains(app)).collect(Collectors.toList());
			childAppsDiff.sort((o1, o2) -> o1.compareTo(o2));
			
			Integer portalUsersCount = null == domainChild.getUsers() ? 0 : (int) domainChild.getUsers().stream().filter(user -> user.isActive() && user.isPortal()).count();			
			List<User> activeUsers = domainChild.getUsers().stream().filter(user -> user.isActive()).collect(Collectors.toList());
			Integer activeUsersDiff = null == activeUsers ? 0 : (activeUsers.size() - portalUsersCount);

		
			CellUtil.createCell(row, cellCount++, domainChild.getDescription(), rowCellStyle);		
			CellUtil.createCell(row, cellCount++, (childAppsDiff.size() == 0 ? "Sin contrataciones" : String.join(", ", childAppsDiff)), rowCellStyle);
			CellUtil.createCell(row, cellCount++, (parentAppsDiff.size() == 0 ? "Sin extensiones heredadas" : String.join(", ", parentAppsDiff)), rowCellStyle);
			CellUtil.createCell(row, cellCount++, childAppsDiff.size() + " / " + parentAppsDiff.size() + " (" +  childApps.size() + ")", rowCenterCellStyle);
			CellUtil.createCell(row, cellCount++, domainChild.getExpirationDate() != null && domainChild.getExpirationDate().before(new Date()) ? "Expirado" : (domainChild.isActive() ? "Activo" : "Inactivo"), rowCenterCellStyle);  
			CellUtil.createCell(row, cellCount++, formatDate(domainChild.getExpirationDate()), rowCenterCellStyle);
			CellUtil.createCell(row, cellCount++, activeUsersDiff + " / " + domainChild.getMaxDefinedUsers(), rowCenterCellStyle);
			CellUtil.createCell(row, cellCount++, portalUsersCount + "", rowCenterCellStyle);
			CellUtil.createCell(row, cellCount++, null == domainChild.getDomainType() ? "" : domainChild.getDomainType().getName(), rowCenterCellStyle);
			
			rowCount++;
			cellCount = 0;
		}
	}

	private String formatDate(Date date) {
		if(null == date) return "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return simpleDateFormat.format(date);
	}
	
}
