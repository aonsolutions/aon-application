package com.esferalia.aon.gwt.template.server;

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
       
		File file =(File) req.getSession().getAttribute(key);
		req.getSession().removeAttribute(key);
		
        long length = file.length();
        FileInputStream fis = new FileInputStream(file);
        
        resp.addHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
    	resp.setContentType("application/msexcel");

        if (length > 0 && length <= Integer.MAX_VALUE);
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
}