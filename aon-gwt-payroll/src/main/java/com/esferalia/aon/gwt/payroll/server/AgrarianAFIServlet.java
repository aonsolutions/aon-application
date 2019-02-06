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
import javax.sql.rowset.serial.SerialBlob;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.server.OpenDocumentConverterServlet.NoSuchDocumentException;
import com.esferalia.aon.gwt.payroll.shared.AgrarianAFIGeneration;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.google.gwt.dev.json.JsonObject;

@WebServlet(name = "Agrarian-AFI", urlPatterns = { "/aon_gwt_payroll/reports/agrarian_afi" })
public class AgrarianAFIServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		JsonObject agrarianJSON = null;
		String agrarianAFI = AgrarianAFIGeneration.generateAgrarianAFI(agrarianJSON);
		
		OutputStream os = resp.getOutputStream();
		if(agrarianAFI == null)
			os.write("Fallo al crear el archivo".getBytes());
		else
			os.write(agrarianAFI.getBytes());
		
		os.flush();
	}

}
