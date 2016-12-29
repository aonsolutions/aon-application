package net.aonsolutions.tgss.jaxb.trabajadorestramos;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.DatosMes;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.ObjectFactory;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Periodo;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Trabajador;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Trabajadores;

public class LiquidacionMesBuilder {

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

	private int anho;
	private Month mes;
	
	private List<DatoSolicitado> datos;
	private List<Trabajador> trabajadores;
	
	public LiquidacionMesBuilder() {
		datos = new ArrayList<DatoSolicitado>();
		trabajadores = new ArrayList<Trabajador>();
	}
	
	public LiquidacionMes create() {
		LiquidacionMes liquidacionMes = OBJECT_FACTORY.createLiquidacionMes();
		
		Periodo periodo = new Periodo();
		periodo.setAnho(String.format("%d",anho));
		periodo.setMes(String.format("%02d",mes.getValue()));
		liquidacionMes.setMesLiquidativo(periodo);
		
		if ( !datos.isEmpty() ) {
			DatosMes datosMes = OBJECT_FACTORY.createDatosMes();
			datosMes.getDatoSolicitado().addAll(datos);
			liquidacionMes.setDatosMes(datosMes);
			datos.clear();
		}
		
		if ( !trabajadores.isEmpty()  ) {
			Trabajadores trabajador3s = OBJECT_FACTORY.createTrabajadores();
			trabajador3s.getTrabajador().addAll(sort(trabajadores));
			liquidacionMes.setTrabajadores(trabajador3s);
			trabajadores.clear();
		}
		
		return liquidacionMes;
	}
	
	public void add(DatoSolicitado dato) {
		datos.add(dato);
	}

	public void add(Trabajador trabajador) {
		trabajadores.add(trabajador);
	}
	
	public LiquidacionMesBuilder setMes(String mes) {
		this.mes = Month.of(Integer.parseInt(mes));
		return this;
	}

	public LiquidacionMesBuilder setMes(Month mes) {
		this.mes = mes;
		return this;
	}

	public LiquidacionMesBuilder setAnho(int anho) {
		this.anho = anho;
		return this;
	}

	public LiquidacionMesBuilder setAnho(String anho) {
		this.anho = Integer.parseInt(anho);
		return this;
	}
	
	// ------------------------------------------------------------------------
	
	protected List<Trabajador> sort(List<Trabajador> trabajadores) {
		
		return trabajadores;
	}

}
