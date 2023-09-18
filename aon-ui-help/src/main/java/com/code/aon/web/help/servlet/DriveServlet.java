package com.code.aon.web.help.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Optional;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.web.help.service.drive.DriveService;
import com.code.aon.web.help.service.drive.exception.GoogleDriveException;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.SearchFiles;


@WebServlet("/DriveServlet")
public class DriveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Drive drive;
	
	public DriveServlet() {
		super();
		try {
			this.drive = DriveService.connect();
		} catch (GoogleDriveException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ServletContext ct = getServletContext();
		
		if(drive == null) {
			response.sendError(500, "Cannot connect to Google Drive");
			return;
		}
		
		final String action = request.getParameter("action");
		
		if("video".equals(action)) {
			String name = request.getParameter("name");
			String parent = request.getParameter("parent");
			
			if(name == null) {
				response.sendError(400);
				return;
			}			
						
			final Optional<InputStream> stream = DriveService.downloadByNameAndParentNotTrashed(drive, name, parent);			
			
			if(stream.isEmpty()) {
				response.sendError(404);
				return;
			}
			
			final InputStream input = stream.get();
			
			OutputStream output = response.getOutputStream();
			response.setContentType("video/ogg");
			response.setHeader("Content-Disposition", "inline; filename=\"" + name + "\".ogg");


		    byte[] buffer = new byte[2096];

		    int read = 0;

		    while ((read = input.read(buffer)) != -1) {
		      output.write(buffer, 0, read);
		    }

		    input.close();
			
			output.flush();
			output.close();
			return;   
		}
		
		if("pdf".equals(request.getParameter("action"))) {
			
			String name = request.getParameter("name");
			String parent = request.getParameter("parent");
			
			if(name == null) {
				response.sendError(400);
				return;
			}

			final Optional<InputStream> stream = DriveService.downloadByNameAndParentNotTrashed(drive, name, parent);			
			
			if(stream.isEmpty()) {
				response.sendError(404);
				return;
			}
			
			response.setContentType("application/pdf");
			final InputStream input = stream.get();
			
			OutputStream output = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\".pdf");
			output.write(input.readAllBytes());
		   
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
