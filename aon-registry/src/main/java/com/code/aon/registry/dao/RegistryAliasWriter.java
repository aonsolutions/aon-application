package com.code.aon.registry.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.registry.Category;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.RegistryRelationship;
import com.code.aon.registry.Relationship;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class RegistryAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-registry/src/main/java/com/code/aon/registry/dao/IRegistryAlias.java");
		String[] classes = new String[10]; 
		classes[0] = Category.class.getName();
		classes[1] = Registry.class.getName();
		classes[2] = RegistryAddress.class.getName();
		classes[3] = RegistryAttachment.class.getName();
		classes[4] = RegistryMedia.class.getName();
		classes[5] = Relationship.class.getName();
		classes[6] = RegistryRelationship.class.getName();
		classes[7] = RegistryNote.class.getName();
		classes[8] = RegistryDirStaff.class.getName();
		classes[9] = RecordData.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.registry.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}