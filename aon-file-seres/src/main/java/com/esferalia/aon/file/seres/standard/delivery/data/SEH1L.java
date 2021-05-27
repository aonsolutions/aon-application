package com.esferalia.aon.file.seres.standard.delivery.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1L entity.
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
 * 		 <td>SEH1L</td> <td>Línea de artículos</td> <td>Obligatorio</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SEH1L {

	private Integer numeroDeLineaDelArticulo;
	private String codigoEANDelArticulo;
	private String descripcionDelArticulo;
	private String tipoDeIdentificacionDelArticulo_CU_DU_;
	private String numeroDeArticuloDelProveedor_SA_;
	private String numeroVariablePromocional_PV_;
	private String codigoDUN_14_ADU_;
	private String codigoACU_ACU_;
	private String numeroDeLote_NB_;
	private String numeroDeArticuloDelComprador_IN_;
	private Double cantidadEnviada_12_;
	private String unidadDeMedidaCantidadEnviada;
	private Double unidadesDeConsumoEnUnidadDeExpedicion_59_;
	private String fechaDeCaducidad_36__102_203_;
	private String calificadorReferencia1;
	private String numeroReferencia1;
	private String fecha_horaReferencia1_102_203_;
	private String calificadorReferencia2;
	private String numeroReferencia2;
	private String fecha_horaReferencia2_102_203_;
	private String calificadorReferencia3;
	private String numeroReferencia3;
	private String fecha_horaReferencia3_102_203_;
	private String unidadesEnAgrupacionSuperior_45E_;
	private String codigoEANAdicional;
	private String cantidadSinCargo_192_;
	private String calificadorCantidadAdicional;
	private Double cantidadAdicional;
	private String unidadDeMedidaCantidadAdicional;
	private String numeroDeSerieDelArticulo_SN_;
	private String numeroArticuloFabricante_MF_;
	private Integer numeroDeLineaReferencia1;
	private Integer numeroDeLineaReferencia2;
	private Integer numeroDeLineaReferencia3;
	private Double diferenciaEnCantidadPedida_21_;
	private String codigoDiscrepancia;
	private Double pesoTotalNetoDeLaLinea_AAI_AAF_;
	private Double pesoTotalBrutoDeLaLinea_AAI_AAB_;
	private String unidadDeMedidaPeso;
	private Double dimensionDeTemperatura1_TC_;
	private Double dimensionDeTemperatura2;
	private String unidadDeMedidaParaLaTemperatura;
	private String denominacionComercial;
	private String denominacionCientifica;
	private String paisDeCaptura_produccion_cosecha_cria;
	private String zonaFAODeCaptura;
	private String metodoDeProduccion;
	private String codigoDePresentacion;
	private String codigoFAODeLaEspecie;
	private String fechaOPeriodoDeCaptura;
	private String fechaDeProduccion;
	private String arteDePesca;
	private String informacionDeCongelado;
	private String fechaDeCongelacion_91E_;


	private static Pattern PATTERN_SEH1L_numeroDeLineaDelArticulo = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SEH1L_codigoEANDelArticulo = Pattern.compile("^.{12}(.{15}).*");
	private static Pattern PATTERN_SEH1L_descripcionDelArticulo = Pattern.compile("^.{27}(.{70}).*");
	private static Pattern PATTERN_SEH1L_tipoDeIdentificacionDelArticulo_CU_DU_ = Pattern.compile("^.{97}(.{7}).*");
	private static Pattern PATTERN_SEH1L_numeroDeArticuloDelProveedor_SA_ = Pattern.compile("^.{104}(.{15}).*");
	private static Pattern PATTERN_SEH1L_numeroVariablePromocional_PV_ = Pattern.compile("^.{119}(.{15}).*");
	private static Pattern PATTERN_SEH1L_codigoDUN_14_ADU_ = Pattern.compile("^.{134}(.{15}).*");
	private static Pattern PATTERN_SEH1L_codigoACU_ACU_ = Pattern.compile("^.{149}(.{15}).*");
	private static Pattern PATTERN_SEH1L_numeroDeLote_NB_ = Pattern.compile("^.{164}(.{35}).*");
	private static Pattern PATTERN_SEH1L_numeroDeArticuloDelComprador_IN_ = Pattern.compile("^.{199}(.{15}).*");
	private static Pattern PATTERN_SEH1L_cantidadEnviada_12_ = Pattern.compile("^.{214}(.{16}).*");
	private static Pattern PATTERN_SEH1L_unidadDeMedidaCantidadEnviada = Pattern.compile("^.{230}(.{6}).*");
	private static Pattern PATTERN_SEH1L_unidadesDeConsumoEnUnidadDeExpedicion_59_ = Pattern.compile("^.{236}(.{16}).*");
	private static Pattern PATTERN_SEH1L_fechaDeCaducidad_36__102_203_ = Pattern.compile("^.{252}(.{12}).*");
	private static Pattern PATTERN_SEH1L_calificadorReferencia1 = Pattern.compile("^.{264}(.{6}).*");
	private static Pattern PATTERN_SEH1L_numeroReferencia1 = Pattern.compile("^.{270}(.{17}).*");
	private static Pattern PATTERN_SEH1L_fecha_horaReferencia1_102_203_ = Pattern.compile("^.{287}(.{12}).*");
	private static Pattern PATTERN_SEH1L_calificadorReferencia2 = Pattern.compile("^.{299}(.{6}).*");
	private static Pattern PATTERN_SEH1L_numeroReferencia2 = Pattern.compile("^.{305}(.{17}).*");
	private static Pattern PATTERN_SEH1L_fecha_horaReferencia2_102_203_ = Pattern.compile("^.{322}(.{12}).*");
	private static Pattern PATTERN_SEH1L_calificadorReferencia3 = Pattern.compile("^.{334}(.{6}).*");
	private static Pattern PATTERN_SEH1L_numeroReferencia3 = Pattern.compile("^.{340}(.{17}).*");
	private static Pattern PATTERN_SEH1L_fecha_horaReferencia3_102_203_ = Pattern.compile("^.{357}(.{12}).*");
	private static Pattern PATTERN_SEH1L_unidadesEnAgrupacionSuperior_45E_ = Pattern.compile("^.{369}(.{16}).*");
	private static Pattern PATTERN_SEH1L_codigoEANAdicional = Pattern.compile("^.{385}(.{15}).*");
	private static Pattern PATTERN_SEH1L_cantidadSinCargo_192_ = Pattern.compile("^.{400}(.{16}).*");
	private static Pattern PATTERN_SEH1L_calificadorCantidadAdicional = Pattern.compile("^.{416}(.{3}).*");
	private static Pattern PATTERN_SEH1L_cantidadAdicional = Pattern.compile("^.{419}(.{16}).*");
	private static Pattern PATTERN_SEH1L_unidadDeMedidaCantidadAdicional = Pattern.compile("^.{435}(.{6}).*");
	private static Pattern PATTERN_SEH1L_numeroDeSerieDelArticulo_SN_ = Pattern.compile("^.{441}(.{35}).*");
	private static Pattern PATTERN_SEH1L_numeroArticuloFabricante_MF_ = Pattern.compile("^.{476}(.{35}).*");
	private static Pattern PATTERN_SEH1L_numeroDeLineaReferencia1 = Pattern.compile("^.{511}(.{6}).*");
	private static Pattern PATTERN_SEH1L_numeroDeLineaReferencia2 = Pattern.compile("^.{517}(.{6}).*");
	private static Pattern PATTERN_SEH1L_numeroDeLineaReferencia3 = Pattern.compile("^.{523}(.{6}).*");
	private static Pattern PATTERN_SEH1L_diferenciaEnCantidadPedida_21_ = Pattern.compile("^.{529}(.{16}).*");
	private static Pattern PATTERN_SEH1L_codigoDiscrepancia = Pattern.compile("^.{545}(.{3}).*");
	private static Pattern PATTERN_SEH1L_pesoTotalNetoDeLaLinea_AAI_AAF_ = Pattern.compile("^.{548}(.{18}).*");
	private static Pattern PATTERN_SEH1L_pesoTotalBrutoDeLaLinea_AAI_AAB_ = Pattern.compile("^.{566}(.{18}).*");
	private static Pattern PATTERN_SEH1L_unidadDeMedidaPeso = Pattern.compile("^.{584}(.{3}).*");
	private static Pattern PATTERN_SEH1L_dimensionDeTemperatura1_TC_ = Pattern.compile("^.{587}(.{18}).*");
	private static Pattern PATTERN_SEH1L_dimensionDeTemperatura2 = Pattern.compile("^.{605}(.{18}).*");
	private static Pattern PATTERN_SEH1L_unidadDeMedidaParaLaTemperatura = Pattern.compile("^.{623}(.{3}).*");
	private static Pattern PATTERN_SEH1L_denominacionComercial = Pattern.compile("^.{626}(.{70}).*");
	private static Pattern PATTERN_SEH1L_denominacionCientifica = Pattern.compile("^.{696}(.{70}).*");
	private static Pattern PATTERN_SEH1L_paisDeCaptura_produccion_cosecha_cria = Pattern.compile("^.{766}(.{17}).*");
	private static Pattern PATTERN_SEH1L_zonaFAODeCaptura = Pattern.compile("^.{783}(.{17}).*");
	private static Pattern PATTERN_SEH1L_metodoDeProduccion = Pattern.compile("^.{800}(.{17}).*");
	private static Pattern PATTERN_SEH1L_codigoDePresentacion = Pattern.compile("^.{817}(.{17}).*");
	private static Pattern PATTERN_SEH1L_codigoFAODeLaEspecie = Pattern.compile("^.{834}(.{35}).*");
	private static Pattern PATTERN_SEH1L_fechaOPeriodoDeCaptura = Pattern.compile("^.{869}(.{16}).*");
	private static Pattern PATTERN_SEH1L_fechaDeProduccion = Pattern.compile("^.{885}(.{12}).*");
	private static Pattern PATTERN_SEH1L_arteDePesca = Pattern.compile("^.{897}(.{17}).*");
	private static Pattern PATTERN_SEH1L_informacionDeCongelado = Pattern.compile("^.{914}(.{17}).*");
	private static Pattern PATTERN_SEH1L_fechaDeCongelacion_91E_ = Pattern.compile("^.{931}(.{12}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SEH1L_numeroDeLineaDelArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaDelArticulo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoEANDelArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEANDelArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_descripcionDelArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDelArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_tipoDeIdentificacionDelArticulo_CU_DU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeIdentificacionDelArticulo_CU_DU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeArticuloDelProveedor_SA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeArticuloDelProveedor_SA_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroVariablePromocional_PV_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroVariablePromocional_PV_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoDUN_14_ADU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDUN_14_ADU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoACU_ACU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoACU_ACU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeLote_NB_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLote_NB_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeArticuloDelComprador_IN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeArticuloDelComprador_IN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_cantidadEnviada_12_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadEnviada_12_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_unidadDeMedidaCantidadEnviada.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaCantidadEnviada(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_unidadesDeConsumoEnUnidadDeExpedicion_59_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadesDeConsumoEnUnidadDeExpedicion_59_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fechaDeCaducidad_36__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeCaducidad_36__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_calificadorReferencia1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferencia1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroReferencia1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroReferencia1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fecha_horaReferencia1_102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaReferencia1_102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_calificadorReferencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferencia2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroReferencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroReferencia2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fecha_horaReferencia2_102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaReferencia2_102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_calificadorReferencia3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferencia3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroReferencia3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroReferencia3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fecha_horaReferencia3_102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaReferencia3_102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_unidadesEnAgrupacionSuperior_45E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadesEnAgrupacionSuperior_45E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoEANAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEANAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_cantidadSinCargo_192_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadSinCargo_192_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_calificadorCantidadAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorCantidadAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_cantidadAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadAdicional(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_unidadDeMedidaCantidadAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaCantidadAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeSerieDelArticulo_SN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeSerieDelArticulo_SN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroArticuloFabricante_MF_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroArticuloFabricante_MF_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeLineaReferencia1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaReferencia1(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeLineaReferencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaReferencia2(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeLineaReferencia3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaReferencia3(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_diferenciaEnCantidadPedida_21_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDiferenciaEnCantidadPedida_21_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoDiscrepancia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDiscrepancia(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_pesoTotalNetoDeLaLinea_AAI_AAF_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoTotalNetoDeLaLinea_AAI_AAF_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_pesoTotalBrutoDeLaLinea_AAI_AAB_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoTotalBrutoDeLaLinea_AAI_AAB_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_unidadDeMedidaPeso.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaPeso(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_dimensionDeTemperatura1_TC_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDimensionDeTemperatura1_TC_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_dimensionDeTemperatura2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDimensionDeTemperatura2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_unidadDeMedidaParaLaTemperatura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaParaLaTemperatura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_denominacionComercial.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDenominacionComercial(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_denominacionCientifica.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDenominacionCientifica(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_paisDeCaptura_produccion_cosecha_cria.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPaisDeCaptura_produccion_cosecha_cria(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_zonaFAODeCaptura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setZonaFAODeCaptura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_metodoDeProduccion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMetodoDeProduccion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoDePresentacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDePresentacion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoFAODeLaEspecie.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoFAODeLaEspecie(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fechaOPeriodoDeCaptura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaOPeriodoDeCaptura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fechaDeProduccion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeProduccion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_arteDePesca.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setArteDePesca(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_informacionDeCongelado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setInformacionDeCongelado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fechaDeCongelacion_91E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeCongelacion_91E_(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 19, 20 y 21 - Referencia 2: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado en el campo 21 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public Integer getNumeroDeLineaDelArticulo() {
		return numeroDeLineaDelArticulo;
	}

	/** 
	 * 19, 20 y 21 - Referencia 2: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado en el campo 21 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Número de línea del articulo</td> <td>N</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaDelArticulo(Integer numeroDeLineaDelArticulo) {
		this.numeroDeLineaDelArticulo = numeroDeLineaDelArticulo;
	}

	/** 
	 * 13 - Unidad de medida cantidad enviada: Solo se utiliza si el producto que se está identificando es de medida variable. Este campo se corresponde con el elemento 6411.
	 */ 
	public String getCodigoEANDelArticulo() {
		return codigoEANDelArticulo;
	}

	/** 
	 * 13 - Unidad de medida cantidad enviada: Solo se utiliza si el producto que se está identificando es de medida variable. Este campo se corresponde con el elemento 6411.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Código EAN del articulo</td> <td>C</td> <td>15</td> <td>13</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEANDelArticulo(String codigoEANDelArticulo) {
		this.codigoEANDelArticulo = codigoEANDelArticulo;
	}

	/** 
	 * 22, 23 y 24 - Referencia 3: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 24 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getDescripcionDelArticulo() {
		return descripcionDelArticulo;
	}

	/** 
	 * 22, 23 y 24 - Referencia 3: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 24 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Descripción del articulo</td> <td>C</td> <td>70</td> <td>28</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDescripcionDelArticulo(String descripcionDelArticulo) {
		this.descripcionDelArticulo = descripcionDelArticulo;
	}

	/** 
	 * 5 - Tipo de identificación del artículo: Se utiliza para indicar el tío de código suministrado en el campo 3. Este campo se corresponde con el elemento 7009. Los posibles valores son:
	 */ 
	public String getTipoDeIdentificacionDelArticulo_CU_DU_() {
		return tipoDeIdentificacionDelArticulo_CU_DU_;
	}

	/** 
	 * 5 - Tipo de identificación del artículo: Se utiliza para indicar el tío de código suministrado en el campo 3. Este campo se corresponde con el elemento 7009. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Tipo de identificación del articulo (CU/DU)</td> <td>C</td> <td>7</td> <td>98</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeIdentificacionDelArticulo_CU_DU_(String tipoDeIdentificacionDelArticulo_CU_DU_) {
		this.tipoDeIdentificacionDelArticulo_CU_DU_ = tipoDeIdentificacionDelArticulo_CU_DU_;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getNumeroDeArticuloDelProveedor_SA_() {
		return numeroDeArticuloDelProveedor_SA_;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Número de articulo del proveedor (SA)</td> <td>C</td> <td>15</td> <td>105</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeArticuloDelProveedor_SA_(String numeroDeArticuloDelProveedor_SA_) {
		this.numeroDeArticuloDelProveedor_SA_ = numeroDeArticuloDelProveedor_SA_;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getNumeroVariablePromocional_PV_() {
		return numeroVariablePromocional_PV_;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Número variable promocional (PV)</td> <td>C</td> <td>15</td> <td>120</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroVariablePromocional_PV_(String numeroVariablePromocional_PV_) {
		this.numeroVariablePromocional_PV_ = numeroVariablePromocional_PV_;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getCodigoDUN_14_ADU_() {
		return codigoDUN_14_ADU_;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Código DUN-14 (ADU)</td> <td>C</td> <td>15</td> <td>135</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoDUN_14_ADU_(String codigoDUN_14_ADU_) {
		this.codigoDUN_14_ADU_ = codigoDUN_14_ADU_;
	}

	/** 
	 * 19, 20 y 21 - Referencia 2: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado en el campo 21 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getCodigoACU_ACU_() {
		return codigoACU_ACU_;
	}

	/** 
	 * 19, 20 y 21 - Referencia 2: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado en el campo 21 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Código ACU (ACU)</td> <td>C</td> <td>15</td> <td>150</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoACU_ACU_(String codigoACU_ACU_) {
		this.codigoACU_ACU_ = codigoACU_ACU_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeLote_NB_() {
		return numeroDeLote_NB_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Número de lote (NB)</td> <td>C</td> <td>35</td> <td>165</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLote_NB_(String numeroDeLote_NB_) {
		this.numeroDeLote_NB_ = numeroDeLote_NB_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeArticuloDelComprador_IN_() {
		return numeroDeArticuloDelComprador_IN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Número de articulo del comprador (IN)</td> <td>C</td> <td>15</td> <td>200</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeArticuloDelComprador_IN_(String numeroDeArticuloDelComprador_IN_) {
		this.numeroDeArticuloDelComprador_IN_ = numeroDeArticuloDelComprador_IN_;
	}

	/** 
	 * 
	 */ 
	public Double getCantidadEnviada_12_() {
		return cantidadEnviada_12_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Cantidad enviada (12)</td> <td>N(12,3)</td> <td>16</td> <td>215</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadEnviada_12_(Double cantidadEnviada_12_) {
		this.cantidadEnviada_12_ = cantidadEnviada_12_;
	}

	/** 
	 * 13 - Unidad de medida cantidad enviada: Solo se utiliza si el producto que se está identificando es de medida variable. Este campo se corresponde con el elemento 6411.
	 */ 
	public String getUnidadDeMedidaCantidadEnviada() {
		return unidadDeMedidaCantidadEnviada;
	}

	/** 
	 * 13 - Unidad de medida cantidad enviada: Solo se utiliza si el producto que se está identificando es de medida variable. Este campo se corresponde con el elemento 6411.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Unidad de medida cantidad enviada</td> <td>C</td> <td>6</td> <td>231</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaCantidadEnviada(String unidadDeMedidaCantidadEnviada) {
		this.unidadDeMedidaCantidadEnviada = unidadDeMedidaCantidadEnviada;
	}

	/** 
	 * 
	 */ 
	public Double getUnidadesDeConsumoEnUnidadDeExpedicion_59_() {
		return unidadesDeConsumoEnUnidadDeExpedicion_59_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Unidades de consumo en unidad de expedición (59)</td> <td>N(12,3)</td> <td>16</td> <td>237</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadesDeConsumoEnUnidadDeExpedicion_59_(Double unidadesDeConsumoEnUnidadDeExpedicion_59_) {
		this.unidadesDeConsumoEnUnidadDeExpedicion_59_ = unidadesDeConsumoEnUnidadDeExpedicion_59_;
	}

	/** 
	 * 15 - Fecha de caducidad (36) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFechaDeCaducidad_36__102_203_() {
		return fechaDeCaducidad_36__102_203_;
	}

	/** 
	 * 15 - Fecha de caducidad (36) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Fecha de caducidad (36) (102/203)</td> <td>C</td> <td>12</td> <td>253</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeCaducidad_36__102_203_(String fechaDeCaducidad_36__102_203_) {
		this.fechaDeCaducidad_36__102_203_ = fechaDeCaducidad_36__102_203_;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getCalificadorReferencia1() {
		return calificadorReferencia1;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Calificador referencia 1</td> <td>C</td> <td>6</td> <td>265</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorReferencia1(String calificadorReferencia1) {
		this.calificadorReferencia1 = calificadorReferencia1;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getNumeroReferencia1() {
		return numeroReferencia1;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Numero referencia 1</td> <td>C</td> <td>17</td> <td>271</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroReferencia1(String numeroReferencia1) {
		this.numeroReferencia1 = numeroReferencia1;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaReferencia1_102_203_() {
		return fecha_horaReferencia1_102_203_;
	}

	/** 
	 * 16, 17 y 18 - Referencia 1: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 18 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Fecha/hora referencia 1(102/203)</td> <td>C</td> <td>12</td> <td>288</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaReferencia1_102_203_(String fecha_horaReferencia1_102_203_) {
		this.fecha_horaReferencia1_102_203_ = fecha_horaReferencia1_102_203_;
	}

	/** 
	 * 19, 20 y 21 - Referencia 2: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado en el campo 21 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getCalificadorReferencia2() {
		return calificadorReferencia2;
	}

	/** 
	 * 19, 20 y 21 - Referencia 2: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado en el campo 21 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Calificador referencia 2</td> <td>C</td> <td>6</td> <td>300</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorReferencia2(String calificadorReferencia2) {
		this.calificadorReferencia2 = calificadorReferencia2;
	}

	/** 
	 * 19, 20 y 21 - Referencia 2: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado en el campo 21 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getNumeroReferencia2() {
		return numeroReferencia2;
	}

	/** 
	 * 19, 20 y 21 - Referencia 2: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado en el campo 21 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Número referencia 2</td> <td>C</td> <td>17</td> <td>306</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroReferencia2(String numeroReferencia2) {
		this.numeroReferencia2 = numeroReferencia2;
	}

	/** 
	 * 19, 20 y 21 - Referencia 2: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado en el campo 21 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaReferencia2_102_203_() {
		return fecha_horaReferencia2_102_203_;
	}

	/** 
	 * 19, 20 y 21 - Referencia 2: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado en el campo 21 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>Fecha/hora referencia 2(102/203)</td> <td>C</td> <td>12</td> <td>323</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaReferencia2_102_203_(String fecha_horaReferencia2_102_203_) {
		this.fecha_horaReferencia2_102_203_ = fecha_horaReferencia2_102_203_;
	}

	/** 
	 * 22, 23 y 24 - Referencia 3: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 24 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getCalificadorReferencia3() {
		return calificadorReferencia3;
	}

	/** 
	 * 22, 23 y 24 - Referencia 3: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 24 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>22</td> <td>Calificador referencia 3</td> <td>C</td> <td>6</td> <td>335</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorReferencia3(String calificadorReferencia3) {
		this.calificadorReferencia3 = calificadorReferencia3;
	}

	/** 
	 * 22, 23 y 24 - Referencia 3: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 24 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getNumeroReferencia3() {
		return numeroReferencia3;
	}

	/** 
	 * 22, 23 y 24 - Referencia 3: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 24 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>Número referencia 3</td> <td>C</td> <td>17</td> <td>341</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroReferencia3(String numeroReferencia3) {
		this.numeroReferencia3 = numeroReferencia3;
	}

	/** 
	 * 22, 23 y 24 - Referencia 3: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 24 es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaReferencia3_102_203_() {
		return fecha_horaReferencia3_102_203_;
	}

	/** 
	 * 22, 23 y 24 - Referencia 3: Estos campos se utilizan para informar referencias relacionadas con la línea del artículo. El formato aceptado para el campo 24 es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Fecha/hora Referencia 3(102/203)</td> <td>C</td> <td>12</td> <td>358</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaReferencia3_102_203_(String fecha_horaReferencia3_102_203_) {
		this.fecha_horaReferencia3_102_203_ = fecha_horaReferencia3_102_203_;
	}

	/** 
	 * 
	 */ 
	public String getUnidadesEnAgrupacionSuperior_45E_() {
		return unidadesEnAgrupacionSuperior_45E_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>25</td> <td>Unidades en Agrupación Superior (45E)</td> <td>C</td> <td>16</td> <td>370</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadesEnAgrupacionSuperior_45E_(String unidadesEnAgrupacionSuperior_45E_) {
		this.unidadesEnAgrupacionSuperior_45E_ = unidadesEnAgrupacionSuperior_45E_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoEANAdicional() {
		return codigoEANAdicional;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>26</td> <td>Código EAN adicional</td> <td>C</td> <td>15</td> <td>386</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEANAdicional(String codigoEANAdicional) {
		this.codigoEANAdicional = codigoEANAdicional;
	}

	/** 
	 * 
	 */ 
	public String getCantidadSinCargo_192_() {
		return cantidadSinCargo_192_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>27</td> <td>Cantidad sin cargo (192)</td> <td>C</td> <td>16</td> <td>401</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadSinCargo_192_(String cantidadSinCargo_192_) {
		this.cantidadSinCargo_192_ = cantidadSinCargo_192_;
	}

	/** 
	 * 28, 29 y 30 - Cantidad adicional: Cuando es necesario enviar otra cantidad.
	 */ 
	public String getCalificadorCantidadAdicional() {
		return calificadorCantidadAdicional;
	}

	/** 
	 * 28, 29 y 30 - Cantidad adicional: Cuando es necesario enviar otra cantidad.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>28</td> <td>Calificador cantidad adicional</td> <td>C</td> <td>3</td> <td>417</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorCantidadAdicional(String calificadorCantidadAdicional) {
		this.calificadorCantidadAdicional = calificadorCantidadAdicional;
	}

	/** 
	 * 28, 29 y 30 - Cantidad adicional: Cuando es necesario enviar otra cantidad.
	 */ 
	public Double getCantidadAdicional() {
		return cantidadAdicional;
	}

	/** 
	 * 28, 29 y 30 - Cantidad adicional: Cuando es necesario enviar otra cantidad.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>29</td> <td>Cantidad adicional</td> <td>N(12,3)</td> <td>16</td> <td>420</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadAdicional(Double cantidadAdicional) {
		this.cantidadAdicional = cantidadAdicional;
	}

	/** 
	 * 28, 29 y 30 - Cantidad adicional: Cuando es necesario enviar otra cantidad.
	 */ 
	public String getUnidadDeMedidaCantidadAdicional() {
		return unidadDeMedidaCantidadAdicional;
	}

	/** 
	 * 28, 29 y 30 - Cantidad adicional: Cuando es necesario enviar otra cantidad.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>30</td> <td>Unidad de medida cantidad adicional</td> <td>C</td> <td>6</td> <td>436</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaCantidadAdicional(String unidadDeMedidaCantidadAdicional) {
		this.unidadDeMedidaCantidadAdicional = unidadDeMedidaCantidadAdicional;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeSerieDelArticulo_SN_() {
		return numeroDeSerieDelArticulo_SN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>31</td> <td>Número de serie del artículo (SN)</td> <td>C</td> <td>35</td> <td>442</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeSerieDelArticulo_SN_(String numeroDeSerieDelArticulo_SN_) {
		this.numeroDeSerieDelArticulo_SN_ = numeroDeSerieDelArticulo_SN_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroArticuloFabricante_MF_() {
		return numeroArticuloFabricante_MF_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>32</td> <td>Número artículo fabricante (MF)</td> <td>C</td> <td>35</td> <td>477</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroArticuloFabricante_MF_(String numeroArticuloFabricante_MF_) {
		this.numeroArticuloFabricante_MF_ = numeroArticuloFabricante_MF_;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeLineaReferencia1() {
		return numeroDeLineaReferencia1;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>33</td> <td>Número de línea referencia 1</td> <td>N</td> <td>6</td> <td>512</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaReferencia1(Integer numeroDeLineaReferencia1) {
		this.numeroDeLineaReferencia1 = numeroDeLineaReferencia1;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeLineaReferencia2() {
		return numeroDeLineaReferencia2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>34</td> <td>Número de línea referencia 2</td> <td>N</td> <td>6</td> <td>518</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaReferencia2(Integer numeroDeLineaReferencia2) {
		this.numeroDeLineaReferencia2 = numeroDeLineaReferencia2;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeLineaReferencia3() {
		return numeroDeLineaReferencia3;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>35</td> <td>Número de línea referencia 3</td> <td>N</td> <td>6</td> <td>524</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaReferencia3(Integer numeroDeLineaReferencia3) {
		this.numeroDeLineaReferencia3 = numeroDeLineaReferencia3;
	}

	/** 
	 * 
	 */ 
	public Double getDiferenciaEnCantidadPedida_21_() {
		return diferenciaEnCantidadPedida_21_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>36</td> <td>Diferencia en cantidad pedida (21)</td> <td>N(12,3)</td> <td>16</td> <td>530</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDiferenciaEnCantidadPedida_21_(Double diferenciaEnCantidadPedida_21_) {
		this.diferenciaEnCantidadPedida_21_ = diferenciaEnCantidadPedida_21_;
	}

	/** 
	 * 37 - Código de Discrepancia: El campo se corresponde con el elemento 4221. Los valores posibles son:
	 */ 
	public String getCodigoDiscrepancia() {
		return codigoDiscrepancia;
	}

	/** 
	 * 37 - Código de Discrepancia: El campo se corresponde con el elemento 4221. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>37</td> <td>Código Discrepancia</td> <td>C</td> <td>3</td> <td>546</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoDiscrepancia(String codigoDiscrepancia) {
		this.codigoDiscrepancia = codigoDiscrepancia;
	}

	/** 
	 * 
	 */ 
	public Double getPesoTotalNetoDeLaLinea_AAI_AAF_() {
		return pesoTotalNetoDeLaLinea_AAI_AAF_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>38</td> <td>Peso total neto de la línea (AAI/AAF)</td> <td>N(14,3)</td> <td>18</td> <td>549</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoTotalNetoDeLaLinea_AAI_AAF_(Double pesoTotalNetoDeLaLinea_AAI_AAF_) {
		this.pesoTotalNetoDeLaLinea_AAI_AAF_ = pesoTotalNetoDeLaLinea_AAI_AAF_;
	}

	/** 
	 * 
	 */ 
	public Double getPesoTotalBrutoDeLaLinea_AAI_AAB_() {
		return pesoTotalBrutoDeLaLinea_AAI_AAB_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>39</td> <td>Peso total bruto de la línea (AAI/AAB)</td> <td>N(14,3)</td> <td>18</td> <td>567</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoTotalBrutoDeLaLinea_AAI_AAB_(Double pesoTotalBrutoDeLaLinea_AAI_AAB_) {
		this.pesoTotalBrutoDeLaLinea_AAI_AAB_ = pesoTotalBrutoDeLaLinea_AAI_AAB_;
	}

	/** 
	 * 
	 */ 
	public String getUnidadDeMedidaPeso() {
		return unidadDeMedidaPeso;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>40</td> <td>Unidad de medida peso</td> <td>C</td> <td>3</td> <td>585</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaPeso(String unidadDeMedidaPeso) {
		this.unidadDeMedidaPeso = unidadDeMedidaPeso;
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
	 * 		 <td>41</td> <td>Dimensión de temperatura 1 (TC)</td> <td>N(14,3)</td> <td>18</td> <td>588</td> <td>O</td>
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
	 * 		 <td>42</td> <td>Dimensión de temperatura 2</td> <td>N(14,3)</td> <td>18</td> <td>606</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDimensionDeTemperatura2(Double dimensionDeTemperatura2) {
		this.dimensionDeTemperatura2 = dimensionDeTemperatura2;
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
	 * 		 <td>43</td> <td>Unidad de medida para la temperatura</td> <td>C</td> <td>3</td> <td>624</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaParaLaTemperatura(String unidadDeMedidaParaLaTemperatura) {
		this.unidadDeMedidaParaLaTemperatura = unidadDeMedidaParaLaTemperatura;
	}

	/** 
	 * 44 - Denominación comercial: Para la trazabilidad de la pesca es obligatorio indicar la denominación comercial.
	 */ 
	public String getDenominacionComercial() {
		return denominacionComercial;
	}

	/** 
	 * 44 - Denominación comercial: Para la trazabilidad de la pesca es obligatorio indicar la denominación comercial.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>44</td> <td>Denominación comercial</td> <td>C</td> <td>70</td> <td>627</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDenominacionComercial(String denominacionComercial) {
		this.denominacionComercial = denominacionComercial;
	}

	/** 
	 * 45 - Denominación científica: Para la trazabilidad de la pesca es obligatorio indicar la denominación científica.
	 */ 
	public String getDenominacionCientifica() {
		return denominacionCientifica;
	}

	/** 
	 * 45 - Denominación científica: Para la trazabilidad de la pesca es obligatorio indicar la denominación científica.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>45</td> <td>Denominación científica</td> <td>C</td> <td>70</td> <td>697</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDenominacionCientifica(String denominacionCientifica) {
		this.denominacionCientifica = denominacionCientifica;
	}

	/** 
	 * 46 - País de captura/producción/cosecha/cría: Para la trazabilidad de la pesca es obligatorio indicar el país o la zona FAO de captura.
	 */ 
	public String getPaisDeCaptura_produccion_cosecha_cria() {
		return paisDeCaptura_produccion_cosecha_cria;
	}

	/** 
	 * 46 - País de captura/producción/cosecha/cría: Para la trazabilidad de la pesca es obligatorio indicar el país o la zona FAO de captura.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>46</td> <td>País de captura/producción/cosecha/cría</td> <td>C</td> <td>17</td> <td>767</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPaisDeCaptura_produccion_cosecha_cria(String paisDeCaptura_produccion_cosecha_cria) {
		this.paisDeCaptura_produccion_cosecha_cria = paisDeCaptura_produccion_cosecha_cria;
	}

	/** 
	 * 47 - Zona FAO de captura: Para la trazabilidad de la pesca es obligatorio indicar el país o la zona FAO de captura.
	 */ 
	public String getZonaFAODeCaptura() {
		return zonaFAODeCaptura;
	}

	/** 
	 * 47 - Zona FAO de captura: Para la trazabilidad de la pesca es obligatorio indicar el país o la zona FAO de captura.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>47</td> <td>Zona FAO de captura</td> <td>C</td> <td>17</td> <td>784</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setZonaFAODeCaptura(String zonaFAODeCaptura) {
		this.zonaFAODeCaptura = zonaFAODeCaptura;
	}

	/** 
	 * 48 - Método de producción: Para la trazabilidad de la pesca es obligatorio indicar el método de producción. Los valores posibles son:
	 */ 
	public String getMetodoDeProduccion() {
		return metodoDeProduccion;
	}

	/** 
	 * 48 - Método de producción: Para la trazabilidad de la pesca es obligatorio indicar el método de producción. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>48</td> <td>Método de producción</td> <td>C</td> <td>17</td> <td>801</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMetodoDeProduccion(String metodoDeProduccion) {
		this.metodoDeProduccion = metodoDeProduccion;
	}

	/** 
	 * 49 - Código de presentación: Código europeo de presentación, que describe el modo en el que el pescado está presentado para la venta. Para la trazabilidad de la pesca es obligatorio indicar el código de presentación.
	 */ 
	public String getCodigoDePresentacion() {
		return codigoDePresentacion;
	}

	/** 
	 * 49 - Código de presentación: Código europeo de presentación, que describe el modo en el que el pescado está presentado para la venta. Para la trazabilidad de la pesca es obligatorio indicar el código de presentación.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>49</td> <td>Código de presentación</td> <td>C</td> <td>17</td> <td>818</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoDePresentacion(String codigoDePresentacion) {
		this.codigoDePresentacion = codigoDePresentacion;
	}

	/** 
	 * 50 - Código FAO de la especie: Para la trazabilidad de la pesca es obligatorio indicar el Código 3-alfa FAO de la especie.
	 */ 
	public String getCodigoFAODeLaEspecie() {
		return codigoFAODeLaEspecie;
	}

	/** 
	 * 50 - Código FAO de la especie: Para la trazabilidad de la pesca es obligatorio indicar el Código 3-alfa FAO de la especie.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>50</td> <td>Código FAO de la especie</td> <td>C</td> <td>35</td> <td>835</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoFAODeLaEspecie(String codigoFAODeLaEspecie) {
		this.codigoFAODeLaEspecie = codigoFAODeLaEspecie;
	}

	/** 
	 * 51 - Fecha de captura: Fecha de captura (AAAAMMDD) o periodo de tiempo que incluya varias fechas de captura (AAAAMMDDAAAAMMDD). Para la trazabilidad de la pesca es obligatorio indicar la fecha o periodo de captura, o en su caso la fecha de producción.
	 */ 
	public String getFechaOPeriodoDeCaptura() {
		return fechaOPeriodoDeCaptura;
	}

	/** 
	 * 51 - Fecha de captura: Fecha de captura (AAAAMMDD) o periodo de tiempo que incluya varias fechas de captura (AAAAMMDDAAAAMMDD). Para la trazabilidad de la pesca es obligatorio indicar la fecha o periodo de captura, o en su caso la fecha de producción.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>51</td> <td>Fecha o periodo de captura</td> <td>C</td> <td>16</td> <td>870</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaOPeriodoDeCaptura(String fechaOPeriodoDeCaptura) {
		this.fechaOPeriodoDeCaptura = fechaOPeriodoDeCaptura;
	}

	/** 
	 * 52 - Fecha de producción: Fecha de producción (AAAAMMDD). Para la trazabilidad de la pesca es obligatorio indicar la fecha o periodo de captura, o en su caso la fecha de producción.
	 */ 
	public String getFechaDeProduccion() {
		return fechaDeProduccion;
	}

	/** 
	 * 52 - Fecha de producción: Fecha de producción (AAAAMMDD). Para la trazabilidad de la pesca es obligatorio indicar la fecha o periodo de captura, o en su caso la fecha de producción.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>52</td> <td>Fecha de producción</td> <td>C</td> <td>12</td> <td>886</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeProduccion(String fechaDeProduccion) {
		this.fechaDeProduccion = fechaDeProduccion;
	}

	/** 
	 * 53 - Arte de pesca: Se utilizará la lista de códigos de artes de pesca FAO.
	 */ 
	public String getArteDePesca() {
		return arteDePesca;
	}

	/** 
	 * 53 - Arte de pesca: Se utilizará la lista de códigos de artes de pesca FAO.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>53</td> <td>Arte de pesca</td> <td>C</td> <td>17</td> <td>898</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setArteDePesca(String arteDePesca) {
		this.arteDePesca = arteDePesca;
	}

	/** 
	 * 54 - Información de congelado: Para la trazabilidad de la pesca es obligatorio indicar si ha sido previamente congelado. En caso de haber estado previamente congelado, se deberá indicar la fecha de congelación campo 55. Los valores posibles son:
	 */ 
	public String getInformacionDeCongelado() {
		return informacionDeCongelado;
	}

	/** 
	 * 54 - Información de congelado: Para la trazabilidad de la pesca es obligatorio indicar si ha sido previamente congelado. En caso de haber estado previamente congelado, se deberá indicar la fecha de congelación campo 55. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>54</td> <td>Información de congelado</td> <td>C</td> <td>17</td> <td>915</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setInformacionDeCongelado(String informacionDeCongelado) {
		this.informacionDeCongelado = informacionDeCongelado;
	}

	/** 
	 * 52 - Fecha de producción: Fecha de producción (AAAAMMDD). Para la trazabilidad de la pesca es obligatorio indicar la fecha o periodo de captura, o en su caso la fecha de producción.
	 */ 
	public String getFechaDeCongelacion_91E_() {
		return fechaDeCongelacion_91E_;
	}

	/** 
	 * 52 - Fecha de producción: Fecha de producción (AAAAMMDD). Para la trazabilidad de la pesca es obligatorio indicar la fecha o periodo de captura, o en su caso la fecha de producción.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>52</td> <td>Fecha de congelación (91E)</td> <td>C</td> <td>12</td> <td>932</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeCongelacion_91E_(String fechaDeCongelacion_91E_) {
		this.fechaDeCongelacion_91E_ = fechaDeCongelacion_91E_;
	}

	/** 
	 * 37 - Código de Discrepancia: El campo se corresponde con el elemento 4221. Los valores posibles son:
	 */
	public enum SEH1L_37 {
		ENVIO_PARCIAL__QUE_TIENE_QUE_COMPLETARS_BP("BP"),
		ENVIO_PARCIAL_CONSIDERADO_COMPLETO__SIN_ENVIO_SUPLEMENTARI_CP("CP"),
		;
		
		private String value;
		
		private SEH1L_37(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1L_37 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 48 - Método de producción: Para la trazabilidad de la pesca es obligatorio indicar el método de producción. Los valores posibles son:
	 */
	public enum SEH1L_48 {
		PESCAD_F01("F01"),
		PESCADO_EN_AGUAS_DULCE_F02("F02"),
		CRIADO_O_ACUICULTUR_F03("F03"),
		;
		
		private String value;
		
		private SEH1L_48(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1L_48 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 54 - Información de congelado: Para la trazabilidad de la pesca es obligatorio indicar si ha sido previamente congelado. En caso de haber estado previamente congelado, se deberá indicar la fecha de congelación campo 55. Los valores posibles son:
	 */
	public enum SEH1L_54 {
		CONGELADO_PREVIAMENT_FZ1("FZ1"),
		NO_CONGELADO_PREVIAMENT_FZ2("FZ2"),
		;
		
		private String value;
		
		private SEH1L_54(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1L_54 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}