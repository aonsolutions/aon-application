package com.esferalia.aon.in.payroll.pdf.creators.settlement.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.Accrual;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.Deduction;

public class Settlement {

	private String  enterprise_name;
	private String  enterprise_NIF;
	private String  enterprise_address;
	
	private String  employee_name;
	private String  employee_NIF;
	private String  employee_category;
	private Date    employee_antiquity;
	
	private Date    end_date;
	private String  end_cause;
	private Boolean exist_representative;
	
	private Map<Integer,ArrayList<Accrual>> accruals;
	private Map<Integer,ArrayList<Deduction>> deductions;
	
	private Double accrual_total;
	private Double deduction_total;
	private Double total;
	
	private Date   date;
	private String location;
	
	private Settlement() {}
	private static Settlement instance() {return new Settlement();}
	
	public Optional<String> enterprise_name() 								{return Optional.ofNullable(enterprise_name);}
	public Settlement setEnterprise_name(String enterprise_name) 			{this.enterprise_name = enterprise_name; 			return this;}
	
	public Optional<String> enterprise_nif() 								{return Optional.ofNullable(enterprise_NIF);}
	public Settlement setEnterprise_NIF(String enterprise_NIF) 				{this.enterprise_NIF = enterprise_NIF;				return this;}
	
	public Optional<String> enterprise_address() 							{return Optional.ofNullable(enterprise_address);}
	public Settlement setEnterprise_address(String enterprise_address) 		{this.enterprise_address = enterprise_address; 		return this;}
	
	public Optional<String> employee_name() 								{return Optional.ofNullable(employee_name);}
	public Settlement setEmployee_name(String employee_name) 				{this.employee_name = employee_name; 				return this;}
	
	public Optional<String> employee_nif() 									{return Optional.ofNullable(employee_NIF);}
	public Settlement setEmployee_NIF(String employee_NIF) 					{this.employee_NIF = employee_NIF; 					return this;}
	
	public Optional<String> employee_category() 							{return Optional.ofNullable(employee_category);}
	public Settlement setEmployee_category(String employee_category) 		{this.employee_category = employee_category; 		return this;}
	
	public Optional<Date> employee_antiquity() 								{return Optional.ofNullable(employee_antiquity);}
	public Settlement setEmployee_antiquity(Date employee_antiquity) 		{this.employee_antiquity = employee_antiquity; 		return this;}
	
	public Optional<Date> end_date() 										{return Optional.ofNullable(end_date);}
	public Settlement setEnd_date(Date end_date) 							{this.end_date = end_date; 							return this;}
	
	public Optional<String> end_cause() 									{return Optional.ofNullable(end_cause);}
	public Settlement setEnd_cause(String end_cause) 						{this.end_cause = end_cause;						return this;}
	
	public Optional<Boolean> exist_representative() 						{return Optional.ofNullable(exist_representative);}
	public Settlement setExist_representative(boolean exist_representative) {this.exist_representative = exist_representative;	return this;}
	
	public Map<Integer,ArrayList<Accrual>> getAccruals() 					{return accruals;}
	public Settlement setAccruals(Map<Integer,ArrayList<Accrual>> accruals) {this.accruals = accruals;							return this;}
	
	public Map<Integer,ArrayList<Deduction>> getDeductions() 							{return deductions;}
	public Settlement setDeductions(Map<Integer,ArrayList<Deduction>> deductions) 		{this.deductions = deductions;						return this;}
	
	public Optional<Double> getAccrual_total() 								{return Optional.ofNullable(accrual_total);}
	public Settlement setAccrual_total(double accrual_total) 				{this.accrual_total = accrual_total;				return this;}
	
	public Optional<Double> deduction_total() 								{return Optional.ofNullable(deduction_total);}
	public Settlement setDeduction_total(double deduction_total) 			{this.deduction_total = deduction_total;			return this;}
	
