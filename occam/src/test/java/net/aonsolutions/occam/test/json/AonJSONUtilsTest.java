package net.aonsolutions.occam.test.json;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.json.AonJSONUtils;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.watson.client.util.AonStringUtils;


@ExtendWith(TimingExtension.class)
class AonJSONUtilsTest extends AbstractOccamTest {
	private static final String BOOKS_JSON = 
		"{"
		    +"\"books\":["
			    +"{"
				    +"\"isbn\":\"9781593279509\","
				    +"\"title\":\"Eloquent JavaScript, Third Edition\","
				    +"\"subtitle\":\"A Modern Introduction to Programming\","
				    +"\"author\":\"Marijn Haverbeke\","
				    +"\"from\":\"2018-12-04\","
				    +"\"published\":\"2018-12-04 00:00:00\","
				    +"\"publisher\":\"No Starch Press\","
				    +"\"pages\":472,"
				    +"\"description\":\"JavaScript lies at the heart of almost every modern web application, from social apps like Twitter to browser-based game frameworks like Phaser and Babylon. Though simple for beginners to pick up and play with, JavaScript is a flexible, complex language that you can use to build full-scale applications.\","
				    +"\"website\":\"http://eloquentjavascript.net/\","
				    +"\"active\":\"true\","
				    +"\"countries\":["
				    	+ "{\"iso\":\"ES\",\"name\":\"Spain\"}"
				    	+ ",{\"iso\":\"FR\",\"name\":\"France\"}"
				    	+ ",{\"iso\":\"IT\",\"name\":\"Italy\"}"
			    	+ "],"
				    +"\"publicationCountry\":{\"iso\":\"US\",\"name\":\"United States\"},"
			    +"},"
			    +"{"
				    +"\"isbn\":\"9781491943533\","
				    +"\"title\":\"Practical Modern JavaScript\","
				    +"\"subtitle\":\"Dive into ES6 and the Future of JavaScript\","
				    +"\"author\":\"Nicolás Bevacqua\","
				    +"\"from\":\"2017-07-16\","
				    +"\"published\":\"2017-07-16 00:00:00\","
				    +"\"publisher\":\"O'Reilly Media\","
				    +"\"pages\":334,"
				    +"\"description\":\"To get the most out of modern JavaScript, you need learn the latest features of its parent specification, ECMAScript 6 (ES6). This book provides a highly practical look at ES6, without getting lost in the specification or its implementation details.\","
				    +"\"website\":\"https://github.com/mjavascript/practical-modern-javascript\","
				    +"\"active\":true,"
			    +"},"
			    +"{"
				    +"\"isbn\":\"9781593277574\","
				    +"\"title\":\"Understanding ECMAScript 6\","
				    +"\"subtitle\":\"The Definitive Guide for JavaScript Developers\","
				    +"\"author\":\"Nicholas C. Zakas\","
				    +"\"from\":\"2016-09-03\","
				    +"\"published\":\"2016-09-03 00:00:00\","
				    +"\"publisher\":\"No Starch Press\","
				    +"\"pages\":352,"
				    +"\"description\":\"ECMAScript 6 represents the biggest update to the core of JavaScript in the history of the language. In Understanding ECMAScript 6, expert developer Nicholas C. Zakas provides a complete guide to the object types, syntax, and other exciting changes that ECMAScript 6 brings to JavaScript.\","
				    +"\"website\":\"https://leanpub.com/understandinges6/read\","
				    +"\"active\":false,"
			    +"},"
			    +"{"
				    +"\"isbn\":\"9781449365035\","
				    +"\"title\":\"Speaking JavaScript\","
				    +"\"subtitle\":\"An In-Depth Guide for Programmers\","
				    +"\"author\":\"Axel Rauschmayer\","
				    +"\"from\":\"2014-04-08\","
				    +"\"published\":\"2014-04-08 00:00:00\","
				    +"\"publisher\":\"O'Reilly Media\","
				    +"\"pages\":460,"
				    +"\"description\":\"Like it or not, JavaScript is everywhere these days -from browser to server to mobile- and now you, too, need to learn the language or dive deeper than you have. This concise book guides you into and through JavaScript, written by a veteran programmer who once found himself in the same position.\","
				    +"\"website\":\"http://speakingjs.com/\","
				    +"\"active\":\"false\","
			    +"},"
			    +"{"
				    +"\"isbn\":\"9781449331818\","
				    +"\"title\":\"Learning JavaScript Design Patterns\","
				    +"\"subtitle\":\"A JavaScript and jQuery Developer's Guide\","
				    +"\"author\":\"Addy Osmani\","
				    +"\"from\":\"2012-08-30\","
				    +"\"published\":\"2012-08-30 00:00:00\","
				    +"\"publisher\":\"O'Reilly Media\","
				    +"\"pages\":254,"
				    +"\"description\":\"With Learning JavaScript Design Patterns, you'll learn how to write beautiful, structured, and maintainable JavaScript by applying classical and modern design patterns to the language. If you want to keep your code efficient, more manageable, and up-to-date with the latest best practices, this book is for you.\","
				    +"\"website\":\"http://www.addyosmani.com/resources/essentialjsdesignpatterns/book/\","
			    +"},"
			    +"{"
				    +"\"isbn\":\"9798602477429\","
				    +"\"title\":\"You Don't Know JS Yet\","
				    +"\"subtitle\":\"Get Started\","
				    +"\"author\":\"Kyle Simpson\","
				    +"\"from\":\"2020-01-28\","
				    +"\"published\":\"2020-01-28 00:00:00\","
				    +"\"publisher\":\"Independently published\","
				    +"\"pages\":143,"
				    +"\"description\":\"The worldwide best selling You Don't Know JS book series is back for a 2nd edition: You Don't Know JS Yet. All 6 books are brand new, rewritten to cover all sides of JS for 2020 and beyond.\","
				    +"\"website\":\"https://github.com/getify/You-Dont-Know-JS/tree/2nd-ed/get-started\","
			    +"},"
			    +"{"
				    +"\"isbn\":\"9781484200766\","
				    +"\"title\":\"Pro Git\","
				    +"\"subtitle\":\"Everything you neeed to know about Git\","
				    +"\"author\":\"Scott Chacon and Ben Straub\","
				    +"\"from\":\"2014/11/18\","					// WRONG DATE FORMAT FOR TESTS
				    +"\"published\":\"2014/11/18-00:00:00\","	// WRONG DATE TIME FORMAT FOR TESTS
				    +"\"publisher\":\"Apress; 2nd edition\","
				    +"\"pages\":458,"
				    +"\"description\":\"Pro Git (Second Edition) is your fully-updated guide to Git and its usage in the modern world. Git has come a long way since it was first developed by Linus Torvalds for Linux kernel development. It has taken the open source world by storm since its inception in 2005, and this book teaches you how to use it like a pro.\","
				    +"\"website\":\"https://git-scm.com/book/en/v2\","
			    +"}"
			+"]"
		+"}";
	private static final String BOOKS = "books";
	private static final String PAGES = "pages";
	private static final String ISBN = "isbn";
	private static final String ACTIVE = "active";
	private static final String FROM = "from";
	private static final String PUBLISHED = "published";
	private static final String PUBLICATION_COUNTRY = "publicationCountry";
	private static final String COUNTRIES = "countries";
	
	
	
