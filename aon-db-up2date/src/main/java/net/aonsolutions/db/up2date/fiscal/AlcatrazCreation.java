package net.aonsolutions.db.up2date.fiscal;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlcatrazCreation implements Update {
	
	public static final AlcatrazCreation ALCATRAZ_CREATION = new AlcatrazCreation();

	private AlcatrazCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table `alcatraz`" );
		
		String sql =
			"CREATE TABLE IF NOT EXISTS `alcatraz` ("
				+"`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
				+"`domain` int(4) NOT NULL COMMENT 'Id Dominio',"
				+"`fs_model` int(4) DEFAULT NULL COMMENT 'Id Modelo fiscal',"
				+"`invoice` int(4) DEFAULT NULL COMMENT 'Id Factura',"
				+"`salary` int(4) DEFAULT NULL COMMENT 'Id Nomina',"
				+"PRIMARY KEY (`id`),"
				+"KEY `IDX_ALCATRAZ_DOMAIN` (`domain`),"
				+"KEY `IDX_ALCATRAZ_FS_MODEL` (`fs_model`),"
				+"KEY `IDX_ALCATRAZ_INVOICE` (`invoice`),"
				+"KEY `IDX_ALCATRAZ_SALARY` (`salary`),"
				+"CONSTRAINT `FK_ALCATRAZ_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+"CONSTRAINT `FK_ALCATRAZ_FS_MODEL` FOREIGN KEY (`fs_model`) REFERENCES `fs_model` (`id`),"
				+"CONSTRAINT `FK_ALCATRAZ_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`),"
				+"CONSTRAINT `FK_ALCATRAZ_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary` (`id`)"
			+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bloqueo de entidades';"
		;
		dslContext.execute(sql);
		
		System.out.println("[END]");
	}
}
