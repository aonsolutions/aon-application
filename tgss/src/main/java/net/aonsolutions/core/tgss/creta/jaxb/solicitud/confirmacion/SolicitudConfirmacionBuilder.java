package net.aonsolutions.core.tgss.creta.jaxb.solicitud.confirmacion;

import java.util.UUID;

import net.aonsolutions.core.tgss.creta.jaxb.AbstractLiquidacionBuilder;


public class SolicitudConfirmacionBuilder extends AbstractLiquidacionBuilder<SolicitudConfirmacionBuilder, Liquidacion, CtaCot, Periodo> {

	private int autorizado;
	

	public SolicitudConfirmacionBuilder setAutorizado(int autorizado) {
		this.autorizado = autorizado;
		return this;
	}
	
	public SolicitudConfirmacion createSolicitudConfirmacion() {
		SolicitudConfirmacion solicitud = new SolicitudConfirmacion();

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
