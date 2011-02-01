package com.code.aon.common.dao.sql;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AbstractFieldMapper;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.jndi.ServiceLocator;
import com.code.aon.common.jndi.ServiceLocatorException;

/**
 * This abstract class access to the <code>DataSource<code>. Only this class can access.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-feb-2005
 * @since 1.0
 *
 */
public abstract class AbstractDAO extends AbstractFieldMapper {

    /**
     * Log messages prefix.
     */
    public static final String LOG_PRFX = " SQL Debug: "; //$NON-NLS-1$

    /**
     * Transfer Objects constructor from a </code>ResultSet<code>
     * 
     * @author Consulting & Development. Eugenio Castellano - 07-feb-2005
     * @since 1.0
     *  
     */
    protected static interface TOFactory {
        /**
         * Return a valid "Transfer Object" instance.
         * 
         * @param rs
         * @return "Transfer Object" instance.
         * @throws SQLException
         */
    	ITransferObject newTO(ResultSet rs) throws SQLException;
    }
    
    /**
     * Return a collection of Transfer Objects retrieved from a SQL statement.
     * 
     * @param stmt
     * @param toFactory
     * @return a collection of Transfer Objects.
     * @throws DAOException
     */
    protected List<ITransferObject> getList(String stmt, TOFactory toFactory)
            throws DAOException {
        ResultSet rs = null;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getDataSource().getConnection();
            debug(stmt);
            ps = con.prepareStatement(stmt, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);
            rs = ps.executeQuery();
            List<ITransferObject> list = new LinkedList<ITransferObject>();
            while (rs.next()) {
                list.add(toFactory.newTO(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        } finally {
            closeAll(con, ps, rs);
        }
    }

    /**
     * Return the rows number.
     * 
     * @param stmt
     * @return the rows number.
     * @throws DAOException
     */
    protected int getCount(String stmt) throws DAOException {
    	ResultSet rs = null;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getDataSource().getConnection();
            debug(stmt);
            ps = con.prepareStatement(stmt, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0; 
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        } finally {
            closeAll(con, ps, rs);
        }
   	}

    /**
     * Execute a prepared SQL statement.
     * 
     * @param stmt
     * @param hostVariables
     * @param types
     * @return int
     * @throws DAOException
     */
    public int execute(String stmt, Object[] hostVariables, int[] types)
            throws DAOException {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            return execute (stmt, hostVariables, types, con);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        } finally {
            closeAll(con, null, null);
        }
    }

    /**
     * Execute a prepared SQL statement.
     * 
     * @param stmt
     * @param hostVariables
     * @param types
     * @param con
     * @return int
     * @throws DAOException
     */
    public int execute(String stmt, Object[] hostVariables, int[] types, Connection con)
            throws DAOException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(stmt);
            debug(stmt);
            for (int i = 0; i < hostVariables.length; i++) {
                debug("\t" + (i+1) + ".-\t[" + hostVariables[i] + "]"); //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
                if ( hostVariables[i] == null ){
                    ps.setNull((i+1), types[i]);
                } else {
                    ps.setObject((i+1), hostVariables[i], types[i]);    
                }
                
            }
            int i = ps.executeUpdate();
            return i;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        } finally {
            closeAll(null, ps, null);
        }
    }

    /**
     * Execute a delete SQL statement.
     * 
     * @param sql
     * @param pk
     * @return true if something was removed.
     * @throws DAOException
     */
    public boolean remove(String sql, Serializable pk) throws DAOException {
        Connection con = null;
        boolean removed = false;
        try {
            con = getDataSource().getConnection();
            removed = remove(sql, pk, con);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        } finally {
            closeAll(con, null, null);
        }
        return removed;
    }

    /**
     * Execute a delete SQL statement.
     * 
     * @param sql
     * @param pk
     * @param con
     * @return true if something was removed.
     * @throws DAOException
     */
    public boolean remove(String sql, Serializable pk, Connection con) 
    	throws DAOException {
    	boolean removed = false;
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(sql);
            stmt.setObject( 1, pk );
            debug(sql);
            debug("\t1.- [" + pk.toString() + "]"); //$NON-NLS-2$ //$NON-NLS-1$
            removed = (stmt.executeUpdate() > 0);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        } finally {
            closeAll(null, stmt, null);
        }
        return removed;
    }

    /**
     * Close the given objects.
     * 
     * @param con
     * @param stmt
     * @param rs
     */
    protected void closeAll(Connection con, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) { // $codepro.audit.disable emptyCatchClause
            // Nothing
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) { // $codepro.audit.disable emptyCatchClause
            // Nothing
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) { // $codepro.audit.disable emptyCatchClause
            // Nothing
        }
    }

    /**
     * Logs defined message by its level.
     * 
     * @param message
     */
    private void debug(String message) {
    	Logger log = LoggerFactory.getLogger(this.getClass());
        log.debug( LOG_PRFX + message);
    }

    /**
     * Return the <code>DataSource</code>
     * 
     * @return The DataSource.
     * @throws DAOException
     */
    protected DataSource getDataSource() throws DAOException {
        try {
            ServiceLocator sl = ServiceLocator.getInstance();
            DataSource ds = sl.getDataSource(getDataSourceName());
            if (ds == null) {
                throw new DAOException("DataSource " + getDataSourceName() + " not found!");     //$NON-NLS-2$ //$NON-NLS-1$
            }
            return ds;
        } catch (ServiceLocatorException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
    
    /**
     * Return the column value.
     * 
     * @param rs
     * @param idx
     * @return the column value as String.
     * @throws SQLException
     */
    public static String getString(ResultSet rs, int idx) throws SQLException {
        String attr = rs.getString(idx);
        return attr==null?null:attr.trim();
    }
   
    /**
     * Transform to a SQL date.
     * 
     * @param date
     * @return The java.sql.Date. 
     */
    protected java.sql.Date toSqlDate(Date date){
        return (date==null)?null:new java.sql.Date( date.getTime() );
    }

    /**
     * Return the <code>DataSource</code>
     * 
     * @return the <code>DataSource</code>.
     */
    protected abstract String getDataSourceName();


}