package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;

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

import com.esferalia.aon.jooq.tables.Holiday;
import com.esferalia.aon.jooq.tables.records.HolidayDetailRecord;

import net.aonsolutions.db.up2date.Update;

public class Holidays2021Insert implements Update {

	public static final Holidays2021Insert HOLIDAYS2021INSERT = new Holidays2021Insert();
	
	private Holidays2021Insert() {
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
		calendar.set(Calendar.YEAR, 2021);

		Date _2021StartDate = new Date(calendar.getTimeInMillis());
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(HOLIDAY_DETAIL.ID)
		.from(HOLIDAY_DETAIL)
		.where(HOLIDAY_DETAIL.DOMAIN.eq(0))
		.and(HOLIDAY_DETAIL.DATE.ge(_2021StartDate))
		) >= 1;
		
		
		if ( upgraded )
			return;

		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			InputStreamReader holidys2021In = 
			new InputStreamReader(Holidays2021Insert.class.getResourceAsStream("holidays2021.csv"), Charset.forName("utf-8")); 
			Scanner scanner = new Scanner(holidys2021In).useDelimiter(Pattern.compile("\\s*[,\\n]\\s*"));
			 
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
				.ifPresent(( holidayId ) -> {

					calendar.set(Calendar.YEAR , year);
					calendar.set(Calendar.MONTH , month -1 );
					calendar.set(Calendar.DAY_OF_MONTH , day);
					
					dslContext
					.insertInto(HOLIDAY_DETAIL)
					.set(HOLIDAY_DETAIL.DOMAIN, 0)
					.set(HOLIDAY_DETAIL.HOLIDAY, holidayId)
					.set(HOLIDAY_DETAIL.DESCRIPTION, description)
					.set(HOLIDAY_DETAIL.DATE, new java.sql.Date(calendar.getTimeInMillis()))
					.execute()
					;
				})
				;
			} while ( scanner.hasNextLine() );
			
			scanner.close();
			holidys2021In.close();
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
