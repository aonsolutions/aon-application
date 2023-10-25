package net.aonsolutions.aon.bank.nordigen;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(NordigenItAvailabilityCondition.class)
@Test
public @interface SkipWhenNordigenUnavailable {
}
