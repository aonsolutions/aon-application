package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqActivitySummary;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;

import net.aonsolutions.core.pool.AonConnectionException;

@SuppressWarnings("serial")
@WebServlet(name = "ActivitySummaryExporter", urlPatterns = { "/aon_gwt_payroll/download_activitySummary/*" })
public class ActivitySummaryExporterServlet extends HttpServlet {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ActivitySummaryExporterServlet.class);

	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String _domainId = request.getParameter("domainId");
		String _parentDomainId = request.getParameter("parentDomainId");
		String _startDate = request.getParameter("startDate");
		String _endDate = request.getParameter("endDate");
		String _starts = request.getParameter("starts");
		String _ends = request.getParameter("ends");
		String _salary = request.getParameter("salary");
		String _salaryExtra = request.getParameter("salaryExtra");
		String _salarySettle = request.getParameter("salarySettle");
		String _salaryOther = request.getParameter("salaryOther");
		String _itCommonDisease = request.getParameter("itCommonDisease");
		String _itOccupationalDisease = request
				.getParameter("itOccupationalDisease");
		String _itMaternity = request.getParameter("itMaternity");
		String _itOther = request.getParameter("itOther");

		Date startDate = null;
		Date endDate = null;
		if (NumberUtils.isNumber(_startDate)) {
			startDate = new Date(Long.parseLong(_startDate));
		}
		if (NumberUtils.isNumber(_endDate)) {
			endDate = new Date(Long.parseLong(_endDate));
		}

		

		try {
			String domainName = AonServletUtils.getDomainName(_domainId);
			
			boolean isParentDomain = !NumberUtils.isNumber(_parentDomainId);

			List<ActivitySummaryObject> list = JooqActivitySummary
					.getActivitySummary(domainName, isParentDomain, NumberUtils
							.toInt(_domainId), startDate, endDate, new Boolean(
							_starts), new Boolean(_ends), new Boolean(_salary),
							new Boolean(_salaryExtra), new Boolean(_salarySettle),
							new Boolean(_salaryOther),
							new Boolean(_itCommonDisease), new Boolean(
									_itOccupationalDisease), new Boolean(
									_itMaternity), new Boolean(_itOther));

			String fileName = "resumen_actividad";
			dateFormatter.applyPattern("yyyy/MM/dd");
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".xls\"");
			ServletOutputStream output = response.getOutputStream();

			if (!excelReport(output, list, isParentDomain)) {
				AonUtil.addErrorMessage("No existen datos para generar el informe.");
			}

			response.flushBuffer();
		} catch (SQLException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ReportException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	// *********************************************
	// EXCEL REPORT
	// *********************************************

	private boolean excelReport(OutputStream output,
			List<ActivitySummaryObject> list, boolean isParent)
			throws IOException, ReportException, AonConnectionException {
		ExcelReportExporter exporter = new ExcelReportExporter();

		exporter.startExport("Informe");
		ReportMetadata metadata = getContractColumnMetadata();
		exporter.exportHeader(metadata);

		list.forEach(o -> {
			exportRow(metadata, exporter, o, isParent);
		});

		exporter.endExport(output);
		output.flush();
		return true;
	}

	private void exportRow(ReportMetadata metadata,
			ExcelReportExporter exporter, ActivitySummaryObject o,
			boolean isParent) {
		int column = 0;
		exporter.startLine();
		try {
			// NAME
			exporter.exportColumn(metadata.getColumns().get(column++),
					o.getFullname());

			// CONTRACT
			if (isParent) {
				exporter.exportColumn(metadata.getColumns().get(column++),
						o.getStartCount()!=null?String.valueOf(o.getStartCount()):0);
				exporter.exportColumn(metadata.getColumns().get(column++),
						o.getEndCount()!=null?String.valueOf(o.getEndCount()):0);
			} else {
				exporter.exportColumn(
						metadata.getColumns().get(column++),
						o.getStartDate() != null ? dateFormatter.format(o
								.getStartDate()) : "");
				exporter.exportColumn(
						metadata.getColumns().get(column++),
						o.getEndDate() != null ? dateFormatter.format(o
								.getEndDate()) : "");
			}

			// SALARY
			exporter.exportColumn(metadata.getColumns().get(column++),
					o.getSalaryCount()!=null?o.getSalaryCount():0);
			exporter.exportColumn(metadata.getColumns().get(column++),
					o.getSalaryExtraCount()!=null?o.getSalaryExtraCount():0);
			exporter.exportColumn(metadata.getColumns().get(column++),
					o.getSalarySettleCount()!=null?o.getSalarySettleCount():0);
			exporter.exportColumn(metadata.getColumns().get(column++),
					o.getSalaryOtherCount()!=null?o.getSalaryOtherCount():0);

			// IT
			exporter.exportColumn(metadata.getColumns().get(column++),
					o.getItCommonDiseaseCount()!=null?o.getItCommonDiseaseCount():0);
			exporter.exportColumn(metadata.getColumns().get(column++),
					o.getItOccupationalDiseaseCount()!=null?o.getItOccupationalDiseaseCount():0);
			exporter.exportColumn(metadata.getColumns().get(column++),
					o.getItMaternityCount()!=null?o.getItMaternityCount():0);
			exporter.exportColumn(metadata.getColumns().get(column++),
					o.getItOtherCount()!=null?o.getItOtherCount():0);
		} catch (ReportException e) {
			LOGGER.error("RESUMEN DE ACTIVIDAD LABORAL: No se ha podido completar la fila del informe.");
		} finally {
			exporter.endLine();
		}
	}

	private ReportMetadata getContractColumnMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(
				new ReportColumnMetadata("NAME", Types.VARCHAR, "Nombre", 30));
		metadata.getColumns().add(
				new ReportColumnMetadata("START", Types.VARCHAR,
						"Inicio contrato", 10));
		metadata.getColumns().add(
				new ReportColumnMetadata("END", Types.VARCHAR, "Fin contrato",
						10));
		metadata.getColumns()
				.add(new ReportColumnMetadata("SALARY", Types.INTEGER,
						"Nominas", 10));
		metadata.getColumns().add(
				new ReportColumnMetadata("SALARY_EXTRA", Types.INTEGER,
						"Extras", 10));
		metadata.getColumns().add(
				new ReportColumnMetadata("SALARY_SETTLE", Types.INTEGER,
						"Finiquitos", 10));
		metadata.getColumns().add(
				new ReportColumnMetadata("SALARY_OTHER", Types.INTEGER,
						"Otros", 10));
		metadata.getColumns().add(
				new ReportColumnMetadata("IT_EC_AN", Types.INTEGER, "IT EC/AN",
						10));
		metadata.getColumns().add(
				new ReportColumnMetadata("IT_AT_EP", Types.INTEGER, "IT AT/EP",
						10));
		metadata.getColumns()
				.add(new ReportColumnMetadata("IT_M_P", Types.INTEGER,
						"IT M/P", 10));
		metadata.getColumns().add(
				new ReportColumnMetadata("IT_OTHER", Types.INTEGER, "IT Otros",
						10));
		return metadata;
	}

}
