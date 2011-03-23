package com.code.aon.groupware.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.Favorite;
import com.code.aon.groupware.FavoriteCategory;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.Notice;

public class GroupWareAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-groupware/src/main/java/com/code/aon/groupware/dao/IGroupWareAlias.java");
		String[] classes = new String[] { 
			Alarm.class.getName(),
        	Favorite.class.getName(),
        	FavoriteCategory.class.getName(),
        	Note.class.getName(),
			Notice.class.getName()
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.groupware.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}

}
