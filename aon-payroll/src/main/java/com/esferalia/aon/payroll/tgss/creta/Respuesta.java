package com.esferalia.aon.payroll.tgss.creta;

import java.util.NoSuchElementException;
import java.util.Optional;

import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Dato;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Fecha;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.LiquidacionMes;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Periodo;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Tramo;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.DatoSolicitadoBuilder;

public class Respuesta {
	
	
	private static class NoSuchDato extends Exception {
		
	}

	private static class NoSuchTramo extends Exception {
		
	}

	private static class NoSuchTrabajador extends Exception {
		
	}
 	
	private static class NoSuchLiquidacionMes extends Exception {
		
	}

	public static TrabajadoresTramos fixTrabajadoresTramos(TrabajadoresTramos tyt, net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta res) {
		
		
		for (Liquidacion resLiquidacion : res.getLiquidacion()) {
			for (LiquidacionMes resLiquidacionMes : resLiquidacion.getLiquidacionMes()) {
				
				try {
					net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes tytLiquidacionMes = 
					getLiquidacionMes(tyt.getLiquidacion(), resLiquidacionMes).orElseThrow(NoSuchLiquidacionMes::new);
					
					for ( Trabajador resTrabajador : resLiquidacionMes.getTrabajadores().getTrabajador()){
						try {
						net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador tytTrabajador = 
						getTrabajador(tytLiquidacionMes, resTrabajador).orElseThrow(NoSuchTrabajador::new);
							for ( Tramo resTramo : resTrabajador.getTramos().getTramo() ) {
								if ( isError(resTramo, "R9623")) { 
										// Tramo no incluido por no remitir datos obligatorios
										try {
											net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo tytTramo = 
											getTramo(tytTrabajador, resTramo).orElseThrow(NoSuchTramo::new);
											for ( Dato resDato : resTramo.getDatosTramo().getDato() ) {
												if ( isError(resDato, "R9582")) {
													// Datos solicitados obligatorios no informados
													getDatoSolicitado(tytTramo, resDato).ifPresentOrElse(d  -> {}, () -> {
														
														DatoSolicitado datoSolicitado = 
														new DatoSolicitadoBuilder()
														.setObligatorio(true)
														.setTipo(resDato.getTipoDato())
														.setCodigo(resDato.getCodigo())
														.create();

														tytTramo.getDatosTramo().getDatoSolicitado().add(datoSolicitado);
													
													});
												}
											}
									} catch ( NoSuchTramo e ) {
									}
								}
							}
						} catch ( NoSuchTrabajador e ) {
						}
					}
				} catch ( NoSuchLiquidacionMes e ) {
				}
			}
		}
		
		return tyt;
	}
	
	
	
	
	public static boolean isError( Dato dato, String codigo ) {
		return dato.getErrores().getError().stream().anyMatch(err -> err.getCodigoErr().equalsIgnoreCase(codigo));
	}

	public static boolean isError( Tramo tramo, String codigo ) {
		return tramo.getErrores().getError().stream().anyMatch(err -> err.getCodigoErr().equalsIgnoreCase(codigo));
	}
	
	public static Optional<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado> getDatoSolicitado( net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo tramo, Dato dato){
		return tramo.getDatosTramo().getDatoSolicitado().stream()
				.filter( d -> d.getTipoDato().equalsIgnoreCase(dato.getTipoDato()))
				.filter( d -> d.getCodigo().equalsIgnoreCase(dato.getCodigo()))
				.findFirst();
	}

	public static Optional<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo> getTramo( net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador trabajador, Tramo tramo){
		return trabajador.getTramos().getTramo().stream()
				.filter( t -> equals(t.getFechaDesde(), tramo.getFechaDesde()))
				.filter( t -> equals(t.getFechaHasta(), tramo.getFechaHasta()))
				.findFirst();
	}

	public static Optional<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador> getTrabajador( net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes liquidacionMes, Trabajador trabajador){
		return liquidacionMes.getTrabajadores().getTrabajador().stream().filter( t -> t.getNaf().equals(trabajador.getNaf())).findFirst();
	}

	public static Optional<net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes> getLiquidacionMes( net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Liquidacion liquidacion, LiquidacionMes liquidacionMes){
		return liquidacion.getLiquidacionMes().stream().filter( lm -> equals(lm.getMesLiquidativo(), liquidacionMes.getMesLiquidativo())).findFirst();
	}
	
	private  static boolean equals (net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Periodo p1,  Periodo p2) {
		return
		p1.getAnho().equals(p2.getAnho())
		&& p1.getMes().equals(p2.getMes());
	}

	private static boolean equals (net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Fecha f1,  Fecha f2) {
		return
		f1.getAnho().equals(f2.getAnho())
		&& f1.getMes().equals(f2.getMes())
		&& f1.getDia().equals(f2.getDia());
	}
	

}
