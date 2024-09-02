package com.esferalia.aon.occam.test.marketing;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.marketing.question.QuestionAONTest;
import com.esferalia.aon.occam.test.marketing.question.QuestionDAOTest;

@Suite
@SelectClasses({
	QuestionDAOTest.class,
	QuestionAONTest.class
})

public class MarketingTestSuite {
	
}
