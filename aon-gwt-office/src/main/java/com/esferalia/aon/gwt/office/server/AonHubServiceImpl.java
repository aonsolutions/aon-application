package com.esferalia.aon.gwt.office.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.esferalia.aon.gwt.office.client.AonHubService;
import com.esferalia.aon.gwt.office.shared.Notice;

@SuppressWarnings("serial")
public class AonHubServiceImpl extends AonRemoteServiceServlet implements
		AonHubService {

	@Override
	public List<Notice> getNotices() throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			
			return null;
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException log) {
					log.printStackTrace();
				}
			}
			
		}
		
	}
	
	

}
