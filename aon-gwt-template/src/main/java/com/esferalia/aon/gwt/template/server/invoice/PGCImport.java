package com.esferalia.aon.gwt.template.server.invoice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PGCImport {

	public static PGCImport getInstance() {
		return new PGCImport();
	}

	public PGCImport() {

	}
	
	Account account; 
	
	public LinkedList<Account> importation(Domain domain, String login, byte[] data){
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);

			HSSFSheet sheet = workbook.getSheetAt(0);

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<Account> list = new LinkedList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				account = new Account().setDomain(domain.getId());
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue());
					} else {
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

	public LinkedList<Account> importationX(Domain domain, String login, byte[] data){
		XSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new XSSFWorkbook(bais);

			XSSFSheet sheet = workbook.getSheetAt(0);

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<Account> list = new LinkedList<>();
			
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				account = new Account().setDomain(domain.getId());
				
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue());
					} else {
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
		if("CODIGO".equalsIgnoreCase(title)
				|| "CÓDIGO".equalsIgnoreCase(title)) {
			account.setCode(o.toString());
			return;
		}

		if("DESCRIPCION".equalsIgnoreCase(title)
				|| "DESCRIPCIÓN".equalsIgnoreCase(title)) {
			account.setDescription(o.toString());
			return;
		}
		
		if("ALIAS".equalsIgnoreCase(title)) {
			account.setAlias(o.toString());
			return ;
		}
	}

	public static void insertPGC(Domain domain, User user,LinkedList<Account> accountList) {
		accountList.stream().sorted((a1, a2) -> a1.getCode().compareTo(a2.getCode()))
		.forEach(acc -> {
			System.out.println(acc.getCode());
			Account account = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), acc.getCode());
			if(account == null || account.getId() == null) {
				checkLowLevelAccount(domain, user, acc);
				ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), acc);
			}
		});
	}
	
	private static void checkLowLevelAccount(Domain domain, User user, Account acc) {
		int level = (byte) ((acc.getCode().length() > 4)? 5: acc.getCode().length());
		if (level>1) {
			int parentLevel = level - 1;
			String parentCode = AonStringUtils.substring(acc.getCode(),0, parentLevel);
			Account account = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), acc.getCode());
			if (account == null || account.getId() == null) {
				account = new Account()
						.setCode(parentCode)
						.setDescription(acc.getDescription())
						.setAlias(acc.getAlias());
				checkLowLevelAccount(domain, user, account);
				ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), account);
			}
		}
	}

}
