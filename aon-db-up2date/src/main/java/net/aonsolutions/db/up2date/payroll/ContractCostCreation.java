package net.aonsolutions.db.up2date.payroll;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ContractCostCreation implements Update {


	public static final ContractCostCreation CONTRACTCOSTCREATION = new ContractCostCreation();

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		String sql = 
		"CREATE TABLE IF NOT EXISTS `contract_cost` (" + 
		"  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico'," + 
		"  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio'," + 
		"  `contract` int(4) NOT NULL COMMENT 'Contrato'," + 
		"  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de Coste'," + 
		"  `code` varchar(10) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo'," +
		"  `expression` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Formula'," + 
		"  `description` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL," + 
		"  `start_date` date NOT NULL COMMENT 'Fecha de inicio '," + 
		"  `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion'," + 
		"  PRIMARY KEY (`id`)," + 
		"  KEY `IDX_CONTRACT_COST_DOMAIN` (`domain`)," + 
		"  KEY `IDX_CONTRACT_COST_CONTRACT` (`contract`)," + 
		"  CONSTRAINT `FK_CONTRACT_COST_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)," + 
		"  CONSTRAINT `FK_CONTRACT_COST_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)" + 
		") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Costes'";
		
		dslContext.execute(sql);

	}
}
