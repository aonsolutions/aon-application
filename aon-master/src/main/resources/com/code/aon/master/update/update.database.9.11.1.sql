# Database: aon_master
# Version: Actualizacion de la version 9.11.1 a la version 9.12.0
# Created by: rtrepiana
# Creation Date: 03/11/2017 

BEGIN;

REPLACE INTO `bonus_concept`
(`domain`,
`expression`,
`description`,
`type` )
VALUES
(0
,'/*read-only*/TRAMO(FIN_BONIFICACION);TRAMO(DIA(INICIO_BONIFICACION,-1));(DIAS_BONIFICACION == 30 )?CUOTA_EMPRESARIAL 
:(CUOTA_EMPRESARIAL/(DIAS(FIN_NOMINA,INICIO_NOMINA)+1) * DIAS_BONIFICACION)/**/'
,'INTERINIDAD POR DESCANSO MATERNIDAD, EMBARAZO, RIESGO LACTANCIA, ETC.'
,NULL
);


UPDATE `db_version` SET `version_number` = '9.12.0';

COMMIT;
