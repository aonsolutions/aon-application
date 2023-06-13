package net.aonsolutions.infovox.json;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.infovox.model.OCRConfidence;
import net.aonsolutions.infovox.model.OCRDocument;
import net.aonsolutions.infovox.model.OCRSeverity;
import net.aonsolutions.infovox.model.OCRType;

public class OCRDocumentJSON {
	
	private OCRDocumentJSON() {
	}
	
	public static List<OCRDocument> from(JSONArray array) {
		if (array == null || array.isEmpty()) return Collections.emptyList();
		return OCRJSONUtils.stream(array)
			.map(OCRDocumentJSON::from)
			.toList();		
	}
	
	public static OCRDocument from(JSONObject json) {
		if (json == null) return null; 
		return new OCRDocument()
			.setId(OCRJSONUtils.getString(json, OCRNames.ID))
			.setAccount(OCRJSONUtils.getString(json, OCRNames.ACCOUNT))
			.setEnvironment(OCRJSONUtils.getString(json, OCRNames.ENVIRONMENT))
			.setCompany(OCRJSONUtils.getString(json, OCRNames.COMPANY))
			.setCreator(OCRJSONUtils.getString(json, OCRNames.CREATOR))
			.setClientData(OCRClientJSON.from(OCRJSONUtils.getArray(json, OCRNames.CLIENT_DATA)))
			.setType( OCRType.safeValueOf(OCRJSONUtils.getString(json, OCRNames.TYPE)).orElse(null) )
			.setName(OCRJSONUtils.getString(json, OCRNames.NAME))
			.setCreation(OCRJSONUtils.getString(json, OCRNames.CREATION))
			.setImages(OCRJSONUtils.getStringArray(json, OCRNames.IMAGES))
			.setMimeType(OCRJSONUtils.getString(json, OCRNames.MIME_TYPE))
			.setPublicState( OCRSeverity.safeValueOf(OCRJSONUtils.getString(json, OCRNames.PUBLIC_STATE)).orElse(null) )
			.setConfidence( OCRConfidence.safeValueOf(OCRJSONUtils.getString(json, OCRNames.CONFIDENCE)).orElse(null) )
			.setValidationInfo(OCRValidationInfoJSON.from(OCRJSONUtils.getObject(json, OCRNames.VALIDATION_INFO)))
			.setApprovedBy(OCRJSONUtils.getStringArray(json, OCRNames.APPROVED_BY))
			.setRequiredSigns(OCRJSONUtils.getStringArray(json, OCRNames.REQUIRED_SIGNS))
			.setExports(OCRJSONUtils.getStringArray(json, OCRNames.EXPORTS))
			.setApprovalInfo(OCRApprovalInfoJSON.from(OCRJSONUtils.getObject(json, OCRNames.APPROVAL_INFO)))
			.setImportData(OCRImportDataJSON.from(OCRJSONUtils.getObject(json, OCRNames.IMPORT)))
			.setGeometry(OCRGeometryJSON.from(OCRJSONUtils.getObject(json, OCRNames.GEOMETRY)))
			.setData(OCRInvoiceJSON.from(OCRJSONUtils.getObject(json, OCRNames.DATA)))
		;
	}
	
	public static JSONArray to(List<OCRDocument> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRDocument> stream) {
		return stream
			.map(OCRDocumentJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRDocument document) {
		if (document == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.ID, document.getId().orElse(null))
			.putOpt(OCRNames.ACCOUNT, document.getAccount().orElse(null))
			.putOpt(OCRNames.ENVIRONMENT, document.getEnvironment().orElse(null))
			.putOpt(OCRNames.COMPANY, document.getCompany().orElse(null))
			.putOpt(OCRNames.CREATOR, document.getCreator().orElse(null))
			.putOpt(OCRNames.CLIENT_DATA, OCRClientJSON.to(document.getClientData().orElse(null)))
			.putOpt(OCRNames.TYPE, document.getType().orElse(null))
			.putOpt(OCRNames.NAME, document.getName().orElse(null))
			.putOpt(OCRNames.CREATION, document.getCreation().orElse(null))
			.putOpt(OCRNames.IMAGES, document.getImages().orElse(null))
			.putOpt(OCRNames.MIME_TYPE, document.getMimeType().orElse(null))
			.putOpt(OCRNames.PUBLIC_STATE, document.getPublicState().orElse(null))
			.putOpt(OCRNames.CONFIDENCE, document.getConfidence().orElse(null))
			.putOpt(OCRNames.VALIDATION_INFO, OCRValidationInfoJSON.to(document.getValidationInfo().orElse(null)))
			.putOpt(OCRNames.APPROVED_BY, document.getApprovedBy().orElse(null))
			.putOpt(OCRNames.REQUIRED_SIGNS, document.getRequiredSigns().orElse(null))
			.putOpt(OCRNames.EXPORTS, document.getExports().orElse(null))
			.putOpt(OCRNames.APPROVAL_INFO, OCRApprovalInfoJSON.to(document.getApprovalInfo().orElse(null)))
			.putOpt(OCRNames.IMPORT, OCRImportDataJSON.to(document.getImportData().orElse(null)))
			.putOpt(OCRNames.GEOMETRY, OCRGeometryJSON.to(document.getGeometry().orElse(null)))
			.putOpt(OCRNames.DATA, OCRInvoiceJSON.to(document.getData().orElse(null)))
		;
	}

	public static List<OCRDocument> from(Object rawObject) {
		if (rawObject instanceof JSONArray array) {
			return from(array);
		}
		if (rawObject instanceof JSONObject obj) {
			LinkedList<OCRDocument> list = new LinkedList<>();
			list.add(from(obj));
			return list;
		}
		return Collections.emptyList();
	}
}
