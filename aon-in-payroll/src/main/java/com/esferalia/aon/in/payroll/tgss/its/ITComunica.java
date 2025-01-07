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
import com.esferalia.aon.watson.server.AonDateUtils;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.SistemaRED.AccidentType;
import solutions.aon.seg.social.SistemaRED.Contingencies;
import solutions.aon.seg.social.SistemaRED.ContractType;
import solutions.aon.seg.social.SistemaRED.SituationEmployee;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;

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
	
	public static List<String> sendEconomicData(byte[] certificateData, String certificatePassword, String certificateType, EmployeeIT employeeIt) {
		 List<String> messages = new ArrayList<>();
		 
		 Optional<EmployeeITPart> baja = employeeIt.getItBaja();
		 if(!baja.isEmpty()) {				 
			 sendEconomicData(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, employeeIt, baja.get(), messages);
		 }

		 return messages;
	}
	
	private static void sendEconomicData(ByteArrayInputStream byteArrayInputStream, String certificatePassword, String certificateType, EmployeeIT employeeIt, EmployeeITPart itPart, List<String> messages) {
		try {
			Optional<Double> baseOptional = employeeIt.getDailyCgcBase();
			if(baseOptional.isEmpty()) throw new IllegalArgumentException("Falta la Base CC");
			verifyData(new Object[] { 
					 employeeIt.getRegime(), employeeIt.getCcc(), employeeIt.getNss(), baseOptional.get(), employeeIt.getQuoteDays(), 
					 employeeIt.getType(),  itPart.getDate(), employeeIt.getContractType()
			});    

			Double base = baseOptional.get();
			int quoteDays = employeeIt.getQuoteDays();
			float baseCtiCgc  = base.floatValue() /* * quoteDays */;
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

			SistemaRED.sendEconomicData(
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
