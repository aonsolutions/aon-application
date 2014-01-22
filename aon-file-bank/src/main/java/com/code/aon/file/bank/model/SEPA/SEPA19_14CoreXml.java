package com.code.aon.file.bank.model.SEPA;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.code.aon.file.bank.model.CSB19.data.Address;
import com.code.aon.file.bank.model.CSB19.data.Individual;
import com.code.aon.file.bank.model.CSB19.data.Lot;
import com.code.aon.file.bank.model.CSB19.data.Orderer;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.format.model.FileFiller;

public class SEPA19_14CoreXml implements FileFiller, ISEPAConstants {
	
	private Lot lot;
	
	private File file;
	
	private Document document;
	
	private ArrayList<Exception> exceptions;

	public SEPA19_14CoreXml(Lot lot, File file) {
		this.lot = lot;
		this.file = file;
		this.exceptions = new ArrayList<Exception>();
	}
	
	private Orderer getOrderer() {
		return this.lot.getOrderersIterator().next();
	}
	
	private Element createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		this.document = docBuilder.newDocument();
		Element rootElement = document.createElement(DOCUMENT);
		
		rootElement.setAttribute(XMLNS_ATTRIBUTE, XMLNS_SEPA_14_14_CORE_VALUE);
		rootElement.setAttribute(XMLNS_XSI_ATTRIBUTE, XMLNS_XSI_VALUE);
		document.appendChild(rootElement);		
		
		Element customerDirectDebitInitiation = document.createElement(CUSTOMER_DIRECT_DEBIT_INITIATION);
		rootElement.appendChild(customerDirectDebitInitiation);
		
