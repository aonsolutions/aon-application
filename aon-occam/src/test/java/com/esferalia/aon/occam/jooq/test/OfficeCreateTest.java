package com.esferalia.aon.occam.jooq.test;

import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class OfficeCreateTest {
	
	enum TagEnum {
		BUG("test-bug"),
		ERROR("test-error"),
		CONSULTA("test-consulta"),
		
		ALTA("test-alta"),
		MEDIA("test-media"),
		BAJA("test-baja"),
		
		LABORAL("test-laboral"),
		FISCAL("test-fiscal"),
		DESPACHO("test-despacho"),
		ENTRADA("test-entrada"),
		ASESORIA("test-asesoria")
		;
		
		private String name;
		
		private TagEnum(String name) {
			this.name = name;
		}
		
		public String getValue() {
			return this.name;
		}
	}

	private static AONContext ctx;
	private static String DOMAIN_NAME = "agroback-mac.amtzdelagos.dev";
	private static int DOMAIN_ID = 228;
	private static String USER_NAME = "mac";
	private static int USER_ID = 1651;
	private static User user;
	
	private static List<Tag> tags = new LinkedList<Tag>();

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException,
			SQLException, AonConnectionException {
		Class.forName(org.gjt.mm.mysql.Driver.class.getName());
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER_NAME);		
		createTags();
		createNotices();
	}
	
	@AfterClass
	public static void afterClass() {

	}

	@Test
	public void testCreateNotices() {
		
	}
	
	private static void createTags() {
		
		// -------------------- TYPE
		
		Tag bug = new Tag();
		bug.setDomain(DOMAIN_ID);
		bug.setName(TagEnum.BUG.getValue());
		bug.setType(TagType.OFFICE_TYPE.value());
		insertTag(bug);

		Tag error = new Tag();
		error.setDomain(DOMAIN_ID);
		error.setName(TagEnum.ERROR.getValue());
		error.setType(TagType.OFFICE_TYPE.value());
		insertTag(error);
		
		Tag consulta = new Tag();
		consulta.setDomain(DOMAIN_ID);
		consulta.setName(TagEnum.CONSULTA.getValue());
		consulta.setType(TagType.OFFICE_TYPE.value());
		insertTag(consulta);

		// -------------------- PRIORIDAD	
		
		Tag alta = new Tag();
		alta.setDomain(DOMAIN_ID);
		alta.setName(TagEnum.ALTA.getValue());
		alta.setType(TagType.OFFICE_PRIORITY.value());
		insertTag(alta);
		
		Tag media = new Tag();
		media.setDomain(DOMAIN_ID);
		media.setName(TagEnum.MEDIA.getValue());
		media.setType(TagType.OFFICE_PRIORITY.value());
		insertTag(media);

		Tag baja = new Tag();
		baja.setDomain(DOMAIN_ID);
		baja.setName(TagEnum.BAJA.getValue());
		baja.setType(TagType.OFFICE_PRIORITY.value());
		insertTag(baja);

		// -------------------- OFFICE		

		Tag laboral = new Tag();
		laboral.setDomain(DOMAIN_ID);
		laboral.setName(TagEnum.LABORAL.getValue());
		laboral.setType(TagType.OFFICE_NOTICE.value());
		insertTag(laboral);

		Tag fiscal = new Tag();
		fiscal.setDomain(DOMAIN_ID);
		fiscal.setName(TagEnum.FISCAL.getValue());
		fiscal.setType(TagType.OFFICE_NOTICE.value());
		insertTag(fiscal);
		
		Tag asesoria = new Tag();
		asesoria.setDomain(DOMAIN_ID);
		asesoria.setName(TagEnum.ASESORIA.getValue());
		asesoria.setType(TagType.OFFICE_NOTICE.value());
		insertTag(asesoria);

		Tag despacho = new Tag();
		despacho.setDomain(DOMAIN_ID);
		despacho.setName(TagEnum.DESPACHO.getValue());
		despacho.setType(TagType.OFFICE_NOTICE.value());
		insertTag(despacho);
		
		Tag entrada = new Tag();
		entrada.setDomain(DOMAIN_ID);
		entrada.setName(TagEnum.ENTRADA.getValue());
		entrada.setType(TagType.OFFICE_NOTICE.value());
		insertTag(entrada);
	}
	
	private static void insertTag(Tag tag) {
		
		try {
			int id = ctx.getDslContext()
					.insertInto(TAG)
					.set(TAG.DOMAIN, tag.getDomain())
					.set(TAG.NAME, tag.getName())
					.set(TAG.TYPE, tag.getType())
					.returning()
					.fetchOne()
					.getValue(TAG.ID);
			tag.setId(id);
			tags.add(tag);
			System.out.println("La etiqueta " + tag.getName() + " insertada correctamente");
			System.out.println("=========================================================");
			
		} catch (Exception ex) {
			System.out.println("La etiqueta " + tag.getName() + " no se ha insertado");
		}
	}
	
	private static void createNotices() {
		
		// ---------------------------- NOTICE 1 OPEN
		
		Notice notice1 = new Notice();
		notice1.setDomain(DOMAIN_ID);
		notice1.setStartDate(new Date());
		notice1.setSender(getUser());
		notice1.setSource(String.valueOf(51884));
		notice1.setCompany("About Social Movement SL");
		notice1.setTitle("test-Title notice 1");
		
		notice1.addTag(getTagByName(TagEnum.ERROR.getValue(), TagType.OFFICE_TYPE.value()));
		notice1.addTag(getTagByName(TagEnum.ALTA.getValue(), TagType.OFFICE_PRIORITY.value()));
		notice1.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));
		notice1.addTag(getTagByName(TagEnum.FISCAL.getValue(), TagType.OFFICE_NOTICE.value()));
		
		notice1.addNotice(buildComment(notice1));
		notice1.addNotice(buildComment(notice1));
		insertNotice(notice1);

		// ---------------------------- NOTICE 2 OPEN
		
		Notice notice2 = new Notice();
		notice2.setDomain(DOMAIN_ID);
		notice2.setStartDate(new Date());
		notice2.setSender(getUser());
		notice2.setSource(String.valueOf(51884));
		notice2.setCompany("About Social Movement SL");
		notice2.setTitle("test-Title notice 2");
		
		notice2.addTag(getTagByName(TagEnum.ERROR.getValue(), TagType.OFFICE_TYPE.value()));
		notice2.addTag(getTagByName(TagEnum.ALTA.getValue(), TagType.OFFICE_PRIORITY.value()));
		notice2.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));
		notice2.addTag(getTagByName(TagEnum.FISCAL.getValue(), TagType.OFFICE_NOTICE.value()));
		notice2.addNotice(buildComment(notice2));
		notice2.addNotice(buildComment(notice2));
		insertNotice(notice2);

		// ---------------------------- NOTICE 3 OPEN
		
		Notice notice3 = new Notice();
		notice3.setDomain(DOMAIN_ID);
		notice3.setStartDate(new Date());
		notice3.setSender(getUser());
		notice3.setSource(String.valueOf(51884));
		notice3.setCompany("About Social Movement SL");
		notice3.setTitle("test-Title notice 3");
		
		notice3.addTag(getTagByName(TagEnum.ERROR.getValue(), TagType.OFFICE_TYPE.value()));
		notice3.addTag(getTagByName(TagEnum.ALTA.getValue(), TagType.OFFICE_PRIORITY.value()));
		notice3.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));
		notice3.addTag(getTagByName(TagEnum.FISCAL.getValue(), TagType.OFFICE_NOTICE.value()));
		
		notice3.addNotice(buildComment(notice3));
		notice3.addNotice(buildComment(notice3));
		insertNotice(notice3);
		
		// ---------------------------- NOTICE 4 OPEN
		
		Notice notice4 = new Notice();
		notice4.setDomain(DOMAIN_ID);
		notice4.setStartDate(new Date());
		notice4.setSender(getUser());
		notice4.setSource(String.valueOf(116056));
		notice4.setCompany("Aldecor Servicios Integrales, S.L.");
		notice4.setTitle("test-Title notice 4");
		
		notice4.addTag(getTagByName(TagEnum.BUG.getValue(), TagType.OFFICE_TYPE.value()));
		notice4.addTag(getTagByName(TagEnum.MEDIA.getValue(), TagType.OFFICE_PRIORITY.value()));
		notice4.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));
		notice4.addTag(getTagByName(TagEnum.FISCAL.getValue(), TagType.OFFICE_NOTICE.value()));
		notice4.addTag(getTagByName(TagEnum.DESPACHO.getValue(), TagType.OFFICE_NOTICE.value()));
		notice4.addTag(getTagByName(TagEnum.ENTRADA.getValue(), TagType.OFFICE_NOTICE.value()));
		
		notice4.addNotice(buildComment(notice4));
		notice4.addNotice(buildComment(notice4));
		insertNotice(notice4);
		
		// ---------------------------- NOTICE 5 OPEN
		
		Notice notice5 = new Notice();
		notice5.setDomain(DOMAIN_ID);
		notice5.setStartDate(new Date());
		notice5.setSender(getUser());
		notice5.setSource(String.valueOf(116056));
		notice5.setCompany("Aldecor Servicios Integrales, S.L.");
		notice5.setTitle("test-Title notice 5");
		
		notice5.addTag(getTagByName(TagEnum.BUG.getValue(), TagType.OFFICE_TYPE.value()));
		notice5.addTag(getTagByName(TagEnum.MEDIA.getValue(), TagType.OFFICE_PRIORITY.value()));
		notice5.addTag(getTagByName(TagEnum.ENTRADA.getValue(), TagType.OFFICE_NOTICE.value()));
		notice5.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));
		
		notice5.addNotice(buildComment(notice5));
		notice5.addNotice(buildComment(notice5));
		insertNotice(notice5);
		
		// ---------------------------- NOTICE 6 OPEN
		
		Notice notice6 = new Notice();
		notice6.setDomain(DOMAIN_ID);
		notice6.setStartDate(new Date());
		notice6.setSender(getUser());
		notice6.setSource(String.valueOf(116056));
		notice6.setCompany("Aldecor Servicios Integrales, S.L.");
		notice6.setTitle("test-Title notice 6");
		
		notice6.addTag(getTagByName(TagEnum.CONSULTA.getValue(), TagType.OFFICE_TYPE.value()));
		notice6.addTag(getTagByName(TagEnum.BAJA.getValue(), TagType.OFFICE_PRIORITY.value()));
		notice6.addTag(getTagByName(TagEnum.DESPACHO.getValue(), TagType.OFFICE_NOTICE.value()));	
		
		notice6.addNotice(buildComment(notice6));
		notice6.addNotice(buildComment(notice6));
		insertNotice(notice6);
		
		// ---------------------------- NOTICE 7 OPEN
		
		Notice notice7 = new Notice();
		notice7.setDomain(DOMAIN_ID);
		notice7.setStartDate(new Date());
		notice7.setSender(getUser());
		notice7.setSource(String.valueOf(116056));
		notice7.setCompany("B4ing Ingeniería, Tecnología y Gestión, sl");
		notice7.setTitle("test-Title notice 7");
		
		notice7.addTag(getTagByName(TagEnum.ERROR.getValue(), TagType.OFFICE_TYPE.value()));
		notice7.addTag(getTagByName(TagEnum.ALTA.getValue(), TagType.OFFICE_PRIORITY.value()));
		notice7.addTag(getTagByName(TagEnum.DESPACHO.getValue(), TagType.OFFICE_NOTICE.value()));
		notice7.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));
		notice7.addTag(getTagByName(TagEnum.FISCAL.getValue(), TagType.OFFICE_NOTICE.value()));
		
		notice7.addNotice(buildComment(notice7));
		notice7.addNotice(buildComment(notice7));
		insertNotice(notice7);
		
		// ---------------------------- NOTICE 8 OPEN
		
		Notice notice8 = new Notice();
		notice8.setDomain(DOMAIN_ID);
		notice8.setStartDate(new Date());
		notice8.setSender(getUser());
		notice8.setSource(String.valueOf(51422));
		notice8.setCompany("AGROBAK 2009 SL");
		notice8.setTitle("test-Title notice 8");
		
		notice8.addTag(getTagByName(TagEnum.BUG.getValue(), TagType.OFFICE_TYPE.value()));
		notice8.addTag(getTagByName(TagEnum.ALTA.getValue(), TagType.OFFICE_PRIORITY.value()));		
		notice8.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));
		notice8.addTag(getTagByName(TagEnum.FISCAL.getValue(), TagType.OFFICE_NOTICE.value()));
		
		notice8.addNotice(buildComment(notice8));
		notice8.addNotice(buildComment(notice8));
		insertNotice(notice8);
		
		// ---------------------------- NOTICE 9 OPEN
		
		Notice notice9 = new Notice();
		notice9.setDomain(DOMAIN_ID);
		notice9.setStartDate(new Date());
		notice9.setSender(getUser());
		notice9.setSource(String.valueOf(51760));
		notice9.setCompany("ALAYN RODRIGUEZ ARTES");
		notice9.setTitle("test-Title notice 9");
		
		notice9.addTag(getTagByName(TagEnum.CONSULTA.getValue(), TagType.OFFICE_TYPE.value()));
		notice9.addTag(getTagByName(TagEnum.BAJA.getValue(), TagType.OFFICE_PRIORITY.value()));		
		notice9.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));
		notice9.addTag(getTagByName(TagEnum.FISCAL.getValue(), TagType.OFFICE_NOTICE.value()));
		notice9.addTag(getTagByName(TagEnum.DESPACHO.getValue(), TagType.OFFICE_NOTICE.value()));
		notice9.addTag(getTagByName(TagEnum.ENTRADA.getValue(), TagType.OFFICE_NOTICE.value()));
		
		notice9.addNotice(buildComment(notice9));
		notice9.addNotice(buildComment(notice9));
		insertNotice(notice9);
		
		// ---------------------------- NOTICE 10 OPEN
		
		Notice notice10 = new Notice();
		notice10.setDomain(DOMAIN_ID);
		notice10.setStartDate(new Date());
		notice10.setSender(getUser());
		notice10.setSource(String.valueOf(175547));
		notice10.setCompany("Mobile Dreams Consulting SL");
		notice10.setTitle("test-Title notice 10");
		
		notice10.addTag(getTagByName(TagEnum.BUG.getValue(), TagType.OFFICE_TYPE.value()));
		notice10.addTag(getTagByName(TagEnum.MEDIA.getValue(), TagType.OFFICE_PRIORITY.value()));		
		notice10.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));		
		notice10.addTag(getTagByName(TagEnum.DESPACHO.getValue(), TagType.OFFICE_NOTICE.value()));
		notice10.addTag(getTagByName(TagEnum.ENTRADA.getValue(), TagType.OFFICE_NOTICE.value()));
		
		notice10.addNotice(buildComment(notice10));
		notice10.addNotice(buildComment(notice10));
		insertNotice(notice10);

		// ---------------------------- NOTICE 11 CLOSED
		
		Notice notice11 = new Notice();
		notice11.setDomain(DOMAIN_ID);
		notice11.setStartDate(new Date());
		notice11.setSender(getUser());
		notice11.setSource(String.valueOf(127104));
		notice11.setCompany("Alberto Tablado Linares");
		notice11.setTitle("test-Title notice 11");
		
		notice11.addTag(getTagByName(TagEnum.BUG.getValue(), TagType.OFFICE_TYPE.value()));
		notice11.addTag(getTagByName(TagEnum.MEDIA.getValue(), TagType.OFFICE_PRIORITY.value()));		
		notice11.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));		
		notice11.addTag(getTagByName(TagEnum.DESPACHO.getValue(), TagType.OFFICE_NOTICE.value()));
		
		notice11.addNotice(buildComment(notice11));
		notice11.addNotice(buildComment(notice11));
		Notice auxN11 = insertNotice(notice11);
		auxN11.setStatus(NoticeStatus.CLOSED.getValue());
		changeNoticeStatus(auxN11);

		// ---------------------------- NOTICE 12 CLOSED
		
		Notice notice12 = new Notice();
		notice12.setDomain(DOMAIN_ID);
		notice12.setStartDate(new Date());
		notice12.setSender(getUser());
		notice12.setSource(String.valueOf(127104));
		notice12.setCompany("Alberto Tablado Linares");
		notice12.setTitle("test-Title notice 12");
		
		notice12.addTag(getTagByName(TagEnum.BUG.getValue(), TagType.OFFICE_TYPE.value()));
		notice12.addTag(getTagByName(TagEnum.ALTA.getValue(), TagType.OFFICE_PRIORITY.value()));		
		notice12.addTag(getTagByName(TagEnum.LABORAL.getValue(), TagType.OFFICE_NOTICE.value()));		
		
		notice12.addNotice(buildComment(notice12));
		notice12.addNotice(buildComment(notice12));
		Notice auxN12 = insertNotice(notice12);
		auxN12.setStatus(NoticeStatus.CLOSED.getValue());
		changeNoticeStatus(auxN12);

		// ---------------------------- NOTICE 13 CLOSED
		
		Notice notice13 = new Notice();
		notice13.setDomain(DOMAIN_ID);
		notice13.setStartDate(new Date());
		notice13.setSender(getUser());
		notice13.setSource(String.valueOf(51360));
		notice13.setCompany("Artizar Joyeros SL");
		notice13.setTitle("test-Title notice 13");
		
		notice13.addTag(getTagByName(TagEnum.BUG.getValue(), TagType.OFFICE_TYPE.value()));
		notice13.addTag(getTagByName(TagEnum.ALTA.getValue(), TagType.OFFICE_PRIORITY.value()));		
		notice13.addTag(getTagByName(TagEnum.FISCAL.getValue(), TagType.OFFICE_NOTICE.value()));		
		
		notice13.addNotice(buildComment(notice13));
		notice13.addNotice(buildComment(notice13));
		Notice auxN13 = insertNotice(notice13);
		auxN13.setStatus(NoticeStatus.CLOSED.getValue());
		changeNoticeStatus(auxN13);



	}
	
	private static Notice insertNotice(Notice notice) {
		
		try {
			
			NoticeRecord noticeRecord = ctx.getDslContext()
					.insertInto(NOTICE)
					.set(NOTICE.DOMAIN, DOMAIN_ID)
					.set(NOTICE.DATE,
							new java.sql.Timestamp(notice.getStartDate().getTime()))
					.set(NOTICE.SENDER, notice.getSender().getId())
					.set(NOTICE.SUBJECT, notice.getTitle())
					.set(NOTICE.STATUS, NoticeStatus.OPEN.value())
					.set(NOTICE.TYPE, NoticeType.TICKET.value())
					.set(NOTICE.COMPANY,
							(notice.getCompany() != null) ? notice.getCompany()
									: null)
					.set(NOTICE.SOURCE,
							(notice.getSource() != null)
									? String.valueOf(notice.getSource()) : null)
					.set(NOTICE.PRIORITY, (byte) 0).returning().fetchOne();
			
			notice.setId(noticeRecord.getValue(NOTICE.ID));

			SelectConditionStep<Record1<Integer>> openId = getOpenNoticesId(
					ctx.getDslContext());

			ctx.getDslContext().insertInto(NOTICE_TAG)
					.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
					.set(NOTICE_TAG.START_DATE, noticeRecord.getValue(NOTICE.DATE))
					.set(NOTICE_TAG.TAG, openId)
					.set(NOTICE_TAG.USER, noticeRecord.getValue(NOTICE.SENDER))
					.execute();
			
			for (Tag tag : notice.getTags()) {
				
				ctx.getDslContext()
				.insertInto(NOTICE_TAG)
				.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
				.set(NOTICE_TAG.START_DATE, noticeRecord.getValue(NOTICE.DATE))
				.set(NOTICE_TAG.TAG, tag.getId())
				.set(NOTICE_TAG.USER, noticeRecord.getValue(NOTICE.SENDER))
				.execute();
			}
			
			for (Notice comment : notice.getComments()) {
				
				NoticeRecord commentRecord = ctx.getDslContext()
				.insertInto(NOTICE)
				.set(NOTICE.DOMAIN, DOMAIN_ID)
				.set(NOTICE.DATE,
						new java.sql.Timestamp(
								comment.getStartDate().getTime()))
				.set(NOTICE.SENDER, noticeRecord.getValue(NOTICE.SENDER))
				.set(NOTICE.SUBJECT, comment.getTitle() + " COMENTADO DESDE TEST")
				.set(NOTICE.NOTICE_, noticeRecord.getValue(NOTICE.ID))
				.set(NOTICE.STATUS, NoticeStatus.OPEN.value())
				.set(NOTICE.TYPE, NoticeType.COMMENT.value())
				.set(NOTICE.PRIORITY, (byte) 0)
				.returning()
				.fetchOne();
				
				comment.setId(commentRecord.getValue(NOTICE.ID));
			}		
			
			return notice;
			
		} catch (Exception ex) {
			System.out.println("Error: Notice " + notice.getTitle() + " no insertada. \n" + ex.getMessage());			
			return null;
		}
	}
	
	private static void changeNoticeStatus(Notice notice) {
		
		try {
			
			Date today = new Date();
			
			ctx.getDslContext()
					.update(NOTICE_TAG)
					.set(NOTICE_TAG.END_DATE,
							new java.sql.Timestamp(today.getTime()))
					.set(NOTICE_TAG.USER, notice.getSender().getId())
					.where(NOTICE_TAG.ID.eq(
							ctx.getDslContext().select(NOTICE_TAG.ID)
							.from(NOTICE_TAG)
							.join(TAG)
							.on(NOTICE_TAG.TAG.eq(TAG.ID))
							.where(NOTICE_TAG.END_DATE.isNull()
									.and(NOTICE_TAG.NOTICE.eq(notice.getId()))
									.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value())))
							))
					.returning()
					.fetchOne();

			ctx.getDslContext().insertInto(NOTICE_TAG)
					.set(NOTICE_TAG.NOTICE, notice.getId())
					.set(NOTICE_TAG.TAG,
							ctx.getDslContext().select(TAG.ID).from(TAG)
									.where(TAG.DOMAIN.eq(0).and(TAG.TYPE
											.eq(TagType.OFFICE_STATUS.value()))
									.and(TAG.NAME.eq(notice.getStatus()))))
					.set(NOTICE_TAG.START_DATE,
							new java.sql.Timestamp(today.getTime()))
					.set(NOTICE_TAG.USER, notice.getSender().getId()).execute();

			
		} catch (Exception ex) {
			
		}
	}
	
	private static SelectConditionStep<Record1<Integer>> getOpenNoticesId(
			DSLContext dsl) {

		return dsl.select(TAG.ID).from(TAG).where(TAG.DOMAIN.eq(0))
				.and(TAG.NAME.eq(NoticeStatus.OPEN.getValue())
						.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value())));
	}
	
	private static Tag getTagByName(String name, byte type) {
		for (Tag tag : tags) {
			if (AonStringUtils.equals(tag.getName(), name) && tag.getType() == type) 
				return tag;
		}
		
		return null;
	}
	
	private static Notice buildComment(Notice notice) {
		
		Notice comment = new Notice();
		comment.setDomain(notice.getDomain());
		comment.setStartDate(new Date());
		comment.setSender(notice.getSender());
		comment.setTitle(notice.getTitle() + " COMENTADO POR MI ");		
		
		return comment;
	}
	
	private static User getUser() {
		
		if (user == null) {
			user = new User();
			user.setDomain(DOMAIN_ID);
			user.setId(USER_ID);
			user.setLogin("mac");
			user.setName("Miguel Angel Calle");
			user.setActive(true);
		}
		return user;
	}
}
