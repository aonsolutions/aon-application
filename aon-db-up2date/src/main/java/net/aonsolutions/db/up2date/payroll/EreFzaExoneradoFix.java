package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.SystemCost;

import net.aonsolutions.db.up2date.Update;

public class EreFzaExoneradoFix implements Update {

	public static final EreFzaExoneradoFix EREFZAEXONERADOFIX = new EreFzaExoneradoFix();
	
	private EreFzaExoneradoFix() {
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
			.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("ERE_FZA_EXONERADO"))
			.fetchInto(PAYMENT_CONCEPT)
			.forEach(concept -> {
				dslContext
				.update(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.EXPRESSION, 
						"/*read-only*/"
						+ "SELF.addCost('RED_SS_E'"
										+ ",'EXONE.ERE.F.MAY.COMP'"
										+ ",'-1*(CGC_E+IT_E+IMS_E+FP_E+DESMPL_E+FOGASA_E+MEI_E) * COEFICIENTE_ERE_FZA_EXONERADO * IFNDEF(\"PORCENTAJE_EXONERADO\", 100.00)/100.00'"
										+ ");"
						+ "DIAS_ERE_FZA_EXONERADO*0.00"
						+ "/**/")
				.where(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(concept.getId()))
				.execute(); 

			});
		});
	}

}
