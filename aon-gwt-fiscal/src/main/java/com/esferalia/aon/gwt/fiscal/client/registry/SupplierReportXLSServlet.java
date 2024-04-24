package com.esferalia.aon.gwt.fiscal.client.registry;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.registry.report.SupplierReportXLS;

@WebServlet(name = "Supplier Report XLS Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/SupplierReportXLSServlet" })
public class SupplierReportXLSServlet extends HttpServlet {

	private static final long serialVersionUID = 6152551748473230508L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			SupplierReportXLS report = new SupplierReportXLS();
			report.printReport("Proveedores");
			OutputStream out = resp.getOutputStream();
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition",
					"attachment; filename=\"Proveedores." + MimeType.MS_EXCEL_2007.getExtension() + "\";");
			Occam occam = new Occam().setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME))
					.setDomain(AonNumberUtils.toint(req.getParameter(IRequestParamsNames.DOMAIN_ID)))
					.setUser(req.getParameter(IRequestParamsNames.USER));
			RegistryParams params = JsonParser
					.parseRegistryParams(req.getParameter(IRequestParamsNames.REGISTRY_PARAMS));

			Stream<SupplierFull> stream = AON.getSuppliers(occam, params, 0, Integer.MAX_VALUE)
					.map(c -> AON.getSupplierFull(occam, c.getId()));
			stream.forEach(report);
			report.finalize(out);
			resp.flushBuffer();
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