	public Optional<Double> total() 										{return Optional.ofNullable(total);}
	public Settlement setTotal(double total) 								{this.total = total;								return this;}
	
	public Optional<Date> date()											{return Optional.ofNullable(date);}
	public Settlement setDate(Date date) 									{this.date = date;									return this;}
	
	public Optional<String> location()										{return Optional.ofNullable(location);}
	public Settlement setLocation(String location) 							{this.location = location;							return this;}
	
	
	public static class SettlementBuilder{

		private String  enterprise_name;
		private String  enterprise_NIF;
		private String  enterprise_address;
		
		private String  employee_name;
		private String  employee_NIF;
		private String  employee_category;
		private Date    employee_antiquity;
		
		private Date    end_date;
		private String  end_cause;
		private boolean exist_representative;
		
		private Map<Integer,ArrayList<Accrual>> accruals;
		private Map<Integer,ArrayList<Deduction>> deductions;
		
		private double accrual_total;
		private double deduction_total;
		private double total;
		private Date   date;
		private String location;
		
		public SettlementBuilder setEnterprise_name(String enterprise_name) 			{this.enterprise_name = enterprise_name; 			return this;}
		public SettlementBuilder setEnterprise_NIF(String enterprise_NIF) 				{this.enterprise_NIF = enterprise_NIF;				return this;}
		public SettlementBuilder setEnterprise_address(String enterprise_address) 		{this.enterprise_address = enterprise_address;		return this;}
		public SettlementBuilder setEmployee_name(String employee_name) 				{this.employee_name = employee_name;				return this;}
		public SettlementBuilder setEmployee_NIF(String employee_NIF) 					{this.employee_NIF = employee_NIF;					return this;}
		public SettlementBuilder setEmployee_category(String employee_category) 		{this.employee_category = employee_category;		return this;}
		public SettlementBuilder setEmployee_antiquity(Date employee_antiquity) 		{this.employee_antiquity = employee_antiquity;		return this;}
		public SettlementBuilder setEnd_date(Date end_date) 							{this.end_date = end_date;							return this;}
		public SettlementBuilder setEnd_cause(String end_cause) 						{this.end_cause = end_cause;						return this;}
		public SettlementBuilder setExist_representative(boolean exist_representative) 	{this.exist_representative = exist_representative;	return this;}
		
		public SettlementBuilder setAccruals(Map<Integer,ArrayList<Accrual>> accruals) 			{this.accruals = accruals;							return this;}
		public SettlementBuilder setDeductions(Map<Integer,ArrayList<Deduction>> deductions) 	{this.deductions = deductions;						return this;}
		
		public SettlementBuilder setAccrual_total(double accrual_total) 				{this.accrual_total = accrual_total;				return this;}
		public SettlementBuilder setDeduction_total(double deduction_total) 			{this.deduction_total = deduction_total;			return this;}
		public SettlementBuilder setTotal(double total) 								{this.total = total;								return this;}
		public SettlementBuilder setDate(Date date) 									{this.date = date;									return this;}
		public SettlementBuilder setLocation(String location) 							{this.location = location;							return this;}
		
		public Settlement build() {
			return instance()
					.setEnterprise_name			(this.enterprise_name)
					.setEnterprise_NIF			(this.enterprise_NIF)
					.setEnterprise_address		(this.enterprise_address)
					.setEmployee_name			(this.employee_name)
					.setEmployee_NIF			(this.employee_NIF)
					.setEmployee_category		(this.employee_category)
					.setEmployee_antiquity		(this.employee_antiquity)
					.setEnd_date				(this.end_date)
					.setEnd_cause				(this.end_cause)
					.setExist_representative	(this.exist_representative)
					.setAccruals				(this.accruals)
					.setDeductions				(this.deductions)
					.setAccrual_total			(this.accrual_total)
					.setDeduction_total			(this.deduction_total)
					.setLocation				(this.location)
					.setDate					(this.date)
					.setTotal					(this.total);
		};
		
	}
}
