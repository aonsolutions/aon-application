package com.code.aon.report.dynamic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.StatusLightCondition;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.report.ReportException;

public class DynaElements {
	
	public static final String DEFAULT_FONT_NAME = "SansSerif";
	public static final int DETAIL_FONT_SIZE = 7;
	public static final int HEADER_FONT_SIZE = 8;
	public static final String DATE_PATTERN = "dd/MM/yyyy";
	public static final String NUMBER_PATTERN = "#,##0.00";
	public static final String INTEGER_PATTERN = "#,##0";
	public static final int DATE_DEFAULT_WIDTH = 50;
	public static final int NUMBER_DEFAULT_WIDTH = 78;
	
	public static final Font DETAIL_FONT = new Font();  
	static {
		DETAIL_FONT.setFontName(DEFAULT_FONT_NAME);
		DETAIL_FONT.setFontSize(DETAIL_FONT_SIZE);
	}

	public static final Font DETAIL_BOLD_FONT = new Font();
	static {
		DETAIL_BOLD_FONT.setFontName(DEFAULT_FONT_NAME);
		DETAIL_BOLD_FONT.setFontSize(DETAIL_FONT_SIZE);
		DETAIL_BOLD_FONT.setBold(true);
	}
	
	public static final Font HEADER_FONT = new Font();
	static {
		HEADER_FONT.setFontName(DEFAULT_FONT_NAME);
		HEADER_FONT.setFontSize(HEADER_FONT_SIZE);
		HEADER_FONT.setBold(true);
	}

	public static final Style COLUMN_HEADER_STYLE = new Style();
	static {
		COLUMN_HEADER_STYLE.setFont(HEADER_FONT);
		COLUMN_HEADER_STYLE.setBorderBottom(Border.PEN_1_POINT());
		COLUMN_HEADER_STYLE.setHorizontalAlign(HorizontalAlign.CENTER);
		COLUMN_HEADER_STYLE.setVerticalAlign(VerticalAlign.MIDDLE);
		COLUMN_HEADER_STYLE.setBackgroundColor(Color.WHITE);
		COLUMN_HEADER_STYLE.setTextColor(Color.BLACK);
	}

	public static Style DETAIL_STYLE = new Style();
	static {
		DETAIL_STYLE.setFont(DETAIL_FONT);
	}
	
	public static final Style DETAIL_BOLD_STYLE = new Style();
	static {
		DETAIL_BOLD_STYLE.setFont(DETAIL_BOLD_FONT);
	}
	
	public static final Style DETAIL_DATE_STYLE = new Style();
	static {
		DETAIL_DATE_STYLE.setFont(DETAIL_FONT);
		DETAIL_DATE_STYLE.setPattern(DATE_PATTERN);
	}
	
	public static final Style DETAIL_INTEGER_STYLE = new Style();
	static {
		DETAIL_INTEGER_STYLE.setFont(DETAIL_FONT);
		DETAIL_INTEGER_STYLE.setHorizontalAlign(HorizontalAlign.RIGHT);
		DETAIL_INTEGER_STYLE.setPattern(INTEGER_PATTERN);
	}

	public static final Style DETAIL_NUMBER_STYLE = new Style();
	static {
		DETAIL_NUMBER_STYLE.setFont(DETAIL_FONT);
		DETAIL_NUMBER_STYLE.setHorizontalAlign(HorizontalAlign.RIGHT);
		DETAIL_NUMBER_STYLE.setPattern(NUMBER_PATTERN);
	}
	
	public static final Style DETAIL_NUMBER_RED_STYLE = new Style();
	static {
		DETAIL_NUMBER_RED_STYLE.setFont(DETAIL_FONT);
		DETAIL_NUMBER_RED_STYLE.setHorizontalAlign(HorizontalAlign.RIGHT);
		DETAIL_NUMBER_RED_STYLE.setPattern(NUMBER_PATTERN);
		DETAIL_NUMBER_RED_STYLE.setTextColor(Color.RED);
	}

	public static final Style DETAIL_NUMBER_BLUE_STYLE = new Style();
	static {
		DETAIL_NUMBER_BLUE_STYLE.setFont(DETAIL_FONT);
		DETAIL_NUMBER_BLUE_STYLE.setHorizontalAlign(HorizontalAlign.RIGHT);
		DETAIL_NUMBER_BLUE_STYLE.setPattern(NUMBER_PATTERN);
		DETAIL_NUMBER_BLUE_STYLE.setTextColor(Color.BLUE);
	}
	
	public static final Style DETAIL_NUMBER_BOLD_STYLE = new Style();
	static {
		DETAIL_NUMBER_BOLD_STYLE.setFont(DETAIL_BOLD_FONT);
		DETAIL_NUMBER_BOLD_STYLE.setHorizontalAlign(HorizontalAlign.RIGHT);
		DETAIL_NUMBER_BOLD_STYLE.setPattern(NUMBER_PATTERN);
	}
	
