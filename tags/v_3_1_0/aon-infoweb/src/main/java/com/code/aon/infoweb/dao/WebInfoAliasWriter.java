package com.code.aon.infoweb.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.infoweb.WebInfo;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.infoweb.WebInfoPageDetail;
import com.code.aon.infoweb.WebInfoPageResource;
import com.code.aon.infoweb.WebInfoStyle;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class WebInfoAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-infoweb/src/main/java/com/code/aon/infoweb/dao/IInfoWebAlias.java");
		String[] classes = new String[5]; 
		classes[0] = WebInfo.class.getName();
		classes[1] = WebInfoPage.class.getName();
		classes[2] = WebInfoPageDetail.class.getName();
		classes[3] = WebInfoPageResource.class.getName();
		classes[4] = WebInfoStyle.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.infoweb.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}