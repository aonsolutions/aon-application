# Database: aon_master
# Version: Actualizacion de la version 7.0.10 a la version 7.0.11.
# Created by: girazu
# Creation Date: 02/04/2012 14:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `hotel` ADD `item_advance` int(4) default NULL COMMENT 'Identificador del Producto para anticipos' AFTER `service_catalogue`;
ALTER TABLE `hotel` ADD KEY `IDX_HOTEL_ITEM_ADVANCE` (`item_advance`);
ALTER TABLE `hotel` ADD CONSTRAINT `FK_HOTEL_ITEM_ADVANCE` FOREIGN KEY (`item_advance`) REFERENCES `item` (`id`);

ALTER TABLE `hotel` ADD `item_no_show` int(4) default NULL COMMENT 'Identificador del Producto para no-show' AFTER `item_advance`;
ALTER TABLE `hotel` ADD KEY `IDX_HOTEL_ITEM_NO_SHOW` (`item_no_show`);
ALTER TABLE `hotel` ADD CONSTRAINT `FK_HOTEL_ITEM_NO_SHOW` FOREIGN KEY (`item_no_show`) REFERENCES `item` (`id`);

ALTER TABLE `project_reservation` MODIFY `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel de Produccion';
ALTER TABLE `project_reservation` ADD `hotel_reservation` int(4) NOT NULL COMMENT 'Identificador del Hotel de la Reserva' AFTER `hotel`;
UPDATE `project_reservation` SET `hotel_reservation` = `hotel`;
ALTER TABLE `project_reservation` ADD KEY `IDX_PROJECT_RESERVATION_HOTEL_RESERVATION` (`hotel_reservation`);
ALTER TABLE `project_reservation` ADD CONSTRAINT `FK_PROJECT_RESERVATION_HOTEL_RESERVATION` FOREIGN KEY (`hotel_reservation`) REFERENCES `hotel` (`id`);

CREATE TABLE `project_reservation_divert` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `request_hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel solicitante',
  `divert_hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel destino',
  `divert_date` date NOT NULL COMMENT 'Fecha de Desvio',
  `status` tinyint(2) default '0' COMMENT 'Estado del Desvio',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_RESERVATION_DIVERT_DOMAIN` (`domain`),
  KEY `IDX_PROJECT_RESERVATION_DIVERT_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_DIVERT_REQUEST_HOTEL` (`request_hotel`),
  KEY `IDX_PROJECT_RESERVATION_DIVERT_DIVERT_HOTEL` (`divert_hotel`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_REQUEST_HOTEL` FOREIGN KEY (`request_hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_DIVERT_DIVERT_HOTEL` FOREIGN KEY (`divert_hotel`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Desvio de Reservas';
 
CREATE TABLE `contact` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',  
  `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `displayName` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Mostrar Como',
  `name` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Nombre',  
  `surname` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Apellido',
  `address` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Direccion',
  `postalCode` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo postal',
  `city` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Localidad',
  `contactState` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Estado',
  `country` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Pais',
  `phone` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Telefono',  
  `cellularPhone` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Movil',
  `fax` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Fax',
  `email` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Email',
  `note` text collate latin1_spanish_ci COMMENT 'Nota',       
  `organization` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Organización',
  `title` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Cargo',
  `organizationAddress` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Dirección de la Organización',
  `organizationPostalCode` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo postal de la Organización',
  `organizationCity` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Localidad de la Organización',
  `organizationState` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Estado de la Organización',
  `organizationPhone` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Telefono de la Organización',  
  `organizationFax` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Fax de la Organización',
  `web` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Web',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTACT_USER` (`user_id`),
  CONSTRAINT `FK_CONTACT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)  
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Contactos';

CREATE TABLE `contact_group` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',  
  `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `displayName` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Mostrar Como',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTACT_GROUP_USER` (`user_id`),
  CONSTRAINT `FK_CONTACT_GROUP_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)  
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Grupo de Contactos';

CREATE TABLE `contact_group_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `contact_group` int(4) NOT NULL COMMENT 'Identificador del Grupo de Contactos',
  `contact` int(4) NOT NULL COMMENT 'Identificador del Contacto',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTACT_GROUP_DETAIL_CONTACT_GROUP` (`contact_group`),
  KEY `IDX_CONTACT_GROUP_DETAIL_CONTACT` (`contact`),
  CONSTRAINT `FK_CONTACT_GROUP_DETAIL_CONTACT_GROUP` FOREIGN KEY (`contact_group`) REFERENCES `contact_group` (`id`),
  CONSTRAINT `FK_CONTACT_GROUP_DETAIL_CONTACT` FOREIGN KEY (`contact`) REFERENCES `contact` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Grupo de Contactos';

ALTER TABLE `user` ADD `passwordExpiration` date default NULL COMMENT 'Fecha de Expiracion de la Contraseña';

ALTER TABLE `domain` ADD `description` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Dominio' AFTER `name`;
ALTER TABLE `domain` ADD `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Dominio' AFTER `parent`;
ALTER TABLE `domain` ADD `subDomainSuffix` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Sufijo de los Dominio Hijo' AFTER `type`;
ALTER TABLE `domain` ADD `userManagement` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Dominio tiene capacidad de MultiUsuario o no' AFTER `subDomainSuffix`;
ALTER TABLE `domain` ADD `domainManagement` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Dominio tiene capacidad de MultiDominio o no' AFTER `userManagement`;
ALTER TABLE `domain` ADD `documentManagement` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Dominio tiene capacidad de Documental o no' AFTER `domainManagement`;
ALTER TABLE `domain` ADD `maxDocumentSize` int(4) default NULL COMMENT 'Tamaño Maximo de los Documentos' AFTER `documentManagement`;
ALTER TABLE `domain` ADD `maxTotalDocumentSize` int(4) default NULL COMMENT 'Almacenamiento Documental Contratado' AFTER `maxDocumentSize`;
ALTER TABLE `domain` ADD `maxDefinedUsers` int(4) default NULL COMMENT 'Numero Maximo de Usuarios' AFTER `maxTotalDocumentSize`;

RENAME TABLE `application` TO `domain_application`;

CREATE TABLE `application` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Aplicacion',
  `description` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion de la Aplicacion',
  `contratable` tinyint(1) NOT NULL default '0' COMMENT 'Indica si la Aplicacion es contratable o no',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Aplicaciones';

INSERT INTO `application` VALUES 
  (1,'aon-academy','ACADEMY',1),
  (2,'aon-account','ACCOUNT',1),
  (3,'aon-asset','RECURSOS',1),
  (4,'aon-audit','AUDIT',1),
  (5,'aon-cms','CMS',1),
  (6,'aon-commercial','COMMERCIAL',1),
  (7,'aon-communicator','COMMUNICATOR',1),
  (8,'aon-consultant','CONSULTANT',1),
  (9,'aon-document','DOCUMENT',1),
  (10,'aon-desktop','DESKTOP',1),
  (11,'aon-ebackoffice','EBACKOFFICE',1),
  (12,'aon-ecm','ECM',1),
  (13,'aon-ecommerce','ECOMMERCE',1),
  (14,'aon-employee','EMPLOYEE',1),
  (15,'aon-enterprise','ENTERPRISE',1),
  (16,'aon-finance','FINANCE',1),
  (17,'aon-fiscal','FISCAL',1),
  (18,'aon-gt','GT',1),
  (19,'aon-manager','MANAGER',1),
  (20,'aon-payroll','PAYROLL',1),
  (21,'aon-pms','PMS',1),
  (22,'aon-project','PROJECT',1),
  (23,'aon-publisher','PUBLISHER',1),
  (24,'aon-smb','SMB',1),
  (25,'aon-task','TASK',1),
  (26,'aon-webinfo','INFOWEB',1),
  (27,'aon-webmail','WEBMAIL',0),
  (28,'aon-aio','AIO',1);

ALTER TABLE `action_denied` DROP FOREIGN KEY `FK_ACTION_DENIED_DOMAIN`;
ALTER TABLE `action_denied` DROP KEY `IDX_ACTION_DENIED_DOMAIN`;
ALTER TABLE `action_denied` DROP `domain`; 

ALTER TABLE `action_entry` DROP FOREIGN KEY `FK_ACTION_ENTRY_DOMAIN`;
ALTER TABLE `action_entry` DROP KEY `IDX_ACTION_ENTRY_DOMAIN`;
ALTER TABLE `action_entry` DROP `domain`; 

ALTER TABLE `action_favorite` DROP FOREIGN KEY `FK_ACTION_FAVORITE_DOMAIN`;
ALTER TABLE `action_favorite` DROP KEY `IDX_ACTION_FAVORITE_DOMAIN`;
ALTER TABLE `action_favorite` DROP `domain`; 

ALTER TABLE `action` DROP FOREIGN KEY `FK_ACTION_DOMAIN`;
ALTER TABLE `action` DROP KEY `IDX_ACTION_DOMAIN`;
ALTER TABLE `action` DROP `domain`;
ALTER TABLE `action` DROP FOREIGN KEY `FK_ACTION_APPLICATION`;
ALTER TABLE `action` CHANGE `application_id` `application` int(4) NOT NULL COMMENT 'Aplicacion a la que pertenece la Accion';
UPDATE `action`, `application`, `domain_application` SET `action`.`application` = `application`.`id` WHERE `action`.`application` = `domain_application`.`id` AND `domain_application`.`name` = `application`.`name`;  
ALTER TABLE `action` ADD CONSTRAINT `FK_ACTION_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`);

