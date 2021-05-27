package net.aonsolutions.core.tgss.creta.jaxb.solicitud.trabajadorestramos;

import java.util.UUID;

import net.aonsolutions.core.tgss.creta.jaxb.AbstractLiquidacionBuilder;

public class SolicitudTrabajadoresTramosBuilder extends AbstractLiquidacionBuilder<SolicitudTrabajadoresTramosBuilder, Liquidacion, CtaCot, Periodo> {

	private int autorizado;
	

	public SolicitudTrabajadoresTramosBuilder setAutorizado(int autorizado) {
		this.autorizado = autorizado;
		return this;
	}
	
	public SolicitudTrabajadoresTramos createSolicitudBorrador() {
		SolicitudTrabajadoresTramos solicitud = new SolicitudTrabajadoresTramos();

		solicitud.setAutorizado(String.format("%08d",autorizado));
		solicitud.setReferenciaExterna(createReferenciaExterna());
		
		solicitud.getLiquidacion().addAll(getLiquidaciones());
		
		return solicitud;
	}

	// -------------------------------------------------------------------------
	// AbstractLiquidacionBuilder
	
	@Override
	protected CtaCot newCtaCot() {
		return new CtaCot();
	}
	
	@Override
	protected Periodo newPeriodo() {
		return new Periodo();
	}
	
	@Override
	protected Liquidacion newLiquidacion() {
		return new Liquidacion();
	}
	
	// -------------------------------------------------------------------------

	private static String createReferenciaExterna() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

}
