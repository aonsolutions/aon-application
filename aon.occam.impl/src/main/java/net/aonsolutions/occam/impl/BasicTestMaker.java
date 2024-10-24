package net.aonsolutions.occam.impl;

import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;

import java.io.PrintStream;

import org.jooq.Field;
import org.jooq.Table;

import com.esferalia.aon.jooq.tables.InvoiceFiscal;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonWordUtils;

public class BasicTestMaker<T extends Table<?>> {
	
	private final T table;
	private PrintStream out;
	
	private BasicTestMaker( T table) {
		this.table = table;
		out = System.out;
	}
	
	private BasicTestMaker<T> println(){
		out.println();
		return this;
	}
	private BasicTestMaker<T> println( String l ){
		out.println(l);
		return this;
	}
	
	private String name( String n) {
		String name = AonStringUtils.replace(n, "_", " ");
		name = AonWordUtils.capitalize(name);
		name = AonStringUtils.remove(name, " ");
		return name;
	}
	
	private void make(boolean makeMetadataTest) {
		println("package net.aonsolutions.occam.api.model;");
		println();
		println("import static org.junit.jupiter.api.Assertions.assertEquals;");
		println("import static org.junit.jupiter.api.Assertions.assertNotEquals;");
		println("import static org.junit.jupiter.api.Assertions.assertSame;");
		if (makeMetadataTest) {
			println("import static org.junit.jupiter.api.Assertions.assertTrue;");
		}
		
		println();
		println("import java.util.ArrayList;");
		println("import java.util.HashSet;");
		println("import java.util.List;");
		println("import java.util.Set;");

		println();
		println("import org.junit.jupiter.api.Test;");
		
		println("import com.esferalia.aon.watson.util.AonCollectionUtils;");
		println();
		if (makeMetadataTest) {
			println("import net.aonsolutions.occam.api.model.metadata."
				+ name( table.getName() )
				+"Metadata;");
			println("import net.aonsolutions.occam.api.model.metadata."
				+ name( table.getName() )
				+"Metadata."
				+ name( table.getName() )
				+"MetadataVisitor;");
			println();
		}
		println("class "
				+ name( table.getName() )
				+"Test {");
		println();
		println("\t@Test");
		println("\tvoid test"
			+ name( table.getName() )
			+"() {");

		println("\t\t"
			+ name( table.getName() )
			+ " expected = AonMocker.mock("
			+ name( table.getName() )
			+".class);");
		println("\t\t"
			+ name( table.getName() )
			+ " actual = new "
			+ name( table.getName() )
			+"()");
		AonCollectionUtils.stream(table.fields())
			.forEach( field -> {
				println("\t\t\t.set"
					+ name(field.getName())
					+"(expected.get"
					+ name(field.getName())
					+"())"
				);
			});
		println("\t\t;");
		println("\t\tAonAsserts.assertClassEquals(expected, actual);");
		
		println("\t}");
		if (makeMetadataTest) {
			println();
			println("\t@Test");
			println("\tvoid test"
				+ name( table.getName() )
				+ "MetadataVisitor() {");
			println("\t\t"
				+ name( table.getName() )
				+ "MetadataVisitor<"
				+ name( table.getName() )
				+ "Metadata> v = new "
				+ name( table.getName() )
				+ "MetadataVisitor<>() {");
			
			AonCollectionUtils.stream(table.fields())
				.filter( field -> !"creation_user".equals( field.getName() ))
				.filter( field -> !"creation_date".equals( field.getName() ))
				.filter( field -> !"modification_user".equals( field.getName() ))
				.filter( field -> !"modification_date".equals( field.getName() ))
				.forEach( field -> {
					println("\t\t\t @Override public "
						+ name( table.getName() )
						+ "Metadata visit"
						+ name(field.getName())
						+ "() {return "
						+ name( table.getName() )
						+ "Metadata."
						+ AonStringUtils.upperCase(field.getName())
						+ ";}"
					);
				});
			println("\t\t};");
			println("\t\tAonCollectionUtils.stream("
				+ name( table.getName() )
				+ "Metadata.values())");
			println("\t\t\t.forEach(a -> assertSame(a, a.visit(v)));");
			println("\t}");
			
			
			AonCollectionUtils.stream(table.fields())
			.filter( field -> !"creation_user".equals( field.getName() ))
			.filter( field -> !"creation_date".equals( field.getName() ))
			.filter( field -> !"modification_user".equals( field.getName() ))
			.filter( field -> !"modification_date".equals( field.getName() ))
			.forEach( field -> {
				println("\t");
				println("\t@Test");
				println("\tvoid testDirty"
					+ name(field.getName())
					+ "() {");
				println("\t\t"
					+ name( table.getName() )
					+ " ent = new "
					+ name( table.getName() )
					+ "();");
				println("\t\tent.set"
					+ name(field.getName())
					+ "("
					+ value( field )
					+ ");");
				println("\t\tassertTrue( ent.isDirty("
					+ name( table.getName() )
					+ "Metadata."
					+ AonStringUtils.upperCase(field.getName())
					+") );");		
				println("\t}");
			});
		}
		
		println("\t@Test");
		println("\tvoid test"
			+ name( table.getName() )
			+"Equals() {");
		println("\t\t"
			+ name( table.getName() )
			+" a1 = new "
			+ name( table.getName() )
			+"().setId(1);");
		println("\t\tassertEquals(a1,a1);");
		println("\t\tassertNotEquals(a1,null);");
		println("\t\tassertNotEquals(null,a1);");
		println("\t\tassertNotEquals(a1,new Object());");
		println("\t\tassertNotEquals(a1,new Account());");
		println();
		println("\t\t"
			+ name( table.getName() )
			+ " a2 = new "
			+ name( table.getName() )
			+"().setId(1);");
		println("\t\tassertEquals(a1,a2);");
			
		println("\t\t"
			+ name( table.getName() )
			+ " a3 = new "
			+ name( table.getName() )
			+"().setId(3);");
		println("\t\tassertNotEquals(a1,a3);");
		println("\t}");
		println();
		
		println("\t@Test");
		println("\tvoid testHashcode() {");
		println("\t\tList<"
			+ name( table.getName() )
			+ "> objects = new ArrayList<>();");
		println("\t\tfor (int i = 0; i < 1000; i++) {");
		println("\t\t\tobjects.add(new "
				+ name( table.getName() )
			+ "().setId(i));");
		println("\t\t}");
		println("\t\tSet<Integer> hashCodes = new HashSet<>();");
		println("\t\tfor ("
				+ name( table.getName() )
				+ " obj : objects) {");
		println("\t\t\thashCodes.add(obj.hashCode());");
		println("\t\t}");
		println("\t\tassertEquals(objects.size(), hashCodes.size(), 10);");
		println("\t}");	
		
		println("}");
	}

	private String value(Field<?> field) {
		if ( field.getDataType().isNumeric()) return "1";
		else if (field.getDataType().isString() ) return "\"1\"";
		else if (field.getDataType().isDate() ) return "new Date()";
		else throw new IllegalArgumentException("Añade el tipo que has encontrado!!");
	}

	public static void main(String[] args) {
		BasicTestMaker<InvoiceFiscal> mm = new BasicTestMaker<>(INVOICE_FISCAL);
		mm.make( true );
	}
}
