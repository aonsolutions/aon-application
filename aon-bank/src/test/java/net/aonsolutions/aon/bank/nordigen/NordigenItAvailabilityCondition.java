package net.aonsolutions.aon.bank.nordigen;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class NordigenItAvailabilityCondition implements ExecutionCondition {

	private static final Logger LOGGER = Logger.getLogger(NordigenItAvailabilityCondition.class.getName()); 

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        final Optional<SkipWhenNordigenUnavailable> optional = findAnnotation(context.getElement(), SkipWhenNordigenUnavailable.class);
        if (optional.isPresent()) {
   			boolean up = NordigenAPI.isNordigenAvailabilitySocketAlive();
   			if (!up) {
   				LOGGER.warning("WARNING!!! Nordigen test are DISABLED!");
   				return ConditionEvaluationResult.disabled("Connection is down");
   			}
    		return ConditionEvaluationResult.enabled("Connection is up");
        }
        return ConditionEvaluationResult.enabled("No assumptions, moving on...");
    }

}
