package com.esferalia.aon.file.seres.udapa.invoice.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCV entity.
 * <br>
 * IMPORTANT: Any changes made to the code will be lost, so...
 * 
 * DO NOT CHANGE THIS!!!!!!!!!!!!!!!!
 * 
 * <table border="1" cellpadding="1" cellspacing="0">
 * 	<tr bgcolor="#CCCCFF"> 
 * 		<th>Tipo de registro</th>
 * 		<th>Descripcion</th>
 * 		<th>Tipo</th>
 * 		<th>Repeticiones</th>
 * 	</tr>
 * 	<tr>
 * 		<td>SINCV</th>
 * 		<td>Vencimientos</th>
 * 		<td>Opcional</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class SINCV {

	private String vencimientos;
	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_SU_;
	private String codigoComprador_BY_;
	private String numeroDeVencimiento;
	private String fechaVencimiento;
	private String importeSujetoAlVencimiento;


	private static java.util.regex.Pattern PATTERN_SINCV_vencimientos = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCV_tipoFactura_325_380_381_383_385_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCV_numeroDeFactura = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCV_codigoVendedor_SU_ = java.util.regex.Pattern.compile("^.{29}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCV_codigoComprador_BY_ = java.util.regex.Pattern.compile("^.{42}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCV_numeroDeVencimiento = java.util.regex.Pattern.compile("^.{55}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCV_fechaVencimiento = java.util.regex.Pattern.compile("^.{61}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_SINCV_importeSujetoAlVencimiento = java.util.regex.Pattern.compile("^.{69}(.{18}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_SINCV_vencimientos.matcher(value)).find()) {
			setVencimientos(m.group(1));
		}
		if((m = PATTERN_SINCV_tipoFactura_325_380_381_383_385_.matcher(value)).find()) {
			setTipoFactura_325_380_381_383_385_(m.group(1));
		}
		if((m = PATTERN_SINCV_numeroDeFactura.matcher(value)).find()) {
			setNumeroDeFactura(m.group(1));
		}
		if((m = PATTERN_SINCV_codigoVendedor_SU_.matcher(value)).find()) {
			setCodigoVendedor_SU_(m.group(1));
		}
		if((m = PATTERN_SINCV_codigoComprador_BY_.matcher(value)).find()) {
			setCodigoComprador_BY_(m.group(1));
		}
		if((m = PATTERN_SINCV_numeroDeVencimiento.matcher(value)).find()) {
			setNumeroDeVencimiento(m.group(1));
		}
		if((m = PATTERN_SINCV_fechaVencimiento.matcher(value)).find()) {
			setFechaVencimiento(m.group(1));
		}
		if((m = PATTERN_SINCV_importeSujetoAlVencimiento.matcher(value)).find()) {
			setImporteSujetoAlVencimiento(m.group(1));
		}
	}


	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>SINCV</th>
	 * 		<td>Vencimientos</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getVencimientos() {
		return vencimientos;
	}
	public void setVencimientos(String vencimientos) {
		this.vencimientos = vencimientos;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F1001T</th>
	 * 		<td>Tipo Factura (325, 380, 381, 383, 385)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>7</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoFactura_325_380_381_383_385_() {
		return tipoFactura_325_380_381_383_385_;
	}
	public void setTipoFactura_325_380_381_383_385_(String tipoFactura_325_380_381_383_385_) {
		this.tipoFactura_325_380_381_383_385_ = tipoFactura_325_380_381_383_385_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F1004N</th>
	 * 		<td>Número de Factura</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>13</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeFactura() {
		return numeroDeFactura;
	}
	public void setNumeroDeFactura(String numeroDeFactura) {
		this.numeroDeFactura = numeroDeFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F3039V</th>
	 * 		<td>Código Vendedor (SU)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>30</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoVendedor_SU_() {
		return codigoVendedor_SU_;
	}
	public void setCodigoVendedor_SU_(String codigoVendedor_SU_) {
		this.codigoVendedor_SU_ = codigoVendedor_SU_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F3039C</th>
	 * 		<td>Código Comprador (BY)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>43</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoComprador_BY_() {
		return codigoComprador_BY_;
	}
	public void setCodigoComprador_BY_(String codigoComprador_BY_) {
		this.codigoComprador_BY_ = codigoComprador_BY_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F1082V</th>
	 * 		<td>Número de Vencimiento</th>
	 * 		<td>N</th>
	 * 		<td>6</th>
	 * 		<td>56</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeVencimiento() {
		return numeroDeVencimiento;
	}
	public void setNumeroDeVencimiento(String numeroDeVencimiento) {
		this.numeroDeVencimiento = numeroDeVencimiento;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F2380D</th>
	 * 		<td>Fecha Vencimiento</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>62</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	/** 
	 * F5004C - Importe sujeto a cada Vencimiento:  La suna de los importes sujetos a cada vencimiento(F5004C) debe coincidir con el Importe Total a Pagar (F5004P) del registro de cabecera (SINCC)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F5004C</th>
	 * 		<td>Importe sujeto al vencimiento</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>70</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteSujetoAlVencimiento() {
		return importeSujetoAlVencimiento;
	}
	public void setImporteSujetoAlVencimiento(String importeSujetoAlVencimiento) {
		this.importeSujetoAlVencimiento = importeSujetoAlVencimiento;
	}

}