package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CalendarHours.DayHours.DayHour;
import com.google.gwt.user.client.Window;

@SuppressWarnings("serial")
public class CalendarHours implements Serializable {
	
	public static class DayHours implements Serializable{
		
		public static class DayHour implements Serializable{
			private String startDate;
			private String endDate;
			private Double value;
			
			public DayHour() {
				super();
			}
			
			public DayHour(Date startDate, Date endDate, Double value) {
				this.startDate = Shared.format(startDate);
				this.endDate = format(endDate);
				this.value = value;
			}

			public Date getStartDate() {
				return Shared.parse(startDate);
			}

			public DayHour setStartDate(Date startDate) {
				this.startDate = Shared.format(startDate);
				return this;
			}

			public Date getEndDate() {
				return Shared.parse(endDate);
			}

			public DayHour setEndDate(Date endDate) {
				this.endDate = format(endDate);
				return this;
			}

			public Double getValue() {
				return value;
			}

			public DayHour setValue(Double value) {
				this.value = value;
				return this;
			}
			
		}
		
		private String hourName;
		private ArrayList<DayHour> hourList;
		
		public DayHours() {
			super();
		}
		
		public DayHours(String hourName) {
			this.hourName = hourName;
			this.hourList = new ArrayList<DayHour>();
		}

		public String getHourName() {
			return hourName;
		}

		public DayHours setHourName(String hourName) {
			this.hourName = hourName;
			return this;
		}

		public ArrayList<DayHour> getHourList() {
			return hourList;
		}
		
		// -----------------------------------------------------
		// 						ADD DAY TYPE
		// -----------------------------------------------------

