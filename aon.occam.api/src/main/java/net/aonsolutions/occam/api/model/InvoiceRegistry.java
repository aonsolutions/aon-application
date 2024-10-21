package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceType;

public class InvoiceRegistry extends Registry implements Serializable {
	
	private static final long serialVersionUID = -5523495170211215075L;

	private InvoiceType type;
	private Account account;
	private RegistryAddress mainAddress;
	private InvoiceTransactionType transaction;
	private boolean surcharge;
	private boolean withholding;
	private boolean withholdingFarmer;
	private boolean vatAccrualPayment;

	

	public InvoiceType getType() {
		return type;
	}
	public void setType(InvoiceType type) {
		this.type = type;
	}

	public Optional<Account> getAccount() {
		return Optional.ofNullable(account);
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	public Optional<RegistryAddress> getMainAddress() {
		return Optional.ofNullable(mainAddress);
	}
	public void setMainAddress(RegistryAddress mainAddress) {
		this.mainAddress = mainAddress;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}

	public boolean isSurcharge() {
		return surcharge;
	}
	public void setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
	}

	public boolean isWithholding() {
		return withholding;
	}
	public void setWithholding(boolean withholding) {
		this.withholding = withholding;
	}

	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}
	public void setWithholdingFarmer(boolean withholdingFarmer) {
		this.withholdingFarmer = withholdingFarmer;
	}

	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public void setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
	}

	public static String getFullDescription(InvoiceRegistry  reg) {
		return AonStringUtils.OPEN_BRACKET
			+ reg.getAccount().map(Account::getCode).orElse("NO CTA CTB")
			+ AonStringUtils.CLOSE_BRACKET
			+ AonStringUtils.SPACE
			+ DocumentType.shortName(reg.getDocumentType()).orElse(AonStringUtils.repeat(AonStringUtils.QUESTION, 3))
			+ AonStringUtils.HYPHEN
			+ AonStringUtils.defaultIfEmpty( Country.value(reg.getDocumentCountry()) , AonStringUtils.repeat(AonStringUtils.QUESTION, 2))
			+ AonStringUtils.SLASH
			+ AonStringUtils.defaultIfEmpty(reg.getDocument(), AonStringUtils.repeat(AonStringUtils.QUESTION, 9))
			+ AonStringUtils.SPACE
			+ AonStringUtils.HYPHEN
			+ AonStringUtils.SPACE
			+ reg.getName()
			+ AonStringUtils.SPACE
			+ (AonStringUtils.isNotBlank(reg.getAlias())
				?(AonStringUtils.SPACE + AonStringUtils.OPEN_PARENTHESIS + reg.getAlias() + AonStringUtils.CLOSE_PARENTHESIS)
				:AonStringUtils.EMPTY)
			;
	}

}
