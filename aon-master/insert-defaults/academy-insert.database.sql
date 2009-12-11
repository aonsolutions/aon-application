
SET FOREIGN_KEY_CHECKS=0;

#
# Data for the `tax` table  (LIMIT 0,500)
#

DELETE FROM `tax`;

INSERT INTO `tax` (`id`, `name`, `tax_type`, `percentage`, `surcharge`, `start_date`) VALUES 
  (1,'SIN IVA',1,0,0,'2000-01-01'),
  (2,'GENERAL',1,16,4,'2000-01-01'),
  (3,'REDUCIDO',1,7,1,'2000-01-01'),
  (4,'SUPERREDUCIDO',1,4,0.5,'2000-01-01'),
  (5,'IRPF',2,15,0,'2000-01-01');

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
