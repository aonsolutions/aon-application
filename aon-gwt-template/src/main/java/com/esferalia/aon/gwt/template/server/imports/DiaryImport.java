package com.esferalia.aon.gwt.template.server.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import com.esferalia.aon.gwt.template.shared.Error;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.watson.server.AonDateUtils;


public class DiaryImport {
	
	public class AccountEntryImportClass {
		private AccountEntry entry;
		private Integer line;
		
		public AccountEntryImportClass() {
			this.entry = new AccountEntry();
		}

		public AccountEntry getEntry() {
			return entry;
		}

		public void setAccount(AccountEntry entry) {
			this.entry = entry;
		}
	
		public Integer getLine() {
			return line;
		}

		public void setLine(Integer line) {
			this.line = line;
		}
	}
	
	public static DiaryImport getInstance() {
		return new DiaryImport();
	}

	public DiaryImport() {

	}
	
	HashMap<Integer, AccountEntryImportClass> diary;
	Integer asiento;
	Integer apunte;
	Boolean invoice;
	public LinkedList<AccountEntryImportClass> importation(Domain domain, String login, byte[] data){
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			importation(domain, login, rowIterator);

			return new LinkedList<AccountEntryImportClass>(diary.values());
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

	public LinkedList<AccountEntryImportClass> importationX(Domain domain, String login, byte[] data){
		XSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new XSSFWorkbook(bais);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			importation(domain, login, rowIterator);

			return new LinkedList<AccountEntryImportClass>(diary.values());
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
		rowStream.forEach(row ->{
			Iterator<Cell> cellIterator = row.cellIterator();
			Iterable<Cell> cellIterable = () -> cellIterator;
			Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
			Integer indexTitle = Utils.isAyudaT(domain.getName()) ? 3 : 0;
			invoice = false;
			if(asiento != null && diary.get(asiento).getLine() == null) {
				diary.get(asiento).setLine(row.getRowNum() + 1);
			}
			cellStream.forEach(cell -> {
				if(row.getRowNum() == indexTitle) {
					titleList.add(cell.getStringCellValue());
				} else if(row.getRowNum() > indexTitle){
					String title = titleList.get(cell.getColumnIndex());
					check(domain, login, title, cell, aonCtx);			
				}
			});
		});
	}

 	private void check(Domain domain , String login, String title, Cell cell, AonConfiguration aonCtx) {
		Object o = Utils.getObjectValue(cell);
		if(o == null) return;
		if("ASIENTO".equalsIgnoreCase(title)
				|| "Nº DIARIO".equalsIgnoreCase(title)) {
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

		if("APUNTE".equalsIgnoreCase(title)) {
			Double d = Double.parseDouble(o.toString());
			apunte = d.intValue();
			if(!apunte.equals(diary.get(asiento).getEntry().getDetails().size())) {
				diary.get(asiento).getEntry().addDetail(new AccountEntryDetail()
						.setDomain(domain.getId())
						.setLine(apunte));
			}
			return;
		}
		
		if("FECHA".equalsIgnoreCase(title)) {
			Date date = new Date();
			try{
				date = cell.getDateCellValue();
			} catch (Exception e) {
				date = AonDateUtils.parse(o.toString(), "dd/MM/yyyy");
			}
			AccountPeriod ap = ACCOUNTING.getAccountPeriod(domain.getName(), domain.getId(), login, date);
			if(ap == null) { 
				Integer year = AonDateUtils.getYear(date);
				ap = ACCOUNTING.insert(domain.getName(), domain.getId(), login, new AccountPeriod()
						.setDomain(domain.getId())
						.setName(year.toString())
						.setInitiationDate(AonDateUtils.getYearFirstDay(year))
						.setDeadline(AonDateUtils.getYearLastDay(year))
						.setStatus(AccountPeriodStatus.ACTIVE)
				);
			}
			diary.get(asiento).getEntry().setEntryDate(date);
			diary.get(asiento).getEntry().setPeriod(ap.getId());
			diary.get(asiento).getEntry().setPeriodName(ap.getName());
			diary.get(asiento).getEntry().setPeriodStatus(ap.getStatus());
			return ;
		}
		
		if("SEGURIDAD".equalsIgnoreCase(title)) {
			// TODO
			return;
		}
		
		if("ACTIVIDAD".equalsIgnoreCase(title)) {
			// TODO
			return;
		}	
		
		if("FACTURA".equalsIgnoreCase(title)) {	
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setDocumentNumber(o.toString());
			invoice = o != null && !o.toString().isBlank();
			return;
		}
		
		if("DOCUMENTO".equalsIgnoreCase(title)) {
			if(!invoice) {
				diary.get(asiento).getEntry().getDetails().get(apunte-1).setDocumentNumber(o.toString());
			}
			return;
		}
		
		if("SUBCUENTA".equalsIgnoreCase(title)
				|| "CUENTA".equalsIgnoreCase(title)) {
			String acc = CellType.NUMERIC == cell.getCellTypeEnum() ? NumberToTextConverter.toText(cell.getNumericCellValue()) : o.toString();
			String subaccount = acc.substring(0,4) + acc.substring(7);
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setAccountCode(subaccount);
			return ;
		}
		if("TITULO DE SUBCUENTA".equalsIgnoreCase(title) || "Título de Subcuenta".equalsIgnoreCase(title)
				|| "DESC. CUENTA".equalsIgnoreCase(title)) {
			String subAccountTitle = o.toString();	
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setAccountDescription(subAccountTitle);
			return;
		}
		
		if("CONTRAPARTIDA".equalsIgnoreCase(title)
				|| "CONTRAP.".equalsIgnoreCase(title)) {
			String acc = CellType.NUMERIC == cell.getCellTypeEnum() ? NumberToTextConverter.toText(cell.getNumericCellValue()) : o.toString();
			String contrapartida = acc.substring(0,4) + acc.substring(7);
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setBalancingAccountCode(contrapartida);
			return;
		}
		
		if("DESC. CONTRAP.".equalsIgnoreCase(title)) {
			String contrapartidaTitle = o.toString();
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setBalancingAccountDescription(contrapartidaTitle);
			return;
		}
		
		if("CONCEPTO".equalsIgnoreCase(title)) {
			String concept = o.toString();
			if(concept.length() > 32) {
				concept = concept.substring(0,32);
			}
			diary.get(asiento).getEntry().getDetails().get(apunte-1).setConcept(concept);
			return;
		}
		
		if("REFERENCIA".equalsIgnoreCase(title)
				|| "TIPO".equalsIgnoreCase(title)) {
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
		
		if("DEBE".equalsIgnoreCase(title)) {
			if(CellType.FORMULA == cell.getCellTypeEnum())
				return;
			Double debit = Double.parseDouble(o.toString());
			if(debit != null && debit < 0) {
				diary.get(asiento).getEntry().getDetails().get(apunte-1).setCredit(debit);
			} else diary.get(asiento).getEntry().getDetails().get(apunte-1).setDebit(debit);
			return;
		}
		
		if("HABER".equalsIgnoreCase(title)) {
			if(CellType.FORMULA == cell.getCellTypeEnum())
				return;
			Double credit = Double.parseDouble(o.toString());
			if(credit != null && credit < 0) {
				diary.get(asiento).getEntry().getDetails().get(apunte-1).setDebit(credit);
			} else diary.get(asiento).getEntry().getDetails().get(apunte-1).setCredit(credit);
			return;
		}
		
		if("COMENTARIOS".equalsIgnoreCase(title)) {
			diary.get(asiento).getEntry().setComments(o.toString());
			return;
		}
	}

	public static Error insertDiary(Domain domain, User user,LinkedList<AccountEntryImportClass> dvs) {
		Error error = new Error().setError(true);
		LinkedList<String> verror = new LinkedList<String>();
		for (AccountEntryImportClass ae : dvs) {
			try {
				for(Integer i = 0; i < ae.getEntry().getDetails().size(); i++) {
					Account acc = ACCOUNTING.getAccount(domain.getName(), domain.getId(),
						user.getLogin(), ae.getEntry().getDetails().get(i).getAccountCode());
					if(acc == null) {
						acc = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
							.setCode(ae.getEntry().getDetails().get(i).getAccountCode())
							.setDescription(ae.getEntry().getDetails().get(i).getAccountDescription())
							.setAlias("")
							.setDomain(domain.getId())
							.setActive(true));
					}
					ae.getEntry().getDetails().get(i).setAccount(acc.getId());
					if(i > 0) {
						ae.getEntry().getDetails().get(i-1).setBalancingAccount(acc.getId());
					}
				}
				if(ae.getEntry().getEntryType() == null) {
					ae.getEntry().setEntryType(AccountEntryType.MANUAL);
				}

				ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), ae.getEntry());
			} catch (Exception e) {
				error.setError(false);
				verror.add("Línea " + ae.getLine() + ": " + e.getMessage());			}
 		}
		error.setTextError(verror);
		return error;
	}
	
}
