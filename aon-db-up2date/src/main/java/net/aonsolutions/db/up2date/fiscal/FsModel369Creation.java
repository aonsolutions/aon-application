package net.aonsolutions.db.up2date.fiscal;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FsModel369Creation implements Update {

	public static final FsModel369Creation FS_MODEL369_CREATION = new FsModel369Creation();

	private FsModel369Creation() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		System.out.println("[START]");
		createFsModel369(dslContext);
		createFsModel369Detail(dslContext);		
		System.out.println("[END]");
	}
	
	private void createFsModel369(DSLContext dslContext) {
		System.out.println( "Creation table `fs_model369`" );
		String sql = 
				"""
				CREATE TABLE IF NOT EXISTS `fs_model369` (
				  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
				  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
				  `year` int NOT NULL COMMENT 'Ejercicio de la Declaracion',
				  `period` tinyint(2) DEFAULT '0' COMMENT 'Periodo de la Declaracion',
				  `administration` tinyint DEFAULT '0' COMMENT 'Administracion',
				  `comments` text CHARACTER SET latin1 COLLATE latin1_spanish_ci COMMENT 'Comentarios de la Declaracion',
				  `status` tinyint DEFAULT '0' COMMENT 'Estado de la Declaracion',
				  `security_level` tinyint DEFAULT '0' COMMENT 'Nivel de seguridad',
				  `country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Pais del declarante',  
				  `document` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del declarante',
				  `name` varchar(125) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellidos y Nombre o Razon Social',
				  `regime` tinyint(2) DEFAULT '0' COMMENT 'Regimen',
				  `pay_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de Pago',
				  `nrc` varchar(22) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NRC',
				  `amount_paid` decimal(17,2) DEFAULT '0' COMMENT 'Importe pagado',
				  `without_activity` tinyint(1) DEFAULT '0' COMMENT 'Sin Actividad',
				  `from_date` datetime DEFAULT NULL COMMENT 'Fecha desde',
				  `to_date` datetime DEFAULT NULL COMMENT 'Fecha hasta',
				  `operator_number` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de operador en el regimen',
				  `intermediary` tinyint(1) DEFAULT '0' COMMENT 'Actua a traves de intermediario',
				  `intermediary_number` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de identificacion del intermediario',
				  `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
				  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
				  `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
				  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
				  `fs_model` int DEFAULT NULL COMMENT 'Identificador de fs_model',
				  PRIMARY KEY (`id`),
				  KEY `IDX_FS_MOD369_DOMAIN` (`domain`),
				  KEY `IDX_FS_MOD369_FS_MODEL` (`fs_model`),
				  CONSTRAINT `FK_FS_MOD369_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
				  CONSTRAINT `FK_FS_MOD369_FS_MODEL` FOREIGN KEY (`fs_model`) REFERENCES `fs_model` (`id`)
				) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Declaracion de Modelo 369';				
				""";
		dslContext.execute(sql);
	}
	
	private void createFsModel369Detail(DSLContext dslContext) {
		System.out.println( "Creation table `fs_model369_detail`" );
		String sql = 
				"""
				CREATE TABLE IF NOT EXISTS `fs_model369_detail` (
				  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
				  `domain` int NOT NULL COMMENT 'Identificador del Dominio',
				  `fs_model369` int NOT NULL COMMENT 'Identificador del modelo 369',
				  `detail_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de detalle',
				  `country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo pais de consumo',
				  `vat_percent` decimal(5,2) DEFAULT NULL COMMENT 'Porcentaje de IVA',
				  `vat_type` tinyint(2) DEFAULT '0' COMMENT 'Tipo de IVA',
				  `base` decimal(17,2) DEFAULT NULL COMMENT 'Base Imponible',
				  `quota` decimal(17,2) DEFAULT NULL COMMENT 'Cuota IVA',
				  `correction_year` int DEFAULT NULL COMMENT 'Ejercicio de la correccion',
				  `correction_period` tinyint(2) DEFAULT NULL COMMENT 'Periodo de la correccion',
				  `other_country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo pais de EP o envio',
				  `other_document` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIVA/Otros codigos identificativos',
				  PRIMARY KEY (`id`),
				  KEY `IDX_FS_MODEL369_DETAIL_DOMAIN` (`domain`),
				  KEY `IDX_FS_MODEL369_DETAIL_FS_MODEL369` (`fs_model369`),
				  CONSTRAINT `FK_FS_MODEL369_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
				  CONSTRAINT `FK_FS_MODEL369_DETAIL_FS_MODEL369` FOREIGN KEY (`fs_model369`) REFERENCES `fs_model369` (`id`)
				) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles de Declaraciones 369';
				""";
		dslContext.execute(sql);
	}
	

}
