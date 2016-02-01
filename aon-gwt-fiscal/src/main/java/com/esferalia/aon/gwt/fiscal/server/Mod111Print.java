package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model110Bizkaia;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model110Gipuzkoa;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111AEAT;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Araba;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Araba2016;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Base.IModelScript;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Bizkaia;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Gipuzkoa;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model715Navarra;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model745Navarra;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod111 Print", urlPatterns = { "/aon_gwt_fiscal/Model111Print" })
public class Mod111Print extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod111"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = AonServletUtils.getLoggedUser();
			Mod111 mod111 = AON.getMod111(domainName, domainId, user,id);

			FiscalModelExcelAction action = new FiscalModelExcelAction(mod111);
			action.initialize(mod111.getModel().getName(mod111.getAdministration(), mod111.getPeriod()));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			for (IModelScript ms : obtainScript(mod111)) {
				action.accept(ms);
			}
			action.finalize(output);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
			String s = mod111.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

			String fileName = "Mod"
					+ mod111.getModel().getName(mod111.getAdministration(),mod111.getPeriod() )
					+ "_" + mod111.getYear() 
					+ "_" + mod111.getPeriod().getName()
					+ "_" + mod111.getAdministration().toString()
					+ "_" + sb.toString()
					+ ".xlsx";
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xslx\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	private IModelScript[] obtainScript(Mod111 mod111) {
		IModelScript[] ms = null;
		if (mod111.getAdministration() == Administration.COMMON_TERRITORY) {
			ms = Model111AEAT.ModelScript.values();
		} else if (mod111.getAdministration() == Administration.GIPUZKOA) {
			if (mod111.getPeriod().isQuarterPeriod()) {
				ms = Model110Gipuzkoa.ModelScript.values();
			} else {
				ms = Model111Gipuzkoa.ModelScript.values();
			}
		} else if (mod111.getAdministration() == Administration.BIZKAIA) {
			if (mod111.getPeriod().isQuarterPeriod()) {
				ms = Model110Bizkaia.ModelScript.values();
			} else {
				ms = Model111Bizkaia.ModelScript.values();
			}
		} else if (mod111.getAdministration() == Administration.NAVARRA) {
			if (mod111.getPeriod().isQuarterPeriod()) {
				ms = Model715Navarra.ModelScript.values();
			} else {
				ms = Model745Navarra.ModelScript.values();
			}
		} else if (mod111.getAdministration() == Administration.ALAVA) {
			if (mod111.getYear() > 2015) {
				ms = Model111Araba2016.ModelScript.values();
			} else {
				ms = Model111Araba.ModelScript.values();
			}
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod111.getAdministration().toString() + " "
							+ mod111.getYear() + " " + mod111.getPeriod().getDescription());
		}
		return ms;
	}

}
