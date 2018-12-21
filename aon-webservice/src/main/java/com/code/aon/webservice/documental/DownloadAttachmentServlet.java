package com.code.aon.webservice.documental;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import net.aonsolutions.aon.google.apis.drive.AonDrive;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;



@WebServlet(name = "DownloadAttachment", urlPatterns = {"/aon_gwt_aio/download_attachment/*",
														"/aon_gwt_fiscal/download_attachment/*"})
public class DownloadAttachmentServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638560042733639057L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		System.out.println("GET METHOD");
		
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(pathInfo[3]);
		Integer domainId = Integer.parseInt(parameters.get("domain"));
		Integer id = Integer.parseInt(parameters.get("id"));
		String attachType = parameters.get("attach_type");
	
		Domain domain = AON.getDomain(domainName, domainId, userName);
		
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(id), AttachType.getAttachType(attachType), true);

		if(attach.getDriveId() != null) {
			DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), userName);
			Drive drive = AonDrive.getInstace().serviceInitialize(g);
			String[] keys = {"fileId", "aontype", "domain"};
			String[] values = {attach.getId() + "", "registry", attach.getDomain().getName()};
			FileList fl = SearchFiles.searchFilesProperties(drive, keys, values);

			if(!fl.getItems().get(0).getId().equals(attach.getDriveId())) {
				attach.setDriveId(fl.getItems().get(0).getId());
				AON.updateAttach(domain.getName(), domain.getId(), "", attach);
			}
			if("0".equals(attach.getDparentId())) {
				attach.setDparentId(fl.getItems().get(0).getFileSize().toString());
				AON.updateAttach(domain.getName(), domain.getId(), "", attach);
			}
			attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
		}
      
        Integer length = attach.getData().length;

        ByteArrayInputStream bais = new ByteArrayInputStream(attach.getData());
        
        resp.addHeader("Content-Disposition","attachment; filename=\"" + attach.getDescription() + "." + attach.getMimeType().getExtension()+"\"");
        //p_response.setContentType("application/octet-stream");
        resp.setContentType(attach.getMimeType().getName());

        if (length > 0 && length <= Integer.MAX_VALUE)
        	resp.setContentLength((int)length);
        ServletOutputStream out = resp.getOutputStream();
        resp.setBufferSize(32768);
        int bufSize = resp.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
            out.write(buffer, 0, bytes);
        
        
        bis.close();
        bais.close();
        out.flush();
        out.close();
    }}
