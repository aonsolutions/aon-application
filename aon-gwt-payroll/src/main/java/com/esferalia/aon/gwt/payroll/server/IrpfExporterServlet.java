package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.report.StatelessReportManager;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfData;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfRegularization;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfResult;

@SuppressWarnings("serial")
@WebServlet(name = "IrpfExporterServlet", urlPatterns = { "/aon_gwt_payroll/irpf/*"})
public class IrpfExporterServlet extends HttpServlet {

	private static Map<String, OutputFormat> OUTPUT_FORMATS = new HashMap<String, OutputFormat>() {
		{
			put("pdf", OutputFormat.PDF);
		}
	};

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestURI = req.getRequestURI();
		String extension = AonServletUtils.getExtn(requestURI);
		String fileName = AonServletUtils.getFileName(requestURI);

		final Irpf irpf = getIrpf(fileName);

		try {
			
			AonServletUtils.initFacesContext(getServletContext(), req, resp);

			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getViewRoot().setLocale(new Locale("es", "ES"));

			ReportManager reportManager = new StatelessReportManager();
			OutputFormat outputFormat = getOutputFormat(extension);
			reportManager.setOutputFormat(outputFormat);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
						// TODO Auto-generated catch block
						return null;
					}
				}

				@Override
				public Collection getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(EmployeesServiceImpl
							.getIrpfOutcome(irpf));
				}

			};

			reportManager.setCollectionProvider(provider);

			resp.setContentType(MimeType.getByExtension(extension).getName());

			OutputStream os = resp.getOutputStream();
			reportManager.execute(os, "irpf");
			os.close();

		} catch (ReportException e) {
			throw new ServletException(e);
		} finally {
			AonServletUtils.releaseFacesContext();
		}

	}

	private static Irpf getIrpf(String fileName) {

		String fileNameParts[] = fileName.split("_");
		Integer irpfDataId = Integer.parseInt(fileNameParts[0]);
		Integer irpfResultId = Integer.parseInt(fileNameParts[1]);
		Integer irpfRegularizationId = fileNameParts.length > 2 ? Integer
				.parseInt(fileNameParts[2]) : null;

		Irpf irpf = new Irpf();
		IrpfData irpfData = new IrpfData();
		irpfData.setId(irpfDataId);
		irpf.setIrpfData(irpfData);
		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setId(irpfResultId);
		irpf.setIrpfResult(irpfResult);
		if (irpfRegularizationId != null) {
			IrpfRegularization irpfRegularization = new IrpfRegularization();
			irpfRegularization.setId(irpfRegularizationId);
			irpf.setIrpfRegularization(irpfRegularization);
		}
		return irpf;

	}

	private static OutputFormat getOutputFormat(String extension) {
		return OUTPUT_FORMATS.get(extension);
	}

}
