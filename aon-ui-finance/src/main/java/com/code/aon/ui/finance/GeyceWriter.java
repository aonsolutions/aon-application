package com.code.aon.ui.finance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.StreetType;

public class GeyceWriter extends BasicExporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GeyceWriter.class.getName());

	private static final String[] ACCOUNT_CODES = new String[] {
		"100", "1030", "1040", "110", "1110", "1140", "120", "130", "1340", "1370", "140", 
			"150", "160", "170", "180", "190",
		"200", "210", "220", "230", "240", "250", "2550", "260", "2800", "2900", "2910", "2920",
		"300", "310", "320", "330", "340", "350", "360", "390",
		"4000", "4030", "4100", "4300", "4310", "4330", "4400", "4410", "4700", "4740", "480", "490",
		"500", "5090", "510", "5200", "5290", "530", "540", "550", "5530", "5580", "5590", "560",
			"570", "580", "5990",
		"600", "6060", "6080", "6090", "610", "620", "6300", "640", "6440", "6450", "650", "6510",
			"6610", "6620", "6630", "6640", "6650", "6660", "6670", "670", "680", "690", "6930",
			"6960", "6970", "6980", "6990",
		"700", "7060", "7080", "7090", "710", "740", "7510", "7600", "7610", "7620", "7630", "7660", "770", "790",
			"7930", "7950", "7960", "7970", "7980", "7990", 
		"800", "810", "820", "8300", "840", "850", "860",
		"900", "910", "920", "940", "950", "960"
	};
	
	private static final int GYCCON_SIZE = 252;
	
	private static final int GYCPLAN_SIZE = 141;
	
	private ByteArrayOutputStream outGycPlan;
	
	private Set<String> exportedAccounts;
	
	public GeyceWriter(InvoiceExportConfiguration configuration) {
		super(configuration);
		this.outGycPlan = new ByteArrayOutputStream();
		this.exportedAccounts = new HashSet<String>();
	}

	@Override
	protected boolean isSkipAccount(Account account) {
		return false;
	}
	
	@Override
	public String getFileName() {
		return "geyce.zip";
	}

	@Override
	public void init(Invoice invoice) throws ManagerBeanException, IOException {
		super.init(invoice);
		writeGycPlan();
	}

	private void setNumber( double value, int offset, int maxLength ) {
		double _value = CommonUtil.round(value);
		String pattern = StringUtils.leftPad("0.00", maxLength, "0");
		DecimalFormat df = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.ENGLISH));
		String string = df.format(_value);
		setString(string, offset, maxLength);
	}
	
	private void resetLine() {
		Arrays.fill(getLine(), 63, getLine().length, (byte) ' ');
	}
	
	private String getTipoDeOperacion() {
		String result = "IN";
		VatDeductionType vdt = getVatDeductionType();
		if ( vdt == VatDeductionType.WITH_RIGHT ) {
			switch ( getInvoice().getTransaction() ) {
				case INTRACOMMUNITY:
					result = "EN";
					break;
				case EXTRACOMMUNITY:
					result = "EX";
					break;
				case CAN_CEU_MEL:
				case NATIONAL:
				case OTHER_ISP:
					result = "IN";
					break;					
			}
		} else if ( vdt == VatDeductionType.WITHOUT_RIGHT ) {
			result = "ND";
		} else if ( vdt == VatDeductionType.NON_TAXABLE) {
			result = "OE";
		} else {
		}
		return result;
	}
	
	private void initLine() {
		setLine( new byte[GYCCON_SIZE] );
		Arrays.fill(getLine(), (byte) ' ');
		// Codigo de Empresa
		setStringLeftPad( getConfiguration().getEnterpriseCode(), 0, 6);
		// Fecha asiento
		setDate(getAccountEntry().getEntryDate(), 6);
		// Contador de Numero de asiento
		setStringLeftPad( getJournal().toString(), 14, 6);		
		// Numero de Diario Contable
		setStringLeftPad( getConfiguration().getJournal(getInvoice().getType()), 24, 2);		
		// Numero de Factura
		setStringRightPad( getInvoice().getId().toString(), 26, 7);
		// Descripcion de la Factura
		setStringRightPad( getInvoice().getReferenceCode(), 33, 30);
		// Acumula 347 S/N
		setString("S", 90, 1);
		// Acumula 349 S/N
		setString("S", 91, 1);
		// Indica si IVA o IGIC
		setString("I", 92, 1);
		// Repercutido o Soportado
		if ( getInvoice().getType() == InvoiceType.SALES ) {
			setString("R", 93, 1);
		} else {
			setString("S", 93, 1);
		}
		// Fecha documento IVA
		setDate(getInvoice().getTaxDate(), 95);
		// Tipo de Operación
		setString(getTipoDeOperacion(), 103, 2);
		// Operaciones Especificas
		setString("RG", 105, 2);
		// Descripcion
		setStringRightPad( getInvoice().getRegistryName(), 107, 30);
		// NIF/CIF
		setStringRightPad( getInvoice().getRegistryDocument(), 137, 15);
		// Tipo de Moneda 
		setString("E", 215, 1);
	}
	
	private void fillLine( AccountEntryDetail aed ) {
		// Contador de Lineas de asiento
		setStringLeftPad( String.valueOf(aed.getLine()), 20, 4);
		// Indivativo de DEBE o HABER
		double amount = 0;
		if ( aed.getCredit() == 0 ) {
			setString("D", 63, 1);
			amount = aed.getDebit();
		} else {
			setString("H", 63, 1);
			amount = aed.getCredit();
		}
		String[] cuenta = getCuenta(aed.getAccount().getCode());
		// Cuenta
		setStringRightPad( cuenta[0], 64, 4);
		// Codigo de Subcuenta
		setStringRightPad( cuenta[1], 68, 10);
		// Importe
		setNumber( amount, 78, 12);
	}
	
	private void fillLine( TaxBreakDown tbd ) {
		if ( tbd.getTaxType() == TaxType.VAT ) {
			// Porcentaje de IVA
			setNumber( tbd.getTaxPercent(), 152, 5);
			if ( tbd.getSurchargePercent() != 0 ) {
				// Porcentaje de Recargo
				setNumber( tbd.getSurchargePercent(), 152, 5);							
			}
			// Base Imponible
			setNumber( tbd.getBase(), 167, 10);
			// Importe IVA
			setNumber( tbd.getTaxQuota(), 177, 10);
			if ( tbd.getSurchargeQuota() != 0 ) {
				// Importe Recargo
				setNumber( tbd.getSurchargeQuota(), 187, 10);							
			}
		} else if ( tbd.getTaxType() == TaxType.RETENTION ) {
			// Porcentaje de IRPF
			setNumber( tbd.getTaxPercent(), 162, 5);
			// Importe IRPF
			setNumber( tbd.getTaxQuota(), 197, 10);
			// Base IRPF
			setNumber( tbd.getBase(), 216, 10);
		}
	}
	
	private void writeDetailWithTaxes() throws IOException {
		TaxBreakDown tbd = getNextTax(getTaxBreakDowns());
		fillLine(tbd);
		TaxBreakDown tbd2 = getRelatedTax(getTaxBreakDowns(), tbd);
		if ( tbd2 != null ) {
			fillLine(tbd2);
		}
		writeLine();
	}	
	
	private String getSiglasViaPublica( StreetType type ) {
		String value = "CL";
		if ( type != null ) {
			value = type.getValue();
		}
		return value;
	}	
	
	private String shortCuentaCode( String cuenta ) {
		if ( StringUtils.endsWith(cuenta, "0") && (!ArrayUtils.contains(ACCOUNT_CODES, cuenta)) ) {
			return StringUtils.substringBeforeLast(cuenta, "0");
		}
		return cuenta;
	}
	
	private String[] getCuenta( String code ) {
		String cuenta = shortCuentaCode(StringUtils.substring(code, 0, 4));
		cuenta = shortCuentaCode(cuenta);
		String subCuenta = StringUtils.trimToNull(StringUtils.substring(code, 4));
		if ( NumberUtils.isDigits(subCuenta) && (NumberUtils.toInt(subCuenta) == 0) ) {
			subCuenta = "00";
		}
		return new String[]{cuenta, subCuenta};
	}
	
	private void writeGycPlan() throws IOException {
		writeGycPlan( getRegistryDetail(), true );
		for( AccountEntryDetail aed : getDetails() ) {
			writeGycPlan( aed, false );
		}
	}
	
	private void writeGycPlan( AccountEntryDetail aed, boolean registry ) throws IOException {
		String code = aed.getAccount().getCode();
		if ( exportedAccounts.contains(code) ) {
			return;
		}
		this.exportedAccounts.add(code);
		
		setLine( new byte[GYCPLAN_SIZE] );
		Arrays.fill(getLine(), (byte) ' ');
		
		// Codigo de Empresa
		setStringLeftPad( getConfiguration().getEnterpriseCode(), 0, 6);
		String[] cuenta = getCuenta(aed.getAccount().getCode());
		// Cuenta
		setStringRightPad( cuenta[0], 6, 4);
		// Codigo de Subcuenta
		setStringRightPad( cuenta[1], 10, 10);
		if ( registry ) {
			// Descripcion
			setStringRightPad(getInvoice().getRegistryName(), 20, 30);
			// NIF
			setStringLeftPad(getInvoice().getRegistryDocument(), 50, 15);
			try {
				RegistryAddress address = getInvoice().getRegistry().getDefaultAddress();
				if ( address != null ) {
					// Siglas
					setStringLeftPad(getSiglasViaPublica(address.getStreetType()), 65, 2);				
					// Calle			
					setStringRightPad(address.getAddress(), 67, 30);
					// Numero		
					setStringLeftPad( address.getNumber(), 97, 5);
					// Codigo Postal		
					setStringRightPad( address.getZip(), 102, 5);
					// Municipio		
					setStringRightPad( address.getCity(), 107, 30);
					// Codigo de Provincia		
					setStringLeftPad( StringUtils.substring(address.getZip(), 0, 2), 137, 2);	
				}
			} catch (ManagerBeanException e) {
				LOGGER.error( "Error obtaining registry address", e ); 
			}
			// Se lista 347 S/N
			setString("S", 139, 1);
			// Se lista 349 S/N
			setString("S", 140, 1);			
		} else {
			// Descripcion
			setStringRightPad(aed.getAccount().getDescription(), 20, 30);			
		}

		if ( this.outGycPlan.size() > 0 ) {
			writeNewLine(this.outGycPlan);
		}
		this.outGycPlan.write(getLine());
	}
	
	private void writeRegistryDetail() throws IOException {
		AccountEntryDetail aed = getRegistryDetail();
		fillLine(aed);
		boolean lineWritten = false;
		while (! getTaxBreakDowns().isEmpty() ) {
			writeDetailWithTaxes();
			if (! getTaxBreakDowns().isEmpty() ) {
				writeNewLine();
			}
			lineWritten = true;
		}
		if (! lineWritten) {
			writeLine();
		}
	}

	private void writeDetail() throws IOException {
		AccountEntryDetail aed = getNextDetail();
		resetLine();
		fillLine(aed);
		writeLine();
	}	
	
	@Override
	public void write() throws IOException, ManagerBeanException {
		initLine();
		writeRegistryDetail();
		while (! getDetails().isEmpty() ) {
			writeNewLine();
			writeDetail();
		}
	}

	@Override
	public Map<String, byte[]> getDataMap() {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		map.put("gyccon.txt", getData());
		map.put("gycplan.txt", this.outGycPlan.toByteArray());
		return map;
	}
	
}