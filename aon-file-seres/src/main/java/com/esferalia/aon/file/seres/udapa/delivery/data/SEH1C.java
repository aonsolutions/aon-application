package com.esferalia.aon.file.seres.udapa.delivery.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1C entity.
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
 * 		<td>SEH1C</th>
 * 		<td>Cabecera</th>
 * 		<td>Obligatorio</th>
 * 		<td>1</th>
 * 	</tr>
 * </table>
 */ 

public class SEH1C {

	private String cabecera;
	private String tipoAvisoDeExpedicion_351_35E_;
	private String numeroAvisoDeExpedicion;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String funcionDelMensaje;
	private String fechaDelDocumento_137__102_203_;
	private String fechaEsperadaDeEntrega_17__102_203_;
	private String calificadorFechaEntrega_2_11_PER_358_359__;
	private String fechaDeServicio1;
	private String horaDeServicio1;
	private String fechaDeServicio2;
	private String horaDeServicio2;
	private String informacionAdicional;
	private String numeroDePedido_ON_;
	private String fechaDePedido_171__102_203_;
	private String numeroDeAlbaran_DQ_;
	private String fechaDeAlbaran_171__102_203_;
	private String calificadorReferencia1;
	private String numeroDeReferencia1;
	private String fechaDeReferencia1_102_203_;
	private String calificadorReferencia2;
	private String numeroDeReferencia2;
	private String fechaDeReferencia2_102_203_;
	private String metodoPagoDeCostesDeTransporte;
	private String codigoCondicionesDeEntrega;
	private String descripcionCondicionesDeEntrega;
	private String modoDeTransporte;
	private String codigoTransportista;
	private String nombreTransportista;
	private String matriculaDelVehiculo;


	public java.util.List<SEH1D> seh1dList;
	public java.util.List<SEH1P> seh1pList;
	public java.util.List<SEH1L> seh1lList;
	public java.util.List<SEH1G> seh1gList;
	public java.util.List<SEH1B> seh1bList;


