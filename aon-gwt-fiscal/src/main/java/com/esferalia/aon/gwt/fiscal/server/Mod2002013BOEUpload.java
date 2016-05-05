package com.esferalia.aon.gwt.fiscal.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import gwtupload.server.exceptions.UploadException;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.server.fiscal.format.mod200.Mod2002013Reader;

@WebServlet(name = "Mod200 - 2013 BOE Upload ", urlPatterns = { "/aon_gwt_fiscal/Mod2002013BOEUpload" })
public class Mod2002013BOEUpload extends UploadAction {

	private static final long serialVersionUID = -3856855479554642408L;

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
					ByteArrayInputStream input = new ByteArrayInputStream(
							item.get());
					Mod2002013 mod200 = Mod2002013Reader.getMod2002013(input);
					request.getSession().setAttribute("Mod2002013Import", mod200);
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
