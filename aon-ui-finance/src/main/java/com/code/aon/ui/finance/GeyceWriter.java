package com.code.aon.ui.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.strategy.TaxBreakDown;

public class GeyceWriter extends BasicExporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GeyceWriter.class.getName());
	
	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	private static final int REGISTRY_SIZE = 252;
	
	private byte[] line;
	
	private void setString( String value, int offset, int maxLength ) {
		if (! StringUtils.isEmpty(value) ) {
			String _value = StringUtils.substring(value, 0, maxLength);
			for( int i = 0; i < _value.length(); i++ ) {
				this.line[offset+i] = (byte) _value.charAt(i);
			}			
		}
	}

	private void setStringLeftPad( String value, int offset, int maxLength ) {
		String _value = StringUtils.leftPad(value, maxLength);
		setString(_value, offset, maxLength);
	}

	private void setStringRightPad( String value, int offset, int maxLength ) {
		String _value = StringUtils.rightPad(value, maxLength);
		setString(_value, offset, maxLength);
	}

	private void setNumber( double value, int offset, int maxLength ) {
		double _value = CommonUtil.round(value);
		String pattern = StringUtils.leftPad("0.00", maxLength, "0");
		DecimalFormat df = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.ENGLISH));
		String string = df.format(_value);
		setString(string, offset, maxLength);
	}
	
	private void setDate( int offset, Date date ) {
		setString( DATE_FORMAT.format(date), offset, 8);
	}
	
	private void resetTaxInfo() {
		Arrays.fill(this.line, 152, 207, (byte) '0');
		Arrays.fill(this.line, 216, 226, (byte) '0');
	}
	
	private void initLine() {
		this.line = new byte[REGISTRY_SIZE];
		Arrays.fill(this.line, (byte) ' ');
		Enterprise enterprise = getEnterprise();
		// Codigo de Empresa
		setStringLeftPad( enterprise.getId().toString(), 0, 6);
		// Fecha asiento
		setDate(6, getAccountEntry().getEntryDate());
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
		setDate(95, getInvoice().getTaxDate());
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
		String code = aed.getAccount().getCode();
		// Cuenta
		setStringRightPad( StringUtils.substring(code, 0, 4), 64, 4);
		// Codigo de Subcuenta
		setStringRightPad( StringUtils.substring(code, 4), 68, 10);
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
			write(line);
			lineWritten = true;
		}
		if (! lineWritten ) {
			write(line);
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
	
	@Override
	public String getFileName() {
		return "geyce.txt";
	}

	@Override
	public void write() throws IOException, ManagerBeanException {
		initLine();
		fillLine(getRegistryDetail());
		write(this.line);
		while (! getDetails().isEmpty() ) {
			AccountEntryDetail aed = getDetails().get(0);
			getDetails().remove(0);
			writeDetail(aed);
		}
	}
	
	public void serialize( Invoice invoice, OutputStream out ) throws AonException {
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {
			HibernateUtil.getSession(sessionFactoryName).refresh(invoice);
			init( invoice, out );
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
    
	public static void main(String[] args) throws IOException, AonException {
		GeyceWriter writer = new GeyceWriter();
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Invoice invoice = (Invoice) bean.get(83282);
		File file = new File("/tmp/geyce.txt");
		OutputStream out = new FileOutputStream(file);
		writer.serialize(invoice, out);
		out.close();
	}
	
}