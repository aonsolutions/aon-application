package com.esferalia.aon.gwt.template.server.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;
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
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PGCImport {

	public class AccountImportClass {
		private Account account;
		private Integer line;
		
		public AccountImportClass() {
			this.account = new Account()
					.setActive(true);
		}

		public Account getAccount() {
			return account;
		}

		public void setAccount(Account account) {
			this.account = account;
		}
	
		public Integer getLine() {
			return line;
		}

		public void setLine(Integer line) {
			this.line = line;
		}
	}
	
	public static PGCImport getInstance() {
		return new PGCImport();
	}

	public PGCImport() {

	}
	
	AccountImportClass account; 
	
	public LinkedList<AccountImportClass> importation(Domain domain, String login, byte[] data){
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);

			HSSFSheet sheet = workbook.getSheetAt(0);

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<AccountImportClass> list = new LinkedList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				account = new AccountImportClass();
				account.getAccount().setDomain(domain.getId());
				account.setLine(row.getRowNum() + 1);
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue().trim());
					} else if(cell.getColumnIndex() < titleList.size()) {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell);			
					}
				});
				if(row.getRowNum() != 0) {
					list.add(account);
				}
			});
			return list.stream().sorted((a1, a2) -> a1.getAccount().getCode().compareTo(a2.getAccount().getCode()))
					.collect(Collectors.toCollection(LinkedList::new));
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

	public LinkedList<AccountImportClass> importationX(Domain domain, String login, byte[] data){
		XSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new XSSFWorkbook(bais);

			XSSFSheet sheet = workbook.getSheetAt(0);

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<AccountImportClass> list = new LinkedList<>();
			
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				account = new AccountImportClass();
				account.getAccount().setDomain(domain.getId());
				account.setLine(row.getRowNum() + 1);				
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue().trim());
					} else if(cell.getColumnIndex() < titleList.size()) {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell);			
					}
				});
				if(row.getRowNum() != 0) {
					list.add(account);
				}
			});

			return list;
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

 	private void check(Domain domain , String login, String title, Cell cell) {
 		Object o = Utils.getObjectValue(cell);
		if(o == null) return;	
		if("CODIGO".equalsIgnoreCase(title) || "CÓDIGO".equalsIgnoreCase(title)) {
			String acc = CellType.NUMERIC == cell.getCellTypeEnum() ? NumberToTextConverter.toText(cell.getNumericCellValue()) : o.toString();
			account.getAccount().setCode(Utils.calculateAccount(acc));
			return;
		}

		if("DESCRIPCION".equalsIgnoreCase(title)
				|| "DESCRIPCIÓN".equalsIgnoreCase(title)) {
			account.getAccount().setDescription(o.toString());
			return;
		}
		
		if("ALIAS".equalsIgnoreCase(title)) {
			account.getAccount().setAlias(o.toString());
			return ;
		}
	}

	public static Error insertPGC(Domain domain, User user, Integer index, LinkedList<AccountImportClass> accountList) {
		Error error = new Error().setError(true);
		if(index >= accountList.size()) {
			error.setLine(index);
			return error;
		}
		AccountImportClass acc = accountList.get(index);
		try {
			Account account = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), acc.getAccount().getCode());
			if(account == null || account.getId() == null) {
				checkLowLevelAccount(domain, user, acc.getAccount());
				ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), acc.getAccount());
			}
		} catch (Exception e) {
			error.setError(false);
			error.setTextError("Línea " + acc.getLine() + ": " + e.getMessage());
		}
	
		error.setLine(index);
		return error;
	}
	
	private static void checkLowLevelAccount(Domain domain, User user, Account acc) {
		int level = (byte) ((acc.getCode().length() > 4)? 5: acc.getCode().length());
		if (level>1) {
			int parentLevel = level - 1;
			String parentCode = AonStringUtils.substring(acc.getCode(),0, parentLevel);
			Account account = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), parentCode);
			if (account == null || account.getId() == null) {
				account = new Account()
						.setDomain(domain.getId())
						.setCode(parentCode)
						.setDescription(acc.getDescription())
						.setAlias(acc.getAlias())
						.setActive(true);
				checkLowLevelAccount(domain, user, account);
				ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), account);
			}
		}
	}

}
