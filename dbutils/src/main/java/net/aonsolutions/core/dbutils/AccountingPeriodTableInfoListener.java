package net.aonsolutions.core.dbutils;

import static net.aonsolutions.core.dbutils.Constants.DOMAIN_COLUMN_NAME;
import static net.aonsolutions.core.dbutils.Constants.NAME_COLUMN_NAME;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountingPeriodTableInfoListener implements TableInfoListener {

	private Integer parentDomain;
	
	public AccountingPeriodTableInfoListener(Integer parentDomain) {
		this.parentDomain = parentDomain;
	}

	@Override
	public void beforeInsert(PreparedStatement insert, ResultSet rs, TableInfo t) throws SQLException {
		boolean updateName = false;
		int nameIndex = -1;
		ColumnInfo[] insertColumns = t.getInsertColumns();
		for (int i = 0; i < insertColumns.length; i++) {
			ColumnInfo ci = insertColumns[i];
			String columnName = ci.getName(); 
			if ( NAME_COLUMN_NAME.equals(columnName) ) {
				nameIndex = i;
			} else if ( DOMAIN_COLUMN_NAME.equals(columnName) ) {
				Integer domain = (Integer) TableUtil.getObject(rs, ci);
				updateName = parentDomain.equals(domain);
			}
		}
		if ( updateName ) {
			String name = (String) TableUtil.getObject(rs, insertColumns[nameIndex]);
			insert.setObject((nameIndex + 1), name + "-" + parentDomain );
		}
	}

}
