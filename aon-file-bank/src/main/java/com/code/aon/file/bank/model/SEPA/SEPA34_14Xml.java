package com.code.aon.file.bank.model.SEPA;

import java.io.PrintWriter;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import com.code.aon.file.bank.model.CSB34.data.Detail;
import com.code.aon.file.bank.model.CSB34.data.Master;

public class SEPA34_14Xml extends BasicSEPAXml {
	
	private Master master;
	
	public SEPA34_14Xml(Master master, PrintWriter writer) {
		super(writer);
		this.master = master;
	}

	@Override
	protected String geDescription() {
		return "SEPA 34-14 CORE (Xml)";
	}

	@Override
	protected Element createMainElement() throws ParserConfigurationException {
		Element rootElement = createDocument(XMLNS_SEPA_34_14_VALUE);
		
		Element customerCreditTransferInitiation = createElement(CUSTOMER_CREDIT_TRANSFER_INITIATION);
		rootElement.appendChild(customerCreditTransferInitiation);
		
		return customerCreditTransferInitiation;
	}

	@Override
	protected void createContent(Element mainElement) {
		addGroupHeader(mainElement);
		addPaymentInformation(mainElement);
	}
	
	private void addGroupHeader( Element customerDirectDebitInitiation ) {
		Element groupHeader = createGroupHeader(customerDirectDebitInitiation,
				master.getId(), master.getOrderDate(),
				master.getReceivers().size(), master.getAmount());
		
		addInitiatingParty(groupHeader);
	}
	
	private void addInitiatingParty( Element groupHeader ) {
		Element initiatingParty = createElement(INITIATING_PARTY);
		groupHeader.appendChild(initiatingParty);		
		
		Element name = createElement(NAME);
		addValue(name, master.getOrderer().getName(), 70);
		initiatingParty.appendChild(name);
	
		addOrganisationIdentification(initiatingParty, master.getCompanyId(), null, null, null);
	}	

	private void addPaymentInformation( Element customerDirectDebitInitiation ) {
		Element paymentInformation = createElement(PAYMENT_INFORMATION);
		customerDirectDebitInitiation.appendChild(paymentInformation);		
		
		Element paymentMethodIdentification = createElement(PAYMENT_INFORMATION_IDENTIFICATION);
		addValue(paymentMethodIdentification, master.getOrderer().getId(), 35);
		paymentInformation.appendChild(paymentMethodIdentification);		

		Element paymentMethod = createElement(PAYMENT_METHOD);
		addValue(paymentMethod, PAYMENT_METHOD_TRF_VALUE);
		paymentInformation.appendChild(paymentMethod);		

		Element requestedExecutionDate = createElement(REQUEST_EXECUTION_DATE);
		addISODate(requestedExecutionDate, master.getSendDate());
		paymentInformation.appendChild(requestedExecutionDate);				

		addDebtor(paymentInformation, master.getOrderer());
		addDebtorAccount(paymentInformation, master.getAccount(), CURRENCY_EUR_VALUE);
		addDebtorAgent(paymentInformation, master.getAccount());
		
		Iterator<Detail> ir = master.getReceiversIterator();
		while ( ir.hasNext() ) {
			addCreditTransferTransactionInformation(paymentInformation, ir.next());	
		}
	}

	private void addAmount( Element parent, double amount ) {
		Element amountElement = createElement(AMOUNT);
		parent.appendChild(amountElement);
		
		addInstructedAmount(amountElement, amount);
	}
	
	private void addCreditTransferTransactionInformation( Element paymentInformation, Detail detail ) {
		Element creditTransferTransactionInformation = createElement(CREDIT_TRANSFER_TRANSACTION_INFORMATION);
		paymentInformation.appendChild(creditTransferTransactionInformation);	
		
		addPaymentIdentification(creditTransferTransactionInformation, detail.getReceiver().getReferenceCode());
		addPaymentTypeInformation(creditTransferTransactionInformation, detail.getCategoryPurposeCode());
		addAmount(creditTransferTransactionInformation, detail.getAmount());
		addChargeBearer(creditTransferTransactionInformation, FOLLOWING_SERVICE_LEVEL);
		addCreditorAgent(creditTransferTransactionInformation, detail.getAccount());
		addCreditor(creditTransferTransactionInformation, detail.getReceiver(), true);
		addCreditorAccount(creditTransferTransactionInformation, detail.getAccount());
		addRemittanceInformation(creditTransferTransactionInformation, detail.getDocumentNumber(), true);
	}	
	
}