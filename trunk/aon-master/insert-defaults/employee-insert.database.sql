
SET FOREIGN_KEY_CHECKS=0;

#
# Data for the `expenditures_items` table  (LIMIT 0,500)
#

INSERT IGNORE INTO `expenditures_items` (`id`, `name`) VALUES 
  (1,'SALARIO BASE'),
  (2,'COMPLEMENTOS SALARIALES'),
  (3,'OTROS SUPLIDOS'),
  (4,'S.SOCIAL DE EMPRESA');

COMMIT;

#
# Data for the `cv_evaluate_type` table  (LIMIT 0,500)
#

INSERT IGNORE INTO `cv_evaluate_type` (`id`, `name`) VALUES 
  (1,'PRESENCIA (imagen)'),
  (2,'SOCIABILIDAD (amabilidad, educación)'),
  (3,'EXPERIENCIA COMERCIAL'),
  (4,'CONOCIMIENTO DEL SECTOR'),
  (5,'GRADO DE MOTIVACIÓN AL PUESTO'),
  (6,'ACTITUD PERSONAL EN LA ENTREVISTA');

COMMIT;

#
# Data for the `incidence_type` table  (LIMIT 0,500)
#

INSERT IGNORE INTO `incidence_type` (`id`, `alias`, `description`, `compute`) VALUES 
  (2,'BE','BAJA POR ENFERMEDAD',0),
  (3,'BA','BAJA POR ACCIDENTE',0),
  (4,'PR','PERMISOS RETRIBUIDOS',1),
  (5,'F','FALTAS',-1),
  (6,'SM','SALIDAS MEDICO',0),
  (7,'PNR','PERMISO NO RETRIBUIDO',-1),
  (8,'V','VIAJE',0),
  (9,'HE','HORAS EXTRA',1);

COMMIT;

SET FOREIGN_KEY_CHECKS=1;
