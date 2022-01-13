package com.esferalia.aon.in.payroll.tgss.its;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDischargeCause;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.watson.server.AonDateUtils;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.SistemaRED.AccidentType;
import solutions.aon.seg.social.SistemaRED.CauseType;
import solutions.aon.seg.social.SistemaRED.Contingencies;
import solutions.aon.seg.social.SistemaRED.ContractType;
import solutions.aon.seg.social.SistemaRED.SituationEmployee;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.object.It;

public class ITComunica {

	private ITComunica() {
		throw new IllegalStateException("Utility class");
	}

	// SINCRONIZAR ITS
	public static void syncUpITs(final byte certificateData[], final String certificatePassword,
			final String certificateType, Domain domain, Optional<String> nss) throws IllegalArgumentException {

		List<CCCInfo> cccs = PAYROLL.getCCCStream(domain.getName(), domain.getId(), "")
				.filter(distinctByKey(CCCInfo::getCccAccount)).collect(Collectors.toList());

		for (CCCInfo cti : cccs) {
			try {
				String regime = cti.getCccRegimeCode();
				String ccc = cti.getCccAccount();

				Date startDate = AonDateUtils.addYears(new Date(), -1);
				Date endDate = new Date();

		
				Collection<It> its = SistemaRED.getIts(certificateData, certificatePassword, certificateType, regime,
						ccc, startDate, endDate, nss);
		
				List<EmployeeIT> employeeITs = new ArrayList<>();
				
				for (It it : its) {
					
					EmployeeIT employeeIT = ITParse.parseTGSSToAon(it);
					
					String naf = nss.isPresent() ? nss.get() : employeeIT.getNss();
					Optional<Employee> contract = ITComunica.contractIts(domain, ccc, naf, employeeIT.getStartDate(), employeeIT.getEndDate());

					if (contract.isPresent()) {

						employeeIT.setContract(contract.get().getEmployeeId()).setDomain(domain.getId());
						
						employeeITs.add(employeeIT);
					} else 
						System.out.println("contractEmpty");		
				}

				AON.setEmployeeIT(domain, new User(), employeeITs.toArray(EmployeeIT[]::new));
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
		}
	}
	
	public static void communicateIT(final byte certificateData[], final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt) throws SegSocialException {
		 Optional<EmployeeITPart> baja = employeeIt.getItBaja();
		 Optional<EmployeeITPart> alta = employeeIt.getItAlta();
		 List<EmployeeITPart> confirmations = employeeIt.getItConfirmations();
		 
		 if(baja.isPresent()) 
			 registerITBaja(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, baja.get());
		 
		 if(!confirmations.isEmpty()) {
			 confirmations.forEach(itPart-> {
				try {
					registerITConfirmation(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, itPart);
				} catch (SegSocialException e) {
					e.printStackTrace();
				}
			});
		 }
	
		 if(alta.isPresent()) 
			 registerITAlta(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, alta.get());
	}
	
	private static void registerITBaja(final ByteArrayInputStream byteArrayInputStream, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt, EmployeeITPart itPart) throws SegSocialException {
		
			verifyData(new Object[] { 
					 employeeIt.getRegime(), employeeIt.getCcc(), employeeIt.getNss(), employeeIt.getDailyCgcBase().get(), employeeIt.getType(),  itPart.getDate(),
			});

			Double base = employeeIt.getDailyCgcBase().get();
			float baseCgc   = base.floatValue();
			int quoteDays = employeeIt.getQuoteDays();
			String regime = employeeIt.getRegime();
			String ccc = employeeIt.getCcc();
			String nss = employeeIt.getNss();
			ContractLeaveType causeBj = employeeIt.getType();
			Date date = itPart.getDate();
		
			Contingencies contingencie = SistemaRED.Contingencies.safeValueOf(causeBj.getValueTGSS()-1); 
			SituationEmployee situation = SituationEmployee.ACTIVO;
			ContractType contractType =  ContractType.FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL;
			
			Optional<AccidentType> accidentType = Optional.of(AccidentType.LEVE);
			Optional<Date> fATEP = Optional.empty();
			Optional<String> occupation = Optional.empty();
			Optional<String> cias = itPart.getCias();
			Optional<String> itPartCollegeNumber = itPart.getCollegeNumber();
			Optional<String> collegeNumber = itPartCollegeNumber.isPresent() && Integer.parseInt(itPartCollegeNumber.get())>0 ?	itPart.getCollegeNumber() :	Optional.empty();

			SistemaRED.registerITBaja(
					byteArrayInputStream.readAllBytes(), certificatePassword, certificateType, 
					regime, ccc, nss, 
					contingencie, situation,  
					date, contractType, 
					baseCgc, quoteDays, 
					fATEP, accidentType,
					collegeNumber, cias, occupation
			);
	}
	
	private static void registerITConfirmation(final ByteArrayInputStream byteArrayInputStream, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt, EmployeeITPart itPart) throws SegSocialException {

			verifyData(new Object[] { 
					employeeIt.getRegime(), employeeIt.getCcc(), employeeIt.getNss(), employeeIt.getType(), 
					employeeIt.getStartDate(), itPart.getDate(), itPart.getConfirmOrder().get()
			});
			
			Date fbaja = employeeIt.getStartDate();
			String regime = employeeIt.getRegime();
			String ccc = employeeIt.getCcc();
			String nss = employeeIt.getNss();
			Date fconfirmation = itPart.getDate();
			Optional<Byte> confirm = itPart.getConfirmOrder();
			Optional<String> npartConfimation = confirm.isPresent() ? Optional.of(confirm.get().intValue()+"") : Optional.empty();
			
			ContractLeaveType causeBj = employeeIt.getType();
		
			Contingencies contingencie = SistemaRED.Contingencies.safeValueOf(causeBj.getValueTGSS()-1); 
			SituationEmployee situation = SituationEmployee.ACTIVO;
	
			Optional<String> cias = itPart.getCias();
			Optional<String> itPartCollegeNumber = itPart.getCollegeNumber();
			Optional<String> collegeNumber = itPartCollegeNumber.isPresent() && Integer.parseInt(itPartCollegeNumber.get())>0 ?	itPart.getCollegeNumber() :	Optional.empty();

			SistemaRED.registerITConfirmation(byteArrayInputStream.readAllBytes(), certificatePassword, certificateType, 
					regime, ccc, nss, contingencie, situation, collegeNumber, cias, fbaja, fconfirmation, npartConfimation);
	}
	
	private static void registerITAlta(final ByteArrayInputStream byteArrayInputStream, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt, EmployeeITPart itPart) throws SegSocialException {
		
			verifyData(new Object[] { 
					employeeIt.getRegime(), employeeIt.getCcc(), employeeIt.getNss(), employeeIt.getType(), 
					employeeIt.getDischargeCause(), employeeIt.getStartDate(), itPart.getDate() 
			});
			
			Date fbaja = employeeIt.getStartDate();
			Date falta = itPart.getDate();
			String regime = employeeIt.getRegime();
			String ccc = employeeIt.getCcc();
			String nss = employeeIt.getNss();
			
			ContractLeaveType causeBj = employeeIt.getType();
			ContractLeaveDischargeCause causeAl = employeeIt.getDischargeCause();
			
			Contingencies contingencie = SistemaRED.Contingencies.safeValueOf(causeBj.getValueTGSS()-1);
			
			CauseType causeType = SistemaRED.CauseType.safeValueOf(causeAl.value()); 
			
			SituationEmployee situation = SituationEmployee.ACTIVO;

			Optional<AccidentType> accidentType = Optional.of(AccidentType.LEVE);
			Optional<Date> fATEP = Optional.empty();
			Optional<String> cias = itPart.getCias();
			Optional<String> itPartCollegeNumber = itPart.getCollegeNumber();
			Optional<String> collegeNumber = itPartCollegeNumber.isPresent() && Integer.parseInt(itPartCollegeNumber.get())>0 ?	itPart.getCollegeNumber() :	Optional.empty();

			SistemaRED.registerITAlta(
					byteArrayInputStream.readAllBytes(), certificatePassword, certificateType, 
					regime, ccc, nss, 
					contingencie, situation, 
					fbaja, falta, 
					fATEP, accidentType, 
					causeType, collegeNumber, cias);
			
	}

	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	/**
	 * 
	 * @param domain
	 * @param employee (ccc, naf, startDate, endDate(Optional))
	 * @return employee( contract exist)
	 */
	private static Optional<Employee> contractIts(Domain domain, String ccc, String nss, Date startDate,
			Optional<Date> endDate) {
		return PAYROLL
				.getEmployee(
						domain.getName(), domain.getId(), "", f -> f
								.getDomainProperty().eq(
										domain.getId())
								.and(f.getCCCProperty()
										.eq(ccc))
								.and(f.getNafProperty().eq(nss))
								.and(endDate.isPresent()
										? f.getStartDateProperty().le(new java.sql.Date(startDate.getTime()))
												.and(f.getEndDateProperty()
														.ge(new java.sql.Date(endDate.get().getTime()))
														.or(f.getEndDateProperty().isNull()))
										: f.getEndDateProperty().isNull()
												.or(f.getEndDateProperty().ge(new java.sql.Date(startDate.getTime()))))

				);
	}

	
	// HANDLES EMPTY DATA
	public static void verifyData(Object[] data) throws InvalidDataException {
		for (Object o : data)
			if (o == null || (o instanceof String && ((String) o).trim().equals("")))
				throw new UnfilledMandatory();
	}
}
