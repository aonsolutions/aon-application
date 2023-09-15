package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;

public class DocumentServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String requestURI = req.getRequestURI();
		String ext = AonServletUtils.getExtn(requestURI);
		String rattachIdStr = AonServletUtils.getFileName(requestURI);
		
		int rattachId = Integer.parseInt(rattachIdStr);
		MimeType mimetype = MimeType.getByExtension(ext);
		
		try {
			PayrollServletUtils.RAttach rattach = 
					PayrollServletUtils.getRAttach(rattachId);
			
			resp.setContentType(mimetype.getName());
			OutputStream os = resp.getOutputStream();
			os.write(rattach.bytes);
			os.flush();
			
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}
