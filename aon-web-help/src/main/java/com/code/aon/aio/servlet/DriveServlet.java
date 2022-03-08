package com.code.aon.aio.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.aio.service.drive.DriveService;
import com.code.aon.aio.service.drive.exception.GoogleDriveException;

import net.aonsolutions.aon.google.apis.drive.SearchFiles;

/**
 * Servlet implementation class DriveServlet
 */
@WebServlet("/DriveServlet")
public class DriveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private DriveService drive;
	
	public DriveServlet() {
		super();
		try {
			this.drive = new DriveService();
		} catch (GoogleDriveException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ServletContext ct = getServletContext();
		
		
		if("video".equals(request.getParameter("action"))) {
			
			final String name = request.getParameter("name");
			final String parent = request.getParameter("parent");
			
			if(name == null || parent == null) {
				response.sendError(400);
				return;
			}
			
			if(drive == null) {
				response.sendError(500, "Cannot connect to Google Drive");
				return;
			}
			
			
			final Optional<InputStream> stream = drive.downloadVideoByNameAndParentNotTrashed(Optional.ofNullable(name), Optional.ofNullable(null));			
			
			if(stream.isEmpty()) {
				response.sendError(404);
				return;
			}
			
			final InputStream input = stream.get();
			
			OutputStream output = response.getOutputStream();
			response.setHeader("Content-Disposition", "inline; filename=\"aonvideo.mp4\"");
			

		    byte[] buffer = new byte[4096];

		    int read = 0;

		    while ((read = input.read(buffer)) != -1) {
		      output.write(buffer, 0, read);
		    }

		    input.close();
			
			output.flush();
			output.close();
			return;
		}

		response.sendError(403);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	


}
