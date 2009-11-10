package com.code.aon.registry.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.registry.Category;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.RegistryRelationship;
import com.code.aon.registry.RegistrySegment;
import com.code.aon.registry.Relationship;
import com.code.aon.registry.Segment;

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
		String[] classes = new String[] { 
			Category.class.getName(),
			RecordData.class.getName(),
			Registry.class.getName(),
			RegistryAddInfo.class.getName(),
			RegistryAddress.class.getName(),
			RegistryAttachment.class.getName(),
			RegistryBank.class.getName(),
			RegistryDirStaff.class.getName(),
			RegistryMedia.class.getName(),
			RegistryNote.class.getName(),
			RegistryPayMethod.class.getName(),
			RegistryRelationship.class.getName(),
			RegistrySegment.class.getName(),
			Relationship.class.getName(),
			Segment.class.getName() };
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.registry.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}