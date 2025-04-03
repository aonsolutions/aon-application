package com.esferalia.aon.gwt.payroll.server.contract;

import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.in.payroll.excel.contract.AnualDaysEntryExcel;
import com.esferalia.aon.in.payroll.excel.contract.ContractType;
import com.esferalia.aon.in.payroll.excel.contract.MonthlyDaysEntryExcel;
import com.esferalia.aon.in.payroll.excel.contract.WorkplaceDaysEntryExcel;
import com.esferalia.aon.in.payroll.excel.contract.WorkplaceMonthlyDaysEntryExcel;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InformeMensualService {

	private final DSLContext dsl;
	private final Integer domainId;
	private final String[] MESES = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };

	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public InformeMensualService(DSLContext dsl, Integer domainId) {
		this.dsl = dsl;
		this.domainId = domainId;
	}

	// Método principal que genera el informe de centros de trabajo y contratos para
	// el rango indicado
	public List<WorkplaceDaysEntryExcel> generarInformeMensualCTs(Date fechaInicio, Date fechaFin) {
		List<WorkplaceDaysEntryExcel> informesCT = new ArrayList<>();

		LocalDate startDate = fechaInicio.toLocalDate();
		LocalDate endDate = fechaFin.toLocalDate();

		// Se consultan centros de trabajo activos que tengan contratos en el rango
		Result<Record> workplaces = dsl.select().from(WORKPLACE).join(CONTRACT).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
				.where(WORKPLACE.DOMAIN.eq(domainId)).and(WORKPLACE.ACTIVE.eq((byte) 1))
				.and(CONTRACT.START_DATE.le(Date.valueOf(endDate)))
				.and(CONTRACT.END_DATE.ge(Date.valueOf(startDate)).or(CONTRACT.END_DATE.isNull())).groupBy(WORKPLACE.ID)
				.fetch();

		for (Record workplace : workplaces) {
			Integer workplaceId = workplace.get(WORKPLACE.ID);
			Integer calendarId = obtenerCalendarIdCT(workplaceId);

			List<WorkplaceMonthlyDaysEntryExcel> mesesCT = new ArrayList<>();
			// Iterar mes a mes en el rango global
			for (LocalDate date = startDate.withDayOfMonth(1); !date.isAfter(endDate); date = date.plusMonths(1)) {
				mesesCT.add(obtenerDatosMesCT(workplaceId, calendarId, date, startDate, endDate));
			}

			List<AnualDaysEntryExcel> informesContratos = new ArrayList<>();
			// Para cada centro, obtener los contratos activos en el rango
			for (Integer contratoId : obtenerContratosActivosCT(startDate, endDate, workplaceId)) {
				informesContratos.add(obtenerInformeMensualContrato(contratoId, startDate, endDate));
			}

			informesCT.add(new WorkplaceDaysEntryExcel().setDescription(workplace.get(WORKPLACE.DESCRIPTION))
					.setMeses(mesesCT).setContracts(informesContratos));
		}
		return informesCT;
	}

	// Obtiene los datos mensuales (CT) para un mes concreto, ajustando los límites
	// al rango global
	private WorkplaceMonthlyDaysEntryExcel obtenerDatosMesCT(Integer workplaceId, Integer calendarId, LocalDate date,
			LocalDate globalStart, LocalDate globalEnd) {
		WorkplaceMonthlyDaysEntryExcel mesDTO = new WorkplaceMonthlyDaysEntryExcel().setMes(date.getMonthValue()).setYear(date.getYear());

		YearMonth yearMonth = YearMonth.of(date.getYear(), date.getMonth());
		LocalDate firstDay = yearMonth.atDay(1);
		LocalDate lastDay = yearMonth.atEndOfMonth();

		if (firstDay.isBefore(globalStart)) {
			firstDay = globalStart;
		}
		if (lastDay.isAfter(globalEnd)) {
			lastDay = globalEnd;
		}

		int diasMes = (int) (lastDay.toEpochDay() - firstDay.toEpochDay() + 1);

		mesDTO.setDiasMes(diasMes);
		mesDTO.setDiasFestivos(contarDiasFestivos(calendarId, firstDay, lastDay));
		mesDTO.setDiasFinDeSemana(contarDiasFinDeSemana(calendarId, firstDay, lastDay));
		mesDTO.setDiasLaborables(diasMes - mesDTO.getDiasFestivos() - mesDTO.getDiasFinDeSemana());
		mesDTO.setHorasJornada(mesDTO.getDiasLaborables() * 8); // De momento por 8h (JC)

		return mesDTO;
	}

	// Cuenta días festivos en el intervalo usando la jerarquía de holidays del
	// calendario
	private int contarDiasFestivos(Integer calendarId, LocalDate firstDay, LocalDate lastDay) {
		if (calendarId == null)
			return 0;
		Integer holidayId = obtenerHolidayIdCT(calendarId);
		Set<Integer> holidayIds = obtenerJerarquiaHolidays(holidayId);
		return dsl.selectCount().from(HOLIDAY_DETAIL).where(HOLIDAY_DETAIL.HOLIDAY.in(holidayIds))
				.and(HOLIDAY_DETAIL.DATE.between(Date.valueOf(firstDay), Date.valueOf(lastDay)))
				.fetchOneInto(Integer.class);
	}

	// Cuenta los días en el intervalo que se consideran fines de semana según la
	// configuración del calendario
	private int contarDiasFinDeSemana(Integer calendarId, LocalDate firstDay, LocalDate lastDay) {
		Record7<Byte, Byte, Byte, Byte, Byte, Byte, Byte> calendario = obtenerCalendario(calendarId);
		if (calendario == null)
			return 0;

		int dias = 0;
		for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
			int dow = date.getDayOfWeek().getValue(); // 1=Lunes, 7=Domingo
			boolean esFinDeSemana = switch (dow) {
			case 1 -> calendario.get(CALENDAR.MONDAY) == 1;
			case 2 -> calendario.get(CALENDAR.TUESDAY) == 1;
			case 3 -> calendario.get(CALENDAR.WEDNESDAY) == 1;
			case 4 -> calendario.get(CALENDAR.THURSDAY) == 1;
			case 5 -> calendario.get(CALENDAR.FRIDAY) == 1;
			case 6 -> calendario.get(CALENDAR.SATURDAY) == 1;
			case 7 -> calendario.get(CALENDAR.SUNDAY) == 1;
			default -> false;
			};
			if (esFinDeSemana) {
				dias++;
			}
		}
		return dias;
	}

	// Obtiene los contratos activos en el centro de trabajo en el rango indicado
	private List<Integer> obtenerContratosActivosCT(LocalDate startDate, LocalDate endDate, Integer workplaceId) {
		return dsl.select(CONTRACT.ID).from(CONTRACT)
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.where(CONTRACT.WORKPLACE.eq(workplaceId))
				.and(CONTRACT.START_DATE.le(Date.valueOf(endDate)))
				.and(CONTRACT.END_DATE.ge(Date.valueOf(startDate)).or(CONTRACT.END_DATE.isNull()))
				.and(CONTRACT.DOMAIN.eq(domainId))
				.orderBy(REGISTRY.NAME)
				.fetchInto(Integer.class);
	}

	// Genera el informe mensual de un contrato en el rango indicado
	private AnualDaysEntryExcel obtenerInformeMensualContrato(Integer contratoId, LocalDate globalStart,
			LocalDate globalEnd) {
		AnualDaysEntryExcel dto = new AnualDaysEntryExcel();

		// Obtener datos básicos del contrato
		Record registro = dsl
				.select(REGISTRY.NAME, REGISTRY.DOCUMENT, PERSON.SOCIAL_SECURITY_NUM, CONTRACT.START_DATE,
						CONTRACT.END_DATE, CONTRACT.AGREEMENT_LEVEL, CONTRACT.CATEGORY_DESCRIPTION, PERSON.GENDER,
						AGREEMENT_LEVEL.DESCRIPTION)
				.from(CONTRACT)
				.join(PERSON).on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.leftOuterJoin(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.where(CONTRACT.ID.eq(contratoId)).and(CONTRACT.DOMAIN.eq(domainId))
				.fetchOne();
		
		ContractType contractType = getContractType(contratoId);
		String partiality = getContractPartiality(contratoId);
		
		dto.setFullName(registro.get(REGISTRY.NAME));
		dto.setDocument(registro.get(REGISTRY.DOCUMENT));
		dto.setNaf(registro.get(PERSON.SOCIAL_SECURITY_NUM));
		dto.setStartDate(DATE_FORMAT.format(registro.get(CONTRACT.START_DATE)));
		dto.setEndDate(
				null == registro.get(CONTRACT.END_DATE) ? "" : DATE_FORMAT.format(registro.get(CONTRACT.END_DATE)));
		dto.setGender(registro.get(PERSON.GENDER) == (byte)0 ? "H"  : "M");
		dto.setAgreementCategory(registro.get(CONTRACT.CATEGORY_DESCRIPTION));
		dto.setAgreementLevel(null == registro.get(AGREEMENT_LEVEL.DESCRIPTION) ? "" : registro.get(AGREEMENT_LEVEL.DESCRIPTION) );
		dto.setContractType(null == contractType ? "" : (AonStringUtils.equals(contractType.getValue(), "000") ? "Becario" : contractType.getValue()) );
		dto.setPartiality(partiality);
		
		LocalDate contratoInicio = registro.get(CONTRACT.START_DATE).toLocalDate();
		LocalDate contratoFin = registro.get(CONTRACT.END_DATE) != null ? registro.get(CONTRACT.END_DATE).toLocalDate()
				: LocalDate.of(9999, 12, 31);

		// Iterar mes a mes en el rango global
		List<MonthlyDaysEntryExcel> meses = new ArrayList<>();
		LocalDate iter = globalStart.withDayOfMonth(1);
		while (!iter.isAfter(globalEnd)) {
			YearMonth ym = YearMonth.of(iter.getYear(), iter.getMonth());
			LocalDate monthStart = ym.atDay(1);
			LocalDate monthEnd = ym.atEndOfMonth();
			if (monthStart.isBefore(globalStart))
				monthStart = globalStart;
			if (monthEnd.isAfter(globalEnd))
				monthEnd = globalEnd;

			// Si el mes no se solapa con el contrato, se añaden ceros
			if (monthEnd.isBefore(contratoInicio) || monthStart.isAfter(contratoFin)) {
				int diasMes = (int) (monthEnd.toEpochDay() - monthStart.toEpochDay() + 1);
				meses.add(new MonthlyDaysEntryExcel()
						.setMes(obtenerNombreMes(iter.getMonthValue()))
						.setYear(iter.getYear())
						.setDiasMes(diasMes)
						.setDiasFestivos(0)
						.setDiasFinDeSemana(0)
						.setDiasLaborables(0)
						.setDiasVacaciones(0)
						.setDiasIT(0)
						.setDiasNoRecuperables(0)
						.setDiasAusencia(0)
						.setDiasTrabajados(0)
				);
			} else {
				// Calcular el intervalo efectivo de evaluación
				LocalDate evalStart = contratoInicio.isAfter(monthStart) ? contratoInicio : monthStart;
				LocalDate evalEnd = contratoFin.isBefore(monthEnd) ? contratoFin : monthEnd;
				meses.add(obtenerDatosMesContrato(contratoId, iter.getMonthValue(), iter.getYear(), evalStart, evalEnd, contractType));
			}
			iter = iter.plusMonths(1);
		}
		dto.setMeses(meses);
		return dto;
	}

	// Calcula los datos mensuales para un contrato en el intervalo de evaluación
	private MonthlyDaysEntryExcel obtenerDatosMesContrato(Integer contratoId, int mes, int year, LocalDate evalStart,
			LocalDate evalEnd, ContractType contractType) {
		MonthlyDaysEntryExcel mesDTO = new MonthlyDaysEntryExcel();
		mesDTO.setMes(obtenerNombreMes(mes));
		mesDTO.setYear(year);

		int diasMes = (int) (evalEnd.toEpochDay() - evalStart.toEpochDay() + 1);
		mesDTO.setDiasMes(diasMes);

		int diasFestivos = contarDiasFestivosContrato(contratoId, evalStart, evalEnd);
		mesDTO.setDiasFestivos(diasFestivos);

		int diasFinDeSemana = contarDiasFinDeSemanaContrato(contratoId, evalStart, evalEnd);
		mesDTO.setDiasFinDeSemana(diasFinDeSemana);

		int diasLaborables = diasMes - diasFestivos - diasFinDeSemana;
		mesDTO.setDiasLaborables(diasLaborables);

		int diasVacaciones = contarDiasVacaciones(contratoId, evalStart, evalEnd);
		mesDTO.setDiasVacaciones(diasVacaciones);

		int diasIT = contarDiasIT(contratoId, evalStart, evalEnd);
		mesDTO.setDiasIT(diasIT);

		int diasNoRecuperables = contarDiasNoRecuperables(contratoId, evalStart, evalEnd);
		mesDTO.setDiasNoRecuperables(diasNoRecuperables);
		
		int diasAusencia = contarDiasAusencia(contratoId, evalStart, evalEnd);
		mesDTO.setDiasAusencia(diasAusencia);
		
		// Se calcula días trabajados según el tipo de contrato
		int fullTimeWorkingDays = diasLaborables - diasVacaciones - diasIT - diasNoRecuperables;
		int diasTrabajados = (contractType == null || !contractType.isPartial() || isFullTime(contratoId))
				? fullTimeWorkingDays
				: contarDiasTrabajadosPorMes(contratoId, evalStart, evalEnd);
		mesDTO.setDiasTrabajados(diasTrabajados);

		return mesDTO;
	}

	// Método auxiliar para contar días festivos para un contrato
	private int contarDiasFestivosContrato(Integer contratoId, LocalDate evalStart, LocalDate evalEnd) {
		Integer calendarHolidayId = dsl.select(CALENDAR.HOLIDAY).from(CALENDAR).join(PAYROLL_WORKPLACE)
				.on(PAYROLL_WORKPLACE.CALENDAR.eq(CALENDAR.ID)).join(WORKPLACE)
				.on(WORKPLACE.ID.eq(PAYROLL_WORKPLACE.WORKPLACE)).join(CONTRACT).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
				.where(CONTRACT.ID.eq(contratoId)).fetchOneInto(Integer.class);
		if (calendarHolidayId == null)
			return 0;
		Set<Integer> holidayIds = new HashSet<>();
		holidayIds.add(calendarHolidayId);
		while (calendarHolidayId != null) {
			calendarHolidayId = dsl.select(HOLIDAY.HOLIDAY_).from(HOLIDAY).where(HOLIDAY.ID.eq(calendarHolidayId))
					.fetchOneInto(Integer.class);
			if (calendarHolidayId != null) {
				holidayIds.add(calendarHolidayId);
			}
		}
		return dsl.selectCount().from(HOLIDAY_DETAIL).where(HOLIDAY_DETAIL.HOLIDAY.in(holidayIds))
				.and(HOLIDAY_DETAIL.DATE.between(Date.valueOf(evalStart), Date.valueOf(evalEnd)))
				.fetchOneInto(Integer.class);
	}

	// Cuenta los fines de semana para un contrato. Se usa configuración específica
	// si existe;
	// de lo contrario se consulta el calendario asociado.
	private int contarDiasFinDeSemanaContrato(Integer contratoId, LocalDate evalStart, LocalDate evalEnd) {
		Result<ContractDataRecord> notWorkingDays = dsl.selectFrom(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contratoId))
				.and(CONTRACT_DATA.NAME.in("LABORABLE_LUNES", "LABORABLE_MARTES", "LABORABLE_MIERCOLES",
						"LABORABLE_JUEVES", "LABORABLE_VIERNES", "LABORABLE_SABADO", "LABORABLE_DOMINGO"))
				.fetch();
		if (!notWorkingDays.isEmpty()) {
			int dias = 0;
			for (LocalDate date = evalStart; !date.isAfter(evalEnd); date = date.plusDays(1)) {
				int dow = date.getDayOfWeek().getValue();
				String dayName = switch (dow) {
				case 1 -> "LABORABLE_LUNES";
				case 2 -> "LABORABLE_MARTES";
				case 3 -> "LABORABLE_MIERCOLES";
				case 4 -> "LABORABLE_JUEVES";
				case 5 -> "LABORABLE_VIERNES";
				case 6 -> "LABORABLE_SABADO";
				case 7 -> "LABORABLE_DOMINGO";
				default -> "\\";
				};
				boolean isNoLaborable = notWorkingDays.stream()
						.filter(r -> AonStringUtils.equalsIgnoreCase(r.getName(), dayName))
						.anyMatch(r -> AonStringUtils.equalsIgnoreCase(r.getExpression(), "1"));
				if (isNoLaborable)
					dias++;
			}
			return dias;
		} else {
			Integer calendarId = obtenerCalendarId(contratoId);
			Record7<Byte, Byte, Byte, Byte, Byte, Byte, Byte> calendario = dsl
					.select(CALENDAR.MONDAY, CALENDAR.TUESDAY, CALENDAR.WEDNESDAY, CALENDAR.THURSDAY, CALENDAR.FRIDAY,
							CALENDAR.SATURDAY, CALENDAR.SUNDAY)
					.from(CALENDAR).where(CALENDAR.ID.eq(calendarId)).and(CALENDAR.DOMAIN.eq(domainId)).fetchOne();
			if (calendario == null)
				return 0;
			int dias = 0;
			for (LocalDate date = evalStart; !date.isAfter(evalEnd); date = date.plusDays(1)) {
				int dow = date.getDayOfWeek().getValue();
				boolean isWeekend = switch (dow) {
				case 1 -> calendario.get(CALENDAR.MONDAY) == 1;
				case 2 -> calendario.get(CALENDAR.TUESDAY) == 1;
				case 3 -> calendario.get(CALENDAR.WEDNESDAY) == 1;
				case 4 -> calendario.get(CALENDAR.THURSDAY) == 1;
				case 5 -> calendario.get(CALENDAR.FRIDAY) == 1;
				case 6 -> calendario.get(CALENDAR.SATURDAY) == 1;
				case 7 -> calendario.get(CALENDAR.SUNDAY) == 1;
				default -> false;
				};
				if (isWeekend)
					dias++;
			}
			return dias;
		}
	}

	// Cuenta los días de vacaciones registrados en el contrato para el intervalo
    private int contarDiasVacaciones(Integer contratoId, LocalDate evalStart, LocalDate evalEnd) {
        BigDecimal result = dsl.select(DSL.sum(DSL.greatest(DSL.val(0), DSL.least(
                DSL.dateDiff(DSL.least(CONTRACT_DATA.END_DATE, DSL.val(Date.valueOf(evalEnd))),
                        DSL.greatest(CONTRACT_DATA.START_DATE, DSL.val(Date.valueOf(evalStart)))).plus(1),
                DSL.dateDiff(DSL.val(Date.valueOf(evalEnd)), DSL.val(Date.valueOf(evalStart))).plus(1)
        )))).from(CONTRACT_DATA)
                .where(CONTRACT_DATA.CONTRACT.eq(contratoId))
                .and(CONTRACT_DATA.NAME.eq("DIAS_VACACIONES"))
                .and(CONTRACT_DATA.DOMAIN.eq(domainId))
                .and(CONTRACT_DATA.START_DATE.le(Date.valueOf(evalEnd)))
                .and(CONTRACT_DATA.END_DATE.ge(Date.valueOf(evalStart)).or(CONTRACT_DATA.END_DATE.isNull()))
                .fetchOne(0, BigDecimal.class);
        return result == null ? 0 : result.intValue();
    }

    // Cuenta los días de IT (incapacidad temporal) en el contrato para el intervalo
    private int contarDiasIT(Integer contratoId, LocalDate evalStart, LocalDate evalEnd) {
        BigDecimal result = dsl.select(DSL.sum(DSL.greatest(DSL.val(0), DSL.least(
                DSL.dateDiff(DSL.least(CONTRACT_LEAVE.END_DATE, DSL.val(Date.valueOf(evalEnd))),
                        DSL.greatest(CONTRACT_LEAVE.START_DATE, DSL.val(Date.valueOf(evalStart)))).plus(1),
                DSL.dateDiff(DSL.val(Date.valueOf(evalEnd)), DSL.val(Date.valueOf(evalStart))).plus(1)
        )))).from(CONTRACT_LEAVE)
                .where(CONTRACT_LEAVE.CONTRACT.eq(contratoId))
                .and(CONTRACT_LEAVE.DOMAIN.eq(domainId))
                .and(CONTRACT_LEAVE.START_DATE.le(Date.valueOf(evalEnd)))
                .and(CONTRACT_LEAVE.END_DATE.ge(Date.valueOf(evalStart)).or(CONTRACT_LEAVE.END_DATE.isNull()))
                .fetchOneInto(BigDecimal.class);
        return result == null ? 0 : result.intValue();
    }
    
    // Cuenta los días no recuperables registrados en el contrato para el intervalo
    private int contarDiasNoRecuperables(Integer contratoId, LocalDate evalStart, LocalDate evalEnd) {
        BigDecimal result = dsl.select(DSL.sum(DSL.greatest(DSL.val(0), DSL.least(
                DSL.dateDiff(DSL.least(CONTRACT_DATA.END_DATE, DSL.val(Date.valueOf(evalEnd))),
                        DSL.greatest(CONTRACT_DATA.START_DATE, DSL.val(Date.valueOf(evalStart)))).plus(1),
                DSL.dateDiff(DSL.val(Date.valueOf(evalEnd)), DSL.val(Date.valueOf(evalStart))).plus(1)
        )))).from(CONTRACT_DATA)
                .where(CONTRACT_DATA.CONTRACT.eq(contratoId))
                .and(CONTRACT_DATA.NAME.eq("PERMISO_RETRIBUIDO"))
                .and(CONTRACT_DATA.DOMAIN.eq(domainId))
                .and(CONTRACT_DATA.START_DATE.le(Date.valueOf(evalEnd)))
                .and(CONTRACT_DATA.END_DATE.ge(Date.valueOf(evalStart)).or(CONTRACT_DATA.END_DATE.isNull()))
                .fetchOne(0, BigDecimal.class);
        return result == null ? 0 : result.intValue();
    }
    
    // Cuenta los días ausencia registrados en el contrato para el intervalo
    private int contarDiasAusencia(Integer contratoId, LocalDate evalStart, LocalDate evalEnd) {
        BigDecimal result = dsl.select(DSL.sum(DSL.greatest(DSL.val(0), DSL.least(
                DSL.dateDiff(DSL.least(CONTRACT_DATA.END_DATE, DSL.val(Date.valueOf(evalEnd))),
                        DSL.greatest(CONTRACT_DATA.START_DATE, DSL.val(Date.valueOf(evalStart)))).plus(1),
                DSL.dateDiff(DSL.val(Date.valueOf(evalEnd)), DSL.val(Date.valueOf(evalStart))).plus(1)
        )))).from(CONTRACT_DATA)
                .where(CONTRACT_DATA.CONTRACT.eq(contratoId))
                .and(CONTRACT_DATA.NAME.eq("COEFICIENTE_AUSENCIA").or(CONTRACT_DATA.NAME.eq("COEFICIENTE_HUELGA")))
                .and(CONTRACT_DATA.DOMAIN.eq(domainId))
                .and(CONTRACT_DATA.START_DATE.le(Date.valueOf(evalEnd)))
                .and(CONTRACT_DATA.END_DATE.ge(Date.valueOf(evalStart)).or(CONTRACT_DATA.END_DATE.isNull()))
                .fetchOne(0, BigDecimal.class);
        return result == null ? 0 : result.intValue();
    }

	// Cuenta los días trabajados, usando configuraciones de horas si el contrato es parcial,
	// y excluye los días que coinciden con vacaciones o IT.
	private int contarDiasTrabajadosPorMes(Integer contratoId, LocalDate evalStart, LocalDate evalEnd) {
	    Result<ContractDataRecord> registrosHoras = dsl.selectFrom(CONTRACT_DATA)
	            .where(CONTRACT_DATA.CONTRACT.eq(contratoId))
	            .and(CONTRACT_DATA.NAME.like("HORAS_%"))
	            .and(CONTRACT_DATA.DOMAIN.eq(domainId))
	            .fetch();
	    int diasTrabajados = 0;
	    for (LocalDate date = evalStart; !date.isAfter(evalEnd); date = date.plusDays(1)) {
	        // Excluir el día si es de vacaciones o IT
	        if (esDiaVacaciones(contratoId, date) || esDiaIT(contratoId, date)) {
	            continue;
	        }
	        final LocalDate currentDate = date; // variable final para usar en la expresión lambda
	        int dow = currentDate.getDayOfWeek().getValue();
	        String nombreDia = switch (dow) {
	            case 1 -> "HORAS_LUNES";
	            case 2 -> "HORAS_MARTES";
	            case 3 -> "HORAS_MIERCOLES";
	            case 4 -> "HORAS_JUEVES";
	            case 5 -> "HORAS_VIERNES";
	            case 6 -> "HORAS_SABADO";
	            case 7 -> "HORAS_DOMINGO";
	            default -> "";
	        };
	        Optional<ContractDataRecord> registroDia = registrosHoras.stream()
	                .filter(r -> r.getName().equals(nombreDia) && estaVigenteEnFecha(r, currentDate))
	                .findFirst();
	        if (registroDia.isPresent() && registroDia.get().getExpression() != null) {
	            diasTrabajados++;
	        }
	    }
	    return diasTrabajados;
	}

	// Verifica si un registro de configuración está vigente en la fecha indicada
	private boolean estaVigenteEnFecha(ContractDataRecord record, LocalDate fecha) {
	    LocalDate start = record.getStartDate().toLocalDate();
	    LocalDate end = record.getEndDate() != null ? record.getEndDate().toLocalDate() : LocalDate.of(9999, 12, 31);
	    return (!fecha.isBefore(start)) && (!fecha.isAfter(end));
	}

	// Devuelve true si para el contrato hay un registro de vacaciones vigente en la fecha
	private boolean esDiaVacaciones(Integer contratoId, LocalDate fecha) {
	    Integer count = dsl.selectCount()
	            .from(CONTRACT_DATA)
	            .where(CONTRACT_DATA.CONTRACT.eq(contratoId))
	            .and(CONTRACT_DATA.NAME.eq("DIAS_VACACIONES"))
	            .and(CONTRACT_DATA.DOMAIN.eq(domainId))
	            .and(CONTRACT_DATA.START_DATE.le(java.sql.Date.valueOf(fecha)))
	            .and(CONTRACT_DATA.END_DATE.ge(java.sql.Date.valueOf(fecha)).or(CONTRACT_DATA.END_DATE.isNull()))
	            .fetchOne(0, Integer.class);
	    return count != null && count > 0;
	}

	// Devuelve true si para el contrato hay un registro de IT vigente en la fecha
	private boolean esDiaIT(Integer contratoId, LocalDate fecha) {
	    Integer count = dsl.selectCount()
	            .from(CONTRACT_LEAVE)
	            .where(CONTRACT_LEAVE.CONTRACT.eq(contratoId))
	            .and(CONTRACT_LEAVE.DOMAIN.eq(domainId))
	            .and(CONTRACT_LEAVE.START_DATE.le(java.sql.Date.valueOf(fecha)))
	            .and(CONTRACT_LEAVE.END_DATE.ge(java.sql.Date.valueOf(fecha)).or(CONTRACT_LEAVE.END_DATE.isNull()))
	            .fetchOne(0, Integer.class);
	    return count != null && count > 0;
	}
	
	// Obtiene el tipo de contrato a partir de los datos del contrato
	private String getContractPartiality(Integer contratoId) {
		Result<ContractDataRecord> contractTypeCodes = dsl.selectFrom(CONTRACT_DATA)
				.where(CONTRACT_DATA.DOMAIN.eq(domainId)).and(CONTRACT_DATA.CONTRACT.eq(contratoId))
				.and(CONTRACT_DATA.NAME.eq("COEFICIENTE_PARCIALIDAD")).orderBy(CONTRACT_DATA.ID.desc()).fetch();
		
		if(contractTypeCodes.isEmpty()) return "100";
		
		String partiality = contractTypeCodes.get(0).getExpression().replace("\"", "").trim();
		try {
			Double part = Double.parseDouble(partiality);
			part = part * 100;
			return part.intValue() + AonStringUtils.EMPTY;
		} catch (Exception e) {
			return "N/D ";
		}
	}

	// Obtiene el tipo de contrato a partir de los datos del contrato
	private ContractType getContractType(Integer contratoId) {
		Result<ContractDataRecord> contractTypeCodes = dsl.selectFrom(CONTRACT_DATA)
				.where(CONTRACT_DATA.DOMAIN.eq(domainId)).and(CONTRACT_DATA.CONTRACT.eq(contratoId))
				.and(CONTRACT_DATA.NAME.eq("TC2")).orderBy(CONTRACT_DATA.ID.desc()).fetch();
		return contractTypeCodes.isEmpty() ? null
				: ContractType.fromStringCode(contractTypeCodes.get(0).getExpression());
	}

	// Devuelve true si la configuración indica que el contrato es de tiempo
	// completo
	private boolean isFullTime(Integer contratoId) {
		Result<ContractDataRecord> fullTimeContractDatas = dsl.selectFrom(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contratoId)).and(CONTRACT_DATA.NAME.like("TIEMPO_COMPLETO"))
				.and(CONTRACT_DATA.DOMAIN.eq(domainId)).orderBy(CONTRACT_DATA.ID.desc()).fetch();
		return fullTimeContractDatas.isEmpty()
				|| AonStringUtils.equalsIgnoreCase(fullTimeContractDatas.get(0).getExpression(), "true");
	}

	// Obtiene el calendarId para un contrato; si no se encuentra en el contrato, se
	// busca en la relación de payroll workplace
	private Integer obtenerCalendarId(Integer contratoId) {
		Integer calendarId = dsl.select(CONTRACT.CALENDAR).from(CONTRACT).where(CONTRACT.ID.eq(contratoId))
				.and(CONTRACT.DOMAIN.eq(domainId)).fetchOneInto(Integer.class);
		if (calendarId == null) {
			calendarId = dsl.select(PAYROLL_WORKPLACE.CALENDAR).from(PAYROLL_WORKPLACE).join(WORKPLACE)
					.on(WORKPLACE.ID.eq(PAYROLL_WORKPLACE.WORKPLACE)).join(CONTRACT)
					.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID)).where(CONTRACT.ID.eq(contratoId))
					.and(PAYROLL_WORKPLACE.DOMAIN.eq(domainId)).fetchOneInto(Integer.class);
		}
		return calendarId;
	}

	// Obtiene el calendarId para el centro de trabajo
	private Integer obtenerCalendarIdCT(Integer workplaceId) {
		return dsl.select(PAYROLL_WORKPLACE.CALENDAR).from(PAYROLL_WORKPLACE)
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId)).and(PAYROLL_WORKPLACE.DOMAIN.eq(domainId))
				.fetchOneInto(Integer.class);
	}
	
	// Obtiene el holidayId para el calendar
	private Integer obtenerHolidayIdCT(Integer calendarId) {
		return dsl.select(CALENDAR.HOLIDAY).from(CALENDAR)
				.where(CALENDAR.ID.eq(calendarId)).and(CALENDAR.DOMAIN.eq(domainId))
				.fetchOneInto(Integer.class);
	}

	// Obtiene la jerarquía de holidays a partir de un calendarId (recursivamente)
	private Set<Integer> obtenerJerarquiaHolidays(Integer calendarId) {
		Set<Integer> holidays = new HashSet<>();
		Integer current = calendarId;
		while (current != null) {
			holidays.add(current);
			current = dsl.select(HOLIDAY.HOLIDAY_).from(HOLIDAY).where(HOLIDAY.ID.eq(current))
					.fetchOneInto(Integer.class);
		}
		return holidays;
	}

	// Obtiene el calendario (configuración de días laborables) a partir del
	// calendarId
	private Record7<Byte, Byte, Byte, Byte, Byte, Byte, Byte> obtenerCalendario(Integer calendarId) {
		return dsl
				.select(CALENDAR.MONDAY, CALENDAR.TUESDAY, CALENDAR.WEDNESDAY, CALENDAR.THURSDAY, CALENDAR.FRIDAY,
						CALENDAR.SATURDAY, CALENDAR.SUNDAY)
				.from(CALENDAR).where(CALENDAR.ID.eq(calendarId).and(CALENDAR.DOMAIN.eq(domainId))).fetchOne();
	}

	// Devuelve la abreviatura del mes según el arreglo MESES
	private String obtenerNombreMes(int mes) {
		return (mes >= 1 && mes <= 12) ? MESES[mes - 1] : "INV";
	}
}
