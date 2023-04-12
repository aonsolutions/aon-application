package com.esferalia.aon.in.payroll.ivl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TimeZone;

import org.jooq.DSLContext;

import com.esferalia.aon.in.payroll.VidaLaboral2AON;
import com.esferalia.aon.occam.api.AONContext;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;



public class TransactionTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "jdbc:mysql://127.0.0.1:3306/test-aonsolutions-org";
//		String properties = "{password=serubd2000, domain=payroll.aonsolutions.org, serverTimezone=Europe/Madrid, user=dbuser, useSSL=false}";
        Properties properties = new Properties();
        properties.setProperty("user", "dbuser");
        properties.setProperty("password","serubd2000" );
        properties.setProperty("useSSL", "false");
        properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
        properties.setProperty("domain", "payroll.aonsolutions.org");


		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, properties);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AONContext aonContext = new AONContext(connection);
		DSLContext dslContext = aonContext.getDslContext();
		dslContext.transaction(ctx ->{
			dslContext.insertInto(DOMAIN)
			.set(DOMAIN.NAME,"assimilated-payroll-test.aonsolutions.--org" )
			.set(DOMAIN.DESCRIPTION, "ASIMILADOS")
			.set(DOMAIN.PARENT, 8776)
			.set(DOMAIN.SCOPE, 3535)
			.set(DOMAIN.OWNER, "admin")
			.execute();
			

		});
		
//		dslContext.transaction(ctx ->{
//			dslContext.insertInto(ENTERPRISE)
//			.set(ENTERPRISE.REGISTRY,0)
//			.set(ENTERPRISE.DOMAIN,0)
//			.set(ENTERPRISE.SCOPE, 3535)
//			.execute(); 
//		});
	}

}
