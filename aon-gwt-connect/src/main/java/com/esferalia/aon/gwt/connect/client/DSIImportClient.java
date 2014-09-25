package com.esferalia.aon.gwt.connect.client;

import com.esferalia.aon.gwt.connect.shared.DSIImportService;
import com.esferalia.aon.gwt.connect.shared.JsEmpres;
import com.esferalia.aon.gwt.connect.shared.JsImportEvent;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class DSIImportClient implements DSIImportService {

	public static interface DSIImportCallback<T> {
		void onSuccess(T t);

		void onError(Throwable t);
	}

	public static void getEmpress(JsArrayString dbs, DSIImportCallback<JsArray<JsEmpres>> cb) {
		StringBuffer requestDataBuffer = new StringBuffer();
		requestDataBuffer.append(GET_ACTION_PARAM);
		requestDataBuffer.append('=');
		requestDataBuffer.append(GetAction.LIST_EMPREES);

		for (int i = 0; i < dbs.length(); i++) {
			requestDataBuffer.append('&');
			requestDataBuffer.append(GET_DB_PARAM);
			requestDataBuffer.append('=');
			requestDataBuffer.append(dbs.get(i));
		}

		try {
			send(requestDataBuffer.toString(), cb);
		} catch (RequestException e) {
			cb.onError(e);
		}
	}

	public static void imp0rt(JsArray<JsEmpres> empress, DSIImportCallback<JsImportEvent> cb) {
		StringBuffer requestDataBuffer = new StringBuffer();
		requestDataBuffer.append(GET_ACTION_PARAM);
		requestDataBuffer.append('=');
		requestDataBuffer.append(GetAction.IMPORT);

		for (int i = 0; i < empress.length(); i++) {
			JsEmpres empres = empress.get(i);
			requestDataBuffer.append('&');
			requestDataBuffer.append(GET_EMPRES_PARAM);
			requestDataBuffer.append('=');
			requestDataBuffer.append('{');
			requestDataBuffer.append("db:\"" + empres.getDB()+ "\","  );
			requestDataBuffer.append("sscod:\"" + empres.getSScod()+ "\","  );
			requestDataBuffer.append("ssnum:\"" + empres.getSSnum()+ "\""  );
			requestDataBuffer.append('}');
		}

		try {
			sendX(requestDataBuffer.toString(), cb);
		} catch (RequestException e) {
			cb.onError(e);
		}
	}

	private static <T extends JavaScriptObject> void send(String requestData,
			final DSIImportCallback<T> cb) throws RequestException {

		// Send request to server and catch any errors.
		String url = URL +"?"+requestData;
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		builder.setCallback(new RequestCallback() {

			@Override
			public void onError(Request request, Throwable exception) {
				cb.onError(exception);
				// Couldn't connect to server (could be timeout, SOP violation, etc.)
			} 

			@Override
			public void onResponseReceived(Request request, Response response) {
				Window.alert(response.getText());
				if (200 == response.getStatusCode())
					cb.onSuccess(JsonUtils.<T> safeEval(response.getText()));
				// TODO: Handle the error. Can get the status text from response.getStatusText()

			}

		});
		builder.send();

	}

	private static <T extends JavaScriptObject> void sendX(String requestData,
			final DSIImportCallback<T> cb) throws RequestException {

		// Send request to server and catch any errors.
		String url = URL +"?"+requestData;

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("GET", url);
		xhr.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

			private int loaded = 0;

			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();

				if (state == XMLHttpRequest.LOADING
						|| state == XMLHttpRequest.DONE) {

					String text = xhr.getResponseText();
					Window.alert( text );

					try {
						for (T t = read(text); text != null; t = read(text))
							cb.onSuccess(t);
					} catch (IndexOutOfBoundsException e) {
					}
				}

			}

			private T read(String text) {
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

		});

		xhr.send(requestData);

	}
}