	public static final Style DETAIL_NUMBER_RED_BOLD_STYLE = new Style();
	static {
		DETAIL_NUMBER_RED_BOLD_STYLE.setFont(DETAIL_BOLD_FONT);
		DETAIL_NUMBER_RED_BOLD_STYLE.setHorizontalAlign(HorizontalAlign.RIGHT);
		DETAIL_NUMBER_RED_BOLD_STYLE.setPattern(NUMBER_PATTERN);
		DETAIL_NUMBER_RED_BOLD_STYLE.setTextColor(Color.RED);
	}

	public static final Style DETAIL_NUMBER_BLUE_BOLD_STYLE = new Style();
	static {
		DETAIL_NUMBER_BLUE_BOLD_STYLE.setFont(DETAIL_BOLD_FONT);
		DETAIL_NUMBER_BLUE_BOLD_STYLE.setHorizontalAlign(HorizontalAlign.RIGHT);
		DETAIL_NUMBER_BLUE_BOLD_STYLE.setPattern(NUMBER_PATTERN);
		DETAIL_NUMBER_BLUE_BOLD_STYLE.setTextColor(Color.BLUE);
	}

	public static final StatusLightCondition POSITIVE_CONDITION = new StatusLightCondition(null, new Double(0));
	public static final StatusLightCondition NEGATIVE_CONDITION  = new StatusLightCondition(new Double(0),null);
	
	public static final ArrayList<ConditionalStyle> DETAIL_NUMBER_BLUE_NORMAL_CONDITIONAL_STYLES = new ArrayList<ConditionalStyle>();
	static {
		DETAIL_NUMBER_BLUE_NORMAL_CONDITIONAL_STYLES.add(new ConditionalStyle(POSITIVE_CONDITION,DETAIL_NUMBER_BLUE_STYLE));
		DETAIL_NUMBER_BLUE_NORMAL_CONDITIONAL_STYLES.add(new ConditionalStyle(NEGATIVE_CONDITION,DETAIL_NUMBER_STYLE));
	}
	
	public static final ArrayList<ConditionalStyle> DETAIL_NUMBER_RED_BLUE_BOLD_CONDITIONAL_STYLES = new ArrayList<ConditionalStyle>();
	static {
		DETAIL_NUMBER_RED_BLUE_BOLD_CONDITIONAL_STYLES.add(new ConditionalStyle(NEGATIVE_CONDITION,DETAIL_NUMBER_RED_BOLD_STYLE));
		DETAIL_NUMBER_RED_BLUE_BOLD_CONDITIONAL_STYLES.add(new ConditionalStyle(POSITIVE_CONDITION,DETAIL_NUMBER_BLUE_BOLD_STYLE));
	}
	
	public static final ArrayList<ConditionalStyle> DETAIL_NUMBER_RED_BLUE_CONDITIONAL_STYLES = new ArrayList<ConditionalStyle>();
	static {
		DETAIL_NUMBER_RED_BLUE_CONDITIONAL_STYLES.add(new ConditionalStyle(NEGATIVE_CONDITION,DETAIL_NUMBER_RED_STYLE));
		DETAIL_NUMBER_RED_BLUE_CONDITIONAL_STYLES.add(new ConditionalStyle(POSITIVE_CONDITION,DETAIL_NUMBER_BLUE_STYLE));
	}

