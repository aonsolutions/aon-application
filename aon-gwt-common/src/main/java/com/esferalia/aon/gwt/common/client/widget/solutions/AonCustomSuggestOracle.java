package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.SuggestOracle;

public class AonCustomSuggestOracle extends SuggestOracle {

	private static final int DEFAULT_LIMIT = 50;

	private final List<String> data = new ArrayList<>();
	private final List<String> normalizedData = new ArrayList<>();

	/**
	 * Sustituye por completo el contenido del oracle. Equivale al
	 * <code>clear() + addAll() + setDefaultSuggestionsFromText()</code> de
	 * MultiWordSuggestOracle, porque con la query vac\u00eda ya se devuelve todo.
	 */
	public void setData(Collection<String> items) {
		clear();

		if (null == items)
			return;

		for (String item : items)
			add(item);
	}

	public void add(String item) {
		if (null == item)
			return;

		data.add(item);
		normalizedData.add(normalize(item));
	}

	public void clear() {
		data.clear();
		normalizedData.clear();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public int size() {
		return data.size();
	}

	@Override
	public boolean isDisplayStringHTML() {
		return false;
	}

	@Override
	public void requestDefaultSuggestions(Request request, Callback callback) {
		requestSuggestions(request, callback);
	}

	@Override
	public void requestSuggestions(Request request, Callback callback) {
		List<Suggestion> suggestions = new ArrayList<>();

		String query = normalize(request.getQuery());
		int limit = request.getLimit() > 0 ? request.getLimit() : DEFAULT_LIMIT;

		int matches = 0;
		for (int i = 0; i < data.size(); i++) {
			String original = data.get(i);
			String normalized = normalizedData.get(i);

			if (query.isEmpty() || normalized.contains(query)) {
				matches++;
				if (suggestions.size() < limit)
					suggestions.add(new MultiWordSuggestion(original, original));
			}
		}

		Response response = new Response(suggestions);
		response.setMoreSuggestions(matches > suggestions.size());
		callback.onSuggestionsReady(request, response);
	}

	public static String normalize(String text) {
		if (text == null) return "";

		// 1) Pasamos a minúsculas
		text = text.toLowerCase();

		// 2) Eliminamos combining marks (NFD)
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c >= '\u0300' && c <= '\u036F') continue;
			sb.append(c);
		}
		text = sb.toString();

		// 3) Reemplazos directos conocidos
		text = text
			.replace("á", "a")
			.replace("é", "e")
			.replace("í", "i")
			.replace("ó", "o")
			.replace("ú", "u")
			.replace("ü", "u")
			.replace("ñ", "n");

		// 4) Reemplazo universal:
		// cualquier letra fuera de ASCII - su versión sin acento
		StringBuilder clean = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			// Si es ASCII, lo dejamos
			if (c >= 'a' && c <= 'z') {
				clean.append(c);
				continue;
			}

			// Si es número, lo dejamos
			if (c >= '0' && c <= '9') {
				clean.append(c);
				continue;
			}

			// Si es espacio, lo dejamos
			if (c == ' ') {
				clean.append(c);
				continue;
			}

			// Si es una letra rara, intentamos convertirla a base
			// (solo nos interesa la base latina)
			if (Character.isLetter(c)) {
				clean.append(removeWeirdAccent(c));
				continue;
			}

			// Otros caracteres se mantienen (.,-/ etc.)
			clean.append(c);
		}

		return clean.toString();
	}

	private static char removeWeirdAccent(char c) {
		switch (c) {
			case '\u01D2':
			case '\u022F':
			case '\u1ECF':
			case '\u1ECD':
			case '\u00F3':
			case '\u00F2':
			case '\u00F4':
			case '\u00F6':
			case '\u0151':
			case '\u01A1':
			case '\u0275':
			case '\uA74B':
			case '\uA74D':
				return 'o';

			case '\u01CE':
			case '\u0227':
			case '\u00E1':
			case '\u00E0':
			case '\u00E2':
			case '\u00E4':
			case '\u00E5':
			case '\u00E3':
			case '\u0103':
			case '\u0105':
				return 'a';

			case '\u00E9':
			case '\u00E8':
			case '\u00EA':
			case '\u00EB':
			case '\u011B':
			case '\u0117':
			case '\u0119':
				return 'e';

			case '\u00ED':
			case '\u00EC':
			case '\u00EE':
			case '\u00EF':
			case '\u0131':
				return 'i';

			case '\u00FA':
			case '\u00F9':
			case '\u00FB':
			case '\u00FC':
			case '\u016F':
			case '\u0171':
			case '\u016D':
				return 'u';

			case '\u00E7':
			case '\u0107':
			case '\u010D':
			case '\u0109':
				return 'c';

			case '\u00F1':
			case '\u0144':
			case '\u0148':
				return 'n';
		}
		return c;
	}

}