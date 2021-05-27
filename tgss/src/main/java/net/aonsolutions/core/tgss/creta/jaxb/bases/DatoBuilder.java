package net.aonsolutions.core.tgss.creta.jaxb.bases;

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
	

	public DatoBuilder setCodigo(String codigo) {
		this.codigo = codigo;
		return this;
	}
	
	
	public DatoBuilder setHoras(int horas ) {
		// H -> Número entero de horas
		this.valor = String.format("%d", horas );
		return this;
	}
	
	public DatoBuilder setValor(String valor) {
		this.valor = valor;
		return this;
	}

	public DatoBuilder setClave(String clave) {
		// I -> Clave alfanumérica asociada al indicador
		this.valor = clave;
		return this;
	}

	public DatoBuilder setImporteCentimos(long importe) {
		// C -> Importe expresado en céntimos de euro
		this.valor = String.format("%d", importe );
		return this;
	}

	public DatoBuilder setImporteEuros(Double importe) {
		// C -> Importe expresado en céntimos de euro
		this.valor = String.format("%d", Math.round( importe * 100.00 ) );
		return this;
	}


}
