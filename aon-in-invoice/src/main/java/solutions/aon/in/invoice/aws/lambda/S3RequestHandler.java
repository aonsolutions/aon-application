package solutions.aon.in.invoice.aws.lambda;

import static solutions.aon.aws.s3.S3UploadEventObject.getS3UploadEventObjects;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.AonInvofox;
import net.aonsolutions.aon.api.AonSecurity;
import net.aonsolutions.aon.in.pdf.maker.exception.CanNotCreatePdfException;
import net.aonsolutions.aon.in.pdf.maker.image.ImageToPdf;
import net.aonsolutions.aon.sign.PdfSigner;
import solutions.aon.aws.s3.S3;
import solutions.aon.aws.s3.S3EventObject;
import solutions.aon.aws.s3.S3UploadEventObject;
import solutions.aon.aws.secrets.SECRETS;
import solutions.aon.in.invoice.aws.lambda.Invofox.DocumentType;

public class S3RequestHandler implements RequestHandler<Object, String> {

	static final String ID = "id";
	static final String _ID = "_id";
	static final String LOAD_S3 = "loadS3";
//	static final String LOAD_TASK = "loadTask";
//	static final String LOAD_BATCH = "loadBatch";
	static final String RAWDOC = "rawdoc";
	static final int MAX_SLEEP_TIME = 2000;
//	static final String LOAD_BATCH_WAIT_ID = "loadBatchId";

	static final String WORKGROUP = "FACTURAS";
	static final String TITLE = "Se han subido documentos de facturas desde '%s'.";
	static final String DESCRIPTION = "La empresa '%s' ha subido facturas para su procesamiento y validación.";

	@Override
	public String handleRequest(Object input, Context context) {
		List<S3UploadEventObject> s3UploadEventObjects = getS3UploadEventObjects(input);
		s3UploadEventObjects.forEach(S3RequestHandler::handleS3EventObject);

		return "That's all folks :-)";
	}

//    static JSONObject newLoadBatchTask(String loadBatchId) {
//    	JSONObject loadBatchTaskJSON = new JSONObject();
//    	loadBatchTaskJSON.put(LOAD_BATCH, Collections.singletonMap(_ID, loadBatchId));
//    	return loadBatchTaskJSON;
//    }
//
//    static String getLoadBatchId(JSONObject loadBatchTaskJSON ) {
//    	JSONObject loadBatchJSON = loadBatchTaskJSON.getJSONObject(LOAD_BATCH);
//    	return loadBatchJSON.getString(_ID);
//    }
//
//    static JSONObject newLoadBatchTask(JSONObject loadBatchJSON, JSONObject taskJSON) {
//    	JSONObject loadBatchTaskJSON = new JSONObject();
//    	loadBatchTaskJSON.put(LOAD_BATCH, loadBatchJSON);
//    	loadBatchTaskJSON.put(LOAD_TASK, taskJSON );
//    	return loadBatchTaskJSON;
//    }