	private static java.util.regex.Pattern PATTERN_SEH1C_cabecera = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_tipoAvisoDeExpedicion_351_35E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_numeroAvisoDeExpedicion = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_funcionDelMensaje = java.util.regex.Pattern.compile("^.{63}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_fechaDelDocumento_137__102_203_ = java.util.regex.Pattern.compile("^.{69}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_fechaEsperadaDeEntrega_17__102_203_ = java.util.regex.Pattern.compile("^.{81}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_calificadorFechaEntrega_2_11_PER_358_359__ = java.util.regex.Pattern.compile("^.{93}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_fechaDeServicio1 = java.util.regex.Pattern.compile("^.{96}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_horaDeServicio1 = java.util.regex.Pattern.compile("^.{104}(.{4}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_fechaDeServicio2 = java.util.regex.Pattern.compile("^.{108}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_horaDeServicio2 = java.util.regex.Pattern.compile("^.{116}(.{4}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_informacionAdicional = java.util.regex.Pattern.compile("^.{120}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_numeroDePedido_ON_ = java.util.regex.Pattern.compile("^.{126}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_fechaDePedido_171__102_203_ = java.util.regex.Pattern.compile("^.{143}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_numeroDeAlbaran_DQ_ = java.util.regex.Pattern.compile("^.{155}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_fechaDeAlbaran_171__102_203_ = java.util.regex.Pattern.compile("^.{172}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_calificadorReferencia1 = java.util.regex.Pattern.compile("^.{184}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_numeroDeReferencia1 = java.util.regex.Pattern.compile("^.{187}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_fechaDeReferencia1_102_203_ = java.util.regex.Pattern.compile("^.{204}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_calificadorReferencia2 = java.util.regex.Pattern.compile("^.{216}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_numeroDeReferencia2 = java.util.regex.Pattern.compile("^.{219}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_fechaDeReferencia2_102_203_ = java.util.regex.Pattern.compile("^.{236}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_metodoPagoDeCostesDeTransporte = java.util.regex.Pattern.compile("^.{248}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_codigoCondicionesDeEntrega = java.util.regex.Pattern.compile("^.{251}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_descripcionCondicionesDeEntrega = java.util.regex.Pattern.compile("^.{254}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_modoDeTransporte = java.util.regex.Pattern.compile("^.{324}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_codigoTransportista = java.util.regex.Pattern.compile("^.{327}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_nombreTransportista = java.util.regex.Pattern.compile("^.{340}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1C_matriculaDelVehiculo = java.util.regex.Pattern.compile("^.{375}(.{17}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_SEH1C_cabecera.matcher(value)).find()) {
			setCabecera(m.group(1));
		}
		if((m = PATTERN_SEH1C_tipoAvisoDeExpedicion_351_35E_.matcher(value)).find()) {
			setTipoAvisoDeExpedicion_351_35E_(m.group(1));
		}
		if((m = PATTERN_SEH1C_numeroAvisoDeExpedicion.matcher(value)).find()) {
			setNumeroAvisoDeExpedicion(m.group(1));
		}
		if((m = PATTERN_SEH1C_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_SEH1C_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_SEH1C_funcionDelMensaje.matcher(value)).find()) {
			setFuncionDelMensaje(m.group(1));
		}
		if((m = PATTERN_SEH1C_fechaDelDocumento_137__102_203_.matcher(value)).find()) {
			setFechaDelDocumento_137__102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1C_fechaEsperadaDeEntrega_17__102_203_.matcher(value)).find()) {
			setFechaEsperadaDeEntrega_17__102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1C_calificadorFechaEntrega_2_11_PER_358_359__.matcher(value)).find()) {
			setCalificadorFechaEntrega_2_11_PER_358_359__(m.group(1));
		}
		if((m = PATTERN_SEH1C_fechaDeServicio1.matcher(value)).find()) {
			setFechaDeServicio1(m.group(1));
		}
		if((m = PATTERN_SEH1C_horaDeServicio1.matcher(value)).find()) {
			setHoraDeServicio1(m.group(1));
		}
		if((m = PATTERN_SEH1C_fechaDeServicio2.matcher(value)).find()) {
			setFechaDeServicio2(m.group(1));
		}
		if((m = PATTERN_SEH1C_horaDeServicio2.matcher(value)).find()) {
			setHoraDeServicio2(m.group(1));
		}
		if((m = PATTERN_SEH1C_informacionAdicional.matcher(value)).find()) {
			setInformacionAdicional(m.group(1));
		}
		if((m = PATTERN_SEH1C_numeroDePedido_ON_.matcher(value)).find()) {
			setNumeroDePedido_ON_(m.group(1));
		}
		if((m = PATTERN_SEH1C_fechaDePedido_171__102_203_.matcher(value)).find()) {
			setFechaDePedido_171__102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1C_numeroDeAlbaran_DQ_.matcher(value)).find()) {
			setNumeroDeAlbaran_DQ_(m.group(1));
		}
		if((m = PATTERN_SEH1C_fechaDeAlbaran_171__102_203_.matcher(value)).find()) {
			setFechaDeAlbaran_171__102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1C_calificadorReferencia1.matcher(value)).find()) {
			setCalificadorReferencia1(m.group(1));
		}
		if((m = PATTERN_SEH1C_numeroDeReferencia1.matcher(value)).find()) {
			setNumeroDeReferencia1(m.group(1));
		}
		if((m = PATTERN_SEH1C_fechaDeReferencia1_102_203_.matcher(value)).find()) {
			setFechaDeReferencia1_102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1C_calificadorReferencia2.matcher(value)).find()) {
			setCalificadorReferencia2(m.group(1));
		}
		if((m = PATTERN_SEH1C_numeroDeReferencia2.matcher(value)).find()) {
			setNumeroDeReferencia2(m.group(1));
		}
		if((m = PATTERN_SEH1C_fechaDeReferencia2_102_203_.matcher(value)).find()) {
			setFechaDeReferencia2_102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1C_metodoPagoDeCostesDeTransporte.matcher(value)).find()) {
			setMetodoPagoDeCostesDeTransporte(m.group(1));
		}
		if((m = PATTERN_SEH1C_codigoCondicionesDeEntrega.matcher(value)).find()) {
			setCodigoCondicionesDeEntrega(m.group(1));
		}
		if((m = PATTERN_SEH1C_descripcionCondicionesDeEntrega.matcher(value)).find()) {
			setDescripcionCondicionesDeEntrega(m.group(1));
		}
		if((m = PATTERN_SEH1C_modoDeTransporte.matcher(value)).find()) {
			setModoDeTransporte(m.group(1));
		}
		if((m = PATTERN_SEH1C_codigoTransportista.matcher(value)).find()) {
			setCodigoTransportista(m.group(1));
		}
		if((m = PATTERN_SEH1C_nombreTransportista.matcher(value)).find()) {
			setNombreTransportista(m.group(1));
		}
		if((m = PATTERN_SEH1C_matriculaDelVehiculo.matcher(value)).find()) {
			setMatriculaDelVehiculo(m.group(1));
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
	 * 		<td>SEH1C</th>
	 * 		<td>Cabecera</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>m</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCabecera() {
		return cabecera;
	}
	public void setCabecera(String cabecera) {
		this.cabecera = cabecera;
	}

	/** 
	 * V1001T-Tipo Aviso de Expedición (351/35E): El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * V1004P-Número Aviso de Expedición: Número de Aviso de Expedición  asignado por el emisor del documento.
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
	 * V3039E-Código Dpto. que emite el mensaje (MS): El campo corresponde a un Punto Operacional EDI. Función asociada 'PROV'
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
	 * V3039R-Código Receptor del mensaje (MR): El campo corresponde a un Punto Operacional EDI. Función asociada 'PRES'
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
	 * V1225F-Función del Mensaje: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V1225F</th>
	 * 		<td>Función del Mensaje</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>64</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFuncionDelMensaje() {
		return funcionDelMensaje;
	}
	public void setFuncionDelMensaje(String funcionDelMensaje) {
		this.funcionDelMensaje = funcionDelMensaje;
	}

	/** 
	 * V2380D-Fecha del Documento (137) (102/203): Fecha de generación del Aviso de Expedición en formato AAAAMMDD o AAAAMMDDHHMM
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
	 * 		<td>V2380D</th>
	 * 		<td>Fecha del Documento (137) (102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>70</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDelDocumento_137__102_203_() {
		return fechaDelDocumento_137__102_203_;
	}
	public void setFechaDelDocumento_137__102_203_(String fechaDelDocumento_137__102_203_) {
		this.fechaDelDocumento_137__102_203_ = fechaDelDocumento_137__102_203_;
	}

	/** 
	 * V2380X-Fecha esperada de Entrega (17) (102/203): Campo Opcional en formato AAAAMMDD o AAAAMMDDHHMM
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
	 * 		<td>V2380X</th>
	 * 		<td>Fecha esperada de Entrega (17) (102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>82</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaEsperadaDeEntrega_17__102_203_() {
		return fechaEsperadaDeEntrega_17__102_203_;
	}
	public void setFechaEsperadaDeEntrega_17__102_203_(String fechaEsperadaDeEntrega_17__102_203_) {
		this.fechaEsperadaDeEntrega_17__102_203_ = fechaEsperadaDeEntrega_17__102_203_;
	}

	/** 
	 * V2005C-Calif. Fecha de Entrega de la mercancía: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V2005C</th>
	 * 		<td>Calificador Fecha  Entrega(2-11-PER(358-359))</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>94</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorFechaEntrega_2_11_PER_358_359__() {
		return calificadorFechaEntrega_2_11_PER_358_359__;
	}
	public void setCalificadorFechaEntrega_2_11_PER_358_359__(String calificadorFechaEntrega_2_11_PER_358_359__) {
		this.calificadorFechaEntrega_2_11_PER_358_359__ = calificadorFechaEntrega_2_11_PER_358_359__;
	}

	/** 
	 * V2380F-Fecha de Servicio 1: Fecha a  partir de la cual entregaremos la mercancía en formato AAAAMMDD
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
	 * 		<td>V2380F</th>
	 * 		<td>Fecha de Servicio 1</th>
	 * 		<td>C</th>
	 * 		<td>8</th>
	 * 		<td>97</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeServicio1() {
		return fechaDeServicio1;
	}
	public void setFechaDeServicio1(String fechaDeServicio1) {
		this.fechaDeServicio1 = fechaDeServicio1;
	}

	/** 
	 * V2380H-Hora de Servicio 1: Puede especificarse una hora asociada a la fecha de servicio 1, en formato HHMM
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
	 * 		<td>V2380H</th>
	 * 		<td>Hora de Servicio 1</th>
	 * 		<td>C</th>
	 * 		<td>4</th>
	 * 		<td>105</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getHoraDeServicio1() {
		return horaDeServicio1;
	}
	public void setHoraDeServicio1(String horaDeServicio1) {
		this.horaDeServicio1 = horaDeServicio1;
	}

	/** 
	 * V2380G-Fecha de Servicio 2: Fecha límite de entrega en formato AAAAMMDD
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
	 * 		<td>V2380G</th>
	 * 		<td>Fecha de Servicio 2</th>
	 * 		<td>C</th>
	 * 		<td>8</th>
	 * 		<td>109</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeServicio2() {
		return fechaDeServicio2;
	}
	public void setFechaDeServicio2(String fechaDeServicio2) {
		this.fechaDeServicio2 = fechaDeServicio2;
	}

	/** 
	 * V2380I-Hora de Servicio 2: Hora asociada a la fecha de servicio 2, en formato HHMM
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
	 * 		<td>V2380I</th>
	 * 		<td>Hora de Servicio 2</th>
	 * 		<td>C</th>
	 * 		<td>4</th>
	 * 		<td>117</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getHoraDeServicio2() {
		return horaDeServicio2;
	}
	public void setHoraDeServicio2(String horaDeServicio2) {
		this.horaDeServicio2 = horaDeServicio2;
	}

	/** 
	 * V4183I-Información adicional: Permite indicar que la mercancía especificada en el Aviso de Expedición se envía en depósito. El campo corresponde a un EANCOM. Los valores posibles son:
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
	 * 		<td>V4183I</th>
	 * 		<td>Información adicional</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>121</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getInformacionAdicional() {
		return informacionAdicional;
	}
	public void setInformacionAdicional(String informacionAdicional) {
		this.informacionAdicional = informacionAdicional;
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
	 * 		<td>V1154P</th>
	 * 		<td>Número de Pedido (ON)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>127</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDePedido_ON_() {
		return numeroDePedido_ON_;
	}
	public void setNumeroDePedido_ON_(String numeroDePedido_ON_) {
		this.numeroDePedido_ON_ = numeroDePedido_ON_;
	}

	/** 
	 * V2380P-Fecha de Pedido (171) (102/203): En formato AAAAMMDD o AAAAMMDDHHMM
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
	 * 		<td>V2380P</th>
	 * 		<td>Fecha de Pedido (171) (102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>144</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDePedido_171__102_203_() {
		return fechaDePedido_171__102_203_;
	}
	public void setFechaDePedido_171__102_203_(String fechaDePedido_171__102_203_) {
		this.fechaDePedido_171__102_203_ = fechaDePedido_171__102_203_;
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
	 * 		<td>V1154L</th>
	 * 		<td>Número de Albarán  (DQ)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>156</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeAlbaran_DQ_() {
		return numeroDeAlbaran_DQ_;
	}
	public void setNumeroDeAlbaran_DQ_(String numeroDeAlbaran_DQ_) {
		this.numeroDeAlbaran_DQ_ = numeroDeAlbaran_DQ_;
	}

	/** 
	 * V2380L-Fecha de Albarán (171) (102/203): En formato AAAAMMDD o AAAAMMDDHHMM
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
	 * 		<td>V2380L</th>
	 * 		<td>Fecha de Albarán (171) (102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>173</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeAlbaran_171__102_203_() {
		return fechaDeAlbaran_171__102_203_;
	}
	public void setFechaDeAlbaran_171__102_203_(String fechaDeAlbaran_171__102_203_) {
		this.fechaDeAlbaran_171__102_203_ = fechaDeAlbaran_171__102_203_;
	}

	/** 
	 * V1153C y V1153V - Calificadores para especificar otras referencias: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V1153C</th>
	 * 		<td>Calificador Referencia 1</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>185</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorReferencia1() {
		return calificadorReferencia1;
	}
	public void setCalificadorReferencia1(String calificadorReferencia1) {
		this.calificadorReferencia1 = calificadorReferencia1;
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
	 * 		<td>V1154C</th>
	 * 		<td>Número de Referencia 1</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>188</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeReferencia1() {
		return numeroDeReferencia1;
	}
	public void setNumeroDeReferencia1(String numeroDeReferencia1) {
		this.numeroDeReferencia1 = numeroDeReferencia1;
	}

	/** 
	 * V2380C y V2380V - Fechas de referencia: En formato AAAAMMDD o AAAAMMDDHHMM
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
	 * 		<td>V2380C</th>
	 * 		<td>Fecha de Referencia 1(102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>205</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeReferencia1_102_203_() {
		return fechaDeReferencia1_102_203_;
	}
	public void setFechaDeReferencia1_102_203_(String fechaDeReferencia1_102_203_) {
		this.fechaDeReferencia1_102_203_ = fechaDeReferencia1_102_203_;
	}

	/** 
	 * V1153C y V1153V - Calificadores para especificar otras referencias: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V1153V</th>
	 * 		<td>Calificador Referencia 2</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>217</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorReferencia2() {
		return calificadorReferencia2;
	}
	public void setCalificadorReferencia2(String calificadorReferencia2) {
		this.calificadorReferencia2 = calificadorReferencia2;
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
	 * 		<td>V1154V</th>
	 * 		<td>Número de Referencia 2</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>220</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeReferencia2() {
		return numeroDeReferencia2;
	}
	public void setNumeroDeReferencia2(String numeroDeReferencia2) {
		this.numeroDeReferencia2 = numeroDeReferencia2;
	}

	/** 
	 * V2380C y V2380V - Fechas de referencia: En formato AAAAMMDD o AAAAMMDDHHMM
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
	 * 		<td>V2380V</th>
	 * 		<td>Fecha de Referencia 2(102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>237</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeReferencia2_102_203_() {
		return fechaDeReferencia2_102_203_;
	}
	public void setFechaDeReferencia2_102_203_(String fechaDeReferencia2_102_203_) {
		this.fechaDeReferencia2_102_203_ = fechaDeReferencia2_102_203_;
	}

	/** 
	 * V4215T-Método de Pago de Costes de Transporte: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V4215T</th>
	 * 		<td>Método Pago de Costes de Transporte</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>249</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getMetodoPagoDeCostesDeTransporte() {
		return metodoPagoDeCostesDeTransporte;
	}
	public void setMetodoPagoDeCostesDeTransporte(String metodoPagoDeCostesDeTransporte) {
		this.metodoPagoDeCostesDeTransporte = metodoPagoDeCostesDeTransporte;
	}

	/** 
	 * V4053T-Código Condiciones de Entrega: Los valores posibles son:
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
	 * 		<td>V4053T</th>
	 * 		<td>Código Condiciones de Entrega</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>252</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoCondicionesDeEntrega() {
		return codigoCondicionesDeEntrega;
	}
	public void setCodigoCondicionesDeEntrega(String codigoCondicionesDeEntrega) {
		this.codigoCondicionesDeEntrega = codigoCondicionesDeEntrega;
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
	 * 		<td>V4052T</th>
	 * 		<td>Descripción Condiciones de Entrega</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>255</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDescripcionCondicionesDeEntrega() {
		return descripcionCondicionesDeEntrega;
	}
	public void setDescripcionCondicionesDeEntrega(String descripcionCondicionesDeEntrega) {
		this.descripcionCondicionesDeEntrega = descripcionCondicionesDeEntrega;
	}

	/** 
	 * V8067T-Modo de Transporte: Los valores posibles son:
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
	 * 		<td>V8067T</th>
	 * 		<td>Modo de Transporte</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>325</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getModoDeTransporte() {
		return modoDeTransporte;
	}
	public void setModoDeTransporte(String modoDeTransporte) {
		this.modoDeTransporte = modoDeTransporte;
	}

	/** 
	 * V3127T-Código  del Transportista: El campo corresponde a un Punto Operacional EDI. Función asociada 'TRAN'
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
	 * 		<td>V3127T</th>
	 * 		<td>Código Transportista</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>328</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoTransportista() {
		return codigoTransportista;
	}
	public void setCodigoTransportista(String codigoTransportista) {
		this.codigoTransportista = codigoTransportista;
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
	 * 		<td>V3128T</th>
	 * 		<td>Nombre Transportista</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>341</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNombreTransportista() {
		return nombreTransportista;
	}
	public void setNombreTransportista(String nombreTransportista) {
		this.nombreTransportista = nombreTransportista;
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
	 * 		<td>V8212T</th>
	 * 		<td>Matricula del Vehículo</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>376</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getMatriculaDelVehiculo() {
		return matriculaDelVehiculo;
	}
	public void setMatriculaDelVehiculo(String matriculaDelVehiculo) {
		this.matriculaDelVehiculo = matriculaDelVehiculo;
	}

	public enum TipoAvisoDeExpedicion_351_35E_ {
		AVISO_DE_EXPEDICIO_351("351"),
		RESPUESTA_AL_AVIS_35E("35E"),
		;
		
		private String value;
		
		private TipoAvisoDeExpedicion_351_35E_(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum FuncionDelMensaje {
		CANCELACIO_1("1"),
		DUPLICAD_7("7"),
		ORIGINA_9("9"),
		COPI_31("31"),
		;
		
		private String value;
		
		private FuncionDelMensaje(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum Calif_FechaDeEntregaDeLaMercancia {
		FECHA_Y_HORA_DE_ENTREGA_SOLICITAD_2("2"),
		DESPACHO_EN_FECHA_Y_O_HOR_11("11"),
		SI_NOS_INTERESA_ESPECIFICAR_UN_PERIODO_DE_ENTREGA____DESDE______HASTA____PER("PER"),
		;
		
		private String value;
		
		private Calif_FechaDeEntregaDeLaMercancia(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum InformacionAdicional {
		ENVIAR_PERO_NO_FACTURAR__EN_DEPOSITO_82E("82E"),
		;
		
		private String value;
		
		private InformacionAdicional(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CalificadoresParaEspecificarOtrasReferencias {
		N__DE_AVISO_DE_EXPEDICIO_AAJ("AAJ"),
		N__DE_LISTA_DE_EMPAQUETAD_PK("PK"),
		N__DE_PEDIDO_PARA_EL_PROVEEDO_VN("VN"),
		;
		
		private String value;
		
		private CalificadoresParaEspecificarOtrasReferencias(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum MetodoDePagoDeCostesDeTransporte {
		DEFINIDO_POR_EL_COMPRADOR_Y_EL_VENDEDO_DF("DF"),
		PAGADO_DE_ANTEMANO_PERO_CON_CARGO_AL_CLIENT_PC("PC"),
		PORTES_PAGADO_PP("PP"),
		;
		
		private String value;
		
		private MetodoDePagoDeCostesDeTransporte(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CodigoCondicionesDeEntrega {
		RECOGIDA_POR_EL_EMISOR_DEL_PEDID_RD("RD"),
		ENVIADO_POR_EL_RECEPTOR_DEL_PEDID_EP("EP"),
		;
		
		private String value;
		
		private CodigoCondicionesDeEntrega(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum ModoDeTransporte {
		MARITIM_10("10"),
		FERROCARRI_20("20"),
		CARRETER_30("30"),
		MULTIMODA_60("60"),
		;
		
		private String value;
		
		private ModoDeTransporte(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}