package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.watson.util.AonDateUtils.get;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.DatoSolicitadoBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.LiquidacionMesBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadorBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadorBuilder.TipoIpf;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TrabajadoresTramosBuilder;
import net.aonsolutions.core.tgss.jaxb.trabajadorestramos.TramoBuilder;

public class CretaListener implements IdcParserListener {
	
	private static final class CretaTramoBuilder extends TramoBuilder {
		Map<String, DatoSolicitado> datosMap = new HashMap<>();
		List<Predicate<DatoSolicitado>> filters = new ArrayList<>();

		public void clear() {
			datosMap.clear();
		}
		
		public boolean isEmpty() {
			return datosMap.isEmpty();
		}
		
		@Override
		public TramoBuilder addDato(DatoSolicitado dato) {
		    	if (!filter(dato))
		    	    datosMap.putIfAbsent(getKey(dato), dato);
			return this;
		}

		@Override
		public Tramo create() {
			datosMap.values().forEach(super::addDato);
			return super.create();
		}

		protected void clear(Predicate<DatoSolicitado> filter) {
		    	List<String> clear = datosMap.keySet().stream()
		    	.filter(key -> filter.test(datosMap.get(key))).toList();
		    	clear.forEach(datosMap::remove);
		}

		private String getKey(DatoSolicitado dato) {
			return dato.getTipoDato() + dato.getCodigo();
		}
		
		protected void filter(Predicate<DatoSolicitado> filter) {
		    clear(filter);
		    filters.add(filter);
		}

		private boolean filter(DatoSolicitado datoSolicitado) {
		    return filters.stream().anyMatch(predicate -> predicate.test(datoSolicitado));
		}
		
	}

	Optional<String> cnae = Optional.empty();
	Optional<CretaTramoBuilder> tramoBuilder = Optional.empty();
	Optional<TrabajadorBuilder> trabajadorBuilder = Optional.empty();
	LiquidacionMesBuilder liquidacionMesBuilder = new LiquidacionMesBuilder();
	TrabajadoresTramosBuilder trabajadoresTramosBuilder = new TrabajadoresTramosBuilder();
	
	
	protected String getIpf( String naf ) {
		return "51013253N";
	}

	protected TipoIpf getTipoIpf( String naf ) {
		return TipoIpf.DNI;
	}

	protected boolean isQuoteByRealDays(String ssNum, String ccc, Date start, Date end) {
		return false;
	}

