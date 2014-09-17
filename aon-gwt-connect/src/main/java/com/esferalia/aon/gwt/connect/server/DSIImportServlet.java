package com.esferalia.aon.gwt.connect.server;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.dsi.DSI2AON;
import com.esferalia.aon.dsi.util.DBUtils;
import com.esferalia.aon.gwt.connect.shared.DSIImportService;
import com.esferalia.aon.gwt.payroll.server.AonServletUtils;
import com.google.gwt.thirdparty.guava.common.io.Files;

@MultipartConfig
public class DSIImportServlet extends HttpServlet implements DSIImportService {

	private int MAX_MEM_SIZE = 4 * 1024;

	/**
	 * The get method is used to monitor the uploading process .
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	/**
	 * The post method is used to receive the file and import/load it .
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		File tempDir = null;
		List<File> dbs = new ArrayList<File>();
		try {
			ServletContext context = getServletContext();
			AonServletUtils.initFacesContext(context, req, resp);

			// checks if the request actually contains upload file
			if (!isMultipartContent(req)) {
				// if not, we stop here(?:X
				resp.sendError(SC_BAD_REQUEST,
						"Petición erronea, no es 'multipart/form-data'");
				return;
			}

			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// Set factory constraints
			factory.setSizeThreshold(MAX_MEM_SIZE);
			tempDir = Files.createTempDir();
			factory.setRepository(tempDir);

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			// No limit for maximum allowed size of a complete request

			// Parse the request
			List<FileItem> fileItems = upload.parseRequest(req);
			for (FileItem fileItem : fileItems) {
				if (fileItem.isFormField()) {
					processFormField(fileItem);
				} else {
					dbs.add(processUploadFile(fileItem));
				}
			}

			
			String owner = "admin"; //getRemoteUser();
			String domain = req.getServerName();//getDomainName();
			Connection aon = DatabaseUtil.getConnection(domain);

			for (File db : dbs) {
				String url = String.format("jdbc:paradox:///%s",
						db.getAbsolutePath());
				Connection dsi = DBUtils.getDsiConnection(url);

				new DSI2AON(dsi, aon)
				.setCommit(false)
				.run(domain, "-" + domain, owner);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileUploadException e) {

		} catch (AonSQLException e) {
			e.printStackTrace();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		} finally {
			if (tempDir != null)
				tempDir.delete();
			for (File db : dbs)
				db.delete();
			AonServletUtils.releaseFacesContext();
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Mark the current process to be canceled.
	 * 
	 * @param request
	 */
	private void cancel(HttpServletRequest request) {
	}

	private void processFormField(FileItem fileItem) {

	}

	private File processUploadFile(FileItem fileItem) throws IOException {
		InputStream in = null;
		ZipInputStream zipin = null;
		try {

			in = fileItem.getInputStream();
			zipin = new ZipInputStream(in);
			File parent = Files.createTempDir();
			unzip(parent, zipin);
			return parent;

		} finally {
			if (zipin != null)
				zipin.close();
		}
	}




	// ------------------------------------------------------------------------

	private static void unzip(File parent, ZipInputStream zin)
			throws IOException {

		byte buff[] = new byte[1024];

		for (ZipEntry entry = zin.getNextEntry(); entry != null; entry = zin
				.getNextEntry()) {
			String name = entry.getName();
			File file = new File(parent, name);

			if (entry.isDirectory()) {
				file.mkdirs();
				continue;
			}

			FileOutputStream fout = new FileOutputStream(file);
			for (int read = zin.read(buff, 0, 1024); read > 0; read = zin.read(
					buff, 0, 1024))
				fout.write(buff, 0, read);
			fout.close();

		}

	}

	private static Connection getConnection() throws SQLException {
		try {
			String domainName = AonUtil.getDomainName();
			Connection connection = DatabaseUtil.getConnection(domainName);
			return connection;
		} catch (AonConnectionException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	private static String getRemoteUser() {
		
		return AonUtil.getRemoteUser();
	}


	private static String getDomainName() {
		return AonUtil.getDomainName();
	}
	
	
	

}
