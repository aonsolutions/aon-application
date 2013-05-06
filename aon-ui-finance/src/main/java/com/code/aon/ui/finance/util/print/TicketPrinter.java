package com.code.aon.ui.finance.util.print;

import static com.code.aon.ui.common.ICommonConstants.DECIMAL_2_PATTERN;
import static com.code.aon.ui.common.ICommonConstants.PERCENT_PATTERN;
import static com.code.aon.ui.common.ICommonConstants.QUANTITY_PATTERN;
import static com.code.aon.ui.common.ICommonConstants.TIMESTAMP_2_PATTERN;
import static com.code.aon.ui.finance.controller.IFinanceConstants.BUNDLE_NAME;
import static com.code.aon.ui.finance.controller.IFinanceConstants.POS_GIFT_RECEIPT;
import static com.code.aon.ui.finance.controller.IFinanceConstants.POS_INVOICE_PARAMS_CONTROLLER_NAME;
import static com.code.aon.ui.finance.controller.IFinanceConstants.POS_RECEIPT;
import static com.code.aon.ui.finance.controller.IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.report.ReportException;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.PosInvoiceParamsController;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.util.AonUtil;

public class TicketPrinter {
	
	private static final String PRODUCTO_LABEL = "PRODUCTO";

	private static final String IMPORTE_LABEL = "IMPORTE";

	private static final Logger LOGGER = LoggerFactory.getLogger(TicketPrinter.class.getName());
	
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(AonUtil.getMessage(TIMESTAMP_2_PATTERN));
	
	private static DecimalFormat PRICE_FORMAT = new DecimalFormat(AonUtil.getMessage(DECIMAL_2_PATTERN));
	
	private static DecimalFormat QUANTITY_FORMAT = new DecimalFormat(AonUtil.getMessage(QUANTITY_PATTERN));
	
	private static DecimalFormat PERCENT_FORMAT = new DecimalFormat(AonUtil.getMessage(PERCENT_PATTERN));
	
	private static int MAX_PRICE_SIZE = 9;
	
	private static int COLUMN_SPACE = 2;
	
	private static String INITIALIZE_PRINTER = "27,64";
	
	private static String FEET_LINES_COMMAND = "27,100,1";
	
	private static String CUT_COMMAND = "29,86,49"; 
	
	private static String OPEN_DRAWER = "27,112,0,25,250";
	
	private static String BOLD_ON_COMMAND = "27,69,49";
	
	private static String BOLD_OFF_COMMAND = "27,69,48";

	private static String UNDERLINE_ON_COMMAND = "27,45,49";
	
	private static String UNDERLINE_2_ON_COMMAND = "27,45,50";
	
	private static String UNDERLINE_OFF_COMMAND = "27,45,48";
	
	private static String ALIGN_LEFT_COMMAND = "27,97,0";
	
	private static String ALIGN_CENTER_COMMAND = "27,97,1";
	
	private static String PRINT_LOGO_COMMAND = "29,40,76,6,0,48,69,48,48,1,1";
	
	private static String LINE_FEED = "\n";
	
	private int width = 48;
	
	private static char[] INVALID_CHARS = new char[] {
		'\u0160', '\u0160', '\u00D0', '\u017D', '\u017E', '\u00C0', '\u00C1', '\u00C2', '\u00C3', '\u00C4',
		'\u00C5', '\u00C6', '\u00C7', '\u00C8', '\u00C9', '\u00CA', '\u00CB', '\u00CC', '\u00CD', '\u00CE',
		'\u00CF', '\u00D1', '\u00D2', '\u00D3', '\u00D4', '\u00D5', '\u00D6', '\u00D8', '\u00D9', '\u00DA',
		'\u00DB', '\u00DC', '\u00DD', '\u00DE', '\u00DF', '\u00E0', '\u00E1', '\u00E2', '\u00E3', '\u00E4',
		'\u00E5', '\u00E6', '\u00E7', '\u00E8', '\u00E9', '\u00EA', '\u00EB', '\u00EC', '\u00ED', '\u00EE',
		'\u00EF', '\u00F0', '\u00F1', '\u00F2', '\u00F3', '\u00F4', '\u00F5', '\u00F6', '\u00F8', '\u00F9',
		'\u00FA', '\u00FB', '\u00FD', '\u00FE', '\u00FF', '\u0131', '\u0130', '\u015F',	'\u015E', '\u00FC',
		'\u00DC', '\u011F', '\u011E'	};

