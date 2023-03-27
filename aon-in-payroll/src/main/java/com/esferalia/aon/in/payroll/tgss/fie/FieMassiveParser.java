package com.esferalia.aon.in.payroll.tgss.fie;

import static com.esferalia.aon.watson.util.AonStringUtils.isBlank;
import static com.esferalia.aon.watson.util.AonStringUtils.substring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

public class FieMassiveParser {

	public static class UnknownFileException extends IOException {
		public UnknownFileException() {super();}
		public UnknownFileException(String message, Throwable cause) {super(message, cause);}
		public UnknownFileException(String message) {super(message);}
		public UnknownFileException(Throwable cause) {super(cause);}
	}

	public static void parse(File file, FieListener listener) throws FileNotFoundException, IOException {
		try(FileInputStream is = new FileInputStream(file)) {
			parse(is, listener);
		}
	}

	public static void parse(InputStream is , FieListener listener) throws IOException {	
		String prefix = "EMP";
		List<List<String>> groups = new ArrayList<>();
		List<String> currentGroup = null;

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        if (line.startsWith(prefix)) {
		            if (currentGroup != null) {
		                groups.add(currentGroup);
		            }
		            currentGroup = new ArrayList<>();
		        }
		        if (currentGroup != null) {
		            currentGroup.add(line);
		        }
		    }
		    if (currentGroup != null) {
		        groups.add(currentGroup);
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}

