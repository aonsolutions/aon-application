package com.esferalia.aon.gwt.payroll.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Logger;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.in.payroll.pdf.jooq.JooqModificationFormBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.ContractAttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "MODIFICATIONFORM", urlPatterns = {"/aon_gwt_aio/modification_form/*", "/aon_gwt_payroll/modification_form/*"})
public class ModificationFormServlet extends AonApiHttpServlet {
	
	private static final Logger LOGGER  = Logger.getLogger(ModificationFormServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API MODIFICATION SERVLET - GET METHOD");
		try {
			manage( req, resp );
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API MODIFICATION SERVLET - POST METHOD");
		try {
			manage( req, resp );
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
		
	private void manage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		switch (req.getPathInfo()) {
			case "/export/":
				exportPDF(req, resp);
				break;
			case "/generate/":
				response(req, resp, generatePDF(req));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
		}
	}
	
	private JSONObject generatePDF(HttpServletRequest req) {
		String login = req.getParameter("currentUser");
		String domainName = req.getParameter("currentDomain");
		int contractId = AonNumberUtils.toint(req.getParameter("contractId"));
		String title = req.getParameter("title");
		String information = req.getParameter("information");
		Date date = AonDateUtils.parse(req.getParameter("date"), AonDateUtils.SIMPLE_DATE_FORMAT);
		Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
		
		int domainId = 0;
		try {
			domainId = AonServletUtils.getDomainID(domainName);
		} catch (SQLException e) {}
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			JooqModificationFormBuilder.createModificationForm(os, domainName, domainId, login, contractId, title, information, date);
			os.close();
			
			Attach attach = new Attach()
				.setAttachType(AttachType.CONTRACT)
				.setDate(new Date())
				.setAttachModule(contractId)
				.setData(os.toByteArray())
				.setDescription(parseDescription(title))
				.setDomain(domain)
				.setMimeType(MimeType.PDF)
				.setConfidential(false)
				.setType(ContractAttachType.MODIFCONTRACT.value());
			
			AON.insertAttach(domainName, domainId, login, attach);
			
			return new JSONObject().put("success", "Documento generado correctamente. Este documento se encuentra en el apartado de Documentos.");
		} catch (CanNotCreatePdfException | IOException e) {
			e.printStackTrace();
			return new JSONObject().put("error", "No se ha podido generar el documento");
		}
	}

	private void exportPDF(HttpServletRequest req, HttpServletResponse resp) {
		String login = req.getParameter("currentUser");
		String domainName = req.getParameter("currentDomain");
		int contractId = AonNumberUtils.toint(req.getParameter("contractId"));
		String title = req.getParameter("title");
		String information = req.getParameter("information");
		Date date = AonDateUtils.parse(req.getParameter("date"), AonDateUtils.SIMPLE_DATE_FORMAT);
		
		int domainId = 0;
		try {
			domainId = AonServletUtils.getDomainID(domainName);
		} catch (SQLException e) {}
		
		resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\"modificacion.pdf\";");
	
		try (OutputStream os = resp.getOutputStream()) {
			JooqModificationFormBuilder.createModificationForm(os, domainName, domainId, login, contractId, title, information, date);
			os.flush();
		} catch (CanNotCreatePdfException | IOException e) {
			e.printStackTrace();
		}
		
	}

	private String parseDescription(String description) {
		return description.length() < 64 ? description : description.substring(0, 63);
	}
	
}
