package com.esferalia.aon.occam.test.marketing.question;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.impl.jooq.RegistryImpl;
import com.esferalia.aon.occam.impl.jooq.validation.QuestionValidation;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.api.AON;

/**
 * Tests the question's methods of the class AON
 */
public class QuestionAONTest extends AbstractOccamTest {

	/**
	 * Test create and delete
	 */
	@Test
	public void crudeTest() {
		// create and update
		Question question = AonFaker.getQuestion(ctx);
		if (!QuestionValidation.checkAlias(ctx, question)) {
			AON.saveQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question);
			Asserts.assertEqualsQuestion(AON.getQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question.getId()),question);
			
			// delete
			AON.deleteQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question.getId());
			assertNull(AON.getQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question.getId()));
		}
	}
	
	/**
	 * Test that it throws and exception if we get a list without the params that we set
	 */
	@Test
	public void checkQuestionListTest() {
		Question question1 = AonFaker.getQuestion(ctx);
		Question question2 = AonFaker.getQuestion(ctx);
		
		question1.setActive(true);
		question2.setActive(false);
		
		if (!QuestionValidation.checkAlias(ctx, question1)) {
			AON.saveQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question1);
		}
		
		if (!QuestionValidation.checkAlias(ctx, question2)) {
			AON.saveQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question2);
		}
		QuestionParams params = new QuestionParams()
				.setDomain(ctx.getDomainId())
				.setDomainName(ctx.getDomainName())
				.setUser(ctx.getUser())
				.setActive((byte) 1)
				;
		
		List<Question> actual = AON.getQuestionList(params);
		
		for (Question question : actual) {
			assertTrue(question.isActive());
		}
		
		AON.deleteQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question1.getId());
		AON.deleteQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question2.getId());
	}

}


