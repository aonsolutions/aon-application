
USE `aon_master`;

SET FOREIGN_KEY_CHECKS=0;

SET @Domain = 1;

DELETE FROM `tax_detail`;
DELETE FROM `tax`;

INSERT INTO `tax` VALUES 
  (1,@Domain,'SIN IVA',1,0,0,'2000-01-01',0,0),
  (2,@Domain,'GENERAL',1,21,4,'2012-09-01',0,0),
  (3,@Domain,'REDUCIDO',1,10,1,'2012-09-01',0,0),
  (4,@Domain,'SUPERREDUCIDO',1,4,0.5,'2000-01-01',0,0),
  (5,@Domain,'IRPF PROFESIONALES',2,18,0,'2000-01-01',0,0),
  (6,@Domain,'IRPF ARRENDAMIENTOS',2,19,0,'2000-01-01',0,1); 

INSERT INTO `tax_detail` VALUES 
  (1,@Domain,2,'2000-01-01','2010-06-30',16,4),
  (2,@Domain,3,'2000-01-01','2010-06-30',7,1),
  (1,@Domain,2,'2010-07-01','2012-08-31',18,4),
  (2,@Domain,3,'2010-07-01','2012-08-31',8,1);

INSERT INTO `customer_segment` VALUES 
  (1,'Preinscrito'),
  (2,'Alumno'),
  (3,'Antiguo Alumno');

COMMIT;

SET FOREIGN_KEY_CHECKS=1;
