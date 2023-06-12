package net.aonsolutions.infovox.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import net.aonsolutions.infovox.OCRInvofox;
import net.aonsolutions.infovox.json.OCRResponseJSON;
import net.aonsolutions.infovox.model.OCRError;
import net.aonsolutions.infovox.model.OCRResponse;

class InfovoxRESTTestCase {
	
//	@Test
//	void validDocument() throws IOException, InterruptedException, URISyntaxException {
//		String documentId = "648088d6c632f4000891fa82";
//		String url = "https://prod.kinequo.com/backends/midas/documents/648088d6c632f4000891fa82";
//		System.out.println( "Attempt to " + url);
//		HttpResponse<String> response = call( url);
//		assertNotNull(response);
//		
//		assertEquals( 200, response.statusCode());
//		String resp = response.body();
//		System.out.println( "resp -->" + resp );
//		JSONObject documentJson = new JSONObject(resp);
//		assertNotNull(documentJson);
//		System.out.println( documentJson.toString(1) );
//	}
	
	@Test
	void invalidDocument() throws IOException, InterruptedException, URISyntaxException {
		String documentId = "DOCUMENTO_NO_VALIDO";
		OCRResponse response = OCRInvofox.getDocument(documentId);
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 400, response.getHttpCode().get());
		assertTrue(response.getError().isPresent());
		OCRError ocrError = response.getError().get(); 
		assertTrue(ocrError.getCode().isPresent());
		assertEquals( "ERR_WRONG_PARAM", ocrError.getCode().get());
	}
	
	@Test
	void validDocument() throws IOException, InterruptedException, URISyntaxException {
		String documentId = "648088d6c632f4000891fa82";
		OCRResponse response = OCRInvofox.getDocument(documentId);
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		// System.out.println( OCRResponseJSON.to(response).toString(1) );
	}
	
	@Test
	void validDocuments() throws IOException, InterruptedException, URISyntaxException {
		OCRResponse response = OCRInvofox.getDocuments();
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		assertTrue(response.getResult().isPresent());
		assertNotEquals(0 , response.getResult().get().size());
		System.out.println( OCRResponseJSON.to(response).toString(1) );
		
		System.out.println( "Documents ..: " + response.getResult().get().size() );
	}
}