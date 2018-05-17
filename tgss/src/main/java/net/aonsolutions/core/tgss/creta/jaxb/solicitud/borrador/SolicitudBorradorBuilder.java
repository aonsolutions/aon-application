package net.aonsolutions.core.tgss.creta.jaxb.solicitud.borrador;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class SolicitudBorradorBuilder {

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

	private String ccc;
	private int autorizado;
	private String cccConcertado;
	private String tipo;
	private int anhoDesde;
	private Month mesDesde;
	private int anhoHasta;
	private Month mesHasta;
	private int anhoControl;
	private Month mesControl;
	private boolean aceptarBasesAnteriores;
	
	private List<Liquidacion> liquidaciones ;
	
	public SolicitudBorradorBuilder() {
		this.liquidaciones = new ArrayList<Liquidacion>();
	}

	public SolicitudBorrador createSolicitudBorrador() {
		SolicitudBorrador solicitudBorrador = OBJECT_FACTORY
				.createSolicitudBorrador();

		solicitudBorrador.setAutorizado(String.format("%08d",autorizado));
		solicitudBorrador.setReferenciaExterna(createReferenciaExterna());
		
		solicitudBorrador.getLiquidacion().addAll(liquidaciones);
		
		return solicitudBorrador;
	}

	public SolicitudBorradorBuilder addLiquidacion() {
		Liquidacion liquidacion = OBJECT_FACTORY.createLiquidacion();
		liquidaciones.add(liquidacion);
		
		liquidacion.setTipo(tipo);

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
		
		return this;
	}
	
	
	public SolicitudBorradorBuilder setTipo(String tipo) {
		this.tipo = tipo;
		return this;
	}

	public SolicitudBorradorBuilder setCCC(String ccc) {
		this.ccc = ccc;
		return this;
	}
	
	public SolicitudBorradorBuilder setMesDesde(Month mesDesde) {
		this.mesDesde = mesDesde;
		return this;
	}
	
	public SolicitudBorradorBuilder setAnhoDesde(int anhoDesde) {
		this.anhoDesde = anhoDesde;
		return this;
	}
	
	public SolicitudBorradorBuilder setMesHasta(Month mesHasta) {
		this.mesHasta = mesHasta;
		return this;
	}

	public SolicitudBorradorBuilder setAnhoHasta(int anhoHasta) {
		this.anhoHasta = anhoHasta;
		return this;
	}
	
	public SolicitudBorradorBuilder setMesControl(Month mesControl) {
		this.mesControl = mesControl;
		return this;
	}

	public SolicitudBorradorBuilder setAnhoControl(int anhoControl) {
		this.anhoControl = anhoControl;
		return this;
	}

	public SolicitudBorradorBuilder setAutorizado(int autorizado) {
		this.autorizado = autorizado;
		return this;
	}

	public SolicitudBorradorBuilder setCCCConcertado(String cccConcertado) {
		this.cccConcertado = cccConcertado;
		return this;
	}
	
	public SolicitudBorradorBuilder setAceptarBasesAnteriores(boolean aceptarBasesAnteriores) {
		this.aceptarBasesAnteriores = aceptarBasesAnteriores;
		return this;
	}

	// ------------------------------------------------------------------------

	private static String createReferenciaExterna() {
		return UUID.randomUUID().toString().substring(0, 8);
	}
	
	// ------------------------------------------------------------------------
	public static SolicitudBorrador createL00(int autorizado,
			Month mes, int anho, boolean aceptarBasesAnteriores, String... cccs)
			throws DatatypeConfigurationException {
		SolicitudBorradorBuilder builder = 
				new SolicitudBorradorBuilder()
		.setAutorizado(autorizado);

		for ( String ccc: cccs ) {
			builder
			.setCCC(ccc)
			.setTipo("L00")
			.setMesDesde(mes)
			.setAnhoDesde(anho)
			.setMesHasta(mes)
			.setAnhoHasta(anho)
			.setAceptarBasesAnteriores(aceptarBasesAnteriores)
			.addLiquidacion()
			;
		}
		return builder.createSolicitudBorrador();
	}

}
