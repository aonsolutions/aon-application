package net.aonsolutions.aon.tbai.responses;

import java.util.Date;
import java.util.Optional;

import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonStringUtils;

public class TbaiResponse {
	
	private String tbaiId;
	private Date receptionDate;
	private Integer status;
	private String description;
	private String descriptionEUS;
	private Integer validationCode;
	private String validationDescription;
	private String validationDescriptionEUS;
	private String sign; 
	private String responseStatus;
	private boolean ok; 
	private byte[] data;
	private JSONObject jsonInfo;
	
	public String getTbaiId() {
		return tbaiId;
	}
	
	public TbaiResponse setTbaiId(String tbaiId) {
		this.tbaiId = tbaiId;
		return this;
	}
	
	public Date getReceptionDate() {
		return receptionDate;
	}
	
	public TbaiResponse setReceptionDate(Date receptionDate) {
		this.receptionDate = receptionDate;
		return this;
	}

	public Optional<Integer> getStatus() {
		return Optional.ofNullable(status);
	}
	
	public TbaiResponse setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public String getDescription() {
		return description;
	}
	
	public TbaiResponse setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getDescriptionEUS() {
		return descriptionEUS;
	}
	
	public TbaiResponse setDescriptionEUS(String descriptionEUS) {
		this.descriptionEUS = descriptionEUS;
		return this;
	}

	public Integer getValidationCode() {
		return validationCode;
	}
	public TbaiResponse setValidationCode(Integer validationCode) {
		this.validationCode = validationCode;	
		return this;
	}

	public String getValidationDescription() {
		return validationDescription;
	}
	
	public TbaiResponse setValidationDescription(String validationDescription) {
		this.validationDescription = validationDescription;
		return this;
	}
	
	public String getValidationDescriptionEUS() { 
		return validationDescriptionEUS;
	}
	
	public TbaiResponse setValidationDescriptionEUS(String validationDescriptionEUS) {
		this.validationDescriptionEUS = validationDescriptionEUS;
		return this;
	}

	public boolean isOk() {
		return ok;
	}
	
	public TbaiResponse setOk(boolean ok) {
		this.ok = ok;
		return this;
	}
	
	public String getResponseStatus() {
		if(!AonStringUtils.isBlank(responseStatus))
			return responseStatus;
		else return isOk() ? "ok" : "error";
	}
	
	public TbaiResponse setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
		return this;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public TbaiResponse setData(byte[] data) {
		this.data = data;
		return this;
	}
	
	public String getSign() {
		return sign;
	}
	
	public TbaiResponse setSign(String sign) {
		this.sign = sign;
		return this;
	}
	
	public JSONObject getJsonInfo() {
		return jsonInfo;
	}
	
	public void setJsonInfo(JSONObject jsonInfo) {
		this.jsonInfo = jsonInfo;
	}
	
	public JSONObject toJSON() {
		return new JSONObject()
				.put("tbaiId", getTbaiId())
				.put("receptionDate", getReceptionDate())
				.put("status", getStatus())
				.put("description", getDescription())
				.put("descriptionEUS", getDescriptionEUS())
				.put("validationCode", getValidationCode())
				.put("validationDescription", getValidationDescription())
				.put("validationDescriptionEUS", getValidationDescriptionEUS());	
	}
}
