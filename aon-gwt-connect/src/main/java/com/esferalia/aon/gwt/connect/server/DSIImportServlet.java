package com.esferalia.aon.gwt.connect.server;

import static com.esferalia.aon.dsi.jooq.tables.Fnempres.FNEMPRES;
import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import org.apache.commons.fileupload.FileItem;

import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.dsi.DSI2AON;
import com.esferalia.aon.dsi.jooq.tables.records.FnempresRecord;
import com.esferalia.aon.dsi.util.DBUtils;
import com.esferalia.aon.dsi.util.DSIUtils;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.connect.shared.DSIImportService;
import com.esferalia.aon.gwt.connect.shared.DSIImportService.GetActionHandler;
import com.esferalia.aon.gwt.connect.shared.JsImportEvent;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.PersonRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
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
public class DSIImportServlet extends HttpServlet implements DSIImportService,
		GetActionHandler<HttpServletRequest, HttpServletResponse, IOException> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int MAX_MEM_SIZE = 4 * 1024;

	private static final Pattern JSON_PROPERTY_PATTERN = Pattern
			.compile("(\\w+):(\"([^\"]*)\"|(\\w*))");

	private static class PrintListener implements DSI2AON.Listener {

		private PrintStream print;

		public PrintListener(PrintStream print) {
			this.print = print;
		}

		@Override
		public void onCommited() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onRollbacked() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPaymentConceptUpdated(PaymentConceptRecord concept) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPaymentConceptIgnored(PaymentConceptRecord concept) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPaymentConceptInserted(PaymentConceptRecord concept) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onContractIgnored(ContractRecord contract,
				PersonRecord person) {
			printJsImportEvent(toJsEmployee(contract, person),
					JsImportEvent.EventType.EMPLOYEE_IGNORED);
		}

		@Override
		public void onContractInserted(ContractRecord contract,
				PersonRecord person) {
			printJsImportEvent(toJsEmployee(contract, person),
					JsImportEvent.EventType.EMPLOYEE_INSERTED);
		}

		@Override
		public void onContractUpdated(ContractRecord contract,
				PersonRecord person) {
			printJsImportEvent(toJsEmployee(contract, person),
					JsImportEvent.EventType.EMPLOYEE_UPDATED);
		}

		@Override
		public void onEnterpriseIgnored(RegistryRecord enterprise) {
			printJsImportEvent(toJsEmpres(enterprise),
					JsImportEvent.EventType.ENTERPRISE_IGNORED);
		}

		@Override
		public void onEnterpriseUpdated(RegistryRecord enterprise) {
			printJsImportEvent(toJsEmpres(enterprise),
					JsImportEvent.EventType.ENTERPRISE_UPDATED);
		}

		@Override
		public void onEnterpriseInserted(RegistryRecord enterprise) {
			printJsImportEvent(toJsEmpres(enterprise),
					JsImportEvent.EventType.ENTERPRISE_INSERTED);
		}

		private void printJsImportEvent(String src, JsImportEvent.EventType type) {
			print.print("{");
			print.print("\"src\":");
			print.print(src);
			print.print(",\"type\":");
			print.print("\"");
			print.print(type.name());
			print.print("\"");
			print.println("}");
			print.flush();
		}

		private static String toJsEmpres(RegistryRecord enterprise) {
			return format("{\"rsocial\":\"%s\"}", enterprise.getName());
		}

		private static String toJsEmployee(ContractRecord contract,
				PersonRecord person) {
			return format(
					"{\"name\":\"%s\", \"firstSurName\":\"%s\", \"secondSurName\":\"%s\" }",
					person.getName(), person.getFirstSurname(),
					person.getSecondSurname());
		}
	}

	/**
	 * The get method is used to monitor the uploading process .
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			GetAction.valueOf(req.getParameter(GET_ACTION_PARAM)).handle(this, req,
					resp);
		}catch (Exception ex) {
			System.out.println(ex.getMessage() 
					+ " " + ex.getCause() + " " + ex.getLocalizedMessage());
		}
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

			resp.setContentType("text/html;charset=UTF-8");

			// @formatter:off
			print.print(req.getParts().stream()
					.map(DSIImportServlet::processPart)
					.map(f -> String.format("\"%s\"", f.getAbsolutePath()))
					.collect(Collectors.joining(",", "[", "]")));
			// @formatter:on

		} finally {

			if (print != null)
				print.close();
		
			AonServletUtils.releaseFacesContext();
		}
	}

	/**
	 * The post method is used to receive the file and import/load it .
	 */
