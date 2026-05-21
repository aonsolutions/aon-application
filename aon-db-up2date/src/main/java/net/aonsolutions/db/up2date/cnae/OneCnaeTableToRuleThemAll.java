package net.aonsolutions.db.up2date.cnae;

import static com.esferalia.aon.jooq.tables.Cnae.CNAE;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.tools.StringUtils;
import org.jooq.tools.csv.CSVReader;

import com.esferalia.aon.jooq.tables.Cnae;

import net.aonsolutions.db.up2date.Update;

public class OneCnaeTableToRuleThemAll implements Update {


	public static final OneCnaeTableToRuleThemAll ONECNAETABLETORULETHEMALL = new OneCnaeTableToRuleThemAll();

	private static Cnae CNAE2025 = CNAE.as("cnae2025");

	private static final Field<String> CNAE2009_CODE = DSL.field(DSL.name("cnae2009_code"), SQLDataType.VARCHAR(5).nullable(true), DSL.comment("CNAE-2009 C\u00f3digo")); 
		private static final Field<String> CNAE2009_TITLE = DSL.field(DSL.name("cnae2009_title"), SQLDataType.VARCHAR(255).nullable(true), DSL.comment("CNAE-2009 T\u00edtulo")); 
	
	private OneCnaeTableToRuleThemAll() {
		
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		
		for ( Field<?> field : new Field[] {CNAE2009_CODE, CNAE2009_TITLE} ) {
	        dslContext.select()
	        .from("information_schema.columns")
	        .where(DSL.field("table_schema").eq(DSL.currentSchema()))
	        .and(DSL.field("table_name").eq(DSL.inline(CNAE.getName())))
	        .and(DSL.field("column_name").eq(DSL.inline(field.getName())))
	        .fetchOptional().ifPresentOrElse( 
	        		r -> System.out.println("Column `" + field.getName() + "` exists in table `" + CNAE.getName() + "`"), 
	        		() ->  { 
	        			dslContext.alterTable(CNAE).addColumn(field).execute();
	        			System.out.println("Column `" + field.getName() + "` added to table `" + CNAE.getName() + "`");
	        		}
	        );
		}

		dslContext.transaction( config -> {
			try ( CSVReader csvReader  = new CSVReader(new InputStreamReader(OneCnaeTableToRuleThemAll.class.getResourceAsStream("cnae09_cnae25.csv")))) {
				String[] line = csvReader.readNext();
				
				Map<String, String> cnae2009TitleMap = new HashMap<>();
				Map<String, String> cnae2025TitleMap = new HashMap<>();
				Map<String, String> cnae2009To2025Map = new HashMap<>();
				
				while ( (line = csvReader.readNext()) != null ) {

					int level = Integer.parseInt(line[4].replaceAll("\\D", "").trim());
					// Only process CNAE codes with level 4, which are the only valid ones. 
					if ( level < 4 ) 
						continue; 

					String cnae2009Title = line[1].trim();
					String cnae2025Title = line[3].trim();
					
					
					String cnae2009Code = line[0].trim().replaceAll("\\D", "");
					String cnae2025Code = line[2].trim().replaceAll("\\D", "");
					cnae2009To2025Map.put(cnae2009Code, cnae2025Code);
					cnae2009TitleMap.put(cnae2009Code, cnae2009Title);
					cnae2025TitleMap.put(cnae2025Code, cnae2025Title);
					
					config.dsl()
						.select()
						.from(CNAE)
						.where(CNAE2009_CODE.isNull())
						.and(CNAE.CODE.eq(cnae2025Code))
						.fetchOptional(CNAE.ID)
						.ifPresentOrElse( 
							cnaeId ->
								// CNAE 2025 and 2009 are the same, just update the code and title
								config.dsl()
								.update(CNAE)
								.set(CNAE.CODE, cnae2025Code)
								.set(CNAE.TITLE, cnae2025Title)
								.set(CNAE2009_CODE, cnae2009Code)
								.set(CNAE2009_TITLE, cnae2009Title)
								.where(CNAE.ID.eq(cnaeId))
								.execute(),
							() -> {
									// CNAE 2025 is new, insert it with the 2009 code and title
									if ( cnae2009Code.equals(cnae2025Code) ) { 
										// If the CNAE codes are the same, we can just insert the new code and title, and
										// overwrite duplicates ( in case the code already exists with a different CNAE 2009 code ).
										config.dsl()
										.insertInto(CNAE)
										.set(CNAE.ID, Integer.parseInt(cnae2025Code))
										.set(CNAE.CODE, cnae2025Code)
										.set(CNAE.TITLE, cnae2009Title)
										.set(CNAE2009_CODE, cnae2009Code)
										.set(CNAE2009_TITLE, cnae2009Title)
										.onDuplicateKeyUpdate()
										.set(CNAE.CODE, cnae2025Code)
										.set(CNAE.TITLE, cnae2009Title)
										.set(CNAE2009_CODE, cnae2009Code)
										.set(CNAE2009_TITLE, cnae2009Title)
										.execute();
									} else {
										// If the CNAE codes are different, we need to insert the new code and title, 
										// but we don't want to update existing codes that match the CNAE 2025
										// code but have a different CNAE 2009 code
										config.dsl()
										.insertInto(CNAE)
										.set(CNAE.ID, Integer.parseInt(cnae2025Code))
										.set(CNAE.CODE, cnae2025Code)
										.set(CNAE.TITLE, cnae2025Title)
										.set(CNAE2009_CODE, cnae2009Code)
										.set(CNAE2009_TITLE, cnae2009Title)
										.onDuplicateKeyIgnore()
										.returning()
										.fetchOptional().ifPresent( 
											cnaeRecord -> 
												// Update EnterpriseActivity to point to the new CNAE code if it was updated
												config.dsl()
												.update(ENTERPRISE_ACTIVITY)
												.set(ENTERPRISE_ACTIVITY.CNAE, DSL.cast(cnae2025Code, Integer.class))
												.where(ENTERPRISE_ACTIVITY.CNAE.eq(DSL.cast(cnae2009Code, Integer.class)))
												.execute()
										)
										;

									}
							}
						);		

				}
					
				
				cnae2009To2025Map.forEach( (cnae2009Code, cnae2025Code) -> {
					
					int cnae2009Id = Integer.parseInt(cnae2009Code);
					int cnae2025Id = Integer.parseInt(cnae2025Code);
					
					config.dsl()
					.select()
					.from(CNAE)
					.where(CNAE.ID.eq(cnae2009Id))
					.and(CNAE2009_CODE.isNull())
					.fetchOptional(CNAE.ID)
					.ifPresent( cnaeId -> 
						config.dsl()
						.update(ENTERPRISE_ACTIVITY)
						.set(ENTERPRISE_ACTIVITY.CNAE, cnae2025Id)
						.where(ENTERPRISE_ACTIVITY.CNAE.eq(cnae2009Id))
						.execute()
					);
					
					// Update any remaining EnterpriseActivity records that have the CNAE 2009 code
					// but were not updated in the previous step ( because the CNAE 2025 code was
					// the same as the CNAE 2009 code, or because the CNAE 2025 code was not found
					// in the CNAE table )
					config.dsl().
					update(ENTERPRISE_ACTIVITY)
					.set(ENTERPRISE_ACTIVITY.CNAE, cnae2025Id )
					.from(CNAE2009)
					.where(CNAE2009.CODE.eq(cnae2009Code))
					.and(ENTERPRISE_ACTIVITY.CNAE2009.eq(CNAE2009.ID))
					.execute();
					
					// Level III with singleton CNAE 2009 code, 
					config.dsl().
					update(ENTERPRISE_ACTIVITY)
					.set(ENTERPRISE_ACTIVITY.CNAE, cnae2025Id )
					.from(CNAE2009)
					.where(DSL.length(CNAE2009.CODE).eq(3))
					.and(DSL.concat(CNAE2009.CODE, "0").eq(cnae2009Code))
					.and(ENTERPRISE_ACTIVITY.CNAE2009.eq(CNAE2009.ID))
					.execute();

					// Level II with singleton CNAE 2009 code, 
					config.dsl().
					update(ENTERPRISE_ACTIVITY)
					.set(ENTERPRISE_ACTIVITY.CNAE, cnae2025Id )
					.from(CNAE2009)
					.where(DSL.length(CNAE2009.CODE).eq(2))
					.and(DSL.concat(CNAE2009.CODE, "00").eq(cnae2009Code))
					.and(ENTERPRISE_ACTIVITY.CNAE2009.eq(CNAE2009.ID))
					.execute();
				});
				
				
			} catch (IOException e) {
				throw new RuntimeException(e);
				
			}
		});
	}
	
	

}
