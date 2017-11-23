package com.code.aon.file.bank.model.SEPA;

import java.io.PrintWriter;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import com.code.aon.file.bank.model.CSB19.data.Individual;
import com.code.aon.file.bank.model.CSB19.data.Lot;

public class SEPA19_14CoreXml extends BasicSEPAXml {
	
	private Lot lot;
	
	private boolean cor1;

	public SEPA19_14CoreXml(Lot lot, boolean cor1, PrintWriter writer) {
		super(writer);
		this.cor1 = cor1;		
		this.lot = lot;
	}

	@Override
	protected String geDescription() {
		return "SEPA 19-14 CORE (Xml)";
	}

	@Override
	protected Element createMainElement() throws ParserConfigurationException {
		Element rootElement = createDocument(XMLNS_SEPA_19_14_CORE_VALUE);
		
		Element customerDirectDebitInitiation = createElement(CUSTOMER_DIRECT_DEBIT_INITIATION);
		rootElement.appendChild(customerDirectDebitInitiation);
		
		return customerDirectDebitInitiation;
	}

	@Override
	protected void createContent(Element mainElement) {
		addGroupHeader(mainElement);
		addPaymentInformation(mainElement);
	}

	private void addInitiatingParty( Element groupHeader ) {
		Element initiatingParty = createElement(INITIATING_PARTY);
		groupHeader.appendChild(initiatingParty);		
		
		Element name = createElement(NAME);
		addValue(name, lot.getPresenter().getName(), 70);
		initiatingParty.appendChild(name);
	
		addOrganisationIdentification(initiatingParty, lot.getOrderer().getId(), null, getLocalInstrumentCode(), null );
	}
	
	private void addGroupHeader( Element customerDirectDebitInitiation ) {
		Element groupHeader = createGroupHeader(customerDirectDebitInitiation,
				lot.getId(), lot.getOrderer().getMakeDate(),
				lot.getOrderer().getNumIndividuals(), lot.getAmount());
		
		addInitiatingParty(groupHeader);
	}
	
	private void addCreditorIdentification( Element paymentInformation ) {
		Element creditorIdentification = createElement(CREDITOR_IDENTIFICATION);
		paymentInformation.appendChild(creditorIdentification);			
		
		addPrivateIdentification(creditorIdentification, lot.getOrderer().getId(), SEPA_VALUE, null);
	}

	private void addDirectDebitTransaction( Element directDebitTransactionInformation, Individual individual ) {
		Element directDebitTransaction = createElement(DIRECT_DEBIT_TRANSACTION);
		directDebitTransactionInformation.appendChild(directDebitTransaction);		
		
		Element mandateRelatedInformation = createElement(MANDATE_RELATED_INFORMATION);
		directDebitTransaction.appendChild(mandateRelatedInformation);
		
		Element mandateIdentification = createElement(MANDATE_IDENTIFICATION);
		addValue(mandateIdentification, individual.getReferenceCode(), 35);
		mandateRelatedInformation.appendChild(mandateIdentification);		
		
		Element dateOfSignature = createElement(DATE_OF_SIGNATURE);
		addValue(dateOfSignature, DATE_OF_SIGNATURE_MIGRATION_VALUE);
		mandateRelatedInformation.appendChild(dateOfSignature);		
	}
	
	private void addDirectDebitTransactionInformation( Element paymentInformation, Individual individual ) {
		Element directDebitTransactionInformation = createElement(DIRECT_DEBIT_TRANSACTION_INFORMATION);
		paymentInformation.appendChild(directDebitTransactionInformation);	
		
		addPaymentIdentification(directDebitTransactionInformation, individual.getInternalCode());
		addInstructedAmount(directDebitTransactionInformation, individual.getAmount());
		addDirectDebitTransaction(directDebitTransactionInformation, individual);
		addDebtorAgent(directDebitTransactionInformation, individual.getAccount());
		addDebtor(directDebitTransactionInformation, individual);
		addDebtorAccount(directDebitTransactionInformation, individual.getAccount(), null);
		addRemittanceInformation(directDebitTransactionInformation, individual.getDocumentNumber(), false);
	}
	
	private String getLocalInstrumentCode() {
		return cor1 ? LOCAL_INSTRUMENT_CODE_COR1_VALUE : LOCAL_INSTRUMENT_CODE_CORE_VALUE;
	}
	
	private void addPaymentInformation( Element customerDirectDebitInitiation ) {
		Element paymentInformation = createElement(PAYMENT_INFORMATION);
		customerDirectDebitInitiation.appendChild(paymentInformation);		
		
		Element paymentMethodIdentification = createElement(PAYMENT_INFORMATION_IDENTIFICATION);
		addValue(paymentMethodIdentification, lot.getPresenter().getId(), 35);
		paymentInformation.appendChild(paymentMethodIdentification);		

		Element paymentMethod = createElement(PAYMENT_METHOD);
		addValue(paymentMethod, PAYMENT_METHOD_DD_VALUE);
		paymentInformation.appendChild(paymentMethod);		

		Element numberOfTransactions = createElement(NUMBER_OF_TRANSACTIONS);
		addValue(numberOfTransactions, lot.getOrderer().getNumIndividuals());
		paymentInformation.appendChild(numberOfTransactions);

		Element controlSum = createElement(CONTROL_SUM);
		addDecimalNumber(controlSum, lot.getAmount());
		paymentInformation.appendChild(controlSum);

		addPaymentTypeInformation(paymentInformation, getLocalInstrumentCode(), SEQUENCE_TYPE_RCUR_VALUE);
		
		Element requestedCollectionDate = createElement(REQUEST_COLLECTION_DATE);
		addISODate(requestedCollectionDate, lot.getPresenter().getMakeDate());
		paymentInformation.appendChild(requestedCollectionDate);				
		
		addCreditor(paymentInformation, lot.getOrderer(), false);
		addCreditorAccount(paymentInformation, lot.getPresenter().getAccount());
		addCreditorAgent(paymentInformation, lot.getPresenter().getAccount());
		addChargeBearer(paymentInformation, FOLLOWING_SERVICE_LEVEL);
		addCreditorIdentification(paymentInformation);
		
		Iterator<Individual> ii = lot.getOrderer().getIndividualsIterator();
		while ( ii.hasNext() ) {
			addDirectDebitTransactionInformation(paymentInformation, ii.next());	
		}
	}
	
}