# Database: aon_master
# Version: Actualizacion de la version 7.30.0 a la version 7.31.0.
# Created by: rtrepiana
# Creation Date: 03/03/2014 
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN; 

# HORAS EXTRAORDINARIAS NO ESTRUCTURALES 002
UPDATE payment_concept SET type=2, description='HORAS EXTRAORDINARIAS NO ESTRUCTURALES' WHERE type=2 AND domain=0;
# HORAS EXTR. ESTRUCTURALES O FUERZA MAYOR 003
UPDATE payment_concept SET type=3, description='HORAS EXTR. ESTRUCTURALES O FUERZA MAYOR' WHERE type=3 AND domain=0;
# PAGAS EXTRAORDINARIAS.PRORRATEO
UPDATE payment_concept SET type=4, description='PAGA EXTRAORDINARIA' WHERE type=4 AND domain=0;

# R.ESPECIE NO INCLUIDA EN OTROS APARTADOS
UPDATE payment_concept SET type=13 WHERE type=5;
UPDATE payment_concept SET description='RETRIBUCIÓN EN ESPECIE' WHERE type=13 AND domain = 0;
UPDATE system_payment SET type=13 WHERE type=5;
UPDATE agreement_payment SET type=13 WHERE type=5;
UPDATE contract_payment SET type=13 WHERE type=5;

# INDEMNIZACIONES POR TRASLADOS
UPDATE payment_concept SET type=52 WHERE type=8;
UPDATE payment_concept SET description='INDEMNIZACIÓN POR TRASLADO' WHERE type=52 AND domain=0;
UPDATE system_payment SET type=52 WHERE type=8;
UPDATE agreement_payment SET type=52 WHERE type=8;
UPDATE contract_payment SET type=52 WHERE type=8;

# GASTOS DE ESTANCIA 
UPDATE payment_concept SET type=42 WHERE type = 1 AND description = 'Gastos de Estancia';
UPDATE system_payment SET type=42 WHERE type = 1 AND description = 'Gastos de Estancia';
UPDATE agreement_payment SET type=42 WHERE type = 1 AND description = 'Gastos de Estancia';
UPDATE contract_payment SET type=42 WHERE type = 1 AND description = 'Gastos de Estancia';

UPDATE payment_concept SET description='GASTOS DE ESTANCIA' WHERE type=42 AND domain=0;

# GASTOS MANUTENCIÓN SIN PERNOCTA ESPAÑA 
UPDATE payment_concept SET type=45 WHERE type = 1 AND description RLIKE 'Gastos de Manutenci.n';
UPDATE system_payment SET type=45 WHERE type = 1 AND description RLIKE 'Gastos de Manutenci.n';
UPDATE agreement_payment SET type=45 WHERE type = 1 AND description RLIKE 'Gastos de Manutenci.n';
UPDATE contract_payment SET type=45 WHERE type = 1 AND description RLIKE 'Gastos de Manutenci.n';

UPDATE payment_concept SET description=CONCAT('GASTOS MANUTENCIÓN ( @{IMPORTE_MANUTENCION} ', _ucs2 0xA4 ,' x @{DIAS_MANUTENCION} DÍAS )') WHERE type=45 AND domain=0;

# GASTOS MANUTENCIÓN PERNOCTA ESPAÑA 
UPDATE payment_concept SET type=43 WHERE type = 1 AND description RLIKE 'Gastos de Pernocta';
UPDATE system_payment SET type=43 WHERE type = 1 AND description RLIKE 'Gastos de Pernocta';
UPDATE agreement_payment SET type=43 WHERE type = 1 AND description RLIKE 'Gastos de Pernocta';
UPDATE contract_payment SET type=43 WHERE type = 1 AND description RLIKE 'Gastos de Pernocta';

