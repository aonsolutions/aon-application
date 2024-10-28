package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class BankAccountTest {
	private static final String[] VALID_CCCS = new String[] { 
		 "02350201920024120222"		,"01824731890201593232"		,"15632626313269622806"		,"30810173210015205222"
		,"21000010350202384292"		,"30582533162810017288"		,"21005983100200100228"		,"21008257620200032279"
		,"30810173210015205222"
	};
	
	private static final String[] VALID_IBANS = new String[] { 
		 "ES8002350201920024120222"		,"ES1901824731890201593232"		,"ES3415632626313269622806"		,"ES2330810173210015205222"
		,"ES0621000010350202384292"		,"ES7830582533162810017288"		,"ES0521005983100200100228"		,"ES9721008257620200032279"
		,"ES2800491840092210035846"		,"ES1630170567512485448928"		,"ES7200817155060001398343"		,"BE18967872233965"
		,"FI3318203000058933"			,"EE2414650230492048053271"		,"DK2530003109422614"			
		,"DE75750500000026440560"		,"DE74251900010573574200"		,"DE73300700100397199100"		,"DE54202201000060280004"
		,"CH600023023061827560L"		,"BE98967693950793"				,"BE98967693950793"				,"BE81454008803124"
		,"BE81370043478824"				,"BE67967115141487"				,"BE46967535805936"				,"BE32967458212202"
		,"BE18967872233965"				,"BE15967107123530"				,"BE12738036799192"				,"FR7611899001200002007314586"
		,"FR7616958000019027447940504"	,"FR7617807008122532176169707"	,"FR7617959000288541802170122"	,"FR7618306000723609310659245"
		,"FR7631489000100026036400547"	,"FR7641199110050002287730164"	,"FR7642559100000800234853705"	,"GB13CITI18500808738904"
		,"GB16HBUK40051692352524"		,"GB16HBUK40051692352524"		,"GB75SVBK62100020107609"		,"IT03W0306954900000001712139"
		,"IT62S0344001612000000160700"	,"IT77V0585636282109571210334"	,"IT77V0585636282109571210334"	,"LT023500010003068689"
		,"LT333250068966494687"			,"LU570030855290750000"			,"NL02ABNA0626086345"			,"NL09INGB0671124331"
		,"NL15RABO0334592070"			,"PT50001000003603902000108"	,"PT50001000005194175000175"	,"PT50001000005842959000140"
		,"PT50001800033005661802016"	,"PT50003300004538265639005"	,"PT50003300004551781395905"	,"PT50003300004553720299805"
		,"PT50003501410009302583094"	,"PT50003504290003070463022"	,"PT50004587354032497056550"	,"SE5680000831391376768121"
		,"TN5904115080002799621444"		,"ES0200811406950001099916"
	};
	
	@Test
	void testConstructIBAN() {
		AonCollectionUtils.stream(VALID_IBANS)
			.forEach( v -> {
				BankAccount ba = new BankAccount(v);
				assertEquals( v, ba.getIban());
			});
	}
	
	@Test
	void testConstructCCC() {
		AonCollectionUtils.stream(VALID_CCCS)
			.forEach( v -> {
				BankAccount ba = new BankAccount(v, true);
				assertEquals( v, ba.getRawCCC());
			});
	}
	
	@Test
	void testValidBankAccount() {
		AonCollectionUtils.stream(VALID_IBANS)
			.forEach( v -> {
				BankAccount ba = new BankAccount(v);
				assertTrue( ba.isValidBankAccount(), ba.getIban());
			});
	}

	@Test
	void testValidCCC() {
		AonCollectionUtils.stream(VALID_CCCS)
			.forEach( v -> {
				BankAccount ba = new BankAccount(v, true);
				assertTrue( ba.isValidBankAccount(), v);
			});
	}

	@Test
	void testToString() {
		AonCollectionUtils.stream(VALID_IBANS)
			.forEach( v -> {
				BankAccount ba = new BankAccount(v);
				String a = AonStringUtils.remove(ba.toString(), '.');
				assertEquals( v, a );
			});
	}

	@Test
	void testGetCCC() {
		AonCollectionUtils.stream(VALID_CCCS)
			.forEach( v -> {
				BankAccount ba = new BankAccount(v, true);
				String a = AonStringUtils.remove(ba.getCCC(), '.');
				assertEquals( v, a );
			});
	}

	@Test
	void testBankAccount() {
		BankAccount expected = AonMocker.mock(BankAccount.class);
		BankAccount actual = new BankAccount()
			.setCountry(expected.getCountry())
			.setCheck(expected.getCheck())
			.setBban1(expected.getBban1())
			.setBban2(expected.getBban2())
			.setBban3(expected.getBban3())
			.setBban4(expected.getBban4())
			.setBban5(expected.getBban5())
			.setBban6(expected.getBban6())
			.setBban7(expected.getBban7())
			.setBban8(expected.getBban8())
			;				
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
