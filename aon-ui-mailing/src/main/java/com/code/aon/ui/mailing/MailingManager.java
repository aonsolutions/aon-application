package com.code.aon.ui.mailing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;

public class MailingManager {

	private static final String SEPARATOR = ",";
	private static final String FILENAME = "mailing.txt";
	
	public static void generateMailing(Collection<? extends IRegistry> collection, Writer writer) throws ManagerBeanException {
		PrintWriter pw = new PrintWriter(writer);

    	pw.print(parseStructure());
		pw.println();
		for( IRegistry iRegistry : collection ) {
        	pw.print(parseLine(iRegistry.getRegistry()));
			pw.println();			
		}
		pw.flush();
	}

	public static void generateMailing(Collection<? extends IRegistry> collection) throws ManagerBeanException {
		String filename = getFicheroDestino();
		try {
			Writer writer = new FileWriter(new File(filename));
			generateMailing(collection, writer);
			writer.close();
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
        
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			response.setContentType("aplication/disk");
			response.setHeader("content-disposition", "attachment;filename=\"" + FILENAME + "\"");
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
			IOUtils.copy(bis, bos);
			bos.flush();
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(bis);
			faces.responseComplete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	private static String getFicheroDestino() {
		return getTmpPath()+FILENAME;
	}

	private static String getTmpPath() {
		String path = System.getProperty("java.io.tmpdir");
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		return path;
	}
	
	private static String parseStructure() {
		return "ID,DOCUMENT,NAME,SURNAME,ADDRESS,CITY,ZIP,GEOZONE,PHONE,CELLULAR,FAX,EMAIL";
	}

	private static String parseLine(Registry registry) throws ManagerBeanException {
		String line;
    	line = "\"" + parseValue(registry.getId()) + "\"" + SEPARATOR;
    	line += "\"" + parseValue(registry.getDocument()) + "\"" + SEPARATOR;
    	line += "\"" + parseValue(registry.getName()) + "\"" + SEPARATOR;
    	line += "\"" + parseValue(registry.getSurname()) + "\"" + SEPARATOR;

    	RegistryAddress rAddress = registry.getDefaultAddress();
    	if (rAddress==null){
        	line += SEPARATOR;
        	line += SEPARATOR;
        	line += SEPARATOR;
        	line += SEPARATOR;
    	}else{
        	String address = "";
        	address += parseValue(rAddress.getAddress());
        	address += parseValue(rAddress.getAddress2());
        	address += parseValue(rAddress.getAddress3());
        	line += "\"" + address + "\"" + SEPARATOR;
        	line += "\"" + parseValue(rAddress.getCity()) + "\"" + SEPARATOR;
        	line += "\"" + parseValue(rAddress.getZip()) + "\"" + SEPARATOR;
        	if (rAddress.getGeozone() != null) {
        		line += "\"" + parseValue(rAddress.getGeozone().getName()) + "\"" + SEPARATOR;
        	}else{
            	line += SEPARATOR;
        	}
    	}

    	RegistryMedia rPhone = registry.getPhone();
    	if (rPhone != null) {
    		line += "\"" + parseValue(rPhone.getValue()) + "\"" + SEPARATOR;
    	}else{
        	line += SEPARATOR;
    	}

    	RegistryMedia rCellular = registry.getCellular();
    	if (rCellular != null) {
    		line += "\"" + parseValue(rCellular.getValue()) + "\"" + SEPARATOR;
    	}else{
        	line += SEPARATOR;
    	}

    	RegistryMedia rFax = registry.getFax();
    	if (rFax != null) {
    		line += "\"" + parseValue(rFax.getValue()) + "\"" + SEPARATOR;
    	}else{
        	line += SEPARATOR;
    	}

    	RegistryMedia rEmail = registry.getEmail();
    	if (rEmail != null) {
    		line += "\"" + parseValue(rEmail.getValue()) + "\"";
    	}
    	return line;
	}
	
	private static String parseValue(Object value){
		try{
			return value.toString();
		}catch (Exception e) {
			return "";
		}
	}
}
