package net.aonsolutions.occam.impl.generic;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.core.domain.JavaModifier.PUBLIC;
import static com.tngtech.archunit.core.domain.properties.HasModifiers.Predicates.modifier;
import static com.tngtech.archunit.lang.conditions.ArchConditions.have;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.OnlyIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

@AnalyzeClasses(packages = {
	"net.aonsolutions.occam.impl.handler"
})
class ArchUnitTests {
	

	@ArchTest
	void testHandlersAreProtectedClasses(JavaClasses importedClasses) {
		classes().that().haveSimpleNameEndingWith("Handler")
			.and().areNotInnerClasses()
			.and().areNotMemberClasses()
		.should()
			.notBePublic()
		.check(importedClasses);
	}
		
	@ArchTest
	void testHandlersHavePrivateConstructor(JavaClasses importedClasses) {
		classes().that().haveSimpleNameEndingWith("Handler")
			.and().areNotInnerClasses()
			.and().areNotMemberClasses()
		.should()
			.haveOnlyPrivateConstructors()
		.check(importedClasses);
	}
	
	@ArchTest
	void testHandlersMustNotHavePublicMethods(JavaClasses importedClasses) {
		classes().that().haveSimpleNameEndingWith("Handler")
			.and().areNotInnerClasses()
			.and().areNotMemberClasses()
        .should(
    		have( 
				describe("no public methods", javaClass -> 
					javaClass.getMethods().stream().filter( modifier(PUBLIC) ).count() == 0)
			)
		)
        .check(importedClasses);		
	}
	
//	@ArchTest
//	void testHandlersHaveTests(JavaClasses importedClasses) {
//		classes().that().haveSimpleNameEndingWith("Handler")
//			.and().areNotInnerClasses()
//			.and().areNotMemberClasses()
//		.should(haveACorrespondingClassEndingWith("Test"))
//		.check(importedClasses)
//		;
//	}
//	 
//	private static ArchCondition<JavaClass> haveACorrespondingClassEndingWith(String testClassSuffix) {
//	    return new ArchCondition<JavaClass>("have a corresponding class with suffix " + testClassSuffix) {
//	        Set<String> testedClasseNames = Collections.emptySet();
//
//	        @Override
//	        public void init(Collection<JavaClass> allClasses) {
//	    		JavaClasses testClasses = new ClassFileImporter()
//	    				.withImportOption(new OnlyIncludeTests())
//	    				.importPackages("net.aonsolutions.occam.impl.handler");
//	        	testedClasseNames = testClasses.stream()
//        			.map(JavaClass::getName)
//        			.filter(className -> className.endsWith(testClassSuffix))
//        			.map(className -> className.substring(0, className.length() - testClassSuffix.length()))
//        			.collect(Collectors.toSet());
//	        }
//
//	        @Override
//	        public void check(JavaClass clazz, ConditionEvents events) {
//	            if (!clazz.getName().endsWith(testClassSuffix)) {
//	                boolean satisfied = testedClasseNames.contains(clazz.getName());
//	                String message = "Class " 
//                		+ clazz.getSimpleName() 
//	                	+ " has "
//                		+ (satisfied ? "a" : "no") 
//                		+ " corresponding test class"
//            		;
//	                events.add(new SimpleConditionEvent(clazz, satisfied, message ));
//	            }
//	        }
//	    };
//	}

	
}
