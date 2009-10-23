package com.code.aon.csb.fd0.model.CSB34;



import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.code.aon.csb.fd0.core.DiskRegisterLoader;
import com.code.aon.csb.fd0.model.AbstractFileFiller;
import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.FileFiller;
import com.code.aon.csb.fd0.model.CSB34.checks.CheckDetail;
import com.code.aon.csb.fd0.model.CSB34.checks.CheckMaster;
import com.code.aon.csb.fd0.model.CSB34.data.Account;
import com.code.aon.csb.fd0.model.CSB34.data.Check;
import com.code.aon.csb.fd0.model.CSB34.data.Detail;
import com.code.aon.csb.fd0.model.CSB34.data.Master;
import com.code.aon.csb.fd0.model.CSB34.data.Orderer;
import com.code.aon.csb.fd0.model.CSB34.data.Receiver;
import com.code.aon.csb.fd0.model.CSB34.data.Transfer;
import com.code.aon.csb.fd0.model.CSB34.xml.XMLLoader;

/**
 * CSB34 format file creator
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class CSB34 extends AbstractFileFiller{

	/**
	 * Main data object
	 */
	private Master master;
	/**
	 * Number of registries
	 */
	private int numreg;
	/**
	 * Number of 010 registries 
	 */
	private int num010;
	/**
	 * Total amount
	 */
	private double amount;
	
	/**
	 * Constructor for this path and this data
	 * Loads all registry formats
	 * 
	 * @param master the main data object
	 * @param filePath the file path
	 * @throws FileNotFoundException
	 */
	public CSB34(Master master, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		this.master = master;
		
		InputStream input = XMLLoader.class.getResourceAsStream("Cabecera_1.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_2.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_3.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_4.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_5.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_6.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_1.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_2.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_3.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_4.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_5.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_6.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_7.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_8.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Beneficiario_9.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("Totales_1.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	/**
	 * Check all data and creates the lines.
	 * 
	 * @see com.code.aon.csb.fd0.model.FileFiller#create()
	 */
	public ArrayList<Exception> create(){
		try{
			if (CheckMaster.parse(master,exceptions)==false)
				throw new Fd0Exception( "ABORTED: ",master.toString());
			numreg = 0;
			num010 = 0;
			amount = 0;
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(CSB34.MASTER, master);
			properties.put(CSB34.MASTER_PERSON, master.getOrderer());
			properties.put(CSB34.MASTER_ACCOUNT, master.getAccount());
			if (createLine("Cabecera_1",properties)!=null)			
				++numreg;
			if (createLine("Cabecera_2",properties)!=null)			
				++numreg;
			if(createLine("Cabecera_3",properties)!=null)			
				++numreg;
			if (createLine("Cabecera_4",properties)!=null)			
				++numreg;
			if (master.getOperator()!=null){
				properties.put(CSB34.MASTER_OPERATOR, master.getOperator());
				if (createLine("Cabecera_5",properties)!=null)			
					++numreg;
				if (createLine("Cabecera_6",properties)!=null)			
					++numreg;
			}
			Iterator iter = master.getReceiversIterator();
			while (iter.hasNext()){
				Detail detail = (Detail)iter.next();
				try{
					if (CheckDetail.parse(detail,exceptions)==false)
						throw new Fd0Exception( "ABORTED: ",detail.toString());
					properties.put(CSB34.DETAIL, detail);
					properties.put(CSB34.DETAIL_PERSON, detail.getReceiver());
					properties.put(CSB34.DETAIL_ACCOUNT, detail.getAccount());
					if (createLine("Beneficiario_1",properties)!=null)			
						++numreg;
					++num010;
					amount += detail.getAmount();
					if (createLine("Beneficiario_2",properties)!=null)			
						++numreg;
					if (createLine("Beneficiario_3",properties)!=null)			
						++numreg;
					if (detail.getReceiver().getAddressPart2()!=null){
						if (createLine("Beneficiario_4",properties)!=null)			
							++numreg;
					}
					if (createLine("Beneficiario_5",properties)!=null)			
						++numreg;
					if (createLine("Beneficiario_6",properties)!=null)			
						++numreg;
					if (createLine("Beneficiario_7",properties)!=null)			
						++numreg;
					if (detail.getConceptDescPart2()!=null){
						if (createLine("Beneficiario_8",properties)!=null)			
							++numreg;
					}
					if (createLine("Beneficiario_9",properties)!=null)			
						++numreg;
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),detail.toString());
						exceptions.add (e);
					}
				}
			}
			++numreg;
			master.setNumreg(numreg);
			master.setNum010(num010);
			master.setAmount(amount);
			createLine("Totales_1",properties);
		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} 
			else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),master.toString());
				exceptions.add (e);
			}
		}
		output.flush();
		writeErrorsFile();
		return exceptions;
	}

	
	/**
	 * The Master object
	 */
	private static String MASTER = "MASTER";
	/**
	 * The Person in Master
	 */
	private static String MASTER_PERSON = "MASTER_PERSON";
	/**
	 * The Account in Master
	 */
	private static String MASTER_ACCOUNT = "MASTER_ACCOUNT";
	/**
	 * The Operator in Master
	 */
	private static String MASTER_OPERATOR = "MASTER_OPERATOR";
	/**
	 * Detail object
	 */
	private static String DETAIL = "DETAIL";
	/**
	 * The Person of Detail
	 */
	private static String DETAIL_PERSON = "DETAIL_PERSON";
	/**
	 * The Account of Detail
	 */
	private static String DETAIL_ACCOUNT = "DETAIL_ACCOUNT";


	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		Receiver R1 = new Receiver();
		R1.setAddress("DIRECCION 11111111111111 22222222222222222 3333333333333333 444444444444444444 5555555555");
		R1.setCode("CODE");
		R1.setCodeAlt("CODE ALTER");
		R1.setCountry("COUNTRY");
		R1.setCounty("COUNTY");
		R1.setDocument("11111111H");
		R1.setName("NAME AAAAAAAA");
		R1.setPostalcode("01001");
		Account accountR1 = new Account();
		accountR1.parse("00000000000000000000");
		Detail dR1 = new Check();
		dR1.setReceiver(R1);
		dR1.setAccount(accountR1);
		dR1.setAmount(123456.99);
		dR1.setConcept("9");
		dR1.setConceptDesc("11111111111111111111111 22222222222222222222222222222 33333333333333333333333 444444444444444444444444 5555555555");

		Receiver R2 = new Receiver();
		R2.setAddress("222 DIRECCION 11111111111111 22222222222222222 3333333333333333 444444444444444444 5555555555");
		R2.setCode("2CODE");
		R2.setCodeAlt("2CODE ALTER");
		R2.setCountry("2COUNTRY");
		R2.setCounty("2COUNTY");
		R2.setDocument("21111111H");
		R2.setName("2NAME AAAAAAAA");
		R2.setPostalcode("21001");
		Account accountR2 = new Account();
		accountR2.parse("00000000000000000000");
		Detail dR2 = new Transfer();
		dR2.setReceiver(R2);
		dR2.setAccount(accountR2);
		dR2.setAmount(654321.11);
		dR2.setConcept("1");
		dR2.setConceptDesc("11111111111111111111111 22222222222222222222222222222 33333333333333333333333 444444444444444444444444 5555555555");

		Orderer person = new Orderer();
		person.setCode("123456789");
		person.setName("PERSON NAME");
		person.setAddress("CALLE NUMERO");
		person.setCounty("PLAZA");

		Account account = new Account();
		account.parse("00000000000000000000");
		
		Master master = new Master();
		master.setOrderer(person);
		master.setAccount(account);
		master.setOrderDate(new java.util.Date());
		master.setSendDate(new java.util.Date());
		master.setDetail("1");
		master.setCosts("1");
		master.addReceiver(dR1);
		master.addReceiver(dR2);
		
		String filePath = "c:/tmp/csb/CSB34.txt";
		FileFiller csb34 = new CSB34(master,filePath);
		csb34.create();
	}
	
}
