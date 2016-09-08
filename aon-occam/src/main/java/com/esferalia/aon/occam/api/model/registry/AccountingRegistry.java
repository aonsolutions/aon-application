package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingRegistry implements Serializable {
	private static final long serialVersionUID = -5523495170211215075L;

	private Integer id;
	private Integer accountId;
	private String accountCode;
	private String accountDescription;
	private String alias;
	private String document;
	private Country documentCountry;
	private DocumentType documentType;
	private String name;
	private AccountingRegistryType type;
	private int scope;
	private int domain;
	private boolean surcharge;
	private boolean withholding;
	private boolean withholdingFarmer;
	private boolean vatAccrualPayment;
	private InvoiceTransactionType transaction;

	public Integer getId() {
		return id;
	}

	public AccountingRegistry setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public AccountingRegistry setAccountId(Integer accountId) {
		this.accountId = accountId;
		return this;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public AccountingRegistry setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public AccountingRegistry setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public AccountingRegistry setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public AccountingRegistry setDocument(String document) {
		this.document = document;
		return this;
	}
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public AccountingRegistry setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public AccountingRegistry setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public AccountingRegistry setName(String name) {
		this.name = name;
		return this;
	}

	public AccountingRegistryType getType() {
		return type;
	}
	
	public AccountingRegistry setType(AccountingRegistryType type) {
		this.type = type;
		return this;
	}
	
	public int getScope() {
		return scope;
	}

	public AccountingRegistry setScope(int scope) {
		this.scope = scope;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public AccountingRegistry setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public boolean isSurcharge() {
		return surcharge;
	}

	public AccountingRegistry setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public boolean isWithholding() {
		return withholding;
	}

	public AccountingRegistry setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}

	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}

	public AccountingRegistry setWithholdingFarmer(boolean withholdingFarmer) {
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}

	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}

	public AccountingRegistry setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}

	public AccountingRegistry setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}

	public static String getFullDescription(AccountingRegistry accRegistry) {
		return AonStringUtils.OPEN_BRACKET
				+ AonStringUtils.defaultIfEmpty(accRegistry.getAccountCode(),"NO CTA CTB")
				+ AonStringUtils.CLOSE_BRACKET
				+ AonStringUtils.SPACE
				+ AonStringUtils.defaultIfEmpty(accRegistry.getDocumentType().getDescription()
					, AonStringUtils.repeat(AonStringUtils.QUESTION, 3))
				+ AonStringUtils.HYPHEN
				+ AonStringUtils.defaultIfEmpty(accRegistry.getDocumentCountry().getIso2()
					, AonStringUtils.repeat(AonStringUtils.QUESTION, 2)) 
				+ AonStringUtils.SLASH
				+ AonStringUtils.defaultIfEmpty(accRegistry.getDocument()
					, AonStringUtils.repeat(AonStringUtils.QUESTION, 9))
				+ AonStringUtils.SPACE
				+ AonStringUtils.HYPHEN
				+ AonStringUtils.SPACE
				+ accRegistry.getName()
				+ AonStringUtils.SPACE
				+ (AonStringUtils.isNotBlank(accRegistry.getAlias())
					?(AonStringUtils.SPACE + AonStringUtils.OPEN_PARENTHESIS + accRegistry.getAlias() + AonStringUtils.CLOSE_PARENTHESIS)
					:AonStringUtils.EMPTY)
				;
	}

}
