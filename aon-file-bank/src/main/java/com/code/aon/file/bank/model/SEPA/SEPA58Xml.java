package com.code.aon.file.bank.model.SEPA;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import com.code.aon.file.bank.model.CSB58.data.Individual;
import com.code.aon.file.bank.model.CSB58.data.Lot;


public class SEPA58Xml extends BasicSEPAXml {
	
	private LinkedList<Lot> lotList;
	
	private boolean desc;

	public SEPA58Xml(LinkedList<Lot> lotList, boolean desc, File file) {
		super(file);
		this.desc = desc;		
		this.lotList = lotList;
	}

	@Override
	protected String geDescription() {
		return "SEPA 58 (Xml)";
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
		addGroupHeader(lotList.get(0),mainElement);
		lotList.remove(0);
		lotList.stream().forEach(lot ->{
			addPaymentInformation(lot,mainElement);
		});
		
	}

	private void addInitiatingParty(Lot lot, Element groupHeader ) {
		Element initiatingParty = createElement(INITIATING_PARTY);
		groupHeader.appendChild(initiatingParty);		
		
		Element name = createElement(NAME);
		addValue(name, lot.getPresenter().getName(), 70);
		initiatingParty.appendChild(name);
	
		addOrganisationIdentification(initiatingParty, lot.getOrderer().getId(), null, getLocalInstrumentCode(), null );
	}
	
	private void addGroupHeader(Lot lot, Element customerDirectDebitInitiation ) {
		Element groupHeader = createGroupHeader(customerDirectDebitInitiation,
				lot.getId(), lot.getOrderer().getMakeDate(),
				lot.getOrderer().getNumIndividuals(), lot.getAmount());
		
		addInitiatingParty(lot, groupHeader);
	}
	
	private void addCreditorIdentification(Lot lot, Element paymentInformation ) {
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
		return desc ? LOCAL_INSTRUMENT_CODE_COR1_VALUE : LOCAL_INSTRUMENT_CODE_CORE_VALUE;
	}
	
	private void addPaymentInformation(Lot lot, Element customerDirectDebitInitiation ) {
		Element paymentInformation = createElement(PAYMENT_INFORMATION);
		customerDirectDebitInitiation.appendChild(paymentInformation);		
		
		Element paymentMethodIdentification = createElement(PAYMENT_INFORMATION_IDENTIFICATION);
		addValue(paymentMethodIdentification, lot.getPresenter().getId(), 35);
		paymentInformation.appendChild(paymentMethodIdentification);		

		Element paymentMethod = createElement(PAYMENT_METHOD);
		addValue(paymentMethod, PAYMENT_METHOD_DD_VALUE);
		paymentInformation.appendChild(paymentMethod);		

		addPaymentTypeInformation(paymentInformation, getLocalInstrumentCode(), SEQUENCE_TYPE_RCUR_VALUE);
		
		Element requestedCollectionDate = createElement(REQUEST_COLLECTION_DATE);
		addISODate(requestedCollectionDate, lot.getPresenter().getMakeDate());
		paymentInformation.appendChild(requestedCollectionDate);				
		
		addCreditor(paymentInformation, lot.getOrderer(), false);
		addCreditorAccount(paymentInformation, lot.getPresenter().getAccount());
		addCreditorAgent(paymentInformation, lot.getPresenter().getAccount());
		addChargeBearer(paymentInformation, FOLLOWING_SERVICE_LEVEL);
		addCreditorIdentification(lot, paymentInformation);
		
		Iterator<Individual> ii = lot.getOrderer().getIndividualsIterator();
		while ( ii.hasNext() ) {
			addDirectDebitTransactionInformation(paymentInformation, ii.next());	
		}
	}
	
}