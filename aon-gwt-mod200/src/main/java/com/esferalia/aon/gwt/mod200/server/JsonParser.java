package com.esferalia.aon.gwt.mod200.server;

import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.mod200.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JsonParser {
	
	private JsonParser() {
		
	}
	
	public static AEATParams parseAEATParams(String aeatParams) throws ParseException {
		AEATParams params = new AEATParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams =  (JSONObject) parser.parse(aeatParams);

		String domainName = (String) jsonParams.get(IRequestParamsNames.DOMAIN_NAME);
		params.setDomainName(domainName);
		
		Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN_ID);
		params.setDomainId( domain.intValue());
		
		String user = (String) jsonParams.get(IRequestParamsNames.USER);
		params.setUser(user);
		
		Long mod = (Long) jsonParams.get(IRequestParamsNames.MOD);
		params.setMod(mod==null?null:mod.intValue());

		Long certificateId = (Long) jsonParams.get(IRequestParamsNames.CERTIFICATE_ID);
		params.setCertificateId(certificateId==null?null:certificateId.intValue());

		String pass = (String) jsonParams.get(IRequestParamsNames.PASS);
		if (AonStringUtils.isNotBlank(pass)) {
			params.setPass(pass);			
		}
		
		String name = (String) jsonParams.get(IRequestParamsNames.NAME);
		if (AonStringUtils.isNotBlank(name)) {
			params.setName(name);			
		}

		String document = (String) jsonParams.get(IRequestParamsNames.DOCUMENT);
		if (AonStringUtils.isNotBlank(document)) {
			params.setDocument(document);			
		}

		String nrc = (String) jsonParams.get(IRequestParamsNames.NRC);
		if (AonStringUtils.isNotBlank(nrc)) {
			params.setNrc(nrc);			
		}
		
		Long test = (Long) jsonParams.get(IRequestParamsNames.TEST);
		if (test != null) {
			params.setTest(test==1);
		}
		return params;
	}
	
}
