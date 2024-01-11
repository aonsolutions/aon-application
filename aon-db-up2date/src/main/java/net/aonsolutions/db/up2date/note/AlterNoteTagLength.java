package net.aonsolutions.db.up2date.note;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterNoteTagLength implements Update {

	public static final AlterNoteTagLength ALTERNOTETAGLENGTH = new AlterNoteTagLength();
	
	private AlterNoteTagLength() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// Alter note_tad length
		try {
			dslContext.execute("ALTER TABLE `note` MODIFY COLUMN `note_tag` varchar(17) DEFAULT NULL COMMENT 'Nombre de la etiqueta'");
			System.out.println("\tAlterNoteTagLength. Change length Note.note_tag column");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\tAlterNoteTagLength. Already changed length Note.note_tag column");
		}
		
	}

}
