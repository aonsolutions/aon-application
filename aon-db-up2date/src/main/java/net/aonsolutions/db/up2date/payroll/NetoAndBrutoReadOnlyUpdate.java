package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class NetoAndBrutoReadOnlyUpdate implements Update {

	public static final NetoAndBrutoReadOnlyUpdate NETOBRUTOREADONLYUPDATE = new NetoAndBrutoReadOnlyUpdate();
	
	private NetoAndBrutoReadOnlyUpdate() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		
		dslContext.transaction( (config) -> {
			
			dslContext
			.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.EXPRESSION, DSL.concat(DSL.concat("/*read-only*/", CONTRACT_PAYMENT.EXPRESSION), "/**/") )
			.where(CONTRACT_PAYMENT.EXPRESSION.likeRegex("^NETO\\([0-9\\.]+\\)$"))
			.execute()
			;

			dslContext
			.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.EXPRESSION, DSL.concat(DSL.concat("/*read-only*/", CONTRACT_PAYMENT.EXPRESSION), "/**/") )
			.where(CONTRACT_PAYMENT.EXPRESSION.likeRegex("^NETO\\([0-9\\.]+(\\s*\\*\\s*DIAS_TRABAJADOS\\s*\\/\\s*DIAS_MES\\s*)\\)$"))
			.execute()
			;
			
//			dslContext
//			.update(CONTRACT_PAYMENT)
//			.set(CONTRACT_PAYMENT.EXPRESSION, DSL.concat(DSL.concat("/*read-only*/", CONTRACT_PAYMENT.EXPRESSION), "/**/") )
//			.where(CONTRACT_PAYMENT.EXPRESSION.likeRegex("^\\/\\*user\\*\\/NETO\\([0-9\\.]+\\)\\/\\*\\*\\/(\\s*\\*\\s*DIAS_TRABAJADOS\\s*\\/\\s*DIAS_MES\\s*)$"))
//			.execute()
//			;
			
			dslContext
			.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.EXPRESSION, DSL.concat(DSL.concat("/*read-only*/", CONTRACT_PAYMENT.EXPRESSION), "/**/") )
			.where(CONTRACT_PAYMENT.EXPRESSION.likeRegex("^BRUTO\\([0-9\\.]+\\)$"))
			.execute()
			;

			dslContext
			.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.EXPRESSION, DSL.concat(DSL.concat("/*read-only*/", CONTRACT_PAYMENT.EXPRESSION), "/**/") )
			.where(CONTRACT_PAYMENT.EXPRESSION.likeRegex("^BRUTO\\([0-9\\.]+(\\s*\\*\\s*DIAS_TRABAJADOS\\s*\\/\\s*DIAS_MES\\s*)\\)$"))
			.execute()
			;

		});
	}

}
