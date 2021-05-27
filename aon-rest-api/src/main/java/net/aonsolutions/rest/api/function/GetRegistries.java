package net.aonsolutions.rest.api.function;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.EmptyStackException;
import java.util.Properties;
import java.util.Stack;
import java.util.TimeZone;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.esferalia.aon.watson.util.AonStringUtils;

public class GetRegistries implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

		JSONTokener jsonTokener = new JSONTokener(input);

		JSONObject event = new JSONObject(jsonTokener);
		JSONObject queryStringParameters = event.getJSONObject("queryStringParameters");
		String documents[] = AonStringUtils.split(queryStringParameters.getString("documents"), ',');
		
		JSONArray registriesJson = getRegistries(documents);

		JSONObject responseJson = new JSONObject();

		JSONObject headerJson = new JSONObject();
		headerJson.put("Access-Control-Allow-Origin", "*");

		responseJson.put("statusCode", 200);
		responseJson.put("headers", headerJson);
		responseJson.put("body", registriesJson.toString());

		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toString());
		writer.close();

	}

	private static Connection getConnection() throws SQLException {
		String port = "3306";
		String host = System.getenv("DB_HOST");
		String user = System.getenv("DB_USER");
		String password = System.getenv("DB_PASSWD");
		String database = System.getenv("DB_NAME");

		Properties properties = new Properties();
		properties.setProperty("user", user);
		properties.setProperty("password", password);
		properties.setProperty("useSSL", "false");
		properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
		String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);

		return DriverManager.getConnection(url, properties);
	}

	private static DSLContext getDSLContext(Connection connection) throws SQLException {

		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		return dslContext;
	}


	private static JSONArray getRegistries(String documents[]) throws IOException {
		try (Connection connection = getConnection(); DSLContext dslContext = getDSLContext(connection);) {
			
			
			Stack<JSONObject> registries = new Stack<JSONObject>();
			
			dslContext
			.select()
			.from(REGISTRY)
			.leftJoin(RADDRESS).onKey()
			.leftJoin(GEOZONE).onKey()
			.where(REGISTRY.DOCUMENT.in(documents))
			.orderBy(REGISTRY.DOCUMENT.asc())
			.fetchStream()
			.forEach(r -> {
//				export interface Registry {
//				  document?: string;
//				  document_country?: string;
//				  name?: string;
//				  address?: Address;
//				}
//			export interface Address {
//				  address?: string;
//				  city?: string;
//				  province?: string;
//				  postal_code?: string;
//				  country?: string;
//				}
				
				JSONObject registryJson = null; 
				JSONObject addressJson = null;
				try {
					if ( AonStringUtils.equalsIgnoreCase(registries.peek().getString("document"), r.get(REGISTRY.DOCUMENT))) {
						registryJson = registries.pop();
						addressJson = registryJson.getJSONObject("address");
					}
						
				} catch (EmptyStackException e ) {
				}
				if ( registryJson == null ) {
					registryJson = new JSONObject();
					addressJson = new JSONObject();
				}
				
				registryJson.put("document", r.get(REGISTRY.DOCUMENT));
				
				replace(registryJson,"name", r.get(REGISTRY.NAME));				
				replace(registryJson,"document_country", r.get(REGISTRY.DOCUMENT_COUNTRY));

				
				replace(addressJson,"city", r.get(RADDRESS.CITY));
				replace(addressJson,"country", r.get(REGISTRY.DOCUMENT_COUNTRY));
				replace(addressJson,"province", r.get(GEOZONE.NAME));
				replace(addressJson,"postal_code", r.get(RADDRESS.ZIP));
				
				String address = AonStringUtils.join(
						new String[] {
						r.get(RADDRESS.STREET_TYPE)	,
						r.get(RADDRESS.ADDRESS),
						r.get(RADDRESS.NUMBER),
						r.get(RADDRESS.ADDRESS2),
						r.get(RADDRESS.ADDRESS3),	
						}, 
						' ');
				
				replace(addressJson,"address", address.toString().trim());
				
				registryJson.put("address", addressJson);
								
				registries.push(registryJson);
			})
			;
			
			return new JSONArray(registries);

		} catch ( DataAccessException | SQLException e ) {
			throw new IOException(e);
		}
	}
	
	private static void replace(JSONObject jsonObject, String key, String value ) {
		if ( AonStringUtils.isBlank(value) )
			return;
		
		if ( jsonObject.has(key)) {
			String old = jsonObject.getString(key);
			if ( AonStringUtils.isBlank(old)) {
				jsonObject.put(key, value);
			} else if (old.length() < value.length()) {
				jsonObject.put(key, value);
			}
		}
		else {
			jsonObject.put(key, value);
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(getRegistries(args).toString(2));
	}

	
	
}
