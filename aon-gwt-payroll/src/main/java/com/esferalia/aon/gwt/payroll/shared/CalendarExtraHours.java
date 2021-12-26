package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.shared.DateUtils;

@SuppressWarnings("serial")
public class CalendarExtraHours implements Serializable {
	
	public static class DayHourExtra implements Serializable {
		
		private String startDate;
		private String endDate;
		private Double value;
		
		public DayHourExtra() {
			super();
		}
		
		public DayHourExtra(Date startDate, Date endDate, Double value) {
			this.startDate = format(startDate);
			this.endDate = format(endDate);
			this.value = value;
		}

		public Date getStartDate() {
			return parse(startDate);
		}

		public DayHourExtra setStartDate(Date startDate) {
			this.startDate = format(startDate);
			return this;
		}

		public Date getEndDate() {
			return parse(endDate);
		}

		public DayHourExtra setEndDate(Date endDate) {
			this.endDate = format(endDate);
			return this;
		}

		public Double getValue() {
			return value;
		}

		public DayHourExtra setValue(Double value) {
			this.value = value;
			return this;
		}
		
	}
	
	// ------------------------------------- Variables
	
	private List<DayHourExtra> extraHours;
	private Map<Date, Double> extraHoursMap;

	private String contractStartDate;
	private String contractEndDate;
	
	// ------------------------------------- Constructor
	
	public CalendarExtraHours() {
		super();
		extraHours = new ArrayList<>();
		extraHoursMap = new TreeMap<>();
	}
	
	public CalendarExtraHours(List<DayHourExtra> extraHours, Map<Date, Double> extraHoursMap) {
		this.extraHours = extraHours;
		this.extraHoursMap = extraHoursMap;
	}
	
	// ------------------------------------- Getter/Setter
	
