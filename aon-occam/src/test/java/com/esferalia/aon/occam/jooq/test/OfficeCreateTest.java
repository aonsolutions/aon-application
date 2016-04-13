package com.esferalia.aon.occam.jooq.test;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.jooq.tables.User;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO2;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.Pair;

public class OfficeCreateTest {
	
	enum TagEnum {
		BUG(-50, "Tbug", TagType.OFFICE_TYPE),
		ERROR(-51, "Terror", TagType.OFFICE_TYPE),
		CONSULTA(-52, "Tconsulta", TagType.OFFICE_TYPE),
		
		ALTA(-53, "Talta", TagType.OFFICE_PRIORITY),
		MEDIA(-54, "Tmedia", TagType.OFFICE_PRIORITY),
		BAJA(-55, "Tbaja", TagType.OFFICE_PRIORITY),
		
		LABORAL(-56, "Tlaboral", TagType.OFFICE_NOTICE),
		FISCAL(-57, "Tfiscal", TagType.OFFICE_NOTICE),
		DESPACHO(-58, "Tdespacho", TagType.OFFICE_NOTICE),
		ENTRADA(-59, "Tentrada", TagType.OFFICE_NOTICE),
		ASESORIA(-60, "Tasesoria", TagType.OFFICE_NOTICE)
		;
		
		private int id;
		private String name;
		private TagType type;
		
		private TagEnum(int id, String name, TagType type) {
			this.id = id;
			this.name = name;
			this.type = type;
		}
		
		public int getId() {
			return this.id;
		}
		
		public int getDomain() {
			return DOMAIN_ID;
		}
		
		public String getValue() {
			return this.name;
		}
		
		public TagType getType() {
			return type;
		}
		
		public Tag getTag() {
			// @formatter:off;
			return new Tag()
					.setId(getId())
					.setName(getValue())
					.setDomain(getDomain())
					.setType(getType().value());
			// @formatter:off;
		}
	}

	private static AONContext ctx;
//	private static String DOMAIN_NAME = "inelco-mac.amtzdelagos.dev";
//	private static int DOMAIN_ID = 400;
//	private static String USER_NAME = "mac";

//	private static String DOMAIN_NAME = "agroback-mac.amtzdelagos.dev";
//	private static int DOMAIN_ID = 228;
//	private static String USER_NAME = "patri";

	private static String DOMAIN_NAME = "macayc-mac.amtzdelagos.dev";
	private static int DOMAIN_ID = 536;
	private static String USER_NAME = "mac";


	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException,
			SQLException, AonConnectionException {
		Class.forName(org.gjt.mm.mysql.Driver.class.getName());
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER_NAME);	
		createTags();
		createNotices();
	}

	@Test
	public void testCreateNotices() {
		
	}
	
	private static void createTags() {
		
		ctx.getDslContext()
			.delete(TAG)
			.where(TAG.ID.between(-100,-50))
			.execute();
		
		System.out.println("=================================================== TAGS");
		for (TagEnum tag : TagEnum.values()) {
			ctx.getDslContext()
				.insertInto(TAG)
				.set(TAG.ID, tag.getId())
				.set(TAG.DOMAIN, DOMAIN_ID)
				.set(TAG.NAME, tag.getValue())
				.set(TAG.TYPE, tag.getType().value())
				.execute();
			
			System.out.println("Etiqueta: " + tag.getValue() + " insertada correctamente");
		}
		System.out.println("=========================================================");
	}
	
	private static void createNotices() {
		ArrayList<Pair<Integer, String>> customers = ctx.getDslContext()
				.select(CUSTOMER.REGISTRY, REGISTRY.NAME)
				.from( CUSTOMER )
				.join( REGISTRY )
				.on(CUSTOMER.REGISTRY.eq(REGISTRY.ID))
				.where(CUSTOMER.DOMAIN.eq(DOMAIN_ID))
				.fetch()
				.stream()
				.map( rec -> new Pair<Integer, String>(rec.getValue(CUSTOMER.REGISTRY), rec.getValue(REGISTRY.NAME)) )
				.collect(Collectors.toCollection(ArrayList::new));
		 
		 ArrayList<Integer> userIds = ctx.getDslContext()
				.select(User.USER.ID)
				.from(User.USER)
				.where(User.USER.DOMAIN.eq(553))
				.fetch()
				.stream()
				.map(id -> id.getValue(User.USER.ID))
				.collect(Collectors.toCollection(ArrayList::new));
		
		String letters = "ABCDEFGHIJKLMNOPQRSTV";
		
		Date now = new Date();
		com.esferalia.aon.occam.api.model.security.User user = null;
		Notice notice;
		Notice create;
		for (int i = 0; i < 10000; i ++) {
			int c = (int) Math.floor( (Math.random() * customers.size() - 1 ) + 1);
			notice = new Notice();
			notice.setDomain(DOMAIN_ID);
			notice.setStartDate(getRandomDate());
			
			int ramdomIndex = (int) Math.floor((Math.random() * userIds.size() - 1) + 1 );			
			user = UserDAO.getUser(ctx, userIds.get(ramdomIndex));
			notice.setSender(user );
			
			notice.setStatus(NoticeStatus.OPEN.getValue());
			notice.setSource(AonNumberUtils.toString(customers.get(c).getLeft()));
			notice.setCompany(customers.get(c).getRight());
			notice.setTitle(AonRandomStringUtils.random(letters.length(),letters));
			create = addNotice(notice);
			
			List<Tag> tags = new LinkedList<Tag>();			
			Random rd = new Random();
			Tag typeRandomTag = TagEnum.values()[rd.nextInt(3)].getTag();
			typeRandomTag.setUser(user);
			tags.add(typeRandomTag);
			
			Tag priorityRandomTag = TagEnum.values()[rd.nextInt(3) + 3].getTag();
			priorityRandomTag.setUser(user);
			tags.add(priorityRandomTag);
			
			Tag noticeRandomTag = TagEnum.values()[rd.nextInt(5) + 6].getTag();
			noticeRandomTag.setUser(user);
			tags.add(noticeRandomTag);			
			
			addLabel2Notice(create.getId(), tags);
			
			if (i % 10 == 0) System.out.print(".");
		}
		System.out.println( (new Date().getTime() - now.getTime()) / 1000 + " sec."  );
	}
	
	private static Notice addNotice(final Notice notice) {
		return ctx.getDslContext().transactionResult( 
				conf -> AonHubDAO2.insertNotice(ctx, notice));
	}
	
	private static Notice addLabel2Notice(int id, List<Tag> tags) {
		return ctx.getDslContext().transactionResult( conf -> AonHubDAO2
				.addLabelsToAnIssue(ctx, id, tags));
	}
	
	private static Date getRandomDate() {
		
		long offset = Timestamp.valueOf("2015-01-01 00:00:00").getTime();
		long end = new Date().getTime();		
		long diff = end - offset + 1;
		Timestamp rand = new Timestamp(offset + (long) (Math.random() * diff));
		
		return new Date(rand.getTime());
	}
}
