package com.code.aon.file.bank.model.SEPA;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.code.aon.file.format.core.Account;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.format.model.FileFiller;

public abstract class BasicSEPAXml implements FileFiller, ISEPAConstants {
	
	private static final char[] VALID_CHARS = new char[] {
		'/', '-', '?', ':', '(', ')', '.', ',', '\'', '+', ' ', '&', '<', '>', '"' };

	private static final char[] REPLACEABLE_CHARS = new char[] {
		'\u00C1', '\u00C9', '\u00CD', '\u00D3', '\u00DA', '\u00E1', '\u00E9', '\u00ED', '\u00F3', '\u00FA',
		'\u00C4', '\u00CB', '\u00CF', '\u00D6', '\u00DC', '\u00E4', '\u00EB', '\u00EF', '\u00F6', '\u00FC',
		'\u00D1', '\u00F1', '\u00C7', '\u00E7' };

	private static final String[] REPLACEMENT_STRINGS = new String[] {
		"A", "E", "I", "O", "U", "a", "e", "i", "o", "u",
		"A", "E", "I", "O", "U", "a", "e", "i", "o", "u",
		"N", "n", "C", "c" };
	
	private Document document;
	
	@Deprecated
	private File file;
	private PrintWriter writer;
	
	private ArrayList<Exception> exceptions;
	
	/**
	 * This will be replaced with <code>PrintWriter</code> argument's Constructor
	 * @param file
	 */
	@Deprecated
	public BasicSEPAXml(File file) {
		this.file = file;
		this.exceptions = new ArrayList<Exception>();
	}

	public BasicSEPAXml(PrintWriter writer) {
		this.writer = writer;
		this.exceptions = new ArrayList<Exception>();
	}
	
	protected Element createDocument( String xmlnsValue) throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		this.document = docBuilder.newDocument();
		Element rootElement = document.createElement(DOCUMENT);
		
		rootElement.setAttribute(XMLNS_ATTRIBUTE, xmlnsValue);
		rootElement.setAttribute(XMLNS_XSI_ATTRIBUTE, XMLNS_XSI_VALUE);
		document.appendChild(rootElement);		
		
