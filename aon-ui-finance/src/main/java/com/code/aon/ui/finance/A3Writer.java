package com.code.aon.ui.finance;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.StreetType;
import com.esferalia.aon.entity.IEntityAlias;

public class A3Writer extends BasicExporter {
	
	private StreetType[] VALID_STREET_TYPES = new StreetType[]{
			StreetType.AD, StreetType.AL, StreetType.AP, StreetType.AV, StreetType.BL, StreetType.BO,
			StreetType.CH, StreetType.CL, StreetType.CM, StreetType.CO, StreetType.CT, StreetType.CS,
			StreetType.CU, StreetType.ED, StreetType.GL, StreetType.GR, StreetType.LG, StreetType.MC,
			StreetType.MN, StreetType.MZ, StreetType.PB, StreetType.PG, StreetType.PJ, StreetType.PQ,
			StreetType.PZ, StreetType.PN, StreetType.PS, StreetType.RB, StreetType.RD, StreetType.TR,
			StreetType.UR};

	private static final int REGISTRY_SIZE = 256;
	
	public A3Writer(InvoiceExportConfiguration configuration) {
		super(configuration);
	}
	
	@Override
	public InvoiceExportType getType() {
		return InvoiceExportType.A3;
	}

	private void setNumber( double value, int offset, int maxLength ) {
		double _value = CommonUtil.round(value);
		String pattern = StringUtils.leftPad("0.00", maxLength-1, "0");
		DecimalFormat df = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.ENGLISH));
		String string = ( value<0 ? "" : "+") + df.format(_value);
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

	private void setAccount( Account account, int offset ) {
		String cuenta = StringUtils.substring(account.getCode(), 0, 4);
		String subCuenta = StringUtils.substring(account.getCode(), 4);
		int length = StringUtils.length(subCuenta);
		if ( length > 5 ) {
			subCuenta = StringUtils.substring(subCuenta, length-5, length);	
		}
		subCuenta = StringUtils.leftPad(subCuenta, getConfiguration().getAccountSize()-4, '0');
		// Cuenta
		String code = StringUtils.rightPad(cuenta + subCuenta, 12, ' '); 
		setString( code, offset, 12);
	}

	private void setAccountAndDescription( Account account ) {
		setAccount(account, 15);
		// Descripción de la cuenta
		setStringRightPad( account.getDescription(), 27, 30);	
	}
	
	private void initLine() {
		setLine( new byte[REGISTRY_SIZE] );
		Arrays.fill(getLine(), (byte) ' ');
		// Tipo de Formato
		setInteger( 3, 0, 1);
		// Codigo de Empresa
		String enterpriseCode = StringUtils.leftPad(getConfiguration().getEnterpriseCode(), 5, "0");
		setString(enterpriseCode, 1, 5);
		// Moneda enlace
		setString("E", 252, 1);
		// Indicador de Generado (N)
		setString("N", 253, 1);
		// Retorno de carro
		setString(BasicExporter.NEW_LINE, 254, 2);
	}
	
	private boolean isAbono() {
		return getRectificationType() == RectificationType.NORMAL_RECTIFIER ||
				getRectificationType() == RectificationType.SPECIAL_RECTIFIER;		
	}
	
	private boolean isFacturaEmitida() {
		return isSales();
	}

	private boolean isFacturaRecibida() {
		return getInvoiceType()==InvoiceType.PURCHASE ||
				getInvoiceType()==InvoiceType.EXPENSES;
	}
	
	private void fillHeader( AccountEntry accountEntry, AccountEntryDetail aed ) {
		initLine();
		// Fecha del apunte
		setDate(accountEntry.getEntryDate(), 6);
		// Tipo de Registro
		if ( isAbono() || (getTotal()<0) ) {
			setInteger( 2, 14, 1);
		} else {
			setInteger( 1, 14, 1);	
		}
		// Cuenta - Descripción de la cuenta 
		setAccountAndDescription(aed.getAccount());
		// Tipo de factura
		setInteger(2, 57, 1);
		if ( isInvestment() ) {
			setInteger(3, 57, 1);
		} else if ( isSales() ) {
			setInteger(1, 57, 1);
		}
		// Numero de Factura o Documento
		setStringRightPad( getMainId().toString(), 58, 10);
		// Linea de apunte (I)
		setString( "I", 68, 1);
		// Descripcion del apunte
		setStringRightPad( getReferenceCode(), 69, 30);
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
				switch ( getTransaction() ) {
					case INTRACOMMUNITY:
						result = "03";
						break;
					case CAN_CEU_MEL:
						result = "05";
						break;
					case EXTRACOMMUNITY:
						result = "06";
						break;
					case NATIONAL:
					case OTHER_ISP:
						break;
				}			
			}
		} else if ( isFacturaRecibida() ) {
			if ( vdt != VatDeductionType.WITH_RIGHT) {
				result = "07";
			} else {
				switch ( getTransaction() ) {
					case INTRACOMMUNITY:
						result = "03";
						break;
					case EXTRACOMMUNITY:
						result = "06";
						break;			
					case CAN_CEU_MEL:
					case NATIONAL:
					case OTHER_ISP:
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
			if ( tbd.getSurchargeQuota()!=0 || tbd.getSurchargePercent() != 0 ) {
				surchargeQuota = tbd.getSurchargeQuota();
				surchargePercent = tbd.getSurchargePercent();
			}
		}
		// Porcentaje de IVA
		setPercent( taxPercent, 115 );
		// Cuota de IVA
		setNumber( taxQuota, 120, 14 );
		if ( surchargeQuota!=0 || surchargePercent!=0 ) {
			// Porcentaje de Recargo
			setPercent( surchargePercent, 134 );
			// Cuota de Recargo
			setNumber( surchargeQuota, 139, 14 );
		}
		if ( retentionIncluded ) {
			// Porcentaje de Retencion
			setPercent( retentionPercent, 153 );
			// Cuota de Retencion
			setNumber( retentionQuota, 158, 14 );
		}
		// Operacion sujeta a IVA
		setString( vatIncluded ? "S" : "N", 174, 1);				
	}
	
	private void resetTaxInfo() {
		Arrays.fill(getLine(), 115, 172, (byte) ' ');
	}
	
	private void writeDetailWithTaxes( List<TaxBreakDown> taxList, boolean last ) throws IOException {
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
			writeLine();
			lineWritten = true;
		}
		if (! lineWritten ) {
			writeLine();
		}
	}
	
	private void writeDetail( AccountEntry accountEntry, AccountEntryDetail aed ) throws IOException {
		initLine();
		boolean last = getDetails().isEmpty();
		// Fecha del apunte
		setDate(accountEntry.getEntryDate(), 6);
		// Tipo de Registro
		setInteger( 9, 14, 1);
		// Cuenta - Descripción de la cuenta 
		setAccountAndDescription(aed.getAccount());
		// Tipo de importe (C o A)
		setString( getTipoDeImporte(aed), 57, 1);
		// Numero de Factura o Documento
		setStringLeftPad( getMainId().toString(), 58, 10);
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
			writeDetailWithTaxes(getTaxBreakDowns(), last);
		} else {
			writeDetailWithTaxes(getTaxes(aed), last);
		}
	}	
	
	private String getTipo( Finance finance ) {
		String tipo = "ME";
		if ( finance.getPayMethod() != null ) {
			switch ( finance.getPayMethod().getType() ) {
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
				case CASH_BASIS:
				case OTHER:
					break;
			}			
		}
		return tipo;
	}
	
	private String getEstado( Finance finance ) {
		String estado = "P";
		if ( finance.isPayment() ) {
			if ( finance.isReturned() ) {
				estado = "D";
			} else if ( finance.isPaid() ) {
				estado = "C";
			}
			
		} else {
			if ( finance.isPaid() ) {
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
		setStringRightPad( getMainId().toString(), 58, 10);
		// Descripcion del vencimiento
		setStringRightPad(finance.getRemarks(), 69, 30);
		// Importe del vencimiento
		setNumber( finance.getAmount(), 99, 14);
		// Fecha de Factura
		setDate(getDate(), 113);
		// Cuenta de tesoreria
		Account account = getTreasuryAccount(finance);
		if ( account != null ) {
			setAccount(account, 121);
		}
		writeLine();
		
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
		if ( finance.getBankAccount() != null && finance.getBankAccount().isValidBankAccount()) {
			setStringRightPad(finance.getBankAccount().getBban(), 93, 20);
		}
		writeLine();
	}
	
	private String getNIF( RegistryDocument rd ) {
		String nif = StringUtils.trim(rd.getDocument());
		if ( rd.getCountry() != null ) {
			switch (rd.getCountry() ) {
				case DK:
				case IE:
				case LU:
				case FI:
					nif = rd.getCountry().getValue() + StringUtils.substring(nif, 0, 8);
					break;
				case DE:
				case BE:					
				case PT:
				case AT:
					nif = rd.getCountry().getValue() + StringUtils.substring(nif, 0, 9);
					break;
				case FR:
				case IT:
					nif = rd.getCountry().getValue() + StringUtils.substring(nif, 0, 11);
					break;
				case GB:
				case NL:
				case SE:
					nif = rd.getCountry().getValue() + StringUtils.substring(nif, 0, 12);
					break;
				case GR:
					nif = "EL" + StringUtils.substring(nif, 0, 8);
					break;
			}
		}
		return nif;
	}
	
	private String getSiglasViaPublica( StreetType type ) {
		String value = "CL";
		if ( type != null && ArrayUtils.contains(VALID_STREET_TYPES, type) ) {
			if ( type == StreetType.CT ) {
				value = "CR";
			} else if ( type == StreetType.CU ) {
				value = "CT";
			} else if ( type == StreetType.PZ ) {
				value = "PA";
			} else if ( type == StreetType.PN ) {
				value = "PR";
			} else {
				value = type.getValue();
			}
		}
		return value;
	}
	
	private void fillAddress( RegistryAddress address ) {
		// Siglas Via Publica
		setString( getSiglasViaPublica(address.getStreetType()), 91, 2);
		// Via Publica		
		setStringRightPad( address.getAddress(), 93, 30);
		// Numero		
		setStringLeftPad( address.getNumber(), 123, 5);
		// Municipio		
		setStringRightPad( address.getCity(), 134, 20);
		// Codigo Postal		
		setStringLeftPad( address.getZip(), 154, 5);
		// Provincia		
		if ( address.getGeozone() != null ) {
			setStringRightPad( address.getGeozone().getName(), 159, 15);	
		}
	}
	
	private void writeRegistry() throws IOException, ManagerBeanException {
		initLine();
		// Fecha de Alta
		setDate(getDate(), 6);
		// Tipo de Registro
		setString( "C", 14, 1);		
		// Cuenta - Descripción de la cuenta 
		setAccountAndDescription(getRegistryDetail().getAccount());		
		// Actualizar Saldo Inicial
		setString( "N", 57, 1);		
		// Saldo Inicial
		setNumber( 0, 58, 14);
		// NIF
		RegistryDocument rd = getRegistryDocument();
		if ( rd != null ) {
			setStringRightPad( getNIF(rd), 77, 14);
		}
		
		if ( getRegistryAddress() != null ) {
			fillAddress( getRegistryAddress() );
		}
		// Telefono
		RegistryMedia phone = getRegistry().getPhone();
		if ( phone == null ) {
			phone = getRegistry().getCellular();
		}
		if ( phone != null ) {
			setStringRightPad( phone.getValue(), 177, 12);
		}
		// Fax
		RegistryMedia fax = getRegistry().getFax();
		if ( fax != null ) {
			setStringRightPad( fax.getValue(), 193, 12);
		}
		// E-mail
		RegistryMedia email = getRegistry().getEmail();
		if ( email != null ) {
			setStringRightPad( email.getValue(), 205, 30);
		}
		writeLine();
		
		RegistryBank rbank = getRegistryBank();
		if ( rbank != null ) {
			writeRegistryBank( rbank, rd );
		}		
	}
	
	private RegistryBank getRegistryBank() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), getRegistry().getId());
		List<ITransferObject> list = bean.getList(criteria);
		if ( !list.isEmpty() ) {
			return (RegistryBank) list.get(0); 
		}
		return null;		
	}
	
	private void writeRegistryBank( RegistryBank rbank, RegistryDocument rd ) throws IOException, ManagerBeanException {
		initLine();
		// Tipo de Registro
		setString( "C", 14, 1);		
		// Ampliacion
		setString( "B", 72, 1);		
		// NIF
		if ( rd != null ) {
			setStringRightPad( getNIF(rd), 73, 14);
		}
		// Nombre / Razon Social
		setStringRightPad(getRegistry().getName(), 87, 30);
		// C.C.C. (Cuenta Bancaria)
		setStringRightPad(rbank.getBankAccount().getBban(), 117, 20);
		// Cuenta por Omision
		setString( "S", 182, 1);		

		writeLine();
	}
	
	@Override
	public void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException {
		fillHeader(accountEntry, getRegistryDetail());
		writeLine();
		while (! getDetails().isEmpty() ) {
			AccountEntryDetail aed = getNextDetail();
			writeDetail(accountEntry, aed);
		}
		for( Finance finance : getFinances() ) {
			writeFinance(finance);
		}
		writeRegistry();
	}

	@Override
	public Map<String, byte[]> getDataMap() {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		map.put("suenlace.dat", getData());
		return map;
	}
	
}