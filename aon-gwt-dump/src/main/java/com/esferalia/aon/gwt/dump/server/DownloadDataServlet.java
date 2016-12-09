package com.esferalia.aon.gwt.dump.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.watson.error.AonCoreException;

public class DownloadDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final int BUFFER_SIZE = 4096;

	public DownloadDataServlet() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("text/html;charset=UTF-8");
		OutputStream out = resp.getOutputStream();
		Connection connection = null;
		Settings settings = null;
		DSLContext dslContext = null;
		
		try {
			connection = AonServletUtils.getConnection();
			
			settings = new Settings();
			settings.setRenderSchema(false);
			settings.setParamType(ParamType.INLINED);

			// Establish context
			dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
			
		} catch (SQLException e1) {
			throw new AonCoreException(e1.getMessage());
			
		}
		
		String idTask = req.getParameter("idTask");
		int id_task = Integer.parseInt(idTask);
		
		try{
			 String sql = "SELECT * FROM rattach WHERE id = ?";
			 PreparedStatement statement = connection.prepareStatement(sql);
			 statement.setInt(1, id_task);
			 
			 ResultSet result = statement.executeQuery();
			 
			 if (result.next()){
				 String name = result.getString("description");
				 Blob blob = result.getBlob("data");
				 InputStream is = blob.getBinaryStream();
				 int fileLength = is.available();
				 
				 ServletContext context = getServletContext();
				 String mimeType = context.getMimeType(name);
				 if (mimeType == null)
					 mimeType = "tmp/dump";
				 
				 resp.setContentType(mimeType);
				 resp.setContentLength(fileLength);
				 String headerKey = "Content-Disposition";
	             String headerValue = String.format("attachment; filename=\"%s\"", name);
	             resp.setHeader(headerKey, headerValue);
			 
	             byte[] buffer = new byte[BUFFER_SIZE];
                 int bytesRead = -1;
                 
                 while ((bytesRead = is.read(buffer)) != -1) {
                     out.write(buffer, 0, bytesRead);
                 }
                 
                 is.close();
                 out.close();
			 }else
				 resp.getWriter().println("File not found for the id: " + id_task);
			 			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			out.close();
			
			if ( connection != null )
				try {
					
					connection.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
	}
}


//byte[] attach = aonDump.dslContext.select(RATTACH.DATA)
//.from(RATTACH)
//.where((RATTACH.ID).eq(id_task))
//.fetchOne().value1();