		public void addDayHour(DayHour newDayHour) {
			// Ordenamos los tramos existentes
			this.hourList.sort(new Comparator<DayHour>() {
				@Override
				public int compare(DayHour dayHour1, DayHour dayHour2) {
					return dayHour1.getStartDate().compareTo(dayHour2.getStartDate());
				}
			});
			
			// Creamos la nueva lista
			ArrayList<DayHour> newHourList = new ArrayList<DayHour>();
			
			// Inicializamos si variable que nos indica si ha sido insertado
			Boolean added = false;
			
			// Si eta vacio se inserta directamente
			if(this.hourList.isEmpty()) {
				newHourList.add(newDayHour);
				added = true;
			}
			
			for(DayHour dayHour: this.hourList) {
				if(added) {
					// Si la fecha fin del nuevo tramo es null ya no se inserta ninguno mas
					if(!isNullOrEndContract(newDayHour.getEndDate())) {
						// Si el dia que estamos analizando es null cogemos el endDate del ultimo dia insertado y se lo ponemos con startDate
						if(isNullOrEndContract(dayHour.getEndDate())) {
							DayHour lastDayHour = newHourList.get(newHourList.size()-1);
							Date newStartDate = DateUtils.copyDateOnly(lastDayHour.getEndDate());
							newStartDate = DateUtils.addDays2Date(newStartDate, 1);
							
							dayHour.setStartDate(newStartDate);
							
							newHourList.add(dayHour);
						// Si la fecha fin del nuevo es posterior al analizado, no se añade
						}else if(afterOrEqual(newDayHour.getEndDate(), dayHour.getEndDate())) {
							continue;
						} else if(newDayHour.getEndDate().before(dayHour.getEndDate()) && newDayHour.getEndDate().after(dayHour.getStartDate())) {
							Date newStartDate = DateUtils.copyDateOnly(newDayHour.getEndDate());
							DateUtils.addDays2Date(newStartDate, 1);
							DateUtils.resetTime(newStartDate);
							
							newHourList.add(new DayHour(newStartDate, dayHour.getEndDate(), dayHour.getValue()));	
						} else
							newHourList.add(dayHour);
					}
				} else {
					// Si el elemento de la lista es anterior al insertado
					if(beforeOrEqual(dayHour.getStartDate(), newDayHour.getStartDate())) {
						// Si el elemento de la lista tiene fecha fin null, tendremos que meter el nuevo tramo entre medias
						if(isNullOrEndContract(dayHour.getEndDate())) {
							if(isNullOrEndContract(newDayHour.getEndDate())) {
								if(dayHour.getStartDate().equals(newDayHour.getStartDate())) {
									newHourList.add(newDayHour);
									added = true;
									continue;
								}else {
									Date newEndDate = DateUtils.copyDateOnly(newDayHour.getStartDate());
									newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
									dayHour.setEndDate(newEndDate);
									
									newHourList.add(dayHour);
									newHourList.add(newDayHour);
									added = true;
									continue;
								}
							} else {
								Date oldEndDate = null;
								if(null != dayHour.getEndDate()) {
									oldEndDate = DateUtils.copyDateOnly(dayHour.getEndDate());
									DateUtils.resetTime(oldEndDate);
								}
								
								Date newEndDate = DateUtils.copyDateOnly(newDayHour.getStartDate());
								newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
								dayHour.setEndDate(newEndDate);
								
								newHourList.add(dayHour);
								newHourList.add(newDayHour);
								
								Window.alert("EndDate : " + newDayHour.getEndDate());
								Date newStartDate = DateUtils.copyDateOnly(newDayHour.getEndDate());
								newStartDate = DateUtils.addDays2Date(newStartDate, 1);
								DayHour postNewDayHour = new DayHour(newStartDate, oldEndDate, dayHour.getValue());
								
								newHourList.add(postNewDayHour);
								
								added = true;
								continue;
							}
						// Si el elemento a añadir es el mismo tramo de la lista
						} else if(newDayHour.getStartDate().equals(dayHour.getStartDate()) && dayHour.getEndDate().equals(newDayHour.getEndDate())) {
							newHourList.add(newDayHour);
							added = true;
							continue;
						// Si el elemento a añadir es posterior al tramo de la lista
						} else if(beforeOrEqual(dayHour.getEndDate(), newDayHour.getStartDate()) &&
								!isBetween(dayHour, newDayHour)) {
							newHourList.add(dayHour);
							continue;
						// Si el elemento a añadir esta en un tramo existente
						} else if(afterOrEqual(dayHour.getEndDate(), newDayHour.getStartDate())) {
							DayHour postNewDayHour = null;
							
							if(!isNullOrEndContract(newDayHour.getEndDate()) && newDayHour.getEndDate().before(dayHour.getEndDate())) {
								Window.alert("EndDate 2 : " + newDayHour.getEndDate());
								Date newStartDate = DateUtils.copyDateOnly(newDayHour.getEndDate());
								newStartDate = DateUtils.addDays2Date(newStartDate, 1);
								
								postNewDayHour = new DayHour(newStartDate, dayHour.getEndDate(), dayHour.getValue());
							}
							
							if(!newDayHour.getStartDate().equals(dayHour.getStartDate()) &&
									!dayHour.getEndDate().equals(newDayHour.getEndDate()) &&
									!isBetween(dayHour, newDayHour)) {
									Date newEndDate = DateUtils.copyDateOnly(newDayHour.getStartDate());
									newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
									dayHour.setEndDate(newEndDate);
									
									newHourList.add(dayHour);
								} else if(dayHour.getEndDate().equals(newDayHour.getEndDate())) {
									Date newEndDate = DateUtils.copyDateOnly(newDayHour.getStartDate());
									newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
									dayHour.setEndDate(newEndDate);
									
									newHourList.add(dayHour);
								}
								
								newHourList.add(newDayHour);
								
								if(null != postNewDayHour)
									newHourList.add(postNewDayHour);
								
								added = true;
								continue;
						}
					} else {
						// Si el elemento a añadir es anterior al tramo de la lista
						if(isNullOrEndContract(newDayHour.getEndDate())) {
							newHourList.add(newDayHour);
							added = true;
							continue;
						} else if (!isNullOrEndContract(newDayHour.getEndDate()) && beforeOrEqual(newDayHour.getEndDate(), dayHour.getStartDate())) {
							newHourList.add(newDayHour);
							newHourList.add(dayHour);
							added = true;
							continue;
						} else if (!isNullOrEndContract(newDayHour.getEndDate()) && afterOrEqual(newDayHour.getEndDate(), dayHour.getStartDate()))	{
							Date newStartDate = DateUtils.copyDateOnly(newDayHour.getEndDate());
							DateUtils.resetTime(newStartDate);
							DateUtils.addDays2Date(newStartDate, 1);
							
							dayHour.setStartDate(newStartDate);
							
							newHourList.add(newDayHour);
							newHourList.add(dayHour);
							
							added = true;
							continue;
						} else
							newHourList.add(dayHour);
					}
				}
					
			}
			
			//Si todavía no se ha añadido es por que va al final de la lista y se añade al final de la lista
			if(!added){
				newHourList.add(newDayHour);
			}
			
			// Asignamos la nueva lista a la antigua
			this.hourList = newHourList;	
		}
		
