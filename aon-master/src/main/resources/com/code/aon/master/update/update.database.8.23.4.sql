# Database: aon_master
# Version: Actualizacion de la version 8.23.4 a la version 8.24.0.
# Created by: rtrepiana

/*!40101 SET NAMES latin1 */;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;


REPLACE INTO `bonus_concept` 
(`id`	,
`domain`,
`expression`,
`description`,
`type` )  
VALUES
(3266 
,0
,'/*read-only*/CHECK(COEFICIENTE_PARCIALIDAD>=0.50,"La jornada debe ser al menos del 50%");TC2S="100,200,300,150,250,350,130,230,330";CHECK(TC2S.indexOf(TC2)>=0,"El contrato debe ser %s", TC2S);FIN_BONIF=DIA(AÑO(INICIO_CONTRATO,2),-1);CHECK(INICIO_NOMINA <= FIN_BONIF, "La Tarifa Reducida finalizo el %1$td/%1$tm/%1$tY", FIN_BONIF);DIAS_NO_BONIF=MAX(0,DIAS(FIN,FIN_BONIF));500.00  * (DIAS_COTIZADOS - DIAS_NO_BONIF) * COEFICIENTE_PARCIALIDAD / DIAS_MES * PORCENTAJE_CGC_E/100/**/'
,'TARIFA REDUCIDA REDUCCION RD-L 1/2015'
,NULL	
);


REPLACE INTO `bonus_concept` 
(`id`	,
`domain`,
`expression`,
`description`,
`type` )  
VALUES
(3268 
,0
,'/*read-only*/CHECK((C=COEFICIENTE_PARCIALIDAD)>=0.50,"La jornada debe ser al menos del 50%");T="100,200,300,150,250,350,130,230,330";CHECK(T.indexOf(TC2)>=0,"El contrato debe ser %s",T);I=AÑO((O=INICIO_CONTRATO),2);CHECK(FIN_NOMINA>=I,"La Tarifa Reducida (tercer año) comienza el %1$td/%1$tm/%1$tY",I);N=MAX(0,DIAS(I,(A=INICIO)));F=DIA(AÑO(O,3),-1);CHECK(A<=F,"La Tarifa Reducida (tercer año) finalizo el %1$td/%1$tm/%1$tY",F);N+=MAX(0,DIAS(FIN,F));250.00*(DIAS_COTIZADOS-N)*C/DIAS_MES*PORCENTAJE_CGC_E/100/**/'
,'TARIFA REDUCIDA.TERCER AÑO .REDUCCIÓN RD-L 1/2015'
,NULL	
);


REPLACE INTO `bonus_concept` 
(`id`	,
`domain`,
`expression`,
`description`,
`type` )  
VALUES
(3262 
,0
,'/*read-only*/
CHECK((C=COEFICIENTE_PARCIALIDAD)>=0.50,"La jornada debe ser al menos del 50%");TC2S="100,200,300";CHECK(TC2S.indexOf(TC2)>=0,"El contrato debe ser %s", TC2S);FIN_BONIF=DIA(AÑO(INICIO_CONTRATO,2),-1);CHECK(INICIO_NOMINA <= FIN_BONIF, "La Tarifa Plana finalizo el %1$td/%1$tm/%1$tY", FIN_BONIF);N=MAX(0,DIAS(FIN,FIN_BONIF));D = (C < 1.00 ? ( C < 75.00 ? 50.00 : 75.00 ) : 100.00);MAX(0,(CGC_E * ((DIAS_COTIZADOS-N)/DIAS_COTIZADOS)-(D * (DIAS_COTIZADOS-N)/DIAS_MES )))/**/'
,'REDUCCIONES RD-L 3/2014 TARIFA PLANA (PRIMEROS DOS AÑOS)'
,NULL	
);

REPLACE INTO `bonus_concept` 
(`id`	,
`domain`,
`expression`,
`description`,
`type` )  
VALUES
(3263 
,0
,'/*read-only*/
CHECK((C=COEFICIENTE_PARCIALIDAD)>=0.50,"La jornada debe ser al menos del 50%");T="100,200,300";CHECK(T.indexOf(TC2)>=0,"El contrato debe ser %s",T);I=AÑO(INICIO_CONTRATO,2);CHECK(FIN_NOMINA>=I,"La Tarifa Plana (tercer año) comienza el %1$td/%1$tm/%1$tY",I);N=MAX(0,DIAS(I,(A=INICIO)));F=DIA(AÑO(INICIO_CONTRATO,3),-1);CHECK(INICIO_NOMINA<=F,"La Tarifa Plana (tercer año) finalizo el %1$td/%1$tm/%1$tY",F);N+=MAX(0,DIAS(FIN,F));CGC_E * ((DIAS_COTIZADOS-N)/DIAS_COTIZADOS) * 0.50 /**/'
,'REDUCCIONES RD-L 3/2014 TARIFA PLANA (TERCER AÑO)'
,NULL	
);

UPDATE `db_version` SET `version_number` = '8.24.0';

COMMIT;


SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;




