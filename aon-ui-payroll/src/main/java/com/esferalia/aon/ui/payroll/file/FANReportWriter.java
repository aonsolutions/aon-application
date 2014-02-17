package com.esferalia.aon.ui.payroll.file;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.Month;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.fan.data.ETI;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.LiquidationType;


public class FANReportWriter {
	
	private FANWriter writer;
	
	public void buildFANReport(List<EnterpriseCCC> list, LiquidationType liquidationType, Integer year, Month startMonth, Month endMonth ) throws ManagerBeanException {
		
		writer = new FANWriter();
		ETI eti = writer.createETIRecord( true, list, liquidationType, year, startMonth, endMonth );
		
	}
	
	
	public String onExcelReport() {
		Connection conn = null; 
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Locale locale = AonUtil.getCurrentLocale();
//			InvoiceReportManager manager = new InvoiceReportManager();

			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "Listado Detallado";
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();
			
//			manager.excelReport(conn, getParams(), locale, output);
			
			response.flushBuffer();
			faces.responseComplete();
			return null;
//		} catch (ReportException e) {
//			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
		
	}
	
	
}
