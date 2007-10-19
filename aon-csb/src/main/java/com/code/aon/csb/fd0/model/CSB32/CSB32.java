package com.code.aon.csb.fd0.model.CSB32;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.code.aon.csb.fd0.core.DiskRegisterLoader;
import com.code.aon.csb.fd0.model.AbstractFileFiller;
import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.FileFiller;
import com.code.aon.csb.fd0.model.CSB32.check.CheckDelivery;
import com.code.aon.csb.fd0.model.CSB32.check.CheckIndividual;
import com.code.aon.csb.fd0.model.CSB32.check.CheckLot;
import com.code.aon.csb.fd0.model.CSB32.data.Account;
import com.code.aon.csb.fd0.model.CSB32.data.Individual;
import com.code.aon.csb.fd0.model.CSB32.data.Delivery;
import com.code.aon.csb.fd0.model.CSB32.data.Lot;
import com.code.aon.csb.fd0.model.CSB32.xml.XMLLoader;

public class CSB32 extends AbstractFileFiller {

	private Lot lot;
	
	public CSB32(Lot lot, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		this.lot = lot;
		
		InputStream input = XMLLoader.class.getResourceAsStream("Cabecera.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("CabeceraRemesa.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("Individual_I.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("Individual_II.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("Individual_III.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("FinRemesa.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("FinCabecera.xml");
		DiskRegisterLoader.load(input, manager);
	}

	public ArrayList<Exception> create() {
		Map<String,Object> properties = new HashMap<String,Object>();
		try{
			properties.put(CSB32.LOT, lot);

			if (CheckLot.parse(lot,exceptions)==false)
				throw new Fd0Exception( "ABORTED: ",lot.toString());

			createLine("Cabecera",properties);

			Iterator<Delivery> iterDelivery = lot.getDeliveriesIterator();
			while (iterDelivery.hasNext()){
				Delivery delivery = iterDelivery.next();
				properties.put(CSB32.DELIVERY, delivery);
				properties.put(CSB32.DELIVERY_ACCOUNT_1, delivery.getPaymentAccount());
				properties.put(CSB32.DELIVERY_ACCOUNT_2, delivery.getOweAccount());
				properties.put(CSB32.DELIVERY_ACCOUNT_3, delivery.getNotPayedAccount());
				try{

					if (CheckDelivery.parse(delivery,exceptions)==false)
						throw new Fd0Exception( "ABORTED: ",delivery.toString());

					createLine("CabeceraRemesa",properties);
	
					Iterator<Individual> iterIndividual = delivery.getIndividualsIterator();
					
					while (iterIndividual.hasNext()){
						Individual individual = iterIndividual.next();

						properties.put(CSB32.INDIVIDUAL, individual);
						properties.put(CSB32.INDIVIDUAL_ACCOUNT, individual.getAccount());
						try{
							if (CheckIndividual.parse(individual,exceptions)==false)
								throw new Fd0Exception( "ABORTED: ",individual.toString());

							createLine("Individual_I",properties);
							createLine("Individual_II",properties);
							createLine("Individual_III",properties);
						} catch (Exception ex) {
							if ( ex instanceof Fd0Exception ) {
								exceptions.add (ex);
							} 
							else {
								Fd0Exception e = new Fd0Exception( ex.getMessage(),individual.toString());
								exceptions.add (e);
							}
						}
					}
					createLine("FinRemesa",properties);
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),delivery.toString());
						exceptions.add (e);
					}
				}
				createLine("FinCabecera",properties);
			}
		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} 
			else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),lot.toString());
				exceptions.add (e);
			}
		}
		output.flush();
		writeErrorsFile();
		return exceptions;
	}

	private static String LOT = "LOT";
	private static String DELIVERY = "DELIVERY";
	private static String DELIVERY_ACCOUNT_1 = "DELIVERY_ACCOUNT_1";
	private static String DELIVERY_ACCOUNT_2 = "DELIVERY_ACCOUNT_2";
	private static String DELIVERY_ACCOUNT_3 = "DELIVERY_ACCOUNT_3";
	private static String INDIVIDUAL = "INDIVIDUAL";
	private static String INDIVIDUAL_ACCOUNT = "INDIVIDUAL_ACCOUNT";

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		Lot lot = new Lot();
		lot.setEntity(new Integer(1113));
		lot.setFileDate(new Date());
		lot.setFileNumber(new Integer(1111));
		lot.setOffice(new Integer(1112));
		
		Delivery delivery = new Delivery();
		delivery.setDeliveyNumber(new Integer(1));
		delivery.setGiverCode("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		Account ccc1 = new Account();
		ccc1.parse("00000000000000000000");
		delivery.setNotPayedAccount(ccc1);
		Account ccc2 = new Account();
		ccc2.parse("00000000000000000000");
		delivery.setOweAccount(ccc2);
		Account ccc3 = new Account();
		ccc3.parse("00000000000000000000");
		delivery.setPaymentAccount(ccc3);
		delivery.setTruncatedEffects(new Integer(2));

		Individual individual = new Individual();
		Account ccc4 = new Account();
		ccc4.parse("00000000000000000000");
		individual.setAccount(ccc4);
		individual.setAceptedCode(new Integer(1));
		individual.setAditionalData("ADITIONAL DATA");
		individual.setAmount(new Double(12.34));
		individual.setDocumentNumber("12131");
		individual.setDocumentType(new Integer(1));
		individual.setEfectPayed("A");
		individual.setEfectPayer("B");
		individual.setExpenseClause(new Integer(9));
		individual.setExpiryDate(new Date());
		individual.setIneCode("INECODE");
		individual.setPayedAddress("PAYED ADDRESS");
		individual.setPayedDocument("PAYED DOCU");
		individual.setPayedPost("POST");
		individual.setPayedPostINE("1234567");
		individual.setPayedPostPostalCode(new Integer(01001));
		individual.setPayedPostProvince(new Integer(1));
		individual.setPaymentDate(new Date());
		individual.setPaymentPost("PAYPOST");
		individual.setProvinceNumber(new Integer(1));
		
		delivery.addIndividual(individual);
		
		lot.addDelivery(delivery);
		
		String filePath = "c:/tmp/csb/CSB32.txt";
		FileFiller csb32 = new CSB32(lot,filePath);
		csb32.create();
	}
	
}
