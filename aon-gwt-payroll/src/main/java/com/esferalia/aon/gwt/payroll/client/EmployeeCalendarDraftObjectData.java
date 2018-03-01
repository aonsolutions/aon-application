package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeCalendarDraftObjectData {

	private Map<Date,Double> mapDaysHour;
	private Map<Date, DayType> mapDaysType;
	private Map<Date, String> mapFestivesDays;
	private Map<Date, Double> mapExtraHours;
	
	private Map<Date,Double> draftMapDaysHour;
	private Map<Date, DayType> draftMapDaysType;
	private Map<Date, Double> draftMapDaysCoefficientEre;
	private Map<Date, Double> draftMapDaysCoefficientStrike;
	private Map<Date, Double> draftMapExtraHours;
	
	private Date startContract;
	private Date endContract;
	
	private Integer employeeId;
	private EmployeesServiceAsync employeesService;
	
	private boolean fullTimeEmployee;
	private int fullTimeEmployeeDraft = -1;
	
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
	
	// --------------------------------------------- INTERFACE DAY TYPE ----------------------------------------------------
	
	public static interface DayTypeVisitor<T>{
		T visitFreeDay(DayType dayType);
		T visitNoWorkingDay(DayType dayType);
		T visitHolyDay(DayType dayType);
		T visitDropDay(DayType dayType);
		T visitEreDay(DayType dayType);
		T visitStrikeDay(DayType dayType);
		T visitReductionDay(DayType dayType);
		T visitSuspensionDay(DayType dayType);
		T visitITDay(DayType dayType);
		T visitNoTypeDay(DayType dayType);
	}
	
	public static enum DayType{
		FREEDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitFreeDay(this);
			}
		},
		NOWORKINGDAY{
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitNoWorkingDay(this);
			}
		},
		HOLIDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitHolyDay(this);
			}
		}, 
		DROPDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitDropDay(this);
			}
		}, 
		STRIKEDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitStrikeDay(this);
			}
		}, 
		EREDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitEreDay(this);
			}
		}, 
		REDUCTIONDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitReductionDay(this);
			}
		}, 
		SUSPENSIONDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitSuspensionDay(this);
			}
		},
		BAJAIT {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitITDay(this);
			}
		},
		NOTYPEDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitNoTypeDay(this);
			}
		
		};
		
		public abstract <T> T visit(DayTypeVisitor<T> visitor); 
		
	};
	
	// --------------------------------------------- INTERFACE REDO/UNDO -----------------------------------------------
	
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
		
		public SetHourEdit(Double oldH, Double newH, Date actualDay) {
			this.oldHour = oldH;
			this.newHour = newH;
			this.day = actualDay;
		}
		
		@Override
		public void undo() {
			if (oldHour == null)
				draftMapDaysHour.remove(day);
			else
				draftMapDaysHour.put(day, oldHour);	
		}
		
		@Override
		public void redo() {
			draftMapDaysHour.put(day, newHour);
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
				draftMapDaysType.remove(day);
			else
				draftMapDaysType.put(day, oldType);
		}
		
		@Override
		public void redo() {
			draftMapDaysType.put(day, newType);
		}
		
	}
	
	class SetExtraHourEdit implements Undoable {

		private Double oldHour;
		private Double newHour;
		private Date month;
		
		public SetExtraHourEdit(Double oldH, Double newH, Date actualMonth) {
			this.oldHour = oldH;
			this.newHour = newH;
			this.month = actualMonth;
		}
		
		@Override
		public void undo() {
			if (oldHour == null)
				draftMapExtraHours.remove(month);
			else
				draftMapExtraHours.put(month, oldHour);	
		}
		
		@Override
		public void redo() {
			draftMapExtraHours.put(month, newHour);
		}
		
	}
	
	class SetEreEdit implements Undoable {

		private Double oldEre;
		private Double newEre;
		private Date day;
		
		public SetEreEdit(Double oldT, Double newT, Date actualDay) {
			this.oldEre = oldT;
			this.newEre = newT;
			this.day = actualDay;
		}
		
		@Override
		public void undo() {
			if (oldEre == null)
				draftMapDaysCoefficientEre.remove(day);
			else
				draftMapDaysCoefficientEre.put(day, oldEre);
		}
		
		@Override
		public void redo() {
			draftMapDaysCoefficientEre.put(day, newEre);
		}
		
	}
	
	class SetStrikeEdit implements Undoable {

		private Double oldStrike;
		private Double newStrike;
		private Date day;
		
		public SetStrikeEdit(Double oldT, Double newT, Date actualDay) {
			this.oldStrike = oldT;
			this.newStrike = newT;
			this.day = actualDay;
		}
		
		@Override
		public void undo() {
			if (oldStrike == null)
				draftMapDaysCoefficientStrike.remove(day);
			else
				draftMapDaysCoefficientStrike.put(day, oldStrike);
		}
		
		@Override
		public void redo() {
			draftMapDaysCoefficientStrike.put(day, newStrike);
		}
		
	}
	
	
	
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public EmployeeCalendarDraftObjectData(Integer employeeId, Date startContract, Date endContract,
			EmployeesServiceAsync employeesService) {
		
		this.undoManager = new UndoManager<>();
		
		this.mapDaysHour = new HashMap<Date,Double>();
		this.mapDaysType = new HashMap<Date,DayType>();
		this.mapFestivesDays = new HashMap<Date,String>();
		this.mapExtraHours = new HashMap<Date,Double>();
		
		this.draftMapDaysHour = new HashMap<Date,Double>();
		this.draftMapDaysType = new HashMap<Date,DayType>();
		this.draftMapDaysCoefficientEre = new HashMap<Date,Double>();
		this.draftMapDaysCoefficientStrike = new HashMap<Date,Double>();
		this.draftMapExtraHours = new HashMap<Date,Double>();
		
		
		this.startContract = startContract;
		this.endContract = endContract;

		this.employeeId = employeeId;
		this.employeesService = employeesService;
		
	}
	
	public Integer getEmployeeId(){
		return this.employeeId;
	}
	
	public Integer getMapSize(){
		return mapDaysHour.keySet().size();
	}
	
	public String getDescriptionFestive(Date actualDay) {
		return mapFestivesDays.get(actualDay);
	}
	
	public Set<Entry<Date, Double>> getChangesHours(){
		return draftMapDaysHour.entrySet();
	}
	
	public Set<Entry<Date, Double>> getChangesExtraHours() {
		return draftMapExtraHours.entrySet();
	}
	
	public Set<Entry<Date, DayType>> getChangesTypes(){
		return draftMapDaysType.entrySet();
	}
	
	public Date getStartDateContract(){
		return startContract;
	}
	
	public Date getEndDateContract(){
		return endContract;
	}
	
	public boolean isFullTimeJourney(){
		if (fullTimeEmployee)
			if (fullTimeEmployeeDraft == 0){
				return false;
			}
		
		if (!fullTimeEmployee)
			if (fullTimeEmployeeDraft == 1){
				return true;
			}
		
		return fullTimeEmployee;
	}
	
	public Date getLastMapDate(){
		Date lastDraftDate = new Date(0);
		for ( Date date : mapDaysHour.keySet() )
			if ( date.after(lastDraftDate) )
				lastDraftDate = DateUtils.copyDateOnly(date);
		return lastDraftDate;
	}
	
	public Date getDateByDay (Date day){
		for ( Date key: mapDaysType.keySet() )
			if ( key.equals(day))
				return day;
		return null;
	}
	
	public void clearDraftHours() {
		draftMapDaysHour.clear();
		undoManager.discardAll();	
	}
	
	// ---------- GETTERS / SETTERS -----------
	
	public DayType getTypeByDay (Date day){
		DayType typeDayDraft = draftMapDaysType.get(day);	
		if (typeDayDraft != null)
			return typeDayDraft;
		else	
			return mapDaysType.getOrDefault(day, DayType.NOTYPEDAY);
	}

	public void setTypeByDay (Date day, DayType typeDay){
		DayType old = draftMapDaysType.put(day, typeDay);
		undoManager.add(new SetTypeEdit(old, typeDay, day));
	} 
	
	public void setTypeByDay (Map<Date, DayType> types){
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Map.Entry<Date, DayType> entry : types.entrySet()) {
			DayType old = draftMapDaysType.put(entry.getKey(), entry.getValue());
			undos.add(new SetTypeEdit(old, entry.getValue(), entry.getKey()));
		}
		undoManager.add(new CompositeUndoable<Undoable>(undos));
	}
	
	public Double getHourByDay (Date day){
		Double hourDayDraft = draftMapDaysHour.get(day);
		if (hourDayDraft != null)
			return hourDayDraft;
		else
			return mapDaysHour.getOrDefault(day, null);
	}
	
	public void setHourByDay (Date day, Double hour){
		Double old = draftMapDaysHour.put(day, hour);
		undoManager.add(new SetHourEdit(old, hour, day));
	} 
	
	public void setHourByDay (Map<Date, Double> hours){
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Map.Entry<Date, Double> entry : hours.entrySet()) {
			DateUtils.resetTime(entry.getKey());
			Double old = draftMapDaysHour.put(entry.getKey(), entry.getValue());
			undos.add(new SetHourEdit(old, entry.getValue(), entry.getKey()));
		}
		undoManager.add(new CompositeUndoable<Undoable>(undos));
	}
	
	public double getExtraHourByMonth (Date findindDate){
		Double extraHourMonthDraft = draftMapExtraHours.get(findindDate);
		
		if (extraHourMonthDraft != null)
			return extraHourMonthDraft;
		else
			return mapExtraHours.getOrDefault(findindDate, (double) 0);
	}
	
	@SuppressWarnings("deprecation")
	public void setExtraHourByMonth(Double extraHourMonth, int month, int year) {
		Date actualDate = new Date(year, month, 1);
		Double old = draftMapExtraHours.put(actualDate, extraHourMonth);
		undoManager.add(new SetExtraHourEdit(old, extraHourMonth, actualDate));	
	}
	
	// ----------- SET SPECIAL DAYS -----------
	
	public void setNonWorkingDays(List<Date> nonWorkingDays, DayType nonWorkingType, Double hour) {
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Date day : nonWorkingDays){
			DayType oldType = draftMapDaysType.put(day, nonWorkingType);
			Double oldHour = draftMapDaysHour.put(day, hour);
			undos.add(new SetTypeEdit(oldType, nonWorkingType, day));
			undos.add(new SetHourEdit(oldHour, hour, day));
		}
		undoManager.add(new CompositeUndoable<Undoable>(undos));	
	}
	
	public void setEreCoefficientDays(List<Date> ereDays, DayType ereType, double ce) {
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Date day : ereDays){
			DayType oldType = draftMapDaysType.put(day, ereType);
			Double oldCE = draftMapDaysCoefficientEre.put(day, ce);
			undos.add(new SetTypeEdit(oldType, ereType, day));
			undos.add(new SetEreEdit(oldCE, ce, day));
		}
		undoManager.add(new CompositeUndoable<Undoable>(undos));
	}
	
	public void setStrikeCoefficientDays(List<Date> strikeDays, DayType strikeType, double cs) {
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Date day : strikeDays){
			DayType oldType = draftMapDaysType.put(day, strikeType);
			Double oldCS = draftMapDaysCoefficientStrike.put(day, cs);
			undos.add(new SetTypeEdit(oldType, strikeType, day));
			undos.add(new SetStrikeEdit(oldCS, cs, day));
		}
		undoManager.add(new CompositeUndoable<Undoable>(undos));
	}
	
	public void setWorkingDays(HashMap<Date, Double> compositeH, HashMap<Date, DayType> compositeT) {
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Map.Entry<Date, Double> entry : compositeH.entrySet()) {
			DateUtils.resetTime(entry.getKey());
			Double old = draftMapDaysHour.put(entry.getKey(), entry.getValue());
			undos.add(new SetHourEdit(old, entry.getValue(), entry.getKey()));
		}
		for (Map.Entry<Date, DayType> entry : compositeT.entrySet()) {
			DayType old = draftMapDaysType.put(entry.getKey(), entry.getValue());
			undos.add(new SetTypeEdit(old, entry.getValue(), entry.getKey()));
		}
		undoManager.add(new CompositeUndoable<Undoable>(undos));
		
	}

	// ---------------------------------------------- METHODS VARIABLES SYNC SALARYDRAFT ---------------------------------------------
	
	public static class CalendarVariable extends StringVariable{
		private static final long serialVersionUID = 1L;
	}
	
	public boolean isMine(com.esferalia.aon.gwt.payroll.shared.Variable v ){
		return v instanceof CalendarVariable ;
	}
	
	
	// ------------ GET VARIABLES LIST METHODS -------------
	
	public ArrayList<StringVariable> getVariablesListCE(Date startDate, Date endDate) {

		ArrayList<StringVariable> variablesList = new ArrayList<StringVariable>();
		
		if ( draftMapDaysCoefficientEre.isEmpty() )
			return variablesList;
		
		List<Date> orderedDraftDatesCE = draftMapDaysCoefficientEre.keySet().stream().collect(Collectors.toList());
		Collections.sort(orderedDraftDatesCE);
		
		CalendarVariable var = null;
		Date dateBefore = null;
		String name = "COEFICIENTE_ERE";
		
		if(null == endDate)
			endDate = DateUtils.copyDateOnly(orderedDraftDatesCE.get(orderedDraftDatesCE.size() - 1));
		
		for(Date date : orderedDraftDatesCE){
			
			if(date.before(startDate))
				continue;
			if(date.after(endDate))
				continue;
			
			if(isNext(date, dateBefore)){
				var.setEndDate(date);
				dateBefore = DateUtils.copyDateOnly(date);
				continue;
			}else{
				if(var != null){
					variablesList.add(var);
				}	
				
				dateBefore = new Date();
				dateBefore = DateUtils.copyDateOnly(date);
				
				var = new CalendarVariable();
				var.setImplicit(false);
				var.setScope(Scope.SALARY); // DRAFT
				var.setName(name);
				var.setEndDate(date);
				var.setStartDate(date);
				var.setExpression(Double.toString(draftMapDaysCoefficientEre.get(date)));
			}	
		}
		
		if(var != null){
			variablesList.add(var);
		}
		
		return variablesList;
	}
	
	public ArrayList<StringVariable> getVariablesListStrike(Date startDate, Date endDate) {

		ArrayList<StringVariable> variablesList = new ArrayList<StringVariable>();
		
		if ( draftMapDaysCoefficientStrike.isEmpty() )
			return variablesList;
		
		List<Date> orderedDraftDatesCS = draftMapDaysCoefficientStrike.keySet().stream().collect(Collectors.toList());
		Collections.sort(orderedDraftDatesCS);
		
		CalendarVariable var = null;
		Date dateBefore = null;
		String name = "DIAS_HUELGA";
		Integer contStrikeDays = 0;
		
		if(null == endDate)
			endDate = DateUtils.copyDateOnly(orderedDraftDatesCS.get(orderedDraftDatesCS.size() - 1));
		
		for(Date date : orderedDraftDatesCS){
			
			if(date.before(startDate))
				continue;
			if(date.after(endDate))
				continue;
			
			if(isNext(date, dateBefore)){
				contStrikeDays++;
				var.setExpression(Integer.toString(contStrikeDays));
				var.setEndDate(date);
				dateBefore = DateUtils.copyDateOnly(date);
				continue;
			}else{
				if(var != null){
					variablesList.add(var);
					contStrikeDays=0;
				}	
				
				dateBefore = DateUtils.copyDateOnly(date);
				contStrikeDays++;
				
				var = new CalendarVariable();
				var.setImplicit(false);
				var.setScope(Scope.SALARY); // DRAFT
				var.setName(name);
				var.setEndDate(date);
				var.setStartDate(date);
				var.setExpression(Integer.toString(contStrikeDays));
			}	
		}
		
		if(var != null){
			variablesList.add(var);
		}
		
		return variablesList;
		
	}
	
	public ArrayList<StringVariable> getVariablesListHolidays(Date startDate, Date endDate) {

		ArrayList<StringVariable> variablesList = new ArrayList<StringVariable>();
		List<Date> orderedDraftDatesHolidays = getHolidaysDates();
		
		if(orderedDraftDatesHolidays.isEmpty())
			return variablesList;
		
		Collections.sort(orderedDraftDatesHolidays);
		
		CalendarVariable var = null;
		Date dateBefore = null;
		String name = "DIAS_VACACIONES";
		Integer contHolidayDays = 0;
		
		if(null == endDate)
			endDate = DateUtils.copyDateOnly(orderedDraftDatesHolidays.get(orderedDraftDatesHolidays.size() - 1));
		
		
		for(Date date : orderedDraftDatesHolidays){
			
			if(date.before(startDate))
				continue;
			if(date.after(endDate))
				continue;
			
			if(isNext(date, dateBefore)){
				contHolidayDays++;
				var.setExpression(Integer.toString(contHolidayDays));
				var.setEndDate(date);
				dateBefore = DateUtils.copyDateOnly(date);
				continue;
			}else{
				if(var != null){
					variablesList.add(var);
					contHolidayDays=0;
				}	
				
				dateBefore = DateUtils.copyDateOnly(date);
				contHolidayDays++;
				
				var = new CalendarVariable();
				var.setImplicit(false);
				var.setScope(Scope.SALARY); // DRAFT
				var.setName(name);
				var.setEndDate(date);
				var.setStartDate(date);
				var.setExpression(Integer.toString(contHolidayDays));
			}	
		}
		
		if(var != null){
			variablesList.add(var);	
		}
		
		return variablesList;
	}
	
	public ArrayList<StringVariable> getVariablesListExtraHours(Date draftStartDate, Date draftEndDate) {
		ArrayList<StringVariable> variablesList = new ArrayList<StringVariable>();
		
		if(draftMapExtraHours.isEmpty())
			return variablesList;
		
		CalendarVariable var = null;
		if(null == draftEndDate)
			draftEndDate = DateUtils.copyDateOnly(DateUtils.getLastDayOfMonth(draftStartDate));
		
		for (Entry<Date, Double> e : draftMapExtraHours.entrySet()){					
			if(!e.getKey().equals(draftStartDate))
				continue;
			
			var = new CalendarVariable();
			var.setImplicit(false);
			var.setScope(Scope.SALARY); // DRAFT
			var.setName("HORAS_EXTRAS");
			var.setStartDate(e.getKey());
			var.setEndDate(DateUtils.copyDateOnly(draftEndDate));
			var.setExpression(Double.toString(e.getValue()));
			
			variablesList.add(var);
			
		}
		
		return variablesList;
	}
	
	@SuppressWarnings("deprecation")
	public ArrayList<StringVariable> getVariablesList(Date startDate, Date endDate) {

		ArrayList<StringVariable> variablesList = new ArrayList<StringVariable>();
		
		if ( draftMapDaysHour.isEmpty() )
			return variablesList;
		
		Date lastDraftDate = new Date(0);
		for (Date date : draftMapDaysHour.keySet())
			if (date.after(lastDraftDate))
				lastDraftDate = DateUtils.copyDateOnly(date);
		
		if (startDate.after(lastDraftDate))
			return variablesList;
		
		if ( endDate == null )
			endDate = DateUtils.copyDateOnly(lastDraftDate);
		
		for ( int day = 0; day <= 6 ; day++ ){
			
			LinkedList<CalendarVariable> queue = new LinkedList<CalendarVariable>();
			
			Date date = DateUtils.copyDateOnly(startDate);
			DateUtils.addDays2Date(date, day);
			String name = calculateDayOfWeek(date.getDay());
			
			CalendarVariable var = null ;
			for(Date start = startDate ; date.compareTo(endDate) <= 0 ; start = DateUtils.addDays2Date(date, 7) ){
				
				if (!draftMapDaysHour.containsKey(date)){
					var = null;
					continue;
				}
				
				Double hours = draftMapDaysHour.get(date);
				if(null == hours){
					if ( var != null && var.getValue() == null) {
						DateUtils.resetTime(date); //Para no pasarnos al siguiente mes
						var.setEndDate(DateUtils.copyDateOnly(date));
						continue;
					}
				}else{
					if ( var != null && var.getValue().equals(Double.toString(hours))) {
						DateUtils.resetTime(date); //Para no pasarnos al siguiente mes
						var.setEndDate(DateUtils.copyDateOnly(date));
						continue;
					}
				}
				
				var = new CalendarVariable();
				var.setName(name);
				var.setValue(hours);
				var.setImplicit(false);
				var.setScope(Scope.SALARY); // DRAFT
				if(null == hours)
					var.setExpression(null);
				else
					var.setExpression(Double.toString(hours));
				DateUtils.resetTime(date); //Para no pasarnos al siguiente mes
				var.setEndDate(DateUtils.copyDateOnly(date));
				var.setStartDate(DateUtils.copyDateOnly(start));
				
				queue.addLast(var);
				
			}
			
			if ( var != null ){
				DateUtils.resetTime(endDate); //Para no pasarnos al siguiente mes
				var.setEndDate(endDate);
			}
			
			variablesList.addAll(queue);
			
		}
		
		editDatesVariablesList(variablesList, startDate, endDate);
		
		//TODO: SI LO GUARDAS DESDE EL BORRADOR LO GUARDA PARA UN DIA MAS QUE LA FECHA FIN
//		for(StringVariable var : variablesList)
//			Window.alert(var.getName()+" = "+var.getExpression()+", startDate :"+var.getStartDate()+", endDate :"+var.getEndDate());
		
		return variablesList;
	}
	
	// ---------- AUX METHODS GET VARIABLES LIST --------
	
	private List<Date> getHolidaysDates() {
		List<Date> holidaysDates = new ArrayList<>();
		for(Entry<Date,DayType> e : draftMapDaysType.entrySet()){
			if(e.getValue().equals(DayType.HOLIDAY))
				holidaysDates.add(e.getKey());
		}
		return holidaysDates;
	}

	private boolean isNext(Date date, Date dateBefore) {
		if(dateBefore == null)
			return false;
		else if(DateUtils.addDays2Date(dateBefore, 1).equals(date))
			return true;
		else
			return false;
	}

	@SuppressWarnings("deprecation")
	private void editDatesVariablesList(ArrayList<StringVariable> variablesList, Date startDate, Date endDate) {
		for(StringVariable v : variablesList){
			if(v.getStartDate().getDay() == 1){
				changeEndDate(v, endDate);
				continue;
			}else if(v.getEndDate().getDay() == 0){
				changeStartDate(v, startDate);
				continue;
			}else{
				changeStartDate(v, startDate);
				changeEndDate(v, endDate);
			}		
		}
	}
	
	@SuppressWarnings("deprecation")
	private void changeEndDate(StringVariable v, Date endDate) {
		Date endDateAux = DateUtils.copyDateOnly(v.getEndDate());
		while (endDateAux.getDay() != 0){
			if(endDateAux.equals(endDate))
				break;
			
			DateUtils.addDays2Date(endDateAux, 1);
		}
		v.setEndDate(endDateAux);
	}

	@SuppressWarnings("deprecation")
	private void changeStartDate(StringVariable v, Date startDate) {
		Date startDateAux = DateUtils.copyDateOnly(v.getStartDate());
		while (startDateAux.getDay() != 1){
			if(startDateAux.equals(startDate))
				break;
			
			DateUtils.addDays2Date(startDateAux, -1);
		}
		v.setStartDate(startDateAux);
	}

	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void initializeDBCalendar(Consumer<EmployeeCalendarData> success, Consumer<Throwable> failure) {
		
		employeesService.getEmployeeCalendar(employeeId, new AsyncCallback<EmployeeCalendarData>() {
			
			@Override
			public void onSuccess(EmployeeCalendarData result) {
				List<Quartet<java.sql.Date, java.sql.Date, String, String>> hoursList = result.getContractHoursList();
				List<Quartet<java.sql.Date, java.sql.Date, String, String>> extraHoursList = result.getContractExtraHoursList();
				List<Quartet<java.sql.Date, java.sql.Date, String, String>> typesList = result.getContractTypeDaysList();
				List<Quartet<java.sql.Date, java.sql.Date, String, String>> ITDaysList = result.getcontractITDayTypeList();
				ArrayList<Byte> nonWorkingList = result.getContractNonWorkingDaysList();
				HashMap<java.util.Date, String> festivesList = result.getContractFestiveDaysList();
				initializeNonWorkingsDaysTypeMap(nonWorkingList);
				initializeHoursMap(hoursList);
				initializeExtraHoursMap(extraHoursList);
				initializeTypesMap(typesList);
				initializeFestivesDaysTypeMap(festivesList);
				initializeITDaysTypeMap(ITDaysList);
				fullTimeEmployee = result.isFullTimeJourney();
				if (fullTimeEmployeeDraft == -1)
					fullTimeEmployeeDraft = result.isFullTimeJourney() ? 1 : 0;
				
				success.accept(result);
				
			}

			private void initializeExtraHoursMap(List<Quartet<java.sql.Date, java.sql.Date, String, String>> extraHoursList) {
				for (Quartet<java.sql.Date, java.sql.Date, String, String> quarterExtraHours : extraHoursList){
					Date startDate = DateUtils.copyDateOnly(quarterExtraHours.getStartDate());
					DateUtils.resetTime(startDate);
					mapExtraHours.put(startDate, Double.parseDouble(quarterExtraHours.getExpression()));
				}
				
			}

			private void initializeHoursMap(List<Quartet<java.sql.Date, java.sql.Date, String, String>> hoursList) {
				
				for (Quartet<java.sql.Date, java.sql.Date, String, String> quarterHours : hoursList) {
					
					Date startDate = DateUtils.copyDateOnly(quarterHours.getStartDate());
					Date endDateAux = DateUtils.getLastDayOfYear(new Date());
					Date endDate;
					
					if (quarterHours.getEndDate() == null){
						endDate = DateUtils.addYears2Date(endDateAux, 1);
					}else
						endDate = DateUtils.copyDateOnly(quarterHours.getEndDate());;
					
					DateUtils.addDays2Date(endDate, 1);
					
					Double hour = null;
					try{
						hour = Double.parseDouble(quarterHours.getExpression());
					}catch (NumberFormatException e) {
						
					}
					
					String stringHourDay = quarterHours.getName();
					
					//Window.alert(stringHourDay+" : "+hour);
					
					@SuppressWarnings("deprecation")
					int initialDay = startDate.getDay();
					int findingDay = DAY_OF_WEEKS.get(stringHourDay);
					
					int auxDay = findingDay - initialDay;
					
					if (auxDay == 7)
						auxDay = 0;
					
					if (auxDay < 0)
						auxDay = 7 + auxDay;
					
					Date auxDate = DateUtils.addDays2Date(startDate, auxDay);
					
					while (auxDate.before(endDate) || auxDate.equals(endDate)){
						Date date = DateUtils.copyDateOnly(auxDate);
						DateUtils.resetTime(date);
						mapDaysHour.put(date, hour);
						
						//if(-1 == hour){
						if(null == hour){
							Date dateType = DateUtils.copyDateOnly(auxDate);
							mapDaysType.put(dateType, DayType.NOWORKINGDAY);
						}else{
							Date dateType = DateUtils.copyDateOnly(auxDate);
							mapDaysType.put(dateType, DayType.NOTYPEDAY);
						}
							
						DateUtils.addDays2Date(auxDate, 7);
					}
				}
				
			}
			
			private void initializeNonWorkingsDaysTypeMap(ArrayList<Byte> nonWorkingList) {
				
				int cont = 0;
				
				for (Byte nonWorkingDay : nonWorkingList) {
					
					Date startDate = DateUtils.copyDateOnly(startContract);
					Date endDateAux = DateUtils.getLastDayOfYear(new Date());
					Date endDate;
					
					if (endContract == null)
						endDate = DateUtils.addYears2Date(endDateAux, 1);
					else
						endDate = DateUtils.copyDateOnly(endContract);
					
					DayType dayType;
					
					if (0 == nonWorkingDay.byteValue())
						dayType = DayType.NOTYPEDAY;
					else
						dayType = DayType.NOWORKINGDAY;
					
					String stringHourDay = calculateNonWorkingDayOfWeek(cont);
					
					@SuppressWarnings("deprecation")
					int initialDay = startDate.getDay();
					int findingDay = DAY_OF_WEEKS.get(stringHourDay)+1;
					
					int auxDay = findingDay - initialDay;
					
					if (auxDay == 7)
						auxDay = 0;
					
					if (auxDay < 0)
						auxDay = 7 + auxDay;
					
					Date auxDate = DateUtils.addDays2Date(startDate, auxDay);
					
					while (auxDate.before(endDate) || auxDate.equals(endDate)){
						Date date = DateUtils.copyDateOnly(auxDate);
						DateUtils.resetTime(date);
						mapDaysType.put(date, dayType);
						DateUtils.addDays2Date(auxDate, 7);
					}
					cont++;
				}	
			}
			
			private void initializeTypesMap(List<Quartet<java.sql.Date, java.sql.Date, String, String>> typesList) {
				
				for (Quartet<java.sql.Date, java.sql.Date, String, String> quarterTypes : typesList) {
					
					Date startDate = DateUtils.copyDateOnly(quarterTypes.getStartDate());
					Date endDateAux = DateUtils.getLastDayOfYear(new Date());
					Date endDate;
					
					if (quarterTypes.getEndDate() == null){
						endDate = DateUtils.addYears2Date(endDateAux, 1);
					}else
						endDate = DateUtils.copyDateOnly(quarterTypes.getEndDate());;
					
					DateUtils.addDays2Date(endDate, 1);
					
					DayType dayType = TYPE_OF_DAY.get(quarterTypes.getName());
					
					Date auxDate = DateUtils.copyDateOnly(startDate);
					
					while (auxDate.before(endDate)){
						Date date = DateUtils.copyDateOnly(auxDate);
						DateUtils.resetTime(date);
						mapDaysType.put(date, dayType);
						DateUtils.addDays2Date(auxDate, 1);
					}
				}	
			}
			
			private void initializeFestivesDaysTypeMap(HashMap<java.util.Date, String> festivesList) {
				for(Entry<java.util.Date, String> entry : festivesList.entrySet()){
					mapDaysType.put(entry.getKey(), DayType.FREEDAY);
					mapFestivesDays.put(entry.getKey(), entry.getValue());
				}
			}
			
			private void initializeITDaysTypeMap(List<Quartet<java.sql.Date, java.sql.Date, String, String>> iTDaysList) {
				for (Quartet<java.sql.Date, java.sql.Date, String, String> quarterTypes : iTDaysList) {
					
					Date startDate = DateUtils.copyDateOnly(quarterTypes.getStartDate());
					Date endDateAux = DateUtils.getLastDayOfYear(new Date());
					Date endDate;
					
					if (quarterTypes.getEndDate() == null){
						endDate = DateUtils.addYears2Date(endDateAux, 1);
					}else
						endDate = DateUtils.copyDateOnly(quarterTypes.getEndDate());;
					
					DateUtils.addDays2Date(endDate, 1);
					
					DayType dayType = DayType.BAJAIT;
					//TYPE_OF_DAY.get(quarterTypes.getName());
					
					Date auxDate = DateUtils.copyDateOnly(startDate);
					
					while (auxDate.before(endDate)){
						Date date = DateUtils.copyDateOnly(auxDate);
						DateUtils.resetTime(date);
						mapDaysType.put(date, dayType);
						DateUtils.addDays2Date(auxDate, 1);
					}
				}
				
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void updateDBCalendar(Consumer<EmployeeCalendarUpdate> success, Consumer<Throwable> failure){
		
		EmployeeCalendarUpdate updateInfo = new EmployeeCalendarUpdate();
		
		updateInfo.setDaysHourMap(createUpdateHoursMap(mapDaysHour, draftMapDaysHour));
		updateInfo.setMonthExtraHoursList(createUpdateExtraHoursList(mapExtraHours, draftMapExtraHours));
		updateInfo.setDaysTypeMap(createUpdateTypesMap(mapDaysType, draftMapDaysType));
		updateInfo.setFullTimeEmployee(this.fullTimeEmployee);
		
		employeesService.setEmployeeCalendar(employeeId, updateInfo, new AsyncCallback<EmployeeCalendarUpdate>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(EmployeeCalendarUpdate result) {
				draftMapDaysHour.clear();
				draftMapDaysType.clear();
				success.accept(result);
			}
			
		});
	}

	// -------- AUX METHODS DATABASE SYNC --------

	private String calculateDayOfWeek(int day) {
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
		
	private String calculateNonWorkingDayOfWeek(int day) {
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
	
	private HashMap<Date, Double> createUpdateHoursMap(Map<Date, Double> mapDaysHour,
			Map<Date, Double> draftMapDaysHour) {
		
		HashMap<Date, Double> updateHoursMap = new HashMap<Date, Double>();
		
		for ( Entry<Date,Double> entry : mapDaysHour.entrySet()){
			DateUtils.resetTime(entry.getKey());
			updateHoursMap.put(entry.getKey(), entry.getValue());
		}
		
		for (Entry<Date,Double> entry : draftMapDaysHour.entrySet()){
			DateUtils.resetTime(entry.getKey());
			updateHoursMap.put(entry.getKey(), entry.getValue());
		}
		
		return updateHoursMap;
	}
	
	private List<Quartet<java.sql.Date, java.sql.Date, String, String>> createUpdateExtraHoursList(
			Map<Date, Double> mapExtraHours, Map<Date, Double> draftMapExtraHours) {
		
		HashMap<Date, Double> updateExtraHoursMap = new HashMap<Date, Double>();
		
		for ( Entry<Date,Double> entry : mapExtraHours.entrySet()){
			DateUtils.resetTime(entry.getKey());
			updateExtraHoursMap.put(entry.getKey(), entry.getValue());
		}
		
		for (Entry<Date,Double> entry : draftMapExtraHours.entrySet()){
			DateUtils.resetTime(entry.getKey());
			updateExtraHoursMap.put(entry.getKey(), entry.getValue());
		}
		
		List<Quartet<java.sql.Date, java.sql.Date, String, String>> updateExtraHoursList = new ArrayList<Quartet<java.sql.Date, java.sql.Date, String, String>>();
		
		createUpdateExtraHoursList(updateExtraHoursList, updateExtraHoursMap);
		
		return updateExtraHoursList;
	}
	
	private void createUpdateExtraHoursList(List<Quartet<java.sql.Date, java.sql.Date, String, String>> updateExtraHoursList,
			HashMap<Date, Double> updateExtraHoursMap) {
		
		for (Entry<Date, Double> entry : updateExtraHoursMap.entrySet()){
			Quartet<java.sql.Date, java.sql.Date, String, String> extraHoursQuarter = new Quartet<java.sql.Date, java.sql.Date, String, String>();
			
			java.sql.Date startDate = new java.sql.Date(entry.getKey().getTime());
			java.sql.Date endDate = new java.sql.Date(DateUtils.getLastDayOfMonth(entry.getKey()).getTime());
			
			extraHoursQuarter.setStartDate(startDate);
			extraHoursQuarter.setEndDate(endDate);
			extraHoursQuarter.setName("HORAS_EXTRAS");
			extraHoursQuarter.setExpression(Double.toString(entry.getValue()));
			
			updateExtraHoursList.add(extraHoursQuarter);
		}
		
	}

	private HashMap<Date, DayType> createUpdateTypesMap(Map<Date, DayType> mapDaysType,
			Map<Date, DayType> draftMapDaysType) {
		
		HashMap<Date, DayType> updateTypesMap = new HashMap<Date, DayType>();
		
		for ( Entry<Date,DayType> e : mapDaysType.entrySet()){
			updateTypesMap.put(e.getKey(), e.getValue());
		}
		
		for (Entry<Date,DayType> e : draftMapDaysType.entrySet()){
			updateTypesMap.put(e.getKey(), e.getValue());
		}
		
		return updateTypesMap;
	}

	public void getSalaryDraftChanged(SalaryDraft salaryDraft) {
		for (Variable var : salaryDraft.getDraftContext()){
			if(var.getName().equals("TIEMPO_COMPLETO"))
				if (var.getExpression().equals("false"))
					this.fullTimeEmployeeDraft = 0;
				else
					this.fullTimeEmployeeDraft = 1;
				
		}
		
	}
	
}
