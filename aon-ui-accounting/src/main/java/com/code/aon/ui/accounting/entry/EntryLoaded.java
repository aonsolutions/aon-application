package com.code.aon.ui.accounting.entry;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class EntryLoaded {

	private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy"); 
	private static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#,##0.00");
	static {
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		dfs.setGroupingSeparator('.');
		dfs.setDecimalSeparator(',');
		NUMBER_FORMATTER.setDecimalFormatSymbols(dfs);	
	}
	
	
	private Date date;
	private Integer id;
	private String account;
	private String description;
	private String concept;
	private String document;
	private String invoice;
	private Double debit;
	private Double credit;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}

	public String getInvoice() {
		return invoice;
	}
	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public Double getDebit() {
		return debit;
	}
	public void setDebit(Double debit) {
		this.debit = debit;
	}

	public Double getCredit() {
		return credit;
	}
	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public void populate(String[] fields) {
		if (fields == null) {
			throw new IllegalArgumentException("No se puede rellenar un apunte con datos null.");
		}
		if (fields.length != 11) {
			throw new IllegalArgumentException("Número de campos incorrecto.("+fields.length+")");
		}
		
		if (StringUtils.isNotBlank(fields[0])) {
			try {
				setDate( DATE_FORMATTER.parse(fields[0]));
				if (!StringUtils.equals(fields[0], DATE_FORMATTER.format(getDate()))) {
					throw new IllegalArgumentException("La fecha '" + fields[0] + "' no es correcta");	
				}
			} catch (ParseException e) {
				throw new IllegalArgumentException("La fecha '" + fields[0] + "' no es correcta");
			}
		}
		
		if (StringUtils.isNotBlank(fields[1])) {
			try {
				setId(Integer.parseInt(fields[1]));	
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("El identificador de apunte no es correcto ("+ fields[1] +")");
			}
		}
		
		if (StringUtils.isBlank(fields[3])) {
			throw new IllegalArgumentException("La cuenta contable es un dato requerido");			
		}
		setAccount(fields[3]);
		
		if (StringUtils.isBlank(fields[4])) {
			throw new IllegalArgumentException("La descripción de la cuenta contable es un dato requerido");			
		}
		setDescription(fields[4]);

		if (StringUtils.isBlank(fields[5])) {
			throw new IllegalArgumentException("El concepto del asiento es un dato requerido");			
		}
		setConcept(fields[5]);

		setDocument(fields[7]);
		setInvoice(fields[8]);
		
		if (StringUtils.isBlank(fields[9]) && StringUtils.isBlank(fields[10])) {
			throw new IllegalArgumentException("Debe indicarse debe o haber.");
		}
		setDebit(0.0);
		setCredit(0.0);
		String debit = fields[9];
		String credit = fields[10];
		if (StringUtils.isNotBlank(debit)) {
			try {
				setDebit(NUMBER_FORMATTER.parse(debit).doubleValue());
			} catch (ParseException e) {
				throw new IllegalArgumentException("El debe no es un valor numérico válido ("+ debit +")");
			}
		} else {
			try {
				setCredit(NUMBER_FORMATTER.parse(credit).doubleValue());
			} catch (ParseException e) {
				throw new IllegalArgumentException("El haber no es un valor numérico válido ("+ credit +")");
			}
		}
	}

}
