package com.code.aon.file.bank.model.BE;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.code.aon.file.bank.model.BE.checks.CheckLot;
import com.code.aon.file.bank.model.BE.checks.CheckRelationship;
import com.code.aon.file.bank.model.BE.checks.CheckTransfer;
import com.code.aon.file.bank.model.BE.data.Lot;
import com.code.aon.file.bank.model.BE.data.Relationship;
import com.code.aon.file.bank.model.BE.data.Transfer;
import com.code.aon.file.bank.model.BE.xml.XMLLoader;
import com.code.aon.file.format.core.Account;
import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.format.model.FileFiller;

/**
 * BE format file creator
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class BE extends AbstractFileFiller{

	/**
	 * The main data object
	 */
	private Lot lot;
	
	/**
	 * Constructor for this path and this data
	 * Loads all registry formats
	 * 
	 * @param lot the lot data object
	 * @param filePath the file path
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public BE(Lot lot, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		this.lot = lot;
		
		InputStream input = XMLLoader.class.getResourceAsStream("Cabecera.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("CabeceraRelacion.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Transferencia.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("FinRelacion.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Fin.xml");
		DiskRegisterLoader.load(input, manager);

	}

	public ArrayList<Exception> create() {
		Map<String,Object> properties = new HashMap<String,Object>();
		int regs = 0;
		try{
			properties.put(BE.LOT, lot);

			if (CheckLot.parse(lot,exceptions)==false)
				throw new Fd0Exception( "ABORTED: ",lot.toString());

			createLine("Cabecera",properties);
			regs++;
			Iterator<Relationship> iterRelationship = lot.getRelationshipsIterator();
			while (iterRelationship.hasNext()){
				Relationship relationship = iterRelationship.next();
				properties.put(BE.RELATION, relationship);
				try{

					if (CheckRelationship.parse(relationship,exceptions)==false)
						throw new Fd0Exception( "ABORTED: ",relationship.toString());

					createLine("CabeceraRelacion",properties);
					regs++;
	
					Iterator<Transfer> iterTransfer = relationship.getTransfersIterator();
					int numberCode = 1;
					while (iterTransfer.hasNext()){
						Transfer transfer = iterTransfer.next();
						transfer.setNumberCode(new Integer(numberCode));
						properties.put(BE.TRANSFER, transfer);
						properties.put(BE.TRANSFER_ACCOUNT, transfer.getCcc());
						try{

							if (CheckTransfer.parse(transfer,exceptions)==false)
								throw new Fd0Exception( "ABORTED: ",transfer.toString());

							createLine("Transferencia",properties);
							numberCode++;
							regs++;
						} catch (Exception ex) {
							if ( ex instanceof Fd0Exception ) {
								exceptions.add (ex);
							} 
							else {
								Fd0Exception e = new Fd0Exception( ex.getMessage(),transfer.toString());
								exceptions.add (e);
							}
						}
					}
					createLine("FinRelacion",properties);
					regs++;
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),relationship.toString());
						exceptions.add (e);
					}
				}
				regs++;
				lot.setRegisterNumber(new Integer(regs));
				createLine("Fin",properties);
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

	/**
	 * Lot object ident
	 */
	private static String LOT = "LOT";
	/**
	 * Relationship object ident
	 */
	private static String RELATION = "RELATION";
	/**
	 * Transfer object ident
	 */
	private static String TRANSFER = "TRANSFER";
	/**
	 * Transfer account object ident
	 */
	private static String TRANSFER_ACCOUNT = "TRANSFER_ACCOUNT";
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		Lot lot = new Lot();
		lot.setPresenterCode(new Integer(1));
		lot.setAplicationCode(new Integer(51));
		lot.setPresenterName("PRESNAME");
		lot.setResponsibleName("RESANAME");
		lot.setResponsiblePhone(new Integer(945000001));
		lot.setGenerationDate(new Date());
		lot.setNumber(new Integer(1));
		lot.setSumKey(new Integer(1));
		
		Relationship relationship = new Relationship();
		relationship.setEmitterCode(new Integer(1));
		relationship.setExecutionDate(new Date());
		relationship.setOrderNumber(new Integer(1));
		relationship.setTransferSumKey(new Integer(1));

		Transfer transfer = new Transfer();
		transfer.setAccountOfPart1(new String("STR"));
		transfer.setAccountOfPart2(new String("STR"));
		transfer.setAmount(new Double(1.1));
		transfer.setAuthKey(new Integer(1));
		Account ccc = new Account();
		ccc.parse("00000000000000000000");
		transfer.setCcc(ccc);
		transfer.setConceptPart1(new String("STR"));
		transfer.setConceptPart2(new String("STR"));
		transfer.setRecipientAddress(new String("STR"));
		transfer.setRecipientCountry(new String("STR"));
		transfer.setRecipientCounty(new String("STR"));
		transfer.setRecipientDocument(new String("STR"));
		transfer.setRecipientForReference(new String("STR"));
		transfer.setRecipientIdent(new String("STR"));
		transfer.setRecipientNamePart1(new String("STR"));
		transfer.setRecipientNamePart2(new String("STR"));
		transfer.setRecipientNamePart3(new String("STR"));
		transfer.setRecipientReference(new String("STR"));
		transfer.setType(new String("1"));
		
		relationship.addTransfer(transfer);
		
		lot.addRelationship(relationship);

		String filePath = "c:/tmp/csb/BE.txt";
		FileFiller be = new BE(lot,filePath);
		be.create();
	}

}
