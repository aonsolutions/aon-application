package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.text.StyledEditorKit.BoldAction;

public class ReportData implements Serializable {
	


	public abstract static class Column implements
			Serializable {

		private String id;
		private String label;

		
		protected Column() {
		}

		public Column(String id){
			this(id,id);
		}

		public Column(String id, String label){
			this.id = id;
			this.label = label;
		}

		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getLabel() {
			return label;
		}
		
		public void setLabel(String label) {
			this.label = label;
		}
		
		public abstract Class getType();
		
		public abstract Object parse(String str);

		public abstract String format(Object obj);
	}
	
	public static class StringColumn extends Column{
		
		protected StringColumn() {
			super();
		}

		public StringColumn(String id){
			super(id);
		}

		public StringColumn(String id, String label){
			super(id, label);
		}

		@Override
		public Class getType() {
			return String.class;
		}
		
		@Override
		public Object parse(String str) {
			return str;
		}

		@Override
		public String format(Object obj) {
			return (String)obj;
		}
	}

	public static class DoubleColumn extends Column{

		protected DoubleColumn() {
			super();
		}

		public DoubleColumn(String id){
			super(id);
		}

		public DoubleColumn(String id, String label){
			super(id, label);
		}
		@Override
		public Class getType() {
			return Double.class;
		}
		
		@Override
		public Object parse(String str) {
			return Double.parseDouble(str);
		}

		@Override
		public String format(Object obj) {
			return Double.toString((Double) obj);
		}
	}

	public static class IntColumn extends Column{

		private IntColumn() {
			super();
		}

		public IntColumn(String id){
			super(id);
		}

		public IntColumn(String id, String label){
			super(id, label);
		}
		@Override
		public Class getType() {
			return Integer.class;
		}
		
		@Override
		public Object parse(String str) {
			return Integer.parseInt(str);
		}

		@Override
		public String format(Object obj) {
			return Integer.toString((Integer) obj);
		}
	}

	public static class DateColumn extends Column{

		private DateColumn() {
			super();
		}

		public DateColumn(String id){
			super(id);
		}

		public DateColumn(String id, String label){
			super(id, label);
		}
		@Override
		public Class getType() {
			return Date.class;
		}
		
		@Override
		public Object parse(String str) {
			String split []= str.split("-");
			return new Date(Integer.valueOf(split[0]),
					Integer.valueOf(split[1]),
					Integer.valueOf(split[2]));
		}

		@Override
		public String format(Object obj) {
			Date date = ( Date ) obj;
			return date.getYear()+"-"+date.getMonth()+"-"+date.getDay();
		}
	}
	
	public static class BooleanColumn extends Column{

		private BooleanColumn() {
			super();
		}

		public BooleanColumn(String id){
			super(id);
		}

		public BooleanColumn(String id, String label){
			super(id, label);
		}
		@Override
		public Class getType() {
			return Boolean.class;
		}
		
		@Override
		public Object parse(String str) {
			return Boolean.parseBoolean(str);
		}

		@Override
		public String format(Object obj) {
			return Boolean.toString((Boolean) obj);
		}
	}
		

	private Column [] columns;
	private List<String[]> rows;
	
	private ReportData() {
	}

	public ReportData(Column...columns) {
		this.columns = columns;
		this.rows = new ArrayList<String[]>();
	}

	public int rows(){
		return rows.size();
	}
	
	public Column [] getColumns() {
		return columns; 
	}
	
	public String[] getRow(int row){
		return rows.get(row);
	}
	
	public void addRow(Object ...values) {
		String strs [] = new String [values.length];
		for (int i = 0; i < values.length; i++) 
			strs[i] = columns[i].format(values[i]);
		rows.add(strs);
	}
	
	
	
	
}
