package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.occam.api.model.type.ContractLeaveType.MATERNIDAD;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveType.PATERNIDAD;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.AndEmployeeITStatus;
import com.esferalia.aon.gwt.payroll.shared.OutOfDateException;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.tgss.its.ITComunica;
import com.esferalia.aon.in.payroll.tgss.its.ITParse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.Filter.EmployeeFilter;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.security.CertificateNotFoundException;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.lowagie.text.pdf.codec.Base64.InputStream;

import solutions.aon.seg.social.Paternity;
import solutions.aon.seg.social.ServicioRED;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.PaternityCertificate;

public class ITStatusUtils {
	private static Logger logger = Logger.getLogger(ITStatusUtils.class.getName());

	private ITStatusUtils() {
		throw new IllegalStateException("Utility class");
	}

	@Deprecated
	public static EnterpriseITStatus getEnterpriseITStatus(Domain domain, User user) {
		Date startDate = getFirstDateOfMonth(AonDateUtils.addMonths(new Date(), -7));
		Date endDate = new Date();
		return getEnterpriseITStatus(domain, user, startDate, endDate, (a, b) -> {
		});
	}

	public static EnterpriseITStatus getEnterpriseITStatus(Domain domain, User user, Date startDate, Date endDate,
			BiConsumer<Employee, Collection<EmployeeIT>> callback) {
		logger.info("getEnterpriseITStatus");

		try {
			AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
			Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(),
					user.getId(), "TGSS");

			List<CCCInfo> cccs = ITComunica.getCccs(domain);

			for (CCCInfo ccc : cccs) {
				employeeITStatus.and(compareComun(domain, certificate, startDate, endDate, ccc, callback));
			}

			try {
				EnterpriseITStatus.isUp2Date(employeeITStatus);
				employeeITStatus.and(new EnterpriseITStatus.Up2Date());
			} catch (OutOfDateException e) {
			}

			employeeITStatus.and(new EnterpriseITStatus.onFinish());
			EnterpriseITStatus.trace(employeeITStatus);
			return employeeITStatus;
		} catch (CertificateNotFoundException e) {
			return new EnterpriseITStatus.CredentialsNotFound();
		} catch (Exception e) {
			e.printStackTrace();
			return new EnterpriseITStatus.UnknownError().setMessage(e.getMessage());
		}
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
							List<EmployeeIT> notInAON = diff(idcEmployeeITs, aonEmployeeITs);
							List<EmployeeIT> notInTGSS = diff(aonEmployeeITs, idcEmployeeITs);
							List<EmployeeIT> inBothList = new ArrayList<>(aonEmployeeITs);
							inBothList.removeAll(notInTGSS);
							List<EmployeeIT> inTgss = new ArrayList<>(inBothList);
							inTgss.addAll(notInAON);
								
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
			System.out.println("Type : " + (first.getType() == second.getType()) + ", StartDate : "
					+ (first.getStartDate().equals(second.getStartDate())) + ", EndDate : "
					+ (first.getEndDate().equals(second.getEndDate())));
			if (first.getType() == second.getType() && first.getStartDate().equals(second.getStartDate())
					&& first.getEndDate().equals(second.getEndDate())) {
				return true;
			}
		}
		return false;
	}

	private static AndEmployeeITStatus compareComun(Domain domain, Certificate certificate, Date startDate,
			Date endDate, CCCInfo ccc, BiConsumer<Employee, Collection<EmployeeIT>> callback) {
		AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
		try {
			logger.info("compareComun");
			List<EmployeeIT> aonEmployeesIT = getITFromAon(domain, startDate, endDate, ccc, false);

			List<EmployeeIT> ssIts = getITFromTGSS(domain, certificate, startDate, endDate, ccc, new String[0],
					(employee, idcEmployeeITs) -> {
						// compareEmployeesITs(aonEmployeesIT, idcEmployeeITs, employeeITStatus,
						// domain); // NO EXIST EN AON
						// compareEmployeesITs(idcEmployeeITs, aonEmployeesIT, employeeITStatus,
						// domain); // NO EXIST EN SS
						callback.accept(employee, idcEmployeeITs);
					});

			// logger.info("------------------NO EXIST EN AON COMUN------------------");
			compareEmployeesITs(aonEmployeesIT, ssIts, employeeITStatus, domain); // NO EXIST EN AON

			// logger.info("------------------NO EXIST EN SS COMUN------------------");
			compareEmployeesITs(ssIts, aonEmployeesIT, employeeITStatus, domain); // NO EXIST EN SS

		} catch (Exception e) {
			e.printStackTrace();
			return new EnterpriseITStatus.unknownErrorAnd().setMessage(e.getMessage());
		}
		return employeeITStatus;
	}

	private static AndEmployeeITStatus comparePaternity(Domain domain, User user, Certificate certificate,
			Date startDate, Date endDate, CCCInfo ccc) {
		AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
		try {
			logger.info("comparePaternity");

			List<EmployeeIT> ssIts = getITFromTGSSPaternity(domain, user, certificate, startDate, endDate, ccc,
					Optional.empty());

			Date start = ssIts.isEmpty() ? startDate : ssIts.get(0).getStartDate();

			List<EmployeeIT> aonEmployeesIT = getITFromAon(domain, start, endDate, ccc, true);
			logger.info("------------------NO EXIST EN AON PATERNITY------------------");
			compareEmployeesITs(aonEmployeesIT, ssIts, employeeITStatus, domain); // NO EXIST EN AON

			logger.info("------------------NO EXIST EN SS PATERNITY------------------");
			compareEmployeesITs(ssIts, aonEmployeesIT, employeeITStatus, domain); // NO EXIST EN SS
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeeITStatus;
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
						Collection<EmployeeIT> idcIts = com.esferalia.aon.in.payroll.tgss.idc.Idc.getEmployeeITs(idc);
						ssIts.addAll(idcIts);

						callback.accept(employee, idcIts);
					}
				}
			} catch (SegSocialException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnknownPDFException e) {
				e.printStackTrace();
			}

		});

		return ssIts;
	}

	// PATERNITY
	private static List<EmployeeIT> getITFromTGSSPaternity(Domain domain, User user, Certificate certificate,
			Date startDate, Date endDate, CCCInfo ccc, Optional<String> nssOpt)
			throws SegSocialException, InterruptedException, IOException {
		List<EmployeeIT> ssIts = new ArrayList<>();

		List<PaternityCertificate> paternitys = Paternity
				.getPaternitys(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
						certificate.getType(), ccc.getCccRegimeCode(), ccc.getCccAccount(), startDate, endDate, nssOpt,
						Optional.empty())
				.stream().distinct().filter(p -> !p.getCanceled()).collect(Collectors.toList());

		if (!paternitys.isEmpty()) {
			setEmployeeData(domain, user, paternitys);
			paternitys.stream().filter(p -> p.getWorkerNif().isPresent()).forEach(paternity -> {
				ssIts.add(ITParse.parsePaternityTGSSToAon(paternity).setDomain(domain.getId()));
			});
		}

		return ssIts;
	}

	/*
	 * FIRST LIST NOT EXIST IN SECOND LIST
	 */
	private static AndEmployeeITStatus compareEmployeesITs(Collection<EmployeeIT> oneList,
			Collection<EmployeeIT> anotherList, AndEmployeeITStatus employeeITStatus, Domain domain) {
		List<EmployeeIT> itsUpdate = new ArrayList<>();

		for (EmployeeIT anotherIt : anotherList) {
			AtomicBoolean change = new AtomicBoolean(false);
			Optional<String> name = anotherIt.getName();
			if (!name.isEmpty() && anotherIt.getStartDate() != null) {

				List<EmployeeIT> oneIt = oneList.stream().filter(e -> e.getNss().equals(anotherIt.getNss()))
						.peek(e -> System.out.println(e.getNss() + ":" + e.getStartDate() + ", "
								+ anotherIt.getStartDate() + " = " + e.getStartDate().equals(anotherIt.getStartDate())))
						.filter(e -> e.getStartDate().equals(anotherIt.getStartDate())).collect(Collectors.toList());
				logger.info("BAJA NSS:" + anotherIt.getNss() + " type: " + anotherIt.getType() + " date:"
						+ anotherIt.getStartDate() + " toAon:" + anotherIt.getId());
				if (oneIt.isEmpty()) {
					employeeITStatus.and(new EnterpriseITStatus.ItNotExist().setEmployeeIT(anotherIt)
					// .setEmployeeITPart(null);
					);
				}

				if (change.get())
					itsUpdate.add(anotherIt);
			} // IF NAME
		}

		if (!itsUpdate.isEmpty()) {
			itUpdate(domain, itsUpdate);
			employeeITStatus.and(new EnterpriseITStatus.UpdatedEnterprise());
		}
		return employeeITStatus;
	}

	private static void setEmployeeData(Domain domain, User user, List<PaternityCertificate> paternitys) {
		List<PaternityCertificate> list = paternitys.stream()
				.filter(it -> it.getWorkerName().isEmpty() && !it.getWorkerNaf().isEmpty())
				.filter(ITComunica.distinctByKey(p -> p.getWorkerNaf().get())).collect(Collectors.toList());

		if (!list.isEmpty()) {
			String[] nssAll = list.stream().map(p -> p.getWorkerNaf().get()).toArray(String[]::new);

			AON.getPersonList(domain.getName(), domain.getId(), user.getLogin(),
					f -> f.getDocumentProperty().isNotNull().and(f.getSocialSecurityNumProperty().in(nssAll)))
					.forEach(person -> {
						paternitys.stream().filter(p -> p.getWorkerNaf().isPresent()
								&& p.getWorkerNaf().get().equals(person.getSocialSecurityNum())).forEach(p -> {
									System.out.println(person.getName() + ", " + person.getDocument());
									Gender gender = person.getGender();
									p.setWorkerName(person.getName()).setWorkerNif(person.getDocument())
											.setIsFather(gender != null && gender.ordinal() == 0 ? true : false);
								});
					});
		}
	}

	private static void itUpdate(Domain domain, List<EmployeeIT> employeeITs) {
		try {
			AON.setEmployeeIT(domain, new User(), employeeITs.toArray(EmployeeIT[]::new));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static java.sql.Date convertDateSql(Date utilDate) {
		return new java.sql.Date(utilDate.getTime());
	}

	private static Date getFirstDateOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

//	private static Date getDateBefore(Date date1, Date date2) {
//		return date1.compareTo(date2) < 0  ? date1 : date2;
//	}

}
