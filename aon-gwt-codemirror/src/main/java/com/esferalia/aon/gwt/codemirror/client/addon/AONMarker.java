package com.esferalia.aon.gwt.codemirror.client.addon;

import com.esferalia.aon.gwt.codemirror.client.ui.CodeArea;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.ChangeEvent;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Doc;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.DocumentChangeHandler;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.MarkOptions;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Pos;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.TextMarker;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;

public class AONMarker extends DocumentChangeHandler {

	private static final int START_GROUP = 1;
	private static final int LABEL_GROUP = 2;
	private static final int INPUT_GROUP = 3;
	private static final int END_GROUP = 4;

	private static String PATTERN = "(/\\*(user|read-only)\\*/)((?:[^/]|(?:/[^\\*]))*)(/\\*\\*/)";

	public AONMarker() {
		super();
	}
	
	public void init(CodeArea codeArea) {
		handleEvent(codeArea.getDoc(), (ChangeEvent) null);
	}

	@Override
	public void handleEvent(Doc doc, ChangeEvent event) {

		cleanMarks(doc);

		String expression = doc.getValue();
		if (expression == null || expression.isEmpty())
			return;
		
		int prevIndex = 0;
		RegExp regExp = RegExp.compile(PATTERN, "g");
		String lastLabel = null;
		for (MatchResult result = regExp.exec(expression); result != null; result = regExp
				.exec(expression)) {
			int index = result.getIndex();
			String label = result.getGroup(LABEL_GROUP);
			handleNoMatch(doc, expression, prevIndex, index -1, lastLabel, label);
			prevIndex = regExp.getLastIndex();
			handleMatch(doc, expression, result);
			lastLabel = label;
		}
		handleNoMatch(doc, expression, prevIndex, expression.length(), lastLabel, null );
	}

	// ------------------------------------------------------------------------

	protected void handleMatch(Doc doc, String expression, MatchResult result) {
		String label = result.getGroup(LABEL_GROUP);

		int to = result.getIndex();
		String start = result.getGroup(START_GROUP);
		int from = to + start.length();
		markStart(doc, label, getPos(expression, to), getPos(expression, from));
		to = from;
		String input = result.getGroup(INPUT_GROUP);
		from += input.length();
		markInput(doc, label, getPos(expression, to), getPos(expression, from));

		to = from;
		String end = result.getGroup(END_GROUP);
		from += end.length();
		markEnd(doc, label, getPos(expression, to), getPos(expression, from));

	}

	protected void handleNoMatch(Doc doc, String expression, int start, int end, String prevLabel, String nextLabel) {
	}

	protected void markStart(Doc doc, String label, Pos to, Pos from) {
		doc.markText(to, from, createOptions4Start(label));
	}

	protected void markInput(Doc doc, String label, Pos to, Pos from) {
		doc.markText(to, from, createOptions4Input(label));
	}

	protected void markEnd(Doc doc, String label, Pos to, Pos from) {
		doc.markText(to, from, createOptions4End(label));
	}

	protected MarkOptions createOptions4Start(String label) {
		return MarkOptions.create().setClassName(getClassName(label))
				.setStartStyle(getStartStyle(label));
	}

	protected MarkOptions createOptions4End(String label) {
		return MarkOptions.create().setClassName(getClassName(label))
				.setEndStyle(getEndStyle(label));
	}

	protected MarkOptions createOptions4Input(String label) {
		return MarkOptions.create().setClassName(getClassName(label));
	}



	// ------------------------------------------------------------------------

	public static void cleanMarks(Doc doc) {
		for (TextMarker marker : doc.getAllMarks())
			marker.clear();
	}

	public static Pos getPos(String str, int index) {
		int line = 0;
		int ln = 0;
		for (int i = 0; i <= index && i < str.length(); i++) {
			if (str.charAt(i) == '\n') {
				line += 1;
				ln = i + 1;
			}
		}
		return Pos.create(line, index - ln);
	}

	public static String getClassName(String label) {
		return "cm-mark-" + label;
	}

	public static String getEndStyle(String label) {
		return getClassName(label) + "-end";
	}

	public  static String getStartStyle(String label) {
		return getClassName(label) + "-start";
	}
}


