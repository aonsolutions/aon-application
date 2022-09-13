package net.aonsolutions.db.up2date.payroll;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ContractExtraCreation implements Update {


	public static ContractExtraCreation CONTRACTEXTRACREATION = new ContractExtraCreation();

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		String sql = 
		"CREATE TABLE IF NOT EXISTS `contract_extra` (" + 
		"  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico'," + 
		"  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio'," + 
		"  `contract` int(4) NOT NULL COMMENT 'Contrato'," +
		"  `contract_payment` int(4) DEFAULT NULL COMMENT 'Concepto',"+
		"  `start_date` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de inicio dd mm [year offset]'," +
		"  `end_date` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de finalizacion dd mm [year offset]'," +
		"  `issue_date` varchar(32) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Fecha de emision dd mm'," +
		"  PRIMARY KEY (`id`)," +
		"  KEY `IDX_CONTRACT_EXTRA_CONTRACT` (`contract`)," +
		"  KEY `IDX_CONTRACT_EXTRA_CONTRACT_PAYMENT` (`contract_payment`)," +
		"  KEY `IDX_CONTRACT_EXTRA_DOMAIN` (`domain`)," +
		"  CONSTRAINT `FK_CONTRACT_EXTRA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)," +
		"  CONSTRAINT `FK_CONTRACT_EXTRA_CONTRACT_PAYMENT` FOREIGN KEY (`contract_payment`) REFERENCES `contract_payment` (`id`)," +
		"  CONSTRAINT `FK_CONTRACT_EXTRA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)" +
		") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costes'";
		
		dslContext.execute(sql);

	}
}
