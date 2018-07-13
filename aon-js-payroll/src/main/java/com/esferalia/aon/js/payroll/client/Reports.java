package com.esferalia.aon.js.payroll.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;

public class Reports {
	
	static {
		ScriptInjector.fromUrl(
		GWT.getModuleName() + "/payroll/lib/bundle.js")
		.setCallback(new com.google.gwt.core.client.Callback<Void,Exception>() {
			
			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onFailure(Exception reason) {
				Window.alert(reason.getLocalizedMessage());
			}
		}).inject();
	}
	
	
	public static interface Payroll {
		
		public Date getEndDate();
		public Date getStartDate();
		public Date getIssueDate();
		public Integer getTimeUnits();

		public Double getTotalLiquid();
		public Double getTotalPayment();
		public Double getTotalDeduction();

		public String getEmployeeSS();
		public String getEmployeeName();
		public String getEmployeeCity();
		public String getEmployeeAddress();
		public String getEmployeeDocument();
		public String getEmployeeQuoteGroup();
		public Date getEmployeeSeniorityDate();
		public String getEmployeeAgreementCategory();

		public String getEnterpriseName();
		public String getEnterpriseCity();
		public String getEnterpriseAddress();
		public String getEnterpriseDocument();
		public String getEnterpriseCCC();
		
		public Double getRemuneration();
		public Double getProrationBase();
		public Double getCgcBase();
		public Double getCgpBase();
		public Double getRawCgcBase();
		public Double gethExtraBase();
		public Double getNonHExtraBase();
		public Double getIrpfBase();
		
		public <P extends Payment> List<P> getPayments();
		public <D extends Deduction> List<D> getDeductions();
		public <D extends Deduction> List<D> getCosts();
		public <P extends Payment> List<P> getPaymentsOrderByCode();
		
		default String getShortEndDate() {
			return DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(getEndDate());
		}

		default String getShortStartDate() {
			return DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(getStartDate());
		}
		
		default String getShortSeniorityDate() {
			return DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(getEmployeeSeniorityDate());
		}
		
		// ------------------ Metodos para el pie de la impresion ---------------------
		
