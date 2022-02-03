package com.esferalia.aon.in.payroll.excel;

import java.util.EnumMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public enum PayrollCellStyle {
		HEADER_CELL_STYLE,
		STRING_CELL_STYLE,
		STRING_CELL_STYLE_WHITE_BACK,
		BLANK_DIFF_CELL_STYLE,
		RED_STRING_CELL_STYLE,
		DOUBLE_CELL_STYLE,
		RED_DOUBLE_CELL_STYLE,
		ORANGE_DOUBLE_CELL_STYLE,
		GREEN_DOUBLE_CELL_STYLE,
		RED_DOUBLE_CELL_STYLE_NO_BORDERS,
		ORANGE_DOUBLE_CELL_STYLE_NO_BORDERS,
		GREEN_DOUBLE_CELL_STYLE_NO_BORDERS,
		IMPORTANT_CELL_STYLE,
		RED_IMPORTANT_CELL_STYLE,
		ORANGE_IMPORTANT_CELL_STYLE,
		GREEN_IMPORTANT_CELL_STYLE,
		RED_IMPORTANT_CELL_STYLE_NO_BORDERS,
		ORANGE_IMPORTANT_CELL_STYLE_NO_BORDERS,
		GREEN_IMPORTANT_CELL_STYLE_NO_BORDERS,
		IMPORTANT_TOTAL_CELL_STYLE,
		RED_IMPORTANT_TOTAL_CELL_STYLE,
		GREEN_IMPORTANT_TOTAL_CELL_STYLE,
		ORANGE_IMPORTANT_TOTAL_CELL_STYLE,
		BOUND_CELL_STYLE_PREV,
		BOUND_CELL_STYLE_PREV_GREEN,		
		BOUND_CELL_STYLE_PREV_ORANGE,		
		BOUND_CELL_STYLE_PREV_RED,		
		FORMULA_CELL_STYLE,
		RED_FORMULA_CELL_STYLE,
		GREEN_FORMULA_CELL_STYLE,
		ORANGE_FORMULA_CELL_STYLE,		
		JOINT_CELL_STYLE,
		BORDER_RIGHT_CELL_STYLE,
		BORDER_LEFT_CELL_STYLE,
		FINAL_CELL_STYLE;
		
		private static final String DATA_FORMAT = "#,###,##0.#0";

		protected  static Map<PayrollCellStyle, CellStyle> getStyles(Workbook wb, DataFormat format) {
			Font headerFont = wb.createFont();
			headerFont.setBold(true);
			Font headerRedFont = wb.createFont();
			headerRedFont.setBold(true);
			headerRedFont.setColor(IndexedColors.RED.getIndex());
			
			Font headerGreenFont = wb.createFont();
			headerGreenFont.setBold(true);
			headerGreenFont.setColor(IndexedColors.GREEN.getIndex());
			
			Font headerOrangeFont = wb.createFont();
			headerOrangeFont.setBold(true);
			headerOrangeFont.setColor(IndexedColors.ORANGE.getIndex());

			Font wrongFont = wb.createFont();
			wrongFont.setColor(IndexedColors.RED.getIndex());
			wrongFont.setFontHeightInPoints((short) 10);
			
			Font warningFont = wb.createFont();
			warningFont.setColor(IndexedColors.ORANGE.getIndex());
			warningFont.setFontHeightInPoints((short) 10);

			Font okFont = wb.createFont();
			okFont.setColor(IndexedColors.GREEN.getIndex());
			okFont.setFontHeightInPoints((short) 10);
			
			
			//Declaring different cell styles
			Map<PayrollCellStyle, CellStyle> stylesMap = new EnumMap<>(PayrollCellStyle.class);
			
			
			CellStyle headerCellStyle = wb.createCellStyle();
			headerCellStyle.setFont(headerFont);
			headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerCellStyle.setBorderTop(BorderStyle.THIN);
			headerCellStyle.setBorderBottom(BorderStyle.THIN);
			headerCellStyle.setBorderLeft(BorderStyle.THIN);
			headerCellStyle.setBorderRight(BorderStyle.THIN);
			headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			stylesMap.put(PayrollCellStyle.HEADER_CELL_STYLE, headerCellStyle);
			
			CellStyle stringCellStyle = wb.createCellStyle();
			stringCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			stringCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			stringCellStyle.setBorderBottom(BorderStyle.THIN);
			stringCellStyle.setBorderTop(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.STRING_CELL_STYLE, stringCellStyle);
			
			CellStyle stringCellStyleWhiteBack = wb.createCellStyle();
			stringCellStyleWhiteBack.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			stringCellStyleWhiteBack.setFillPattern(FillPatternType.FINE_DOTS);
			stringCellStyleWhiteBack.setBorderBottom(BorderStyle.THIN);
			stringCellStyleWhiteBack.setBorderTop(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.STRING_CELL_STYLE_WHITE_BACK, stringCellStyleWhiteBack);
			
			CellStyle redStringCellStyle = wb.createCellStyle();
			redStringCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			redStringCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redStringCellStyle.setBorderBottom(BorderStyle.THIN);
			redStringCellStyle.setBorderTop(BorderStyle.THIN);
			redStringCellStyle.setFont(wrongFont);
			stylesMap.put(PayrollCellStyle.RED_STRING_CELL_STYLE, redStringCellStyle);
			
			CellStyle blankDiffCellStyle = wb.createCellStyle();
			blankDiffCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			blankDiffCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			stylesMap.put(PayrollCellStyle.BLANK_DIFF_CELL_STYLE, blankDiffCellStyle);
			
			CellStyle doubleCellStyle = wb.createCellStyle();
			doubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			doubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			doubleCellStyle.setBorderBottom(BorderStyle.THIN);
			doubleCellStyle.setBorderTop(BorderStyle.THIN);
			doubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.DOUBLE_CELL_STYLE, doubleCellStyle);
			
			CellStyle redDoubleCellStyle = wb.createCellStyle();
			redDoubleCellStyle.setFont(wrongFont);
			redDoubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redDoubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redDoubleCellStyle.setBorderBottom(BorderStyle.THIN);
			redDoubleCellStyle.setBorderTop(BorderStyle.THIN);
			redDoubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.RED_DOUBLE_CELL_STYLE, redDoubleCellStyle);
			
			CellStyle orangeDoubleCellStyle = wb.createCellStyle();
			orangeDoubleCellStyle.setFont(warningFont);
			orangeDoubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeDoubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			orangeDoubleCellStyle.setBorderBottom(BorderStyle.THIN);
			orangeDoubleCellStyle.setBorderTop(BorderStyle.THIN);
			orangeDoubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.ORANGE_DOUBLE_CELL_STYLE, orangeDoubleCellStyle);
			
			CellStyle greenDoubleCellStyle = wb.createCellStyle();
			greenDoubleCellStyle.setFont(okFont);
			greenDoubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenDoubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenDoubleCellStyle.setBorderBottom(BorderStyle.THIN);
			greenDoubleCellStyle.setBorderTop(BorderStyle.THIN);
			greenDoubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_DOUBLE_CELL_STYLE, greenDoubleCellStyle);
			
			CellStyle redDoubleCellStyleNoBorders = wb.createCellStyle();
			redDoubleCellStyleNoBorders.setFont(wrongFont);
			redDoubleCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redDoubleCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			redDoubleCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.RED_DOUBLE_CELL_STYLE_NO_BORDERS, redDoubleCellStyleNoBorders);
			
			
			CellStyle greenDoubleCellStyleNoBorders = wb.createCellStyle();
			greenDoubleCellStyleNoBorders.setFont(okFont);
			greenDoubleCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenDoubleCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			greenDoubleCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_DOUBLE_CELL_STYLE_NO_BORDERS, greenDoubleCellStyleNoBorders);
			
			
			CellStyle orangeDoubleCellStyleNoBorders = wb.createCellStyle();
			orangeDoubleCellStyleNoBorders.setFont(warningFont);
			orangeDoubleCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeDoubleCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			orangeDoubleCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.ORANGE_DOUBLE_CELL_STYLE_NO_BORDERS, orangeDoubleCellStyleNoBorders);
			
			CellStyle importantCellStyle = wb.createCellStyle();
			importantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			importantCellStyle.setFont(headerFont);
			importantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			importantCellStyle.setBorderBottom(BorderStyle.THIN);
			importantCellStyle.setBorderTop(BorderStyle.THIN);
			importantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.IMPORTANT_CELL_STYLE, importantCellStyle);
			
			CellStyle redImportantCellStyle = wb.createCellStyle();
			redImportantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redImportantCellStyle.setFont(headerRedFont);
			redImportantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redImportantCellStyle.setBorderBottom(BorderStyle.THIN);
			redImportantCellStyle.setBorderTop(BorderStyle.THIN);
			redImportantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.RED_IMPORTANT_CELL_STYLE, redImportantCellStyle);
			
			CellStyle orangeImportantCellStyle = wb.createCellStyle();
			orangeImportantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeImportantCellStyle.setFont(headerOrangeFont);
			orangeImportantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			orangeImportantCellStyle.setBorderBottom(BorderStyle.THIN);
			orangeImportantCellStyle.setBorderTop(BorderStyle.THIN);
			orangeImportantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.ORANGE_IMPORTANT_CELL_STYLE, orangeImportantCellStyle);
			
			CellStyle greenImportantCellStyle = wb.createCellStyle();
			greenImportantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenImportantCellStyle.setFont(headerGreenFont);
			greenImportantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenImportantCellStyle.setBorderBottom(BorderStyle.THIN);
			greenImportantCellStyle.setBorderTop(BorderStyle.THIN);
			greenImportantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_IMPORTANT_CELL_STYLE, greenImportantCellStyle);
			
			CellStyle greenImportantCellStyleNoBorders = wb.createCellStyle();
			greenImportantCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenImportantCellStyleNoBorders.setFont(headerGreenFont);
			greenImportantCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			greenImportantCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_IMPORTANT_CELL_STYLE_NO_BORDERS, greenImportantCellStyleNoBorders);
			
			CellStyle redImportantCellStyleNoBorders = wb.createCellStyle();
			redImportantCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redImportantCellStyleNoBorders.setFont(headerRedFont);
			redImportantCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			redImportantCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.RED_IMPORTANT_CELL_STYLE_NO_BORDERS, redImportantCellStyleNoBorders);
			
			CellStyle orangeImportantCellStyleNoBorders = wb.createCellStyle();
			orangeImportantCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeImportantCellStyleNoBorders.setFont(headerOrangeFont);
			orangeImportantCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			orangeImportantCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.ORANGE_IMPORTANT_CELL_STYLE_NO_BORDERS, orangeImportantCellStyleNoBorders);
			
			CellStyle importantTotalCellStyle = wb.createCellStyle();
			importantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			importantTotalCellStyle.setFont(headerFont);
			importantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			importantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			importantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			importantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.IMPORTANT_TOTAL_CELL_STYLE, importantTotalCellStyle);
			
			CellStyle redImportantTotalCellStyle = wb.createCellStyle();
			redImportantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			redImportantTotalCellStyle.setFont(headerRedFont);
			redImportantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redImportantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			redImportantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			redImportantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.RED_IMPORTANT_TOTAL_CELL_STYLE, redImportantTotalCellStyle);
			
			CellStyle greenImportantTotalCellStyle = wb.createCellStyle();
			greenImportantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			greenImportantTotalCellStyle.setFont(headerGreenFont);
			greenImportantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenImportantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			greenImportantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			greenImportantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_IMPORTANT_TOTAL_CELL_STYLE, greenImportantTotalCellStyle);
			
			CellStyle orangeImportantTotalCellStyle = wb.createCellStyle();
			orangeImportantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			orangeImportantTotalCellStyle.setFont(headerOrangeFont);
			orangeImportantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			orangeImportantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			orangeImportantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			orangeImportantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.ORANGE_IMPORTANT_TOTAL_CELL_STYLE, orangeImportantTotalCellStyle);

			CellStyle boundCellStylePrev = wb.createCellStyle();
			boundCellStylePrev.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrev.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrev.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrev.setBorderTop(BorderStyle.THIN);
			boundCellStylePrev.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BOUND_CELL_STYLE_PREV, boundCellStylePrev);

			CellStyle boundCellStylePrevGreen = wb.createCellStyle();
			boundCellStylePrevGreen.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrevGreen.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrevGreen.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrevGreen.setBorderTop(BorderStyle.THIN);
			boundCellStylePrevGreen.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BOUND_CELL_STYLE_PREV_GREEN, boundCellStylePrevGreen);		
			
			CellStyle boundCellStylePrevOrange = wb.createCellStyle();
			boundCellStylePrevOrange.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrevOrange.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrevOrange.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrevOrange.setBorderTop(BorderStyle.THIN);
			boundCellStylePrevOrange.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BOUND_CELL_STYLE_PREV_ORANGE, boundCellStylePrevOrange);		
			
			CellStyle boundCellStylePrevRed = wb.createCellStyle();
			boundCellStylePrevRed.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrevRed.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrevRed.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrevRed.setBorderTop(BorderStyle.THIN);
			boundCellStylePrevRed.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BOUND_CELL_STYLE_PREV_RED, boundCellStylePrevRed);		

			CellStyle formulaCellStyle = wb.createCellStyle();
			formulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			formulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			formulaCellStyle.setBorderBottom(BorderStyle.THIN);
			formulaCellStyle.setBorderTop(BorderStyle.THIN);
			formulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.FORMULA_CELL_STYLE, formulaCellStyle);
			
			CellStyle redFormulaCellStyle = wb.createCellStyle();
			redFormulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			redFormulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redFormulaCellStyle.setBorderBottom(BorderStyle.THIN);
			redFormulaCellStyle.setBorderTop(BorderStyle.THIN);
			redFormulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			redFormulaCellStyle.setFont(wrongFont);
			stylesMap.put(PayrollCellStyle.RED_FORMULA_CELL_STYLE, redFormulaCellStyle);
			
			CellStyle greenFormulaCellStyle = wb.createCellStyle();
			greenFormulaCellStyle.setFont(okFont);
			greenFormulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			greenFormulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenFormulaCellStyle.setBorderBottom(BorderStyle.THIN);
			greenFormulaCellStyle.setBorderTop(BorderStyle.THIN);
			greenFormulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(PayrollCellStyle.GREEN_FORMULA_CELL_STYLE, greenFormulaCellStyle);

			CellStyle yellowFormulaCellStyle = wb.createCellStyle();
			yellowFormulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			yellowFormulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			yellowFormulaCellStyle.setBorderBottom(BorderStyle.THIN);
			yellowFormulaCellStyle.setBorderTop(BorderStyle.THIN);
			yellowFormulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			yellowFormulaCellStyle.setFont(warningFont);
			stylesMap.put(PayrollCellStyle.ORANGE_FORMULA_CELL_STYLE, yellowFormulaCellStyle);
			
			CellStyle jointCellStyle = wb.createCellStyle();
			jointCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			jointCellStyle.setBorderLeft(BorderStyle.THIN);
			jointCellStyle.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.JOINT_CELL_STYLE, jointCellStyle);
			
			CellStyle borderRightCellStyle = wb.createCellStyle();
			borderRightCellStyle.setBorderRight(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BORDER_RIGHT_CELL_STYLE, borderRightCellStyle);
			
			CellStyle borderLeftCellStyle = wb.createCellStyle();
			borderLeftCellStyle.setBorderLeft(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.BORDER_LEFT_CELL_STYLE, borderLeftCellStyle);
			
			CellStyle finalCellStyle = wb.createCellStyle();
			finalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			finalCellStyle.setBorderRight(BorderStyle.THIN);
			finalCellStyle.setBorderBottom(BorderStyle.THIN);
			finalCellStyle.setBorderTop(BorderStyle.THIN);
			stylesMap.put(PayrollCellStyle.FINAL_CELL_STYLE, finalCellStyle);
			
			
			return stylesMap;
		}
	}