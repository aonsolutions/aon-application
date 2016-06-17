package com.esferalia.aon.file.seres.udapa.sales.data;

public class ERE1G {

	private String desgloseDeCantidad;
	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroDeLineaArticulo;
	private String contadorDesglose;
	private String codigoLugarDeEntrega_7_;
	private String cantidadDividida_11_;


	private static java.util.regex.Pattern PATTERN_ERE1G_desgloseDeCantidad = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_tipoDePedido_220_221_224_226_22E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_numeroDePedido = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_numeroDeLineaArticulo = java.util.regex.Pattern.compile("^.{63}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_contadorDesglose = java.util.regex.Pattern.compile("^.{69}(.{4}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_codigoLugarDeEntrega_7_ = java.util.regex.Pattern.compile("^.{73}(.{25}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_cantidadDividida_11_ = java.util.regex.Pattern.compile("^.{98}(.{16}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_ERE1G_desgloseDeCantidad.matcher(value)).find()) {
			setDesgloseDeCantidad(m.group(1));
		}
		if((m = PATTERN_ERE1G_tipoDePedido_220_221_224_226_22E_.matcher(value)).find()) {
			setTipoDePedido_220_221_224_226_22E_(m.group(1));
		}
		if((m = PATTERN_ERE1G_numeroDePedido.matcher(value)).find()) {
			setNumeroDePedido(m.group(1));
		}
		if((m = PATTERN_ERE1G_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_ERE1G_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_ERE1G_numeroDeLineaArticulo.matcher(value)).find()) {
			setNumeroDeLineaArticulo(m.group(1));
		}
		if((m = PATTERN_ERE1G_contadorDesglose.matcher(value)).find()) {
			setContadorDesglose(m.group(1));
		}
		if((m = PATTERN_ERE1G_codigoLugarDeEntrega_7_.matcher(value)).find()) {
			setCodigoLugarDeEntrega_7_(m.group(1));
		}
		if((m = PATTERN_ERE1G_cantidadDividida_11_.matcher(value)).find()) {
			setCantidadDividida_11_(m.group(1));
		}
	}


	public String getDesgloseDeCantidad() {
		return desgloseDeCantidad;
	}
	public void setDesgloseDeCantidad(String desgloseDeCantidad) {
		this.desgloseDeCantidad = desgloseDeCantidad;
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
	public String getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}
	public void setNumeroDeLineaArticulo(String numeroDeLineaArticulo) {
		this.numeroDeLineaArticulo = numeroDeLineaArticulo;
	}
	public String getContadorDesglose() {
		return contadorDesglose;
	}
	public void setContadorDesglose(String contadorDesglose) {
		this.contadorDesglose = contadorDesglose;
	}
	public String getCodigoLugarDeEntrega_7_() {
		return codigoLugarDeEntrega_7_;
	}
	public void setCodigoLugarDeEntrega_7_(String codigoLugarDeEntrega_7_) {
		this.codigoLugarDeEntrega_7_ = codigoLugarDeEntrega_7_;
	}
	public String getCantidadDividida_11_() {
		return cantidadDividida_11_;
	}
	public void setCantidadDividida_11_(String cantidadDividida_11_) {
		this.cantidadDividida_11_ = cantidadDividida_11_;
	}

}