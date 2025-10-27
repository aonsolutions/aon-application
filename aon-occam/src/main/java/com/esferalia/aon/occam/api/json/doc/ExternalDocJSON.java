package com.esferalia.aon.occam.api.json.doc;

import java.util.Optional;
import java.util.function.Supplier;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.doc.ExternalDoc;
import com.esferalia.aon.occam.api.model.doc.ExternalStorage;

public class ExternalDocJSON {

	private ExternalDocJSON() {
	}
	
	// ***********************************************************
	// ************************************************ [FROM] ***
	// ***********************************************************
	
	public static <T extends Enum<?>> Optional<ExternalDoc<T>> from(JSONObject json, Supplier<ExternalDoc<T>> docSupplier) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		ExternalDoc<T> doc = docSupplier.get();
		DocJSON.from(json, () -> doc);
		return Optional.of(doc
			.setAonTable(JsonUtils.getString(json, IJsonNames.AON_TABLE))
			.setExternalStorage(ExternalStorage.safeValueOf(JsonUtils.getString(json, IJsonNames.EXTERNAL_STORAGE)))
			.setS3Bucket(JsonUtils.getString(json, IJsonNames.S3_BUCKET))
			.setS3Key(JsonUtils.getString(json, IJsonNames.S3_KEY))
			.setDriveId(JsonUtils.getString(json, IJsonNames.DRIVE_ID))
			.setAonId(JsonUtils.getInteger(json, IJsonNames.AON_ID))
			.setUrl(JsonUtils.getString(json, IJsonNames.URL))
		);
	}
	
	// ***********************************************************
	// ************************************************** [TO] ***
	// ***********************************************************
	
	public static <T extends Enum<?>> Optional<JSONObject> to(ExternalDoc<T> doc, Supplier<JSONObject> jsonSupplier) {
		if (doc == null) return Optional.empty();
		return DocJSON.to(doc, jsonSupplier )
			.map(json -> json
				.put(IJsonNames.AON_TABLE, doc.getAonTable() )
				.put(IJsonNames.EXTERNAL_STORAGE, ExternalStorage.name( doc.getExternalStorage()) )
				.put(IJsonNames.S3_BUCKET, doc.getS3Bucket())
				.put(IJsonNames.S3_KEY, doc.getS3Key())
				.put(IJsonNames.DRIVE_ID, doc.getDriveId())
				.put(IJsonNames.AON_ID, doc.getAonId())
				.put(IJsonNames.URL, doc.getUrl())
		);
	}

}
