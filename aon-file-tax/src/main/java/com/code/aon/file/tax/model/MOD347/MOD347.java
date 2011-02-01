package com.code.aon.file.tax.model.MOD347;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD347.check.CheckBuilding;
import com.code.aon.file.tax.model.MOD347.check.CheckDeclared;
import com.code.aon.file.tax.model.MOD347.check.CheckDeponent;
import com.code.aon.file.tax.model.MOD347.data.Building;
import com.code.aon.file.tax.model.MOD347.data.Declared;
import com.code.aon.file.tax.model.MOD347.data.Deponent;

public class MOD347  extends AbstractFileFiller{

	private static String DEPONENT = "DEPONENT";
	private static String DECLARED = "DECLARED";
	private static String BUILDING = "BUILDING";
	
	private Deponent deponent;
	
	public MOD347(Deponent deponent, MOD347Format format,String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		if (deponent == null)  {
			throw new IllegalArgumentException("Deponent can not be null!");
		}
		if (format == null)  {
			throw new IllegalArgumentException("Format can not be null!");
		}
		
		this.deponent = deponent;
		
		InputStream input = MOD347.class.getResourceAsStream(format.getDeponentMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD347.class.getResourceAsStream(format.getDeclaredMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD347.class.getResourceAsStream(format.getBuildingMetadataResource());
		DiskRegisterLoader.load(input, manager);

	}

	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(MOD347.DEPONENT, deponent);
			
			if (CheckDeponent.parse(deponent,exceptions)==false) {
				throw new Fd0Exception( "ABORTED: ",deponent.toString());
			}
			
			createLine("Declarante",properties);

			for (Declared declared: deponent.getDeclareds()){
				properties.put(MOD347.DECLARED, declared);
				try{
					if (CheckDeclared.parse(declared,exceptions)==false) {
						throw new Fd0Exception( "ABORTED: ",declared.toString());
					}
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
			
			for (Building building:deponent.getBuildings()){
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
	
}
