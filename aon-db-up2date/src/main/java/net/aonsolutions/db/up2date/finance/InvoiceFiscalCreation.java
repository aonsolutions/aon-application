package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceFiscalCreation implements Update {

//	#
//	# Structure for the `invoice_fiscal` table : 
//	#
//
//	CREATE TABLE `invoice_fiscal` (
//	 `invoice` int(4) DEFAULT NULL COMMENT 'Identificador de la Factura',
//	 `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
//	 `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Factura',
//	 `tax_date` date DEFAULT NULL COMMENT 'Fecha de Impuestos de la Factura',
//	 `vat_general` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen General',
//	 `vat_simplified` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial simplificado',
//	 `vat_surcharge` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial recargo de equivalencia',
//	 `vat_accrual_payment` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial del criterio de caja',
//	 `vat_rebu_operation` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial bienes usados por operacion',
//	 `vat_rebu_profit` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial bienes usados, objetos de arte. Beneficio global',
//	 `vat_travel_agency` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial agencias de viajes',
//	 `vat_agriculture` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial agricultura, ganaderia y pesca',
//	 `vat_gold` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial oro de inversion, realizacion de operaciones que puedan tributar por este regimen',
//	 `vat_union_external` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen exterior a la Union',
//	 `vat_union` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen de la Union',
//	 `vat_importation` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen de importacion',
//	  PRIMARY KEY (`invoice`),
//	  KEY `IDX_INVOICE_FISCAL_DOMAIN` (`domain`),
//	  CONSTRAINT `FK_INVOICE_FISCAL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
//	  CONSTRAINT `FK_INVOICE_FISCAL_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
//	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Info fiscal de facturas';


	public static final InvoiceFiscalCreation INVOICE_FISCAL_CREATION = new InvoiceFiscalCreation();

	private InvoiceFiscalCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table `invoice_fiscal`" );

		String SQL =
				"CREATE TABLE IF NOT EXISTS `invoice_fiscal` ("
				+"   `invoice` int(4) DEFAULT NULL COMMENT 'Identificador de la Factura',"
				+"   `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
				+"   `issue_date` date DEFAULT NULL COMMENT 'Fecha de emision de la Factura',"
				+"   `tax_date` date DEFAULT NULL COMMENT 'Fecha de Impuestos de la Factura',"
				+"   `vat_general` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen General',"
				+"   `vat_simplified` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial simplificado',"
				+"   `vat_surcharge` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial recargo de equivalencia',"
				+"   `vat_accrual_payment` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial del criterio de caja',"
				+"   `vat_rebu_operation` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial bienes usados por operacion',"
				+"   `vat_rebu_profit` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial bienes usados, objetos de arte. Beneficio global',"
				+"   `vat_travel_agency` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial agencias de viajes',"
				+"   `vat_agriculture` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial agricultura, ganaderia y pesca',"
				+"   `vat_gold` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen especial oro de inversion, realizacion de operaciones que puedan tributar por este regimen',"
				+"   `vat_union_external` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen exterior a la Union',"
				+"   `vat_union` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen de la Union',"
				+"   `vat_importation` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Regimen de importacion',"
				+"    PRIMARY KEY (`invoice`),"
				+"    KEY `IDX_INVOICE_FISCAL_DOMAIN` (`domain`),"
				+"    CONSTRAINT `FK_INVOICE_FISCAL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+"    CONSTRAINT `FK_INVOICE_FISCAL_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)"
				+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Info fiscal de facturas';"
		;

		dslContext.execute(SQL);
		
		System.out.println("[END]");
	}

}
