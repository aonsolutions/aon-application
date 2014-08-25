package com.esferalia.aon.gwt.codemirror.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;

public class CodeMirror extends JavaScriptObject {

	public static final class Doc extends JavaScriptObject {
		protected Doc() {
		}

		public native int getFirstLine() /*-{
			return this.firstLine();
		}-*/;

		public native int getLastLine() /*-{
			return this.lastLine();
		}-*/;

		public native int getLineCount() /*-{
			return this.lineCount();
		}-*/;

		public native CodeMirror getEditor() /*-{
			return this.getEditor();
		}-*/;

		public native String getValue() /*-{
			return this.getValue();
		}-*/;

		public native String getLine(int line) /*-{
			return this.getLine(line);
		}-*/;

		public int getLineCount(int line) {
			return getLine(line).length();
		}

		public native TextMarker markText(Pos from, Pos to, MarkOptions options) /*-{
			return this.markText(from, to, options);
		}-*/;

		public native TextMarker[] getAllMarks() /*-{
			return this.getAllMarks();
		}-*/;

	}

	public static final class Pos extends JavaScriptObject {

		public static Pos create(int line, int ch) {
			Pos pos = JavaScriptObject.createObject().cast();
			return pos.setLine(line).setCh(ch);
		}

		protected Pos() {
		}

		public native int getCh() /*-{
			return this.ch;
		}-*/;

		public native Pos setCh(int ch) /*-{
			this.ch = ch;
			return this;
		}-*/;

		public native int getLine() /*-{
			return this.line;
		}-*/;

		public native Pos setLine(int line) /*-{
			this.line = line;
			return this;
		}-*/;

		public native Pos set(int line, int ch) /*-{
			this.line = line;
			this.ch = ch;
			return this;
		}-*/;

	}

	public static final class Token extends JavaScriptObject {
		protected Token() {
		}

		public native int getStart() /*-{
			return this.start;
		}-*/;

		public native int getEnd() /*-{
			return this.end;
		}-*/;

		public native String getType() /*-{
			return this.type;
		}-*/;

		public native String getString() /*-{
			return this.string;
		}-*/;
	}

	public static final class TextMarker extends JavaScriptObject {
		protected TextMarker() {
		}

		public native void clear() /*-{
			this.clear();
		}-*/;
	}

	public static final class MarkOptions extends JavaScriptObject {
		public static MarkOptions create() {
			return JavaScriptObject.createObject().cast();
		}

		protected MarkOptions() {
		}

		public native MarkOptions setStartStyle(String startStyle) /*-{
			this.startStyle = startStyle;
			return this;
		}-*/;

		public native MarkOptions setEndStyle(String endStyle) /*-{
			this.endStyle = endStyle;
			return this;
		}-*/;

		public native MarkOptions setClassName(String className) /*-{
			this.className = className;
			return this;
		}-*/;

		public native MarkOptions setAtomic(boolean atomic) /*-{
			this.atomic = atomic;
			return this;
		}-*/;

		public native MarkOptions setReadOnly(boolean readOnly) /*-{
			this.readOnly = readOnly;
			return this;
		}-*/;

		public native MarkOptions setCollapsed(boolean collapsed) /*-{
			this.collapsed = collapsed;
			return this;
		}-*/;

		public native MarkOptions setInclusiveLeft(boolean inclusiveLeft) /*-{
			this.inclusiveLeft = inclusiveLeft;
			return this;
		}-*/;

		public native MarkOptions setInclusiveRight(boolean inclusiveRight) /*-{
			this.inclusiveRight = inclusiveRight;
			return this;
		}-*/;
	}

	public static final class ChangeEvent extends JavaScriptObject {
		protected ChangeEvent() {
		}

	}

	public static final class Stream extends JavaScriptObject {

		public static Stream create() {
			return JavaScriptObject.createObject().cast();
		}

		protected Stream() {
		}

		/**
		 * 
		 * @return
		 */
		public native boolean eol()/*-{
			return this.eol();
		}-*/;

		public native boolean sol()/*-{
			return this.sol();
		}-*/;

		public native String peek()/*-{
			return this.peek();
		}-*/;

		public native String next()/*-{
			return this.next();
		}-*/;

		public native int column()/*-{
			return this.peek();
		}-*/;

		public native String current()/*-{
			return this.peek();
		}-*/;

		public native String eat(String ch)/*-{
			return this.eat(ch);
		}-*/;

		public native void skipToEnd()/*-{
			return this.skipToEnd();
		}-*/;

		public native boolean skipTo(String ch)/*-{
			return this.skipTo(ch);
		}-*/;

		public native boolean match(String pattern)/*-{
			return this.match(pattern, true);
		}-*/;

		public native JsArrayString match(RegExp regexp)/*-{
			return this.match(regexp, true);
		}-*/;
	}

	public static final class Configuration extends JavaScriptObject {

		public static Configuration create() {
			return JavaScriptObject.createObject().cast();
		}

