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


@WebServlet("/HelpSearch")
public class HelpSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Drive drive;
	
	public HelpSearch() {
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
	
		
		
			
		response.sendError(403);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	


}
