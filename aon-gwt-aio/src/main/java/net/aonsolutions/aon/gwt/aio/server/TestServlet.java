package net.aonsolutions.aon.gwt.aio.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
//import com.code.aon.ui.audit.session.JSFStartupUtil;
import com.code.aon.ui.util.AonUtil;

import net.aonsolutions.core.dbutils.DatabaseUtil;

@WebServlet(name = "TestServlet", urlPatterns = {"/testServlet"})
public class TestServlet extends HttpServlet {
	
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestServlet.class.getName());
	private static final String SELECT_DOMAIN_ID = "SELECT id FROM domain WHERE name =?";	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Integer domainId = null;
		Date start = new Date();
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String host = null;
		try {
			host = req.getParameter("aon.domain");
			if (host == null || "".equals(host)){
				host = AonUtil.getServerName(req);
			}
			c = DatabaseUtil.getConnection(host);
			ps = c.prepareStatement(SELECT_DOMAIN_ID, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setString(1,host);
			rs = ps.executeQuery();
			while (rs.next()) {
				domainId = rs.getInt(1);
			}
			
//			EmployeesServiceImpl employeesServiceImpl = new EmployeesServiceImpl();
//			Agreement agreement = new Agreement();
//			agreement.setId(1165);
//			agreement.setDomain(0);
//			for ( Date date : employeesServiceImpl.getChanges(host, agreement) )
//				System.out.println(date);
			
			resp.sendError(HttpServletResponse.SC_OK);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(),e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(c);
		}
		long duration = new Date().getTime() - start.getTime();
		String message = "TestServlet: ("+domainId+") response time : " + duration + "Ms.";
		if ( duration > 50 ) {
			LOGGER.info(message);
		} else {
			LOGGER.debug(message);
		}			
	}


}
