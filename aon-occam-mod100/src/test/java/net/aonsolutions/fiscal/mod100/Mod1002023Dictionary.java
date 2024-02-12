package net.aonsolutions.fiscal.mod100;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.mvel2.ast.ForEachNode;

import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod1002023Dictionary {

	private static final String XSD_SCHEMA = "Renta2023.xsd";
	private static final String DICT_XSD_SCHEMA = "diccionarioXSD_2023.properties";
	private static final String DICT_DLG_XSD_SCHEMA = "diccionarioDlgXSD_2023.properties";
	
	private static class Mod100Script {
		private final Map<String, Mod100Menu > menus = new LinkedHashMap<>();
		
		public boolean hasMenus() {
			return AonCollectionUtils.isEmpty( getMenus() );
		}
		public Map<String,Mod100Menu> getMenus() {
			return menus;
		}
		public Mod100Script addMenu(Mod100Menu menu) {
			menus.put( menu.getKey(), menu);
			return this;
		}
		
	}
	
	private static class Mod100Menu {
		private String key;
		private final Map<String, Mod100Menu > menus = new LinkedHashMap<>();
		private final Map<String, Mod100Field > fields = new LinkedHashMap<>();
		
		public String getKey() {
			return key;
		}
		public Mod100Menu setKey(String key) {
			this.key = key;
			return this;
		}
		
		public boolean hasMenus() {
			return AonCollectionUtils.isEmpty( getMenus() );
		}
		public Map<String,Mod100Menu> getMenus() {
			return menus;
		}
		public Mod100Menu addMenu(Mod100Menu menu) {
			menus.put( menu.getKey(), menu);
			return this;
		}
		
		public boolean hasFields() {
			return AonCollectionUtils.isEmpty( getFields() );
		}
		public Map<String,Mod100Field> getFields() {
			return fields;
		}
		public Mod100Menu addField(Mod100Field field) {
			fields.put( field.getKey(), field);
			return this;
		}
		
	}

	private static class Mod100Field {
		private String key;
		private String path;
		private String type;
		private String box;
		private String description;
		
		public String getKey() {
			return key;
		}
		public Mod100Field setKey(String key) {
			this.key = key;
			return this;
		}
		
		public String getPath() {
			return path;
		}
		public Mod100Field setPath(String path) {
			this.path = path;
			return this;
		}
		public String getType() {
			return type;
		}
		public Mod100Field setType(String type) {
			this.type = type;
			return this;
		}
		public String getBox() {
			return box;
		}
		public Mod100Field setBox(String box) {
			this.box = box;
			return this;
		}
		public String getDescription() {
			return description;
		}
		public Mod100Field setDescription(String description) {
			this.description = description;
			return this;
		}
		public String toString() {
			return "Key: " + getKey()
				+ "\n\tPath........: " + getPath()
				+ "\n\tType........: " + getType()
				+ "\n\tBox ........: " + getBox()
				+ "\n\tDescription.: " + getDescription()
				+ "\n"
			;
		}
	}

	public static void main(String[] args) throws IOException {
		InputStream in = Mod1002023Dictionary.class.getResourceAsStream( DICT_XSD_SCHEMA );
		InputStreamReader reader = new InputStreamReader( in );
		LineNumberReader props = new LineNumberReader( reader );
		LinkedHashMap<String,Mod100Field > map = props.lines()
			.map( e -> toMod100Dictionary(e) )
			.collect(Collectors.toMap(d -> d.getKey(),d -> d,(d1,d2) -> d1,LinkedHashMap::new));
		Mod100Script script = buildScript( map );
		AonIOUtils.closeQuietly(in);
	}

	private static Mod100Field toMod100Dictionary(String line ) {
		String dictPattern = 
		  	  "(?<key>.*)\\="
		  	+ "\\[(?<path>.*)\\]"
			+ "\\[(?<type>.*)\\]"
			+ "\\[(?<box>.*)\\]"
			+ "\\[(?<desc>.*)\\]?";
		Pattern pattern = Pattern.compile( dictPattern, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		matcher.matches();
		String k = matcher.group("key");
		String p = matcher.group("path");
		String t = matcher.group("type");
		String b = matcher.group("box");
		String d = AonStringUtils.removeEnd( matcher.group("desc"), "]" ); 
		return new Mod100Field()
			.setKey( k )
			.setPath( p )
			.setType( t )			
			.setBox( b )
			.setDescription( d )
			;
	}
	
	private static Mod100Script buildScript(Map<String, Mod100Field> map) {
		Mod100Script script = new Mod100Script();
		map.values().stream()
			.map( d -> d.getPath() )
			.forEach( p -> {
				System.out.println(p);
				LinkedList<String> stack = new LinkedList<>();
				AonCollectionUtils.stream( AonStringUtils.split(p, '/') ).forEach( t -> stack.add(t));
				String itemKey = stack.getLast();
				Mod100Menu parentMenu = null;
				for (int i = 0 ; i < (stack.size() - 1); i++) {
					String key = stack.get(i);
					if (i == 0) {
						Mod100Menu menu = script.getMenus().get( key );
						if ( menu == null ) {
							menu = new Mod100Menu().setKey(key);
						}
					} else {
						
					}
					if ( parentMenu == null) {
						
					}
				}
			});
		return script;
	}
}
