package com.esferalia.aon.pms.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;

public class PmsAliasWriter {
	
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-pms/src/main/java/com/esferalia/aon/pms/dao/IPmsAlias.java");
		String[] classes = new String[]{
			Hotel.class.getName(),
			ProjectReservation.class.getName(),
			ProjectReservationGuest.class.getName(),
			ProjectReservationRoom.class.getName(),
			ProjectReservationService.class.getName()
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.esferalia.aon.pms.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}