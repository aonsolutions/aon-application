package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractCost.CONTRACT_COST;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.ContractCost;
import com.esferalia.aon.jooq.tables.ContractDeduction;

import net.aonsolutions.db.up2date.Update;

public class MEIPECUpdate implements Update {

    public static final MEIPECUpdate MEIPECUPDATE = new MEIPECUpdate();

    private MEIPECUpdate() {
	super();
    }

    @Override
    public void upgrade(Connection connection) {
	Settings settings;
	DSLContext dslContext;

	settings = new Settings();
	settings.setRenderSchema(false);
	settings.setParamType(ParamType.INLINED);

	// Establish context
	dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

	// START_DATE 01/01/2023
	Calendar calendar = Calendar.getInstance();
	calendar.set(Calendar.MILLISECOND, 0);
	calendar.set(Calendar.SECOND, 0);
	calendar.set(Calendar.MINUTE, 0);
	calendar.set(Calendar.HOUR_OF_DAY, 0);
	calendar.set(Calendar.DAY_OF_MONTH, 1);
	calendar.set(Calendar.MONTH, Calendar.JANUARY);
	calendar.set(Calendar.YEAR, 2023);

	Date startOf2023Date = new Date(calendar.getTimeInMillis());

	calendar.add(Calendar.DAY_OF_MONTH, -1);
	Date endOf2022Date = new Date(calendar.getTimeInMillis());

	dslContext.transaction(config -> {

	    // DISABLED FOREING_KEY FOR INSERT
	    dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

	    Map<String, String> cuotas = new HashMap<>();
	    cuotas.put("03", "CUOTA EMPRESARIAL POR CONTINGENCIAS COMUNES");
	    cuotas.put("68", "CONTINGENCIAS COMUNES Y PROFESIONALES - CUOTA TOTAL");

	    for (Map.Entry<String, String> entry : cuotas.entrySet()) {
		String key = entry.getKey();
		String description = entry.getValue();


		int inserted = dslContext.insertInto(CONTRACT_BONUS)
			.columns(CONTRACT_BONUS.DOMAIN, 
				CONTRACT_BONUS.CONTRACT, 
				CONTRACT_BONUS.START_DATE,
				CONTRACT_BONUS.END_DATE, 
				CONTRACT_BONUS.DESCRIPTION, 
				CONTRACT_BONUS.BONUS_CONCEPT,
				CONTRACT_BONUS.EXPRESSION)
			.select(DSL
				.select(CONTRACT_BONUS.DOMAIN, 
					CONTRACT_BONUS.CONTRACT, 
					DSL.date(startOf2023Date),
					CONTRACT_BONUS.END_DATE, 
					CONTRACT_BONUS.DESCRIPTION,
					CONTRACT_BONUS.BONUS_CONCEPT,
					DSL.replace(CONTRACT_BONUS.EXPRESSION, "CGC_E", "CGC_E + MEI_E"))
				.from(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.START_DATE.lt(startOf2023Date))
				.and(CONTRACT_BONUS.END_DATE.isNull().or(CONTRACT_BONUS.END_DATE.ge(startOf2023Date)))
				.and(CONTRACT_BONUS.EXPRESSION.like("/*epoch:%pec:%,quota:" + key + "*//*read-only*/%"))
				.and(CONTRACT_BONUS.EXPRESSION.notLike("/*epoch:%pec:%,quota:" + key + "*//*read-only*/%MEI_E%")))
			.execute();

		dslContext.update(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.END_DATE, endOf2022Date)
			.where(CONTRACT_BONUS.START_DATE.lt(startOf2023Date))
			.and(CONTRACT_BONUS.END_DATE.isNull().or(CONTRACT_BONUS.END_DATE.ge(startOf2023Date)))
			.and(CONTRACT_BONUS.EXPRESSION.like("/*epoch:%pec:%,quota:" + key + "*//*read-only*/%"))
			.and(CONTRACT_BONUS.EXPRESSION.notLike("/*epoch:%pec:%,quota:" + key + "*//*read-only*/%MEI_E%"))
			.execute();

		int updated = dslContext.update(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.EXPRESSION, DSL.replace(CONTRACT_BONUS.EXPRESSION, "CGC_E", "CGC_E + MEI_E") )
			.where(CONTRACT_BONUS.START_DATE.ge(startOf2023Date))
			.and(CONTRACT_BONUS.END_DATE.isNull().or(CONTRACT_BONUS.END_DATE.ge(startOf2023Date)))
			.and(CONTRACT_BONUS.EXPRESSION.like("/*epoch:%pec:%,quota:" + key + "*//*read-only*/%"))
			.and(CONTRACT_BONUS.EXPRESSION.notLike("/*epoch:%pec:%,quota:" + key + "*//*read-only*/%MEI_E%"))
			.execute();

		System.out.println(description + ": " + inserted  + " + " +  updated );

		
	    }
	    
	    {
        	    ContractCost MEI_COST = CONTRACT_COST.as("MEI_COST");
        	    
        	    int inserted = dslContext
        		    .insertInto(CONTRACT_COST)
        		    .columns(CONTRACT_COST.DOMAIN, 
        			    CONTRACT_COST.CONTRACT, 
        			    CONTRACT_COST.START_DATE,
        			    CONTRACT_COST.END_DATE, 
        			    CONTRACT_COST.DESCRIPTION, 
        			    CONTRACT_COST.CODE,
        			    CONTRACT_COST.EXPRESSION)
        		    .select(DSL
        			    .select(CONTRACT_COST.DOMAIN, 
        				    CONTRACT_COST.CONTRACT, 
        				    DSL.greatest(CONTRACT_COST.START_DATE, DSL.date(startOf2023Date)),
        				    CONTRACT_COST.END_DATE, 
        				    CONTRACT_COST.DESCRIPTION, 
        				    DSL.inline("MEI_E"),
        				    CONTRACT_COST.EXPRESSION)
        			    .from(CONTRACT_COST)
        			    .leftJoin(MEI_COST)
        			    .on(MEI_COST.CODE.eq("MEI_E")
        				    .and(DSL.substring(CONTRACT_COST.EXPRESSION,9,12).eq(DSL.substring(MEI_COST.EXPRESSION,9,12))))
        			    .where(MEI_COST.ID.isNull())
        			    .and(CONTRACT_COST.CODE.eq("CGC_E"))
        			    //.and(CONTRACT_COST.START_DATE.lt(startOf2023Date))
        			    .and(CONTRACT_COST.END_DATE.isNull().or(CONTRACT_COST.END_DATE.ge(startOf2023Date)))
        			    .and(CONTRACT_COST.EXPRESSION.like("/*epoch:%pec:03,quota:01*//*read-only*/REMOVE()/**/")))
        		    .execute();
        
        	    System.out.println("REDUCCIÓN CUOTA EMPRESARIAL POR AT Y EP, CUOTAS DE RECAUDACIÓN CONJUNTA : " + inserted);
	    }

	    ContractDeduction MEI_DEDUCTION = CONTRACT_DEDUCTION.as("MEI_DEDUCTION");
	    
	    Integer [] cgcs = dslContext.select()
        	    .from(DEDUCTION_CONCEPT)
        	    .where(DEDUCTION_CONCEPT.CODE.eq("CGC"))
        	    .fetchArray(DEDUCTION_CONCEPT.ID);
	    
	    Integer [] meis = dslContext.select()
        	    .from(DEDUCTION_CONCEPT)
        	    .where(DEDUCTION_CONCEPT.CODE.eq("MEI"))
        	    .fetchArray(DEDUCTION_CONCEPT.ID);
    	    int inserted = dslContext
    		    .insertInto(CONTRACT_DEDUCTION)
    		    .columns(CONTRACT_DEDUCTION.DOMAIN, 
    			    CONTRACT_DEDUCTION.CONTRACT, 
    			    CONTRACT_DEDUCTION.START_DATE,
    			    CONTRACT_DEDUCTION.END_DATE, 
    			    CONTRACT_DEDUCTION.DESCRIPTION, 
    			    CONTRACT_DEDUCTION.DEDUCTION_CONCEPT,
    			    CONTRACT_DEDUCTION.EXPRESSION)
    		    .select(DSL
    			    .select(CONTRACT_DEDUCTION.DOMAIN, 
    				    CONTRACT_DEDUCTION.CONTRACT, 
				    DSL.greatest(CONTRACT_DEDUCTION.START_DATE, DSL.date(startOf2023Date)),
    				    CONTRACT_DEDUCTION.END_DATE, 
    				    CONTRACT_DEDUCTION.DESCRIPTION, 
    				    DSL.inline(meis[0]),
    				    CONTRACT_DEDUCTION.EXPRESSION)
    			    .from(CONTRACT_DEDUCTION)
    			    .leftJoin(MEI_DEDUCTION)
    			    .on(MEI_DEDUCTION.DEDUCTION_CONCEPT.in(meis)
    				    .and(DSL.substring(CONTRACT_DEDUCTION.EXPRESSION,9,12).eq(DSL.substring(MEI_DEDUCTION.EXPRESSION,9,12))))
    			    .where(MEI_DEDUCTION.ID.isNull())
    			    .and(CONTRACT_DEDUCTION.DEDUCTION_CONCEPT.in(cgcs))
    			    //.and(CONTRACT_DEDUCTION.START_DATE.lt(startOf2023Date))
    			    .and(CONTRACT_DEDUCTION.END_DATE.isNull().or(CONTRACT_DEDUCTION.END_DATE.ge(startOf2023Date)))
    			    .and(CONTRACT_DEDUCTION.EXPRESSION.like("/*epoch:%pec:03,quota:08*//*read-only*/REMOVE()/**/")))
    		    .execute();
    
    	    System.out.println("RED.CUOTA SS-PORCENT CONTINGENCIAS COMUNES : " + inserted);

	    dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

	});
    }

}