	protected boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
		return false;
	}
	
	protected boolean isScholarEmployee(String ssNum, String ccc, Date start, Date end) {
		return false;
	}

	protected boolean isTraining421Employee(String ssNum, String ccc, Date start, Date end) {
		return false;
	}
	
	@Override
	public void onAuthorized(Integer number, String name) {
		trabajadoresTramosBuilder.setAutorizado(number);
	}

	@Override
	public void onPeriod(Date date) {
		int year = get(date, Calendar.YEAR);

		trabajadoresTramosBuilder.setAnhoDesde(year);
		trabajadoresTramosBuilder.setAnhoHasta(year);
		trabajadoresTramosBuilder.setAnhoControl(year);

		Month month = Month.of(get(date, Calendar.MONTH) +1);

		trabajadoresTramosBuilder.setMesDesde(month);
		trabajadoresTramosBuilder.setMesHasta(month);
		trabajadoresTramosBuilder.setMesControl(month);
		
		liquidacionMesBuilder.setAnho(year);
		liquidacionMesBuilder.setMes(month);
	
	}

	@Override
	public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
			String economicActivityDescription, String regime, String fullCCC) {
		trabajadoresTramosBuilder.setTipo("L00");
		trabajadoresTramosBuilder.setCCC(getRegime(regime) + ccc);
		cnae = Optional.ofNullable(economicActivityCode);
	}
	
	@Override
	public void onEmployee(String nss, String name) {
		trabajadorBuilder
		.ifPresent( trabj -> {
			tramoBuilder.ifPresent(tramo -> trabj.addTramo(tramo.create()));
			liquidacionMesBuilder.add(trabj.create());
		}); 
		
		
		TrabajadorBuilder builder = new TrabajadorBuilder();
		builder.setNaf(nss);
		
		builder.setName(getName(name));		
		builder.setFirstSurname(getFirstSurname(name));		
		builder.setSecondSurname(getSecondSurname(name));		
		
		builder.setNumeroIpf(getIpf(nss));
		builder.setTipoIpf(getTipoIpf(nss));
		
		tramoBuilder = Optional.empty();
		trabajadorBuilder  = Optional.of(builder);
	}
	
	@Override
	public void onEmployeeQuoteGroup(String group) {
	}
	
	@Override
	public void onEmployeeQuoteGroup(String group, boolean monthly) {
		tramoBuilder.ifPresent(b -> b.setGrupoCotizacion(group));
		if ( !monthly && isDaily(group ) ) {
			tramoBuilder.ifPresent(b -> addModalidadSalario(b));
		}
	}
	
	@Override
	public void onEmployeePerido(String ssNum, String ccc, Date startDate, Date endDate) {
		tramoBuilder.ifPresent(b -> trabajadorBuilder.get().addTramo(b.create()));

		CretaTramoBuilder builder = new CretaTramoBuilder();
		
		setDesdeHasta(startDate, endDate, builder);
		
		tramoBuilder = Optional.of(builder);
	}
	
	@Override
	public void onNoEmployeeQuotePEC(String ssNum, String ccc, Date start, Date end) {
		tramoBuilder.ifPresent( b -> addActivoNormal(ssNum, ccc, start, end, b) );
	}

	protected void addActivoNormal(String ssNum, String ccc, Date start, Date end, CretaTramoBuilder b) {
		if ( isQuoteByRealDays(ssNum, ccc, start, end )) {
		    	b.filter(d -> AonStringUtils.equals( d.getCodigo(),"51"));
		    	addTiempoCompletoNormal(b) ; // REG.GRAL.(SIST.ESP.AGRARIO CCC) COTIZACION POR JR
		}else if ( isPartTimeEmployee(ssNum, ccc, start, end )) {
			addTiempoParcialNormal(b);
		} else if ( isScholarEmployee(ssNum, ccc, start, end)) {
			addBecariosNormal(b);
		} else if ( isTraining421Employee(ssNum, ccc, start, end)) {
			addFormacionNormal(b);
		} else { 
			addTiempoCompletoNormal(b);
		}
	}

	@Override
	public void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) {
		tramoBuilder.ifPresent( b -> {
			switch (code) {
			case "17": //APORT.NO OBL.SUS.EMP
			    	// clean all different from 'Indicador'
				b.clear( d-> "C".equalsIgnoreCase(d.getTipoDato()));
				b.clear( d-> "H".equalsIgnoreCase(d.getTipoDato()));
				addExpedienteRegulacionEmpleoTotal(b);
				return;
			case "21": //IT.CC.PAGO DELEGADO
				b.clear();
				addIncapacidadTemporalPagoDelegadoEstandar(b);
				return;
			case "22": //IT.CC.PAGO DIRECTO
				b.clear();
				addIncapacidadTemporalCCPagoDirectoEstandar(b);
				return;
			case "23": //IT.AT.PAGO DELEGADO
				b.clear();
				if ( isScholarEmployee(ssNum, ccc, start, end))
					addIncapacidadTemporalATEPPagoDelegadoBecario(b);
				else if ( isTraining421Employee(ssNum, ccc, start, end))
					addIncapacidadTemporalATEPPagoDelegadoFormacion(b);
				else 
					addIncapacidadTemporalATEPPagoDelegadoEstandar(b);
				return;
			case "29": //IT.CC.COLAB.EXCL.15D
				b.clear();
				addIncapacidadTemporal15PrimerosDiasEstandar(b);
				return;
			case "31": //31 MATERN/PATERN.T.COMP
				b.clear();
				addMaternidadPaternidadTiempoCompleto(b);
				return;

			default:
				break;
			}
			onNoEmployeeQuotePEC(ssNum, ccc, start, end);
		});
	}
	
	@Override
	public void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo,
	    String quota, String colective, Date start, Date end) {
	    if (isMonthlySEA(colective)) { 
		tramoBuilder .ifPresent(b -> b.filter(d -> "I".equals(d.getTipoDato())));
	    }
		
	    IdcParserListener.super.onEmployeeQuotePEC(ssNum, ccc, code, description, portTipo, quota, colective, start, end);
	}
	
	public TrabajadoresTramos getTrabajadoresTramos() {
		tramoBuilder
		.ifPresent(b -> trabajadorBuilder.get().addTramo(b.create()));
		
		trabajadorBuilder
		.ifPresent( b -> liquidacionMesBuilder.add(b.create()));
		
		LiquidacionMes liquidacionMes = liquidacionMesBuilder.create();
		trabajadoresTramosBuilder.addLiquidacionMes(liquidacionMes);
		
		addBonificacionFormacionContinua(trabajadoresTramosBuilder);
		
		return trabajadoresTramosBuilder.create();
	}
	
	private static boolean isMonthlySEA(String colective) {
		return AonStringUtils.equals("4216", colective); 
	}

	private static String getName( String fullName) {
		String names []  = fullName.split("\\s", -1);
		return names.length > 0 ? names[0] : "-";
	}
	
	private static String getFirstSurname( String fullName) {
		String names []  = fullName.split("\\s", -1);
		return names.length > 1 ? names[1] : "-";
	}

	private static String getSecondSurname( String fullName) {
		String names []  = fullName.split("\\s", -1);
		return names.length > 2 ? names[names.length-1] : "-";
	}

	private static String getRegime(String description) {
		if ( AonStringUtils.containsIgnoreCase(description, "AGRARIO"))
			return "0163";
		return "0111";	
	}
	
	private static boolean isDaily(String quoteGroup) {
		return Integer.parseInt(quoteGroup) >= 8; 
	}
	
	private static void  addBonificacionFormacionContinua(TrabajadoresTramosBuilder trabajadoresTramosBuilder) {
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("763");
		dataSolicitadoBuilder.setObligatorio(false);
		trabajadoresTramosBuilder.addDatoSolicitado(dataSolicitadoBuilder.create());
	}
	
	private static void setDesdeHasta(Date start, Date end, TramoBuilder tramoBuilder) {
		int startYear = get(start, Calendar.YEAR);
		int endYear = get(end, Calendar.YEAR);
		Month startMonth = Month.of(get(start, Calendar.MONTH) +1);
		Month endMonth = Month.of(get(end, Calendar.MONTH) +1);
		int startDay = get(start, Calendar.DAY_OF_MONTH);
		int endDay = get(end, Calendar.DAY_OF_MONTH);
		
		tramoBuilder.setAnhoDesde(startYear);
		tramoBuilder.setMesDesde(startMonth);
		tramoBuilder.setDiaDesde(startDay);
		tramoBuilder.setAnhoHasta(endYear);
		tramoBuilder.setMesHasta(endMonth);
		tramoBuilder.setDiaHasta(endDay);
	}
	

	private static void addModalidadSalario(TramoBuilder tramoBuilder) {
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();
		// Modalidad de salario
		dataSolicitadoBuilder.setTipo("I");
		dataSolicitadoBuilder.setCodigo("51");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
	}
	
	private static void addTiempoCompletoNormal(TramoBuilder tramoBuilder) {
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();
		// 2.1 Situación de activo "normal" 
		// 2.1.1 Trabajador a Tiempo Completo  
		
		// Base de contingencias comunes
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("500");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Base de Horas Extras Fuerza Mayor
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("501");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Base Otras Horas Extras
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("502");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Base de Accidentes de Trabajo
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("601");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
										
	}
	
	private static void addFormacionNormal(TramoBuilder tramoBuilder) {
		
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();
		// 3.1 Contratos para la formación (TRL 087)  
		// 3.1.1 Tramo en situación de activo "normal"  
	
		// N horas formación teórica presencial 
		dataSolicitadoBuilder.setTipo("H");
		dataSolicitadoBuilder.setCodigo("03");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// N horas formación teórica a distancia 
		dataSolicitadoBuilder.setTipo("H");
		dataSolicitadoBuilder.setCodigo("04");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// N horas tutoría 
		dataSolicitadoBuilder.setTipo("H");
		dataSolicitadoBuilder.setCodigo("06");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Bonificación tutoría
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("737");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Base de Horas Extras Fuerza Mayor
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("501");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Base Otras Horas Extras
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("502");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
	}
	
	private static void addBecariosNormal(TramoBuilder tramoBuilder) {
		
	}
	
	private static void addTiempoParcialNormal(TramoBuilder tramoBuilder) {
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();

		// 2.1 Situación de activo "normal" 
		addTiempoCompletoNormal(tramoBuilder);
		// 2.1.1 Trabajador a Tiempo Parcial
		// N horas realizadas  a tiempo parcial 
		dataSolicitadoBuilder.setTipo("H");
		dataSolicitadoBuilder.setCodigo("01");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// N horas complementarias 
		dataSolicitadoBuilder.setTipo("H");
		dataSolicitadoBuilder.setCodigo("02");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Base de horas complementarias
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("537");
		dataSolicitadoBuilder.setObligatorio(false);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
	}
	
	private static void addIncapacidadTemporalATEPPagoDelegadoEstandar(TramoBuilder tramoBuilder) {
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();
		// 2.2 Situaciones de Incapacidad Temporal  
		// 2.2.3 Incapacidad Temporal de AT Y EP pago delegado 
		// Base de contingencias comunes en situación de IT
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("500");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Compensación IT AT y EP
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("663");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Base de Accidentes de Trabajo en situación de IT
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("603");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
	}
	
	private static void addIncapacidadTemporalATEPPagoDelegadoFormacion(TramoBuilder tramoBuilder) {
		addIncapacidadTemporalATEPPagoDelegadoBecario(tramoBuilder);
	}

	private static void addIncapacidadTemporalATEPPagoDelegadoBecario(TramoBuilder tramoBuilder) {
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();
		// 3.2 Programas de formación (TRL 986) /becarios de investigación (TRL 933)  
		// 3.2.2 Incapacidad Temporal de AT Y EP pago delegado 
		// Compensación IT AT y EP
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("663");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
	}
	
	private static void addIncapacidadTemporalCCPagoDirectoEstandar(TramoBuilder tramoBuilder) {
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();
		// 2.2 Situaciones de Incapacidad Temporal  
		// 2.2.2 Incapacidad Temporal pago directo 
		// Base de contingencias comunes en situación de IT
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("509");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Base de Accidentes de Trabajo en situación de IT
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("603");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
	}
	
	private static void addIncapacidadTemporal15PrimerosDiasEstandar(TramoBuilder tramoBuilder) {
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();
		// Base de contingencias comunes en situación de IT
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("500");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Base de Accidentes de Trabajo en situación de IT
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("603");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
	}
	
	private static void addIncapacidadTemporalPagoDelegadoEstandar(TramoBuilder tramoBuilder) {
		addIncapacidadTemporal15PrimerosDiasEstandar(tramoBuilder);
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();
		// Compensación IT Contingencias Comunes
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("563");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
	}
	
	private static void addMaternidadPaternidadTiempoCompleto(TramoBuilder tramoBuilder) {
		DatoSolicitadoBuilder dataSolicitadoBuilder = new DatoSolicitadoBuilder();
		// Base de contingencias comunes en situación de Maternidad/Paternidad
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("509");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
		// Base de Accidentes de Trabajo en situación de Maternidad/Paternidad
		dataSolicitadoBuilder.setTipo("C");
		dataSolicitadoBuilder.setCodigo("603");
		dataSolicitadoBuilder.setObligatorio(true);
		tramoBuilder.addDato(dataSolicitadoBuilder.create());
	}

	private static void addExpedienteRegulacionEmpleoTotal(TramoBuilder tramoBuilder) {
	    addMaternidadPaternidadTiempoCompleto(tramoBuilder);
	}
}
