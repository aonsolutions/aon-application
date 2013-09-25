package com.code.aon.ui.finance;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.StreetType;

public class GeyceWriter extends BasicExporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GeyceWriter.class.getName());

	private static final int GYCCON_SIZE = 252;
	
	private static final int GYCPLAN_SIZE = 141;

	private String enterpriseCode;
	
	private String journal;	
	
	public GeyceWriter(String enterpriseCode, String journal) {
		this.enterpriseCode = enterpriseCode;
		this.journal = journal;
	}

	private void setNumber( double value, int offset, int maxLength ) {
		double _value = CommonUtil.round(value);
		String pattern = StringUtils.leftPad("0.00", maxLength, "0");
		DecimalFormat df = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.ENGLISH));
		String string = df.format(_value);
		setString(string, offset, maxLength);
	}
	
	private void resetTaxInfo() {
		Arrays.fill(getLine(), 152, 207, (byte) '0');
		Arrays.fill(getLine(), 216, 226, (byte) '0');
	}
	
	private String getDiario() {
		if (! StringUtils.isBlank(journal) ) {
			return journal;
		}
		switch ( getInvoice().getType() ) {
			case SALES:
				return "2";
			case PURCHASE:
				return "3";
			case EXPENSES:
				return "4";
		}		
		return "1";
	}
	
	private void initLine() {
		setLine( new byte[GYCCON_SIZE] );
		Arrays.fill(getLine(), (byte) ' ');
		// Codigo de Empresa
		setStringLeftPad( enterpriseCode, 0, 6);
		// Fecha asiento
		setDate(getAccountEntry().getEntryDate(), 6);
		// Numero de Diario Contable
		setStringLeftPad( getDiario(), 24, 2);		
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
		// Descripcion
		setStringRightPad( getInvoice().getRegistryName(), 107, 30);
		// NIF/CIF
		setStringRightPad( getInvoice().getRegistryDocument(), 137, 15);
		// Tipo de Moneda 
		setString("E", 215, 1);
		if ( getInvoice().getProject() != null ) {
			// Codigo de proyecto
			setStringRightPad( getInvoice().getProject().getId().toString(), 228, 5);			
		}
	}
	
	private void fillLine( AccountEntryDetail aed ) {
		// Contador de Numero de asiento
		setStringLeftPad( aed.getId().toString(), 14, 6);
		// Contador de Lineas de asiento
		setStringLeftPad( String.valueOf(aed.getLine()), 20, 4);
		// Indivativo de DEBE o HABER
		double amount = 0;
		if ( aed.getCredit() == 0 ) {
			setString("D", 63, 1);
			amount = aed.getDebit();
		} else {
			setString("B", 63, 1);
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
	
	private void writeDetailWithTaxes( AccountEntryDetail aed, List<TaxBreakDown> taxList, boolean last ) throws IOException {
		boolean lineWritten = false;
		resetTaxInfo();
		while (! taxList.isEmpty() ) {
			TaxBreakDown tbd = getNextTax(taxList);
			fillLine(tbd);
			TaxBreakDown tbd2 = getRelatedTax(taxList, tbd);
			if ( tbd2 != null ) {
				fillLine(tbd2);
			}
			writeLine();
			lineWritten = true;
		}
		if (! lineWritten ) {
			writeLine();
		}
	}	
	
	private void writeDetail( AccountEntryDetail aed ) throws IOException {
		boolean last = getDetails().isEmpty();
		fillLine(aed);
		if ( last ) {
			writeDetailWithTaxes(aed, getTaxBreakDowns(), last);
		} else {
			writeDetailWithTaxes(aed, getTaxes(aed), last);
		}
	}	
	
	private String getSiglasViaPublica( StreetType type ) {
		String value = "CL";
		if ( type != null ) {
			value = type.getValue();
		}
		return value;
	}	
	
	private String[] getCuenta( String code ) {
		String cuenta = StringUtils.substring(code, 0, 4);
		String subCuenta = StringUtils.trimToNull(StringUtils.substring(code, 4));
		if ( NumberUtils.isDigits(subCuenta) && (NumberUtils.toInt(subCuenta) == 0) ) {
			subCuenta = "00";
			if ( cuenta.endsWith("00") ) {
				cuenta = StringUtils.substring(cuenta, 0, 3);
			}
		}
		return new String[]{cuenta, subCuenta};
	}
	
	private byte[] getGycPlan() {
		setLine( new byte[GYCPLAN_SIZE] );
		Arrays.fill(getLine(), (byte) ' ');
		
		// Codigo de Empresa
		setStringLeftPad( enterpriseCode, 0, 6);
		AccountEntryDetail aed = getRegistryDetail();		
		String[] cuenta = getCuenta(aed.getAccount().getCode());
		// Cuenta
		setStringRightPad( cuenta[0], 6, 4);
		// Codigo de Subcuenta
		setStringRightPad( cuenta[1], 10, 10);
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

		return getLine();
	}

	@Override
	public void write() throws IOException, ManagerBeanException {
		initLine();
		fillLine(getRegistryDetail());
		writeLine();
		while (! getDetails().isEmpty() ) {
			writeNewLine();
			AccountEntryDetail aed = getDetails().get(0);
			getDetails().remove(0);
			writeDetail(aed);
		}
	}
	
	public void serialize( Invoice invoice ) throws AonException {
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {
			HibernateUtil.getSession(sessionFactoryName).refresh(invoice);
			init( invoice );
			write();
		} catch (Throwable t ) {
		    try {
				HibernateUtil.rollbackTransaction(sessionFactoryName);
			} catch (DAOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		    throw new AonException( t.getMessage(), t);
		} finally {
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}
		}
	}

	@Override
	public Map<String, byte[]> getDataMap() {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		map.put("gyccon.txt", getData());
		map.put("gycplan.txt", getGycPlan());
		return map;
	}
	
}