	private static String[] VALID_CHARS = new String[] {
		"S", "s", "Dj","Z", "z", "A", "A", "A", "A", "A",
		"A", "A", "C", "E", "E", "E", "E", "I", "I", "I",
		"I", "N", "O", "O", "O", "O", "O", "O", "U", "U",
		"U", "U", "Y", "B", "Ss","a", "a", "a", "a", "a",
		"a", "a", "c", "e", "e", "e", "e", "i", "i", "i",
		"i", "o", "n", "o", "o", "o", "o", "o", "o", "u",
		"u", "u", "y", "b", "y", "i", "I", "s", "S", "u",
		"U", "g", "G" };
	
	private String getInitializePrinter() {
		return INITIALIZE_PRINTER;
	}
	
	private String getFeedLines( int lines ) {
		return StringUtils.repeat(FEET_LINES_COMMAND, ",", lines);
	}
	
	private String getCutTicket() {
		return CUT_COMMAND;
	}
	
	private String getOpenDrawer() {
		return OPEN_DRAWER;
	}

	private String getBold( boolean value ) {
		return value ? BOLD_ON_COMMAND : BOLD_OFF_COMMAND;
	}

	private String getUnderline() {
		return UNDERLINE_ON_COMMAND;
	}	

	private String getUnderline2() {
		return UNDERLINE_2_ON_COMMAND;
	}	

	private String getUnderlineOff() {
		return UNDERLINE_OFF_COMMAND;
	}	

	private String getAlignCenter() {
		return ALIGN_CENTER_COMMAND;
	}	

	private String getAlignLeft() {
		return ALIGN_LEFT_COMMAND;
	}	

	private String getPrintLogo() {
		return PRINT_LOGO_COMMAND;
	}	

	
	private String getCenteredLine( String text ) {
		if (! StringUtils.isEmpty(text) ) {
			StringBuffer sb = new StringBuffer();
			append( sb, getAlignCenter() );
			sb.append( StringUtils.substring(getText(text), 0, width ) );
			sb.append(LINE_FEED);
			append( sb, getAlignLeft() );
			return sb.toString();
		}
		return LINE_FEED;
	}

	private String getLine( String text ) {
		if (! StringUtils.isEmpty(text) ) {
			return StringUtils.substring(getText(text), 0, width ) + LINE_FEED;
		}
		return LINE_FEED;
	}	
	
	private int getLeftColumnWidth() {
		return this.width - MAX_PRICE_SIZE - COLUMN_SPACE;
	}
	
	private String getText( String text ) {
		if (! StringUtils.isAsciiPrintable(text) ) {
			StringBuffer sb = new StringBuffer();
			for( int i = 0; i < text.length(); i++ ) {
				char c = text.charAt(i);
				if (! CharUtils.isAsciiPrintable(c) ) {
					int n = ArrayUtils.indexOf(INVALID_CHARS, c);
					if ( n != -1 ) {
						sb.append(VALID_CHARS[n]);
					}
				} else {
					sb.append(c);
				}
			}
			return sb.toString();
		}
		return text;
	}
	
	private void append( StringBuffer sb, String commands ) {
		sb.append( getCommands(commands) );
	}
	
	private String getCommands( String commands ) {
		StringBuffer sb = new StringBuffer();
		String _commands = StringUtils.deleteWhitespace(commands);
		String[] list = StringUtils.split(_commands, ",");
		for( String value : list ) {
			if ( NumberUtils.isDigits(value) ) {
				sb.append( (char) NumberUtils.toInt(value) );
			}
		}
		return sb.toString();
	}
	
	private Enterprise getEnterprise( Invoice invoice ) {
		Set<InvoiceDetail> set = invoice.getLines();
		if (! set.isEmpty() ) {
			InvoiceDetail id = (InvoiceDetail) set.iterator().next();
			return id.getWorkPlace().getEnterprise();
		}	
		return null;
	}	
	
