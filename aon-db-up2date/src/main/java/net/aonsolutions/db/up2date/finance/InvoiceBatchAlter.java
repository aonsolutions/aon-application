package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceBatchAlter implements Update {

	public static final InvoiceBatchAlter INVOICE_BATCH_ALTER = new InvoiceBatchAlter();

	private InvoiceBatchAlter() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		try {
			String addDescription = "ALTER TABLE `invoice_batch` ADD COLUMN `description` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion del lote' AFTER `domain`";
			String addEndDate = "ALTER TABLE `invoice_batch` ADD COLUMN `end_date` date DEFAULT NULL COMMENT 'Fecha de finalizacion' AFTER `date`";
			String modifyDataResponse = "ALTER TABLE `invoice_batch` MODIFY COLUMN `data_response` int DEFAULT NULL COMMENT 'Envio de la comunicacion'";
			String addMd5 = "ALTER TABLE `invoice_batch` ADD COLUMN `md5` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Hash md5' AFTER `data_response`";
			
			String addCreationDate = "ALTER TABLE `invoice_batch` ADD COLUMN `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion'";
			String addModificationUser = "ALTER TABLE `invoice_batch` ADD COLUMN `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion'";
			String addModificationDate = "ALTER TABLE `invoice_batch` ADD COLUMN `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion'";

			dslContext.execute(addDescription);
			dslContext.execute(addEndDate);
			dslContext.execute(modifyDataResponse);
			dslContext.execute(addMd5);
			dslContext.execute(addCreationDate);
			dslContext.execute(addModificationUser);
			dslContext.execute(addModificationDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			String addInvoiceBatch = "ALTER TABLE `alcatraz` ADD COLUMN `invoice_batch` int DEFAULT NULL COMMENT 'Id Lote Factura'";
			String addInvoiceBatchKey =  "ALTER TABLE `alcatraz` ADD KEY `IDX_ALCATRAZ_INVOICE_BATCH` (`invoice_batch`)";
			String addInvoiceBatchConstraint =  "ALTER TABLE `alcatraz` ADD CONSTRAINT `FK_ALCATRAZ_INVOICE_BATCH` FOREIGN KEY (`invoice_batch`) REFERENCES `invoice_batch` (`id`)";
			
			dslContext.execute(addInvoiceBatch);
			dslContext.execute(addInvoiceBatchKey);
			dslContext.execute(addInvoiceBatchConstraint);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[END]");
	}
}
