package com.esferalia.aon.file.seres.connect2.salesresponse.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ORSPV {
	private Integer contadorVencimiento;
	private String referenciaDelTiempoDePago_codificado;
	private String relacionDeTiempo_codificado;
	private String tipoDePeriodo_codificado_D_M_Y_;
	private Integer numeroDePeriodos;
	private String fechaDeVencimiento;
	private Double importeDeVencimiento;
	
	private static Pattern PATTERN_ORSPV_contadorVencimiento = Pattern.compile("^.{6}(.{2}).*");
	private static Pattern PATTERN_ORSPV_referenciaDelTiempoDePago_codificado = Pattern.compile("^.{8}(.{3}).*");
	private static Pattern PATTERN_ORSPV_relacionDeTiempo_codificado = Pattern.compile("^.{11}(.{3}).*");
	private static Pattern PATTERN_ORSPV_tipoDePeriodo_codificado_D_M_Y_ = Pattern.compile("^.{14}(.{3}).*");
	private static Pattern PATTERN_ORSPV_numeroDePeriodos = Pattern.compile("^.{17}(.{3}).*");
	private static Pattern PATTERN_ORSPV_fechaDeVencimiento = Pattern.compile("^.{20}(.{8}).*");
	private static Pattern PATTERN_ORSPV_importeDeVencimiento = Pattern.compile("^.{28}(.{18}).*");
	
	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ORSPV_contadorVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setContadorVencimiento(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPV_referenciaDelTiempoDePago_codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setReferenciaDelTiempoDePago_codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPV_relacionDeTiempo_codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setRelacionDeTiempo_codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPV_tipoDePeriodo_codificado_D_M_Y_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDePeriodo_codificado_D_M_Y_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPV_numeroDePeriodos.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePeriodos(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPV_fechaDeVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeVencimiento(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPV_importeDeVencimiento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
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
	public String getReferenciaDelTiempoDePago_codificado() {
		return referenciaDelTiempoDePago_codificado;
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
	public void setReferenciaDelTiempoDePago_codificado(String referenciaDelTiempoDePago_codificado) {
		this.referenciaDelTiempoDePago_codificado = referenciaDelTiempoDePago_codificado;
	}

	/**
	 * 4 - Relación de tiempo, codificado: Este campo corresponde al elemento 2009. Los valores posibles son:
	 */
	public String getRelacionDeTiempo_codificado() {
		return relacionDeTiempo_codificado;
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
	public void setRelacionDeTiempo_codificado(String relacionDeTiempo_codificado) {
		this.relacionDeTiempo_codificado = relacionDeTiempo_codificado;
	}

	/**
	 * 5 - Tipo de período, codificado: Este campo corresponde al elemento 2151. Los valores posibles son:
	 */
	public String getTipoDePeriodo_codificado_D_M_Y_() {
		return tipoDePeriodo_codificado_D_M_Y_;
	}


	/**
	 * 5 - Tipo de período, codificado: Este campo corresponde al elemento 2151. Los valores posibles son:
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
	public void setTipoDePeriodo_codificado_D_M_Y_(String tipoDePeriodo_codificado_D_M_Y_) {
		this.tipoDePeriodo_codificado_D_M_Y_ = tipoDePeriodo_codificado_D_M_Y_;
	}

	/**
	 * 6 - Número de periodos
	 */
	public Integer getNumeroDePeriodos() {
		return numeroDePeriodos;
	}

	
	/**
	 * 6 - Número de periodos
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
	 * 7 - Fecha de vencimiento
	 */
	public String getFechaDeVencimiento() {
		return fechaDeVencimiento;
	}


	/**
	 * 7 - Fecha de vencimiento
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
	 * 8 - Importe de vencimiento
	 */
	public Double getImporteDeVencimiento() {
		return importeDeVencimiento;
	}


	/**
	 * 8 - Importe de vencimiento
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
	public enum ORSPV_3 {
		FECHA_DE_FACTURA("5"),
		FECHA_ESPECIFICADA("66"),
		FECHA_ENVIO_DE_LA_FACTURA("69"),
		FECHA_DE_ENVIO("81"),
		;
		
		private String value;
		
		private ORSPV_3(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPV_3 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 4 - Relación de tiempo, codificado: Este campo corresponde al elemento 2009. Los valores posibles son:
	 */
	public enum ORSPV_4 {
		FECHA_DE_LA_REFERENCIA("1"),
		DESPUES_DE_LA_REFERENCIA("3"),
		;
		
		private String value;
		
		private ORSPV_4(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPV_4 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 5 - Tipo de período, codificado: Este campo corresponde al elemento 2151. Los valores posibles son:
	 */
	public enum ORSPV_5 {
		DIA("D"),
		MES("M"),
		DIAS_LABORABLES("WD"),
		ANO("Y"),
		;
		
		private String value;
		
		private ORSPV_5(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ORSPV_5 enumByValue(String value) {
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