package com.code.aon.dbutils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.AonDataSource;

public class DatabaseUtil {
	
	private static final String SELECT_DOMAIN_ID = "SELECT id FROM domain WHERE name =?";

	public synchronized static Connection getConnection(String domain) throws AonConnectionException {
		AonDataSource ds = AonDataSource.getInstance();
		return ds.getConnection( domain );
	}
	
	public synchronized static Integer getDomain( Connection conn, String domain) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_DOMAIN_ID, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setString(1,domain);
			rs = ps.executeQuery();
			Integer domainId = null;
			while (rs.next()) {
				domainId = rs.getInt(1);
			}
			return domainId; 
		} finally {
			closeQuietly(ps);
			closeQuietly(rs);
		}
	}
	
    public synchronized static void closeQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) { 
        	// Nothing
        }
    }
	
    
    public synchronized static void closeQuietly(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
			}
		}
	}
    
    public synchronized static void closeQuietly(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
	}
    
    
    
    public static void main(String[] args) throws AonConnectionException, SQLException, IOException {
    	AonDataSource ds = AonDataSource.getInstance();
    	Connection c = ds.getConnection("mac.aonsolutions.org");
    	System.out.println( c );
    	c.close();
	} 
    
}





