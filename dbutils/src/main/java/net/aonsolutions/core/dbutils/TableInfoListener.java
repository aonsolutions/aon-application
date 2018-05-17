package net.aonsolutions.core.dbutils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TableInfoListener {

	void beforeInsert( PreparedStatement insert,ResultSet rs, TableInfo t ) throws SQLException;
	
}