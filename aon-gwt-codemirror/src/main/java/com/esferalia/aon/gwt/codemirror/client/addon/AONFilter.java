package com.esferalia.aon.gwt.codemirror.client.addon;

import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Doc;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.MarkOptions;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Pos;
import com.google.gwt.user.client.Window;

public class AONFilter extends AONMarker {
	
	
	private Pos prevUserNoMatch ;
	private Pos lastUserEndMatch ;

	@Override
	protected MarkOptions createOptions4Input(String label) {
		MarkOptions options = MarkOptions.create();
		return isReadOnly(label) ? customizeOptions4ReadOnlyInput(options,
				label) : customizeOptions4UserInput(options, label);
	}

	@Override
	protected void handleNoMatch(Doc doc, String expression, int from, int to,
			String prevLabel, String nextLabel) {
		boolean user = isUser(prevLabel) || isUser(nextLabel);
		if (user)
			handleNoMatchUser(doc, expression, from, to, prevLabel, nextLabel);
		else
			handleNoMatchReadOnly(doc, expression, from, to, prevLabel,
					nextLabel);
	}
	
	@Override
	protected void markStart(Doc doc, String label, Pos to, Pos from) {
		if ( isReadOnly(label) ) 
			doc.markText(to, from, createOptions4ReadOnlyStart(label));
		else
			doc.markText(prevUserNoMatch, from, createOptions4UserStart(label));
	}


	@Override
	protected void markEnd(Doc doc, String label, Pos to, Pos from) {
		if ( isReadOnly(label) ) 
			doc.markText(to, from, createOptions4ReadOnlyEnd(label));
		else 
			lastUserEndMatch = to;
	}

	// ------------------------------------------------------------------------

	protected MarkOptions createOptions4UserStart(String label) {
		return MarkOptions.create().setCollapsed(true).setReadOnly(true).setInclusiveLeft(true);
	}


	protected MarkOptions createOptions4ReadOnlyStart(String label) {
		return MarkOptions.create().setCollapsed(true).setReadOnly(true)
				.setInclusiveLeft(false);
	}

	protected MarkOptions createOptions4ReadOnlyEnd(String label) {
		return MarkOptions.create().setCollapsed(true).setReadOnly(true)
				.setInclusiveRight(false);
	}

	protected MarkOptions createOptions4UserEnd(String label) {
		return MarkOptions.create().setCollapsed(true).setReadOnly(true)
				.setInclusiveRight(true);
	}

	protected void handleNoMatchUser(Doc doc, String expression, int from,
			int to, String prevLabel, String nextLabel) {
		if ( isUser(nextLabel)) 
			prevUserNoMatch = getPos(expression, from);
		else if ( isUser(prevLabel))  {
			doc.markText(lastUserEndMatch, getPos(expression, to), createOptions4UserEnd(prevLabel));
		}
	}

	protected void handleNoMatchReadOnly(Doc doc, String expression, int to,
			int from, String prevLabel, String nextLabel) {
	}

	protected MarkOptions customizeOptions4UserInput(MarkOptions options,
			String label) {
		return options.setInclusiveLeft(false).setInclusiveRight(false);
	}

	protected MarkOptions customizeOptions4ReadOnlyInput(MarkOptions options,
			String label) {
		return options.setClassName(getClassName(label))
				.setStartStyle(getStartStyle(label))
				.setEndStyle(getEndStyle(label)).setReadOnly(true)
				.setAtomic(true).setInclusiveLeft(true).setInclusiveRight(true);
	}

	boolean isUser(String label) {
		return label != null && "user".equals(label);
	}

	boolean isReadOnly(String label) {
		return label != null && "read-only".equals(label);
	}

}
