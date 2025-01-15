package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.occam.api.model.type.ContractLeaveType.MATERNIDAD;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveType.PATERNIDAD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.tgss.its.ITComunica;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.Filter.EmployeeFilter;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.salary.expression.Period;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Idc;

public class ITStatusUtils {
	private static Logger logger = Logger.getLogger(ITStatusUtils.class.getName());

	private ITStatusUtils() {
		throw new IllegalStateException("Utility class");
	}

	public interface EmployeeItCallback<A, B, C, D, E> {
		public void accept(A a, B b, C c, D d, E e);
	}

	public static void getEnterpriseEmployeesITs(Domain domain, User user, Date startDate, Date endDate,
			EmployeeItCallback<Employee, Collection<EmployeeIT>, Collection<EmployeeIT>, Collection<EmployeeIT>, Collection<EmployeeIT>> callback) {

		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId(),
				"TGSS");

		List<CCCInfo> cccs = ITComunica.getCccs(domain);

		for (CCCInfo ccc : cccs) {

			List<EmployeeIT> aonCCCEmployeesIT = getITFromAon(domain, startDate, endDate, ccc, false);

			try {
				getITFromTGSS(domain, certificate, startDate, endDate, ccc, new String[0],
						(employee, idcEmployeeITs) -> {

							List<EmployeeIT> aonEmployeeITs = aonCCCEmployeesIT.stream()
									.filter(e -> e.getNss().equals(employee.getNaf())).toList();
							
//							System.out.println("----- aonEmployeeITs -------");
//							aonEmployeeITs.forEach(employeeIt -> System.out.println(employeeIt.getName().get() + " --> " + employeeIt.getType() + ", start : " + employeeIt.getStartDate() + ", end : " + employeeIt.getEndDate()));
							
//							System.out.println("----- idcEmployeeITs -------");
//							idcEmployeeITs.forEach(employeeIt -> System.out.println(employeeIt.getName().get() + " --> " + employeeIt.getType() + ", start : " + employeeIt.getStartDate() + ", end : " + employeeIt.getEndDate()));
							
							List<EmployeeIT> notInAON = diff(idcEmployeeITs, aonEmployeeITs);
//							if(!notInAON.isEmpty()) {
//								System.out.println("----- DIFERENCIAS (NOT IN AON) -------");
//								notInAON.forEach(employeeIt -> System.out.println(employeeIt.getName().get() + " --> " + employeeIt.getType() + ", start : " + employeeIt.getStartDate() + ", end : " + employeeIt.getEndDate()));
//							}
							
							List<EmployeeIT> notInTGSS = diff(aonEmployeeITs, idcEmployeeITs);
//							if(!notInTGSS.isEmpty()) {
//								System.out.println("----- DIFERENCIAS (NOT IN TGSS) -------");
//								notInTGSS.forEach(employeeIt -> System.out.println(employeeIt.getName().get() + " --> " + employeeIt.getType() + ", start : " + employeeIt.getStartDate() + ", end : " + employeeIt.getEndDate()));
//							}
							
							List<EmployeeIT> inBothList = new ArrayList<>(aonEmployeeITs);
							inBothList.removeAll(notInTGSS);
//							if(!inBothList.isEmpty()) {
//								System.out.println("----- DIFERENCIAS (IN BOTH LIST) -------");
//								inBothList.forEach(employeeIt -> System.out.println(employeeIt.getName().get() + " --> " + employeeIt.getType() + ", start : " + employeeIt.getStartDate() + ", end : " + employeeIt.getEndDate()));
//							}
							
							List<EmployeeIT> inTgss = new ArrayList<>(inBothList);
							inTgss.addAll(notInAON);
//							if(!inTgss.isEmpty()) {
//								System.out.println("----- DIFERENCIAS (IN TGSS) -------");
//								inTgss.forEach(employeeIt -> System.out.println(employeeIt.getName().get() + " --> " + employeeIt.getType() + ", start : " + employeeIt.getStartDate() + ", end : " + employeeIt.getEndDate()));
//							}
								
							callback.accept(employee, notInAON, notInTGSS, inBothList, inTgss);
						});
			} catch (SegSocialException e) {
			}
		}

	}

	private static List<EmployeeIT> diff(Collection<EmployeeIT> firstList, Collection<EmployeeIT> secondList) {
		List<EmployeeIT> diffList = new ArrayList<>();
		if (!firstList.isEmpty()&&!secondList.isEmpty()) {
			for (EmployeeIT first : firstList) {
				boolean find = find(first, secondList);
				if (find) {
					continue;
				} else {
					diffList.add(first);
				}
			}
		}
		return diffList;
	}

	private static boolean find(EmployeeIT first, Collection<EmployeeIT> secondList) {
		for (EmployeeIT second : secondList) {
//			System.out.println("Type : " + first.getType() + ", " + second.getType() + " --> " + (first.getType() == second.getType())
//					+ ", StartDate : " + first.getStartDate() + ", " + second.getStartDate() + " --> " + (first.getStartDate().equals(second.getStartDate())) 
//					+ ", EndDate : " + first.getEndDate() + ", " + second.getEndDate() + " --> " + (first.getEndDate().equals(second.getEndDate())));
			
			if (first.getType() == second.getType() && first.getStartDate().equals(second.getStartDate())
					&& first.getEndDate().equals(second.getEndDate())) {
				return true;
			}
		}
		
		return false;
	}

	private static List<EmployeeIT> getITFromAon(Domain domain, Date startDate, Date endDate, CCCInfo ccc,
			boolean paternity) {
		Byte[] types = new Byte[] { MATERNIDAD.value(), PATERNIDAD.value() };

		return AON
				.getEmployeesIT(domain, new User(),
						f -> f.getDomainProperty().eq(domain.getId())
								.and(f.getStartDateProperty().ge(convertDateSql(startDate))
										.and(f.getStartDateProperty().le(convertDateSql(endDate))))
								.and(f.getCCCProperty().eq(ccc.getCccAccount()))
								.and(paternity ? f.getTypeProperty().in(types) : f.getTypeProperty().notIn(types)))
				.collect(Collectors.toList());
	}

	protected static java.sql.Date toSqlDate(Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}

	// COMUN
	public static List<EmployeeIT> getITFromTGSS(Domain domain, Certificate certificate, Date startDate, Date endDate,
			CCCInfo ccc, String[] nafs, BiConsumer<Employee, Collection<EmployeeIT>> callback)
			throws SegSocialException {
		List<EmployeeIT> ssIts = new ArrayList<>();

		EmployeeFilter employeeFilter;
		if (nafs.length == 0) {
			employeeFilter = p -> p.getCCCProperty().eq(ccc.getCcc())
					.and(p.getEndDateProperty().ge(toSqlDate(startDate))).or(p.getEndDateProperty().isNull())
					.and(p.getStartDateProperty().le(toSqlDate(endDate)));
		} else {
			employeeFilter = p -> p.getCCCProperty().eq(ccc.getCcc()).and(p.getNafProperty().in(nafs));
		}

		Period itPeriod = new Period(startDate, endDate);

		Stream<Employee> employees = PAYROLL.getEmployees(domain.getName(), domain.getId(), null, employeeFilter);
		employees.forEach(employee -> {
			try {
				Collection<Idc> idcDates = SistemaRED.getIDCDates(certificate.getData(),
						certificate.getPassword(), certificate.getType(), ccc.getCccRegimeCode(),
						ccc.getCcc(), employee.getNaf());

				List<Idc> idcDatesList = new ArrayList<>(idcDates);

				for (int i = 0; i < idcDatesList.size(); i += 2) {
					Idc idcDate = idcDatesList.get(i);
					Date idcEndDate = null;
					if (i + 1 < idcDatesList.size()) {
						idcEndDate = idcDatesList.get(i + 1).getFecha();
					}
					Period idcPeriod = new Period(idcDate.getFecha(), idcEndDate);
					if (itPeriod.intersects(idcPeriod)) {
						byte[] idc = SistemaRED.getIDC(certificate.getData(), certificate.getPassword(),
								certificate.getType(), ccc.getCccRegimeCode(), ccc.getCcc(), employee.getNaf(),
								idcDate.getFecha());
						if(null == idc) {
							callback.accept(employee, Collections.emptyList());
						} else {
							Collection<EmployeeIT> idcIts = com.esferalia.aon.in.payroll.tgss.idc.Idc.getEmployeeITs(idc);
							ssIts.addAll(idcIts);

							callback.accept(employee, idcIts);
						}
						
					}
				}
			} catch (SegSocialException e) {
				System.err.println("---------- ERROR SegSocialException ; " + e.getMessage());
//				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnknownPDFException e) {
				e.printStackTrace();
			}

		});

		return ssIts;
	}

	private static java.sql.Date convertDateSql(Date utilDate) {
		return new java.sql.Date(utilDate.getTime());
	}

}
