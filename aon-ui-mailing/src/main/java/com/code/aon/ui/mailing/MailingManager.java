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
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.IRegistry;

public class MailingManager {

	private static final String FILENAME = "mailing.txt";
	
	public static List<MailData> generateMailingList(Collection<? extends IRegistry> collection) throws ManagerBeanException {
		List<MailData> list = new LinkedList<MailData>();
		for( IRegistry iRegistry : collection ) {
			list.add( new MailData(iRegistry.getRegistry()) );
		}
		return list;
	}
	
	public static void generateMailing(List<MailData> list, Writer writer) {
		PrintWriter pw = new PrintWriter(writer);

    	pw.print(parseStructure());
		pw.println();
		for( MailData data : list ) {
        	pw.print(data.toString());
			pw.println();			
		}
		pw.flush();
	}

	public static void generateMailing(List<MailData> list) throws IOException {
		File file = File.createTempFile("mailing", ".txt");
		Writer writer = new FileWriter(file);
		generateMailing(list, writer);
		writer.close();
        
		FacesContext faces = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
		response.setContentType("aplication/disk");
		response.setHeader("content-disposition", "attachment;filename=\"" + FILENAME + "\"");
		BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		IOUtils.copy(bis, bos);
		bos.flush();
		IOUtils.closeQuietly(bos);
		IOUtils.closeQuietly(bis);
		faces.responseComplete();
	}	
	
	private static String parseStructure() {
		return "ID,DOCUMENT,NAME,SURNAME,ADDRESS,CITY,ZIP,GEOZONE,PHONE,CELLULAR,FAX,EMAIL";
	}

}
