package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.server.OpenDocumentConverterServlet.NoSuchDocumentException;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;

@WebServlet(name = "Enterprise-Logo", urlPatterns = { "/aon_gwt_payroll/reports/*" })
public class EnterpriseLogoServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String paramName = "contractid";
		String paramValue = req.getParameter(paramName);
		int contractId = Integer.parseInt(paramValue);
		
		String requestURI = req.getRequestURI();
		String ext = AonServletUtils.getExtn(requestURI);
		MimeType mimetype = MimeType.getByExtension(ext);
		
		try {
			Blob rattachData = getRAttachLogo(req, contractId);
			resp.setContentType(mimetype.getName());
			OutputStream os = resp.getOutputStream();
			String blank_image = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9/KKKKAP/2Q==";
			
			if(rattachData == null)
				os.write(blank_image.getBytes());
			else
				os.write(rattachData.getBytes(1, (int) rattachData.length()));
			
			os.flush();
			
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	private Blob getRAttachLogo(HttpServletRequest req, int contractId) throws SQLException, NoSuchDocumentException {
		Connection conn = null;
		String domain = req.getServerName();
		
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			
			stmt = conn.prepareStatement("SELECT * FROM " + SQLConstants.RATTACH + " WHERE " + RattachColumns.TYPE + "=0 AND " +RattachColumns.REGISTRY
					+ " IN ( SELECT " + WorkplaceColumns.ENTERPRISE + " FROM " + SQLConstants.WORKPLACE + " WHERE " + WorkplaceColumns.ID
					+ " IN ( SELECT " + ContractColumns.WORKPLACE + " FROM " + SQLConstants.CONTRACT + " WHERE " + ContractColumns.ID + "=?))");
			stmt.setInt(1, contractId);

			rs = stmt.executeQuery();

			if (!rs.next()) {
				throw new OpenDocumentConverterServlet.NoSuchDocumentException(
						contractId);
			}
			
			Blob blob = rs.getBlob(RattachColumns.DATA);
			return blob;

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

	}
}
