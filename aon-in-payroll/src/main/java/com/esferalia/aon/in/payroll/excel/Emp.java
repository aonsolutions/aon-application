package com.esferalia.aon.in.payroll.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Emp {
	private String name;
    private String email;
    private Date dateOfBirth;
    private double salary;

    public Emp(String name, String email, Date dateOfBirth, double salary) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.salary = salary;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		Emp e =  new Emp("Peponsio", "peponsio@gmail.es", new Date(), 288);
		String[] columns = {"Name", "Email", "Date Of Birth", "Salary"};
		Workbook workbook = new XSSFWorkbook();
		CreationHelper createHelper = workbook.getCreationHelper();
		Sheet sheet = workbook.createSheet("Employee");
		Row headerRow = sheet.createRow(0);
		for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
		CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
        int rowNum = 1;
		Row row = sheet.createRow(1);
		row.createCell(0).setCellValue(e.getName());
		row.createCell(1).setCellValue(e.getEmail());
		Cell dateOfBirthCell = row.createCell(2);
		dateOfBirthCell.setCellValue(e.getDateOfBirth());
		dateOfBirthCell.setCellStyle(dateCellStyle);
        row.createCell(3).setCellValue(e.getSalary());
        
        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
     // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("poi-generated-file.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        
		 
	}
	
}