	private String getNumberDate( Invoice invoice ) {
		StringBuffer sb = new StringBuffer();
		sb.append( "Num: ").append( invoice.getReferenceCode() );
		sb.append( "    Fecha: ");
		sb.append( DATE_FORMAT.format(new Date()) );
		return sb.toString();
	}
	
	private String getTradeName( Enterprise enterprise ) {
		StringBuffer sb = new StringBuffer();
		append( sb, getBold(true) );
		sb.append( getCenteredLine(enterprise.getRegistry().getFullName()) );
		append( sb, getBold(false) );
		return sb.toString();
	}
	
	private String getCompanyDocument( Company company ) {
		StringBuffer sb = new StringBuffer();
		sb.append( AonUtil.getMessage(ICommonConstants.COMPANY_DOCUMENT) ).append( ": ");
		sb.append( company.getDocumentType().getName(AonUtil.getCurrentLocale()) ).append( ": ");
		sb.append( company.getDocumentCountry() ).append( "-");
		sb.append( company.getDocument() );
		return sb.toString();		
	}

	private String getAddress( RegistryAddress address ) {
		StringBuffer sb = new StringBuffer();
		
		if (! StringUtils.isEmpty(address.getZip()) ) {
			sb.append( address.getZip() ).append( " ");
		}
		if (! StringUtils.isEmpty(address.getCity()) ) {
			sb.append( address.getCity() ).append( " ");
		}
		sb.append( "(").append(address.getGeozone().getName()).append(")");
		return sb.toString();		
	}

	private String getContact( Enterprise enterprise ) throws ManagerBeanException {
		StringBuffer sb = new StringBuffer();
		sb.append( AonUtil.getMessage(ICommonConstants.PHONE) ).append( ": ");
		sb.append( enterprise.getRegistry().getPhone().getValue() );
		RegistryMedia fax = enterprise.getRegistry().getFax();
		if ( (fax != null) && (fax.getId() != null) ) {
			sb.append(" - ").append( AonUtil.getMessage(ICommonConstants.FAX) );
			sb.append( ": ").append( fax.getValue() );			
		}
		return sb.toString();		
	}
	
	private void appendSeller( StringBuffer sb, Invoice invoice ) {
		if ( (invoice.getSeller() != null) && (invoice.getSeller().getId() != null) ) {
			sb.append( getCenteredLine("Le atendio: " + invoice.getSeller().getRegistry().getFullName()) );
		}
	}
	
	private String getDetailHeader( boolean gift ) {
		StringBuffer sb = new StringBuffer();
		append( sb, getBold(true) );
		append( sb, getUnderline2() );
		if ( gift ) {
			sb.append( StringUtils.rightPad(PRODUCTO_LABEL, this.width) );
		} else {
			sb.append( StringUtils.rightPad(PRODUCTO_LABEL, getLeftColumnWidth()) );
			append( sb, getUnderlineOff() );
			sb.append( "  " );
			append( sb, getUnderline2() );
			sb.append( StringUtils.leftPad(IMPORTE_LABEL, MAX_PRICE_SIZE) );			
		}
		append( sb, getBold(false) );
		append( sb, getUnderlineOff() );
		sb.append( LINE_FEED );
		return sb.toString();
	}

	private String getDetailLines( Invoice invoice, boolean gift ) {
		StringBuffer sb = new StringBuffer();
		for( InvoiceDetail id : invoice.getLines() ) {
			if ( id.getQuantity() > 1 ) {
				sb.append( QUANTITY_FORMAT.format(id.getQuantity()) );
				if (! gift ) {
					sb.append( " x " );
					sb.append( PRICE_FORMAT.format(id.getSalesPrice()) );					
				}
				sb.append( LINE_FEED );
			}
			if ( gift ) {
				sb.append( getLine(id.getDescription()) );
			} else {
				String description = StringUtils.substring(id.getDescription(), 0, getLeftColumnWidth() );
				sb.append( StringUtils.rightPad( description, getLeftColumnWidth()) );
				sb.append( StringUtils.leftPad( PRICE_FORMAT.format(id.getTotalSalesPrice()), (MAX_PRICE_SIZE+COLUMN_SPACE)) );
				sb.append( LINE_FEED );
			}
		}
		return sb.toString();
	}
	
