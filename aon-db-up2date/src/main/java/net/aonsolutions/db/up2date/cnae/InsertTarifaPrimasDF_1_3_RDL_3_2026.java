package net.aonsolutions.db.up2date.cnae;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Row3;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.tools.csv.CSVReader;

import net.aonsolutions.db.up2date.Update;

public class InsertTarifaPrimasDF_1_3_RDL_3_2026 implements Update {

	
	public static final InsertTarifaPrimasDF_1_3_RDL_3_2026 INSERTTARIFAPRIMASDF_1_3_RDL_3_2026 = new InsertTarifaPrimasDF_1_3_RDL_3_2026();
	
	private static final Name CNAE2025_RATE = DSL.name("cnae2025_rate");
	
    /**
     * The column <code>cnae2025_rate.id</code>. 
     */
    private static final Field<Integer> ID = DSL.field(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), DSL.comment("Identificador Único")); 
    /**
     * The column <code>cnae2025_rate.code</code>. 	
     */
    private static final Field<String> CODE = DSL.field(DSL.name("code"), SQLDataType.VARCHAR(5).nullable(false), DSL.comment("Codigo CNAE 2025")); 
    /**
     * The column <code>cnae2025_rate.start_date</code>.
     */
    public final Field<Date> START_DATE = DSL.field(DSL.name("start_date"), SQLDataType.DATE.nullable(false).defaultValue(DSL.inline("2026-01-01", SQLDataType.DATE)), DSL.comment("Fecha de inicio "));

    /**
     * The column <code>cnae2025_rate.end_date</code>.
     */
    public final Field<Date> END_DATE = DSL.field(DSL.name("end_date"), SQLDataType.DATE, DSL.comment("Fecha de finalizacion"));	

    /**
     * The column <code>aon-master.cnae2009_rate.it_amount</code>.
     */
    public final Field<Double> IT_AMOUNT = DSL.field(DSL.name("it_amount"), SQLDataType.DOUBLE.defaultValue(DSL.inline("0.000", SQLDataType.DOUBLE)), DSL.comment("Importe por Incapacidad Temporal (I.T.)"));

    /**
     * The column <code>aon-master.cnae2009_rate.ims_amount</code>.
     */
    public final Field<Double> IMS_AMOUNT = DSL.field(DSL.name("ims_amount"), SQLDataType.DOUBLE.defaultValue(DSL.inline("0.000", SQLDataType.DOUBLE)), DSL.comment("Importe por Incapacidad Permanente, Muerte y Supervivencia (I.M.S.)"));
	
	private InsertTarifaPrimasDF_1_3_RDL_3_2026() {
		
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		String database = dslContext.fetchOne("SELECT DATABASE()").getValue(0, String.class);

		
        dslContext.select()
        .from("information_schema.tables")
        .where(DSL.field("table_schema").eq(database))
        .and(DSL.field("table_name").eq(CNAE2025_RATE.last()))
        .fetchOptional().ifPresentOrElse(
			r -> 
				dslContext.truncate(CNAE2025_RATE).execute()
			,
			() -> 
				dslContext.createTable(CNAE2025_RATE)
				.column(ID)
				.column(CODE)
				.column(START_DATE)
				.column(END_DATE)
				.column(IT_AMOUNT)
				.column(IMS_AMOUNT)
				.primaryKey(ID)
				.execute()
		);
		
		
		
		dslContext.transaction( config -> {
			try ( CSVReader csvReader  = new CSVReader(new InputStreamReader(InsertTarifaPrimasDF_1_3_RDL_3_2026.class.getResourceAsStream("tarifaprimasdf_1_3_rdl_3_2026.csv")))) {
				String[] line ;
				Collection<Row3<String, Double, Double>> rows = new ArrayList<>();
				while ( (line = csvReader.readNext()) != null ) {
					String code = line[0];

					Double itAmount = Double.valueOf(line[1]);
					Double imsAmount = Double.valueOf(line[2]);
					
					rows.add(DSL.row(code, itAmount, imsAmount));
						
				}
				dslContext.insertInto(DSL.table(CNAE2025_RATE), CODE, IT_AMOUNT, IMS_AMOUNT).valuesOfRows(rows).execute();
			}
			
		});
	}
	
	

}
