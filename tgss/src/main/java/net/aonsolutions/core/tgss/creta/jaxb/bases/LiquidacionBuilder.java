package net.aonsolutions.core.tgss.creta.jaxb.bases;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class LiquidacionBuilder {
	
	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory(); 
	
	// Liquidacion
	private String ccc;
	private String tipo;
	private int anhoDesde;
	private Month mesDesde;
	private int anhoHasta;
	private Month mesHasta;
	private int anhoControl;
	private Month mesControl;
	private String cccConcertado;
	private boolean aceptarBasesAnteriores;

	private List<Dato> datosLiquidacion;
	private List<LiquidacionMes> liquidacionesMes;

	public LiquidacionBuilder() {
		datosLiquidacion = new ArrayList<Dato>();
		liquidacionesMes = new ArrayList<LiquidacionMes>();
		
	}
	
	
	
	public Liquidacion create(){
		Liquidacion liquidacion = OBJECT_FACTORY.createLiquidacion();
		
		CtaCot ctaCot = OBJECT_FACTORY.createCtaCot();
		ctaCot.setRegimen(ccc.substring(0,4));
		ctaCot.setProvincia(ccc.substring(4,6));
		ctaCot.setNumero(ccc.substring(6));
		liquidacion.setCcc(ctaCot);
		
		Periodo periodoDesde = new Periodo();
		periodoDesde.setAnho(String.format("%d",anhoDesde));
		periodoDesde.setMes(String.format("%02d",mesDesde.getValue()));
		liquidacion.setPeriodoDesde(periodoDesde);
		
		Periodo periodoHasta = new Periodo();
		periodoHasta.setAnho(String.format("%d",anhoHasta));
		periodoHasta.setMes(String.format("%02d",mesHasta.getValue()));
		liquidacion.setPeriodoHasta(periodoHasta);
		
		liquidacion.setTipo(tipo);
		
		if ( "L00".indexOf(tipo) >= 0 && aceptarBasesAnteriores ) {
			liquidacion.setAceptarBasesAnteriores("S");
		}

		if ( "L03,C03".indexOf(tipo) >= 0 ) {
			Periodo periodoControl = new Periodo();
			periodoControl.setAnho(String.format("%d",anhoControl));
			periodoControl.setMes(String.format("%02d",mesControl.getValue()));
			liquidacion.setFechaControl(periodoControl);
		}
		
		if ( "C00, C02, C03, C13, C90 y C91".indexOf(tipo) >= 0 ) {
			CtaCot ctaCotConertado = OBJECT_FACTORY.createCtaCot();
			ctaCotConertado.setRegimen(cccConcertado.substring(0,4));
			ctaCotConertado.setRegimen(cccConcertado.substring(4,6));
			ctaCotConertado.setRegimen(cccConcertado.substring(6));
			liquidacion.setCcc(ctaCotConertado);
		}
		
		if ( !datosLiquidacion.isEmpty() ) {
			liquidacion.setDatosLiquidacion(OBJECT_FACTORY.createDatosLiquidacion());
			liquidacion.getDatosLiquidacion().getDato().addAll(datosLiquidacion);
			datosLiquidacion.clear();
		}
		
		liquidacion.getLiquidacionMes().addAll(liquidacionesMes);
		liquidacionesMes.clear();
		
		return liquidacion;
	}
	
	
	public LiquidacionBuilder addDato ( Dato dato ) {
		datosLiquidacion.add(dato);
		return this;
	}
	
	public LiquidacionBuilder addLiquidacionMes(LiquidacionMes liquidacionMes){
		liquidacionesMes.add(liquidacionMes);
		return this;
	}


	
	public LiquidacionBuilder setCCC(String ccc) {
		this.ccc = ccc;
		return this;
	}
	
	public LiquidacionBuilder setCCC(
			String regimen, 
			String provincia, 
			String numero) {
		this.ccc = String.format("%s%s%s", regimen, provincia, numero);
		return this;
	}

	public LiquidacionBuilder setTipo(String tipo) {
		this.tipo = tipo;
		return this;
	}

	public LiquidacionBuilder setMesDesde(int mesDesde) {
		this.mesDesde = Month.of(mesDesde);
		return this;
	}
	
	public LiquidacionBuilder setMesDesde(String mesDesde) {
		this.mesDesde = Month.of(Integer.parseInt(mesDesde));
		return this;
	}

	public LiquidacionBuilder setMesDesde(Month mesDesde) {
		this.mesDesde = mesDesde;
		return this;
	}

	public LiquidacionBuilder setAnhoDesde(int anhoDesde) {
		this.anhoDesde = anhoDesde;
		return this;
	}

	public LiquidacionBuilder setAnhoDesde(String anhoDesde) {
		this.anhoDesde = Integer.parseInt(anhoDesde);
		return this;
	}
	
	public LiquidacionBuilder setMesHasta(String mesHasta) {
		this.mesHasta = Month.of(Integer.parseInt(mesHasta));
		return this;
	}

	public LiquidacionBuilder setMesHasta(Month mesHasta) {
		this.mesHasta = mesHasta;
		return this;
	}

	public LiquidacionBuilder setAnhoHasta(int anhoHasta) {
		this.anhoHasta = anhoHasta;
		return this;
	}

	public LiquidacionBuilder setAnhoHasta(String anhoHasta) {
		this.anhoHasta = Integer.parseInt(anhoHasta);
		return this;
	}
	
	public LiquidacionBuilder setMesControl(Month mesControl) {
		this.mesControl = mesControl;
		return this;
	}

	public LiquidacionBuilder setMesControl(String mesControl) {
		this.mesControl = Month.of(Integer.parseInt(mesControl));
		return this;
	}

	public LiquidacionBuilder setAnhoControl(int anhoControl) {
		this.anhoControl = anhoControl;
		return this;
	}

	public LiquidacionBuilder setAnhoControl(String anhoControl) {
		this.anhoControl = Integer.parseInt(anhoControl);
		return this;
	}

	public LiquidacionBuilder setCCCConcertado(
			String regimen, 
			String provincia, 
			String numero) {
		this.cccConcertado = String.format("%s%s%s", regimen, provincia, numero);
		return this;
	}
	
	public LiquidacionBuilder setAceptarBasesAnteriores(boolean aceptarBasesAnteriores) {
		this.aceptarBasesAnteriores = aceptarBasesAnteriores;
		return this;
	}
	// -------------------------------------------------------------------------

	private static String createReferenciaExterna() {
		return UUID.randomUUID().toString().substring(0, 8);
	}
	
}
