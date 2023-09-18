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

import net.aonsolutions.db.up2date.Update;

public class MEIPECUpdateUndo implements Update {

    public static final MEIPECUpdateUndo MEIPECUPDATEUNDO = new MEIPECUpdateUndo();

    private MEIPECUpdateUndo() {
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


	dslContext.transaction(config -> {

	    // DISABLED FOREING_KEY FOR INSERT
	    dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

	    Map<String, String> cuotas = new HashMap<>();
	    cuotas.put("03", "CUOTA EMPRESARIAL POR CONTINGENCIAS COMUNES");
	    cuotas.put("68", "CONTINGENCIAS COMUNES Y PROFESIONALES - CUOTA TOTAL");

	    for (Map.Entry<String, String> entry : cuotas.entrySet()) {
		String key = entry.getKey();
		String description = entry.getValue();

		int updated = dslContext.update(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.EXPRESSION,
				DSL.replace(CONTRACT_BONUS.EXPRESSION, "CGC_E + MEI_E", "CGC_E"))
			.where(CONTRACT_BONUS.START_DATE.ge(startOf2023Date))
			.and(CONTRACT_BONUS.END_DATE.isNull().or(CONTRACT_BONUS.END_DATE.ge(startOf2023Date)))
			.and(CONTRACT_BONUS.EXPRESSION.like("/*epoch:%pec:%,quota:" + key + "*//*read-only*/%MEI_E%"))
			.execute();

		System.out.println(description + ": " + updated);

	    }

	    {
		int deleted = dslContext.delete(CONTRACT_COST).where(CONTRACT_COST.CODE.eq("MEI_E"))
			.and(CONTRACT_COST.END_DATE.isNull().or(CONTRACT_COST.END_DATE.ge(startOf2023Date)))
			.and(CONTRACT_COST.EXPRESSION.like("/*epoch:%pec:03,quota:01*//*read-only*/REMOVE()/**/"))
			.execute();

		System.out.println(
			"REDUCCIÓN CUOTA EMPRESARIAL POR AT Y EP, CUOTAS DE RECAUDACIÓN CONJUNTA : " + deleted);
	    }
	    {
		Integer[] meis = dslContext.select().from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.CODE.eq("MEI"))
			.fetchArray(DEDUCTION_CONCEPT.ID);
		int deleted = dslContext.delete(CONTRACT_DEDUCTION).where(CONTRACT_DEDUCTION.DEDUCTION_CONCEPT.in(meis))
			.and(CONTRACT_DEDUCTION.END_DATE.isNull().or(CONTRACT_DEDUCTION.END_DATE.ge(startOf2023Date)))
			.and(CONTRACT_DEDUCTION.EXPRESSION.like("/*epoch:%pec:03,quota:08*//*read-only*/REMOVE()/**/"))
			.execute();

		System.out.println("RED.CUOTA SS-PORCENT CONTINGENCIAS COMUNES : " + deleted);
	    }

	    dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

	});
    }

}
