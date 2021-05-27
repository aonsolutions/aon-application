package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class EreFzaExoneradoUpdate implements Update {

	public static final EreFzaExoneradoUpdate EREFZAEXONERADOUPDATE = new EreFzaExoneradoUpdate();
	
	private EreFzaExoneradoUpdate() {
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
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("ERE_FZA_EXONERADO"))
			.fetchOptional(PAYMENT_CONCEPT.ID)
			.ifPresent(ereFzaConceptId -> {
				dslContext
				.update(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.EXPRESSION, 
				"isdef COEFICIENTE_ERE_FZA_EXONERADO ? "
				+ "( SELF.addBonus('EXPDTE. REG. DE EMPLEO POR FZA. MAYOR EXONERADO','CUOTA_EMPRESARIAL * COEFICIENTE_ERE_FZA_EXONERADO * (isdef PORCENTAJE_EXONERADO ? PORCENTAJE_EXONERADO : 100.00)/100.00');0.00 ) "
				+ ": HIDE()")
				.where(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(ereFzaConceptId))
				.execute(); 
			});
			;


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
