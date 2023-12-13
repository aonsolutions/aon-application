package net.aonsolutions.db.up2date.note;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterNoteArchiveTag implements Update {

	public static final AlterNoteArchiveTag ALTERNOTEARCHIVETAG = new AlterNoteArchiveTag();
	
	private AlterNoteArchiveTag() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean isNoteAltered = isNoteAltered(connection);
		
		if(!isNoteAltered) {
			dslContext.execute("ALTER TABLE `note` ADD `archive` tinyint(4) DEFAULT 0 COMMENT 'Indica si la nota esta o no archivada'");
			System.out.println("\tAlterNoteArchiveTag. achive CREATED");
			
			dslContext.execute("ALTER TABLE `note` ADD `tag` int(11) DEFAULT NULL COMMENT 'Identificador de la etiqueta'");
			dslContext.execute("ALTER TABLE `note` ADD KEY `IDX_NOTE_TAG` (`tag`)");
			dslContext.execute("ALTER TABLE `note` ADD CONSTRAINT FK_NOTE_TAG FOREIGN KEY (tag) REFERENCES `tag`(id)");
			System.out.println("\tAlterNoteArchiveTag. tag CREATED");
		} else {
			System.out.println("\tAlterNoteArchiveTag. achive EXISTS!");
			System.out.println("\tAlterNoteArchiveTag. tag EXISTS!");
		}
	}
	
	private boolean isNoteAltered(Connection connection) {
		boolean updateNote = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from note limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("archive".equals(name)) {
					if (rsmd.getPrecision(i) != 64) {
						updateNote = true;
					};	
				}				
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (stmt != null)
				try { stmt.close(); } 
				catch (SQLException e) {}
			if (rs != null)
				try { rs.close(); } 
				catch (SQLException e) {}
		}
		
		return updateNote;
	}

}
