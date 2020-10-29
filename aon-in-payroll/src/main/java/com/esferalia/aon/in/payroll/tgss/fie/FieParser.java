package com.esferalia.aon.in.payroll.tgss.fie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class FieParser {

	public static class UnknownFileException extends IOException {

		public UnknownFileException() {
			super();
		}

		public UnknownFileException(String message, Throwable cause) {
			super(message, cause);
		}

		public UnknownFileException(String message) {
			super(message);
		}

		public UnknownFileException(Throwable cause) {
			super(cause);
		}

	}

	public static interface Listener {
		
		public void startEnterprise();
			public void onRegime(String regime);
			public void onCCC(String ccc);
		public void endEnterprise();
		
		public void startRZS();
			public void onEntepriseName(String enterpriseName);
		public void endRZS();
		
		public void startEmployee();
			public void onNaf(String naf);
			public void onIPF(String ipf);
		public void endEmployee();
		
		public void startNameData();
			public void onFirstSurname(String firstSurname);
			public void onSecondSurname(String secondSurname);
			public void onName(String name);
		public void endNameData();
				
		public void startDIT();
			public void onDitResponsibleEntity(String responsibleEntity); 
			public void onDitItStartDate(Date itStartDate);
			public void onDitRelapse(Boolean relapse);
			public void onDitInitialProcessDate(Date initialProcessDate);
			public void onDitLastProcessDate(Date lastProcessDate);
			public void onDitAcumulatedDays(Integer acumulatedDays);
			public void onDitNonExistantProcessDate(Date nonExistantProcessDate);
			public void onDitNonExistantProcessCause(String nonExistantProcessCause);
			public void onDitContingency(Integer contingency);
			public void onDitDeficiencyIndicator(String deficiencyIndicator);
			public void onDitProcessType(Integer processType);
			public void onDitEstimatedDuration(Integer estimatedDuration);
			public void onDitDelegatePaymendEndDate(Date delegatePaymentEndDate);
			public void onDitDelegatePaymendEndCause(String delegatePaymentEndCause);
			public void onDitItEndDate(Date itEndDate);
			public void onDitItEndCause(String itEndCause);
			public void onDitItPartCancel(Boolean itPartCancel);
		public void endDIT();
		
		public void startITD();
			public void onItdDirectPaymentStartDate(Date directPaymentStartDate);
			public void onItdDirectPaymentStartEntity(String directPaymentStartEntity);
			public void onItdActualResponsibleEntity(String actualResponsibleEntity);
			public void onItdResponsibleEntityInitialPaymentDate(Date responsibleEntityInitialPaymentDate);
			public void onItdResponsibleEntityFinalPaymentDate(Date responsibleEntityFinalPaymentDate);
			public void onItdRegulatoryBase(Float regulatoryBase);
		public void endITD();
		
		public void startOIT();
			public void onOitMcssProcessRevisionStartDate(Date mcssProcessRevisionStartDate);
			public void onOitCause89Date(Date cause89Date);
			public void onOitCause90Date(Date cause90Date);
			public void onOitCause91Date(Date cause91Date);
			public void onOitCause92Date(Date cause92Date);
			public void onOitProcess170_2(Boolean process170_2);
			public void onOitCause42Date(Date cause42Date);
			public void onOitCause35Date(Date cause35Date);
			public void onOitMedicalCertificateDate(Date MedicalCertificateDate);
		public void endOIT();

	}
	
	public static void parse(File file, Listener listener) throws FileNotFoundException, IOException {
		try(FileInputStream is = new FileInputStream(file)) {
			parse(is, listener);
		}
	}

	public static void parse(InputStream is , Listener listener) throws IOException {	
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))){
			
			// ETI ETIquetas de proceso
			String eti = find(reader, "ETI");
			
			do {
			
				// EMP Identificación de EMPpresa
				Optional<String> empl = attemp(reader, "EMP");
				String emp = empl.get();
				listener.startEnterprise();
				
				String regime = emp.substring(3, 7);
				listener.onRegime(regime);
				
				String ccc = emp.substring(7, 18);
				listener.onCCC(ccc);				
				
				// RZS RaZón Social
				String rzs = find(reader, "RZS");
				listener.startRZS();
				
				String enterpriseName = rzs.substring(5,60);
				listener.onEntepriseName(enterpriseName.trim());
				listener.endRZS();

				
				// TRA TRAbajador
				String tra = find(reader, "TRA");
				listener.startEmployee();
				
				String naf = tra.substring(3, 15);
				listener.onNaf(naf);
				
				String ipf = tra.substring(19,33);
				listener.onIPF(ipf);
				listener.endEmployee();
				
				// AYN Apellidos Y Nombre
				String ayn = find(reader, "AYN");
				listener.startNameData();
				
				String firstSurname = ayn.substring(3,23);
				listener.onFirstSurname(firstSurname.trim());
				
				String secondSurname = ayn.substring(23,43);
				listener.onSecondSurname(secondSurname.trim());
				
				String name = ayn.substring(43,58);
				listener.onName(name.trim());
				listener.endNameData();
				
				//DAF				
				//DIT
					listener.startDIT();
					String dit =  find(reader,"DIT");
					
					String responsibleEntity = dit.substring(3,6).trim();
					if(responsibleEntity.equals("")) responsibleEntity = null;
					listener.onDitResponsibleEntity(responsibleEntity); 

					Date itStartDate = parseDateFromFie(dit.substring(6,14));
					listener.onDitItStartDate(itStartDate);				
					
					String relapseStr = dit.substring(14,15).trim();
					Boolean relapse = (relapseStr.toUpperCase().equals("S"));
					if(relapseStr.equals(""))
							relapse = null;
					listener.onDitRelapse(relapse);
					
					Date initialProcessDate = parseDateFromFie(dit.substring(15,23));
					listener.onDitInitialProcessDate(initialProcessDate);
					
					Date lastProcessDate = parseDateFromFie(dit.substring(23,31));
					listener.onDitLastProcessDate(lastProcessDate);
					
					Integer acumulatedDays = null;
					try { acumulatedDays = Integer.parseInt(dit.substring(31,35));}
					catch(NumberFormatException e) {}
					listener.onDitAcumulatedDays(acumulatedDays);
					
					Date nonExistantProcessDate = parseDateFromFie(dit.substring(35, 43));
					listener.onDitNonExistantProcessDate(nonExistantProcessDate);
					
					String nonExistantProcessCause = dit.substring(43, 45).trim();
					if(nonExistantProcessCause.equals("")) nonExistantProcessCause = null;
					listener.onDitNonExistantProcessCause(nonExistantProcessCause);
					
					Integer contingency = null;
					try {contingency = Integer.parseInt(dit.substring(45,46));}
					catch(NumberFormatException e) {}
					listener.onDitContingency(contingency);
					
					String deficiencyIndicator = dit.substring(46,47);
					if(deficiencyIndicator.equals("")) deficiencyIndicator = null;
					listener.onDitDeficiencyIndicator(deficiencyIndicator);
					
					Integer processType = null;
					try {processType = Integer.parseInt(dit.substring(47,48));}
					catch(NumberFormatException e) {}
					listener.onDitProcessType(processType);
					
					
					Integer estimatedDuration = null;
					try {estimatedDuration = Integer.parseInt(dit.substring(48,52).trim());}
					catch (NumberFormatException e) {}					
					listener.onDitEstimatedDuration(estimatedDuration);
					
					Date delegatePaymentEndDate = parseDateFromFie(dit.substring(52,60));
					listener.onDitDelegatePaymendEndDate(delegatePaymentEndDate);
					
					String delegatePaymentEndCause = dit.substring(60,62).trim();
					if(delegatePaymentEndCause.equals("")) delegatePaymentEndCause = null;
					listener.onDitDelegatePaymendEndCause(delegatePaymentEndCause);
					
					Date itEndDate = parseDateFromFie(dit.substring(62,70));
					listener.onDitItEndDate(itEndDate);
					
					String itEndCause = dit.substring(70,72);
					if(itEndCause.trim().equals("")) itEndCause = null;
					listener.onDitItEndCause(itEndCause);
					
					String itPartCancelStr = dit.substring(72,73);
					Boolean itPartCancel = itPartCancelStr.equals("S");
					if(itPartCancelStr.trim().equals("")) itPartCancel = null;
					listener.onDitItPartCancel(itPartCancel);					
					listener.endDIT();

				//ITD
					Optional<String> itd =  attemp(reader,"ITD");
					if(!itd.isEmpty()) {
						listener.startITD();
						
//						Date directPaymentStartDate = parseDateFromFie(itd.get().substring(3,11));					
//						listener.onItdDirectPaymentStartDate(directPaymentStartDate);						
//						listener.onItdDirectPaymentStartEntity(directPaymentStartEntity);
//						listener.onItdActualResponsibleEntity(actualResponsibleEntity);
//						listener.onItdResponsibleEntityInitialPaymentDate(responsibleEntityInitialPaymentDate);
//						listener.onItdResponsibleEntityFinalPaymentDate(responsibleEntityFinalPaymentDate);
//						listener.onItdRegulatoryBase(regulatoryBase);			
						
						listener.endITD();	
					}
								
				//OIT
					Optional<String> oit =  attemp(reader,"OIT");
					if(!oit.isEmpty()) {
						listener.startOIT();
						
						Date mcssProcessRevisionStartDate = parseDateFromFie(oit.get().substring(3,11));			
						listener.onOitMcssProcessRevisionStartDate(mcssProcessRevisionStartDate);
						
						Date cause89Date = parseDateFromFie(oit.get().substring(11,19));							
						listener.onOitCause89Date(cause89Date);
						
						Date cause90Date = parseDateFromFie(oit.get().substring(19,27));	
						listener.onOitCause90Date(cause90Date);

						Date cause91Date = parseDateFromFie(oit.get().substring(27,35));	
						listener.onOitCause91Date(cause91Date);
						
						Date cause92Date = parseDateFromFie(oit.get().substring(35,43));	
						listener.onOitCause92Date(cause92Date);

						Boolean process170_2 = null;
						oit.get().substring(35,43);	
						listener.onOitProcess170_2(process170_2);
//						listener.onOitCause42Date(cause42Date);
//						listener.onOitCause35Date(cause35Date);
//						listener.onOitMedicalCertificateDate(MedicalCertificateDate);
						
						listener.endOIT();	
					}
		
				listener.endEnterprise();
			} while ( false );

			// ETF ETiquetas de proceso
			String etf = find(reader, "ETF");
		}
	}
	
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
	
	
	private static String find( BufferedReader reader, String head ) throws IOException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			if ( line.startsWith(head) ) {
				return line;
			}
			
		}
		
		throw new UnknownFileException(String.format("Segment: '%s' Not found" ,  head));
				
	}	
	
	private static Optional<String> attemp( BufferedReader reader, String head ) throws IOException {
		reader.mark(1024);
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			if ( line.startsWith(head) ) {
				return Optional.of(line);
			}
			
		}
		
		reader.reset();
		return Optional.empty();
				
	}	
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		parse(new File(args[0]), new Listener() {
			
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
			public void onDitItPartCancel(Boolean itPartCancel) {System.out.println("\t ItPartCancel: '" + itPartCancel + "'");}
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
			public void endEnterprise() {System.out.println("}");}
			@Override
			public void endEmployee() {System.out.println("}");}
			@Override
			public void endDIT() {System.out.println("}");}
		});
	}
	

}
