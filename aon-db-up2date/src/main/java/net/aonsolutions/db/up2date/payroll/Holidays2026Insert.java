package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class Holidays2026Insert implements Update {

	public static final Holidays2026Insert HOLIDAYS2026INSERT = new Holidays2026Insert();
	
	private Holidays2026Insert() {
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
		calendar.set(Calendar.YEAR, 2026);

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			InputStreamReader holidys2026In = 
			new InputStreamReader(Holidays2026Insert.class.getResourceAsStream("holidays2026.csv"), StandardCharsets.UTF_8); 
			Scanner scanner = new Scanner(holidys2026In).useDelimiter(Pattern.compile("\\s*[,\\n]\\s*"));
			 
			do {
				scanner.nextLine();

				String holiday = scanner.next().trim();
				
				int year = scanner.nextInt();
				int month = scanner.nextInt();
				int day = scanner.nextInt();
				String description = scanner.next();
				dslContext
				.select()
				.from(HOLIDAY)
				.where(HOLIDAY.DOMAIN.eq(0))
				.and(HOLIDAY.DESCRIPTION.like(holiday))
				.fetchOptional(HOLIDAY.ID)
				.ifPresent( holidayId  -> {
				    	
				    
					calendar.set(Calendar.YEAR , year);
					calendar.set(Calendar.MONTH , month -1 );
					calendar.set(Calendar.DAY_OF_MONTH , day);
					
					Date date = new java.sql.Date(calendar.getTimeInMillis());
					

					int count = 
    					dslContext.fetchCount(
        					dslContext
        					.select()
        					.from(HOLIDAY_DETAIL)
        					.where(HOLIDAY_DETAIL.DATE.eq(date))
        					.and(HOLIDAY_DETAIL.HOLIDAY.eq(holidayId))
    					);
					if ( count == 0 ) {
        					dslContext
        					.insertInto(HOLIDAY_DETAIL)
        					.set(HOLIDAY_DETAIL.DOMAIN, 0)
        					.set(HOLIDAY_DETAIL.HOLIDAY, holidayId)
        					.set(HOLIDAY_DETAIL.DESCRIPTION, description)
        					.set(HOLIDAY_DETAIL.DATE, date)
        					.execute()
        					;
					} else {
					    //System.out.println( description + " " + date + " :-)");
					}
				})
				;
			} while ( scanner.hasNextLine() );
			
			scanner.close();
			holidys2026In.close();
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
