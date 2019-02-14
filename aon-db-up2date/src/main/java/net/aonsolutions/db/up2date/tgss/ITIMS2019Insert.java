package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Cnae2009Rate.CNAE2009_RATE;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.InsertSetStep;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Cnae2009;
import com.esferalia.aon.jooq.tables.Cnae2009Rate;
import com.esferalia.aon.jooq.tables.Holiday;
import com.esferalia.aon.jooq.tables.SystemData;
import com.esferalia.aon.jooq.tables.records.HolidayDetailRecord;

import net.aonsolutions.db.up2date.Update;

public class ITIMS2019Insert implements Update {

	public static final ITIMS2019Insert ITIMS2019INSERT = new ITIMS2019Insert();
	
	private static final String OCUPACION_IT = "OCUPACION_IT";
	private static final String OCUPACION_IMS = "OCUPACION_IMS";
	
	private ITIMS2019Insert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2019);

		Date _2019StartDate = new Date(calendar.getTimeInMillis());
		
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date _2018EndDate = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(CNAE2009_RATE.ID)
		.from(CNAE2009_RATE)
		.where(CNAE2009_RATE.START_DATE.eq(_2019StartDate))
		) >= 1;
		
		
		if ( upgraded )
			return;

		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			InputStreamReader cnae2009RateIn = 
			new InputStreamReader(ITIMS2019Insert.class.getResourceAsStream("cnae2009_rate.csv"), Charset.forName("utf-8")); 
			Scanner scanner = new Scanner(cnae2009RateIn).useDelimiter(Pattern.compile("\\s*[\\|\\n]\\s*"));
			do {
				scanner.nextLine();

				String cnae2009 = scanner.next();
				cnae2009 = cnae2009.trim();
				
				String title = scanner.next();

				double it = scanner.nextDouble();
				double ims = scanner.nextDouble();
				double total = scanner.nextDouble();
				
				
				try {
				Integer cnae2009Id =
				dslContext
				.select()
				.from(CNAE2009)
				.where(CNAE2009.CODE.eq(cnae2009))
				.fetchOptional(CNAE2009.ID)
				.orElseGet(() -> 					
					dslContext
					.select()
					.from(CNAE2009)
					.where(CNAE2009.TITLE.eq(title))
					.fetchOptional(CNAE2009.ID)
					.orElseThrow( () -> new NoSuchElementException() ) 
				 );

				dslContext
				.update(CNAE2009_RATE)
				.set(CNAE2009_RATE.END_DATE,_2018EndDate)
				.where(CNAE2009_RATE.CNAE2009.eq(cnae2009Id))
				.and(CNAE2009_RATE.END_DATE.isNull())
				.execute()
				;
				
				dslContext
				.insertInto(CNAE2009_RATE)
				.set(CNAE2009_RATE.CNAE2009, cnae2009Id)
				.set(CNAE2009_RATE.START_DATE,_2019StartDate)
				.set(CNAE2009_RATE.IT_AMOUNT, it)
				.set(CNAE2009_RATE.IMS_AMOUNT, ims)
				.execute()
				;


				} catch ( NoSuchElementException e ) {
					//System.out.printf("\r\nError: %s %s, no found", cnae2009, title );
				} 
				
				;
			} while ( scanner.hasNextLine() );
			scanner.close();
			cnae2009RateIn.close();
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.END_DATE, _2018EndDate)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.in(OCUPACION_IT, OCUPACION_IMS))
			.and(SYSTEM_DATA.END_DATE.isNull())
			.execute()
			;
			
			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, OCUPACION_IT)
			.set(SYSTEM_DATA.START_DATE, _2019StartDate)
			.set(SYSTEM_DATA.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_DATA.EXPRESSION, "[\"a\": 0.80, \"b\": 1.00, \"d\": 3.35, \"e\": 1.80, \"f\": 3.35, \"g\": 2.10, \"h\": 1.40]" )
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, OCUPACION_IMS)
			.set(SYSTEM_DATA.START_DATE, _2019StartDate)
			.set(SYSTEM_DATA.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_DATA.EXPRESSION, "[\"a\": 0.70, \"b\": 1.00, \"d\": 3.35, \"e\": 1.50, \"f\": 3.35, \"g\": 1.50, \"h\": 2.20]" )
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
