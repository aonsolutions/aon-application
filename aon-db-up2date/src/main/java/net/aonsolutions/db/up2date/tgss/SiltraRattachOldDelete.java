package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class SiltraRattachOldDelete implements Update {


	public static final SiltraRattachOldDelete SILTRARATTACHOLDDELETE = new SiltraRattachOldDelete();
	
	private static final Byte SILTRA_TYPES [] = {
		(byte) 20, //CRETA_RESPUESTA,
		(byte) 21, //CRETA_TRABAJADORES_Y_TRAMOS
		(byte) 23 // CRETA_BASES
	};
	
	private SiltraRattachOldDelete() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		
		calendar.add(Calendar.MONTH, -6);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		Date endLineDate = new Date(calendar.getTimeInMillis());


		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			try {
				dslContext
	//			.createIndex("IDX_ATTACH_DATE")
	//			.on(RATTACH, RATTACH.ATTACH_DATE)
				.execute("CREATE INDEX `IDX_ATTACH_DATE_4_DELETE` ON `rattach` (`attach_date`)");
			} catch ( DataAccessException e ) {
				System.err.println(e.getMessage());
			}

			dslContext
			.delete(RATTACH)
			.where(RATTACH.TYPE.in( SILTRA_TYPES ))
			.and(RATTACH.ATTACH_DATE.lessThan(endLineDate))
			.execute();
			
			dslContext
//			.dropIndex("IDX_ATTACH_DATE")
//			.on(RATTACH)
			.execute("DROP INDEX `IDX_ATTACH_DATE_4_DELETE` ON `rattach`");

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

}
