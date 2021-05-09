package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.shared.DateUtils;

@SuppressWarnings("serial")
public class CalendarHoursExtraCompl implements Serializable {
	
	public static class DayHourExtraCompl implements Serializable {
		private Date startDate;
		private Date endDate;
		private Double value;
		
		public DayHourExtraCompl() {
			super();
		}
		
		public DayHourExtraCompl(Date startDate, Date endDate, Double value) {
			this.startDate = startDate;
			this.endDate = endDate;
			this.value = value;
		}

		public Date getStartDate() {
			return startDate;
		}

		public DayHourExtraCompl setStartDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}

		public Date getEndDate() {
			return endDate;
		}

		public DayHourExtraCompl setEndDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}

		public Double getValue() {
			return value;
		}

		public DayHourExtraCompl setValue(Double value) {
			this.value = value;
			return this;
		}
		
	}
	
	// -----------------------------------------------------
	// 						VARIABLES
	// -----------------------------------------------------
	
	private List<DayHourExtraCompl> dayHoursComplementary;
	private Map<Date, Double> mapDayHoursComplementary;

	private Date contractStartDate;
	private static Date contractEndDate;
	
	public CalendarHoursExtraCompl() {
		super();
		dayHoursComplementary = new ArrayList<CalendarHoursExtraCompl.DayHourExtraCompl>();
		mapDayHoursComplementary = new HashMap<Date, Double>();
	}
	
	public CalendarHoursExtraCompl(List<DayHourExtraCompl> dayHoursComplementary, Map<Date, Double> mapDayHoursComplementary) {
		this.dayHoursComplementary = dayHoursComplementary;
		this.mapDayHoursComplementary = mapDayHoursComplementary;
	}
	
	public Date getContractStartDate() {
		return contractStartDate;
	}

	public void setDayHoursComplementary(List<DayHourExtraCompl> dayHoursComplementary) {
		this.dayHoursComplementary = dayHoursComplementary;
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	public static Date getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDateIn) {
		contractEndDate = contractEndDateIn;
	}
	
	public List<DayHourExtraCompl> getComplementaryHours() {
		sortDayHoursComplementary();
		return dayHoursComplementary;
	}
	
	// -----------------------------------------------------
	// 						METHODS INIT
	// -----------------------------------------------------
	
	public void initMapDayHoursComplementary() {
		this.mapDayHoursComplementary = new HashMap<Date, Double>();
		
		sortDayHoursComplementary();
		
		for(DayHourExtraCompl dayHourComplementary : dayHoursComplementary) {
			
			Date iterableDate = DateUtils.copyDateOnly(dayHourComplementary.getStartDate());
			DateUtils.resetTime(iterableDate);
			
			// Si es null la fecha fin le pongo el 1 de enero del 2021 para ver como se dibuja
			Date newEndDate = null;
			
			if(null == dayHourComplementary.getEndDate())
				newEndDate = DateUtils.getLastDayOfYear(DateUtils.getYear() - 1900 + 1);
			else
				newEndDate = DateUtils.copyDateOnly(dayHourComplementary.getEndDate());
			
			DateUtils.resetTime(newEndDate);
			
			while(iterableDate.before(newEndDate) || iterableDate.equals(newEndDate)) {
				
				Date date = DateUtils.copyDateOnly(iterableDate);
				DateUtils.resetTime(date);
				mapDayHoursComplementary.put(date, dayHourComplementary.getValue());
				
				DateUtils.addDays2Date(iterableDate, 1);
			}
		}
		
	}
	
	private void sortDayHoursComplementary() {
		dayHoursComplementary.sort(new Comparator<DayHourExtraCompl>() {

			@Override
			public int compare(DayHourExtraCompl dayHourComplementary1, DayHourExtraCompl dayHourComplementary2) {
				return DateUtils.compare(dayHourComplementary1.getStartDate(), dayHourComplementary2.getStartDate());
			}
		});
	}

	// -----------------------------------------------------
	// 						AUX METHODS
	// -----------------------------------------------------
	
	public boolean isEmpty() {
		return dayHoursComplementary.isEmpty();
	}
	
	public Double getHourByDate(Date date) {
		DateUtils.resetTime(date);
		return this.mapDayHoursComplementary.get(date);
	}
	
	public void addDayHourComplementary(DayHourExtraCompl newDayHourExtraCompl) {
		// Ordenamos los tramos existentes
		this.dayHoursComplementary.sort(new Comparator<DayHourExtraCompl>() {
			@Override
			public int compare(DayHourExtraCompl dayHourExtraCompl1, DayHourExtraCompl dayHourExtraCompl2) {
				return dayHourExtraCompl1.getStartDate().compareTo(dayHourExtraCompl2.getStartDate());
			}
		});
		
		// Creamos la nueva lista
		ArrayList<DayHourExtraCompl> newHourExtraComplList = new ArrayList<DayHourExtraCompl>();
		
		// Inicializamos si variable que nos indica si ha sido insertado
		Boolean added = false;
		
		// Si eta vacio se inserta directamente
		if(this.dayHoursComplementary.isEmpty()) {
			newHourExtraComplList.add(newDayHourExtraCompl);
			added = true;
		}
		
		for(DayHourExtraCompl dayHourExtraCompl: this.dayHoursComplementary) {
			if(added) {
				// Si la fecha fin del nuevo tramo es null ya no se inserta ninguno mas
				if(!isNullOrEndContract(newDayHourExtraCompl.getEndDate())) {
					// Si el dia que estamos analizando es null cogemos el endDate del ultimo dia insertado y se lo ponemos con startDate
					if(isNullOrEndContract(dayHourExtraCompl.getEndDate())) {
						DayHourExtraCompl lastDayHour = newHourExtraComplList.get(newHourExtraComplList.size()-1);
						Date newStartDate = DateUtils.copyDateOnly(lastDayHour.getEndDate());
						newStartDate = DateUtils.addDays2Date(newStartDate, 1);
						
						dayHourExtraCompl.setStartDate(newStartDate);
						
						newHourExtraComplList.add(dayHourExtraCompl);
					} else if(newDayHourExtraCompl.getEndDate().equals(dayHourExtraCompl.getStartDate())) {
						Date newStartDate = DateUtils.copyDateOnly(dayHourExtraCompl.getStartDate());
						DateUtils.resetTime(newStartDate);
						DateUtils.addDays2Date(newStartDate, 1);
						
						dayHourExtraCompl.setStartDate(newStartDate);
						newHourExtraComplList.add(dayHourExtraCompl);
			
					// Si la fecha fin del nuevo es posterior al analizado, no se añade
					}else if(afterOrEqual(newDayHourExtraCompl.getEndDate(), dayHourExtraCompl.getEndDate())) {
						continue;
					} else if(newDayHourExtraCompl.getEndDate().before(dayHourExtraCompl.getEndDate()) && newDayHourExtraCompl.getEndDate().after(dayHourExtraCompl.getStartDate())) {
						Date newStartDate = DateUtils.copyDateOnly(newDayHourExtraCompl.getEndDate());
						DateUtils.addDays2Date(newStartDate, 1);
						DateUtils.resetTime(newStartDate);
						
						newHourExtraComplList.add(new DayHourExtraCompl(newStartDate, dayHourExtraCompl.getEndDate(), dayHourExtraCompl.getValue()));	
					} else
						newHourExtraComplList.add(dayHourExtraCompl);
				}
			} else {
				// Si el elemento de la lista es anterior al insertado
				if(beforeOrEqual(dayHourExtraCompl.getStartDate(), newDayHourExtraCompl.getStartDate())) {
					
					// Si el elemento de la lista tiene fecha fin null, tendremos que meter el nuevo tramo entre medias
					if(isNullOrEndContract(dayHourExtraCompl.getEndDate())) {
						if(isNullOrEndContract(newDayHourExtraCompl.getEndDate())) {
							if(dayHourExtraCompl.getStartDate().equals(newDayHourExtraCompl.getStartDate())) {
								newHourExtraComplList.add(newDayHourExtraCompl);
								added = true;
								continue;
							}else {
								Date newEndDate = DateUtils.copyDateOnly(newDayHourExtraCompl.getStartDate());
								newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
								dayHourExtraCompl.setEndDate(newEndDate);
								
								newHourExtraComplList.add(dayHourExtraCompl);
								newHourExtraComplList.add(newDayHourExtraCompl);
								added = true;
								continue;
							}
						} else {
							Date oldEndDate = null;
							if(null != dayHourExtraCompl.getEndDate()) {
								oldEndDate = DateUtils.copyDateOnly(dayHourExtraCompl.getEndDate());
								DateUtils.resetTime(oldEndDate);
							}
							
							Date newEndDate = DateUtils.copyDateOnly(newDayHourExtraCompl.getStartDate());
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							dayHourExtraCompl.setEndDate(newEndDate);
							
							newHourExtraComplList.add(dayHourExtraCompl);
							newHourExtraComplList.add(newDayHourExtraCompl);
							
							Date newStartDate = DateUtils.copyDateOnly(newDayHourExtraCompl.getEndDate());
							newStartDate = DateUtils.addDays2Date(newStartDate, 1);
							DayHourExtraCompl postNewDayHour = new DayHourExtraCompl(newStartDate, oldEndDate, dayHourExtraCompl.getValue());
							
							newHourExtraComplList.add(postNewDayHour);
							
							added = true;
							continue;
						}
					// Si el elemento a añadir es el mismo tramo de la lista
					} else if (dayHourExtraCompl.getEndDate().equals(newDayHourExtraCompl.getStartDate())) {
						
						Date newEndDate = DateUtils.copyDateOnly(dayHourExtraCompl.getEndDate());
						DateUtils.resetTime(newEndDate);
						DateUtils.deleteDays2Date(newEndDate, 1);
						
						dayHourExtraCompl.setEndDate(newEndDate);
						
						newHourExtraComplList.add(dayHourExtraCompl);
						newHourExtraComplList.add(newDayHourExtraCompl);
						added = true;
						continue;
					} else if(newDayHourExtraCompl.getStartDate().equals(dayHourExtraCompl.getStartDate()) && newDayHourExtraCompl.getEndDate().equals(dayHourExtraCompl.getEndDate())) {
						newHourExtraComplList.add(newDayHourExtraCompl);
						added = true;
						continue;
					// Si el elemento a añadir es posterior al tramo de la lista
					} else if(beforeOrEqual(dayHourExtraCompl.getEndDate(), newDayHourExtraCompl.getStartDate()) &&
							!isBetween(dayHourExtraCompl, newDayHourExtraCompl)) {
						newHourExtraComplList.add(dayHourExtraCompl);
						continue;
					// Si el elemento a añadir esta en un tramo existente
					} else if(afterOrEqual(dayHourExtraCompl.getEndDate(), newDayHourExtraCompl.getStartDate())) {
						DayHourExtraCompl postNewDayHour = null;
						
						if(!isNullOrEndContract(newDayHourExtraCompl.getEndDate()) && newDayHourExtraCompl.getEndDate().before(dayHourExtraCompl.getEndDate())) {
							Date newStartDate = DateUtils.copyDateOnly(newDayHourExtraCompl.getEndDate());
							newStartDate = DateUtils.addDays2Date(newStartDate, 1);
							
							postNewDayHour = new DayHourExtraCompl(newStartDate, dayHourExtraCompl.getEndDate(), dayHourExtraCompl.getValue());
						}
						
						if(!newDayHourExtraCompl.getStartDate().equals(dayHourExtraCompl.getStartDate()) &&
								!newDayHourExtraCompl.getEndDate().equals(dayHourExtraCompl.getEndDate()) &&
								!isBetween(dayHourExtraCompl, newDayHourExtraCompl)) {
								Date newEndDate = DateUtils.copyDateOnly(newDayHourExtraCompl.getStartDate());
								newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
								dayHourExtraCompl.setEndDate(newEndDate);
								
								newHourExtraComplList.add(dayHourExtraCompl);
							} else if(newDayHourExtraCompl.getEndDate().equals(dayHourExtraCompl.getEndDate())) {
								Date newEndDate = DateUtils.copyDateOnly(newDayHourExtraCompl.getStartDate());
								newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
								dayHourExtraCompl.setEndDate(newEndDate);
								
								newHourExtraComplList.add(dayHourExtraCompl);
							}
							
							newHourExtraComplList.add(newDayHourExtraCompl);
							
							if(null != postNewDayHour)
								newHourExtraComplList.add(postNewDayHour);
							
							added = true;
							continue;
					}
				} else {
					
					// Si el elemento a añadir es anterior al tramo de la lista
					if(isNullOrEndContract(newDayHourExtraCompl.getEndDate())) {
						newHourExtraComplList.add(newDayHourExtraCompl);
						added = true;
						continue;
					} else if (beforeOrEqual(newDayHourExtraCompl.getEndDate(), dayHourExtraCompl.getStartDate())) {
						if(newDayHourExtraCompl.getEndDate().equals(dayHourExtraCompl.getStartDate())) {
							Date newStartDate = DateUtils.copyDateOnly(dayHourExtraCompl.getStartDate());
							DateUtils.resetTime(newStartDate);
							DateUtils.addDays2Date(newStartDate, 1);
							dayHourExtraCompl.setStartDate(newStartDate);
						}
						
						newHourExtraComplList.add(newDayHourExtraCompl);
						newHourExtraComplList.add(dayHourExtraCompl);
						added = true;
						continue;
					} else if (afterOrEqual(newDayHourExtraCompl.getEndDate(), dayHourExtraCompl.getStartDate()))	{
						Date newStartDate = DateUtils.copyDateOnly(newDayHourExtraCompl.getEndDate());
						DateUtils.resetTime(newStartDate);
						DateUtils.addDays2Date(newStartDate, 1);
						
						dayHourExtraCompl.setStartDate(newStartDate);
						
						newHourExtraComplList.add(newDayHourExtraCompl);
						newHourExtraComplList.add(dayHourExtraCompl);
						
						added = true;
						continue;
					} else
						newHourExtraComplList.add(dayHourExtraCompl);
				}
			}
				
		}
		
		//Si todavía no se ha añadido es por que va al final de la lista y se añade al final de la lista
		if(!added){
			newHourExtraComplList.add(newDayHourExtraCompl);
		}
		
		// Asignamos la nueva lista a la antigua
		this.dayHoursComplementary = newHourExtraComplList;	
	}
	
	// -----------------------------------------------------
	// 						AUX METHODS
	// -----------------------------------------------------
	
	private boolean isBetween(DayHourExtraCompl dayHourComplementary, DayHourExtraCompl newDayHourComplementary) {
		return afterOrEqual(dayHourComplementary.getStartDate(), newDayHourComplementary.getStartDate()) &&
				beforeOrEqual(dayHourComplementary.getEndDate(), newDayHourComplementary.getEndDate());
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
		String result = "";
		for( Entry<Date, Double> entry: this.mapDayHoursComplementary.entrySet()) {
			result += entry.getKey() + " -> " + entry.getValue() + "\n";
		}
		return result;
	}
	
	public String toStringList() {
		String result = "";
		for(DayHourExtraCompl dayHourComplementary: this.dayHoursComplementary) {
			result += dayHourComplementary.getStartDate() + " - " +  dayHourComplementary.getEndDate() + " -> " +  dayHourComplementary.getValue() + "\n";
		}
		return result;
	}
	
}

