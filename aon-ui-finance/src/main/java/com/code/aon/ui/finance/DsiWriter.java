package com.code.aon.ui.finance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.finance.Finance;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;

public class DsiWriter extends BasicExporter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(DsiWriter.class);

	public static final String DBF_SUFFIX = ".dbf";
	
	private static final String NO_FISCAL = "NO_FISCAL";

	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private Map<Integer,List<Object[]>> accountEntryMap;
	
	private Map<Integer,List<Object[]>> accountEntryDetailMap;

	private Map<Integer,List<Object[]>> facturasEmitidasMap;
	
	private Map<Integer,List<Object[]>> facturasEmitidasDetailMap;
	
	private Map<Integer,List<Object[]>> facturasRecibidasMap;
	
	private Map<Integer,List<Object[]>> facturasRecibidasDetailMap;
	
	private Map<Integer,List<Object[]>> carteraCobrosMap;
	
	private Map<Integer,List<Object[]>> accountMap;
	
	private Set<String> exportedAccounts;
	
	private int line;
	
	public DsiWriter(InvoiceExportConfiguration configuration) {
		super(configuration);
		this.accountEntryMap = new HashMap<Integer, List<Object[]>>();
		this.accountEntryDetailMap = new HashMap<Integer, List<Object[]>>();
		this.facturasEmitidasMap = new HashMap<Integer, List<Object[]>>();
		this.facturasEmitidasDetailMap = new HashMap<Integer, List<Object[]>>();
		this.facturasRecibidasMap = new HashMap<Integer, List<Object[]>>();
		this.facturasRecibidasDetailMap = new HashMap<Integer, List<Object[]>>();
		this.carteraCobrosMap = new HashMap<Integer, List<Object[]>>();
		this.accountMap = new HashMap<Integer, List<Object[]>>();
		this.exportedAccounts = new HashSet<String>();		
	}

	@Override
	public InvoiceExportType getType() {
		return InvoiceExportType.DSI_GESTION;
	}
	
	@Override
	public String getFileName() {
		return "dsi-gestion.zip";
	}
	
	@Override
	protected boolean isSkipAccount(Account account) {
		if (! isInvoiceExport() ) {
			return false;			
		}
		return super.isSkipAccount(account);
	}	

	private void addData( Map<Integer,List<Object[]>> dataMap, Object[] data, Date date) {
		int year = CommonUtil.getYear(date) % 100;
		List<Object[]> list = dataMap.get(year);
		if ( list == null ) {
			list = new LinkedList<Object[]>();
			dataMap.put(year, list);
		}
		list.add(data);
	}
	
	private String getString( String value, int maxLength) {
		String _value = StringUtils.trimToNull(value);
		if ( _value != null ) {
			_value = StringUtils.substring(_value, 0, maxLength);
		}		
		return _value;
	}
	
	private String getInteger( Integer value, int maxLength ) {
		String string = (value != null) ? value.toString() : null;
		return getString(StringUtils.leftPad(string, maxLength, "0"), maxLength);
	}

	private String getDate( Date date ) {
		return (date!=null) ? DATE_FORMAT.format(date) : null;
	}
	
	private void appendEntry( AccountEntry accountEntry ) throws IOException {
		Object[] data = new Object[9];
		
		// 1 - CODIGO CHAR(6), Numero de Asiento (temporal)
		data[0] = getInteger(getJournal(accountEntry), 6);
		// 2 - FECHA DATE, Fecha del asiento
		data[1] = accountEntry.getEntryDate();
		// 3 - DIARIO CHAR(2), Codigo de Diario
		data[2] = getString("01", 2);
		// 4 - CODCON CHAR(3), Codigo de concepto (opcional)
		data[3] = null;
		// 5 - CONCEP CHAR(37), Descripción Concepto (opcional)
		String concept = accountEntry.getComments();
		if ( StringUtils.isEmpty(concept) ) {
			concept = getRegistryName();
		}
		data[4] = getString(concept, 37);
		// 6 - DOCUM CHAR(5), Numero de documento (opcional)
		data[5] = getInteger(accountEntry.getId(), 5);
		// 7 - DEBE NUMERIC(20,3), Total debe
		data[6] = null;
		// 8 - HABER NUMERIC(20,3), Total haber
		data[7] = null;
		// 9 - ASIENTO CHAR(6), Codigo de asiento real
		data[8] = null;
		
		addData(this.accountEntryMap, data, accountEntry.getEntryDate());
	}
	
	private int nextLine( AccountEntryDetail aed ) {
		if ( aed.getLine() > 0 ) {
			return aed.getLine();
		}
		return line++;
	}
	
	private void appendEntryDetail( AccountEntry accountEntry, AccountEntryDetail aed ) throws IOException {
		Object[] data = new Object[11];
		
		// 1 - CODIGO CHAR(6), Numero de Asiento (temporal)
		data[0] = getInteger(getJournal(accountEntry), 6);
		// 2 - LINEA CHAR(5), Numero de linea de asiento
		data[1] = getInteger(nextLine(aed), 5);
		// 3 - CUENTA CHAR(12), Cuenta contable
		data[2] = getString(aed.getAccount().getCode(), 12);
		// 4 - CODCON CHAR(3), Codigo de concepto (opcional)
		data[3] = null;
		// 5 - CONCEP CHAR(37), Descripcion concepto
		String concept = aed.getConcept();
		if ( StringUtils.isEmpty(concept) ) {
			concept = getReferenceCode();
		}
		data[4] = getString(concept, 37);
		// 6 - DH CHAR(1), Debe o Haber (D/H)
		double amount = 0;
		if ( aed.getCredit() == 0 ) {
			data[5] = getString("D", 1);
			amount = aed.getDebit();
		} else {
			data[5] = getString("H", 1);
			amount = aed.getCredit();
		}
		// 7 - IMPORTE NUMERIC(20,3), Importe
		data[6] = amount;
		// 8 - SUBDEP CHAR(4), Codigo Subdepartamento (opcional)
		data[7] = null;
		// 9 - DEPART CHAR(3), Codigo Departamento (opcional)
		data[8] = null;
		// 10 - T347 CHAR(1), Marca para el Modelo 347: (opcional) Ventas / Compras / Mediaciones
		data[9] = null;
		// 11 - PUNTEO CHAR(1), Marca para el punteo (debe ser una de las configuradas en contabilidad)
		data[10] = null;
		
		addData(this.accountEntryDetailMap, data, accountEntry.getEntryDate());
	}
	
	private String getTipoFacturaEmitida() {
		// Normal
		String result = "1";
		VatDeductionType vdt = getVatDeductionType();
		if ( vdt == VatDeductionType.WITH_RIGHT ) {
			switch ( getTransaction() ) {
				case INTRACOMMUNITY:
					// Intracomunitarios
					result = "3";
					break;
				case EXTRACOMMUNITY:
					// Exportacion
					result = "4";
					break;
				default:
					result = "1";
			}
		} else if ( vdt == VatDeductionType.NON_TAXABLE) {
			// Exento
			result = "2";
		}
		return result;
	}	
	
	private String getTipoFacturaRecibida() {
		// Normal
		String result = "1";
		VatDeductionType vdt = getVatDeductionType();
		if ( isWithholdingFarmer() ) {
			// Agricultura
			result = "5";
		} else if ( vdt != VatDeductionType.WITH_RIGHT) {
			// Inversion
			result = "2";
		} else {
			switch ( getTransaction() ) {
				case INTRACOMMUNITY:
					// Intracomunitarios
					result = "3";
					break;
				case EXTRACOMMUNITY:
					// Importacion
					result = "4";
					break;		
				case OTHER_ISP:
					// Invers. Suj. Pas. O.I.
					result = "6";
					break;
				default:
					result = "1";
			}
		}
		return result;
	}		
	
	private List<TaxBreakDown> getVats( AccountEntryDetail aed ) {
		List<TaxBreakDown> list = new LinkedList<TaxBreakDown>();
		try {
			for( TaxBreakDown tbd : getInvoiceTaxes(aed) ) {
				if ( tbd.isVat() ) {
					list.add(tbd);
				}
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error( e.getMessage(), e );
		}
		return list;
	}

	private void appendDetails( Object[] data, int amountIndex, int vatIndex ) throws IOException {
		int amountCount = 0;
		int vatCount = 0;
		for( AccountEntryDetail aed : getDetails() ) {
			if ( amountCount < 8 ) {
				// Total importes (20,3)
				double amount = (isSales()) ? (aed.getCredit() - aed.getDebit()) : (aed.getDebit() - aed.getCredit());
				data[amountIndex+amountCount++] = amount;
				// Cuenta importes (12)
				data[amountIndex+amountCount++] = getString(aed.getAccount().getCode(), 12);				
			}
			List<TaxBreakDown> vats = getVats(aed);
			if ( vats != null ) {
				int line = 0;
				boolean retention = (getRetentionTax() != null);
				for( TaxBreakDown tbd : vats ) {
					String vatType = getVatType(tbd);
					if ( isSales() ) {
						writeFacturaEmitidaDetails(aed, vatType, retention,  ++line);
					} else {
						writeFacturaRecibidaDetails(aed, vatType, retention, ++line);
					}								
					if ( vatCount < 8 ) {
						// Codigo Tipo de IVA 1 (2)
						data[vatIndex+vatCount++] = getString(vatType, 2);
						// vatIndex Imponible (20)
						data[vatIndex+vatCount++] = tbd.getBase();
						// Importe IVA (20);
						data[vatIndex+vatCount++] = tbd.getTaxQuota();
						// Importe Recargo (20);
						data[vatIndex+vatCount++] = tbd.getSurchargeQuota();
					}
				}				
			}
		}		
	}
	
	private String getVatType( TaxBreakDown tbd ) {
		String type = "01";
		for( int i = 0; i < getConfiguration().getTaxs().length; i++ ) {
			Tax tax = getConfiguration().getTaxs()[i];
			if ( (tbd.getTaxPercent() == tax.getPercentage()) &&
				(tbd.getVatDeductionType() == tax.getVatDeductionType()) ) {
				Integer vatCode = getConfiguration().getVats()[i];
				return StringUtils.leftPad(vatCode.toString(), 2, '0');
			}
		}
		return type;
	}
	
	private TaxBreakDown getRetentionTax() {
		for ( TaxBreakDown tax : getTaxBreakDowns() ) {
			if ( tax.isRetention() ) {
				return tax;
			}
		}
		return null;
	}
	
	private AccountEntryDetail getRetentionDetail( AccountEntry accountEntry ) {
		for( AccountEntryDetail aed : accountEntry.getDetail() ) {
			if ( isRetention(aed.getAccount()) ) {			
				return aed;
			}
		}		
		return null;
	}
	
	private void writeFacturaEmitidaFinances() throws IOException {
		int line = 1;
		
		for( Finance finance : getFinances() ) {
			Object[] data = new Object[10];
			
			int year = CommonUtil.getYear(getDate());
			// 1 - EJERCIC CHAR(4), Ejercicio origen del efecto
			data[0] = getInteger(year, 4);
			// 2 - SERIE CHAR(3), Serie de la factura
			data[1] = getString(StringUtils.right(getInvoice().getSeries(), 3), 3);
			// 3 - NUMFAC CHAR(5), Numero de la factura (5)
			data[2] = getInteger(getInvoice().getNumber(), 5);
			// 4 - NUMEFE CHAR(2), Numero de Efecto
			data[3] = getInteger(line++, 2);
			// 5 - CARTERA CHAR(3), Codigo de Cartera de cobros
			data[4] = getString(getCarteraCobros(), 3);
			// 6 - FECHA CHAR(10), Fecha de Emisión
			data[5] = getDate(getDate());
			// 7 - VTO CHAR(10), Fecha de Vencimiento
			data[6] = getDate(finance.getDueDate());
			// 8 - CLIPRO CHAR(12), Cuenta contable del cliente
			data[7] = getString(getRegistryDetail().getAccount().getCode(), 12);
			// 9 - IMPORTE NUMERIC(20,3), Importe del efecto
			data[8] = finance.getAmount();
			// 10 - CONTA CHAR(1), Contabilizado S/N			
			data[9] = getString("S", 1);
			
			addData(this.carteraCobrosMap, data, getDate());
		}		
	}		
	
	private void writeFacturaEmitida( AccountEntry accountEntry ) throws IOException {
		Object[] data = new Object[52];
		
		// 1 - REGIST CHAR(3), Serie de la factura
		data[0] = getString(StringUtils.right(getInvoice().getSeries(), 3), 3);
		// 2 - FRA CHAR(5), Numero Factura
		data[1] = getInteger(getInvoice().getNumber(), 5);
		// 3 - FECHA_F CHAR(10), Fecha Factura
		data[2] = getDate(getDate());
		// 4 - CUENTA CHAR(12), Cuenta contable del cliente
		data[3] = getString(getRegistryDetail().getAccount().getCode(), 12);
		// 5 - TIPO	CHAR(1), Tipo Factura
		data[4] = getString(getTipoFacturaEmitida(), 1);	
		
		appendDetails(data, 5, 16);

		TaxBreakDown retentionTax = getRetentionTax();
		if ( retentionTax != null ) {
			// 33  -PORRET NUMERIC(5,2), Porcentaje de Retencion
			data[32] = retentionTax.getTaxPercent();
			String debeHaber = "D";
			String accountCode =null;
			AccountEntryDetail aed = getRetentionDetail(accountEntry);
			if ( aed != null ) {
				if ( aed.getCredit() != 0 ) {
					debeHaber = "H";
				}
				accountCode = aed.getAccount().getCode();
			}
			// 38 - DHRET CHAR(1), Debe/Haber retencion (1)
			data[37] = debeHaber; 
			// 39 - IMPRET NUMERIC(20,3), Importe Retencion
			data[38] = retentionTax.getTaxQuota();
			// 40 - CTARET CHAR(12), Cuenta contable retención
			data[39] = getString(accountCode, 12);		
		}
		String retention = (retentionTax != null) ? "S" : "N";
		// 34 _ RET1_SN CHAR(1), Aplicar retención importe 1
		data[33] = retention; 
		// 35 - RET2_SN CHAR(1), Aplicar retención importe 2
		data[34] = retention; 
		// 36 - RET3_SN CHAR(1), Aplicar retención importe 3
		data[35] = retention; 
		// 37 - RET4_SN CHAR(1), Aplicar retención importe 4
		data[36] = retention; 
			
		// 41 - TOTFRA NUMERIC(20,3), Importe Total Factura
		data[40] = getTotal();
		// 42 - SUBDEP CHAR(4), Subdepartamento
		data[41] = "";
		// 43 - DEPART CHAR(3), Departamento
		data[42] = "";
		// 44 - PUNTEO CHAR(1), Marca para el punteo
		data[43] = "0"; 
		// 45 - DOCUM CHAR(5), Numero de Documento
		data[44] = null; 
		// 46 - DIARIO CHAR(2), Codigo del Diario
		data[45] = "01";
		// 47 - ASIENTO CHAR(6), Codigo de asiento real creado
		data[46] = null; 
		// 48 - NOMBRE CHAR(30), Nombre del cliente
		data[47] = getString(getRegistryName(), 30);
		// 49 - NIF CHAR(15), NIF del cliente
		if ( getRegistryDocument() != null ) {
			data[48] = getString(getRegistryDocument().getDocument(), 15);	
		} 
		// 50 - DTOREC CHAR(1), DescuentoPP ('D') o Rec.Financiero ('R')
		data[49] = "D"; 
		// 51 - FECHA_A CHAR(10), Fecha Asiento
		data[50] = getDate(accountEntry.getEntryDate());
		// 52 - RECC CHAR(1), Factura acogida al Reg. Crit. Caja (S/N)
		data[51] = "N";
		
		addData(this.facturasEmitidasMap, data, getDate());

		writeFacturaEmitidaFinances();
	}
	
	private void writeFacturaEmitidaDetails( AccountEntryDetail aed, String vatType, boolean retention, int line ) throws IOException {
		Object[] data = new Object[8];
			
		// 1 - REGIST CHAR(3), Serie de la factura
		data[0] = getString(StringUtils.right(getInvoice().getSeries(), 3), 3);
		// 2 - FRA CHAR(5), Numero Factura
		data[1] = getInteger(getInvoice().getNumber(), 5);
		// 3 - LINEA CHAR(4), Numero de Linea
		data[2] = getInteger(line++, 4);
		// 4 - TIVA CHAR(2), Codigo Tipo de Iva
		data[3] = getString(vatType, 2);
		// 5 - CUENTA CHAR(12), Cuenta contable de ingresos
		data[4] = getString(aed.getAccount().getCode(), 12);
		// 6 - IMPORTE NUMERIC(20,3), Importe
		double amount = (isSales()) ? (aed.getCredit() - aed.getDebit()) : (aed.getDebit() - aed.getCredit());
		data[5] = amount;
		// 7 - RETENSN CHAR(1), Retencion S/N
		data[6] = retention ? "S" : "N";
		// 8 - M347SN CHAR(1), Modelo 347 S/N
		data[7] = "S";

		addData(this.facturasEmitidasDetailMap, data, getDate());
	}
	
	private void appendFacturaRecibidaFinaces( Object[] data, int start ) {
		int index = 0;
		for( Finance finance : getFinances() ) {
			// Fecha vencimiento (10)
			data[start+index++] = getDate(finance.getDueDate());
			// Importe vencimiento (20)
			data[start+index++] = finance.getAmount(); 
			if ( index == 24 ) {
				break;
			}
		}		
	}	
	
	private String getCarteraPorDefecto() {
		String cartera = getConfiguration().getGeneralJournal();
		if ( StringUtils.isEmpty(cartera) ) {
			cartera = "001";
		}
		return StringUtils.leftPad(cartera, 3, '0');
	}
	
	private String getCarteraCobros() {
		String cartera = getConfiguration().getSalesJournal();
		if ( StringUtils.isEmpty(cartera) ) {
			cartera = getCarteraPorDefecto();
		}
		return StringUtils.leftPad(cartera, 3, '0');
	}

	private String getCarteraPagos() {
		String cartera = getConfiguration().getPurchaseJournal();
		if ( StringUtils.isEmpty(cartera) ) {
			cartera = getCarteraPorDefecto();
		}
		return StringUtils.leftPad(cartera, 3, '0');
	}
	
	private void writeFacturaRecibidaDetails( AccountEntryDetail aed, String vatType, boolean retention, int line ) throws IOException {
		Object[] data = new Object[12];
		
		// 1 - REGIST CHAR(5), Numero de orden de iva soportado
		data[0] = !isUndeductible() ? getInteger(getInvoice().getNumber(), 5) : "";
		// 2 - LINEA CHAR(4), Numero de Linea
		data[1] = getInteger(line++, 4);
		// 3 - TIVA CHAR(2), Codigo Tipo de Iva
		data[2] = getString(vatType, 2);
		// 4 - CUENTA CHAR(12), Cuenta contable de Gastos
		data[3] = getString(aed.getAccount().getCode(), 12);
		// 5 - IMPORTE NUMERIC(20,3), Importe
		double amount = (isSales()) ? (aed.getCredit() - aed.getDebit()) : (aed.getDebit() - aed.getCredit());
		data[4] = amount;
		// 6 - SUBDEP CHAR(4), Subdepartamento
		data[5] = "";
		// 7 - DEPART CHAR(3), Departamento
		data[6] = "";
		// 8 - RETENSN CHAR(1, Retencion S/N
		data[7] =  retention ? "S" : "N";
		// 9 - M347SN CHAR(1), Modelo 347 S/N
		data[8] = "S";
		// 10 - IVAPRO NUMERIC(20,3), Valor fijo = 0
		data[9] = 0.0;
		// 11 - PDESGLO CHAR(6), Valor fijo = ''
		data[10] = "";
		// 12 - IVAPROT NUMERIC(20,3), Valor fijo = 0
		data[11] = 0.0;

		addData(this.facturasRecibidasDetailMap, data, getDate());
	}
		
	private void writeFacturaRecibida( AccountEntry accountEntry ) throws IOException {
		Object[] data = new Object[67];
		
		// 1 - REGIST CHAR(5), Numero de orden de iva soportado
		data[0] = !isUndeductible() ? getInteger(getInvoice().getNumber(), 5) : "";
		// 2 - FRA CHAR(10), Numero de Factura del Proveedor
		data[1] = getString(getReferenceCode(), 15);
		// 3 - FECHA_F CHAR(10), Fecha Factura
		data[2] = getDate(getDate());
		// 4 - FECHA_A CHAR(10), Fecha Asiento
		data[3] = getDate(accountEntry.getEntryDate());
		// 5 - CUENTA CHAR(12), Cuenta Contable del Proveedor
		data[4] = getString(getRegistryDetail().getAccount().getCode(), 12);
		// 6 - TIPO CHAR(1), Tipo Factura
		data[5] = getString(getTipoFacturaRecibida(), 1);	
		
		appendDetails(data, 6, 17);
		
		// 34 - TOTFRA NUMERIC(20,3), Total Factura
		data[33] = getTotal();
		TaxBreakDown retentionTax = getRetentionTax();
		if ( retentionTax != null ) {
			// 35 - CTARET CHAR(12), Cuenta Retencion (solo si hay tabla de lineas de desglose)
			AccountEntryDetail aed = getRetentionDetail(accountEntry);
			if ( aed != null ) {
				data[34] = getString(aed.getAccount().getCode(), 12);	
			}
			// 36 - PRET NUMERIC(5,2), Porcentaje Retencion (solo si hay tabla de lineas de desglose)
			data[35] = retentionTax.getTaxPercent();
		}
		// 37 - CARTERA CHAR(3), Codigo Cartera de Pagos
		data[36] = getString(getCarteraPagos(), 3); 
		
		appendFacturaRecibidaFinaces(data, 37);

		// 62 - DTOREC CHAR(1), Indica si lleva Descuento ('D') o Recargo Financiero ('R')
		data[61] = getString("D", 1); 
		// 63 - DOCUM CHAR(5), Documento
		data[62] = null; 
		// 64 - DIARIO CHAR(2), Codigo de Diario
		data[63] = "01"; 
		// 65 - ASIENTO CHAR(6), Codigo asiento real
		data[64] = null; 
		// 66 - NUMORDEN CHAR(5), Numero orden real en el libro de IVA
		data[65] = null; 
		// 67 - RECC CHAR(1), Factura acogida al Reg. Crit. Caja (S/N)
		data[66] = getString("N", 1); 
		
		addData(this.facturasRecibidasMap, data, getDate());
	}
	
	private void writeInvoice( AccountEntry accountEntry ) throws IOException {
		if ( getInvoice() != null ) {
			if ( isSales() ) {
				writeFacturaEmitida(accountEntry);
			} else {
				writeFacturaRecibida(accountEntry);
			}			
		}
	}
	
	@Override
	public void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException {
		this.line = 1;
		writeAccounts();
		if (! isInvoiceExport() ) {
			appendEntry(accountEntry);
			if ( getRegistryDetail() != null ) {
				appendEntryDetail( accountEntry, getRegistryDetail() );	
			}
			while (! getDetails().isEmpty() ) {
				AccountEntryDetail aed = getDetails().get(0);
				getDetails().remove(0);
				appendEntryDetail( accountEntry, aed );
			}			
		} else {
			writeInvoice(accountEntry);	
		}
	}

	private void writeAccounts() throws IOException {
		if ( (getRegistryDetail() != null) && !isExported(getRegistryDetail()) ) {
			writeAccounts( getRegistryDetail(), true );
		}
		for( AccountEntryDetail aed : getDetails() ) {
			if (! isExported(aed) ) {
				writeAccounts( aed, false );				
			}
		}
	}
	
	private boolean isExported( AccountEntryDetail aed ) {
		String code = aed.getAccount().getCode();
		return exportedAccounts.contains(code);
	}
	
	private void writeAccounts( AccountEntryDetail aed, boolean registry ) throws IOException {
		String[] data = new String[17];
		
		// 1 - CODIGO CHAR(12), Código cuenta contable
		data[0] = getString( aed.getAccount().getCode(), 12 );
		// 2 - NOMBRE CHAR(30), Nombre
		data[1] = getString( aed.getAccount().getDescription(), 30 );
		// 3 - NIF CHAR(15), NIF
		if ( registry && (getRegistryDocument() != null) ) {
			data[2] = getString( getRegistryDocument().getDocument(), 15 );
		} else {
			data[2] = NO_FISCAL;
		}
		if ( registry ) {
			RegistryAddress address = getRegistryAddress();
			if ( getRegistryAddress() != null ) {
				// 4 - CP CHAR(5), Codigo Postal		
				data[3] = getString( address.getZip(), 5 );
				// 5 - MUNICIP CHAR(18), Poblacion 		
				data[4] = getString( address.getCity(), 18 );
				// 6 - PROVINC CHAR(20), Provincia				
				data[5] = getString( address.getProvince(), 20 );
				// 7 - SG CHAR(3), Siglas de la via (p.e.:CL,AV)		
				String siglas = (address.getStreetType()!=null) ? address.getStreetType().getValue() : null; 
				data[6] = getString( siglas, 3 );
				// 8 - DIRECCI CHAR(50), Direccion			
				data[7] = getString( address.getAddress(), 50 );
				// 9 - NUMERO CHAR(6), Numero de portal
				data[8] = getString( address.getNumber(), 6 );
			}
			try {
				RegistryMedia phone = getRegistry().getPhone();
				if ( phone != null ) {
					// 11 - TELEFON CHAR(15), Telefono
					data[10] = getString( phone.getValue(), 15 );
				}				
				RegistryMedia cellular = getRegistry().getCellular();
				if ( cellular != null ) {
					// 12 - MOVIL CHAR(15), Telefono movil
					data[11] = getString( cellular.getValue(), 15 );
				}				
				RegistryMedia fax = getRegistry().getFax();
				if ( fax != null ) {
					// 13 - FAX CHAR(15), Fax
					data[12] = getString( fax.getValue(), 15 );
				}				
				RegistryMedia email = getRegistry().getEmail();
				if ( email != null ) {
					// 14 - DIRMAIL CHAR(254), Direccion de correo electronico
					data[13] = getString( email.getValue(), 254 );
				}				
				RegistryMedia web = getRegistry().getWeb();
				if ( web != null ) {
					// 15 - DIRNET CHAR(254), Direccion de Internet
					data[14] = getString( web.getValue(), 254 );
				}				
			} catch ( ManagerBeanException e ) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		// 16 - CARTERA CHAR(3), Codigo Cartera por defecto
		data[15] = getString( getCarteraPorDefecto(), 3 );
		// 17 - ACTUALIZAR CHAR(1), Actualizar datos si existe S/N
		data[16] = getString( "S", 1 );


		addData(this.accountMap, data, aed.getAccountEntry().getEntryDate());
		this.exportedAccounts.add(aed.getAccount().getCode());		
	}
	
	private DBFField[] getAccounFields() throws AonException {
		return new DBFField[] {
				new DBFField("CODIGO", 'C', 12, 0),
				new DBFField("NOMBRE", 'C', 30, 0),
				new DBFField("NIF", 'C', 15, 0),
				new DBFField("CP", 'C', 5, 0),
				new DBFField("MUNICIP", 'C', 18, 0),
				new DBFField("PROVINC", 'C', 20, 0),
				new DBFField("SG", 'C', 3, 0),
				new DBFField("DIRECCI", 'C', 50, 0),
				new DBFField("NUMERO", 'C', 6, 0),
				new DBFField("CONTACT", 'C', 35, 0),
				new DBFField("TELEFON", 'C', 15, 0),
				new DBFField("MOVIL", 'C', 15, 0),
				new DBFField("FAX", 'C', 15, 0),
				new DBFField("DIRMAIL", 'C', 254, 0),
				new DBFField("DIRNET", 'C', 254, 0),
				new DBFField("CARTERA", 'C', 3, 0),
				new DBFField("ACTUALIZAR", 'C', 1, 0) 
				};
	}

	private DBFField[] getAccounEntryFields() throws AonException {
		return new DBFField[] {
				new DBFField("CODIGO", 'C', 6, 0),
				new DBFField("FECHA", 'D', 8, 0),
				new DBFField("DIARIO", 'C', 2, 0),
				new DBFField("CODCON", 'C', 3, 0),
				new DBFField("CONCEP", 'C', 37, 0),
				new DBFField("DOCUM", 'C', 5, 0),
				new DBFField("DEBE", 'N', 20, 3),
				new DBFField("HABER", 'N', 20, 3),
				new DBFField("ASIENTO", 'C', 6, 0),
				};
	}	

	private DBFField[] getAccounEntryDetailFields() throws AonException {
		return new DBFField[] {
				new DBFField("CODIGO", 'C', 6, 0),
				new DBFField("LINEA", 'C', 5, 0),
				new DBFField("CUENTA", 'C', 12, 0),
				new DBFField("CODCON", 'C', 3, 0),
				new DBFField("CONCEP", 'C', 37, 0),
				new DBFField("DH", 'C', 1, 0),
				new DBFField("IMPORTE", 'N', 20, 3),
				new DBFField("SUBDEP", 'C', 4, 0),
				new DBFField("DEPART", 'C', 3, 0),
				new DBFField("T347", 'C', 1, 0),
				new DBFField("PUNTEO", 'C', 1, 0),
				};
	}	
	
	private DBFField[] getFacturasEmitidasFields() throws AonException {
		return new DBFField[] {
				new DBFField("REGIST", 'C', 3, 0),
				new DBFField("FRA", 'C', 5, 0),
				new DBFField("FECHA_F", 'C', 10, 0),
				new DBFField("CUENTA", 'C', 12, 0),
				new DBFField("TIPO", 'C', 1, 0),
				new DBFField("TOTIMP1", 'N', 20, 3),
				new DBFField("CUENTA1", 'C', 12, 0),
				new DBFField("TOTIMP2", 'N', 20, 3),
				new DBFField("CUENTA2", 'C', 12, 0),
				new DBFField("TOTIMP3", 'N', 20, 3),
				new DBFField("CUENTA3", 'C', 12, 0),
				new DBFField("TOTIMP4", 'N', 20, 3),
				new DBFField("CUENTA4", 'C', 12, 0),
				new DBFField("PDTO", 'N', 5, 2),
				new DBFField("TOTDTO", 'N', 20, 3),
				new DBFField("CTADTO", 'C', 12, 0),
				new DBFField("TIVA1", 'C', 2, 0),
				new DBFField("BI1", 'N', 20, 3),
				new DBFField("IMPIVA1", 'N', 20, 3),
				new DBFField("IMPREC1", 'N', 20, 3),
				new DBFField("TIVA2", 'C', 2, 0),
				new DBFField("BI2", 'N', 20, 3),
				new DBFField("IMPIVA2", 'N', 20, 3),
				new DBFField("IMPREC2", 'N', 20, 3),
				new DBFField("TIVA3", 'C', 2, 0),
				new DBFField("BI3", 'N', 20, 3),
				new DBFField("IMPIVA3", 'N', 20, 3),
				new DBFField("IMPREC3", 'N', 20, 3),
				new DBFField("TIVA4", 'C', 2, 0),
				new DBFField("BI4", 'N', 20, 3),
				new DBFField("IMPIVA4", 'N', 20, 3),
				new DBFField("IMPREC4", 'N', 20, 3),
				new DBFField("PORRET", 'N', 5, 2),
				new DBFField("RET1_SN", 'C', 1, 0),
				new DBFField("RET2_SN", 'C', 1, 0),
				new DBFField("RET3_SN", 'C', 1, 0),
				new DBFField("RET4_SN", 'C', 1, 0),
				new DBFField("DHRET", 'C', 1, 0),
				new DBFField("IMPRET", 'N', 20, 3),
				new DBFField("CTARET", 'C', 12, 0),
				new DBFField("TOTFRA", 'N', 20, 3),
				new DBFField("SUBDEP", 'C', 4, 0),
				new DBFField("DEPART", 'C', 3, 0),
				new DBFField("PUNTEO", 'C', 1, 0),
				new DBFField("DOCUM", 'C', 5, 0),
				new DBFField("DIARIO", 'C', 2, 0),
				new DBFField("ASIENTO", 'C', 6, 0),
				new DBFField("NOMBRE", 'C', 30, 0),
				new DBFField("NIF", 'C', 15, 0),
				new DBFField("DTOREC", 'C', 1, 0),				
				new DBFField("FECHA_A", 'C', 10, 0),
				new DBFField("RECC", 'C', 1, 0),
				};
	}
	
	private DBFField[] getFacturasEmitidasDetailFields() throws AonException {
		return new DBFField[] {
				new DBFField("REGIST", 'C', 3, 0),
				new DBFField("FRA", 'C', 5, 0),
				new DBFField("LINEA", 'C', 4, 0),
				new DBFField("TIVA", 'C', 2, 0),
				new DBFField("CUENTA", 'C', 12, 0),
				new DBFField("IMPORTE", 'N', 20, 3),
				new DBFField("RETENSN", 'C', 1, 0),
				new DBFField("M347SN", 'C', 1, 0),
				};
	}	

	private DBFField[] getCarteraCobrosFields() throws AonException {
		return new DBFField[] {
				new DBFField("EJERCIC", 'C', 4, 0),
				new DBFField("SERIE", 'C', 3, 0),
				new DBFField("NUMFAC", 'C', 5, 0),
				new DBFField("NUMEFE", 'C', 2, 0),
				new DBFField("CARTERA", 'C', 3, 0),
				new DBFField("FECHA", 'C', 10, 0),
				new DBFField("VTO", 'C', 10, 0),
				new DBFField("CLIPRO", 'C', 12, 0),
				new DBFField("IMPORTE", 'N', 20, 3),
				new DBFField("CONTA", 'C', 1, 0),
				};
	}	
	
	private DBFField[] getFacturasRecibidasFields() throws AonException {
		return new DBFField[] {
				new DBFField("REGIST", 'C', 5, 0),
				new DBFField("FRA", 'C', 15, 0),
				new DBFField("FECHA_F", 'C', 10, 0),
				new DBFField("FECHA_A", 'C', 10, 0),
				new DBFField("CUENTA", 'C', 12, 0),
				new DBFField("TIPO", 'C', 1, 0),
				new DBFField("TOTIMP1", 'N', 20, 3),
				new DBFField("CUENTA1", 'C', 12, 0),
				new DBFField("TOTIMP2", 'N', 20, 3),
				new DBFField("CUENTA2", 'C', 12, 0),
				new DBFField("TOTIMP3", 'N', 20, 3),
				new DBFField("CUENTA3", 'C', 12, 0),
				new DBFField("TOTIMP4", 'N', 20, 3),
				new DBFField("CUENTA4", 'C', 12, 0),
				new DBFField("PDTO", 'N', 5, 2),
				new DBFField("TOTDTO", 'N', 20, 3),
				new DBFField("CTADTO", 'C', 12, 0),
				new DBFField("TIVA1", 'C', 2, 0),
				new DBFField("BI1", 'N', 20, 3),
				new DBFField("IMPIVA1", 'N', 20, 3),
				new DBFField("IMPREC1", 'N', 20, 3),
				new DBFField("TIVA2", 'C', 2, 0),
				new DBFField("BI2", 'N', 20, 3),
				new DBFField("IMPIVA2", 'N', 20, 3),
				new DBFField("IMPREC2", 'N', 20, 3),
				new DBFField("TIVA3", 'C', 2, 0),
				new DBFField("BI3", 'N', 20, 3),
				new DBFField("IMPIVA3", 'N', 20, 3),
				new DBFField("IMPREC3", 'N', 20, 3),
				new DBFField("TIVA4", 'C', 2, 0),
				new DBFField("BI4", 'N', 20, 3),
				new DBFField("IMPIVA4", 'N', 20, 3),
				new DBFField("IMPREC4", 'N', 20, 3),
				new DBFField("TOTFRA", 'N', 20, 3),
				new DBFField("CTARET", 'C', 12, 0),
				new DBFField("PRET", 'N', 5, 2),
				new DBFField("CARTERA", 'C', 3, 0),
				new DBFField("FECVTO1", 'C', 10, 0),
				new DBFField("IMPVTO1", 'N', 20, 3),
				new DBFField("FECVTO2", 'C', 10, 0),
				new DBFField("IMPVTO2", 'N', 20, 3),
				new DBFField("FECVTO3", 'C', 10, 0),
				new DBFField("IMPVTO3", 'N', 20, 3),
				new DBFField("FECVTO4", 'C', 10, 0),
				new DBFField("IMPVTO4", 'N', 20, 3),
				new DBFField("FECVTO5", 'C', 10, 0),
				new DBFField("IMPVTO5", 'N', 20, 3),
				new DBFField("FECVTO6", 'C', 10, 0),
				new DBFField("IMPVTO6", 'N', 20, 3),
				new DBFField("FECVTO7", 'C', 10, 0),
				new DBFField("IMPVTO7", 'N', 20, 3),
				new DBFField("FECVTO8", 'C', 10, 0),
				new DBFField("IMPVTO8", 'N', 20, 3),
				new DBFField("FECVTO9", 'C', 10, 0),
				new DBFField("IMPVTO9", 'N', 20, 3),
				new DBFField("FECVTO10", 'C', 10, 0),
				new DBFField("IMPVTO10", 'N', 20, 3),
				new DBFField("FECVTO11", 'C', 10, 0),
				new DBFField("IMPVTO11", 'N', 20, 3),
				new DBFField("FECVTO12", 'C', 10, 0),
				new DBFField("IMPVTO12", 'N', 20, 3),
				new DBFField("DTOREC", 'C', 1, 0),
				new DBFField("DOCUM", 'C', 5, 0),
				new DBFField("DIARIO", 'C', 2, 0),
				new DBFField("ASIENTO", 'C', 6, 0),
				new DBFField("NUMORDEN", 'C', 5, 0),
				new DBFField("RECC", 'C', 1, 0),
				};
	}

	private DBFField[] getFacturasRecibidasDetailFields() throws AonException {
		return new DBFField[] {
				new DBFField("REGIST", 'C', 5, 0),
				new DBFField("LINEA", 'C', 4, 0),
				new DBFField("TIVA", 'C', 2, 0),
				new DBFField("CUENTA", 'C', 12, 0),
				new DBFField("IMPORTE", 'N', 20, 3),
				new DBFField("SUBDEP", 'C', 4, 0),
				new DBFField("DEPART", 'C', 3, 0),
				new DBFField("RETENSN", 'C', 1, 0),
				new DBFField("M347SN", 'C', 1, 0),
				new DBFField("IVAPRO", 'N', 20, 3),
				new DBFField("PDESGLO", 'C', 1, 0),
				new DBFField("IVAPROT", 'N', 20, 3),
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
	
	private void addDBF( Map<String, File> map, Map<Integer,List<Object[]>> dataMap, DBFField[] fields, String preffix) throws AonException {
		for(Entry<Integer,List<Object[]>> entry: dataMap.entrySet() ) {
			String name = preffix + entry.getKey() + StringUtils.leftPad(getConfiguration().getEnterpriseCode(), 3, "0");
			addData(map, name, DBF_SUFFIX, getData(entry.getValue(), fields));
		}
	}
		
	@Override
	public Map<String, File> getDataMap() {
		Map<String, File> map = new HashMap<String, File>();
		try {
			addDBF(map, accountMap, getAccounFields(), isInvoiceExport() ? "CLI" : "CUE" );
			addDBF(map, accountEntryMap, getAccounEntryFields(), "CAS");
			addDBF(map, accountEntryDetailMap, getAccounEntryDetailFields(), "LAS");
			addDBF(map, facturasRecibidasMap, getFacturasRecibidasFields(), "FAP");
			addDBF(map, facturasRecibidasDetailMap, getFacturasRecibidasDetailFields(), "FPL");
			addDBF(map, carteraCobrosMap, getCarteraCobrosFields(), "CAR");
			addDBF(map, facturasEmitidasMap, getFacturasEmitidasFields(), "FAC");
			addDBF(map, facturasEmitidasDetailMap, getFacturasEmitidasDetailFields(), "FCL");
		} catch (AonException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return map;
	}
	
}