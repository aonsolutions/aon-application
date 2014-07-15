package com.esferalia.aon.gwt.fiscal.server.file;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;
import com.esferalia.aon.gwt.fiscal.server.mod200.Mod200File;

public class MOD200  extends AbstractFileFiller{

	private Mod200File mod200File;
	
	public MOD200(Mod200File mod200File, MOD200Format format, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (mod200File == null)  {
			throw new IllegalArgumentException("Mod200File can not be null!");
		}
		if (format == null)  {
			throw new IllegalArgumentException("Format can not be null!");
		}
		
		this.mod200File = mod200File;
		for (String resource : format.getResources() ) {
			InputStream input = MOD200.class.getResourceAsStream(resource);
			DiskRegisterLoader.load(input, manager);
		}
	}

	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put("MOD200", mod200File);
			
			if ( !isValid(mod200File)) {
				throw new Fd0Exception( "ERROR: ",mod200File.toString());
			}
			
			createLine("DP200000_START",properties);
			createLine("DP200001",properties);
			createLine("DP200002",properties);
			createLine("DP200003",properties);
			createLine("DP200004",properties);
			createLine("DP200005",properties);
			createLine("DP200006",properties);
			createLine("DP200007",properties);
			createLine("DP200008",properties);
			createLine("DP200009",properties);
			createLine("DP200010",properties);
			createLine("DP200011",properties);
			createLine("DP200012",properties);
			createLine("DP200013",properties);
			createLine("DP200014",properties);
			createLine("DP200015",properties);
			createLine("DP200016",properties);
			createLine("DP200017",properties);
			createLine("DP200018",properties);
			createLine("DP200018B",properties);
//			createLine("DP200024",properties);
			createLine("DP200DID",properties);
			createLine("DP200000_END",properties);

		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} 
			else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),mod200File.toString());
				exceptions.add (e);
			}
		}
		output.flush();
		writeErrorsFile();
		return exceptions;
	}


	private boolean isValid(Mod200File mod200File) {
		return true;
	}

	
}