		//Imprimir grupos
		for (List<String> group : groups) {
			
			listener.startEnterprise();
			
		    for (String line : group) {
		    	if(line.startsWith("EMP")) {
		    		String regime = substring(line, 3, 7);
					listener.onRegime(regime);
					
					String ccc = substring(line,7, 18);
					listener.onCCC(ccc);
		    	} else if(line.startsWith("RZS")) {
		    		listener.startRZS();
					String enterpriseName = substring(line,5,60);
					listener.onEntepriseName(enterpriseName.trim());
					listener.endRZS();
		    	} else if(line.startsWith("TRA")) {
		    		listener.startEmployee();
					
					String naf = substring(line,3, 15);
					listener.onNaf(naf);
					
					String ipf = substring(line,19,33);
					listener.onIPF(ipf);
					listener.endEmployee();
		    	} else if(line.startsWith("AYN")) {
		    		listener.startNameData();
					
					String firstSurname = substring(line,3,23);
					listener.onFirstSurname(firstSurname.trim());
					
					String secondSurname = substring(line,23,43);
					listener.onSecondSurname(secondSurname.trim());
					
					String name = substring(line,43,58);
					listener.onName(name.trim());
					listener.endNameData();
		    	} else if(line.startsWith("DIT")) {
		    		listener.startDIT();
					
					String responsibleEntity = substring(line,3,6).trim();
					if(AonStringUtils.isBlank(responsibleEntity)) responsibleEntity = null;
					listener.onDitResponsibleEntity(responsibleEntity); 

					Date itStartDate = parseDateFromFie(substring(line,6,14));
					listener.onDitItStartDate(itStartDate);				
					
					String relapseStr = substring(line,14,15).trim();
					Boolean relapse = (relapseStr.toUpperCase().equals("S"));
					if(isBlank(relapseStr))
							relapse = null;
					listener.onDitRelapse(relapse);
					
					Date initialProcessDate = parseDateFromFie(substring(line,15,23));
					listener.onDitInitialProcessDate(initialProcessDate);
					
					Date lastProcessDate = parseDateFromFie(substring(line,23,31));
					listener.onDitLastProcessDate(lastProcessDate);
					
					Integer acumulatedDays = null;
					try { acumulatedDays = Integer.parseInt(substring(line,31,35));}
					catch(NumberFormatException e) {}
					listener.onDitAcumulatedDays(acumulatedDays);
					
					Date nonExistantProcessDate = parseDateFromFie(substring(line,35, 43));
					listener.onDitNonExistantProcessDate(nonExistantProcessDate);
					
					String nonExistantProcessCause = substring(line,43, 45).trim();
					if(isBlank(nonExistantProcessCause)) nonExistantProcessCause = null;
					listener.onDitNonExistantProcessCause(nonExistantProcessCause);
					
					Integer contingency = null;
					try {contingency = Integer.parseInt(substring(line,45,46));}
					catch(NumberFormatException e) {}
					listener.onDitContingency(contingency);
					
					String deficiencyIndicator = substring(line,46,47);
					if(isBlank(deficiencyIndicator)) deficiencyIndicator = null;
					listener.onDitDeficiencyIndicator(deficiencyIndicator);
					
					Integer processType = null;
					try {processType = Integer.parseInt(substring(line,47,48));}
					catch(NumberFormatException e) {}
					listener.onDitProcessType(processType);
					
					
					Integer estimatedDuration = null;
					try {estimatedDuration = Integer.parseInt(substring(line,48,52).trim());}
					catch (NumberFormatException e) {}					
					listener.onDitEstimatedDuration(estimatedDuration);
					
					Date delegatePaymentEndDate = parseDateFromFie(substring(line,52,60));
					listener.onDitDelegatePaymendEndDate(delegatePaymentEndDate);
					
					String delegatePaymentEndCause = substring(line,60,62).trim();
					if(isBlank(delegatePaymentEndCause)) delegatePaymentEndCause = null;
					listener.onDitDelegatePaymendEndCause(delegatePaymentEndCause);
					
					Date itEndDate = parseDateFromFie(substring(line,62,70));
					listener.onDitItEndDate(itEndDate);
					
					String itEndCause = substring(line,70,72);
					if(isBlank(itEndCause)) itEndCause = null;
					listener.onDitItEndCause(itEndCause);
					
					String itPartCancelStr = substring(line,72,73);
					listener.onDitItPartCancel(itPartCancelStr!=null && itPartCancelStr.equals("S"));					
					listener.endDIT();
		    	} else if(line.startsWith("ITD")) {
		    		listener.startITD();
					
					Date directPaymentStartDate = parseDateFromFie(substring(line,3,11));					
					listener.onItdDirectPaymentStartDate(directPaymentStartDate);						

					String directPaymentStartEntity = substring(line,11,14);
					listener.onItdDirectPaymentStartEntity(directPaymentStartEntity);
					
					String actualResponsibleEntity = substring(line,14,17);
					listener.onItdActualResponsibleEntity(actualResponsibleEntity);
					
					Date responsibleEntityInitialPaymentDate = parseDateFromFie(substring(line,17,25));
					listener.onItdResponsibleEntityInitialPaymentDate(responsibleEntityInitialPaymentDate);
					
					Date responsibleEntityFinalPaymentDate = parseDateFromFie(substring(line,25,33));
					listener.onItdResponsibleEntityFinalPaymentDate(responsibleEntityFinalPaymentDate);
					
					Float regulatoryBase = null;
				
					try { regulatoryBase = Float.parseFloat(substring(line,33,34)+"."+substring(line,33,35));}
					catch (NumberFormatException e){}
					listener.onItdRegulatoryBase(regulatoryBase);			
					
					listener.endITD();
		    	}  else if(line.startsWith("OIT")) {
		    		listener.startOIT();
					
					Date mcssProcessRevisionStartDate = parseDateFromFie(substring(line,3,11));			
					listener.onOitMcssProcessRevisionStartDate(mcssProcessRevisionStartDate);
					
					Date cause89Date = parseDateFromFie(substring(line,11,19));							
					listener.onOitCause89Date(cause89Date);
					
					Date cause90Date = parseDateFromFie(substring(line,19,27));	
					listener.onOitCause90Date(cause90Date);

					Date cause91Date = parseDateFromFie(substring(line,27,35));	
					listener.onOitCause91Date(cause91Date);
					
					Date cause92Date = parseDateFromFie(substring(line,35,43));	
					listener.onOitCause92Date(cause92Date);

					Boolean process170_2 = null;
					process170_2 = (substring(line,43,44).equals("S"));	
					listener.onOitProcess170_2(process170_2);
											
					Date cause42Date = parseDateFromFie(substring(line,44,52));						
					listener.onOitCause42Date(cause42Date);
					
					Date cause35Date = parseDateFromFie(substring(line,52,60));
					listener.onOitCause35Date(cause35Date);

					Date medicalCertificateDate = parseDateFromFie(substring(line,60,68));
					listener.onOitMedicalCertificateDate(medicalCertificateDate);
					
					listener.endOIT();	
		    	} else if(line.startsWith("CIT")) {
		    		listener.startCIT();
					
					Date confirmationStartDate = parseDateFromFie(substring(line,3,11));			
					listener.onCitConfirmationStartDate(confirmationStartDate);
					
					String confirmationNumberPart = substring(line,11,13);
					listener.onCitConfirmationNumberPart(confirmationNumberPart);
					
					listener.endCIT();	
		    	}
		    	
//		        System.out.println(line);
		    }
		    
		    listener.endEnterprise();
		   
//		    System.out.println();
		}
		
