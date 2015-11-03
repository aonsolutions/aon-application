package com.esferalia.aon.gwt.office.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.groupware.enumeration.NoticeStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.Priority;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.office.jooq.JooqAonHub;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "New Notice Servlet", urlPatterns = { "/aon_gwt_office/NewNoticeServlet" })
public class NewNoticeServlet extends HttpServlet {
	
	static class JooqSave extends JooqAonHub {
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {	
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			saveIssue(req, resp);
		} catch (Exception ex) {
			throw new ServletException();
		} 
	}
	
	private void saveIssue (HttpServletRequest req, HttpServletResponse resp) {
		
		PrintStream os = null;
		Connection conn = null;
		
		try {
			resp.setContentType("application/json;charset=UTF-8");
			conn = AonServletUtils.getConnection();
			Integer domain = AonServletUtils.getRequestDomain(req);
			String buffer = doJson(req);
			
			JSONObject json = new JSONObject(buffer.toString());
			String title = json.getString("title");
			String body = json.getString("body");
			Integer type = NoticeType.valueOf(json.getString("type").toUpperCase()).ordinal();
			Integer priority = Priority.valueOf(json.getString("priority").toUpperCase()).ordinal();
			Integer status = NoticeStatus.valueOf(json.getString("state").toUpperCase()).ordinal();
			
		} catch (IOException ex) {
			System.out.println(ex.getMessage() + " " + ex.getLocalizedMessage());
		} catch (Exception ex) {
			System.out.println(ex.getMessage() + " " + ex.getLocalizedMessage());
		} finally {
			
		}
	}
	
	private String doJson (HttpServletRequest req) throws IOException, Exception {
		StringBuffer buffer = new StringBuffer();
		String line = null;
		BufferedReader reader = req.getReader();
		
		while ((line = reader.readLine()) != null)
			buffer.append(line);			
		
		return buffer.toString();
		
	}

}