/*	protected void __doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		File tempDir = null;

		OutputStream out = null;
		PrintStream print = null;

		try {
			out = resp.getOutputStream();
			print = new PrintStream(out, false, "UTF-8");
			// resp.setContentType("application/json;charset=UTF-8");
			resp.setContentType("text/html;charset=UTF-8");

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

			List<File> dbs = new ArrayList<File>();
			// Parse the request
			List<FileItem> fileItems = upload.parseRequest(req);
			for (FileItem fileItem : fileItems) {
				if (fileItem.isFormField()) {
					processFormField(fileItem);
				} else {
					dbs.add(processUploadFile(fileItem));
				}
			}

			print(print, dbs);

		} catch (FileUploadException e) {

		} finally {
			if (tempDir != null)
				tempDir.delete();
			if (print != null)
				print.close();

			AonServletUtils.releaseFacesContext();
		}
	}
*/
	// ------------------------------------------------------------------------

	public void doCancel(HttpServletRequest t, HttpServletResponse i)
			throws IOException {
		// TODO Auto-generated method stub
	};

	@Override
	public void doImport(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		OutputStream out = null;
		PrintStream print = null;
		try {
			out = resp.getOutputStream();
			print = new PrintStream(out);

			String owner = "admin";// getRemoteUser();
			String domain = req.getServerName(); // getDomainName();
			Connection aonConn = DatabaseUtil.getConnection(domain);

			Map<String, List<Properties>> empresMap = getEmpresMap(req);
			for (String db : empresMap.keySet()) {
				try {

					Condition condition = DSL.condition(true);
					for (Properties empres : empresMap.get(db)) {
						// @formatter:off
						condition = condition.or(FNEMPRES.F20SSCOD.eq(
								empres.getProperty("sscod")).and(
								FNEMPRES.F20SSNUM.eq(empres
										.getProperty("ssnum"))));
						// @formatter:on
					}

					boolean commit = getCommit(req);
					boolean replace = getReplace(req);
					PrintListener listener = new PrintListener(print);

					Connection dsiConn = getDSIConn(db);
					// @formatter:off
					new DSI2AON(dsiConn, aonConn).setCommit(commit)
							.setReplace(replace).addListener(listener)
							.run(domain, owner, condition);
					// @formatter:on

				} catch (SQLException e) {
					throw new IOException(e);
				} catch (AonSQLException e) {
					throw new IOException(e);
				}
			}

		} catch (AonConnectionException e) {
			throw new IOException(e);
		} finally {
			if (print != null)
				print.close();
		}
	}

	@Override
	public void doListEmpress(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		OutputStream out = null;
		PrintStream print = null;
		try {

			out = resp.getOutputStream();
			print = new PrintStream(out);

			for (String db : getDBs(req)) {
				Connection conn = null;
				try {
					conn = getDSIConn(db);
					List<FnempresRecord> empress = DSIUtils.getEmpress(conn);

					// @formatter:off
					print.print('[');
					for (int i = 0; i < empress.size(); i++) {
						if (i > 0)
							print.println(',');
						FnempresRecord empres = empress.get(i);
						print.print("{");
						print.print(format("\"db\":\"%s\",", db));
						print.print(format("\"sscod\":\"%s\",",
								empres.getF20sscod()));
						print.print(format("\"ssnum\":\"%s\",",
								empres.getF20ssnum()));
						print.print(format("\"rsocial\":\"%s\",",
								empres.getF20rsocial()));
						print.print(format("\"nif\":\"%s\"",
								empres.getF20nif()));

						print.print("}");
					}
					print.print(']');
					// @formatter:on

				} catch (SQLException e) {
					throw new IOException(e);
				} finally {
					if (conn != null)
						try {
							conn.close();
						} catch (SQLException e) {
						}
				}
			}
		} finally {
			if (print != null)
				print.close(); // closes unserlying stream
		}
	}

	// ------------------------------------------------------------------------

	private boolean getCommit(HttpServletRequest req) {
		String value = req.getParameter(GET_COMMIT_PARAM);
		return StringUtils.equalsIgnoreCase(value, String.valueOf(true));
	}

	private boolean getReplace(HttpServletRequest req) {
		String value = req.getParameter(GET_REPLACE_PARAM);
		return StringUtils.equalsIgnoreCase(value, String.valueOf(true));
	}

	private String[] getDBs(HttpServletRequest req) {
		return req.getParameterValues(GET_DB_PARAM);
	}

	private Map<String, List<Properties>> getEmpresMap(HttpServletRequest req) {
		String values[] = req.getParameterValues(GET_EMPRES_PARAM);
		Map<String, List<Properties>> map = new HashMap<String, List<Properties>>();
		for (int i = 0; i < values.length; i++) {
			Properties properties = getProperties(values[i]);
			String db = properties.getProperty("db");
			List<Properties> list = map.get(db);
			if (list == null) {
				list = new ArrayList<Properties>();
				map.put(db, list);
			}
			list.add(properties);
		}
		return map;
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

	private static Connection getDSIConn(String path) throws SQLException {
		String url = format("jdbc:paradox:///%s", path);
		return DBUtils.getDsiConnection(url);
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

}
