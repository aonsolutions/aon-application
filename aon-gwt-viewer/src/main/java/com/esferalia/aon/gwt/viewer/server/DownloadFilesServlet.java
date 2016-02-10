package com.esferalia.aon.gwt.viewer.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonFileUtils;

@WebServlet(name = "DownloadViewer", urlPatterns = { "/aon_gwt_document/gwt_download_viewer/*" })
public class DownloadFilesServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638560042733639057L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		
		String driveId = !req.getParameter("drive_id").equals("null") ? req.getParameter("drive_id") : null;
		Integer attachId = Integer.parseInt(req.getParameter("attach_id"));
		AttachType attachType = AttachType.values()[Integer.parseInt(req.getParameter("attach_type"))];
		String attachName = req.getParameter("attach_name");
		MimeType mimeType = MimeType.values()[Integer.parseInt(req.getParameter("mimetype"))];
		String domainName = req.getParameter("domain_name");
		Integer domainId = Integer.parseInt(req.getParameter("domain_id"));
		String login = "";
		
		Domain domain = AON.getDomain(domainName, domainId, login);
		Attach attach = new Attach().setDomain(domain)
				.setDescription(attachName)
				.setDriveId(driveId)
				.setId(attachId)
				.setAttachType(attachType)
				.setMimeType(mimeType);
		
		byte[] data = getData(attach, login);
        
        
        File file= AonFileUtils.byteToFile(data, attachName);
        long length = file.length();
        FileInputStream fis = new FileInputStream(file);
        
        resp.addHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"."+mimeType.getExtension()+"\"");
        //p_response.setContentType("application/octet-stream");
        resp.setContentType(mimeType.getName());

        if (length > 0 && length <= Integer.MAX_VALUE)
            resp.setContentLength((int)length);
        ServletOutputStream out = resp.getOutputStream();
        resp.setBufferSize(32768);
        int bufSize = resp.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(fis,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
            out.write(buffer, 0, bytes);
        
        
        bis.close();
        fis.close();
        out.flush();
        out.close();
    }
	
	public byte[] getData(Attach attach, String login){
		byte[] b = "".getBytes();
		if(attach.getDriveId() != null || 
				(attach.getId() != null && attach.getAttachType() != null)){
			Integer attachId = attach.getId();
			if(attach.getDriveId() == null)
				attach = AON.getAttach(attach.getDomain().getName(), attach.getDomain().getId(), login,
						f-> f.getIdProperty().eq(attachId), attach.getAttachType());
			if(attach.getDriveId() != null){
				b = DriveUtils.getByteFile(attach, new User().setLogin(login));
			} else if(attach.getData() != null) b = attach.getData();
		}
		return b;
	}
}






