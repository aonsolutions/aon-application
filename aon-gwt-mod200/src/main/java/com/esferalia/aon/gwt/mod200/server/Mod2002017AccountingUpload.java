package com.esferalia.aon.gwt.mod200.server;

import java.io.ByteArrayInputStream;
import java.util.List;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.fileupload.FileItem;

import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2017.jaxb.MOD2002017;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import gwtupload.server.exceptions.UploadException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 - 2017 Accounting Upload ", urlPatterns = { "/aon_gwt_mod200/Mod2002017AccountingUpload" })
public class Mod2002017AccountingUpload extends UploadAction {

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
					JAXBContext context = JAXBContext.newInstance(MOD2002017.class);
					Unmarshaller um = context.createUnmarshaller();
					MOD2002017 xml = (MOD2002017) um.unmarshal(input);
					request.getSession().setAttribute("Mod2002017Accounting", xml);
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
