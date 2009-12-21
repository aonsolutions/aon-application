package com.code.aon.csb.fd0.model.MOD349;

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
import com.code.aon.csb.fd0.model.MOD349.data.Presenter;
import com.code.aon.csb.fd0.model.MOD349.data.Lot;
import com.code.aon.csb.fd0.model.MOD349.check.CheckCorrection;
import com.code.aon.csb.fd0.model.MOD349.check.CheckDeponent;
import com.code.aon.csb.fd0.model.MOD349.check.CheckOperator;
import com.code.aon.csb.fd0.model.MOD349.check.CheckPresenter;
import com.code.aon.csb.fd0.model.MOD349.data.Operator;
import com.code.aon.csb.fd0.model.MOD349.data.Correction;
import com.code.aon.csb.fd0.model.MOD349.xml.XMLLoader;
import com.code.aon.csb.fd0.model.MOD349.data.Deponent;

/**
 * 349 format file creator
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class MOD349 extends AbstractFileFiller{

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
	protected MOD349(Lot lot, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		this.lot = lot;
		
		InputStream input = XMLLoader.class.getResourceAsStream("Presentador.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Declarante.xml");
		DiskRegisterLoader.load(input, manager);
		
		input = XMLLoader.class.getResourceAsStream("Operador.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Rectificacion.xml");
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
			properties.put(MOD349.LOT, lot);
			
			if (presenter != null){
				properties.put(MOD349.PRESENTER, presenter);
				if (CheckPresenter.parse(presenter,exceptions)==false)
					throw new Fd0Exception( "ABORTED: ",presenter.toString());

				createLine("Presentador",properties);
			}
			
			Iterator<Deponent> iterDep = lot.getDeponentsIterator();
			while (iterDep.hasNext()){
				Deponent deponent = iterDep.next();
				properties.put(MOD349.DEPONENT, deponent);
		
				try{
					if (CheckDeponent.parse(deponent,exceptions)==false)
						throw new Fd0Exception( "ABORTED: ",deponent.toString());
		
					createLine("Declarante",properties);
		
					Iterator<Operator> iterD = deponent.getOperatorsIterator();
					while (iterD.hasNext()){
						Operator operator = iterD.next();
						properties.put(MOD349.OPERATOR, operator);
						try{
							
							if (CheckOperator.parse(operator,exceptions)==false)
								throw new Fd0Exception( "ABORTED: ",operator.toString());
		
							createLine("Operador",properties);
						} catch (Exception ex) {
							if ( ex instanceof Fd0Exception ) {
								exceptions.add (ex);
							} 
							else {
								Fd0Exception e = new Fd0Exception( ex.getMessage(),operator.toString());
								exceptions.add (e);
							}
						}
					}
					
					Iterator<Correction> iterI = deponent.getCorrectionsIterator();
					while (iterI.hasNext()){
						Correction correction = iterI.next();
						properties.put(MOD349.CORRECTION, correction );
						try{
		
							if (CheckCorrection.parse(correction,exceptions)==false)
								throw new Fd0Exception( "ABORTED: ",correction.toString());
		
							createLine("Rectificacion",properties);
						} catch (Exception ex) {
							if ( ex instanceof Fd0Exception ) {
								exceptions.add (ex);
							} 
							else {
								Fd0Exception e = new Fd0Exception( ex.getMessage(),correction.toString());
								exceptions.add (e);
							}
						}
					}
		
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),deponent.toString());
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
	 * Deponent object ident
	 */
	private static String DEPONENT = "DEPONENT";
	/**
	 * Operator object ident
	 */
	private static String OPERATOR = "OPERATOR";
	/**
	 * Correction object ident
	 */
	private static String CORRECTION = "CORRECTION";

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		Lot lot = new Lot();
		
		Presenter presenter = new Presenter();
		presenter.setYear(2006);
		presenter.setCode("11111111H");
		presenter.setName("NAME");
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
		presenter.setPeriod("1T");

		lot.setPresenter(presenter);

		Deponent deponent = new Deponent();
		deponent.setYear(2006);
		deponent.setCode("11111111H");
		deponent.setType("C");
		deponent.setName("nombre");
		deponent.setRelPhone(945111111);
		deponent.setRelName("nombre relacionarse");
		deponent.setJustify(1234565);
		deponent.setComplementary(false);
		deponent.setReplaces(false);
		deponent.setReplacedJustify(null);
		deponent.setPeriod("1T");
		
		Operator operator = new Operator();
		operator.setCountry("DE");
		operator.setCode("111111111");
		operator.setName("NAME");
		operator.setKey("A");
		operator.setBase(12345.89);

		Correction correction = new Correction();
		correction.setCountry("D");
		correction.setCode("111111111");
		correction.setName("NAME");
		correction.setKey("A");
		correction.setYear(2005);
		correction.setPeriod("1T");
		correction.setBase(14214.0);
		correction.setPreviousBase(2134.8);
		
		deponent.addCorrection(correction);
		deponent.addOperator(operator);

		lot.addDeponents(deponent);
		
		String filePath = "c:/tmp/csb/MOD349.txt";
		FileFiller mod349 = new MOD349(lot,filePath);
		mod349.create();
	}

}
