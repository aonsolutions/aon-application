# Database: aon_master
# Version: Actualizacion de la version 1.3.2 a la version 1.4.0
# Created by: girazu
# Creation Date: 26/03/2008 11:13
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `geotree` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `parent` int(4) default NULL COMMENT 'Identificador de la Zona Geografica Padre',
  `child` int(4) NOT NULL COMMENT 'Identificador de la Zona Geografica Hijo',
  PRIMARY KEY  (`id`),
  KEY `parent` (`parent`),
  KEY `child` (`child`),
  CONSTRAINT `geotree_fk1` FOREIGN KEY (`parent`) REFERENCES `geozone` (`id`),
  CONSTRAINT `geotree_fk2` FOREIGN KEY (`child`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Jerarquia de Zonas Geograficas';

UPDATE `geozone` SET name = 'A CORUÑA' WHERE id = 15;

UPDATE `geozone` SET name = 'LA RIOJA' WHERE id = 26;

UPDATE `geozone` SET name = 'LAS PALMAS' WHERE id = 35;

INSERT INTO `geozone` (id, name) VALUES (53, 'ESPAÑA');

UPDATE `geozone` SET id = 0 WHERE id = 53;

INSERT INTO `geotree` (parent, child) VALUES (NULL, 0);

INSERT INTO `geotree` (parent, child) VALUES (0, 1);

INSERT INTO `geotree` (parent, child) VALUES (0, 2);

INSERT INTO `geotree` (parent, child) VALUES (0, 3);

INSERT INTO `geotree` (parent, child) VALUES (0, 4);

INSERT INTO `geotree` (parent, child) VALUES (0, 5);

INSERT INTO `geotree` (parent, child) VALUES (0, 6);

INSERT INTO `geotree` (parent, child) VALUES (0, 7);

INSERT INTO `geotree` (parent, child) VALUES (0, 8);

INSERT INTO `geotree` (parent, child) VALUES (0, 9);

INSERT INTO `geotree` (parent, child) VALUES (0, 10);

INSERT INTO `geotree` (parent, child) VALUES (0, 11);

INSERT INTO `geotree` (parent, child) VALUES (0, 12);

INSERT INTO `geotree` (parent, child) VALUES (0, 13);

INSERT INTO `geotree` (parent, child) VALUES (0, 14);

INSERT INTO `geotree` (parent, child) VALUES (0, 15);

INSERT INTO `geotree` (parent, child) VALUES (0, 16);

INSERT INTO `geotree` (parent, child) VALUES (0, 17);

INSERT INTO `geotree` (parent, child) VALUES (0, 18);

INSERT INTO `geotree` (parent, child) VALUES (0, 19);

INSERT INTO `geotree` (parent, child) VALUES (0, 20);

INSERT INTO `geotree` (parent, child) VALUES (0, 21);

INSERT INTO `geotree` (parent, child) VALUES (0, 22);

INSERT INTO `geotree` (parent, child) VALUES (0, 23);

INSERT INTO `geotree` (parent, child) VALUES (0, 24);

INSERT INTO `geotree` (parent, child) VALUES (0, 25);

INSERT INTO `geotree` (parent, child) VALUES (0, 26);

INSERT INTO `geotree` (parent, child) VALUES (0, 27);

INSERT INTO `geotree` (parent, child) VALUES (0, 28);

INSERT INTO `geotree` (parent, child) VALUES (0, 29);

INSERT INTO `geotree` (parent, child) VALUES (0, 30);

INSERT INTO `geotree` (parent, child) VALUES (0, 31);

INSERT INTO `geotree` (parent, child) VALUES (0, 32);

INSERT INTO `geotree` (parent, child) VALUES (0, 33);

INSERT INTO `geotree` (parent, child) VALUES (0, 34);

INSERT INTO `geotree` (parent, child) VALUES (0, 35);

INSERT INTO `geotree` (parent, child) VALUES (0, 36);

INSERT INTO `geotree` (parent, child) VALUES (0, 37);

INSERT INTO `geotree` (parent, child) VALUES (0, 38);

INSERT INTO `geotree` (parent, child) VALUES (0, 39);

INSERT INTO `geotree` (parent, child) VALUES (0, 40);

INSERT INTO `geotree` (parent, child) VALUES (0, 41);

INSERT INTO `geotree` (parent, child) VALUES (0, 42);

INSERT INTO `geotree` (parent, child) VALUES (0, 43);

INSERT INTO `geotree` (parent, child) VALUES (0, 44);

INSERT INTO `geotree` (parent, child) VALUES (0, 45);

INSERT INTO `geotree` (parent, child) VALUES (0, 46);

INSERT INTO `geotree` (parent, child) VALUES (0, 47);

INSERT INTO `geotree` (parent, child) VALUES (0, 48);

INSERT INTO `geotree` (parent, child) VALUES (0, 49);

INSERT INTO `geotree` (parent, child) VALUES (0, 50);

INSERT INTO `geotree` (parent, child) VALUES (0, 51);

INSERT INTO `geotree` (parent, child) VALUES (0, 52);

UPDATE `invoice` SET `status` = 1 WHERE `status` = 2;

CREATE TABLE `invoice_address` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',
  `address` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Primera parte de la Direccion',
  `address2` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Segunda parte de la Direccion',
  `zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo Postal',
  `city` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Localidad',
  `geozone` int(4) NOT NULL COMMENT 'Identificador de la Zona Geografica',
  PRIMARY KEY  (`id`),
  KEY `invoice` (`invoice`),
  KEY `geozone` (`geozone`),
  CONSTRAINT `invoice_address_fk1` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`),
  CONSTRAINT `invoice_address_fk` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Direcciones de la Factura';

UPDATE `invoice` SET `raddress` = (SELECT `raddress`.`id` FROM `raddress` WHERE `raddress`.`registry` = `invoice`.`registry` AND `raddress`.`type` = 0) WHERE `raddress` IS NULL;

ALTER TABLE `workactivity` ADD `active` tinyint(1) default '1' COMMENT 'Indica si la Actividad esta activa o no';

UPDATE `workactivity` SET `active` = 1;

ALTER TABLE `workplace` ADD `active` tinyint(1) default '1' COMMENT 'Indica si el Centro de Trabajo esta activo o no';

UPDATE `workplace` SET `active` = 1;


UPDATE `db_version` SET `version_number` = '1.4.0';

COMMIT;
