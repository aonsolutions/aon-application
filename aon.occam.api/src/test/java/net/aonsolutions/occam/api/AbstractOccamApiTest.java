package net.aonsolutions.occam.api;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import com.esferalia.aon.watson.util.AonStringUtils;

public class AbstractOccamApiTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOccamApiTest.class);
	
	@BeforeEach
	public void beforeEach(TestInfo testInfo) {
		boolean isRepeatedTest = AonStringUtils.contains(testInfo.getDisplayName(),"repetition");
		String className = testInfo.getTestClass().map(clazz -> clazz.getName()).orElse("?");
		String methodName = testInfo.getTestMethod().map(tm -> tm.getName()).map(mn -> mn + "()").orElse("?");
		String repetitionInfo = isRepeatedTest?testInfo.getDisplayName():"";
		LOGGER.debug(() -> String.format("Running [ %s.%s %s ]",className,methodName,repetitionInfo));				
	}


}
