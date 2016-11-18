package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskComment.TASK_COMMENT;
import static com.esferalia.aon.jooq.tables.TaskEvent.TASK_EVENT;
import static com.esferalia.aon.jooq.tables.TaskTag.TASK_TAG;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.records.AppParamRecord;
import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.NoticeTagRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.jooq.tables.records.TaskHolderRecord;
import com.esferalia.aon.jooq.tables.records.TaskRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistryMediaProperties;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NoticeContainer;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TagColor;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.occam.api.model.type.TagType;

public class AonHubDAO {
	// TODO
	
	public static void OLD2NEW(AONContext ctx, Integer domainId){
		System.out.println("DATABASE PROCCES");
		LinkedList<NoticeRecord> list = getNotices(ctx, domainId);
		System.out.println("number of notices:" + list.size());
		
		updateTaskNumber(ctx, domainId, list.size());

		for(Integer i = 0; i < list.size(); i++){
			NoticeRecord n = list.get(i);
			System.out.println("Notice " + n.getId() );
			TaskRecord task = new TaskRecord();
			task.setDescription(n.getSubject());
			task.setCreationDate(n.getDate());
			task.setStartDate(n.getDate());
			task.setCreationUser(getUserName(ctx, n.getSender()));
			task.setDomain(domainId);
			task.setNumber(i + 1);
			task.setRegistry(getEnterprise(ctx, domainId, n.getCompany()).getId());
			task.setTaskHolder(getTaskHolder(ctx, n.getRecipient()).getRegistry());
			Integer taskId = insertTask(ctx, task);
			System.out.println("INSERT TASK " + taskId );
			getNoticeTags(ctx, n.getId()).forEach(t -> { // ORDENADOS POR FECHA (DE VIEJO A NUEVO)
				TagRecord tag = getTag(ctx, t.getTag());
				if(tag.getType().equals(TagType.OFFICE_NOTICE.value())){
					TagRecord tag2 = getTag(ctx, tag.getName(), domainId, TagType.TASK_LABEL);
					if(tag2.getId() != null){
						insertTaskTag(ctx, domainId, taskId, tag2.getId());
					} else {
						Random rnd = new Random();
						Integer tagId = insertTag(ctx, domainId, tag.getName(), TagColor.values()[rnd.nextInt(9)], TagType.TASK_LABEL);
						insertTaskTag(ctx, domainId, taskId, tagId);
					}
				} else if(tag.getType().equals(TagType.OFFICE_TYPE.value())){
					TagRecord tag2 = getTag(ctx, tag.getName(), domainId, TagType.TASK_TYPE);
					if(tag2.getId() != null){
						insertTaskTag(ctx, domainId, taskId, tag2.getId());
					} else {
						Random rnd = new Random();
						Integer tagId = insertTag(ctx, domainId, tag.getName(), TagColor.values()[rnd.nextInt(9)], TagType.TASK_TYPE);
						insertTaskTag(ctx, domainId, taskId, tagId);
					}
				} else if(tag.getType().equals(TagType.OFFICE_STATUS.value())){
					insertTaskEvent(ctx, domainId, taskId,tag.getName(), t);
					if(t.getEndDate() == null)
						updateTaskStatus(ctx, taskId, tag.getName(),t);
				}
			});
		
			LinkedList<NoticeRecord> commentList = getNoticeComments(ctx, n.getId());
			for(Integer j = 0 ; j < commentList.size(); j++){
				NoticeRecord c = commentList.get(j);
				if(j == 0){
					updateTaskComment(ctx, task.getId(), c);
				} else {
					insertTaskComment(ctx, domainId, c, taskId);
				}
			}
		}
	}
	
