package com.esferalia.aon.occam.jooq.test;

import java.sql.SQLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NoticeFilter;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO2;

public class OfficeTest {


	private static AONContext ctx;
//	private static String DOMAIN_NAME = "agroback-mac.amtzdelagos.dev";
	private static String DOMAIN_NAME = "macayc-mac.amtzdelagos.dev";
//	private static int DOMAIN_ID = 228;
	private static int DOMAIN_ID = 536;
	private static String USER_NAME = "mac";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException,
			SQLException, AonConnectionException {
		Class.forName(org.gjt.mm.mysql.Driver.class.getName());
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER_NAME);		
	}

	@Test
	public void testCreateNotices() {		
		
		NoticeFilter filter = new NoticeFilter();
		filter.setState("all");
		filter.setTags(new String[] {			
			
		});		
		filter.setOffset(0);

		List<Notice> notices = AonHubDAO2.getTicketNotices(ctx, filter);
		
		for (Notice notice : notices) {			
			System.out.println("\nIncidencia: " + notice.getTitle());
			System.out.println("Company: " + notice.getCompany());
			for (Tag tag : notice.getTags()) {
				System.out.println("\t Etiqueta: " + tag.getName());
				System.out.println("\t StartDate: " + tag.getStartDate());
				System.out.println("\t EndDate: " + tag.getEndDate());
				System.out.println("\t Tipo: " + tag.getType());
			}
		}
		System.out.println("==============================");
		System.out.println("Numero de incidencias: " + notices.size());
		
		List<User> users = AonHubDAO2.fillUsersFromNotices(ctx, 553);
		
		for (User user: users) {
			System.out.println(user.getName());
		}
	
	}
	
}