		default Double getCommonContAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(cost.getTypeName().equals("Contingencias Comunes"))
					if(cost.getAmount() != null)
						return cost.getAmount();
			}
			return null;
		}
		
		default Double getCommonContPercent(){
			Double commonContAmount = getCommonContAmount();
			if (commonContAmount != null){
				return (commonContAmount / getCgcBase()) * 100;
			}else
				return null;
		}
		
		default Double getATyEPAmount(){
			Double amount = 0.00;
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(cost.getTypeName().equals("Contingencias Profesionales"))
					amount += cost.getAmount();
				else if(cost.getTypeName().equals("Contingencias Profesionales"))
					amount += cost.getAmount();		
			}
			return amount;
		}
		
		default Double getATyEPPercent(){
			Double ATyEPAmount = getATyEPAmount();
			if (ATyEPAmount != null){
				return (ATyEPAmount / getCgcBase()) * 100;
			}else
				return null;
		}
		
		default Double getUnemploymentAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(cost.getTypeName().equals("Desempleo"))
					if(cost.getAmount() != null)
						return cost.getAmount();
			}
			return null;
		}
		
		default Double getUnemploymentPercent(){
			Double unemploymentAmount = getUnemploymentAmount();
			if (unemploymentAmount != null){
				return (unemploymentAmount / getCgpBase()) * 100;
			}else
				return null;
		}
		
		default Double getProfesionalFormatAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(cost.getTypeName().equals("Formaci\u00f3n Profesional"))
					if(cost.getAmount() != null)
						return cost.getAmount();
			}
			return null;
		}
		
		default Double getProfesionalFormatPercent(){
			Double profesionalFormat = getProfesionalFormatAmount();
			if (profesionalFormat != null){
				return (profesionalFormat / getCgpBase()) * 100;
			}else
				return null;
		}
		
		default Double getFogasaAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(cost.getTypeName().equals("FOGASA"))
					if(cost.getAmount() != null)
						return cost.getAmount();
			}
			return null;
		}
		
		default Double getFogasaPercent(){
			Double fogasaAmount = getFogasaAmount();
			if (fogasaAmount != null){
				return (fogasaAmount / getCgpBase()) * 100;
			}else
				return null;
		}
		
		default Double getHExtraAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(cost.getTypeName().equals("Horas Extraordinarias Fuerza Mayor"))
					if(cost.getAmount() != null)
						return cost.getAmount();
			}
			return null;
		}
		
		default Double getHExtraPercent(){
			Double hExtraAmount = getHExtraAmount();
			if (hExtraAmount != null){
				return (hExtraAmount / gethExtraBase()) * 100;
			}else
				return null;
		}
		
		default Double getNonHExtraAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(cost.getTypeName().equals("Resto Horas Extraordinarias"))
					if(cost.getAmount() != null)
						return cost.getAmount();
			}
			return null;
		}
		
		default Double getNonHExtraPercent(){
			Double nonHExtraAmount = getNonHExtraAmount();
			if (nonHExtraAmount != null){
				return (nonHExtraAmount / getNonHExtraBase()) * 100;
			}else
				return null;
		}

	}
	
	public static interface Payment {
		public Double getAmount();
		public String getDescription();
		public int getCode();
		public String getCodeDescription();
	}
	
	public static interface Deduction {
		public Double getAmount();
		public String getDescription();
		public String getTypeName();
		public Double getPercent();
	}
	
	
	public static interface Callback {
		public void onSuccess(String dataURI);
	}
	
	public static void standard(Payroll salary, Callback callback) {
		standard(payroll2JSON(salary), callback);
	}

	public static void standard_new(Payroll salary, Callback callback) {
		standard_new(payroll2JSON(salary), callback);
	}

	public static void standard_cols(Payroll salary, Callback callback) {
		standard_cols(payroll2JSON(salary), callback);
	}

	public static void recibe(Payroll salary, Callback callback) {
		recibe(payroll2JSON(salary), callback);
	}

	public static void recibe_cra(Payroll salary, Callback callback) {
		recibe_cra(payroll2JSON(salary), callback);
	}

	public static void a3Letter(Payroll settlement, Callback callback) {
		a3Letter(payroll2JSON(settlement), callback);
	}

	public static void defLetter(Payroll settlement, Callback callback) {
		defLetter(payroll2JSON(settlement), callback);
	}
	
	private static native void standard(JavaScriptObject salary, Callback callback) /*-{
		var stream = payroll.blobStream();
		payroll.reports.standard(salary, stream);
		stream.on('finish', function() {
			callback.@com.esferalia.aon.js.payroll.client.Reports.Callback::onSuccess(Ljava/lang/String;)(this.toBlobURL('application/pdf'));
		});
	}-*/;

	private static native void standard_new(JavaScriptObject salary, Callback callback) /*-{
		var stream = payroll.blobStream();
		payroll.reports.standard_new(salary, stream);
		stream.on('finish', function() {
			callback.@com.esferalia.aon.js.payroll.client.Reports.Callback::onSuccess(Ljava/lang/String;)(this.toBlobURL('application/pdf'));
		});
	}-*/;

	private static native void standard_cols(JavaScriptObject salary, Callback callback) /*-{
		var stream = payroll.blobStream();
		payroll.reports.standard_cols(salary, stream);
		stream.on('finish', function() {
			callback.@com.esferalia.aon.js.payroll.client.Reports.Callback::onSuccess(Ljava/lang/String;)(this.toBlobURL('application/pdf'));
		});
	}-*/;

	private static native void recibe(JavaScriptObject salary, Callback callback) /*-{
		var stream = payroll.blobStream();
		payroll.reports.recibe(salary, stream);
		stream.on('finish', function() {
			callback.@com.esferalia.aon.js.payroll.client.Reports.Callback::onSuccess(Ljava/lang/String;)(this.toBlobURL('application/pdf'));
		});
	}-*/;

	private static native void recibe_cra(JavaScriptObject salary, Callback callback) /*-{
		var stream = payroll.blobStream();
		payroll.reports.recibe_cra(salary, stream);
		stream.on('finish', function() {
			callback.@com.esferalia.aon.js.payroll.client.Reports.Callback::onSuccess(Ljava/lang/String;)(this.toBlobURL('application/pdf'));
		});
	}-*/;

	private static native void a3Letter(JavaScriptObject settlement, Callback callback) /*-{
		var stream = payroll.blobStream();
		payroll.reports.a3Letter(settlement, stream);
		stream.on('finish', function() {
			callback.@com.esferalia.aon.js.payroll.client.Reports.Callback::onSuccess(Ljava/lang/String;)(this.toBlobURL('application/pdf'));
		});
	}-*/;

	private static native void defLetter(JavaScriptObject settlement, Callback callback) /*-{
		var stream = payroll.blobStream();
		payroll.reports.defLetter(settlement, stream);
		stream.on('finish', function() {
			callback.@com.esferalia.aon.js.payroll.client.Reports.Callback::onSuccess(Ljava/lang/String;)(this.toBlobURL('application/pdf'));
		});
	}-*/;

	private static native JavaScriptObject payroll2JSON(Payroll payroll) /*-{
		var blank_image = 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9/KKKKAP/2Q==';
		var json =  {
			net: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalLiquid()(),
			payment: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalPayment()(),
			deduction: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalDeduction()(),
			total_accrual: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalPayment()(),
			total_deductions: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalDeduction()(),
			liquid_perceive: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalLiquid()(),
			place: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseCity()(),
			date: new Date(),
			reason: 'FIN CONTRATO TEMPORAL',
			enterprise: {
				cif: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseDocument()(),
				name : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseName()(),
				city : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseCity()(),
				address: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseAddress()(),
				ccc: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseCCC()()
			},
			employee: {
				ss: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeSS()(),
				nif: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeDocument()(),
				fullname : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeName()(),
				city : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeCity()(),
				address: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeAddress()(),
				category: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeAgreementCategory()(),
				quote_group: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeQuoteGroup()(),
				professional_group: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeAgreementCategory()(),
				seniority_date: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getShortSeniorityDate()()
			},
			settlement: {
				start_date: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getShortStartDate()(),
				end_date: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getShortEndDate()(),
				total_days: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTimeUnits()()
			},
			accruals: [
			],
			payments: [
			],
			deductions: [
			],
			total_contributions: 0,
			footer_ss_quotation: {
				common_contingency : {
					monthly_remuneration : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getRemuneration()(),
      				extraordinary_pay_packet : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getProrationBase()(),
			    	base : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCgcBase()(),
			      	type_percent : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCommonContPercent()(),
			      	company_input : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCommonContAmount()()
				},
				professional_contingency : {
					base : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCgpBase()(),
		      		type_percent_at : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getATyEPPercent()(),
		      		company_input_at : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getATyEPAmount()(),
		      		type_percent_unemployment : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getUnemploymentPercent()(),
		      		company_input_unemployment : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getUnemploymentAmount()(),
		      		type_percent_professional_formation : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getProfesionalFormatPercent()(),
		      		company_input_professional_formation : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getProfesionalFormatAmount()(),
		      		type_percent_salary_warranty : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getFogasaPercent()(),
		      		company_input_salary_warranty : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getFogasaAmount()()
				},
				aditional_quotation : {
					base_overwhelming_force :  payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::gethExtraBase()(),
      				company_input_overwhelming_force : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getHExtraAmount()(),
      				base_non_structural : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getNonHExtraBase()(),
      				company_input_non_structural : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getNonHExtraAmount()()
				},
				base_irpf: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getIrpfBase()(),
				total_company : 0
			},
			logo : blank_image,
			signature_logo : blank_image
		};
		
		var payments = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getPayments()();
		for ( i = 0; i <  payments.@java.util.List::size()(); i++ ) {
			var payment = payments.@java.util.List::get(I)(i); 
			var amount = payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getAmount()();
			if ( amount )
				json.payments.push( {
					amount : amount,
					description : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getDescription()(),
					code: payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCode()(),
					cra: payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCodeDescription()()
				});
		}
		
		var contributions = 0;
		var deductions = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getDeductions()();
		for ( i = 0; i <  deductions.@java.util.List::size()(); i++ ) {
			var deduction = deductions.@java.util.List::get(I)(i);
			var amount =  deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getAmount()();
			if ( amount ) {
				json.deductions.push ({
					amount : amount,
					description : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()(),
					value : amount,
					percent : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()(),
					name : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getTypeName()()
					//name : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()(),
				});
				contributions += amount;
			}
		}
		json.total_contributions = contributions;
		
		var total_costs = 0;
		var costs = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCosts()();
		for ( i = 0; i <  costs.@java.util.List::size()(); i++ ) {
			var deduction = costs.@java.util.List::get(I)(i);
			var amount =  deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getAmount()();
			if ( amount ) {
				total_costs += amount;
			}
		}
		json.footer_ss_quotation.total_company = total_costs;
		
		var paymentsOrdered = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getPaymentsOrderByCode()();
		var paymentFirst = paymentsOrdered.@java.util.List::get(I)(0);
		var accrual = {};
		accrual.accrual_name = paymentFirst.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCodeDescription()();
		accrual.types = [];
		var amount = paymentFirst.@com.esferalia.aon.js.payroll.client.Reports.Payment::getAmount()();
		if( amount ){
			accrual.types.push({
				type_expression : paymentFirst.@com.esferalia.aon.js.payroll.client.Reports.Payment::getDescription()(),
       			code : paymentFirst.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCode()(),
       			type_value : paymentFirst.@com.esferalia.aon.js.payroll.client.Reports.Payment::getAmount()()
			});
		}
		for ( i = 1; i <  paymentsOrdered.@java.util.List::size()(); i++ ) {
			var payment = paymentsOrdered.@java.util.List::get(I)(i);
			var paymentPrevius = paymentsOrdered.@java.util.List::get(I)(i-1); 
			var paymentCode = payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCode()();
			var paymentCodePrevius = paymentPrevius.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCode()();
			
			if(paymentCode == paymentCodePrevius){
				var amount = payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getAmount()();
				if( amount ){
					accrual.types.push({
						type_expression : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getDescription()(),
	           			code : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCode()(),
	           			type_value : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getAmount()()
					});
				}
			}else{
				json.accruals.push(accrual);
				accrual = {};
				accrual.accrual_name = payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCodeDescription()();
				accrual.types = [];
				if(paymentsOrdered.@java.util.List::size()() == i+1){
					var amount = payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getAmount()();
					if( amount ){
						accrual.types.push({
							type_expression : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getDescription()(),
		           			code : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCode()(),
		           			type_value : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getAmount()()
						});
						json.accruals.push(accrual);
					}
				}
			}
		}
		

		console.log(json);
		return json;
	}-*/;

}
