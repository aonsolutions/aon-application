
package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

public class UploadFileServlet extends UploadAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void checkRequest(HttpServletRequest request) {
		maxSize = 10485760;
		super.checkRequest(request);
	}

	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> sessionFiles) throws UploadActionException {
		String domain_id = request.getParameter("domain_id");

		String response = "";
		for (FileItem item : sessionFiles) {
			System.out.println(item.isFormField());
			if (false == item.isFormField()) {

	

				request.getSession().setAttribute("D2DepositFile"+domain_id, item.get());
				request.getSession().setAttribute("D2DepositMimeType"+domain_id, item.getContentType());
				

				/*
				 * try { Esquema schema = Utils.readXml(b);
				 * if(schema.getCabecera().getCIF().equals(cif)){
				 * //DBConsults.insertDeposit(domain, b, domainId);
				 * request.getSession
				 * ().removeAttribute("ModifyD2DepositSchema"+cif);
				 * request.getSession().putValue("d2DepositSchema"+cif, schema);
				 * } } catch (JAXBException e) { e.printStackTrace(); }
				 */
			}
		}

		// / Remove files from session because we have a copy of them
		super.removeSessionFileItems(request);

		// / Send your customized message to the client.
		return response;
	}

}
