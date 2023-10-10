package com.esferalia.aon.occam.test.marketing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.marketing.question.QuestionDAOTest;
import com.esferalia.aon.occam.test.marketing.question.QuestionAONTest;

@RunWith(Suite.class)
@SuiteClasses({
	QuestionDAOTest.class,
	QuestionAONTest.class
})

public class MarketingTestSuite {
	
}
