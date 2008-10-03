package com.code.aon.geozone.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.geozone.GeoTree;
import com.code.aon.geozone.GeoZone;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class GeoZoneAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-geozone/src/main/java/com/code/aon/geozone/dao/IGeoZoneAlias.java");
		String[] classes = new String[2]; 
		classes[0] = GeoTree.class.getName();
		classes[1] = GeoZone.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.geozone.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}