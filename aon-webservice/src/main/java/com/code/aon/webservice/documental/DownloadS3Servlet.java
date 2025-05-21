package com.code.aon.webservice.documental;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.S3Document;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.google.apis.drive.AonDrive;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;
import solutions.aon.aws.s3.S3rDoc;

@WebServlet(name = "DownloadS3Document", urlPatterns = "/ms/download_s3_document/*")
public class DownloadS3Servlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638560042733639057L;
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userName = req.getParameter("user");
		String domainId = req.getParameter("domainId");
		String domainName = req.getParameter("domainName");
		String rattach = req.getParameter("rattach");
		String attachType = req.getParameter("attachType");
		
		try {
			
			Domain domain = AON.getDomain(domainName, Integer.parseInt(domainId), userName);	
			Attach attach = AON.getAttach(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(Integer.parseInt(rattach)), AttachType.getAttachType(attachType), true);

			resp.addHeader("Content-Disposition","attachment; filename=\"" + attach.getDescription() + "." + attach.getMimeType().getExtension()+"\"");
	        resp.setContentType(attach.getMimeType().getName());
			
	        ServletOutputStream output = resp.getOutputStream();
	        output.write(attach.getData());
	        resp.flushBuffer();
		}catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		System.out.println("GET METHOD");
		
		
		String[] pathInfo = req.getPathInfo().split("/");
			
		Boolean bool = pathInfo.length <= 2;
		
		String domainName = bool ? req.getServerName() : pathInfo[1]; 
		String userName = pathInfo.length > 2 ? pathInfo[2] : "";
		
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(pathInfo[bool ? 1 : 3]);
		Integer domainId = Integer.parseInt(parameters.get("domain"));
		Integer id = Integer.parseInt(parameters.get("id"));
		Integer docType = Integer.parseInt(parameters.get("type"));
		System.out.println("type : " + docType);
		Attach attach = new Attach();
		Domain domain = AON.getDomain(domainName, domainId, userName);
		User user = AON.getUser(domainName, domainId, userName);
		if(docType == 0) {
			Optional<S3Document> documents = AON_SOLUTIONS.getS3DocumentStream(domain, user, f -> f.getIdProperty().eq(id), f -> f.getIdProperty().eq(id), docType, null, null, null).findFirst();
			if (documents.isPresent()) {
				try {
					S3Document document = documents.get();
					attach.setData(S3rDoc.download(document.getS3key(), document.getS3bucket()));
					attach.setDescription(document.getName());
					attach.setMimeType(document.getMimetype());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(docType == 1) {
			attach = AON.getAttach(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(id), AttachType.getAttachType("data"), true);
			if(attach.getDriveId() != null) {
				DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), userName);
				Drive drive = AonDrive.getInstace().serviceInitialize(g);
				String[] keys = {"fileId", "aontype", "domain"};
				String[] values = {attach.getId() + "", "registry", attach.getDomain().getName()};
				FileList fl = SearchFiles.searchFilesAppProperties(drive, keys, values);
				if(fl.getFiles().size() > 0) {
					if(!fl.getFiles().get(0).getId().equals(attach.getDriveId())) {
						attach.setDriveId(fl.getFiles().get(0).getId());
						AON.updateAttach(domain.getName(), domain.getId(), "", attach);
					}
					if("0".equals(attach.getDparentId())) {
						attach.setDparentId(fl.getFiles().get(0).getSize().toString());
						AON.updateAttach(domain.getName(), domain.getId(), "", attach);
					}
				}
				attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
			}
			if(attach.getData() == null) {
				Optional<S3Document> documents = AON_SOLUTIONS.getS3DocumentStream(domain, user, f -> f.getIdProperty().eq(id), f -> f.getIdProperty().eq(id), docType, null, null, null).findFirst();
				attach.setData(AON_SOLUTIONS.getFileS3Document(domain, user, id));
				if (documents.isPresent()) {
						S3Document document = documents.get();
						attach.setDescription(document.getName());
						attach.setMimeType(document.getMimetype());
				}
			}
		}
		
        Integer length = attach.getData().length;
        
        ByteArrayInputStream bais = new ByteArrayInputStream(attach.getData());
        
        resp.addHeader("Content-Disposition","attachment; filename=\"" + attach.getDescription() + "." + attach.getMimeType().getExtension()+"\"");
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
    }
}

