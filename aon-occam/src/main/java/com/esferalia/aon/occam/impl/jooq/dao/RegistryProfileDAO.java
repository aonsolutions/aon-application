package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Question.QUESTION;
import static com.esferalia.aon.jooq.tables.Rprofile.RPROFILE;

import java.sql.Timestamp;
import java.util.Date;

import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.QuestionRecord;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.registry.QuestionType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class RegistryProfileDAO {
	
	private RegistryProfileDAO() {}
	
	public static void save(CloseableAONContext ctx, Integer registry, String questionAlias, String value) {
		Result<QuestionRecord> questions = ctx.getDslContext().selectFrom(QUESTION)
				.where(QUESTION.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.and(QUESTION.ALIAS.eq(questionAlias))
				.fetch();
		
		if(!questions.isEmpty())
			insertRProfile(ctx, registry, questions.get(0), value);
			
	}

	private static void insertRProfile(CloseableAONContext ctx, Integer registry, QuestionRecord questionRecord, String value) {
		switch (QuestionType.safeValueOf(questionRecord.getValue(QUESTION.TYPE))) {
		case TEXT:
			insertValueText(ctx, registry, questionRecord.getId(), value);
			break;
		case DATE:
			insertValueDate(ctx, registry, questionRecord.getId(), value);
			break;
		case NUMBER:
			insertValueNumber(ctx, registry, questionRecord.getId(), value);
			break;
		default:
			break;
		}
	}

	private static void insertValueText(CloseableAONContext ctx, Integer registry, Integer questionId, String value) {
		ctx.getDslContext().insertInto(RPROFILE)
			.set(RPROFILE.DOMAIN, ctx.getDomainId())
			.set(RPROFILE.REGISTRY, registry)
			.set(RPROFILE.LAST_UPDATE, new Timestamp(new java.util.Date().getTime()))
			.set(RPROFILE.QUESTION, questionId)
			.set(RPROFILE.VALUE_TEXT, value)
			.execute();
	}

	private static void insertValueDate(CloseableAONContext ctx, Integer registry, Integer questionId, String value) {
		try {
			
			Date date = AonDateUtils.parse(value.replace("\"", ""));
			if(date == null) date = new Date(Long.parseLong(value));
			
			ctx.getDslContext().insertInto(RPROFILE)
			.set(RPROFILE.DOMAIN, ctx.getDomainId())
			.set(RPROFILE.REGISTRY, registry)
			.set(RPROFILE.LAST_UPDATE, new Timestamp(new java.util.Date().getTime()))
			.set(RPROFILE.QUESTION, questionId)
			.set(RPROFILE.VALUE_DATE, new Timestamp(date.getTime()))
			.execute();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static void insertValueNumber(CloseableAONContext ctx, Integer registry, Integer questionId, String value) {
		try {
			
			ctx.getDslContext().insertInto(RPROFILE)
			.set(RPROFILE.DOMAIN, ctx.getDomainId())
			.set(RPROFILE.REGISTRY, registry)
			.set(RPROFILE.LAST_UPDATE, new Timestamp(new java.util.Date().getTime()))
			.set(RPROFILE.QUESTION, questionId)
			.set(RPROFILE.VALUE_NUMBER, Double.parseDouble(value))
			.execute();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
