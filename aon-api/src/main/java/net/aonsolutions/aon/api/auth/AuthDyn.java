package net.aonsolutions.aon.api.auth;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue.Builder;
import solutions.aon.aws.dynamodb.DYNAMODB;
import solutions.aon.aws.s3.S3;

public class AuthDyn {
	
	public static final String AUTH_ATTACH_BUCKET = "aon-auth-attach";
	
	private static final String TABLE = "auth";
	private static final String EMAIL = "email";
	private static final String DOCUMENT = "document";
	private static final String PASSWORD = "password";
	private static final String PHONE = "phone";
	private static final String NAME = "name";
	private static final String SURNAME = "surname";
	private static final String UUID = "uuid";
	private static final String AVATAR = "avatar";
	private static final String DEVICES = "devices";
	private static final String TYPE = "type";
	private static final String TOKEN = "token";
	private static final String DATE = "date";

	private AuthDyn() {
		
	}
	
	public static Auth getAuth(String email) {
		return getAuth(EMAIL, email);
	}

	public static Auth getAuthByUuid(String uuid) {
		Map<String, AttributeValue> map = DYNAMODB.get(TABLE, UUID, uuid);
		return map.isEmpty() ? new Auth() : new Auth()
				.setEmail(map.get(EMAIL).s())
				.setDocument(map.get(DOCUMENT).s())
				.setPassword(map.get(PASSWORD).s())
				.setPhone(map.get(PHONE).s())
				.setName(map.get(NAME).s())
				.setSurname(map.get(SURNAME).s())
				.setUuid(map.get(UUID).s());	
	}
	
	public static Auth getAuth(String key, String value) {
		Map<String, AttributeValue> map = DYNAMODB.get2(TABLE, key, value);
		return map.isEmpty() ? new Auth() : new Auth()
				.setEmail(map.get(EMAIL).s())
				.setDocument(map.get(DOCUMENT).s())
				.setPassword(map.get(PASSWORD).s())
				.setPhone(map.get(PHONE).s())
				.setName(map.get(NAME).s())
				.setSurname(map.get(SURNAME).s())
				.setUuid(map.get(UUID).s());
	}
	
	public static Auth putAuth(Auth auth) {
		checkTable();
		Map<String, AttributeValue> map = new HashMap<>();
		map.put(UUID, AttributeValue.builder().s(auth.getUuid()).build());
		map.put(EMAIL, AttributeValue.builder().s(auth.getEmail()).build());
		map.put(PASSWORD, AttributeValue.builder().s(auth.getPassword()).build());
		if(!AonStringUtils.isBlank(auth.getDocument())) map.put(DOCUMENT, AttributeValue.builder().s(auth.getDocument()).build());
		if(!AonStringUtils.isBlank(auth.getPhone())) map.put(PHONE, AttributeValue.builder().s(auth.getPhone()).build());
		if(!AonStringUtils.isBlank(auth.getName())) map.put(NAME, AttributeValue.builder().s(auth.getName()).build());
		if(!AonStringUtils.isBlank(auth.getSurname())) map.put(SURNAME, AttributeValue.builder().s(auth.getSurname()).build());
		
		if(!auth.getDevices().isEmpty()) {
			Builder devices = AttributeValue.builder();
			auth.getDevices().stream().forEach(r -> {
				Map<String, AttributeValue> device = new HashMap<>();
				device.put(TYPE, AttributeValue.builder().s(r.getDeviceType().name()).build());
				device.put(TOKEN, AttributeValue.builder().s(r.getDeviceToken()).build());
				device.put(DATE, AttributeValue.builder().s(AonDateUtils.DATE_TIME_FORMAT).build());
				devices.m(device);
			});
			map.put(DEVICES, devices.build());
		}
		DYNAMODB.put(TABLE, map);
		return auth;
	}
	
	public static void checkTable() {
		if(!DYNAMODB.exist(TABLE)) {
			DYNAMODB.createTable(TABLE, UUID);
		}
	}
	
	public static void backup() {	
		AON_SOLUTIONS.getAuthsWithDevices(f -> f.getEmailProperty().eq("aibanez@aonsolutions.es")).stream().forEach(auth -> {
			if(auth.getAttach().getData() != null) {
				try {
					File file = File.createTempFile(AVATAR, auth.getAttach().getMimetype().getExtension());
					AonFileUtils.writeByteArrayToFile(file, auth.getAttach().getData());
					String fileId = S3.upload(AUTH_ATTACH_BUCKET, auth.getUuid(), file);
					auth.setAvatar(fileId);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			putAuth(auth);
		});	
	}
 
}
