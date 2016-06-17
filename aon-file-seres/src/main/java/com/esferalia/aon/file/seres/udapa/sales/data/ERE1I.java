package com.esferalia.aon.file.seres.udapa.sales.data;

public class ERE1I {

	private String impuestos;
	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroDeLineaImpuesto;
	private String calificadorTipoDeImpuesto;
	private String porcentajeTipoDeImpuesto;
	private String importeTipoDeImpuesto;
	private String baseImponible;


	private static java.util.regex.Pattern PATTERN_ERE1I_impuestos = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1I_tipoDePedido_220_221_224_226_22E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1I_numeroDePedido = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1I_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1I_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1I_numeroDeLineaImpuesto = java.util.regex.Pattern.compile("^.{63}(.{2}).*");
	private static java.util.regex.Pattern PATTERN_ERE1I_calificadorTipoDeImpuesto = java.util.regex.Pattern.compile("^.{65}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1I_porcentajeTipoDeImpuesto = java.util.regex.Pattern.compile("^.{71}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1I_importeTipoDeImpuesto = java.util.regex.Pattern.compile("^.{77}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1I_baseImponible = java.util.regex.Pattern.compile("^.{95}(.{18}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_ERE1I_impuestos.matcher(value)).find()) {
			setImpuestos(m.group(1));
		}
		if((m = PATTERN_ERE1I_tipoDePedido_220_221_224_226_22E_.matcher(value)).find()) {
			setTipoDePedido_220_221_224_226_22E_(m.group(1));
		}
		if((m = PATTERN_ERE1I_numeroDePedido.matcher(value)).find()) {
			setNumeroDePedido(m.group(1));
		}
		if((m = PATTERN_ERE1I_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_ERE1I_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_ERE1I_numeroDeLineaImpuesto.matcher(value)).find()) {
			setNumeroDeLineaImpuesto(m.group(1));
		}
		if((m = PATTERN_ERE1I_calificadorTipoDeImpuesto.matcher(value)).find()) {
			setCalificadorTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_ERE1I_porcentajeTipoDeImpuesto.matcher(value)).find()) {
			setPorcentajeTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_ERE1I_importeTipoDeImpuesto.matcher(value)).find()) {
			setImporteTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_ERE1I_baseImponible.matcher(value)).find()) {
			setBaseImponible(m.group(1));
		}
	}


	public String getImpuestos() {
		return impuestos;
	}
	public void setImpuestos(String impuestos) {
		this.impuestos = impuestos;
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
	public String getNumeroDeLineaImpuesto() {
		return numeroDeLineaImpuesto;
	}
	public void setNumeroDeLineaImpuesto(String numeroDeLineaImpuesto) {
		this.numeroDeLineaImpuesto = numeroDeLineaImpuesto;
	}
	public String getCalificadorTipoDeImpuesto() {
		return calificadorTipoDeImpuesto;
	}
	public void setCalificadorTipoDeImpuesto(String calificadorTipoDeImpuesto) {
		this.calificadorTipoDeImpuesto = calificadorTipoDeImpuesto;
	}
	public String getPorcentajeTipoDeImpuesto() {
		return porcentajeTipoDeImpuesto;
	}
	public void setPorcentajeTipoDeImpuesto(String porcentajeTipoDeImpuesto) {
		this.porcentajeTipoDeImpuesto = porcentajeTipoDeImpuesto;
	}
	public String getImporteTipoDeImpuesto() {
		return importeTipoDeImpuesto;
	}
	public void setImporteTipoDeImpuesto(String importeTipoDeImpuesto) {
		this.importeTipoDeImpuesto = importeTipoDeImpuesto;
	}
	public String getBaseImponible() {
		return baseImponible;
	}
	public void setBaseImponible(String baseImponible) {
		this.baseImponible = baseImponible;
	}

}