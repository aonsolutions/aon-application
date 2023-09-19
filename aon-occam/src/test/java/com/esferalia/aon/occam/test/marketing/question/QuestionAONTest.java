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
import com.esferalia.aon.occam.impl.jooq.RegistryImpl;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.api.AON;


public class QuestionAONTest extends AbstractOccamTest {

	@Test
	public void saveQuestionTest() {
		Question question = AonFaker.getQuestion(ctx);
		
		RegistryImpl registryImpl = new RegistryImpl();
		
		registryImpl.saveQuestion(ctx, question);
		Asserts.assertEqualsQuestion(registryImpl.getQuestion(ctx, question.getId()),question);
	}
}
