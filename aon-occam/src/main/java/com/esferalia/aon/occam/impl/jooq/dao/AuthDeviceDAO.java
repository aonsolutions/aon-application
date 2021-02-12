package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AuthDevice.AUTH_DEVICE;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jooq.Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.AuthDeviceFilter;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.AuthDevicePropertiesDAO;

public class AuthDeviceDAO {
	private static final AuthDevicePropertiesDAO AUTH_DEVICE_PROPERTIES = new AuthDevicePropertiesDAO();
//	 LocationFilter filter
	public static Stream<AuthDevice> getLocationStream(AONContext ctx, AuthDeviceFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.from(AUTH_DEVICE)
			.where(AUTH_DEVICE_PROPERTIES.getConditions(filter))
			.orderBy(AUTH_DEVICE.ID.desc())
			.fetch().stream().map(new AuthDeviceFiller());
	}
	
	public static AuthDevice saveLocation(AONContext ctx, AuthDevice ad) {
		return ad.getId()!=0
			? update(ctx, ad)
			: insert(ctx, ad);
	}
	private static AuthDevice insert(AONContext ctx, AuthDevice ad) {
		ctx.checkWrite();
//		Integer id = ctx.getDslContext()
//			.insertInto(
//					AUTH_DEVICE, 
//					AUTH_DEVICE.DOMAIN,
//			)
//			.values(
//					ad.getDomain().getId(), 
//			)
//			.returning(AUTH_DEVICE.ID).fetchOne().getValue(AUTH_DEVICE.ID);
//		return ad.setId(id);
		return null;
	}
	
	private static AuthDevice update(AONContext ctx, AuthDevice ad) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(AUTH_DEVICE)
			.set(AUTH_DEVICE.DEVICE_TOKEN, ad.getDeviceToken())
			.where(AUTH_DEVICE.ID.eq(ad.getId()))
			.execute();		
		return ad;
	}
	
	
	public static void deleteLocation(AONContext ctx, AuthDevice ad) {
		ctx.checkWrite();
		ctx.getDslContext().delete(AUTH_DEVICE).where(AUTH_DEVICE.ID.eq(ad.getId())).execute();	
	}
	
	public static AuthDevice getAuthDevice(AONContext ctx, AuthDeviceFilter filter) {
		ctx.checkRead();
		return  ctx.getDslContext()
				.select()
				.from(AUTH_DEVICE)
				.where(AUTH_DEVICE_PROPERTIES.getConditions(filter))
				.stream()
				.map( new AuthDeviceFiller() )
				.findFirst()
				.orElse(null);
	}
	
	
	public static class AuthDeviceFiller  implements Function<Record, AuthDevice> {
		@Override
		public AuthDevice apply(Record record) {
			return new AuthDevice()
					.setId(record.getValue(AUTH_DEVICE.ID));
		}
	}
}