		protected Configuration() {
			// TODO Auto-generated constructor stub
		}

		public native void setValue(String value)/*-{
			this.value = value;
		}-*/;

		public native void setMode(String mode)/*-{
			this.mode = mode;
		}-*/;

		public native void setMode(ModeConfiguration mode)/*-{
			this.mode = mode;
		}-*/;

		public native void setTheme(String theme)/*-{
			this.theme = theme;
		}-*/;

		public native void setKeyMap(String keyMap)/*-{
			this.keyMap = keyMap;
		}-*/;

		public native void setReadOnly(boolean readOnly)/*-{
			this.readOnly = readOnly;
		}-*/;

		public native void setLineNumbers(boolean lineNumbers)/*-{
			this.lineNumbers = lineNumbers;
		}-*/;

		public native void setMatchBrackets(boolean matchBrackets)/*-{
			this.matchBrackets = matchBrackets;
		}-*/;
	}

	public static class ModeConfiguration extends JavaScriptObject {

		protected ModeConfiguration() {
		}

		public final native ModeConfiguration setName(String name)/*-{
			this.name = name;
			return this;
		}-*/;

	}

	public abstract static class EventHandler<I, E> {

		Object eventHandler;

		public EventHandler() {
			eventHandler = this;
			exportFunc();
		}

		public abstract void handleEvent(I source, E event);

		// --------------------------------------------------------------------

		public final native void exportFunc()/*-{
			function _exportFunc(handler) {
				var _handleEvent = function() {
					@com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.EventHandler::handleEvent(Ljava/lang/Object;[Ljava/lang/Object;)(handler,arguments)
				}
				return _handleEvent;
			}
			this.exportFunc = _exportFunc(this.@com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.EventHandler::eventHandler);

		}-*/;

		public static void handleEvent(Object object, Object arguments[]) {
			((EventHandler) object).handleEvent(arguments[0], arguments[1]);
		}

	}

	public abstract static class DocumentChangeHandler extends
			EventHandler<Doc, ChangeEvent> {

		public DocumentChangeHandler() {
			super();
		}

	}

	public static native String getVersion() /*-{
		return $wnd.CodeMirror.version;
	}-*/;

	public static native CodeMirror fromTextArea(
			TextAreaElement textAreaElement, Configuration config) /*-{
		return $wnd.CodeMirror.fromTextArea(textAreaElement, config);
	}-*/;

	public static native final void defineMIME(String mime, String mode)/*-{
		$wnd.CodeMirror.defineMIME(mime, mode);
	}-*/;

	public static native final void defineMode(String name,
			JavaScriptObject mode)/*-{
		$wnd.CodeMirror.defineMode(name, mode.define);
	}-*/;

	// ------------------------------------------------------------------------

	protected CodeMirror() {
	}

	public final native Doc getDoc() /*-{
		return this.getDoc();
	}-*/;

	/**
	 * Copy the content of the editor into the textarea.
	 */
	public final native void save() /*-{
		this.save();
	}-*/;

	/**
	 * Set the editor content.
	 * 
	 * @param value
	 */
	public final native void setValue(String value) /*-{
		this.getDoc().setValue(value);
	}-*/;

	public final native void setSize(int width, int height) /*-{
		this.setSize(width, height);
	}-*/;

	/**
	 * Get the current editor content. Separate lines with '\n'.
	 * 
	 * @return Editor content
	 */
	public final native String getValue() /*-{
		return this.getDoc().getValue();
	}-*/;

	/**
	 * Get the current editor content.
	 * 
	 * @param separator
	 *            String to be used to separate lines.
	 * @return Editor content.
	 */
	public final native String getValue(String separator) /*-{
		return this.getDoc().getValue(separator);
	}-*/;

	/**
	 * Returns the DOM node that represents the editor.
	 * 
	 * @return DOM node that represents the editor.
	 */
	public final native Element getWrapperElement() /*-{
		return this.getWrapperElement();
	}-*/;

	public final void addKeyMap(String key, String action) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(key, action);
		addKeyMap(map);
	}

	/**
	 * Attach an additional key map to the editor.
	 * 
	 * @param map
	 */
	public final native void addKeyMap(Map<String, String> map) /*-{
		this.addKeyMap(map);
	}-*/;

	public final native void addOverlay(String mode) /*-{
		this.addOverlay(mode);
	}-*/;

	public final native void addDocumentChangeHandler(
			DocumentChangeHandler handler) /*-{
		$wnd.CodeMirror.on(this.getDoc(), "change", handler.exportFunc);
	}-*/;

	public final native void removeDocumentChangeHandler(
			DocumentChangeHandler handler) /*-{
		$wnd.CodeMirror.off(this.getDoc(), "change", handler.exportFunc);
	}-*/;

	public final native Token getTokenAt(Pos pos, boolean precise) /*-{
		return this.getTokenAt(pos, precise);
	}-*/;

	public final native <M extends JavaScriptObject> M getMode() /*-{
		return this.getDoc().getMode();
	}-*/;

}
