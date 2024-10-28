package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.math.BigInteger;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.type.Country;

public class BankAccount implements Serializable {
	
	private static final long serialVersionUID = 8997466979099180561L;
	
	private static final int[] DIGITS = new int[] { 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };

	private static final int DEFAULT_BANK_ID_LENGTH = 4;
	private static final int DEFAULT_IBAN_LENGTH = 34;
	
	private Country country;
	private String check;
	private String bban1;
	private String bban2;
	private String bban3;
	private String bban4;
	private String bban5;
	private String bban6;
	private String bban7;
	private String bban8;

	public BankAccount() {
		setCountry(Country.ES);
		setCheck(AonStringUtils.EMPTY);
		setBban1(AonStringUtils.EMPTY);
		setBban2(AonStringUtils.EMPTY);
		setBban3(AonStringUtils.EMPTY);
		setBban4(AonStringUtils.EMPTY);
		setBban5(AonStringUtils.EMPTY);
		setBban6(AonStringUtils.EMPTY);
		setBban7(AonStringUtils.EMPTY);
		setBban8(AonStringUtils.EMPTY);
	}
	
	public BankAccount(String value) {
		this( value, false ); 
	}
	
	public BankAccount(String value, boolean ccc) {
		if (ccc) {
			value =  Country.ES.getIso2() + calculateIbanControlDigit(value, Country.ES) + value;
		}
		setCountry(Country.value(AonStringUtils.substring(value, 0, 2)).orElse(null));
		setCheck(AonStringUtils.substring(value, 2, 4));
		setBban1(AonStringUtils.substring(value, 4, 8));
		setBban2(AonStringUtils.substring(value, 8, 12));
		setBban3(AonStringUtils.substring(value, 12, 16));
		setBban4(AonStringUtils.substring(value, 16, 20));
		setBban5(AonStringUtils.substring(value, 20, 24));
		setBban6(AonStringUtils.substring(value, 24, 28));
		setBban7(AonStringUtils.substring(value, 28, 32));
		setBban8(AonStringUtils.substring(value, 32, 34));
	}

	public Country getCountry() {
		return country;
	}
	public final BankAccount setCountry(Country country) {
		this.country = country;
		return this;
	}

	public String getCheck() {
		return check;
	}
	public final BankAccount setCheck(String check) {
		this.check = AonStringUtils.defaultIfBlank(check);
		return this;
	}

	public String getBban1() {
		return bban1;
	}
	public final BankAccount setBban1(String bban1) {
		this.bban1 = AonStringUtils.defaultIfBlank(bban1);
		return this;
	}

	public String getBban2() {
		return bban2;
	}
	public final BankAccount setBban2(String bban2) {
		this.bban2 = AonStringUtils.defaultIfBlank(bban2);
		return this;
	}

	public String getBban3() {
		return bban3;
	}
	public final BankAccount setBban3(String bban3) {
		this.bban3 = AonStringUtils.defaultIfBlank(bban3);
		return this;
	}

	public String getBban4() {
		return bban4;
	}
	public final BankAccount setBban4(String bban4) {
		this.bban4 = AonStringUtils.defaultIfBlank(bban4);
		return this;
	}

	public String getBban5() {
		return bban5;
	}
	public final BankAccount setBban5(String bban5) {
		this.bban5 = AonStringUtils.defaultIfBlank(bban5);
		return this;
	}

	public String getBban6() {
		return bban6;
	}
	public final BankAccount setBban6(String bban6) {
		this.bban6 = AonStringUtils.defaultIfBlank(bban6);
		return this;
	}

	public String getBban7() {
		return bban7;
	}
	public final BankAccount setBban7(String bban7) {
		this.bban7 = AonStringUtils.defaultIfBlank(bban7);
		return this;
	}

	public String getBban8() {
		return bban8;
	}
	public final BankAccount setBban8(String bban8) {
		this.bban8 = AonStringUtils.defaultIfBlank(bban8);
		return this;
	}

