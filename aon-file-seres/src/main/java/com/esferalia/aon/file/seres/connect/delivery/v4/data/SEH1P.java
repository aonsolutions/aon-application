package com.esferalia.aon.file.seres.connect.delivery.v4.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1P entity.
 * <br>
 * IMPORTANT: Any changes made to the code will be lost, so...
 * 
 * DO NOT CHANGE THIS!!!!!!!!!!!!!!!!
 * 
 * <table border="1" cellpadding="1" cellspacing="0">
 * 	<tr bgcolor="#CCCCFF"> 
 * 		 <th>Tipo de registro</th> <th>Descripcion</th> <th>Tipo</th> <th>Repeticiones</th>
 * 	</tr>
 * 	<tr>
 * 		 <td>SEH1P</td> <td>Secuencia de embalajes</td> <td>Obligatorio</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SEH1P {

	private String numeroDeJerarquiaDeEmbalaje;
	private String numeroDeJerarquiaPadreDeEmbalaje;
	private Integer numeroDePaquetes;
	private String informacionSobreElEmbalaje_Codificado;
	private String terminosYCondicionesDelEmbalaje_Codificado;
	private String tipoDeEmbalaje_Codificado;
	private String tipoDeEmbalaje_TextoLibre;
	private String responsabilidadPagoTransporteDeEmbalaje;
	private Double pesoNeto1_AAC_;
	private Double pesoNeto2;
	private String codigoSignificacionDeLaMedidaPesoNeto;
	private String unidadDeMedidaParaElPesoNeto;
	private Double pesoBruto1_AAD_;
	private Double pesoBruto2;
	private String codigoSignificacionDeLaMedidaPesoBruto;
	private String unidadDeMedidaParaElPesoBruto;
	private Double dimensionDeAltura1_HT_;
	private Double dimensionDeAltura2;
	private String codigoSignificacionDeLaMedidaAltura;
	private String unidadDeMedidaParaLaAltura;
	private Double dimensionDeAncho1_WD_;
	private Double dimensionDeAncho2;
	private String codigoSignificacionDeLaMedidaAncho;
	private String unidadDeMedidaParaElAncho;
	private Double dimensionDeLongitud1_LN_;
	private Double dimensionDeLongitud2;
	private String codigoSignificacionDeLaMedidaLongitud;
	private String unidadDeMedidaParaLaLongitud;
	private Double dimensionDeTemperatura1_TC_;
	private Double dimensionDeTemperatura2;
	private String codigoSignificacionDeLaMedidaTemperatura;
	private String unidadDeMedidaParaLaTemperatura;
	private Double cantidadPorEmbalaje;
	private String instruccionesDeManejo_Codificado;
	private String instruccionesDeManejo_TextoLibre;
	private String marcaDeEnvio1;
	private String marcaDeEnvio2;
	private String marcaDeEnvio3;
	private String marcaDeEnvio4;
	private String numeroSerial1ONumeroDeIdentificacionInferior;
	private String numeroSerial1ONumeroDeIdentificacionSuperior;
	private String numeroSerial2oNumeroDeIdentificacionInferior;
	private String numeroSerial2ONumeroDeIdentificacionSuperior;
	private String numeroSerial3ONumeroDeIdentificacionInferior;
	private String numeroSerial3ONumeroDeIdentificacionSuperior;


	private static Pattern PATTERN_SEH1P_numeroDeJerarquiaDeEmbalaje = Pattern.compile("^.{6}(.{12}).*");
	private static Pattern PATTERN_SEH1P_numeroDeJerarquiaPadreDeEmbalaje = Pattern.compile("^.{18}(.{12}).*");
	private static Pattern PATTERN_SEH1P_numeroDePaquetes = Pattern.compile("^.{30}(.{8}).*");
	private static Pattern PATTERN_SEH1P_informacionSobreElEmbalaje_Codificado = Pattern.compile("^.{38}(.{6}).*");
	private static Pattern PATTERN_SEH1P_terminosYCondicionesDelEmbalaje_Codificado = Pattern.compile("^.{44}(.{6}).*");
	private static Pattern PATTERN_SEH1P_tipoDeEmbalaje_Codificado = Pattern.compile("^.{50}(.{6}).*");
	private static Pattern PATTERN_SEH1P_tipoDeEmbalaje_TextoLibre = Pattern.compile("^.{56}(.{35}).*");
	private static Pattern PATTERN_SEH1P_responsabilidadPagoTransporteDeEmbalaje = Pattern.compile("^.{91}(.{6}).*");
	private static Pattern PATTERN_SEH1P_pesoNeto1_AAC_ = Pattern.compile("^.{97}(.{18}).*");
	private static Pattern PATTERN_SEH1P_pesoNeto2 = Pattern.compile("^.{115}(.{18}).*");
	private static Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaPesoNeto = Pattern.compile("^.{133}(.{6}).*");
	private static Pattern PATTERN_SEH1P_unidadDeMedidaParaElPesoNeto = Pattern.compile("^.{139}(.{6}).*");
	private static Pattern PATTERN_SEH1P_pesoBruto1_AAD_ = Pattern.compile("^.{145}(.{18}).*");
	private static Pattern PATTERN_SEH1P_pesoBruto2 = Pattern.compile("^.{163}(.{18}).*");
	private static Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaPesoBruto = Pattern.compile("^.{181}(.{6}).*");
	private static Pattern PATTERN_SEH1P_unidadDeMedidaParaElPesoBruto = Pattern.compile("^.{187}(.{6}).*");
	private static Pattern PATTERN_SEH1P_dimensionDeAltura1_HT_ = Pattern.compile("^.{193}(.{18}).*");
	private static Pattern PATTERN_SEH1P_dimensionDeAltura2 = Pattern.compile("^.{211}(.{18}).*");
	private static Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaAltura = Pattern.compile("^.{229}(.{6}).*");
	private static Pattern PATTERN_SEH1P_unidadDeMedidaParaLaAltura = Pattern.compile("^.{235}(.{6}).*");
	private static Pattern PATTERN_SEH1P_dimensionDeAncho1_WD_ = Pattern.compile("^.{241}(.{18}).*");
	private static Pattern PATTERN_SEH1P_dimensionDeAncho2 = Pattern.compile("^.{259}(.{18}).*");
	private static Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaAncho = Pattern.compile("^.{277}(.{6}).*");
	private static Pattern PATTERN_SEH1P_unidadDeMedidaParaElAncho = Pattern.compile("^.{283}(.{6}).*");
	private static Pattern PATTERN_SEH1P_dimensionDeLongitud1_LN_ = Pattern.compile("^.{289}(.{18}).*");
	private static Pattern PATTERN_SEH1P_dimensionDeLongitud2 = Pattern.compile("^.{307}(.{18}).*");
	private static Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaLongitud = Pattern.compile("^.{325}(.{6}).*");
	private static Pattern PATTERN_SEH1P_unidadDeMedidaParaLaLongitud = Pattern.compile("^.{331}(.{6}).*");
	private static Pattern PATTERN_SEH1P_dimensionDeTemperatura1_TC_ = Pattern.compile("^.{337}(.{18}).*");
	private static Pattern PATTERN_SEH1P_dimensionDeTemperatura2 = Pattern.compile("^.{355}(.{18}).*");
	private static Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaTemperatura = Pattern.compile("^.{373}(.{6}).*");
	private static Pattern PATTERN_SEH1P_unidadDeMedidaParaLaTemperatura = Pattern.compile("^.{379}(.{6}).*");
	private static Pattern PATTERN_SEH1P_cantidadPorEmbalaje = Pattern.compile("^.{385}(.{16}).*");
	private static Pattern PATTERN_SEH1P_instruccionesDeManejo_Codificado = Pattern.compile("^.{401}(.{6}).*");
	private static Pattern PATTERN_SEH1P_instruccionesDeManejo_TextoLibre = Pattern.compile("^.{407}(.{70}).*");
	private static Pattern PATTERN_SEH1P_marcaDeEnvio1 = Pattern.compile("^.{477}(.{35}).*");
	private static Pattern PATTERN_SEH1P_marcaDeEnvio2 = Pattern.compile("^.{512}(.{35}).*");
	private static Pattern PATTERN_SEH1P_marcaDeEnvio3 = Pattern.compile("^.{547}(.{35}).*");
	private static Pattern PATTERN_SEH1P_marcaDeEnvio4 = Pattern.compile("^.{582}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numeroSerial1ONumeroDeIdentificacionInferior = Pattern.compile("^.{617}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numeroSerial1ONumeroDeIdentificacionSuperior = Pattern.compile("^.{652}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numeroSerial2oNumeroDeIdentificacionInferior = Pattern.compile("^.{687}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numeroSerial2ONumeroDeIdentificacionSuperior = Pattern.compile("^.{722}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numeroSerial3ONumeroDeIdentificacionInferior = Pattern.compile("^.{757}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numeroSerial3ONumeroDeIdentificacionSuperior = Pattern.compile("^.{792}(.{35}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SEH1P_numeroDeJerarquiaDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeJerarquiaDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroDeJerarquiaPadreDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeJerarquiaPadreDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroDePaquetes.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePaquetes(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_informacionSobreElEmbalaje_Codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setInformacionSobreElEmbalaje_Codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_terminosYCondicionesDelEmbalaje_Codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTerminosYCondicionesDelEmbalaje_Codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_tipoDeEmbalaje_Codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeEmbalaje_Codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_tipoDeEmbalaje_TextoLibre.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeEmbalaje_TextoLibre(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_responsabilidadPagoTransporteDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setResponsabilidadPagoTransporteDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_pesoNeto1_AAC_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoNeto1_AAC_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_pesoNeto2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoNeto2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaPesoNeto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoSignificacionDeLaMedidaPesoNeto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_unidadDeMedidaParaElPesoNeto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaParaElPesoNeto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_pesoBruto1_AAD_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoBruto1_AAD_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_pesoBruto2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoBruto2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaPesoBruto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoSignificacionDeLaMedidaPesoBruto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_unidadDeMedidaParaElPesoBruto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaParaElPesoBruto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_dimensionDeAltura1_HT_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDimensionDeAltura1_HT_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_dimensionDeAltura2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDimensionDeAltura2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaAltura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoSignificacionDeLaMedidaAltura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_unidadDeMedidaParaLaAltura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaParaLaAltura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_dimensionDeAncho1_WD_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDimensionDeAncho1_WD_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_dimensionDeAncho2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDimensionDeAncho2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaAncho.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoSignificacionDeLaMedidaAncho(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_unidadDeMedidaParaElAncho.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaParaElAncho(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_dimensionDeLongitud1_LN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDimensionDeLongitud1_LN_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_dimensionDeLongitud2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDimensionDeLongitud2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaLongitud.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoSignificacionDeLaMedidaLongitud(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_unidadDeMedidaParaLaLongitud.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaParaLaLongitud(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_dimensionDeTemperatura1_TC_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDimensionDeTemperatura1_TC_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_dimensionDeTemperatura2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDimensionDeTemperatura2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaTemperatura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoSignificacionDeLaMedidaTemperatura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_unidadDeMedidaParaLaTemperatura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaParaLaTemperatura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_cantidadPorEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadPorEmbalaje(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_instruccionesDeManejo_Codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setInstruccionesDeManejo_Codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_instruccionesDeManejo_TextoLibre.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setInstruccionesDeManejo_TextoLibre(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_marcaDeEnvio1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaDeEnvio1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_marcaDeEnvio2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaDeEnvio2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_marcaDeEnvio3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaDeEnvio3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_marcaDeEnvio4.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaDeEnvio4(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroSerial1ONumeroDeIdentificacionInferior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial1ONumeroDeIdentificacionInferior(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroSerial1ONumeroDeIdentificacionSuperior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial1ONumeroDeIdentificacionSuperior(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroSerial2oNumeroDeIdentificacionInferior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial2oNumeroDeIdentificacionInferior(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroSerial2ONumeroDeIdentificacionSuperior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial2ONumeroDeIdentificacionSuperior(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroSerial3ONumeroDeIdentificacionInferior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial3ONumeroDeIdentificacionInferior(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroSerial3ONumeroDeIdentificacionSuperior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial3ONumeroDeIdentificacionSuperior(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Número de jerarquía de embalaje: Un número único asignado por el emisor para identificar el nivel de embalaje. Se recomienda utilizar un contador secuencial.
	 */ 
	public String getNumeroDeJerarquiaDeEmbalaje() {
		return numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * 2 - Número de jerarquía de embalaje: Un número único asignado por el emisor para identificar el nivel de embalaje. Se recomienda utilizar un contador secuencial.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Número de jerarquía de embalaje</td> <td>C</td> <td>12</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeJerarquiaDeEmbalaje(String numeroDeJerarquiaDeEmbalaje) {
		this.numeroDeJerarquiaDeEmbalaje = numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * 3 - Número de jerarquía padre de embalaje: Identifica el nivel de embalaje superior al que pertenece.
	 */ 
	public String getNumeroDeJerarquiaPadreDeEmbalaje() {
		return numeroDeJerarquiaPadreDeEmbalaje;
	}

	/** 
	 * 3 - Número de jerarquía padre de embalaje: Identifica el nivel de embalaje superior al que pertenece.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Número de jerarquía padre de embalaje</td> <td>C</td> <td>12</td> <td>19</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeJerarquiaPadreDeEmbalaje(String numeroDeJerarquiaPadreDeEmbalaje) {
		this.numeroDeJerarquiaPadreDeEmbalaje = numeroDeJerarquiaPadreDeEmbalaje;
	}

	/** 
	 * 51 - Embalaje con código de barras ITF-14 o ITF-6
	 */ 
	public Integer getNumeroDePaquetes() {
		return numeroDePaquetes;
	}

	/** 
	 * 51 - Embalaje con código de barras ITF-14 o ITF-6
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Número de paquetes</td> <td>N</td> <td>8</td> <td>31</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePaquetes(Integer numeroDePaquetes) {
		this.numeroDePaquetes = numeroDePaquetes;
	}

	/** 
	 * 5 - Información sobre el embalaje, codificada: Este campo se corresponde con el elemento 7233. Lo posibles valores son:
	 */ 
	public String getInformacionSobreElEmbalaje_Codificado() {
		return informacionSobreElEmbalaje_Codificado;
	}

	/** 
	 * 5 - Información sobre el embalaje, codificada: Este campo se corresponde con el elemento 7233. Lo posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Información sobre el embalaje, codificado</td> <td>C</td> <td>6</td> <td>39</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setInformacionSobreElEmbalaje_Codificado(String informacionSobreElEmbalaje_Codificado) {
		this.informacionSobreElEmbalaje_Codificado = informacionSobreElEmbalaje_Codificado;
	}

	/** 
	 * 6 - Términos y condiciones del embalaje, codificado: Este campo se corresponde con el elemento 7073. Los posibles valores son:
	 */ 
	public String getTerminosYCondicionesDelEmbalaje_Codificado() {
		return terminosYCondicionesDelEmbalaje_Codificado;
	}

	/** 
	 * 6 - Términos y condiciones del embalaje, codificado: Este campo se corresponde con el elemento 7073. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Términos y Condiciones del embalaje, codificado</td> <td>C</td> <td>6</td> <td>45</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTerminosYCondicionesDelEmbalaje_Codificado(String terminosYCondicionesDelEmbalaje_Codificado) {
		this.terminosYCondicionesDelEmbalaje_Codificado = terminosYCondicionesDelEmbalaje_Codificado;
	}

	/** 
	 * 7 - Tipo de embalaje, codificado: Este campo se corresponde con el elemento 7065. Los posibles valores son:
	 */ 
	public String getTipoDeEmbalaje_Codificado() {
		return tipoDeEmbalaje_Codificado;
	}

	/** 
	 * 7 - Tipo de embalaje, codificado: Este campo se corresponde con el elemento 7065. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Tipo de embalaje, codificado</td> <td>C</td> <td>6</td> <td>51</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeEmbalaje_Codificado(String tipoDeEmbalaje_Codificado) {
		this.tipoDeEmbalaje_Codificado = tipoDeEmbalaje_Codificado;
	}

	/** 
	 * 08 - Pallet no retornable
	 */ 
	public String getTipoDeEmbalaje_TextoLibre() {
		return tipoDeEmbalaje_TextoLibre;
	}

	/** 
	 * 08 - Pallet no retornable
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Tipo de embalaje, texto libre</td> <td>C</td> <td>35</td> <td>57</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeEmbalaje_TextoLibre(String tipoDeEmbalaje_TextoLibre) {
		this.tipoDeEmbalaje_TextoLibre = tipoDeEmbalaje_TextoLibre;
	}

	/** 
	 * 09 - Pallet retornable
	 */ 
	public String getResponsabilidadPagoTransporteDeEmbalaje() {
		return responsabilidadPagoTransporteDeEmbalaje;
	}

	/** 
	 * 09 - Pallet retornable
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Responsabilidad Pago Transporte de Embalaje</td> <td>C</td> <td>6</td> <td>92</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setResponsabilidadPagoTransporteDeEmbalaje(String responsabilidadPagoTransporteDeEmbalaje) {
		this.responsabilidadPagoTransporteDeEmbalaje = responsabilidadPagoTransporteDeEmbalaje;
	}

	/** 
	 * 
	 */ 
	public Double getPesoNeto1_AAC_() {
		return pesoNeto1_AAC_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Peso neto1 (AAC)</td> <td>N(14,3)</td> <td>18</td> <td>98</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoNeto1_AAC_(Double pesoNeto1_AAC_) {
		this.pesoNeto1_AAC_ = pesoNeto1_AAC_;
	}

	/** 
	 * 
	 */ 
	public Double getPesoNeto2() {
		return pesoNeto2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Peso neto 2</td> <td>N(14,3)</td> <td>18</td> <td>116</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoNeto2(Double pesoNeto2) {
		this.pesoNeto2 = pesoNeto2;
	}

	/** 
	 * 12,16,20,24,28,32 - Código significación de la medida: Código que especifica el valor de las cantidades de medida. El campo se corresponde con el elemento 6321. Los posibles valores son:
	 */ 
	public String getCodigoSignificacionDeLaMedidaPesoNeto() {
		return codigoSignificacionDeLaMedidaPesoNeto;
	}

	/** 
	 * 12,16,20,24,28,32 - Código significación de la medida: Código que especifica el valor de las cantidades de medida. El campo se corresponde con el elemento 6321. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Código significación de la medida peso neto</td> <td>C</td> <td>6</td> <td>134</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoSignificacionDeLaMedidaPesoNeto(String codigoSignificacionDeLaMedidaPesoNeto) {
		this.codigoSignificacionDeLaMedidaPesoNeto = codigoSignificacionDeLaMedidaPesoNeto;
	}

	/** 
	 * 50 - Embalaje con código de barras EAN-13 o EAN-8
	 */ 
	public String getUnidadDeMedidaParaElPesoNeto() {
		return unidadDeMedidaParaElPesoNeto;
	}

	/** 
	 * 50 - Embalaje con código de barras EAN-13 o EAN-8
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Unidad de medida para el peso neto</td> <td>C</td> <td>6</td> <td>140</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaParaElPesoNeto(String unidadDeMedidaParaElPesoNeto) {
		this.unidadDeMedidaParaElPesoNeto = unidadDeMedidaParaElPesoNeto;
	}

	/** 
	 * 51 - Embalaje con código de barras ITF-14 o ITF-6
	 */ 
	public Double getPesoBruto1_AAD_() {
		return pesoBruto1_AAD_;
	}

	/** 
	 * 51 - Embalaje con código de barras ITF-14 o ITF-6
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Peso bruto 1 (AAD)</td> <td>N(14,3)</td> <td>18</td> <td>146</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoBruto1_AAD_(Double pesoBruto1_AAD_) {
		this.pesoBruto1_AAD_ = pesoBruto1_AAD_;
	}

	/** 
	 * 
	 */ 
	public Double getPesoBruto2() {
		return pesoBruto2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Peso bruto 2</td> <td>N(14,3)</td> <td>18</td> <td>164</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoBruto2(Double pesoBruto2) {
		this.pesoBruto2 = pesoBruto2;
	}

	/** 
	 * 12,16,20,24,28,32 - Código significación de la medida: Código que especifica el valor de las cantidades de medida. El campo se corresponde con el elemento 6321. Los posibles valores son:
	 */ 
	public String getCodigoSignificacionDeLaMedidaPesoBruto() {
		return codigoSignificacionDeLaMedidaPesoBruto;
	}

	/** 
	 * 12,16,20,24,28,32 - Código significación de la medida: Código que especifica el valor de las cantidades de medida. El campo se corresponde con el elemento 6321. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Código significación de la medida peso bruto</td> <td>C</td> <td>6</td> <td>182</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoSignificacionDeLaMedidaPesoBruto(String codigoSignificacionDeLaMedidaPesoBruto) {
		this.codigoSignificacionDeLaMedidaPesoBruto = codigoSignificacionDeLaMedidaPesoBruto;
	}

	/** 
	 * 
	 */ 
	public String getUnidadDeMedidaParaElPesoBruto() {
		return unidadDeMedidaParaElPesoBruto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Unidad de medida para el peso bruto</td> <td>C</td> <td>6</td> <td>188</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaParaElPesoBruto(String unidadDeMedidaParaElPesoBruto) {
		this.unidadDeMedidaParaElPesoBruto = unidadDeMedidaParaElPesoBruto;
	}

	/** 
	 * 
	 */ 
	public Double getDimensionDeAltura1_HT_() {
		return dimensionDeAltura1_HT_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Dimensión de altura 1 (HT)</td> <td>N(14,3)</td> <td>18</td> <td>194</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDimensionDeAltura1_HT_(Double dimensionDeAltura1_HT_) {
		this.dimensionDeAltura1_HT_ = dimensionDeAltura1_HT_;
	}

	/** 
	 * 
	 */ 
	public Double getDimensionDeAltura2() {
		return dimensionDeAltura2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Dimensión de altura 2</td> <td>N(14,3)</td> <td>18</td> <td>212</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDimensionDeAltura2(Double dimensionDeAltura2) {
		this.dimensionDeAltura2 = dimensionDeAltura2;
	}

	/** 
	 * 201 - Pallet ISO 1 - 1/1 EURO Pallet
	 */ 
	public String getCodigoSignificacionDeLaMedidaAltura() {
		return codigoSignificacionDeLaMedidaAltura;
	}

	/** 
	 * 201 - Pallet ISO 1 - 1/1 EURO Pallet
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Código significación de la medida altura</td> <td>C</td> <td>6</td> <td>230</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoSignificacionDeLaMedidaAltura(String codigoSignificacionDeLaMedidaAltura) {
		this.codigoSignificacionDeLaMedidaAltura = codigoSignificacionDeLaMedidaAltura;
	}

	/** 
	 * 
	 */ 
	public String getUnidadDeMedidaParaLaAltura() {
		return unidadDeMedidaParaLaAltura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>Unidad de medida para la altura</td> <td>C</td> <td>6</td> <td>236</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaParaLaAltura(String unidadDeMedidaParaLaAltura) {
		this.unidadDeMedidaParaLaAltura = unidadDeMedidaParaLaAltura;
	}

	/** 
	 * 
	 */ 
	public Double getDimensionDeAncho1_WD_() {
		return dimensionDeAncho1_WD_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>22</td> <td>Dimensión de ancho 1 (WD)</td> <td>N(14,3)</td> <td>18</td> <td>242</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDimensionDeAncho1_WD_(Double dimensionDeAncho1_WD_) {
		this.dimensionDeAncho1_WD_ = dimensionDeAncho1_WD_;
	}

	/** 
	 * 
	 */ 
	public Double getDimensionDeAncho2() {
		return dimensionDeAncho2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>Dimensión de ancho 2</td> <td>N(14,3)</td> <td>18</td> <td>260</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDimensionDeAncho2(Double dimensionDeAncho2) {
		this.dimensionDeAncho2 = dimensionDeAncho2;
	}

	/** 
	 * 12,16,20,24,28,32 - Código significación de la medida: Código que especifica el valor de las cantidades de medida. El campo se corresponde con el elemento 6321. Los posibles valores son:
	 */ 
	public String getCodigoSignificacionDeLaMedidaAncho() {
		return codigoSignificacionDeLaMedidaAncho;
	}

	/** 
	 * 12,16,20,24,28,32 - Código significación de la medida: Código que especifica el valor de las cantidades de medida. El campo se corresponde con el elemento 6321. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Código significación de la medida ancho</td> <td>C</td> <td>6</td> <td>278</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoSignificacionDeLaMedidaAncho(String codigoSignificacionDeLaMedidaAncho) {
		this.codigoSignificacionDeLaMedidaAncho = codigoSignificacionDeLaMedidaAncho;
	}

	/** 
	 * 
	 */ 
	public String getUnidadDeMedidaParaElAncho() {
		return unidadDeMedidaParaElAncho;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>25</td> <td>Unidad de medida para el ancho</td> <td>C</td> <td>6</td> <td>284</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaParaElAncho(String unidadDeMedidaParaElAncho) {
		this.unidadDeMedidaParaElAncho = unidadDeMedidaParaElAncho;
	}

	/** 
	 * 
	 */ 
	public Double getDimensionDeLongitud1_LN_() {
		return dimensionDeLongitud1_LN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>26</td> <td>Dimensión de longitud 1 (LN)</td> <td>N(14,3)</td> <td>18</td> <td>290</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDimensionDeLongitud1_LN_(Double dimensionDeLongitud1_LN_) {
		this.dimensionDeLongitud1_LN_ = dimensionDeLongitud1_LN_;
	}

	/** 
	 * 
	 */ 
	public Double getDimensionDeLongitud2() {
		return dimensionDeLongitud2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>27</td> <td>Dimensión de longitud 2</td> <td>N(14,3)</td> <td>18</td> <td>308</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDimensionDeLongitud2(Double dimensionDeLongitud2) {
		this.dimensionDeLongitud2 = dimensionDeLongitud2;
	}

	/** 
	 * 12,16,20,24,28,32 - Código significación de la medida: Código que especifica el valor de las cantidades de medida. El campo se corresponde con el elemento 6321. Los posibles valores son:
	 */ 
	public String getCodigoSignificacionDeLaMedidaLongitud() {
		return codigoSignificacionDeLaMedidaLongitud;
	}

	/** 
	 * 12,16,20,24,28,32 - Código significación de la medida: Código que especifica el valor de las cantidades de medida. El campo se corresponde con el elemento 6321. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>28</td> <td>Código significación de la medida longitud</td> <td>C</td> <td>6</td> <td>326</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoSignificacionDeLaMedidaLongitud(String codigoSignificacionDeLaMedidaLongitud) {
		this.codigoSignificacionDeLaMedidaLongitud = codigoSignificacionDeLaMedidaLongitud;
	}

	/** 
	 * 
	 */ 
	public String getUnidadDeMedidaParaLaLongitud() {
		return unidadDeMedidaParaLaLongitud;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>29</td> <td>Unidad de medida para la longitud</td> <td>C</td> <td>6</td> <td>332</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaParaLaLongitud(String unidadDeMedidaParaLaLongitud) {
		this.unidadDeMedidaParaLaLongitud = unidadDeMedidaParaLaLongitud;
	}

	/** 
	 * 
	 */ 
	public Double getDimensionDeTemperatura1_TC_() {
		return dimensionDeTemperatura1_TC_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>30</td> <td>Dimensión de temperatura 1 (TC)</td> <td>N(14,3)</td> <td>18</td> <td>338</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDimensionDeTemperatura1_TC_(Double dimensionDeTemperatura1_TC_) {
		this.dimensionDeTemperatura1_TC_ = dimensionDeTemperatura1_TC_;
	}

	/** 
	 * 
	 */ 
	public Double getDimensionDeTemperatura2() {
		return dimensionDeTemperatura2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>31</td> <td>Dimensión de temperatura 2</td> <td>N(14,3)</td> <td>18</td> <td>356</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDimensionDeTemperatura2(Double dimensionDeTemperatura2) {
		this.dimensionDeTemperatura2 = dimensionDeTemperatura2;
	}

	/** 
	 * 12,16,20,24,28,32 - Código significación de la medida: Código que especifica el valor de las cantidades de medida. El campo se corresponde con el elemento 6321. Los posibles valores son:
	 */ 
	public String getCodigoSignificacionDeLaMedidaTemperatura() {
		return codigoSignificacionDeLaMedidaTemperatura;
	}

	/** 
	 * 12,16,20,24,28,32 - Código significación de la medida: Código que especifica el valor de las cantidades de medida. El campo se corresponde con el elemento 6321. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>32</td> <td>Código significación de la medida temperatura</td> <td>C</td> <td>6</td> <td>374</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoSignificacionDeLaMedidaTemperatura(String codigoSignificacionDeLaMedidaTemperatura) {
		this.codigoSignificacionDeLaMedidaTemperatura = codigoSignificacionDeLaMedidaTemperatura;
	}

	/** 
	 * 
	 */ 
	public String getUnidadDeMedidaParaLaTemperatura() {
		return unidadDeMedidaParaLaTemperatura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>33</td> <td>Unidad de medida para la temperatura</td> <td>C</td> <td>6</td> <td>380</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaParaLaTemperatura(String unidadDeMedidaParaLaTemperatura) {
		this.unidadDeMedidaParaLaTemperatura = unidadDeMedidaParaLaTemperatura;
	}

	/** 
	 * 34 - Cantidad por embalaje: La cantidad contenida en el embalaje actual.
	 */ 
	public Double getCantidadPorEmbalaje() {
		return cantidadPorEmbalaje;
	}

	/** 
	 * 34 - Cantidad por embalaje: La cantidad contenida en el embalaje actual.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>34</td> <td>Cantidad por embalaje</td> <td>N(12,3)</td> <td>16</td> <td>386</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadPorEmbalaje(Double cantidadPorEmbalaje) {
		this.cantidadPorEmbalaje = cantidadPorEmbalaje;
	}

	/** 
	 * 35 - Instrucciones de manejo, codificado: Este campo se corresponde con el elemento 4079. Los posibles valores son:
	 */ 
	public String getInstruccionesDeManejo_Codificado() {
		return instruccionesDeManejo_Codificado;
	}

	/** 
	 * 35 - Instrucciones de manejo, codificado: Este campo se corresponde con el elemento 4079. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>35</td> <td>Instrucciones de manejo, codificado</td> <td>C</td> <td>6</td> <td>402</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setInstruccionesDeManejo_Codificado(String instruccionesDeManejo_Codificado) {
		this.instruccionesDeManejo_Codificado = instruccionesDeManejo_Codificado;
	}

	/** 
	 * 
	 */ 
	public String getInstruccionesDeManejo_TextoLibre() {
		return instruccionesDeManejo_TextoLibre;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>36</td> <td>Instrucciones de manejo, texto libre</td> <td>C</td> <td>70</td> <td>408</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setInstruccionesDeManejo_TextoLibre(String instruccionesDeManejo_TextoLibre) {
		this.instruccionesDeManejo_TextoLibre = instruccionesDeManejo_TextoLibre;
	}

	/** 
	 * 
	 */ 
	public String getMarcaDeEnvio1() {
		return marcaDeEnvio1;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>37</td> <td>Marca de envió 1</td> <td>C</td> <td>35</td> <td>478</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaDeEnvio1(String marcaDeEnvio1) {
		this.marcaDeEnvio1 = marcaDeEnvio1;
	}

	/** 
	 * 
	 */ 
	public String getMarcaDeEnvio2() {
		return marcaDeEnvio2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>38</td> <td>Marca de envió 2</td> <td>C</td> <td>35</td> <td>513</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaDeEnvio2(String marcaDeEnvio2) {
		this.marcaDeEnvio2 = marcaDeEnvio2;
	}

	/** 
	 * 
	 */ 
	public String getMarcaDeEnvio3() {
		return marcaDeEnvio3;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>39</td> <td>Marca de envió 3</td> <td>C</td> <td>35</td> <td>548</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaDeEnvio3(String marcaDeEnvio3) {
		this.marcaDeEnvio3 = marcaDeEnvio3;
	}

	/** 
	 * 
	 */ 
	public String getMarcaDeEnvio4() {
		return marcaDeEnvio4;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>40</td> <td>Marca de envió 4</td> <td>C</td> <td>35</td> <td>583</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaDeEnvio4(String marcaDeEnvio4) {
		this.marcaDeEnvio4 = marcaDeEnvio4;
	}

	/** 
	 * 
	 */ 
	public String getNumeroSerial1ONumeroDeIdentificacionInferior() {
		return numeroSerial1ONumeroDeIdentificacionInferior;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>41</td> <td>Número Serial 1 o número de identificación inferior</td> <td>C</td> <td>35</td> <td>618</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial1ONumeroDeIdentificacionInferior(String numeroSerial1ONumeroDeIdentificacionInferior) {
		this.numeroSerial1ONumeroDeIdentificacionInferior = numeroSerial1ONumeroDeIdentificacionInferior;
	}

	/** 
	 * 
	 */ 
	public String getNumeroSerial1ONumeroDeIdentificacionSuperior() {
		return numeroSerial1ONumeroDeIdentificacionSuperior;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>42</td> <td>Número Serial 1 o número de identificación superior</td> <td>C</td> <td>35</td> <td>653</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial1ONumeroDeIdentificacionSuperior(String numeroSerial1ONumeroDeIdentificacionSuperior) {
		this.numeroSerial1ONumeroDeIdentificacionSuperior = numeroSerial1ONumeroDeIdentificacionSuperior;
	}

	/** 
	 * 
	 */ 
	public String getNumeroSerial2oNumeroDeIdentificacionInferior() {
		return numeroSerial2oNumeroDeIdentificacionInferior;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>43</td> <td>Número Serial 2o número de identificación inferior</td> <td>C</td> <td>35</td> <td>688</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial2oNumeroDeIdentificacionInferior(String numeroSerial2oNumeroDeIdentificacionInferior) {
		this.numeroSerial2oNumeroDeIdentificacionInferior = numeroSerial2oNumeroDeIdentificacionInferior;
	}

	/** 
	 * 
	 */ 
	public String getNumeroSerial2ONumeroDeIdentificacionSuperior() {
		return numeroSerial2ONumeroDeIdentificacionSuperior;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>44</td> <td>Número Serial 2 o número de identificación superior</td> <td>C</td> <td>35</td> <td>723</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial2ONumeroDeIdentificacionSuperior(String numeroSerial2ONumeroDeIdentificacionSuperior) {
		this.numeroSerial2ONumeroDeIdentificacionSuperior = numeroSerial2ONumeroDeIdentificacionSuperior;
	}

	/** 
	 * 
	 */ 
	public String getNumeroSerial3ONumeroDeIdentificacionInferior() {
		return numeroSerial3ONumeroDeIdentificacionInferior;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>45</td> <td>Número Serial 3 o número de identificación inferior</td> <td>C</td> <td>35</td> <td>758</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial3ONumeroDeIdentificacionInferior(String numeroSerial3ONumeroDeIdentificacionInferior) {
		this.numeroSerial3ONumeroDeIdentificacionInferior = numeroSerial3ONumeroDeIdentificacionInferior;
	}

	/** 
	 * 
	 */ 
	public String getNumeroSerial3ONumeroDeIdentificacionSuperior() {
		return numeroSerial3ONumeroDeIdentificacionSuperior;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>46</td> <td>Número Serial 3 o número de identificación superior</td> <td>C</td> <td>35</td> <td>793</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial3ONumeroDeIdentificacionSuperior(String numeroSerial3ONumeroDeIdentificacionSuperior) {
		this.numeroSerial3ONumeroDeIdentificacionSuperior = numeroSerial3ONumeroDeIdentificacionSuperior;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}