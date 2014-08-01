package com.esferalia.aon.gwt.codemirror.client.addon;

import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Doc;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.MarkOptions;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.user.client.Window;

public class AONFilter extends AONMarker {

	protected MarkOptions createOptions4Start(String label) {
		return MarkOptions.create().setCollapsed(true);
	}

	protected MarkOptions createOptions4End(String label) {
		return MarkOptions.create().setCollapsed(true);
	}

	protected MarkOptions createOptions4Input(String label) {
		boolean readOnly = isReadOnly(label);
		return MarkOptions.create().setClassName(getClassName(label))
				.setStartStyle(getStartStyle(label))
				.setEndStyle(getEndStyle(label)).setReadOnly(readOnly)
				.setAtomic(readOnly);
	}

	@Override
	protected void handleNoMatch(Doc doc, String expression, int from, int to, String prevLabel, String nextLabel) {
		boolean user = isUser(prevLabel) || isUser(nextLabel);
		doc.markText(getPos(expression, from), getPos(expression, to),
				MarkOptions.create().setCollapsed(user));
	}

	// ------------------------------------------------------------------------

	boolean isUser(String label) {
		return label != null && "user".equals(label);
	}

	boolean isReadOnly(String label) {
		return label != null && "read-only".equals(label);
	}

}

