package com.code.aon.config;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Country;
import com.code.aon.config.enumeration.BankConfig;

public class BankAccount implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final int[] DIGITS = new int[] { 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
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
		setCheck(StringUtils.EMPTY);
		setBban1(StringUtils.EMPTY);
		setBban2(StringUtils.EMPTY);
		setBban3(StringUtils.EMPTY);
		setBban4(StringUtils.EMPTY);
		setBban5(StringUtils.EMPTY);
		setBban6(StringUtils.EMPTY);
		setBban7(StringUtils.EMPTY);
		setBban8(StringUtils.EMPTY);
	}

	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}

	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = StringUtils.isBlank(check) ? StringUtils.EMPTY : check;
	}

	public String getBban1() {
		return bban1;
	}
	public void setBban1(String bban1) {
		this.bban1 = StringUtils.isBlank(bban1) ? StringUtils.EMPTY : bban1;
	}

	public String getBban2() {
		return bban2;
	}
	public void setBban2(String bban2) {
		this.bban2 = StringUtils.isBlank(bban2) ? StringUtils.EMPTY : bban2;
	}

	public String getBban3() {
		return bban3;
	}
	public void setBban3(String bban3) {
		this.bban3 = StringUtils.isBlank(bban3) ? StringUtils.EMPTY : bban3;
	}

	public String getBban4() {
		return bban4;
	}
	public void setBban4(String bban4) {
		this.bban4 = StringUtils.isBlank(bban4) ? StringUtils.EMPTY : bban4;
	}

	public String getBban5() {
		return bban5;
	}
	public void setBban5(String bban5) {
		this.bban5 = StringUtils.isBlank(bban5) ? StringUtils.EMPTY : bban5;
	}

	public String getBban6() {
		return bban6;
	}
	public void setBban6(String bban6) {
		this.bban6 = StringUtils.isBlank(bban6) ? StringUtils.EMPTY : bban6;
	}

	public String getBban7() {
		return bban7;
	}
	public void setBban7(String bban7) {
		this.bban7 = StringUtils.isBlank(bban7) ? StringUtils.EMPTY : bban7;
	}

	public String getBban8() {
		return bban8;
	}
	public void setBban8(String bban8) {
		this.bban8 = StringUtils.isBlank(bban8) ? StringUtils.EMPTY : bban8;
	}


	public String getBankCode() {
		int length = getBankCodeLength();
		String maxBankCode = getBban1() + getBban2();
		return (maxBankCode.length() >= length) ? StringUtils.substring(maxBankCode, 0, length) : null;
	}

	public int getBankCodeLength() {
		BankConfig bankConfig = BankConfig.valueOf(getCountry().getValue());
		return bankConfig.getBankIdLength();
	}
	
	public int getIbanLength() {
		BankConfig bankConfig = BankConfig.valueOf(getCountry().getValue());
		return bankConfig.getIbanLength();
	}

	public String getBban() {
		return getBban1() + getBban2() + getBban3() + getBban4() + getBban5() + getBban6() + getBban7() + getBban8();
	}

	public String getIban() {
		if (getCountry() != null && getCheck() != null) {
			return getCountry().getValue() + getCheck() + getBban();
		}
		return null;
	}
	
	public String getCCC() {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(getBban1())) {
			sb.append(getBban1()).append(".");
		}
		if (StringUtils.isNotBlank(getBban2())) {
			sb.append(getBban2()).append(".");
		}
		if (StringUtils.isNotBlank(getBban3())) {
			sb.append(StringUtils.substring(getBban3(), 0, 2)).append(".").append(StringUtils.substring(getBban3(), 2, 4));
		}
		if (StringUtils.isNotBlank(getBban4())) {
			sb.append(getBban4());
		}
		if (StringUtils.isNotBlank(getBban5())) {
			sb.append(getBban5());
		}
		return sb.toString();
	}	

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (getCountry() != null) {
			sb.append(getCountry().getValue());
		}
		if (StringUtils.isNotBlank(getCheck())) {
			sb.append(getCheck());
		}
		if (StringUtils.isNotBlank(getBban1())) {
			sb.append("." + getBban1());
		}
		if (StringUtils.isNotBlank(getBban2())) {
			sb.append("." + getBban2());
		}
		if (StringUtils.isNotBlank(getBban3())) {
			sb.append("." + getBban3());
		}
		if (StringUtils.isNotBlank(getBban4())) {
			sb.append("." + getBban4());
		}
		if (StringUtils.isNotBlank(getBban5())) {
			sb.append("." + getBban5());
		}
		if (StringUtils.isNotBlank(getBban6())) {
			sb.append("." + getBban6());
		}
		if (StringUtils.isNotBlank(getBban7())) {
			sb.append("." + getBban7());
		}
		if (StringUtils.isNotBlank(getBban8())) {
			sb.append("." + getBban8());
		}
		return sb.toString();
	}
	
	public String getMaskedIban(){
		StringBuilder sb = new StringBuilder();
		String bban = getBban();
		if (StringUtils.isNotBlank(bban)) {
			sb.append(getCountry().getValue());
			sb.append(getCheck());
			sb.append("." + getBban1());
			for (int i=4; i<bban.length(); i=i+4) {
				if (StringUtils.isNotBlank(StringUtils.substring(bban, i+4, i+8))) {
					sb.append(".****");
				} else {
					sb.append("." + StringUtils.substring(bban, i, i+4));
				}
			}
		}
		return sb.toString();
	}

	public boolean isValidBankAccount() {
		return isValidBban() && isValidIban();
	}

	public boolean isValidBban() {
		if (!isValidBbanLength()) {
			return false;
		}
		if (getCountry() == Country.ES) {
			String control = calculateBbanControlDigit();
			return control != null && control.equals(StringUtils.substring(getBban3(), 0, 2));
		}
		return true;
	}

	public boolean isValidBbanLength() {
		if (getCountry() == null) {
			return false;
		}

		BankConfig bankConfig = BankConfig.valueOf(getCountry().getValue());
		return bankConfig.getBbanLength() == 0 || getBban().length() == bankConfig.getBbanLength();
	}

	public String calculateBbanControlDigit() {
		String bank = getBban1() + getBban2();
		String account = StringUtils.substring(getBban3(), 2) + getBban4() + getBban5();

		int sum = 0;
		int control1 = 0;
		for (int i=0; i<bank.length(); i++) {
			int digit = Integer.parseInt(String.valueOf(bank.charAt(bank.length() - 1 - i)));
			sum = digit * DIGITS[i];
			control1 = control1 + sum;
		}
		control1 = 11 - (control1 % 11);
		if (control1 == 10) {
			control1 = 1;
		}
		if (control1 == 11) {
			control1 = 0;
		}

		sum = 0;
		int control2 = 0;
		for (int i=0; i<account.length(); i++) {
			int digit = Integer.parseInt(String.valueOf(account.charAt(account.length() - 1 - i)));
			sum = digit * DIGITS[i];
			control2 = control2 + sum;
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
		if (!isValidIbanLength()) {
			return false;
		}
		String control = calculateIbanControlDigit();
		return control != null && control.equals(getCheck());
	}

	public boolean isValidIbanLength() {
		if (getCountry() == null) {
			return false;
		}

		BankConfig bankConfig = BankConfig.valueOf(getCountry().getValue());
		return bankConfig.getBbanLength() == 0  || getIban().length() == bankConfig.getIbanLength();
	}

	public String calculateIbanControlDigit() {
		String iban = getIbanAsNumber(getBban().toUpperCase() + getCountry().getValue() + "00");
		BigInteger control = new BigInteger(iban);
		control = control.mod(BigInteger.valueOf(97));
		return StringUtils.leftPad("" + (98 - control.intValue()), 2, "0");
	}

	private String getIbanAsNumber(String iban) {
		StringBuilder ibanAsNumber = new StringBuilder();
		for (int i=0; i<iban.length(); i++) {
			if (Character.isDigit(iban.charAt(i))) {
				ibanAsNumber.append(iban.charAt(i));
			} else {
				ibanAsNumber.append("" + ((int)iban.charAt(i) - (int)'A' + 10));
			}
		}
		return ibanAsNumber.toString();
	}

}