		// -----------------------------------------------------
		// 					GET FIXEDLIST (UPDATE)
		// -----------------------------------------------------
		
		public ArrayList<DayHour> getFixUpdateList() {
			// Creamos la nueva lista
			ArrayList<DayHour> newHourList = new ArrayList<DayHour>();
			
			// Si esta vacio
			if(getHourList().isEmpty())
				return newHourList;
			
			// Si solo tiene un registo en la lista
			if(getHourList().size() == 1) {
				newHourList = getHourList();
				return newHourList;
			}
			
			// Ordenamos la lista
			this.hourList.sort(new Comparator<DayHour>() {
				@Override
				public int compare(DayHour dayHour1, DayHour dayHour2) {
					return dayHour1.getStartDate().compareTo(dayHour2.getStartDate());
				}
			});
			
			
			Date startDate = this.hourList.get(0).getStartDate();
			Date endDate = this.hourList.get(0).getEndDate();
			Double value = this.hourList.get(0).getValue();
			
			DayHour newDayHour = new DayHour(startDate, endDate, value);
			
			for(int i=0; i<getHourList().size()-1; i++) {
				if(newDayHour.getValue() == this.hourList.get(i+1).getValue()) {
					newDayHour.setEndDate(hourList.get(i+1).getEndDate());
					continue;
				} else if(null != newDayHour.getValue() && null != this.hourList.get(i+1).getValue() && Double.compare(newDayHour.getValue(), this.hourList.get(i+1).getValue()) == 0) {
					newDayHour.setEndDate(hourList.get(i+1).getEndDate());
					continue;
				} else {
					newHourList.add(new DayHour(newDayHour.getStartDate(), newDayHour.getEndDate(), newDayHour.getValue()));
					
					startDate = this.hourList.get(i+1).getStartDate();
					endDate = this.hourList.get(i+1).getEndDate();
					value = this.hourList.get(i+1).getValue();
					
					newDayHour = new DayHour(startDate, endDate, value);
				}
			}
			
			// Last iteration
			if(newDayHour.getValue() == this.hourList.get(hourList.size()-1).getValue()) {
				newDayHour.setEndDate(hourList.get(hourList.size()-1).getEndDate());
				newHourList.add(newDayHour);
			} else {
				newHourList.add(new DayHour(newDayHour.getStartDate(), newDayHour.getEndDate(), newDayHour.getValue()));
			}
			
			return newHourList;
			
//			return getStrechUpdateList(newHourList);
			
		}

		private ArrayList<DayHour> getStrechUpdateList(ArrayList<DayHour> newHourList) {
			// Creamos la nueva lista
			ArrayList<DayHour> strechHourList = new ArrayList<DayHour>();
			
			for(DayHour dayHour : newHourList) {
				if(null == dayHour.getEndDate()) {
					strechHourList.add(dayHour);
				} else {
					if(dayHour.getStartDate().getMonth() == dayHour.getEndDate().getMonth()) {
						strechHourList.add(dayHour);
					} else {
						Date iterableDate = DateUtils.copyDateOnly(dayHour.getStartDate());
						Date startDate = DateUtils.copyDateOnly(dayHour.getStartDate());
						Double value = dayHour.getValue();
						
						while(beforeOrEqual(iterableDate, dayHour.getEndDate())) {
							// Ya ha llegado al fin
							if(iterableDate.equals(dayHour.getEndDate())) {
								Date newEndDate = DateUtils.copyDateOnly(iterableDate);
								DateUtils.resetTime(newEndDate);
								strechHourList.add(new DayHour(startDate, newEndDate, value));
								DateUtils.addDays2Date(iterableDate, 1);
								continue;
							}
							
							// Si son el mimso mes avanzamos y seguimos iterando
							if(startDate.getMonth() == iterableDate.getMonth()) {
								DateUtils.addDays2Date(iterableDate, 1);
								continue;
							}
							
							// No son el mismo mes
							
							DateUtils.deleteDays2Date(iterableDate, 1);
							
							Date newEndDate = DateUtils.copyDateOnly(iterableDate);
							DateUtils.resetTime(newEndDate);
							
							strechHourList.add(new DayHour(startDate, newEndDate, value));
							
							DateUtils.addDays2Date(iterableDate, 1);
							startDate = DateUtils.copyDateOnly(iterableDate);
						}
					}
				}
			}
			
			return strechHourList;
		}
		
