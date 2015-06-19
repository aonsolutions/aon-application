package net.aonsolutions.tgss.creta.jaxb.solicitud.trabajadorestramos;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import net.aonsolutions.tgss.creta.jaxb.Utils;

public class SolicitudTrabajadoresTramosBuilder {

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
	private List<Liquidacion> liquidaciones ;
	
	public SolicitudTrabajadoresTramosBuilder() {
		liquidaciones = new ArrayList<Liquidacion>();
	}
	
	public SolicitudTrabajadoresTramos createSolicitudBorrador() {
		SolicitudTrabajadoresTramos solicitud = OBJECT_FACTORY
				.createSolicitudTrabajadoresTramos();

		solicitud.setAutorizado(String.format("%08d",autorizado));
		solicitud.setReferenciaExterna(createReferenciaExterna());
		
		solicitud.getLiquidacion().addAll(liquidaciones);
		
		return solicitud;
	}

	public SolicitudTrabajadoresTramosBuilder addLiquidacion() {
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


		if ( "L03,C03".indexOf(tipo) >= 0 ) {
			Periodo periodoControl = new Periodo();
			periodoControl.setAnho(String.format("%d",anhoControl));
			periodoControl.setMes(String.format("%02d",mesControl.getValue()));
			liquidacion.setPeriodoHasta(periodoControl);
		}
		
		if ( "C00, C02, C03, C13, C90 y C91".indexOf(tipo) >= 0 ) {
			CtaCot ctaCotConertado = OBJECT_FACTORY.createCtaCot();
			ctaCotConertado.setRegimen(cccConcertado.substring(0,4));
			ctaCotConertado.setProvincia(cccConcertado.substring(4,6));
			ctaCotConertado.setNumero(cccConcertado.substring(6));
			liquidacion.setCcc(ctaCotConertado);
		}
		
		return this;
	}

	public SolicitudTrabajadoresTramosBuilder setTipo(String tipo) {
		this.tipo = tipo;
		return this;
	}

	public SolicitudTrabajadoresTramosBuilder setCCC(String ccc) {
		this.ccc = ccc;
		return this;
	}
	
	public SolicitudTrabajadoresTramosBuilder setMesDesde(Month mesDesde) {
		this.mesDesde = mesDesde;
		return this;
	}
	
	public SolicitudTrabajadoresTramosBuilder setAnhoDesde(int anhoDesde) {
		this.anhoDesde = anhoDesde;
		return this;
	}
	
	public SolicitudTrabajadoresTramosBuilder setMesHasta(Month mesHasta) {
		this.mesHasta = mesHasta;
		return this;
	}

	public SolicitudTrabajadoresTramosBuilder setAnhoHasta(int anhoHasta) {
		this.anhoHasta = anhoHasta;
		return this;
	}
	
	public SolicitudTrabajadoresTramosBuilder setMesControl(Month mesControl) {
		this.mesControl = mesControl;
		return this;
	}

	public SolicitudTrabajadoresTramosBuilder setAnhoControl(int anhoControl) {
		this.anhoControl = anhoControl;
		return this;
	}

	public SolicitudTrabajadoresTramosBuilder setAutorizado(int autorizado) {
		this.autorizado = autorizado;
		return this;
	}

	public SolicitudTrabajadoresTramosBuilder setCCCConcertado(String cccConcertado) {
		this.cccConcertado = cccConcertado;
		return this;
	}
	// -------------------------------------------------------------------------

	public static SolicitudTrabajadoresTramos createL00(int autorizado,
			Month month, int year, String... cccs)
			throws DatatypeConfigurationException {
		SolicitudTrabajadoresTramosBuilder builder = 
				new SolicitudTrabajadoresTramosBuilder()
		.setAutorizado(autorizado);

		for ( String ccc: cccs ) {
			builder
			.setCCC(ccc)
			.setTipo("L00")
			.setMesDesde(month)
			.setAnhoDesde(year)
			.setMesHasta(month)
			.setAnhoHasta(year)
			.addLiquidacion()
			;
		}
		return builder.createSolicitudBorrador();
	}

	// -------------------------------------------------------------------------

	private static String createReferenciaExterna() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	// -------------------------------------------------------------------------

	public static void main(String[] args) throws JAXBException,
			DatatypeConfigurationException {
		Utils.marshal(
				createL00(228115, Month.MAY, 2015, "011101105360062",
						"011101105577910"), System.out);
	}
}
