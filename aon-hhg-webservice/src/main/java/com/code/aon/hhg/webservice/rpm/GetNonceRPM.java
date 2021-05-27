package com.code.aon.hhg.webservice.rpm;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.hhg.webservice.dialog.GetNonce;
import com.code.aon.hhg.webservice.dialog.HHGPost;
import com.code.aon.hhg.webservice.dialog.Response;

public class GetNonceRPM {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetNonceRPM.class.getName());
	
	public static void main(String[] args) {
		try {
			JSONObject json = HHGPost.post2(GetNonce.URL, null);
			Response response = new Response(json);
			LOGGER.info("POST " + GetNonce.URL);
			View.response(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
