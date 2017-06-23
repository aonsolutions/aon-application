package net.aonsolutions.aon.sii;

public enum SIIType {

	FACTURAS_EMITIDAS,
	FACTURAS_EMITIDAS_COBROS,
	FACTURAS_RECIBIDAS,
	FACTURAS_RECIBIDAS_PAGOS,
	BIENES_INVERSION,
	OPERACIONES_INTRACOMUNITARIAS,
	COBROS_METALICO,
	OPERACIONES_SEGUROS,
	AGENCIAS_VIAJES;
	
	private SIIType() {
	
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String description(){
		return this.toString();
	}
}
