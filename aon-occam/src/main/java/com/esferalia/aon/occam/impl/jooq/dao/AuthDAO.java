package com.esferalia.aon.occam.impl.jooq.dao;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.model.security.DeviceType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.validation.AuthValidation;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.aws.dynamodb.DYNAMODB;
import solutions.aon.aws.s3.S3;

public class AuthDAO {

    private AuthDAO() {
        
    }

    public static Stream<Auth> getStream(List<String> uuids) {
        return DYNAMODB.scan(IJsonNames.AUTH, IJsonNames.UUID, uuids)
                .stream().map(new AuthFiller());
    }
    
    public static List<Auth> getList(List<String> uuids) {
        return getStream(uuids).collect(Collectors.toCollection(LinkedList::new));
    }
    
    public static Auth getAuthByEmail(String email) {
        return getAuth(IJsonNames.EMAIL, email);
    }
    
    public static Auth getAuthByPhone(String phone) {
        return getAuth(IJsonNames.PHONE, phone);
    }
    
    public static Auth getAuthByDocument(String document) {
        return getAuth(IJsonNames.DOCUMENT, document);
    }

    public static Auth getAuthByUuid(String uuid) {
        Map<String, AttributeValue> map = DYNAMODB.get(IJsonNames.AUTH, IJsonNames.UUID, uuid);
        return getAuth(map);
    }
    
    public static Auth getAuth(String key, String value) {
        Map<String, AttributeValue> map = DYNAMODB.scan(IJsonNames.AUTH, key, value);
        return getAuth(map);
    }
    
    private static Auth getAuth(Map<String, AttributeValue> map) {
        return map == null || map.isEmpty() ? new Auth() : new Auth()
                .setEmail(map.get(IJsonNames.EMAIL).getS())
                .setDocument(map.get(IJsonNames.DOCUMENT).getS())
                .setPassword(map.get(IJsonNames.PASSWORD).getS())
                .setPhone(map.get(IJsonNames.PHONE).getS())
                .setName(map.get(IJsonNames.NAME).getS())
                .setSurname(map.get(IJsonNames.SURNAME).getS())
                .setUuid(map.get(IJsonNames.UUID).getS())
                .setExpiredDate(map.containsKey(IJsonNames.EXPIRED_DATE)
                        ? AonDateUtils.dateTimeParse(map.get(IJsonNames.EXPIRED_DATE).getS())
                        : null);
    }
    
    public static Auth save(Auth auth) {
        AuthValidation.validate(auth);
        return putAuth(auth);
    }
    
    private static Auth putAuth(Auth auth) {
        // checkTable();
        Map<String, AttributeValue> map = new HashMap<>();
        map.put(IJsonNames.UUID,new AttributeValue(auth.getUuid()));
        map.put(IJsonNames.EMAIL, new AttributeValue(auth.getEmail().replace(" ", "")));
        map.put(IJsonNames.PASSWORD, new AttributeValue(auth.getPassword()));
        if(!AonStringUtils.isBlank(auth.getDocument())) map.put(IJsonNames.DOCUMENT, new AttributeValue(auth.getDocument().replace(" ", "")));
        if(!AonStringUtils.isBlank(auth.getPhone())) map.put(IJsonNames.PHONE, new AttributeValue(auth.getPhone().replace(" ", "")));
        if(!AonStringUtils.isBlank(auth.getName())) map.put(IJsonNames.NAME, new AttributeValue(auth.getName()));
        if(!AonStringUtils.isBlank(auth.getSurname())) map.put(IJsonNames.SURNAME, new AttributeValue(auth.getSurname()));
        map.put(IJsonNames.EXPIRED_DATE, new AttributeValue(AonDateUtils.dateTimeFormat(auth.getExpiredDate())));
        if(!auth.getDevices().isEmpty()) {
            AttributeValue devices = new AttributeValue();
            auth.getDevices().stream().forEach(r -> {
                Map<String, AttributeValue> device = new HashMap<>();
                device.put(IJsonNames.TYPE, new AttributeValue(r.getDeviceType().name()));
                device.put(IJsonNames.TOKEN, new AttributeValue(r.getDeviceToken()));
//              device.put(DATE, new AttributeValue(AonDateUtils.DATE_TIME_FORMAT));
                devices.setM(device);
            });
            map.put(IJsonNames.DEVICES, devices);
        }
        DYNAMODB.put(IJsonNames.AUTH, map);
        return auth;
    }
    
    public static void checkTable() {
        if(!DYNAMODB.exist(IJsonNames.AUTH)) {
            DYNAMODB.createTable(IJsonNames.AUTH, IJsonNames.UUID);
        }
    }
    
    public static void savePassword(String uuid, String password) {
        DYNAMODB.update(IJsonNames.AUTH, IJsonNames.UUID, uuid, IJsonNames.PASSWORD, password);
    }
    
