package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AuthDevice.AUTH_DEVICE;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.AuthDeviceFilter;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.model.security.DeviceType;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.AuthDevicePropertiesDAO;

public class AuthDeviceDAO {
	private static final AuthDevicePropertiesDAO AUTH_DEVICE_PROPERTIES = new AuthDevicePropertiesDAO();

	public static AuthDevice saveAuthDevice(AONContext ctx, AuthDevice ad) {
		return ad.getId()!=0
			? update(ctx, ad)
			: insert(ctx, ad);
	}
	private static AuthDevice insert(AONContext ctx, AuthDevice ad) {
		ad.toJSON();
		ctx.checkWrite();
		String tokenFCM = ad.getDeviceToken();
		AuthDevice authDeviceExist = getAuthDevice(ctx, f-> f.getDeviceTokenProperty().eq(tokenFCM));
		if(authDeviceExist!=null){
			ad = update(ctx, authDeviceExist);
		} else {
			Integer id = ctx.getDslContext()
					.insertInto(
							AUTH_DEVICE, 
							AUTH_DEVICE.AUTH,
							AUTH_DEVICE.DEVICE_TYPE,
							AUTH_DEVICE.DEVICE_TOKEN
					)
					.values(
							ad.getAuth(),
							ad.getDeviceType().value(),
							ad.getDeviceToken()
					)
					.returning(AUTH_DEVICE.ID).fetchOne().getValue(AUTH_DEVICE.ID);
			ad.setId(id);
		}

		return ad;
	}
	
	private static AuthDevice update(AONContext ctx, AuthDevice ad) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(AUTH_DEVICE)
			.set(AUTH_DEVICE.DEVICE_TYPE, ad.getDeviceType().value())
			.set(AUTH_DEVICE.DEVICE_TOKEN, ad.getDeviceToken())
			.set(AUTH_DEVICE.LAST_DATE, Timestamp.from(Instant.now()))
			.where(AUTH_DEVICE.ID.eq(ad.getId()))
			.execute();		
		return ad;
	}
	
	
	public static void deleteAuthDevice(AONContext ctx, AuthDeviceFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext().delete(AUTH_DEVICE).where(AUTH_DEVICE_PROPERTIES.getConditions(filter)).execute();	
	}
	
	public static AuthDevice getAuthDevice(AONContext ctx, AuthDeviceFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(AUTH_DEVICE)
				.where(AUTH_DEVICE_PROPERTIES.getConditions(filter))
				.stream()
				.map( new AuthDeviceFiller() )
				.findFirst()
				.orElse(null);
	}
	
	public static LinkedList<AuthDevice> getAuthDevices(AONContext ctx, AuthDeviceFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(AUTH_DEVICE)
				.where(AUTH_DEVICE_PROPERTIES.getConditions(filter))
				.stream()
				.map( new AuthDeviceFiller() )
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	
	public static class AuthDeviceFiller implements Function<Record, AuthDevice> {
		@Override
		public AuthDevice apply(Record record) {
			return new AuthDevice()
					.setId(record.getValue(AUTH_DEVICE.ID))
					.setAuth(record.getValue(AUTH_DEVICE.AUTH))
					.setDeviceType(DeviceType.safeValueOf(record.getValue(AUTH_DEVICE.DEVICE_TYPE)))
					.setDeviceToken(record.getValue(AUTH_DEVICE.DEVICE_TOKEN));
		}
	}
}
