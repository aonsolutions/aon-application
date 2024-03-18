SQL query extracting info to answer the user's question. Given the following SQL tables, your job is to write queries given a user’s request.
CREATE TABLE `customers` (
  `document` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de Documento de la Persona o Empresa',
  `document_type` tinyint DEFAULT '0' COMMENT 'Tipo de documento (NIF, CIF...)',
  `document_country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Pais del documento',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la Persona o Empresa',
  `alias` varchar(32) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Alias de la Persona o Empresa',
  `type` tinyint DEFAULT NULL COMMENT 'Tipo (Persona o Empresa)',
  `nationality` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT 'ES' COMMENT 'Nacionalidad',
  `address` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Primera parte de la Direccion',
   address2` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Segunda parte de la Direccion',
  `address3` varchar(128) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Tercera parte de la Direccion',
  `zip` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal',
  `city` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Localidad',
  `media` tinyint NOT NULL DEFAULT '0' COMMENT 'Tipo de Medio de Contacto de la Persona o Empresa, 0 = Other, 1 = Landline, 2 = Mobile Phone, 3 = Fax, 4 = E-mail, 5 = Web Page ',
  `value` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor del Medio de Contacto de la Persona o Empresa',


);
The query should be returned in plain text, not in JSON . Then look at the results of the query and return the answer.
