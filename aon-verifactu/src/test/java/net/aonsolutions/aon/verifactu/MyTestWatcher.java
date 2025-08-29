package net.aonsolutions.aon.verifactu;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class MyTestWatcher implements TestWatcher {
    @Override
    public void testAborted(ExtensionContext extensionContext, Throwable throwable) {
        System.out.println( "testAborted" );
    }

    @Override
    public void testDisabled(ExtensionContext extensionContext, Optional<String> optional) {
        System.out.println( "testDisabled" );
    }

    @Override
    public void testFailed(ExtensionContext extensionContext, Throwable throwable) {
        System.out.println( "TEST FAILED " + extensionContext.getTestMethod().map(Method::getName).orElse(null) );
        System.out.println(" ------------ ");
    }

    @Override
    public void testSuccessful(ExtensionContext extensionContext) {
        // Nothing to do here
    }
}
