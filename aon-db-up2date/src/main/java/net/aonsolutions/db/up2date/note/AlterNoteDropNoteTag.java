package net.aonsolutions.db.up2date.note;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterNoteDropNoteTag implements Update {

	public static final AlterNoteDropNoteTag ALTERNOTEDROPNOTETAG = new AlterNoteDropNoteTag();
	
	private AlterNoteDropNoteTag() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// Drop tag column from note table
		try {
			dslContext.execute("ALTER TABLE note DROP FOREIGN KEY `FK_NOTE_TAG`");
			System.out.println("\tAlterNoteDropNoteTag. Drop FK_NOTE_TAG");
		} catch (Exception e) {
			System.out.println("\tAlterNoteDropNoteTag. Already droped FK_NOTE_TAG");
		}
		
		try {
			dslContext.execute("ALTER TABLE note DROP COLUMN tag");
			System.out.println("\tAlterNoteDropNoteTag. Drop Note.tag column");
		} catch (Exception e) {
			System.out.println("\tAlterNoteDropNoteTag. Already droped Note.tag column");
		}
		
		// Add archive column
		try {
			dslContext.execute("ALTER TABLE `note` ADD `archive` tinyint(1) DEFAULT 0 COMMENT 'Indica si la nota esta o no archivada'");
			System.out.println("\tAlterNoteDropNoteTag. Add Note.archive column");
		} catch (Exception e) {
			System.out.println("\tAlterNoteDropNoteTag. Already added Note.archive column");
		}
		
		// Add pin_up column
		try {
			dslContext.execute("ALTER TABLE `note` ADD `pin_up` tinyint(1) DEFAULT 0 COMMENT 'Indica si la nota esta o no fijada'");
			System.out.println("\tAlterNoteDropNoteTag. Add Note.pin_up column");
		} catch (Exception e) {
			System.out.println("\tAlterNoteDropNoteTag. Already added Note.pin_up column");
		}
		
		// Add note_tag column
		try {
			dslContext.execute("ALTER TABLE `note` ADD `note_tag` varchar(11) DEFAULT NULL COMMENT 'Nombre de la etiqueta'");
			System.out.println("\tAlterNoteDropNoteTag. Add Note.note_tag column");
		} catch (Exception e) {
			System.out.println("\tAlterNoteDropNoteTag. Already added Note.note_tag column");
		}
		
		// Add color column
		try {
			dslContext.execute("ALTER TABLE `note` ADD `color` varchar(8) DEFAULT NULL COMMENT 'Color de la nota'");
			System.out.println("\tAlterNoteDropNoteTag. Add Note.color column");
		} catch (Exception e) {
			System.out.println("\tAlterNoteDropNoteTag. Already added Note.color column");
		}
		
		// Add archive_date column
		try {
			dslContext.execute("ALTER TABLE `note` ADD `archive_date` datetime DEFAULT NULL COMMENT 'Fecha de archivacion'");
			System.out.println("\tAlterNoteDropNoteTag. Add Note.archive_date column");
		} catch (Exception e) {
			System.out.println("\tAlterNoteDropNoteTag. Already added Note.archive_date column");
		}
		
		// Add creation_date column
		try {
			dslContext.execute("ALTER TABLE `note` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion'");
			System.out.println("\tAlterNoteDropNoteTag. Add Note.creation_date column");
		} catch (Exception e) {
			System.out.println("\tAlterNoteDropNoteTag. Already added Note.creation_date column");
		}
		
		// Add modification_date column
		try {
			dslContext.execute("ALTER TABLE `note` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion'");
			System.out.println("\tAlterNoteDropNoteTag. Add Note.modification_date column");
		} catch (Exception e) {
			System.out.println("\tAlterNoteDropNoteTag. Already added Note.modification_date column");
		}
	}

}
