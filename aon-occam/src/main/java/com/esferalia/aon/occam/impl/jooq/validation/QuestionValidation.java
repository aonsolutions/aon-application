package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.List;
import java.util.function.BiConsumer;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

import static com.esferalia.aon.jooq.tables.Question.QUESTION;


public class QuestionValidation {
	
	private QuestionValidation() {
		
	}
	
	/**
	 * Throws an exception if the question is null
	 */
	private static final BiConsumer<AONContext, Question> NULL = (ctx, question) -> {
		if (question == null)
			throw new AonCoreException(AonError.QUESTION_NULL.getMessage());
	};
	
	/**
	 * Throws an exception if the question is empty
	 */
	private static final BiConsumer<AONContext, Question> EMPTY = (ctx, question) -> {
		if (question != null && question.isEmpty())
			throw new AonCoreException(AonError.QUESTION_EMPTY.getMessage());
	};
	
	/**
	 * Throws an exception if the domain is null
	 */
	private static final BiConsumer<AONContext, Question> EMPTY_DOMAIN = (ctx, question) -> {
		if (question != null && question.getDomain() == null)
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * Throws an exception if the text is null
	 */
	private static final BiConsumer<AONContext, Question> NULL_QUESTION_TEXT = (ctx, question) -> {
		if (question != null && question.getText() == null)
			throw new AonCoreException(AonError.NULL_QUESTION_TEXT.getMessage());
	};
	
	/**
	 * Throws an exception if the text is empty
	 */
	private static final BiConsumer<AONContext, Question> EMPTY_QUESTION_TEXT = (ctx, question) -> {
		if (question != null && question.getText() != null && question.getText().isEmpty())
			throw new AonCoreException(AonError.EMPTY_QUESTION_TEXT.getMessage());
	};
	
	/**
	 * Throws an exception if the type is null
	 */
	private static final BiConsumer<AONContext, Question> NULL_TYPE = (ctx, question) -> {
		if (question != null && question.getType() == null)
			throw new AonCoreException(AonError.NULL_TYPE.getMessage());
	};
	
	/**
	 * Throws an exception if the size of the question_text is invalid
	 */
	private static final BiConsumer<AONContext, Question> INVALID_SIZE_QUESTION_TEXT = (ctx, question) -> {
		if (question != null && question.getText() != null && question.getText().length() > QUESTION.QUESTION_TEXT.getDataType().length())
			throw new AonCoreException(AonError.INVALID_SIZE_QUESTION_TEXT.getMessage());
	};
	
	/**
	 * Throws an exception if the size of the alias is invalid
	 */
	private static final BiConsumer<AONContext, Question> INVALID_SIZE_ALIAS = (ctx, question) -> {
		if (question != null && question.getAlias() != null) {
			if (question.getAlias().length() > QUESTION.ALIAS.getDataType().length())
				throw new AonCoreException(AonError.INVALID_SIZE_ALIAS.getMessage());
		}
	};
	
	/**
	 * Throws an exception if the alias is repeated
	 */
	private static final BiConsumer<AONContext, Question> REPEATED_ALIAS = (ctx, question) -> {
		if (AonStringUtils.isNotBlank(question.getAlias()) && checkAlias(ctx, question)) {
			throw new AonCoreException(AonError.REPEATED_ALIAS.getMessage());
		}
	};
	
	public static void validate(AONContext ctx, Question question) throws AonCoreException{
		NULL.andThen(EMPTY)
		.andThen(EMPTY_DOMAIN)
		.andThen(EMPTY_QUESTION_TEXT)
		.andThen(NULL_QUESTION_TEXT)
		.andThen(NULL_TYPE)
		.andThen(INVALID_SIZE_QUESTION_TEXT)
		.andThen(INVALID_SIZE_ALIAS)
		.andThen(REPEATED_ALIAS)
		.accept(ctx, question);
	}
	
	/**
	 * Checks if an alias is repeated
	 * @param ctx the context
	 * @param question the question
	 * @return true if the alias is repeated, false otherwise
	 */
	public static boolean checkAlias(AONContext ctx, Question question) {
		Condition condition = question.getId() == null ? DSL.trueCondition() : QUESTION.ID.ne(question.getId());
		List<Record> questionRecords = ctx.getDslContext().select().from(QUESTION)
			.where(QUESTION.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.and(QUESTION.ALIAS.eq(question.getAlias()))
		 	.and(condition)
			.fetch();
			
		return !questionRecords.isEmpty();
	}

}
