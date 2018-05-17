package net.aonsolutions.core.dbutils;

import static net.aonsolutions.core.dbutils.Constants.CODE_COLUMN_NAME;
import static net.aonsolutions.core.dbutils.Constants.DOMAIN_COLUMN_NAME;
import static net.aonsolutions.core.dbutils.Constants.HOTEL_TABLE_NAME;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotelTableInfoListener implements TableInfoListener {

	private final static Logger LOGGER = LoggerFactory.getLogger(HotelTableInfoListener.class);
	
	private boolean isValidCode(Connection connection, String code) {
		String sentence = "SELECT COUNT(*) FROM " + HOTEL_TABLE_NAME + " WHERE " + CODE_COLUMN_NAME  + " = ?"; 
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sentence);
			ps.setString(1, code);
			rs = ps.executeQuery();
			if ( rs.next() ) {
				Integer count = rs.getInt(1);
				return count == 0;
			}
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps);
		}
		return true;
	}
	
	private String getNewCode( Connection connection, String code, Integer domain ) {
		String newCode = StringUtils.substring(code + "-" + domain, 0, 16);
		if ( isValidCode(connection, newCode) ) {
			return newCode;
		}
		newCode = RandomStringUtils.randomNumeric(16);
		while (! isValidCode(connection, newCode) ) {
			newCode = RandomStringUtils.randomNumeric(16);
		}
		return newCode;
	}
	
	@Override
	public void beforeInsert(PreparedStatement insert, ResultSet rs, TableInfo t) throws SQLException {
		int nameIndex = -1;
		String code = null;
		Integer domain = null;
		ColumnInfo[] insertColumns = t.getInsertColumns();
		for (int i = 0; i < insertColumns.length; i++) {
			ColumnInfo ci = insertColumns[i];
			String columnName = ci.getName(); 
			if ( CODE_COLUMN_NAME.equals(columnName) ) {
				code = (String) TableUtil.getObject(rs, ci);
				nameIndex = i;
			} else if ( DOMAIN_COLUMN_NAME.equals(columnName) ) {
				domain = (Integer) TableUtil.getObject(rs, ci);
			}
		}
		String newCode = getNewCode(insert.getConnection(), code, domain);
		insert.setObject((nameIndex + 1), newCode );
	}

}
