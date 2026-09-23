package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Fbatch.FBATCH;

import org.jooq.Record2;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.type.FBatchType;
import com.esferalia.aon.watson.error.AonCoreException;

/** Punto unico de generacion de ficheros SEPA: enruta por direccion de la remesa. */
public class FBatchSepaDAO {

	public static Integer createSepaFile(CloseableAONContext ctx, Integer fbatchId) throws Exception {
		ctx.checkWrite();
		if (fbatchId == null) throw new AonCoreException("ID de remesa vacio");

		Record2<Byte, Byte> rec = ctx.getDslContext()
			.select(FBATCH.PAYMENT, FBATCH.TYPE)
			.from(FBATCH)
			.where(FBATCH.ID.eq(fbatchId))
			.and(FBATCH.DOMAIN.eq(ctx.getDomainId()))
			.fetchOne();

		if (rec == null)
			throw new AonCoreException("No existe la remesa " + fbatchId);

		Byte payment = rec.value1();
		Byte type    = rec.value2();

		if (!FBatchType.generatesFile(type))
			throw new AonCoreException("La remesa " + fbatchId + " es de tipo '"
					+ FBatchType.of(payment, type).getFileTypeDescription(type)
					+ "' y no genera fichero");

		return payment != null && payment == (byte) 0
				? FBatchChargeSepaDAO.createSepaFile(ctx, fbatchId)
				: SettleSalariesDAO.createSepaFile(ctx, fbatchId);
	}
}