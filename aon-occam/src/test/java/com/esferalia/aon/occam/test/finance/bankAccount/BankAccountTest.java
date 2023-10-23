package com.esferalia.aon.occam.test.finance.bankAccount;

import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

/**
 * Tests the methods of the class BankAccount
 */
public class BankAccountTest extends AbstractOccamTest {
	
	/**
	 * Test the constructor BankAccount(String value)
	 */
	@Test
	public void firstConstructorTest() {
		String value = AonFaker.getCountry(ctx).toString();
		value.concat(RandomStringUtils.random(RBANK.BANK_ACCOUNT.getDataType().length()-2, true, false));
		BankAccount bankAccount = new BankAccount(value);
		
		assertEquals(AonStringUtils.substring(value, 0, 2), bankAccount.getCountry().toString());
		assertEquals(AonStringUtils.substring(value, 2, 4), bankAccount.getCheck());
		assertEquals(AonStringUtils.substring(value, 4, 8), bankAccount.getBban1());
		assertEquals(AonStringUtils.substring(value, 8, 12), bankAccount.getBban2());
		assertEquals(AonStringUtils.substring(value, 12, 16), bankAccount.getBban3());
		assertEquals(AonStringUtils.substring(value, 16, 20), bankAccount.getBban4());
		assertEquals(AonStringUtils.substring(value, 20, 24), bankAccount.getBban5());
		assertEquals(AonStringUtils.substring(value, 24, 28), bankAccount.getBban6());
		assertEquals(AonStringUtils.substring(value, 28, 32), bankAccount.getBban7());
		assertEquals(AonStringUtils.substring(value, 32, 34), bankAccount.getBban8());
	}
	
	/**
	 * Test the constructor BankAccount(String value, Boolean ccc) if ccc is true
	 */
	@Test
	public void secondConstructorTestTrue() {
		String value = AonFaker.getCountry(ctx).toString();
		value.concat(RandomStringUtils.random(RBANK.BANK_ACCOUNT.getDataType().length()-2, true, false));
		BankAccount bankAccount = new BankAccount(value,true);

		value = Country.ES.getIso2() + bankAccount.calculateIbanControlDigit(value, Country.ES) + value;
		
		assertEquals(AonStringUtils.substring(value, 0, 2), bankAccount.getCountry().toString());
		assertEquals(AonStringUtils.substring(value, 2, 4), bankAccount.getCheck());
		assertEquals(AonStringUtils.substring(value, 4, 8), bankAccount.getBban1());
		assertEquals(AonStringUtils.substring(value, 8, 12), bankAccount.getBban2());
		assertEquals(AonStringUtils.substring(value, 12, 16), bankAccount.getBban3());
		assertEquals(AonStringUtils.substring(value, 16, 20), bankAccount.getBban4());
		assertEquals(AonStringUtils.substring(value, 20, 24), bankAccount.getBban5());
		assertEquals(AonStringUtils.substring(value, 24, 28), bankAccount.getBban6());
		assertEquals(AonStringUtils.substring(value, 28, 32), bankAccount.getBban7());
		assertEquals(AonStringUtils.substring(value, 32, 34), bankAccount.getBban8());
	}
	
	/**
	 * Test the constructor BankAccount(String value, Boolean ccc) if ccc is false
	 */
	@Test
	public void secondConstructorTestFalse() {
		String value = AonFaker.getCountry(ctx).toString();
		value.concat(RandomStringUtils.random(RBANK.BANK_ACCOUNT.getDataType().length()-2, true, false));
		BankAccount bankAccount = new BankAccount(value,false);
		
		assertEquals(AonStringUtils.substring(value, 0, 2), bankAccount.getCountry().toString());
		assertEquals(AonStringUtils.substring(value, 2, 4), bankAccount.getCheck());
		assertEquals(AonStringUtils.substring(value, 4, 8), bankAccount.getBban1());
		assertEquals(AonStringUtils.substring(value, 8, 12), bankAccount.getBban2());
		assertEquals(AonStringUtils.substring(value, 12, 16), bankAccount.getBban3());
		assertEquals(AonStringUtils.substring(value, 16, 20), bankAccount.getBban4());
		assertEquals(AonStringUtils.substring(value, 20, 24), bankAccount.getBban5());
		assertEquals(AonStringUtils.substring(value, 24, 28), bankAccount.getBban6());
		assertEquals(AonStringUtils.substring(value, 28, 32), bankAccount.getBban7());
		assertEquals(AonStringUtils.substring(value, 32, 34), bankAccount.getBban8());
	}
	
