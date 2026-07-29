package com.esferalia.aon.occam.test.marketing.question;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.util.List;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.impl.jooq.dao.QuestionDAO;
import com.esferalia.aon.occam.impl.jooq.validation.QuestionValidation;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;
import static com.esferalia.aon.jooq.tables.Question.QUESTION;;

/**
 * Tests the mehotds of the class QuestionDAO
 */
public class QuestionDAOTest extends AbstractOccamTest {
	
	/**
	 * Test create, update and delete
	 */
	@Test
	public void crudeTest() {
		// create
		Question question = AonFaker.getQuestion(ctx);
		if (!QuestionValidation.checkAlias(ctx, question)) {
			question = QuestionDAO.insert(ctx, question);
			Integer questionId = question.getId();
			Question inserted = QuestionDAO.get(ctx, questionId);
			Asserts.assertEqualsQuestion(question, inserted);
			
			// update
			question = QuestionDAO.update(ctx, question);
			Question updated = QuestionDAO.get(ctx, questionId);
			Asserts.assertEqualsQuestion(question, updated);
			
			// delete
			QuestionDAO.delete(ctx, questionId);
			Question deleted = QuestionDAO.get(ctx, questionId);
			assertNull(deleted);
		}
	}

	/**
	 * Test that it throws an exception if the question is null
	 */
	@Test
	public void saveNullQuestionTest() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, null));
		assertEquals(AonError.QUESTION_NULL.getMessage(), e.getMessage());
	}
	
	/**
	 * Test that it throws an exception if the question is empty
	 */
	@Test
	public void saveEmptyQuestionTest() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, new Question()));
		assertEquals(AonError.QUESTION_EMPTY.getMessage(), e.getMessage());
	}
	
	/**
	 * Test that it throws an exception if we save a question where the domain is null
	 */
	@Test
	public void saveNullDomainQuestionTest() {
		Question question = AonFaker.getQuestion(ctx);
		
		if (!QuestionValidation.checkAlias(ctx, question)) {
			question.setDomain(null);
			AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, question));
			assertEquals(AonError.EMPTY_DOMAIN.getMessage(), e.getMessage());
		}
	}
	
	/**
	 * Test that it throws an exception if we save a question where the text is null
	 */
	@Test
	public void saveNullTextQuestionTest() {
		Question question = AonFaker.getQuestion(ctx);
		
		if (!QuestionValidation.checkAlias(ctx, question)) {
			question.setText(null);
			AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, question));
			assertEquals(AonError.NULL_QUESTION_TEXT.getMessage(), e.getMessage());
		}
	}
	
	/**
	 * Test that it throws an exception if we save a question where the text is empty
	 */
	@Test
	public void saveEmptyTextQuestionTest() {
		Question question = AonFaker.getQuestion(ctx);
		
		if (!QuestionValidation.checkAlias(ctx, question)) {
			question.setText("");
			AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, question));
			assertEquals(AonError.EMPTY_QUESTION_TEXT.getMessage(), e.getMessage());
		}
	}
	
	/**
	 * Test that it throws an exception if we save a question where the size of the text is invalid
	 */
	@Test
	public void saveInvalidTextSizeQuestionTest() {
		Question question = AonFaker.getQuestion(ctx);
		
		if (!QuestionValidation.checkAlias(ctx, question)) {
			String text = AonStringUtils.repeat("h", QUESTION.QUESTION_TEXT.getDataType().length() + 1);
			question.setText(text);
			AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, question));
			assertEquals(AonError.INVALID_SIZE_QUESTION_TEXT.getMessage(), e.getMessage());
		}
	}
	
	/**
	 * Test that it throws an exception if we save a question where the size of the alias is invalid
	 */
	@Test
	public void saveInvalidAliasSizeQuestionTest() {
		Question question = AonFaker.getQuestion(ctx);
		
		if (!QuestionValidation.checkAlias(ctx, question)) {
			String alias = AonStringUtils.repeat("h", QUESTION.ALIAS.getDataType().length() + 1);
			question.setAlias(alias);
			AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, question));
			assertEquals(AonError.INVALID_SIZE_ALIAS.getMessage(), e.getMessage());
		}
	}
	
	/**
	 * Test that it throws an exception if we save a question where the alias is repeated
	 */
	@Test
	public void saveRepeatedAliasQuestionTest() {
		Question question1 = AonFaker.getQuestion(ctx);
		Question question2 = AonFaker.getQuestion(ctx);
		
		question1.setAlias("alias");
		question2.setAlias("alias");
		
		QuestionDAO.save(ctx, question1);
		
		AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, question2));
		assertEquals(AonError.REPEATED_ALIAS.getMessage(),e.getMessage());
		
		QuestionDAO.delete(ctx, question1.getId());
		QuestionDAO.delete(ctx, question2.getId());
	}
	
	/**
	 * Test that it throws an exception if we get a list without the params that we set
	 */
	@Test
	public void getListTest() {
		Question question1 = AonFaker.getQuestion(ctx);
		Question question2 = AonFaker.getQuestion(ctx);
		
		question1.setActive(true);
		question2.setActive(false);
		
		if (!QuestionValidation.checkAlias(ctx, question1)) {
			QuestionDAO.insert(ctx, question1);
		}
		
		if (!QuestionValidation.checkAlias(ctx, question2)) {
			QuestionDAO.insert(ctx, question2);
		}
		
		QuestionParams questionParams = new QuestionParams();
		questionParams.setActive((byte) 1);
		
		List<Question> actual = QuestionDAO.getList(ctx, questionParams);
		
		for (Question question : actual) {
			assertTrue(question.isActive());
		}

		QuestionDAO.delete(ctx, question1.getId());
		QuestionDAO.delete(ctx, question2.getId());
	}
}
