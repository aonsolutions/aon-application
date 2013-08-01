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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class A3Writer extends BasicExporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(A3Writer.class.getName());
	
	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	private static final int REGISTRY_SIZE = 256;
	
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
		String pattern = StringUtils.leftPad("0.00", maxLength-1, "0");
		DecimalFormat df = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.ENGLISH));
		String string = ( value<0 ? "-" : "+") + df.format(_value);
		setString(string, offset, maxLength);
	}

	private void setPercent( double value, int offset ) {
		double _value = CommonUtil.round(value);
		DecimalFormat df = new DecimalFormat("00.00", new DecimalFormatSymbols(Locale.ENGLISH));
		setString(df.format(_value), offset, 5);
	}
	
	private void setInteger( Integer value, int offset, int maxLength ) {
		String pattern = StringUtils.leftPad("0", maxLength, "0");
		DecimalFormat df = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.ENGLISH));
		String string = df.format(value);
		setString(string, offset, maxLength);
	}
		
	private void setDate( Date date, int offset ) {
		setString( DATE_FORMAT.format(date), offset, 8);
	}

	private void setAccount( Account account, int offset ) {
		// Cuenta
		String code = StringUtils.rightPad(account.getCode(), 12, '0'); 
		setString( code, offset, 12);
	}

	private void setAccountAndDescription( Account account ) {
		setAccount(account, 15);
		// Descripción de la cuenta
		setStringRightPad( account.getDescription(), 27, 30);	
	}
	
	private void initLine() {
		this.line = new byte[REGISTRY_SIZE];
		Arrays.fill(this.line, (byte) ' ');
		// Tipo de Formato
		setInteger( 3, 0, 1);
		// Codigo de Empresa
		Enterprise enterprise = getEnterprise();
		setInteger( enterprise.getId(), 1, 5);
		// Moneda enlace
		setString("E", 252, 1);
		// Indicador de Generado (N)
		setString("N", 253, 1);
		// Retorno de carro
		setString("\r\n", 254, 2);
	}
	
	private boolean isAbono() {
		return (getInvoice().getRectificationType() == RectificationType.NORMAL_RECTIFIER) ||
				(getInvoice().getRectificationType() == RectificationType.SPECIAL_RECTIFIER);		
	}
	
	private boolean isFacturaEmitida() {
		return (getInvoice().getType() == InvoiceType.SALES);
	}

	private boolean isFacturaRecibida() {
		return (getInvoice().getType() == InvoiceType.PURCHASE) || (getInvoice().getType() == InvoiceType.EXPENSES);
	}
	
	private void fillHeader( AccountEntryDetail aed ) {
		initLine();
		// Fecha del apunte
		setDate(getAccountEntry().getEntryDate(), 6);
		// Tipo de Registro
		if ( isAbono() ) {
			setInteger( 2, 14, 1);
		} else {
			setInteger( 1, 14, 1);	
		}
		// Cuenta - Descripción de la cuenta 
		setAccountAndDescription(aed.getAccount());
		// Tipo de factura
		setInteger(2, 57, 1);
		if ( getInvoice().isInvestment() ) {
			setInteger(3, 57, 1);
		} else if ( getInvoice().getType() == InvoiceType.SALES ) {
			setInteger(1, 57, 1);
		}
		// Numero de Factura o Documento
		setStringLeftPad( getInvoice().getId().toString(), 58, 10);
		// Linea de apunte (I)
		setString( "I", 68, 1);
		// Descripcion del apunte
		setStringRightPad( getInvoice().getReferenceCode(), 69, 30);
		// Importe
		double amount = (aed.getCredit() != 0) ? aed.getCredit() : aed.getDebit();
		setNumber( amount, 99, 14);
	}
	
	private String getTipoDeImporte( AccountEntryDetail aed ) {
		String result = "C";
		if ( aed.getDebit() != 0 ) {
			if ( isAbono() ) {
				if ( isFacturaRecibida() ) {
					result = "A";
				}
			} else {
				if ( isFacturaEmitida() ) {
					result = "A";					
				}
			}
		} else {
			if ( isAbono() ) {
				if ( isFacturaEmitida() ) {
					result = "A";
				}
			} else {
				if ( isFacturaRecibida() ) {
					result = "A";					
				}
			}			
		}
		return result;
	}
	
	private String getSubtipoDeFactura( VatDeductionType vdt ) {
		String result = "01";
		if ( isFacturaEmitida() ) {
			if ( vdt == VatDeductionType.WITHOUT_RIGHT) {
				result = "02";
			} else if ( vdt == VatDeductionType.NON_TAXABLE) {
				result = "07";
			} else {
				switch ( getInvoice().getTransaction() ) {
					case INTRACOMMUNITY:
						result = "03";
						break;
					case CAN_CEU_MEL:
						result = "05";
						break;
					case EXTRACOMMUNITY:
						result = "06";
						break;
				}			
			}
		} else if ( isFacturaRecibida() ) {
			if ( vdt != VatDeductionType.WITH_RIGHT) {
				result = "07";
			} else {
				switch ( getInvoice().getTransaction() ) {
					case INTRACOMMUNITY:
						result = "03";
						break;
					case EXTRACOMMUNITY:
						result = "06";
						break;			
				}
			}
		}
		return result;
	}
	
	private void addTaxes( List<TaxBreakDown> taxList ) {
		double taxPercent = 0;
		double retentionPercent = 0;
		double surchargePercent = 0;
		double taxQuota = 0;
		double surchargeQuota = 0;
		double retentionQuota = 0;
		boolean vatIncluded = false;
		boolean retentionIncluded = false;

		resetTaxInfo();		
		for( TaxBreakDown tbd : taxList ) {
			if ( tbd.getTaxType() == TaxType.VAT ) {
				taxQuota = tbd.getTaxQuota();
				taxPercent = tbd.getTaxPercent();
				vatIncluded = true;
			} else if ( tbd.getTaxType() == TaxType.RETENTION ) {
				retentionQuota = tbd.getTaxQuota();
				retentionPercent = tbd.getTaxPercent();
				retentionIncluded = true;
			}
			if ( (tbd.getSurchargeQuota() != 0) || (tbd.getSurchargePercent() != 0) ) {
				surchargeQuota = tbd.getSurchargeQuota();
				surchargePercent = tbd.getSurchargePercent();
			}
		}
		// Porcentaje de IVA
		setPercent( taxPercent, 115 );
		// Cuota de IVA
		setNumber( taxQuota, 120, 14 );
		if ( (surchargeQuota != 0) || (surchargePercent != 0) ) {
			// Porcentaje de Recargo
			setPercent( surchargePercent, 134 );
			// Cuota de Recargo
			setNumber( surchargeQuota, 139, 14 );
		}
		if ( retentionIncluded ) {
			// Porcentaje de Retencion
			setPercent( retentionPercent, 134 );
			// Cuota de Retencion
			setNumber( retentionQuota, 139, 14 );
		}
		// Operacion sujeta a IVA
		setString( vatIncluded ? "S" : "N", 174, 1);				
	}
	
	private void resetTaxInfo() {
		Arrays.fill(this.line, 115, 172, (byte) ' ');
	}
	
	private void writeDetailWithTaxes( AccountEntryDetail aed, List<TaxBreakDown> taxList, boolean last ) throws IOException {
		boolean lineWritten = false;
		while (! taxList.isEmpty() ) {
			List<TaxBreakDown> list = new LinkedList<TaxBreakDown>();
			TaxBreakDown tbd = getNextTax(taxList);
			list.add(tbd);
			TaxBreakDown tbd2 = getRelatedTax(taxList, tbd);
			if ( tbd2 != null ) {
				list.add(tbd2);
			}
			addTaxes(list);
			// Linea de apunte (M o U)
			if ( last && taxList.isEmpty() ) {
				setString( "U", 68, 1);
			} else {
				setString( "M", 68, 1);	
			}		
			// Subtipo de factura (01 a 07)
			setString( getSubtipoDeFactura(tbd.getVatDeductionType()), 99, 2);					
			// Base imponible
			setNumber( tbd.getBase(), 101, 14);			
			write(line);
			lineWritten = true;
		}
		if (! lineWritten ) {
			write(line);
		}
	}
	
	private void writeDetail( AccountEntryDetail aed ) throws IOException {
		initLine();
		boolean last = getDetails().isEmpty();
		// Fecha del apunte
		setDate(getAccountEntry().getEntryDate(), 6);
		// Tipo de Registro
		setInteger( 9, 14, 1);
		// Cuenta - Descripción de la cuenta 
		setAccountAndDescription(aed.getAccount());
		// Tipo de importe (C o A)
		setString( getTipoDeImporte(aed), 57, 1);
		// Numero de Factura o Documento
		setStringLeftPad( getInvoice().getId().toString(), 58, 10);
		// Linea de apunte (M o U)
		setString( last ? "U" : "M", 68, 1);		
		// Subtipo de factura (01 a 07)
		setString( getSubtipoDeFactura(VatDeductionType.WITH_RIGHT), 99, 2);	
		// Base imponible
		double amount = (aed.getCredit() != 0) ? aed.getCredit() : aed.getDebit();
		setNumber( amount, 101, 14);
		// Impreso
		setString( "01", 172, 2);
		if ( last ) {
			writeDetailWithTaxes(aed, getTaxBreakDowns(), last);
		} else {
			writeDetailWithTaxes(aed, getTaxes(aed), last);
		}
	}	
	
	private String getTipo( Finance finance ) {
		String tipo = "ME";
		switch ( finance.getPayMethod().getType() ) {
			case CASH_BASIS:
				tipo = "ME";
				break;				
			case BANK_TRANSFER:
				tipo = "TR";
				break;
			case CHEQUE:
				tipo = "CH";
				break;
			case NEGOTIABLE_DOCUMENT:
				tipo = "DO";
				break;
			case CREDIT_CARD:
			case DEBIT_CARD:
				tipo = "GI";
				break;
		}
		return tipo;
	}
	
	private String getEstado( Finance finance ) {
		String estado = "P";
		if ( finance.isPayment() ) {
			if ( finance.getFinanceStatus() == FinanceStatus.RETURNED ) {
				estado = "D";
			} else if ( finance.getFinanceStatus() == FinanceStatus.PAID ) {
				estado = "C";
			}
			
		} else {
			if ( finance.getFinanceStatus() == FinanceStatus.PAID ) {
				estado = "G";
			}
		}
		return estado;
	}

	private Account getTreasuryAccount( Finance finance ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		String id = bean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ID);
		criteria.addEqualExpression(id, finance.getId());
		String rbank = bean.getFieldName(IEntityAlias.FINANCE_TRACKING_REGISTRY_BANK_ID);
		criteria.addNotNullExpression(rbank);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return ((FinanceTracking) list.get(0)).getRegistryBank().getAccount();
		}
		return null;
	}
	
	private Date getFechaCobroPago( Finance finance ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		String id = bean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ID);
		criteria.addEqualExpression(id, finance.getId());
		String type = bean.getFieldName(IEntityAlias.FINANCE_TRACKING_TYPE);
		criteria.addEqualExpression(type, FinanceTrackingType.PAID);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return ((FinanceTracking) list.get(0)).getTrackingDate();
		}
		return null;
	}
	
	private void writeFinance( Finance finance ) throws IOException, ManagerBeanException {
		initLine();
		// Fecha del Vencimiento
		setDate(finance.getDueDate(), 6);
		// Tipo de Registro
		setString( "V", 14, 1);		
		// Cuenta - Descripción de la cuenta 
		setAccountAndDescription(getRegistryDetail().getAccount());		
		// Tipo de Vencimiento (C o P)
		setString( finance.isPayment() ? "P" : "C", 57, 1);
		// Numero de Factura o Documento
		setStringLeftPad( getInvoice().getId().toString(), 58, 10);
		// Descripcion del vencimiento
		setStringRightPad(finance.getRemarks(), 69, 30);
		// Importe del vencimiento
		setNumber( finance.getAmount(), 99, 14);
		// Fecha de Factura
		setDate(getInvoice().getDate(), 113);
		// Cuenta de tesoreria
		Account account = getTreasuryAccount(finance);
		if ( account != null ) {
			setAccount(account, 121);
		}
		write(line);
		
		initLine();
		// Fecha del Vencimiento
		setDate(finance.getDueDate(), 6);
		// Tipo de Registro
		setString( "V", 14, 1);
		// Tipo Cobro/Pago
		setString( getTipo(finance), 58, 2);
		// Fecha Cobro/Pago
		Date date = getFechaCobroPago(finance);
		if ( date != null ) {
			setDate(date, 60);	
		}
		// Indicador Ampliacion
		setString( "A", 68, 1);
		// Estado
		setString( getEstado(finance), 69, 2);
		// C.C.C. (Cuenta Bancaria)
		if ( finance.getBank() != null ) {
			setStringRightPad(finance.getBankAccount().getValue(), 93, 20);
		}
		write(line);
	}
	
	@Override
	public String getFileName() {
		return "suenlac3.txt";
	}
	
	@Override
	public void write() throws IOException, ManagerBeanException {
		fillHeader(getRegistryDetail());
		write(this.line);
		while (! getDetails().isEmpty() ) {
			AccountEntryDetail aed = getDetails().get(0);
			getDetails().remove(0);
			writeDetail(aed);
		}
		for( Finance finance : getInvoice().getFinances() ) {
			writeFinance(finance);
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
		A3Writer writer = new A3Writer();
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Invoice invoice = (Invoice) bean.get(280791);
		File file = new File("/tmp/suenlac3.txt");
		OutputStream out = new FileOutputStream(file);
		writer.serialize(invoice, out);
		out.close();
	}
	
}