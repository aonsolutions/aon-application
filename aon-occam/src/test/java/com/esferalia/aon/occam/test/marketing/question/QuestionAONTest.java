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
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.api.AON;


public class QuestionAONTest extends AbstractOccamTest {

	@Test
	public void crudeTest() {
		// create
		Question question = AonFaker.getQuestion(ctx);
		AON.saveQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question);
		Asserts.assertEqualsQuestion(AON.getQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question.getId()),question);
		
		// delete
		AON.deleteQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question.getId());
		assertNull(AON.getQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question.getId()));
	}
	
	@Test
	public void checkQuestionAliasTrueTest() {
		Question question = AonFaker.getQuestion(ctx);
		AON.saveQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question);
		assertTrue(AON.checkQuestionAlias(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question.getAlias()));
		AON.deleteQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question.getId());
	}
	
	@Test
	public void checkQuestionListTest() {
		Question question1 = AonFaker.getQuestion(ctx);
		Question question2 = AonFaker.getQuestion(ctx);
		
		question1.setAlias("alias");
		question2.setAlias("alias");
		
		QuestionParams params = new QuestionParams()
				.setAlias("alias")
				.setDomain(ctx.getDomainId())
				.setDomainName(ctx.getDomainName())
				.setUser(ctx.getUser())
				;
		
		// if the alias does not exists
		if (!AON.checkQuestionAlias(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), "alias")) {
			assertTrue(AON.getQuestionList(params).isEmpty());
			
			AON.saveQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question1);
			AON.saveQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question2);
			
			assertFalse(AON.getQuestionList(params).isEmpty());
			
			AON.deleteQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question1.getId());
			AON.deleteQuestion(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), question2.getId());
			
			assertTrue(AON.getQuestionList(params).isEmpty());

		} else {
			assertFalse(AON.getQuestionList(params).isEmpty());
		}
	}

}


