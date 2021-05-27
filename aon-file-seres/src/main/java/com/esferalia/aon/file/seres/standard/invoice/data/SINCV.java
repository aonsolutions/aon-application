package com.esferalia.aon.file.seres.standard.invoice.data;

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

	private Integer numeroDeVencimientos;
	private Integer fechaDeVencimiento;
	private Double importeSujetoAlVencimiento;


	private static Pattern PATTERN_SINCV_numeroDeVencimientos = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SINCV_fechaDeVencimiento = Pattern.compile("^.{12}(.{8}).*");
	private static Pattern PATTERN_SINCV_importeSujetoAlVencimiento = Pattern.compile("^.{20}(.{18}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCV_numeroDeVencimientos.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeVencimientos(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCV_fechaDeVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeVencimiento(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCV_importeSujetoAlVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteSujetoAlVencimiento(Double.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Número de Vencimiento: Es un campo contador. Se sumará 1 por cada vencimiento dentro de una misma factura. El valor inicial por cada factura es '1'.
	 */ 
	public Integer getNumeroDeVencimientos() {
		return numeroDeVencimientos;
	}

	/** 
	 * 2 - Número de Vencimiento: Es un campo contador. Se sumará 1 por cada vencimiento dentro de una misma factura. El valor inicial por cada factura es '1'.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Número de vencimientos</td> <td>N</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeVencimientos(Integer numeroDeVencimientos) {
		this.numeroDeVencimientos = numeroDeVencimientos;
	}

	/** 
	 * 
	 */ 
	public Integer getFechaDeVencimiento() {
		return fechaDeVencimiento;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Fecha de vencimiento</td> <td>N</td> <td>8</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeVencimiento(Integer fechaDeVencimiento) {
		this.fechaDeVencimiento = fechaDeVencimiento;
	}

	/** 
	 * 4 - Importe sujeto a cada Vencimiento: La suma de los importes sujetos a cada vencimiento (4) debe coincidir con el Importe Total a Pagar (22) del registro de cabecera (SINCC)
	 */ 
	public Double getImporteSujetoAlVencimiento() {
		return importeSujetoAlVencimiento;
	}

	/** 
	 * 4 - Importe sujeto a cada Vencimiento: La suma de los importes sujetos a cada vencimiento (4) debe coincidir con el Importe Total a Pagar (22) del registro de cabecera (SINCC)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Importe sujeto al vencimiento</td> <td>N(14,3)</td> <td>18</td> <td>21</td> <td>M</td>
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