package net.aonsolutions.occam.dao;

import java.util.Arrays;

import org.jooq.Field;
import org.jooq.Record;

import com.esferalia.aon.watson.server.AonObjectUtils;

class FillerUtils {
	
	private static final Byte TRUE_BYTE = Byte.valueOf((byte) 1);

	static boolean checkField(Record r , Field<?> f) {
		return Arrays.stream(r.fields())
			.anyMatch(field -> AonObjectUtils.equals(field, f));
	}
	
	static <K> K getValue(Record r, Field<K> field) {
		return checkField(r, field)
			? r.getValue(field)
			: null;
	}

	static boolean getBoolean(Record r, Field<Byte> field) {
		return checkField(r, field) && AonObjectUtils.equals (r.getValue(field) , TRUE_BYTE );
	}
	
}
