package com.code.aon.ui.finance;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.product.strategy.TaxBreakDown;

public class LogicWinWriter extends BasicExporter {
	
	private static final String CONSTANT_0_00 = "0,00";

	private static final String CONSTANT_0 = "0";

	private static final String CONSTANT_01 = "01";

	private static final String CONSTANT_000 = "000";

	private static final int REGISTRY_SIZE = 380;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyy");
	
	public LogicWinWriter(InvoiceExportConfiguration configuration) {
		super(configuration);
	}
	
	@Override
	public InvoiceExportType getType() {
		return InvoiceExportType.LOGIC_WIN;
	}	

	@Override
	protected boolean isSkipAccount(Account account) {
		return false;
	}
	
	protected void setDate( Date date, int offset ) {
		setString( DATE_FORMAT.format(date), offset, 6);
	}	
	
	private void setInteger( Integer value, int offset, int maxLength ) {
		setStringLeftPad(String.valueOf(value), offset, maxLength);
	}	
	
	private void setNumber( double value, int offset, int maxLength ) {
		double _value = CommonUtil.round(value);
		DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(new Locale("es", "ES")));
		String string = df.format(_value);
		setStringLeftPad(string, offset, maxLength);
	}		
	
	private Integer getPeriod() {
		Integer period = 1;
		if ( getDate() != null ) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(getDate());
			period = cal.get(Calendar.MONTH) + 1;
		}
		return period;
	}
	
	private void initLine( AccountEntry accountEntry ) {
		setLine( new byte[REGISTRY_SIZE] );
		Arrays.fill(getLine(), (byte) ' ');
		// Periodo (mes 00)
		setInteger( getPeriod(), 19, 2);
		// Asiento
		setInteger( getJournal(accountEntry), 21, 5);
		// Documento
		setInteger( getMainId(), 26, 8);		
		// Fecha 
		setDate(getDate(), 59);		
		// Vencimiento Fecha 
		setDate(getDueDate(), 65);		
		// Codigo Diario
		setString(CONSTANT_000, 86, 3);
		// Codigo Canal
		setString(CONSTANT_000, 89, 3);
		// Factura Registro
		setInteger( getMainId(), 101, 8);	
		// L1: Porcentaje IVA
		setStringLeftPad(CONSTANT_0, 162, 5);		
		// L1: Porcentaje Recargo Equivalencia
		setStringLeftPad(CONSTANT_0, 167, 5);
		// L1: Base IVA
		setStringLeftPad(CONSTANT_0_00, 174, 15);
		// L1: Codigo Transaccion
		setString(CONSTANT_01, 189, 2);
		// L1: Cuota IVA
		setStringLeftPad(CONSTANT_0_00, 191, 15);
		// L1: Recargo Equivalencia
		setStringLeftPad(CONSTANT_0_00, 206, 13);
		// L2: Porcentaje IVA
		setStringLeftPad(CONSTANT_0, 224, 5);
		// L2: Porcentaje Recargo Equivalencia
		setStringLeftPad(CONSTANT_0, 229, 5);
		// L2: Base IVA
		setStringLeftPad(CONSTANT_0_00, 236, 15);
		// L2: Codigo Transaccion
		setString(CONSTANT_01, 251, 2);
		// L2: Cuota IVA
		setStringLeftPad(CONSTANT_0_00, 253, 15);
		// L2: Recargo Equivalencia
		setStringLeftPad(CONSTANT_0_00, 268, 13);
		// L3: Porcentaje IVA
		setStringLeftPad(CONSTANT_0, 286, 5);
		// L3: Porcentaje Recargo Equivalencia
		setStringLeftPad(CONSTANT_0, 291, 5);
		// L3: Base IVA
		setStringLeftPad(CONSTANT_0_00, 298, 15);
		// L3: Codigo Transaccion
		setString(CONSTANT_01, 313, 2);
		// L3: Cuota IVA
		setStringLeftPad(CONSTANT_0_00, 315, 15);
		// L3: Recargo Equivalencia
		setStringLeftPad(CONSTANT_0_00, 330, 13);
	}
	
	private void fillLine( AccountEntryDetail aed ) {
		// Indicador (C) Cargo (A) Abono
		double amount = 0;
		if ( aed.getCredit() == 0 ) {
			setString("C", 0, 1);
			amount = aed.getDebit();
		} else {
			setString("A", 0, 1);
			amount = aed.getCredit();
		}
		// Cuenta
		setStringLeftPad(aed.getAccount().getCode(), 1, 9);
		// Contrapartida
		if ( aed.getBalancingAccount() != null ) {
			setStringLeftPad(aed.getBalancingAccount().getCode(), 10, 9);
		}
		// Comentario
		setStringRightPad(aed.getConcept(), 34, 25);
		// Importe
		setNumber( amount, 71, 15);
		
		// Cuenta Cliente Proveedor
		setStringLeftPad(getRegistryDetail().getAccount().getCode(), 110, 9);
		// NIF Cliente Proveedor
		if ( getRegistryDocument() != null ) {
			setStringLeftPad( getRegistryDocument().getDocument(), 119, 11);	
		}
		// Descripcion
		setStringRightPad( getRegistryName(), 130, 25);		
	}
	
	private void fillLine( TaxBreakDown tbd, int line ) {
		// Porcentaje IVA
		setNumber( tbd.getTaxPercent(), 100+line*62, 5);
		// Porcentaje Recargo Equivalencia
		if ( tbd.getSurchargePercent() != 0 ) {
			setNumber(tbd.getSurchargePercent(), 105+line*62, 5);			
		}
		// Base Iva
		setNumber( tbd.getBase(), 112+line*62, 15);
		// Cuota IVA
		setNumber( tbd.getTaxQuota(), 129+line*62, 15);
		// Recargo Equivalencia
		if ( tbd.getSurchargeQuota() != 0 ) {
			setNumber(tbd.getSurchargeQuota(), 144+line*62, 13);			
		}
	}
	
	private void writeTaxes() throws IOException {
		int line = 1;
		for( TaxBreakDown tbd : getTaxBreakDowns() ) {
			if ( tbd.getTaxType() == TaxType.VAT ) {
				fillLine(tbd, line++);
			}
		}
	}	
	
	private void writeRegistryDetail() throws IOException {
		AccountEntryDetail aed = getRegistryDetail();
		fillLine(aed);
		// IndicadorIvaRepSop (R) repercutido (S) soportado
		if ( isSales() ) {
			setString("R", 109, 1);
		} else {
			setString("S", 109, 1);
		}
		writeTaxes();
		writeLine();
		setString(" ", 109, 1);
	}
	
	@Override
	public void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException {
		initLine( accountEntry );
		writeRegistryDetail();
		while (! getDetails().isEmpty() ) {
			writeNewLine();
			fillLine(getNextDetail());
			writeLine();
		}
		writeNewLine();
	}

	@Override
	public Map<String, File> getDataMap() {
		Map<String, File> map = new HashMap<String, File>();
		addData(map, "logicwin", ".txt", getData());
		return map;
	}
	
}