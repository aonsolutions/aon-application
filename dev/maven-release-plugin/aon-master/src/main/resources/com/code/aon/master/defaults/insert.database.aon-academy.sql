
USE `aon_master`;

SET FOREIGN_KEY_CHECKS=0;

#
# Data for the `tax` table  (LIMIT 0,500)
#

DELETE FROM `tax_detail`;
DELETE FROM `tax`;

INSERT INTO `tax` (`id`, `name`, `tax_type`, `percentage`, `surcharge`, `start_date`) VALUES 
  (1,'SIN IVA',1,0,0,'2000-01-01',0,0),
  (2,'GENERAL',1,18,4,'2010-07-01',0,0),
  (3,'REDUCIDO',1,8,1,'2010-07-01',0,0),
  (4,'SUPERREDUCIDO',1,4,0.5,'2000-01-01',0,0),
  (5,'IRPF PROFESIONALES',2,18,0,'2000-01-01',0,0),
  (6,'IRPF ARRENDAMIENTOS',2,19,0,'2000-01-01',0,1); 

COMMIT;

#
# Data for the `tax_detail` table  (LIMIT 0,500)
#

INSERT INTO `tax_detail` (`id`, `tax`, `start_date`, `end_date`, `value`, `surcharge`) VALUES 
  (1,2,'2000-01-01','2010-06-30',16,4),
  (2,3,'2000-01-01','2010-06-30',7,1);

COMMIT;

#
# Data for the `customer_segment` table  (LIMIT 0,500)
#

INSERT INTO `customer_segment` (`id`, `description`) VALUES 
  (1,'Preinscrito'),
  (2,'Alumno'),
  (3,'Antiguo Alumno');

COMMIT;

SET FOREIGN_KEY_CHECKS=1;
