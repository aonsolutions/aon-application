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
import com.esferalia.aon.occam.server.fiscal.format.mod200.Mod200Reader;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 - 2013 BOE Upload ", urlPatterns = { "/aon_gwt_fiscal/Mod2002013BOEUpload" })
public class Mod2002013BOEUpload extends UploadAction {

// upload settings
//	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
//	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
//	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	/*
	 * @Override protected void doPost(HttpServletRequest request,
	 * HttpServletResponse response) throws ServletException, IOException {
	 * 
	 * if (!ServletFileUpload.isMultipartContent(request)) {
	 * response.getWriter()
	 * .println("Error: Form must has enctype=multipart/form-data.");
	 * response.getWriter().flush(); return; }
	 * 
	 * String domainName = request.getParameter("domainName"); int domainId =
	 * Integer.parseInt(request.getParameter("domainId")); String saveCheck =
	 * request.getParameter("saveCheck"); System.out.println("saveCheck ...: " +
	 * saveCheck);
	 * 
	 * DiskFileItemFactory factory = new DiskFileItemFactory();
	 * factory.setSizeThreshold(MEMORY_THRESHOLD); factory.setRepository(new
	 * File(System.getProperty("java.io.tmpdir")));
	 * 
	 * ServletFileUpload upload = new ServletFileUpload(factory);
	 * upload.setFileSizeMax(MAX_FILE_SIZE);
	 * upload.setSizeMax(MAX_REQUEST_SIZE);
	 * 
	 * try { List<FileItem> formItems = upload.parseRequest(request);
	 * System.out.println( "SERVLET ..: " + formItems.size() ); if (formItems !=
	 * null && formItems.size() > 0) { for (FileItem item : formItems) { if
	 * (!item.isFormField()) { ByteArrayInputStream input = new
	 * ByteArrayInputStream(item.get()); Mod2002013 mod200 =
	 * Mod200Reader.getMod2002013(input); mod200.setDomain(domainId); Company
	 * company = AON.getCompanyForDomain(domainName, domainId);
	 * mod200.setEnterprise(company.getId()); AON.saveMod2002013(domainName,
	 * domainId, mod200); } } } } catch (Exception ex) {
	 * response.getWriter().println("Error: " + ex.getMessage());
	 * response.getWriter().flush(); }
	 * 
	 * }
	 */

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
					Mod2002013 mod200 = Mod200Reader.getMod2002013(input);
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
