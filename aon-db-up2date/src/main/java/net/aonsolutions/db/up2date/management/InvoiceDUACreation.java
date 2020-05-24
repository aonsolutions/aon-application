package net.aonsolutions.db.up2date.management;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceDUACreation implements Update {
	
	public static InvoiceDUACreation INVOICEDUACREATION = new InvoiceDUACreation();
	
	private class Counters {
		int f_read;
		int f_updated;
		int bs_updated;
		int ft_deleted;
		int bsl_deleted;
	}

	private static enum FinanceStatus {
		PENDING,
		BATCHED,
		RETURNED,
		PAID,
		SETTLED;

		public Byte value() {
			return (byte) this.ordinal();
		}
	}
	
	private static enum FinanceTrackingType {
		BATCHED ( FinanceStatus.BATCHED),
		PAID ( FinanceStatus.PAID),
		RETURNED ( FinanceStatus.RETURNED),
	    FRACTIONED ( FinanceStatus.PENDING),
	    SETTLED( FinanceStatus.SETTLED);
		
		private FinanceStatus financeStatus;
		private FinanceTrackingType (FinanceStatus financeStatus) {
			this.financeStatus = financeStatus;
		}
		
		public FinanceStatus getFinanceStatus() {
			return financeStatus;
		}
	}	
	public static final int KALDEVI_DOMAIN = 8353;
	public static final int KALDEVI_BANK_STATEMENT_ID = 2433681; 
	
	public static final InvoiceDUACreation ALTER_FS_MODEL_200_2018 = new InvoiceDUACreation();

	private InvoiceDUACreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		System.out.println("[START]");
		System.out.println( "Creacion table INVOICE_DUA" );
		
		String SQL = 
		"CREATE TABLE IF NOT EXISTS `invoice_dua` ("
			  + "`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',"
			  + "`domain` int(4) NOT NULL DEFAULT 0 COMMENT 'Dominio',"
			  + "`invoice_national` int(4) NOT NULL DEFAULT 0 COMMENT 'ID de la factura nacional',"
			  + "`invoice_import` int(4) NOT NULL DEFAULT 0 COMMENT 'ID de la factura de importacion',"
			  + "`code` varchar(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo DUA',"
			  + "`price` double(15,3) DEFAULT 0.000 COMMENT 'Precio del articulo',"
			  + "`adjust` double(15,3) DEFAULT 0.000 COMMENT 'Ajuste',"
			  + "`statistical_value` double(15,3) DEFAULT 0.000 COMMENT 'Valor estadistico',"
			  + "`duty_account` int(4) NOT NULL COMMENT 'ID cuenta para aranceles',"
			  + "`duty_base` double(15,3) DEFAULT 0.000 COMMENT 'Base aranceles',"
			  + "`duty_percent` double(15,3) DEFAULT 0.000 COMMENT 'Porcentaje aranceles',"
			  + "`duty_total` double(15,3) DEFAULT 0.000 COMMENT 'Cuota aranceles',"
			  + "`vat_account` int(4) NOT NULL COMMENT 'ID cuenta para IVA',"
		  + "PRIMARY KEY (`id`),"
		  + "KEY `IDX_INVOICE_DUA_NATIONAL` (`invoice_national`),"
		  + "KEY `IDX_INVOICE_DUA_IMPORT` (`invoice_import`),"
		  + "KEY `IDX_INVOICE_DUA_DOMAIN` (`domain`),"
		  + "KEY `IDX_INVOICE_DUA_DUTY_ACCOUNT` (`duty_account`),"
		  + "KEY `IDX_INVOICE_DUA_VAT_ACCOUNT` (`vat_account`),"
		  + "CONSTRAINT `FK_INVOICE_DUA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
		  + "CONSTRAINT `FK_INVOICE_DUA_INVOICE_NATIONAL` FOREIGN KEY (`invoice_national`) REFERENCES `invoice` (`id`),"
		  + "CONSTRAINT `FK_INVOICE_DUA_INVOICE_IMPORT` FOREIGN KEY (`invoice_import`) REFERENCES `invoice` (`id`),"
		  + "CONSTRAINT `FK_INVOICE_DUA_DUTY_ACCOUNT` FOREIGN KEY (`duty_account`) REFERENCES `account` (`id`),"
		  + "CONSTRAINT `FK_INVOICE_DUA_VAT_ACCOUNT` FOREIGN KEY (`vat_account`) REFERENCES `account` (`id`)"
		+ ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vinculo Factura DUA';"
		;		
		dslContext.execute(SQL);
		System.out.println("[END]");
		
		
	}

}
