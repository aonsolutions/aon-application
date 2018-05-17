package net.aonsolutions.core.tgss.creta.jaxb.bases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;



public class BasesBuilder {
	
	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory(); 
	

	private int autorizado;
	
	private List<Liquidacion> liquidaciones;

	public BasesBuilder() {
		liquidaciones = new ArrayList<Liquidacion>();
	}
	
	
	public Bases create(){
		Bases bases = OBJECT_FACTORY.createBases();

		bases.setAutorizado(String.format("%08d",autorizado));
		bases.setReferenciaExterna(createReferenciaExterna());
		
		bases.getLiquidacion().addAll(liquidaciones);
		
		liquidaciones.clear();
		
		return bases;
	}
	
	public BasesBuilder addLiquidacion(Liquidacion liquidacion){
		liquidaciones.add(liquidacion);
		
		return this;
	}
	
	public BasesBuilder addLiquidaciones(Collection<Liquidacion> liquidaciones){
		this.liquidaciones.addAll(liquidaciones);
		
		return this;
	}

	public BasesBuilder setAutorizado(int autorizado) {
		this.autorizado = autorizado;
		return this;
	}

	public BasesBuilder setAutorizado(String autorizado) {
		this.autorizado = Integer.parseInt(autorizado);
		return this;
	}
	
	// -------------------------------------------------------------------------

	private static String createReferenciaExterna() {
		return UUID.randomUUID().toString().substring(0, 8);
	}
	
}
