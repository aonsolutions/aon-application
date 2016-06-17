package com.esferalia.aon.file.seres.udapa.sales.data;

public class ERE1T {

	private String textos;
	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroTexto;
	private String calificadorDelTemaDelTexto_AAI_DEL_;
	private String texto1;
	private String texto2;
	private String texto3;
	private String texto4;
	private String texto5;


	private static java.util.regex.Pattern PATTERN_ERE1T_textos = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_tipoDePedido_220_221_224_226_22E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_numeroDePedido = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_numeroTexto = java.util.regex.Pattern.compile("^.{63}(.{2}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_calificadorDelTemaDelTexto_AAI_DEL_ = java.util.regex.Pattern.compile("^.{65}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_texto1 = java.util.regex.Pattern.compile("^.{68}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_texto2 = java.util.regex.Pattern.compile("^.{138}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_texto3 = java.util.regex.Pattern.compile("^.{208}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_texto4 = java.util.regex.Pattern.compile("^.{278}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_ERE1T_texto5 = java.util.regex.Pattern.compile("^.{348}(.{70}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_ERE1T_textos.matcher(value)).find()) {
			setTextos(m.group(1));
		}
		if((m = PATTERN_ERE1T_tipoDePedido_220_221_224_226_22E_.matcher(value)).find()) {
			setTipoDePedido_220_221_224_226_22E_(m.group(1));
		}
		if((m = PATTERN_ERE1T_numeroDePedido.matcher(value)).find()) {
			setNumeroDePedido(m.group(1));
		}
		if((m = PATTERN_ERE1T_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_ERE1T_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_ERE1T_numeroTexto.matcher(value)).find()) {
			setNumeroTexto(m.group(1));
		}
		if((m = PATTERN_ERE1T_calificadorDelTemaDelTexto_AAI_DEL_.matcher(value)).find()) {
			setCalificadorDelTemaDelTexto_AAI_DEL_(m.group(1));
		}
		if((m = PATTERN_ERE1T_texto1.matcher(value)).find()) {
			setTexto1(m.group(1));
		}
		if((m = PATTERN_ERE1T_texto2.matcher(value)).find()) {
			setTexto2(m.group(1));
		}
		if((m = PATTERN_ERE1T_texto3.matcher(value)).find()) {
			setTexto3(m.group(1));
		}
		if((m = PATTERN_ERE1T_texto4.matcher(value)).find()) {
			setTexto4(m.group(1));
		}
		if((m = PATTERN_ERE1T_texto5.matcher(value)).find()) {
			setTexto5(m.group(1));
		}
	}


	public String getTextos() {
		return textos;
	}
	public void setTextos(String textos) {
		this.textos = textos;
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
	public String getNumeroTexto() {
		return numeroTexto;
	}
	public void setNumeroTexto(String numeroTexto) {
		this.numeroTexto = numeroTexto;
	}
	public String getCalificadorDelTemaDelTexto_AAI_DEL_() {
		return calificadorDelTemaDelTexto_AAI_DEL_;
	}
	public void setCalificadorDelTemaDelTexto_AAI_DEL_(String calificadorDelTemaDelTexto_AAI_DEL_) {
		this.calificadorDelTemaDelTexto_AAI_DEL_ = calificadorDelTemaDelTexto_AAI_DEL_;
	}
	public String getTexto1() {
		return texto1;
	}
	public void setTexto1(String texto1) {
		this.texto1 = texto1;
	}
	public String getTexto2() {
		return texto2;
	}
	public void setTexto2(String texto2) {
		this.texto2 = texto2;
	}
	public String getTexto3() {
		return texto3;
	}
	public void setTexto3(String texto3) {
		this.texto3 = texto3;
	}
	public String getTexto4() {
		return texto4;
	}
	public void setTexto4(String texto4) {
		this.texto4 = texto4;
	}
	public String getTexto5() {
		return texto5;
	}
	public void setTexto5(String texto5) {
		this.texto5 = texto5;
	}

}