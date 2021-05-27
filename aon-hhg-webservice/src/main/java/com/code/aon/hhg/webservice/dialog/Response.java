package com.code.aon.hhg.webservice.dialog;

import org.json.JSONException;
import org.json.JSONObject;

public class Response {
	
	static final String error = "{\"result\":{\"type\":\"error\",\"payload\":{\"code\":1,\"message\":\"ERROR CHABAL!\"}}} ";
	static final String ok = "{\"result\":{\"type\":\"ok\",\"payload\":{\"nonce\":\"213456789347215436743\"}}} ";
	
	public Response(JSONObject json) {
		try {
			System.out.println(json.toString());
			setResult(new Result(json.getJSONObject("result")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	Result result;
	
	public Result getResult() {
		return result;
	}

	public Response setResult(Result result) {
		this.result = result;
		return this;
	}

	public class Result {
		String type;
		Payload payload;
		
		public Result(JSONObject json) {
			try {
				setType(json.get("type").toString());
				if(getType().equals("error"))
					setPayload(new Payload(json.getJSONObject("payload"), getType().equals("error")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		public String getType() {
			return type;
		}
		
		public Result setType(String type) {
			this.type = type;
			return this;
		}
		
		public Payload getPayload() {
			return payload;
		}
		
		public Result setPayload(Payload payload) {
			this.payload = payload;
			return this;
		}
	}
	
	public class Payload {
		String nonce;
		Integer code;
		String message;
		Respons respons;

		public Payload(JSONObject json, Boolean isError) {
			try {
				if(isError){
					setCode((Integer) json.get("code"));
					setMessage(json.get("message").toString());
				}else{
					setNonce(json.get("nonce").toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		public String getNonce() {
			return nonce;
		}

		public Payload setNonce(String nonce) {
			this.nonce = nonce;
			return this;
		}

		public Integer getCode() {
			return code;
		}

		public Payload setCode(Integer code) {
			this.code = code;
			return this;
		}

		public String getMessage() {
			return message;
		}

		public Payload setMessage(String message) {
			this.message = message;
			return this;
		}

		public Respons getRespons() {
			return respons;
		}

		public Payload setRespons(Respons respons) {
			this.respons = respons;
			return this;
		}
	}
	
	public class Respons {
		
	}
	public static void main(String[] args) {
		/*
			JSONObject jsonError = new JSONObject(error);
			JSONObject jsonOk = new JSONObject(ok);
			System.out.println(jsonError.toString());
			
			Response responseError = new Response(jsonError);
			Response responseOk = new Response(jsonOk);
			
			System.out.println("***** ERROR *****");
			System.out.println(responseError.getResult().getType());
			System.out.println(responseError.getResult().getPayload().getCode());
			System.out.println(responseError.getResult().getPayload().getMessage());
			
			System.out.println("***** OK *****");
			System.out.println(responseOk.getResult().getType());
			System.out.println(responseOk.getResult().getPayload().getNonce());
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
	}
}
