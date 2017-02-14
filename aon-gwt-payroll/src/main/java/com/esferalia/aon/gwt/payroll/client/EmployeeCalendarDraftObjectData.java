package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeCalendarDraftObjectData {

	private Map<Date,Double> mapaDiasHoras;
	private Map<Date, DayType> mapaDiasTipo;
	
	private Map<Date,Double> draftMapaDiasHoras;
	private Map<Date, DayType> draftMapaDiasTipo;
	
	private Date startContract;
	private Date endContract;
	
	private Integer employeeId;
	private EmployeesServiceAsync employeesService;
	
	private boolean jornadaEmpleado;

	private com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft;
	
	public UndoManager<Undoable> undoManager;
	
	private static final Map<String, Integer> DAY_OF_WEEKS  = new HashMap<String, Integer>(){
		
		private static final long serialVersionUID = 1L;

		{
			put("HORAS_DOMINGO", 0);
			put("HORAS_LUNES", 1);
			put("HORAS_MARTES", 2);
			put("HORAS_MIERCOLES", 3);
			put("HORAS_JUEVES", 4);
			put("HORAS_VIERNES", 5);
			put("HORAS_SABADO", 6);
		}
	};
	
private static final Map<String, DayType> TYPE_OF_DAY  = new HashMap<String, DayType>(){
		
		private static final long serialVersionUID = 1L;

		{
			put("DIAS_IT", DayType.BAJAIT);
			put("DIAS_VACACIONES", DayType.HOLIDAY);
			put("DIAS_HUELGA", DayType.STRIKEDAY);
			put("DIAS_ERE", DayType.EREDAY);
		}
	};
	
	public static interface DayTypeVisitor{
		void visitFreeDay(DayType dayType);
		void visitHolyDay(DayType dayType);
		void visitDropDay(DayType dayType);
		void visitEreDay(DayType dayType);
		void visitStrikeDay(DayType dayType);
		void visitReductionDay(DayType dayType);
		void visitSuspensionDay(DayType dayType);
		void visitITDay(DayType dayType);
		void visitNoTypeDay(DayType dayType);
	}
	
	public static enum DayType{
		FREEDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitFreeDay(this);
			}
		}, 
		HOLIDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitHolyDay(this);
			}
		}, 
		DROPDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitDropDay(this);
			}
		}, 
		STRIKEDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitStrikeDay(this);
			}
		}, 
		EREDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitEreDay(this);
			}
		}, 
		REDUCTIONDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitReductionDay(this);
			}
		}, 
		SUSPENSIONDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitSuspensionDay(this);
			}
		},
		BAJAIT {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitITDay(this);
			}
		},
		NOTYPEDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitNoTypeDay(this);
			}
		
		};
		
		public abstract void visit(DayTypeVisitor visitor); 
		
	};
	
	// --------------------------------------------- INTERFAZ REDO/UNDO -----------------------------------------------
	
	private class CompositeUndoable<T extends Undoable > implements Undoable {

		private Collection<T> undos;

		public CompositeUndoable(Collection<T> undos) {
			this.undos = undos;
		}

		@Override
		public void redo() {
			for (T undo : undos)
				undo.redo();
		}

		@Override
		public void undo() {
			for (T undo : undos){
				undo.undo();
			}
		}

	}
	
	class SetHourEdit implements Undoable {

		private Double oldHour;
		private Double newHour;
		private Date day;
		private StringVariable variable;
		
		public SetHourEdit(Double oldH, Double newH, Date actualDay, StringVariable var) {
			this.oldHour = oldH;
			this.newHour = newH;
			this.day = actualDay;
			this.variable = var;
		}
		
		@Override
		public void undo() {
			if (oldHour == null)
				draftMapaDiasHoras.remove(day);
			else
				draftMapaDiasHoras.put(day, oldHour);
			
			salaryDraft.removeDraftVariable(this.variable);
		}
		
		@Override
		public void redo() {
			draftMapaDiasHoras.put(day, newHour);
			salaryDraft.addDraftVariable(this.variable);
		}
	}
	
	class SetTypeEdit implements Undoable {

		private DayType oldType;
		private DayType newType;
		private Date day;
		
		public SetTypeEdit(DayType oldT, DayType newT, Date actualDay) {
			this.oldType = oldT;
			this.newType = newT;
			this.day = actualDay;
		}
		
		@Override
		public void undo() {
			if (oldType == null)
				draftMapaDiasTipo.remove(day);
			else
				draftMapaDiasTipo.put(day, oldType);
		}
		
		@Override
		public void redo() {
			draftMapaDiasTipo.put(day, newType);
		}
	}
	
	// ---------------------------------------------- METODOS DE LA CLASE ---------------------------------------------	
	public EmployeeCalendarDraftObjectData(Integer employeeId,
			Date startContract, Date endContract, EmployeesServiceAsync employeesService) {
		this.undoManager = new UndoManager<>();
		
		this.mapaDiasHoras = new HashMap<Date,Double>();
		this.mapaDiasTipo = new HashMap<Date,DayType>();
		
		this.draftMapaDiasHoras = new HashMap<Date,Double>();
		this.draftMapaDiasTipo = new HashMap<Date,DayType>();
		
		this.startContract = startContract;
		this.endContract = endContract;

		this.employeeId = employeeId;
		this.employeesService = employeesService;
		
	}
	
	public DayType getTypeByDay (Date dia){
		DayType typeDayDraft = draftMapaDiasTipo.get(dia);
		
		if (typeDayDraft != null)
			return typeDayDraft;
		else	
			return mapaDiasTipo.getOrDefault(dia, DayType.NOTYPEDAY);
	}
	
	public Date getDateByDay (Date dia){
		for ( Date key: mapaDiasTipo.keySet() )
			if ( key.equals(dia ))
				return dia;
		return null;
	}

	public void setTypeByDay (Date dia, DayType typeDay){
		DayType old = draftMapaDiasTipo.put(dia, typeDay);
		undoManager.add(new SetTypeEdit(old, typeDay, dia));
	} 
	
	public void setTypeByDay (Map<Date, DayType> types){
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Map.Entry<Date, DayType> entry : types.entrySet()) {
			DayType old = draftMapaDiasTipo.put(entry.getKey(), entry.getValue());
			undos.add(new SetTypeEdit(old, entry.getValue(), entry.getKey()));
		}
		undoManager.add(new CompositeUndoable<Undoable>(undos));
	}
	

	public double getHourByDay (Date dia){
		Double hourDayDraft = draftMapaDiasHoras.get(dia);
		
		if (hourDayDraft != null)
			return hourDayDraft;
		else{
			return mapaDiasHoras.getOrDefault(dia, (double) 0);
		}
	}
	
	public void setHourByDay (Date dia, Double hour){
		Double old = draftMapaDiasHoras.put(dia, hour);
		
		@SuppressWarnings("deprecation")
		String name = calcularDiaSemana (dia.getDay()-1);
		
		StringVariable var = new StringVariable();
		var.setImplicit(false);
		var.setScope(Scope.SALARY); // DRAFT
		var.setName(name);
		var.setEndDate(dia);
		var.setStartDate(dia);
		var.setExpression(Double.toString(hour));
		
		this.salaryDraft.addDraftVariable(var);
		
		undoManager.add(new SetHourEdit(old, hour, dia, var));
	} 

	public void setHourByDay (Map<Date, Double> hours){
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Map.Entry<Date, Double> entry : hours.entrySet()) {
			Double old = draftMapaDiasHoras.put(entry.getKey(), entry.getValue());
			
			@SuppressWarnings("deprecation")
			String name = calcularDiaSemana (entry.getKey().getDay());
			
			StringVariable var = new StringVariable();
			var.setImplicit(false);
			var.setScope(Scope.SALARY); // DRAFT
			var.setName(name);
			var.setEndDate(entry.getKey());
			var.setStartDate(entry.getKey());
			var.setExpression(Double.toString(entry.getValue()));
			
			this.salaryDraft.addDraftVariable(var);
			
			undos.add(new SetHourEdit(old, entry.getValue(), entry.getKey(), var));
		}
		undoManager.add(new CompositeUndoable<Undoable>(undos));
		
	} 

	public Set<Entry<Date, Double>> getHourChanges(){
		return draftMapaDiasHoras.entrySet();
	}
	
	public Set<Entry<Date, DayType>> getTypeChanges(){
		return draftMapaDiasTipo.entrySet();
	}
	
	public Date getStartDateContract(){
		return startContract;
	}
	
	public Date getEndDateContract(){
		return endContract;
	}
	
	public boolean getJornadaEmpleado(){
		return jornadaEmpleado;
	}

	public void setSalaryDraft(com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft) {
		this.salaryDraft = salaryDraft;
	}
	
	private String calcularDiaSemana(int day) {
		String result = "";
		switch (day) {
		case 0:
			result = "HORAS_DOMINGO";
			break;
		case 1:
			result = "HORAS_LUNES";
			break;
		case 2:
			result = "HORAS_MARTES";
			break;
		case 3:
			result = "HORAS_MIERCOLES";
			break;
		case 4:
			result = "HORAS_JUEVES";
			break;
		case 5:
			result = "HORAS_VIERNES";
			break;
		default:
			result = "HORAS_SABADO";
			break;
		}
		
		return result;
	}
	
	private String calcularDiaSemanaNoLaboral(int day) {
		String result = "";
		switch (day) {
		case 0:
			result = "HORAS_DOMINGO";
			break;
		case 1:
			result = "HORAS_LUNES";
			break;
		case 2:
			result = "HORAS_MARTES";
			break;
		case 3:
			result = "HORAS_MIERCOLES";
			break;
		case 4:
			result = "HORAS_JUEVES";
			break;
		case 5:
			result = "HORAS_VIERNES";
			break;
		case 6:
			result = "HORAS_SABADO";
			break;
		default:
			result = "";
			break;
		}
		
		return result;
	}
	
	
	public void inicialiazarCalendarioBD(Consumer<EmployeeCalendarData> success, Consumer<Throwable> failure) {
		Window.alert("ID Cliente: "+employeeId);
		
		employeesService.getEmployeeCalendar(employeeId, 
			new AsyncCallback<EmployeeCalendarData>() {
			
			@Override
			public void onSuccess(EmployeeCalendarData result) {
				List<Quartet<java.sql.Date, java.sql.Date, String, String>> listaHoras = result.getListaHorasContrato();
				List<Quartet<java.sql.Date, java.sql.Date, String, String>> listaTipos = result.getListaTipoDiasContrato();
				ArrayList<Byte> listaNoLaborables = result.getListaNoLaborablesContrato();
				ArrayList<java.util.Date> listaFestivos = result.getListaFestivosContrato();
				inicializarMapaHoras(listaHoras);
				inicializarMapaTiposNoLaborables(listaNoLaborables);
				inicializarMapaTipos(listaTipos);
				inicializarMapaTiposFestivos(listaFestivos);
				jornadaEmpleado = result.isJornadaCompleta();
				Window.alert("Jornada Empleado :"+jornadaEmpleado);
				
				success.accept(result);
				
			}
			
			private void inicializarMapaTiposFestivos(ArrayList<java.util.Date> listaFestivos) {
				for(java.util.Date d : listaFestivos){
					mapaDiasTipo.put(d, DayType.FREEDAY);
				}
			}

			private void inicializarMapaTiposNoLaborables(ArrayList<Byte> listaNoLaborables) {
				int cont = 0;
				
				for (Byte noLabroles : listaNoLaborables) {
					
					Date fechaInicio = DateUtils.copyDateOnly(startContract);
					Date endDateAux = DateUtils.getLastDayOfYear(new Date());
					Date fechaFin;
					
					if (endContract == null)
						fechaFin = DateUtils.addYears2Date(endDateAux, 1);
					else
						fechaFin = DateUtils.copyDateOnly(endContract);
					
					DayType tipoDia;
					
					if (0 == noLabroles.byteValue())
						tipoDia = DayType.NOTYPEDAY;
					else
						tipoDia = DayType.FREEDAY;
					
					String horasDias = calcularDiaSemanaNoLaboral(cont);
					
					@SuppressWarnings("deprecation")
					int initialDay = fechaInicio.getDay();
					int findingDay = DAY_OF_WEEKS.get(horasDias)+1;
					
					int auxDay = findingDay - initialDay;
					
					if (auxDay == 7)
						auxDay = 0;
					
					if (auxDay < 0)
						auxDay = 7 + auxDay;
					
					Date auxDate = DateUtils.addDays2Date(fechaInicio, auxDay);
					
					while (auxDate.before(fechaFin) || auxDate.equals(fechaFin)){
						Date date = DateUtils.copyDateOnly(auxDate);
						DateUtils.resetTime(date);
						mapaDiasTipo.put(date, tipoDia);
						DateUtils.addDays2Date(auxDate, 7);
					}
					cont++;
				}	
			}

			private void inicializarMapaTipos(List<Quartet<java.sql.Date, java.sql.Date, String, String>> listaTipos) {
				
				for (Quartet<java.sql.Date, java.sql.Date, String, String> quartetTipos : listaTipos) {
					
					Date startDate = DateUtils.copyDateOnly(quartetTipos.getStartDate());
					Date endDateAux = DateUtils.getLastDayOfYear(new Date());
					Date endDate;
					if (quartetTipos.getEndDate() == null){
						endDate = DateUtils.addYears2Date(endDateAux, 1);
					}else
						endDate = DateUtils.copyDateOnly(quartetTipos.getEndDate());;
					
					DateUtils.addDays2Date(endDate, 1);
					
					DayType TipoDia = TYPE_OF_DAY.get(quartetTipos.getName());
					
					Date auxDate = DateUtils.copyDateOnly(startDate);
					
					while (auxDate.before(endDate) || auxDate.equals(endDate)){
						Date date = DateUtils.copyDateOnly(auxDate);
						DateUtils.resetTime(date);
						mapaDiasTipo.put(date, TipoDia);
						DateUtils.addDays2Date(auxDate, 1);
					}
				}	
			}

			private void inicializarMapaHoras(List<Quartet<java.sql.Date, java.sql.Date, String, String>> listaHoras) {
				
				for (Quartet<java.sql.Date, java.sql.Date, String, String> quartetHoras : listaHoras) {
					
					Date startDate = DateUtils.copyDateOnly(quartetHoras.getStartDate());
					Date endDateAux = DateUtils.getLastDayOfYear(new Date());
					Date endDate;
					if (quartetHoras.getEndDate() == null){
						endDate = DateUtils.addYears2Date(endDateAux, 1);
					}else
						endDate = DateUtils.copyDateOnly(quartetHoras.getEndDate());;
					
					DateUtils.addDays2Date(endDate, 1);
					
					Double horas = Double.parseDouble(quartetHoras.getExpression());
					String horasDias = quartetHoras.getName();
					
					@SuppressWarnings("deprecation")
					int initialDay = startDate.getDay();
					int findingDay = DAY_OF_WEEKS.get(horasDias);
					
					int auxDay = findingDay - initialDay;
					
					if (auxDay == 7)
						auxDay = 0;
					
					if (auxDay < 0)
						auxDay = 7 + auxDay;
					
					Date auxDate = DateUtils.addDays2Date(startDate, auxDay);
					
					while (auxDate.before(endDate) || auxDate.equals(endDate)){
						Date date = DateUtils.copyDateOnly(auxDate);
						DateUtils.resetTime(date);
						mapaDiasHoras.put(date, horas);
						DateUtils.addDays2Date(auxDate, 7);
					}
				}
				
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void actualizarCalendarioBD(){
		EmployeeCalendarUpdate updateInfo = new EmployeeCalendarUpdate();
		
		updateInfo.setMapaHorasDias(crearMapaHorasUpdate(mapaDiasHoras, draftMapaDiasHoras));
		updateInfo.setMapaTipoDias(crearMapaTiposUpdate(mapaDiasTipo, draftMapaDiasTipo));
		
		employeesService.setEmployeeCalendar(employeeId, updateInfo, new AsyncCallback<EmployeeCalendarUpdate>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(EmployeeCalendarUpdate result) {
				//TODO: reiniciar todo
				draftMapaDiasHoras.clear();
				draftMapaDiasTipo.clear();
				
			}
			
		});
	}

	private HashMap<Date, DayType> crearMapaTiposUpdate(Map<Date, DayType> mapaDiasTipo,
			Map<Date, DayType> draftMapaDiasTipo) {
		
		HashMap<Date, DayType> mapaUpdate = new HashMap<Date, DayType>();
		
		for ( Entry<Date,DayType> e : mapaDiasTipo.entrySet()){
			mapaUpdate.put(e.getKey(), e.getValue());
		}
		
		for (Entry<Date,DayType> e : draftMapaDiasTipo.entrySet()){
			mapaUpdate.put(e.getKey(), e.getValue());
		}
		
		return mapaUpdate;
		
		
	}

	private HashMap<Date, Double> crearMapaHorasUpdate(Map<Date, Double> mapaDiasHoras,
			Map<Date, Double> draftMapaDiasHoras) {
		
		HashMap<Date, Double> mapaUpdate = new HashMap<Date, Double>();
		
		for ( Entry<Date,Double> e : mapaDiasHoras.entrySet()){
			mapaUpdate.put(e.getKey(), e.getValue());
		}
		
		for (Entry<Date,Double> e : draftMapaDiasHoras.entrySet()){
			mapaUpdate.put(e.getKey(), e.getValue());
		}
		
		return mapaUpdate;
	}
}
