package com.code.aon.webservice.finance;

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
import com.esferalia.aon.occam.api.model.Rawdoc;



@WebServlet(name = "Download Rawdoc Servlet", urlPatterns = {"/ms/download_rawdoc/*",
														"/aon_gwt_aio/ms/download_rawdoc/*",
														"/aon_gwt_fiscal/ms/download_rawdoc/*"})
public class DownloadRawdocServlet extends HttpServlet {
	
	private static final long serialVersionUID = 7711853628975825865L;

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
		Rawdoc rawdoc = AON.getRawdocFull(domainName, domainId, userName, id);
		if (rawdoc == null)  {
			throw new ServletException("Documento no encontrado");
		}
		if (rawdoc.getData() != null)  {
	        Integer length = rawdoc.getData().length;
	
	        ByteArrayInputStream bais = new ByteArrayInputStream(rawdoc.getData());
	        resp.addHeader("Content-Disposition","attachment; filename=\"RAWDOC_" + rawdoc.getId() + "." + rawdoc.getMimeType().getExtension()+"\"");
	        resp.setContentType(rawdoc.getMimeType().getName());
	        if (length > 0 && length <= Integer.MAX_VALUE)
	        	resp.setContentLength((int)length);
	        ServletOutputStream out = resp.getOutputStream();
	        resp.setBufferSize(32768);
	        int bufSize = resp.getBufferSize();
	        byte[] buffer = new byte[bufSize];
	        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
	        int bytes;
	        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0) {
	        	out.write(buffer, 0, bytes);
	        }
	        bis.close();
	        bais.close();
	        out.flush();
	        out.close();
		}
    }}
