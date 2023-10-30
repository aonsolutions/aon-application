package net.aonsolutions.aon.bank.checkit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(CheckItAvailabilityCondition.class)
@Test
public @interface SkipWhenCheckItUnavailable {
}
