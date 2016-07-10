package com.esferalia.aon.file.seres.standard.delivery.data;

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
 * 		 <td>SEH1B</td> <td>Información de lotes</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SEH1B {

	private String codigoInstrucciones;
	private String marcasDeEnvio;
	private String fechaDeCaducidad_36__102_203_;
	private String fecha_horaRecepcionDeLaMercancia_50__102_203_;
	private String consumirAntesDeFecha_361__102_203_;
	private String calificadorDeCantidad_11_12_;
	private Double cantidad;
	private String calificadorNumeroIdentidad;
	private String numeroIdentidad;
	private String fechaDeEnvasadoOEmpaquetado_365__102_203_;
	private String fechaProduccion_fabricacion_94__102_203_;


	private static Pattern PATTERN_SEH1B_codigoInstrucciones = Pattern.compile("^.{6}(.{3}).*");
	private static Pattern PATTERN_SEH1B_marcasDeEnvio = Pattern.compile("^.{9}(.{35}).*");
	private static Pattern PATTERN_SEH1B_fechaDeCaducidad_36__102_203_ = Pattern.compile("^.{44}(.{12}).*");
	private static Pattern PATTERN_SEH1B_fecha_horaRecepcionDeLaMercancia_50__102_203_ = Pattern.compile("^.{56}(.{12}).*");
	private static Pattern PATTERN_SEH1B_consumirAntesDeFecha_361__102_203_ = Pattern.compile("^.{68}(.{12}).*");
	private static Pattern PATTERN_SEH1B_calificadorDeCantidad_11_12_ = Pattern.compile("^.{80}(.{3}).*");
	private static Pattern PATTERN_SEH1B_cantidad = Pattern.compile("^.{83}(.{16}).*");
	private static Pattern PATTERN_SEH1B_calificadorNumeroIdentidad = Pattern.compile("^.{99}(.{3}).*");
	private static Pattern PATTERN_SEH1B_numeroIdentidad = Pattern.compile("^.{102}(.{35}).*");
	private static Pattern PATTERN_SEH1B_fechaDeEnvasadoOEmpaquetado_365__102_203_ = Pattern.compile("^.{137}(.{12}).*");
	private static Pattern PATTERN_SEH1B_fechaProduccion_fabricacion_94__102_203_ = Pattern.compile("^.{149}(.{12}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SEH1B_codigoInstrucciones.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInstrucciones(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_marcasDeEnvio.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcasDeEnvio(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_fechaDeCaducidad_36__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeCaducidad_36__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_fecha_horaRecepcionDeLaMercancia_50__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaRecepcionDeLaMercancia_50__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_consumirAntesDeFecha_361__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setConsumirAntesDeFecha_361__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_calificadorDeCantidad_11_12_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDeCantidad_11_12_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_cantidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidad(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_calificadorNumeroIdentidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorNumeroIdentidad(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_numeroIdentidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroIdentidad(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_fechaDeEnvasadoOEmpaquetado_365__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeEnvasadoOEmpaquetado_365__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1B_fechaProduccion_fabricacion_94__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaProduccion_fabricacion_94__102_203_(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Código Instrucciones: Código indicando las instrucciones de como los embalajes o unidades físicas deben ser marcados. Este campo se corresponde con el elemento 4233. Los posibles valores son:
	 */ 
	public String getCodigoInstrucciones() {
		return codigoInstrucciones;
	}

	/** 
	 * 2 - Código Instrucciones: Código indicando las instrucciones de como los embalajes o unidades físicas deben ser marcados. Este campo se corresponde con el elemento 4233. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Código Instrucciones</td> <td>C</td> <td>3</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoInstrucciones(String codigoInstrucciones) {
		this.codigoInstrucciones = codigoInstrucciones;
	}

	/** 
	 * 33E - Marcado con un código de seriado (Código EAN)
	 */ 
	public String getMarcasDeEnvio() {
		return marcasDeEnvio;
	}

	/** 
	 * 33E - Marcado con un código de seriado (Código EAN)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Marcas de envío</td> <td>C</td> <td>35</td> <td>10</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcasDeEnvio(String marcasDeEnvio) {
		this.marcasDeEnvio = marcasDeEnvio;
	}

	/** 
	 * 4,5,6,11,12 - Fecha/Hora: El formato aceptado para fecha/hora es el de CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFechaDeCaducidad_36__102_203_() {
		return fechaDeCaducidad_36__102_203_;
	}

	/** 
	 * 4,5,6,11,12 - Fecha/Hora: El formato aceptado para fecha/hora es el de CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Fecha de caducidad (36) (102/203)</td> <td>C</td> <td>12</td> <td>45</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeCaducidad_36__102_203_(String fechaDeCaducidad_36__102_203_) {
		this.fechaDeCaducidad_36__102_203_ = fechaDeCaducidad_36__102_203_;
	}

	/** 
	 * 4,5,6,11,12 - Fecha/Hora: El formato aceptado para fecha/hora es el de CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaRecepcionDeLaMercancia_50__102_203_() {
		return fecha_horaRecepcionDeLaMercancia_50__102_203_;
	}

	/** 
	 * 4,5,6,11,12 - Fecha/Hora: El formato aceptado para fecha/hora es el de CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Fecha/hora recepción de la mercancía (50) (102/203)</td> <td>C</td> <td>12</td> <td>57</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaRecepcionDeLaMercancia_50__102_203_(String fecha_horaRecepcionDeLaMercancia_50__102_203_) {
		this.fecha_horaRecepcionDeLaMercancia_50__102_203_ = fecha_horaRecepcionDeLaMercancia_50__102_203_;
	}

	/** 
	 * 36E - Marcado con un número de lote
	 */ 
	public String getConsumirAntesDeFecha_361__102_203_() {
		return consumirAntesDeFecha_361__102_203_;
	}

	/** 
	 * 36E - Marcado con un número de lote
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Consumir antes de fecha (361) (102/203)</td> <td>C</td> <td>12</td> <td>69</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setConsumirAntesDeFecha_361__102_203_(String consumirAntesDeFecha_361__102_203_) {
		this.consumirAntesDeFecha_361__102_203_ = consumirAntesDeFecha_361__102_203_;
	}

	/** 
	 * 17 - Instrucciones del proveedor
	 */ 
	public String getCalificadorDeCantidad_11_12_() {
		return calificadorDeCantidad_11_12_;
	}

	/** 
	 * 17 - Instrucciones del proveedor
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Calificador de cantidad (11/12)</td> <td>C</td> <td>3</td> <td>81</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDeCantidad_11_12_(String calificadorDeCantidad_11_12_) {
		this.calificadorDeCantidad_11_12_ = calificadorDeCantidad_11_12_;
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
	 * 		 <td>8</td> <td>Cantidad</td> <td>N(12,3)</td> <td>16</td> <td>84</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	/** 
	 * 9 - Calificador número identidad: Código que especifica el tipo/fuente de la identificación de número. Este campo se corresponde con el elemento 7405. Los posibles valores son:
	 */ 
	public String getCalificadorNumeroIdentidad() {
		return calificadorNumeroIdentidad;
	}

	/** 
	 * 9 - Calificador número identidad: Código que especifica el tipo/fuente de la identificación de número. Este campo se corresponde con el elemento 7405. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Calificador número identidad</td> <td>C</td> <td>3</td> <td>100</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorNumeroIdentidad(String calificadorNumeroIdentidad) {
		this.calificadorNumeroIdentidad = calificadorNumeroIdentidad;
	}

	/** 
	 * 10 - Número de lote
	 */ 
	public String getNumeroIdentidad() {
		return numeroIdentidad;
	}

	/** 
	 * 10 - Número de lote
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Número identidad</td> <td>C</td> <td>35</td> <td>103</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroIdentidad(String numeroIdentidad) {
		this.numeroIdentidad = numeroIdentidad;
	}

	/** 
	 * 11 - Cantidad dividida
	 */ 
	public String getFechaDeEnvasadoOEmpaquetado_365__102_203_() {
		return fechaDeEnvasadoOEmpaquetado_365__102_203_;
	}

	/** 
	 * 11 - Cantidad dividida
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Fecha de Envasado o Empaquetado (365) (102/203)</td> <td>C</td> <td>12</td> <td>138</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeEnvasadoOEmpaquetado_365__102_203_(String fechaDeEnvasadoOEmpaquetado_365__102_203_) {
		this.fechaDeEnvasadoOEmpaquetado_365__102_203_ = fechaDeEnvasadoOEmpaquetado_365__102_203_;
	}

	/** 
	 * 12 - Cantidad enviada
	 */ 
	public String getFechaProduccion_fabricacion_94__102_203_() {
		return fechaProduccion_fabricacion_94__102_203_;
	}

	/** 
	 * 12 - Cantidad enviada
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Fecha producción/fabricación (94) (102/203)</td> <td>C</td> <td>12</td> <td>150</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaProduccion_fabricacion_94__102_203_(String fechaProduccion_fabricacion_94__102_203_) {
		this.fechaProduccion_fabricacion_94__102_203_ = fechaProduccion_fabricacion_94__102_203_;
	}

}