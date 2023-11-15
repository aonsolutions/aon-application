package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.math.BigInteger;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;

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

	public BankAccount() {}
	
	public BankAccount(String value) {
		setCountry(Country.safeValueOf(AonStringUtils.substring(value, 0, 2)));
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
	
	public BankAccount(String value, Boolean ccc) {
		if(Boolean.TRUE.equals(ccc) ) {
			value =  Country.ES.getIso2() + calculateIbanControlDigit(value, Country.ES) + value;
		}
		setCountry(Country.safeValueOf(AonStringUtils.substring(value, 0, 2)));
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
	public void setCountry(Country country) {
		this.country = country;
	}

	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = AonStringUtils.isBlank(check) ? AonStringUtils.EMPTY : check;
	}

	public String getBban1() {
		return bban1;
	}
	public void setBban1(String bban1) {
		this.bban1 = AonStringUtils.isBlank(bban1) ? AonStringUtils.EMPTY : bban1;
	}

	public String getBban2() {
		return bban2;
	}
	public void setBban2(String bban2) {
		this.bban2 = AonStringUtils.isBlank(bban2) ? AonStringUtils.EMPTY : bban2;
	}

	public String getBban3() {
		return bban3;
	}
	public void setBban3(String bban3) {
		this.bban3 = AonStringUtils.isBlank(bban3) ? AonStringUtils.EMPTY : bban3;
	}

	public String getBban4() {
		return bban4;
	}
	public void setBban4(String bban4) {
		this.bban4 = AonStringUtils.isBlank(bban4) ? AonStringUtils.EMPTY : bban4;
	}

	public String getBban5() {
		return bban5;
	}
	public void setBban5(String bban5) {
		this.bban5 = AonStringUtils.isBlank(bban5) ? AonStringUtils.EMPTY : bban5;
	}

	public String getBban6() {
		return bban6;
	}
	public void setBban6(String bban6) {
		this.bban6 = AonStringUtils.isBlank(bban6) ? AonStringUtils.EMPTY : bban6;
	}

	public String getBban7() {
		return bban7;
	}
	public void setBban7(String bban7) {
		this.bban7 = AonStringUtils.isBlank(bban7) ? AonStringUtils.EMPTY : bban7;
	}

	public String getBban8() {
		return bban8;
	}
	public void setBban8(String bban8) {
		this.bban8 = AonStringUtils.isBlank(bban8) ? AonStringUtils.EMPTY : bban8;
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
			sb.append(getBban1()).append(".");
		}
		if (AonStringUtils.isNotBlank(getBban2())) {
			sb.append(getBban2()).append(".");
		}
		if (AonStringUtils.isNotBlank(getBban3())) {
			sb.append(AonStringUtils.substring(getBban3(), 0, 2)).append(".").append(AonStringUtils.substring(getBban3(), 2, 4));
		}
		if (AonStringUtils.isNotBlank(getBban4())) {
			sb.append(getBban4());
		}
		if (AonStringUtils.isNotBlank(getBban5())) {
			sb.append(getBban5());
		}
		return sb.toString();
	}	

	public String getCCC1() {
		return AonStringUtils.defaultString(getBban1());
	}
	public void setCCC1(String ccc1) {
		setBban1(ccc1);
	}
	public String getCCC2() {
		return AonStringUtils.defaultString(getBban2());
	}
	public void setCCC2(String ccc2) {
		setBban2(ccc2);
	}
	public String getCCC3() {
		return AonStringUtils.substring(getBban3(), 0, 2);
		
	}
	public void setCCC3(String ccc3) {
		String second = getBban3();
		setBban3(
			AonStringUtils.defaultString(AonStringUtils.substring(ccc3, 0, 2))
		  + AonStringUtils.defaultString(AonStringUtils.substring(second, 2, 4)));
		
	}
	public String getCCC4() {
		return AonStringUtils.defaultString(AonStringUtils.substring(getBban3(), 2, 4))
			+ AonStringUtils.defaultString(getBban4()) 
			+ AonStringUtils.defaultString(getBban5());
	}
	public void setCCC4(String ccc4) {
		String first = getBban3();
		setBban3(
			  AonStringUtils.defaultString(AonStringUtils.substring(first, 0, 4))
			+ AonStringUtils.defaultString(AonStringUtils.substring(ccc4, 0, 2))
		  );
		setBban4(AonStringUtils.defaultString(AonStringUtils.substring(ccc4, 2, 6)));
		setBban5(AonStringUtils.defaultString(AonStringUtils.substring(ccc4, 6, 10)));
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

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (getCountry() != null) {
			sb.append(getCountry().getIso2());
		}
		if (AonStringUtils.isNotBlank(getCheck())) {
			sb.append(getCheck());
		}
		if (AonStringUtils.isNotBlank(getBban1())) {
			sb.append("." + getBban1());
		}
		if (AonStringUtils.isNotBlank(getBban2())) {
			sb.append("." + getBban2());
		}
		if (AonStringUtils.isNotBlank(getBban3())) {
			sb.append("." + getBban3());
		}
		if (AonStringUtils.isNotBlank(getBban4())) {
			sb.append("." + getBban4());
		}
		if (AonStringUtils.isNotBlank(getBban5())) {
			sb.append("." + getBban5());
		}
		if (AonStringUtils.isNotBlank(getBban6())) {
			sb.append("." + getBban6());
		}
		if (AonStringUtils.isNotBlank(getBban7())) {
			sb.append("." + getBban7());
		}
		if (AonStringUtils.isNotBlank(getBban8())) {
			sb.append("." + getBban8());
		}
		return sb.toString();
	}
	
	public String getMaskedIban(){
		StringBuilder sb = new StringBuilder();
		String bban = getBban();
		if (AonStringUtils.isNotBlank(bban)) {
			sb.append(getCountry().getIso2());
			sb.append(getCheck());
			sb.append("." + getBban1());
			for (int i=4; i<bban.length(); i=i+4) {
				if (AonStringUtils.isNotBlank(AonStringUtils.substring(bban, i+4, i+8))) {
					sb.append(".****");
				} else {
					sb.append("." + AonStringUtils.substring(bban, i, i+4));
				}
			}
		}
		return sb.toString();
	}

	public String getSeparatedIban(){
		StringBuilder sb = new StringBuilder();
		String bban = getBban();
		if (AonStringUtils.isNotBlank(bban)) {
			sb.append(getCountry().getIso2());
			sb.append(getCheck());
			sb.append(" " + getBban1());
			for (int i=4; i<bban.length(); i=i+4) {
				sb.append(" " + AonStringUtils.substring(bban, i, i+4));
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
			return control != null && control.equals(AonStringUtils.substring(getBban3(), 0, 2));
		}
		return true;
	}

	public boolean isValidBbanLength() {
		if (getCountry() == null) {
			return false;
		}
		return getBban().length() == getCountry().getBbanLength();
	}

	public String calculateBbanControlDigit() {
		String bank = getBban1() + getBban2();
		String account = AonStringUtils.substring(getBban3(), 2) + getBban4() + getBban5();

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
		return getIban().length() == getCountry().getIbanLength();
	}

	public String calculateIbanControlDigit() {
		String iban = getIbanAsNumber(getBban().toUpperCase() + getCountry().getIso2() + "00");
		BigInteger control = new BigInteger(iban);
		control = control.mod(BigInteger.valueOf(97));
		return AonStringUtils.leftPad("" + (98 - control.intValue()), 2, "0");
	}
	
	public String calculateIbanControlDigit(String bban, Country country) {
		String iban = getIbanAsNumber(bban.toUpperCase() + country.getIso2() + "00");
		BigInteger control = new BigInteger(iban);
		control = control.mod(BigInteger.valueOf(97));
		
		return AonStringUtils.leftPad("" + (98 - control.intValue()), 2, "0");
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

	public boolean isEmpty() {
		if (country != null && check != null && bban1 != null && bban2 != null && bban3 != null && bban4 != null &&
			bban5 != null && bban6 != null && bban7 != null && bban8 != null) {
			return (check.isEmpty() && bban1.isEmpty() && bban2.isEmpty() && bban3.isEmpty() && bban4.isEmpty() && bban5.isEmpty()
			&& bban6.isEmpty() && bban7.isEmpty() && bban8.isEmpty());
		} else {
			return false;
		}
				
	}

}
