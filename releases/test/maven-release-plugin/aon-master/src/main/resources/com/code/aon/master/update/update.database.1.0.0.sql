# Database: aon_master
# Version: Actualizacion de la version 1.0.0 a la version 1.1.0
# Created by: girazu
# Creation Date: 11/10/2007 12:46
# Comentarios: este script unicamente realiza los cambios necesarios en la estructura de la base de datos Aon.
#              Luego depende de cada uno el ir apañando los datos de sus aplicaciones a la nueva estructura.
#	       Cambios invalidantes: se ha añadido la columna scope en las tablas customer, supplier y creditor, y se ha 
#	       añadido la columna workplace en las tablas sales, offer, purchase, support_order e invoice_detail.


CREATE TABLE `series` (
  `id` varchar(5) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador unico',
  `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Serie',
  `workplace` int(4) NOT NULL COMMENT 'Centro de Trabajo para el que se define la Serie',
  `security_level` tinyint(2) NOT NULL COMMENT 'Nivel de seguridad de la Serie',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Serie esta activa o no',
  PRIMARY KEY  (`id`),
  KEY `workplace` (`workplace`),
  CONSTRAINT `series_fk` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Series';

CREATE TABLE `periodical_task` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `next_date` date default NULL COMMENT 'Fecha en la que se generara la siguiente Tarea',
  `period` tinyint(2) NOT NULL COMMENT 'Periodicidad de la Tarea',
  `quantity` int(4) default NULL COMMENT 'Numero de periodos para obtener la fecha de la siguiente Tarea',
  `task` int(4) NOT NULL COMMENT 'Tarea que se repite',
  `owner` int(4) NOT NULL COMMENT 'Propietario de la Tarea',
  PRIMARY KEY  (`id`),
  KEY `task` (`task`),
  KEY `owner` (`owner`),
  CONSTRAINT `periodical_task_fk` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
  CONSTRAINT `periodical_task_fk1` FOREIGN KEY (`owner`) REFERENCES `user` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tareas Periodicas';

CREATE TABLE `scope` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Ambito',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos';

CREATE TABLE `user_scope` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `user` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  PRIMARY KEY  (`id`),
  KEY `user` (`user`),
  KEY `scope` (`scope`),
  CONSTRAINT `user_scope_fk` FOREIGN KEY (`user`) REFERENCES `user` (`registry`),
  CONSTRAINT `user_scope_fk1` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ambitos de Usuario';

ALTER TABLE `calendar` ADD `allow_spread` tinyint(1) default '1' COMMENT 'Indica si el Calendario permite propagar eventos';

ALTER TABLE `item_pos` ADD `plu_product_type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Articulo en Balanza';

ALTER TABLE `invoice` MODIFY `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de la Factura';

ALTER TABLE `delivery` MODIFY `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Albaran';

ALTER TABLE `sales` MODIFY `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Pedido';

ALTER TABLE `income` MODIFY `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Albaran';

ALTER TABLE `purchase` MODIFY `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Pedido';

ALTER TABLE `offer` MODIFY `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie del Presupuesto';

ALTER TABLE `scale` MODIFY `serie` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de Albaran para la importacion de albaranes';

ALTER TABLE `support_order` MODIFY `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de la Orden de Reparacion';

ALTER TABLE `creditor` ADD `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';

ALTER TABLE `creditor` ADD KEY `scope` (`scope`);

ALTER TABLE `creditor` ADD CONSTRAINT `creditor_ibfk_2` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `supplier` ADD `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';

ALTER TABLE `supplier` ADD KEY `scope` (`scope`);

ALTER TABLE `supplier` ADD CONSTRAINT `supplier_ibfk_2` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `customer` ADD `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';

ALTER TABLE `customer` ADD KEY `scope` (`scope`);

ALTER TABLE `customer` ADD CONSTRAINT `customer_ibfk_3` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `sales` ADD `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo';

ALTER TABLE `sales` ADD KEY `workplace` (`workplace`);

ALTER TABLE `sales` ADD CONSTRAINT `sales_ibfk_6` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `offer` ADD `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo';

ALTER TABLE `offer` ADD KEY `workplace` (`workplace`);

ALTER TABLE `offer` ADD CONSTRAINT `offer_ibfk_4` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `purchase` ADD `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo';

ALTER TABLE `purchase` ADD KEY `workplace` (`workplace`);

ALTER TABLE `purchase` ADD CONSTRAINT `purchase_ibfk_3` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `support_order` ADD `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo';

ALTER TABLE `support_order` ADD KEY `workplace` (`workplace`);

ALTER TABLE `support_order` ADD CONSTRAINT `support_order_ibfk_4` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `invoice_detail` ADD `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo';

ALTER TABLE `invoice_detail` ADD KEY `workplace` (`workplace`);

ALTER TABLE `invoice_detail` ADD CONSTRAINT `invoice_detail_ibfk_3` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);


UPDATE `db_version` SET `version_number` = '1.1.0';

COMMIT;
