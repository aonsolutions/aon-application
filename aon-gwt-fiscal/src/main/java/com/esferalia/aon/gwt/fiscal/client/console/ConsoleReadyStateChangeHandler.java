package com.esferalia.aon.gwt.fiscal.client.console;

import com.esferalia.aon.occam.api.model.console.ConsoleMessageType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class ConsoleReadyStateChangeHandler implements ReadyStateChangeHandler {
	
	private IConsoleLogger aonConsole;
	private AsyncCallback<Boolean> cbk;
	private int loaded = 0;
	private boolean hasError = false;
	private String errorMessage = null;
	
	public ConsoleReadyStateChangeHandler( IConsoleLogger aonConsole, AsyncCallback<Boolean> cbk ) {
		this.aonConsole = aonConsole;
		this.cbk = cbk;
	}
	
	@Override
	public void onReadyStateChange(XMLHttpRequest xhr) {
		int state = xhr.getReadyState();
		if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
			String text = xhr.getResponseText();
			try {
				for (JsConsoleMessage msg = read(text); text != null; msg = read(text)) {
					aonConsole.log(msg);
					if (!hasError && msg.getType() == ConsoleMessageType.ERROR ) {
						errorMessage = msg.getMessage();
						hasError = true; 
					}
				}
				
			} catch (IndexOutOfBoundsException e) {
			}
		}
		if (state == XMLHttpRequest.DONE) {
			if (cbk != null && hasError) {
				cbk.onFailure(new AonCoreException(errorMessage));
			} else {
				if (cbk != null) cbk.onSuccess(true);
			}
		}
	}

	private JsConsoleMessage read(String text) {
		for (int begin = loaded; begin < text.length(); begin++) {
			if (text.charAt(begin) == '{') {
				loaded = findEnd(text, begin + 1) + 1;
				String json = text.substring(begin, loaded);
				return JsonUtils.safeEval(json);
			}
		}
		throw new IndexOutOfBoundsException();
	}

	private int findEnd(String text, int start) {
		for (int end = start; end < text.length(); end++) {
			switch (text.charAt(end)) {
			case '}':
				return end;
			case '{':
				end = findEnd(text, end + 1);
			}
		}
		throw new IndexOutOfBoundsException();
	}
}
