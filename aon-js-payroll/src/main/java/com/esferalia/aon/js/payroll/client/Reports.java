package com.esferalia.aon.js.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.user.client.Window;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

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
		public Long getEmployeeSeniorityDateTime();
		public String getEmployeeAgreementCategory();
		public String getEmployeeContractType();
		public Integer getEmployeeId();

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
		
		default String getMediumEndDate() {
			return DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM).format(getEndDate());
		}

		default String getMediumStartDate() {
			return DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM).format(getStartDate());
		}
		
		default String getMediumSeniorityDate() {
			return DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM).format(getEmployeeSeniorityDate());
		}
		
		// ------------------ Metodos para el pie de la impresion ---------------------
		
		default Boolean isCostType(Deduction cost, String typeName) {
			return (null != cost.getTypeName() && (cost.getTypeName().equals(typeName) || cost.getTypeName() == typeName)) ? true : false;
		}
		
		default Boolean isCostName(Deduction cost, String name) {
			return (cost.getName().equals(name) || cost.getName() == name) ? true : false;
		}
		
		default Double getCommonContAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
//				Window.alert("Deduction \n Name : " + cost.getName() + "\n Description : " + cost.getDescription() +
//						"\n Type : " + cost.getTypeName() + "\n Percent : " + cost.getPercent() + "\n Amount : " + cost.getAmount());
				if(isCostType(cost, "Contingencias Comunes") || isCostName(cost, "CGC_E"))
					if(cost.getAmount() != null)
						return cost.getAmount();
			}
			return null;
		}
		
		default Double getCommonContPercent(){
			Double commonContAmount = getCommonContAmount();
			Double cgcBase = getCgcBase();
			if(null == cgcBase)
				return 0.00;
			
			if (commonContAmount != null){
				return (commonContAmount / cgcBase) * 100;
			}else
				return null;
		}
		
		default Double getATyEPAmount(){
			Double amount = 0.00;
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(isCostType(cost, "Contingencias Profesionales") || isCostName(cost, "IMS_E") || isCostName(cost, "IT_E"))
					amount += cost.getAmount();
				else if(isCostType(cost, "Contingencias Profesionales") || isCostName(cost, "IMS_E")  || isCostName(cost, "IT_E"))
					amount += cost.getAmount();		
			}
			return amount;
		}
		
		default Double getATyEPPercent(){
			Double ATyEPAmount = getATyEPAmount();
			Double cgcBase = getCgcBase();
			if(null == cgcBase)
				return 0.00;
			
			
			if (ATyEPAmount != null){
				return (ATyEPAmount / cgcBase) * 100;
			}else
				return null;
		}
		
		default Double getUnemploymentAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(isCostType(cost, "Desempleo") || isCostName(cost, "DESMPL_E"))
					if(cost.getAmount() != null)
						return cost.getAmount();
			}
			return null;
		}
		
		default Double getUnemploymentPercent(){
			Double unemploymentAmount = getUnemploymentAmount();
			Double cgpBase = getCgpBase();
			if(null == cgpBase)
				return 0.00;
			
			if (unemploymentAmount != null){
				return (unemploymentAmount / cgpBase) * 100;
			}else
				return null;
		}
		
		default Double getProfesionalFormatAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(isCostType(cost, "Formaci\u00f3n Profesional") || isCostName(cost, "FP_E"))
					if(cost.getAmount() != null)
						return cost.getAmount();
			}
			return null;
		}
		
		default Double getProfesionalFormatPercent(){
			Double profesionalFormat = getProfesionalFormatAmount();
			Double cgpBase = getCgpBase();
			if(null == cgpBase)
				return 0.00;
			
			if (profesionalFormat != null){
				return (profesionalFormat / cgpBase) * 100;
			}else
				return null;
		}
		
		default Double getFogasaAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(isCostType(cost, "FOGASA") || isCostName(cost, "FOGASA_E"))
					if(cost.getAmount() != null)
						return cost.getAmount();
			}
			return null;
		}
		
		default Double getFogasaPercent(){
			Double fogasaAmount = getFogasaAmount();
			Double cgpBase = getCgpBase();
			if(null == cgpBase)
				return 0.00;
			
			if (fogasaAmount != null){
				return (fogasaAmount / cgpBase) * 100;
			}else
				return null;
		}
		
		default Double getHExtraAmount(){
			for(int i = 0; i < getCosts().size(); i++){
				Deduction cost = getCosts().get(i);
				if(null != cost.getTypeName() && cost.getTypeName().equals("Horas Extraordinarias Fuerza Mayor"))
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
				if(null != cost.getTypeName() && cost.getTypeName().equals("Resto Horas Extraordinarias"))
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
		
		default String getEnterpriseLogoURL(){
			String CRETA_URL = URL.encode(GWT.getModuleBaseURL() + "reports");
			return CRETA_URL + "/Enterprise-Logo?contractid="+getEmployeeId();
		}
		
		default Double getMoneyIrpfBase() {
			Double moneyIrpf = 0.00;
			for(Deduction deduction : getDeductions()) {
				if(deduction.getName().equals("IRPF") || deduction.getName() == "IRPF") {
					moneyIrpf = deduction.getAmount() - getInkindIrpfBase();
				}
			}
			return moneyIrpf;
		};
		
		default Double getInkindIrpfBase() {
			Double inkindIrpf = 0.00;
			for(Deduction deduction : getDeductions()) {
				if(deduction.getName().equals("IRPF") || deduction.getName() == "IRPF") {
					for(Payment payment : getPayments()) {
						if(payment.getCode() == 13)
							inkindIrpf += payment.getAmount();
					}
				}
			}
			return inkindIrpf;
		};
		
		default Double getTotalAccruals() {
			Double totalAccrual = 0.00;
			Double totalPayment = getTotalPayment();
			for(Deduction deduction : getDeductions()) {
				if(deduction.getName().equals("IRPF") || deduction.getName() == "IRPF") {
					for(Payment payment : getPayments()) {
						if(payment.getCode() == 13)
							totalAccrual += payment.getAmount();
					}
				}
			}
			
			totalAccrual += totalPayment;
			
			return totalAccrual;
		}
		
		default Double getTotalDeductions() {
			Double totalDeductions = 0.00;
			Double totalDeduction = getTotalDeduction();
			for(Deduction deduction : getDeductions()) {
				if(deduction.getName().equals("IRPF") || deduction.getName() == "IRPF") {
					for(Payment payment : getPayments()) {
						if(payment.getCode() == 13)
							totalDeductions += payment.getAmount();
					}
				}
			}
			
			totalDeductions += totalDeduction;
			
			return totalDeductions;
		}
		
		default Double getTotalCompany() {
			Double commonContAmount = null == getCommonContAmount() ? 0.00 : getCommonContAmount();
			Double aTyEPAmount = null ==  getATyEPAmount() ? 0.00 : getATyEPAmount();
			Double unemploymentAmount = null == getUnemploymentAmount() ? 0.00 : getUnemploymentAmount();
			Double profesionalFormatAmount = null == getProfesionalFormatAmount() ? 0.00 : getProfesionalFormatAmount();
			Double fogasaAmount = null == getFogasaAmount() ? 0.00 : getFogasaAmount();
			Double hExtraAmount = null == getHExtraAmount() ? 0.00 : getHExtraAmount();
			Double nonHExtraAmount = null == getNonHExtraAmount() ? 0.00 : getNonHExtraAmount();
			
			return commonContAmount + aTyEPAmount + unemploymentAmount + profesionalFormatAmount + 
					fogasaAmount + hExtraAmount + nonHExtraAmount;
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
		public String getName();
	}
	
	
	public static interface Callback {
		public void onSuccess(String dataURI);
	}
	

	public static void standard(Payroll salary, Callback callback) {
		payroll2JS0N(salary, (json) -> standard(json, callback));
	}

	private static void getEnterpriseLogo(Payroll salary, Consumer<String> consumer) {
		String LOGO_URL = URL.encode(GWT.getModuleBaseURL() + "reports") + "/Enterprise-Logo?"+"contractid=" + salary.getEmployeeId();

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("GET", LOGO_URL);
		//xhr.setResponseType(ResponseType.ArrayBuffer);
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
				
				
				if (state != XMLHttpRequest.DONE)
					return;
				
				//consumer.accept("data:image/jpeg;base64,"+base64ArrayBuffer(xhr.getResponseArrayBuffer()));
				consumer.accept("data:image/jpeg;base64,"+xhr.getResponseText());
				
				
			}
		});
		
		xhr.send("&contractid=" + salary.getEmployeeId());
	}

	public static void standard_new(Payroll salary, Callback callback) {
		//standard_new(payroll2JSON(salary), callback);
		payroll2JS0N(salary, (json) -> standard_new(json, callback));
	}

	public static void standard_cols(Payroll salary, Callback callback) {
		//standard_cols(payroll2JSON(salary), callback);
		payroll2JS0N(salary, (json) -> standard_cols(json, callback));
	}

	public static void recibe(Payroll salary, Callback callback) {
		//recibe(payroll2JSON(salary), callback);
		payroll2JS0N(salary, (json) -> recibe(json, callback));
	}

	public static void recibe_cra(Payroll salary, Callback callback) {
		//recibe_cra(payroll2JSON(salary), callback);
		payroll2JS0N(salary, (json) -> recibe_cra(json, callback));

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
	
	private static void payroll2JS0N(Payroll payroll, Consumer<JavaScriptObject> consumer){
		JavaScriptObject json = payroll2JSON(payroll);
		getEnterpriseLogo(payroll, (dataURI) ->  {
			setEnterpriseLogo(json, dataURI);
			consumer.accept(json);
		} );
	}
	
	private static native void setEnterpriseLogo(JavaScriptObject json, String dataURI) /*-{
		// TODO: Change blank_image to dataURI 
		var blank_image = 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9/KKKKAP/2Q==';
		json.logo = blank_image;
		json.signature_logo = blank_image;
		json.logoEnterprise = blank_image;
		json.logoEnterprise2 = blank_image;
	}-*/;
	
	private static native JavaScriptObject payroll2JSON(Payroll payroll) /*-{
		var blank_image = 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9/KKKKAP/2Q==';
		var json =  {
			logoEnterprise : blank_image,
			logoEnterprise2 : blank_image,
			net: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalLiquid()(),
			payment: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalPayment()(),
			deduction: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalDeduction()(),
			total_accrual: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalAccruals()(),
			total_deductions: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalDeductions()(),
			liquid_perceive: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalLiquid()(),
			place: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseCity()(),
			date: new Date(),
			reason: 'FIN CONTRATO TEMPORAL'
		}
		
		console.log('Enterprise');
		
		json.enterprise = {
				cif: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseDocument()(),
				name : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseName()(),
				city : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseCity()(),
				address: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseAddress()(),
				ccc: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEnterpriseCCC()()
		}
		
		console.log('Employee');
		
		json.employee = {
				ss: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeSS()(),
				nif: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeDocument()(),
				fullname : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeName()(),
				city : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeCity()(),
				address: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeAddress()(),
				category: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeAgreementCategory()(),
				quote_group: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeQuoteGroup()(),
				professional_group: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeAgreementCategory()(),
				seniority_date: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeSeniorityDateTime()(),
				contract_type: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeContractType()()
		}
		
		console.log('Settlement');
		
		json.settlement = {
				start_date: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getMediumStartDate()(),
				end_date: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getMediumEndDate()(),
				total_days: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTimeUnits()()
		}
		
		json.accruals = [];
		json.payments = [];
		json.deductions = [];
		json.total_contributions = 0;
		
		console.log('Footer');
		
		json.footer_ss_quotation = {}
		
		console.log('Footer common_contingency');
		
		json.footer_ss_quotation.common_contingency = {
					monthly_remuneration : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getRemuneration()(),
					extraordinary_pay_packet : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getProrationBase()(),
					base : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCgcBase()(),
					type_percent : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCommonContPercent()(),
					company_input : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCommonContAmount()()
				}
				
		console.log('Footer professional_contingency');
		
		console.log('Footer professional_contingency getCgpBase : ' + payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCgpBase()());
		console.log('Footer professional_contingency getATyEPPercent : ' + payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getATyEPPercent()());
		console.log('Footer professional_contingency getATyEPAmount : ' + payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getATyEPAmount()());
		console.log('Footer professional_contingency getUnemploymentPercent : ' + payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getUnemploymentPercent()());
		console.log('Footer professional_contingency getUnemploymentAmount : ' + payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getUnemploymentAmount()());
		console.log('Footer professional_contingency getProfesionalFormatPercent : ' + payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getProfesionalFormatPercent()());
		console.log('Footer professional_contingency getProfesionalFormatAmount : ' + payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getProfesionalFormatAmount()());
		console.log('Footer professional_contingency getFogasaPercent : ' + payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getFogasaPercent()());
		console.log('Footer professional_contingency getFogasaAmount : ' + payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getFogasaAmount()());
				
		json.footer_ss_quotation.professional_contingency = {
					base : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCgpBase()(),
		      		type_percent_at : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getATyEPPercent()(),
		      		company_input_at : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getATyEPAmount()(),
		      		type_percent_unemployment : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getUnemploymentPercent()(),
		      		company_input_unemployment : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getUnemploymentAmount()(),
		      		type_percent_professional_formation : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getProfesionalFormatPercent()(),
		      		company_input_professional_formation : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getProfesionalFormatAmount()(),
		      		type_percent_salary_warranty : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getFogasaPercent()(),
		      		company_input_salary_warranty : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getFogasaAmount()()
				}
				
		console.log('Footer aditional_quotation');
				
		json.footer_ss_quotation.aditional_quotation = {
					base_overwhelming_force :  payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::gethExtraBase()(),
      				company_input_overwhelming_force : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getHExtraAmount()(),
      				base_non_structural : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getNonHExtraBase()(),
      				company_input_non_structural : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getNonHExtraAmount()()
		}
		
		console.log('Footer base_irpf');
		
		json.footer_ss_quotation.base_irpf = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getIrpfBase()();
		json.footer_ss_quotation.total_company = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getTotalCompany()();
			
		
		json.logo = blank_image;
		json.signature_logo = blank_image;
		
		console.log('Payments');
		
		var payments = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getPayments()();
		for ( i = 0; i <  payments.@java.util.List::size()(); i++ ) {
			var payment = payments.@java.util.List::get(I)(i); 
			var amount = payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getAmount()();
			if ( amount ){
				json.payments.push( {
					amount : amount,
					description : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getDescription()(),
					cra: payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCodeDescription()(),
					code: payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCode()()
				});
			}
		}
		
		console.log('Deductions');
		
		var contributions = 0;
		var deductions = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getDeductions()();
		for ( i = 0; i <  deductions.@java.util.List::size()(); i++ ) {
			var deduction = deductions.@java.util.List::get(I)(i);
			var amount =  deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getAmount()();
				
				console.log('Amount : ' + amount);
				console.log('description : ' + deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()());
				console.log('percent : ' + deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()());
				console.log('name : ' + deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getName()());
				console.log('Type Name : ' + deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getTypeName()());
					
				
			if ( amount ) {
				if('IRPF' == deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getName()()){
					especie = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getInkindIrpfBase()();
					dinerario = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getMoneyIrpfBase()();
					console.log("IRPF ESPECIE = " + especie);
					console.log("IRPF DINERARIO = " + dinerario);
					if(dinerario && dinerario > 0){
						json.deductions.push ({
							amount : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getMoneyIrpfBase()(),
							description : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()(),
							value : amount,
							percent : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()(),
							name : 'Dinerario',
							type_name : 'Dinerario'
						});
					}
					if(especie && especie > 0){
						json.deductions.push ({
								amount : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getInkindIrpfBase()(),
								description : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()(),
								value : amount,
								percent : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()(),
								name : 'Especie',
								type_name : 'Especie'
							});
					}
				}
				
				json.deductions.push ({
					amount : amount,
					description : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()(),
					value : amount,
					percent : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getDescription()(),
					name : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getName()(),
					type_name : deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getTypeName()()
				});
				contributions += amount;
				
			}
		}
		json.total_contributions = contributions;
		
		console.log('Costs');
		
		var total_costs = 0;
		var costs = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getCosts()();
		for ( i = 0; i <  costs.@java.util.List::size()(); i++ ) {
			var deduction = costs.@java.util.List::get(I)(i);
			var amount =  deduction.@com.esferalia.aon.js.payroll.client.Reports.Deduction::getAmount()();
			if ( amount ) {
				total_costs += amount;
			}
		}
//		json.footer_ss_quotation.total_company = total_costs;
		
		var paymentsOrdered = payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getPaymentsOrderByCode()();
		if ( paymentsOrdered.@java.util.List::isEmpty()() ) {
			console.log('paymentsOrdered : ' + json);
			return json;
		}

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
		
		if(paymentsOrdered.@java.util.List::size()() == 1){
			json.accruals.push(accrual);
		} else {
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
					if(paymentsOrdered.@java.util.List::size()() == i+1){
						json.accruals.push(accrual);
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
					}else{
						var amount = payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getAmount()();
						if( amount ){
							accrual.types.push({
								type_expression : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getDescription()(),
			           			code : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getCode()(),
			           			type_value : payment.@com.esferalia.aon.js.payroll.client.Reports.Payment::getAmount()()
							});
						}
					}
				}
			}
		}
		

		console.log(json);
		return json;
	}-*/;

	private static native String base64ArrayBuffer(ArrayBuffer arrayBuffer) /*-{
		  var base64    = ''
		  var encodings = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'

		  var bytes         = new Uint8Array(arrayBuffer)
		  var byteLength    = bytes.byteLength
		  var byteRemainder = byteLength % 3
		  var mainLength    = byteLength - byteRemainder

		  var a, b, c, d
		  var chunk

		  // Main loop deals with bytes in chunks of 3
		  for (var i = 0; i < mainLength; i = i + 3) {
		    // Combine the three bytes into a single integer
		    chunk = (bytes[i] << 16) | (bytes[i + 1] << 8) | bytes[i + 2]

		    // Use bitmasks to extract 6-bit segments from the triplet
		    a = (chunk & 16515072) >> 18 // 16515072 = (2^6 - 1) << 18
		    b = (chunk & 258048)   >> 12 // 258048   = (2^6 - 1) << 12
		    c = (chunk & 4032)     >>  6 // 4032     = (2^6 - 1) << 6
		    d = chunk & 63               // 63       = 2^6 - 1

		    // Convert the raw binary segments to the appropriate ASCII encoding
		    base64 += encodings[a] + encodings[b] + encodings[c] + encodings[d]
		  }

		  // Deal with the remaining bytes and padding
		  if (byteRemainder == 1) {
		    chunk = bytes[mainLength]

		    a = (chunk & 252) >> 2 // 252 = (2^6 - 1) << 2

		    // Set the 4 least significant bits to zero
		    b = (chunk & 3)   << 4 // 3   = 2^2 - 1

		    base64 += encodings[a] + encodings[b] + '=='
		  } else if (byteRemainder == 2) {
		    chunk = (bytes[mainLength] << 8) | bytes[mainLength + 1]

		    a = (chunk & 64512) >> 10 // 64512 = (2^6 - 1) << 10
		    b = (chunk & 1008)  >>  4 // 1008  = (2^6 - 1) << 4

		    // Set the 2 least significant bits to zero
		    c = (chunk & 15)    <<  2 // 15    = 2^4 - 1

		    base64 += encodings[a] + encodings[b] + encodings[c] + '='
		  }
		  
		  return base64
		}-*/;

}
