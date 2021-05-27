package net.aonsolutions.core.dbutils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;

public class DatabaseUtil {
	
	private static final String SELECT_DOMAIN_ID = "SELECT id FROM domain WHERE name =?";

	public static Connection getConnection(String domain) throws AonConnectionException {
		AonDataSource ds = AonDataSource.getInstance();
		return ds.getConnection( domain );
	}
	
	public static Integer getDomain( Connection conn, String domain) throws SQLException {
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
	
    public static void closeQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) { 
        	// Nothing
        }
    }
	
    
    public static void closeQuietly(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
			}
		}
	}
    
    public static void closeQuietly(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
	}
    
    
    public static void main(String[] args) throws AonConnectionException, SQLException, IOException {
    	AonDataSource ds = AonDataSource.getInstance();
    	Connection c1 = ds.getConnection("mac.esferalia.net");
    	Connection c2 = ds.getConnection("mac.esferalia.net");
    	Connection c3 = ds.getConnection("mac.esferalia.net");
    	Connection c4 = ds.getConnection("mac.esferalia.net");
    	Connection c5 = ds.getConnection("mac.esferalia.net");
    	Connection c6 = ds.getConnection("mac.esferalia.net");
    	Connection c7 = ds.getConnection("mac.esferalia.net");
    	Connection c8 = ds.getConnection("mac.esferalia.net");
    	Connection c9 = ds.getConnection("mac.esferalia.net");
    	Connection c10 = ds.getConnection("mac.esferalia.net");
    	c1.close();
    	c2.close();
    	c3.close();
    	c4.close();
    	c5.close();
    	c6.close();
    	c7.close();
    	c8.close();
    	c9.close();
    	c10.close();
	} 
    
}





