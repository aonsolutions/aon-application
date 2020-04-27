package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.DateUtils;

@SuppressWarnings("serial")
public class CalendarDaysType implements Serializable {
	
	public static interface DayTypeVisitor<T>{
		// Laborable
		T visitWorkingDay(DayType dayType);
		// No laborable
		T visitNoWorkingDay(DayType dayType);
		// Vacaciones
		T visitHolyDay(DayType dayType);
		// Inactividad
		T visitInactivityDay(DayType dayType);
		// Ausencia
		T visitDropDay(DayType dayType);
		// Huelga
		T visitStrikeDay(DayType dayType);
		// ERE, ERE Fza, ERE Fza Exonerado
		T visitEreDay(DayType dayType);
		T visitEreFzaDay(DayType dayType);
		T visitEreFzaExonDay(DayType dayType);
		// IT
		T visitITDay(DayType dayType);
		// Peonadas
		T visitPeonadasDay(DayType dayType);
		//Parcialidad
		T visitPartialityDay(DayType dayType);
		// OTROS
		T visitFreeDay(DayType dayType);
		T visitNoTypeDay(DayType dayType);
	}
	
	public static enum DayType{
		// No laborable
		WORKINGDAY{
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitWorkingDay(this);
			}
		},
		// No laborable
		NOWORKINGDAY{
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitNoWorkingDay(this);
			}
		},
		// Vacaciones
		HOLIDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitHolyDay(this);
			}
		},
		// Inactividad
		INACTIVITY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitInactivityDay(this);
			}
		},
		// Ausencia
		DROPDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitDropDay(this);
			}
		},
		// Huelga
		STRIKEDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitStrikeDay(this);
			}
		},
		// ERE, ERE Fza, ERE Fza Exonerado
		EREDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitEreDay(this);
			}
		},
		EREFZADAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitEreFzaDay(this);
			}
		},
		EREFZAEXONDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitEreFzaExonDay(this);
			}
		},
		// IT
		BAJAIT {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitITDay(this);
			}
		},
		// Peonadas
		PEONADAS{
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitPeonadasDay(this);
			}
		},
		// Parcialidad
		PARTIALITY{
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitPartialityDay(this);
			}
		},
		// OTROS
		FREEDAY {
			@Override
			public <T> T visit(DayTypeVisitor<T> visitor) {
				return visitor.visitFreeDay(this);
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
	
	@SuppressWarnings("hiding")
	public static class DayTypeInfo<DayType, String> implements Serializable{
		public DayType fst;
		public String snd;
	 
		public DayTypeInfo() {
			super();
		}
		
		public DayTypeInfo(DayType fst, String snd) {
			this.fst = fst;
			this.snd = snd;
		}
	 
		public DayType getFirst() { return fst; }
		public String getSecond() { return snd; }
	 
		public void setFirst(DayType v) { fst = v; }
		public void setSecond(String v) { snd = v; }
	 
		private static boolean equals(Object x, Object y) {
			return (x == null && y == null) || (x != null && x.equals(y));
		}
	 
		public boolean equals(Object other) {
			return
					other instanceof DayTypeInfo &&
					equals(fst, ((DayTypeInfo)other).fst) &&
					equals(snd, ((DayTypeInfo)other).snd);
		}
	}

	public static class CalendarDayType implements Serializable{

		// -----------------------------------------------------
		// 						VARIABLES
		// -----------------------------------------------------
		
		private Date startDate;
		private Date endDate;
		private DayType dayType;
		private String expession;
		
		public CalendarDayType() {
			super();
		}

		public CalendarDayType(Date startDate, Date endDate, DayType dayType, String expession) {
			super();
			this.startDate = startDate;
			this.endDate = endDate;
			this.dayType = dayType;
			this.expession = expession;
		}

		public Date getStartDate() {
			return startDate;
		}

		public CalendarDayType setStartDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}

		public Date getEndDate() {
			return endDate;
		}

		public CalendarDayType setEndDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}

		public DayType getDayType() {
			return dayType;
		}

		public CalendarDayType setDayType(DayType dayType) {
			this.dayType = dayType;
			return this;
		}

		public String getExpession() {
			return expession;
		}

		public CalendarDayType setExpession(String expession) {
			this.expession = expession;
			return this;
		}
		
	}

	// -----------------------------------------------------
	// 						VARIABLES
	// -----------------------------------------------------
	
	
	private ArrayList<CalendarDayType> dayTypeList;
	private Map<Date, DayTypeInfo<DayType, String>> mapDaysDayType;
	private Date contractStartDate;
	private Date contractEndDate;
	
	public CalendarDaysType() {
		super();
		dayTypeList = new ArrayList<CalendarDaysType.CalendarDayType>();
	}
	
	public CalendarDaysType(ArrayList<CalendarDayType> dayTypeList, Map<Date, DayTypeInfo<DayType, String>> mapDaysDayType) {
		this.dayTypeList = dayTypeList;
		this.mapDaysDayType = mapDaysDayType;
	}

	public ArrayList<CalendarDayType> getDayTypeList() {
		return dayTypeList;
	}
	
	public Date getContractStartDate() {
		return contractStartDate;
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	public Date getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}
	
	// -----------------------------------------------------
	// 						METHODS INIT
	// -----------------------------------------------------

	public void initMapDaysDayType(){
		mapDaysDayType = new HashMap<Date, DayTypeInfo<DayType,String>>();
		for(CalendarDayType calendarDayType : dayTypeList) {
			Date iterableDate = DateUtils.copyDateOnly(calendarDayType.getStartDate());
			DateUtils.resetTime(iterableDate);
			
			// Si es null la fecha fin le pongo el 1 de enero del 2021 para ver como se dibuja
			Date newEndDate = null;
			if(null == calendarDayType.getEndDate()) {
				Integer nextYear = new Date().getYear() + 1;
				newEndDate = new Date(nextYear, 11, 31);
			} else
				newEndDate = DateUtils.copyDateOnly(calendarDayType.getEndDate());
			DateUtils.resetTime(newEndDate);
			
			while(iterableDate.before(newEndDate) || iterableDate.equals(newEndDate)) {
				Date startDate = DateUtils.copyDateOnly(iterableDate);
				DateUtils.resetTime(startDate);
				
				mapDaysDayType.put(startDate, new DayTypeInfo<DayType, String>(calendarDayType.getDayType(), calendarDayType.getExpession()));
				
				DateUtils.addDays2Date(iterableDate, 1);
			}
		}
	}
	
	// -----------------------------------------------------
	// 						ADD DAY TYPE
	// -----------------------------------------------------
	
	public void addDayType(CalendarDayType newCalendarDayType) {
		// Ordenamos los tramos existentes
		this.dayTypeList.sort(new Comparator<CalendarDayType>() {
			@Override
			public int compare(CalendarDayType calendarDayType1, CalendarDayType calendarDayType2) {
				return calendarDayType1.getStartDate().compareTo(calendarDayType2.getStartDate());
			}
		});
		
		// Creamos la nueva lista
		ArrayList<CalendarDayType> newDayTypeList = new ArrayList<CalendarDayType>();
		
		// Inicializamos si variable que nos indica si ha sido insertado
		Boolean added = false;
		
		// Si eta vacio se inserta directamente
		if(this.dayTypeList.isEmpty()) {
			newDayTypeList.add(newCalendarDayType);
			added = true;
		}
		
		for(CalendarDayType calendarDayType: this.dayTypeList) {
			if(added) {
				// Si la fecha fin del nuevo tramo es null ya no se inserta ninguno mas
				if(!isNullOrEndContract(newCalendarDayType.getEndDate())) {
					// Si el dia que estamos analizando es null cogemos el endDate del ultimo dia insertado y se lo ponemos con startDate
					if(isNullOrEndContract(calendarDayType.getEndDate())) {
						CalendarDayType lastCalendarDayType = newDayTypeList.get(newDayTypeList.size()-1);
						Date newStartDate = DateUtils.copyDateOnly(lastCalendarDayType.getEndDate());
						newStartDate = DateUtils.addDays2Date(newStartDate, 1);
						
						calendarDayType.setStartDate(newStartDate);
						
						newDayTypeList.add(calendarDayType);
					// Si la fecha fin del nuevo es posterior al analizado, no se añade
					}else if(afterOrEqual(newCalendarDayType.getEndDate(), calendarDayType.getEndDate())) {
						continue;
					} else if(newCalendarDayType.getEndDate().before(calendarDayType.getEndDate()) && newCalendarDayType.getEndDate().after(calendarDayType.getStartDate())) {
						Date newStartDate = DateUtils.copyDateOnly(newCalendarDayType.getEndDate());
						DateUtils.addDays2Date(newStartDate, 1);
						DateUtils.resetTime(newStartDate);
						
						newDayTypeList.add(new CalendarDayType(newStartDate, calendarDayType.getEndDate(), calendarDayType.getDayType(), calendarDayType.getExpession()));	
					} else
						newDayTypeList.add(calendarDayType);
				}
			} else {
				// Si el elemento de la lista es anterior al insertado
				if(beforeOrEqual(calendarDayType.getStartDate(), newCalendarDayType.getStartDate())) {
					// Si el elemento de la lista tiene fecha fin null, tendremos que meter el nuevo tramo entre medias
					if(isNullOrEndContract(calendarDayType.getEndDate())) {
						if(isNullOrEndContract(newCalendarDayType.getEndDate())) {
							Date newEndDate = DateUtils.copyDateOnly(newCalendarDayType.getStartDate());
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							calendarDayType.setEndDate(newEndDate);
							
							newDayTypeList.add(calendarDayType);
							newDayTypeList.add(newCalendarDayType);
							added = true;
							continue;
						} else {
							Date oldEndDate = null;
							if(null != calendarDayType.getEndDate()) {
								oldEndDate = DateUtils.copyDateOnly(calendarDayType.getEndDate());
								DateUtils.resetTime(oldEndDate);
							}
							
							Date newEndDate = DateUtils.copyDateOnly(newCalendarDayType.getStartDate());
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							calendarDayType.setEndDate(newEndDate);
							
							newDayTypeList.add(calendarDayType);
							newDayTypeList.add(newCalendarDayType);
							
							Date newStartDate = DateUtils.copyDateOnly(newCalendarDayType.getEndDate());
							newStartDate = DateUtils.addDays2Date(newStartDate, 1);
							CalendarDayType postNewCalendarDayType = new CalendarDayType(newStartDate, oldEndDate, calendarDayType.getDayType(), calendarDayType.getExpession());
							
							newDayTypeList.add(postNewCalendarDayType);
							
							added = true;
							continue;
						}
					// Si el elemento a añadir es el mismo tramo de la lista
					} else if(calendarDayType.getEndDate().equals(newCalendarDayType.getEndDate()) &&
							calendarDayType.getStartDate().equals(newCalendarDayType.getStartDate())) {
						newDayTypeList.add(newCalendarDayType);
						added = true;
						continue; 
					// Si el elemento a añadir es posterior al tramo de la lista
					} else if(calendarDayType.getEndDate().before(newCalendarDayType.getStartDate()) &&
							!isBetween(calendarDayType, newCalendarDayType)) {
						newDayTypeList.add(calendarDayType);
						continue;
					// Si el elemento a añadir esta en un tramo existente
					} else if(afterOrEqual(calendarDayType.getEndDate(), newCalendarDayType.getStartDate())) {
						CalendarDayType postNewCalendarDayType = null;
						
						if(!isNullOrEndContract(newCalendarDayType.getEndDate()) && newCalendarDayType.getEndDate().before(calendarDayType.getEndDate())) {
							Date newStartDate = DateUtils.copyDateOnly(newCalendarDayType.getEndDate());
							newStartDate = DateUtils.addDays2Date(newStartDate, 1);
							
							postNewCalendarDayType = new CalendarDayType(newStartDate, calendarDayType.getEndDate(), calendarDayType.getDayType(), calendarDayType.getExpession());
						}
						
						if(!newCalendarDayType.getStartDate().equals(calendarDayType.getStartDate()) &&
							!newCalendarDayType.getEndDate().equals(calendarDayType.getEndDate()) &&
							!isBetween(calendarDayType, newCalendarDayType)) {
							Date newEndDate = DateUtils.copyDateOnly(newCalendarDayType.getStartDate());
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							calendarDayType.setEndDate(newEndDate);
							
							newDayTypeList.add(calendarDayType);
						} else if(newCalendarDayType.getEndDate().equals(calendarDayType.getEndDate())) {
							Date newEndDate = DateUtils.copyDateOnly(newCalendarDayType.getStartDate());
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							calendarDayType.setEndDate(newEndDate);
							
							newDayTypeList.add(calendarDayType);
						}
						
						newDayTypeList.add(newCalendarDayType);
						
						if(null != postNewCalendarDayType)
							newDayTypeList.add(postNewCalendarDayType);
						
						added = true;
						continue;
					}
				} else {
					// Si el elemento a añadir es anterior al tramo de la lista
					if(isNullOrEndContract(newCalendarDayType.getEndDate())) {
						newDayTypeList.add(newCalendarDayType);
						added = true;
						continue;
					} else if (beforeOrEqual(newCalendarDayType.getEndDate(), calendarDayType.getStartDate())) {
						if(newCalendarDayType.getEndDate().equals(calendarDayType.getStartDate())) {
							Date newStartDate = DateUtils.copyDateOnly(calendarDayType.getStartDate());
							DateUtils.addDays2Date(newStartDate, 1);
							calendarDayType.setStartDate(newStartDate);
							
							newDayTypeList.add(newCalendarDayType);
							newDayTypeList.add(calendarDayType);
						} else {
							newDayTypeList.add(newCalendarDayType);
							newDayTypeList.add(calendarDayType);
						}
						added = true;
						continue;
					} else if (afterOrEqual(newCalendarDayType.getEndDate(), calendarDayType.getStartDate()))	{
						Date newStartDate = DateUtils.copyDateOnly(newCalendarDayType.getEndDate());
						DateUtils.resetTime(newStartDate);
						DateUtils.addDays2Date(newStartDate, 1);
						
						calendarDayType.setStartDate(newStartDate);
						
						newDayTypeList.add(newCalendarDayType);
						newDayTypeList.add(calendarDayType);
						
						added = true;
						continue;
					} else
						newDayTypeList.add(calendarDayType);
				}
			}
				
		}
		
		//Si todavía no se ha añadido se añade al final de la lista
		if(!added){
			newDayTypeList.add(newCalendarDayType);
		}
		
		// Asignamos la nueva lista a la antigua
		this.dayTypeList = newDayTypeList;
	}
	
	// -----------------------------------------------------
	// 					GET FIXEDLIST (UPDATE)
	// -----------------------------------------------------

	public  ArrayList<CalendarDayType> getFixUpdateList() {
		// Creamos la nueva lista
		ArrayList<CalendarDayType> newDayTypeList = new ArrayList<CalendarDayType>();
		
		// Si esta vacio
		if(getDayTypeList().isEmpty())
			return newDayTypeList;
		
		// Si solo tiene un registo en la lista
		if(getDayTypeList().size() == 1) {
			if(validDayType(dayTypeList.get(0).getDayType()))
				newDayTypeList = getDayTypeList();
			return getStrechUpdateList(newDayTypeList);
		}
		
		// Ordenamos la lista
		dayTypeList.sort(new Comparator<CalendarDayType>() {
			@Override
			public int compare(CalendarDayType dayHour1, CalendarDayType dayHour2) {
				return dayHour1.getStartDate().compareTo(dayHour2.getStartDate());
			}
		});
		
		
		Date startDate = dayTypeList.get(0).getStartDate();
		Date endDate = dayTypeList.get(0).getEndDate();
		DayType dayType = dayTypeList.get(0).getDayType();
		String expression = dayTypeList.get(0).getExpession();
		
		CalendarDayType newCalendarDayType = new CalendarDayType(startDate, endDate, dayType, expression);
		
		for(int i=1; i<getDayTypeList().size()-1; i++) {
			if(!validDayType(dayTypeList.get(i).getDayType()))
				continue;
			
			if(newCalendarDayType.getDayType() == dayTypeList.get(i).getDayType() && isNextDay(newCalendarDayType.getEndDate(), dayTypeList.get(i).getStartDate())) {
				if(isCoefficientDayType(newCalendarDayType.getDayType())) {
					if(Double.compare(Double.parseDouble(newCalendarDayType.getExpession()), Double.parseDouble(dayTypeList.get(i).getExpession())) == 0) {
						newCalendarDayType.setEndDate(dayTypeList.get(i).getEndDate());
						continue;
					} else {
						newDayTypeList.add(new CalendarDayType(newCalendarDayType.getStartDate(), newCalendarDayType.getEndDate(), newCalendarDayType.getDayType(), newCalendarDayType.getExpession()));
						
						startDate = dayTypeList.get(i).getStartDate();
						endDate = dayTypeList.get(i).getEndDate();
						dayType = dayTypeList.get(i).getDayType();
						expression = dayTypeList.get(i).getExpession();
						
						newCalendarDayType = new CalendarDayType(startDate, endDate, dayType, expression);
						continue;
					}
				} else if(isDaysBetweenExpression(newCalendarDayType.getDayType())) {
					newCalendarDayType.setEndDate(dayTypeList.get(i).getEndDate());
					continue;
				} else if(newCalendarDayType.getExpession() == dayTypeList.get(i+1).getExpession()) {
					newCalendarDayType.setEndDate(dayTypeList.get(i).getEndDate());
					continue;	
				} else {
					newDayTypeList.add(new CalendarDayType(newCalendarDayType.getStartDate(), newCalendarDayType.getEndDate(), newCalendarDayType.getDayType(), newCalendarDayType.getExpession()));
					
					startDate = dayTypeList.get(i).getStartDate();
					endDate = dayTypeList.get(i).getEndDate();
					dayType = dayTypeList.get(i).getDayType();
					expression = dayTypeList.get(i).getExpession();
					
					newCalendarDayType = new CalendarDayType(startDate, endDate, dayType, expression);
				}
			} else {
				if(validDayType(newCalendarDayType.getDayType()))
					newDayTypeList.add(new CalendarDayType(newCalendarDayType.getStartDate(), newCalendarDayType.getEndDate(), newCalendarDayType.getDayType(), newCalendarDayType.getExpession()));
				
				startDate = dayTypeList.get(i).getStartDate();
				endDate = dayTypeList.get(i).getEndDate();
				dayType = dayTypeList.get(i).getDayType();
				expression = dayTypeList.get(i).getExpession();
				
				newCalendarDayType = new CalendarDayType(startDate, endDate, dayType, expression);
			}
		}
		
		// Last iteration
		if(newCalendarDayType.getDayType() == dayTypeList.get(dayTypeList.size()-1).getDayType() && isNextDay(newCalendarDayType.getEndDate(), dayTypeList.get(dayTypeList.size()-1).getStartDate())) {
			if(newCalendarDayType.getExpession() == dayTypeList.get(dayTypeList.size()-1).getExpession()) {
				newCalendarDayType.setEndDate(dayTypeList.get(dayTypeList.size()-1).getEndDate());
				if(validDayType(newCalendarDayType.getDayType()))
					newDayTypeList.add(newCalendarDayType);
			}
		} else {
			if(validDayType(newCalendarDayType.getDayType()))
				newDayTypeList.add(new CalendarDayType(newCalendarDayType.getStartDate(), newCalendarDayType.getEndDate(), newCalendarDayType.getDayType(), newCalendarDayType.getExpession()));
			
			startDate = dayTypeList.get(dayTypeList.size()-1).getStartDate();
			endDate = dayTypeList.get(dayTypeList.size()-1).getEndDate();
			dayType = dayTypeList.get(dayTypeList.size()-1).getDayType();
			expression = dayTypeList.get(dayTypeList.size()-1).getExpession();
			
			if(!startDate.equals(newCalendarDayType.getStartDate()) && !endDate.equals(newCalendarDayType.getEndDate())
					&& validDayType(dayType))
				newDayTypeList.add(new CalendarDayType(startDate, endDate, dayType, expression));
		}
		
		return getStrechUpdateList(newDayTypeList);
		
	}

	private  ArrayList<CalendarDayType> getStrechUpdateList(ArrayList<CalendarDayType> newCalendarDayTypeList) {
		// Creamos la nueva lista
		ArrayList<CalendarDayType> strechCalendarDayTypeList = new ArrayList<CalendarDayType>();
		
		for(CalendarDayType calendarDayType : newCalendarDayTypeList) {
			if(null == calendarDayType.getEndDate()) {
				strechCalendarDayTypeList.add(calendarDayType);
			} else if(DayType.PARTIALITY == calendarDayType.getDayType()) {
				strechCalendarDayTypeList.add(calendarDayType);
			} else {
				if(calendarDayType.getStartDate().getMonth() == calendarDayType.getEndDate().getMonth()) {
					if(isDaysBetweenExpression(calendarDayType.getDayType())) {
						String expression = Integer.toString(DateUtils.getDaysBetween(calendarDayType.getStartDate(), calendarDayType.getEndDate()) + 1);
						calendarDayType.setExpession(expression);
					}
					strechCalendarDayTypeList.add(calendarDayType);
				} else {
					Date iterableDate = DateUtils.copyDateOnly(calendarDayType.getStartDate());
					Date startDate = DateUtils.copyDateOnly(calendarDayType.getStartDate());
					DayType dayType = calendarDayType.getDayType();
					String expression = calendarDayType.getExpession();
					
					while(beforeOrEqual(iterableDate, calendarDayType.getEndDate())) {
						// Ya ha llegado al fin
						if(iterableDate.equals(calendarDayType.getEndDate())) {
							if(isDaysBetweenExpression(dayType))
								expression = Integer.toString(DateUtils.getDaysBetween(startDate, iterableDate) + 1);
							
							Date newEndDate = DateUtils.copyDateOnly(iterableDate);
							DateUtils.resetTime(newEndDate);
							strechCalendarDayTypeList.add(new CalendarDayType(startDate, newEndDate, dayType, expression));
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
						
						if(isDaysBetweenExpression(dayType))
							expression = Integer.toString(DateUtils.getDaysBetween(startDate, newEndDate) + 1);
						
						strechCalendarDayTypeList.add(new CalendarDayType(startDate, newEndDate, dayType, expression));
						
						DateUtils.addDays2Date(iterableDate, 1);
						startDate = DateUtils.copyDateOnly(iterableDate);
					}
				}
			}
		}
		
		return strechCalendarDayTypeList;
	}
	
	// -----------------------------------------------------
	// 						AUX METHODS
	// -----------------------------------------------------
	
	private boolean isBetween(CalendarDayType calendarDayType, CalendarDayType newCalendarDayType) {
		return afterOrEqual(calendarDayType.getStartDate(), newCalendarDayType.getStartDate()) &&
				beforeOrEqual(calendarDayType.getEndDate(), newCalendarDayType.getEndDate());
	}
	
	private boolean isNextDay(Date endDate, Date startDate) {
		if(null == endDate)
			return false;
		
		Date compareDate = DateUtils.copyDateOnly(endDate);
		DateUtils.addDays2Date(compareDate, 1);
		DateUtils.resetTime(compareDate);
		
		return compareDate.equals(startDate);
	}

	private boolean validDayType(DayType dayType) {
		ArrayList<DayType> validDayTypes = new ArrayList<DayType>();
		validDayTypes.add(DayType.NOWORKINGDAY);
		validDayTypes.add(DayType.PEONADAS);
		validDayTypes.add(DayType.HOLIDAY);
		validDayTypes.add(DayType.EREDAY);
		validDayTypes.add(DayType.EREFZADAY);
		validDayTypes.add(DayType.EREFZAEXONDAY);
		validDayTypes.add(DayType.INACTIVITY);
		validDayTypes.add(DayType.STRIKEDAY);
		validDayTypes.add(DayType.DROPDAY);
		validDayTypes.add(DayType.WORKINGDAY);
		validDayTypes.add(DayType.PARTIALITY);
		
		return validDayTypes.contains(dayType);
	}

	private boolean isCoefficientDayType(DayType dayType) {
		ArrayList<DayType> validDayTypes = new ArrayList<DayType>();
		validDayTypes.add(DayType.EREDAY);
		validDayTypes.add(DayType.EREFZADAY);
		validDayTypes.add(DayType.EREFZAEXONDAY);
		validDayTypes.add(DayType.STRIKEDAY);
		validDayTypes.add(DayType.DROPDAY);
		validDayTypes.add(DayType.PARTIALITY);
		
		return validDayTypes.contains(dayType);
	}

	private boolean isDaysBetweenExpression(DayType dayType) {
		ArrayList<DayType> validDayTypes = new ArrayList<DayType>();
		validDayTypes.add(DayType.NOWORKINGDAY);
		validDayTypes.add(DayType.PEONADAS);
		validDayTypes.add(DayType.HOLIDAY);
		validDayTypes.add(DayType.WORKINGDAY);
		
		return validDayTypes.contains(dayType);
	}
	
	// -----------------------------------------------------
	// 					GET SPECIAL METHODS
	// -----------------------------------------------------

	
	public DayType getDayTypeByDate(Date date) {
		DateUtils.resetTime(date);
		
		DayTypeInfo<DayType, String> dayTypeInfo = mapDaysDayType.get(date);
		return null == dayTypeInfo ? null : dayTypeInfo.getFirst();
	}
	
	public String getExpressionByDate(Date date) {
		DateUtils.resetTime(date);
		
		DayTypeInfo<DayType, String> dayTypeInfo = mapDaysDayType.get(date);
		return null == dayTypeInfo ? null : dayTypeInfo.getSecond();
	}
	
	// -----------------------------------------------------
	// 							DATES
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

	public String toString(CalendarDayType dayAdded) {
		String result = "------ LISTA ------ \n";
		result += "DATE ADDED : Start : " + parseDate(dayAdded.getStartDate()) + " End : " + parseDate(dayAdded.getEndDate()) + " DayType : " + dayAdded.getDayType() + "\n\n";
		for(CalendarDayType calendarDayType : dayTypeList) {
			result += "Start : " + parseDate(calendarDayType.getStartDate());
			result += " End : " + parseDate(calendarDayType.getEndDate());
			result += " DayType : " + calendarDayType.getDayType() + "\n";
		}
		return result + "\n";
	}
	
	public String toString() {
		String result = "------ LISTA ------ \n";
		
		for(CalendarDayType calendarDayType : getDayTypeList()) {
			result += "Start : " + parseDate(calendarDayType.getStartDate());
			result += " End : " + parseDate(calendarDayType.getEndDate());
			result += " DayType : " + calendarDayType.getDayType() + "\n";
		}
		return result + "\n";
	}
	
	// -----------------------------------------------------
	// 						MAIN
	// -----------------------------------------------------


//	public static void main(String[] args) {
//		CalendarDaysType calendarDaysType = new CalendarDaysType();
		
		// ------------------------- Partiality two period not same expression
		
//		// 01-03-2020
//		// 15-03-2020
//		Date startDate = new Date(120, 2, 1);
//		Date endDate =  new Date(120, 2, 15);
//		DayType dayType = DayType.INACTIVITY;
//		CalendarDayType calendarDayType = new CalendarDayType(startDate, endDate, dayType, "Empleo y sueldo");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
//		
//		// 10-03-2020
//		// 31-03-2020
//		startDate = new Date(120, 2, 10);
//		endDate = new Date(120, 2, 31);
//		dayType = DayType.INACTIVITY;
//		calendarDayType = new CalendarDayType(startDate, endDate, dayType, "Empleo y sueldo");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
		
		// ------------------------- Partiality two period not same expression
		
//		// 01-03-2020
//		// 15-03-2020
//		Date startDate = new Date(120, 2, 1);
//		Date endDate =  new Date(120, 2, 15);
//		DayType dayType = DayType.INACTIVITY;
//		CalendarDayType calendarDayType = new CalendarDayType(startDate, endDate, dayType, "Suspension");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
//		
//		// 10-03-2020
//		// 31-03-2020
//		startDate = new Date(120, 2, 10);
//		endDate = new Date(120, 2, 31);
//		dayType = DayType.INACTIVITY;
//		calendarDayType = new CalendarDayType(startDate, endDate, dayType, "Empleo y sueldo");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
		
		// ------------------------- Override complete period
		
//		// 10-03-2020
//		// 31-03-2020
//		Date startDate = new Date(120, 2, 10);
//		Date endDate =  new Date(120, 2, 31);
//		DayType dayType = DayType.HOLIDAY;
//		CalendarDayType calendarDayType = new CalendarDayType(startDate, endDate, dayType, "");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
//		
//		// 10-03-2020
//		// 31-03-2020
//		startDate = new Date(120, 2, 10);
//		endDate = new Date(120, 2, 31);
//		dayType = DayType.NOWORKINGDAY;
//		calendarDayType = new CalendarDayType(startDate, endDate, dayType, "");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
		
		// ------------------------- Override partial period
		
//		// 01-03-2020
//		// 31-03-2020
//		Date startDate = new Date(120, 2, 1);
//		Date endDate =  new Date(120, 2, 31);
//		DayType dayType = DayType.HOLIDAY;
//		CalendarDayType calendarDayType = new CalendarDayType(startDate, endDate, dayType, "");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
//		
//		// 20-02-2020
//		// 10-03-2020
//		startDate = new Date(120, 1, 20);
//		endDate = new Date(120, 2, 10);
//		dayType = DayType.NOWORKINGDAY;
//		calendarDayType = new CalendarDayType(startDate, endDate, dayType, "");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
//		
//		// 20-03-2020
//		// 10-04-2020
//		startDate = new Date(120, 2, 20);
//		endDate = new Date(120, 3, 10);
//		dayType = DayType.DROPDAY;
//		calendarDayType = new CalendarDayType(startDate, endDate, dayType, "");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
		
		// ------------------------- Between period
		
//		// 01-03-2020
//		// 31-03-2020
//		Date startDate = new Date(120, 2, 1);
//		Date endDate =  new Date(120, 2, 31);
//		DayType dayType = DayType.HOLIDAY;
//		CalendarDayType calendarDayType = new CalendarDayType(startDate, endDate, dayType, "");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
//		
//		// 10-03-2020
//		// 20-03-2020
//		startDate = new Date(120, 2, 10);
//		endDate = new Date(120, 2, 20);
//		dayType = DayType.NOWORKINGDAY;
//		calendarDayType = new CalendarDayType(startDate, endDate, dayType, "");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
		
		// ------------------------- Same startDate and endDate
		
//		// 14-03-2020
//		// 31-03-2020
//		Date startDate = new Date(120, 2, 14);
//		Date endDate =  new Date(120, 2, 31);
//		DayType dayType = DayType.HOLIDAY;
//		CalendarDayType calendarDayType = new CalendarDayType(startDate, endDate, dayType, "");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
//		
//		// 01-03-2020
//		// 14-03-2020
//		startDate = new Date(120, 2, 1);
//		endDate = new Date(120, 2, 14);
//		dayType = DayType.EREFZAEXONDAY;
//		calendarDayType = new CalendarDayType(startDate, endDate, dayType, "1.0");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
//		
//		// 31-03-2020
//		// 10-04-2020
//		startDate = new Date(120, 2, 31);
//		endDate = new Date(120, 3, 10);
//		dayType = DayType.DROPDAY;
//		calendarDayType = new CalendarDayType(startDate, endDate, dayType, "1.0");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
		
		// ------------------------- Same dayType, same value
		
//		// 14-03-2020
//		// 31-03-2020
//		Date startDate = new Date(120, 2, 14);
//		Date endDate =  new Date(120, 2, 31);
//		DayType dayType = DayType.EREFZAEXONDAY;
//		CalendarDayType calendarDayType = new CalendarDayType(startDate, endDate, dayType, "1.0");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
//		
//		// 01-04-2020
//		// 20-04-2020
//		startDate = new Date(120, 3, 1);
//		endDate = new Date(120, 3, 20);
//		dayType = DayType.EREFZAEXONDAY;
//		calendarDayType = new CalendarDayType(startDate, endDate, dayType, "1.0");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
//		
//		// 01-04-2020
//		// 30-04-2020
//		startDate = new Date(120, 3, 1);
//		endDate = new Date(120, 3, 30);
//		dayType = DayType.EREFZAEXONDAY;
//		calendarDayType = new CalendarDayType(startDate, endDate, dayType, "1.0");
//		calendarDaysType.addDayType(calendarDayType);
//		
//		System.out.println(toString(calendarDayType));
		
		
		// ------------------------------------------------------------------------
		//								GET FIXED LIST
		// ------------------------------------------------------------------------
		
//		ArrayList<CalendarDayType> fixedList = getFixUpdateList();
//		
//		String result = "------ FIXED LIST ------ \n";
//		for(CalendarDayType calenDayType : fixedList) {
//			result += "Start : " + parseDate(calenDayType.getStartDate());
//			result += " End : " + parseDate(calenDayType.getEndDate());
//			result += " DayType : " + calenDayType.getDayType() + "\n";
//		}
//		System.out.println(result + "\n");
//
//	}
}
