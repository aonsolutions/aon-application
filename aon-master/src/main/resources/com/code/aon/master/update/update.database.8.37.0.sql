# Database: aon_master
# Version: Actualizacion de la version 8.37.0 a la version 8.37.1.
# Created by: rtrepiana
# Creation Date: 30/12/2015 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

## FIESTAS ESTATALES
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%ESTATALES%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-01-01', 'Año Nuevo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-01-06', 'Epifanía del Señor');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-25', 'Viernes Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-15', 'Asunción de la Virgen');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-12', 'Fiesta Nacional de España');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-11-01', 'Todos los Santos');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-06', 'Día de la Constitución Española');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-08', 'La Inmaculada Concepción');

## FIESTAS CC.AA / CIUDADES AUTONOMAS
# Andalucía
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Andaluc_a%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-29', 'Día de Andalucía');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-02', 'Lunes siguiente a la Fiesta del Trabajo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor San Esteban');
# Aragón
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Arag_n%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-04-23', 'San Jorge, Día de Aragón');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-02', 'Lunes siguiente a la Fiesta del Trabajo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor San Esteban');
# Asturias
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Asturias%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-02', 'Lunes siguiente a la Fiesta del Trabajo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-08', 'Día de Asturias');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor San Esteban');
# Baleares
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Baleares%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-01', 'Día de las Islas Baleares');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-28', 'Lunes de Pascua');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor San Esteban');
# Canarias
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Canarias%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-02', 'Lunes siguiente a la Fiesta del Trabajo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-30', 'Día de Canarias');
# Cantabria
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Cantabria%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-28', 'Día de las Instituciones de Cantabria');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-15', 'Festividad de la Bien Aparecida');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor San Esteban');
# Castilla La Mancha
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Castilla%La Mancha%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-26', 'Corpus Christi');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-31', 'Día de la Región de Castilla- La Mancha');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor San Esteban');
# Castilla y León
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Castilla y Le_n%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-04-23', 'Fiesta de la Comunidad Autónoma');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-02', 'Lunes siguiente a la Fiesta del Trabajo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor San Esteban');
# Cataluña
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Catalu_a%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-28', 'Lunes de Pascua');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-16', 'Lunes de Pascua Granada');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-24', 'San Juan');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor  San Esteban');
# Comunidad Valenciana
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Comunidad Valenciana%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-19', 'San José');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-28', 'Lunes de Pascua');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor  San Esteban');
# Extremadura
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Extremadura%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-02', 'Lunes siguiente a la Fiesta del Trabajo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-08', 'Día de Extremadura');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor  San Esteban');
# Galicia
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Galicia%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-17', 'Día de las Letras Gallegas');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-24', 'San Juan');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-25', 'Día Nacional de Galicia');
# Madrid, Comunidad
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE (description like '%Comunidad%Madrid%' OR description like '%Madrid%Comunidad%') and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-02', 'Lunes siguiente a la Fiesta del Trabajo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-25', 'Santiago Apóstol');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor  San Esteban');
# Murcia, Región de
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE (description like '%Regi_n%Murcia%' OR description like '%Murcia%Regi_n%') and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-19', 'San José');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-09', 'Día de la Región de Murcia');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor  San Esteban');
# Navarra
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Navarra%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-28', 'Lunes de Pascua');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-25', 'Santiago Apóstol');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor  San Esteban');
# País Vasco
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%Pa_s Vasco%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-28', 'Lunes de Pascua');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-25', 'Santiago Apóstol');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-07', '80º Aniversario del primer Gobierno Vasco');
# La Rioja
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like '%La Rioja%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-28', 'Lunes de Pascua');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-09', 'Día de La Rioja');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-25', 'Santiago Apóstol');
# Ceuta, Ciudad de
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE (description like '%Ciudad%Ceuta%' OR description like '%Ceuta%Ciudad%') and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-02', 'Día de la Ciudad Autónoma de Ceuta');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-12', 'Festividad de la Pascua del Sacrificio(EidulAdha)');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor  San Esteban');
# Melilla, Ciudad de
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE (description like '%Ciudad%Melilla%' OR description like '%Melilla%Ciudad%') and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-19', 'San José');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-24', 'Jueves Santo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-12', 'Fiesta del Sacrificio (Aid El Kebir)');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-26', 'Lunes siguiente a la Natividad del Señor  San Esteban');

