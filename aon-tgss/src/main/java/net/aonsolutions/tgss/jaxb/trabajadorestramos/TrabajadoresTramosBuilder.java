package net.aonsolutions.tgss.jaxb.trabajadorestramos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import net.aonsolutions.tgss.creta.jaxb.AbstractLiquidacionBuilder;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.CtaCot;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.DatosLiquidacion;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Fecha;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.FechaHoraRecaudacion;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Liquidacion;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.ObjectFactory;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.Periodo;
import net.aonsolutions.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos;


public class TrabajadoresTramosBuilder extends AbstractLiquidacionBuilder<TrabajadoresTramosBuilder, Liquidacion, CtaCot, Periodo> {
	
	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory(); 
	

	private int autorizado;
	
	private List<DatoSolicitado> datosSolicitados;
	private List<LiquidacionMes> liquidacionesMes;
	
	public TrabajadoresTramosBuilder() {
		datosSolicitados = new ArrayList<DatoSolicitado>();
		liquidacionesMes = new ArrayList<LiquidacionMes>();
	}
	
	// --------------------------------------------- AbstractLiquidacionBuilder
	@Override
	protected CtaCot newCtaCot() {
		return OBJECT_FACTORY.createCtaCot();
	}

	@Override
	protected Periodo newPeriodo() {
		return OBJECT_FACTORY.createPeriodo();
	}
	@Override
	protected Liquidacion newLiquidacion() {
		return  OBJECT_FACTORY.createLiquidacion();
	}
	
	
	
	public TrabajadoresTramos create() {
		TrabajadoresTramos trabajadoresTramos = OBJECT_FACTORY.createTrabajadoresTramos();
		
		trabajadoresTramos.setAutorizado(String.format("%08d",autorizado));
		trabajadoresTramos.setReferenciaExterna(createReferenciaExterna());
		
		Liquidacion  liquidacion = createLiquidacion();
		Calendar calendarRecaudacion = Calendar.getInstance(); // NOW
		FechaHoraRecaudacion fechaHoraRecaudacion = OBJECT_FACTORY.createFechaHoraRecaudacion();
		Fecha fechaReacudacion = OBJECT_FACTORY.createFecha();
		fechaReacudacion.setAnho(String.format("%d",calendarRecaudacion.get(Calendar.YEAR)));
		fechaReacudacion.setMes(String.format("%02d",calendarRecaudacion.get(Calendar.MONTH)+1));
		fechaReacudacion.setDia(String.format("%02d",calendarRecaudacion.get(Calendar.DAY_OF_MONTH)));
		fechaHoraRecaudacion.setFechaRecaudacion(fechaReacudacion);
		fechaHoraRecaudacion.setHoraRecaudacion(String.format("%02d%02d%02d", 
				calendarRecaudacion.get(Calendar.HOUR_OF_DAY),
				calendarRecaudacion.get(Calendar.MINUTE),
				calendarRecaudacion.get(Calendar.SECOND)
				));
		
		liquidacion.setFechaHoraRecaudacion(fechaHoraRecaudacion);

		DatosLiquidacion datosLiquidacion = OBJECT_FACTORY.createDatosLiquidacion();
		datosLiquidacion.getDatoSolicitado().addAll(datosSolicitados);
		liquidacion.setDatosLiquidacion(datosLiquidacion);

		liquidacion.getLiquidacionMes().addAll(liquidacionesMes);
		trabajadoresTramos.setLiquidacion(liquidacion);
		
		return trabajadoresTramos;
	}
	
	
	public TrabajadoresTramosBuilder setAutorizado(int autorizado) {
		this.autorizado = autorizado;
		return this;
	}
	

	public TrabajadoresTramosBuilder setAutorizado(String autorizado) {
		this.autorizado = Integer.parseInt(autorizado);
		return this;
	}
	
	public TrabajadoresTramosBuilder addDatoSolicitado(DatoSolicitado datoSolicitado){
		datosSolicitados.add(datoSolicitado);
		return this;
	}
	
	public TrabajadoresTramosBuilder addLiquidacionMes(LiquidacionMes liquidacionMes) {
		liquidacionesMes.add(liquidacionMes);
		return this;
	}
	// -------------------------------------------------------------------------

	private static String createReferenciaExterna() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

}
