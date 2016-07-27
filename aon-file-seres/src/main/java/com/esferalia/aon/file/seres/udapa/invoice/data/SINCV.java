package com.esferalia.aon.file.seres.udapa.invoice.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCV entity.
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
 * 		 <td>SINCV</td> <td>Vencimientos</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SINCV {

	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_SU_;
	private String codigoComprador_BY_;
	private Integer numeroDeVencimiento;
	private Integer fechaVencimiento;
	private Double importeSujetoAlVencimiento;


	private static Pattern PATTERN_SINCV_tipoFactura_325_380_381_383_385_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SINCV_numeroDeFactura = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SINCV_codigoVendedor_SU_ = Pattern.compile("^.{29}(.{13}).*");
	private static Pattern PATTERN_SINCV_codigoComprador_BY_ = Pattern.compile("^.{42}(.{13}).*");
	private static Pattern PATTERN_SINCV_numeroDeVencimiento = Pattern.compile("^.{55}(.{6}).*");
	private static Pattern PATTERN_SINCV_fechaVencimiento = Pattern.compile("^.{61}(.{8}).*");
	private static Pattern PATTERN_SINCV_importeSujetoAlVencimiento = Pattern.compile("^.{69}(.{18}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCV_tipoFactura_325_380_381_383_385_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoFactura_325_380_381_383_385_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCV_numeroDeFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCV_codigoVendedor_SU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoVendedor_SU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCV_codigoComprador_BY_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoComprador_BY_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCV_numeroDeVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeVencimiento(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCV_fechaVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaVencimiento(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCV_importeSujetoAlVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteSujetoAlVencimiento(Double.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 
	 */ 
	public String getTipoFactura_325_380_381_383_385_() {
		return tipoFactura_325_380_381_383_385_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1001T</td> <td>Tipo Factura (325, 380, 381, 383, 385)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoFactura_325_380_381_383_385_(String tipoFactura_325_380_381_383_385_) {
		this.tipoFactura_325_380_381_383_385_ = tipoFactura_325_380_381_383_385_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeFactura() {
		return numeroDeFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1004N</td> <td>Número de Factura</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeFactura(String numeroDeFactura) {
		this.numeroDeFactura = numeroDeFactura;
	}

	/** 
	 * 
	 */ 
	public String getCodigoVendedor_SU_() {
		return codigoVendedor_SU_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039V</td> <td>Código Vendedor (SU)</td> <td>C</td> <td>13</td> <td>30</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoVendedor_SU_(String codigoVendedor_SU_) {
		this.codigoVendedor_SU_ = codigoVendedor_SU_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoComprador_BY_() {
		return codigoComprador_BY_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039C</td> <td>Código Comprador (BY)</td> <td>C</td> <td>13</td> <td>43</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoComprador_BY_(String codigoComprador_BY_) {
		this.codigoComprador_BY_ = codigoComprador_BY_;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeVencimiento() {
		return numeroDeVencimiento;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1082V</td> <td>Número de Vencimiento</td> <td>N</td> <td>6</td> <td>56</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeVencimiento(Integer numeroDeVencimiento) {
		this.numeroDeVencimiento = numeroDeVencimiento;
	}

	/** 
	 * 
	 */ 
	public Integer getFechaVencimiento() {
		return fechaVencimiento;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F2380D</td> <td>Fecha Vencimiento</td> <td>N</td> <td>8</td> <td>62</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaVencimiento(Integer fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	/** 
	 * F5004C - Importe sujeto a cada Vencimiento:  La suna de los importes sujetos a cada vencimiento(F5004C) debe coincidir con el Importe Total a Pagar (F5004P) del registro de cabecera (SINCC)
	 */ 
	public Double getImporteSujetoAlVencimiento() {
		return importeSujetoAlVencimiento;
	}

	/** 
	 * F5004C - Importe sujeto a cada Vencimiento:  La suna de los importes sujetos a cada vencimiento(F5004C) debe coincidir con el Importe Total a Pagar (F5004P) del registro de cabecera (SINCC)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004C</td> <td>Importe sujeto al vencimiento</td> <td>N(14,3)</td> <td>18</td> <td>70</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteSujetoAlVencimiento(Double importeSujetoAlVencimiento) {
		this.importeSujetoAlVencimiento = importeSujetoAlVencimiento;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}