package com.esferalia.aon.file.seres.udapa.sales.data;

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


	public String getVencimientos() {
		return vencimientos;
	}
	public void setVencimientos(String vencimientos) {
		this.vencimientos = vencimientos;
	}
	public String getTipoDePedido_220_221_224_226_22E_() {
		return tipoDePedido_220_221_224_226_22E_;
	}
	public void setTipoDePedido_220_221_224_226_22E_(String tipoDePedido_220_221_224_226_22E_) {
		this.tipoDePedido_220_221_224_226_22E_ = tipoDePedido_220_221_224_226_22E_;
	}
	public String getNumeroDePedido() {
		return numeroDePedido;
	}
	public void setNumeroDePedido(String numeroDePedido) {
		this.numeroDePedido = numeroDePedido;
	}
	public String getCodigoEmisor_MS_() {
		return codigoEmisor_MS_;
	}
	public void setCodigoEmisor_MS_(String codigoEmisor_MS_) {
		this.codigoEmisor_MS_ = codigoEmisor_MS_;
	}
	public String getCodigoReceptor_MR_() {
		return codigoReceptor_MR_;
	}
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
	}
	public String getNumeroDeVencimiento() {
		return numeroDeVencimiento;
	}
	public void setNumeroDeVencimiento(String numeroDeVencimiento) {
		this.numeroDeVencimiento = numeroDeVencimiento;
	}
	public String getReferenciaDeTiempoDePago() {
		return referenciaDeTiempoDePago;
	}
	public void setReferenciaDeTiempoDePago(String referenciaDeTiempoDePago) {
		this.referenciaDeTiempoDePago = referenciaDeTiempoDePago;
	}
	public String getRelacionDeTiempo() {
		return relacionDeTiempo;
	}
	public void setRelacionDeTiempo(String relacionDeTiempo) {
		this.relacionDeTiempo = relacionDeTiempo;
	}
	public String getTipoDePeriodo_D_M_Y_() {
		return tipoDePeriodo_D_M_Y_;
	}
	public void setTipoDePeriodo_D_M_Y_(String tipoDePeriodo_D_M_Y_) {
		this.tipoDePeriodo_D_M_Y_ = tipoDePeriodo_D_M_Y_;
	}
	public String getNumeroDePeriodos() {
		return numeroDePeriodos;
	}
	public void setNumeroDePeriodos(String numeroDePeriodos) {
		this.numeroDePeriodos = numeroDePeriodos;
	}
	public String getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public String getImporteSujetoAlVencimiento() {
		return importeSujetoAlVencimiento;
	}
	public void setImporteSujetoAlVencimiento(String importeSujetoAlVencimiento) {
		this.importeSujetoAlVencimiento = importeSujetoAlVencimiento;
	}

}