package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.VATTaxRegime;

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
	private EnumMap<VATTaxRegime,Boolean> vatRegimes = new EnumMap<>(VATTaxRegime.class);

	public InvoiceType getType() {
		return type;
	}
	public InvoiceRegistry setType(InvoiceType type) {
		this.type = type;
		return this;
	}

	public Optional<Account> getAccount() {
		return Optional.ofNullable(account);
	}
	public InvoiceRegistry setAccount(Account account) {
		this.account = account;
		return this;
	}

	public Optional<RegistryAddress> getMainAddress() {
		return Optional.ofNullable(mainAddress);
	}
	public InvoiceRegistry setMainAddress(RegistryAddress mainAddress) {
		this.mainAddress = mainAddress;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public InvoiceRegistry setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}

	public boolean isSurcharge() {
		return surcharge;
	}
	public InvoiceRegistry setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public boolean isWithholding() {
		return withholding;
	}
	public InvoiceRegistry setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}

	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}
	public InvoiceRegistry setWithholdingFarmer(boolean withholdingFarmer) {
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}

	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public InvoiceRegistry setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}
	public boolean isVatRegimeEnabled(VATTaxRegime vatRegime) {
		return vatRegimes.get(vatRegime) != null && Boolean.TRUE.equals( vatRegimes.get(vatRegime)); 
	}
	public InvoiceRegistry setVatRegime(VATTaxRegime vatRegime, boolean bool) {
		vatRegimes.put(vatRegime, bool );
		return this;
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
