package com.esferalia.aon.gwt.template.server.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.gwt.template.shared.AccountEntryImportClass;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonArrayUtils;


public class DiaryImport extends ImportUtils {
		
	public static DiaryImport getInstance() {
		return new DiaryImport();
	}

	HashMap<Integer, AccountEntryImportClass> diary;
	Integer asiento;
	Integer asientoIndex;
	Integer apunte;
	Boolean invoice;
	Integer indexTitle;
	
	public LinkedList<AccountEntryImportClass> importation(Domain domain, String login, byte[] data){
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			importation(domain, login, rowIterator);

			return new LinkedList<>(diary.values());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OfficeXmlFileException e){
			return importationX(domain, login, data);
		} finally {
			if(workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public LinkedList<AccountEntryImportClass> importationX(Domain domain, String login, byte[] data){
		XSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new XSSFWorkbook(bais);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			importation(domain, login, rowIterator);

			return new LinkedList<>(diary.values());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private void importation(Domain domain, String login, Iterator<Row> rowIterator) {
		AonConfiguration aonCtx = AON.getConfiguration(domain.getName(), domain.getId(), login);
		diary = new HashMap<Integer, AccountEntryImportClass>();
		LinkedList<String> titleList = new LinkedList<>();
		Iterable<Row> rowIterable = () -> rowIterator;
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
		apunte = 0;
		indexTitle = 0;
		rowStream.forEach(row ->{
			Iterator<Cell> cellIterator = row.cellIterator();
			Iterable<Cell> cellIterable = () -> cellIterator;
			Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
			Object obj = Utils.getObjectValue(row.getCell(0));
			if(titleList.isEmpty() && ( obj == null || !AonArrayUtils.constainsIgnoreCase(IConstants.DIARY_TITLES, obj.toString().trim()))) {
				indexTitle = indexTitle + 1;
			} else if(titleList.isEmpty()) {
				indexTitle = row.getRowNum();
			}
			invoice = false;
			if(asiento != null && diary.get(asiento).getLine() == null) {
				diary.get(asiento).setLine(row.getRowNum() + 1);
			}
			cellStream.forEach(cell -> {
				if(row.getRowNum() == indexTitle) {
					String title = cell.getStringCellValue();
					titleList.add(title.trim());
					if(IConstants.ASIENTO.equalsIgnoreCase(title)
							|| IConstants.N_DIARIO.equalsIgnoreCase(title)
							|| IConstants.N_ASIENTO.equalsIgnoreCase(title)) {
						asientoIndex = cell.getColumnIndex();
					}
				} else if(row.getRowNum() > indexTitle && cell.getColumnIndex() < titleList.size()){
					if(checkAsiento(domain, row.getCell(asientoIndex), aonCtx)) {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell, aonCtx);			
					}
				}
			});
		});
	}
	
	private Boolean checkAsiento(Domain domain, Cell cell, AonConfiguration aonCtx) {
		Object o = Utils.getObjectValue(cell);
		if(o == null) {
			return false;
		}
		Double d = Utils.parseDouble(o);
		asiento = d.intValue();
		if(!diary.containsKey(asiento)) {
			apunte = 0;
			EnterpriseActivity ea = aonCtx.getMainActivity();	
			AccountEntryImportClass aeic = new AccountEntryImportClass();
			aeic.getEntry()
				.setDomain(domain.getId())
				.setActivity(ea==null ? null : ea.getId());
			diary.put(asiento, aeic);
		}
		return true;
	}

 	private void check(Domain domain , String login, String title, Cell cell, AonConfiguration aonCtx) {
 		Object o = Utils.getObjectValue(cell);
		if(o == null) return;
		if(isAsiento(title)) {
			Double d = Double.parseDouble(o.toString());
			asiento = d.intValue();
			if(!diary.containsKey(asiento)) {
				apunte = 1;
				EnterpriseActivity ea = aonCtx.getMainActivity();	
				AccountEntryImportClass aeic = new AccountEntryImportClass();
				aeic.getEntry()
					.setDomain(domain.getId())
					.setActivity(ea==null ? null : ea.getId())
					.addDetail(new AccountEntryDetail()
						.setDomain(domain.getId())
						.setLine(apunte));
				diary.put(asiento, aeic);
			} else {
				diary.get(asiento).getEntry().addDetail(
						new AccountEntryDetail()
						.setDomain(domain.getId())
						.setLine(apunte++));	
			}
			return;
		}

		if(isApunte(title)) {
			Double d = Utils.parseDouble(o);
			apunte = d.intValue();
			if(!apunte.equals(diary.get(asiento).getEntry().getDetails().size())) {
				diary.get(asiento).getEntry().addDetail(new AccountEntryDetail()
						.setDomain(domain.getId())
						.setLine(apunte));
			}
			return;
		}
		
		if(isDate(title)) {
			Date date = new Date();
			try{
				date = cell.getDateCellValue();
			} catch (Exception e) {
				date = AonDateUtils.parse(o.toString(), "dd/MM/yyyy");
			}
//			AccountPeriod ap = ACCOUNTING.getAccountPeriod(domain.getName(), domain.getId(), login, date);
//			if(ap == null) { 
//				Integer year = AonDateUtils.getYear(date);
//				ap = ACCOUNTING.save(domain.getName(), domain.getId(), login, new AccountPeriod()
//						.setDomain(domain.getId())
//						.setName(year.toString())
//						.setInitiationDate(AonDateUtils.getYearFirstDay(year))
//						.setDeadline(AonDateUtils.getYearLastDay(year))
//						.setStatus(AccountPeriodStatus.ACTIVE)
//				);
//			}
			diary.get(asiento).getEntry().setEntryDate(date);
//			diary.get(asiento).getEntry().setPeriod(ap.getId());
//			diary.get(asiento).getEntry().setPeriodName(ap.getName());
//			diary.get(asiento).getEntry().setPeriodStatus(ap.getStatus());
			return ;
		}
		
		if(isSecurity(title)) {
			// TODO
			return;
		}
		
		if(isActivity(title)) {
			// TODO
			return;
		}	
		
		if(isInvoice(title)) {	
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setDocumentNumber(
					CellType.NUMERIC == cell.getCellTypeEnum() 
						? NumberToTextConverter.toText(cell.getNumericCellValue()) 
						: o.toString());
			invoice = o != null && !o.toString().isBlank();
			return;
		}
		
		if(isDocument(title)) {
			if(!invoice) {
				diary.get(asiento).getEntry().getDetails().get(apunte-1).setDocumentNumber(
					CellType.NUMERIC == cell.getCellTypeEnum() 
						? NumberToTextConverter.toText(cell.getNumericCellValue()) 
						: o.toString());
			}
			return;
		}
		
		if(isAccount(title)) {
			String acc = CellType.NUMERIC == cell.getCellTypeEnum() ? NumberToTextConverter.toText(cell.getNumericCellValue()) : o.toString();
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setAccountCode(Utils.calculateAccount(acc));
			return ;
		}
		if(isAccountDescription(title)) {
			String subAccountTitle = o.toString();	
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setAccountDescription(subAccountTitle);
			return;
		}
		
		if(isContrapartida(title)) {
			String acc = CellType.NUMERIC == cell.getCellTypeEnum() ? NumberToTextConverter.toText(cell.getNumericCellValue()) : o.toString();
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setBalancingAccountCode(Utils.calculateAccount(acc));
			return;
		}
		
		if(isContrapartidaDescription(title)) {
			String contrapartidaTitle = o.toString();
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setBalancingAccountDescription(contrapartidaTitle);
			return;
		}
		
		if(isConcept(title)) {
			String concept = o.toString();
			if(concept.length() > 32) {
				concept = concept.substring(0,32);
			}
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setConcept(concept);
			return;
		}
		
		if(isReference(title)) {
			diary.get(asiento).getEntry().setEntryType(AccountEntryType.safeValueOf(o.toString()));	
			if(diary.get(asiento).getEntry().getEntryType() == null) {
				if("&AP".equals(o.toString())) {
					diary.get(asiento).getEntry().setEntryType(AccountEntryType.OPENING); 
				} else if("&CR".equals(o.toString())) {
					diary.get(asiento).getEntry().setEntryType(AccountEntryType.CLOSING);
				} else diary.get(asiento).getEntry().setEntryType(AccountEntryType.MANUAL);
			}
			return;
		}
		
		if(isDebe(title)) {
			Double debit = Utils.parseDouble(o);
			Double actDebit = diary.get(asiento).getEntry().getDetails().get(apunte-1).getDebit();
			if(debit != null && debit < 0) {
				diary.get(asiento).getEntry().getDetails().get(apunte-1).setCredit(-debit);
			} else if(debit != null && actDebit == 0.0) diary.get(asiento).getEntry().getDetails().get(apunte-1).setDebit(debit);
			return;
		}
		
		if(isHaber(title)) {
			Double credit = Utils.parseDouble(o);
			Double actCredit = diary.get(asiento).getEntry().getDetails().get(apunte-1).getCredit();
			if(credit != null && credit < 0) {
				diary.get(asiento).getEntry().getDetails().get(apunte-1).setDebit(-credit);
			} else if(credit != null && actCredit == 0.0) diary.get(asiento).getEntry().getDetails().get(apunte-1).setCredit(credit);
			return;
		}
		
		if(isComments(title)) {
			diary.get(asiento).getEntry().setComments(o.toString());
			return;
		}
	}
 	
 	private boolean isAsiento(String value) {
 		return compare(value, IConstants.ASIENTO, IConstants.N_DIARIO, IConstants.N_ASIENTO);
 	}
 	
 	private boolean isApunte(String value) {
 		return compare(value, IConstants.APUNTE, IConstants.N_APUNTE);
 	}
 	
 	private boolean isDate(String value) {
 		return compare(value, IConstants.FECHA);
 	}
 	
 	private boolean isSecurity(String value) {
 		return compare(value, IConstants.SEGURIDAD);
 	}
 	
 	private boolean isActivity(String value) {
 		return compare(value, IConstants.ACTIVIDAD);
 	}
 	
 	private boolean isInvoice(String value) {
 		return compare(value, IConstants.FACTURA);
 	}
 	
 	private boolean isDocument(String value) {
 		return compare(value, IConstants.DOCUMENTO);
 	}
 	
 	private boolean isAccount(String value) {
 		return compare(value, IConstants.SUBCUENTA, IConstants.CUENTA);
 	}
 	
 	private boolean isContrapartida(String value) {
 		return compare(IConstants.CONTRAPARTIDA, IConstants.CONTRAP);
 	}
 	
 	private boolean isContrapartidaDescription(String value) {
 		return compare(value, IConstants.DESC_CONTRAP, IConstants.DESCRIPCION_CONTRAPARTIDA);
	}

 	private boolean isConcept(String value) {
 		return compare(value, IConstants.CONCEPTO);
 	}
 	
 	private boolean isAccountDescription(String value) {
		return compare(value, IConstants.TITULO_DE_SUBCUENTA, IConstants.TITULO_DE_SUBCUENTA2,
				IConstants.DESC_CUENTA, IConstants.DESCRIPCION_CUENTA);
 	}
 	
 	private boolean isReference(String value) {
 		return compare(value, IConstants.REFERENCIA, IConstants.TIPO);
 	}
 	
 	private boolean isDebe(String value) {
 		return compare(value, IConstants.DEBE);
 	}
 	
 	private boolean isHaber(String value) {
 		return compare(value, IConstants.HABER);
 	}
 	
 	private boolean isComments(String value) {
 		return compare(value, IConstants.COMENTARIOS);
 	}
 	
	public static Error insertDiary(Domain domain, User user, Integer index, LinkedList<AccountEntryImportClass> dvs) {
		Error error = new Error().setError(true);
		
		if(index >= dvs.size()) {
			error.setLine(index);
			return error;
		}

		AccountEntryImportClass ae = dvs.get(index);
		return insertDiary(domain, user, index, ae);
	}
	
	public static Error insertDiary(Domain domain, User user, Integer index, AccountEntryImportClass ae) {
		Error error = new Error().setError(true);
		
		try {
			
			AccountPeriod ap = ACCOUNTING.getAccountPeriod(domain.getName(), domain.getId(), "", ae.getEntry().getEntryDate());
			if(ap == null) { 
				Integer year = AonDateUtils.getYear(ae.getEntry().getEntryDate());
				ap = ACCOUNTING.save(domain.getName(), domain.getId(), "", new AccountPeriod()
						.setDomain(domain.getId())
						.setName(year.toString())
						.setInitiationDate(AonDateUtils.getYearFirstDay(year))
						.setDeadline(AonDateUtils.getYearLastDay(year))
						.setStatus(AccountPeriodStatus.ACTIVE)
				);
			}
			ae.getEntry().setPeriod(ap.getId());
			ae.getEntry().setPeriodName(ap.getName());
			ae.getEntry().setPeriodStatus(ap.getStatus());
			
			for(Integer i = 0; i < ae.getEntry().getDetails().size(); i++) {
				Account acc = ACCOUNTING.getAccount(domain.getName(), domain.getId(),
				user.getLogin(), ae.getEntry().getDetails().get(i).getAccountCode());
				if(acc == null) {
					Occam occam = new Occam()
									.setDomain(domain.getId())
									.setDomainName(domain.getName())
									.setUser(user.getLogin());
					
					Account account = new Account()
							.setCode(ae.getEntry().getDetails().get(i).getAccountCode())
							.setDescription(ae.getEntry().getDetails().get(i).getAccountDescription())
							.setAlias("")
							.setDomain(domain.getId())
							.setActive(true);
					
					List<Account> lowLevels = ACCOUNTING.generateLowerLevels(occam, account, 3);
					if (!lowLevels.isEmpty()) {
						LinkedList<String> warnList = new LinkedList<>();
						warnList.add("Se autogeneraron las siguientes cuentas:");
						lowLevels.forEach(ll -> warnList.add(ll.getCode()));
						error.setTextWarning(warnList);
						error.setError(true);
					}
					
					acc = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), account);
				}
				ae.getEntry().getDetails().get(i).setAccount(acc.getId());
				if(i > 0) {
				//	ae.getEntry().getDetails().get(i-1).setBalancingAccount(acc.getId());
				}
			}
			if(ae.getEntry().getEntryType() == null) {
				ae.getEntry().setEntryType(AccountEntryType.MANUAL);
			}
			if(ae.getEntry().getDetails().size() > 0) {
				String docNumber = ae.getEntry().getDetails().get(0).getDocumentNumber();
				
				Invoice invoice = AON.getInvoice(domain.getName(), domain.getId(), user.getLogin(), f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getReferenceCodeProperty().eq(docNumber)));
				if(invoice != null && invoice.getId() != null) {
					AccountingInvoice ai = ACCOUNTING.getAccountingInvoiceFromInvoice(domain.getName(), domain.getId(), user.getLogin(), invoice.getId());
					if(ai != null) {
						throw new Exception("Ya existe un asiento de la factura " + invoice.getReferenceCode());
					}
				}
			}
		
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), ae.getEntry());	
		} catch (Exception e) {
			e.printStackTrace();
			error.setError(false);
			error.setTextError("Línea " + ae.getLine() + ": " + e.getMessage());
 		}
		error.setLine(index);
		return error;
	}
	
}