		return customerDirectDebitInitiation;
	}
	
	private void writeDocument() throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(this.file);
 		transformer.transform(source, result);				
	}
	
	private boolean isHoliday( Calendar calendar ) {
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		if (Calendar.JANUARY == month) {
			if ( (dayOfMonth==1) || (dayOfMonth==6) ) {
				return true;	
			}
		}
		if ( (Calendar.MAY == month) && (dayOfMonth==1) ) {
			return true;
		}
		if ( (Calendar.OCTOBER == month) && (dayOfMonth==12) ) {
			return true;
		}
		if ( (Calendar.NOVEMBER == month) && (dayOfMonth==1) ) {
			return true;
		}
		if (Calendar.DECEMBER == month) {
			if ( (dayOfMonth==6) || (dayOfMonth==8) || (dayOfMonth==25)) {
				return true;	
			}
		}
		return false;		
	}
	
	private boolean esHabil(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		switch ( calendar.get(Calendar.DAY_OF_WEEK) ) {
			case Calendar.SUNDAY:
				return false;
			default:
				return !isHoliday(calendar);
        }
    }	
	
    private Date fechaHabil(Date date, int days) {
    	Date result = date;
    	for (int i = 0; i < days; i++) {
    		result = DateUtils.addDays(result, 1);
    		while (!esHabil(result)) {
    			result = DateUtils.addDays(result, 1);
    		}
    	}
    	return result;
    }
    
	private void addRawValue( Element element, String value ) {
		element.appendChild(document.createTextNode(value));
	}    
    
	private void addValue( Element element, String value, int maxLength ) {
		if (! StringUtils.isEmpty(value) ) {
			String _value = StringEscapeUtils.escapeXml(value);
			addRawValue(element, StringUtils.substring(_value, 0, maxLength));
		}
	}

	private void addValue( Element element, String value ) {
		if (! StringUtils.isEmpty(value) ) {
			addRawValue(element, StringEscapeUtils.escapeXml(value) );
		}
	}
	
	private void addValue( Element element, Integer value ) {
		addValue(element, value.toString());
	}

	private void addISODateTime( Element element, Date value ) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		addValue(element, df.format(value));
	}

	private void addISODate( Element element, Date value ) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		addValue(element, df.format(value));
	}

	private void addNumber( Element element, double value, int maxLength ) {
		DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.ENGLISH));
		String string = df.format(value);
		addValue(element, string, maxLength);
	}	
	
	private void addDecimalNumber( Element element, double value ) {
		addNumber(element, value, 18);
	}	
	
	private void addMoney( Element element, double value ) {
		addNumber(element, value, 11);
	}	
	
	private void addPrivateIdentification( Element parent, String id, String propietary, String issuer ) {
		Element identification = document.createElement(IDENTIFICATION);
		parent.appendChild(identification);		

		Element privateIdentification = document.createElement(PRIVATE_IDENTIFICATION);
		identification.appendChild(privateIdentification);		

		Element other = document.createElement(OTHER);
		privateIdentification.appendChild(other);		

		Element innerIdentification = document.createElement(IDENTIFICATION);
		addValue(innerIdentification, id, 35);
		other.appendChild(innerIdentification);
		
		if ( propietary != null ) {
			Element schemeName = document.createElement(SCHEME_NAME);
			other.appendChild(schemeName);		
			
			Element propietaryElement = document.createElement(PROPRIETARY);
			addValue(propietaryElement, propietary, 35);
			schemeName.appendChild(propietaryElement);
		}
		
		if ( issuer != null ) {
			Element issuerElement = document.createElement(ISSUER);
			addValue(issuerElement, issuer, 35);
			other.appendChild(issuerElement);					
		}
	}

	private void addOrganisationIdentification( Element parent, String id, String issuer ) {
		Element identification = document.createElement(IDENTIFICATION);
		parent.appendChild(identification);		

		Element organisationIdentification = document.createElement(ORGANISATION_IDENTIFICATION);
		identification.appendChild(organisationIdentification);
		
		Element other = document.createElement(OTHER);
		organisationIdentification.appendChild(other);		

		Element innerIdentification = document.createElement(IDENTIFICATION);
		addValue(innerIdentification, id, 35);
		other.appendChild(innerIdentification);

		if ( issuer != null ) {
			Element issuerElement = document.createElement(ISSUER);
			addValue(issuerElement, issuer, 35);
			other.appendChild(issuerElement);					
		}
	}
	
	private void addInitiatingParty( Element groupHeader ) {
		Element initiatingParty = document.createElement(INITIATING_PARTY);
		groupHeader.appendChild(initiatingParty);		
		
		Element name = document.createElement(NAME);
		addValue(name, lot.getPresenter().getName(), 70);
		initiatingParty.appendChild(name);
	
		addPrivateIdentification(initiatingParty, getOrderer().getId(), null, null);
	}
	
	private void addGroupHeader( Element customerDirectDebitInitiation ) {
		Element groupHeader = document.createElement(GROUP_HEADER);
		customerDirectDebitInitiation.appendChild(groupHeader);
	
		Element messageIdentification = document.createElement(MESSAGE_IDENTIFICATION);
		addValue(messageIdentification, lot.getPresenter().getId(), 35);
		groupHeader.appendChild(messageIdentification);
		
		Element creationDateTime = document.createElement(CREATION_DATE_TIME);
		addISODateTime(creationDateTime, lot.getPresenter().getMakeDate());
		groupHeader.appendChild(creationDateTime);
		
		Element numberOfTransactions = document.createElement(NUMBER_OF_TRANSACTIONS);
		addValue(numberOfTransactions, getOrderer().getNumIndividuals());
		groupHeader.appendChild(numberOfTransactions);

		Element controlSum = document.createElement(CONTROL_SUM);
		addDecimalNumber(controlSum, lot.getAmount());
		groupHeader.appendChild(controlSum);
		
		addInitiatingParty(groupHeader);
	}

	private void addPaymentTypeInformation( Element paymentInformation ) {
		Element paymentTypeInformation = document.createElement(PAYMENT_TYPE_INFORMATION);
		paymentInformation.appendChild(paymentTypeInformation);	

		Element serviceLevel = document.createElement(SERVICE_LEVEL);
		paymentTypeInformation.appendChild(serviceLevel);
		
		Element serviceLevelCode = document.createElement(CODE);
		addValue(serviceLevelCode, SEPA_VALUE);
		serviceLevel.appendChild(serviceLevelCode);
		
		Element localInstrument = document.createElement(LOCAL_INSTRUMENT);
		paymentTypeInformation.appendChild(localInstrument);
		
		Element localInstrumentCode = document.createElement(CODE);
		addValue(localInstrumentCode, LOCAL_INSTRUMENT_CODE_CORE_VALUE);
		localInstrument.appendChild(localInstrumentCode);		

		Element sequenceType = document.createElement(SEQUENCE_TYPE);
		addValue(sequenceType, SEQUENCE_TYPE_RCUR_VALUE);
		paymentTypeInformation.appendChild(sequenceType);		
	}
	
	private void addAddress( Element element, Address address ) {
		Element postalAddress = document.createElement(POSTAL_ADDRESS);
		element.appendChild(postalAddress);
		
		if ( address.getCountry() != null ) {
			Element country = document.createElement(COUNTRY);
			addValue(country, address.getCountry(), 2);
			postalAddress.appendChild(country);
		}

		String addressLine2 = null;
		String addressLine = StringEscapeUtils.escapeXml(address.getAddressLine());
		if ( addressLine.length() > 70 ) {
			int mid = StringUtils.lastIndexOf(StringUtils.substring(addressLine, 0, 70), ' ');
			int start = (mid == -1) ? 70 : mid+1;
			addressLine2 = StringUtils.substring(StringUtils.substring(addressLine, start), 0, 70);
			int end = (mid == -1) ? 70 : mid;
			addressLine = StringUtils.substring(addressLine, 0, end);
		}
		
		Element addressLine1Element = document.createElement(ADDRESS_LINE);
		addRawValue(addressLine1Element, addressLine);
		postalAddress.appendChild(addressLine1Element);
		
		if ( addressLine2 != null ) {
			Element addressLine2Element = document.createElement(ADDRESS_LINE);
			addRawValue(addressLine2Element, StringUtils.substring(addressLine2, 0, 70));
			postalAddress.appendChild(addressLine2Element);				
		}		
	}
	
	private void addCreditor( Element paymentInformation ) {
		Element creditor = document.createElement(CREDITOR);
		paymentInformation.appendChild(creditor);				

		Element name = document.createElement(NAME);
		addValue(name, getOrderer().getName(), 70);
		creditor.appendChild(name);
		
		Address address = getOrderer().getAddress();
		if ( address != null ) {
			addAddress(creditor, address);
		}
	}

	private void addCreditorAccount( Element paymentInformation ) {
		Element creditorAccount = document.createElement(CREDITOR_ACCOUNT);
		paymentInformation.appendChild(creditorAccount);			
		
		Element id = document.createElement(IDENTIFICATION);
		creditorAccount.appendChild(id);				

		Element iban = document.createElement(IBAN);
		addValue(iban, lot.getPresenter().getIban(), 34);
		id.appendChild(iban);				
	}
	
	private void addBic( Element parent, String id ) {
		Element financialInstitutionIdentification = document.createElement(FINANCIAL_INSTITUTION_IDENTIFICATION);
		parent.appendChild(financialInstitutionIdentification);				

		Element bic = document.createElement(BIC);
		addValue(bic, lot.getPresenter().getBic(), 34);
		financialInstitutionIdentification.appendChild(bic);						
	}

	private void addCreditorAgent( Element paymentInformation ) {
		Element creditorAgent = document.createElement(CREDITOR_AGENT);
		paymentInformation.appendChild(creditorAgent);		
		
		addBic(creditorAgent, lot.getPresenter().getBic());
	}

	private void addCreditorIdentification( Element paymentInformation ) {
		Element creditorIdentification = document.createElement(CREDITOR_IDENTIFICATION);
		paymentInformation.appendChild(creditorIdentification);			
		
		addPrivateIdentification(creditorIdentification, getOrderer().getId(), SEPA_VALUE, null);
	}

	private void addDirectDebitTransaction( Element directDebitTransactionInformation, Individual individual ) {
		Element directDebitTransaction = document.createElement(DIRECT_DEBIT_TRANSACTION);
		directDebitTransactionInformation.appendChild(directDebitTransaction);		
		
		Element mandateRelatedInformation = document.createElement(MANDATE_RELATED_INFORMATION);
		directDebitTransaction.appendChild(mandateRelatedInformation);
		
		Element mandateIdentification = document.createElement(MANDATE_IDENTIFICATION);
		addValue(mandateIdentification, individual.getReferenceCode(), 35);
		mandateRelatedInformation.appendChild(mandateIdentification);		
		
		Element dateOfSignature = document.createElement(DATE_OF_SIGNATURE);
		addValue(dateOfSignature, DATE_OF_SIGNATURE_MIGRATION_VALUE);
		mandateRelatedInformation.appendChild(dateOfSignature);		
	}
	
	private void addDebtorAgent( Element directDebitTransactionInformation, Individual individual ) {
		Element debtorAgent = document.createElement(DEBTOR_AGENT);
		directDebitTransactionInformation.appendChild(debtorAgent);		
		
		addBic(debtorAgent, individual.getAccount().getBic());
	}

	private void addDebtor( Element directDebitTransactionInformation, Individual individual ) {
		Element debtor = document.createElement(DEBTOR);
		directDebitTransactionInformation.appendChild(debtor);			
		
		Element name = document.createElement(NAME);
		addValue(name, individual.getName(), 70);
		debtor.appendChild(name);		
	
		Address address = individual.getAddress();
		if ( address != null ) {
			addAddress(debtor, address);
		}		
		
		if ( individual.isOrganisation() ) {
			addOrganisationIdentification(debtor, individual.getDocument(), individual.getDocumentType());
		} else {
			addPrivateIdentification(debtor, individual.getDocument(), null, individual.getDocumentType());
		}
	}

	private void addDebtorAccount( Element directDebitTransactionInformation, Individual individual ) {
		Element debtorAccount = document.createElement(DEBTOR_ACCOUNT);
		directDebitTransactionInformation.appendChild(debtorAccount);		
		
		Element identification = document.createElement(IDENTIFICATION);
		debtorAccount.appendChild(identification);		
		
		Element iban = document.createElement(IBAN);
		addValue(iban, individual.getAccount().getIban(), 34);
		identification.appendChild(iban);								
	}
	
	private void addDirectDebitTransactionInformation( Element paymentInformation, Individual individual ) {
		Element directDebitTransactionInformation = document.createElement(DIRECT_DEBIT_TRANSACTION_INFORMATION);
		paymentInformation.appendChild(directDebitTransactionInformation);	
		
		Element paymentIdentification = document.createElement(PAYMENT_IDENTIFICATION);
		directDebitTransactionInformation.appendChild(paymentIdentification);
		
		Element endToEndIdentification = document.createElement(END_TO_END_IDENTIFICATION);
		addValue(endToEndIdentification, individual.getReferenceCode(), 35);
		paymentIdentification.appendChild(endToEndIdentification);						

		Element instructedAmount = document.createElement(INSTRUCTED_AMOUNT);
		instructedAmount.setAttribute(CURRENCY_ATTRIBUTE, CURRENCY_EUR_VALUE);
		addMoney(instructedAmount, individual.getAmount());
		directDebitTransactionInformation.appendChild(instructedAmount);			
		
		addDirectDebitTransaction(directDebitTransactionInformation, individual);
		addDebtorAgent(directDebitTransactionInformation, individual);
		addDebtor(directDebitTransactionInformation, individual);
		addDebtorAccount(directDebitTransactionInformation, individual);
	}
	
	private void addPaymentInformation( Element customerDirectDebitInitiation ) {
		Element paymentInformation = document.createElement(PAYMENT_INFORMATION);
		customerDirectDebitInitiation.appendChild(paymentInformation);		
		
		Element paymentMethodIdentification = document.createElement(PAYMENT_INFORMATION_IDENTIFICATION);
		addValue(paymentMethodIdentification, lot.getPresenter().getId(), 35);
		paymentInformation.appendChild(paymentMethodIdentification);		

		Element paymentMethod = document.createElement(PAYMENT_METHOD);
		addValue(paymentMethod, PAYMENT_METHOD_VALUE);
		paymentInformation.appendChild(paymentMethod);		

		Element batchBooking = document.createElement(BATCH_BOOKING);
		addValue(batchBooking, FALSE_VALUE);
		paymentInformation.appendChild(batchBooking);		
		
		addPaymentTypeInformation(paymentInformation);
		
		Element requestedCollectionDate = document.createElement(REQUEST_COLLECTION_DATE);
		Date date = fechaHabil(new Date(), 5);
		addISODate(requestedCollectionDate, date);
		paymentInformation.appendChild(requestedCollectionDate);				
		
		addCreditor(paymentInformation);
		addCreditorAccount(paymentInformation);
		addCreditorAgent(paymentInformation);
		addCreditorIdentification(paymentInformation);
		
		Iterator<Individual> ii = getOrderer().getIndividualsIterator();
		while ( ii.hasNext() ) {
			addDirectDebitTransactionInformation(paymentInformation, ii.next());	
		}
	}
	
	@Override
	public ArrayList<Exception> create() {
		try {
			Element customerDirectDebitInitiation = createDocument();
			addGroupHeader(customerDirectDebitInitiation);
			addPaymentInformation(customerDirectDebitInitiation);
			writeDocument();
		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(), "SEPA 19-14 CORE (Xml)");
				exceptions.add (e);
			}
		}
		return exceptions;
	}

}