package com.esferalia.aon.in.payroll.tgss.its;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
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
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDischargeCause;
import com.esferalia.aon.watson.server.AonDateUtils;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.SistemaRED.AccidentType;
import solutions.aon.seg.social.SistemaRED.CauseType;
import solutions.aon.seg.social.SistemaRED.Contingencies;
import solutions.aon.seg.social.SistemaRED.ContractType;
import solutions.aon.seg.social.SistemaRED.PartType;
import solutions.aon.seg.social.SistemaRED.SituationEmployee;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.object.PaternityCertificate.ApplicantType;
import solutions.aon.seg.social.object.PaternityCertificate.ReasonType;

public class ITComunica {
	
	private static String SUCCESS = "success";
	
	private ITComunica() {
		throw new IllegalStateException("Utility class");
	}

	// SINCRONIZAR ITS
	public static void syncUpITs(final byte[] certificateData, final String certificatePassword,
			final String certificateType, Domain domain, Optional<String> nss) throws IllegalArgumentException {

		List<CCCInfo> cccs = getCccs(domain);
		
		Date startDate = AonDateUtils.addYears(new Date(), -1);
		Date endDate = new Date();

		for (CCCInfo cti : cccs) {
			try {
				String regime = cti.getCccRegimeCode();
				String ccc = cti.getCccAccount();
				
				List<EmployeeIT> employeeITs = new ArrayList<>();
				
				SistemaRED.getIts(certificateData, certificatePassword, certificateType, regime, ccc, startDate, endDate, nss)
				.stream()
				.forEach(it -> employeeITs.add(ITParse.parseTGSSToAon(it)));

				saveITs(domain, employeeITs);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
		}
	}
	
	// SINCRONIZAR ITS
	public static void saveITs(Domain domain, List<EmployeeIT> employeeITs) throws IllegalArgumentException {
		
		employeeITs.stream().filter(e->e.getContract()==null)
		.forEach(employeeIT->{
			employeeIT.setDomain(domain.getId());	
		});

		AON.setEmployeeIT(domain, new User(), employeeITs.toArray(EmployeeIT[]::new));
	}
	
	public static List<String> communicateITs(final byte[] certificateData, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt) {
		 List<String> messages = new ArrayList<>();
		 
		 if(employeeIt.isPaternity()) {
			 communicatePaternity(certificateData, certificatePassword, certificateType, employeeIt, messages);
		 } else {
	
			 Optional<EmployeeITPart> baja = employeeIt.getItBaja();
			 Optional<EmployeeITPart> alta = employeeIt.getItAlta();
			 List<EmployeeITPart> confirmations = employeeIt.getItConfirmations();

			 if(!baja.isEmpty()) {				 
				 registerITBaja(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, baja.get(), messages);
			 }
			 
			 if(!confirmations.isEmpty()) {
				 confirmations.forEach(itPart-> 
					registerITConfirmation(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, itPart, messages)
				 );
			 }
			 if(!alta.isEmpty()) {				 
				 registerITAlta(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, alta.get(), messages);
			 }
		 }
		 
		 return messages;
	}
	
	private static void communicatePaternity(final byte[] certificateData, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt, List<String> messages){
		try {
			String regime = employeeIt.getRegime();
			String ccc = employeeIt.getCcc();
			String nss = employeeIt.getNss();
			
			Optional<String> type = employeeIt.getPaternityType();
			Optional<String> reasonOpt = employeeIt.getPaternityReason();
			Optional<Date> endDate = employeeIt.getEndDate();
			Optional<String> dni = employeeIt.getDni();
			Optional<Double> baseCgcOpt = employeeIt.getDailyCgcBase();
			Optional<Double> baseCgpOpt = employeeIt.getDailyCgpBase();

			Integer days = employeeIt.getQuoteDays();
			Date dateFrom = employeeIt.getStartDate();
			
			ApplicantType applicantType = ApplicantType.safeValueOf( Integer.parseInt(type.get()) );
			
			ReasonType reason = ReasonType.safeValueOf( Integer.parseInt(reasonOpt.get()) );
			
			float baseCtiCC = baseCgcOpt.get().floatValue() * days;
	
			float baseCtiCP = baseCgpOpt.get().floatValue() * days;
			
			Date dateTo = endDate.isPresent() ? endDate.get() : AonDateUtils.addDays(dateFrom, (16*7));
			if(applicantType.equals(ApplicantType.OTRO_PROGENITOR)) {
				dateTo = endDate.isPresent() ? endDate.get() : AonDateUtils.addDays(dateFrom, (12*7));
			}
			
			dateTo = AonDateUtils.addDays(dateTo, -1);
	
			System.out.println(nss+", "+regime+", "+ccc+", "+dni.get()+", "+applicantType+", "+reason+", "+dateFrom+", "+dateTo+", "+baseCtiCC+", "+baseCtiCP+", "+days);
			
			SistemaRED.sendPaternity(certificateData, certificatePassword, certificateType, 
						nss, regime, ccc, dni.get(), applicantType, reason, dateFrom, dateTo, baseCtiCC, baseCtiCP, days);
			
			messages.add(SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			messages.add(e.getMessage());
		}
	}
	
	public static List<String> removeITs(final byte[] certificateData, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt) {
		
		 List<String> messages = new ArrayList<>();
		 if(employeeIt.isPaternity()) {
			 removePaternity(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, messages);
		 } else {
			 Optional<EmployeeITPart> baja = employeeIt.getItBaja();
			 Optional<EmployeeITPart> alta = employeeIt.getItAlta();
			 List<EmployeeITPart> confirmations = employeeIt.getItConfirmations();
			 
			 if(!baja.isEmpty()) 
				 removeIt(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, baja.get(), messages);
			 
			 if(!confirmations.isEmpty()) {
				 confirmations.forEach(itPart-> 
				 	removeIt(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, itPart, messages)
				 );
			 }
		
			 if(!alta.isEmpty()) 
				 removeIt(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, alta.get(), messages);
		 }
		 return messages;
	}
	
	private static void registerITBaja(final ByteArrayInputStream byteArrayInputStream, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt, EmployeeITPart itPart, List<String> messages) {
			try {
				Optional<Double> baseOptional = employeeIt.getDailyCgcBase();
				if(baseOptional.isEmpty()) throw new IllegalArgumentException("Falta la Base CC");
				verifyData(new Object[] { 
						 employeeIt.getRegime(), employeeIt.getCcc(), employeeIt.getNss(), baseOptional.get(), employeeIt.getQuoteDays(), 
						 employeeIt.getType(),  itPart.getDate(), employeeIt.getContractType()
				});    
	
				Double base = baseOptional.get();
				int quoteDays = employeeIt.getQuoteDays();
				float baseCtiCgc  = base.floatValue() * quoteDays;
				String regime = employeeIt.getRegime();
				String ccc = employeeIt.getCcc();
				String nss = employeeIt.getNss();
				Date date = itPart.getDate();
				Optional<Date> fATEP = Optional.of(date);
				
				Contingencies contingencie = SistemaRED.Contingencies.safeValueOf(employeeIt.getType().getValueTGSS()-1); 
		
				SituationEmployee situation = SituationEmployee.ACTIVO;
				
				Optional<AccidentType> accidentType = Optional.empty();
				Optional<String> occupation = Optional.empty();
				Optional<String> cias = itPart.getCias();
				Optional<String> itPartCollegeNumber = itPart.getCollegeNumber();
				Optional<String> collegeNumber = !itPartCollegeNumber.isEmpty() && Integer.parseInt(itPartCollegeNumber.get())>0 ?	itPart.getCollegeNumber() :	Optional.empty();
				Optional<String> job = employeeIt.getJob();
				Optional<String> jobDescription = employeeIt.getJobDescription();
	
				SistemaRED.registerITBaja(
						byteArrayInputStream.readAllBytes(), certificatePassword, certificateType, 
						regime, ccc, nss, 
						contingencie, situation,  
						date, ContractType.safeValueOf(employeeIt.getContractType().value()),
						baseCtiCgc, quoteDays, 
						fATEP, accidentType,
						collegeNumber, cias, occupation, job, jobDescription
				);
				messages.add(SUCCESS);
			} catch (SegSocialException e) {
				e.printStackTrace();
				messages.add(e.getMessage());
			}
	}
	
	private static void registerITConfirmation(final ByteArrayInputStream byteArrayInputStream, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt, EmployeeITPart itPart, List<String> messages) {
		
		try {
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
			Optional<String> npartConfimation = !confirm.isEmpty() ? Optional.of(confirm.get().intValue()+"") : Optional.empty();
			

			Contingencies contingencie = SistemaRED.Contingencies.safeValueOf(employeeIt.getType().getValueTGSS()-1); 
			SituationEmployee situation = SituationEmployee.ACTIVO;
	
			Optional<String> cias = itPart.getCias();
			Optional<String> itPartCollegeNumber = itPart.getCollegeNumber();
			Optional<String> collegeNumber = !itPartCollegeNumber.isEmpty() && Integer.parseInt(itPartCollegeNumber.get())>0 ?	itPart.getCollegeNumber() :	Optional.empty();

			SistemaRED.registerITConfirmation(byteArrayInputStream.readAllBytes(), certificatePassword, certificateType, 
					regime, ccc, nss, contingencie, situation, collegeNumber, cias, fbaja, fconfirmation, npartConfimation);
			
			messages.add(SUCCESS);
		} catch (SegSocialException e) {
			e.printStackTrace();
			messages.add(e.getMessage());
		}
	}
	
	private static void registerITAlta(final ByteArrayInputStream byteArrayInputStream, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt, EmployeeITPart itPart,  List<String> messages) {
		try {

			verifyData(new Object[] { 
					employeeIt.getRegime(), employeeIt.getCcc(), employeeIt.getNss(), employeeIt.getType(), 
					employeeIt.getDischargeCause(), employeeIt.getStartDate(), itPart.getDate() 
			});
			
			Date fbaja = employeeIt.getStartDate();
			Date falta = itPart.getDate();
			String regime = employeeIt.getRegime();
			String ccc = employeeIt.getCcc();
			String nss = employeeIt.getNss();
			
			ContractLeaveDischargeCause causeAl = employeeIt.getDischargeCause();
			
			Contingencies contingencie = SistemaRED.Contingencies.safeValueOf(employeeIt.getType().getValueTGSS()-1);
			
			CauseType causeType = SistemaRED.CauseType.safeValueOf(causeAl.value()); 
			
			SituationEmployee situation = SituationEmployee.ACTIVO;

			Optional<AccidentType> accidentType = Optional.empty();
	
			Optional<String> cias = itPart.getCias();
			Optional<String> itPartCollegeNumber = itPart.getCollegeNumber();
			Optional<String> collegeNumber = !itPartCollegeNumber.isEmpty() && Integer.parseInt(itPartCollegeNumber.get())>0 ?	itPart.getCollegeNumber() :	Optional.empty();
			Optional<Date> fATEP = Optional.empty();
			
			SistemaRED.registerITAlta(
					byteArrayInputStream.readAllBytes(), certificatePassword, certificateType, 
					regime, ccc, nss, 
					contingencie, situation, 
					fbaja, falta, 
					fATEP, accidentType, 
					causeType, collegeNumber, cias);
			
			messages.add(SUCCESS);
		} catch (SegSocialException e) {
			e.printStackTrace();
			messages.add(e.getMessage());
		}
	}

	private static void removeIt(final ByteArrayInputStream byteArrayInputStream, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt, EmployeeITPart itPart, List<String> messages) {
		try {
			String regime = employeeIt.getRegime();
			String ccc = employeeIt.getCcc();
			String nss = employeeIt.getNss();
			Date startDate = employeeIt.getStartDate();
			Date date  = itPart.getDate();
			ContractLeaveDetailType type = itPart.getType();
			
			verifyData(new Object[] { regime, ccc, nss, startDate, date, type });    
			
			PartType partType = null;
			
			switch (type) {
				case ALTA:
					partType = PartType.ALTA;
				break;
				case CONFIRMACION:
					partType = PartType.CONFIRMACION;
				break;
				default:
					partType = PartType.BAJA;
				break;
			}
		
			SistemaRED.removeIT(
					byteArrayInputStream.readAllBytes(), certificatePassword, certificateType, 
					regime, ccc, nss, partType, startDate, date
			);
			
			messages.add(SUCCESS);
		} catch (SegSocialException e) {
			e.printStackTrace();
			messages.add(e.getMessage());
		}
	}
	
	private static void removePaternity(final ByteArrayInputStream byteArrayInputStream, final String certificatePassword,
			final String certificateType, EmployeeIT employeeIt, List<String> messages) {
		try {
			String regime = employeeIt.getRegime();
			String ccc = employeeIt.getCcc();
			String nss = employeeIt.getNss();
			Date startDate = employeeIt.getStartDate();
			Optional<Date> endDate  = employeeIt.getEndDate();
	
			verifyData(new Object[] { regime, ccc, nss, startDate, endDate.get() });    
			
			SistemaRED.removePaternity(byteArrayInputStream.readAllBytes(), certificatePassword, certificateType, nss, regime, ccc, startDate, endDate.get(), Optional.empty());
		
			messages.add(SUCCESS);
		} catch (SegSocialException e) {
			e.printStackTrace();
			messages.add(e.getMessage());
		}
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public static List<CCCInfo> getCccs(Domain domain) {
		return PAYROLL.getCCCStream(domain.getName(), domain.getId(), "").filter(distinctByKey(CCCInfo::getCccAccount)).collect(Collectors.toList());
	}

	// HANDLES EMPTY DATA
	private static void verifyData(Object[] data) throws InvalidDataException {
		for (Object o : data)
			if (o == null || (o instanceof String && ((String) o).trim().equals("")))
				throw new UnfilledMandatory();
	}
}