	/**
	 * Test the constructor BankAccount()
	 */
	@Test
	public void thirdConstructorTest() {
		BankAccount bankAccount = new BankAccount();
		
		assertEquals(Country.ES, bankAccount.getCountry());
		assertTrue(bankAccount.getCheck().isEmpty());
		assertTrue(bankAccount.getBban1().isEmpty());
		assertTrue(bankAccount.getBban2().isEmpty());
		assertTrue(bankAccount.getBban3().isEmpty());
		assertTrue(bankAccount.getBban4().isEmpty());
		assertTrue(bankAccount.getBban5().isEmpty());
		assertTrue(bankAccount.getBban6().isEmpty());
		assertTrue(bankAccount.getBban7().isEmpty());
		assertTrue(bankAccount.getBban8().isEmpty());
	}
	
	/**
	 * Test the getter and setter of country
	 */
	@Test
	public void CountryTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		Country country = AonFaker.getCountry(ctx);
		bankAccount.setCountry(country);
		assertEquals(country, bankAccount.getCountry());
	}
	
	/**
	 * Test the getter and setter of check
	 */
	@Test
	public void CheckTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String check = Faker.instance().letterify("??");
		bankAccount.setCheck(check);
		assertEquals(check, bankAccount.getCheck());
	}
	
	/**
	 * Test the getter and setter of bban1
	 */
	@Test
	public void Bban1Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String bban1 = Faker.instance().letterify("??");
		bankAccount.setBban1(bban1);
		assertEquals(bban1, bankAccount.getBban1());
	}
	
	/**
	 * Test the getter and setter of bban2
	 */
	@Test
	public void Bban2Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String bban2 = Faker.instance().letterify("??");
		bankAccount.setBban2(bban2);
		assertEquals(bban2, bankAccount.getBban2());
	}
	
	/**
	 * Test the getter and setter of bban3
	 */
	@Test
	public void Bban3Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String bban3 = Faker.instance().letterify("??");
		bankAccount.setBban3(bban3);
		assertEquals(bban3, bankAccount.getBban3());
	}
	
	/**
	 * Test the getter and setter of bban4
	 */
	@Test
	public void Bban4Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String bban4 = Faker.instance().letterify("??");
		bankAccount.setBban4(bban4);
		assertEquals(bban4, bankAccount.getBban4());
	}
	
	/**
	 * Test the getter and setter of bban5
	 */
	@Test
	public void Bban5Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String bban5 = Faker.instance().letterify("??");
		bankAccount.setBban5(bban5);
		assertEquals(bban5, bankAccount.getBban5());
	}
	
	/**
	 * Test the getter and setter of bban6
	 */
	@Test
	public void Bban6Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String bban6 = Faker.instance().letterify("??");
		bankAccount.setBban6(bban6);
		assertEquals(bban6, bankAccount.getBban6());
	}
	
	/**
	 * Test the getter and setter of bban7
	 */
	@Test
	public void Bban7Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String bban7 = Faker.instance().letterify("??");
		bankAccount.setBban7(bban7);
		assertEquals(bban7, bankAccount.getBban7());
	}
	
	/**
	 * Test the getter and setter of bban8
	 */
	@Test
	public void Bban8Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String bban8 = Faker.instance().letterify("??");
		bankAccount.setBban8(bban8);
		assertEquals(bban8, bankAccount.getBban8());
	}
	
	/**
	 * Test the method getBankCode
	 */
	@Test
	public void getBankCodeTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String maxBankCode = bankAccount.getBban1() + bankAccount.getBban2(); 
		String bankCode = (maxBankCode.length()  >= bankAccount.getBankCodeLength()) ? AonStringUtils.substring(maxBankCode, 0, bankAccount.getBankCodeLength()) : null;
		assertEquals(bankCode, bankAccount.getBankCode());
	}
	
	/**
	 * Test the method getBankCodeLength
	 */
	@Test
	public void getBankCodeLengthTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		if (bankAccount.getCountry() != null)
			assertEquals(bankAccount.getCountry().getBankIdLength(), bankAccount.getBankCodeLength());
	}
	
	/**
	 * Test the method getIban
	 */
	@Test
	public void getIbanTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		if (bankAccount.getCountry() != null && bankAccount.getCheck() != null)
			assertEquals(bankAccount.getCountry().getIso2() + bankAccount.getCheck() + bankAccount.getBban(), bankAccount.getIban());
	}
	
	/**
	 * Test the method getIban if the country is null
	 */
	@Test
	public void getIbanTestNull() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		bankAccount.setCountry(null);
		if (bankAccount.getCountry() == null || bankAccount.getCheck() == null)
			assertNull(bankAccount.getIban());
	}
	
	/**
	 * Test the method getIbanLength
	 */
	@Test
	public void getIbanLengthTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		if (bankAccount.getCountry() != null)
			assertEquals(bankAccount.getCountry().getIbanLength(), bankAccount.getIbanLength());
	}
	
	/**
	 * Test the method getBban
	 */
	@Test
	public void getBbanTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		assertEquals(bankAccount.getBban1() + bankAccount.getBban2() + bankAccount.getBban3() + bankAccount.getBban4() + bankAccount.getBban5() + bankAccount.getBban6()
		+ bankAccount.getBban7() + bankAccount.getBban8(), bankAccount.getBban());
	}
	
	/**
	 * Test the method get CCC
	 */
	@Test
	public void getCCCTEst() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		StringBuilder stringBuilder = new StringBuilder();
		
		if (AonStringUtils.isNotBlank(bankAccount.getBban1())) {
			stringBuilder.append(bankAccount.getBban1()).append(".");
		}
		if (AonStringUtils.isNotBlank(bankAccount.getBban2())) {
			stringBuilder.append(bankAccount.getBban2()).append(".");
		}
		if (AonStringUtils.isNotBlank(bankAccount.getBban3())) {
			stringBuilder.append(AonStringUtils.substring(bankAccount.getBban3(), 0, 2)).append(".").append(AonStringUtils.substring(bankAccount.getBban3(), 2, 4));
		}
		if (AonStringUtils.isNotBlank(bankAccount.getBban4())) {
			stringBuilder.append(bankAccount.getBban4());
		}
		if (AonStringUtils.isNotBlank(bankAccount.getBban5())) {
			stringBuilder.append(bankAccount.getBban5());
		}
		
		assertEquals(stringBuilder.toString(), bankAccount.getCCC());
	}
	
	/**
	 * Test the getter and setter of ccc1
	 */
	@Test
	public void CCC1Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String CCC1 = Faker.instance().letterify("????");
		bankAccount.setCCC1(CCC1);
		
		assertEquals(CCC1, bankAccount.getCCC1());
	}
	
	/**
	 * Test the getter and setter of ccc2
	 */
	@Test
	public void CCC2Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		String CCC2 = Faker.instance().letterify("????");
		bankAccount.setCCC2(CCC2);
		
		assertEquals(CCC2, bankAccount.getCCC2());
	}
	
	/**
	 * Test the getter and setter of ccc3
	 */
	@Test
	public void CCC3Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		
		String CCC3 = Faker.instance().letterify("??");
		
		String bban3 = AonStringUtils.defaultString(AonStringUtils.substring(CCC3, 0, 2))
		+ AonStringUtils.defaultString(AonStringUtils.substring(bankAccount.getBban3(), 2, 4));
		
		bankAccount.setCCC3(CCC3);
		
		assertEquals(bban3, bankAccount.getBban3());
		assertEquals(CCC3, bankAccount.getCCC3());
	}
	
	/**
	 * Test the getter and setter of ccc4
	 */
	@Test
	public void CCC4Test() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		
		String CCC4 = Faker.instance().letterify("??????????");
		
		String bban3 = AonStringUtils.defaultString(AonStringUtils.substring(bankAccount.getBban3(), 0, 4))
			+ AonStringUtils.defaultString(AonStringUtils.substring(CCC4, 0, 2));
				  
		String bban4 = AonStringUtils.defaultString(AonStringUtils.substring(CCC4, 2, 6));
		String bban5 = AonStringUtils.defaultString(AonStringUtils.substring(CCC4, 6, 10));
		
		bankAccount.setCCC4(CCC4);
		
		assertEquals(bban3, bankAccount.getBban3());
		assertEquals(bban4, bankAccount.getBban4());
		assertEquals(bban5, bankAccount.getBban5());
		
		CCC4 = AonStringUtils.substring(bban3, 2, 4) + AonStringUtils.defaultString(bban4) 
		+ AonStringUtils.defaultString(bban5);
		
		assertEquals(CCC4, bankAccount.getCCC4());
	}
	
	/**
	 * Test the method getPureCCC
	 */
	@Test
	public void getPureCCCTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		StringBuilder stringBuilder = new StringBuilder();
		
		if (AonStringUtils.isNotBlank(bankAccount.getBban1())) { stringBuilder.append(bankAccount.getBban1()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban2())) { stringBuilder.append(bankAccount.getBban2()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban3())) { stringBuilder.append(bankAccount.getBban3()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban4())) { stringBuilder.append(bankAccount.getBban4()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban5())) { stringBuilder.append(bankAccount.getBban5()); }
		
		String pureCCC = stringBuilder.toString();
		
		assertEquals(pureCCC, bankAccount.getPureCCC());
	}
	
	/**
	 * Test the method toString
	 */
	@Test
	public void toStringTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		StringBuilder stringBuilder = new StringBuilder();
		
		if (bankAccount.getCountry() != null) { stringBuilder.append(bankAccount.getCountry().getIso2()); }
		if (AonStringUtils.isNotBlank(bankAccount.getCheck())) { stringBuilder.append(bankAccount.getCheck()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban1())) { stringBuilder.append("." + bankAccount.getBban1()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban2())) { stringBuilder.append("." + bankAccount.getBban2()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban3())) { stringBuilder.append("." + bankAccount.getBban3()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban4())) { stringBuilder.append("." + bankAccount.getBban4()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban5())) { stringBuilder.append("." + bankAccount.getBban5()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban6())) { stringBuilder.append("." + bankAccount.getBban6()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban7())) { stringBuilder.append("." + bankAccount.getBban7()); }
		if (AonStringUtils.isNotBlank(bankAccount.getBban8())) { stringBuilder.append("." + bankAccount.getBban8()); }
	
		String toString = stringBuilder.toString();
		
		assertEquals(toString, bankAccount.toString());
	}
	
	/**
	 * Test the method getMaskedIban
	 */
	@Test
	public void getMaskedIbanTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		StringBuilder stringBuilder = new StringBuilder();
		
		if (AonStringUtils.isNotBlank(bankAccount.getBban()) && bankAccount.getCountry() != null) {
			stringBuilder.append(bankAccount.getCountry().getIso2());
			stringBuilder.append(bankAccount.getCheck());
			stringBuilder.append("." + bankAccount.getBban1());
			for (int i=4; i< bankAccount.getBban().length(); i=i+4) {
				if (AonStringUtils.isNotBlank(AonStringUtils.substring(bankAccount.getBban(), i+4, i+8))) {
					stringBuilder.append(".****");
				} else {
					stringBuilder.append("." + AonStringUtils.substring(bankAccount.getBban(), i, i+4));
				}
			}
		}
		
		String maskedIban = stringBuilder.toString();
		
		assertEquals(maskedIban, bankAccount.getMaskedIban());
	}	
	
	/**
	 * Test the method getSeparatedIban
	 */
	@Test
	public void getSeparatedIbanTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		StringBuilder stringBuilder = new StringBuilder();
		
		if (AonStringUtils.isNotBlank(bankAccount.getBban()) && bankAccount.getCountry() != null) {
			stringBuilder.append(bankAccount.getCountry().getIso2());
			stringBuilder.append(bankAccount.getCheck());
			stringBuilder.append(" " + bankAccount.getBban1());
			for (int i=4; i<bankAccount.getBban().length(); i=i+4) {
				stringBuilder.append(" " + AonStringUtils.substring(bankAccount.getBban(), i, i+4));
			}
		}
		
		String separatedIban = stringBuilder.toString();
		
		assertEquals(separatedIban, bankAccount.getSeparatedIban());
	}	
	
	/**
	 * Test the method isValidBankAccount if the bank account is valid
	 */
	@Test
	public void isValidBankAccountOkTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		
		if (bankAccount.isValidBban() && bankAccount.isValidIban())
			assertTrue(bankAccount.isValidBankAccount());
	}
	
	/**
	 * Test the method isValidBankAccount if the bank account is invalid
	 */
	@Test
	public void isValidBankAccountInvalidTest() {
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		bankAccount.setBban1("?????");

		if (!bankAccount.isValidBban() || !bankAccount.isValidIban())
			assertFalse(bankAccount.isValidBankAccount());
	}
}
