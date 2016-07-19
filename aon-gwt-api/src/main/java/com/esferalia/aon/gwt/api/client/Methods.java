package com.esferalia.aon.gwt.api.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class Methods {
	
	public final String HTTP_GET = "GET";
	private final String HTTP_POST = "POST";
	protected  String accessToken = "";
	public boolean authorized = false;
	
	protected <T extends JavaScriptObject> void get(String url, AsyncCallback<T> callback) {
		String requestUrl = makeRequestUrl(url);
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(requestUrl, hookCallback(callback));
	}

	private <T extends JavaScriptObject> AsyncCallback<T> hookCallback(final AsyncCallback<T> callback) {
		return new AsyncCallback<T>() {
			@Override
			public void onSuccess(T result) {
				if (accessToken != null) authorized = true;
				callback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Ha fallado : " + caught.getMessage());
				callback.onFailure(caught);
			}
		};
	}
	
	private  String makeRequestUrl(String url) {
		String prefix = "?";
		if (url.contains("?")) prefix = "&";
		if (accessToken != null) url += prefix + "access_token=" + accessToken;
		return url;
	}
	
	private String makeNamesString(String[] names) {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < names.length; x++) {
			sb.append(names[x]);
			sb.append(',');
		}
		return sb.toString().substring(0, sb.length() - 1);
	}
	
	protected <T extends JavaScriptObject> void post(String url, String requestData, AsyncCallback<T> callback) {
		String requestUrl = makeRequestUrl(url);

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(HTTP_POST, requestUrl);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			private int loaded = 0;
	
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
	
				if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
					String text = xhr.getResponseText();
					try {
						for (T result = read(text); text != null; result = read(text))
							callback.onSuccess(result);
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
	
	protected <T extends JavaScriptObject> void post2(String url, String request,
			AsyncCallback<T> callback) {

		String requestUrl = makeRequestUrl(url);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
				requestUrl);

		final AsyncCallback<T> hookedCallback = hookCallback(callback);
		final StringBuilder log = new StringBuilder();
		log.append("[POST]" + requestUrl + "\n" + request);

		try {
			builder.sendRequest(request, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request,
						Response response) {

					T result = JsonUtils.<T> safeEval(response.getText());
					log.append("\n\n--" + response.getStatusText() + ":"
							+ response.getStatusCode() + "\n"
							+ response.getText());
					hookedCallback.onSuccess(result);
					GWT.log(log.toString());
				}

				@Override
				public void onError(Request request, Throwable e) {
					log.append("\n\n--" + e.getStackTrace());
					hookedCallback.onFailure(e);
					GWT.log(log.toString());
				}
			});
		} catch (RequestException e) {
			log.append("\n\n--" + e.getStackTrace());
			hookedCallback.onFailure(e);
			GWT.log(log.toString());
		}
	}
	
	protected <T extends JavaScriptObject> void delete(String url, AsyncCallback<T> callback) {
		String requestUrl = makeRequestUrl(url);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.DELETE,
				requestUrl);
		final AsyncCallback<T> hookedCallback = hookCallback(callback);
		final StringBuilder log = new StringBuilder();
		log.append(" [DELETE] ---> " + requestUrl);

		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					log.append("\n\n--" + response.getStatusText() + ":"
							+ response.getStatusCode() + "\n"
							+ response.getText());
					hookedCallback.onSuccess(null);
					GWT.log(log.toString());
				}

				@Override
				public void onError(Request request, Throwable e) {
					log.append("\n\n--" + e.getStackTrace());
					hookedCallback.onFailure(e);
					GWT.log(log.toString());
				}
			});

		} catch (RequestException ex) {
			log.append("\n\n--" + ex.getStackTrace());
			hookedCallback.onFailure(ex);
			GWT.log(log.toString());
		}
	}
	
	
}
