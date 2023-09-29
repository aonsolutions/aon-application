package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.List;
import java.util.function.BiConsumer;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

import static com.esferalia.aon.jooq.tables.Question.QUESTION;

public class QuestionValidation {
	
	private QuestionValidation() {
		
	}
	
	// the object is null
	private static final BiConsumer<AONContext, Question> NULL = (ctx, question) -> {
		if (question == null)
			throw new AonCoreException(AonError.QUESTION_NULL.getMessage());
	};
	
	// the object is empty
	private static final BiConsumer<AONContext, Question> EMPTY = (ctx, question) -> {
		if (question.isEmpty())
			throw new AonCoreException(AonError.QUESTION_EMPTY.getMessage());
	};
	
	// the domain is empty (not_null)
	private static final BiConsumer<AONContext, Question> EMPTY_DOMAIN = (ctx, question) -> {
		if (question.getDomain() == null)
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	// the question_text is empty (not_null)
	private static final BiConsumer<AONContext, Question> EMPTY_QUESTION_TEXT = (ctx, question) -> {
		if (question.getText() == null)
			throw new AonCoreException(AonError.EMPTY_QUESTION_TEXT.getMessage());
	};
	
	// the type is empty (not_null)
	private static final BiConsumer<AONContext, Question> EMPTY_TYPE = (ctx, question) -> {
		if (question.getType() == null)
			throw new AonCoreException(AonError.EMPTY_TYPE.getMessage());
	};
	
	// the size of question_text is valid
	private static final BiConsumer<AONContext, Question> INVALID_SIZE_QUESTION_TEXT = (ctx, question) -> {
		if (question.getText().length() > QUESTION.QUESTION_TEXT.getDataType().length())
			throw new AonCoreException(AonError.INVALID_SIZE_QUESTION_TEXT.getMessage());
	};
	
	// the size of alias is valid
	private static final BiConsumer<AONContext, Question> INVALID_SIZE_ALIAS = (ctx, question) -> {
		if (question.getAlias() != null) {
			if (question.getAlias().length() > QUESTION.ALIAS.getDataType().length())
				throw new AonCoreException(AonError.INVALID_SIZE_ALIAS.getMessage());
		}
	};
	
	// the alias is not repeated
	private static final BiConsumer<AONContext, Question> REPEATED_ALIAS = (ctx, question) -> {
		if (checkAlias(ctx, question.getAlias())) {
			throw new AonCoreException(AonError.REPEATED_ALIAS.getMessage());
		}
	};
	
	public static void validate(AONContext ctx, Question question) throws AonCoreException{
		NULL.andThen(EMPTY)
		.andThen(EMPTY_DOMAIN)
		.andThen(EMPTY_QUESTION_TEXT)
		.andThen(EMPTY_TYPE)
		.andThen(INVALID_SIZE_QUESTION_TEXT)
		.andThen(INVALID_SIZE_ALIAS)
		.andThen(REPEATED_ALIAS)
		.accept(ctx, question);
	}
	
	public static Boolean checkAlias(AONContext ctx, String alias) {
		List<Record> questionRecords = ctx.getDslContext().select().from(QUESTION)
				.where(QUESTION.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.and(QUESTION.ALIAS.eq(alias))
				.fetch();
		
		return !questionRecords.isEmpty();
	}

}