		 System.out.println("END");
	}
	
	//PARSE A DATE FROM FIE FILE
	private static Date parseDateFromFie(String dateStr){
		if(dateStr.equals("00000000"))		return null;
		
		SimpleDateFormat dateBuilder = new SimpleDateFormat("yyyyMMdd");
		Date d;
		
		
		try {
			d = dateBuilder.parse(dateStr);
			return d;
		} 
		catch (ParseException e) {}
		
		return null;
	}
	
	//FIND A STRING IN FILE AND RETURN LINE
	private static String find( BufferedReader reader, String head ) throws IOException {
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) 
			if ( line.startsWith(head) ) 
				return line;
		throw new UnknownFileException(String.format("Segment: '%s' Not found" ,  head));
	}	
	
	//FIND A STRING IN FILE AND RETURN LINE (optional)
	private static Optional<String> attemp( BufferedReader reader, String head ) throws IOException {
		reader.mark(1024);
		String line  ; 
		while ( ( line = reader.readLine() ) != null  )
			if ( line.startsWith(head) ) return Optional.of(line);
		
		reader.reset();
		return Optional.empty();
	}	
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		parse(new File(args[0]), new FieListener() {
			
			@Override
			public void startRZS() {System.out.println("RZS: \n{");}
			@Override
			public void startOIT() {System.out.println("OIT: \n{");}
			@Override
			public void startNameData() {System.out.println("AYN: \n{");}
			@Override
			public void startITD() {System.out.println("IDT: \n{");}
			@Override
			public void startEnterprise() {System.out.println("EMP: \n{");}
			@Override
			public void startEmployee() {System.out.println("TRA: \n{");}
			@Override
			public void startDIT() {System.out.println("DIT: \n{");}
			@Override
			public void startCIT() {System.out.println("CIT: \n{");}
			@Override
			public void onSecondSurname(String secondSurname) {System.out.println("\t SecondSurname : '" + secondSurname + "'");}
			@Override
			public void onRegime(String regime) {System.out.println("\t Regime : '" + regime + "'");}
			@Override
			public void onOitProcess170_2(Boolean process170_2) {System.out.println("\t Process170_2 : '" + process170_2 + "'");}
			@Override
			public void onOitMedicalCertificateDate(Date MedicalCertificateDate) {System.out.println("\t MedicalCertificateDate : '" + MedicalCertificateDate + "'");}
			@Override
			public void onOitMcssProcessRevisionStartDate(Date mcssProcessRevisionStartDate) {System.out.println("\t McssProcessRevisionStartDate : '" + mcssProcessRevisionStartDate + "'");}
			@Override
			public void onOitCause92Date(Date cause92Date) {System.out.println("\t Cause92Date : '" + cause92Date + "'");}
			@Override
			public void onOitCause91Date(Date cause91Date) {System.out.println("\t Cause91Date : '" + cause91Date + "'");}
			@Override
			public void onOitCause90Date(Date cause90Date) {System.out.println("\t Cause90Date : '" + cause90Date + "'");}
			@Override
			public void onOitCause89Date(Date cause89Date) {System.out.println("\t Cause89Date : '" + cause89Date + "'");}
			@Override
			public void onOitCause42Date(Date cause42Date) {System.out.println("\t Cause42Date : '" + cause42Date + "'");}
			@Override
			public void onOitCause35Date(Date cause35Date) {System.out.println("\t Cause35Date : '" + cause35Date + "'");}
			@Override
			public void onName(String name) {System.out.println("\t Name : '" + name + "'");}
			@Override
			public void onNaf(String naf) {System.out.println("\t Naf : '" + naf + "'");}
			@Override
			public void onItdResponsibleEntityInitialPaymentDate(Date responsibleEntityInitialPaymentDate) {System.out.println("\t ResponsibleEntityInitialPaymentDate : '" + responsibleEntityInitialPaymentDate + "'");}
			@Override
			public void onItdResponsibleEntityFinalPaymentDate(Date responsibleEntityFinalPaymentDate) {System.out.println("\t ResponsibleEntityFinalPaymentDate : '" + responsibleEntityFinalPaymentDate + "'");}
			@Override
			public void onItdRegulatoryBase(Float regulatoryBase) {System.out.println("\t RegulatoryBase : '" + regulatoryBase + "'");}
			@Override
			public void onItdDirectPaymentStartEntity(String directPaymentStartEntity) {System.out.println("\t DirectPaymentStartEntity : '" + directPaymentStartEntity + "'");}
			@Override
			public void onItdDirectPaymentStartDate(Date directPaymentStartDate) {System.out.println("\t DirectPaymentStartDate : '" + directPaymentStartDate + "'");}
			@Override
			public void onItdActualResponsibleEntity(String actualResponsibleEntity) {System.out.println("\t ActualResponsibleEntity : '" + actualResponsibleEntity + "'");}
			@Override
			public void onIPF(String ipf) {System.out.println("\t Ipf: '" + ipf + "'");}
			@Override
			public void onFirstSurname(String firstSurname) {System.out.println("\t FirstSurname: '" + firstSurname + "'");}
			@Override
			public void onEntepriseName(String enterpriseName) {System.out.println("\t EnterpriseName: '" + enterpriseName + "'");}
			@Override
			public void onDitResponsibleEntity(String responsibleEntity) {System.out.println("\t ResponsibleEntity: '" + responsibleEntity + "'");}
			@Override
			public void onDitRelapse(Boolean relapse) {System.out.println("\t Relapse: '" + relapse + "'");}
			@Override
			public void onDitProcessType(Integer processType) {System.out.println("\t ProcessType: '" + processType + "'");}
			@Override
			public void onDitNonExistantProcessDate(Date nonExistantProcessDate) {System.out.println("\t NonExistantProcessDate: '" + nonExistantProcessDate + "'");}
			@Override
			public void onDitNonExistantProcessCause(String nonExistantProcessCause) {System.out.println("\t NonExistantProcessCause: '" + nonExistantProcessCause + "'");}
			@Override
			public void onDitLastProcessDate(Date lastProcessDate) {System.out.println("\t LastProcessDate: '" + lastProcessDate + "'");}
			@Override
			public void onDitItStartDate(Date itStartDate) {System.out.println("\t ItStartDate: '" + itStartDate + "'");}
			@Override
			public void onDitItPartCancel(boolean itPartCancel) {System.out.println("\t ItPartCancel: '" + itPartCancel + "'");}
			@Override
			public void onDitItEndDate(Date itEndDate) {System.out.println("\t ItEndDate: '" + itEndDate + "'");}
			@Override
			public void onDitItEndCause(String itEndCause) {System.out.println("\t ItEndCause: '" + itEndCause + "'");}
			@Override
			public void onDitInitialProcessDate(Date initialProcessDate) {System.out.println("\t initialProcessDate: '" + initialProcessDate + "'");}
			@Override
			public void onDitEstimatedDuration(Integer estimatedDuration) {System.out.println("\t EstimatedDuration: '" + estimatedDuration + "'");}
			@Override
			public void onDitDelegatePaymendEndDate(Date delegatePaymentEndDate) {System.out.println("\t DelegatePaymentEndDate: '" + delegatePaymentEndDate + "'");}
			@Override
			public void onDitDelegatePaymendEndCause(String delegatePaymentEndCause) {System.out.println("\t DelegatePaymentEndCause: '" + delegatePaymentEndCause + "'");}
			@Override
			public void onDitDeficiencyIndicator(String deficiencyIndicator) {System.out.println("\t DeficiencyIndicator: '" + deficiencyIndicator + "'");}
			@Override
			public void onDitContingency(Integer contingency) {System.out.println("\t Contingency: '" + contingency + "'");}
			@Override
			public void onDitAcumulatedDays(Integer acumulatedDays) {System.out.println("\t AcumulatedDays: '" + acumulatedDays + "'");}
			@Override
			public void onCitConfirmationStartDate(Date confirmationStartDate) {System.out.println("\t ConfirmationStartDate: '" + confirmationStartDate + "'");}
			@Override
			public void onCitConfirmationNumberPart(String confirmationNumberPart) {System.out.println("\t ConfirmationNumberPart: '" + confirmationNumberPart + "'");}
			@Override
			public void onCCC(String ccc) {System.out.println("\t Ccc: '" + ccc + "'");}
			@Override
			public void endRZS() {System.out.println("}");}
			@Override
			public void endOIT() {System.out.println("}");}
			@Override
			public void endNameData() {System.out.println("}");}
			@Override
			public void endITD() {System.out.println("}");}
			@Override
			public void endCIT() {System.out.println("}");}
			@Override
			public void endEnterprise() {System.out.println("}");}
			@Override
			public void endEmployee() {System.out.println("}");}
			@Override
			public void endDIT() {System.out.println("}");}
		});
	}
}
