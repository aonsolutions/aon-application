package net.aonsolutions.aon.api.auth;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.aws.dynamodb.DYNAMODB;
import solutions.aon.aws.s3.S3;

public class AuthDyn {
	
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
				.setEmail(map.get(EMAIL).getS())
				.setDocument(map.get(DOCUMENT).getS())
				.setPassword(map.get(PASSWORD).getS())
				.setPhone(map.get(PHONE).getS())
				.setName(map.get(NAME).getS())
				.setSurname(map.get(SURNAME).getS())
				.setUuid(map.get(UUID).getS());	
	}
	
	public static Auth getAuth(String key, String value) {
		Map<String, AttributeValue> map = DYNAMODB.get2(TABLE, key, value);
		return map.isEmpty() ? new Auth() : new Auth()
				.setEmail(map.get(EMAIL).getS())
				.setDocument(map.get(DOCUMENT).getS())
				.setPassword(map.get(PASSWORD).getS())
				.setPhone(map.get(PHONE).getS())
				.setName(map.get(NAME).getS())
				.setSurname(map.get(SURNAME).getS())
				.setUuid(map.get(UUID).getS());
	}
	
	public static Auth putAuth(Auth auth) {
		checkTable();
		Map<String, AttributeValue> map = new HashMap<>();
		map.put(UUID, new AttributeValue(auth.getUuid()));
		map.put(EMAIL, new AttributeValue(auth.getEmail()));
		map.put(PASSWORD, new AttributeValue(auth.getPassword()));
		if(!AonStringUtils.isBlank(auth.getDocument())) map.put(DOCUMENT, new AttributeValue(auth.getDocument()));
		if(!AonStringUtils.isBlank(auth.getPhone())) map.put(PHONE, new AttributeValue(auth.getPhone()));
		if(!AonStringUtils.isBlank(auth.getName())) map.put(NAME, new AttributeValue(auth.getName()));
		if(!AonStringUtils.isBlank(auth.getSurname())) map.put(SURNAME, new AttributeValue(auth.getSurname()));
		
		if(!auth.getDevices().isEmpty()) {
			AttributeValue devices = new AttributeValue();
			auth.getDevices().stream().forEach(r -> {
				Map<String, AttributeValue> device = new HashMap<>();
				device.put(TYPE, new AttributeValue(r.getDeviceType().name()));
				device.put(TOKEN, new AttributeValue(r.getDeviceToken()));
				device.put(DATE, new AttributeValue(AonDateUtils.DATE_TIME_FORMAT));
				devices.setM(device);
			});
			map.put(DEVICES, devices);
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
					String fileId = S3.upload(S3.AUTH_ATTACH_BUCKET, auth.getUuid(), file);
					auth.setAvatar(fileId);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			putAuth(auth);
		});	
	}
 
}
