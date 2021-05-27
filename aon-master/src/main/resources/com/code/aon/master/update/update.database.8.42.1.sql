# Database: aon_master
# Version: Actualizacion de la version 8.42.1 a la version 8.42.2.
# Created by: girazu
# Creation Date: 18/02/2016 17:10

BEGIN;

ALTER TABLE `invest_asset` ADD `activity` int(4) default NULL COMMENT 'Identificador de la Actividad' AFTER `domain`;
ALTER TABLE `invest_asset` ADD KEY `IDX_INVEST_ASSET_ACTIVITY` (`activity`);
ALTER TABLE `invest_asset` ADD CONSTRAINT `FK_INVEST_ASSET_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`);

UPDATE `invoice_tax` SET `deductible_quota` = (`quota` + `surcharge_quota`) WHERE `tax_type` = 1 AND `deductible_percent` = 100;

UPDATE `bonus_concept` SET `expression` = '/read-only/ CHECK((C=COEFICIENTE_PARCIALIDAD)>=0.50,\"La jornada debe ser al menos del 50%\");TC2S=\"100,200,300\";CHECK(TC2S.indexOf(TC2)>=0,\"El contrato debe ser %s\", TC2S);FIN_BONIF=DIA(AÑO(INICIO_CONTRATO,2),-1);CHECK(INICIO_NOMINA <= FIN_BONIF, \"La Tarifa Plana finalizo el %1$td/%1$tm/%1$tY\", FIN_BONIF);N=MAX(0,DIAS(FIN,FIN_BONIF));D = (C < 1.00 ? ( C < 0.75 ? 50.00 : 75.00 ) : 100.00);MAX(0,(CGC_E * ((DIAS_COTIZADOS-N)/DIAS_COTIZADOS)-(D * (DIAS_COTIZADOS-N)/DIAS_MES )))/**/' WHERE `id` = 3262;


UPDATE `db_version` SET `version_number` = '8.42.2';

COMMIT;
