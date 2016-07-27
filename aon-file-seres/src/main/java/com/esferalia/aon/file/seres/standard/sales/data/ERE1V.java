package com.esferalia.aon.file.seres.standard.sales.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1V entity.
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
 * 		 <td>ERE1V</td> <td>Vencimientos</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class ERE1V {

	private Integer contadorVencimiento;
	private String referenciaDelTiempoDePago_Codificado;
	private String relacionDeTiempo_Codificado;
	private String tipoDePeriodo_Codificado_D_M_Y_;
	private Integer numeroDePeriodos;
	private String fechaDeVencimiento;
	private Double importeDeVencimiento;


	private static Pattern PATTERN_ERE1V_contadorVencimiento = Pattern.compile("^.{6}(.{2}).*");
	private static Pattern PATTERN_ERE1V_referenciaDelTiempoDePago_Codificado = Pattern.compile("^.{8}(.{3}).*");
	private static Pattern PATTERN_ERE1V_relacionDeTiempo_Codificado = Pattern.compile("^.{11}(.{3}).*");
	private static Pattern PATTERN_ERE1V_tipoDePeriodo_Codificado_D_M_Y_ = Pattern.compile("^.{14}(.{3}).*");
	private static Pattern PATTERN_ERE1V_numeroDePeriodos = Pattern.compile("^.{17}(.{3}).*");
	private static Pattern PATTERN_ERE1V_fechaDeVencimiento = Pattern.compile("^.{20}(.{8}).*");
	private static Pattern PATTERN_ERE1V_importeDeVencimiento = Pattern.compile("^.{28}(.{18}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ERE1V_contadorVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setContadorVencimiento(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1V_referenciaDelTiempoDePago_Codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setReferenciaDelTiempoDePago_Codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1V_relacionDeTiempo_Codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setRelacionDeTiempo_Codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1V_tipoDePeriodo_Codificado_D_M_Y_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDePeriodo_Codificado_D_M_Y_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1V_numeroDePeriodos.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePeriodos(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1V_fechaDeVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeVencimiento(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1V_importeDeVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteDeVencimiento(Double.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Contador vencimiento: Este campo es un contador. Se recomienda utilizar contador secuencial.
	 */ 
	public Integer getContadorVencimiento() {
		return contadorVencimiento;
	}

	/** 
	 * 2 - Contador vencimiento: Este campo es un contador. Se recomienda utilizar contador secuencial.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Contador vencimiento</td> <td>N</td> <td>2</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setContadorVencimiento(Integer contadorVencimiento) {
		this.contadorVencimiento = contadorVencimiento;
	}

	/** 
	 * 3 - Referencia del tiempo de pago, codificado: Código para especificar la fecha de referencia del tiempo de pago. Este campo corresponde al elemento 2475. Los valores posibles son:
	 */ 
	public String getReferenciaDelTiempoDePago_Codificado() {
		return referenciaDelTiempoDePago_Codificado;
	}

	/** 
	 * 3 - Referencia del tiempo de pago, codificado: Código para especificar la fecha de referencia del tiempo de pago. Este campo corresponde al elemento 2475. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Referencia del tiempo de pago, codificado</td> <td>C</td> <td>3</td> <td>9</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setReferenciaDelTiempoDePago_Codificado(String referenciaDelTiempoDePago_Codificado) {
		this.referenciaDelTiempoDePago_Codificado = referenciaDelTiempoDePago_Codificado;
	}

	/** 
	 * 4 - Relación de tiempo, codificado: Este campo corresponde al elemento 2009. Los valores posibles son:
	 */ 
	public String getRelacionDeTiempo_Codificado() {
		return relacionDeTiempo_Codificado;
	}

	/** 
	 * 4 - Relación de tiempo, codificado: Este campo corresponde al elemento 2009. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Relación de tiempo, codificado</td> <td>C</td> <td>3</td> <td>12</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setRelacionDeTiempo_Codificado(String relacionDeTiempo_Codificado) {
		this.relacionDeTiempo_Codificado = relacionDeTiempo_Codificado;
	}

	/** 
	 * 5 - Fecha de factura
	 */ 
	public String getTipoDePeriodo_Codificado_D_M_Y_() {
		return tipoDePeriodo_Codificado_D_M_Y_;
	}

	/** 
	 * 5 - Fecha de factura
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Tipo de período, codificado (D/M/Y)</td> <td>C</td> <td>3</td> <td>15</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDePeriodo_Codificado_D_M_Y_(String tipoDePeriodo_Codificado_D_M_Y_) {
		this.tipoDePeriodo_Codificado_D_M_Y_ = tipoDePeriodo_Codificado_D_M_Y_;
	}

	/** 
	 * 66 - Fecha especificada
	 */ 
	public Integer getNumeroDePeriodos() {
		return numeroDePeriodos;
	}

	/** 
	 * 66 - Fecha especificada
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Número de periodos</td> <td>N</td> <td>3</td> <td>18</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePeriodos(Integer numeroDePeriodos) {
		this.numeroDePeriodos = numeroDePeriodos;
	}

	/** 
	 * 
	 */ 
	public String getFechaDeVencimiento() {
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
	 * 		 <td>7</td> <td>Fecha de vencimiento</td> <td>C</td> <td>8</td> <td>21</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeVencimiento(String fechaDeVencimiento) {
		this.fechaDeVencimiento = fechaDeVencimiento;
	}

	/** 
	 * 81 - Fecha de envío
	 */ 
	public Double getImporteDeVencimiento() {
		return importeDeVencimiento;
	}

	/** 
	 * 81 - Fecha de envío
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Importe de vencimiento</td> <td>N(14,3)</td> <td>18</td> <td>29</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteDeVencimiento(Double importeDeVencimiento) {
		this.importeDeVencimiento = importeDeVencimiento;
	}

	/** 
	 * 3 - Referencia del tiempo de pago, codificado: Código para especificar la fecha de referencia del tiempo de pago. Este campo corresponde al elemento 2475. Los valores posibles son:
	 */
	public enum ERE1V_3 {
		FECHA_DE_FACTUR_5("5"),
		FECHA_ESPECIFICAD_66("66"),
		FECHA_ENVIO_DE_LA_FACTUR_69("69"),
		FECHA_DE_ENVI_81("81"),
		;
		
		private String value;
		
		private ERE1V_3(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1V_3 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 4 - Relación de tiempo, codificado: Este campo corresponde al elemento 2009. Los valores posibles son:
	 */
	public enum ERE1V_4 {
		FECHA_DE_LA_REFERENCI_1("1"),
		DESPUES_DE_LA_REFERENCI_3("3"),
		;
		
		private String value;
		
		private ERE1V_4(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1V_4 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 5 - Tipo de período, codificado: Este campo corresponde al elemento 2151. Los valores posibles son:
	 */
	public enum ERE1V_5 {
		DI_D("D"),
		ME_M("M"),
		DIAS_LABORABLE_WD("WD"),
		A__Y("Y"),
		;
		
		private String value;
		
		private ERE1V_5(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1V_5 enumByValue(String value) {
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