	public String getBankCode() {
		int length = getBankCodeLength();
		String maxBankCode = getBban1() + getBban2();
		return (maxBankCode.length() >= length) ? AonStringUtils.substring(maxBankCode, 0, length) : null;
	}

	public int getBankCodeLength() {
		return getCountry() == null ? DEFAULT_BANK_ID_LENGTH : getCountry().getBankIdLength();
	}
	
	public int getIbanLength() {
		return getCountry() == null ? DEFAULT_IBAN_LENGTH : getCountry().getIbanLength();
	}

	public String getBban() {
		return getBban1() + getBban2() + getBban3() + getBban4() + getBban5() + getBban6() + getBban7() + getBban8();
	}

	public String getIban() {
		if (getCountry() != null && getCheck() != null) {
			return getCountry().getIso2() + getCheck() + getBban();
		}
		return null;
	}
	
	public String getCCC() {
		StringBuilder sb = new StringBuilder();
		if (AonStringUtils.isNotBlank(getBban1())) {
			sb.append(getBban1())
				.append('.');
		}
		if (AonStringUtils.isNotBlank(getBban2())) {
			sb.append(getBban2())
				.append('.');
		}
		if (AonStringUtils.isNotBlank(getBban3())) {
			sb.append(AonStringUtils.substring(getBban3(), 0, 2))
				.append('.')
				.append(AonStringUtils.substring(getBban3(), 2, 4));
		}
		if (AonStringUtils.isNotBlank(getBban4())) {
			sb.append(getBban4());
		}
		if (AonStringUtils.isNotBlank(getBban5())) {
			sb.append(getBban5());
		}
		return sb.toString();
	}	

	public String getRawCCC() {
		return new StringBuilder()
			.append(AonStringUtils.defaultIfBlank(getBban1()))
			.append(AonStringUtils.defaultIfBlank(getBban2()))
			.append(AonStringUtils.defaultIfBlank(AonStringUtils.substring(getBban3(), 0, 2)))
			.append(AonStringUtils.defaultIfBlank(AonStringUtils.substring(getBban3(), 2, 4)))
			.append(AonStringUtils.defaultIfBlank(getBban4()))
			.append(AonStringUtils.defaultIfBlank(getBban5()))
			.toString();
	}	

	@Override
	public String toString() {
		return  new StringBuilder()
			.append(Country.value(getCountry()))
			.append(getCheck())
			.append('.')
			.append(getBban1())
			.append('.')
			.append(getBban2())
			.append('.')
			.append(getBban3())
			.append('.')
			.append(getBban4())
			.append('.')
			.append(getBban5())
			.append('.')
			.append(getBban6())
			.append('.')
			.append(getBban7())
			.append('.')
			.append(getBban8())
			.toString();
	}
	
	public String getMaskedIban(){
		if (AonStringUtils.isNotBlank(getBban())) {
			StringBuilder sb = new StringBuilder();
			sb.append(Country.value(getCountry()));
			sb.append(getCheck());
			sb.append('.' + getBban1());
			for (int i=4; i<getBban().length(); i=i+4) {
				if (AonStringUtils.isNotBlank(AonStringUtils.substring(getBban(), i+4, i+8))) {
					sb.append(".****");
				} else {
					sb.append('.' + AonStringUtils.substring(getBban(), i, i+4));
				}
			}
			return sb.toString();
		}
		return AonStringUtils.EMPTY;
	}

	public String getSeparatedIban(){
		if (AonStringUtils.isNotBlank(getBban())) {
			StringBuilder sb = new StringBuilder();
			sb.append(Country.value(getCountry()));
			sb.append(getCheck());
			sb.append(" " + getBban1());
			for (int i=4; i<getBban().length(); i=i+4) {
				sb.append(" " + AonStringUtils.substring(getBban(), i, i+4));
			}
			return sb.toString();
		}
		return AonStringUtils.EMPTY;
	}
	