	static void handleS3EventObject(S3UploadEventObject s3UploadEventObject) {
		try {
			s3UploadEventObject
					.setDocument(s3UploadEventObject.getDocument() != null ? s3UploadEventObject.getDocument().trim()
							: s3UploadEventObject.getDocument());
			if (isImage(s3UploadEventObject)) {
				try {
					byte[] image = download(s3UploadEventObject);
					byte[] pdf = imageToPdf(image);

					// sign PDF.
					try {
						Certificate certificate = getAonCert();
						pdf = PdfSigner.getInstance().sign(certificate, pdf);
						s3UploadEventObject.setSigned(true);
					} catch (Exception e) {
						e.printStackTrace();
					}

					// save PDF IN S3.
					String extension = getExtension(s3UploadEventObject.getKey());
					if (extension != null) {
						s3UploadEventObject.setKey(s3UploadEventObject.getKey().replace(extension, ".pdf"),
								s3UploadEventObject);
						s3UploadEventObject.setFileName(s3UploadEventObject.getFileName().replace(extension, ".pdf"));
					}
					solutions.aon.aws.s3.S3.getInstance().upload(s3UploadEventObject.getBucket(),
							s3UploadEventObject.getKey(), pdf);
					s3UploadEventObject.setContentType("application/pdf", s3UploadEventObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			DomainUserRoles dur = getDomainUserRoles(s3UploadEventObject);
			if (dur.hasInvofox()) {
				Integer rawdocId = null;
				try {
					rawdocId = createRawdoc(s3UploadEventObject, RawdocStatus.PROCESSING);
				} catch (Exception e) {
					e.printStackTrace();
				}
				InvofoxConfiguration invofoxConfiguration = getInvofoxConfiguration(s3UploadEventObject);
				String companyId = getCompanyId(invofoxConfiguration, s3UploadEventObject);
				String downloadURL = getDowloadURL(s3UploadEventObject);
//				JSONObject loadBatchTaskJSON = getLoadBatchTask(invofoxConfiguration, companyId, s3UploadEventObject);
//				String loadBatchId = getLoadBatchId(loadBatchTaskJSON);
//
//				JSONObject loadTask = JsonUtils.getJSONObject(loadBatchTaskJSON, LOAD_TASK);
//				Integer loadTaskId = JsonUtils.getInteger(loadTask, "id");

				JSONObject clientData = new JSONObject()
						.put(LOAD_S3,
								new JSONObject().put("key", s3UploadEventObject.getKey())
										.put("user", s3UploadEventObject.getUser())
										.put("domain", s3UploadEventObject.getDomain())
										.put("bucket", s3UploadEventObject.getBucket()))
//						.put(LOAD_TASK, new JSONObject().put("id", loadTaskId))
						.put(RAWDOC, rawdocId);

				loadDocuments(invofoxConfiguration, DocumentType.INVOICE, companyId, clientData,
						downloadURL);
			} else {
				createRawdoc(s3UploadEventObject, RawdocStatus.PROCESSED);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchCompanyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static DomainUserRoles getDomainUserRoles(S3UploadEventObject s3Object)
			throws URISyntaxException, IOException, InterruptedException {
		return AonSecurity.getDomainUserRoles(s3Object.getDomain(), s3Object.getUser());
	}

	static InvofoxConfiguration getInvofoxConfiguration(S3UploadEventObject s3Object)
			throws URISyntaxException, IOException, InterruptedException {
		return AonInvofox.getInvofoxConfiguration(s3Object.getDomain(), s3Object.getUser());
	}

	static boolean isImage(S3EventObject s3UploadEventObject) {
		System.out.println(s3UploadEventObject.getBucket());
		System.out.println(s3UploadEventObject.getKey());
		String contentType = solutions.aon.aws.s3.S3.getInstance().getContentType(s3UploadEventObject.getBucket(),
				s3UploadEventObject.getKey());
		System.out.println(contentType);
		return contentType != null && contentType.toLowerCase().contains("image");
	}

	static byte[] imageToPdf(byte[] image) throws IOException, CanNotCreatePdfException {
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("ref_homologation", "RGE405069592024");
		metadata.put("software_name", "Aon Solutions");
		metadata.put("software_version", "9.23");
		metadata.put("timestamp", AonDateUtils.format(new Date(), "hh:mm dd/MM/yyyy"));

		try (ImageToPdf imageToPdf = new ImageToPdf(image, metadata)) {
			return imageToPdf.toByteArray();
		}
	}

	static String getDowloadURL(S3EventObject s3Object) {
		return S3.getInstance().getURL(s3Object.getBucket(), s3Object.getKey()).toExternalForm();
	}

	static byte[] download(S3EventObject s3Object) throws IOException {
		return S3.getInstance().download(s3Object.getBucket(), s3Object.getKey());
	}

	static String getLoadBatchKey(S3UploadEventObject s3Object) {
		return s3Object.getPrefix() + "/" + s3Object.getDomain() + "/" + s3Object.getDocument() + "/"
				+ s3Object.getUser() + "/" + s3Object.getJob() + "/" + "loadbatch";
	}

	public static String getCompanyId(InvofoxConfiguration invofoxConfiguration,
			S3UploadEventObject s3UploadEventObject) throws URISyntaxException, IOException, InterruptedException {
		return getCompanyId(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl(), s3UploadEventObject);
	}

	public static String getCompanyId(String invofoxApiKey, String invofoxApiUrl,
			S3UploadEventObject s3UploadEventObject) throws URISyntaxException, IOException, InterruptedException {
		try {
			return Invofox.getCompanyId(invofoxApiKey, invofoxApiUrl, s3UploadEventObject.getDocument());
		} catch (NoSuchCompanyException ne) {
			try {
				String companyName = getCompanyName(s3UploadEventObject);
				return Invofox.newCompany(invofoxApiKey, invofoxApiUrl, s3UploadEventObject.getDocument(), companyName,
						Collections.emptyMap());
			} catch (AlreadyCompanyExistsException ae) {
				return Invofox.getCompanyId(invofoxApiKey, invofoxApiUrl, s3UploadEventObject.getDocument());
			}
		}
	}

	/**
	 * @param s3UploadEventObject
	 * @param companyName
	 * @return
	 */
	static String getCompanyName(S3UploadEventObject s3UploadEventObject) {
		try {
			return S3Invoice.getCompanyName(s3UploadEventObject.getBucket(), s3UploadEventObject.getKey());
		} catch (Exception e) {
			e.printStackTrace();
			return s3UploadEventObject.getDomain();
		}
	}

//	public static JSONObject getLoadBatchTask(InvofoxConfiguration invofoxConfiguration, String companyId,
//			S3UploadEventObject s3UploadEventObject) throws URISyntaxException, IOException, InterruptedException {
//		return getLoadBatchTask(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl(), companyId,
//				s3UploadEventObject);
//	}

//	public static JSONObject getLoadBatchTask(String invofoxApiKey, String invofoxApiUrl, String companyId,
//			S3UploadEventObject s3UploadEventObject) throws URISyntaxException, IOException, InterruptedException {
//		JSONObject loadBatchTaskJSON = newLoadBatchTask(LOAD_BATCH_WAIT_ID);
//		String loadBatchKey = getLoadBatchKey(s3UploadEventObject);
//
//		try {
//			String loadBatchId = LOAD_BATCH_WAIT_ID;
//			while (LOAD_BATCH_WAIT_ID.equals(loadBatchId)) {
//				Thread.sleep(Math.min(s3UploadEventObject.getOrder() * 1000L, MAX_SLEEP_TIME));
//				loadBatchTaskJSON = S3Invoice.getLoadBatchTask(s3UploadEventObject.getBucket(), loadBatchKey);
//				loadBatchId = getLoadBatchId(loadBatchTaskJSON);
//			}
//		} catch (NoSuchLoadBatchException e) {
//			// Write semaphore, if present other lambdas must wait.
//			S3Invoice.setLoadBatchTask(s3UploadEventObject.getBucket(), loadBatchKey, loadBatchTaskJSON);
//
//			JSONObject loadBatchJSON = Invofox.newLoadBatch(invofoxApiKey, invofoxApiUrl, companyId);
//			// new issue for this batch, and don't wait for it
//			String companyName = getCompanyName(s3UploadEventObject);
//			JSONObject taskJSON;
//
//			try {
//				taskJSON = new JSONObject().put(IJsonNames.TITLE, companyName).put(IJsonNames.DESCRIPTION, companyName)
//						.put(IJsonNames.WORKGROUP, new JSONObject().put(IJsonNames.DESCRIPTION, WORKGROUP));
//			} catch (Exception t) {
//				taskJSON = new JSONObject().put("id", Integer.MAX_VALUE);
//			}
//
//			loadBatchTaskJSON = newLoadBatchTask(loadBatchJSON, taskJSON);
//			S3Invoice.setLoadBatchTask(s3UploadEventObject.getBucket(), loadBatchKey, loadBatchTaskJSON);
//
//		}
//		return loadBatchTaskJSON;
//	}

	protected static JSONObject loadDocuments(InvofoxConfiguration invofoxConfiguration, DocumentType type,
			String companyId, JSONObject clientData, String... downloadURLs)
			throws URISyntaxException, IOException, InterruptedException {
		return Invofox.loadDocuments(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl(),
				DocumentType.INVOICE, companyId, clientData, invofoxConfiguration.isBeta(), downloadURLs);
	}    

	private static Integer createRawdoc(S3UploadEventObject s3UploadEventObject, RawdocStatus rawdocStatus)
			throws URISyntaxException, IOException, InterruptedException {
		JSONObject json = new JSONObject();
		JSONObject file = new JSONObject();
		file.put("s3Bucket", s3UploadEventObject.getBucket());
		file.put("s3Key", s3UploadEventObject.getKey());
		file.put("url", getDowloadURL(s3UploadEventObject));
		file.put("path", getDowloadURL(s3UploadEventObject));

		String contentType = s3UploadEventObject.getContentType() != null ? s3UploadEventObject.getContentType()
				: solutions.aon.aws.s3.S3.getInstance().getContentType(s3UploadEventObject.getBucket(),
						s3UploadEventObject.getKey());
		file.put("content_type", contentType);
		json.put(IJsonNames.FILE, file);
		json.put(IJsonNames.STATUS, rawdocStatus.getTediName());
		json.put("camera", s3UploadEventObject.isCamera());
		json.put("signed", s3UploadEventObject.isSigned());
		if (AonStringUtils.isNotBlank(s3UploadEventObject.getActivity())
				&& AonNumberUtils.isNumber(s3UploadEventObject.getActivity())) {
			JSONObject activityJSON = new JSONObject();
			activityJSON.put("id", s3UploadEventObject.getActivity());
			json.put("activity", activityJSON);
		}
		json.put("bidoq", s3UploadEventObject.isBidoq());
		JSONObject resp = AonInvofox.createRawdoc(s3UploadEventObject.getDomain(), s3UploadEventObject.getUser(), json);
		return JsonUtils.getInteger(resp, IJsonNames.ID);
	}

	private static Certificate getAonCert() {
		String value = SECRETS.getValue("aonsolutions/aoncert");
		JSONObject json = new JSONObject(value);
		String cert = JsonUtils.getString(json, "AON_CERT");
		String password = JsonUtils.getString(json, "AON_PASSWORD");

		byte[] data = Base64.getDecoder().decode(cert);

		return new Certificate().setData(data).setPassword(password);
	}

	public static String getExtension(String filename) {
		Optional<String> ext = Optional.ofNullable(filename).filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1));
		if (ext.isPresent()) {
			String extension = ext.get();
			return extension.length() > 5 ? null : "." + extension;
		} else
			return null;
	}

	public static void main(String[] args) {
//      handleObject("aon-upload-post",  "");
//    	String bucket = "";
//    	String key = "invoices/pruebacarga-newsuite.aonsolutions.org/B01487271/app/20240923055153/00_(sep. 2024) Factura iDENDA.pdf";
//    	S3EventObject s3EventObject = new S3EventObject();
//    	s3EventObject.setBucket(bucket);
//    	s3EventObject.setDomain("b72384936-ayudat.aibanez.net");
//    	s3EventObject.setUser("albertocastro");
//    	s3EventObject.setDocument("b72384936");
//    	s3EventObject.setKey(key);
//
//    	JSONObject json = new JSONObject();
//    	JSONObject file = new JSONObject();
//    	file.put("s3Bucket", bucket);
//    	file.put("s3Key", key); 
//    	file.put("url", getDowloadURL(s3EventObject));
//    	file.put("path", getDowloadURL(s3EventObject));
//    	String contentType = solutions.aon.aws.s3.S3.getContentType(s3EventObject.getBucket(), s3EventObject.getKey());
//    	file.put("content_type", "application/pdf");
//    	json.put(IJsonNames.FILE, file);
//    	json.put(IJsonNames.STATUS, RawdocStatus.PROCESSING.getTediName());
//		JSONObject resp = AonInvofox.createRawdoc(s3EventObject.getDomain(), s3EventObject.getUser(), json);
//
//		InvofoxConfiguration invofoxConfiguration = getInvofoxConfiguration(s3EventObject);
//		String companyId =  getCompanyId(invofoxConfiguration, s3EventObject);
//
//		System.out.println(companyId);
//		System.out.println(invofoxConfiguration.getApiKey());
//		System.out.println(invofoxConfiguration.getApiUrl());
//		System.out.println(resp.toString());
	}

}
