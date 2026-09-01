package com.esferalia.aon.gwt.common.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Traduce una respuesta HTTP a AsyncCallback garantizando que SIEMPRE se
 * invoca onSuccess u onFailure. El patron anterior (if statusCode == 200)
 * dejaba el callback sin resolver ante cualquier error del servidor.
 */
public abstract class AonHttpCallback<T> implements RequestCallback {

    private final AsyncCallback<T> callback;

    protected AonHttpCallback(AsyncCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        int status = response.getStatusCode();

        if (Response.SC_OK != status) {
            callback.onFailure(new AonHttpException(describe(status, response), status));
            return;
        }

        try {
            callback.onSuccess(parse(response));
        } catch (Exception e) {
            callback.onFailure(new AonHttpException("Respuesta no valida: " + e.getMessage(), status));
        }
    }

    @Override
    public void onError(Request request, Throwable exception) {
        callback.onFailure(exception);
    }

    /** Solo se invoca con status 200. */
    protected abstract T parse(Response response) throws Exception;

    private static String describe(int status, Response response) {
        // status 0 = red caida, CORS o peticion abortada; no hay cuerpo util
        if (0 == status)
            return "No se pudo contactar con el servidor";

        String text = response.getText();
        return "Error " + status
                + (null != text && !text.isEmpty() ? " : " + text : "");
    }
}