		// -----------------------------------------------------
		// 						AUX METHODS
		// -----------------------------------------------------
		
		private boolean isBetween(DayHour dayHour, DayHour newDayHour) {
			return afterOrEqual(dayHour.getStartDate(), newDayHour.getStartDate()) &&
					beforeOrEqual(dayHour.getEndDate(), newDayHour.getEndDate());
		}

		// -----------------------------------------------------
		// 					AUX METHODS (DATES)
		// -----------------------------------------------------
		
		private String parseDate(Date date) {
			if(null == date)
				return "null";
			
			String day = StringUtils.leftPad(Integer.toString(date.getDate()), 2, '0');
			String month = StringUtils.leftPad(Integer.toString(date.getMonth() + 1), 2, '0');
			String year = Integer.toString(date.getYear() + 1900);
			
			return day + "/" + month + "/" + year;
		}
		
		private boolean beforeOrEqual(Date date1, Date date2) {
			return date1.before(date2) || date1.equals(date2);
		}
		
		private boolean afterOrEqual(Date date1, Date date2) {
			return date1.after(date2) || date1.equals(date2);
		}
		
		private boolean isNullOrEndContract(Date date) {
			return null == date || date.equals(getContractEndDate());
		}
		
		// -----------------------------------------------------
		// 						TO STRING
		// -----------------------------------------------------
		
		public String toString(DayHour dayAdded) {
			String result = "------ LISTA ------ \n";
			result += "DATE ADDED : Start : " + parseDate(dayAdded.getStartDate()) + " End : " + parseDate(dayAdded.getEndDate()) + " Value : " + dayAdded.getValue() + "\n\n";
			for(DayHour dayHour : getHourList()) {
				result += "Start : " + parseDate(dayHour.getStartDate());
				result += " End : " + parseDate(dayHour.getEndDate());
				result += " Value : " + dayHour.getValue() + "\n";
			}
			return result + "\n";
		}
		
	}
	
	// -----------------------------------------------------
	// 						VARIABLES
	// -----------------------------------------------------
	
	private DayHours dayHours[] = new DayHours[7];
	private Map<Date, Double> mapDaysHour;

	private String contractStartDate;
	private static String contractEndDate;
	
	public CalendarHours() {
		super();
		this.dayHours[0] = new DayHours("HORAS_DOMINGO");
		this.dayHours[1] = new DayHours("HORAS_LUNES");
		this.dayHours[2] = new DayHours("HORAS_MARTES");
		this.dayHours[3] = new DayHours("HORAS_MIERCOLES");
		this.dayHours[4] = new DayHours("HORAS_JUEVES");
		this.dayHours[5] = new DayHours("HORAS_VIERNES");
		this.dayHours[6] = new DayHours("HORAS_SABADO");
	}
	
	public CalendarHours(DayHours[] dayHours, Map<Date, Double> mapDaysHour) {
		this.dayHours = dayHours;
		this.mapDaysHour = mapDaysHour;
	}
	
	public DayHours[] getDayHours() {
		return this.dayHours;
	}
	
	public Date getContractStartDate() {
		return parse(contractStartDate);
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = format(contractStartDate);
	}

	public static Date getContractEndDate() {
		return parse(contractEndDate);
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = format(contractEndDate);
	}
	
	// -----------------------------------------------------
	// 						METHODS INIT
	// -----------------------------------------------------
	