	public static LinkedList<NoticeRecord> getNotices(AONContext ctx, Integer domainId){
		 return ctx.getDslContext()
				 .select()
				 .from(NOTICE)
				 .where(NOTICE.NOTICE_.isNull()).and(NOTICE.DOMAIN.eq(domainId))
				 .fetchInto(NOTICE).stream().collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<NoticeRecord> getNoticeComments(AONContext ctx, Integer id){
		 return ctx.getDslContext()
				 .select()
				 .from(NOTICE)
				 .where(NOTICE.NOTICE_.eq(id))
				 .fetchInto(NOTICE).stream().collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Stream<NoticeTagRecord> getNoticeTags(AONContext ctx, Integer id){
		 return ctx.getDslContext()
				 .select()
				 .from(NOTICE_TAG)
				 .where(NOTICE_TAG.NOTICE.eq(id))
				 .fetchInto(NOTICE_TAG).stream();
	}
	
	public static TagRecord getTag(AONContext ctx, Integer id){
		return ctx.getDslContext()
				.select()
				.from(TAG)
				.where(TAG.ID.eq(id))
				.fetchInto(TAG).stream().findFirst().orElse(new TagRecord());
	}
	
	public static TagRecord getTag(AONContext ctx, String name, Integer domainId, TagType tagType){
		return ctx.getDslContext()
				.select()
				.from(TAG)
				.where(TAG.NAME.eq(name))
					.and(TAG.TYPE.eq(tagType.value()))
					.and(TAG.DOMAIN.eq(domainId))
				.fetchInto(TAG).stream().findFirst().orElse(new TagRecord());
	}
	
	public static String getUserName(AONContext ctx, Integer id){
		return ctx.getDslContext().select(USER.LOGIN)
				.from(USER)
				.where(USER.ID.eq(id))
				.fetch().get(0).getValue(USER.LOGIN);
	}
	
	public static RegistryRecord getEnterprise(AONContext ctx, Integer domainId, String company){
		return ctx.getDslContext().select()
				.from(REGISTRY)
				.where(REGISTRY.NAME.eq(company))
				.and(REGISTRY.DOMAIN.eq(domainId))
				.fetchInto(REGISTRY).stream().findFirst().orElse(new RegistryRecord());
	}
	
	public static void updateTaskNumber(AONContext ctx, Integer domainId, Integer number) {
		ctx.getDslContext().update(TASK).set(TASK.NUMBER, TASK.NUMBER.add(number))
		.where(TASK.DOMAIN.eq(domainId)).execute();
	}
	
	public static void updateTaskStatus(AONContext ctx, Integer taskId, String status, NoticeTagRecord ntr) {
		if(status.equals("OPEN") || status.equals("REOPEN")){
			ctx.getDslContext().update(TASK).set(TASK.STATUS, TaskStatus.PENDING.value())
			.set(TASK.MODIFICATION_DATE, ntr.getStartDate())
			.set(TASK.MODIFICATION_USER, getUserName(ctx, ntr.getUser()))
			.where(TASK.ID.eq(taskId)).execute();			
		}
		else {
			ctx.getDslContext().update(TASK).set(TASK.STATUS, TaskStatus.FINISHED.value())
			.set(TASK.END_DATE, ntr.getStartDate())
			.set(TASK.MODIFICATION_DATE, ntr.getStartDate())
			.set(TASK.MODIFICATION_USER, getUserName(ctx, ntr.getUser()))
			.where(TASK.ID.eq(taskId)).execute();
		}
	}
	
	public static void updateTaskComment(AONContext ctx, Integer taskId, NoticeRecord c){
		ctx.getDslContext().update(TASK).set(TASK.COMMENTS, c.getSubject())
			.set(TASK.MODIFICATION_DATE, c.getDate())
			.set(TASK.MODIFICATION_USER, getUserName(ctx, c.getSender())).execute();
	}
	
	public static Integer insertTask(AONContext ctx, TaskRecord task){
		return ctx.getDslContext().insertInto(TASK, TASK.DESCRIPTION, TASK.CREATION_DATE, TASK.CREATION_USER, TASK.START_DATE, TASK.DOMAIN, 
						TASK.NUMBER, TASK.REGISTRY, TASK.TASK_HOLDER, TASK.DUE_DATE, TASK.PERCENT, TASK.PRIORITY, TASK.SOURCE,
						TASK.MODIFICATION_DATE, TASK.MODIFICATION_USER, TASK.STATUS)
				.values(task.getDescription(), task.getCreationDate(), task.getCreationUser(), task.getStartDate(), task.getDomain(), task.getNumber(),
						task.getRegistry(), task.getTaskHolder(), new Timestamp(Calendar.getInstance().getTime().getTime()), (byte) 0, (byte) 0,
						TaskSource.MANUAL.value(), task.getCreationDate(), task.getCreationUser(), TaskStatus.PENDING.value())
				.returning(TASK.ID).fetchOne().getId();
	}
	
	public static void insertTaskEvent(AONContext ctx,Integer domainId, Integer taskId,  String tagName, NoticeTagRecord ntr){
		String event = "";
		if(!tagName.equals("OPEN")) {
			if(tagName.equals("REOPEN")) event = "reopened";
			else event = "closed";
			ctx.getDslContext().insertInto(TASK_EVENT,TASK_EVENT.CREATION_DATE, TASK_EVENT.CREATION_USER, TASK_EVENT.DOMAIN
						,TASK_EVENT.EVENT, TASK_EVENT.MODIFICATION_DATE, TASK_EVENT.MODIFICATION_USER, TASK_EVENT.TASK)
						.values(ntr.getStartDate(), getUserName(ctx, ntr.getUser()), domainId, 
							event, ntr.getStartDate(), getUserName(ctx, ntr.getUser()), taskId)
						.execute();
		}
	}
	
	public static Integer insertTaskTag(AONContext ctx, Integer domainId, Integer taskId, Integer tagId){
		return ctx.getDslContext().insertInto(TASK_TAG, TASK_TAG.DOMAIN, TASK_TAG.TAG, TASK_TAG.TASK)
						.values(domainId, tagId, taskId)
						.returning(TAG.ID).fetchOne().getId();
	}

	public static Integer insertTag(AONContext ctx, Integer domainId, String name, TagColor color, TagType type){
		return ctx.getDslContext().insertInto(TAG,TAG.COLOR, TAG.DOMAIN, TAG.NAME, TAG.TYPE)
						.values(color.getColor(), domainId, name, type.value())
						.returning(TAG.ID).fetchOne().getId();
	}
	
	public static void insertTaskComment(AONContext ctx, Integer domainId, NoticeRecord c, Integer taskId){
		ctx.getDslContext().insertInto(TASK_COMMENT, TASK_COMMENT.COMMENT, TASK_COMMENT.CREATION_DATE, TASK_COMMENT.CREATION_USER,
				TASK_COMMENT.DOMAIN, TASK_COMMENT.MODIFICATION_DATE, TASK_COMMENT.MODIFICATION_USER, TASK_COMMENT.TASK)
					.values(c.getSubject(), c.getDate(), getUserName(ctx, c.getSender()), domainId, c.getDate(), getUserName(ctx, c.getSender()), taskId)
					.execute();
	}
	
	
	public static TaskHolderRecord getTaskHolder(AONContext ctx, Integer userId) {
		return ctx.getDslContext().select(TASK_HOLDER.REGISTRY).from(TASK_HOLDER).where(TASK_HOLDER.USER_ID.eq(userId)).fetchInto(TASK_HOLDER)
				.stream().findFirst().orElse(new TaskHolderRecord());
	}
	
	// TODO
	
	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	public static Notice addNewNotice(AONContext ctx, Notice notice)
			throws Exception {

		NoticeRecord noticeRecord = ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
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

		SelectConditionStep<Record1<Integer>> openId = getOpenNoticesId(
				ctx.getDslContext());

		ctx.getDslContext().insertInto(NOTICE_TAG)
				.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
				.set(NOTICE_TAG.START_DATE, noticeRecord.getValue(NOTICE.DATE))
				.set(NOTICE_TAG.TAG, openId)
				.set(NOTICE_TAG.USER, noticeRecord.getValue(NOTICE.SENDER))
				.execute();

		return buildNoticeById(ctx, noticeRecord.getValue(NOTICE.ID));
	}

	public static Notice changeNoticeState(AONContext ctx, Notice notice) {

		try {

			Date today = new Date();

			Record result = ctx.getDslContext()
					.selectFrom(NOTICE_TAG.rightOuterJoin(TAG)
							.on(NOTICE_TAG.TAG.eq(TAG.ID)))
					.where(NOTICE_TAG.END_DATE.isNull()
							.and(NOTICE_TAG.NOTICE.eq(notice.getId()))
							.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value())))
					.fetchOne();

			ctx.getDslContext().update(NOTICE_TAG)
					.set(NOTICE_TAG.END_DATE,
							new java.sql.Timestamp((today).getTime()))
					.set(NOTICE_TAG.USER, notice.getSender().getId())
					.where(NOTICE_TAG.ID.eq(result.getValue(NOTICE_TAG.ID)))
					.execute();

			ctx.getDslContext().insertInto(NOTICE_TAG)
					.set(NOTICE_TAG.NOTICE, notice.getId())
					.set(NOTICE_TAG.TAG,
							ctx.getDslContext().select(TAG.ID).from(TAG)
									.where(TAG.DOMAIN.eq(0).and(TAG.TYPE
											.eq(TagType.OFFICE_STATUS.value()))
									.and(TAG.NAME.eq(notice.getStatus()))))
					.set(NOTICE_TAG.START_DATE,
							new java.sql.Timestamp((today).getTime()))
					.set(NOTICE_TAG.USER, notice.getSender().getId()).execute();

			return buildNoticeById(ctx, notice.getId());
		} catch (Exception ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
			throw ex;
		}
	}

