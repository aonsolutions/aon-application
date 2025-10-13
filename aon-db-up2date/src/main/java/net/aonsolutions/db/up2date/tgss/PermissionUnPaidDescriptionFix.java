package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class PermissionUnPaidDescriptionFix implements Update {
	
	public static final PermissionUnPaidDescriptionFix PERMISSIONUNPAIDDESCRIPTIONFIX = new PermissionUnPaidDescriptionFix();
	
	private PermissionUnPaidDescriptionFix() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			int updated = 
			dslContext.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, DSL.concat(SYSTEM_PAYMENT.DESCRIPTION, " @{/*fechas*/FORMAT('dd/MM', INICIO)}@{SI(INICIO != FIN, FORMAT(' - dd/MM', FIN))}"))
			.from(PAYMENT_CONCEPT)
			.where(SYSTEM_PAYMENT.DOMAIN.le(0))
			.and(PAYMENT_CONCEPT.CODE.eq("UNPAID"))
			.and(PAYMENT_CONCEPT.ID.eq(SYSTEM_PAYMENT.PAYMENT_CONCEPT))
			.and(SYSTEM_PAYMENT.DESCRIPTION.notContains("@"))
			.execute();
			
			System.out.println("Updated " + updated );
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
