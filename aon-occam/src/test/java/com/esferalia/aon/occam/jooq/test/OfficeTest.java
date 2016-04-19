package com.esferalia.aon.occam.jooq.test;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NoticeFilter;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO2;

public class OfficeTest {

	private static AONContext ctx;
	// private static String DOMAIN_NAME = "macayc-mac.amtzdelagos.dev";
	// private static int DOMAIN_ID = 536;
	// private static String USER_NAME = "mac";

	// private static String DOMAIN_NAME = "agroback-mac.amtzdelagos.dev";
	// private static int DOMAIN_ID = 228;
	// private static String USER_NAME = "patri";

	private static String DOMAIN_NAME = "macayc-mac.amtzdelagos.dev";
	private static int DOMAIN_ID = 536;
	private static String USER_NAME = "mac";

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException,
			SQLException, AonConnectionException {
		Class.forName(org.gjt.mm.mysql.Driver.class.getName());
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER_NAME);
	}

	@Test
	public void testGetTicketNotices() {

		NoticeFilter filter = new NoticeFilter();
		filter.setState("open");
		filter.setOffset(0);		

		filter.setTags(new String[] {});

		Date now = new Date();
		List<Notice> notices = getNotices(filter);
		String segundos = (new Date().getTime() - now.getTime()) / 1000
				+ " sec.";

		for (Notice notice : notices) {

			System.out.println("\nIncidencia: " + notice.getTitle());
			System.out.println("Company: " + notice.getCompany());
			System.out.println("Status: " + notice.getStatus());
			System.out.println("Duplicated: " + notice.getDuplicated());
			for (Tag tag : notice.getTags()) {
				System.out.println("\t Etiqueta: " + tag.getName());
				System.out.println("\t StartDate: " + tag.getStartDate());
				System.out.println("\t EndDate: " + tag.getEndDate());
				System.out.println("\t Tipo: " + tag.getType());
			}

		}
		System.out.println("==============================");
		System.out.println("Numero de incidencias: " + notices.size());
		System.out.println(segundos + " segundos");

		int size = AonHubDAO2.getSelectedCount(ctx, filter);
		System.out.println("Numero de incidencias: " + size);

	}

	private List<Notice> getNotices(NoticeFilter filter) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.getTicketNotices(ctx, filter));
	}
}
