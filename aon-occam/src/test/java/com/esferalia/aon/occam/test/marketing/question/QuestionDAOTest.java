package com.esferalia.aon.occam.test.marketing.question;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.api.model.QuestionValue;
import com.esferalia.aon.occam.impl.jooq.dao.QuestionDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.github.javafaker.Faker;

public class QuestionDAOTest extends AbstractOccamTest {
	
	@Test
	public void crudeTest() {
		// create
		Question question = AonFaker.getQuestion(ctx);
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

	
	@Test
	public void saveNullQuestionTest() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, null));
		assertEquals(AonError.QUESTION_NULL.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveEmptyQuestionTest() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, new Question()));
		assertEquals(AonError.QUESTION_EMPTY.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveEmptyDomainQuestionTest() {
		Question question = AonFaker.getQuestion(ctx);
		question.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, question));
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveEmptyTextQuestionTest() {
		Question question = AonFaker.getQuestion(ctx);
		question.setText(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, question));
		assertEquals(AonError.EMPTY_QUESTION_TEXT.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveInvalidTextSizeQuestionTest() {
		Question question = AonFaker.getQuestion(ctx);
		
		char[] caracteres = new char[257];
		Arrays.fill(caracteres, 'h');
		String text = new String(caracteres);
		
		question.setText(text);
		AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, question));
		assertEquals(AonError.INVALID_SIZE_QUESTION_TEXT.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveInvalidAliasSizeQuestionTest() {
		Question question = AonFaker.getQuestion(ctx);
		
		char[] caracteres = new char[257];
		Arrays.fill(caracteres, 'h');
		String alias = new String(caracteres);
		
		question.setAlias(alias);
		AonCoreException e = assertThrows(AonCoreException.class, () -> QuestionDAO.save(ctx, question));
		assertEquals(AonError.INVALID_SIZE_ALIAS.getMessage(), e.getMessage());
	}
	
	@Test
	public void checkAliasText() {
		Question question = AonFaker.getQuestion(ctx);
		String alias = "alias";
		question.setAlias(alias);
		QuestionDAO.insert(ctx, question);
		
		assertTrue(QuestionDAO.checkAlias(ctx, alias));
		
		QuestionDAO.delete(ctx, question.getId());
	}
	
	@Test
	public void checkAliasFalseText() {
		String alias = "alias";
		QuestionParams questionParams = new QuestionParams();
		questionParams.setAlias(alias);
		
		List<Question> questionList = QuestionDAO.getList(ctx, questionParams);
		
		if (questionList.isEmpty()) {
			assertFalse(QuestionDAO.checkAlias(ctx, alias));
		} else {
			assertTrue(QuestionDAO.checkAlias(ctx, alias));
		}
	}
	
	@Test
	public void getListTest() {
		Question question1 = AonFaker.getQuestion(ctx);
		Question question2 = AonFaker.getQuestion(ctx);
		
		String alias = "alias in common";
		
		question1.setAlias(alias);
		question2.setAlias(alias);
		
		QuestionParams questionParams = new QuestionParams();
		questionParams.setAlias(alias);
		
		// after inserting the questionList
		QuestionDAO.insert(ctx, question1);
		QuestionDAO.insert(ctx, question2);
		
		List<Question> actual = QuestionDAO.getList(ctx, questionParams);
		
		// check that all the elements has that alias
		for (Question question : actual) {
			assertEquals(question.getAlias(),alias);
		}

		QuestionDAO.delete(ctx, question1.getId());
		QuestionDAO.delete(ctx, question2.getId());
	}
	
	@Test
	public void getListFalseTest() {
		String alias = "alias";
		QuestionParams questionParams = new QuestionParams();
		questionParams.setAlias(alias);
				
		if (!QuestionDAO.checkAlias(ctx, alias)) {
			assertEquals(QuestionDAO.getList(ctx, questionParams), new LinkedList<>());
		}
	}
}
