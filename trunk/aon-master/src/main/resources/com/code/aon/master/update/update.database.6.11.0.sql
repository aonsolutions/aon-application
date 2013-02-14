# Database: aon_master
# Version: Actualizacion de la version 6.11.0 a la version 6.12.0.
# Created by: rtrepiana
# Creation Date: 12/08/2011
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;


CREATE TABLE `geozone_irpf` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `geozone` int(4) NOT NULL COMMENT 'Identificador de la Zona Geografica',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `amount` double(15,3) default '0.000' COMMENT 'Importe rendimiento anual',
  PRIMARY KEY  (`id`),
  KEY `IDX_GEOZONE_IRPF_GEOZONE` (`geozone`),
  CONSTRAINT `FK_GEOZONE_IRPF_GEOZONE` FOREIGN KEY (`geozone`) REFERENCES `geozone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de tramos del IRPF';

CREATE TABLE `geozone_irpf_descendant` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `geozone_irpf` int(4) default NULL COMMENT 'Identificador del tramo de IRPF',
  `descendant` tinyint(2) default '0' COMMENT 'Descendientes',
  `percent` double(15,2) default '0.00' COMMENT 'Porcentaje',
  PRIMARY KEY  (`id`),
  KEY `IDX_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF` (`geozone_irpf`),
  CONSTRAINT `FK_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de porcentajes IRPF segun descendientes';