	private String getTotalVAT( Invoice invoice, IPriceStrategy priceStrategy ) {
		StringBuffer sb = new StringBuffer();
		append( sb, getUnderline() );
		append( sb, getBold(true) );
		sb.append( StringUtils.leftPad("Total IVA Incluido", getLeftColumnWidth()) );
		double price = priceStrategy.getTotalPrice(invoice, invoice);
		sb.append( StringUtils.leftPad( PRICE_FORMAT.format(price), (MAX_PRICE_SIZE+COLUMN_SPACE)) );
		append( sb, getBold(false) );
		append( sb, getUnderlineOff() );
		sb.append( LINE_FEED );
		return sb.toString();
	}

	private String getFinances( Invoice invoice ) {
		StringBuffer sb = new StringBuffer();
		for( Finance finance : invoice.getFinances() ) {
			sb.append( StringUtils.leftPad( "Pagado " + finance.getPayMethod().getName(), getLeftColumnWidth()) );
			sb.append( StringUtils.leftPad( PRICE_FORMAT.format(finance.getAmount()), (MAX_PRICE_SIZE+COLUMN_SPACE)) );
			sb.append( LINE_FEED );
		}
		append( sb, getUnderlineOff() );
		return sb.toString();
	}
	
	private String getTaxBreakDownHeader() {
		StringBuffer sb = new StringBuffer();
		int leftSpace = this.width-(3*(MAX_PRICE_SIZE+COLUMN_SPACE));
		sb.append( StringUtils.leftPad("", leftSpace+COLUMN_SPACE) );
		append( sb, getBold(true) );
		append( sb, getUnderline2() );
		sb.append( StringUtils.leftPad("BASE", MAX_PRICE_SIZE) );
		append( sb, getUnderlineOff() );
		sb.append( "  " );
		append( sb, getUnderline2() );
		sb.append( StringUtils.leftPad("CUOTA", MAX_PRICE_SIZE) );
		append( sb, getUnderlineOff() );
		sb.append( "  " );
		append( sb, getUnderline2() );
		sb.append( StringUtils.leftPad("TOTAL", MAX_PRICE_SIZE) );
		append( sb, getUnderlineOff() );
		append( sb, getBold(false) );
		sb.append( LINE_FEED );
		return sb.toString();
	}
	
	private String getTaxBreakDowns( Invoice invoice, IPriceStrategy priceStrategy ) {
		StringBuffer sb = new StringBuffer();
		int leftSpace = this.width-(3*(MAX_PRICE_SIZE+COLUMN_SPACE));
		for( TaxBreakDown tbd : priceStrategy.getTaxBreakDowns(invoice, invoice) ) {
			if ( tbd.getBase() > 0 ) {
				StringBuffer base = new StringBuffer();
				base.append( PERCENT_FORMAT.format(tbd.getTaxPercent()) ); 
				if ( tbd.getSurchargePercent() > 0 ) {
					base.append( " + ").append( PERCENT_FORMAT.format(tbd.getSurchargePercent()) );
				}
				base.append("% ").append(tbd.getTaxType().getName(AonUtil.getCurrentLocale()));
				sb.append( StringUtils.rightPad( StringUtils.substring(base.toString(), 0, leftSpace), leftSpace) );
			} else {
				sb.append( StringUtils.leftPad("", leftSpace) );
			}
			sb.append( StringUtils.leftPad( PRICE_FORMAT.format(tbd.getBase()), MAX_PRICE_SIZE+COLUMN_SPACE) );
			double quota = tbd.getTaxQuota() + tbd.getSurchargeQuota();
			sb.append( StringUtils.leftPad( PRICE_FORMAT.format(quota), MAX_PRICE_SIZE+COLUMN_SPACE) );
			String total = PRICE_FORMAT.format(tbd.getBase() + quota);
			sb.append( StringUtils.leftPad( total, MAX_PRICE_SIZE+COLUMN_SPACE) );			
			sb.append( LINE_FEED );
		}
		append( sb, getUnderlineOff() );
		return sb.toString();
	}
	
