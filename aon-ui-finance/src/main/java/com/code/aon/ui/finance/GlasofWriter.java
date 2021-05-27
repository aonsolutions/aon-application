package com.code.aon.ui.finance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.ExcelSheet;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;

public class GlasofWriter extends BasicExporter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(GlasofWriter.class.getName());
	
	private ExcelReportExporter exporter;
	
	private ExcelSheet salesSheet;
	
	private ExcelSheet purchasesSheet;
	
	private ExcelSheet accountingSheet;
	
	public GlasofWriter(InvoiceExportConfiguration configuration) {
		super(configuration);
		initExcel();
	}	

	@Override
	public InvoiceExportType getType() {
		return InvoiceExportType.GLASOF;
	}
	
	@Override
	protected boolean isSkipAccount(Account account) {
		return false;
	}	
	
	private void initExcel() {
		this.exporter = new ExcelReportExporter();
		try {
			this.exporter.startExport("ventas");
			this.salesSheet = this.exporter.getSheet();
			addHeader(true);
			
			this.purchasesSheet = this.exporter.createSheet("compras");
			this.exporter.setSheet(purchasesSheet);
			addHeader(false);

			this.accountingSheet = this.exporter.createSheet("Simples-1");
			this.exporter.setSheet(accountingSheet);
			addAccountingHeader();
		} catch (ReportException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void addAccountingHeader() {
		this.exporter.addHeaderCell("Asiento");
		this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.DATE));
		this.exporter.addHeaderCell("Subcuenta");
		this.exporter.addHeaderCell("Descripción");
		this.exporter.addHeaderCell("Concepto");
		this.exporter.addHeaderCell("Debe");
		this.exporter.addHeaderCell("Haber");
	}
	
	private void addHeader( boolean sales ) {
		this.exporter.addHeaderCell("Número de factura");
		this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.DATE));
		this.exporter.addHeaderCell("Clave de operación");			
		this.exporter.addHeaderCell("Código de país");
		this.exporter.addHeaderCell("NIF-NIE");
		this.exporter.addHeaderCell("Primer apellido o razón social");
		this.exporter.addHeaderCell("Segundo apellido");
		this.exporter.addHeaderCell("Nombre");
		this.exporter.addHeaderCell("Cuenta contable de contrapartida");
		this.exporter.addHeaderCell("Base imponible factura");
		this.exporter.addHeaderCell("Porcentaje de IVA");			
		this.exporter.addHeaderCell("Cuota de IVA");
		this.exporter.addHeaderCell("Porcentaje de recargo");
		this.exporter.addHeaderCell("Importe de recargo");			
		this.exporter.addHeaderCell("Importe de los suplidos");
		this.exporter.addHeaderCell("Porcentaje de IRPF");
		this.exporter.addHeaderCell("Base IRPF");
		this.exporter.addHeaderCell("Cuota IRPF");
		this.exporter.addHeaderCell("Cuenta contable IRPF");
		this.exporter.addHeaderCell("Clave IRPF");
		if ( sales ) {
			this.exporter.addHeaderCell("Columna no usada");
		} else {
			this.exporter.addHeaderCell("Subclave IRPF");
		}
		this.exporter.addHeaderCell("Incluir en el modelo 347");
		if ( sales ) {
			this.exporter.addHeaderCell("Tipo de venta");
			this.exporter.addHeaderCell("Número factura rectificada");			
		} else {
			this.exporter.addHeaderCell("Columna no usada");
			this.exporter.addHeaderCell("Columna no usada");
		}
		this.exporter.addHeaderCell("Fecha de expedición");
		this.exporter.addHeaderCell("Fecha operación");
		this.exporter.addHeaderCell("Signatura de la calle");
		this.exporter.addHeaderCell("Domicilio, nombre de la calle...");
		this.exporter.addHeaderCell("Número de la calle");
		this.exporter.addHeaderCell("Piso");
		this.exporter.addHeaderCell("Puerta");
		this.exporter.addHeaderCell("Escalera");
		this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.POSTAL_CODE));
		this.exporter.addHeaderCell("Población");
		this.exporter.addHeaderCell("Provincia");
		this.exporter.addHeaderCell("Teléfono");
		this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.CELLULAR));
		this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.FAX));
		this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.EMAIL));
		this.exporter.addHeaderCell("Factura/Tique inicial");
		this.exporter.addHeaderCell("Factura/Tique final");
		this.exporter.addHeaderCell("Total facturas/tiques");
		this.exporter.addHeaderCell("Refer. catastral inmueble");
		if ( sales ) {
			this.exporter.addHeaderCell("No usado");	
		} else {
			this.exporter.addHeaderCell("Subcuenta proveedor");
		}
		this.exporter.addHeaderCell("Anotar cobros");
		this.exporter.addHeaderCell("Fecha cobro");
		this.exporter.addHeaderCell("Cuenta cobro");
	}
	
	private void addEmptyCells( int count ) {
		for( int i = 0; i < count; i++ ) {
			this.exporter.addCell();
		}
	}
	
	private void addStringCell( String value ) {
		if (! StringUtils.isBlank(value) ) {
			this.exporter.addStringCell(value);
		} else {
			this.exporter.addCell();
		}
	}

	private void addDateCell( Date value ) {
		if ( value != null ) {
			this.exporter.addDateCell(value);
		} else {
			this.exporter.addCell();
		}
	}
	
	private void addAccount( Account account ) {
		if ( account != null ) {
			String cuenta = StringUtils.substring(account.getCode(), 0, 4);
			cuenta = StringUtils.removeEnd(cuenta, "0");
			String subCuenta = StringUtils.trim(StringUtils.substring(account.getCode(), 4));
			while ( StringUtils.startsWith(subCuenta, "0") ) {
				subCuenta = StringUtils.removeStart(subCuenta, "0");
			}
			if ( StringUtils.isEmpty(subCuenta) ) {
				subCuenta = "0";
			}
			addStringCell(cuenta + "." + subCuenta ); 			
		} else {
			addEmptyCells(1);
		}
	}	
	
	private String getClaveDeOperacion() {
		String clave = null;
		if ( isRectifier() ) {
			// Factura rectificativa
			clave = "D";
		} else if ( getTransaction() == InvoiceTransactionType.OTHER_ISP ) {
			// Inversión sujeto pasivo
			clave = "I";			
		} else if ( isTicket() ) {
			// Tiques
			clave = "J";			
		} else if ( getTransaction() == InvoiceTransactionType.INTRACOMMUNITY ) {
			// Adquisiciones intracomunitarias bienes
			clave = "P";			
		} else if ( isRenting() ) {
			// Arrendamiento de locales de negocios
			clave = "R";			
		} else if ( isWithholdingFarmer() ) {
			// Operaciones compensaciones agrícolas o ganaderas
			clave = "X";
		}
		return clave;
	}
	
	private TaxBreakDown getTax( TaxType type ) {
		for ( TaxBreakDown tax : getTaxBreakDowns() ) {
			if ( tax.getTaxType() == type ) {
				return tax;
			}
		}
		return null;
	}
	
	private TaxBreakDown getVatTax() {
		if ( getTransaction() == InvoiceTransactionType.INTRACOMMUNITY ) {
			for( InvoiceDetail id : getInvoice().getLines() ) {
				for( TaxBreakDown tbd : id.getTaxBreakDowns() ) {
					if ( (tbd.getTaxType() == TaxType.VAT) && (tbd.getTaxPercent() != 0) ) {
						return tbd;
					}
				}
			}			
		}
		return getTax(TaxType.VAT);
	}
	
	private double getPrepaymentTotal() {
		double total = 0.0;
		for( InvoiceDetail id : getInvoice().getLines() ) {
			if ( id.isPrepayment() ) {
				total += id.getTotalSalesPrice();
			}
		}
		return total;
	}
	
	private String getClaveIRPF() {
		String clave = null;
		if ( isSales() ) {
			if ( isRenting() ) {
				// Arrendamientos
				clave = "A";
			} else {
				// Actividades economicas
				clave = "E";
			}
		} else {
			// Profesionales
			clave = "G";
			if ( isWithholdingFarmer() ) {
				// Agricolas y ganaderas / Modulos dinerarios
				clave = "H";
			} else if ( isRenting() ) {
				// Arrendamientos de bienes inmuebles
				clave = "U";
			}
		}
		return clave;
	}
	
	private String getSubclaveIRPF( String claveIRPF ) {
		String subclave = null;
		if ( claveIRPF == "G" ) {
			subclave = "01";
		} else if ( claveIRPF == "H" ) {
			subclave = "01";
		}
		return subclave;
	}
	
	private String getTipoDeVenta() {
		String tipo = null;
		if ( getTransaction() == InvoiceTransactionType.INTRACOMMUNITY ) {
			// Entrega intracomunitaria
			tipo = "E";
		} else if ( getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY ) {
			// Exportaciones				
			tipo = "X";
		}		
		return tipo;
	}
	
	private void addInvoice() throws ManagerBeanException {
		// A - Número de factura
		addStringCell( getReferenceCode() );
		// B - Fecha de factura
		addDateCell( getDate() );
		// C - Clave de operación
		addStringCell( getClaveDeOperacion() );
		if ( getRegistryDocument() != null ) {
			// D - Código de país
			addStringCell( getRegistryDocument().getCountry().getValue() );
			// E - NIF-NIE			
			addStringCell( getRegistryDocument().getDocument() );
		} else {
			addEmptyCells(2);
		}
		// F - Primer apellido o razón social
		addStringCell( getRegistryName() );
		// G - Segundo apellido	
		addEmptyCells(1);
		// H - Nombre	
		addEmptyCells(1);
		// I - Cuenta contable de contrapartida	
		if ( getRegistryDetail() != null ) {
			addAccount(getRegistryDetail().getBalancingAccount());	
		} else {
			addEmptyCells(1);
		}
		double vatPercent = 0.0;
		// J - Base imponible factura		
		this.exporter.addDecimalCell(getInvoice().getTaxableBase());
		TaxBreakDown vat = getVatTax();
		if ( vat != null ) {
			// K - Porcentaje de IVA		
			vatPercent = vat.getTaxPercent();
			this.exporter.addDecimalCell(vatPercent);
			// L - Cuota de IVA		
			this.exporter.addDecimalCell(vat.getTaxQuota());
			if ( vat.getSurchargeQuota() != 0 ) {
				// M - Porcentaje de recargo		
				this.exporter.addDecimalCell(vat.getSurchargePercent());
				// N - Cuota de recargo		
				this.exporter.addDecimalCell(vat.getSurchargeQuota());				
			} else {
				addEmptyCells(2);
			}
		} else {
			addEmptyCells(4);
		}
		// O - Importe de los suplidos		
		this.exporter.addDecimalCell(getPrepaymentTotal());
		TaxBreakDown retention = getTax(TaxType.RETENTION);
		if ( retention != null ) {
			// P - Porcentaje de IRPF		
			this.exporter.addDecimalCell(retention.getTaxPercent());
			// Q - Base IRPF
			this.exporter.addDecimalCell(retention.getBase());
			// R - Cuota IRPF
			this.exporter.addDecimalCell(retention.getTaxQuota());
			// S - Cuenta contable IRPF
			addAccount(retention.getAccount());
			// T - Clave IRPF
			String claveIRPF = getClaveIRPF(); 
			addStringCell(claveIRPF);
			if (! isSales()) {
				// U - Subclave IRPF
				addStringCell(getSubclaveIRPF(claveIRPF));
			} else {
				// U - Columna no usada
				addEmptyCells(1);				
			}
		} else {
			addEmptyCells(6);
		}
		// V - Incluir en el modelo 347
		addStringCell("S");
		if ( isSales() ) {
			// W - Tipo de venta	
			if ( vatPercent == 0.0 ) {
				addStringCell(getTipoDeVenta());	
			} else {
				addEmptyCells(1);
			}
			// X - Numero factura rectificada	
			addStringCell(getInvoice().getRectificationInvoicesString());
		} else {
			addEmptyCells(2);
		}
		// Y - Fecha de expedicion	
		addEmptyCells(1);
		// Z - Fecha operacion	
		addEmptyCells(1);
		RegistryAddress address = getRegistryAddress();
		if ( getRegistryAddress() != null ) {
			// AA - Signatura de la calle
			if ( address.getStreetType() != null ) {
				addStringCell( address.getStreetType().getValue() );				
			} else {
				addEmptyCells(1);
			}
			// AB - Domicilio, nombre de la calle...
			addStringCell( address.getAddress() );
			// AC - Numero de la calle
			addStringCell( address.getNumber() );
			// AD - Piso
			addEmptyCells(1);
			// AE - Puerta
			addEmptyCells(1);
			// AF - Escalera
			addEmptyCells(1);
			// AG - Codigo Postal
			addStringCell( address.getZip() );
			// AH - Poblacion			
			addStringCell( address.getCity() );
			// AI - Provincia			
			if ( address.getGeozone() != null ) {
				addStringCell( address.getGeozone().getName() );
			} else {
				addEmptyCells(1);	
			}
		} else {
			addEmptyCells(9);				
		}
		if ( getRegistry() != null ) {
			try {
				// AJ - Telefono
				RegistryMedia phone = getRegistry().getPhone();
				if ( phone != null ) {
					addStringCell( phone.getValue() );
				} else {
					addEmptyCells(1);
				}
				// AK - Telefono movil
				RegistryMedia cellular = getRegistry().getCellular();
				if ( cellular != null ) {
					addStringCell( cellular.getValue() );
				} else {
					addEmptyCells(1);
				}				
				// AL - Fax
				RegistryMedia fax = getRegistry().getFax();
				if ( fax != null ) {
					addStringCell( fax.getValue() );
				} else {
					addEmptyCells(1);
				}				
				// AM - Email
				RegistryMedia email = getRegistry().getEmail();
				if ( email != null ) {
					addStringCell( email.getValue() );
				} else {
					addEmptyCells(1);
				}				
			} catch ( ManagerBeanException e ) {
				LOGGER.error(e.getMessage(), e);
			}			
		} else {
			addEmptyCells(4);
		}
		// AN - Factura/Tique inicial
		addEmptyCells(1);
		// AO - Factura/Tique final
		addEmptyCells(1);
		// AP - Total facturas/tiques
		addEmptyCells(1);
		// AQ - Refer. catastral inmueble"
		addEmptyCells(1);
		if ( isSales()) {
			// AR - No usado	
			addEmptyCells(1);
		} else {
			// AR - Subcuenta proveedor
			if ( getRegistryDetail() != null ) {
				addAccount(getRegistryDetail().getAccount());
			} else {
				addEmptyCells(1);
			}
		}
		// AS - Anotar cobros
		addEmptyCells(1);
		// AS - Fecha cobro
		addEmptyCells(1);
		// AS - Cuenta cobro		
		addEmptyCells(1);
	}
	
	private void writeInvoice() throws ManagerBeanException {
		if ( isSales() ) {
			this.exporter.setSheet(this.salesSheet);
		} else {
			this.exporter.setSheet(this.purchasesSheet);
		}		
		this.exporter.startLine();
		addInvoice();
		this.exporter.endLine();
	}	
	
	private void writeDetail( AccountEntry accountEntry, AccountEntryDetail aed ) throws ManagerBeanException {
		this.exporter.setSheet(accountingSheet);
		this.exporter.startLine();

		// Asiento
		addStringCell(getJournal(accountEntry).toString());
		// Fecha
		addDateCell( accountEntry.getEntryDate() );
		// Subcuenta
		addAccount(aed.getAccount());
		// Descripcion
		addStringCell(aed.getAccount().getDescription());
		// Concepto
		addStringCell( aed.getConcept() );
		// Debe
		this.exporter.addDecimalCell(aed.getDebit());
		// Haber
		this.exporter.addDecimalCell(aed.getCredit());
		
		this.exporter.endLine();
	}
	
	@Override
	public void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException {
		if ( isInvoiceExport() ) {
			writeInvoice();	
		} else {
			if ( getRegistryDetail() != null ) {
				writeDetail(accountEntry, getRegistryDetail());
			}
			while (! getDetails().isEmpty() ) {
				AccountEntryDetail aed = getNextDetail();
				writeDetail(accountEntry, aed);
			}
		}
	}
	
	private byte[] getExcelData() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			exporter.autoSizeColumns();
			exporter.endExport(bos);
		} catch (ReportException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return bos.toByteArray();
	}
	
	@Override
	public Map<String, File> getDataMap() {
		Map<String, File> map = new HashMap<String, File>();
		addData(map, "glasof", ".xls", getExcelData());
		return map;
	}
	
}