package net.aonsolutions.core.tgss.jaxb.trabajadorestramos;

import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.ObjectFactory;

public class DatoSolicitadoBuilder {

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory(); 
	
	private String tipo;
	private String valor;
	private String codigo;
	private boolean obligatorio;
	
	public DatoSolicitado create(){
		DatoSolicitado dato = OBJECT_FACTORY.createDatoSolicitado();
		
		dato.setCodigo(codigo);
		dato.setTipoDato(tipo);
		if ( valor != null )
			dato.setValor(valor);
		dato.setIndicadorObligatoriedad(obligatorio ? "B": "P");
		
		return dato;
	}
	
	public DatoSolicitadoBuilder setTipo(String tipo) {
		this.tipo = tipo;
		return this;
	}
	

	public DatoSolicitadoBuilder setCodigo(String codigo) {
		this.codigo = codigo;
		return this;
	}
	
	
	public DatoSolicitadoBuilder setObligatorio(boolean obligatorio) {
		this.obligatorio = obligatorio;
		return this;
	}

	public DatoSolicitadoBuilder setHoras(int horas ) {
		// H -> Número entero de horas
		this.valor = String.format("%d", horas );
		return this;
	}
	
	public DatoSolicitadoBuilder setValor(String valor) {
		this.valor = valor;
		return this;
	}

	public DatoSolicitadoBuilder setClave(String clave) {
		// I -> Clave alfanumérica asociada al indicador
		this.valor = clave;
		return this;
	}
	
	public DatoSolicitadoBuilder setImporteCentimos(long importe) {
		// C -> Importe expresado en céntimos de euro
		this.valor = String.format("%d", importe );
		return this;
	}

	public DatoSolicitadoBuilder setImporteEuros(Double importe) {
		// C -> Importe expresado en céntimos de euro
		this.valor = String.format("%d", Math.round( importe * 100.00 ) );
		return this;
	}


}
