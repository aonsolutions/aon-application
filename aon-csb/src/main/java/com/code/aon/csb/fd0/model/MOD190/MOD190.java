package com.code.aon.csb.fd0.model.MOD190;

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
import com.code.aon.csb.fd0.model.MOD190.checks.CheckPresenter;
import com.code.aon.csb.fd0.model.MOD190.checks.CheckReceiver;
import com.code.aon.csb.fd0.model.MOD190.checks.CheckWithHolder;
import com.code.aon.csb.fd0.model.MOD190.data.Lot;
import com.code.aon.csb.fd0.model.MOD190.data.Presenter;
import com.code.aon.csb.fd0.model.MOD190.data.Receiver;
import com.code.aon.csb.fd0.model.MOD190.data.WithHolder;
import com.code.aon.csb.fd0.model.MOD190.xml.XMLLoader;

/**
 * 190 format file creator
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class MOD190  extends AbstractFileFiller{

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
	 */
	public MOD190(Lot lot, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		this.lot = lot;
		
		InputStream input = XMLLoader.class.getResourceAsStream("Presentador.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("Retenedor.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Perceptor.xml");
		DiskRegisterLoader.load(input, manager);

	}

	/**
	 * Check all data and creates the lines.
	 * 
	 * @see com.code.aon.csb.fd0.model.FileFiller#create()
	 */
	public ArrayList<Exception> create() {
		Map<String,Object> properties = new HashMap<String,Object>();
		Presenter presenter = lot.getPresenter();
		try{
			properties.put(MOD190.LOT, lot);
			
			if (presenter != null){
				properties.put(MOD190.PRESENTER, presenter);
				if (CheckPresenter.parse(presenter,exceptions)==false)
					throw new Fd0Exception( "ABORTED: ",presenter.toString());

				createLine("Presentador",properties);
			}
			
			Iterator<WithHolder> iterWH = lot.getWithHoldersIterator();
			while (iterWH.hasNext()){
				WithHolder withHolder = iterWH.next();
				properties.put(MOD190.WITHHOLDER, withHolder);
				try{
					if (CheckWithHolder.parse(withHolder,exceptions)==false)
						throw new Fd0Exception( "ABORTED: ",withHolder.toString());
					
					createLine("Retenedor",properties);
	
					Iterator<Receiver> iter = withHolder.getReceiversIterator();
					while (iter.hasNext()){
						Receiver receiver = iter.next();
						properties.put(MOD190.RECEIVER, receiver);
						try{
							if (CheckReceiver.parse(receiver,exceptions)==false)
								throw new Fd0Exception( "ABORTED: ",receiver.toString());
							
							createLine("Perceptor",properties);
						} catch (Exception ex) {
							if ( ex instanceof Fd0Exception ) {
								exceptions.add (ex);
							} 
							else {
								Fd0Exception e = new Fd0Exception( ex.getMessage(),receiver.toString());
								exceptions.add (e);
							}
						}
					}
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),withHolder.toString());
						exceptions.add (e);
					}
				}
			}
		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} 
			else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),presenter.toString());
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
	 * Presenter object ident
	 */
	private static String PRESENTER = "PRESENTER";
	/**
	 * WithHolder object ident
	 */
	private static String WITHHOLDER = "WITHHOLDER";
	/**
	 * Receiver object ident
	 */
	private static String RECEIVER = "RECEIVER";
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		Lot lot = new Lot();
		
		Presenter presenter = new Presenter();
		presenter.setYear(2006);
		presenter.setCode("11111111H");
		presenter.setName("NAME PRESENTER 1");
		presenter.setStreetType("AV");
		presenter.setStreetName("CALLE NOMBRE");
		presenter.setStreetNumber(1);
		presenter.setStreetStair(null);
		presenter.setStreetStorey(null);
		presenter.setStreetDoor(null);
		presenter.setPostalCode(01004);
		presenter.setCounty("VITORIA");
		presenter.setProvince(01);
		presenter.setRelPhone(945100000);
		presenter.setRelName("NAMEREL");

		lot.setPresenter(presenter);

		WithHolder withHolder = new WithHolder();
		withHolder.setYear(2006);
		withHolder.setCode("11111112L");
		withHolder.setName("NAME WITHHOLDER 1");
		withHolder.setRelPhone(945121010);
		withHolder.setRelName("NAME_RELLLL");
		withHolder.setJustify(1223434);
		withHolder.setComplementary(false);
		withHolder.setReplaces(true);
		withHolder.setReplacedJustify(12312321);
		
		Receiver receiver = new Receiver();
		receiver.setCode("11111113C");
		receiver.setManagerCode("11111114K");
		receiver.setName("NOMBRE RECEIVER 1");
		receiver.setProvince(01);
		receiver.setKey("B");
		receiver.setSubkey(01);
		receiver.setReceibedMoney(400.0);
		receiver.setWithholdedMoney(200.0);
		receiver.setReceibedSpice(100.0);
		receiver.setPayEfect(50.0);
		receiver.setPayReperc(50.0);
		receiver.setYear(2006);
		receiver.setCeuMel(0);
		receiver.setBornYear(1980);
		receiver.setFamilyStatus(2);
		receiver.setMarriageCode("1");
		receiver.setDiscapacity(1);
		receiver.setRelation(2);
		receiver.setLaboralExtension(3);
		receiver.setGeoMovility(4);
		receiver.setReductions(250.0);
		receiver.setCosts(10.0);
		receiver.setPension(10.0);
		receiver.setFood(0.0);
		receiver.setChildren1(1);
		receiver.setChildren2(2);
		receiver.setChildren3(12);
		receiver.setChildren4(22);
		receiver.setChildHandicapped1(1);
		receiver.setChildHandicapped2(2);
		receiver.setChildHandicapped3(3);
		receiver.setChildHandicapped4(4);
		receiver.setChildHandicapped5(5);
		receiver.setChildHandicapped6(6);
		receiver.setParents1(1);
		receiver.setParents2(2);
		receiver.setParents3(3);
		receiver.setParents4(4);
		receiver.setParentsHandicapped1(1);
		receiver.setParentsHandicapped2(2);
		receiver.setParentsHandicapped3(3);
		receiver.setParentsHandicapped4(4);
		receiver.setParentsHandicapped5(5);
		receiver.setParentsHandicapped6(6);

		withHolder.addReceiver(receiver);

		Receiver receiver2 = new Receiver();
		receiver2.setCode("11111115E");
		receiver2.setManagerCode("11111116T");
		receiver2.setName("NOMBRE RECEIVER 2");
		receiver2.setProvince(01);
		receiver2.setKey("F");
		receiver2.setSubkey(01);
		receiver2.setReceibedMoney(400.0);
		receiver2.setWithholdedMoney(300.0);
		receiver2.setReceibedSpice(100.0);
		receiver2.setPayEfect(0.0);
		receiver2.setPayReperc(0.0);
		receiver2.setYear(2006);
		receiver2.setCeuMel(0);

		withHolder.addReceiver(receiver2);

		lot.addWithHolder(withHolder);

		WithHolder withHolder2 = new WithHolder();
		withHolder2.setYear(2006);
		withHolder2.setCode("11111117R");
		withHolder2.setName("NOMBRE WH 2");
		withHolder2.setRelPhone(945121010);
		withHolder2.setRelName("NAME_RELLLL");
		withHolder2.setJustify(1223434);
		withHolder2.setComplementary(false);
		withHolder2.setReplaces(true);
		withHolder2.setReplacedJustify(12312321);
		
		Receiver receiver3 = new Receiver();
		receiver3.setCode("11111118W");
		receiver3.setManagerCode("11111117R");
		receiver3.setName("NOMBRE RECEIVER 3");
		receiver3.setProvince(01);
		receiver3.setKey("F");
		receiver3.setSubkey(01);
		receiver3.setReceibedMoney(0.0);
		receiver3.setWithholdedMoney(0.0);
		receiver3.setReceibedSpice(0.0);
		receiver3.setPayEfect(0.0);
		receiver3.setPayReperc(0.0);
		receiver3.setYear(2006);
		receiver3.setCeuMel(0);

		withHolder2.addReceiver(receiver3);

		lot.addWithHolder(withHolder2);

		String filePath = "c:/tmp/csb/MOD190.txt";
		FileFiller mod190 = new MOD190(lot,filePath);
		mod190.create();
	}

}