	@Test
	void testStream() {
		JSONArray array = new JSONArray();
		Stream<JSONObject> stream = AonJSONUtils.stream(array); 
		assertNotNull( stream );
		assertFalse( stream.findAny().isPresent() );
	}
	
	@Test
	void testArray() {
		JSONObject json = new JSONObject(BOOKS_JSON);
		JSONArray books = json.getJSONArray(BOOKS);
		Stream<JSONObject> stream = AonJSONUtils.stream(books); 
		assertNotNull( stream );
		JSONObject book = stream.findFirst().orElse(null);
		assertNotNull( book );
		JSONArray array = AonJSONUtils.getArray(book, COUNTRIES);
		assertNotNull( array );
		array = AonJSONUtils.getArray(null, null);
		assertNull( array );
		array = AonJSONUtils.getArray(book, null);
		assertNull( array );
		array = AonJSONUtils.getArray(book, "");
		assertNull( array );
		array = AonJSONUtils.getArray(book, " ");
		assertNull( array );
	}
	
	@Test
	void testObject() {
		JSONObject json = new JSONObject(BOOKS_JSON);
		JSONArray books = json.getJSONArray(BOOKS);
		Stream<JSONObject> stream = AonJSONUtils.stream(books); 
		assertNotNull( stream );
		JSONObject book = stream.findFirst().orElse(null);
		JSONObject obj = AonJSONUtils.getObject(book, PUBLICATION_COUNTRY);
		assertNotNull( obj );
		obj = AonJSONUtils.getObject(null, null);
		assertNull( obj );
		obj = AonJSONUtils.getObject(book, null);
		assertNull( obj );
		obj = AonJSONUtils.getObject(book, "");
		assertNull( obj );
		obj = AonJSONUtils.getObject(book, " ");
		assertNull( obj );
	}

	@Test
	void testNullInteger() {
		assertNull( AonJSONUtils.getInteger(null, PAGES) );
	}
	
