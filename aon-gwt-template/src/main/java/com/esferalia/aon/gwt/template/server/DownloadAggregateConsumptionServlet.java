package com.esferalia.aon.gwt.template.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

@WebServlet(name = "DownloadTemplatesAggregateConsumption", urlPatterns = { "/aon_gwt_template/gwt_download_aggregate_consumption/*"
																			,"/aon_gwt_aio/gwt_download_aggregate_consumption/*"})
public class DownloadAggregateConsumptionServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		String key = req.getParameter("tmpkey");
		String username = req.getParameter("username");
		String domainName = req.getParameter("dname");
		String domainId = req.getParameter("did");
		
		Attach attach = AON.getAttach(domainName, Integer.parseInt(domainId), username, f-> f.getIdProperty().eq(Integer.parseInt(key)), AttachType.DATA);
		AON.deleteAttach(domainName, Integer.parseInt(domainId), username, f -> f.getIdProperty().eq(Integer.parseInt(key)), AttachType.DATA);
		
		if(attach.getData() == null && attach.getDriveId() != null) {
			DomainGserviceaccount d = AON.getDomainGserviceaccount(domainName, Integer.parseInt(domainId), username);
			Drive drive = AonDrive.getInstace().serviceInitialize(d);
			AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId());
		}
		InputStream fis = new ByteArrayInputStream(attach.getData());
				
        resp.addHeader("Content-Disposition","attachment; filename=\"" + "consumo.xls" +"\"");
    	resp.setContentType("application/msexcel");

     //   if (length > 0 && length <= Integer.MAX_VALUE);
      //      resp.setContentLength((int)length);
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
}