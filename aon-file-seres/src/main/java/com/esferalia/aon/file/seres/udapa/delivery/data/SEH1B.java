package com.esferalia.aon.file.seres.udapa.delivery.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1B entity.
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
 * 		<td>SEH1B</th>
 * 		<td>Información de Lotes</th>
 * 		<td>Opcional</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class SEH1B {

	private String informacionDeLotes;
	private String tipoAvisoDeExpedicion_351_35E_;
	private String numeroAvisoDeExpedicion;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroDeJerarquiaDeEmbalaje;
	private String numeroDeSub_jerarquiaDeEmbalaje;
	private String numeroDeLineaArticulo;
	private String contadorInformacionDeLotes;
	private String codigoInstrucciones;
	private String fechaDeCaducidad_36__102_203_;
	private String fechaRecepcionDeMercancias_50__102_203_;
	private String mejorAntesDeFecha_361__102_203_;
	private String calificadorCantidad_11_12_;
	private String cantidad;
	private String calificadorDelNumeroDeIdentidad;
	private String numeroDeIdentidad;
	private String fechaDeEnvasadoOEmpaquetado;


	private static java.util.regex.Pattern PATTERN_SEH1B_informacionDeLotes = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_tipoAvisoDeExpedicion_351_35E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_numeroAvisoDeExpedicion = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_numeroDeJerarquiaDeEmbalaje = java.util.regex.Pattern.compile("^.{63}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_numeroDeSub_jerarquiaDeEmbalaje = java.util.regex.Pattern.compile("^.{75}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_numeroDeLineaArticulo = java.util.regex.Pattern.compile("^.{87}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_contadorInformacionDeLotes = java.util.regex.Pattern.compile("^.{93}(.{4}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_codigoInstrucciones = java.util.regex.Pattern.compile("^.{97}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_fechaDeCaducidad_36__102_203_ = java.util.regex.Pattern.compile("^.{100}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_fechaRecepcionDeMercancias_50__102_203_ = java.util.regex.Pattern.compile("^.{112}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_mejorAntesDeFecha_361__102_203_ = java.util.regex.Pattern.compile("^.{124}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_calificadorCantidad_11_12_ = java.util.regex.Pattern.compile("^.{136}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_cantidad = java.util.regex.Pattern.compile("^.{139}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_calificadorDelNumeroDeIdentidad = java.util.regex.Pattern.compile("^.{155}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_numeroDeIdentidad = java.util.regex.Pattern.compile("^.{158}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1B_fechaDeEnvasadoOEmpaquetado = java.util.regex.Pattern.compile("^.{193}(.{12}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_SEH1B_informacionDeLotes.matcher(value)).find()) {
			setInformacionDeLotes(m.group(1));
		}
		if((m = PATTERN_SEH1B_tipoAvisoDeExpedicion_351_35E_.matcher(value)).find()) {
			setTipoAvisoDeExpedicion_351_35E_(m.group(1));
		}
		if((m = PATTERN_SEH1B_numeroAvisoDeExpedicion.matcher(value)).find()) {
			setNumeroAvisoDeExpedicion(m.group(1));
		}
		if((m = PATTERN_SEH1B_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_SEH1B_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_SEH1B_numeroDeJerarquiaDeEmbalaje.matcher(value)).find()) {
			setNumeroDeJerarquiaDeEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1B_numeroDeSub_jerarquiaDeEmbalaje.matcher(value)).find()) {
			setNumeroDeSub_jerarquiaDeEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1B_numeroDeLineaArticulo.matcher(value)).find()) {
			setNumeroDeLineaArticulo(m.group(1));
		}
		if((m = PATTERN_SEH1B_contadorInformacionDeLotes.matcher(value)).find()) {
			setContadorInformacionDeLotes(m.group(1));
		}
		if((m = PATTERN_SEH1B_codigoInstrucciones.matcher(value)).find()) {
			setCodigoInstrucciones(m.group(1));
		}
		if((m = PATTERN_SEH1B_fechaDeCaducidad_36__102_203_.matcher(value)).find()) {
			setFechaDeCaducidad_36__102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1B_fechaRecepcionDeMercancias_50__102_203_.matcher(value)).find()) {
			setFechaRecepcionDeMercancias_50__102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1B_mejorAntesDeFecha_361__102_203_.matcher(value)).find()) {
			setMejorAntesDeFecha_361__102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1B_calificadorCantidad_11_12_.matcher(value)).find()) {
			setCalificadorCantidad_11_12_(m.group(1));
		}
		if((m = PATTERN_SEH1B_cantidad.matcher(value)).find()) {
			setCantidad(m.group(1));
		}
		if((m = PATTERN_SEH1B_calificadorDelNumeroDeIdentidad.matcher(value)).find()) {
			setCalificadorDelNumeroDeIdentidad(m.group(1));
		}
		if((m = PATTERN_SEH1B_numeroDeIdentidad.matcher(value)).find()) {
			setNumeroDeIdentidad(m.group(1));
		}
		if((m = PATTERN_SEH1B_fechaDeEnvasadoOEmpaquetado.matcher(value)).find()) {
			setFechaDeEnvasadoOEmpaquetado(m.group(1));
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
	 * 		<td>SEH1B</th>
	 * 		<td>Información de Lotes</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getInformacionDeLotes() {
		return informacionDeLotes;
	}
	public void setInformacionDeLotes(String informacionDeLotes) {
		this.informacionDeLotes = informacionDeLotes;
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
	 * 		<td>V1001T</th>
	 * 		<td>Tipo Aviso de Expedición (351/35E)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>7</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoAvisoDeExpedicion_351_35E_() {
		return tipoAvisoDeExpedicion_351_35E_;
	}
	public void setTipoAvisoDeExpedicion_351_35E_(String tipoAvisoDeExpedicion_351_35E_) {
		this.tipoAvisoDeExpedicion_351_35E_ = tipoAvisoDeExpedicion_351_35E_;
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
	 * 		<td>V1004P</th>
	 * 		<td>Número Aviso de Expedición</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>13</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroAvisoDeExpedicion() {
		return numeroAvisoDeExpedicion;
	}
	public void setNumeroAvisoDeExpedicion(String numeroAvisoDeExpedicion) {
		this.numeroAvisoDeExpedicion = numeroAvisoDeExpedicion;
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
	 * 		<td>V3039E</th>
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
	 * 		<td>V3039R</th>
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
	 * 		<td>V7164J</th>
	 * 		<td>Número de Jerarquía de Embalaje</th>
	 * 		<td>N</th>
	 * 		<td>12</th>
	 * 		<td>64</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeJerarquiaDeEmbalaje() {
		return numeroDeJerarquiaDeEmbalaje;
	}
	public void setNumeroDeJerarquiaDeEmbalaje(String numeroDeJerarquiaDeEmbalaje) {
		this.numeroDeJerarquiaDeEmbalaje = numeroDeJerarquiaDeEmbalaje;
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
	 * 		<td>V7166J</th>
	 * 		<td>Número de Sub-jerarquía de Embalaje</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>76</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeSub_jerarquiaDeEmbalaje() {
		return numeroDeSub_jerarquiaDeEmbalaje;
	}
	public void setNumeroDeSub_jerarquiaDeEmbalaje(String numeroDeSub_jerarquiaDeEmbalaje) {
		this.numeroDeSub_jerarquiaDeEmbalaje = numeroDeSub_jerarquiaDeEmbalaje;
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
	 * 		<td>V1082L</th>
	 * 		<td>Número de Línea Artículo</th>
	 * 		<td>N</th>
	 * 		<td>6</th>
	 * 		<td>88</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}
	public void setNumeroDeLineaArticulo(String numeroDeLineaArticulo) {
		this.numeroDeLineaArticulo = numeroDeLineaArticulo;
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
	 * 		<td>V1082B</th>
	 * 		<td>Contador Información de Lotes</th>
	 * 		<td>N</th>
	 * 		<td>4</th>
	 * 		<td>94</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getContadorInformacionDeLotes() {
		return contadorInformacionDeLotes;
	}
	public void setContadorInformacionDeLotes(String contadorInformacionDeLotes) {
		this.contadorInformacionDeLotes = contadorInformacionDeLotes;
	}

	/** 
	 * V4233L-Código Instrucciones: Los valores posibles son:
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
	 * 		<td>V4233L</th>
	 * 		<td>Código Instrucciones</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>98</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoInstrucciones() {
		return codigoInstrucciones;
	}
	public void setCodigoInstrucciones(String codigoInstrucciones) {
		this.codigoInstrucciones = codigoInstrucciones;
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
	 * 		<td>V2380M</th>
	 * 		<td>Fecha de Caducidad (36) (102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>101</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeCaducidad_36__102_203_() {
		return fechaDeCaducidad_36__102_203_;
	}
	public void setFechaDeCaducidad_36__102_203_(String fechaDeCaducidad_36__102_203_) {
		this.fechaDeCaducidad_36__102_203_ = fechaDeCaducidad_36__102_203_;
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
	 * 		<td>V2380R</th>
	 * 		<td>Fecha Recepción de Mercancías (50) (102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>113</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaRecepcionDeMercancias_50__102_203_() {
		return fechaRecepcionDeMercancias_50__102_203_;
	}
	public void setFechaRecepcionDeMercancias_50__102_203_(String fechaRecepcionDeMercancias_50__102_203_) {
		this.fechaRecepcionDeMercancias_50__102_203_ = fechaRecepcionDeMercancias_50__102_203_;
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
	 * 		<td>V2380B</th>
	 * 		<td>Mejor antes de fecha (361) (102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>125</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getMejorAntesDeFecha_361__102_203_() {
		return mejorAntesDeFecha_361__102_203_;
	}
	public void setMejorAntesDeFecha_361__102_203_(String mejorAntesDeFecha_361__102_203_) {
		this.mejorAntesDeFecha_361__102_203_ = mejorAntesDeFecha_361__102_203_;
	}

	/** 
	 * V6063D-Calificador de Cantidad (11/12): Los valores posibles son:
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
	 * 		<td>V6063D</th>
	 * 		<td>Calificador cantidad  (11/12)</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>137</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorCantidad_11_12_() {
		return calificadorCantidad_11_12_;
	}
	public void setCalificadorCantidad_11_12_(String calificadorCantidad_11_12_) {
		this.calificadorCantidad_11_12_ = calificadorCantidad_11_12_;
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
	 * 		<td>V6060D</th>
	 * 		<td>Cantidad</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>140</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCantidad() {
		return cantidad;
	}
	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

	/** 
	 * V7405L-Calificador  del número de identidad: Los valores posibles son:
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
	 * 		<td>V7405L</th>
	 * 		<td>Calificador  del número de identidad</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>156</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorDelNumeroDeIdentidad() {
		return calificadorDelNumeroDeIdentidad;
	}
	public void setCalificadorDelNumeroDeIdentidad(String calificadorDelNumeroDeIdentidad) {
		this.calificadorDelNumeroDeIdentidad = calificadorDelNumeroDeIdentidad;
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
	 * 		<td>V7402L</th>
	 * 		<td>Número de Identidad</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>159</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeIdentidad() {
		return numeroDeIdentidad;
	}
	public void setNumeroDeIdentidad(String numeroDeIdentidad) {
		this.numeroDeIdentidad = numeroDeIdentidad;
	}

	/** 
	 * V2380K-Fecha de Embasado o Empaquetado (365) (102/103): En formato AAAAMMDD o AAAAMMDDHHMM.
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
	 * 		<td>V2380K</th>
	 * 		<td>Fecha de Envasado o Empaquetado</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>194</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeEnvasadoOEmpaquetado() {
		return fechaDeEnvasadoOEmpaquetado;
	}
	public void setFechaDeEnvasadoOEmpaquetado(String fechaDeEnvasadoOEmpaquetado) {
		this.fechaDeEnvasadoOEmpaquetado = fechaDeEnvasadoOEmpaquetado;
	}

	public enum CodigoInstrucciones {
		INSTRUCCIONES_DEL_FABRICANT_17("17"),
		MARCADO_CON_UN_CODIGO_DE_EXPEDICION_DE_SERI_33E("33E"),
		;
		
		private String value;
		
		private CodigoInstrucciones(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CalificadorDeCantidad_11_12_ {
		CANTIDAD_DIVIDID_11("11"),
		CANTIDAD_POR_LOTE__PETICION_ALCAMPO_12("12"),
		;
		
		private String value;
		
		private CalificadorDeCantidad_11_12_(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CalificadorDelNumeroDeIdentidad {
		NUMERO_EAN_UP_EU("EU"),
		N__SE_SERIE_DEL_CONTINENTE_DE_LA_EXPEDICIO_BJ("BJ"),
		N__DE_LA_EXPEDICIO_BX("BX"),
		;
		
		private String value;
		
		private CalificadorDelNumeroDeIdentidad(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}