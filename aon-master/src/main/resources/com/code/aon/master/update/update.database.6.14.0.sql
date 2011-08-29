# Database: aon_master
# Version: Actualizacion de la version 6.14.0 a la version 6.15.0.
# Created by: girazu
# Creation Date: 29/08/2011 12:02
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `offer` ADD KEY `IDX_OFFER_PROJECT` (`project`);

ALTER TABLE `offer` DROP KEY `IDX_OFFR_PROJECT`;

ALTER TABLE `sales` ADD `project` int(4) default NULL COMMENT 'Identificador del Proyecto' AFTER `id`;

ALTER TABLE `sales` ADD KEY `IDX_SALES_PROJECT` (`project`);

ALTER TABLE `sales` ADD CONSTRAINT `FK_SALES_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `delivery` ADD `project` int(4) default NULL COMMENT 'Identificador del Proyecto' AFTER `id`;

ALTER TABLE `delivery` ADD KEY `IDX_DELIVERY_PROJECT` (`project`);

ALTER TABLE `delivery` ADD CONSTRAINT `FK_DELIVERY_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `purchase` ADD `project` int(4) default NULL COMMENT 'Identificador del Proyecto' AFTER `id`;

ALTER TABLE `purchase` ADD KEY `IDX_PURCHASE_PROJECT` (`project`);

ALTER TABLE `purchase` ADD CONSTRAINT `FK_PURCHASE_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `purchase_detail` ADD `project` int(4) default NULL COMMENT 'Identificador del Proyecto' AFTER `purchase`;

ALTER TABLE `purchase_detail` ADD KEY `IDX_PURCHASE_DETAIL_PROJECT` (`project`);

ALTER TABLE `purchase_detail` ADD CONSTRAINT `FK_PURCHASE_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `income` ADD `project` int(4) default NULL COMMENT 'Identificador del Proyecto' AFTER `id`;

ALTER TABLE `income` ADD KEY `IDX_INCOME_PROJECT` (`project`);

ALTER TABLE `income` ADD CONSTRAINT `FK_INCOME_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `income_detail` ADD `project` int(4) default NULL COMMENT 'Identificador del Proyecto' AFTER `income`;

ALTER TABLE `income_detail` ADD KEY `IDX_INCOME_DETAIL_PROJECT` (`project`);

ALTER TABLE `income_detail` ADD CONSTRAINT `FK_INCOME_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `invoice` ADD `project` int(4) default NULL COMMENT 'Identificador del Proyecto' AFTER `id`;

ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_PROJECT` (`project`);

ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `invoice_detail` ADD `project` int(4) default NULL COMMENT 'Identificador del Proyecto' AFTER `invoice`;

ALTER TABLE `invoice_detail` ADD KEY `IDX_INVOICE_DETAIL_PROJECT` (`project`);

ALTER TABLE `invoice_detail` ADD CONSTRAINT `FK_INVOICE_DETAIL_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `project` ADD `registry` int(4) default NULL COMMENT 'Identificador del Cliente (Potencial) asociado' AFTER `alias`;

UPDATE `project` SET `registry` = (SELECT `target` FROM `project_commercial` WHERE `project` = `project`.`id`) WHERE `commercial` = 1;

UPDATE `project` SET `registry` = (SELECT `target` FROM `project_tas` WHERE `project` = `project`.`id`) WHERE `tas` = 1;

ALTER TABLE `project` MODIFY `registry` int(4) NOT NULL COMMENT 'Identificador del Cliente (Potencial) asociado';

ALTER TABLE `project` ADD KEY `IDX_PROJECT_REGISTRY` (`registry`);

ALTER TABLE `project` ADD CONSTRAINT `FK_PROJECT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `project` ADD `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si el Proyecto esta activo o no';

UPDATE `project` SET `active` = 0 WHERE `commercial` = 1 AND id IN (SELECT `project` FROM `project_commercial` WHERE `status` = 3);

UPDATE `project` SET `active` = 0 WHERE `tas` = 1 AND id IN (SELECT `project` FROM `project_tas` WHERE `status` = 1);

ALTER TABLE `project_tas` ADD `workplace` int(4) NOT NULL default '1' COMMENT 'Identificador del Centro de Trabajo';

UPDATE `project_tas` SET `workplace` = (SELECT min(`id`) FROM `workplace` WHERE `active` = 1);

ALTER TABLE `project_tas` ADD KEY `IDX_PROJECT_TAS_WORKPLACE` (`workplace`);

ALTER TABLE `project_tas` ADD CONSTRAINT `FK_PROJECT_TAS_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `mk_template` ADD `subject` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Asunto de la Plantilla';

ALTER TABLE `mk_template` ADD `append_signature` tinyint(1) NOT NULL COMMENT 'Indica si la Plantilla incluye la firma o no';


UPDATE `db_version` SET `version_number` = '6.15.0';

COMMIT;
