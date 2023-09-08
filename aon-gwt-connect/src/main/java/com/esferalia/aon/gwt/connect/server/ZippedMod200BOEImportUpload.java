package com.esferalia.aon.gwt.connect.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import gwtupload.server.exceptions.UploadException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Zipped Mod200 - 2013 BOE Upload ", urlPatterns = { "/aon_gwt_connect/ZippedMod200BOEImportUpload" })
public class ZippedMod200BOEImportUpload extends UploadAction {

	private static final long serialVersionUID = -8711190307608213317L;


	@Override
	public void checkRequest(HttpServletRequest request) {
		maxSize = 10485760;
		super.checkRequest(request);
	}


	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> sessionFiles) throws UploadActionException {
		String response = "";
		for (FileItem item : sessionFiles) {
			if (false == item.isFormField()) {
				try {
					ByteArrayInputStream input = new ByteArrayInputStream(item.get());
					File zipFile = File.createTempFile("aonMod200BOE", ".zip");
					FileOutputStream output = new FileOutputStream(zipFile); 
					AonIOUtils.copy(input, output);
					request.getSession().setAttribute("aonMod200BOEZIP", zipFile);
				} catch (Exception ex) {
					throw new UploadException(ex);
				}

			}
		}
		// / Remove files from session because we have a copy of them
		super.removeSessionFileItems(request);
		// / Send your customized message to the client.
		return response;
	}
	
}
