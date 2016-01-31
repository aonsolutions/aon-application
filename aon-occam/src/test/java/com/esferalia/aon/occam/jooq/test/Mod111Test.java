package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod111Test {

	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 536;
	private static String USER = "mac";
	
	@Test
	public void testInitializeAlava() throws IOException {
		Mod111 mod111 = AON.initializeMod111(DOMAIN_NAME, DOMAIN_ID, USER, null);
		mod111.setAdministration(Administration.ALAVA);
		System.out.println("INITIALIZED!");
		print(mod111);
		
		mod111 = AON.createMod111(DOMAIN_NAME, DOMAIN_ID, USER, mod111);
		
		System.out.println("CREATED!");
		print(mod111);
		mod111.ensureDetail(Mod111Key.AR_C50).setAmount(1.0);
		mod111.ensureDetail(Mod111Key.AR_C60).setAmount(1000.0);
		mod111.ensureDetail(Mod111Key.AR_C70).setAmount(200.0);
		mod111.ensureDetail(Mod111Key.AR_C51).setAmount(1.0);
		mod111.ensureDetail(Mod111Key.AR_C61).setAmount(1000.0);
		mod111.ensureDetail(Mod111Key.AR_C71).setAmount(200.0);
		
		mod111.ensureDetail(Mod111Key.AR_C83).setAmount(10.0);
		mod111.ensureDetail(Mod111Key.AR_C84).setAmount(20.0);
		mod111.ensureDetail(Mod111Key.AR_C85).setAmount(30.0);
		
		mod111 = AON.calculate(DOMAIN_NAME, USER, mod111);
		
		System.out.println("CALCULATED!");
		print(mod111);
	}
	
	@Test
	@Ignore
	public void testInitializeBizkaia() throws IOException {
		Mod111 mod111 = new Mod111();
		mod111.setDomain(DOMAIN_ID);
		mod111.setAdministration(Administration.BIZKAIA);
		mod111.setYear(2016);
		mod111.setPeriod(Period.M01);
		mod111 = AON.initializeMod111(DOMAIN_NAME, DOMAIN_ID, USER, mod111);
		print(mod111);
	}

	@Test
	@Ignore
	public void testInitializeCommonTerritory() throws IOException {
		Mod111 mod111 = new Mod111();
		mod111.setDomain(DOMAIN_ID);
		mod111.setAdministration(Administration.COMMON_TERRITORY);
		mod111.setYear(2016);
		mod111.setPeriod(Period.M01);
		mod111 = AON.initializeMod111(DOMAIN_NAME, DOMAIN_ID, USER, mod111);
		print(mod111);
	}

	@Test
	@Ignore
	public void testInitializeGipuzkoa() throws IOException {
		Mod111 mod111 = new Mod111();
		mod111.setDomain(DOMAIN_ID);
		mod111.setAdministration(Administration.GIPUZKOA);
		mod111.setYear(2016);
		mod111.setPeriod(Period.M01);
		mod111 = AON.initializeMod111(DOMAIN_NAME, DOMAIN_ID, USER, mod111);
		print(mod111);
	}

	@Test
	@Ignore
	public void testInitializeNavarra() throws IOException {
		Mod111 mod111 = new Mod111();
		mod111.setDomain(DOMAIN_ID);
		mod111.setAdministration(Administration.NAVARRA);
		mod111.setYear(2016);
		mod111.setPeriod(Period.M01);
		mod111 = AON.initializeMod111(DOMAIN_NAME, DOMAIN_ID, USER, mod111);
		print(mod111);
	}

	private void print(Mod111 mod111) {
		System.out.println( " ..: " + mod111 );
		System.out.println( " Id ..: " + mod111.getId());
		System.out.println( " Domain ..: " + mod111.getDomain());
		System.out.println( " DomainName ..: " + mod111.getDomainName());
		System.out.println( " Year ..: " + mod111.getYear());
		System.out.println( " Finance ..: " + mod111.getFinance());
		System.out.println( " Model ..: " + mod111.getModel());
		System.out.println( " Period ..: " + mod111.getPeriod());
		System.out.println( " Administration ..: " + mod111.getAdministration());
		System.out.println( " Status ..: " + mod111.getStatus());
		System.out.println( " Confidential ..: " + mod111.isConfidential());
		System.out.println( " Complementary ..: " + mod111.isComplementary());
		System.out.println( " Replacement ..: " + mod111.isReplacement());
		System.out.println( " WithoutActivity ..: " + mod111.isWithoutActivity());
		System.out.println( " Number ..: " + mod111.getNumber());
		System.out.println( " ReplacedNumber ..: " + mod111.getReplacedNumber());
		System.out.println( " Comments ..: " + mod111.getComments());
		System.out.println( " Document ..: " + mod111.getDocument());
		System.out.println( " Surname ..: " + mod111.getSurname());
		System.out.println( " Name ..: " + mod111.getName());
		System.out.println( " Entity ..: " + mod111.isEntity());
		System.out.println( " StreetInitial ..: " + mod111.getStreetInitial());
		System.out.println( " StreetName ..: " + mod111.getStreetName());
		System.out.println( " StreetNumber ..: " + mod111.getStreetNumber());
		System.out.println( " StreetStair ..: " + mod111.getStreetStair());
		System.out.println( " StreetFloor ..: " + mod111.getStreetFloor());
		System.out.println( " StreetDoor ..: " + mod111.getStreetDoor());
		System.out.println( " Phone ..: " + mod111.getPhone());
		System.out.println( " Town ..: " + mod111.getTown());
		System.out.println( " Province ..: " + mod111.getProvince());
		System.out.println( " Zip ..: " + mod111.getZip());
		System.out.println( " AdmonAeat ..: " + mod111.getAdmonAeat());
		System.out.println( " ContactPerson ..: " + mod111.getContactPerson());
		System.out.println( " ContactPhone ..: " + mod111.getContactPhone());
		System.out.println( " ContactCellular ..: " + mod111.getContactCellular());
		System.out.println( " ContactEmail ..: " + mod111.getContactEmail());
		System.out.println( " Iban ..: " + mod111.getIban());
		
		System.out.println( AonStringUtils.repeat("=",100));
		for (FiscalModelDetail detail : mod111.getMap().values()) {
			Mod111Key key = Mod111Key.getKey(detail.getType());
			System.out.println(
				detail.getType()	
				+ " " + AonStringUtils.leftPad( AonNumberUtils.toString( key.getBox()) , 3)
				+ " " + AonStringUtils.leftPad( AonNumberUtils.toString( detail.getAmount()) , 12)
				);
		}
		System.out.println( AonStringUtils.repeat("=",100));
		
	}

}
