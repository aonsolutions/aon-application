package net.aonsolutions.tgss.creta.jaxb.solicitud.calculos;

import java.time.Month;
import java.util.UUID;

import net.aonsolutions.tgss.creta.jaxb.AbstractLiquidacionBuilder;
import net.aonsolutions.tgss.creta.jaxb.solicitud.confirmacion.SolicitudConfirmacion;


public class SolicitudCalculosBuilder extends AbstractLiquidacionBuilder<SolicitudCalculosBuilder, Liquidacion, CtaCot, Periodo> {

	private int autorizado;
	
	private int anhoPresentacion;
	private Month mesPresentacion;
	private boolean indicadorCalculosDesglosados;
	
	public SolicitudCalculosBuilder setAutorizado(int autorizado) {
		this.autorizado = autorizado;
		return this;
	}
	
	public SolicitudCalculos createSolicitudCalculos() {
		SolicitudCalculos solicitud = new SolicitudCalculos();

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
		Liquidacion liquidacion = new Liquidacion();

		Periodo periodoPresentacion = newPeriodo();
		periodoPresentacion.setAnho(String.format("%d",anhoPresentacion));
		periodoPresentacion.setMes(String.format("%02d",mesPresentacion.getValue()));
		liquidacion.setPeriodoPresentacion(periodoPresentacion);
		
		if ( indicadorCalculosDesglosados )
			liquidacion.setIndicadorCalculosDesglosados("S");
		
		return liquidacion;
	}
	
	
	// -------------------------------------------------------------------------
	
	private static String createReferenciaExterna() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	public SolicitudCalculosBuilder setMesPresentacion(Month mesPresentacion) {
		this.mesPresentacion = mesPresentacion;
		return this;
	}
	
	public SolicitudCalculosBuilder setAnhoPresentacion(int anhoPresentacion) {
		this.anhoPresentacion = anhoPresentacion;
		return this;
	}
	

	public SolicitudCalculosBuilder setIndicadorCalculosDesglosados(
			boolean indicadorCalculosDesglosados) {
		this.indicadorCalculosDesglosados = indicadorCalculosDesglosados;
		return this;
	}

}
