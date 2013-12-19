package com.code.aon.ui.finance;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.registry.RegistryAddress;

public class AplifisaWriter extends BasicExporter {

	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private static final String FIELD_SEPARATOR = "#";
	
	private CharArrayWriter writer;
	
	private CharArrayWriter accountWriter;
	
	private Set<String> exportedAccounts;
	
	public AplifisaWriter(InvoiceExportConfiguration configuration) {
		super(configuration);
		this.writer = new CharArrayWriter();
		this.accountWriter = new CharArrayWriter();
		this.exportedAccounts = new HashSet<String>();		
	}

	@Override
	public InvoiceExportType getType() {
		return InvoiceExportType.APLIFISA;
	}
	
	@Override
	public String getFileName() {
		return "aplifisa.zip";
	}
	
	@Override
	protected boolean isSkipAccount(Account account) {
		return false;
	}
	
	private void appendNewLine( CharArrayWriter writer ) throws IOException {
		if ( writer.size() > 0 ) {
			writer.write("\r\n");
		}
	}	
	
	private void appendString( StringBuffer sb, String value, int maxLength ) {
		String _value = StringUtils.trimToNull(value);
		if ( _value != null ) {
			sb.append( StringUtils.substring(_value, 0, maxLength) );
		}
		sb.append(FIELD_SEPARATOR);
	}	
	
	private void appendNumber( StringBuffer sb, double value, int maxLength ) {
		double _value = CommonUtil.round(value);
		DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(new Locale("es", "ES")));
		String string = df.format(_value);
		appendString(sb, string, maxLength);
	}	
	
	private void appendDate( StringBuffer sb, Date date ) {
		appendString(sb, (date!=null) ? DATE_FORMAT.format(date) : null, 10);
	}		
	
	private TaxBreakDown getTaxBreakDown( AccountEntryDetail aed ) {
		double amount = (aed.getCredit() != 0) ? aed.getCredit() : aed.getDebit();
		for( TaxBreakDown tbd : getTaxBreakDowns() ) {
			if ( tbd.getTaxQuota()==amount && tbd.getTaxType()==TaxType.VAT ) {
				return tbd;
			}
		}
		return null;
	}
	
	private void appendEntryDetail( AccountEntry accountEntry, AccountEntryDetail aed ) {
		StringBuffer sb = new StringBuffer();
		
		// Numero Asiento
		appendNumber( sb, getJournal(accountEntry), 12);
		// Fecha
		appendDate(sb, accountEntry.getEntryDate());
		// Subcuenta
		appendString(sb, aed.getAccount().getCode(), 12);
		// Concepto
		String concept = aed.getConcept();
		if ( StringUtils.isEmpty(concept) ) {
			concept = getReferenceCode();
		}
		appendString(sb, concept, 40);
		// Debe
		appendNumber(sb, aed.getDebit(), 15);
		// Haber
		appendNumber(sb, aed.getCredit(), 15);
		
		String code = aed.getAccount().getCode();
		if ( StringUtils.startsWith(code, "472") || StringUtils.startsWith(code, "477") ) {
			// Contra
			appendString(sb, (aed.getBalancingAccount() != null) ? aed.getBalancingAccount().getCode() : null, 12);	
			// Factura
			appendString(sb, getMainId().toString(), 15);
			TaxBreakDown tbd = getTaxBreakDown(aed);
			if ( tbd != null ) {
				// Base Imponible
				appendNumber(sb, tbd.getBase(), 15);
				// IVA
				appendNumber(sb, tbd.getTaxPercent(), 5);
				// Recargo
				appendNumber(sb, tbd.getSurchargePercent(), 5);
				// Cuota IVA
				appendNumber(sb, tbd.getTaxQuota(), 15);
				// Cuota Recargo
				appendNumber(sb, tbd.getSurchargeQuota(), 15);
			}
		}
		
		this.writer.append(sb);
	}
	
	@Override
	public void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException {
		writeAccounts();
		appendNewLine(this.writer);
		appendEntryDetail( accountEntry, getRegistryDetail() );
		while (! getDetails().isEmpty() ) {
			appendNewLine(this.writer);
			AccountEntryDetail aed = getDetails().get(0);
			getDetails().remove(0);
			appendEntryDetail( accountEntry, aed );
		}
		writeNewLine();
	}

	private void writeAccounts() throws IOException {
		if (! isExported(getRegistryDetail()) ) {
			appendNewLine(this.accountWriter);
			writeAccounts( getRegistryDetail(), true );
		}
		for( AccountEntryDetail aed : getDetails() ) {
			if (! isExported(aed) ) {
				appendNewLine(this.accountWriter);
				writeAccounts( aed, false );				
			}
		}
	}
	
	private boolean isExported( AccountEntryDetail aed ) {
		String code = aed.getAccount().getCode();
		return exportedAccounts.contains(code);
	}
	
	private void writeAccounts( AccountEntryDetail aed, boolean registry ) throws IOException {
		StringBuffer sb = new StringBuffer();
		
		// Subcuenta
		appendString(sb, aed.getAccount().getCode(), 12);
		// Concepto
		appendString(sb, aed.getAccount().getDescription(), 50);
		
		if ( registry ) {
			// CIF
			if ( getRegistryDocument() != null ) {
				appendString(sb, getRegistryDocument().getDocument(), 16);	
			} else {
				sb.append(FIELD_SEPARATOR);
			}
			RegistryAddress address = getRegistryAddress();
			if ( getRegistryAddress() != null ) {
				// Direccion			
				appendString(sb, address.getFullAddress(), 50);
				// Codigo Postal		
				appendString(sb, address.getZip(), 5);
				// Poblacion		
				appendString(sb, address.getCity(), 40);
				// Provincia					
				appendString(sb, (address.getGeozone() != null)?address.getGeozone().getName():null, 40);
			}
		}

		this.accountWriter.append(sb);
		this.exportedAccounts.add(aed.getAccount().getCode());		
	}
	
	@Override
	public Map<String, byte[]> getDataMap() {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		try {
			map.put("asientos.txt", writer.toString().getBytes("ISO-8859-1"));
			map.put("subcuentas.txt", accountWriter.toString().getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return map;
	}
	
}