	@Test
	void testInteger() {
		JSONObject json = new JSONObject(BOOKS_JSON);
		JSONArray array = json.getJSONArray(BOOKS);
		Stream<JSONObject> stream = AonJSONUtils.stream(array); 
		assertNotNull( stream );
		JSONObject book = stream.findFirst().orElse(null);
		assertNotNull( book );
		Integer pages = AonJSONUtils.getInteger(book, PAGES);
		assertNotNull( pages );
		assertEquals( 472, pages );
	}
	
	@Test
	void testNullString() {
		assertNull( AonJSONUtils.getString(null, ISBN) );
	}
	
	@Test
	void testString() {
		JSONObject json = new JSONObject(BOOKS_JSON);
		JSONArray array = json.getJSONArray(BOOKS);
		Stream<JSONObject> stream = AonJSONUtils.stream(array); 
		assertNotNull( stream );
		JSONObject book = stream.findFirst().orElse(null);
		assertNotNull( book );
		String isbn = AonJSONUtils.getString(book, ISBN);
		assertNotNull( isbn );
		assertEquals( "9781593279509", isbn);
	}

	@Test
	void testNullBoolean() {
		assertFalse( AonJSONUtils.getBoolean(null, ACTIVE) );
	}

	@Test
	void testWrongBoolean() {
		JSONObject json = new JSONObject(BOOKS_JSON);
		JSONArray array = json.getJSONArray(BOOKS);
		Stream<JSONObject> stream = AonJSONUtils.stream(array); 
		assertNotNull( stream );
		JSONObject book = stream.findFirst().orElse(null);
		assertNotNull( book );
		Boolean actve = AonJSONUtils.getBoolean(book, "WRONG");
		assertFalse( actve);
	}

	@Test
	void testBoolean() {
		JSONObject json = new JSONObject(BOOKS_JSON);
		JSONArray array = json.getJSONArray(BOOKS);
		Stream<JSONObject> stream = AonJSONUtils.stream(array); 
		assertNotNull( stream );
		JSONObject book = stream.findFirst().orElse(null);
		assertNotNull( book );
		Boolean actve = AonJSONUtils.getBoolean(book, ACTIVE);
		assertNotNull( actve );
		assertTrue( actve);
	}
	
	@Test
	void testDate() {
		JSONObject json = new JSONObject(BOOKS_JSON);
		
		try {
			Date nullDate = AonJSONUtils.getDate(null, null);
			assertNull( nullDate );
			nullDate = AonJSONUtils.getDate(json, null);
			assertNull( nullDate );
			nullDate = AonJSONUtils.getDate(json, "");
			assertNull( nullDate );
			nullDate = AonJSONUtils.getDate(json, " ");
			assertNull( nullDate );
		} catch (DateTimeParseException e) {
			// Nothing
		}
		
		JSONArray array = json.getJSONArray(BOOKS);
		Stream<JSONObject> stream = AonJSONUtils.stream(array); 
		assertNotNull( stream );
		JSONObject book = stream.findFirst().orElse(null);
		assertNotNull( book );
		assertDoesNotThrow(() -> AonJSONUtils.getDate(book, FROM));
		JSONObject book2 = AonJSONUtils.stream(array)
			.filter(j -> AonStringUtils.equals("9781484200766", AonJSONUtils.getString(j, ISBN)))
			.findFirst().orElse(null);	
		;
		assertNotNull( book2 );
		assertThrows(DateTimeParseException.class, () -> AonJSONUtils.getDate(book2, FROM));
	}

	@Test
	void testDateTime() {
		JSONObject json = new JSONObject(BOOKS_JSON);
		try {
			Date nullDate = AonJSONUtils.getDateTime(null, null);
			assertNull( nullDate );
			nullDate = AonJSONUtils.getDateTime(json, null);
			assertNull( nullDate );
			nullDate = AonJSONUtils.getDateTime(json, "");
			assertNull( nullDate );
			nullDate = AonJSONUtils.getDateTime(json, " ");
			assertNull( nullDate );
		} catch (DateTimeParseException e) {
			// Nothing
		}
		JSONArray array = json.getJSONArray(BOOKS);
		Stream<JSONObject> stream = AonJSONUtils.stream(array); 
		assertNotNull( stream );
		JSONObject book = stream.findFirst().orElse(null);
		assertNotNull( book );
		assertDoesNotThrow(() -> AonJSONUtils.getDateTime(book, PUBLISHED));
		JSONObject book2 = AonJSONUtils.stream(array)
			.filter(j -> AonStringUtils.equals("9781484200766", AonJSONUtils.getString(j, ISBN)))
			.findFirst().orElse(null);	
		;
		assertNotNull( book2 );
		assertThrows(DateTimeParseException.class, () -> AonJSONUtils.getDateTime(book2, PUBLISHED));
	}
	
}
