package com.esferalia.aon.file.seres.udapa.sales.data;

public class ERE1D {

	private String descuentos;
	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroDeLineaArticulo;
	private String numeroDescuento_Cargo;
	private String indicadorDescuento_Cargo_A_C_;
	private String indicadorSecuenciaDeCalculo;
	private String serviciosEspeciales;
	private String porcentajeDescuento_Cargo_3_;
	private String importeDescuento_Cargo_23_204_;
	private String cantidadDeUnidadesQueSeDescuentan_1_;
	private String descuentosMonetariosPorUnidad;
	private String unidadDeMedida;


	private static java.util.regex.Pattern PATTERN_ERE1D_descuentos = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_tipoDePedido_220_221_224_226_22E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_numeroDePedido = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_numeroDeLineaArticulo = java.util.regex.Pattern.compile("^.{63}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_numeroDescuento_Cargo = java.util.regex.Pattern.compile("^.{69}(.{2}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_indicadorDescuento_Cargo_A_C_ = java.util.regex.Pattern.compile("^.{71}(.{1}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_indicadorSecuenciaDeCalculo = java.util.regex.Pattern.compile("^.{72}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_serviciosEspeciales = java.util.regex.Pattern.compile("^.{75}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_porcentajeDescuento_Cargo_3_ = java.util.regex.Pattern.compile("^.{81}(.{9}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_importeDescuento_Cargo_23_204_ = java.util.regex.Pattern.compile("^.{90}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_cantidadDeUnidadesQueSeDescuentan_1_ = java.util.regex.Pattern.compile("^.{108}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_descuentosMonetariosPorUnidad = java.util.regex.Pattern.compile("^.{124}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_ERE1D_unidadDeMedida = java.util.regex.Pattern.compile("^.{140}(.{6}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_ERE1D_descuentos.matcher(value)).find()) {
			setDescuentos(m.group(1));
		}
		if((m = PATTERN_ERE1D_tipoDePedido_220_221_224_226_22E_.matcher(value)).find()) {
			setTipoDePedido_220_221_224_226_22E_(m.group(1));
		}
		if((m = PATTERN_ERE1D_numeroDePedido.matcher(value)).find()) {
			setNumeroDePedido(m.group(1));
		}
		if((m = PATTERN_ERE1D_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_ERE1D_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_ERE1D_numeroDeLineaArticulo.matcher(value)).find()) {
			setNumeroDeLineaArticulo(m.group(1));
		}
		if((m = PATTERN_ERE1D_numeroDescuento_Cargo.matcher(value)).find()) {
			setNumeroDescuento_Cargo(m.group(1));
		}
		if((m = PATTERN_ERE1D_indicadorDescuento_Cargo_A_C_.matcher(value)).find()) {
			setIndicadorDescuento_Cargo_A_C_(m.group(1));
		}
		if((m = PATTERN_ERE1D_indicadorSecuenciaDeCalculo.matcher(value)).find()) {
			setIndicadorSecuenciaDeCalculo(m.group(1));
		}
		if((m = PATTERN_ERE1D_serviciosEspeciales.matcher(value)).find()) {
			setServiciosEspeciales(m.group(1));
		}
		if((m = PATTERN_ERE1D_porcentajeDescuento_Cargo_3_.matcher(value)).find()) {
			setPorcentajeDescuento_Cargo_3_(m.group(1));
		}
		if((m = PATTERN_ERE1D_importeDescuento_Cargo_23_204_.matcher(value)).find()) {
			setImporteDescuento_Cargo_23_204_(m.group(1));
		}
		if((m = PATTERN_ERE1D_cantidadDeUnidadesQueSeDescuentan_1_.matcher(value)).find()) {
			setCantidadDeUnidadesQueSeDescuentan_1_(m.group(1));
		}
		if((m = PATTERN_ERE1D_descuentosMonetariosPorUnidad.matcher(value)).find()) {
			setDescuentosMonetariosPorUnidad(m.group(1));
		}
		if((m = PATTERN_ERE1D_unidadDeMedida.matcher(value)).find()) {
			setUnidadDeMedida(m.group(1));
		}
	}


	public String getDescuentos() {
		return descuentos;
	}
	public void setDescuentos(String descuentos) {
		this.descuentos = descuentos;
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
	public String getNumeroDescuento_Cargo() {
		return numeroDescuento_Cargo;
	}
	public void setNumeroDescuento_Cargo(String numeroDescuento_Cargo) {
		this.numeroDescuento_Cargo = numeroDescuento_Cargo;
	}
	public String getIndicadorDescuento_Cargo_A_C_() {
		return indicadorDescuento_Cargo_A_C_;
	}
	public void setIndicadorDescuento_Cargo_A_C_(String indicadorDescuento_Cargo_A_C_) {
		this.indicadorDescuento_Cargo_A_C_ = indicadorDescuento_Cargo_A_C_;
	}
	public String getIndicadorSecuenciaDeCalculo() {
		return indicadorSecuenciaDeCalculo;
	}
	public void setIndicadorSecuenciaDeCalculo(String indicadorSecuenciaDeCalculo) {
		this.indicadorSecuenciaDeCalculo = indicadorSecuenciaDeCalculo;
	}
	public String getServiciosEspeciales() {
		return serviciosEspeciales;
	}
	public void setServiciosEspeciales(String serviciosEspeciales) {
		this.serviciosEspeciales = serviciosEspeciales;
	}
	public String getPorcentajeDescuento_Cargo_3_() {
		return porcentajeDescuento_Cargo_3_;
	}
	public void setPorcentajeDescuento_Cargo_3_(String porcentajeDescuento_Cargo_3_) {
		this.porcentajeDescuento_Cargo_3_ = porcentajeDescuento_Cargo_3_;
	}
	public String getImporteDescuento_Cargo_23_204_() {
		return importeDescuento_Cargo_23_204_;
	}
	public void setImporteDescuento_Cargo_23_204_(String importeDescuento_Cargo_23_204_) {
		this.importeDescuento_Cargo_23_204_ = importeDescuento_Cargo_23_204_;
	}
	public String getCantidadDeUnidadesQueSeDescuentan_1_() {
		return cantidadDeUnidadesQueSeDescuentan_1_;
	}
	public void setCantidadDeUnidadesQueSeDescuentan_1_(String cantidadDeUnidadesQueSeDescuentan_1_) {
		this.cantidadDeUnidadesQueSeDescuentan_1_ = cantidadDeUnidadesQueSeDescuentan_1_;
	}
	public String getDescuentosMonetariosPorUnidad() {
		return descuentosMonetariosPorUnidad;
	}
	public void setDescuentosMonetariosPorUnidad(String descuentosMonetariosPorUnidad) {
		this.descuentosMonetariosPorUnidad = descuentosMonetariosPorUnidad;
	}
	public String getUnidadDeMedida() {
		return unidadDeMedida;
	}
	public void setUnidadDeMedida(String unidadDeMedida) {
		this.unidadDeMedida = unidadDeMedida;
	}

}