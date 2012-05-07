package com.code.aon.ui.report.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.faces.event.AbortProcessingException;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.report.dynamic.DynaElements;
import com.code.aon.report.dynamic.DynaReport;
import com.code.aon.report.jr.JRBeanCollectionDataSource;
import com.code.aon.report.jr.exporter.IJRExporterFactory;
import com.code.aon.report.jr.exporter.JRExporterFactoryManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class DynaReportManager {

	public void toExcel(DynaReport dynaReport,String filename, Collection<?> c) {
		HttpServletResponse response = null;
		OutputStream out = null;
		try {
			response = DownloadUtil.getResponse();
			out = DownloadUtil.initDownload(response, filename, MimeType.MIME_MS_EXCEL);
			dynaReport.getReport().setPrintColumnNames(true)
				.setIgnorePagination(true)
				.setMargins(0, 0, 0, 0);
			DynamicReport dr = dynaReport.getReport().build();
			dr.setWhenNoDataStyle(DynaElements.DETAIL_STYLE);
			JRDataSource ds = new JRBeanCollectionDataSource(c);
			JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
			IJRExporterFactory fm = JRExporterFactoryManager.getJRExporterFactory(OutputFormat.XLS); 
			JRExporter exporter = fm.getJRExporter();
			fm.fillJRParametersMap(null,exporter.getParameters());
		    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out); 
			exporter.exportReport();
		} catch (IOException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (JRException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (ReportException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DownloadUtil.finishDownload(response, out);
		}
	}
	
	
	public void toJRXML(DynaReport dynaReport, String filePath) {
		try {
			dynaReport.getReport().setPrintColumnNames(true)
			.setIgnorePagination(true)
			.setMargins(0, 0, 0, 0);
			DynamicReport dr = dynaReport.getReport().build();
			DynamicJasperHelper.generateJRXML(dr, new ClassicLayoutManager(), null, "UTF-8", filePath);
		} catch (JRException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

}