ALTER TABLE `session` DROP FOREIGN KEY `FK_SESSION_DOMAIN`;
ALTER TABLE `session` DROP KEY `IDX_SESSION_DOMAIN`;
ALTER TABLE `session` DROP `domain`;
ALTER TABLE `session` DROP FOREIGN KEY `FK_SESSION_APPLICATION`;
ALTER TABLE `session` CHANGE `application_id` `application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion';
UPDATE `session`, `application`, `domain_application` SET `session`.`application` = `application`.`id` WHERE `session`.`application` = `domain_application`.`id` AND `domain_application`.`name` = `application`.`name`;  
ALTER TABLE `session` ADD CONSTRAINT `FK_SESSION_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`); 

ALTER TABLE `domain_application` ADD `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si la Aplicacion del Dominio esta activa o no' AFTER `domain`;
ALTER TABLE `domain_application` ADD `application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion' AFTER `domain`;
UPDATE `domain_application`, `application` SET `domain_application`.`application` = `application`.`id` WHERE `domain_application`.`name` = `application`.`name`;  
ALTER TABLE `domain_application` ADD KEY `IDX_DOMAIN_APPLICATION_APPLICATION` (`application`);
ALTER TABLE `domain_application` ADD CONSTRAINT `FK_DOMAIN_APPLICATION_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`);  
ALTER TABLE `domain_application` DROP KEY `IDX_UNQ_APPLICATION_DOMAIN_NAME`;
ALTER TABLE `domain_application` DROP `name`;

CREATE TABLE `role` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Role',  
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Roles';

CREATE TABLE `application_role` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion',
  `role` int(4) NOT NULL COMMENT 'Identificador del Role',
  PRIMARY KEY  (`id`),
  KEY `IDX_APPLICATION_ROLE_APPLICATION` (`application`),
  KEY `IDX_APPLICATION_ROLE_ROLE` (`role`),
  CONSTRAINT `FK_APPLICATION_ROLE_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_APPLICATION_ROLE_ROLE` FOREIGN KEY (`role`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Roles de la Aplicacion';

CREATE TABLE `profile` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Perfil',
  `application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion',
  `domain` int(4) default NULL COMMENT 'Identificador del Dominio',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROFILE_APPLICATION` (`application`),
  KEY `IDX_PROFILE_DOMAIN` (`domain`),
  CONSTRAINT `FK_PROFILE_APPLICATION` FOREIGN KEY (`application`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles';

CREATE TABLE `profile_role` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `profile` int(4) NOT NULL COMMENT 'Identificador del Perfil',
  `application_role` int(4) NOT NULL COMMENT 'Identificador del Role de la Aplicacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROFILE_ROLE_PROFILE` (`profile`),
  KEY `IDX_PROFILE_ROLE_APPLICATION_ROLE` (`application_role`),
  CONSTRAINT `FK_PROFILE_ROLE_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`),
  CONSTRAINT `FK_PROFILE_ROLE_APPLICATION_ROLE` FOREIGN KEY (`application_role`) REFERENCES `application_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Roles del Perfil';

CREATE TABLE `application_user` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario',
  `domain_application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion del Dominio',
  `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si la Aplicacion del Dominio esta activo o no',
  PRIMARY KEY  (`id`),
  KEY `IDX_APPLICATION_USER_USER` (`user_id`),
  KEY `IDX_APPLICATION_USER_DOMAIN_APPLICATION` (`domain_application`),
  CONSTRAINT `FK_APPLICATION_USER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_APPLICATION_USER_DOMAIN_APPLICATION` FOREIGN KEY (`domain_application`) REFERENCES `domain_application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Usuarios de las Aplicaciones del Dominio';

