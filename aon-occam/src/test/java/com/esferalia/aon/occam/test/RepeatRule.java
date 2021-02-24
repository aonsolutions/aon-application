package com.esferalia.aon.occam.test;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * <p>
 * JUnit rule to be declared in a test whose some test methods need to be executed repeatedly.
 * </p>
 * <p>
 * Usage:
 *
 * <pre>
 * &#64;Rule
 * public RepeatRule repeatRule = new RepeatRule();
 *
 * &#64;Test
 * &#64;Repeat(10)
 * public void testRepeatTenTimes()
 * {
 *     PrintStream err = System.err;
 *     err.println(Math.random());
 * }
 * </pre>
 * </p>
 *
 * @since 1.0
 * @author fappel, ttran
 * @see https://gist.github.com/fappel/8bcb2aea4b39ff9cfb6e
 **/
public class RepeatRule implements TestRule
{
    /**
     * <p>
     * The actual statement to carry out the actual repeated execution.
     * </p>
     *
     * @since 1.0
     * @author fappel, ttran
     **/
    private static final class RepeatStatement extends Statement
    {
        private final int times;
        private final Statement statement;

        private RepeatStatement(final int t, final Statement s)
        {
            this.times = t;
            this.statement = s;
        }

        @Override
        public void evaluate() throws Throwable
        {
            for (int i = 0; i < times; i++)
            {
                statement.evaluate();
            }
        }
    }

    @Override
    public Statement apply(final Statement statement, final Description description)
    {
        Statement result = statement;
        Repeat repeat = description.getAnnotation(Repeat.class);
        if (repeat != null)
        {
            int times = repeat.value();
            result = new RepeatStatement(times, statement);
        }
        return result;
    }
}