	public String getPureCCC() {
		StringBuilder sb = new StringBuilder();
		if (AonStringUtils.isNotBlank(getBban1())) {
			sb.append(getBban1());
		}
		if (AonStringUtils.isNotBlank(getBban2())) {
			sb.append(getBban2());
		}
		if (AonStringUtils.isNotBlank(getBban3())) {
			sb.append(AonStringUtils.substring(getBban3(), 0, 2))
			  .append(AonStringUtils.substring(getBban3(), 2, 4));
		}
		if (AonStringUtils.isNotBlank(getBban4())) {
			sb.append(getBban4());
		}
		if (AonStringUtils.isNotBlank(getBban5())) {
			sb.append(getBban5());
		}
		return sb.toString();
	}	

	public boolean isValidBankAccount() {
		return isValidBban() && isValidIban();
	}

	public boolean isValidBban() {
		if (getCountry() == null) return false;
		if (getCountry() != Country.ES) return true;
		if (!isValidBbanLength()) return false;
		String control = calculateBbanControlDigit();
		return control != null && control.equals(AonStringUtils.substring(getBban3(), 0, 2));
	}

	public boolean isValidBbanLength() {
		if (getCountry() == null) return false;
		if (getCountry() != Country.ES) return true;
		return getBban().length() == getCountry().getBbanLength();
	}

	public String calculateBbanControlDigit() {
		String bank = getBban1() + getBban2();
		String account = AonStringUtils.substring(getBban3(), 2) + getBban4() + getBban5();
		int control1 = 0;
		for (int i=0; i<bank.length(); i++) {
			int digit = Integer.parseInt(String.valueOf(bank.charAt(bank.length() - 1 - i)));
			control1 = control1 + digit * DIGITS[i];
		}
		control1 = 11 - (control1 % 11);
		if (control1 == 10) {
			control1 = 1;
		}
		if (control1 == 11) {
			control1 = 0;
		}

		int control2 = 0;
		for (int i=0; i<account.length(); i++) {
			int digit = Integer.parseInt(String.valueOf(account.charAt(account.length() - 1 - i)));
			control2 = control2 + digit * DIGITS[i];
		}
		control2 = 11 - (control2 % 11);
		if (control2 == 10) {
			control2 = 1;
		}
		if (control2 == 11) {
			control2 = 0;
		}
		return String.valueOf(control1) + String.valueOf(control2);
	}

	public boolean isValidIban() {
		if (getCountry() == null) return false;
		if (getCountry() != Country.ES) return true;
		if (!isValidIbanLength())
			return false;
		else {
			String control = calculateIbanControlDigit();
			return control != null && control.equals(getCheck());
		}
	}
	
	public boolean isValidIbanLength() {
		if (getCountry() == null) return false;
		if (getCountry() != Country.ES) return true;
		else return getIban().length() == getCountry().getIbanLength();
	}

	public String calculateIbanControlDigit() {
		String iban = getIbanAsNumber(getBban().toUpperCase() + Country.value(getCountry()) + "00");
		BigInteger control = new BigInteger(iban);
		control = control.mod(BigInteger.valueOf(97));
		return AonStringUtils.leftPad("" + (98 - control.intValue()), 2, "0");
	}
	
	public final String calculateIbanControlDigit(String bban, Country country) {
		String iban = getIbanAsNumber(bban.toUpperCase() + country.getIso2() + "00");
		BigInteger control = new BigInteger(iban);
		control = control.mod(BigInteger.valueOf(97));
		
		return AonStringUtils.leftPad("" + (98 - control.intValue()), 2, "0");
	}

	private final String getIbanAsNumber(String iban) {
		StringBuilder ibanAsNumber = new StringBuilder();
		for (int i=0; i<iban.length(); i++) {
			if (Character.isDigit(iban.charAt(i))) {
				ibanAsNumber.append(iban.charAt(i));
			} else {
				ibanAsNumber.append("" + (iban.charAt(i) - 'A' + 10));
			}
		}
		return ibanAsNumber.toString();
	}

}