	private String getTicket( Invoice invoice, boolean gift ) throws ManagerBeanException {
		PosInvoiceParamsController pipc = (PosInvoiceParamsController) AonUtil.getRegisteredBean(POS_INVOICE_PARAMS_CONTROLLER_NAME);
		pipc.onInit(null);
		SaleInvoiceController sic = (SaleInvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
		IPriceStrategy priceStrategy = sic.getPriceStrategy();
		
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = companyController.obtainCompany();
		Enterprise enterprise = getEnterprise(invoice);
		
		StringBuffer sb = new StringBuffer();
		append( sb, getInitializePrinter() );
		
		if ( pipc.isPrintLogo() ) {
			append( sb, getAlignCenter() );
			append( sb, getPrintLogo() );
			append( sb, getAlignLeft() );
			append( sb, getFeedLines(1) );
		}
					
		if ( pipc.isPrintTradename() ) {
			sb.append( getTradeName(enterprise) );
			append( sb, getFeedLines(1) );
		}

		if ( pipc.getPrintDirStaff() == ReportPrintOption.HEADER ) {
			sb.append( getLine(getCompanyDocument(company)) );
			sb.append( getLine(getAddress(enterprise.obtainAddress())) );
			sb.append( getLine(getContact(enterprise)) );
			append( sb, getFeedLines(1) );
		}

		if ( gift ) {
			String label = AonUtil.getMessage(BUNDLE_NAME, POS_GIFT_RECEIPT);
			sb.append( getCenteredLine(StringUtils.upperCase(label)) );	
		} else {
			String label = AonUtil.getMessage(BUNDLE_NAME, POS_RECEIPT);
			sb.append( getCenteredLine(StringUtils.upperCase(label)) );				
		}
		sb.append( getLine(getNumberDate(invoice)) );
		if ( pipc.getPrintSellerName() == ReportPrintOption.HEADER ) {
			appendSeller(sb, invoice);
		}

		append( sb, getFeedLines(1) );
		sb.append( getDetailHeader(gift) );
		sb.append( getDetailLines(invoice, gift) );
		if (! gift ) {
			append( sb, getFeedLines(1) );
			sb.append( getTotalVAT(invoice, priceStrategy) );
			append( sb, getFeedLines(1) );
			sb.append( getFinances(invoice) );
			append( sb, getFeedLines(1) );
			sb.append( getTaxBreakDownHeader() );
			sb.append( getTaxBreakDowns(invoice, priceStrategy) );
		}
		
		append( sb, getFeedLines(2) );
		
		if ( pipc.getPrintDirStaff() == ReportPrintOption.FOOTER ) {
			sb.append( getLine(getCompanyDocument(company)) );
			sb.append( getLine(getAddress(enterprise.obtainAddress())) );
			sb.append( getLine(getContact(enterprise)) );
			append( sb, getFeedLines(1) );
		}
		
		if ( pipc.getPrintSellerName() == ReportPrintOption.FOOTER ) {
			appendSeller(sb, invoice);
			append( sb, getFeedLines(1) );
		}
		if ( pipc.isPrintDomain() ) {
			sb.append( getCenteredLine(company.getWeb().getValue()) );
		}
		sb.append( getCenteredLine(pipc.getFooterText()) );

		append( sb, getFeedLines(3) );
		append( sb, getCutTicket() );
		append( sb, getOpenDrawer() );
		append( sb, getInitializePrinter() );
		return new String(Base64.encodeBase64(sb.toString().getBytes()));
	}

	public String execute( Invoice invoice, boolean gift ) throws ReportException {
		String ticket = null;
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {
			HibernateUtil.startSession(sessionFactoryName);
			HibernateUtil.beginTransaction(sessionFactoryName);

			HibernateUtil.getSession(sessionFactoryName).refresh(invoice);
			ticket = getTicket(invoice, gift);

			HibernateUtil.commitTransaction(sessionFactoryName);
			HibernateUtil.closeSession(sessionFactoryName);
		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
			try {
				HibernateUtil.rollbackTransaction(sessionFactoryName);
			} catch (DAOException e) {
				LOGGER.error(e.getMessage(), e);
			}
			AonUtil.addFatalMessage("Ticket Error:" + t.getMessage());
			throw new ReportException(t.getMessage(), t);
		} finally {
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}
		}
		return ticket;
	}
	
	
}