	public void initMapDaysHour() {
		this.mapDaysHour = new HashMap<Date, Double>();
		
		for(int i = 0; i < 7; i++) {
			for(DayHour dayHour : this.dayHours[i].getHourList()) {
				Date iterableDate = DateUtils.copyDateOnly(dayHour.getStartDate());
				DateUtils.resetTime(iterableDate);
				
				// Si es null la fecha fin le pongo el 1 de enero del 2021 para ver como se dibuja
				Date newEndDate = null;
				if(null == dayHour.getEndDate()) {
					Integer nextYear = new Date().getYear() + 1;
					newEndDate = new Date(nextYear, 11, 31);
				} else
					newEndDate = DateUtils.copyDateOnly(dayHour.getEndDate());
				DateUtils.resetTime(newEndDate);
				
				while(iterableDate.before(newEndDate) || iterableDate.equals(newEndDate)) {
					if(iterableDate.getDay() == i) {
						Date startDate = DateUtils.copyDateOnly(iterableDate);
						DateUtils.resetTime(startDate);
						mapDaysHour.put(startDate, dayHour.getValue());
					}
					DateUtils.addDays2Date(iterableDate, 1);
				}
			}
		}
	}
	
	// -----------------------------------------------------
	// 						AUX METHODS
	// -----------------------------------------------------
	
	public boolean isEmpty() {
		boolean isEmpty = true;
		for(int i = 0; i < 7; i++) {
			if(!this.dayHours[i].getHourList().isEmpty()) {
				isEmpty = false;
				break;
			}
		}
		
		return isEmpty;
	}
	
	public Double getHourByDate(Date date) {
		DateUtils.resetTime(date);
		
		return this.mapDaysHour.get(date);
	}
	
	// -----------------------------------------------------
	// 						MAIN
	// -----------------------------------------------------
	
//	public static void main(String[] args) {
//		CalendarHours calendar = new CalendarHours();
//		
//		// 15-03-2020
//		// 31-03-2020
//		Date startDate = new Date(120, 2, 15);
//		Date endDate =  new Date(120, 2, 31);
//		Double value = 8.00;
//		DayHour dayHour = new DayHour(startDate, endDate, value);
//		
//		calendar.getDayHours()[0].addDayHour(dayHour);
//		System.out.println(calendar.getDayHours()[0].toString(dayHour));
//		
//		// 15-03-2020
//		// 25-03-2020
//		startDate = new Date(120, 2, 15);
//		endDate = new Date(120, 2, 25);
//		value = 4.00;
//		dayHour = new DayHour(startDate, endDate, value);
//		
//		calendar.getDayHours()[0].addDayHour(dayHour);
//		System.out.println(calendar.getDayHours()[0].toString(dayHour));
//		
//		// 01-03-2020
//		// 12-03-2020
//		startDate = new Date(120, 2, 1);
//		endDate = new Date(120, 2, 12);
//		value = 2.00;
//		dayHour = new DayHour(startDate, endDate, value);
//		
//		calendar.getDayHours()[0].addDayHour(dayHour);
//		System.out.println(calendar.getDayHours()[0].toString(dayHour));
//		
//		// 10-03-2020
//		// 17-03-2020
//		startDate = new Date(120, 2, 10);
//		endDate = new Date(120, 2, 17);
//		value = 6.00;
//		dayHour = new DayHour(startDate, endDate, value);
//		
//		calendar.getDayHours()[0].addDayHour(dayHour);
//		System.out.println(calendar.getDayHours()[0].toString(dayHour));
//		
//		// 28-03-2020
//		// 04-04-2020
//		startDate = new Date(120, 2, 28);
//		endDate = new Date(120, 3, 4);
//		value = 7.00;
//		dayHour = new DayHour(startDate, endDate, value);
//		
//		calendar.getDayHours()[0].addDayHour(dayHour);
//		System.out.println(calendar.getDayHours()[0].toString(dayHour));
//		
//		// 25-02-2020
//		// 03-03-2020
//		startDate = new Date(120, 1, 25);
//		endDate = new Date(120, 2, 3);
//		value = 5.00;
//		dayHour = new DayHour(startDate, endDate, value);
//		
//		calendar.getDayHours()[0].addDayHour(dayHour);
//		System.out.println(calendar.getDayHours()[0].toString(dayHour));
//		
//		// 25-02-2020
//		// 02-04-2020
//		startDate = new Date(120, 1, 25);
//		endDate = new Date(120, 3, 4);
//		value = null;
//		dayHour = new DayHour(startDate, endDate, value);
//		
//		calendar.getDayHours()[0].addDayHour(dayHour);
//		System.out.println(calendar.getDayHours()[0].toString(dayHour));
//		
//	}
}

