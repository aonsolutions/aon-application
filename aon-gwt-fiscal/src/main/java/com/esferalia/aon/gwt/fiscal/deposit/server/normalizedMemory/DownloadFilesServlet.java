package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

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

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;


@WebServlet(name = "DownloadMemoryFiles", urlPatterns = { "/aon_gwt_deposit/gwt_download_memory/*" })
public class DownloadFilesServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638560042733639057L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{
        String fileId = p_request.getParameter("file_id");
        String domainId =  p_request.getParameter("domain_id");
        String name = p_request.getParameter("name");
        String domain = AonServletUtils.getRequestDomainName(p_request);
        Integer idFile = Integer.parseInt(fileId);
        Integer idDomain = Integer.parseInt(domainId);
        
        File file = DBConsults.getMemoryFile(domain, idDomain, idFile,name);
        Byte m = DBConsults.getMimeType(domain, idDomain, idFile);
        String mimetype = MimeType.values()[m].getName();
       
        long length = file.length();
        FileInputStream fis = new FileInputStream(file);
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + name+"."+MimeType.values()[m].getExtension()+"\"");
        p_response.setContentType(mimetype);

        if (length > 0 && length <= Integer.MAX_VALUE);
            p_response.setContentLength((int)length);
        ServletOutputStream out = p_response.getOutputStream();
        p_response.setBufferSize(32768);
        int bufSize = p_response.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(fis,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
            out.write(buffer, 0, bytes);
        
        
        bis.close();
        fis.close();
        out.flush();
        out.close();
    }}
