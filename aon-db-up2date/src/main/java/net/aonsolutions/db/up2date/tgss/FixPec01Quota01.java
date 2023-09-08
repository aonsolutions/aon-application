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

public class FixPec01Quota01 implements Update {

    public static final FixPec01Quota01 FIXPEC01QUOTA01 = new FixPec01Quota01();

    private FixPec01Quota01() {
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

	    dslContext.update(CONTRACT_BONUS)
		    .set(CONTRACT_BONUS.EXPRESSION,
			    DSL.regexpReplaceAll(CONTRACT_BONUS.EXPRESSION, "\\(\\s*CUOTA_EMPRESARIAL[^\\)]*\\)",
				    "(CGC_E + IT_E + IMS_E + FP_E + DESMPL_E + FOGASA_E)"))
		    .where(CONTRACT_BONUS.EXPRESSION.like("/*epoch:%pec:01,quota:01*//*read-only*/%"))
		    .and(CONTRACT_BONUS.END_DATE.isNull().or(CONTRACT_BONUS.END_DATE.ge(startOf2023Date))).execute();

	});
    }

}
