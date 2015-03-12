package com.code.aon.google.apis.servlet;

import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.JooqSettings;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;

public class GmailServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String registry = req.getParameter("registry");
        String email = req.getParameter("email");
        String domain = req.getParameter("domain");
        String type = req.getParameter("type");
        Integer reg = Integer.parseInt(registry);
        
        if(type.equals("baja")){
        	try {
				updateRmedia(domain, reg, email);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	RequestDispatcher dispatcher = getServletContext()
    				.getRequestDispatcher("/login/baja.jsp");
    			req.setAttribute("email", req.getParameter("email"));
    			req.setAttribute("type", req.getParameter("type"));
    			req.setAttribute("logo","http://"+domain+"/aon-aio/aonDocuments/company.logo");
    			req.setAttribute("css", "http://"+domain + "/aon-aio/aonResource/com/code/aon/ui/resources/facelet/login/css/login-aon.css");
    			req.setAttribute("favicon","http://"+domain + "/aon-aio/aonResource/8.18-SNAPSHOT/images/favicon.ico");
    			dispatcher.forward(req, resp);
        }
        else{
        	try {
				updateRAddInfo(domain,reg,type);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/login/emailchange.jsp");
			req.setAttribute("email", req.getParameter("email"));
			req.setAttribute("type", req.getParameter("type"));
			req.setAttribute("logo","http://"+domain+"/aon-aio/aonDocuments/company.logo");
			req.setAttribute("css", "http://"+domain + "/aon-aio/aonResource/com/code/aon/ui/resources/facelet/login/css/login-aon.css");
			req.setAttribute("favicon","http://"+domain + "/aon-aio/aonResource/8.18-SNAPSHOT/images/favicon.ico");

			dispatcher.forward(req, resp);
        }    
	}
	
	public static void updateRAddInfo(String domain, Integer registry, String email) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1<Integer>> data = dslContext.select(RADDINFO.DOMAIN).from(RADDINFO).where(RADDINFO.REGISTRY.eq(registry)).and(RADDINFO.ATTRIBUTE.eq("GOOGLEMAIL")).fetch();
			if(data.isEmpty()){
				Domain d = DBConsults.getDomain(domain);
				dslContext.insertInto(RADDINFO, RADDINFO.DOMAIN, RADDINFO.REGISTRY, RADDINFO.ATTRIBUTE, RADDINFO.VALUE, RADDINFO.VALUE_DATE)
						.values(d.getId(),registry,"GOOGLEMAIL",email, new Date(new java.util.Date().getTime())).execute();
			}
			else dslContext.update(RADDINFO)
						.set(RADDINFO.VALUE,email)
						.where(RADDINFO.REGISTRY.eq(registry))
							.and(RADDINFO.ATTRIBUTE.eq("GOOGLEMAIL"))
						.execute();
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void updateRmedia(String domain,Integer registry, String email) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			dslContext.update(RMEDIA)
			.set(RMEDIA.COMMERCIAL,(byte)0)
			.where(RMEDIA.REGISTRY.eq(registry))
				.and(RMEDIA.MEDIA.eq((byte) 4))
				.and(RMEDIA.VALUE.eq(email))
			.execute();
			
			dslContext.delete(RADDINFO).where(RADDINFO.REGISTRY.eq(registry)).and(RADDINFO.ATTRIBUTE.eq("GOOGLEMAIL")).execute();

			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
}
