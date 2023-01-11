package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceFiscalAddExpDate implements Update {

	public static final InvoiceFiscalAddExpDate INVOICE_FISCAL_ADD_EXP_DATE= new InvoiceFiscalAddExpDate();

	private InvoiceFiscalAddExpDate() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Alter table `invoice_fiscal` add exp_date column" );
		
		String sql = "ALTER TABLE `invoice_fiscal` ADD COLUMN `exp_date` date DEFAULT NULL Comment 'Fecha de expedicion de la Factura para Ticket Bai' AFTER `tax_date`";
		dslContext.execute(sql);
		
		System.out.println("[END]");
	}

}
