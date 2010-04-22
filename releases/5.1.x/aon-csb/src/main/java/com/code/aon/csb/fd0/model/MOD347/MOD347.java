package com.code.aon.csb.fd0.model.MOD347;

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
import com.code.aon.csb.fd0.model.MOD347.check.CheckBuilding;
import com.code.aon.csb.fd0.model.MOD347.check.CheckDeclared;
import com.code.aon.csb.fd0.model.MOD347.check.CheckDeponent;
import com.code.aon.csb.fd0.model.MOD347.data.Building;
import com.code.aon.csb.fd0.model.MOD347.data.Declared;
import com.code.aon.csb.fd0.model.MOD347.data.Deponent;
import com.code.aon.csb.fd0.model.MOD347.xml.XMLLoader;

/**
 * 347 format file creator
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class MOD347  extends AbstractFileFiller{

	/**
	 * The main data object
	 */
	private Deponent deponent;
	
	/**
	 * Constructor for this path and this data
	 * Loads all registry formats
	 * 
	 * @param deponent the deponent data object
	 * @param filePath the file path
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public MOD347(Deponent deponent, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		this.deponent = deponent;
		
		InputStream input = XMLLoader.class.getResourceAsStream("Declarante.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Declarado.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Inmueble.xml");
		DiskRegisterLoader.load(input, manager);

	}

	/**
	 * Check all data and creates the lines.
	 * 
	 * @see com.code.aon.csb.fd0.model.FileFiller#create()
	 */
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(MOD347.DEPONENT, deponent);
			
			if (CheckDeponent.parse(deponent,exceptions)==false)
				throw new Fd0Exception( "ABORTED: ",deponent.toString());
			
			createLine("Declarante",properties);

			Iterator<Declared> iterD = deponent.getDeclaredsIterator();
			while (iterD.hasNext()){
				Declared declared = iterD.next();
				properties.put(MOD347.DECLARED, declared);
				try{
					if (CheckDeclared.parse(declared,exceptions)==false)
						throw new Fd0Exception( "ABORTED: ",declared.toString());
					
					createLine("Declarado",properties);
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),declared.toString());
						exceptions.add (e);
					}
				}
			}
			
			Iterator<Building> iterI = deponent.getBuildingsIterator();
			while (iterI.hasNext()){
				Building building = iterI.next();
				properties.put(MOD347.BUILDING, building );
				try{
					if (CheckBuilding.parse(building,exceptions)==false)
						throw new Fd0Exception( "ABORTED: ",building.toString());
					
					createLine("Inmueble",properties);
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),building.toString());
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
		output.flush();
		writeErrorsFile();
		return exceptions;
	}

	/**
	 * Deponent object ident
	 */
	private static String DEPONENT = "DEPONENT";
	/**
	 * Declared object ident
	 */
	private static String DECLARED = "DECLARED";
	/**
	 * Building object ident
	 */
	private static String BUILDING = "BUILDING";
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		Deponent deponent = new Deponent();
		deponent.setYear(2006);
		deponent.setCode("00000000H");
		deponent.setType("C");
		deponent.setName("NAME");
		deponent.setRelPhone(945121010);
		deponent.setRelName("REL NAME");
		deponent.setJustify(123456789);
		deponent.setComplementary(false);
		deponent.setReplaces(true);
		deponent.setReplacedJustify(123456789);
		
		Declared declared = new Declared();
		declared.setCode("11111111G");
		declared.setManagerCode(null);
		declared.setName("NAME RRRRR");
		declared.setProvince(01);
		declared.setCountry(333);
		declared.setKey("A");
		declared.setQuantity(123.78);
		declared.setInsurance(true);
		//declared.setRenting(true);
		
		deponent.addDeclared(declared);

		Building building = new Building();
		building.setCode("22222222J");
		building.setManagerCode(null);
		building.setName("NAME FFFFF");
		building.setQuantity(2123.67);
		building.setCadastre("DSAD11");
		building.setProvince(01);
		building.setCounty("ALAVA");
		building.setStreetType("CL");
		building.setStreetName("NOMBRE CALLE");
		building.setStreetNumber(12);
		building.setStreetStair("A");
		building.setStreetStorey("1");
		building.setStreetDoor("D");
		
		deponent.addBuilding(building);
		
		String filePath = "c:/tmp/csb/MOD347.txt";
		FileFiller mod347 = new MOD347(deponent,filePath);
		mod347.create();
	}

}