	public AbstractColumn getDateColumn(String property, String header) throws ReportException {
		try {
			return ColumnBuilder.getNew()
				.setColumnProperty(property, Date.class.getName())
				.setTitle(header)
				.setWidth(DATE_DEFAULT_WIDTH)
				.setStyle(DETAIL_DATE_STYLE)
				.setHeaderStyle(COLUMN_HEADER_STYLE)
				.build();
		} catch (ColumnBuilderException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}
	
	public AbstractColumn getStringColumn(String property, String header, int width) throws ReportException {
		try {
			return ColumnBuilder.getNew()
				.setColumnProperty(property, String.class.getName())
				.setTitle(header)
				.setWidth(width)
				.setStyle(DETAIL_DATE_STYLE)
				.setHeaderStyle(COLUMN_HEADER_STYLE)
				.build();
		} catch (ColumnBuilderException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}

	public AbstractColumn getNumberColumn(CustomExpression expression, String header) throws ReportException {
		try {
			return ColumnBuilder.getNew()
				.setCustomExpression(expression)
				.setTitle(header)
				.setWidth(NUMBER_DEFAULT_WIDTH)
				.setStyle(DETAIL_NUMBER_STYLE)
				.setHeaderStyle(COLUMN_HEADER_STYLE)
				.build();
		} catch (ColumnBuilderException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}

	public AbstractColumn getIntegerColumn(String property, String header) throws ReportException {
		try {
			return ColumnBuilder.getNew()
				.setColumnProperty(property, Integer.class.getName())
				.setTitle(header)
				.setWidth(NUMBER_DEFAULT_WIDTH)
				.setStyle(DETAIL_INTEGER_STYLE)
				.setHeaderStyle(COLUMN_HEADER_STYLE)
				.build();
		} catch (ColumnBuilderException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}

	public AbstractColumn getNumberColumn(String property, String header) throws ReportException {
		try {
			return ColumnBuilder.getNew()
				.setColumnProperty(property, Double.class.getName())
				.setTitle(header)
				.setWidth(NUMBER_DEFAULT_WIDTH)
				.setStyle(DETAIL_NUMBER_STYLE)
				.setHeaderStyle(COLUMN_HEADER_STYLE)
				.build();
		} catch (ColumnBuilderException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}

	public AbstractColumn getNumberBlueNormalColumn(String property, String header) throws ReportException {
		try {
			return ColumnBuilder.getNew()
				.setColumnProperty(property, Double.class.getName())
				.setTitle(header)
				.setWidth(NUMBER_DEFAULT_WIDTH)
				.setStyle(DETAIL_NUMBER_STYLE)
				.addConditionalStyles(DETAIL_NUMBER_BLUE_NORMAL_CONDITIONAL_STYLES)
				.setHeaderStyle(COLUMN_HEADER_STYLE)
				.build();
		} catch (ColumnBuilderException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}

	public AbstractColumn getNumberRedBlueBoldColumn(String property, String header) throws ReportException {
		try {
			return ColumnBuilder.getNew()
				.setColumnProperty(property, Double.class.getName())
				.setTitle(header)
				.setWidth(NUMBER_DEFAULT_WIDTH)
				.setStyle(DETAIL_NUMBER_STYLE)
				.addConditionalStyles(DETAIL_NUMBER_RED_BLUE_BOLD_CONDITIONAL_STYLES)
				.setHeaderStyle(COLUMN_HEADER_STYLE)
				.build();
		} catch (ColumnBuilderException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}
	public AbstractColumn getNumberRedBlueColumn(CustomExpression expression, String header) throws ReportException {
		try {
			return ColumnBuilder.getNew()
				.setCustomExpression(expression)					
				.setTitle(header)
				.setWidth(NUMBER_DEFAULT_WIDTH)
				.setStyle(DETAIL_NUMBER_STYLE)
				.addConditionalStyles(DETAIL_NUMBER_RED_BLUE_CONDITIONAL_STYLES)
				.setHeaderStyle(COLUMN_HEADER_STYLE)
				.build();
		} catch (ColumnBuilderException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}

	public AbstractColumn getBooleanColumn(String property, String valueIfTrue, String valueIfFalse, String header, int width) throws ReportException {
		try {
			return ColumnBuilder.getNew()
				.setColumnProperty(property, Boolean.class.getName())
				.setCustomExpression(new BooleanCustomExpression(property, valueIfTrue, valueIfFalse))
				.setTitle(header)
				.setWidth(width)
				.setStyle(DETAIL_DATE_STYLE)
				.setHeaderStyle(COLUMN_HEADER_STYLE)
				.build();
		} catch (ColumnBuilderException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}

	public class BooleanCustomExpression implements CustomExpression {

		private static final long serialVersionUID = 5532097656804800650L;

		private String property;
		private String valueIfTrue;
		private String valueIfFalse;

		public BooleanCustomExpression(String property, String valueIfTrue, String valueIfFalse) {
			this.property = property;
			this.valueIfTrue = valueIfTrue;
			this.valueIfFalse = valueIfFalse;
		}

		@Override
		public String getClassName() {
			return String.class.getName();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object evaluate(Map fields, Map variables, Map parameters) {
			Boolean field = (Boolean) fields.get(property);
			return field ? valueIfTrue : valueIfFalse;
		}
	}

	public AbstractColumn getEnumColumn(String property, Locale locale, String header, int width) throws ReportException {
		try {
			return ColumnBuilder.getNew()
				.setColumnProperty(property, IResourceable.class.getName())
				.setCustomExpression(new EnumCustomExpression(property, locale))
				.setTitle(header)
				.setWidth(width)
				.setStyle(DETAIL_DATE_STYLE)
				.setHeaderStyle(COLUMN_HEADER_STYLE)
				.build();
		} catch (ColumnBuilderException e) {
			throw new ReportException(e.getMessage(),e);
		}
	}

	public class EnumCustomExpression implements CustomExpression {

		private static final long serialVersionUID = 5482842245681755312L;

		private String property;
		private Locale locale;

		public EnumCustomExpression(String property, Locale locale) {
			this.property = property;
			this.locale = locale;
		}

		@Override
		public String getClassName() {
			return String.class.getName();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object evaluate(Map fields, Map variables, Map parameters) {
			IResourceable iResourceable = (IResourceable) fields.get(property);
			return iResourceable.getName(locale);
		}
	}

}
