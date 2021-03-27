package com.esferalia.aon.payroll.agreement;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDatabaseOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDbPasswordOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDbUserOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getHostNameOption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.esferalia.aon.jooq.tables.records.AgreementLevelRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.agreement.Agreement.AgreementLevel;
import com.esferalia.aon.payroll.agreement.Agreement.AgreementLevelData;
import com.esferalia.aon.payroll.agreement.ServiAgreement.Extension;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AgreementParser {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static Integer DOMAIN_ID = 0;
	
	@SuppressWarnings("serial")
	private static Map<String, String> variablesNameMap = new HashMap<String, String>(){{
		put("SALARIO_CONVENIO_ANUAL", "SALARIO_CONVENIO_A");
		put("SALARIO_CONVENIO_ANUAL_ANUAL", "SALARIO_CONVENIO_A");
		put("SALARIO_BASE_ANUAL", "SALARIO_BASE_A");
		put("SALARIO_BASE_ANUAL_ANUAL", "SALARIO_BASE_A");
		put("SALARIO_CONVENIO_MENSUAL", "SALARIO_CONVENIO_M");
		put("SALARIO_BASE_MENSUAL", "SALARIO_BASE_M");
		put("SALARIO_CONVENIO_HORAS", "SALARIO_CONVENIO_H");
		put("SALARIO_HORA_HORAS", "SALARIO_BASE_H");
		put("SALARIO_BASE_HORAS", "SALARIO_BASE_H");
		put("SALARIO_CONVENIO_DIARIO", "SALARIO_CONVENIO_D");
		put("SALARIO_BASE_DIARIO", "SALARIO_BASE_D");
		put("SALARIO_BASE_MINIMO_ANUAL", "SALARIO_BASE_MIN");
		put("PLUS_CONVENIO_ANUAL", "PLUS_CONVENIO");
		put("PLUS_CONVENIO_MENSUAL", "PLUS_CONVENIO");
		put("COMPLEMENTO_CONVENIO_MENSUAL", "PLUS_CONVENIO");
		put("PLUS_MANUTENCION_NO_OBLIGATORIA_MENSUAL", "P_MANUTENCION_NO_OBL_M");
		put("PLUS_MANUTENCION_MENSUAL", "P_MANUTENCION_M");
		put("PLUS_CONVENIO_HORAS", "PLUS_CONVENIO");
		put("COMPLEMENTO_CONVENIO_DIARIO", "PLUS_CONVENIO_DIARIO");
		put("PLUS_CONVENIO_DIARIO", "PLUS_CONVENIO");
		put("PLUS_MANUTENCION_DIARIO", "P_MANUTENCION_D");
		put("COMPLEMENTO_SALARIAL_DIARIO", "PLUS_DIARIO");
		put("PLUS_SALARIAL_DIARIO", "PLUS_DIARIO");
		put("PLUS_SALARIAL_LABORAL_DIARIO", "PLUS_LABORABLES");
		put("PLUS_SALARIAL_FIJO_MENSUAL", "PLUS_FIJO");
		put("PLUS_EXTRA_CATEGORIA_ANUAL", "PLUS_EXTRA_CATEGORIA_A");
		put("PLUS_FIESTAS_PATRONALES_ANUAL", "P_FIESTAS_PATRONALES_A");
		put("PLUS_HERRAMIENTAS_ANUAL", "PLUS_HERRAMIENTAS_A");
		put("PLUS_HERRAMIENTAS_MENSUAL", "PLUS_HERRAMIENTAS_M");
		put("PLUS_HERRAMIENTAS_DIARIO", "PLUS_HERRAMIENTAS_D");
		put("PLUS_ACTIVIDAD_MENSUAL", "PLUS_ACTIVIDAD_MENSUAL");
		put("PLUS_ACTIVIDAD_DIARIO", "PLUS_ACTIVIDAD_DIARIO");
		put("PLUS_EXTRA_SALARIAL_MENSUAL", "PLUS_XS_MENSUAL");
		put("PLUS_EXTRA_SALARIAL_DIARIO", "PLUS_XS_DIARIO");
		put("PLUS_EXTRA_SALARIAL_LABORAL_DIARIO", "PLUS_XS_LABORABLES");
		put("PLUS_EXTRA_SALARIAL_FIJO_MENSUAL", "PLUS_XS_FIJO");
		put("PLUS_EXTRASALARIAL_DIARIO", "PLUS_XS_DIARIO");
		put("PLUS_VESTUARIO_MENSUAL", "PLUS_VESTUARIO");
		put("PLUS_DISPONIBILIDAD_MENSUAL", "P_DISPONIBILIDAD_M");
		put("PLUS_ROTACION_MENSUAL", "PLUS_ROTACION");
		put("PLUS_DISTANCIA_MENSUAL", "PLUS_DISTANCIA_M");
		put("PLUS_DISTANCIA_HORAS", "PLUS_DISTANCIA_H");
		put("PLUS_DISTANCIA_DIARIO", "PLUS_DISTANCIA_D");
		put("PLUS_URGENCIA_MENSUAL", "PLUS_URGENCIA");
		put("PLUS_ASISTENCIA_ANUAL", "PLUS_ASISTENCIA");
		put("PLUS_ASISTENCIA_MENSUAL", "PLUS_ASISTENCIA");
		put("PLUS_ASISTENCIA_DIARIO", "PLUS_ASISTENCIA");
		put("PLUS_TRANSPORTE_MENSUAL", "PLUS_TRANSPORTE_MEN");
		put("PLUS_TRANSPORTE_DIARIO", "PLUS_TRANSPORTE_DIA");
		put("PLUS_CARENCIA_DE_INCENTIVOS_MENSUAL", "PLUS_CARENCIA_INCENT_M");
		put("PLUS_RIESGO_DIARIO", "PLUS_RIESGO");
		put("PLUS_FESTIVOS_DIARIO", "PLUS_FESTIVO");
		put("PLUS_FESTIVOS_ESPECIALES_DIARIO", "PLUS_FESTIVO_ESP");
		put("PLUS_DOMINGOS_Y_FESTIVOS_DIARIO", "PLUS_FINDES");
		put("PLUS_DIA_NAVIDAD_Y_AÑO_NUEVO_DIARIO", "PLUS_NAVIDAD_AÑO_NUEVO");
		put("PLUS_PENOSIDAD_1_CIRCUNSTANCIA_DIARIO", "PLUS_PENOSIDAD_1");
		put("PLUS_PENOSIDAD_2_CIRCUNSTANCIAS_DIARIO", "PLUS_PENOSIDAD_2");
		put("PLUS_PENOSIDAD_MENSUAL", "PLUS_PENOSIDAD");
		put("PLUS_PENOSIDAD_DIARIO", "PLUS_PENOSIDAD");
		put("PLUS_TOXICIDAD_MENSUAL", "PLUS_TOXICIDAD");
		put("PLUS_TOXICIDAD_DIARIO", "PLUS_TOXICIDAD");
		put("SUPLIDO_MENSUAL", "SUPLIDO_MENSUAL");
		put("SUPLIDO_DIARIO", "SUPLIDO_DIARIO");
		put("PLUS_ACERCAMIENTO_ANUAL", "PLUS_ACERCAMIENTO_ANUAL");
		put("COMPLEMENTO_LINEAL_ANUAL", "COMPL_LINEAL_A");
		put("COMPLEMENTO_NACIMIENTO_O_ADOPCION_DE_HIJO_ANUAL", "COMPL_NAC_ADOP_HIJO");
		put("COMPLEMENTO_ESPECIFICO_MENSUAL", "COMPL_ESPECIFICO_MEN");
		put("SEGURO_COMPLEMENTARIO_ANUAL", "SEGURO_COMPLEMENT_A");
		put("FIDELIZACION_POR_EXTINCION_DE_CONTRATO_ANUAL", "FID_EXTIN_CONTRATO_A");
		put("PLUS_ANTIGUEDAD_3_AÑOS_ANUAL", "PLUS_ANTIGUEDAD");
		put("COMPLEMENTO_NO_SALARIAL_ANUAL", "COMPL_NO_SALARIAL_ANUAL");
		put("COMPLEMENTO_NO_SALARIAL_MENSUAL", "COMPL_NO_SALARIAL_MEN");
		put("COMPLEMENTO_NO_SALARIAL_DIARIO", "COMPL_NO_SALARIAL_DIA");
		put("COMPLEMENTO_PERSONAL_REPARTO_HORAS", "COMPL_PER_REPARTO_H");
		put("PLUS_FESTIVO_NOCTURNO_DIARIO", "P_FESTIVO_NOCT_D");
		put("PLUS_PUNTUALIDAD_MENSUAL", "PLUS_PUNTUALIDAD_M");
		put("PLUS_IDIOMAS_MENSUAL", "PLUS_IDIOMAS_M");
		put("PLUS_NOCTURNIDAD_MENSUAL", "PLUS_NOCTURNIDAD_M");
		put("PLUS_NOCTURNIDAD_DIARIO", "PLUS_NOCTURNIDAD");
		put("PLUS_NOCTURNIDAD_HORAS", "PLUS_NOCTURNIDAD_H");
		put("PLUS_DE_LAVADO_HORAS", "PLUS_LAVADO_H");
		put("PLUS_DESPLAZAMIENTOS_HORAS", "PLUS_DESPLAZAMIENTOS_H");
		put("PLUS_HOSPITAL_HORAS", "PLUS_HOSPITAL_H");
		put("PLUS_CENTRO_DE_SALUD_HORAS", "PLUS_CENT_SALUD_H");
		put("PLUS_GERIATRICO_HORAS", "PLUS_GERIATRICO_H");
		put("PLUS_DISPONIBILIDAD_HORAS", "PLUS_DISPONIBILIDAD_H");
		put("PLUS_SABADOS_DIARIO", "PLUS_SABADOS");
		put("PLUS_DOMINGOS_DIARIO", "PLUS_DOMINGOS");
		put("PAGAS_EXTRA_VERANO_Y_NAVIDAD_ANUAL", "P_E_VERANO_Y_NAVIDAD_A");
		put("PAGAS_EXTRA_VERANO_Y_NAVIDAD_MENSUAL", "P_E_VERANO_Y_NAVIDAD_M");
		put("PAGA_EXTRA_VERANO_MENSUAL", "P_E_VERANO_M");
		put("PAGA_EXTRA_NAVIDAD_MENSUAL", "P_E_NAVIDAD_M");
		put("PAGA_EXTRA_SIN_ANTIGUEDAD_MENSUAL", "P_EXTRA_SIN_ANTIGUEDAD_M");
		put("PAGA_EXTRA_MARZO_MENSUAL", "PAGA_EXTRA_MARZO");
		put("HORA_ORDINARIA_HORAS", "HORA_ORDINARIA");
		put("HORA_EXTRA_HORAS", "HORA_EXTRA");
		put("HORA_EXTRA_DOMINGOS_Y_FESTIVOS_HORAS", "HORAS_EXTRAS_F");
		put("HORA_EXTRA_NOCTURNA_HORAS", "HORAS_EXTRAS_NOC");
		put("HORA_EXTRA_SIN_ANTIGUEDAD_HORAS", "P_EXTRA_SIN_ANTIGUEDAD_H");
		put("HORA_EXTRA_SIN_ANTIGUEDAD_MENSUAL", "P_EXTRA_SIN_ANTIGUEDAD_M");
		put("ANTIGUEDAD_1_TRIENIO_MENSUAL", "ANTIGUEDAD_1_TRIENIO");
		put("HORA_EXTRA_1_QUINQUENIO_HORAS", "H_E_1_QUINQUENIO");
		put("HORA_EXTRA_2_QUINQUENIO_HORAS", "H_E_2_QUINQUENIO");
		put("HORA_EXTRA_3_QUINQUENIO_HORAS", "H_E_3_QUINQUENIO");
		put("HORA_EXTRA_4_QUINQUENIO_HORAS", "H_E_4_QUINQUENIO");
		put("HORA_EXTRA_ANTIGUEDAD_2_AÑOS_MENSUAL", "H_E_ANTIGUEDAD_2");
		put("HORA_EXTRA_ANTIGUEDAD_4_AÑOS_MENSUAL", "H_E_ANTIGUEDAD_4");
		put("HORA_EXTRA_ANTIGUEDAD_9_AÑOS_MENSUAL", "H_E_ANTIGUEDAD_9");
		put("HORA_EXTRA_ANTIGUEDAD_14_AÑOS_MENSUAL", "H_E_ANTIGUEDAD_14");
		put("HORA_EXTRA_ANTIGUEDAD_19_AÑOS_MENSUAL", "H_E_ANTIGUEDAD_19");
		put("HORA_EXTRA_ANTIGUEDAD_24_AÑOS_MENSUAL", "H_E_ANTIGUEDAD_24");
		put("HORA_EXTRA_ANTIGUEDAD_29_AÑOS_MENSUAL", "H_E_ANTIGUEDAD_29");
		put("HORA_EXTRA_DIARIO", "HORA_EXTRA_D");
		put("HORA_NOCTURNA_DISPONIBLE_HORAS", "HORA_NOCTURNA_DISP_H");
		put("HORA_NOCTURNA_HORAS", "HORA_NOCTURNA_H");
		put("HORA_REGULARIZACION_HORAS", "HORA_REGULARIZACION");
		put("HORA_BOLSA_HORARIA_HORAS", "HORA_BOLSA_HORARIA");
		put("VACACIONES_ANUAL", "VACACIONES_ANUAL");
		put("VACACIONES_MENSUAL", "VACACIONES_MENSUAL");
		put("MEDIA_DIETA_ANUAL", "MEDIA_DIETA");
		put("DIETA_COMPLETA_ANUAL", "DIETA_COMPLETA");
		put("DIETA_ALOJAMIENTO_MENSUAL", "DIETA_ALOJAMIENTO_M");
		put("GASTOS_PECNORTA_DIARIO", "IMPORTE_PERNOCTA");
		put("GASTOS_MANUTENCION_DIARIO", "IMPORTE_MANUTENCION");
		put("GASTOS_LOCOMOCION_SIN_JUSTIFICANTE_DIARIO", "IMPORTE_KM");
		put("DIETA_COMPLETA_DIARIO", "DIETA_COMPLETA");
		put("DIETA_COMPLETA_DESPL_MAS_100KMS_DIARIO", "DIETA_COMPLETA_G_100");
		put("DIETA_COMPLETA_DESPL_MENOS_100KMS_DIARIO", "DIETA_COMPLETA_L_100");
		put("DIETA_PERNOCTA_DIARIO", "DIETA_PERNOCTA");
		put("DIETA_PERNOCTA_EXTRANJERO_DIARIO", "DIET_PERNOCTA_EXT_D");
		put("DIETAS_DIARIO", "DIETA");
		put("DIETAS_EXTRANJERO_DIARIO", "DIETA_EXT");
		put("DIETA_COMPLETA_EXTRANJERO_DIARIO", "DIETA_COMPL_EXT");
		put("DIETA_COMIDA_DIARIO", "DIETA_COMIDA");
		put("MEDIA_DIETA_DIARIO", "MEDIA_DIETA");
		put("MEDIA_DIETA_DESPL_MAS_100KMS_DIARIO", "MEDIA_DIETA_G_100");
		put("MEDIA_DIETA_DESPL_MENOS_100KMS_DIARIO", "MEDIA_DIETA_L_100");
		put("DIETA_DESAYUNO_DIARIO", "DIETA_DESAYUNO_D");
		put("DIETA_COMIDA_O_CENA_EXTRANJERO_DIARIO", "D_COMIDA_CENA_EXT_D");
		put("DIETA_FUERA_DOMICILIO_HABITUAL_DIARIO", "D_FUERA_DOM_HABIT_D");
		put("COMIDA_O_CENA_PORTUGAL_DIARIO", "COMIDA_CENA_PORT_D");
		put("DESAYUNO_Y_CAMA_PORTUGAL_DIARIO", "DES_CAMA_PORT_D");
		put("APARTAMENTO_5_MENSUAL", "APARTAMENTO_5_M");
		put("APARTAMENTO_4_MENSUAL", "APARTAMENTO_4_M");
		put("APARTAMENTO_3_MENSUAL", "APARTAMENTO_3_M");
		put("PENSIONES_MENSUAL", "PENSIONES_M");
		put("CAMPING_CAT_2_MENSUAL", "CAMPING_CAT_2_M");
		put("CAMPING_CAT_1_MENSUAL", "CAMPING_CAT_1_M");
		put("VIVIENDA_VACACIONAL_MENSUAL", "VIVIENDA_VACAC_M");
		put("HOTEL_5_MENSUAL", "HOTEL_5_M");
		put("HOTEL_4_MENSUAL", "HOTEL_4_M");
		put("HOTEL_3_MENSUAL", "HOTEL_3_M");
		put("HOTEL_2_MENSUAL", "HOTEL_2_M");
		put("HOTEL_1_MENSUAL", "HOTEL_1_M");
		put("HOTEL_RURAL_MENSUAL", "HOTEL_RURAL_M");
		put("CLUB_PRIVADO_CAT_3_MENSUAL", "CLUB_PRIV_CAT_3_M");
		put("CLUB_PRIVADO_CAT_2_MENSUAL", "CLUB_PRIV_CAT_2_M");
		put("CLUB_PRIVADO_CAT_1_MENSUAL", "CLUB_PRIV_CAT_1_M");
		put("CATERING_MENSUAL", "CATERING_M");
		put("COLECTIVIDADES_MENSUAL", "COLECTIVIDADES_M");
		put("HOTEL_EMBLEMATICO_MENSUAL", "HOTEL_EMBLEM_M");
		put("APARTAMENTO_2_LLAVES_MENSUAL", "APART_2_LLAVES_M");
		put("APARTAMENTO_1_LLAVE_MENSUAL", "APART_1_LLAVE_M");
		put("CAMPING_LUJO_MENSUAL", "CAMPING_LUJO_M");
		put("INCENTIVOS_MENSUAL", "INCENTIVOS_MENSUAL");
		put("INCENTIVOS_DIARIO", "INCENTIVO_DIARIO");
		put("INCENTIVOS_MARMOLERIAS_DIARIO", "INCENTIVOS_MARMOL_DIA");
		put("TURNICIDAD_ANUAL", "TURNICIDAD_ANUAL");
		put("SEGURO_DE_ACCIDENTES_ANUAL", "SEGURO_DE_ACCIDENTES_A");
		put("GRATIFICACION_ANUAL", "GRATIFICACION_ANUAL");
		put("GRATIFICACION_NAVIDAD_ANUAL", "GRATIFICACION_NAVIDAD_A");
		put("INVALIDEZ_PERMANENTE_ABSOLUTA_ANUAL", "INVALIDEZ_PERMANENTE_ABS");
		put("SEGURO_GRAN_INVALIDEZ_ANUAL", "SEGURO_GRAN_INVALIDEZ");
		put("SEGURO_DE_MUERTE_ANUAL", "SEGURO_DE_MUERTE");
		put("QUEBRANDO_DE_MONEDA_ANUAL", "QUEBRANDO_MONEDA");
		put("BOLSA_DE_ESTUDIOS_ANUAL", "BOLSA_ESTUDIOS");
		put("POLIVALENCIA_ANUAL", "POLIVALENCIA_ANUAL");
		put("PLUS_FUNCIONAL_INSPECCION_CON_PERNOCA_ANUAL", "P_FUNC_INSPECCION_CP");
		put("PLUS_FUNCIONAL_INSPECCION_SIN_PERNOCA_ANUAL", "P_FUNC_INSPECCION_SP");
		put("AYUDA_VIVIENDA_TRAMO_1_ANUAL", "AYUDA_VIVIENDA_T_1");
		put("AYUDA_VIVIENDA_TRAMO_2_ANUAL", "AYUDA_VIVIENDA_T_2");
		put("POLIZA_SEGURO_DE_VIDA_ANUAL", "POLIZA_SEG_VIDA");
		put("FALLECIMIENTO_POR_ACCIDENTE_LABORAL_ANUAL", "FALLECIMIENTO_ACC");
		put("COMPLEMENTO_POR_EXPERIENCIA_ANUAL", "COMPL_EXPERIENCIA");
		put("PREMIO_NUPCIALIDAD_ANUAL", "PREMIO_NUPCIALIDAD");
		put("AYUDA_MATRICULA_ANUAL", "AYUDA_MATRICULA");
		put("PLUS_DOMINGOS_Y_FESTIVOS_SIN_PRORRATEO_VACACIONES_ANUAL", "P_DOMIN_FEST_SP");
		put("PLUS_DOMINGOS_Y_FESTIVOS_CON_PRORRATEO_VACACIONES_ANUAL", "P_DOMIN_FEST_CP");
		put("AYUDA_DISCAPACITADOS_ANUAL", "AYUDA_DISCAPACIT");
		put("FALLECIMIENTO_POR_ACCIDENTE_DE_TRABAJO_ANUAL", "FALLEC_X_ACC_A");
		put("ANTIGUEDAD_MENSUAL", "ANTIGUEDAD_MENSUAL");
		put("COMPENSACIONES_MARMOLERIAS_MENSUAL", "COMP_MARMOLERIAS");
		put("AYUDA_FAMILIA_NUMEROSA_MENSUAL", "AYUDA_FAMILIA_NUM");
		put("AYUDA_ESCOLAR_MENSUAL", "AYUDA_ESCOLAR_M");
		put("QUEBRANDO_DE_MONEDA_MENSUAL", "QUEBRANDO_MONEDA");
		put("COMPENSACIONES_MENSUAL", "COMPENSACIONES");
		put("PAGAS_EXTRA_MENSUAL", "PAGAS_EXTRA_MENSUAL");
		put("TURNICIDAD_MENSUAL", "TURNICIDAD_MENSUAL");
		put("PLUS_TURNICIDAD_3_TURNOS_MENSUAL", "P_TURNICIDAD_3T_M");
		put("PLUS_TURNICIDAD_2_TURNOS_MENSUAL", "P_TURNICIDAD_2T_M");
		put("QUINQUENIOS_MENSUAL", "QUINQUENIOS_MENSUAL");
		put("CUATRIENIOS_MENSUAL", "CUATRIENIOS_MENSUAL");
		put("TRIENIOS_MENSUAL", "TRIENIOS_MENSUAL");
		put("BIENIOS_MENSUAL", "BIENIOS_MENSUAL");
		put("P_C_I__MENSUAL", "PCI_MENUSAL");
		put("ESTUDIOS_MENSUAL", "ESTUDIOS_MENSUAL");
		put("PANTALLA_MENSUAL", "PANTALLA_MENSUAL");
		put("PREMIO_NUPCIALIDAD_MENSUAL", "PREMIO_NUPCIALIDAD");
		put("AYUDA_DISCAPACITADOS_MENSUAL", "AYUDA_DISCAPACIT");
		put("AYUDA_MATRICULA_MENSUAL", "AYUDA_MATRICULA");
		put("DIETA_KILOMETRAJE_HORAS", "DIETA_KILOMETRAJE_H");
		put("GARANTIZADO_MENSUAL", "GARANTIZADO_MENSUAL");
		put("RETRIBUCION_EN_ESPECIE_MENSUAL", "RETRIBUCION_ESPECIE");
		put("COMPLEMENTO_PERSONAL_DE_ANTIGUEDAD_MENSUAL", "IMPORTE_ANTIGUEDAD");
		put("COMPLEMENTO_MOVILIDAD_MENSUAL", "COMPL_MOVILIDAD_M");
		put("SERVICIO_EXTRA_CAMARERO_DIARIO", "S_E_CAMARERO_D");
		put("SERVICIO_EXTRA_LAVAPLATOS_DIARIO", "S_E_LAVAPLATOS_D");
		put("SERVICIO_EXTRA_AYUDANTE_DIARIO", "S_E_AYUDANTE_D");
		put("SERVICIO_EXTRA_COCINERO_DIARIO", "S_E_COCINERO_D");
		put("SERVICIO_EXTRA_COBRADOR_DIARIO", "S_E_COBRADOR_D");
		put("SERVICIO_EXTRA_AYDTE_COCINA_DIARIO", "S_E_AYDTE_COCINA_D");
		put("SERVICIO_EXTRA_LAVAPLATOS_FESTIVOS_DIARIO", "S_E_LAVAPLATOS_FEST_D");
		put("SERVICIO_EXTRA_CAMARERO_FESTIVOS_DIARIO", "S_E_CAMARERO_FEST_D");
		put("PLUS_DOMINGOS_Y_FESTIVOS_SIN_PRORRATEO_VACACIONES_DIARIO", "P_DOMIN_FEST_SP");
		put("PLUS_DOMINGOS_Y_FESTIVOS_CON_PRORRATEO_VACACIONES_DIARIO", "P_DOMIN_FEST_CP");
		put("TURNICIDAD_DIARIO", "TURNICIDAD_DIARIO");
		put("PLUS_TURNICIDAD_3_TURNOS_DIARIO", "P_TURNICIDAD_3T_D");
		put("PLUS_TURNICIDAD_2_TURNOS_DIARIO", "P_TURNICIDAD_2T_D");
		put("DIETA_JORNADA_COMPLETA_DIARIO", "DIETA_JORN_COMPLETA");
		put("DIETA_MEDIA_JORNADA_DIARIO", "DIETA_MEDIA_JORN");
		put("COMPENSACION_POR_COMIDA_DIARIO", "COMPEN_COMIDA");
		put("TRANSFER_SERVICIO_NOCTURNO_DIARIO", "T_SERVI_NOCT_DI");
		put("TRANSFER_SERVICIO_DIURNO_DIARIO", "T_SERVI_DIUR_DI");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_C10_MENSUAL", "SIST_INCENT_PROMO_C10_M");
		put("PLUS_VINCULACION_17_AÑOS_ANUAL", "P_VINCULACION_17_AÑOS_A");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_C10_RVOG_ANUAL", "SIST_INC_PROM_C10_RVOG_M");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_A5_ANUAL", "SIST_INCENT_PROMO_A5_A");
		put("PLUS_VINCULACION_16_AÑOS_MENSUAL", "P_VINCULACION_16_AÑOS_M");
		put("PLUS_VINCULACION_5_AÑOS_MENSUAL", "P_VINCULACION_5_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_8_AÑOS_ANUAL", "P_VINC_RVOG_8_AÑOS_A");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_B8_ANUAL", "SIST_INCENT_PROMO_B8_A");
		put("COMPLEMENTO_ATENCION_PRIMARIA_RVOG_ANUAL", "COMPL_ATEN_PRIM_RVOG_A");
		put("COMPLEMENTO_ATENCION_PROGRAMADA_RVOG_MENSUAL", "COMPL_ATEN_PRIM_RVOG_M");
		put("PLUS_RESPONSABILIDAD_MENSUAL", "PLUS_RESPONSABILIDAD_M");
		put("RETRIBUCION_FIJA_MENSUAL", "RETRIBUCION_FIJA_M");
		put("PLUS_VINCULACION_RVOG_15_AÑOS_ANUAL", "P_VINC_RVOG_15_AÑOS_A");
		put("PLUS_VINCULACION_21_AÑOS_ANUAL", "P_VINCULACION_21_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_11_AÑOS_MENSUAL", "P_VINC_RVOG_11_AÑOS_M");
		put("PLUS_VINCULACION_9_AÑOS_ANUAL", "P_VINCULACION_9_AÑOS_A");
		put("PLUS_VINCULACION_9_AÑOS_MENSUAL", "P_VINCULACION_9_AÑOS_M");
		put("PLUS_VINCULACION_11_AÑOS_ANUAL", "P_VINCULACION_11_AÑOS_A");
		put("PLUS_VINCULACION_14_AÑOS_ANUAL", "P_VINCULACION_14_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_9_AÑOS_MENSUAL", "P_VINC_RVOG_9_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_18_AÑOS_ANUAL", "P_VINC_RVOG_18_AÑOS_A");
		put("PLUS_VINCULACION_6_AÑOS_ANUAL", "P_VINCULACION_6_AÑOS_A");
		put("PLUS_CONVENIO_RVOG_MENSUAL", "PLUS_CONV_RVOG_M");
		put("COMPLEMENTO_ESPECIFICO_RVOG_MENSUAL", "COMPL_ESPECI_RVOG_M");
		put("SALARIO_BASE_RVOG_MENSUAL", "S_BASE_RVOG_M");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_B8_RVOG_ANUAL", "SIS_INC_PROMO_B8_RVOG_M");
		put("PLUS_VINCULACION_RVOG_12_AÑOS_ANUAL", "P_VINC_RVOG_12_AÑOS_A");
		put("PLUS_VINCULACION_1_AÑOS_ANUAL", "P_VINCULACION_1_AÑOS_A");
		put("RETRIBUCION_FIJA_RVOG_MENSUAL", "RETRIB_FIJA_RVOG_M");
		put("PLUS_VINCULACION_RVOG_5_AÑOS_ANUAL", "P_VINC_RVOG_5_AÑOS_A");
		put("PLUS_NOCTURNIDAD_ANUAL", "P_NOCTURNIDAD_A");
		put("PLUS_RESPONSABILIDAD_ANUAL", "P_RESPONSABILIDAD_A");
		put("PLUS_VINCULACION_RVOG_1_AÑOS_MENSUAL", "P_VINC_RVOG_1_AÑOS_M");
		put("PLUS_VINCULACION_18_AÑOS_ANUAL", "P_VINCULACION_18_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_10_AÑOS_ANUAL", "P_VINC_RVOG_10_AÑOS_A");
		put("COMPLEMENTO_ATENCION_CONTINUADA_MENSUAL", "COMPL_ATEN_CONT_M");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_A5_RVOG_MENSUAL", "S_INCENT_PROMO_A5_RVOG_M");
		put("PLUS_VINCULACION_5_AÑOS_ANUAL", "P_VINCULACION_5_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_20_AÑOS_ANUAL", "P_VINC_RVOG_20_AÑOS_A");
		put("PLUS_VINCULACION_20_AÑOS_MENSUAL", "P_VINCULACION_20_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_7_AÑOS_ANUAL", "P_VINC_RVOG_7_AÑOS_A");
		put("PLUS_NOCTURNIDAD_RVOG_MENSUAL", "P_NOCT_RVOG_M");
		put("PLUS_VINCULACION_3_AÑOS_ANUAL", "P_VINCULACION_3_AÑOS_A");
		put("COMPLEMENTO_ATENCION_PROGRAMADA_MENSUAL", "COMPL_ATEN_PROG_M");
		put("PLUS_RESPONSABILIDAD_RVOG_MENSUAL", "P_RESP_RVOG_A");
		put("PLUS_VINCULACION_18_AÑOS_MENSUAL", "P_VINCULACION_18_AÑOS_M");
		put("PLUS_VINCULACION_7_AÑOS_MENSUAL", "P_VINCULACION_7_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_13_AÑOS_MENSUAL", "P_VINC_RVOG_13_AÑOS_M");
		put("DIETA_CENA_DIARIO", "DIETA_CENA_D");
		put("PLUS_VINCULACION_4_AÑOS_ANUAL", "P_VINCULACION_4_AÑOS_A");
		put("COMPLEMENTO_ATENCION_CONTINUADA_RVOG_MENSUAL", "COMPL_ATEN_CONT_RVOG_M");
		put("PLUS_VINCULACION_RVOG_7_AÑOS_MENSUAL", "P_VINC_RVOG_7_AÑOS_M");
		put("COMPLEMENTO_ATENCION_PRIMARIA_RVOG_MENSUAL", "COMPL_ATEN_PRIM_RVOG_M");
		put("RETRIBUCION_FIJA_ANUAL", "RETRIB_FIJA_A");
		put("PLUS_VINCULACION_21_AÑOS_MENSUAL", "P_VINCULACION_21_AÑOS_M");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_A5_MENSUAL", "S_INCENT_PROMO_A5_M");
		put("PLUS_VINCULACION_RVOG_14_AÑOS_ANUAL", "P_VINC_RVOG_14_AÑOS_A");
		put("PLUS_VINCULACION_12_AÑOS_ANUAL", "P_VINCULACION_12_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_11_AÑOS_ANUAL", "P_VINC_RVOG_11_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_21_AÑOS_ANUAL", "P_VINC_RVOG_21_AÑOS_A");
		put("PLUS_VINCULACION_19_AÑOS_ANUAL", "P_VINCULACION_19_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_6_AÑOS_ANUAL", "P_VINC_RVOG_6_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_20_AÑOS_MENSUAL", "P_VINC_RVOG_20_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_1_AÑOS_ANUAL", "P_VINC_RVOG_1_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_21_AÑOS_MENSUAL", "P_VINC_RVOG_21_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_13_AÑOS_ANUAL", "P_VINC_RVOG_13_AÑOS_A");
		put("PLUS_VINCULACION_7_AÑOS_ANUAL", "P_VINCULACION_7_AÑOS_A");
		put("PLUS_VINCULACION_13_AÑOS_ANUAL", "P_VINCULACION_13_AÑOS_A");
		put("PLUS_VINCULACION_8_AÑOS_ANUAL", "P_VINCULACION_8_AÑOS_A");
		put("PLUS_VINCULACION_10_AÑOS_ANUAL", "P_VINCULACION_10_AÑOS_A");
		put("PLUS_VINCULACION_20_AÑOS_ANUAL", "P_VINCULACION_20_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_3_AÑOS_ANUAL", "P_VINC_RVOG_3_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_1_AÑO_ANUAL", "P_VINC_RVOG_1_AÑO_A");
		put("PLUS_VINCULACION_1_AÑO_ANUAL", "P_VINCULACION_1_AÑO_A");
		put("PLUS_VINCULACION_19_AÑOS_MENSUAL", "P_VINCULACION_19_AÑOS_M");
		put("PLUS_VINCULACION_6_AÑOS_MENSUAL", "P_VINCULACION_6_AÑOS_M");
		put("PLUS_VINCULACION_1_AÑO_MENSUAL", "P_VINCULACION_1_AÑO_M");
		put("PLUS_VINCULACION_17_AÑOS_MENSUAL", "P_VINCULACION_17_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_4_AÑOS_ANUAL", "P_VINC_RVOG_4_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_19_AÑOS_ANUAL", "P_VINC_RVOG_19_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_1_AÑO_MENSUAL", "P_VINC_RVOG_1_AÑO_M");
		put("PLUS_VINCULACION_8_AÑOS_MENSUAL", "P_VINCULACION_8_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_10_AÑOS_MENSUAL", "P_VINC_RVOG_10_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_12_AÑOS_MENSUAL", "P_VINC_RVOG_12_AÑOS_M");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_C10_ANUAL", "SIST_INC_PROM_C10_M");
		put("PLUS_RESPONSABILIDAD_RVOG_ANUAL", "P_RESP_RVOG_A");
		put("PLUS_VINCULACION_RVOG_6_AÑOS_MENSUAL", "P_VINC_RVOG_6_AÑOS_M");
		put("SALARIO_BASE_RVOG_ANUAL", "SALARIO_BASE_RVOG_A");
		put("PLUS_VINCULACION_RVOG_8_AÑOS_MENSUAL", "P_VINC_RVOG_8_AÑOS_M");
		put("COMPLEMENTO_SIPDP_RVOG_MENSUAL", "COMP_SIPDP_RVOG_M");
		put("COMPLEMENTO_ATENCION_PRIMARIA_ANUAL", "COMP_ATEN_PRIM_A");
		put("PLUS_VINCULACION_RVOG_2_AÑOS_MENSUAL", "P_VINC_RVOG_2_AÑOS_M");
		put("PLUS_VINCULACION_15_AÑOS_ANUAL", "P_VINCULACION_15_AÑOS_A");
		put("RETRIBUCION_EN_FUNCION_DE_OBJETIVOS_RVOG_MENSUAL", "RETRIB_FUNC_OBJ_RVOG_M");
		put("PLUS_VINCULACION_RVOG_2_AÑOS_ANUAL", "P_VINC_RVOG_2_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_17_AÑOS_ANUAL", "P_VINC_RVOG_17_AÑOS_A");
		put("PLUS_VINCULACION_RVOG_5_AÑOS_MENSUAL", "P_VINC_RVOG_5_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_4_AÑOS_MENSUAL", "P_VINC_RVOG_4_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_3_AÑOS_MENSUAL", "P_VINC_RVOG_3_AÑOS_M");
		put("RETRIBUCION_FIJA_RVOG_ANUAL", "RETRIB_FIJA_RVOG_A");
		put("COMPLEMENTO_ATENCION_PRIMARIA_MENSUAL", "COMPL_ATEN_PRIM_M");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_A5_RVOG_ANUAL", "S_INCENT_PROMO_A5_RVOG_A");
		put("PLUS_VINCULACION_2_AÑOS_ANUAL", "P_VINCULACION_2_AÑOS_A");
		put("PLUS_VINCULACION_10_AÑOS_MENSUAL", "P_VINCULACION_10_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_14_AÑOS_MENSUAL", "P_VINC_RVOG_14_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_15_AÑOS_MENSUAL", "P_VINC_RVOG_15_AÑOS_M");
		put("PLUS_VINCULACION_16_AÑOS_ANUAL", "P_VINCULACION_16_AÑOS_A");
		put("PLUS_CONVENIO_RVOG_ANUAL", "P_CONV_RVOG_A");
		put("PLUS_VINCULACION_11_AÑOS_MENSUAL", "P_VINCULACION_11_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_17_AÑOS_MENSUAL", "P_VINC_RVOG_17_AÑOS_M");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_C10_RVOG_MENSUAL", "SIS_INCEN_PROM_C10_RVOG_M");
		put("PLUS_VINCULACION_14_AÑOS_MENSUAL", "P_VINCULACION_14_AÑOS_M");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_B8_MENSUAL", "SIST_INCENT_PROMO_B8_M");
		put("PLUS_VINCULACION_15_AÑOS_MENSUAL", "P_VINCULACION_15_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_16_AÑOS_MENSUAL", "P_VINC_RVOG_16_AÑOS_M");
		put("PLUS_VINCULACION_4_AÑOS_MENSUAL", "P_VINCULACION_4_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_9_AÑOS_ANUAL", "P_VINC_RVOG_9_AÑOS_A");
		put("SISTEMA_INCENTIVACION_Y_PROMOCION_B8_RVOG_MENSUAL", "S_INC_PROMO_B8_RVOG_M");
		put("PLUS_VINCULACION_RVOG_19_AÑOS_MENSUAL", "P_VINC_RVOG_19_AÑOS_M");
		put("PLUS_VINCULACION_3_AÑOS_MENSUAL", "P_VINCULACION_3_AÑOS_M");
		put("RETRIBUCION_EN_FUNCION_DE_OBJETIVOS_MENSUAL", "RETRIB_FUNC_OBJ_M");
		put("COMPLEMENTO_SIPDP_MENSUAL", "COMPL_SIPDP_M");
		put("PLUS_VINCULACION_13_AÑOS_MENSUAL", "P_VINCULACION_13_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_18_AÑOS_MENSUAL", "P_VINC_RVOG_18_AÑOS_M");
		put("PLUS_VINCULACION_RVOG_16_AÑOS_ANUAL", "P_VINC_RVOG_16_AÑOS_A");
		put("PLUS_NOCTURNIDAD_RVOG_ANUAL", "P_NOCT_RVOG_A");
		put("PLUS_VINCULACION_2_AÑOS_MENSUAL", "P_VINCULACION_2_AÑOS_M");
		put("PLUS_VINCULACION_12_AÑOS_MENSUAL", "P_VINCULACION_12_AÑOS_M");
		put("PLUS_ESCAPARATES_MENSUAL", "PLUS_ESCAPARATES");
		put("AYUDA_HIJOS_MENSUAL", "AYUDA_HIJOS");
		put("GRATIFICACION_MATRIMONIO_MENSUAL", "GRATIFI_MATRIMONIO");
		put("SALARIO_12_PAGAS_MENSUAL", "SALARIO_12P_M");
		put("SALARIO_14_PAGAS_MENSUAL", "SALARIO_14P_M");
		put("SALARIO_15_PAGAS_MENSUAL", "SALARIO_15P_M");
		put("SALARIO_16_PAGAS_MENSUAL", "SALARIO_16P_M");
		put("SALARIO_MINIMO_GARANTIZADO_ANUAL", "S_MIN_GARANT_A");

	}};
	
	public static Pair<Integer,String> getAgreement(DSLContext dslContext, String agreementCode, Integer domainId) {
		DOMAIN_ID = domainId;
		Pair<Integer,String> agreementLog = new Pair<Integer, String>(-1, "");
		
		String log = "";
		Map<String, String> varNotInsertMap = new HashMap<String, String>();
		Pair<Integer,Map<String, String>> insertResult = new Pair<Integer, Map<String,String>>(-1, new HashMap<String, String>());
		
		InputStream is = null;
		if(AonStringUtils.contains(agreementCode, 'a'))
			is = AgreementParser.class.getResourceAsStream(agreementCode + ".xml");
		else
			is = ServiAgreement.get_online_file(agreementCode, Extension.XML);
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder documentBuilder;
		try {
			
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(is);
			
			// Agreement general info
			Agreement agreement = getAgreementInfo(dslContext, document);
			
			// Agreement Concepts
			getAgreementConcepts(dslContext, document, agreement);
			
			// Agreement levels and categories
			getAgreementLevelAndCategory(dslContext, document, agreement);
			
			// Agreement levels data
			getAgreementLevelData(dslContext, document, agreement);
			
			// Insert Agreement to DataBase
			insertResult = insertAgreementDB(dslContext, agreement, agreementCode);
			
//			System.out.println("serviAgreementsMap.put(\"" + agreement.getSSCode() + " - " + agreement.getAgreementDescription().toUpperCase() + "\",  \"" + agreement.getServiAgreementCode() + "\");"  );
			
			varNotInsertMap = insertResult.getSecond();
			
			if(!varNotInsertMap.isEmpty()) {
				log = getAgreementLog(dslContext, domainId, agreement, varNotInsertMap);
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		agreementLog.setFirst(insertResult.getFirst());
		agreementLog.setSecond(log);
		
		return agreementLog;
	}

	private static String getAgreementLog(DSLContext dslContext, Integer domainId, Agreement agreement, Map<String, String> varNotInsertMap) {
		// Domain Record
		Record domainRecord = dslContext.select().from(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetchOne();
		
		String html = "<html>";
		
		html += "<head>";
		html += "<style>";
		html += "#serviAgrement {";
			html += "font-family: Arial, Helvetica, sans-serif;";
			html += "border-collapse: collapse;";
			html += "width: 100%;";
			html += "}";

		html += "#serviAgrement td, #serviAgrement th {";
			html += "border: 1px solid #ddd;";
			html += "padding: 8px;";
			html += "}";

		html += "#serviAgrement tr:nth-child(even){background-color: #f2f2f2;}";

		html += "#serviAgrement tr:hover {background-color: #ddd;}";

		html += "#serviAgrement th {";
			html += "padding-top: 12px;";
			html += "padding-bottom: 12px;";
			html += "text-align: left;";
			html += "background-color: #0065a8;";
			html += "color: white;";
			html += "}";
		html += "</style>";
		html += "</head>";
		
		html += "<body>";
		
		html += "<div style=\"font-family: \"Lucida Sans Unicode\", \"Lucida Grande\", sans-serif;font-size: 12px;letter-spacing: 2px;word-spacing: 0px;color: #000000;font-weight: normal;text-decoration: none;font-style: normal;font-variant: normal;text-transform: none;\">";
		html += "<p>Estimado desarrollador:</p>";
		html += "<p>Le adjuntamos el log generado a la hora de intentar importar un convenio desde la plataforma de ServiConvenios.</p>";
		
		// Domain Data
		
		html += "<ul>";
		html += "<li>";
		html += "<a style=\"font-weight: bold; color: black;\"> Domain Id : </a>" + domainId;
		html += "</li>";
		html += "<li>";
		html += "<a style=\"font-weight: bold; color: black;\"> Domain URL : </a>" + domainRecord.get(DOMAIN.NAME);
		html += "</li>";
		html += "<li>";
		html += "<a style=\"font-weight: bold; color: black;\"> Domain Description : </a>" + domainRecord.get(DOMAIN.DESCRIPTION);
		html += "</li>";
		html += "</ul>";
		
		// Domain Data
		
		html += "<ul>";
		html += "<li>";
		html += "<a style=\"font-weight: bold; color: black;\"> Agreement SS Code : </a>" + agreement.getSSCode();
		html += "</li>";
		html += "<li>";
		html += "<a style=\"font-weight: bold; color: black;\"> Agreement Description : </a>" + agreement.getAgreementDescription().toUpperCase();
		html += "</li>";
		html += "<li>";
		html += "<a style=\"font-weight: bold; color: black;\"> ServiAgreement Code : </a>" + agreement.getServiAgreementCode();
		html += "</li>";
		html += "</ul>";
		
		html += "<br>";
		
		html += "<p>Variables que no se han podido insertar : </p>";
		
		html += "<br>";
		
		html += "<table id=\"serviAgrement\">";
		html += "<tr>";
		html += "<th>Nombre ServiConvenios</th>";
		html += "<th>Nombre AON (Revisar)</th>";
		html += "</tr>";

		for(Entry<String, String> entry: varNotInsertMap.entrySet()) {
			html += "<tr>";
			html += "<td>" + entry.getKey() + "</td>";
			html += "<td>" + entry.getValue() + "</td>";
			html += "</tr>";
		}
		
		html += "</table>";
		
		html += "</div>";
		html += "</body>";
		html += "</html>";
		
		return html;
	}

	private static Agreement getAgreementInfo(DSLContext dslContext, Document document) {
		NodeList list = document.getElementsByTagName("DATOS_GENERALES");
		Agreement agreement = null;
		
		for(int i=0; i<list.getLength(); i++) {
			Node node = list.item(i);

	        if (node.getNodeType() == Node.ELEMENT_NODE) {

	            Element element = (Element) node;
	            
	            String description = element.getElementsByTagName("NOMBRE").item(0).getTextContent();
	            String ssCode = element.getElementsByTagName("CODIGO_SS").item(0).getTextContent();
	            String serviAgreementCode = element.getElementsByTagName("FICHEROS").item(0).getTextContent();
	            
	            Date lastUpdate = null;
				try {
					lastUpdate = dateFormat.parse(element.getElementsByTagName("FECHA_ULT_ACT").item(0).getTextContent());
				} catch (DOMException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				String startDateYear = element.getElementsByTagName("AÑO_APLIC_DESDE").item(0).getTextContent();
				Calendar startDateCal = Calendar.getInstance();
				startDateCal.set(Calendar.YEAR, Integer.parseInt(startDateYear));
				startDateCal.set(Calendar.MONTH, 0);
				startDateCal.set(Calendar.DAY_OF_MONTH, 1);
				Date startDate = startDateCal.getTime();
				
	            
	            agreement = new Agreement(description, ssCode, serviAgreementCode, lastUpdate, startDate);
	            
	        }
		}
		
		return agreement;
	}
	
	private static void getAgreementConcepts(DSLContext dslContext, Document document, Agreement agreement) {
		NodeList listCPR = document.getElementsByTagName("CATALOGO_CPTOS_RETRIB");
		
		for(int i=0; i<listCPR.getLength(); i++) {
			Node nodeCPR = listCPR.item(i);

	        if (nodeCPR.getNodeType() == Node.ELEMENT_NODE) {

	            Element elementCPR = (Element) nodeCPR;
	            
	            NodeList listCI = elementCPR.getElementsByTagName("CPTO_IT");
	            
	            for(int j=0; j<listCI.getLength(); j++) {
	    			Node nodeCI = listCI.item(j);

	    	        if (nodeCI.getNodeType() == Node.ELEMENT_NODE) {
	    	        	Element elementCI = (Element) nodeCI;
	    	        	
	    	        	String name = elementCI.getElementsByTagName("NOMBRE").item(0).getTextContent();
	    	            String type = elementCI.getElementsByTagName("TIPO_AMDH").item(0).getTextContent();
	    	            
	    	            String realName = getParseName(name, type);
	    	            
	    	            agreement.addAgreementConcept(realName);
	    	        }
	            }   
	        }
		}
	}

	private static void getAgreementLevelAndCategory(DSLContext dslContext, Document document, Agreement agreement) {
		NodeList list = document.getElementsByTagName("CATALOGO_CAT_PROF");
		for(int i=0; i<list.getLength(); i++) {
			Node node = list.item(i);

	        if (node.getNodeType() == Node.ELEMENT_NODE) {

	            Element element = (Element) node;
	            
	            NodeList listCatProfIt = element.getElementsByTagName("CAT_PROF_IT");
	            
	            for(int j=0; j<listCatProfIt.getLength(); j++) {
	    			Node nodeCatProfIt = listCatProfIt.item(j);

	    	        if (nodeCatProfIt.getNodeType() == Node.ELEMENT_NODE) {

	    	            Element elementCatProfIt = (Element) nodeCatProfIt;
	    	            
	    	            String code = elementCatProfIt.getElementsByTagName("CODIGO").item(0).getTextContent();
	    	            
	    	            NodeList listdescriptions = elementCatProfIt.getElementsByTagName("GRUPO");
	    	            String description = "";
	    	            
	    	            if(listdescriptions.getLength() == 0) {
	    	            	description = "NIVEL " + (j+1);
	    	            } else {
		    	            for(int b=0; b<listdescriptions.getLength(); b++)
		    	            	description += listdescriptions.item(b).getTextContent() + " ";
		    	            description.trim();
	    	            }
	    	            
	    	            String category = elementCatProfIt.getElementsByTagName("NOMBRE").item(0).getTextContent();
	    	            
//	    	            AgreementLevel agreementLevel = agreement.checkLevelExist(description);
//	    	            
//	    	            if(null != agreementLevel)
//	    	            	description = agreementLevel + "_" + j;
//	    	            
	    	            agreement.addAgreementLevel(code, description, category);
	    	            
	    	        }
	    		}
	        }
		}
	}
	
	private static void getAgreementLevelData(DSLContext dslContext, Document document, Agreement agreement) {
		NodeList listTS = document.getElementsByTagName("TABLAS_SALARIALES");
		for(int i=0; i<listTS.getLength(); i++) {
			Node nodeTS = listTS.item(i);

	        if (nodeTS.getNodeType() == Node.ELEMENT_NODE) {

	            Element elementTS = (Element) nodeTS;
	            
	            NodeList listTSI = elementTS.getElementsByTagName("TAB_SAL_IT");
	            
	            for(int j=0; j<listTSI.getLength(); j++) {
	    			Node nodeTSI = listTSI.item(j);

	    	        if (nodeTSI.getNodeType() == Node.ELEMENT_NODE) {

	    	            Element elementTSI = (Element) nodeTSI;
	    	            
	    	            String yearStr = elementTSI.getElementsByTagName("AÑO").item(0).getTextContent();
	    	            Integer year = Integer.parseInt(yearStr);
	    	            
	    	            // Calendar StartDate
	    	            Calendar startDate = Calendar.getInstance();
	    	            startDate.set(Calendar.YEAR, year);
	    	            startDate.set(Calendar.MONTH, 0);
	    	            startDate.set(Calendar.DAY_OF_MONTH, 1);
	    	            
	    	            // EndDate
	    	            Calendar endDate = Calendar.getInstance();
	    	            endDate.set(Calendar.YEAR, year-1);
	    	            endDate.set(Calendar.MONTH, 11);
	    	            endDate.set(Calendar.DAY_OF_MONTH, 31);
	    	            
	    	            agreement.setEndDateToExistingLevelData(endDate.getTime());
	    	            
	    	            NodeList listCP = elementTSI.getElementsByTagName("CATEGORIAS_PROFESIONALES");
	    	            
	    	            for(int k=0; k<listCP.getLength(); k++) {
	    	            	Node nodeCP = listCP.item(k);
	    	            	
	    	            	if (nodeCP.getNodeType() == Node.ELEMENT_NODE) {
	    	            		Element elementCP = (Element) nodeCP;
	    	     	            
	    	     	            NodeList listCPI = elementCP.getElementsByTagName("CAT_PROF_IT");
	    	     	            
	    	     	            for(int l=0; l<listCPI.getLength(); l++) {
	    	     	            	Node nodeCPI = listCPI.item(l);
	    	     	            	
	    	     	            	if (nodeCPI.getNodeType() == Node.ELEMENT_NODE) {
	    	    	            		Element elementCPI = (Element) nodeCPI;

		    	     	            	NodeList listdescriptions = elementCPI.getElementsByTagName("GRUPO");
		    		    	            String description = "";
		    		    	            
		    		    	            if(listdescriptions.getLength() == 0)
		    		    	            	description = "NIVEL " + (l+1);
		    		    	            else {
			    		    	            for(int b=0; b<listdescriptions.getLength(); b++)
			    		    	            	description += listdescriptions.item(b).getTextContent() + " ";
			    		    	            description.trim();
		    		    	            }
		    		    	            
			   	    	            	String category = elementCPI.getElementsByTagName("NOMBRE").item(0).getTextContent();
			   	    	            	
				   	    	            AgreementLevel agreementLevel = agreement.getAgreementLevel(description, category);
			   	    	            	
			   	    	            	Node nodeConcept = elementCPI.getElementsByTagName("CONCEPTOS").item(0);
			   	    	            	if (null != nodeConcept && nodeConcept.getNodeType() == Node.ELEMENT_NODE) {
			   	    	            		Element elementConcept = (Element) nodeConcept;
			   	    	            		
			   	    	            		NodeList listCPTO = elementConcept.getElementsByTagName("CPTO_IT");
				   	    	            	
				   	    	            	for(int b=0; b<listCPTO.getLength(); b++) {
				    	     	            	Node nodeCPTO = listCPTO.item(b);
				    	     	            	
				    	     	            	if (nodeCPTO.getNodeType() == Node.ELEMENT_NODE) {
				    	     	            		Element elementCPTO = (Element) nodeCPTO;
				    	     	            		
				    	     	            		String name = elementCPTO.getElementsByTagName("NOMBRE").item(0).getTextContent();
						   	    	            	String value = elementCPTO.getElementsByTagName("IMPORTE").item(0).getTextContent();
						   	    	            	String type = elementCPTO.getElementsByTagName("TIPO_AMDH").item(0).getTextContent();
						   	    	            	
						   	    	            	String realName = getParseName(name, type);
						   	    	            	
						   	    	            	agreementLevel.addLevelData(realName, value, startDate.getTime());
				    	     	            	}
				   	    	            	}
			   	    	            	}
	    	     	            	}
	    	     	            } 
	    	            	}
	    	            }
	    	        }
	    		}
	        }
		}
	}
	
	private static Pair<Integer,Map<String, String>> insertAgreementDB(DSLContext dslContext, Agreement agreement, String agreementCode) {
		Pair<Integer,Map<String, String>> result = new Pair<Integer, Map<String,String>>(-1, new HashMap<String, String>());
		// Variables not insert
		Map<String, String> mapVarNotInsert = new HashMap<String, String>();
		
		// Agreement
		
		AgreementRecord agreementRecord = dslContext.insertInto(AGREEMENT)
			.set(AGREEMENT.DOMAIN, DOMAIN_ID)
			.set(AGREEMENT.DESCRIPTION, parseDescription(agreement.getAgreementDescription()))
			.set(AGREEMENT.SS_NUMBER, agreement.getSSCode())
			.returning(AGREEMENT.ID)
			.fetchOne();
		
		Integer agreementId = agreementRecord.getId();
		
		// Agreement Level / Agreement Level Category / Agreement Level Data
		
		String oldLevelDescription = null;
		Integer count = 1;
		
		for(AgreementLevel lvl : agreement.getAgreementLevels()) {
			
			// Agreement Level
			
			String levelDescription = parseLevelDescription(lvl.getDescription());
			
			if(null != oldLevelDescription && (oldLevelDescription ==  levelDescription|| oldLevelDescription.equals(levelDescription))) {
				if(count >= 10 && levelDescription.length() >= 61)
					levelDescription = levelDescription.substring(0, 61);
				else if(levelDescription.length() >= 62)
					levelDescription = levelDescription.substring(0, 61);
				levelDescription = levelDescription + "_" + count;
				count++;
			} else {
				oldLevelDescription = levelDescription;
				count = 1;
			}
			
//			System.out.println(levelDescription + " - " + levelDescription.length());
			
			AgreementLevelRecord agreementLevelRecord = dslContext.insertInto(AGREEMENT_LEVEL)
				.set(AGREEMENT_LEVEL.DOMAIN, DOMAIN_ID)
				.set(AGREEMENT_LEVEL.AGREEMENT, agreementId)
				.set(AGREEMENT_LEVEL.DESCRIPTION, levelDescription)
				.returning(AGREEMENT_LEVEL.ID)
				.fetchOne();
			
			Integer agreementLevelId = agreementLevelRecord.getId();
			
			// Agreement Level Category
			
			for(String lvlCategory : lvl.getLevelCategories()) {
				dslContext.insertInto(AGREEMENT_LEVEL_CATEGORY)
					.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, DOMAIN_ID)
					.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelId)
					.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, parseDescription(lvlCategory))
					.execute();
			}
			
			// Agreement Level Data
			
			for(AgreementLevelData lvlData : lvl.getLevelDatas()) {
				String realName = variablesNameMap.getOrDefault(lvlData.getName(), null);
				
				if(null != realName) {
					dslContext.insertInto(AGREEMENT_LEVEL_DATA)
						.set(AGREEMENT_LEVEL_DATA.DOMAIN, DOMAIN_ID)
						.set(AGREEMENT_LEVEL_DATA.NAME, realName)
						.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, agreementLevelId)
						.set(AGREEMENT_LEVEL_DATA.EXPRESSION, lvlData.getValue())
						.set(AGREEMENT_LEVEL_DATA.START_DATE, parseDateToSql(lvlData.getStartDate()))
						.set(AGREEMENT_LEVEL_DATA.END_DATE, parseDateToSql(lvlData.getEndDate()))
						.execute();
				} else {
					if( !AonStringUtils.containsIgnoreCase(lvlData.getName(), "TOTAL") &&
						!AonStringUtils.containsIgnoreCase(lvlData.getName(), "PAGA_EXTRA") &&
						!AonStringUtils.containsIgnoreCase(lvlData.getName(), "+") &&
						!AonStringUtils.containsIgnoreCase(lvlData.getName(), "%"))
							
							mapVarNotInsert.put(lvlData.getName(), lvlData.getName());
				}
			}
			
		}
		
		// Agreement Data
		
		Date auxEndDate = null;
		
		dslContext.insertInto(AGREEMENT_DATA)
			.set(AGREEMENT_DATA.DOMAIN, DOMAIN_ID)
			.set(AGREEMENT_DATA.NAME, "PAGAS")
			.set(AGREEMENT_DATA.AGREEMENT, agreementId)
			.set(AGREEMENT_DATA.EXPRESSION, "14")
			.set(AGREEMENT_DATA.START_DATE, parseDateToSql(agreement.getStartDate()))
			.set(AGREEMENT_DATA.END_DATE, parseDateToSql(auxEndDate))
			.execute();
		
		// Agreement Payment
		
		for(String agreementConceptName : agreement.getAgreementConcepts()) {
			AgreementPayment agreementPayment = AgreementPayment.safeValueOf(agreementConceptName);
			
			if(null != agreementPayment) {
//				System.out.println(agreementPayment.getConceptCode());
				PaymentConceptRecord paymentConceptRecord = dslContext.insertInto(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.DOMAIN, DOMAIN_ID)
						.set(PAYMENT_CONCEPT.CODE, agreementPayment.getConceptCode())
						.set(PAYMENT_CONCEPT.DESCRIPTION, agreementPayment.getNormalizeName())
						.set(PAYMENT_CONCEPT.TYPE, agreementPayment.getType())
						.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
						.set(PAYMENT_CONCEPT.EXPRESSION, agreementPayment.getExpression())
						.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
						.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
						.returning(PAYMENT_CONCEPT.ID)
						.fetchOne();
				
				Integer paymentConceptId = paymentConceptRecord.getId();
				
				dslContext.insertInto(AGREEMENT_PAYMENT)
						.set(AGREEMENT_PAYMENT.DOMAIN, DOMAIN_ID)
						.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
						.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
						.set(AGREEMENT_PAYMENT.TYPE, agreementPayment.getType())
						.set(AGREEMENT_PAYMENT.EXPRESSION, agreementPayment.getExpression())
						.set(AGREEMENT_PAYMENT.DESCRIPTION, agreementPayment.getNormalizeName())
						.set(AGREEMENT_PAYMENT.START_DATE, parseDateToSql(agreement.getStartDate()))
						.set(AGREEMENT_PAYMENT.END_DATE, parseDateToSql(auxEndDate))
						.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
						.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
						.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "_P")
						.execute();
			}
		}
		
		Integer paymentConceptId = insertOrGetPaymentConceptExtraPay(dslContext);
		
		AgreementPaymentRecord agreementPaymentRecord = dslContext.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, DOMAIN_ID)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
			.set(AGREEMENT_PAYMENT.TYPE, (byte)4)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "SALARIO_BASE+PLUS_SALARIAL")
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[90] PAGA VERANO")
			.set(AGREEMENT_PAYMENT.START_DATE, parseDateToSql(agreement.getStartDate()))
			.set(AGREEMENT_PAYMENT.END_DATE, parseDateToSql(auxEndDate))
			.set(AGREEMENT_PAYMENT.MONTH, (byte)6)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "_P")
			.returning(AGREEMENT_PAYMENT.ID)
			.fetchOne();
		
		Integer agreementPaymentId = agreementPaymentRecord.getId();
		
		dslContext.insertInto(AGREEMENT_EXTRA)
			.set(AGREEMENT_EXTRA.DOMAIN, DOMAIN_ID)
			.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
			.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPaymentId)
			.set(AGREEMENT_EXTRA.START_DATE, "1/1")
			.set(AGREEMENT_EXTRA.END_DATE, "30/6")
			.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/7")
			.execute();
		
		agreementPaymentRecord = dslContext.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, DOMAIN_ID)
			.set(AGREEMENT_PAYMENT.AGREEMENT, agreementId)
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
			.set(AGREEMENT_PAYMENT.TYPE, (byte)4)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "SALARIO_BASE+PLUS_SALARIAL")
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "[91] PAGA NAVIDAD")
			.set(AGREEMENT_PAYMENT.START_DATE, parseDateToSql(agreement.getStartDate()))
			.set(AGREEMENT_PAYMENT.END_DATE, parseDateToSql(auxEndDate))
			.set(AGREEMENT_PAYMENT.MONTH, (byte)11)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "_P")
			.returning(AGREEMENT_PAYMENT.ID)
			.fetchOne();
		
		agreementPaymentId = agreementPaymentRecord.getId();
		
		dslContext.insertInto(AGREEMENT_EXTRA)
		.set(AGREEMENT_EXTRA.DOMAIN, DOMAIN_ID)
		.set(AGREEMENT_EXTRA.AGREEMENT, agreementId)
		.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPaymentId)
		.set(AGREEMENT_EXTRA.START_DATE, "1/7")
		.set(AGREEMENT_EXTRA.END_DATE, "31/12")
		.set(AGREEMENT_EXTRA.ISSUE_DATE, "31/12")
		.execute();
		
		// Set agreement_data is ServiAgreement
		
		if(!AonStringUtils.contains(agreementCode, 'a'))
			dslContext.insertInto(AGREEMENT_DATA)
				.set(AGREEMENT_DATA.DOMAIN, DOMAIN_ID)
				.set(AGREEMENT_DATA.NAME, "SERVIAGREEMENT")
				.set(AGREEMENT_DATA.AGREEMENT, agreementId)
				.set(AGREEMENT_DATA.EXPRESSION, "TRUE")
				.set(AGREEMENT_DATA.START_DATE, parseDateToSql(agreement.getStartDate()))
				.set(AGREEMENT_DATA.END_DATE, parseDateToSql(auxEndDate))
				.execute();
		
		result.setFirst(agreementId);
		result.setSecond(mapVarNotInsert);
		
		return result;
		
	}
	
	private static Integer insertOrGetPaymentConceptExtraPay(DSLContext dslContext) {
		
		Result<Record> paymentConceptRecords = dslContext.select().from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(DOMAIN_ID))
			.and(PAYMENT_CONCEPT.CODE.eq("PAGA_EXTRA"))
			.and(PAYMENT_CONCEPT.DESCRIPTION.eq("PAGA EXTRAORDINARIA"))
			.and(PAYMENT_CONCEPT.EXPRESSION.eq("SALARIO_BASE+PLUS_SALARIAL"))
			.and(PAYMENT_CONCEPT.TYPE.eq((byte)4))
			.fetch();
		
		if(paymentConceptRecords.isNotEmpty())
			return paymentConceptRecords.get(0).get(PAYMENT_CONCEPT.ID);
			
		PaymentConceptRecord paymentConceptRecord = dslContext.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.DOMAIN, DOMAIN_ID)
				.set(PAYMENT_CONCEPT.CODE, "PAGA_EXTRA")
				.set(PAYMENT_CONCEPT.DESCRIPTION, "PAGA EXTRAORDINARIA")
				.set(PAYMENT_CONCEPT.TYPE, (byte)4)
				.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
				.set(PAYMENT_CONCEPT.EXPRESSION, "SALARIO_BASE+PLUS_SALARIAL")
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
				.returning(PAYMENT_CONCEPT.ID)
				.fetchOne();
		
		return paymentConceptRecord.getId();
	}

	private static String parseDescription(String description) {
		if(description.contains("CONVENIO COLECTIVO")){
			description = "CC" + description.split("CONVENIO COLECTIVO")[1];
		}
		if(description.length() > 64)
			return description.substring(0, 63);
		return description;
	}
	
	private static String parseLevelDescription(String description) {
		if(description.length() > 64)
			return description.substring(0, 61).trim();
		return description.trim();
	}
	
	private static java.sql.Date parseDateToSql(Date date) {
		if(null == date)
			return null;
		
		return new java.sql.Date(date.getTime());
	}
	
	private static String getParseName(String name, String type) {
		String realName = "";
		
		name = name.replaceAll(" ", "_");
		name = name.replaceAll(",", "");
		name = name.replaceAll("\\.", "_");
		name = name.replaceAll("\\*", "");
		
		switch (type) {
		case "A":
			realName = name + "_" + "ANUAL";
			break;
		case "M":
			realName = name + "_" + "MENSUAL";
			break;
		case "D":
			realName = name + "_" + "DIARIO";
			break;
		case "H":
			realName = name + "_" + "HORAS";
			break;
		default:
			realName = name + "_" + "ANUAL";
			break;
		}
		
		return realName;
	}

	public static void main(String[] args) {
		//@formatter:off
		Option hostName = getHostNameOption();
		Option user = getDbUserOption();
		Option password = getDbPasswordOption();
		Option database = getDatabaseOption();
		Option agreementCodeOpt = getAgreementCodeOption();
		Option domainOpt = getAgreementDomainOption();

		Options options = new Options()
				.addOption(hostName)
				.addOption(user)
				.addOption(password)
				.addOption(database)
				.addOption(agreementCodeOpt)
				.addOption(domainOpt)
				;	
		//@formatter:on

		// Create the parser
		CommandLineParser parser = new DefaultParser();

		try {
			Class.forName(com.mysql.cj.jdbc.Driver.class.getName());
			
			// Parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			Connection connection = DriverManager.getConnection(
					String.format(
							"jdbc:mysql://%s:%d/%s",
							cmd.getOptionValue(hostName.getLongOpt(), "127.0.0.1"),
							3306, 
							cmd.getOptionValue(database.getLongOpt())
					),
					cmd.getOptionValue(user.getLongOpt()),
					cmd.getOptionValue(password.getLongOpt()));
			
			
			String agreementCode = cmd.getOptionValue(agreementCodeOpt.getLongOpt());
			String domainIdStr = cmd.getOptionValue(domainOpt.getLongOpt(), null);
			
			if(null != domainIdStr)
				DOMAIN_ID = Integer.parseInt(domainIdStr);
			
			// Get dslContext for given connection
			AONContext ctx = new AONContext(connection);
			DSLContext dslContext = ctx.getDslContext();
			
//			getAgreement(dslContext, agreementCode);
			
			Pair<Integer,String> agreementResult = new Pair<Integer, String>(-1, "");
			
			String log = "";
			agreementResult = getAgreement(dslContext, agreementCode, Integer.parseInt(domainIdStr));
			log = agreementResult.getSecond();
			
//			for(String agreementCodeAux : agreementCodes)
//				log += getAgreement(dslContext, agreementCodeAux, Integer.parseInt(domainIdStr));
			
			if(AonStringUtils.isNotBlank(log)) {
				PrintWriter out = new PrintWriter(new File("/Users/sergio/Desktop/ParseAgreement_NotValidVars.txt"));
				out.write(log);
				out.close();
			}
			

		} catch (ClassNotFoundException | SQLException | org.apache.commons.cli.ParseException | FileNotFoundException e) {
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("AGREEMENT", options);
		}
	}
	
	private static Option getAgreementCodeOption() {
		return Option.builder("a")
				.longOpt("agreementCode")
				.desc("Servi Agreement Code : c00000xx")
				.required()
				.argName("name")
				.hasArg()
				.build();
	}
	
	private static Option getAgreementDomainOption() {
		return Option.builder("d")
				.longOpt("domain")
				.desc("Domain to save Agreement")
				.argName("name")
				.hasArg()
				.build();
	}
}
