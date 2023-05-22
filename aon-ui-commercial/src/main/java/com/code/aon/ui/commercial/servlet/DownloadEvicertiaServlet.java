package com.code.aon.ui.commercial.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.ui.commercial.util.DocumentOnlineSigner;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;



@WebServlet(name = "DownloadEvicertia", urlPatterns = {"/download_evicertia/*"})
public class DownloadEvicertiaServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638560042733639057L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		System.out.println("GET METHOD");
		String[] pathInfo = req.getPathInfo().split("/");
		for (String path : pathInfo) {
			System.out.println(path);
		}
		
		Domain domain = AON.getDomain(req.getServerName(), Integer.parseInt(pathInfo[1]), "");
		ApplicationParameter username = AON.getApplicationParameter(domain.getName(), domain.getId(), "", "DOCUMENT_ONLINE_SIGN_username");
		ApplicationParameter password = AON.getApplicationParameter(domain.getName(), domain.getId(), "", "DOCUMENT_ONLINE_SIGN_password");
		ApplicationParameter signingType = AON.getApplicationParameter(domain.getName(), domain.getId(), "", "DOCUMENT_ONLINE_SIGN_signingType");

		DocumentOnlineSigner dos = new DocumentOnlineSigner();
		dos.init(username.getValue(), password.getValue(), Integer.parseInt(signingType.getValue()));

		byte[] data = dos.getEvicertiaData(pathInfo[2]);
      
        Integer length = data.length;

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        resp.addHeader("Content-Disposition","attachment; filename=\"contrato.pdf\"" );
        //p_response.setContentType("application/octet-stream");
        resp.setContentType(com.esferalia.aon.occam.api.model.type.MimeType.PDF.getName());

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
