package net.aonsolutions.aon.sii;

public enum SendType {

	// TODO ¿? ó -> \u00f3
	
	ALTA_EMITIDAS("Alta Facturas Emitidas"),
	MOD_EMITIDAS("Mod Facturas Emitidas"),
	BAJA_EMITIDAS("Baja Facturas Emitidas"),
	ALTA_RECIBIDAS("Alta Facturas Recibidas"),
	MOD_RECIBIDAS("Mod Facturas Recibidas"),
	BAJA_RECIBIDAS("Baja Facturas Recibidas"),
	ALTA_INTRACOMUNITARIAS("Alta Op Intracomunitarias"),
	MOD_INTRACOMUNITARIAS("Mod Op Intracomunitarias"),
	BAJA_INTRACOMUNITARIAS("Baja Op Intracomunitarias"),
	ALTA_INVERSION("Alta Bienes de Inversión"),
	MOD_INVERSION("Mod Bienes de Inversión"),
	BAJA_INVERSION("Baja Bienes de Inversión"),
	COBROS_PAGOS("Operaciones Cobros/Pagos")
	;	
	
	String description;
	private SendType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte value(){
		return (byte) this.ordinal();
	}

	public Boolean isEmitida(){
		return this.equals(ALTA_EMITIDAS) 
			|| this.equals(MOD_EMITIDAS)
			|| this.equals(BAJA_EMITIDAS);
	}
	
	public Boolean isRecibida(){
		return this.equals(ALTA_RECIBIDAS) 
			|| this.equals(MOD_RECIBIDAS)
			|| this.equals(BAJA_RECIBIDAS);
	}
	
	public Boolean isIntracomunitaria(){
		return this.equals(ALTA_INTRACOMUNITARIAS) 
			|| this.equals(MOD_INTRACOMUNITARIAS)
			|| this.equals(BAJA_INTRACOMUNITARIAS);
	}

	public Boolean isInversion(){
		return this.equals(ALTA_INVERSION) 
			|| this.equals(MOD_INVERSION)
			|| this.equals(BAJA_INVERSION);
	}
	
	public Boolean isCobrosPagos(){
		return this.equals(COBROS_PAGOS);
	}
	
	public Boolean isAlta(){
		return this.equals(ALTA_EMITIDAS) 
			|| this.equals(ALTA_RECIBIDAS)
			|| this.equals(ALTA_INTRACOMUNITARIAS)
			|| this.equals(ALTA_INVERSION);
	}
	
	public Boolean isModificacion(){
		return this.equals(MOD_EMITIDAS) 
			|| this.equals(MOD_RECIBIDAS)
			|| this.equals(MOD_INTRACOMUNITARIAS)
			|| this.equals(MOD_INVERSION);	}
	
	public Boolean isBaja(){
		return this.equals(BAJA_EMITIDAS) 
			|| this.equals(BAJA_RECIBIDAS)
			|| this.equals(BAJA_INTRACOMUNITARIAS)
			|| this.equals(BAJA_INVERSION);
	}
	
}
