package com.esferalia.aon.payroll.tgss.creta;

import static com.esferalia.aon.payroll.tgss.creta.Respuesta.getDatoSolicitado;
import static com.esferalia.aon.payroll.tgss.creta.Respuesta.getLiquidacionMes;
import static com.esferalia.aon.payroll.tgss.creta.Respuesta.getTrabajador;
import static com.esferalia.aon.payroll.tgss.creta.Respuesta.getTramo;
import static com.esferalia.aon.payroll.tgss.creta.Respuesta.isError;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Dato;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.LiquidacionMes;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Tramo;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos;

public class RespuestaTestCase {
	
	@Test
	public void respuestaTestI() throws IOException, JAXBException {
		try ( InputStream tytIs = RespuestaTestCase.class.getResourceAsStream("TrabajadoresYTramosI.xml"); 
				InputStream resIs = RespuestaTestCase.class.getResourceAsStream("RespuestaI.xml")){
			Respuesta res = Utils.unmarshal(Respuesta.class, resIs);
			TrabajadoresTramos tyt = Utils.unmarshal(TrabajadoresTramos.class, tytIs);
			respuestaTest(res, tyt);
		}
	}

	@Test
	public void respuestaTestII() throws IOException, JAXBException {
		try ( InputStream tytIs = RespuestaTestCase.class.getResourceAsStream("TrabajadoresYTramosII.xml"); 
				InputStream resIs = RespuestaTestCase.class.getResourceAsStream("RespuestaII.xml")){
			Respuesta res = Utils.unmarshal(Respuesta.class, resIs);
			TrabajadoresTramos tyt = Utils.unmarshal(TrabajadoresTramos.class, tytIs);
			respuestaTest(res, tyt);
		}
	}
	private void respuestaTest(Respuesta res, TrabajadoresTramos tyt) {
		tyt = com.esferalia.aon.payroll.tgss.creta.Respuesta.fixTrabajadoresTramos(tyt, res);
		
		for (Liquidacion resLiquidacion : res.getLiquidacion()) {
			
			for (LiquidacionMes resLiquidacionMes : resLiquidacion.getLiquidacionMes()) {
				net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes tytLiquidacionMes = getLiquidacionMes(tyt.getLiquidacion(), resLiquidacionMes).orElseThrow();
				for ( Trabajador resTrabajador : resLiquidacionMes.getTrabajadores().getTrabajador()){
					net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador tytTrabajador = getTrabajador(tytLiquidacionMes, resTrabajador).orElseThrow(() -> new NoSuchElementException(resTrabajador.getNaf()));
					for ( Tramo resTramo : resTrabajador.getTramos().getTramo() ) {
						if ( isError(resTramo, "R9623")) { 
							// Tramo no incluido por no remitir datos obligatorios
							net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo tytTramo = getTramo(tytTrabajador, resTramo).orElseThrow();
							for ( Dato resDato : resTramo.getDatosTramo().getDato() ) {
								if ( isError(resDato, "R9582")) {
									// Datos solicitados obligatorios no informados
									DatoSolicitado tytDato = getDatoSolicitado(tytTramo, resDato).orElseThrow( () -> new NoSuchElementException(resDato.getTipoDato()+resDato.getCodigo()));
									assertEquals("B", tytDato.getIndicadorObligatoriedad());
								}
							}
						}
					}
				}
			}
			
			for ( LiquidacionMes resLiquidacionMes : com.esferalia.aon.payroll.tgss.creta.Respuesta.getLiquidacionMesNoTratados(resLiquidacion) ) {
				net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes tytLiquidacionMes = getLiquidacionMes(tyt.getLiquidacion(), resLiquidacionMes).orElseThrow();
				for ( Trabajador resTrabajador : resLiquidacionMes.getTrabajadores().getTrabajador()){
					net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador tytTrabajador = getTrabajador(tytLiquidacionMes, resTrabajador).orElseThrow(() -> new NoSuchElementException(resTrabajador.getNaf()));
					for ( Tramo resTramo : resTrabajador.getTramos().getTramo() ) {
						if ( isError(resTramo, "R9503")) { 
							// Tramo inexistente en Afiliaci�n para ese trabajador
							getTramo(tytTrabajador, resTramo).ifPresent(t -> fail("Tramo inexistente en Afiliaci�n para ese trabajador"));
						}
					}
				}
			
			}
		}
		
	}

}