UPDATE payment_concept SET description=CONCAT('GASTOS PERNOCTA ( @{IMPORTE_PERNOCTA} ', _ucs2 0xA4 ,' x @{DIAS_PERNOCTA} DÍAS )') WHERE type=43 AND domain=0;

# GASTOS MANUTENCIÓN SIN PERNOCTA EXTRANJERO 
UPDATE payment_concept SET type=46 WHERE type = 1 AND description RLIKE 'Gast.s de Manutenci.n en el Extranjero';
UPDATE system_payment SET type=46 WHERE type = 1 AND description RLIKE 'Gast.s de Manutenci.n en el Extranjero';
UPDATE agreement_payment SET type=46 WHERE type = 1 AND description RLIKE 'Gast.s de Manutenci.n en el Extranjero';
UPDATE contract_payment SET type=46 WHERE type = 1 AND description RLIKE 'Gast.s de Manutenci.n en el Extranjero';

UPDATE payment_concept SET description=CONCAT('GASTOS MANUTENCIÓN EXTRANJERO ( @{IMPORTE_MANUTENCION_EXTRANJERO} ', _ucs2 0xA4 ,' x @{DIAS_MANUTENCION_EXTRANJERO} DÍAS )') WHERE type=46 AND domain=0;

# GASTOS MANUTENCIÓN PERNOCTA EXTRANJERO 
UPDATE payment_concept SET type=44 WHERE type = 1 AND description RLIKE 'Gast.s de Pernocta en el Extranjero';
UPDATE system_payment SET type=44 WHERE type = 1 AND description RLIKE 'Gast.s de Pernocta en el Extranjero';
UPDATE agreement_payment SET type=44 WHERE type = 1 AND description RLIKE 'Gast.s de Pernocta en el Extranjero';
UPDATE contract_payment SET type=44 WHERE type = 1 AND description RLIKE 'Gast.s de Pernocta en el Extranjero';

UPDATE payment_concept SET description=CONCAT('GASTOS PERNOCTA EXTRANJERO ( @{IMPORTE_PERNOCTA_EXTRANJERO} ', _ucs2 0xA4 ,' x @{DIAS_PERNOCTA_EXTRANJERO} DÍAS )') WHERE type=44 AND domain=0;

# GASTOS DE LOCOMOCIÓN TRANSPORTE PÚBLICO
UPDATE payment_concept SET type=49 WHERE type = 1 AND description RLIKE 'Gast.s de Locomoci.n seg.n Factura';
UPDATE system_payment SET type=49 WHERE type = 1 AND description RLIKE 'Gast.s de Locomoci.n seg.n Factura';
UPDATE agreement_payment SET type=49 WHERE type = 1 AND description RLIKE 'Gast.s de Locomoci.n seg.n Factura';
UPDATE contract_payment SET type=49 WHERE type = 1 AND description RLIKE 'Gast.s de Locomoci.n seg.n Factura';

UPDATE payment_concept SET description='GASTOS DE LOCOMOCIÓN TRANSPORTE PÚBLICO' WHERE type=49 AND domain=0;

# GASTOS LOCOMOCIÓN SIN JUSTIFIC. IMPORTE
UPDATE payment_concept SET type=50 WHERE type = 1 AND description RLIKE 'Gast.s de Locomoci.n';
UPDATE system_payment SET type=50 WHERE type = 1 AND description RLIKE 'Gast.s de Locomoci.n';
UPDATE agreement_payment SET type=50 WHERE type = 1 AND description RLIKE 'Gast.s de Locomoci.n';
UPDATE contract_payment SET type=50 WHERE type = 1 AND description RLIKE 'Gast.s de Locomoci.n';

UPDATE payment_concept SET description=CONCAT('GASTOS LOCOMOCIÓN SIN JUSTIFICICANTE ( @{IMPORTE_KM} ', _ucs2 0xA4 ,' x @{KMS} KMS )') WHERE type=50 AND domain=0;


