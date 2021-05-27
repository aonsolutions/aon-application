package net.aonsolutions.aon.tbai._beans;

public class TbaiPrint {

	private String prev_invoice_series;
	private String prev_invoice_number;
	private String prev_invoice_expedition_name;
	private String prev_invoice_signature_value;
	
	
	public TbaiPrint(String prev_invoice_series, String prev_invoice_number, String prev_invoice_expedition_name,
			String prev_invoice_signature_value) {
		super();
		this.prev_invoice_series = prev_invoice_series;
		this.prev_invoice_number = prev_invoice_number;
		this.prev_invoice_expedition_name = prev_invoice_expedition_name;
		this.prev_invoice_signature_value = prev_invoice_signature_value;
	}


	public String getPrev_invoice_series() {
		return prev_invoice_series;
	}


	public void setPrev_invoice_series(String prev_invoice_series) {
		this.prev_invoice_series = prev_invoice_series;
	}


	public String getPrev_invoice_number() {
		return prev_invoice_number;
	}


	public void setPrev_invoice_number(String prev_invoice_number) {
		this.prev_invoice_number = prev_invoice_number;
	}


	public String getPrev_invoice_expedition_name() {
		return prev_invoice_expedition_name;
	}


	public void setPrev_invoice_expedition_name(String prev_invoice_expedition_name) {
		this.prev_invoice_expedition_name = prev_invoice_expedition_name;
	}


	public String getPrev_invoice_signature_value() {
		return prev_invoice_signature_value;
	}


	public void setPrev_invoice_signature_value(String prev_invoice_signature_value) {
		this.prev_invoice_signature_value = prev_invoice_signature_value;
	}					
	
	
	
}