CREATE TABLE `geozone_irpf_handicap` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `geozone_irpf` int(4) default NULL COMMENT 'Identificador del tramo de IRPF',
  `handicap` tinyint(2) default '0' COMMENT 'Grado Minusvalia',
  `percent` double(15,2) default '0.00' COMMENT 'Porcentaje',
  PRIMARY KEY  (`id`),
  KEY `IDX_GEOZONE_IRPF_PERCENT_GEOZONE_IRPF` (`geozone_irpf`),
  CONSTRAINT `FK_GEOZONE_IRPF_PERCENT_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tabla de  ';

set @ARABA=1; 
set @GIPUZKOA=20; 
set @NAVARRA=31; 
set @BIZKAIA=48; 

INSERT INTO geozone_irpf (geozone, start_date, end_date, amount ) VALUES
(1, '2011-01-01', '2011-12-31', 0.00 ),(20, '2011-01-01', '2011-12-31', 0.00 ), (48, '2011-01-01', '2011-12-31', 0.00 ),
(1, '2011-01-01', '2011-12-31', 12230.01 ),(20, '2011-01-01', '2011-12-31', 12230.01 ), (48, '2011-01-01', '2011-12-31', 12230.01 ),
(1, '2011-01-01', '2011-12-31', 12720.01 ),(20, '2011-01-01', '2011-12-31', 12720.01 ), (48, '2011-01-01', '2011-12-31', 12720.01 ),
(1, '2011-01-01', '2011-12-31', 13250.01 ),(20, '2011-01-01', '2011-12-31', 13250.01 ), (48, '2011-01-01', '2011-12-31', 13250.01 ),
(1, '2011-01-01', '2011-12-31', 13820.01 ),(20, '2011-01-01', '2011-12-31', 13820.01 ), (48, '2011-01-01', '2011-12-31', 13820.01 ),
(1, '2011-01-01', '2011-12-31', 14450.01 ),(20, '2011-01-01', '2011-12-31', 14450.01 ), (48, '2011-01-01', '2011-12-31', 14450.01 ),
(1, '2011-01-01', '2011-12-31', 15140.01 ),(20, '2011-01-01', '2011-12-31', 15140.01 ), (48, '2011-01-01', '2011-12-31', 15140.01 ),
(1, '2011-01-01', '2011-12-31', 15900.01 ),(20, '2011-01-01', '2011-12-31', 15900.01 ), (48, '2011-01-01', '2011-12-31', 15900.01 ),
(1, '2011-01-01', '2011-12-31', 16970.01 ),(20, '2011-01-01', '2011-12-31', 16970.01 ), (48, '2011-01-01', '2011-12-31', 16970.01 ),
(1, '2011-01-01', '2011-12-31', 18260.01 ),(20, '2011-01-01', '2011-12-31', 18260.01 ), (48, '2011-01-01', '2011-12-31', 18260.01 ),
(1, '2011-01-01', '2011-12-31', 19460.01 ),(20, '2011-01-01', '2011-12-31', 19460.01 ), (48, '2011-01-01', '2011-12-31', 19460.01 ),
(1, '2011-01-01', '2011-12-31', 20630.01 ),(20, '2011-01-01', '2011-12-31', 20630.01 ), (48, '2011-01-01', '2011-12-31', 20630.01 ),
(1, '2011-01-01', '2011-12-31', 21560.01 ),(20, '2011-01-01', '2011-12-31', 21560.01 ), (48, '2011-01-01', '2011-12-31', 21560.01 ),
(1, '2011-01-01', '2011-12-31', 22790.01 ),(20, '2011-01-01', '2011-12-31', 22790.01 ), (48, '2011-01-01', '2011-12-31', 22790.01 ),
(1, '2011-01-01', '2011-12-31', 23910.01 ),(20, '2011-01-01', '2011-12-31', 23910.01 ), (48, '2011-01-01', '2011-12-31', 23910.01 ),
(1, '2011-01-01', '2011-12-31', 25830.01 ),(20, '2011-01-01', '2011-12-31', 25830.01 ), (48, '2011-01-01', '2011-12-31', 25830.01 ),
(1, '2011-01-01', '2011-12-31', 28260.01 ),(20, '2011-01-01', '2011-12-31', 28260.01 ), (48, '2011-01-01', '2011-12-31', 28260.01 ),
(1, '2011-01-01', '2011-12-31', 31200.01 ),(20, '2011-01-01', '2011-12-31', 31200.01 ), (48, '2011-01-01', '2011-12-31', 31200.01 ),
(1, '2011-01-01', '2011-12-31', 34810.01 ),(20, '2011-01-01', '2011-12-31', 34810.01 ), (48, '2011-01-01', '2011-12-31', 34810.01 ),
(1, '2011-01-01', '2011-12-31', 37820.01 ),(20, '2011-01-01', '2011-12-31', 37820.01 ), (48, '2011-01-01', '2011-12-31', 37820.01 ),
(1, '2011-01-01', '2011-12-31', 40490.01 ),(20, '2011-01-01', '2011-12-31', 40490.01 ), (48, '2011-01-01', '2011-12-31', 40490.01 ),
(1, '2011-01-01', '2011-12-31', 43350.01 ),(20, '2011-01-01', '2011-12-31', 43350.01 ), (48, '2011-01-01', '2011-12-31', 43350.01 ),
(1, '2011-01-01', '2011-12-31', 46670.01 ),(20, '2011-01-01', '2011-12-31', 46670.01 ), (48, '2011-01-01', '2011-12-31', 46670.01 ),
(1, '2011-01-01', '2011-12-31', 50540.01 ),(20, '2011-01-01', '2011-12-31', 50540.01 ), (48, '2011-01-01', '2011-12-31', 50540.01 ),
(1, '2011-01-01', '2011-12-31', 53700.01 ),(20, '2011-01-01', '2011-12-31', 53700.01 ), (48, '2011-01-01', '2011-12-31', 53700.01 ),
(1, '2011-01-01', '2011-12-31', 57260.01 ),(20, '2011-01-01', '2011-12-31', 57260.01 ), (48, '2011-01-01', '2011-12-31', 57260.01 ),
(1, '2011-01-01', '2011-12-31', 61320.01 ),(20, '2011-01-01', '2011-12-31', 61320.01 ), (48, '2011-01-01', '2011-12-31', 61320.01 ),
(1, '2011-01-01', '2011-12-31', 66010.01 ),(20, '2011-01-01', '2011-12-31', 66010.01 ), (48, '2011-01-01', '2011-12-31', 66010.01 ),
(1, '2011-01-01', '2011-12-31', 71060.01 ),(20, '2011-01-01', '2011-12-31', 71060.01 ), (48, '2011-01-01', '2011-12-31', 71060.01 ),
(1, '2011-01-01', '2011-12-31', 75470.01 ),(20, '2011-01-01', '2011-12-31', 75470.01 ), (48, '2011-01-01', '2011-12-31', 75470.01 ),
(1, '2011-01-01', '2011-12-31', 80490.01 ),(20, '2011-01-01', '2011-12-31', 80490.01 ), (48, '2011-01-01', '2011-12-31', 80490.01 ),
(1, '2011-01-01', '2011-12-31', 86190.01 ),(20, '2011-01-01', '2011-12-31', 86190.01 ), (48, '2011-01-01', '2011-12-31', 86190.01 ),
(1, '2011-01-01', '2011-12-31', 92780.01 ),(20, '2011-01-01', '2011-12-31', 92780.01 ), (48, '2011-01-01', '2011-12-31', 92780.01 ),
(1, '2011-01-01', '2011-12-31', 100450.01 ),(20, '2011-01-01', '2011-12-31', 100450.01 ), (48, '2011-01-01', '2011-12-31', 100450.01 ),
(1, '2011-01-01', '2011-12-31', 109500.01 ),(20, '2011-01-01', '2011-12-31', 109500.01 ), (48, '2011-01-01', '2011-12-31', 109500.01 ),
(1, '2011-01-01', '2011-12-31', 120310.01 ),(20, '2011-01-01', '2011-12-31', 120310.01 ), (48, '2011-01-01', '2011-12-31', 120310.01 ),
(1, '2011-01-01', '2011-12-31', 133500.01 ),(20, '2011-01-01', '2011-12-31', 133500.01 ), (48, '2011-01-01', '2011-12-31', 133500.01 ),
(1, '2011-01-01', '2011-12-31', 149790.01 ),(20, '2011-01-01', '2011-12-31', 149790.01 ), (48, '2011-01-01', '2011-12-31', 149790.01 ),
(1, '2011-01-01', '2011-12-31', 169760.01 ),(20, '2011-01-01', '2011-12-31', 169760.01 ), (48, '2011-01-01', '2011-12-31', 169760.01 ),
(1, '2011-01-01', '2011-12-31', 195880.01 ),(20, '2011-01-01', '2011-12-31', 195880.01 ), (48, '2011-01-01', '2011-12-31', 195880.01 ),
(1, '2011-01-01', '2011-12-31', 231490.01 ),(20, '2011-01-01', '2011-12-31', 231490.01 ), (48, '2011-01-01', '2011-12-31', 231490.01 );

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=0.00 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 0),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=0.00 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 0),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=0.00 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 0),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12230.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12230.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12230.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12720.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12720.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12720.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 0),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13250.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 1),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13250.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 1),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13250.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 1),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13820.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 2),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13820.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 2),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=13820.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 2),(@IRPF, 2, 0),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=14450.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 3),(@IRPF, 2, 1),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=14450.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 3),(@IRPF, 2, 1),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=14450.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 3),(@IRPF, 2, 1),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15140.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 4),(@IRPF, 2, 2),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15140.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 4),(@IRPF, 2, 2),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15140.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 4),(@IRPF, 2, 2),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15900.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 7),(@IRPF, 1, 5),(@IRPF, 2, 3),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15900.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 7),(@IRPF, 1, 5),(@IRPF, 2, 3),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=15900.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 7),(@IRPF, 1, 5),(@IRPF, 2, 3),(@IRPF, 3, 0),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=16970.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 8),(@IRPF, 1, 6),(@IRPF, 2, 5),(@IRPF, 3, 1),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=16970.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 8),(@IRPF, 1, 6),(@IRPF, 2, 5),(@IRPF, 3, 1),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=16970.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 8),(@IRPF, 1, 6),(@IRPF, 2, 5),(@IRPF, 3, 1),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=18260.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 9),(@IRPF, 1, 8),(@IRPF, 2, 6),(@IRPF, 3, 3),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=18260.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 9),(@IRPF, 1, 8),(@IRPF, 2, 6),(@IRPF, 3, 3),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=18260.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 9),(@IRPF, 1, 8),(@IRPF, 2, 6),(@IRPF, 3, 3),(@IRPF, 4, 0),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=19460.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 10),(@IRPF, 1, 9),(@IRPF, 2, 7),(@IRPF, 3, 4),(@IRPF, 4, 1),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=19460.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 10),(@IRPF, 1, 9),(@IRPF, 2, 7),(@IRPF, 3, 4),(@IRPF, 4, 1),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=19460.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 10),(@IRPF, 1, 9),(@IRPF, 2, 7),(@IRPF, 3, 4),(@IRPF, 4, 1),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=20630.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 11),(@IRPF, 1, 10),(@IRPF, 2, 8),(@IRPF, 3, 5),(@IRPF, 4, 2),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=20630.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 11),(@IRPF, 1, 10),(@IRPF, 2, 8),(@IRPF, 3, 5),(@IRPF, 4, 2),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=20630.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 11),(@IRPF, 1, 10),(@IRPF, 2, 8),(@IRPF, 3, 5),(@IRPF, 4, 2),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=21560.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 12),(@IRPF, 1, 11),(@IRPF, 2, 9),(@IRPF, 3, 7),(@IRPF, 4, 3),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=21560.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 12),(@IRPF, 1, 11),(@IRPF, 2, 9),(@IRPF, 3, 7),(@IRPF, 4, 3),(@IRPF, 5, 0),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=21560.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 12),(@IRPF, 1, 11),(@IRPF, 2, 9),(@IRPF, 3, 7),(@IRPF, 4, 3),(@IRPF, 5, 0),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22790.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 13),(@IRPF, 1, 12),(@IRPF, 2, 10),(@IRPF, 3, 8),(@IRPF, 4, 4),(@IRPF, 5, 1),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22790.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 13),(@IRPF, 1, 12),(@IRPF, 2, 10),(@IRPF, 3, 8),(@IRPF, 4, 4),(@IRPF, 5, 1),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22790.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 13),(@IRPF, 1, 12),(@IRPF, 2, 10),(@IRPF, 3, 8),(@IRPF, 4, 4),(@IRPF, 5, 1),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=23910.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 14),(@IRPF, 1, 13),(@IRPF, 2, 11),(@IRPF, 3, 9),(@IRPF, 4, 6),(@IRPF, 5, 2),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=23910.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 14),(@IRPF, 1, 13),(@IRPF, 2, 11),(@IRPF, 3, 9),(@IRPF, 4, 6),(@IRPF, 5, 2),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=23910.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 14),(@IRPF, 1, 13),(@IRPF, 2, 11),(@IRPF, 3, 9),(@IRPF, 4, 6),(@IRPF, 5, 2),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=25830.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 15),(@IRPF, 1, 14),(@IRPF, 2, 13),(@IRPF, 3, 10),(@IRPF, 4, 8),(@IRPF, 5, 4),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=25830.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 15),(@IRPF, 1, 14),(@IRPF, 2, 13),(@IRPF, 3, 10),(@IRPF, 4, 8),(@IRPF, 5, 4),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=25830.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 15),(@IRPF, 1, 14),(@IRPF, 2, 13),(@IRPF, 3, 10),(@IRPF, 4, 8),(@IRPF, 5, 4),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28260.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 16),(@IRPF, 1, 15),(@IRPF, 2, 14),(@IRPF, 3, 12),(@IRPF, 4, 9),(@IRPF, 5, 6),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28260.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 16),(@IRPF, 1, 15),(@IRPF, 2, 14),(@IRPF, 3, 12),(@IRPF, 4, 9),(@IRPF, 5, 6),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28260.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 16),(@IRPF, 1, 15),(@IRPF, 2, 14),(@IRPF, 3, 12),(@IRPF, 4, 9),(@IRPF, 5, 6),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=31200.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 17),(@IRPF, 1, 16),(@IRPF, 2, 15),(@IRPF, 3, 13),(@IRPF, 4, 11),(@IRPF, 5, 8),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=31200.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 17),(@IRPF, 1, 16),(@IRPF, 2, 15),(@IRPF, 3, 13),(@IRPF, 4, 11),(@IRPF, 5, 8),(@IRPF, 6, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=31200.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 17),(@IRPF, 1, 16),(@IRPF, 2, 15),(@IRPF, 3, 13),(@IRPF, 4, 11),(@IRPF, 5, 8),(@IRPF, 6, 0);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=34810.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 18),(@IRPF, 1, 17),(@IRPF, 2, 16),(@IRPF, 3, 14),(@IRPF, 4, 12),(@IRPF, 5, 10),(@IRPF, 6, 2);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=34810.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 18),(@IRPF, 1, 17),(@IRPF, 2, 16),(@IRPF, 3, 14),(@IRPF, 4, 12),(@IRPF, 5, 10),(@IRPF, 6, 2);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=34810.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 18),(@IRPF, 1, 17),(@IRPF, 2, 16),(@IRPF, 3, 14),(@IRPF, 4, 12),(@IRPF, 5, 10),(@IRPF, 6, 2);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=37820.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 19),(@IRPF, 1, 18),(@IRPF, 2, 17),(@IRPF, 3, 16),(@IRPF, 4, 14),(@IRPF, 5, 11),(@IRPF, 6, 4);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=37820.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 19),(@IRPF, 1, 18),(@IRPF, 2, 17),(@IRPF, 3, 16),(@IRPF, 4, 14),(@IRPF, 5, 11),(@IRPF, 6, 4);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=37820.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 19),(@IRPF, 1, 18),(@IRPF, 2, 17),(@IRPF, 3, 16),(@IRPF, 4, 14),(@IRPF, 5, 11),(@IRPF, 6, 4);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=40490.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 20),(@IRPF, 1, 19),(@IRPF, 2, 19),(@IRPF, 3, 17),(@IRPF, 4, 15),(@IRPF, 5, 13),(@IRPF, 6, 7);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=40490.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 20),(@IRPF, 1, 19),(@IRPF, 2, 19),(@IRPF, 3, 17),(@IRPF, 4, 15),(@IRPF, 5, 13),(@IRPF, 6, 7);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=40490.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 20),(@IRPF, 1, 19),(@IRPF, 2, 19),(@IRPF, 3, 17),(@IRPF, 4, 15),(@IRPF, 5, 13),(@IRPF, 6, 7);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=43350.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 21),(@IRPF, 1, 20),(@IRPF, 2, 20),(@IRPF, 3, 18),(@IRPF, 4, 17),(@IRPF, 5, 15),(@IRPF, 6, 9);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=43350.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 21),(@IRPF, 1, 20),(@IRPF, 2, 20),(@IRPF, 3, 18),(@IRPF, 4, 17),(@IRPF, 5, 15),(@IRPF, 6, 9);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=43350.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 21),(@IRPF, 1, 20),(@IRPF, 2, 20),(@IRPF, 3, 18),(@IRPF, 4, 17),(@IRPF, 5, 15),(@IRPF, 6, 9);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=46670.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 22),(@IRPF, 1, 21),(@IRPF, 2, 21),(@IRPF, 3, 20),(@IRPF, 4, 18),(@IRPF, 5, 16),(@IRPF, 6, 11);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=46670.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 22),(@IRPF, 1, 21),(@IRPF, 2, 21),(@IRPF, 3, 20),(@IRPF, 4, 18),(@IRPF, 5, 16),(@IRPF, 6, 11);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=46670.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 22),(@IRPF, 1, 21),(@IRPF, 2, 21),(@IRPF, 3, 20),(@IRPF, 4, 18),(@IRPF, 5, 16),(@IRPF, 6, 11);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=50540.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 23),(@IRPF, 1, 22),(@IRPF, 2, 22),(@IRPF, 3, 21),(@IRPF, 4, 19),(@IRPF, 5, 18),(@IRPF, 6, 12);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=50540.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 23),(@IRPF, 1, 22),(@IRPF, 2, 22),(@IRPF, 3, 21),(@IRPF, 4, 19),(@IRPF, 5, 18),(@IRPF, 6, 12);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=50540.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 23),(@IRPF, 1, 22),(@IRPF, 2, 22),(@IRPF, 3, 21),(@IRPF, 4, 19),(@IRPF, 5, 18),(@IRPF, 6, 12);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=53700.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 24),(@IRPF, 1, 23),(@IRPF, 2, 23),(@IRPF, 3, 22),(@IRPF, 4, 21),(@IRPF, 5, 19),(@IRPF, 6, 14);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=53700.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 24),(@IRPF, 1, 23),(@IRPF, 2, 23),(@IRPF, 3, 22),(@IRPF, 4, 21),(@IRPF, 5, 19),(@IRPF, 6, 14);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=53700.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 24),(@IRPF, 1, 23),(@IRPF, 2, 23),(@IRPF, 3, 22),(@IRPF, 4, 21),(@IRPF, 5, 19),(@IRPF, 6, 14);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=57260.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 25),(@IRPF, 1, 25),(@IRPF, 2, 24),(@IRPF, 3, 23),(@IRPF, 4, 22),(@IRPF, 5, 20),(@IRPF, 6, 16);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=57260.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 25),(@IRPF, 1, 25),(@IRPF, 2, 24),(@IRPF, 3, 23),(@IRPF, 4, 22),(@IRPF, 5, 20),(@IRPF, 6, 16);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=57260.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 25),(@IRPF, 1, 25),(@IRPF, 2, 24),(@IRPF, 3, 23),(@IRPF, 4, 22),(@IRPF, 5, 20),(@IRPF, 6, 16);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=61320.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 26),(@IRPF, 1, 26),(@IRPF, 2, 25),(@IRPF, 3, 24),(@IRPF, 4, 23),(@IRPF, 5, 22),(@IRPF, 6, 17);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=61320.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 26),(@IRPF, 1, 26),(@IRPF, 2, 25),(@IRPF, 3, 24),(@IRPF, 4, 23),(@IRPF, 5, 22),(@IRPF, 6, 17);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=61320.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 26),(@IRPF, 1, 26),(@IRPF, 2, 25),(@IRPF, 3, 24),(@IRPF, 4, 23),(@IRPF, 5, 22),(@IRPF, 6, 17);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=66010.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 27),(@IRPF, 1, 27),(@IRPF, 2, 26),(@IRPF, 3, 25),(@IRPF, 4, 24),(@IRPF, 5, 23),(@IRPF, 6, 19);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=66010.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 27),(@IRPF, 1, 27),(@IRPF, 2, 26),(@IRPF, 3, 25),(@IRPF, 4, 24),(@IRPF, 5, 23),(@IRPF, 6, 19);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=66010.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 27),(@IRPF, 1, 27),(@IRPF, 2, 26),(@IRPF, 3, 25),(@IRPF, 4, 24),(@IRPF, 5, 23),(@IRPF, 6, 19);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=71060.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 28),(@IRPF, 1, 28),(@IRPF, 2, 27),(@IRPF, 3, 26),(@IRPF, 4, 25),(@IRPF, 5, 24),(@IRPF, 6, 20);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=71060.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 28),(@IRPF, 1, 28),(@IRPF, 2, 27),(@IRPF, 3, 26),(@IRPF, 4, 25),(@IRPF, 5, 24),(@IRPF, 6, 20);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=71060.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 28),(@IRPF, 1, 28),(@IRPF, 2, 27),(@IRPF, 3, 26),(@IRPF, 4, 25),(@IRPF, 5, 24),(@IRPF, 6, 20);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=75470.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 29),(@IRPF, 1, 29),(@IRPF, 2, 28),(@IRPF, 3, 27),(@IRPF, 4, 27),(@IRPF, 5, 25),(@IRPF, 6, 22);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=75470.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 29),(@IRPF, 1, 29),(@IRPF, 2, 28),(@IRPF, 3, 27),(@IRPF, 4, 27),(@IRPF, 5, 25),(@IRPF, 6, 22);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=75470.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 29),(@IRPF, 1, 29),(@IRPF, 2, 28),(@IRPF, 3, 27),(@IRPF, 4, 27),(@IRPF, 5, 25),(@IRPF, 6, 22);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=80490.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 30),(@IRPF, 1, 30),(@IRPF, 2, 29),(@IRPF, 3, 29),(@IRPF, 4, 28),(@IRPF, 5, 27),(@IRPF, 6, 23);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=80490.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 30),(@IRPF, 1, 30),(@IRPF, 2, 29),(@IRPF, 3, 29),(@IRPF, 4, 28),(@IRPF, 5, 27),(@IRPF, 6, 23);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=80490.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 30),(@IRPF, 1, 30),(@IRPF, 2, 29),(@IRPF, 3, 29),(@IRPF, 4, 28),(@IRPF, 5, 27),(@IRPF, 6, 23);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=86190.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 31),(@IRPF, 1, 31),(@IRPF, 2, 30),(@IRPF, 3, 30),(@IRPF, 4, 29),(@IRPF, 5, 28),(@IRPF, 6, 25);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=86190.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 31),(@IRPF, 1, 31),(@IRPF, 2, 30),(@IRPF, 3, 30),(@IRPF, 4, 29),(@IRPF, 5, 28),(@IRPF, 6, 25);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=86190.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 31),(@IRPF, 1, 31),(@IRPF, 2, 30),(@IRPF, 3, 30),(@IRPF, 4, 29),(@IRPF, 5, 28),(@IRPF, 6, 25);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=92780.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 32),(@IRPF, 1, 32),(@IRPF, 2, 31),(@IRPF, 3, 31),(@IRPF, 4, 30),(@IRPF, 5, 29),(@IRPF, 6, 26);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=92780.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 32),(@IRPF, 1, 32),(@IRPF, 2, 31),(@IRPF, 3, 31),(@IRPF, 4, 30),(@IRPF, 5, 29),(@IRPF, 6, 26);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=92780.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 32),(@IRPF, 1, 32),(@IRPF, 2, 31),(@IRPF, 3, 31),(@IRPF, 4, 30),(@IRPF, 5, 29),(@IRPF, 6, 26);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=100450.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 33),(@IRPF, 1, 33),(@IRPF, 2, 32),(@IRPF, 3, 32),(@IRPF, 4, 31),(@IRPF, 5, 30),(@IRPF, 6, 28);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=100450.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 33),(@IRPF, 1, 33),(@IRPF, 2, 32),(@IRPF, 3, 32),(@IRPF, 4, 31),(@IRPF, 5, 30),(@IRPF, 6, 28);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=100450.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 33),(@IRPF, 1, 33),(@IRPF, 2, 32),(@IRPF, 3, 32),(@IRPF, 4, 31),(@IRPF, 5, 30),(@IRPF, 6, 28);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=109500.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 34),(@IRPF, 1, 34),(@IRPF, 2, 33),(@IRPF, 3, 33),(@IRPF, 4, 32),(@IRPF, 5, 32),(@IRPF, 6, 29);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=109500.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 34),(@IRPF, 1, 34),(@IRPF, 2, 33),(@IRPF, 3, 33),(@IRPF, 4, 32),(@IRPF, 5, 32),(@IRPF, 6, 29);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=109500.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 34),(@IRPF, 1, 34),(@IRPF, 2, 33),(@IRPF, 3, 33),(@IRPF, 4, 32),(@IRPF, 5, 32),(@IRPF, 6, 29);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=120310.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 35),(@IRPF, 1, 35),(@IRPF, 2, 35),(@IRPF, 3, 34),(@IRPF, 4, 34),(@IRPF, 5, 33),(@IRPF, 6, 31);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=120310.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 35),(@IRPF, 1, 35),(@IRPF, 2, 35),(@IRPF, 3, 34),(@IRPF, 4, 34),(@IRPF, 5, 33),(@IRPF, 6, 31);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=120310.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 35),(@IRPF, 1, 35),(@IRPF, 2, 35),(@IRPF, 3, 34),(@IRPF, 4, 34),(@IRPF, 5, 33),(@IRPF, 6, 31);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=133500.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 36),(@IRPF, 1, 36),(@IRPF, 2, 36),(@IRPF, 3, 35),(@IRPF, 4, 35),(@IRPF, 5, 34),(@IRPF, 6, 32);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=133500.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 36),(@IRPF, 1, 36),(@IRPF, 2, 36),(@IRPF, 3, 35),(@IRPF, 4, 35),(@IRPF, 5, 34),(@IRPF, 6, 32);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=133500.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 36),(@IRPF, 1, 36),(@IRPF, 2, 36),(@IRPF, 3, 35),(@IRPF, 4, 35),(@IRPF, 5, 34),(@IRPF, 6, 32);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=149790.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 37),(@IRPF, 1, 37),(@IRPF, 2, 37),(@IRPF, 3, 36),(@IRPF, 4, 36),(@IRPF, 5, 35),(@IRPF, 6, 34);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=149790.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 37),(@IRPF, 1, 37),(@IRPF, 2, 37),(@IRPF, 3, 36),(@IRPF, 4, 36),(@IRPF, 5, 35),(@IRPF, 6, 34);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=149790.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 37),(@IRPF, 1, 37),(@IRPF, 2, 37),(@IRPF, 3, 36),(@IRPF, 4, 36),(@IRPF, 5, 35),(@IRPF, 6, 34);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=169760.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 38),(@IRPF, 1, 38),(@IRPF, 2, 38),(@IRPF, 3, 37),(@IRPF, 4, 37),(@IRPF, 5, 36),(@IRPF, 6, 35);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=169760.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 38),(@IRPF, 1, 38),(@IRPF, 2, 38),(@IRPF, 3, 37),(@IRPF, 4, 37),(@IRPF, 5, 36),(@IRPF, 6, 35);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=169760.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 38),(@IRPF, 1, 38),(@IRPF, 2, 38),(@IRPF, 3, 37),(@IRPF, 4, 37),(@IRPF, 5, 36),(@IRPF, 6, 35);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=195880.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 39),(@IRPF, 1, 39),(@IRPF, 2, 39),(@IRPF, 3, 38),(@IRPF, 4, 38),(@IRPF, 5, 38),(@IRPF, 6, 36);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=195880.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 39),(@IRPF, 1, 39),(@IRPF, 2, 39),(@IRPF, 3, 38),(@IRPF, 4, 38),(@IRPF, 5, 38),(@IRPF, 6, 36);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=195880.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 39),(@IRPF, 1, 39),(@IRPF, 2, 39),(@IRPF, 3, 38),(@IRPF, 4, 38),(@IRPF, 5, 38),(@IRPF, 6, 36);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=231490.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 40),(@IRPF, 1, 40),(@IRPF, 2, 40),(@IRPF, 3, 40),(@IRPF, 4, 39),(@IRPF, 5, 39),(@IRPF, 6, 38);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=231490.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 40),(@IRPF, 1, 40),(@IRPF, 2, 40),(@IRPF, 3, 40),(@IRPF, 4, 39),(@IRPF, 5, 39),(@IRPF, 6, 38);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=231490.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 40),(@IRPF, 1, 40),(@IRPF, 2, 40),(@IRPF, 3, 40),(@IRPF, 4, 39),(@IRPF, 5, 39),(@IRPF, 6, 38);


SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22790.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 11),(@IRPF, 2, 11);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22790.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 11),(@IRPF, 2, 11);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=22790.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 6),(@IRPF, 1, 11),(@IRPF, 2, 11);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28260.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 10),(@IRPF, 2, 10);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28260.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 10),(@IRPF, 2, 10);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28260.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 10),(@IRPF, 2, 10);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=40490.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 8),(@IRPF, 2, 8);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=40490.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 8),(@IRPF, 2, 8);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=40490.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 4),(@IRPF, 1, 8),(@IRPF, 2, 8);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=57260.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 6),(@IRPF, 2, 6);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=57260.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 6),(@IRPF, 2, 6);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=57260.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 6),(@IRPF, 2, 6);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=86190.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 5),(@IRPF, 2, 5);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=86190.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 5),(@IRPF, 2, 5);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=86190.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 5),(@IRPF, 2, 5);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=149790.01 AND geozone=@ARABA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 3),(@IRPF, 2, 3);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=149790.01 AND geozone=@GIPUZKOA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 3),(@IRPF, 2, 3);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=149790.01 AND geozone=@BIZKAIA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 1),(@IRPF, 1, 3),(@IRPF, 2, 3);

INSERT INTO geozone_irpf (geozone, start_date, end_date, amount ) VALUES
(31, '2011-01-01', '2011-12-31', 0.00 ),
(31, '2011-01-01', '2011-12-31', 10000.01 ),
(31, '2011-01-01', '2011-12-31', 11250.01 ),
(31, '2011-01-01', '2011-12-31', 12750.01 ),
(31, '2011-01-01', '2011-12-31', 14250.01 ),
(31, '2011-01-01', '2011-12-31', 16750.01 ),
(31, '2011-01-01', '2011-12-31', 19750.01 ),
(31, '2011-01-01', '2011-12-31', 23250.01 ),
(31, '2011-01-01', '2011-12-31', 25750.01 ),
(31, '2011-01-01', '2011-12-31', 28250.01 ),
(31, '2011-01-01', '2011-12-31', 32250.01 ),
(31, '2011-01-01', '2011-12-31', 35750.01 ),
(31, '2011-01-01', '2011-12-31', 41250.01 ),
(31, '2011-01-01', '2011-12-31', 48000.01 ),
(31, '2011-01-01', '2011-12-31', 55000.01 ),
(31, '2011-01-01', '2011-12-31', 62000.01 ),
(31, '2011-01-01', '2011-12-31', 69250.01 ),
(31, '2011-01-01', '2011-12-31', 75250.01 ),
(31, '2011-01-01', '2011-12-31', 82250.01 ),
(31, '2011-01-01', '2011-12-31', 94750.01 ),
(31, '2011-01-01', '2011-12-31', 107250.01 ),
(31, '2011-01-01', '2011-12-31', 120000.01 ),
(31, '2011-01-01', '2011-12-31', 132750.01 ),
(31, '2011-01-01', '2011-12-31', 146000.01 );

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=0.00 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 0.00),(@IRPF, 1, 0.00),(@IRPF, 2, 0.00),(@IRPF, 3, 0.00),(@IRPF, 4, 0.00),(@IRPF, 5, 0.00),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=10000.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 2.00),(@IRPF, 1, 0.00),(@IRPF, 2, 0.00),(@IRPF, 3, 0.00),(@IRPF, 4, 0.00),(@IRPF, 5, 0.00),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=11250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 4.00),(@IRPF, 1, 2.00),(@IRPF, 2, 0.00),(@IRPF, 3, 0.00),(@IRPF, 4, 0.00),(@IRPF, 5, 0.00),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=12750.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 6.00),(@IRPF, 1, 4.00),(@IRPF, 2, 2.00),(@IRPF, 3, 0.00),(@IRPF, 4, 0.00),(@IRPF, 5, 0.00),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=14250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 8.00),(@IRPF, 1, 6.00),(@IRPF, 2, 4.00),(@IRPF, 3, 2.00),(@IRPF, 4, 0.00),(@IRPF, 5, 0.00),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=16750.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 10.00),(@IRPF, 1, 8.00),(@IRPF, 2, 6.00),(@IRPF, 3, 4.00),(@IRPF, 4, 1.00),(@IRPF, 5, 0.00),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=19750.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 12.00),(@IRPF, 1, 11.00),(@IRPF, 2, 9.50),(@IRPF, 3, 8.00),(@IRPF, 4, 6.00),(@IRPF, 5, 4.00),(@IRPF, 6, 0.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=23250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 13.50),(@IRPF, 1, 12.00),(@IRPF, 2, 11.50),(@IRPF, 3, 9.00),(@IRPF, 4, 8.00),(@IRPF, 5, 6.00),(@IRPF, 6, 4.00),(@IRPF, 7, 0.00),(@IRPF, 8, 0.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=25750.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 14.50),(@IRPF, 1, 13.00),(@IRPF, 2, 12.50),(@IRPF, 3, 10.00),(@IRPF, 4, 9.50),(@IRPF, 5, 8.00),(@IRPF, 6, 6.00),(@IRPF, 7, 4.00),(@IRPF, 8, 1.00),(@IRPF, 9, 0.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=28250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 15.50),(@IRPF, 1, 14.00),(@IRPF, 2, 13.50),(@IRPF, 3, 12.00),(@IRPF, 4, 10.50),(@IRPF, 5, 9.00),(@IRPF, 6, 8.00),(@IRPF, 7, 6.00),(@IRPF, 8, 4.00),(@IRPF, 9, 1.00),(@IRPF, 10, 0.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=32250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 16.50),(@IRPF, 1, 15.00),(@IRPF, 2, 14.50),(@IRPF, 3, 13.00),(@IRPF, 4, 12.50),(@IRPF, 5, 11.00),(@IRPF, 6, 10.00),(@IRPF, 7, 8.00),(@IRPF, 8, 7.00),(@IRPF, 9, 5.00),(@IRPF, 10, 2.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=35750.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 17.50),(@IRPF, 1, 16.50),(@IRPF, 2, 16.50),(@IRPF, 3, 14.00),(@IRPF, 4, 13.50),(@IRPF, 5, 13.00),(@IRPF, 6, 12.00),(@IRPF, 7, 10.00),(@IRPF, 8, 9.00),(@IRPF, 9, 7.00),(@IRPF, 10, 6.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=41250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 18.50),(@IRPF, 1, 17.50),(@IRPF, 2, 17.50),(@IRPF, 3, 16.00),(@IRPF, 4, 15.50),(@IRPF, 5, 15.00),(@IRPF, 6, 13.00),(@IRPF, 7, 12.00),(@IRPF, 8, 11.00),(@IRPF, 9, 10.00),(@IRPF, 10, 9.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=48000.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 20.50),(@IRPF, 1, 20.50),(@IRPF, 2, 20.00),(@IRPF, 3, 18.00),(@IRPF, 4, 17.50),(@IRPF, 5, 17.00),(@IRPF, 6, 16.00),(@IRPF, 7, 15.00),(@IRPF, 8, 14.00),(@IRPF, 9, 13.00),(@IRPF, 10, 12.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=55000.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 23.00),(@IRPF, 1, 22.50),(@IRPF, 2, 22.00),(@IRPF, 3, 20.50),(@IRPF, 4, 20.50),(@IRPF, 5, 20.00),(@IRPF, 6, 19.00),(@IRPF, 7, 18.00),(@IRPF, 8, 17.00),(@IRPF, 9, 16.00),(@IRPF, 10, 14.50);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=62000.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 25.00),(@IRPF, 1, 24.50),(@IRPF, 2, 23.50),(@IRPF, 3, 23.50),(@IRPF, 4, 22.50),(@IRPF, 5, 22.00),(@IRPF, 6, 21.00),(@IRPF, 7, 20.50),(@IRPF, 8, 19.00),(@IRPF, 9, 18.00),(@IRPF, 10, 16.50);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=69250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 27.00),(@IRPF, 1, 26.50),(@IRPF, 2, 26.00),(@IRPF, 3, 25.50),(@IRPF, 4, 24.00),(@IRPF, 5, 24.00),(@IRPF, 6, 23.50),(@IRPF, 7, 22.00),(@IRPF, 8, 21.00),(@IRPF, 9, 19.50),(@IRPF, 10, 18.50);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=75250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 28.00),(@IRPF, 1, 28.00),(@IRPF, 2, 27.00),(@IRPF, 3, 26.00),(@IRPF, 4, 26.00),(@IRPF, 5, 25.00),(@IRPF, 6, 25.00),(@IRPF, 7, 23.50),(@IRPF, 8, 22.50),(@IRPF, 9, 21.50),(@IRPF, 10, 20.50);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=82250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 29.00),(@IRPF, 1, 29.00),(@IRPF, 2, 29.00),(@IRPF, 3, 28.00),(@IRPF, 4, 28.00),(@IRPF, 5, 27.00),(@IRPF, 6, 26.50),(@IRPF, 7, 25.50),(@IRPF, 8, 24.50),(@IRPF, 9, 23.50),(@IRPF, 10, 23.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=94750.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 30.00),(@IRPF, 1, 30.00),(@IRPF, 2, 30.00),(@IRPF, 3, 29.00),(@IRPF, 4, 29.00),(@IRPF, 5, 29.00),(@IRPF, 6, 28.00),(@IRPF, 7, 27.50),(@IRPF, 8, 26.50),(@IRPF, 9, 25.50),(@IRPF, 10, 24.50);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=107250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 32.00),(@IRPF, 1, 32.00),(@IRPF, 2, 32.00),(@IRPF, 3, 31.00),(@IRPF, 4, 30.50),(@IRPF, 5, 30.00),(@IRPF, 6, 29.50),(@IRPF, 7, 28.50),(@IRPF, 8, 28.00),(@IRPF, 9, 27.00),(@IRPF, 10, 26.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=120000.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 33.00),(@IRPF, 1, 33.00),(@IRPF, 2, 32.50),(@IRPF, 3, 32.00),(@IRPF, 4, 31.50),(@IRPF, 5, 31.00),(@IRPF, 6, 30.50),(@IRPF, 7, 29.50),(@IRPF, 8, 29.00),(@IRPF, 9, 28.00),(@IRPF, 10, 27.50);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=132750.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 33.50),(@IRPF, 1, 33.50),(@IRPF, 2, 33.00),(@IRPF, 3, 32.50),(@IRPF, 4, 32.00),(@IRPF, 5, 31.50),(@IRPF, 6, 31.00),(@IRPF, 7, 30.50),(@IRPF, 8, 30.00),(@IRPF, 9, 29.00),(@IRPF, 10, 28.50);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=146000.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_descendant (geozone_irpf, descendant, percent ) VALUES
(@IRPF, 0, 34.00),(@IRPF, 1, 34.00),(@IRPF, 2, 34.00),(@IRPF, 3, 33.50),(@IRPF, 4, 33.00),(@IRPF, 5, 32.50),(@IRPF, 6, 32.00),(@IRPF, 7, 31.50),(@IRPF, 8, 31.00),(@IRPF, 9, 30.50),(@IRPF, 10, 30.00);

SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=0.00 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 0),(@IRPF, 1, 0),(@IRPF, 2, 0);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=10000.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 5),(@IRPF, 1, 5),(@IRPF, 2, 15);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=23250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 3),(@IRPF, 1, 3),(@IRPF, 2, 15);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=41250.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 2),(@IRPF, 2, 8);
SET @IRPF=(SELECT id FROM geozone_irpf WHERE amount=94750.01 AND geozone=@NAVARRA);
INSERT INTO geozone_irpf_handicap (geozone_irpf, handicap, percent ) VALUES
(@IRPF, 0, 2),(@IRPF, 1, 2),(@IRPF, 2, 5);


UPDATE `db_version` SET `version_number` = '6.12.0';

COMMIT;
