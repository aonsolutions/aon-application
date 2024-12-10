package net.aonsolutions.core.tgss.jaxb.trabajadorestramos;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import net.aonsolutions.core.tgss.creta.jaxb.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatosTramo;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Fecha;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.InformacionAfiliacion;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.ObjectFactory;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo;

public class TramoBuilder {

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory(); 
	
	
	private int anhoDesde;
	private Month mesDesde;
	private int diaDesde;
	
	private int anhoHasta;
	private Month mesHasta;
	private int diaHasta;

	private int diasCotizados;
	
	private int grupoCotizacion;
	private String tipoDeContrato;
	
	private List<DatoSolicitado> datos;
	
	public TramoBuilder() {
		this.datos = new ArrayList<DatoSolicitado>();
	}

	public Tramo create() {
		Tramo tramo = OBJECT_FACTORY.createTramo();
		
		Fecha fechaDesde = OBJECT_FACTORY.createFecha();
		fechaDesde.setAnho(String.format("%d",anhoDesde));
		fechaDesde.setMes(String.format("%02d",mesDesde.getValue()));
		fechaDesde.setDia(String.format("%02d",diaDesde));
		tramo.setFechaDesde(fechaDesde);

		Fecha fechaHasta = OBJECT_FACTORY.createFecha();
		fechaHasta.setAnho(String.format("%d",anhoHasta));
		fechaHasta.setMes(String.format("%02d",mesHasta.getValue()));
		fechaHasta.setDia(String.format("%02d",diaHasta));
		tramo.setFechaHasta(fechaHasta);
		
		tramo.setDiasCotizados(String.format("%d",diasCotizados));
		
		DatosTramo datosTramo = OBJECT_FACTORY.createDatosTramo();
		datosTramo.getDato().addAll(datos);
		tramo.setDatosTramo(datosTramo);
		datos.clear();
		
		InformacionAfiliacion informacionAfiliacion = OBJECT_FACTORY.createInformacionAfiliacion();
		informacionAfiliacion.setTipoContrato(tipoDeContrato);
		informacionAfiliacion.setGrupoCotizacion(String.format("%02d",grupoCotizacion));
		tramo.setInformacionAfiliacion(informacionAfiliacion);
		
		return tramo;
	}
	
	public boolean isEmpty() {
		return datos.isEmpty();
	}
	
	
	public TramoBuilder addDato(DatoSolicitado dato) {
		datos.add(dato);
		return this;
	}
	
	public TramoBuilder setTipoDeContrato(String tipoDeContrato) {
		this.tipoDeContrato = tipoDeContrato;
		return this;
	}
	
	public TramoBuilder setDiaDesde(int diaDesde) {
		this.diaDesde = diaDesde;
		return this;
	}

	public TramoBuilder setDiaDesde(String diaDesde) {
		this.diaDesde = Integer.parseInt(diaDesde);
		return this;
	}

	public TramoBuilder setMesDesde(int mesDesde) {
		this.mesDesde = Month.of(mesDesde);
		return this;
	}

	public TramoBuilder setMesDesde(String mesDesde) {
		this.mesDesde = Month.of(Integer.parseInt(mesDesde));
		return this;
	}

	public TramoBuilder setMesDesde(Month mesDesde) {
		this.mesDesde = mesDesde;
		return this;
	}

	public TramoBuilder setAnhoDesde(int anhoDesde) {
		this.anhoDesde = anhoDesde;
		return this;
	}

	public TramoBuilder setAnhoDesde(String anhoDesde) {
		this.anhoDesde = Integer.parseInt(anhoDesde);
		return this;
	}
	
	
	public TramoBuilder setDiaHasta(int diaHasta) {
		this.diaHasta = diaHasta;
		return this;
	}

	public TramoBuilder setDiaHasta(String diaHasta) {
		this.diaHasta = Integer.parseInt(diaHasta);
		return this;
	}

	public TramoBuilder setMesHasta(int mesHasta) {
		this.mesHasta = Month.of(mesHasta);
		return this;
	}

	public TramoBuilder setMesHasta(String mesHasta) {
		this.mesHasta = Month.of(Integer.parseInt(mesHasta));
		return this;
	}

	public TramoBuilder setMesHasta(Month mesHasta) {
		this.mesHasta = mesHasta;
		return this;
	}

	public TramoBuilder setAnhoHasta(int anhoHasta) {
		this.anhoHasta = anhoHasta;
		return this;
	}

	public TramoBuilder setAnhoHasta(String anhoHasta) {
		this.anhoHasta = Integer.parseInt(anhoHasta);
		return this;
	}
	
	public TramoBuilder setDiasCotizados(int diasCotizados) {
		this.diasCotizados = diasCotizados;
		return this;
	}
	
	public TramoBuilder setGrupoCotizacion(int grupoCotizacion) {
		this.grupoCotizacion = grupoCotizacion;
		return this;
	}

	public TramoBuilder setGrupoCotizacion(String grupoCotizacion) {
		this.grupoCotizacion = Integer.parseInt(grupoCotizacion);
		return this;
	}
}
