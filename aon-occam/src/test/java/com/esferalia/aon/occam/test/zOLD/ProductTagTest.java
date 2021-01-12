package com.esferalia.aon.occam.test.zOLD;


import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.mysql.jdbc.Driver;

import net.aonsolutions.core.pool.AonConnectionException;


public class ProductTagTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "sig.aonsolutions.es";
	private static Integer DOMAIN_ID = 5;
	private static String LOGIN = "jgarcia";
	
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,LOGIN);
	}
	
	// @Test
	public void test1() throws IOException {
		LinkedHashMap<Integer, String[]> map = ProductDAO.getProductTagMap(ctx);
		for (Integer i : map.keySet() ) {
			System.out.print( i + " -- ");
			for ( String tag : map.get(i)) {
				System.out.print( tag + " ");	
			}
			System.out.println();
		}
		
	}
	
}
