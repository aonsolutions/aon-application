package com.esferalia.aon.file.seres.udapa.delivery.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1B entity.
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
 * 		 <td>SEH1B</td> <td>Información de Lotes</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SEH1B {

	private String tipoAvisoDeExpedicion_351_35E_;
	private String numeroAvisoDeExpedicion;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private Integer numeroDeJerarquiaDeEmbalaje;
	private String numeroDeSub_jerarquiaDeEmbalaje;
	private Integer numeroDeLineaArticulo;
	private Integer contadorInformacionDeLotes;
	private String codigoInstrucciones;
	private String fechaDeCaducidad_36__102_203_;
	private String fechaRecepcionDeMercancias_50__102_203_;
	private String mejorAntesDeFecha_361__102_203_;
	private String calificadorCantidad_11_12_;
	private Double cantidad;
	private String calificadorDelNumeroDeIdentidad;
	private String numeroDeIdentidad;
	private String fechaDeEnvasadoOEmpaquetado;


	private static Pattern PATTERN_SEH1B_tipoAvisoDeExpedicion_351_35E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SEH1B_numeroAvisoDeExpedicion = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SEH1B_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_SEH1B_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_SEH1B_numeroDeJerarquiaDeEmbalaje = Pattern.compile("^.{63}(.{12}).*");
	private static Pattern PATTERN_SEH1B_numeroDeSub_jerarquiaDeEmbalaje = Pattern.compile("^.{75}(.{12}).*");
	private static Pattern PATTERN_SEH1B_numeroDeLineaArticulo = Pattern.compile("^.{87}(.{6}).*");
	private static Pattern PATTERN_SEH1B_contadorInformacionDeLotes = Pattern.compile("^.{93}(.{4}).*");
	private static Pattern PATTERN_SEH1B_codigoInstrucciones = Pattern.compile("^.{97}(.{3}).*");
	private static Pattern PATTERN_SEH1B_fechaDeCaducidad_36__102_203_ = Pattern.compile("^.{100}(.{12}).*");
	private static Pattern PATTERN_SEH1B_fechaRecepcionDeMercancias_50__102_203_ = Pattern.compile("^.{112}(.{12}).*");
	private static Pattern PATTERN_SEH1B_mejorAntesDeFecha_361__102_203_ = Pattern.compile("^.{124}(.{12}).*");
	private static Pattern PATTERN_SEH1B_calificadorCantidad_11_12_ = Pattern.compile("^.{136}(.{3}).*");
	private static Pattern PATTERN_SEH1B_cantidad = Pattern.compile("^.{139}(.{16}).*");
	private static Pattern PATTERN_SEH1B_calificadorDelNumeroDeIdentidad = Pattern.compile("^.{155}(.{3}).*");
	private static Pattern PATTERN_SEH1B_numeroDeIdentidad = Pattern.compile("^.{158}(.{35}).*");
	private static Pattern PATTERN_SEH1B_fechaDeEnvasadoOEmpaquetado = Pattern.compile("^.{193}(.{12}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SEH1B_tipoAvisoDeExpedicion_351_35E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoAvisoDeExpedicion_351_35E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_numeroAvisoDeExpedicion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroAvisoDeExpedicion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_numeroDeJerarquiaDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeJerarquiaDeEmbalaje(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_numeroDeSub_jerarquiaDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeSub_jerarquiaDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_numeroDeLineaArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaArticulo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_contadorInformacionDeLotes.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setContadorInformacionDeLotes(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_codigoInstrucciones.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInstrucciones(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_fechaDeCaducidad_36__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeCaducidad_36__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_fechaRecepcionDeMercancias_50__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaRecepcionDeMercancias_50__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_mejorAntesDeFecha_361__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMejorAntesDeFecha_361__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_calificadorCantidad_11_12_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorCantidad_11_12_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_cantidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidad(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_calificadorDelNumeroDeIdentidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDelNumeroDeIdentidad(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_numeroDeIdentidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeIdentidad(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_fechaDeEnvasadoOEmpaquetado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeEnvasadoOEmpaquetado(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 
	 */ 
	public String getTipoAvisoDeExpedicion_351_35E_() {
		return tipoAvisoDeExpedicion_351_35E_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1001T</td> <td>Tipo Aviso de Expedición (351/35E)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoAvisoDeExpedicion_351_35E_(String tipoAvisoDeExpedicion_351_35E_) {
		this.tipoAvisoDeExpedicion_351_35E_ = tipoAvisoDeExpedicion_351_35E_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroAvisoDeExpedicion() {
		return numeroAvisoDeExpedicion;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1004P</td> <td>Número Aviso de Expedición</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroAvisoDeExpedicion(String numeroAvisoDeExpedicion) {
		this.numeroAvisoDeExpedicion = numeroAvisoDeExpedicion;
	}

	/** 
	 * 
	 */ 
	public String getCodigoEmisor_MS_() {
		return codigoEmisor_MS_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3039E</td> <td>Código Emisor  (MS)</td> <td>C</td> <td>17</td> <td>30</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEmisor_MS_(String codigoEmisor_MS_) {
		this.codigoEmisor_MS_ = codigoEmisor_MS_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoReceptor_MR_() {
		return codigoReceptor_MR_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3039R</td> <td>Código Receptor (MR)</td> <td>C</td> <td>17</td> <td>47</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeJerarquiaDeEmbalaje() {
		return numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7164J</td> <td>Número de Jerarquía de Embalaje</td> <td>N</td> <td>12</td> <td>64</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeJerarquiaDeEmbalaje(Integer numeroDeJerarquiaDeEmbalaje) {
		this.numeroDeJerarquiaDeEmbalaje = numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeSub_jerarquiaDeEmbalaje() {
		return numeroDeSub_jerarquiaDeEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7166J</td> <td>Número de Sub-jerarquía de Embalaje</td> <td>C</td> <td>12</td> <td>76</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeSub_jerarquiaDeEmbalaje(String numeroDeSub_jerarquiaDeEmbalaje) {
		this.numeroDeSub_jerarquiaDeEmbalaje = numeroDeSub_jerarquiaDeEmbalaje;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1082L</td> <td>Número de Línea Artículo</td> <td>N</td> <td>6</td> <td>88</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaArticulo(Integer numeroDeLineaArticulo) {
		this.numeroDeLineaArticulo = numeroDeLineaArticulo;
	}

	/** 
	 * 
	 */ 
	public Integer getContadorInformacionDeLotes() {
		return contadorInformacionDeLotes;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1082B</td> <td>Contador Información de Lotes</td> <td>N</td> <td>4</td> <td>94</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setContadorInformacionDeLotes(Integer contadorInformacionDeLotes) {
		this.contadorInformacionDeLotes = contadorInformacionDeLotes;
	}

	/** 
	 * V4233L-Código Instrucciones: Los valores posibles son:
	 */ 
	public String getCodigoInstrucciones() {
		return codigoInstrucciones;
	}

	/** 
	 * V4233L-Código Instrucciones: Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V4233L</td> <td>Código Instrucciones</td> <td>C</td> <td>3</td> <td>98</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoInstrucciones(String codigoInstrucciones) {
		this.codigoInstrucciones = codigoInstrucciones;
	}

	/** 
	 * 
	 */ 
	public String getFechaDeCaducidad_36__102_203_() {
		return fechaDeCaducidad_36__102_203_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V2380M</td> <td>Fecha de Caducidad (36) (102/203)</td> <td>C</td> <td>12</td> <td>101</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeCaducidad_36__102_203_(String fechaDeCaducidad_36__102_203_) {
		this.fechaDeCaducidad_36__102_203_ = fechaDeCaducidad_36__102_203_;
	}

	/** 
	 * 
	 */ 
	public String getFechaRecepcionDeMercancias_50__102_203_() {
		return fechaRecepcionDeMercancias_50__102_203_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V2380R</td> <td>Fecha Recepción de Mercancías (50) (102/203)</td> <td>C</td> <td>12</td> <td>113</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaRecepcionDeMercancias_50__102_203_(String fechaRecepcionDeMercancias_50__102_203_) {
		this.fechaRecepcionDeMercancias_50__102_203_ = fechaRecepcionDeMercancias_50__102_203_;
	}

	/** 
	 * 
	 */ 
	public String getMejorAntesDeFecha_361__102_203_() {
		return mejorAntesDeFecha_361__102_203_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V2380B</td> <td>Mejor antes de fecha (361) (102/203)</td> <td>C</td> <td>12</td> <td>125</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMejorAntesDeFecha_361__102_203_(String mejorAntesDeFecha_361__102_203_) {
		this.mejorAntesDeFecha_361__102_203_ = mejorAntesDeFecha_361__102_203_;
	}

	/** 
	 * V6063D-Calificador de Cantidad (11/12): Los valores posibles son:
	 */ 
	public String getCalificadorCantidad_11_12_() {
		return calificadorCantidad_11_12_;
	}

	/** 
	 * V6063D-Calificador de Cantidad (11/12): Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6063D</td> <td>Calificador cantidad  (11/12)</td> <td>C</td> <td>3</td> <td>137</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorCantidad_11_12_(String calificadorCantidad_11_12_) {
		this.calificadorCantidad_11_12_ = calificadorCantidad_11_12_;
	}

	/** 
	 * 
	 */ 
	public Double getCantidad() {
		return cantidad;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6060D</td> <td>Cantidad</td> <td>N(12,3)</td> <td>16</td> <td>140</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	/** 
	 * V7405L-Calificador  del número de identidad: Los valores posibles son:
	 */ 
	public String getCalificadorDelNumeroDeIdentidad() {
		return calificadorDelNumeroDeIdentidad;
	}

	/** 
	 * V7405L-Calificador  del número de identidad: Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7405L</td> <td>Calificador  del número de identidad</td> <td>C</td> <td>3</td> <td>156</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDelNumeroDeIdentidad(String calificadorDelNumeroDeIdentidad) {
		this.calificadorDelNumeroDeIdentidad = calificadorDelNumeroDeIdentidad;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeIdentidad() {
		return numeroDeIdentidad;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7402L</td> <td>Número de Identidad</td> <td>C</td> <td>35</td> <td>159</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeIdentidad(String numeroDeIdentidad) {
		this.numeroDeIdentidad = numeroDeIdentidad;
	}

	/** 
	 * V2380K-Fecha de Embasado o Empaquetado (365) (102/103): En formato AAAAMMDD o AAAAMMDDHHMM.
	 */ 
	public String getFechaDeEnvasadoOEmpaquetado() {
		return fechaDeEnvasadoOEmpaquetado;
	}

	/** 
	 * V2380K-Fecha de Embasado o Empaquetado (365) (102/103): En formato AAAAMMDD o AAAAMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V2380K</td> <td>Fecha de Envasado o Empaquetado</td> <td>C</td> <td>12</td> <td>194</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeEnvasadoOEmpaquetado(String fechaDeEnvasadoOEmpaquetado) {
		this.fechaDeEnvasadoOEmpaquetado = fechaDeEnvasadoOEmpaquetado;
	}

	/** 
	 * V4233L-Código Instrucciones: Los valores posibles son:
	 */
	public enum V4233L {
		INSTRUCCIONES_DEL_FABRICANT_17("17"),
		MARCADO_CON_UN_CODIGO_DE_EXPEDICION_DE_SERI_33E("33E"),
		;
		
		private String value;
		
		private V4233L(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V4233L enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V6063D-Calificador de Cantidad (11/12): Los valores posibles son:
	 */
	public enum V6063D {
		CANTIDAD_DIVIDID_11("11"),
		CANTIDAD_POR_LOTE__PETICION_ALCAMPO_12("12"),
		;
		
		private String value;
		
		private V6063D(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V6063D enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V7405L-Calificador  del número de identidad: Los valores posibles son:
	 */
	public enum V7405L {
		NUMERO_EAN_UP_EU("EU"),
		N__SE_SERIE_DEL_CONTINENTE_DE_LA_EXPEDICIO_BJ("BJ"),
		N__DE_LA_EXPEDICIO_BX("BX"),
		;
		
		private String value;
		
		private V7405L(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V7405L enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}