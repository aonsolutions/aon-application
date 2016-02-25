package com.esferalia.aon.occam.jooq.test;

import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.SQLException;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.TagType;

public class OfficeTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "mac.amtzdelagos.dev";
	private static int DOMAIN_ID = 553;
	private static String USER_NAME = "mac";

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException,
			SQLException, AonConnectionException {
		Class.forName(org.gjt.mm.mysql.Driver.class.getName());
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER_NAME);
	}

	@Test
	public void testCreateNotices() {

		Date today = new Date();
		
		ctx.getDslContext().insertInto(TAG)
		.set(TAG.DOMAIN, ctx.getDomainId())
		.set(TAG.NAME, "Bug")
		.set(TAG.TYPE, TagType.OFFICE_TYPE.value())
		.execute();
		
		ctx.getDslContext().insertInto(TAG)
		.set(TAG.DOMAIN, ctx.getDomainId())
		.set(TAG.NAME, "Laboral")
		.set(TAG.TYPE, TagType.OFFICE_NOTICE.value())
		.execute();
		
		ctx.getDslContext().insertInto(TAG)
		.set(TAG.DOMAIN, ctx.getDomainId())
		.set(TAG.NAME, "Fiscal")
		.set(TAG.TYPE, TagType.OFFICE_NOTICE.value())
		.execute();


		Integer userId = ctx.getDslContext().selectFrom(USER)
				.where(USER.LOGIN.eq(ctx.getUser())
						.and(USER.DOMAIN.eq(ctx.getDomainId())))
				.fetchOne().getValue(USER.ID);
		
		// NOTICE 1
		NoticeRecord noticeRecord = ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
				.set(NOTICE.DATE, new java.sql.Timestamp((today).getTime()))
				.set(NOTICE.SENDER, userId)
				.set(NOTICE.SUBJECT, "Titulo Prueba 1")
				.set(NOTICE.STATUS, NoticeStatus.OPEN.value())
				.set(NOTICE.TYPE, NoticeType.MESSAGE.value())
				.set(NOTICE.PRIORITY, Priority.LOW.value()).returning()
				.fetchOne();
		
		System.out.println("Cabecera NOTICE1 Insertada correctamente");

		ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
				.set(NOTICE.DATE, noticeRecord.getValue(NOTICE.DATE))
				.set(NOTICE.SENDER, noticeRecord.getValue(NOTICE.SENDER))
				.set(NOTICE.SUBJECT, "Body para prueba 1")
				.set(NOTICE.STATUS, noticeRecord.getValue(NOTICE.STATUS))
			//	.set(NOTICE.TYPE, NoticeType.DESCRIPTION.value())
				.set(NOTICE.PRIORITY, noticeRecord.getValue(NOTICE.PRIORITY))
				.set(NOTICE.NOTICE_, noticeRecord.getValue(NOTICE.ID))
				.execute();
		
		System.out.println("Cuerpo NOTICE1 Insertada correctamente");

		ctx.getDslContext().insertInto(NOTICE_TAG)
				.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
				.set(NOTICE_TAG.TAG,
						ctx.getDslContext().select(TAG.ID).from(TAG)
								.where(TAG.DOMAIN.eq(0)
										.and(TAG.NAME.eq(
												NoticeStatus.OPEN.getValue()))
										.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value()))))
				.set(NOTICE_TAG.START_DATE, noticeRecord.getValue(NOTICE.DATE))
				.execute();
		System.out.println("Notice 1 asignada a OPEN");

		ctx.getDslContext().insertInto(NOTICE_TAG)
				.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
				.set(NOTICE_TAG.TAG,
						ctx.getDslContext().select(TAG.ID).from(TAG)
								.where(TAG.DOMAIN.eq(ctx.getDomainId())
										.and(TAG.NAME.eq("Normal"))
										.and(TAG.TYPE.eq(TagType.OFFICE_PRIORITY.value()))))
				.set(NOTICE_TAG.START_DATE, noticeRecord.getValue(NOTICE.DATE))
				.execute();
		System.out.println("Notice 1 asignada a NORMAL");

		ctx.getDslContext().insertInto(NOTICE_TAG)
				.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
				.set(NOTICE_TAG.TAG,
						ctx.getDslContext().select(TAG.ID).from(TAG)
								.where(TAG.DOMAIN.eq(ctx.getDomainId())
										.and(TAG.NAME.eq("Ticket"))
										.and(TAG.TYPE.eq(TagType.OFFICE_TYPE.value()))))				
				.set(NOTICE_TAG.START_DATE, noticeRecord.getValue(NOTICE.DATE))
				.execute();
		System.out.println("Notice 1 asignada a TICKET");
	
		// NOTICE 2
		
		today = new Date();
		System.out.println(" ------------------------------------ ");
		NoticeRecord noticeRecord2 = ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
				.set(NOTICE.DATE, new java.sql.Timestamp((today).getTime()))
				.set(NOTICE.SENDER, userId)
				.set(NOTICE.SUBJECT, "Titulo Prueba 2 --------------- 2")
				.set(NOTICE.STATUS, NoticeStatus.OPEN.value())
				.set(NOTICE.TYPE, NoticeType.MESSAGE.value())
				.set(NOTICE.PRIORITY, Priority.LOW.value()).returning()
				.fetchOne();
		System.out.println("Cabecera Notice2 correcto");
		
		ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
				.set(NOTICE.DATE, noticeRecord2.getValue(NOTICE.DATE))
				.set(NOTICE.SENDER, noticeRecord2.getValue(NOTICE.SENDER))
				.set(NOTICE.SUBJECT, "Body para prueba 2 ------------- 2")
				.set(NOTICE.STATUS, noticeRecord2.getValue(NOTICE.STATUS))
			//	.set(NOTICE.TYPE, NoticeType.DESCRIPTION.value())
				.set(NOTICE.PRIORITY, noticeRecord2.getValue(NOTICE.PRIORITY))
				.set(NOTICE.NOTICE_, noticeRecord2.getValue(NOTICE.ID))
				.execute();
		
		System.out.println("Cuerpo Notice2 correcto");
		
		ctx.getDslContext().insertInto(NOTICE_TAG)
				.set(NOTICE_TAG.NOTICE, noticeRecord2.getValue(NOTICE.ID))
				.set(NOTICE_TAG.TAG,
						ctx.getDslContext().select(TAG.ID).from(TAG)
								.where(TAG.DOMAIN.eq(0)
										.and(TAG.NAME.eq(
												NoticeStatus.OPEN.getValue()))
										.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value()))))
				.set(NOTICE_TAG.START_DATE, noticeRecord2.getValue(NOTICE.DATE))
				.execute();
		
		System.out.println("Notice 2 asignada a OPEN");
		
		ctx.getDslContext().insertInto(NOTICE_TAG)
				.set(NOTICE_TAG.NOTICE, noticeRecord2.getValue(NOTICE.ID))
				.set(NOTICE_TAG.TAG,
						ctx.getDslContext().select(TAG.ID).from(TAG)
								.where(TAG.DOMAIN.eq(ctx.getDomainId())
										.and(TAG.NAME.eq("Baja"))
										.and(TAG.TYPE.eq(TagType.OFFICE_PRIORITY.value()))))
				.set(NOTICE_TAG.START_DATE, noticeRecord2.getValue(NOTICE.DATE))
				.execute();
		
		System.out.println("Notice 2 asignada a Baja");
		
		ctx.getDslContext().insertInto(NOTICE_TAG)
				.set(NOTICE_TAG.NOTICE, noticeRecord2.getValue(NOTICE.ID))
				.set(NOTICE_TAG.TAG,
						ctx.getDslContext().select(TAG.ID).from(TAG)
								.where(TAG.DOMAIN.eq(ctx.getDomainId())
										.and(TAG.NAME.eq("Bug"))
										.and(TAG.TYPE.eq(TagType.OFFICE_TYPE.value()))))				
				.set(NOTICE_TAG.START_DATE, noticeRecord2.getValue(NOTICE.DATE))
				.execute();
		
		System.out.println("Notice 1 asignada a Bug");
		
		ctx.getDslContext().insertInto(NOTICE_TAG)
		.set(NOTICE_TAG.NOTICE, noticeRecord2.getValue(NOTICE.ID))
		.set(NOTICE_TAG.START_DATE, noticeRecord2.getValue(NOTICE.DATE))
		.set(NOTICE_TAG.TAG, ctx.getDslContext()
				.select(TAG.ID).from(TAG)
				.where(TAG.DOMAIN.eq(ctx.getDomainId())
						.and(TAG.NAME.eq("Laboral"))
						.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value()))));
		System.out.println("Notice 1 asignada a Laboral");

		ctx.getDslContext().insertInto(NOTICE_TAG)
		.set(NOTICE_TAG.NOTICE, noticeRecord2.getValue(NOTICE.ID))
		.set(NOTICE_TAG.START_DATE, noticeRecord2.getValue(NOTICE.DATE))
		.set(NOTICE_TAG.TAG, ctx.getDslContext()
				.select(TAG.ID).from(TAG)
				.where(TAG.DOMAIN.eq(ctx.getDomainId())
						.and(TAG.NAME.eq("Laboral"))
						.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value()))));
		System.out.println("Notice 1 asignada a Fiscal");



	}

	/*
	 * @Test public void testCreateNotice() {
	 * 
	 * Notice notice = new Notice(); notice.setTitle("Title Issue");
	 * notice.setBody("Body Issue");
	 * notice.setStatus(NoticeStatus.OPEN.value()); notice.setType("TICKET");
	 * notice.setPriority("LOW");
	 * 
	 * Date today = new Date(); Integer userId =
	 * ctx.getDslContext().selectFrom(USER) .where(USER.LOGIN.eq(ctx.getUser())
	 * .and(USER.DOMAIN.eq(ctx.getDomainId()))) .fetchOne().getValue(USER.ID);
	 * 
	 * InsertSetMoreStep<NoticeRecord> noticeRecord = ctx.getDslContext()
	 * .insertInto(NOTICE) .set(NOTICE.DOMAIN, ctx.getDomainId())
	 * .set(NOTICE.DATE, new java.sql.Timestamp((new Date()).getTime()))
	 * .set(NOTICE.SENDER, userId) .set(NOTICE.SUBJECT, notice.getTitle());
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * }
	 * 
	 * @Test public void testPruebaCreateNotice () {
	 * 
	 * Notice notice = new Notice(); notice.setTitle("Title Issue");
	 * notice.setBody("Body Issue");
	 * notice.setStatus(NoticeStatus.OPEN.value()); notice.setType("TICKET");
	 * notice.setPriority("LOW");
	 * 
	 * Date today = new Date();
	 * 
	 * Integer userId = ctx.getDslContext().selectFrom(USER)
	 * .where(USER.LOGIN.eq(ctx.getUser())
	 * .and(USER.DOMAIN.eq(ctx.getDomainId()))) .fetchOne().getValue(USER.ID);
	 * 
	 * NoticeRecord noticeRecord = ctx.getDslContext().insertInto(NOTICE)
	 * .set(NOTICE.DOMAIN, ctx.getDomainId()) .set(NOTICE.SENDER, userId)
	 * .set(NOTICE.DATE, new java.sql.Timestamp((new Date()).getTime()))
	 * .set(NOTICE.SUBJECT, notice.getTitle()) .set(NOTICE.STATUS,
	 * NoticeStatus.OPEN.value()) .set(NOTICE.TYPE, (byte)
	 * NoticeType.valueOf(notice.getType()).ordinal()) .set(NOTICE.PRIORITY,
	 * (byte) Priority.valueOf(notice.getPriority()).ordinal())
	 * .set(NOTICE.RECIPIENT, (notice.getRecipient() != null) ?
	 * notice.getRecipient() : null) .returning().fetchOne();
	 * 
	 * 
	 * ctx.getDslContext().insertInto(NOTICE) .set(NOTICE.DOMAIN,
	 * noticeRecord.getValue(NOTICE.DOMAIN)) .set(NOTICE.SENDER,
	 * noticeRecord.getValue(NOTICE.SENDER)) .set(NOTICE.DATE,
	 * noticeRecord.getValue(NOTICE.DATE)) .set(NOTICE.STATUS,
	 * noticeRecord.getValue(NOTICE.STATUS)) .set(NOTICE.TYPE,
	 * noticeRecord.getValue(NOTICE.TYPE)) .set(NOTICE.PRIORITY,
	 * noticeRecord.getValue(NOTICE.PRIORITY)) .set(NOTICE.NOTICE_,
	 * noticeRecord.getValue(NOTICE.ID)) .execute();
	 * 
	 * if ( notice.getTags().isEmpty() ) {
	 * ctx.getDslContext().insertInto(NOTICE_TAG) .set(NOTICE_TAG.NOTICE,
	 * noticeRecord.getValue(NOTICE.ID)) .set(NOTICE_TAG.START_DATE,
	 * noticeRecord.getValue(NOTICE.DATE)) .execute(); } else {
	 * 
	 * for (Tag tag : notice.getTags()) { // PUEDE QUE SOLO VENGA EL NOMBRE
	 * Integer tagId = ctx.getDslContext().selectFrom(TAG)
	 * .where(TAG.DOMAIN.eq(ctx.getDomainId()) .and(TAG.NAME.eq(tag.getName())))
	 * .fetchOne().getValue(TAG.ID);
	 * 
	 * ctx.getDslContext().insertInto(NOTICE_TAG) .set(NOTICE_TAG.NOTICE,
	 * noticeRecord.getValue(NOTICE.ID)) .set(NOTICE_TAG.TAG,
	 * tagId).set(NOTICE_TAG.START_DATE, noticeRecord.getValue(NOTICE.DATE))
	 * .execute(); } }
	 * 
	 * Notice object = new Notice();
	 * object.setId(noticeRecord.getValue(NOTICE.ID));
	 * object.setDomain(ctx.getDomainId()); object.setStartDate(today);
	 * 
	 * User user = new User(); UserRecord userRecord =
	 * ctx.getDslContext().selectFrom(USER)
	 * .where(USER.DOMAIN.eq(ctx.getDomainId())
	 * .and(USER.LOGIN.eq(ctx.getUser()))) .fetchOne();
	 * user.setId(userRecord.getValue(USER.ID)); user.setName(ctx.getUser());
	 * 
	 * object.setSender(user); object.setTitle(notice.getTitle());
	 * object.setBody(notice.getBody());
	 * object.setRecipient(noticeRecord.getValue(NOTICE.RECIPIENT));
	 * object.setStatus(noticeRecord.getValue(NOTICE.STATUS));
	 * object.setType(NoticeType.values()[noticeRecord.getValue(NOTICE.TYPE)]
	 * .getValue()); object.setPriority(
	 * Priority.values()[noticeRecord.getValue(NOTICE.PRIORITY)] .getValue());
	 * 
	 * System.out.println("ID: " + object.getId()); System.out.println(
	 * "DOMAIN: " + object.getDomain()); System.out.println("START DATE: " +
	 * object.getStartDate()); System.out.println("User: ............. ");
	 * System.out.println(" User ID : " + object.getSender().getId());
	 * System.out.println(" User Name : " + object.getSender().getName());
	 * System.out.println(" .................................. ");
	 * System.out.println("Title:_ " + object.getTitle()); System.out.println(
	 * "Body: " + object.getBody());
	 * 
	 * }
	 * 
	 * /*
	 * 
	 * @Test public void testCreate() {
	 * 
	 * Integer user_id = 1651; // CREANDO ETIQUETAS....
	 * 
	 * System.out.println("Creando etiquetas ...."); TagRecord tag1 =
	 * ctx.getDslContext().insertInto(TAG) .set(TAG.DOMAIN,
	 * DOMAIN_ID).set(TAG.NAME, "Et. 1") .set(TAG.TYPE,
	 * TagType.NOTICE.value()).returning().fetchOne(); System.out.println(
	 * "Etiqueta 1 creada correctamente .... ");
	 * 
	 * TagRecord tag2 = ctx.getDslContext().insertInto(TAG) .set(TAG.DOMAIN,
	 * DOMAIN_ID).set(TAG.NAME, "Et. 2") .set(TAG.TYPE,
	 * TagType.NOTICE.value()).returning().fetchOne(); System.out.println(
	 * "Etiqueta 2 creada correctamente .... ");
	 * 
	 * TagRecord tag3 = ctx.getDslContext().insertInto(TAG) .set(TAG.DOMAIN,
	 * DOMAIN_ID).set(TAG.NAME, "Et. 3") .set(TAG.TYPE,
	 * TagType.NOTICE.value()).returning().fetchOne(); System.out.println(
	 * "Etiqueta 3 creada correctamente .... ");
	 * 
	 * System.out .println("OK!  ======== >> Etiquetas insertadas correctamente"
	 * );
	 * 
	 * tagList.add(tag1); tagList.add(tag2); tagList.add(tag3);
	 * 
	 * // ********************************************************* //
	 * ******************************************** NOTICE 1 *** //
	 * *********************************************************
	 * 
	 * String title = "Titulo: Abriendo Notice 1 desde OfficeTest"; String body
	 * = "Cuerpo del mensaje para el notice 1 abierto dede OfficeTest"; String
	 * phone = "555-000.000";
	 * 
	 * NoticeRecord newNotice = ctx.getDslContext().insertInto(NOTICE)
	 * .set(NOTICE.DOMAIN, DOMAIN_ID).set(NOTICE.SENDER, user_id)
	 * .set(NOTICE.DATE, new java.sql.Timestamp(newDate().getTime()))
	 * .set(NOTICE.SUBJECT, title).set(NOTICE.PHONE, phone) .set(NOTICE.STATUS,
	 * NoticeStatus.OPEN.value()) .set(NOTICE.TYPE, NoticeType.TICKET.value())
	 * .set(NOTICE.PRIORITY, Priority.NORMAL.value()).returning() .fetchOne();
	 * 
	 * ctx.getDslContext().insertInto(NOTICE) .set(NOTICE.DOMAIN,
	 * newNotice.getValue(NOTICE.DOMAIN)) .set(NOTICE.SENDER,
	 * newNotice.getValue(NOTICE.SENDER)) .set(NOTICE.DATE,
	 * newNotice.getValue(NOTICE.DATE)) .set(NOTICE.SUBJECT, body)
	 * .set(NOTICE.PHONE, newNotice.getValue(NOTICE.PHONE)) .set(NOTICE.STATUS,
	 * newNotice.getValue(NOTICE.STATUS)) .set(NOTICE.TYPE,
	 * newNotice.getValue(NOTICE.TYPE)) .set(NOTICE.PRIORITY,
	 * newNotice.getValue(NOTICE.PRIORITY)) .set(NOTICE.NOTICE_,
	 * newNotice.getValue(NOTICE.ID)).execute();
	 * 
	 * ctx.getDslContext().insertInto(NOTICE_TAG) .set(NOTICE_TAG.NOTICE,
	 * newNotice.getValue(NOTICE.ID)) .set(NOTICE_TAG.TAG,
	 * tag1.getValue(TAG.ID)) .set(NOTICE_TAG.START_DATE,
	 * newNotice.getValue(NOTICE.DATE)) .execute();
	 * 
	 * System.out.println("Notice 1 creado correctamente .... ");
	 * 
	 * noticeList.add(newNotice);
	 * 
	 * // ********************************************************* //
	 * ******************************************** NOTICE 2 *** //
	 * *********************************************************
	 * 
	 * String title2 = "Titulo del mensaje 2 para evaluar"; String body2 =
	 * "En un lugar de la mancha de cuyo nombre no quiero acordarme, " +
	 * "residia un hombre cuya prepotencia y carisma nada altruista " +
	 * "hacia que fuera solitario y amante de la bebida."; String phone2 =
	 * "555-000.000";
	 * 
	 * NoticeRecord notice2 = ctx.getDslContext().insertInto(NOTICE)
	 * .set(NOTICE.DOMAIN, DOMAIN_ID).set(NOTICE.SENDER, user_id)
	 * .set(NOTICE.DATE, new java.sql.Timestamp(newDate().getTime()))
	 * .set(NOTICE.SUBJECT, title2).set(NOTICE.PHONE, phone2)
	 * .set(NOTICE.STATUS, NoticeStatus.OPEN.value()) .set(NOTICE.TYPE,
	 * NoticeType.TICKET.value()) .set(NOTICE.PRIORITY,
	 * Priority.NORMAL.value()).returning() .fetchOne();
	 * 
	 * ctx.getDslContext().insertInto(NOTICE) .set(NOTICE.DOMAIN,
	 * notice2.getValue(NOTICE.DOMAIN)) .set(NOTICE.SENDER,
	 * notice2.getValue(NOTICE.SENDER)) .set(NOTICE.DATE,
	 * notice2.getValue(NOTICE.DATE)) .set(NOTICE.SUBJECT, body2)
	 * .set(NOTICE.PHONE, notice2.getValue(NOTICE.PHONE)) .set(NOTICE.STATUS,
	 * notice2.getValue(NOTICE.STATUS)) .set(NOTICE.TYPE,
	 * notice2.getValue(NOTICE.TYPE)) .set(NOTICE.PRIORITY,
	 * notice2.getValue(NOTICE.PRIORITY)) .set(NOTICE.NOTICE_,
	 * notice2.getValue(NOTICE.ID)).execute();
	 * 
	 * ctx.getDslContext().insertInto(NOTICE_TAG) .set(NOTICE_TAG.NOTICE,
	 * notice2.getValue(NOTICE.ID)) .set(NOTICE_TAG.TAG, tag1.getValue(TAG.ID))
	 * .set(NOTICE_TAG.START_DATE, notice2.getValue(NOTICE.DATE)) .execute();
	 * ctx.getDslContext().insertInto(NOTICE_TAG) .set(NOTICE_TAG.NOTICE,
	 * notice2.getValue(NOTICE.ID)) .set(NOTICE_TAG.TAG, tag2.getValue(TAG.ID))
	 * .set(NOTICE_TAG.START_DATE, notice2.getValue(NOTICE.DATE)) .execute();
	 * 
	 * System.out.println("Notice 2 creado correctamente .... ");
	 * 
	 * noticeList.add(notice2);
	 * 
	 * // ********************************************************* //
	 * ******************************************** NOTICE 3 *** //
	 * *********************************************************
	 * 
	 * String title3 =
	 * "Titulo del mensaje 3 kjasljipqjwekñajsp oaskpqasjñajs iasñjkañs"; String
	 * body3 =
	 * "The fact that you are seeing this page indicates that the website " +
	 * "you just visited is either experiencing problems, or is undergoing routine maintenance."
	 * +
	 * "If you would like to let the administrators of this website know that you've seen this "
	 * +
	 * "page instead of the page you expected, you should send them e-mail. In general, mail sent "
	 * +
	 * "to the name webmaster and directed to the website's domain should reach the appropriate person."
	 * +
	 * "For example, if you experienced problems while visiting www.example.com, you should send e-mail "
	 * + "to webmaster@example.com." +
	 * "Fedora is a distribution of Linux, a popular computer operating system. It is commonly used by "
	 * +
	 * "ting companies because it is free, and includes free web server software. Many times, they do not set "
	 * +
	 * "up their web server correctly, and it displays this test page instead of the expected website. "
	 * ;
	 * 
	 * String phone3 = "555-000.000";
	 * 
	 * NoticeRecord notice3 = ctx.getDslContext().insertInto(NOTICE)
	 * .set(NOTICE.DOMAIN, DOMAIN_ID).set(NOTICE.SENDER, user_id)
	 * .set(NOTICE.DATE, new java.sql.Timestamp(newDate().getTime()))
	 * .set(NOTICE.SUBJECT, title3).set(NOTICE.PHONE, phone3)
	 * .set(NOTICE.STATUS, NoticeStatus.OPEN.value()) .set(NOTICE.TYPE,
	 * NoticeType.TICKET.value()) .set(NOTICE.PRIORITY,
	 * Priority.NORMAL.value()).returning() .fetchOne();
	 * 
	 * ctx.getDslContext().insertInto(NOTICE) .set(NOTICE.DOMAIN,
	 * notice3.getValue(NOTICE.DOMAIN)) .set(NOTICE.SENDER,
	 * notice3.getValue(NOTICE.SENDER)) .set(NOTICE.DATE,
	 * notice3.getValue(NOTICE.DATE)) .set(NOTICE.SUBJECT, body3)
	 * .set(NOTICE.PHONE, notice3.getValue(NOTICE.PHONE)) .set(NOTICE.STATUS,
	 * notice3.getValue(NOTICE.STATUS)) .set(NOTICE.TYPE,
	 * notice2.getValue(NOTICE.TYPE)) .set(NOTICE.PRIORITY,
	 * notice3.getValue(NOTICE.PRIORITY)) .set(NOTICE.NOTICE_,
	 * notice3.getValue(NOTICE.ID)).execute();
	 * 
	 * ctx.getDslContext().insertInto(NOTICE_TAG) .set(NOTICE_TAG.NOTICE,
	 * notice3.getValue(NOTICE.ID)) .set(NOTICE_TAG.TAG, tag1.getValue(TAG.ID))
	 * .set(NOTICE_TAG.START_DATE, notice3.getValue(NOTICE.DATE)) .execute();
	 * ctx.getDslContext().insertInto(NOTICE_TAG) .set(NOTICE_TAG.NOTICE,
	 * notice3.getValue(NOTICE.ID)) .set(NOTICE_TAG.TAG, tag2.getValue(TAG.ID))
	 * .set(NOTICE_TAG.START_DATE, notice3.getValue(NOTICE.DATE)) .execute();
	 * ctx.getDslContext().insertInto(NOTICE_TAG) .set(NOTICE_TAG.NOTICE,
	 * notice3.getValue(NOTICE.ID)) .set(NOTICE_TAG.TAG, tag3.getValue(TAG.ID))
	 * .set(NOTICE_TAG.START_DATE, notice3.getValue(NOTICE.DATE)) .execute();
	 * 
	 * System.out.println("Notice 3 creado correctamente .... ");
	 * System.out.println(
	 * " *************  ========== Notices creados correctamente ");
	 * 
	 * noticeList.add(notice3); }
	 * 
	 * @Test public void testOpenNotices() {
	 * 
	 * List<Notice> notices = new LinkedList<Notice>();
	 * ctx.getDslContext().selectFrom(NOTICE) .where(NOTICE.DOMAIN.eq(DOMAIN_ID)
	 * .and(NOTICE.STATUS.eq(NoticeStatus.OPEN.value()) .or(NOTICE.STATUS
	 * .eq(NoticeStatus.REOPEN.value())))
	 * .and(NOTICE.NOTICE_.isNull())).fetch().stream() .forEach(record -> {
	 * Notice notice = new Notice(); int id = record.getValue(NOTICE.ID); int
	 * domain = record.getValue(NOTICE.DOMAIN);
	 * 
	 * notice.setId(id); notice.setDomain(domain);
	 * notice.setStartDate(record.getValue(NOTICE.DATE));
	 * 
	 * User user = new User(); UserRecord userRecord =
	 * ctx.getDslContext().selectFrom(USER)
	 * .where(USER.DOMAIN.eq(ctx.getDomainId())
	 * .and(USER.LOGIN.eq(ctx.getUser()))) .fetchOne();
	 * user.setId(userRecord.getValue(USER.ID)); user.setName(ctx.getUser());
	 * 
	 * notice.setSender(user); notice.setTitle(record.getValue(NOTICE.SUBJECT));
	 * notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
	 * notice.setContact(record.getValue(NOTICE.PHONE));
	 * notice.setSource(record.getValue(NOTICE.SOURCE));
	 * notice.setCompany(record.getValue(NOTICE.COMPANY));
	 * notice.setStatus(record.getValue(NOTICE.STATUS));
	 * notice.setWorkgroup(record.getValue(NOTICE.WORK_GROUP));
	 * 
	 * byte ordinalType = record.getValue(NOTICE.TYPE);
	 * notice.setType(NoticeType.values()[ordinalType].getValue());
	 * 
	 * try {
	 * 
	 * String priorityName = ctx.getDslContext()
	 * .selectFrom(TAG.rightOuterJoin(NOTICE_TAG)
	 * .on(TAG.ID.eq(NOTICE_TAG.TAG)))
	 * .where(TAG.TYPE.eq(TagType.PRIORITY.value()) .and(TAG.DOMAIN.eq(domain))
	 * .and(NOTICE_TAG.NOTICE.eq(id)) .and(NOTICE_TAG.START_DATE.eq(
	 * record.getValue(NOTICE.DATE)))) .fetchOne().getValue(TAG.NAME);
	 * 
	 * notice.setPriority(priorityName);
	 * 
	 * } catch (Exception ex) { System.out.println(ex.getLocalizedMessage() +
	 * " " + ex.getMessage()); }
	 * 
	 * setBody2Notice(ctx, notice);
	 * 
	 * notices.add(notice); });
	 * 
	 * System.out.println("Size: " + notices.size());
	 * 
	 * for (Notice notice : notices) {
	 * 
	 * System.out.println("=================================");
	 * 
	 * System.out.println("Id: " + notice.getId()); System.out.println(
	 * "Subject: " + notice.getTitle()); System.out.println("Body: " +
	 * notice.getBody()); System.out.println("PriorityName: " +
	 * notice.getPriority()); System.out.println("UserId: " +
	 * notice.getSender().getId()); System.out.println("userLogin: " +
	 * notice.getSender().getName());
	 * 
	 * System.out.println("================================= \n"); } }
	 * 
	 * private Date newDate() { return new Date(); }
	 * 
	 * private void setBody2Notice(AONContext ctx, Notice notice) {
	 * 
	 * NoticeRecord record = ctx.getDslContext().selectFrom(NOTICE)
	 * .where(NOTICE.NOTICE_.eq(notice.getId())).fetchOne();
	 * 
	 * if (record == null) notice.setBody(notice.getTitle()); else {
	 * notice.setBody(record.getValue(NOTICE.SUBJECT));
	 * notice.setNotice(record.getValue(NOTICE.NOTICE_)); } }
	 * 
	 * @AfterClass public static void testRemove() {
	 * 
	 * for (NoticeRecord record : noticeList) {
	 * 
	 * ctx.getDslContext().delete(NOTICE_TAG)
	 * .where(NOTICE_TAG.NOTICE.eq(record.getValue(NOTICE.ID))) .execute();
	 * 
	 * ctx.getDslContext().delete(NOTICE)
	 * .where(NOTICE.NOTICE_.eq(record.getValue(NOTICE.ID))) .execute();
	 * 
	 * ctx.getDslContext().delete(NOTICE)
	 * .where(NOTICE.ID.eq(record.getValue(NOTICE.ID))).execute();
	 * 
	 * }
	 * 
	 * for (TagRecord tag : tagList) { ctx.getDslContext().delete(TAG)
	 * .where(TAG.ID.eq(tag.getValue(TAG.ID))).execute();
	 * 
	 * System.out.println( tag.getValue(TAG.NAME) +
	 * " BORRADA CORRECTAMENTE .... "); }
	 * 
	 * }
	 */
}
