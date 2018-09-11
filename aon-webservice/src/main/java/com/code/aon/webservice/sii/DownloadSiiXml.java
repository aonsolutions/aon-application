package com.code.aon.webservice.sii;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

@WebServlet(name = "DownloadSiiXml", urlPatterns = {"/aon_gwt_aio/download_sii_xml/*"})
public class DownloadSiiXml extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	       
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		String id = parameters.get("id");
		Integer dataResponseId = Integer.parseInt(id);
		String option = parameters.get("option");

		Attach attach = null;
		if("request".equals(option)){
			attach = AON.getAttach(domain.getName(), domain.getId(), login, 
					f -> f.getSourceTypeProperty().eq(DataAttachSource.SII.value())
					.and(f.getSourceBatchProperty().eq(dataResponseId))
					.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value())),
				AttachType.DATA);
		} else if("response".equals(option)){
			attach = AON.getAttach(domain.getName(), domain.getId(), login, 
					f -> f.getSourceTypeProperty().eq(DataAttachSource.SII.value())
					.and(f.getSourceBatchProperty().eq(dataResponseId))
					.and(f.getTypeProperty().ne(DataAttachType.REQUEST.value())),
				AttachType.DATA);
		}		
		
		if(attach != null && attach.getData() == null) {
			DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), login);
			Drive drive = AonDrive.getInstace().serviceInitialize(g);
			attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
		}
		if(attach != null && attach.getData() != null){
			Utils.addCorsHeader(resp);
			resp.setContentType(MimeType.XML.getName());
			resp.setHeader("Content-disposition", "inline; filename=\"SII_" + option.toUpperCase() + ".xml\";");
			ByteArrayInputStream fileInpurOs =  new ByteArrayInputStream(attach.getData());
			AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
			resp.flushBuffer();
			fileInpurOs.close();
		}
	}
}
