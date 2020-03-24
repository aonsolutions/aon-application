package com.esferalia.aon.gwt.template.server.invoice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
	
	
	
	public static DiaryImport getInstance() {
		return new DiaryImport();
	}

	public DiaryImport() {

	}
	
	HashMap<Integer, AccountEntry> diary;
	Integer asiento;
	Integer apunte;
	Boolean invoice;
	public LinkedList<AccountEntry> importation(Domain domain, String login, byte[] data){
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			importation(domain, login, rowIterator);

			return new LinkedList<AccountEntry>(diary.values());
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

	public LinkedList<AccountEntry> importationX(Domain domain, String login, byte[] data){
		XSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new XSSFWorkbook(bais);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			importation(domain, login, rowIterator);

			return new LinkedList<AccountEntry>(diary.values());
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
		diary = new HashMap<Integer, AccountEntry>();
		LinkedList<String> titleList = new LinkedList<>();
		Iterable<Row> rowIterable = () -> rowIterator;
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
		apunte = 0;
		rowStream.forEach(row ->{
			Iterator<Cell> cellIterator = row.cellIterator();
			Iterable<Cell> cellIterable = () -> cellIterator;
			Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
			Integer indexTitle = Utils.isAyudaT(domain.getName()) ? 3 : 0;
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
				diary.put(asiento, new AccountEntry()
						.setDomain(domain.getId())
						.setActivity(ea==null ? null : ea.getId())
						.addDetail(new AccountEntryDetail()
								.setDomain(domain.getId())
								.setLine(apunte))
						);
			} else {
				diary.get(asiento).addDetail(
						new AccountEntryDetail()
						.setDomain(domain.getId())
						.setLine(apunte++));	
			}
			return;
		}

		if("APUNTE".equalsIgnoreCase(title)) {
			Double d = Double.parseDouble(o.toString());
			apunte = d.intValue();
			if(!apunte.equals(diary.get(asiento).getDetails().size())) {
				diary.get(asiento).addDetail(new AccountEntryDetail()
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
			diary.get(asiento).setEntryDate(date);
			diary.get(asiento).setPeriod(ap.getId());
			diary.get(asiento).setPeriodName(ap.getName());
			diary.get(asiento).setPeriodStatus(ap.getStatus());
			return ;
		}
		
		if("TIPO".equalsIgnoreCase(title)) {
			diary.get(asiento).setEntryType(AccountEntryType.safeValueOf(o.toString()));
			return;
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
			invoice = o != null && !"".equals(o.toString()) && !" ".equals(o.toString());
			return;
		}
		
		if("DOCUMENTO".equalsIgnoreCase(title)) {
			// TODO
			return;
		}
		
		if("SUBCUENTA".equalsIgnoreCase(title)
				|| "CUENTA".equalsIgnoreCase(title)) {	
			String acc = CellType.NUMERIC == cell.getCellTypeEnum() ? NumberToTextConverter.toText(cell.getNumericCellValue()) : o.toString();
			String subaccount = acc.substring(0,4) + acc.substring(7);
			diary.get(asiento).getDetails().get(apunte-1).setAccountCode(subaccount);
			return ;
		}
		if("TITULO DE SUBCUENTA".equalsIgnoreCase(title) || "Título de Subcuenta".equalsIgnoreCase(title)
				|| "DESC. CUENTA".equalsIgnoreCase(title)) {
			String subAccountTitle = o.toString();	
			diary.get(asiento).getDetails().get(apunte-1).setAccountDescription(subAccountTitle);
			return;
		}
		
		if("CONTRAPARTIDA".equalsIgnoreCase(title)
				|| "CONTRAP.".equalsIgnoreCase(title)) {
			String acc = CellType.NUMERIC == cell.getCellTypeEnum() ? NumberToTextConverter.toText(cell.getNumericCellValue()) : o.toString();
			String contrapartida = acc.substring(0,4) + acc.substring(7);
			diary.get(asiento).getDetails().get(apunte-1).setBalancingAccountCode(contrapartida);
			return;
		}
		
		if("DESC. CONTRAP.".equalsIgnoreCase(title)) {
			String contrapartidaTitle = o.toString();
			diary.get(asiento).getDetails().get(apunte-1).setBalancingAccountDescription(contrapartidaTitle);
			return;
		}
		
		if("CONCEPTO".equalsIgnoreCase(title)) {
			String concept = o.toString();
			if(concept.length() > 32) {
				concept = concept.substring(0,32);
			}
			diary.get(asiento).getDetails().get(apunte-1).setConcept(concept);
			return;
		}
		
		if("REFERENCIA".equalsIgnoreCase(title)) {
			if(diary.get(asiento).getEntryType() == null) {
				if("&AP".equals(o.toString())) {
					diary.get(asiento).setEntryType(AccountEntryType.OPENING); 
				} else if("&CR".equals(o.toString())) {
					diary.get(asiento).setEntryType(AccountEntryType.CLOSING);
				} else diary.get(asiento).setEntryType(AccountEntryType.MANUAL);
			}
			return;
		}
		
		if("DEBE".equalsIgnoreCase(title)) {
			if(CellType.FORMULA == cell.getCellTypeEnum())
				return;
			Double debit = Double.parseDouble(o.toString());
			if(debit != null && debit < 0) {
				diary.get(asiento).getDetails().get(apunte-1).setCredit(debit);
			} else diary.get(asiento).getDetails().get(apunte-1).setDebit(debit);
			return;
		}
		
		if("HABER".equalsIgnoreCase(title)) {
			if(CellType.FORMULA == cell.getCellTypeEnum())
				return;
			Double credit = Double.parseDouble(o.toString());
			if(credit != null && credit < 0) {
				diary.get(asiento).getDetails().get(apunte-1).setDebit(credit);
			} else diary.get(asiento).getDetails().get(apunte-1).setCredit(credit);
			return;
		}
		
		if("COMENTARIOS".equalsIgnoreCase(title)) {
			diary.get(asiento).setComments(o.toString());
			return;
		}
	}

	public static void insertDiary(Domain domain, User user,LinkedList<AccountEntry> dvs) {
		for (AccountEntry ae : dvs) {
			for(Integer i = 0; i < ae.getDetails().size(); i++) {
				Account acc = ACCOUNTING.getAccount(domain.getName(), domain.getId(),
						user.getLogin(), ae.getDetails().get(i).getAccountCode());
				if(acc == null) {
					acc = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
							.setCode(ae.getDetails().get(i).getAccountCode())
							.setDescription(ae.getDetails().get(i).getAccountDescription())
							.setAlias("")
							.setDomain(domain.getId())
							.setActive(true));
				}
				ae.getDetails().get(i).setAccount(acc.getId());
				if(i > 0) {
					ae.getDetails().get(i-1).setBalancingAccount(acc.getId());
				}
			}
			if(ae.getEntryType() == null) {
				ae.setEntryType(AccountEntryType.MANUAL);
			}

			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), ae);
		}
	}
	
}
