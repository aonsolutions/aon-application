package com.esferalia.aon.ui.pms.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.PMS;
import com.esferalia.aon.occam.api.model.pms.HotelGuestByCountry;
import com.esferalia.aon.pms.Hotel;

public class HotelGuestUtils {

	private static Integer cont;
	private static Integer cont2;

	public static void downloadHotelGuestByCountryExcel(Hotel hotel, Date date) throws IOException{
		String domainName = AonUtil.getServerName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = AonUtil.getRemoteUser();
		Integer hotelId = hotel.getId();
		
		LinkedList<HotelGuestByCountry> hgbcList = PMS.getHotelGuestByCountry(domainName, domainId, login, hotelId, date);
		
		HSSFWorkbook libro = new HSSFWorkbook();
	    ByteArrayOutputStream archivo = new ByteArrayOutputStream();
	    HSSFSheet hoja = libro.createSheet("Por Pais");
	    
	    CellStyle style = libro.createCellStyle();CellStyle styleInfo = libro.createCellStyle();
        Font font = libro.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
        style.setFont(font);styleInfo.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);styleInfo.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM); styleInfo.setBorderBottom(BorderStyle.MEDIUM);
       	styleInfo.setFillBackgroundColor(HSSFColorPredefined.LIGHT_YELLOW.getIndex());
       	
        CellStyle style2 = libro.createCellStyle();
        Font font2 = libro.createFont();
        font.setFontHeightInPoints((short)12);
		style2.setFont(font2);
		style2.setAlignment(HorizontalAlignment.RIGHT);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		
        CellStyle style3 = libro.createCellStyle();
		style3.setFont(font2);
		style3.setAlignment(HorizontalAlignment.LEFT);
		style3.setBorderBottom(BorderStyle.THIN);
		style3.setBorderRight(BorderStyle.THIN);
		style3.setBorderLeft(BorderStyle.THIN);
	
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        Row rowInfo = hoja.createRow(0);
       	rowInfo.setHeightInPoints(32);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String info = hgbcList.get(0).getHotelName() + "\n" + format.format(date) ;
        Cell cellInfo = rowInfo.createCell(0);
       	cellInfo.setCellValue(info);
       	cellInfo.setCellStyle(styleInfo);
		
       	
	    Row fila = hoja.createRow(1);
    	
		Cell celda2 = fila.createCell(0);
		celda2.setCellValue("País");
		celda2.setCellStyle(style);
    	
		Cell celda3 = fila.createCell(1);
		celda3.setCellValue("Huespedes");
		celda3.setCellStyle(style);
		cont = 1;
		hgbcList.stream().filter(r -> r.getCountry() != null).forEach(hgbc ->{
			Row row = hoja.createRow(cont+1);cont++;
	    	
			Cell cell2 = row.createCell(0);
			cell2.setCellValue(hgbc.getCountry().getName());
			cell2.setCellStyle(style3);
	    	
			Cell cell3 = row.createCell(1);
			cell3.setCellValue(hgbc.getGuestQuantity());
			cell3.setCellStyle(style2);
		});
		cont2 = 0;
		hgbcList.stream().filter(r -> r.getCountry() == null).forEach(hgbc -> cont2 = hgbc.getGuestQuantity());
		if(cont2 > 0){
			Row row = hoja.createRow(cont+1);
	    	
			Cell cell2 = row.createCell(0);
			cell2.setCellValue("Otros");
			cell2.setCellStyle(style3);
	    
			Cell cell3 = row.createCell(1);
			cell3.setCellValue(cont2);
			cell3.setCellStyle(style2);
		}
    	hoja.autoSizeColumn(0);
    	hoja.autoSizeColumn(1);
    	
    	libro.write(archivo);  
        byte[] data = archivo.toByteArray();
        archivo.close();
        libro.close();

        Integer length = data.length;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        HttpServletResponse resp = DownloadUtil.getResponse();
        resp.addHeader("Content-Disposition","attachment; filename=\"" + "HotelGuestByCountry" + ".xls" +"\"");
        resp.setContentType("application/msexcel");

        if (length > 0 && length <= Integer.MAX_VALUE);
            resp.setContentLength((int)length);
        ServletOutputStream out = resp.getOutputStream();
        resp.setBufferSize(32768);
        int bufSize = resp.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
            out.write(buffer, 0, bytes);
        
        bis.close();
        bais.close();
        out.flush();
        out.close();
	}
}
