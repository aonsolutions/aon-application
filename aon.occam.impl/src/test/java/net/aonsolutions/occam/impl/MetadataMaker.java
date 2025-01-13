package net.aonsolutions.occam.impl;

import static com.esferalia.aon.jooq.tables.Item.ITEM;

import java.io.PrintStream;

import org.jooq.Table;

import com.esferalia.aon.jooq.tables.Item;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonWordUtils;

class MetadataMaker {
	
	public static void main(String[] args) {
		MetadataMakerTestImpl<Item> mm = new MetadataMakerTestImpl<>(ITEM);
		mm.make();
	}
	
	private static class MetadataMakerTestImpl<T extends Table<?>> {
		
		private final T table;
		private PrintStream out;
		
		private MetadataMakerTestImpl( T table) {
			this.table = table;
			out = System.out;
		}
		
		private MetadataMakerTestImpl<T> println(){
			out.println();
			return this;
		}
		private MetadataMakerTestImpl<T> println( String l ){
			out.println(l);
			return this;
		}
		private MetadataMakerTestImpl<T> print( String l ){
			out.print(l);
			return this;
		}
		private String name( String n) {
			String name = AonStringUtils.replace(n, "_", " ");
			name = AonWordUtils.capitalize(name);
			name = AonStringUtils.remove(name, " ");
			return name;
		}
		
		private void make() {
			println("package net.aonsolutions.occam.impl;");
			println();
			println("import java.io.Serializable;");
			println();
			println("public enum "+name( table.getName() )+"Metadata implements Serializable {");
			print("\t");
			AonCollectionUtils.stream(table.fields())
				.filter( field -> !"creation_user".equals( field.getName() ))
				.filter( field -> !"creation_date".equals( field.getName() ))
				.filter( field -> !"modification_user".equals( field.getName() ))
				.filter( field -> !"modification_date".equals( field.getName() ))
				.forEach( field -> {
					println(AonStringUtils.upperCase(field.getName())
							+ " { @Override public <T> T visit("
							+ name(table.getName())
							+"MetadataVisitor<T> v) { return v.visit"
							+ name(field.getName())
							+"();} }"
					);
					print("\t,");
				});
			println("\t;");
			println();
			println("\tpublic abstract <T> T visit("
					+ name(table.getName())
					+"MetadataVisitor<T> v);");
			println("\tpublic static interface "
					+ name(table.getName())
					+"MetadataVisitor<T> {");
			AonCollectionUtils.stream(table.fields())
			.filter( field -> !"creation_user".equals( field.getName() ))
			.filter( field -> !"creation_date".equals( field.getName() ))
			.filter( field -> !"modification_user".equals( field.getName() ))
			.filter( field -> !"modification_date".equals( field.getName() ))
			.forEach( field -> {
				println("\t\tT visit"
						+name(field.getName())
						+"();");
				
			});
			println("\t}");
			
			
			
			
			println("}");
		}

	}
	
}
