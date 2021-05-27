package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.fileupload.FileItem;

import com.esferalia.aon.occam.impl.jooq.dao.mod200_2019.jaxb.MOD2002019;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import gwtupload.server.exceptions.UploadException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 - 2019 Accounting Upload ", urlPatterns = { "/aon_gwt_fiscal/Mod2002019AccountingUpload" })
public class Mod2002019AccountingUpload extends UploadAction {

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
					JAXBContext context = JAXBContext.newInstance(MOD2002019.class);
					Unmarshaller um = context.createUnmarshaller();
					MOD2002019 xml = (MOD2002019) um.unmarshal(input);
					request.getSession().setAttribute("Mod2002019Accounting", xml);
				} catch (Exception ex) {
					throw new UploadException(ex);
				}
			}
		}
		// Remove files from session because we have a copy of them
		super.removeSessionFileItems(request);
		// Send your customized message to the client.
		return response;
	}

}