# INDEMNIZACIONES POR DESPIDO O CESE
UPDATE payment_concept SET type=54 WHERE type = 6 AND description RLIKE 'Indemnizaci.n por Despido';
UPDATE system_payment SET type=54 WHERE type = 6 AND description RLIKE 'Indemnizaci.n por Despido';
UPDATE agreement_payment SET type=54 WHERE type = 6 AND description RLIKE 'Indemnizaci.n por Despido';
UPDATE contract_payment SET type=54 WHERE type = 6 AND description RLIKE 'Indemnizaci.n por Despido';

UPDATE payment_concept SET description=CONCAT('INDEMNIZACIONES POR DESPIDO O CESE ( @{SALARIO_BRUTO_DIARIO} ', _ucs2 0xA4 ,' x @{DIAS_INDEMNIZACION * AÑOS_TRABAJADOS} DÍAS )') WHERE type=54 AND domain=0;



# RETRIBUCIÓN NO INCLUIDA OTROS APARATADOS 00001 
UPDATE payment_concept SET type=1 WHERE type IN (0,1,6,7,9);
UPDATE system_payment SET type=1 WHERE type IN (0,1,6,7,9);
UPDATE agreement_payment SET type=1 WHERE type IN (0,1,6,7,9);
UPDATE contract_payment SET type=1 WHERE type IN (0,1,6,7,9);


UPDATE payment_concept SET description=UCASE(description) WHERE domain = 0;

UPDATE payment_concept SET code=NULL WHERE domain = 0 AND code LIKE 'GT_%';
UPDATE payment_concept SET code=NULL WHERE domain = 0 AND code LIKE 'SL_ESPECIE';
UPDATE payment_concept SET code=NULL WHERE domain = 0 AND code LIKE 'IN_DESPIDO%';

UPDATE payment_concept SET code='SALARIO_BASE' WHERE domain = 0 AND code LIKE 'SB_%';
UPDATE payment_concept SET code='ANTIGUEDAD' WHERE domain = 0 AND code LIKE 'SL_ANTIGUEDAD';
UPDATE payment_concept SET code='PLUS_SALARIAL' WHERE domain = 0 AND code LIKE 'PL_SALARIAL';
UPDATE payment_concept SET code='HORAS_EXTRAS' WHERE domain = 0 AND code LIKE 'HR_EXTRA%';
UPDATE payment_concept SET code='PAGA_EXTRA' WHERE domain = 0 AND code LIKE 'PG_EXTRA%';

DELETE FROM payment_concept WHERE code='PL_EXTRA_SL' AND domain=0;

INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 01, 'RETRIBUCIÓN NO INCLUIDA OTROS APARTADOS', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 02, 'HORAS EXTRAORDINARIAS NO ESTRUCTURALES', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 03, 'HORAS EXTR. ESTRUCTURALES O FUERZA MAYOR', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 04, 'PAGAS EXTRAORDINARIAS.PRORRATEO', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 05, 'RETR<>PAGA.EXTR.VENCIM.SUP.MES.PRORRATEO', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 06, 'VACACIONES RETRIBUIDAS NO DISFRUTADAS', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 07, 'SALARIOS DE TRAMITACIÓN', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 08, 'RETR.POR ATRASOS NO INCLUIDA OTROS APART', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 09, 'RETRIBUCIÓN POR ATRASOS.CONV.COLECTIVO', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 10, 'RETRIBUCIÓN POR ATRASOS.SENTENCIA JUD.', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 11, 'RETRIBUCIÓN POR ATRASOS.NORMATIVA', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 12, 'RETRIBUCIÓN POR ATRASOS.ACTA CONCILIAC', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 13, 'R.ESPECIE NO INCLUIDA EN OTROS APARTADOS', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 14, 'R.ESP.VIVIENDA.PROP.PAGAD.C/VALOR.CATAST.', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 15, 'R.ESP.VIVIENDA.PROP.PAGAD.PTE.VALOR.CAT.', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 16, 'R.ESP.VIVIENDA.NO PROPIEDAD PAGADOR', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 17, 'R.ESP.VEHÍCULO.ENTREGA AL TRABAJADOR', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 18, 'R.ESP.VEHÍCULO.USO.PROPIEDAD PAGADOR', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 19, 'R.ESP.VEHÍCULO USO.NO PROPIEDAD PAGADOR', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 20, 'R.ESP.VEHÍCULO USO Y POSTERIOR ENTREGA', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 21, 'R.ESP.PRÉSTAMO.TIPO INTERÉS < LEGAL', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 22, 'R.ESP. MANUTENCIÓN Y SIMILARES', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 23, 'R.ESP. HOSPEDAJE Y SIMILARES', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 24, 'R.ESP. VIAJES Y SIMILARES', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 25, 'R.ESP.GASTOS DE ESTUDIOS Y MANUTENCIÓN', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 26, 'R.ESP.DERECHOS FUNDADORES DE SOCIEDADES', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 27, 'QUEBRANTO DE MONEDA', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 28, 'DESGASTE ÚTILES Y HERRAMIENTAS', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 29, 'ADQUISICIÓN Y MANTENIMIENTO ROPA TRABAJO', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 30, 'PERCEPCIONES POR MATRIMONIO', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 31, 'DONACIONES PROMOCIONALES', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 32, 'PLUSES DE TRANSPORTE Y DE DISTANCIA', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 33, 'PLANES PENSIONES Y SIST. ALTERNATIVOS', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 34, 'ACCIONES O PARTICIPACIONES EMPRESA', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 35, 'GASTOS ESTUDIO ACT. CAPACIT. O RECICLAJE', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 36, 'PRODUCTOS.PREC.REB.CANTIN.COMED.ECONOM.', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 37, 'BIENES DESTINADOS A SERV. SOC. Y CULT.', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 38, 'PRIMAS SEGURO AT O RESPONS. CIVIL TRAB.', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 39, 'PRIMAS SEGURO ENFERMEDAD COMÚN TRABAJ.', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 40, 'PRIMAS SEGURO ENFERMEDAD COMÚN FAMILIAR.', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 41, 'PREST. EDUC. POR CENTR.AUT. A HIJ. TRAB.', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 42, 'GASTOS DE ESTANCIA', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 43, 'GASTOS MANUTENCIÓN PERNOCTA ESPAÑA', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 44, 'GASTOS MANUTENCIÓN PERNOCTA EXTRANJERO', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 45, 'GASTOS MANUTENCIÓN SIN PERNOCTA ESPAÑA', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 46, 'GASTOS MANUTENCIÓN SIN PERNOCTA EXTRANJERO', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 47, 'GASTOS MANUTENCIÓN PERSONAL VUELO ESPAÑA', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 48, 'GASTOS MANUTENCIÓN PERSONAL VUELO EXTR.', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 49, 'GASTOS DE LOCOMOCIÓN TRANSPORTE PÚBLICO', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 50, 'GASTOS LOCOMOCIÓN SIN JUSTIFIC. IMPORTE', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 51, 'INDEMNIZACIONES POR FALLECIMIENTO', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 52, 'INDEMNIZACIONES POR TRASLADOS', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 53, 'INDEMNIZACIONES POR SUSPENSIONES', '_P', '_P' );
# INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 54, 'INDEMNIZACIONES POR DESPIDO O CESE', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 55, 'MEJORAS PREST.SS.INCAPACIDAD TEMPORAL', '_P', '_P' );
INSERT INTO payment_concept ( domain, code, type, description, irpf_expression, quote_expression ) VALUES (0, NULL, 56, 'MEJORAS PREST.SS.<>INCAPACIDAD TEMPORAL', '_P', '_P' );


UPDATE `db_version` SET `version_number` = '7.30.1';

COMMIT;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=1;