	public Date getContractStartDate() {
		return parse(contractStartDate);
	}
	
	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = format(contractStartDate);
	}
	
	public Date getContractEndDate() {
		return parse(contractEndDate);
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = format(contractEndDate);
	}
	
	public List<DayHourExtra> getExtraHours() {
		sortExtraHours();
		return extraHours;
	}
	
	public void setDayHoursComplementary(List<DayHourExtra> extraHours) {
		this.extraHours = extraHours;
	}

	// ------------------------------------- Auxiliar Methods
	
	public void initExtraHoursMap() {
		this.extraHoursMap = new TreeMap<>();
		
		sortExtraHours();
		
		for(DayHourExtra extraHour : extraHours) {
			
			Date iterableDate = DateUtils.copyDateOnly(extraHour.getStartDate());
			DateUtils.resetTime(iterableDate);
			
			// Si es null la fecha fin le pongo el 1 de enero del 2021 para ver como se dibuja
			Date newEndDate = null;
			
			if(null == extraHour.getEndDate())
				newEndDate = DateUtils.getLastDayOfYear(DateUtils.getYear() - 1900 + 1);
			else
				newEndDate = DateUtils.copyDateOnly(extraHour.getEndDate());
			
			DateUtils.resetTime(newEndDate);
			
			while(iterableDate.before(newEndDate) || iterableDate.equals(newEndDate)) {
				
				Date date = DateUtils.copyDateOnly(iterableDate);
				DateUtils.resetTime(date);
				extraHoursMap.put(date, extraHour.getValue());
				
				DateUtils.addDays2Date(iterableDate, 1);
			}
		}
		
	}
	
	private void sortExtraHours() {
		extraHours.sort((dayHourExtra1, dayHourExtra2) -> DateUtils.compare(dayHourExtra1.getStartDate(), dayHourExtra2.getStartDate()));
	}

	public boolean isEmpty() {
		return extraHours.isEmpty();
	}
	
	public Double getExtraHourByDate(Date date) {
		DateUtils.resetTime(date);
		return this.extraHoursMap.get(date);
	}
	
	public void addExtraHour(DayHourExtra newDayHourExtra) {
		// Ordenamos los tramos existentes
		sortExtraHours();
		
		// Creamos la nueva lista
		ArrayList<DayHourExtra> newExtraHourList = new ArrayList<>();
		
		// Inicializamos si variable que nos indica si ha sido insertado
		Boolean added = false;
		
		// Si eta vacio se inserta directamente
		if(this.extraHours.isEmpty()) {
			newExtraHourList.add(newDayHourExtra);
			added = true;
		}
		
		for(DayHourExtra dayHourExtra: this.extraHours) {
			if(Boolean.TRUE.equals(added)) {
				// Si la fecha fin del nuevo tramo es null ya no se inserta ninguno mas
				if(!isNullOrEndContract(newDayHourExtra.getEndDate())) {
					// Si el dia que estamos analizando es null cogemos el endDate del ultimo dia insertado y se lo ponemos con startDate
					if(isNullOrEndContract(dayHourExtra.getEndDate())) {
						DayHourExtra lastDayHour = newExtraHourList.get(newExtraHourList.size()-1);
						Date newStartDate = DateUtils.copyDateOnly(lastDayHour.getEndDate());
						newStartDate = DateUtils.addDays2Date(newStartDate, 1);
						
						dayHourExtra.setStartDate(newStartDate);
						
						newExtraHourList.add(dayHourExtra);
					} else if(newDayHourExtra.getEndDate().equals(dayHourExtra.getStartDate())) {
						Date newStartDate = DateUtils.copyDateOnly(dayHourExtra.getStartDate());
						DateUtils.resetTime(newStartDate);
						DateUtils.addDays2Date(newStartDate, 1);
						
						dayHourExtra.setStartDate(newStartDate);
						newExtraHourList.add(dayHourExtra);
			
					// Si la fecha fin del nuevo es posterior al analizado, no se añade
					} else if(afterOrEqual(newDayHourExtra.getEndDate(), dayHourExtra.getEndDate())) {
						continue;
					} else if(newDayHourExtra.getEndDate().before(dayHourExtra.getEndDate()) && newDayHourExtra.getEndDate().after(dayHourExtra.getStartDate())) {
						Date newStartDate = DateUtils.copyDateOnly(newDayHourExtra.getEndDate());
						DateUtils.addDays2Date(newStartDate, 1);
						DateUtils.resetTime(newStartDate);
						
						newExtraHourList.add(new DayHourExtra(newStartDate, dayHourExtra.getEndDate(), dayHourExtra.getValue()));	
					} else
						newExtraHourList.add(dayHourExtra);
				}
			} else {
				// Si el elemento de la lista es anterior al insertado
				if(beforeOrEqual(dayHourExtra.getStartDate(), newDayHourExtra.getStartDate())) {
					
					// Si el elemento de la lista tiene fecha fin null, tendremos que meter el nuevo tramo entre medias
					if(isNullOrEndContract(dayHourExtra.getEndDate())) {
						if(isNullOrEndContract(newDayHourExtra.getEndDate())) {
							if(dayHourExtra.getStartDate().equals(newDayHourExtra.getStartDate())) {
								newExtraHourList.add(newDayHourExtra);
								added = true;
							}else {
								Date newEndDate = DateUtils.copyDateOnly(newDayHourExtra.getStartDate());
								newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
								dayHourExtra.setEndDate(newEndDate);
								
								newExtraHourList.add(dayHourExtra);
								newExtraHourList.add(newDayHourExtra);
								added = true;
							}
						} else {
							Date oldEndDate = null;
							if(null != dayHourExtra.getEndDate()) {
								oldEndDate = DateUtils.copyDateOnly(dayHourExtra.getEndDate());
								DateUtils.resetTime(oldEndDate);
							}
							
							Date newEndDate = DateUtils.copyDateOnly(newDayHourExtra.getStartDate());
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							dayHourExtra.setEndDate(newEndDate);
							
							newExtraHourList.add(dayHourExtra);
							newExtraHourList.add(newDayHourExtra);
							
							Date newStartDate = DateUtils.copyDateOnly(newDayHourExtra.getEndDate());
							newStartDate = DateUtils.addDays2Date(newStartDate, 1);
							DayHourExtra postNewDayHour = new DayHourExtra(newStartDate, oldEndDate, dayHourExtra.getValue());
							
							newExtraHourList.add(postNewDayHour);
							
							added = true;
						}
					// Si el elemento a añadir es el mismo tramo de la lista
					} else if (dayHourExtra.getEndDate().equals(newDayHourExtra.getStartDate())) {
						
						Date newEndDate = DateUtils.copyDateOnly(dayHourExtra.getEndDate());
						DateUtils.resetTime(newEndDate);
						DateUtils.deleteDays2Date(newEndDate, 1);
						
						dayHourExtra.setEndDate(newEndDate);
						
						newExtraHourList.add(dayHourExtra);
						newExtraHourList.add(newDayHourExtra);
						added = true;
					} else if(newDayHourExtra.getStartDate().equals(dayHourExtra.getStartDate()) && newDayHourExtra.getEndDate().equals(dayHourExtra.getEndDate())) {
						newExtraHourList.add(newDayHourExtra);
						added = true;
					// Si el elemento a añadir es posterior al tramo de la lista
					} else if(beforeOrEqual(dayHourExtra.getEndDate(), newDayHourExtra.getStartDate()) && !isBetween(dayHourExtra, newDayHourExtra)) {
						newExtraHourList.add(dayHourExtra);
					// Si el elemento a añadir esta en un tramo existente
					} else if(afterOrEqual(dayHourExtra.getEndDate(), newDayHourExtra.getStartDate())) {
						DayHourExtra postNewDayHour = null;
						
						if(!isNullOrEndContract(newDayHourExtra.getEndDate()) && newDayHourExtra.getEndDate().before(dayHourExtra.getEndDate())) {
							Date newStartDate = DateUtils.copyDateOnly(newDayHourExtra.getEndDate());
							newStartDate = DateUtils.addDays2Date(newStartDate, 1);
							
							postNewDayHour = new DayHourExtra(newStartDate, dayHourExtra.getEndDate(), dayHourExtra.getValue());
						}
						
						if(!newDayHourExtra.getStartDate().equals(dayHourExtra.getStartDate()) &&
								!newDayHourExtra.getEndDate().equals(dayHourExtra.getEndDate()) &&
								!isBetween(dayHourExtra, newDayHourExtra)) {
								Date newEndDate = DateUtils.copyDateOnly(newDayHourExtra.getStartDate());
								newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
								dayHourExtra.setEndDate(newEndDate);
								
								newExtraHourList.add(dayHourExtra);
							} else if(newDayHourExtra.getEndDate().equals(dayHourExtra.getEndDate())) {
								Date newEndDate = DateUtils.copyDateOnly(newDayHourExtra.getStartDate());
								newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
								dayHourExtra.setEndDate(newEndDate);
								
								newExtraHourList.add(dayHourExtra);
							}
							
						newExtraHourList.add(newDayHourExtra);
							
							if(null != postNewDayHour)
								newExtraHourList.add(postNewDayHour);
							
							added = true;
					}
				} else {
					
					// Si el elemento a añadir es anterior al tramo de la lista
					if(isNullOrEndContract(newDayHourExtra.getEndDate())) {
						newExtraHourList.add(newDayHourExtra);
						added = true;
					} else if (beforeOrEqual(newDayHourExtra.getEndDate(), dayHourExtra.getStartDate())) {
						if(newDayHourExtra.getEndDate().equals(dayHourExtra.getStartDate())) {
							Date newStartDate = DateUtils.copyDateOnly(dayHourExtra.getStartDate());
							DateUtils.resetTime(newStartDate);
							DateUtils.addDays2Date(newStartDate, 1);
							dayHourExtra.setStartDate(newStartDate);
						}
						
						newExtraHourList.add(newDayHourExtra);
						newExtraHourList.add(dayHourExtra);
						added = true;
					} else if (afterOrEqual(newDayHourExtra.getEndDate(), dayHourExtra.getStartDate()))	{
						Date newStartDate = DateUtils.copyDateOnly(newDayHourExtra.getEndDate());
						DateUtils.resetTime(newStartDate);
						DateUtils.addDays2Date(newStartDate, 1);
						
						dayHourExtra.setStartDate(newStartDate);
						
						newExtraHourList.add(newDayHourExtra);
						newExtraHourList.add(dayHourExtra);
						
						added = true;
					} else
						newExtraHourList.add(dayHourExtra);
				}
			}
				
		}
		
		//Si todavía no se ha añadido es por que va al final de la lista y se añade al final de la lista
		if(Boolean.FALSE.equals(added)){
			newExtraHourList.add(newDayHourExtra);
		}
		
		// Asignamos la nueva lista a la antigua
		this.extraHours = newExtraHourList;	
	}
	
	// -----------------------------------------------------
	// 						AUX METHODS
	// -----------------------------------------------------
	
	private boolean isBetween(DayHourExtra dayHourExtra, DayHourExtra newDayHourExtra) {
		return afterOrEqual(dayHourExtra.getStartDate(), newDayHourExtra.getStartDate()) &&
				beforeOrEqual(dayHourExtra.getEndDate(), newDayHourExtra.getEndDate());
	}

	// -----------------------------------------------------
	// 					AUX METHODS (DATES)
	// -----------------------------------------------------
	
	private boolean beforeOrEqual(Date date1, Date date2) {
		return date1.before(date2) || date1.equals(date2);
	}
	
	private boolean afterOrEqual(Date date1, Date date2) {
		return date1.after(date2) || date1.equals(date2);
	}
	
	private boolean isNullOrEndContract(Date date) {
		return null == date || date.equals(getContractEndDate());
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		this.extraHoursMap.entrySet().forEach(entry -> builder.append(entry.getKey() + " -> " + entry.getValue() + "\n"));
		return builder.toString();
	}
	
	public String toStringList() {
		StringBuilder builder = new StringBuilder();
		this.extraHours.forEach(extraHour -> builder.append(extraHour.getStartDate() + " - " +  extraHour.getEndDate() + " -> " +  extraHour.getValue() + "\n"));
		return builder.toString();
	}
	
}

