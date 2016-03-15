package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod131Key implements IFiscalModelKey {

	 P1  		("131-P1"		, 0)
	,P2  		("131-P2"		, 0)
	,P3  		("131-P3"		, 0)	//	Si el contribuyente es discapacitado en grado igual o superior al 33 por 100, marque X
	// 	Actividad 1	
	,AC1_EPI	("131-AC11"		, 0)
	,AC1_EPD	("131-AC111"	, 0)
	,AC1_COM	("131-AC1COM"	, 0)	//	COMUNIDAD, SOCIEDAD CIVIL O SIMILAR: porcentaje de participación
	,AC1_TEM	("131-AC1TEM"	, 0)	//	ACTIVIDAD DE TEMPORADA: nº de días de ejercicio en el año anterior	
	,AC1_NUE	("131-AC1NUE"	, 0)	//	NUEVAS ACTIVIDADES iniciadas a partir del 1-1-2014: Año de inicio
	,AC1_CEU	("131-AC1CEU"	, 0)	//	¿DEDUCCIÓN POR RENTAS OBTENIDAS EN CEUTA Y MELILLA? (S/N)	
	,AC1_LOC	("131-AC1LOC"	, 0)	//	¿Ejerce la actividad en un solo local o sin él? (S/N).	
	,AC1_VEH	("131-AC1VEH"	, 0)	//	Indique el número de vehículos afectos a la actividad
	,AC1_CAP	("131-AC1CAP"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC1_TNS	("131-AC1TNS"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC1_TSS	("131-AC1TSS"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC1_BAT	("131-AC1BAT"	, 0)
	,AC1_MUN	("131-AC1MUN"	, 0)	//	Municipio donde se ejerce la actividad:
	,AC1_EMP	("131-AC1EMP"	, 0)	//	Nº de empleados a 1-01-2015 (o en la fecha de inicio de la actividad)
	,AC1_LOR	("131-AC1LOR"	, 0)	//	Si en 2015 realiza la actividad en LORCA, seleccione lo que proceda
	,AC1_PRC	("131-AC1PRC"	, 0)	//	Si para el cálculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar %
	,AC1_M1D	("131-AC1M01"	, 0)
	,AC1_M1U	("131-AC1M01"	, 0)
	,AC1_M1F	("131-AC1M01"	, 0)
	,AC1_M1R	("131-AC1M01"	, 0)
	,AC1_M2D	("131-AC1M02"	, 0)
	,AC1_M2U	("131-AC1M02"	, 0)
	,AC1_M2F	("131-AC1M02"	, 0)
	,AC1_M2R	("131-AC1M02"	, 0)
	,AC1_M3D	("131-AC1M03"	, 0)
	,AC1_M3U	("131-AC1M03"	, 0)
	,AC1_M3F	("131-AC1M03"	, 0)
	,AC1_M3R	("131-AC1M03"	, 0)
	,AC1_M4D	("131-AC1M04"	, 0)
	,AC1_M4U	("131-AC1M04"	, 0)
	,AC1_M4F	("131-AC1M04"	, 0)
	,AC1_M4R	("131-AC1M04"	, 0)
	,AC1_M5D	("131-AC1M05"	, 0)
	,AC1_M5U	("131-AC1M05"	, 0)
	,AC1_M5F	("131-AC1M05"	, 0)
	,AC1_M5R	("131-AC1M05"	, 0)
	,AC1_M6D	("131-AC1M06"	, 0)
	,AC1_M6U	("131-AC1M06"	, 0)
	,AC1_M6F	("131-AC1M06"	, 0)
	,AC1_M6R	("131-AC1M06"	, 0)
	,AC1_M7D	("131-AC1M07"	, 0)
	,AC1_M7U	("131-AC1M07"	, 0)
	,AC1_M7F	("131-AC1M07"	, 0)
	,AC1_M7R	("131-AC1M07"	, 0)
	,AC1_RNP	("131-AC1RNP"	, 0)	//	RENDIMIENTO NETO PREVIO
	,AC1_IEM	("131-AC1IEM"	, 0)	//	Incentivos al empleo
	,AC1_IIN	("131-AC1IIN"	, 0)	//	Incentivos a la inversión
	,AC1_RNM	("131-AC1RNM"	, 0)	//	RENDIMIENTO NETO MINORADO
	,AC1_IC1	("131-AC1IC1"	, 0)	//	1. Índice Corrector. Especiales
	,AC1_IC2	("131-AC1IC2"	, 0)	//	2. Índice Corrector. Empresas de pequeña dimensión
	,AC1_IC3	("131-AC1IC3"	, 0)	//	3. Índice Corrector. De temporada
	,AC1_IC4	("131-AC1IC4"	, 0)	//	4. Índice Corrector. De exceso
	,AC1_IC5	("131-AC1IC5"	, 0)	//	5. Índice Corrector. De inicio de nueva actividad
	,AC1_RPF	("131-AC1RPF"	, 0)	//	RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (*)
	,AC1_RLO	("131-AC1RLO"	, 0)	//	Reducción para actividades económicas realizadas en el término municipal de Lorca
	,AC1_RDR	("131-AC1RDR"	, 0)	//	Rendimiento a efectos de pagos fraccionados después de la reducción
	,AC1_DIA	("131-AC1DIA"	, 0)	//	Días de ejercicio en 2015.
	,AC1_NET	("131-AC12"		, 0)
	,AC1_POR	("131-AC13"		, 0)
	,AC1_RES	("131-AC14"		, 0)
	
	// 	Actividad 2	
	,AC2_EPI	("131-AC21"		, 0)
	,AC2_EPD	("131-AC211"	, 0)
	,AC2_COM	("131-AC2COM"	, 0)	//	COMUNIDAD, SOCIEDAD CIVIL O SIMILAR: porcentaje de participación
	,AC2_TEM	("131-AC2TEM"	, 0)	//	ACTIVIDAD DE TEMPORADA: nº de días de ejercicio en el año anterior	
	,AC2_NUE	("131-AC2NUE"	, 0)	//	NUEVAS ACTIVIDADES iniciadas a partir del 1-1-2014: Año de inicio
	,AC2_CEU	("131-AC2CEU"	, 0)	//	¿DEDUCCIÓN POR RENTAS OBTENIDAS EN CEUTA Y MELILLA? (S/N)	
	,AC2_LOC	("131-AC2LOC"	, 0)	//	¿Ejerce la actividad en un solo local o sin él? (S/N).	
	,AC2_VEH	("131-AC2VEH"	, 0)	//	Indique el número de vehículos afectos a la actividad
	,AC2_CAP	("131-AC2CAP"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC2_TNS	("131-AC2TNS"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC2_TSS	("131-AC2TSS"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC2_BAT	("131-AC2BAT"	, 0)
	,AC2_MUN	("131-AC2MUN"	, 0)	//	Municipio donde se ejerce la actividad:
	,AC2_EMP	("131-AC2EMP"	, 0)	//	Nº de empleados a 1-01-2015 (o en la fecha de inicio de la actividad)
	,AC2_LOR	("131-AC2LOR"	, 0)	//	Si en 2015 realiza la actividad en LORCA, seleccione lo que proceda
	,AC2_PRC	("131-AC2PRC"	, 0)	//	Si para el cálculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar %
	,AC2_M1D	("131-AC2M01"	, 0)
	,AC2_M1U	("131-AC2M01"	, 0)
	,AC2_M1F	("131-AC2M01"	, 0)
	,AC2_M1R	("131-AC2M01"	, 0)
	,AC2_M2D	("131-AC2M02"	, 0)
	,AC2_M2U	("131-AC2M02"	, 0)
	,AC2_M2F	("131-AC2M02"	, 0)
	,AC2_M2R	("131-AC2M02"	, 0)
	,AC2_M3D	("131-AC2M03"	, 0)
	,AC2_M3U	("131-AC2M03"	, 0)
	,AC2_M3F	("131-AC2M03"	, 0)
	,AC2_M3R	("131-AC2M03"	, 0)
	,AC2_M4D	("131-AC2M04"	, 0)
	,AC2_M4U	("131-AC2M04"	, 0)
	,AC2_M4F	("131-AC2M04"	, 0)
	,AC2_M4R	("131-AC2M04"	, 0)
	,AC2_M5D	("131-AC2M05"	, 0)
	,AC2_M5U	("131-AC2M05"	, 0)
	,AC2_M5F	("131-AC2M05"	, 0)
	,AC2_M5R	("131-AC2M05"	, 0)
	,AC2_M6D	("131-AC2M06"	, 0)
	,AC2_M6U	("131-AC2M06"	, 0)
	,AC2_M6F	("131-AC2M06"	, 0)
	,AC2_M6R	("131-AC2M06"	, 0)
	,AC2_M7D	("131-AC2M07"	, 0)
	,AC2_M7U	("131-AC2M07"	, 0)
	,AC2_M7F	("131-AC2M07"	, 0)
	,AC2_M7R	("131-AC2M07"	, 0)
	,AC2_RNP	("131-AC2RNP"	, 0)	//	RENDIMIENTO NETO PREVIO
	,AC2_IEM	("131-AC2IEM"	, 0)	//	Incentivos al empleo
	,AC2_IIN	("131-AC2IIN"	, 0)	//	Incentivos a la inversión
	,AC2_RNM	("131-AC2RNM"	, 0)	//	RENDIMIENTO NETO MINORADO
	,AC2_IC1	("131-AC2IC1"	, 0)	//	1. Índice Corrector. Especiales
	,AC2_IC2	("131-AC2IC2"	, 0)	//	2. Índice Corrector. Empresas de pequeña dimensión
	,AC2_IC3	("131-AC2IC3"	, 0)	//	3. Índice Corrector. De temporada
	,AC2_IC4	("131-AC2IC4"	, 0)	//	4. Índice Corrector. De exceso
	,AC2_IC5	("131-AC2IC5"	, 0)	//	5. Índice Corrector. De inicio de nueva actividad
	,AC2_RPF	("131-AC2RPF"	, 0)	//	RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (*)
	,AC2_RLO	("131-AC2RLO"	, 0)	//	Reducción para actividades económicas realizadas en el término municipal de Lorca
	,AC2_RDR	("131-AC2RDR"	, 0)	//	Rendimiento a efectos de pagos fraccionados después de la reducción
	,AC2_DIA	("131-AC2DIA"	, 0)	//	Días de ejercicio en 2015.
	,AC2_NET	("131-AC22"		, 0)
	,AC2_POR	("131-AC23"		, 0)
	,AC2_RES	("131-AC24"		, 0)
	
	// 	Actividad 3	
	,AC3_EPI	("131-AC31"		, 0)
	,AC3_EPD	("131-AC311"	, 0)
	,AC3_COM	("131-AC3COM"	, 0)	//	COMUNIDAD, SOCIEDAD CIVIL O SIMILAR: porcentaje de participación
	,AC3_TEM	("131-AC3TEM"	, 0)	//	ACTIVIDAD DE TEMPORADA: nº de días de ejercicio en el año anterior	
	,AC3_NUE	("131-AC3NUE"	, 0)	//	NUEVAS ACTIVIDADES iniciadas a partir del 1-1-2014: Año de inicio
	,AC3_CEU	("131-AC3CEU"	, 0)	//	¿DEDUCCIÓN POR RENTAS OBTENIDAS EN CEUTA Y MELILLA? (S/N)	
	,AC3_LOC	("131-AC3LOC"	, 0)	//	¿Ejerce la actividad en un solo local o sin él? (S/N).	
	,AC3_VEH	("131-AC3VEH"	, 0)	//	Indique el número de vehículos afectos a la actividad
	,AC3_CAP	("131-AC3CAP"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)	
	,AC3_TNS	("131-AC3TNS"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC3_TSS	("131-AC3TSS"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC3_BAT	("131-AC3BAT"	, 0)
	,AC3_MUN	("131-AC3MUN"	, 0)	//	Municipio donde se ejerce la actividad:
	,AC3_EMP	("131-AC3EMP"	, 0)	//	Nº de empleados a 1-01-2015 (o en la fecha de inicio de la actividad)
	,AC3_LOR	("131-AC3LOR"	, 0)	//	Si en 2015 realiza la actividad en LORCA, seleccione lo que proceda
	,AC3_PRC	("131-AC3PRC"	, 0)	//	Si para el cálculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar %
	,AC3_M1D	("131-AC3M01"	, 0)
	,AC3_M1U	("131-AC3M01"	, 0)
	,AC3_M1F	("131-AC3M01"	, 0)
	,AC3_M1R	("131-AC3M01"	, 0)
	,AC3_M2D	("131-AC3M02"	, 0)
	,AC3_M2U	("131-AC3M02"	, 0)
	,AC3_M2F	("131-AC3M02"	, 0)
	,AC3_M2R	("131-AC3M02"	, 0)
	,AC3_M3D	("131-AC3M03"	, 0)
	,AC3_M3U	("131-AC3M03"	, 0)
	,AC3_M3F	("131-AC3M03"	, 0)
	,AC3_M3R	("131-AC3M03"	, 0)
	,AC3_M4D	("131-AC3M04"	, 0)
	,AC3_M4U	("131-AC3M04"	, 0)
	,AC3_M4F	("131-AC3M04"	, 0)
	,AC3_M4R	("131-AC3M04"	, 0)
	,AC3_M5D	("131-AC3M05"	, 0)
	,AC3_M5U	("131-AC3M05"	, 0)
	,AC3_M5F	("131-AC3M05"	, 0)
	,AC3_M5R	("131-AC3M05"	, 0)
	,AC3_M6D	("131-AC3M06"	, 0)
	,AC3_M6U	("131-AC3M06"	, 0)
	,AC3_M6F	("131-AC3M06"	, 0)
	,AC3_M6R	("131-AC3M06"	, 0)
	,AC3_M7D	("131-AC3M07"	, 0)
	,AC3_M7U	("131-AC3M07"	, 0)
	,AC3_M7F	("131-AC3M07"	, 0)
	,AC3_M7R	("131-AC3M07"	, 0)
	,AC3_RNP	("131-AC3RNP"	, 0)	//	RENDIMIENTO NETO PREVIO
	,AC3_IEM	("131-AC3IEM"	, 0)	//	Incentivos al empleo
	,AC3_IIN	("131-AC3IIN"	, 0)	//	Incentivos a la inversión
	,AC3_RNM	("131-AC3RNM"	, 0)	//	RENDIMIENTO NETO MINORADO
	,AC3_IC1	("131-AC3IC1"	, 0)	//	1. Índice Corrector. Especiales
	,AC3_IC2	("131-AC3IC2"	, 0)	//	2. Índice Corrector. Empresas de pequeña dimensión
	,AC3_IC3	("131-AC3IC3"	, 0)	//	3. Índice Corrector. De temporada
	,AC3_IC4	("131-AC3IC4"	, 0)	//	4. Índice Corrector. De exceso
	,AC3_IC5	("131-AC3IC5"	, 0)	//	5. Índice Corrector. De inicio de nueva actividad
	,AC3_RPF	("131-AC3RPF"	, 0)	//	RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (*)
	,AC3_RLO	("131-AC3RLO"	, 0)	//	Reducción para actividades económicas realizadas en el término municipal de Lorca
	,AC3_RDR	("131-AC3RDR"	, 0)	//	Rendimiento a efectos de pagos fraccionados después de la reducción
	,AC3_DIA	("131-AC3DIA"	, 0)	//	Días de ejercicio en 2015.
	,AC3_NET	("131-AC32"		, 0)
	,AC3_POR	("131-AC33"		, 0)
	,AC3_RES	("131-AC34"		, 0)
	
	// 	Actividad 4	
	,AC4_EPI	("131-AC51"		, 0)
	,AC4_EPD	("131-AC511"	, 0)
	,AC4_COM	("131-AC5COM"	, 0)	//	COMUNIDAD, SOCIEDAD CIVIL O SIMILAR: porcentaje de participación
	,AC4_TEM	("131-AC5TEM"	, 0)	//	ACTIVIDAD DE TEMPORADA: nº de días de ejercicio en el año anterior	
	,AC4_NUE	("131-AC5NUE"	, 0)	//	NUEVAS ACTIVIDADES iniciadas a partir del 1-1-2014: Año de inicio
	,AC4_CEU	("131-AC5CEU"	, 0)	//	¿DEDUCCIÓN POR RENTAS OBTENIDAS EN CEUTA Y MELILLA? (S/N)	
	,AC4_LOC	("131-AC5LOC"	, 0)	//	¿Ejerce la actividad en un solo local o sin él? (S/N).	
	,AC4_VEH	("131-AC5VEH"	, 0)	//	Indique el número de vehículos afectos a la actividad
	,AC4_CAP	("131-AC5CAP"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)	
	,AC4_TNS	("131-AC4TNS"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC4_TSS	("131-AC4TSS"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC4_BAT	("131-AC4BAT"	, 0)
	,AC4_MUN	("131-AC5MUN"	, 0)	//	Municipio donde se ejerce la actividad:
	,AC4_EMP	("131-AC5EMP"	, 0)	//	Nº de empleados a 1-01-2015 (o en la fecha de inicio de la actividad)
	,AC4_LOR	("131-AC5LOR"	, 0)	//	Si en 2015 realiza la actividad en LORCA, seleccione lo que proceda
	,AC4_PRC	("131-AC5PRC"	, 0)	//	Si para el cálculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar %
	,AC4_M1D	("131-AC5M01"	, 0)
	,AC4_M1U	("131-AC5M01"	, 0)
	,AC4_M1F	("131-AC5M01"	, 0)
	,AC4_M1R	("131-AC5M01"	, 0)
	,AC4_M2D	("131-AC5M02"	, 0)
	,AC4_M2U	("131-AC5M02"	, 0)
	,AC4_M2F	("131-AC5M02"	, 0)
	,AC4_M2R	("131-AC5M02"	, 0)
	,AC4_M3D	("131-AC5M03"	, 0)
	,AC4_M3U	("131-AC5M03"	, 0)
	,AC4_M3F	("131-AC5M03"	, 0)
	,AC4_M3R	("131-AC5M03"	, 0)
	,AC4_M4D	("131-AC5M04"	, 0)
	,AC4_M4U	("131-AC5M04"	, 0)
	,AC4_M4F	("131-AC5M04"	, 0)
	,AC4_M4R	("131-AC5M04"	, 0)
	,AC4_M5D	("131-AC5M05"	, 0)
	,AC4_M5U	("131-AC5M05"	, 0)
	,AC4_M5F	("131-AC5M05"	, 0)
	,AC4_M5R	("131-AC5M05"	, 0)
	,AC4_M6D	("131-AC5M06"	, 0)
	,AC4_M6U	("131-AC5M06"	, 0)
	,AC4_M6F	("131-AC5M06"	, 0)
	,AC4_M6R	("131-AC5M06"	, 0)
	,AC4_M7D	("131-AC5M07"	, 0)
	,AC4_M7U	("131-AC5M07"	, 0)
	,AC4_M7F	("131-AC5M07"	, 0)
	,AC4_M7R	("131-AC5M07"	, 0)
	,AC4_RNP	("131-AC5RNP"	, 0)	//	RENDIMIENTO NETO PREVIO
	,AC4_IEM	("131-AC5IEM"	, 0)	//	Incentivos al empleo
	,AC4_IIN	("131-AC5IIN"	, 0)	//	Incentivos a la inversión
	,AC4_RNM	("131-AC5RNM"	, 0)	//	RENDIMIENTO NETO MINORADO
	,AC4_IC1	("131-AC5IC1"	, 0)	//	1. Índice Corrector. Especiales
	,AC4_IC2	("131-AC5IC2"	, 0)	//	2. Índice Corrector. Empresas de pequeña dimensión
	,AC4_IC3	("131-AC5IC3"	, 0)	//	3. Índice Corrector. De temporada
	,AC4_IC4	("131-AC5IC4"	, 0)	//	4. Índice Corrector. De exceso
	,AC4_IC5	("131-AC5IC5"	, 0)	//	5. Índice Corrector. De inicio de nueva actividad
	,AC4_RPF	("131-AC5RPF"	, 0)	//	RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (*)
	,AC4_RLO	("131-AC5RLO"	, 0)	//	Reducción para actividades económicas realizadas en el término municipal de Lorca
	,AC4_RDR	("131-AC5RDR"	, 0)	//	Rendimiento a efectos de pagos fraccionados después de la reducción
	,AC4_DIA	("131-AC5DIA"	, 0)	//	Días de ejercicio en 2015.
	,AC4_NET	("131-AC52"		, 0)
	,AC4_POR	("131-AC53"		, 0)
	,AC4_RES	("131-AC54"		, 0)
	
	// 	Actividad 5	
	,AC5_EPI	("131-AC51"		, 0)
	,AC5_EPD	("131-AC511"	, 0)
	,AC5_COM	("131-AC5COM"	, 0)	//	COMUNIDAD, SOCIEDAD CIVIL O SIMILAR: porcentaje de participación
	,AC5_TEM	("131-AC5TEM"	, 0)	//	ACTIVIDAD DE TEMPORADA: nº de días de ejercicio en el año anterior	
	,AC5_NUE	("131-AC5NUE"	, 0)	//	NUEVAS ACTIVIDADES iniciadas a partir del 1-1-2014: Año de inicio
	,AC5_CEU	("131-AC5CEU"	, 0)	//	¿DEDUCCIÓN POR RENTAS OBTENIDAS EN CEUTA Y MELILLA? (S/N)	
	,AC5_LOC	("131-AC5LOC"	, 0)	//	¿Ejerce la actividad en un solo local o sin él? (S/N).	
	,AC5_VEH	("131-AC5VEH"	, 0)	//	Indique el número de vehículos afectos a la actividad
	,AC5_CAP	("131-AC5CAP"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)	
	,AC5_TNS	("131-AC5TNS"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC5_TSS	("131-AC5TSS"	, 0)	//	¿La capacidad de carga del vehículo es superior a 1000 Kg.? (S/N)
	,AC5_BAT	("131-AC5BAT"	, 0)
	,AC5_MUN	("131-AC5MUN"	, 0)	//	Municipio donde se ejerce la actividad:
	,AC5_EMP	("131-AC5EMP"	, 0)	//	Nº de empleados a 1-01-2015 (o en la fecha de inicio de la actividad)
	,AC5_LOR	("131-AC5LOR"	, 0)	//	Si en 2015 realiza la actividad en LORCA, seleccione lo que proceda
	,AC5_PRC	("131-AC5PRC"	, 0)	//	Si para el cálculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar %
	,AC5_M1D	("131-AC5M01"	, 0)
	,AC5_M1U	("131-AC5M01"	, 0)
	,AC5_M1F	("131-AC5M01"	, 0)
	,AC5_M1R	("131-AC5M01"	, 0)
	,AC5_M2D	("131-AC5M02"	, 0)
	,AC5_M2U	("131-AC5M02"	, 0)
	,AC5_M2F	("131-AC5M02"	, 0)
	,AC5_M2R	("131-AC5M02"	, 0)
	,AC5_M3D	("131-AC5M03"	, 0)
	,AC5_M3U	("131-AC5M03"	, 0)
	,AC5_M3F	("131-AC5M03"	, 0)
	,AC5_M3R	("131-AC5M03"	, 0)
	,AC5_M4D	("131-AC5M04"	, 0)
	,AC5_M4U	("131-AC5M04"	, 0)
	,AC5_M4F	("131-AC5M04"	, 0)
	,AC5_M4R	("131-AC5M04"	, 0)
	,AC5_M5D	("131-AC5M05"	, 0)
	,AC5_M5U	("131-AC5M05"	, 0)
	,AC5_M5F	("131-AC5M05"	, 0)
	,AC5_M5R	("131-AC5M05"	, 0)
	,AC5_M6D	("131-AC5M06"	, 0)
	,AC5_M6U	("131-AC5M06"	, 0)
	,AC5_M6F	("131-AC5M06"	, 0)
	,AC5_M6R	("131-AC5M06"	, 0)
	,AC5_M7D	("131-AC5M07"	, 0)
	,AC5_M7U	("131-AC5M07"	, 0)
	,AC5_M7F	("131-AC5M07"	, 0)
	,AC5_M7R	("131-AC5M07"	, 0)
	,AC5_RNP	("131-AC5RNP"	, 0)	//	RENDIMIENTO NETO PREVIO
	,AC5_IEM	("131-AC5IEM"	, 0)	//	Incentivos al empleo
	,AC5_IIN	("131-AC5IIN"	, 0)	//	Incentivos a la inversión
	,AC5_RNM	("131-AC5RNM"	, 0)	//	RENDIMIENTO NETO MINORADO
	,AC5_IC1	("131-AC5IC1"	, 0)	//	1. Índice Corrector. Especiales
	,AC5_IC2	("131-AC5IC2"	, 0)	//	2. Índice Corrector. Empresas de pequeña dimensión
	,AC5_IC3	("131-AC5IC3"	, 0)	//	3. Índice Corrector. De temporada
	,AC5_IC4	("131-AC5IC4"	, 0)	//	4. Índice Corrector. De exceso
	,AC5_IC5	("131-AC5IC5"	, 0)	//	5. Índice Corrector. De inicio de nueva actividad
	,AC5_RPF	("131-AC5RPF"	, 0)	//	RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (*)
	,AC5_RLO	("131-AC5RLO"	, 0)	//	Reducción para actividades económicas realizadas en el término municipal de Lorca
	,AC5_RDR	("131-AC5RDR"	, 0)	//	Rendimiento a efectos de pagos fraccionados después de la reducción
	,AC5_DIA	("131-AC5DIA"	, 0)	//	Días de ejercicio en 2015.
	,AC5_NET	("131-AC52"		, 0)
	,AC5_POR	("131-AC53"		, 0)
	,AC5_RES	("131-AC54"		, 0)
	
	,C01		("131-AC01"		, 1)
	,C02		("131-AC02"		, 2)
	
	,C03		("131-03"		, 3)
	,C04		("131-04"		, 4)
	
	,C05		("131-05"		, 5)
	,C06		("131-06"		, 6)
	
	,C07		("131-07"		, 7)
	,C08		("131-08"		, 8)
	,C09		("131-09"		, 9)
	,C091		("131-091"		, 9)
	,C10		("131-10"		,10)
	,C11		("131-11"		,11)
	,C12		("131-12"		,12)
	,C13		("131-13"		,13)
	,C14		("131-14"		,14)
	,C15		("131-15"		,15)
	,CT_TIP		("131-DT"		,0)
	;

    private String value;
    private int box;
    
    private Mod131Key(String value,int box) {
    	this.value = value;
    	this.box = box;
    }

	@Override
	public String getValue() {
		return value;
	}
    @Override
	public int getBox() {
		return box;
	}
	public static Mod131Key getKey(String value) {
		for (Mod131Key key : Mod131Key.values()) {
			if (AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		for (Mod131Key key : Mod131Key.values()) {
			System.out.println(
				"," + key.toString() 
				+ "\t(Mod131Key."+key.toString()
				+ "\t, (mod -> mod.isAEAT())"
				+ "\t, null"
				+ "\t, null"
				+ ")"
					);
		}
	}
}
