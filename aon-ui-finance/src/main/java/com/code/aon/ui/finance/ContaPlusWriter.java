package com.code.aon.ui.finance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.product.strategy.TaxBreakDown;

public class ContaPlusWriter extends BasicExporter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(ContaPlusWriter.class);

	private static final String DEFAULT_CHARGED_VAT_ACCOUNT = "477000000";
	
	private static final String DEFAULT_PAID_VAT_ACCOUNT = "472000000";

	private static final String DEFAULT_SALES_ACCOUNT = "700000000";
	
	private static final String CHARGED_VAT_ACCOUNT = "4772100000";
	
	private static final String SALES_T_ACCOUNT = "7050500000";
	
	private static final String SALES_F_ACCOUNT = "7050600000";
	
	private static final String SALES_G_ACCOUNT = "7050700000";

	private static final String DBF_SUFFIX = ".dbf";

	private String salesAccount;
	
	private String chargedVat;
	
	private String paidVat;
	
	private List<Object[]> lines;
	
	public ContaPlusWriter(InvoiceExportConfiguration configuration) {
		super(configuration);
		this.lines = new LinkedList<Object[]>();
		this.salesAccount = obtainAccount(AppParam.ACC_DEFAULT_SALES_ACC, DEFAULT_SALES_ACCOUNT);
		this.chargedVat = obtainAccount(AppParam.ACC_DEFAULT_CHARGED_VAT_ACC, DEFAULT_CHARGED_VAT_ACCOUNT);
		this.paidVat = obtainAccount(AppParam.ACC_DEFAULT_PAID_VAT_ACC, DEFAULT_PAID_VAT_ACCOUNT);
	}

	private String obtainAccount( AppParam appPparam, String _default ) {
		String code = _default; 
		Integer id = AppParamUtil.getValueAsInteger(appPparam);
		if ( id != null ) {
			try {
				Account account = (Account) BeanManager.getManagerBean(Account.class).get(id);
				code = account.getCode();
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			} 
		}
		return code;
	}
	
	@Override
	public InvoiceExportType getType() {
		return InvoiceExportType.CONTA_PLUS;
	}
	
	@Override
	protected boolean isSkipAccount(Account account) {
		return false;
	}	
	
	private String getString( String value, int maxLength) {
		String _value = StringUtils.trimToNull(value);
		if ( _value != null ) {
			_value = StringUtils.substring(_value, 0, maxLength);
		}		
		return _value;
	}
	
	@Override
	public void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException {
		if ( getRegistryDetail() != null ) {
			writeAccounts( accountEntry, getRegistryDetail(), true );
		}
		for( AccountEntryDetail aed : getDetails() ) {
			writeAccounts( accountEntry, aed, false );				
		}
	}

	private TaxBreakDown getVat() {
		return getNextTax(getTaxBreakDowns());
	}
	
	private String getSalesAccount() {
		if ( "T".equals(getInvoiceSeries()) ) {
			return SALES_T_ACCOUNT;
		} else if ( "F".equals(getInvoiceSeries()) ) {
			return SALES_F_ACCOUNT;
		} else if ( "G".equals(getInvoiceSeries()) ) {
			return SALES_G_ACCOUNT;
		}
		return getAccountCode(salesAccount);
	}
	
	private String getAccount( Account account ) {
		String code = account.getCode();
		if ( StringUtils.equals(salesAccount, code) ) {
			code = getSalesAccount();
		} else if ( StringUtils.equals(chargedVat, code) ) {
			code = CHARGED_VAT_ACCOUNT;
		} else {
			code = StringUtils.substring(code, 0, 3) + "0" + StringUtils.substring(code, 3);
		}
		return getString( getAccountCode(code), 12 );
	}
	
	private boolean isVatAccount( Account account ) {
		return StringUtils.equals(chargedVat, account.getCode()) || 
				StringUtils.equals(paidVat, account.getCode());
	}
	
	private Integer getFacturaNumber() {
		if ( getInvoiceNumber() != null ) {
			int number = getInvoiceNumber();
			if ( "T".equals(getInvoiceSeries()) ) {
				number += 1000000;
			} else if ( "F".equals(getInvoiceSeries()) ) {
				number += 1100000;
			} else if ( "G".equals(getInvoiceSeries()) ) {
				number += 1400000;
			}			
			return number;
		}
		return getMainId();
	}
	
	private String getBalancingAccount( AccountEntryDetail aed, boolean registry ) {
		String code = null;
		if ( aed.getBalancingAccount() != null ) {
			code = getAccount(aed.getBalancingAccount()); 
		} else if ( registry ) {
			code = getSalesAccount();
		}
		return code;
	}
	
	private Date getAccountDate() {
		if ( isInvoiceExport() ) {
			return getDate();
		} else {
			Date date = null;
			try {
				if ( getFinance() != null ) {
					date = getLastFinanceTrackingPaidDate(getFinance());
				}
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return (date != null) ? date : getDueDate();
		}
	}
	
	private void writeAccounts( AccountEntry accountEntry, AccountEntryDetail aed, boolean registry ) {
		Object[] data = new Object[32];
		
		boolean vatAccount = isVatAccount(aed.getAccount());
		
		// 01 - ASIEN (N6)
		data[0] = getJournal(accountEntry);
		// 02 - FECHA (D8)
		data[1] = getAccountDate();
		// 03 - SUBCTA (C12)
		data[2] = getAccount(aed.getAccount());
		// 04 - CONTRA (C12)
		data[3] = getBalancingAccount(aed, registry);
		// 05 - PTADEBE (N16, 2)
		data[4] = 0.0;
		// 06 - CONCEPTO (C25)
		String concept = aed.getConcept();
		if ( StringUtils.isEmpty(concept) ) {
			concept = getReferenceCode();
		}		
		data[5] = getString(concept, 25 );
		// 07 - PTAHABER (N16, 2)
		data[6] = 0.0;
		// 08 - FACTURA (N8)
		if ( vatAccount ) {
			data[7] = getFacturaNumber();	
		}
		// 09 - BASEIMPO (N16, 2)
		data[8] = 0.0;
		TaxBreakDown vat = null;
		if ( vatAccount ) {
			vat = getVat();
			if ( vat != null ) {
				// 10 - IVA (N5, 2)
				data[9] = vat.getTaxPercent();	
				// 11 - RECEQUIV (N5, 2)
				data[10] = vat.getSurchargePercent();				
			}
		}
		// 12 - DOCUMENTO (C10)
		data[11] = getString(getReferenceCode(), 10 );
		// 13 - DEPARTA (C3)
		data[12] = null;
		// 14 - CLAVE (C6)
		data[13] = null;
		// 15 - ESTADO (C1)
		data[14] = null;
		// 16 - NCASADO (N6)
		data[15] = 0.0;
		// 17 - TCASADO (N1)
		data[16] = 0.0;
		// 18 - TRANS (N6)
		data[17] = 0.0;
		// 19 - CAMBIO (N16, 6)
		data[18] = 0.0;
		// 20 - DEBEME (N16, 2)
		data[19] = 0.0;
		// 21 - HABERME (N16, 2)
		data[20] = 0.0;
		// 22 - AUXILIAR (C1)
		data[21] = null;
		// 23 - SERIE (C1)
		data[22] = null;
		// 24 - SUCURSAL (C4)
		data[23] = null;
		// 25 - CODDIVISA (C5)
		data[24] = null;
		// 26 - IMPAUXME (N16, 2)
		data[25] = null;
		// 27 - MONEDAUSO (C1)
		data[26] = getString( "2", 1 );
		// 28 - EURODEBE (N16, 2)
		data[27] = aed.getDebit();
		// 29 - EUROHABER (N16, 2)
		data[28] = aed.getCredit();
		// 30 - BASEEURO (N16, 2)
		if ( vat != null ) {
			data[29] = vat.getBase();	
		}
		// 31 - NOCONV (L,1)
		data[30] = null;
		// 32 - NUMEROINV (C10)
		data[31] = null;
		
		this.lines.add(data);
	}
	
	private DBFField[] getDBFFields() throws AonException {
		return new DBFField[] {
				new DBFField("ASIEN", 'N', 6, 0),
				new DBFField("FECHA", 'D', 8, 0),
				new DBFField("SUBCTA", 'C', 12, 0),
				new DBFField("CONTRA", 'C', 12, 0),
				new DBFField("PTADEBE", 'N', 16, 2),
				new DBFField("CONCEPTO", 'C', 25, 0),
				new DBFField("PTAHABER", 'N', 16, 2),
				new DBFField("FACTURA", 'N', 8, 0),
				new DBFField("BASEIMPO", 'N', 16, 2),
				new DBFField("IVA", 'N', 5, 2),
				new DBFField("RECEQUIV", 'N', 5, 2),
				new DBFField("DOCUMENTO", 'C', 10, 0),
				new DBFField("DEPARTA", 'C', 3, 0),
				new DBFField("CLAVE", 'C', 6, 0),
				new DBFField("ESTADO", 'C', 1, 0),
				new DBFField("NCASADO", 'N', 6, 0),
				new DBFField("TCASADO", 'N', 1, 0),
				new DBFField("TRANS", 'N', 6, 0),
				new DBFField("CAMBIO", 'N', 16, 6),
				new DBFField("DEBEME", 'N', 16, 2),
				new DBFField("HABERME", 'N', 16, 2),
				new DBFField("AUXILIAR", 'C', 1, 0),
				new DBFField("SERIE", 'C', 1, 0),
				new DBFField("SUCURSAL", 'C', 4, 0),
				new DBFField("CODDIVISA", 'C', 5, 0),
				new DBFField("IMPAUXME", 'N', 16, 2),
				new DBFField("MONEDAUSO", 'C', 1, 0),
				new DBFField("EURODEBE", 'N', 16, 2),
				new DBFField("EUROHABER", 'N', 16, 2),
				new DBFField("BASEEURO", 'N', 16, 2),
				new DBFField("NOCONV", 'L', 1, 0),
				new DBFField("NUMEROINV", 'C', 10, 0)
				};
	}

	private byte[] getData( List<Object[]> data, DBFField[] fields ) throws AonException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DBFWriter writer = new DBFWriter(out, fields, data.size());
		writer.setEncoding(OUTPUT_ENCODING);
		for( Object[] record : data ) {
			writer.addRecord(record);
		}
		writer.close();
		return out.toByteArray();
	}
		
	@Override
	public Map<String, File> getDataMap() {
		Map<String, File> map = new HashMap<String, File>();
		try {
			addData(map, "diarfc", DBF_SUFFIX, getData(lines, getDBFFields()));
		} catch (AonException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return map;
	}
	
}