    public static void saveAvatar(String uuid, byte[] data, MimeType mimetype) {
        try {
            File file = File.createTempFile(IJsonNames.AVATAR, mimetype.getExtension());
            AonFileUtils.writeByteArrayToFile(file, data);
            S3.upload(S3.AUTH_ATTACH_BUCKET, uuid, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void backup(String email) {   
        Auth auth = getAuthByEmail(email);
        if(auth.isEmpty()) {
            List<Auth> list = AON_SOLUTIONS.getAuthsWithDevices(f -> f.getEmailProperty().eq(email));

            for (Auth r : list) {
                if(r.getAttach().getData() != null) {
                    try {
                        File file = File.createTempFile(IJsonNames.AVATAR, r.getAttach().getMimetype().getExtension());
                        AonFileUtils.writeByteArrayToFile(file, r.getAttach().getData());
                        String fileId = S3.upload(S3.AUTH_ATTACH_BUCKET, r.getUuid(), file);
                        r.setAvatar(fileId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(auth.isEmpty()) {
                    auth = r;
                } else if(!auth.getUuid().equalsIgnoreCase(r.getUuid())){
                    auth.setDocument(compareDocument(auth.getDocument(), r.getDocument()));
                    auth.setPhone(comparePhone(auth.getPhone(), r.getPhone()));
                
                    List<User> users = AON_SOLUTIONS.getUsersByAuth(r);
                    for (User user : users) {
                        user.setAuth(auth.getUuid());
                        
                        AON.saveUser(user.getDomain(), "", user);
                    }
                }   
            }
        }
        
        save(auth);
    }
    
    
    public static void removeAuhtWithoutUser() {   
        List<Auth> list = AON_SOLUTIONS.getAuthsWithDevices(f -> f.getEmailProperty().isNotNull());

        for (Auth r : list) {
            List<User> users = AON_SOLUTIONS.getUsersByAuth(r);
            if(users == null || users.isEmpty()) {
                try (CloseableAONContext ctx = AONContext.getAONContext(r.getSchema())){
                    ctx.getDslContext().delete(com.esferalia.aon.jooq.tables.Auth.AUTH)
                    .where(com.esferalia.aon.jooq.tables.Auth.AUTH.ID.eq(r.getAuth()))
                    .execute();
                }
            }
        }
    }
    
    
    private static String compareDocument(String document1, String document2) {
        if(AonStringUtils.isBlank(document1) && AonStringUtils.isBlank(document2)) {
            return "";
        } else if(AonStringUtils.isBlank(document1) && AonStringUtils.isNotBlank(document2)) {
            return document2;
        } else if(AonStringUtils.isNotBlank(document1) && AonStringUtils.isBlank(document2) ) {
            return document1;
        } else if(AonDocumentUtil.isValid(document1) && !AonDocumentUtil.isValid(document2)) {
            return document1;
        } else if(!AonDocumentUtil.isValid(document1) && AonDocumentUtil.isValid(document2)) {
            return document2;
        } else return document1;
    }
    
    private static String comparePhone(String phone1, String phone2) {
        if(AonStringUtils.isBlank(phone1) && AonStringUtils.isBlank(phone2)) {
            return "";
        } else if(AonStringUtils.isBlank(phone1) && AonStringUtils.isNotBlank(phone2)) {
            return phone2;
        } else if(AonStringUtils.isNotBlank(phone1) && AonStringUtils.isBlank(phone2) ) {
            return phone1;
        } else return phone1;  
    }

    public static class AuthFiller implements Function< Map<String, AttributeValue>, Auth> {

        @Override
        public Auth apply(Map<String, AttributeValue> r) {
            return build(r);
        }
        
        public static Auth build(Map<String, AttributeValue> r) {
            return r.isEmpty() ? new Auth() : new Auth()
                    .setEmail(r.get(IJsonNames.EMAIL).getS())
                    .setDocument(r.get(IJsonNames.DOCUMENT).getS())
                    .setPassword(r.get(IJsonNames.PASSWORD).getS())
                    .setPhone(r.get(IJsonNames.PHONE).getS())
                    .setName(r.get(IJsonNames.NAME).getS())
                    .setSurname(r.get(IJsonNames.SURNAME).getS())
                    .setUuid(r.get(IJsonNames.UUID).getS())
                    .setDevices(r.get(IJsonNames.DEVICES).getL()
                            .stream().map(new AuthDeviceFiller())
                            .collect(Collectors.toCollection(LinkedList::new)))
                    .setExpiredDate(AonDateUtils.dateTimeParse(r.get(IJsonNames.EXPIRED_DATE).getS()));
        }
        
    }
    
    public static class AuthDeviceFiller implements Function<AttributeValue, AuthDevice> {

        @Override
        public AuthDevice apply(AttributeValue r) {
            return build(r);
        }
        
        public static AuthDevice build(AttributeValue r) {
            Map<String, AttributeValue> map = r.getM();
            return map.isEmpty() ? new AuthDevice() : new AuthDevice()
                    .setDeviceType(DeviceType.safeValueOf(map.get(IJsonNames.EMAIL).getS()))
                    .setDeviceToken(map.get(IJsonNames.TOKEN).getS());
        }
    }
    
}
