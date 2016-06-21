package com.esferalia.aon.file.seres.udapa.sales.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1V entity.
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
 * 		<td>ERE1V</th>
 * 		<td>Vencimientos</th>
 * 		<td>Opcional</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class ERE1V {

	private String vencimientos;
	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroDeVencimiento;
	private String referenciaDeTiempoDePago;
	private String relacionDeTiempo;
	private String tipoDePeriodo_D_M_Y_;
	private String numeroDePeriodos;
	private String fechaVencimiento;
	private String importeSujetoAlVencimiento;


	private static java.util.regex.Pattern PATTERN_ERE1V_vencimientos = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_tipoDePedido_220_221_224_226_22E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_numeroDePedido = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_numeroDeVencimiento = java.util.regex.Pattern.compile("^.{63}(.{2}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_referenciaDeTiempoDePago = java.util.regex.Pattern.compile("^.{65}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_relacionDeTiempo = java.util.regex.Pattern.compile("^.{68}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_tipoDePeriodo_D_M_Y_ = java.util.regex.Pattern.compile("^.{71}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_numeroDePeriodos = java.util.regex.Pattern.compile("^.{74}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_fechaVencimiento = java.util.regex.Pattern.compile("^.{77}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_ERE1V_importeSujetoAlVencimiento = java.util.regex.Pattern.compile("^.{85}(.{18}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_ERE1V_vencimientos.matcher(value)).find()) {
			setVencimientos(m.group(1));
		}
		if((m = PATTERN_ERE1V_tipoDePedido_220_221_224_226_22E_.matcher(value)).find()) {
			setTipoDePedido_220_221_224_226_22E_(m.group(1));
		}
		if((m = PATTERN_ERE1V_numeroDePedido.matcher(value)).find()) {
			setNumeroDePedido(m.group(1));
		}
		if((m = PATTERN_ERE1V_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_ERE1V_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_ERE1V_numeroDeVencimiento.matcher(value)).find()) {
			setNumeroDeVencimiento(m.group(1));
		}
		if((m = PATTERN_ERE1V_referenciaDeTiempoDePago.matcher(value)).find()) {
			setReferenciaDeTiempoDePago(m.group(1));
		}
		if((m = PATTERN_ERE1V_relacionDeTiempo.matcher(value)).find()) {
			setRelacionDeTiempo(m.group(1));
		}
		if((m = PATTERN_ERE1V_tipoDePeriodo_D_M_Y_.matcher(value)).find()) {
			setTipoDePeriodo_D_M_Y_(m.group(1));
		}
		if((m = PATTERN_ERE1V_numeroDePeriodos.matcher(value)).find()) {
			setNumeroDePeriodos(m.group(1));
		}
		if((m = PATTERN_ERE1V_fechaVencimiento.matcher(value)).find()) {
			setFechaVencimiento(m.group(1));
		}
		if((m = PATTERN_ERE1V_importeSujetoAlVencimiento.matcher(value)).find()) {
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
	 * 		<td>ERE1V</th>
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
	 * 		<td>C1001T</th>
	 * 		<td>Tipo de Pedido (220, 221, 224, 226, 22E)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>7</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoDePedido_220_221_224_226_22E_() {
		return tipoDePedido_220_221_224_226_22E_;
	}
	public void setTipoDePedido_220_221_224_226_22E_(String tipoDePedido_220_221_224_226_22E_) {
		this.tipoDePedido_220_221_224_226_22E_ = tipoDePedido_220_221_224_226_22E_;
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
	 * 		<td>C1004P</th>
	 * 		<td>Número de Pedido</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>13</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDePedido() {
		return numeroDePedido;
	}
	public void setNumeroDePedido(String numeroDePedido) {
		this.numeroDePedido = numeroDePedido;
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
	 * 		<td>C3039E</th>
	 * 		<td>Código Emisor  (MS)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>30</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoEmisor_MS_() {
		return codigoEmisor_MS_;
	}
	public void setCodigoEmisor_MS_(String codigoEmisor_MS_) {
		this.codigoEmisor_MS_ = codigoEmisor_MS_;
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
	 * 		<td>C3039R</th>
	 * 		<td>Código Receptor (MR)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>47</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoReceptor_MR_() {
		return codigoReceptor_MR_;
	}
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
	}

	/** 
	 * C1082V - Número de Vencimiento: Es un campo contador. Se sumará 1 por cada vencimiento dentro de un mismo pedido. El valor inicial por cada pedido es '1'.
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
	 * 		<td>C1082V</th>
	 * 		<td>Número de Vencimiento</th>
	 * 		<td>N</th>
	 * 		<td>2</th>
	 * 		<td>64</th>
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
	 * C2475V - Referencia de Tiempo de Pago: Los valores posibles son:
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
	 * 		<td>C2475V</th>
	 * 		<td>Referencia de Tiempo de Pago</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>66</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getReferenciaDeTiempoDePago() {
		return referenciaDeTiempoDePago;
	}
	public void setReferenciaDeTiempoDePago(String referenciaDeTiempoDePago) {
		this.referenciaDeTiempoDePago = referenciaDeTiempoDePago;
	}

	/** 
	 * C2009V - Relación de Tiempo: Los valores posibles son:
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
	 * 		<td>C2009V</th>
	 * 		<td>Relación de Tiempo</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>69</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getRelacionDeTiempo() {
		return relacionDeTiempo;
	}
	public void setRelacionDeTiempo(String relacionDeTiempo) {
		this.relacionDeTiempo = relacionDeTiempo;
	}

	/** 
	 * C2151V - Tipo de Período (D/M/Y): Los valores posibles son:
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
	 * 		<td>C2151V</th>
	 * 		<td>Tipo de Periodo (D/M/Y)</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>72</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoDePeriodo_D_M_Y_() {
		return tipoDePeriodo_D_M_Y_;
	}
	public void setTipoDePeriodo_D_M_Y_(String tipoDePeriodo_D_M_Y_) {
		this.tipoDePeriodo_D_M_Y_ = tipoDePeriodo_D_M_Y_;
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
	 * 		<td>C2152V</th>
	 * 		<td>Número de Periodos</th>
	 * 		<td>N</th>
	 * 		<td>3</th>
	 * 		<td>75</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDePeriodos() {
		return numeroDePeriodos;
	}
	public void setNumeroDePeriodos(String numeroDePeriodos) {
		this.numeroDePeriodos = numeroDePeriodos;
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
	 * 		<td>C2380P</th>
	 * 		<td>Fecha Vencimiento</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>78</th>
	 * 		<td>C</th>
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
	 * C5004C - Importe sujeto al vencimiento: La suma de los importes sujetos a cada vencimiento (C5004C) debe coincidir con el Importe Total a Pagar (C5004P) del fichero de cabecera (ERE1C)
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
	 * 		<td>C5004C</th>
	 * 		<td>Importe sujeto al vencimiento</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>86</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteSujetoAlVencimiento() {
		return importeSujetoAlVencimiento;
	}
	public void setImporteSujetoAlVencimiento(String importeSujetoAlVencimiento) {
		this.importeSujetoAlVencimiento = importeSujetoAlVencimiento;
	}

	public enum ReferenciaDeTiempoDePago {
		DATE_OF_INVOIC_5("5"),
		SPECIFIED_DAT_66("66"),
		INVOICE_TRANSMISSION_DAT_69("69"),
		DATE_OF_SHIPMENT_AS_EVIDENCED_BY_THE_TRANSPORT_DOCUMENT__81("81"),
		;
		
		private String value;
		
		private ReferenciaDeTiempoDePago(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum RelacionDeTiempo {
		FECHA_DE_LA_REFERENCI_1("1"),
		DESPUES_DE_LA_REFERENCI_3("3"),
		;
		
		private String value;
		
		private RelacionDeTiempo(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum TipoDePeriodo_D_M_Y_ {
		DA_D("D"),
		MONT_M("M"),
		WORKING_DAY_WD("WD"),
		YEA_Y("Y"),
		;
		
		private String value;
		
		private TipoDePeriodo_D_M_Y_(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}