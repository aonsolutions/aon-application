package com.esferalia.aon.gwt.fiscal.server.normalizedMemory;

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

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;

@WebServlet(name = "DownloadXml", urlPatterns = { "/aon_gwt_fiscal/gwt_download_deposit/*" })
public class DownloadXmlFileServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)throws ServletException, IOException{
        String domain_id = p_request.getParameter("domain_id");
        Integer domainId = Integer.parseInt(domain_id);
        String domain = AonUtil.getDomainName();


        File f = DBConsults.getXmlFile(domain, domainId) ;
        
       


        long length = f.length();
        FileInputStream fis = new FileInputStream(f);
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"DEPOSITO\"");
        //p_response.setContentType("application/octet-stream");
        p_response.setContentType(MimeType.XML.getName());

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
        
       //TODO probar --->  libro.close();

    }
	
	}
