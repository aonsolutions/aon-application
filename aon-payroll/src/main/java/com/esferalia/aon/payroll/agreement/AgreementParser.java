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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.esferalia.aon.payroll.agreement.Agreement.AgreementLevel;
import com.esferalia.aon.payroll.agreement.Agreement.AgreementLevelData;
import com.esferalia.aon.payroll.agreement.ServiAgreement.Extension;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AgreementParser {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static Integer DOMAIN_ID = 0;
	
	private static List<String> agreementCodes = new ArrayList<String>() {{
		add("c0000000");
		add("c0000001");
		add("c0000002");
		add("c0000017");
		add("c0000022");
		add("c0000023");
		add("c0000047");
		add("c0000055");
		add("c0000065");
		add("c0000080");
		add("c0000086");
		add("c0000087");
		add("c0000088");
		add("c0000131");
		add("c0000139");
		add("c0000183");
		add("c0000184");
		add("c0000218");
		add("c0000233");
		add("c0000235");
		add("c0000268");
		add("c0000286");
		add("c0000300");
		add("c0000310");
		add("c0000332");
		add("c0000429");
		add("c0000474");
		add("c0000645");
		add("c0000649");
		add("c0000679");
		add("c0000710");
		add("c0000741");
		add("c0000836");
		add("c0000905");
		add("c0001091");
		add("c0001093");
		add("c0001124");
		add("c0001285");
		add("c0001313");
		add("c0001385");
		add("c0001393");
		add("c0001672");
		add("c0001710");
		add("c0001911");
		add("c0001931");
		add("c0001940");
		add("c0002060");
	}};
	
	@SuppressWarnings("serial")
	private static Map<String, String> variablesNameMap = new HashMap<String, String>(){{
		put("SALARIO_CONVENIO_ANUAL", "SALARIO_ANUAL");
		put("SALARIO_CONVENIO_ANUAL_ANUAL", "SALARIO_ANUAL");
		put("SALARIO_BASE_ANUAL", "SALARIO_ANUAL");
		put("SALARIO_BASE_ANUAL_ANUAL", "SALARIO_ANUAL");
		put("SALARIO_CONVENIO_MENSUAL", "SALARIO_MENSUAL");
		put("SALARIO_BASE_MENSUAL", "SALARIO_MENSUAL");
		put("SALARIO_CONVENIO_HORAS", "SALARIO_HORA");
		put("SALARIO_HORA_HORAS", "SALARIO_HORA");
		put("SALARIO_BASE_HORAS", "SALARIO_HORA");
		put("SALARIO_CONVENIO_DIARIO", "SALARIO_DIARIO");
		put("SALARIO_BASE_DIARIO", "SALARIO_DIARIO");
		put("SALARIO_BASE_MINIMO_ANUAL", "PORC_SALARIO");
		put("PLUS_CONVENIO_ANUAL", "PLUS_CONVENIO_ANUAL");
		put("PLUS_CONVENIO_MENSUAL", "PLUS_CONVENIO");
		put("COMPLEMENTO_CONVENIO_MENSUAL", "PLUS_CONVENIO_MENSUAL");
		put("PLUS_MANUTENCION_NO_OBLIGATORIA_MENSUAL", "P_MANUTENCION_NO_OBL_M");
		put("PLUS_MANUTENCION_MENSUAL", "P_MANUTENCION_M");
		put("PLUS_CONVENIO_HORAS", "PLUS_CONVENIO_HORAS");
		put("COMPLEMENTO_CONVENIO_DIARIO", "PLUS_CONVENIO_DIARIO");
		put("PLUS_CONVENIO_DIARIO", "PLUS_CONVENIO_DIARIO");
		put("PLUS_SALARIAL_DIARIO", "PLUS_DIARIO");
		put("PLUS_SALARIAL_LABORAL_DIARIO", "PLUS_LABORABLES");
		put("PLUS_SALARIAL_FIJO_MENSUAL", "PLUS_FIJO");
		put("PLUS_EXTRA_CATEGORIA_ANUAL", "PLUS_EXTRA_CATEGORIA_A");
		put("PLUS_HERRAMIENTAS_ANUAL", "PLUS_HERRAMIENTAS_A");
		put("PLUS_HERRAMIENTAS_DIARIO", "PLUS_HERRAMIENTAS_D");
		put("PLUS_ACTIVIDAD_MENSUAL", "PLUS_ACTIVIDAD_MENSUAL");
		put("PLUS_ACTIVIDAD_DIARIO", "PLUS_ACTIVIDAD_DIARIO");
		put("PLUS_EXTRA_SALARIAL_MENSUAL", "PLUS_XS_MENSUAL");
		put("PLUS_EXTRA_SALARIAL_DIARIO", "PLUS_XS_DIARIO");
		put("PLUS_EXTRA_SALARIAL_LABORAL_DIARIO", "PLUS_XS_LABORABLES");
		put("PLUS_EXTRA_SALARIAL_FIJO_MENSUAL", "PLUS_XS_FIJO");
		put("PLUS_VESTUARIO_MENSUAL", "PLUS_VESTUARIO");
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
		put("PLUS_TOXICIDAD_MENSUAL", "PLUS_TOXICIDAD");
		put("PLUS_TOXICIDAD_DIARIO", "PLUS_TOXICIDAD");
		put("PLUS_ACERCAMIENTO_ANUAL", "PLUS_ACERCAMIENTO_ANUAL");
		put("COMPLEMENTO_LINEAL_ANUAL", "COMPL_LINEAL_A");
		put("COMPLEMENTO_NACIMIENTO_O_ADOPCION_DE_HIJO_ANUAL", "COMPL_NAC_ADOP_HIJO");
		put("COMPLEMENTO_ESPECIFICO_MENSUAL", "COMPL_ESPECIFICO_MEN");
		put("COMPLEMENTO_NO_SALARIAL_ANUAL", "COMPL_NO_SALARIAL_ANUAL");
		put("COMPLEMENTO_NO_SALARIAL_MENSUAL", "COMPL_NO_SALARIAL_MEN");
		put("COMPLEMENTO_NO_SALARIAL_DIARIO", "COMPL_NO_SALARIAL_DIA");
		put("PLUS_FESTIVO_NOCTURNO_DIARIO", "P_FESTIVO_NOCT_D");
		put("PLUS_PUNTUALIDAD_MENSUAL", "PLUS_PUNTUALIDAD_M");
		put("PLUS_IDIOMAS_MENSUAL", "PLUS_IDIOMAS_M");
		put("PLUS_NOCTURNIDAD_MENSUAL", "PLUS_NOCTURNIDAD_M");
		put("PLUS_NOCTURNIDAD_HORAS", "PLUS_NOCTURNIDAD_H");
		put("PLUS_DE_LAVADO_HORAS", "PLUS_LAVADO_H");
		put("PLUS_DESPLAZAMIENTOS_HORAS", "PLUS_DESPLAZAMIENTOS_H");
		put("PLUS_HOSPITAL_HORAS", "PLUS_HOSPITAL_H");
		put("PLUS_CENTRO_DE_SALUD_HORAS", "PLUS_CENT_SALUD_H");
		put("PLUS_GERIATRICO_HORAS", "PLUS_GERIATRICO_H");
		put("PLUS_DISPONIBILIDAD_HORAS", "PLUS_DISPONIBILIDAD_H");
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
		put("HORA_REGULARIZACION_HORAS", "HORA_REGULARIZACION");
		put("HORA_BOLSA_HORARIA_HORAS", "HORA_BOLSA_HORARIA");
		put("VACACIONES_ANUAL", "VACACIONES_ANUAL");
		put("VACACIONES_MENSUAL", "VACACIONES_MENSUAL");
		put("DIETA_ALOJAMIENTO_MENSUAL", "DIETA_ALOJAMIENTO_M");
		put("GASTOS_PECNORTA_DIARIO", "IMPORTE_PERNOCTA");
		put("GASTOS_MANUTENCION_DIARIO", "IMPORTE_MANUTENCION");
		put("GASTOS_LOCOMOCION_SIN_JUSTIFICANTE_DIARIO", "IMPORTE_KM");
		put("DIETA_COMPLETA_DIARIO", "DIETA_COMPLETA");
		put("DIETA_COMPLETA_DESPL_MAS_100KMS_DIARIO", "DIETA_COMPLETA_G_100");
		put("DIETA_COMPLETA_DESPL_MENOS_100KMS_DIARIO", "DIETA_COMPLETA_L_100");
		put("DIETA_PERNOCTA_DIARIO", "DIETA_PERNOCTA");
		put("DIETAS_DIARIO", "DIETA");
		put("DIETA_COMIDA_DIARIO", "DIETA_COMIDA");
		put("MEDIA_DIETA_DIARIO", "MEDIA_DIETA");
		put("MEDIA_DIETA_DESPL_MAS_100KMS_DIARIO", "MEDIA_DIETA_G_100");
		put("MEDIA_DIETA_DESPL_MENOS_100KMS_DIARIO", "MEDIA_DIETA_L_100");
		put("DIETA_DESAYUNO_DIARIO", "DIETA_DESAYUNO_D");
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
		put("ANTIGUEDAD_MENSUAL", "ANTIGUEDAD_MENSUAL");
		put("COMPENSACIONES_MARMOLERIAS_MENSUAL", "COMP_MARMOLERIAS");
		put("AYUDA_FAMILIA_NUMEROSA_MENSUAL", "AYUDA_FAMILIA_NUM");
		put("AYUDA_ESCOLAR_MENSUAL", "AYUDA_ESCOLAR_M");
		put("QUEBRANDO_DE_MONEDA_MENSUAL", "QUEBRANDO_MONEDA");
		put("COMPENSACIONES_MENSUAL", "COMPENSACIONES");
		put("PAGAS_EXTRA_MENSUAL", "PAGAS_EXTRA_MENSUAL");
		put("TURNICIDAD_MENSUAL", "TURNICIDAD_MENSUAL");
		put("QUINQUENIOS_MENSUAL", "QUINQUENIOS_MENSUAL");
		put("CUATRIENIOS_MENSUAL", "CUATRIENIOS_MENSUAL");
		put("TRIENIOS_MENSUAL", "TRIENIOS_MENSUAL");
		put("BIENIOS_MENSUAL", "BIENIOS_MENSUAL");
		put("P_C_I__MENSUAL", "PCI_MENUSAL");
		put("ESTUDIOS_MENSUAL", "ESTUDIOS_MENSUAL");
		put("DIETA_KILOMETRAJE_HORAS", "DIETA_KILOMETRAJE_H");
		put("GARANTIZADO_MENSUAL", "GARANTIZADO_MENSUAL");
		put("RETRIBUCION_EN_ESPECIE_MENSUAL", "RETRIBUCION_ESPECIE");
		put("COMPLEMENTO_PERSONAL_DE_ANTIGUEDAD_MENSUAL", "IMPORTE_ANTIGUEDAD");
		put("SERVICIO_EXTRA_CAMARERO_DIARIO", "S_E_CAMARERO_D");
		put("SERVICIO_EXTRA_LAVAPLATOS_DIARIO", "S_E_LAVAPLATOS_D");
		put("SERVICIO_EXTRA_AYUDANTE_DIARIO", "S_E_AYUDANTE_D");
		put("SERVICIO_EXTRA_COCINERO_DIARIO", "S_E_COCINERO_D");
		put("SERVICIO_EXTRA_COBRADOR_DIARIO", "S_E_COBRADOR_D");
		put("SERVICIO_EXTRA_AYDTE_COCINA_DIARIO", "S_E_AYDTE_COCINA_D");
		put("SERVICIO_EXTRA_LAVAPLATOS_FESTIVOS_DIARIO", "S_E_LAVAPLATOS_FEST_D");
		put("SERVICIO_EXTRA_CAMARERO_FESTIVOS_DIARIO", "S_E_CAMARERO_FEST_D");
		put("TURNICIDAD_DIARIO", "TURNICIDAD_DIARIO");
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
		put("SALARIO_MINIMO_GARANTIZADO_ANUAL", "S_MIN_GARANT_A");


	}};
	
	public static String getAgreement(DSLContext dslContext, String agreementCode, Integer domainId) {
		DOMAIN_ID = domainId;
		String log = "";
		Map<String, String> varNotInsertMap = new HashMap<String, String>();
		
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
			varNotInsertMap = insertAgreementDB(dslContext, agreement);
			
//			System.out.println("serviAgreementsMap.put(\"" + agreement.getSSCode() + " - " + agreement.getAgreementDescription().toUpperCase() + "\",  \"" + agreement.getServiAgreementCode() + "\");"  );
			
			if(!varNotInsertMap.isEmpty()) {
				return getAgreementLog(dslContext, domainId, agreement, varNotInsertMap);
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return log;
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
	
	private static Map<String, String> insertAgreementDB(DSLContext dslContext, Agreement agreement) {
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
						!AonStringUtils.containsIgnoreCase(lvlData.getName(), "PAGA_EXTRA"))
							
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
		
		return mapVarNotInsert;
		
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
		name = name.replaceAll("\\.", "_");
		
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
			
			String log = "";
			log += getAgreement(dslContext, agreementCode, Integer.parseInt(domainIdStr));
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
