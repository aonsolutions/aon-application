package net.aonsolutions.occam.impl.generic;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reflections.ReflectionUtils.Methods;
import static org.reflections.ReflectionUtils.get;
import static org.reflections.util.ReflectionUtilsPredicates.withModifier;

import java.lang.reflect.Modifier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import com.esferalia.aon.watson.util.AonStringUtils;

class PackageVisibilityTests {

	@Test
	void testPublicMethods() {
		Reflections reflections = new Reflections(
			"net.aonsolutions.occam.impl.handler"
			, Scanners.SubTypes.filterResultsBy(s -> true));
		String message = reflections.getSubTypesOf(Object.class) .stream()
			.filter( clazz -> !AonStringUtils.contains(clazz.getName(), '$'))
			.filter( clazz -> !AonStringUtils.startsWithAny(clazz.getSimpleName(),"Filter"))
			.filter( clazz -> !AonStringUtils.endsWithAny(clazz.getName(),"Test","Tests"))
			.filter( clazz -> AonStringUtils.contains(clazz.getName(), "Handler"))
			.flatMap( clazz -> get(Methods.of(clazz).filter(withModifier(Modifier.PUBLIC))).stream())
			.map( m ->  
				"\tPUBLIC method found. -->  [ " +
				m.getDeclaringClass().getSimpleName()
				+ " " + 
				m.getName() 
				+ "() ] "
				+ "Los \"Handlers\" deben tener métodos PROTECTED\n"
			)
			.findFirst()
			.orElse(null)
		;
		assertNull(message);
//		String publicMethods = buf.toString();
//		publicMethods = AonStringUtils.trimToNull(publicMethods);
//		if (AonStringUtils.isNotBlank(publicMethods)) {
//			fail( publicMethods );
//			fail( "POR QUE SI" );
//		}
	}

}
