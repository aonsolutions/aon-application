package com.code.aon.ui.fiscal.controller;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.fiscal.invoice.InvoiceReportManager;
import com.code.aon.fiscal.invoice.InvoiceReportParams;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.report.ReportException;
import com.code.aon.ui.util.AonUtil;

public class InvoiceReportController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private InvoiceReportParams params;
	
	public InvoiceReportParams getParams() {
		if (params == null) {
			params = new InvoiceReportParams( DomainManager.getCurrentDomain() );
		}
		return params;
	}
	public void setParams(InvoiceReportParams params) {
		this.params = params;
	}
	
	public void onReset(ActionEvent event) {
		getParams().reset();
		getParams().setSecurityLevel(
				AonUtil.getRoleManager().isConfidentiality()
					?null
					:SecurityLevel.OFFICIAL);
	}
	
	public String onExcelReport() {
		Connection conn = null; 
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Locale locale = AonUtil.getCurrentLocale();
			InvoiceReportManager manager = new InvoiceReportManager();

			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "Listado Detallado";
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();
			
			manager.excelReport(conn, getParams(), locale, output);
			
			response.flushBuffer();
			faces.responseComplete();
			return null;
		} catch (ReportException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
		
	}
	
}