	public static Notice editNotice(AONContext ctx, Notice notice) {

		try {

			ctx.getDslContext().update(NOTICE)
					.set(NOTICE.SUBJECT, notice.getTitle())
					.where(NOTICE.ID.eq(notice.getId())).execute();

			ctx.getDslContext().update(NOTICE)
					.set(NOTICE.SUBJECT, notice.getBody())
					.where(NOTICE.NOTICE_.eq(notice.getId())
							.and(NOTICE.TYPE.eq(NoticeType.MESSAGE.value())))
					.execute();

			return buildNoticeById(ctx, notice.getId());

		} catch (Exception ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
			throw ex;
		}
	}

	public static Tag addNewTag(AONContext ctx, Tag tag) {

		TagRecord record = ctx.getDslContext().insertInto(TAG)
				.set(TAG.DOMAIN, ctx.getDomainId()).set(TAG.NAME, tag.getName())
				.set(TAG.TYPE, tag.getType())
				.set(TAG.COLOR,
						(tag.getColor() != null) ? tag.getColor() : null)
				.returning().fetchOne();

		return buildTag(record);
	}

	public static Tag editTag(AONContext ctx, String labelName, Tag tag) {

		ctx.getDslContext().update(TAG).set(TAG.NAME, tag.getName())
				.where(TAG.DOMAIN.eq(ctx.getDomainId())
						.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value()))
						.and(TAG.NAME.eq(labelName)))
				.execute();

		TagRecord tagRecord = ctx.getDslContext().selectFrom(TAG)
				.where(TAG.DOMAIN.eq(ctx.getDomainId())
						.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value()))
						.and(TAG.NAME.eq(tag.getName())))
				.fetchOne();

		return buildTag(tagRecord);
	}

	public static boolean deleteTag(AONContext ctx, String labelName) {

		try {

			TagRecord tagRecord = ctx
					.getDslContext().selectFrom(TAG).where(TAG.DOMAIN
							.eq(ctx.getDomainId()).and(TAG.NAME.eq(labelName)))
					.fetchOne();

			ctx.getDslContext().delete(NOTICE_TAG)
					.where(NOTICE_TAG.TAG.eq(tagRecord.getValue(TAG.ID)))
					.execute();

			ctx.getDslContext().delete(TAG)
					.where(TAG.ID.eq(tagRecord.getValue(TAG.ID))).execute();

			return true;

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			return false;
		}
	}

	private static void evalOpenTag(AONContext ctx, Integer noticeId, Tag tag,
			Date date) {

		Record result = ctx.getDslContext()
				.selectFrom(NOTICE_TAG.rightOuterJoin(TAG)
						.on(NOTICE_TAG.TAG.eq(TAG.ID)))
				.where(TAG.TYPE.eq(tag.getType())
						.and(NOTICE_TAG.TAG.eq(tag.getId()))
						.and(NOTICE_TAG.END_DATE.isNull())
						.and(NOTICE_TAG.NOTICE.eq(noticeId)))
				.fetchOne();

		if (result != null) {

			ctx.getDslContext().update(NOTICE_TAG)
					.set(NOTICE_TAG.END_DATE,
							new java.sql.Timestamp((date).getTime()))
					.set(NOTICE_TAG.USER, tag.getUser().getId())
					.where(NOTICE_TAG.ID.eq(result.getValue(NOTICE_TAG.ID)))
					.execute();
		}

	}

	public static Notice addLabelsToAnIssue(AONContext ctx, Integer noticeId,
			List<Tag> tagList) {

		Date today = new Date();

		try {

			for (Tag tag : tagList) {
				evalOpenTag(ctx, noticeId, tag, today);

				ctx.getDslContext().insertInto(NOTICE_TAG)
						.set(NOTICE_TAG.NOTICE, noticeId)
						.set(NOTICE_TAG.TAG, tag.getId())
						.set(NOTICE_TAG.START_DATE,
								new java.sql.Timestamp((today).getTime()))
						.set(NOTICE_TAG.USER, tag.getUser().getId()).execute();
			}

			return buildNoticeById(ctx, noticeId);

		} catch (Exception ex) {
			System.out.println("Error al grabar etiqueta");
			return null;
		}
	}

	public static boolean removeLabelFromIssue(AONContext ctx, Integer issueId,
			Tag tag) throws DataAccessException {

		Date today = new Date();

		try {
			Record result = ctx.getDslContext()
					.selectFrom(NOTICE_TAG.rightOuterJoin(TAG)
							.on(NOTICE_TAG.TAG.eq(TAG.ID)))
					.where(TAG.TYPE.eq(tag.getType())
							.and(NOTICE_TAG.TAG.eq(tag.getId()))
							.and(NOTICE_TAG.END_DATE.isNull())
							.and(NOTICE_TAG.NOTICE.eq(issueId)))
					.fetchOne();

			if (result != null) {
				ctx.getDslContext().update(NOTICE_TAG)
						.set(NOTICE_TAG.END_DATE,
								new java.sql.Timestamp((today).getTime()))
						.set(NOTICE_TAG.USER, tag.getUser().getId())
						.where(NOTICE_TAG.ID.eq(result.getValue(NOTICE_TAG.ID)))
						.execute();

				return true;
			}
			return true;
		} catch (Exception ex) {
			System.out.println("Error al cerrar la etiqueta: " + tag.getName());
			return false;
		}
	}

	public static Notice replaceLabelsForIssue(AONContext ctx, Integer noticeId,
			List<Tag> addLabels, List<Tag> deletedLabels) {

		Date today = new Date();

		for (Tag tag : deletedLabels) {

			Record result = ctx.getDslContext()
					.selectFrom(NOTICE_TAG.rightOuterJoin(TAG)
							.on(NOTICE_TAG.TAG.eq(TAG.ID)))
					.where(TAG.TYPE.eq(tag.getType())
							.and(NOTICE_TAG.TAG.eq(tag.getId()))
							.and(NOTICE_TAG.END_DATE.isNull())
							.and(NOTICE_TAG.NOTICE.eq(noticeId)))
					.fetchOne();

			if (result != null) {
				ctx.getDslContext().update(NOTICE_TAG)
						.set(NOTICE_TAG.END_DATE,
								new java.sql.Timestamp((today).getTime()))
						.set(NOTICE_TAG.USER, tag.getUser().getId())
						.where(NOTICE_TAG.ID.eq(result.getValue(NOTICE_TAG.ID)))
						.execute();
			}
		}

		for (Tag tag : addLabels) {
			evalOpenTag(ctx, noticeId, tag, today);

			ctx.getDslContext().insertInto(NOTICE_TAG)
					.set(NOTICE_TAG.NOTICE, noticeId)
					.set(NOTICE_TAG.TAG, tag.getId())
					.set(NOTICE_TAG.START_DATE,
							new java.sql.Timestamp((today).getTime()))
					.set(NOTICE_TAG.USER, tag.getUser().getId()).execute();
		}

		return buildNoticeById(ctx, noticeId);
	}

	private static Notice buildNotice(AONContext ctx, Record record) {

		Notice notice = new Notice();
		int noticeId = record.getValue(NOTICE.ID);
		notice.setId(noticeId);
		notice.setDomain(ctx.getDomainId());
		notice.setStartDate(record.getValue(NOTICE.DATE));
		notice.setCompany(record.getValue(NOTICE.COMPANY));
		notice.setSource(record.getValue(NOTICE.SOURCE));

		try {

			User user = UserDAO.getUser(ctx, record.getValue(NOTICE.SENDER));

			notice.setSender(user);
			notice.setTitle(record.getValue(NOTICE.SUBJECT));
			//notice.setRecipient(record.getValue(NOTICE.RECIPIENT));

			Result<Record> statusListRecord = ctx.getDslContext()
					.selectFrom(NOTICE_TAG.rightOuterJoin(TAG)
							.on(NOTICE_TAG.TAG.eq(TAG.ID)))
					.where(NOTICE_TAG.NOTICE.eq(noticeId)
							.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value()))
							.and(TAG.DOMAIN.eq(0)))
					.orderBy(NOTICE_TAG.START_DATE.desc()).fetch();

			if (statusListRecord != null) {
				for (Record statusRecord : statusListRecord) {
					Tag tag = new Tag();
					tag.setId(statusRecord.getValue(TAG.ID));
					tag.setDomain(statusRecord.getValue(NOTICE.DOMAIN));
					tag.setName(statusRecord.getValue(TAG.NAME));
					tag.setStartDate(
							statusRecord.getValue(NOTICE_TAG.START_DATE));
					tag.setEndDate(statusRecord.getValue(NOTICE_TAG.END_DATE));
					tag.setType(statusRecord.getValue(TAG.TYPE));
					tag.setColor(statusRecord.getValue(TAG.COLOR));

					if (statusRecord.getValue(NOTICE_TAG.USER) != null) {
						tag.setUser(UserDAO.getUser(ctx,
								statusRecord.getValue(NOTICE_TAG.USER)));
					} else {
						tag.setUser(UserDAO.getUser(ctx,
								notice.getSender().getId()));
					}
					notice.addTag(tag);

					if (statusRecord.getValue(NOTICE_TAG.END_DATE) == null)
						notice.setStatus(statusRecord.getValue(TAG.NAME));
				}
			}

			Result<Record> priorityListRecord = ctx.getDslContext()
					.selectFrom(NOTICE_TAG.rightOuterJoin(TAG)
							.on(NOTICE_TAG.TAG.eq(TAG.ID)))
					.where(NOTICE_TAG.NOTICE.eq(noticeId)
							.and(TAG.TYPE.eq(TagType.OFFICE_PRIORITY.value()))
							.and(TAG.DOMAIN.eq(ctx.getDomainId())))
					.orderBy(NOTICE_TAG.START_DATE.desc()).fetch();

			if (priorityListRecord != null) {
				for (Record priorityRecord : priorityListRecord) {
					Tag tag = new Tag();
					tag.setId(priorityRecord.getValue(TAG.ID));
					tag.setDomain(priorityRecord.getValue(NOTICE.DOMAIN));
					tag.setName(priorityRecord.getValue(TAG.NAME));
					tag.setStartDate(
							priorityRecord.getValue(NOTICE_TAG.START_DATE));
					tag.setEndDate(
							priorityRecord.getValue(NOTICE_TAG.END_DATE));
					tag.setType(priorityRecord.getValue(TAG.TYPE));
					tag.setColor(priorityRecord.getValue(TAG.COLOR));

					if (priorityRecord.getValue(NOTICE_TAG.USER) != null) {
						tag.setUser(UserDAO.getUser(ctx,
								priorityRecord.getValue(NOTICE_TAG.USER)));
					} else {
						tag.setUser(UserDAO.getUser(ctx,
								notice.getSender().getId()));
					}
					notice.addTag(tag);

					if (priorityRecord.getValue(NOTICE_TAG.END_DATE) == null)
						notice.setPriority(priorityRecord.getValue(TAG.NAME));
				}
			}

			Result<Record> typeListRecord = ctx.getDslContext()
					.selectFrom(NOTICE_TAG.rightOuterJoin(TAG)
							.on(NOTICE_TAG.TAG.eq(TAG.ID)))
					.where(NOTICE_TAG.NOTICE.eq(noticeId)
							.and(TAG.TYPE.eq(TagType.OFFICE_TYPE.value()))
							.and(TAG.DOMAIN.eq(ctx.getDomainId())))
					.orderBy(NOTICE_TAG.START_DATE.desc()).fetch();

			if (typeListRecord != null) {
				for (Record typeRecord : typeListRecord) {
					Tag tag = new Tag();
					tag.setId(typeRecord.getValue(TAG.ID));
					tag.setDomain(typeRecord.getValue(NOTICE.DOMAIN));
					tag.setName(typeRecord.getValue(TAG.NAME));
					tag.setStartDate(
							typeRecord.getValue(NOTICE_TAG.START_DATE));
					tag.setEndDate(typeRecord.getValue(NOTICE_TAG.END_DATE));
					tag.setType(typeRecord.getValue(TAG.TYPE));
					tag.setColor(typeRecord.getValue(TAG.COLOR));

					if (typeRecord.getValue(NOTICE_TAG.USER) != null) {
						tag.setUser(UserDAO.getUser(ctx,
								typeRecord.getValue(NOTICE_TAG.USER)));
					} else {
						tag.setUser(UserDAO.getUser(ctx,
								notice.getSender().getId()));
					}
					notice.addTag(tag);

					if (typeRecord.getValue(NOTICE_TAG.END_DATE) == null)
						notice.setType(typeRecord.getValue(TAG.NAME));
				}
			}

			ctx.getDslContext().select().from(NOTICE_TAG).rightOuterJoin(TAG)
					.on(NOTICE_TAG.TAG.eq(TAG.ID))
					.where(NOTICE_TAG.NOTICE.eq(noticeId)
							.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value()))
							.and(TAG.DOMAIN.eq(ctx.getDomainId()))
							.and(NOTICE_TAG.END_DATE.isNull()))
					.orderBy(NOTICE_TAG.END_DATE.desc()).fetch().stream()
					.forEach(noticeTag -> {
						Tag tag = buildTag(noticeTag);

						Integer userId = noticeTag
								.getValue(NOTICE_TAG.USER) != null
										? noticeTag.getValue(NOTICE_TAG.USER)
										: notice.getSender().getId();
						User userTag = UserDAO.getUser(ctx, userId);
						tag.setUser(userTag);
						tag.setStartDate(
								noticeTag.getValue(NOTICE_TAG.START_DATE));
						tag.setEndDate(noticeTag.getValue(NOTICE_TAG.END_DATE));
						notice.addTag(tag);
					});

			getComents(ctx, noticeId, notice.getComments());

		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}

		return notice;
	}

	public static NoticeContainer getOpenNotices(AONContext ctx, String pSince,
			String sender, int offset, List<String> tags, String text) {

		List<Notice> notices = new LinkedList<Notice>();		

		Date since = getSince(pSince);
		Date tomorrow = getTomorrow();

		SelectConditionStep<Record1<Integer>> openId = getOpenNoticesId(
				ctx.getDslContext());
		SelectConditionStep<Record1<Integer>> reopenId = getReOpenNoticesId(
				ctx.getDslContext());
		
		SelectConditionStep<Record> select = ctx.getDslContext()
				.select()				
				.from(NOTICE).rightOuterJoin(NOTICE_TAG)
				.on(NOTICE.ID.eq(NOTICE_TAG.NOTICE)).rightOuterJoin(TAG)
				.on(NOTICE_TAG.TAG.eq(TAG.ID))
				.where(NOTICE.DOMAIN.eq(ctx.getDomainId()).and(NOTICE.NOTICE_.isNull())
						.and(NOTICE.TYPE.eq(NoticeType.TICKET.value()))
						.and(TAG.ID.eq(openId).or(TAG.ID.eq(reopenId)))
						.and(NOTICE_TAG.END_DATE.isNull()));

		if (since != null) {
			select = select.and(
					NOTICE.DATE.between(new java.sql.Timestamp(since.getTime()),
							new java.sql.Timestamp(tomorrow.getTime())));
		}

		if (text != null) {
			select = select.and(NOTICE.SUBJECT.like("%" + text + "%"));
		}

		if (sender != null) {			
			select = select.and(NOTICE.COMPANY.eq(sender));
		}
		
		int countAux = select.fetch().size();

		Result<Record> record = select.groupBy(NOTICE.ID)
				.orderBy(NOTICE.DATE.desc()).limit(offset, 50).fetch();

		if (record != null) {

			record.stream().forEach(result -> {				
				Notice notice = buildNotice(ctx, result);
				if (tags.size() > 0) {
					boolean encontrado = false;
					for (Tag tag : notice.getTags()) {
						for (String name : tags) {
							if (tag.getName().compareTo(name) == 0) {
								encontrado = true;
								break;
							}
						}
					}
					if (encontrado)
						notices.add(notice);
				}
				else {
					notices.add(notice);	
				}
			});
		}
		
		NoticeContainer container = new NoticeContainer();
		container.setCount(countAux);
		container.setNotices(notices);

		return container;
	}

	public static NoticeContainer getClosedIsues(AONContext ctx, String pSince,
			String sender, int offset, List<String> tags, String text) {

		List<Notice> notices = new LinkedList<Notice>();

		Date since = getSince(pSince);
		Date tomorrow = getTomorrow();

		SelectConditionStep<Record1<Integer>> closedId = getClosedNoticesId(
				ctx.getDslContext());

		SelectConditionStep<Record> select = ctx.getDslContext().select()
				.from(NOTICE).rightOuterJoin(NOTICE_TAG)
				.on(NOTICE_TAG.NOTICE.eq(NOTICE.ID)).rightOuterJoin(TAG)
				.on(NOTICE_TAG.TAG.eq(TAG.ID))
				.where(NOTICE.DOMAIN.eq(ctx.getDomainId())
						.and(NOTICE.NOTICE_.isNull())
						.and(NOTICE.TYPE.eq(NoticeType.TICKET.value()))
						.and(TAG.ID.eq(closedId))
						.and(NOTICE_TAG.END_DATE.isNull()));

		if (since != null) {
			select = select.and(
					NOTICE.DATE.between(new java.sql.Timestamp(since.getTime()),
							new java.sql.Timestamp(tomorrow.getTime())));
		}

		if (sender != null) {
			select = select.and(NOTICE.COMPANY.eq(sender));
		}

		if (text != null) {
			select = select.and(NOTICE.SUBJECT.like("%" + text + "%"));
		}
		
		int countAux = select.fetch().size();

		Result<Record> record = select.orderBy(NOTICE.DATE.desc())
				.limit(offset, 50)
				.fetch();
		

		if (record != null)

			record.stream().forEach(result -> {
				Notice notice = buildNotice(ctx, result);
				if (tags.size() > 0) {
					boolean encontrado = false;
					for (Tag tag : notice.getTags()) {
						for (String name : tags) {
							if (tag.getName().compareTo(name) == 0) {
								encontrado = true;
								break;
							}
						}
					}
					if (encontrado)
						notices.add(notice);
				}
				else {
					notices.add(notice);	
				}
			});

		NoticeContainer container = new NoticeContainer();
		container.setCount(countAux);
		container.setNotices(notices);

		return container;
	}

	public static NoticeContainer getAllNotices(AONContext ctx, String pSince,
			String sender, int offset, List<String> tags, String text) {

		List<Notice> notices = new LinkedList<Notice>();

		Date since = getSince(pSince);
		Date tomorrow = getTomorrow();

		SelectConditionStep<Record1<Integer>> openId = getOpenNoticesId(
				ctx.getDslContext());
		SelectConditionStep<Record1<Integer>> reopenId = getReOpenNoticesId(
				ctx.getDslContext());
		SelectConditionStep<Record1<Integer>> closedId = getClosedNoticesId(
				ctx.getDslContext());

		SelectConditionStep<Record> select = ctx.getDslContext().select()
				.from(NOTICE.rightOuterJoin(NOTICE_TAG)
						.on(NOTICE.ID.eq(NOTICE_TAG.NOTICE)).rightOuterJoin(TAG)
						.on(NOTICE_TAG.TAG.eq(TAG.ID)))
				.where(NOTICE.DOMAIN.eq(ctx.getDomainId())
						.and(NOTICE.NOTICE_.isNull())
						.and(NOTICE.TYPE.eq(NoticeType.TICKET.value()))
						.and(TAG.ID.eq(openId)
								.or(TAG.ID.eq(reopenId)
										.or(TAG.ID.eq(closedId))))
						.and(NOTICE_TAG.END_DATE.isNull()));

		if (since != null) {
			select = select.and(
					NOTICE.DATE.between(new java.sql.Timestamp(since.getTime()),
							new java.sql.Timestamp(tomorrow.getTime())));
		}

		if (sender != null) {
			select = select.and(NOTICE.COMPANY.eq(sender));
		}

		if (text != null) {
			select = select.and(NOTICE.SUBJECT.like("%" + text + "%"));
		}
		
		int countAux = select.fetch().size();

		Result<Record> record = select.orderBy(NOTICE.DATE.desc())
				.limit(offset, 50).fetch();

		if (record != null) {
			record.stream().forEach(result -> {
				Notice notice = buildNotice(ctx, result);
				if (tags.size() > 0) {
					boolean encontrado = false;
					for (Tag tag : notice.getTags()) {
						for (String name : tags) {
							if (tag.getName().compareTo(name) == 0) {
								encontrado = true;
								break;
							}
						}
					}
					if (encontrado)
						notices.add(notice);
				}
				else {
					notices.add(notice);	
				}
			});
		}
	
		NoticeContainer container = new NoticeContainer();
		container.setCount(countAux);
		container.setNotices(notices);

		return container;
	}

	public static Notice createComment(AONContext ctx, Integer headId,
			Notice comment) {

		NoticeRecord noticeRecord = ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
				.set(NOTICE.DATE,
						new java.sql.Timestamp(
								comment.getStartDate().getTime()))
				.set(NOTICE.SENDER, comment.getSender().getId())
				.set(NOTICE.SUBJECT, comment.getBody())
				.set(NOTICE.TYPE, NoticeType.COMMENT.value())
				.set(NOTICE.NOTICE_, headId)
				.set(NOTICE.STATUS, NoticeStatus.OPEN.value())
				.set(NOTICE.PRIORITY, (byte) 0).returning().fetchOne();

		comment.setId(noticeRecord.getValue(NOTICE.ID));
		comment.setStartDate(noticeRecord.getValue(NOTICE.DATE));
		return comment;
	}

	public static Notice editComment(AONContext ctx, Integer commentId,
			String body) {

		ctx.getDslContext().update(NOTICE).set(NOTICE.SUBJECT, body)
				.where(NOTICE.ID.eq(commentId)).execute();

		NoticeRecord noticeRecord = ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.ID.eq(commentId)).fetchOne();
		Notice comment = new Notice();

		if (noticeRecord != null) {
			comment.setId(noticeRecord.getValue(NOTICE.ID));
			comment.setDomain(noticeRecord.getValue(NOTICE.DOMAIN));
			comment.setStartDate(noticeRecord.getValue(NOTICE.DATE));
			User user = UserDAO.getUser(ctx,
					noticeRecord.getValue(NOTICE.SENDER));
			comment.setSender(user);
			comment.setBody(noticeRecord.getValue(NOTICE.SUBJECT));
		}

		return comment;
	}

	public static void getComents(AONContext ctx, Integer headId,
			List<Notice> comments) {

		Result<NoticeRecord> result = ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.NOTICE_.eq(headId)
						.and(NOTICE.TYPE.eq(NoticeType.COMMENT.value())))
				.orderBy(NOTICE.DATE.asc()).fetch();

		if (result != null) {
			result.stream().forEach(record -> {
				Notice notice = new Notice();
				notice.setId(record.getValue(NOTICE.ID));
				notice.setDomain(record.getValue(NOTICE.DOMAIN));
				notice.setStartDate(record.getValue(NOTICE.DATE));
				User user = UserDAO.getUser(ctx,
						record.getValue(NOTICE.SENDER));
				notice.setSender(user);
				notice.setBody(record.getValue(NOTICE.SUBJECT));
				comments.add(notice);
			});
		}
	}

	public static Tag getTag(AONContext ctx, String name) {

		TagRecord tagRecord = ctx.getDslContext()
				.selectFrom(TAG).where(
						TAG.DOMAIN.eq(ctx.getDomainId()).and(TAG.NAME.eq(name))
								.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value())
										.or(TAG.TYPE.eq(TagType.OFFICE_PRIORITY
												.value()))
								.or(TAG.TYPE.eq(TagType.OFFICE_TYPE.value()))))
				.fetchOne();

		return buildTag(tagRecord);
	}

	public static List<Tag> getTags(AONContext ctx) {

		List<Tag> tags = new LinkedList<Tag>();

		ctx.getDslContext()
				.selectFrom(
						TAG)
				.where(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value())
						.or(TAG.TYPE.eq(TagType.OFFICE_PRIORITY.value()))
						.or(TAG.TYPE.eq(TagType.OFFICE_STATUS.value()))
						.or(TAG.TYPE.eq(TagType.OFFICE_TYPE.value()))
						.and(TAG.DOMAIN.eq(0)
								.or(TAG.DOMAIN.eq(ctx.getDomainId()))))
				.fetch().stream().forEach(record -> {
					Tag tag = buildTag(record);
					tags.add(tag);
				});

		return tags;
	}

	private static Tag buildTag(Record record) {

		Tag tag = new Tag();
		tag.setId(record.getValue(TAG.ID));
		tag.setDomain(record.getValue(TAG.DOMAIN));
		tag.setName(record.getValue(TAG.NAME));
		tag.setType(record.getValue(TAG.TYPE));
		tag.setColor(record.getValue(TAG.COLOR));

		return tag;

	}

	// -----------------------------------------------------------------------

	private static SelectConditionStep<Record1<Integer>> getOpenNoticesId(
			DSLContext dsl) {

		return dsl.select(TAG.ID).from(TAG).where(TAG.DOMAIN.eq(0))
				.and(TAG.NAME.eq(NoticeStatus.OPEN.getValue())
						.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value())));
	}

	private static SelectConditionStep<Record1<Integer>> getReOpenNoticesId(
			DSLContext dsl) {

		return dsl.select(TAG.ID).from(TAG).where(TAG.DOMAIN.eq(0))
				.and(TAG.NAME.eq(NoticeStatus.REOPEN.getValue())
						.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value())));
	}

	private static SelectConditionStep<Record1<Integer>> getClosedNoticesId(
			DSLContext dsl) {

		return dsl.select(TAG.ID).from(TAG).where(TAG.DOMAIN.eq(0))
				.and(TAG.NAME.eq(NoticeStatus.CLOSED.getValue())
						.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value())));
	}

	private static Notice buildNoticeById(AONContext ctx, Integer id) {

		NoticeRecord noticeRecord = ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.ID.eq(id)).fetchOne();

		return buildNotice(ctx, noticeRecord);
	}

	public static List<RegistryMedia> getRMedias(AONContext ctx,
			Integer parentDomain) {

		Cursor<Record> cursor = null;
		List<RegistryMedia> rmedias = new LinkedList<RegistryMedia>();

		try {

			if (parentDomain == null)
				return rmedias;

			SelectConditionStep<Record1<Integer>> domainSelect = ctx
					.getDslContext().select(DOMAIN.ID).from(DOMAIN)
					.where(DOMAIN.ID.eq(ctx.getDomainId())
							.and(DOMAIN.PARENT.eq(parentDomain)));

			cursor = ctx.getDslContext().select()
					.from(RMEDIA.rightOuterJoin(REGISTRY)
							.on(RMEDIA.REGISTRY.eq(REGISTRY.ID))
							.rightOuterJoin(CUSTOMER)
							.on(CUSTOMER.REGISTRY.eq(REGISTRY.ID)))
					.where(REGISTRY.DOMAIN.in(domainSelect)
							.and(CUSTOMER.STATUS.eq((byte) 0))
							.and(RMEDIA.MEDIA.eq(MediaType.CELLULAR.value())
									.or(RMEDIA.MEDIA
											.eq(MediaType.EMAIL.value()))))
					.fetchLazy();

			if (cursor != null) {

				for (Record value : cursor) {
					RegistryMedia rmedia = new RegistryMedia();
					rmedia.setId(value.getValue(RMEDIA.ID));
					rmedia.setDomain(value.getValue(RMEDIA.DOMAIN));
					rmedia.setMedia(value.getValue(RMEDIA.MEDIA));
					rmedia.setValue(value.getValue(RMEDIA.VALUE));
					rmedia.setComment(value.getValue(RMEDIA.COMMENT));

					Registry reg = new Registry();
					reg.setId(value.getValue(REGISTRY.ID));
					reg.setName(value.getValue(REGISTRY.NAME));
					reg.setAlias(value.getValue(REGISTRY.ALIAS));
					reg.setDocument(value.getValue(REGISTRY.DOCUMENT));
					rmedia.setRegistry(reg);

					rmedias.add(rmedia);
				}

			}

			return rmedias;

		} finally {
			if (cursor != null)
				cursor.close();
		}
	}
	
	private static final RegistryMediaPropertiesDAO RMEDIA_PROPERTIES = new RegistryMediaPropertiesDAO();
	protected static class RegistryMediaPropertiesDAO implements RegistryMediaProperties {
		protected Condition[] getConditions(RegistryMediaFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.REGISTRY);}
		@Override public Property<Byte> getMediaProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.MEDIA);}
		@Override public Property<String> getValueProperty() {return new FilterDAO.PropertyDAO<String>(RMEDIA.VALUE);}
		@Override public Property<String> getCommentProperty() {return new FilterDAO.PropertyDAO<String>(RMEDIA.COMMENT);}
		@Override public Property<Byte> getAdministrativeProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.ADMINISTRATIVE);}
		@Override public Property<Byte> getCommercialProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.COMMERCIAL);}
		@Override public Property<Byte> getTechnicalProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.TECHNICAL);}
		@Override public Property<Integer> getRaddressProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.RADDRESS);}
	}
	private static class FullRegistryMediaFiller implements Function<RmediaRecord, RegistryMedia> {

		@Override
		public RegistryMedia apply(RmediaRecord r) {
			return new RegistryMedia().setComment(r.getComment())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setMedia(r.getMedia())
					.setRegistry(new Registry().setId(r.getRegistry()))
					.setValue(r.getValue());
		}
	}
	
	public static LinkedList<RegistryMedia> getRMediaList(AONContext ctx, RegistryMediaFilter filter) {
		return ctx.getDslContext().select().from(RMEDIA).where(RMEDIA_PROPERTIES.getConditions(filter)).fetchInto(RMEDIA)
				.stream().map(new FullRegistryMediaFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<User> getUserFromNotices(AONContext ctx, Integer parentDomain) {
		
		Cursor<Record> cursor = null;
		List<User> users = new LinkedList<User>();
	
		try {
			
			SelectConditionStep<Record1<Integer>> noticeSelect = ctx.getDslContext()
					.select(NOTICE.SENDER)
					.from(NOTICE)
					.where(NOTICE.DOMAIN.eq(ctx.getDomainId()));
			
			if (parentDomain != null) 
				noticeSelect = noticeSelect.or(NOTICE.DOMAIN.eq(parentDomain));
					
			
			cursor = ctx.getDslContext()
					.select(USER.fields())
					.from(USER)
					.where(USER.ID.in(noticeSelect)
							.and(USER.ACTIVE.eq( (byte) 1)))
					.orderBy(USER.NAME.asc())
					.fetchLazy();
			
			for (Record record : cursor) {
				User user = new User();
				user.setId(record.getValue(USER.ID));
				user.setDomain(record.getValue(USER.DOMAIN));
				user.setLogin(record.getValue(USER.LOGIN));
				user.setName(record.getValue(USER.NAME));
				user.setActive(record.getValue(USER.ACTIVE) != 0);
				users.add(user);
			}
			
			return users;

			
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public static List<Registry> getRegistries(AONContext ctx,
			Integer parentDomain) {

		Cursor<Record> cursor = null;
		List<Registry> registries = new LinkedList<Registry>();

		try {

			if (parentDomain == null)
				return registries;

			SelectConditionStep<Record1<Integer>> domainSelect = ctx
					.getDslContext().select(DOMAIN.ID).from(DOMAIN)
					.where(DOMAIN.ID.eq(ctx.getDomainId())
							.and(DOMAIN.PARENT.eq(parentDomain)));

			cursor = ctx.getDslContext().select()
					.from(REGISTRY.rightOuterJoin(CUSTOMER)
							.on(REGISTRY.ID.eq(CUSTOMER.REGISTRY)))
					.where(REGISTRY.DOMAIN.in(domainSelect))
					.and(CUSTOMER.STATUS.eq((byte) 0)).fetchLazy();

			for (Record registry : cursor) {
				Registry reg = new Registry();
				reg.setId(registry.getValue(REGISTRY.ID));
				reg.setName(registry.getValue(REGISTRY.NAME));
				reg.setAlias(registry.getValue(REGISTRY.ALIAS));
				reg.setDocument(registry.getValue(REGISTRY.DOCUMENT));
				registries.add(reg);
			}

			return registries;

		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	private static Date getTomorrow() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	private static Date getSince(String pSince) {

		Date since = null;
		try {
			since = sdf.parse(pSince);
		} catch (Exception ex) {
			return null;
		}

		if (since == null)
			return null;
		else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(since);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		}
	}
	
	public static NotificationInfo getNotificationInfo(AONContext ctx){
		Result<AppParamRecord> mail = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MAIL.getValue())).limit(1).fetchInto(APP_PARAM);
		Result<AppParamRecord> auto = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_AUTO.getValue())).limit(1).fetchInto(APP_PARAM);
		Result<AppParamRecord> bcc = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_BCC.getValue())).limit(1).fetchInto(APP_PARAM);
		Result<AppParamRecord> history = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_HISTORY.getValue())).limit(1).fetchInto(APP_PARAM);
		Result<AppParamRecord> signature = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue())).limit(1).fetchInto(APP_PARAM);
		Result<AppParamRecord> mode = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MODE.getValue())).limit(1).fetchInto(APP_PARAM);

		Result<AppParamRecord> logo = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_LOGO.getValue())).limit(1).fetchInto(APP_PARAM);
					
		return new NotificationInfo()
				.setCommentsHistory(history.isNotEmpty() && history.get(0).getValue().length() == 2 
						? history.get(0).getValue().substring(0, 1).equals("1") : false)
				.setStatusHistory(history.isNotEmpty() && history.get(0).getValue().length() == 2
						? history.get(0).getValue().substring(1).equals("1") : false)
				.setMailAccount(mail.isNotEmpty() && mail.get(0).getValue() != null ? SecurityDAO.getMailAccount(ctx, f-> f.getIdProperty().eq(Integer.parseInt(mail.get(0).getValue()))) : null)
				
				.setNotifyOpen(auto.isNotEmpty() && auto.get(0).getValue().length() == 4 
						? auto.get(0).getValue().substring(0, 1).equals("1") : false)
				.setNotifyClose(auto.isNotEmpty() && auto.get(0).getValue().length() == 4
						? auto.get(0).getValue().substring(1, 2).equals("1") : false)
				.setNotifyReopen(auto.isNotEmpty() && auto.get(0).getValue().length() == 4
						? auto.get(0).getValue().substring(2, 3).equals("1") : false)
				.setNotifyComment(auto.isNotEmpty() && auto.get(0).getValue().length() == 4
						? auto.get(0).getValue().substring(3).equals("1") : false)
				
				.setSignature(signature.isNotEmpty() && signature.get(0).getValue()!= null ? SecurityDAO.getSignature(ctx, Integer.parseInt(signature.get(0).getValue())) : null)
				.setBcc(bcc.isNotEmpty() ? bcc.get(0).getValue() : "")
				.setMode(mode.isNotEmpty() ?  Integer.parseInt(mode.get(0).getValue()): 1)
				.setIsLogo(logo.isNotEmpty() ? logo.get(0).getValue().substring(0,1).equals("1"): true)
				.setLogoPercentage(logo.isNotEmpty() && logo.get(0).getId() != null ? Integer.parseInt(logo.get(0).getValue().substring(1)): 20);
	}
	
	public static void insertNotificationInfo(AONContext ctx, String data, AppParam appParam){
		if(appParam.equals(AppParam.NOTICE_NOTIFICATION_AUTO)){
			Result<Record1<Integer>> auto = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_AUTO.getValue())).limit(1).fetch();
			if(auto.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_AUTO.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_AUTO.getValue())).execute();
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_BCC)){
			Result<Record1<Integer>> bcc = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_BCC.getValue())).limit(1).fetch();
			if(bcc.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_BCC.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_BCC.getValue())).execute();
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_HISTORY)){
			Result<Record1<Integer>> history = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_HISTORY.getValue())).limit(1).fetch();
			if(history.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_HISTORY.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_HISTORY.getValue())).execute();
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_LOGO)){
			Result<Record1<Integer>> logo = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_LOGO.getValue())).limit(1).fetch();
			if(logo.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_LOGO.getValue(),data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_LOGO.getValue())).execute();
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_MAIL)){
			Result<Record1<Integer>> mail = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MAIL.getValue())).limit(1).fetch();
			if(mail.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_MAIL.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MAIL.getValue())).execute();	
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_MODE)){
			Result<Record1<Integer>> mode = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MODE.getValue())).limit(1).fetch();
			if(mode.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_MODE.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MODE.getValue())).execute();
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_SIGNATURE)){
			Result<Record1<Integer>> sign = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue())).limit(1).fetch();
			if(sign.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue())).execute();
		}
	}

	
	public static void insertNotificationInfo(AONContext ctx, NotificationInfo notificationInfo){
		Result<Record1<Integer>> auto = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
		.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_AUTO.getValue())).limit(1).fetch();
		String open = notificationInfo.getNotifyOpen() ? "1" : "0";
		String close = notificationInfo.getNotifyClose() ? "1" : "0";
		String reopen = notificationInfo.getNotifyReopen() ? "1" : "0";
		String comment = notificationInfo.getNotifyComment() ? "1" : "0";
		if(auto.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_AUTO.getValue(), open+close+reopen+comment).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, open+close+reopen+comment)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_AUTO.getValue())).execute();

		
		Result<Record1<Integer>> bcc = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_BCC.getValue())).limit(1).fetch();
		if(bcc.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_BCC.getValue(), notificationInfo.getBcc()).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, notificationInfo.getBcc())
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_BCC.getValue())).execute();

			
		Result<Record1<Integer>> history = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_HISTORY.getValue())).limit(1).fetch();
		String commentsHistory = notificationInfo.getCommentsHistory() ? "1" : "0";
		String statusHistory = notificationInfo.getStatusHistory() ? "1" : "0";
		if(history.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_HISTORY.getValue(), commentsHistory + statusHistory).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, commentsHistory + statusHistory)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_HISTORY.getValue())).execute();
		
		Result<Record1<Integer>> mail = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MAIL.getValue())).limit(1).fetch();
		if(mail.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_MAIL.getValue(), notificationInfo.getMailAccount().getId() != null ?
					notificationInfo.getMailAccount().getId().toString(): null).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE,  notificationInfo.getMailAccount().getId() != null ?
				notificationInfo.getMailAccount().getId().toString(): null)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MAIL.getValue())).execute();	
		
		Result<Record1<Integer>> sign = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue())).limit(1).fetch();
		if(sign.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue(), notificationInfo.getSignature().getId() != null ? 
					notificationInfo.getSignature().getId().toString(): null).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, notificationInfo.getSignature().getId() != null ?
				notificationInfo.getSignature().getId().toString(): null)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue())).execute();

		Result<Record1<Integer>> mode = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MODE.getValue())).limit(1).fetch();
		if(mode.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_MODE.getValue(), notificationInfo.getMode() != null ? 
					notificationInfo.getMode().toString(): "1").execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, notificationInfo.getMode() != null ?
				notificationInfo.getMode().toString(): "1")
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MODE.getValue())).execute();

		Result<Record1<Integer>> logo = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_LOGO.getValue())).limit(1).fetch();
		String val ="0";
		if(notificationInfo.getIsLogo()) val = "1"; 
		
		if(logo.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_LOGO.getValue(), val + notificationInfo.getLogoPercentage().toString()).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, val + notificationInfo.getLogoPercentage().toString())
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_LOGO.getValue())).execute();

	}
}
