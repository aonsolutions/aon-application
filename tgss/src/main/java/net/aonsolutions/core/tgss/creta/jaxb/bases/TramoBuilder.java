package net.aonsolutions.core.tgss.creta.jaxb.bases;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class TramoBuilder {

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory(); 
	
	
	private int anhoDesde;
	private Month mesDesde;
	private int diaDesde;
	
	private int anhoHasta;
	private Month mesHasta;
	private int diaHasta;

	private int anhoDesdeAlta;
	private Month mesDesdeAlta;
	private int diaDesdeAlta;
	
	private int anhoHastaAlta;
	private Month mesHastaAlta;
	private int diaHastaAlta;
	
	private List<Dato> datos;
	
	public TramoBuilder() {
		this.datos = new ArrayList<Dato>();
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
		
		
		if ( mesDesdeAlta != null ) {
			Fecha fechaDesdeAlta = OBJECT_FACTORY.createFecha();
			fechaDesdeAlta.setAnho(String.format("%d",anhoDesdeAlta));
			fechaDesdeAlta.setMes(String.format("%02d",mesDesdeAlta.getValue()));
			fechaDesdeAlta.setDia(String.format("%02d",diaDesdeAlta));
			tramo.setFechaDesdeAlta(fechaDesdeAlta);
		}

		if ( mesHastaAlta != null ) {
			Fecha fechaHastaAlta = OBJECT_FACTORY.createFecha();
			fechaHastaAlta.setAnho(String.format("%d",anhoHastaAlta));
			fechaHastaAlta.setMes(String.format("%02d",mesHastaAlta.getValue()));
			fechaHastaAlta.setDia(String.format("%02d",diaHastaAlta));
			tramo.setFechaDesdeAlta(fechaHastaAlta);
		}
		
//		tramo.setMarcaBorrado(marcaBorardor);
		
		DatosTramo datosTramo = OBJECT_FACTORY.createDatosTramo();
		datosTramo.getDato().addAll(datos);
		tramo.setDatosTramo(datosTramo);
		datos.clear();
		
		return tramo;
	}
	
	
	public void addDato(Dato dato) {
		datos.add(dato);
	}

	public TramoBuilder setDiaDesde(int diaDesde) {
		this.diaDesde = diaDesde;
		return this;
	}

	public TramoBuilder setDiaDesde(String diaDesde) {
		this.diaDesde = Integer.parseInt(diaDesde);
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
	
	
	
	public TramoBuilder setDiaDesdeAlta(int diaDesdeAlta) {
		this.diaDesdeAlta = diaDesdeAlta;
		return this;
	}

	public TramoBuilder setDiaDesdeAlta(String diaDesdeAlta) {
		this.diaDesdeAlta = Integer.parseInt(diaDesdeAlta);
		return this;
	}

	public TramoBuilder setMesDesdeAlta(String mesDesdeAlta) {
		this.mesDesdeAlta = Month.of(Integer.parseInt(mesDesdeAlta));
		return this;
	}

	public TramoBuilder setMesDesdeAlta(Month mesDesdeAlta) {
		this.mesDesdeAlta = mesDesde;
		return this;
	}

	public TramoBuilder setAnhoDesdeAlta(int anhoDesdeAlta) {
		this.anhoDesdeAlta = anhoDesdeAlta;
		return this;
	}

	public TramoBuilder setAnhoDesdeAlta(String anhoDesdeAlta) {
		this.anhoDesdeAlta = Integer.parseInt(anhoDesdeAlta);
		return this;
	}
	
	
	
	public TramoBuilder setDiaHastaAlta(int diaHastaAlta) {
		this.diaHastaAlta = diaHastaAlta;
		return this;
	}

	public TramoBuilder setDiaHastaAlta(String diaHastaAlta) {
		this.diaHastaAlta = Integer.parseInt(diaHastaAlta);
		return this;
	}
	public TramoBuilder setMesHastaAlta(String mesHastaAlta) {
		this.mesHastaAlta = Month.of(Integer.parseInt(mesHastaAlta));
		return this;
	}

	public TramoBuilder setMesHastaAlta(Month mesHastaAlta) {
		this.mesHastaAlta = mesHastaAlta;
		return this;
	}

	public TramoBuilder setAnhoHastaAlta(int anhoHastaAlta) {
		this.anhoHastaAlta = anhoHastaAlta;
		return this;
	}

	public TramoBuilder setAnhoHastaAlta(String anhoHastaAlta) {
		this.anhoHastaAlta = Integer.parseInt(anhoHastaAlta);
		return this;
	}
	
}
