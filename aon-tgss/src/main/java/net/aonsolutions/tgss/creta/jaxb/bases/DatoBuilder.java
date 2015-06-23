package net.aonsolutions.tgss.creta.jaxb.bases;

public class DatoBuilder {

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory(); 
	
	private String tipo;
	private String valor;
	private String codigo;
	
	public Dato create(){
		Dato dato = OBJECT_FACTORY.createDato();
		
		dato.setCodigo(codigo);
		dato.setTipoDato(tipo);
		dato.setValor(valor);
		
		return dato;
	}
	
	public DatoBuilder setTipo(String tipo) {
		this.tipo = tipo;
		return this;
	}
	
	public DatoBuilder setValor(String valor) {
		this.valor = valor;
		return this;
	}

	public DatoBuilder setValor(Double valor) {
		this.valor = String.format("%.2f", valor);
		return this;
	}

	public DatoBuilder setCodigo(String codigo) {
		this.codigo = codigo;
		return this;
	}
	
	
}
