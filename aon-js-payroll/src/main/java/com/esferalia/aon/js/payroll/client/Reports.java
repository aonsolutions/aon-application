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
		
		public <P extends Payment> List<P> getPayments();
		public <D extends Deduction> List<D> getDeductions();
		
		default String getShortEndDate() {
			return DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(getEndDate());
		}

		default String getShortStartDate() {
			return DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(getStartDate());
		}

	}
	
	public static interface Payment {
		public Double getAmount();
		public String getDescription();
	}
	
	public static interface Deduction {
		public Double getAmount();
		public String getDescription();
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
			place: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseCity()(),
			date: new Date(),
			reason: 'FIN CONTRATO TEMPORAL',
			enterprise: {
				cif: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseDocument()(),
				name : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseName()(),
				city : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseCity()(),
				address: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseAddress()()
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
			},
			settlement: {
				start_date: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getShortStartDate()(),
				end_date: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getShortEndDate()(),
				total_days: 30
			},
			accruals: [
			],
			payments: [
			],
			deductions: [
			],
			footer_ss_quotation: {
				common_contingency : {
				},
				professional_contingency : {
				},
				aditional_quotation : {
				},
				base_irpf: 0.00
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
					description : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getDescription()()
				});
		}
		
		var deductions = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getDeductions()();
		for ( i = 0; i <  deductions.@java.util.List::size()(); i++ ) {
			var deduction = deductions.@java.util.List::get(I)(i);
			var amount =  deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getAmount()();
			if ( amount ) {
				json.deductions.push ({
					amount : amount,
					description : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()(),
					value : amount,
					name : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()()
				});
			}
		}

		return json;
	}-*/;

}