## FIESTAS LOCALES
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos A Coru_a%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-09', 'Martes de Carnaval');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-07', 'Virgen del Rosario');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Albacete%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-24', 'San Juan');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-08', 'Virgen de los Llanos');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Alicante%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-04-07', 'Santa Faz');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-24', 'San Juan');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Almer_a%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-24', 'San Juan');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-27', 'Virgen del Mar');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos _vila%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-03', 'Día después deSan Segundo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-15', 'Santa Teresa de Jesús');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Badajoz%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-09', 'Martes de Carnaval');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-24', 'San Juan');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Barcelona%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-12', 'Santa Eulàlia');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-24', 'Día de la Merced');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Bilbao%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-04', 'San Valentín de Berriotxoa');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-26', 'Viernes de la Semana Grande');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Burgos%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-03', 'Curpillos');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-29', 'San Pedro y San Pablo');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos C_ceres%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-04-23', 'San Jorge');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-27', 'San Fernando');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos C_diz%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-08', 'Lunes de Carnaval');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-07', 'Virgen del Rosario');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Castell_n%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-29', 'Lunes de Magdalena');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-29', 'San Pedro y San Pablo');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Ceuta%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-13', 'San Antonio');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-05', 'Virgen de África');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Ciudad Real%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-16', 'Virgen de Alarcos');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-22', 'La Octava');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos C_rdoba%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-08', 'Virgen de laFuensanta');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-24', 'San Rafael');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Cuenca%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-01-28', 'San Julián');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-01', 'Virgen de la Luz');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Girona%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-25', 'San Jaime');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-29', 'San Narcís');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Granada%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-01-02', 'Día de la Toma');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-26', 'Corpus Christi');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Guadalajara%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-08', 'Virgen de la Antigua');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-16', 'Viernes de la Semana de Feria');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Huelva%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-03', 'Fiestas Colombinas');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-08', 'Virgen de la Cinta');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Huesca%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-01-22', 'San Vicente');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-10', 'San Lorenzo');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Ja_n%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-18', 'San Lucas');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-11-25', 'Santa Catalina de Alejandría');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Las Palmas%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-09', 'Martes de Carnaval');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-24', 'San Juan Bautista');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Le_n%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-24', 'San Juan');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-05', 'San Froilán');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Lleida%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-11', 'San Anastasio');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-29', 'San Miquel');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Logro_o%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-11', 'San Bernabé');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-21', 'San Mateo');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Lugo%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-09', 'Martes de Carnaval');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-05', 'San Froilán');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Madrid%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-16', 'Lunes siguiente a San Isidro');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-11-09', 'Nuestra Señora de la Almudena');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos M_laga%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-19', 'Reconquista de Málaga para la Corona de Castilla');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-08', 'Virgen de la Victoria');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Melilla%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-08', 'Virgen de la Victoria');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-17', 'Día de Melilla');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Murcia%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-29', 'Bando de la Huerta');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-13', 'Fiesta de la Romería');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Ourense%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-09', 'Martes de Carnaval');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-11-11', 'San Martiño');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Oviedo%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-17', 'Martes de Campo');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-21', 'San Mateo');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Palencia%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-02', 'Las Candelas');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-02', 'San Antolín');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Palma de Mallorca%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-01-20', 'San Sebastián');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-24', 'San Juan');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Pamplona%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-11-29', 'San Saturnino');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-12-03', 'San Francisco Javier');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Pontevedra%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-10', 'Miércoles de Ceniza');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-11', 'San Benito de Lerez');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Salamanca%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-13', 'Lunes siguiente a San Juan de Sahagún');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-08', 'Virgen de la Vega');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos San Sebasti_n%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-01-20', 'Día de San Sebastián');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-09', 'Virgen de Arantzazu');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Santa Cruz de Tenerife%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-02-09', 'Martes de Carnaval');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-03', 'Día de la Santa Cruz');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Santander%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-25', 'Santiago Apóstol');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-30', 'Santos Mártires');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Segovia%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-29', 'San Pedro');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-25', 'San Frutos');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Sevilla%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-04-13', 'Miércoles de Feria');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-26', 'Corpus Christi');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Soria%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-30', 'Jueves de La Saca');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-10-03', 'Lunes siguiente a San Saturio');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Tarragona%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-19', 'Sant Magí');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-23', 'Santa Tecla');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Teruel%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-29', 'Sermón de las Tortillas');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-07-11', 'Lunes de Vaquillas');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Toledo%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-01-23', 'San Ildefonso');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-28', 'Lunes de Pascua');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Valencia%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-01-22', 'San Vicente Mártir');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-04-04', 'San Vicente Ferrer');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Valladolid%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-13', 'San Pedro Regalado');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-09-08', 'Nuestra Sra. de San Lorenzo');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Vitoria%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-04-28', 'San Prudencio');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-08-05', 'Ntra. Sra. De la Virgen Blanca');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Zamora%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-05-26', 'La Hiniesta');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-06-29', 'San Pedro');
SET @HOLIDAY=(SELECT IFNULL((SELECT id FROM holiday WHERE description like 'Festivos Zaragoza%' and domain = 0),0));
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-01-29', 'San Valero');
INSERT INTO `holiday_detail` (`domain`, `holiday`, `date`, `description`) VALUES (0, @HOLIDAY, '2016-03-05', 'Cincomarzada');


UPDATE `db_version` SET `version_number` = '8.37.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

