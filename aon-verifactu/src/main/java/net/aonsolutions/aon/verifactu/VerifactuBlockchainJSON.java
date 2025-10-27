package net.aonsolutions.aon.verifactu;

import java.util.Optional;
import java.util.function.Supplier;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.watson.util.AonStringUtils;

public class VerifactuBlockchainJSON {
	
	private VerifactuBlockchainJSON() {
		
	}

	public static Optional<JSONObject> toJSON(VerifactuBlockchain block) {
		if (block == null) return Optional.empty();
		return Optional.of( new JSONObject()
			.put(IJsonNames.DOCUMENT, block.getDocument())
			.put(IJsonNames.REFERENCE, block.getReference())
			.put(IJsonNames.DATE, block.getDate())
			.put(IJsonNames.HUELLA, block.getHuella()));
	}
	
	public static Optional<VerifactuBlockchain> fromJSON(String str) {
		if (AonStringUtils.isBlank(str)) return Optional.empty();
		return fromJSON(new JSONObject(str));		
	}
	
	public static Optional<VerifactuBlockchain> fromJSON(JSONObject json ) {
		return fromJSON(json, VerifactuBlockchain::new );
	}
	
	public static Optional<VerifactuBlockchain> fromJSON(JSONObject json, Supplier<VerifactuBlockchain> sup) {
		if ( JsonUtils.isEmpty(json) ) return Optional.empty();
		return Optional.of( sup.get()
			.setReference(JsonUtils.getString(json, IJsonNames.REFERENCE))
			.setDocument(JsonUtils.getString(json, IJsonNames.DOCUMENT))
			.setDate(JsonUtils.getString(json, IJsonNames.DATE))
			.setHuella(JsonUtils.getString(json, IJsonNames.HUELLA)));		
	}
	
}
