package com.esferalia.aon.gwt.connect.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.server.fiscal.format.mod200.Mod200Reader;
import com.google.gwt.thirdparty.guava.common.io.Files;

//@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024, 
//maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
//
//Instead of using the @MultipartConfig annotation to hard-code these attributes
//in your file upload servlet, you could add the following as a child element of 
// the servlet configuration element in the web.xml file.
//
//<multipart-config>
//	<location>/tmp</location>
//	<max-file-size>20848820</max-file-size>
//	<max-request-size>418018841</max-request-size>
//	<file-size-threshold>1048576</file-size-threshold>
//</multipart-config>
//

@MultipartConfig
public class BOEImportServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int MAX_MEM_SIZE = 4 * 1024;

	private static final Pattern JSON_PROPERTY_PATTERN = Pattern
			.compile("(\\w+):(\"([^\"]*)\"|(\\w*))");

	private static String domainName = null;
	private static Integer parentDomain = null;
	private static Connection connection = null;

	/**
	 * The get method is used to monitor the uploading process .
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doPost(req, resp);
	}

	/**
	 * The post method is used to receive the file and import/load it .
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		PrintStream print = new PrintStream(resp.getOutputStream(), false,
				"UTF-8");

		try {

			ServletContext context = getServletContext();
			AonServletUtils.initFacesContext(context, req, resp);

			domainName = req.getServerName();
			connection = DatabaseUtil.getConnection(domainName);
			parentDomain = DatabaseUtil.getDomain(connection, domainName); // parentDomain

			resp.setContentType("text/html;charset=UTF-8");

			print.print(req.getParts().stream()
					.map(BOEImportServlet::processPart)
					.map(f -> String.format("\"%s\"", f.getAbsolutePath()))
					.collect(Collectors.joining(",", "[", "]")));

		} catch (SQLException ex) {

		} catch (AonConnectionException ex) {
			throw new IOException(ex);

		} finally {

			if (print != null)
				print.close();

			AonServletUtils.releaseFacesContext();
		}
	}

	// ------------------------------------------------------------------------

	private static File processPart(Part part) {
		InputStream in = null;
		ZipInputStream zipin = null;
		try {

			in = part.getInputStream();
			zipin = new ZipInputStream(in);
			File parent = Files.createTempDir();
			unzip(parent, zipin);
			return parent;

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (zipin != null)
				try {
					zipin.close();
				} catch (IOException e) {
				}
			;
		}
	}

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

			FileInputStream input = new FileInputStream(file);

			Mod2002013 mod200 = Mod200Reader.getMod2002013(input);

			String enterDocument = mod200.getEnterpriseDocument();
			String enterName = mod200.getEnterpriseName();
			
			try {
				
				Domain domain = AON.insertDomain(domainName,
						parentDomain, enterDocument, enterName);
				
				Company company = AON.getCompanyForDomain(
						domain.getName(), domain.getId());
				int enterpriseID = company.getId();

				mod200.setDomain(domain.getId());
				mod200.setEnterprise(enterpriseID);
				AON.saveMod2002013(domain.getName(),
						domain.getId(), mod200);
				
			} catch (Exception ex) {
				
			}
			

		}
	}

	private static Properties getProperties(String json) {
		Properties properties = new Properties();
		Matcher matcher = JSON_PROPERTY_PATTERN.matcher(json);
		while (matcher.find()) {
			String name = matcher.group(1);
			String value1 = matcher.group(3);
			String value2 = matcher.group(4);
			properties.put(name, value1 != null ? value1 : value2);
		}
		return properties;
	}

	private static void proccessFile(String domainName, InputStream input) {

		Mod2002013 mod200 = Mod200Reader.getMod2002013(input);

		String document = mod200.getEnterpriseDocument();
		String name = mod200.getEnterpriseName();
		// CREAR DOMINIO SI NO EXISTE, SI EXISTE RECUPERARLO.
		int domainID = 0;
		// --------------------------------------------------
		Company company = AON.getCompanyForDomain(domainName, domainID);
		int enterpriseID = company.getId();

		mod200.setDomain(domainID);
		mod200.setEnterprise(enterpriseID);
		AON.saveMod2002013(domainName, domainID, mod200);
	}

}
