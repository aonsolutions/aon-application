package net.aonsolutions.aon.bank.checkit;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CheckItAvailabilityCondition implements ExecutionCondition {
	private static final Logger LOGGER = Logger.getLogger(CheckItAvailabilityCondition.class.getName()); 
	
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        final Optional<SkipWhenCheckItUnavailable> optional = findAnnotation(context.getElement(), SkipWhenCheckItUnavailable.class);
        if (optional.isPresent()) {
   			boolean up = CheckItAPI.isCheckItAvailabilitySocketAlive();
   			if (!up) {
   				LOGGER.warning("WARNING!!! CheckIt test are DISABLED!");
   				return ConditionEvaluationResult.disabled("Connection is down");
   			}
    		return ConditionEvaluationResult.enabled("Connection is up");
        }
        return ConditionEvaluationResult.enabled("No assumptions, moving on...");
    }

}
