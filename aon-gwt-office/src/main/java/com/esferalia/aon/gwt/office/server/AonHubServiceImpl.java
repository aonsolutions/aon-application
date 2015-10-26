package com.esferalia.aon.gwt.office.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;

import java.sql.Connection;
import java.sql.SQLException;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.office.client.AonHubService;
import com.esferalia.aon.gwt.office.jooq.JooqNotices;

@SuppressWarnings("serial")
public class AonHubServiceImpl extends AonRemoteServiceServlet implements
		AonHubService {

	@Override
	public String getIssues() throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
			return JooqNotices.getIssues(conn, getParentDomainID(),
					getDomainID());
		} catch (SQLException ex) {
			throw new IllegalArgumentException();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			releaseFacesContext();
		}
	}
}