CREATE TABLE `application_user_profile` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `application_user` int(4) NOT NULL COMMENT 'Identificador del Usuario de la Aplicacion',  
  `profile` int(4) NOT NULL COMMENT 'Identificador del Perfil',
  PRIMARY KEY  (`id`),
  KEY `IDX_APPLICATION_USER_PROFILE_PROFILE` (`profile`),
  KEY `IDX_APPLICATION_USER_PROFILE_APPLICATION_USER` (`application_user`),
  CONSTRAINT `FK_APPLICATION_USER_PROFILE_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`),
  CONSTRAINT `FK_APPLICATION_USER_PROFILE_APPLICATION_USER` FOREIGN KEY (`application_user`) REFERENCES `application_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Perfiles del Usuario para la Aplicacion';

INSERT INTO `role` VALUES 
  (1,'User'),
  (2,'Guest'),
  (3,'Admin'),
  (4,'Config'),
  (5,'Auditor'),
  (6,'Confidentiality'),
  (7,'Product'),
  (8,'Commercial'),
  (9,'Sale'),
  (10,'Purchase'),
  (11,'Warehouse'),
  (12,'Accounting'),
  (13,'Finance'),
  (14,'Statistics'),
  (15,'TaskMonitoring'),
  (16,'eSignature'),
  (17,'SisAdmin'),
  (18,'Tgc'),
  (101,'Administrador'),
  (102,'Invitado'),
  (103,'Manager'),
  (104,'Designer'),
  (105,'Editor'),
  (106,'Revisor'),
  (107,'Publisher'),
  (108,'SMSSender'),
  (109,'FAXSender'),
  (110,'DocumentsVisible'),
  (111,'PredefinedDocumentsExecutable'),
  (112,'CorporativeDocumentsManager'),          
  (113,'Administrator'),
  (114,'ContentManagement'),
  (115,'Consultant'),
  (116,'Anonymous'),
  (1001,'Alumnos'),
  (1002,'AlumnosAusencias'),
  (1003,'AlumnosDatosAlumno'),
  (1004,'AlumnosDatosEconomicos'),
  (1005,'AlumnosDatosPersonales'),
  (1006,'AlumnosHistorial'),
  (1007,'AlumnosRelaciones'),
  (1008,'AlumnosSeguimiento'),
  (1009,'AuxiliaresAnyosAcademicos'),
  (1010,'AuxiliaresAptitudesCalidad'),
  (1011,'AuxiliaresBancos'),
  (1012,'AuxiliaresCalificaciones'),
  (1013,'AuxiliaresCategorias'),
  (1014,'AuxiliaresFormasDePago'),
  (1015,'AuxiliaresHabilidades'),
  (1016,'AuxiliaresMaterias'),
  (1017,'AuxiliaresNiveles'),
  (1018,'AuxiliaresObservaciones'),
  (1019,'AuxiliaresProductos'),
  (1020,'AuxiliaresTiposAlumno'),
  (1021,'AuxiliaresTiposRelaciones'),
  (1022,'BorradoFacturas'),
  (1023,'CentrosFormacion'),
  (1024,'ChecksTesoreria'),
  (1025,'CierreGrupos'),
  (1026,'ConfiguracionEjercicios'),
  (1027,'ConfiguracionImpuestos'),
  (1028,'ConfiguracionProvincias'),
  (1029,'ConfiguracionSeries'),
  (1030,'ConsultaNotas'),
  (1031,'ContabilizacionFacturas'),
  (1032,'CuotasAGrupos'),
  (1033,'DatosEmpresa'),
  (1034,'DevolucionVencimientos'),
  (1035,'DuplicarGrupos'),
  (1036,'FacturacionCuotas'),
  (1037,'FacturasVenta'),
  (1038,'GestionCartera'),
  (1039,'Grupos'),
  (1040,'GruposAlumnos'),
  (1041,'GruposAusencias'),
  (1042,'GruposCalificaciones'),
  (1043,'GruposFacturacion'),
  (1044,'GruposGrupo'),
  (1045,'GruposHabilidades'),
  (1046,'GruposObservaciones'),
  (1047,'GruposVCalidad'),
  (1048,'HabilidadesAGrupos'),
  (1049,'ImpresionFacturas'),
  (1050,'ListadoCuotas'),
  (1051,'MenuContabilidad'),
  (1052,'Prefacturacion'),
  (1053,'Prestamos'),
  (1054,'Profesores'),
  (1055,'ReclasificacionAlumnos'),
  (1056,'Remesas'),
  (1057,'Vencimientos'),
  (1058,'Invitado');  


UPDATE `db_version` SET `version_number` = '7.0.11';

COMMIT;
