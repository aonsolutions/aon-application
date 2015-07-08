package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Note.NOTE;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.callcenter.Issue;
import com.esferalia.aon.occam.api.model.callcenter.IssueComment;


public class GroupwareDAO {


	public static ArrayList<Issue> getOpenIssues(AONContext ctx, int domain, String subject) {
		ctx.checkRead();
 		final ArrayList<Issue> list = new ArrayList<Issue>();
		ctx.getDslContext()
			.select(NOTICE.ID, NOTICE.DOMAIN, NOTICE.DATE,
					NOTICE.SENDER, NOTICE.WORK_GROUP,
					NOTICE.RECIPIENT, NOTICE.SOURCE,
					NOTICE.COMPANY, NOTICE.PHONE,
					NOTICE.SUBJECT, NOTICE.STATUS,
					NOTICE.TYPE, NOTICE.PRIORITY)
			.from(NOTICE)
			.where(NOTICE.DOMAIN.equal(domain))
				.and(NOTICE.TYPE.equal((byte)4))
				.and(NOTICE.SUBJECT.like(subject!=null?"%"+subject+"%":"%"))
				.and(NOTICE.STATUS.notEqual((byte)0))
			.fetch()
			.stream()
			.forEach( record -> {
				Issue issue = new Issue();
				issue.setId(record.getValue(NOTICE.ID));
				issue.setDomain(record.getValue(NOTICE.DOMAIN)); 
				issue.setDate(record.getValue(NOTICE.DATE));
				issue.setSenderId(record.getValue(NOTICE.SENDER)); 
				issue.setWorkGroupId(record.getValue(NOTICE.WORK_GROUP));
				issue.setRecipientId(record.getValue(NOTICE.RECIPIENT));
				issue.setSource(record.getValue(NOTICE.SOURCE));
				issue.setCompany(record.getValue(NOTICE.COMPANY));
				issue.setPhone(record.getValue(NOTICE.PHONE));
				issue.setSubject(record.getValue(NOTICE.SUBJECT));
				issue.setStatus(record.getValue(NOTICE.STATUS));
				issue.setType(record.getValue(NOTICE.TYPE));
				issue.setPriority(record.getValue(NOTICE.PRIORITY));
				list.add(issue);
			});
		return list; 
	}

	public static ArrayList<Issue> getClosedIssues(AONContext ctx, int domain, String subject) {
		ctx.checkRead();
		final ArrayList<Issue> list = new ArrayList<Issue>();
		ctx.getDslContext()
		.select(NOTICE.ID, NOTICE.DOMAIN, NOTICE.DATE,
				NOTICE.SENDER, NOTICE.WORK_GROUP,
				NOTICE.RECIPIENT, NOTICE.SOURCE,
				NOTICE.COMPANY, NOTICE.PHONE,
				NOTICE.SUBJECT, NOTICE.STATUS,
				NOTICE.TYPE, NOTICE.PRIORITY)
				.from(NOTICE)
				.where(NOTICE.DOMAIN.equal(domain))
					.and(NOTICE.TYPE.equal((byte)4))
					.and(NOTICE.SUBJECT.like(subject!=null?"%"+subject+"%":"%"))
					.and(NOTICE.STATUS.equal((byte)0))
				.fetch()
				.stream()
				.forEach( record -> {
					Issue issue = new Issue();
					issue.setId(record.getValue(NOTICE.ID));
					issue.setDomain(record.getValue(NOTICE.DOMAIN)); 
					issue.setDate(record.getValue(NOTICE.DATE));
					issue.setSenderId(record.getValue(NOTICE.SENDER)); 
					issue.setWorkGroupId(record.getValue(NOTICE.WORK_GROUP));
					issue.setRecipientId(record.getValue(NOTICE.RECIPIENT));
					issue.setSource(record.getValue(NOTICE.SOURCE));
					issue.setCompany(record.getValue(NOTICE.COMPANY));
					issue.setPhone(record.getValue(NOTICE.PHONE));
					issue.setSubject(record.getValue(NOTICE.SUBJECT));
					issue.setStatus(record.getValue(NOTICE.STATUS));
					issue.setType(record.getValue(NOTICE.TYPE));
					issue.setPriority(record.getValue(NOTICE.PRIORITY));
					list.add(issue);
				});
		return list; 
	}

	public static ArrayList<IssueComment> getIssueComments(AONContext ctx,
			int issue) {
		ctx.checkRead();
		final ArrayList<IssueComment> list = new ArrayList<IssueComment>();
		ctx.getDslContext()
			.select(NOTE.ID, NOTE.DOMAIN, NOTE.DATE,
					NOTE.NOTE_, NOTE.OWNER,
					NOTE.SUBJECT)
			.from(NOTE)
			.where(NOTE.DOMAIN.equal(issue))
			.fetch()
			.stream()
			.forEach( record -> {
				IssueComment comment = new IssueComment();
				comment.setId(record.getValue(NOTE.ID));
				comment.setDomain(record.getValue(NOTE.DOMAIN)); 
				comment.setDate(record.getValue(NOTE.DATE));
				comment.setNote(record.getValue(NOTE.NOTE_)); 
				comment.setSubject(record.getValue(NOTE.SUBJECT));
				comment.setOwnerId(record.getValue(NOTE.OWNER));
				list.add(comment);
			});
		return list; 
	}
	
	
	
	public static void insert(AONContext ctx, Issue issue) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
					ctx.getDslContext()
							.insertInto(NOTICE, NOTICE.DOMAIN, NOTICE.DATE,
									NOTICE.SENDER, NOTICE.WORK_GROUP,
									NOTICE.RECIPIENT, NOTICE.SOURCE,
									NOTICE.COMPANY, NOTICE.PHONE,
									NOTICE.SUBJECT, NOTICE.STATUS, NOTICE.TYPE,
									NOTICE.PRIORITY)
							.values(issue.getDomain(), issue.getDate(),
									issue.getSenderId(),
									issue.getWorkGroupId(),
									issue.getRecipientId(), issue.getSource(),
									issue.getCompany(), issue.getPhone(),
									issue.getSubject(), issue.getStatus(),
									issue.getType(), issue.getPriority())
							.execute();
		});
	}
	
	public static void insert(AONContext ctx, IssueComment comment) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
					ctx.getDslContext()
							.insertInto(NOTE, NOTE.DOMAIN, NOTE.DATE,
									NOTE.NOTE_, NOTE.OWNER, NOTE.SUBJECT)
							.values(comment.getDomain(), comment.getDate(),
									comment.getNote(), comment.getOwnerId(),
									comment.getSubject()).execute();
		});
	}
	
	
	
}