		return rootElement;
	}
	
	protected void writeDocument() throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = null;
		if(this.file!=null){
			result = new StreamResult(this.file);
		} else {
			result = new StreamResult(this.writer);
		}
 		transformer.transform(source, result);
	}
	
    protected void addRawValue( Element element, String value ) {
		element.appendChild(document.createTextNode(value));
	}    
    
	protected void addValue( Element element, String value, int maxLength ) {
		if (! StringUtils.isEmpty(value) ) {
			String _value = getText(value);
			addRawValue(element, StringUtils.substring(_value, 0, maxLength));
		}
	}
	
	private boolean isValidChar( char c ) {
		return CharUtils.isAsciiAlphanumeric(c) || ArrayUtils.contains(VALID_CHARS, c);
	}
	
	private String getText( String text ) {
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < text.length(); i++ ) {
			char c = text.charAt(i);
			if ( isValidChar(c) ) {
				sb.append(c);
			} else {
				int n = ArrayUtils.indexOf(REPLACEABLE_CHARS, c);
				if ( n != -1 ) {
					sb.append(REPLACEMENT_STRINGS[n]);
				} else {
					sb.append( ' ' );
				}
			}
		}
		return sb.toString();
	}	

	protected void addValue( Element element, String value ) {
		if (! StringUtils.isEmpty(value) ) {
			addRawValue(element, getText(value) );
		}
	}
	
	protected void addValue( Element element, Integer value ) {
		addValue(element, value.toString());
	}

	protected void addISODateTime( Element element, Date value ) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		addValue(element, df.format(value));
	}

	protected void addISODate( Element element, Date value ) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		addValue(element, df.format(value));
	}

	protected void addNumber( Element element, double value, int maxLength ) {
		DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.ENGLISH));
		String string = df.format(value);
		addValue(element, string, maxLength);
	}	
	
	protected void addDecimalNumber( Element element, double value ) {
		addNumber(element, value, 18);
	}	
	
	protected void addMoney( Element element, double value ) {
		addNumber(element, value, 11);
	}		

	protected Element createElement( String value ) {
		return document.createElement(value);
	}		
	
	protected abstract String geDescription();
	
	protected abstract Element createMainElement() throws ParserConfigurationException;
	
	protected abstract void createContent( Element mainElement );
	
	protected Element createGroupHeader( Element parent, String id, Date date, int numberOfTxs, double amount ) {
		Element groupHeader = createElement(GROUP_HEADER);
		parent.appendChild(groupHeader);
	
		Element messageIdentification = createElement(MESSAGE_IDENTIFICATION);
		addValue(messageIdentification, id, 35);
		groupHeader.appendChild(messageIdentification);
		
		Element creationDateTime = createElement(CREATION_DATE_TIME);
		addISODateTime(creationDateTime, date);
		groupHeader.appendChild(creationDateTime);
		
		Element numberOfTransactions = createElement(NUMBER_OF_TRANSACTIONS);
		addValue(numberOfTransactions, numberOfTxs);
		groupHeader.appendChild(numberOfTransactions);

		Element controlSum = createElement(CONTROL_SUM);
		addDecimalNumber(controlSum, amount);
		groupHeader.appendChild(controlSum);
		
		return groupHeader;
	}
	
	protected void addAddress( Element element, Address address ) {
		Element postalAddress = createElement(POSTAL_ADDRESS);
		element.appendChild(postalAddress);
		
		if ( address.getCountry() != null ) {
			Element country = createElement(COUNTRY);
			addValue(country, address.getCountry(), 2);
			postalAddress.appendChild(country);
		}

		String addressLine2 = null;
		String addressLine = getText(address.getAddressLine());
		if ( addressLine.length() > 70 ) {
			int mid = StringUtils.lastIndexOf(StringUtils.substring(addressLine, 0, 70), ' ');
			int start = (mid == -1) ? 70 : mid+1;
			addressLine2 = StringUtils.substring(StringUtils.substring(addressLine, start), 0, 70);
			int end = (mid == -1) ? 70 : mid;
			addressLine = StringUtils.substring(addressLine, 0, end);
		}
		
		Element addressLine1Element = createElement(ADDRESS_LINE);
		addRawValue(addressLine1Element, addressLine);
		postalAddress.appendChild(addressLine1Element);
		
		if ( addressLine2 != null ) {
			Element addressLine2Element = createElement(ADDRESS_LINE);
			addRawValue(addressLine2Element, StringUtils.substring(addressLine2, 0, 70));
			postalAddress.appendChild(addressLine2Element);				
		}		
	}	
	
	protected void addOrganisationIdentification( Element parent, String id, String issuer, String code, String propietary ) {
		addIdentification( parent, ORGANISATION_IDENTIFICATION, id, code, propietary, issuer );
	}	
	
	protected void addPrivateIdentification( Element parent, String id, String propietary, String issuer ) {
		addIdentification( parent, PRIVATE_IDENTIFICATION, id, null, propietary, issuer );
	}	
	
	protected void addIdentification( Element parent, String idElement, String id, String code, String propietary, String issuer ) {
		if (! StringUtils.isEmpty(id) ) {
			Element mainElement = createElement(IDENTIFICATION);
			parent.appendChild(mainElement);		
	
			Element identification = createElement(idElement);
			mainElement.appendChild(identification);		
	
			Element other = createElement(OTHER);
			identification.appendChild(other);		
	
			Element innerIdentification = createElement(IDENTIFICATION);
			addValue(innerIdentification, id, 35);
			other.appendChild(innerIdentification);
			
			if ( (code != null) || (propietary != null) ) {
				Element schemeName = createElement(SCHEME_NAME);
				other.appendChild(schemeName);		
				
				if ( code != null ) {
					Element codeElement = createElement(CODE);
					addValue(codeElement, code, 4);
					schemeName.appendChild(codeElement);										
				}
				if ( propietary != null ) {
					Element propietaryElement = createElement(PROPRIETARY);
					addValue(propietaryElement, propietary, 35);
					schemeName.appendChild(propietaryElement);					
				}
			}
			
			if ( issuer != null ) {
				Element issuerElement = createElement(ISSUER);
				addValue(issuerElement, issuer, 35);
				other.appendChild(issuerElement);					
			}
		}
	}		
	
	protected void addDebtor( Element parent, Entity entity ) {
		addEntity(parent, DEBTOR, entity, true);
	}
	
	protected void addCreditor( Element parent, Entity entity, boolean includeIdentification ) {
		addEntity(parent, CREDITOR, entity, includeIdentification);
	}
	
	private void addEntity( Element parent, String elementName, Entity entity, boolean includeIdentification ) {
		Element debtor = createElement(elementName);
		parent.appendChild(debtor);			
		
		Element name = createElement(NAME);
		addValue(name, entity.getName(), 70);
		debtor.appendChild(name);		
	
		Address address = entity.getSEPAAddress();
		if ( address != null ) {
			addAddress(debtor, address);
		}		
		
		if ( includeIdentification ) {
			if ( entity.isOrganisation() ) {
				addOrganisationIdentification(debtor, entity.getDocument(), entity.getDocumentType(), null, null );
			} else {
				addPrivateIdentification(debtor, entity.getDocument(), null, entity.getDocumentType());
			}			
		}
	}
	
	protected void addDebtorAccount( Element parent, Account account, String currency ) {
		Element debtorAccount = createElement(DEBTOR_ACCOUNT);
		parent.appendChild(debtorAccount);		
		
		Element identification = createElement(IDENTIFICATION);
		debtorAccount.appendChild(identification);		
		
		Element iban = createElement(IBAN);
		addValue(iban, account.getIban(), 34);
		identification.appendChild(iban);			
		
		if ( currency != null ) {
			Element currencyElement = createElement(CURRENCY_ATTRIBUTE);
			addValue(currencyElement, currency, 3);
			debtorAccount.appendChild(currencyElement);					
		}		
	}	
	
	protected void addBic( Element parent, String bic ) {
		Element financialInstitutionIdentification = createElement(FINANCIAL_INSTITUTION_IDENTIFICATION);
		parent.appendChild(financialInstitutionIdentification);				

		Element bicElement = createElement(BIC);
		addValue(bicElement, bic, 34);
		financialInstitutionIdentification.appendChild(bicElement);						
	}
	
	protected void addDebtorAgent( Element directDebitTransactionInformation, Account account ) {
		Element debtorAgent = createElement(DEBTOR_AGENT);
		directDebitTransactionInformation.appendChild(debtorAgent);		
		
		addBic(debtorAgent, account.getBic());
	}	

	protected void addPaymentIdentification( Element parent, String code ) {
		Element paymentIdentification = createElement(PAYMENT_IDENTIFICATION);
		parent.appendChild(paymentIdentification);
		
		Element endToEndIdentification = createElement(END_TO_END_IDENTIFICATION);
		addValue(endToEndIdentification, code, 35);
		paymentIdentification.appendChild(endToEndIdentification);								
	}	
	
	protected void addInstructedAmount( Element parent, double amount ) {
		Element instructedAmount = createElement(INSTRUCTED_AMOUNT);
		instructedAmount.setAttribute(CURRENCY_ATTRIBUTE, CURRENCY_EUR_VALUE);
		addMoney(instructedAmount, amount);
		parent.appendChild(instructedAmount);					
	}	
	
	protected void addCreditorAccount( Element parent, Account account ) {
		Element creditorAccount = createElement(CREDITOR_ACCOUNT);
		parent.appendChild(creditorAccount);			
		
		Element id = createElement(IDENTIFICATION);
		creditorAccount.appendChild(id);				

		Element iban = createElement(IBAN);
		addValue(iban, account.getIban(), 34);
		id.appendChild(iban);				
	}	
	
	protected void addCreditorAgent( Element parent, Account account ) {
		Element creditorAgent = createElement(CREDITOR_AGENT);
		parent.appendChild(creditorAgent);		
		
		addBic(creditorAgent, account.getBic());
	}	
	
	protected void addChargeBearer( Element parent, String value ) {
		Element chargeBearer = createElement(CHARGE_BEARER);
		parent.appendChild(chargeBearer);		
		
		addValue(chargeBearer, value);
	}	
	
	protected void addPaymentTypeInformation( Element parent, String localInstrumentCode, String sequenceType ) {
		addPaymentTypeInformation(parent, null, localInstrumentCode, sequenceType);
	}

	protected void addPaymentTypeInformation( Element parent, String categoryPurposeCode ) {
		addPaymentTypeInformation(parent, categoryPurposeCode, null, null);
	}
	
	private void addPaymentTypeInformation( Element parent, String categoryPurposeCode, String localInstrumentCode, String sequenceType ) {
		Element paymentTypeInformation = createElement(PAYMENT_TYPE_INFORMATION);
		parent.appendChild(paymentTypeInformation);	

		Element serviceLevel = createElement(SERVICE_LEVEL);
		paymentTypeInformation.appendChild(serviceLevel);
		
		Element serviceLevelCode = createElement(CODE);
		addValue(serviceLevelCode, SEPA_VALUE);
		serviceLevel.appendChild(serviceLevelCode);

		if ( categoryPurposeCode != null ) {
			Element categoryPurpose = createElement(CATEGORY_PURPOSE);
			paymentTypeInformation.appendChild(categoryPurpose);
			
			Element categoryPurposeCodeElement = createElement(CODE);
			addValue(categoryPurposeCodeElement, categoryPurposeCode);
			categoryPurpose.appendChild(categoryPurposeCodeElement);
		}
		
		if ( localInstrumentCode != null ) {
			Element localInstrument = createElement(LOCAL_INSTRUMENT);
			paymentTypeInformation.appendChild(localInstrument);
			
			Element localInstrumentCodeElement = createElement(CODE);
			addValue(localInstrumentCodeElement, localInstrumentCode);
			localInstrument.appendChild(localInstrumentCodeElement);
		}

		if ( sequenceType != null ) {
			Element sequenceTypeElement = createElement(SEQUENCE_TYPE);
			addValue(sequenceTypeElement, sequenceType);
			paymentTypeInformation.appendChild(sequenceTypeElement);					
		}
	}	
	
	protected void addRemittanceInformation( Element parent, String documentNumber, boolean payment ) {
		if (! StringUtils.isEmpty(documentNumber) ) {			
			Element remittanceInformation = createElement(REMITTANCE_INFORMATION);
			parent.appendChild(remittanceInformation);			
			
			Element unstructured = createElement(UNSTRUCTURED);
			StringBuffer sb = new StringBuffer(payment?"PAGO":"COBRO");
			sb.append( " Factura: ").append(documentNumber);
			addValue(unstructured, sb.toString(), 140);
			remittanceInformation.appendChild(unstructured);				
		}
	}	
	
	@Override
	public ArrayList<Exception> create() {
		try {
			Element mainElement = createMainElement();
			createContent(mainElement);
			writeDocument();
		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(), geDescription());
				exceptions.add (e);
			}
		}
		return exceptions;
	}
	
}
