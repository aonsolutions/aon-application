package aon.bank;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class CheckIt {

	public static void main(String[] args) throws IOException {
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//		HttpPost httpPost = new HttpPost("https://www.checkitbancario.com/openapi/empresas/add");
//		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("claveApi", "84d9ee44e457ddef7f2c4f25dc8fa865"));
//		params.add(new BasicNameValuePair("nombre", "Rayson & Co."));
//		params.add(new BasicNameValuePair("cif", "58025118M"));
//		params.add(new BasicNameValuePair("email", "09iker05@gmail.com"));
//		params.add(new BasicNameValuePair("nombrecorto", "Rayson"));
//		httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//		CloseableHttpResponse response = httpClient.execute(httpPost);
//		if (response.getEntity() != null) {
//			String body = EntityUtils.toString(response.getEntity(), "UTF-8");
//			System.out.println(body);
//		}
//	}
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			// [{"userID":"Código de
			// empresa","userPassword":"Usuario","userPIN":"Contraseña"}]
			HttpPost httpPost = new HttpPost("https://www.checkitbancario.com/openapi/cuentas");
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("claveApi", "84d9ee44e457ddef7f2c4f25dc8fa865"));
			params.add(new BasicNameValuePair("empresa_id", "11413"));
			params.add(new BasicNameValuePair("tipo_cuenta_bancaria_id", "1"));
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			CloseableHttpResponse response = httpClient.execute(httpPost);
			if (response.getEntity() != null) {
				String body = EntityUtils.toString(response.getEntity(), "UTF-8");
				System.out.println(body);
			}
		}
	}

}
