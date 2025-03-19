package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.Properties.S3DocumentProperties;
import com.esferalia.aon.occam.api.model.S3Document;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.type.MimeType;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.aws.s3.S3rDoc;

@SuppressWarnings("serial")
@WebServlet(name = "DocumentalS3Servlet", urlPatterns = {"/ms/api/s3/*"})
public class DocumentalS3Servlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(DocumentalS3Servlet.class.getName());
	
	public static final String DOCUMENT = "/";
	public static final String DOCUMENT_FILE = "/file";
	public static final String DOCUMENT_FILE_MULTIPLE = "/file_multiple";
	public static final String COUNT = "/count";
	
	private static final String AON_BUCKET_NAME = "aon-documental";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		post(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		delete(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch(api.getPath()) {
				case DOCUMENT:
					response(req, resp, getAction(api));
					break;
				case DOCUMENT_FILE:
					responseFile(resp, getFile(api));
					break;
				case DOCUMENT_FILE_MULTIPLE:
					responseFile(resp, getFileMultiple(api, resp));
					break;
				case COUNT:
					response(req, resp, getCount(api));
					break;
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void post(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch(api.getPath()) {
				case DOCUMENT:
					response(req, resp, postAction(api));
					break;
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
				.addRoute(DOCUMENT, DocumentalS3Servlet::putAction)
				.apply();
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
				.addRoute(DOCUMENT, DocumentalS3Servlet::deleteAction)
				.apply();
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getCount(AonApiData api) {
		return new JSONObject().put(IJsonNames.COUNT, 
				AON_SOLUTIONS.getCountS3Document(
						api.getDomain(), 
						api.getUser(), 
						f -> generateFilter(f, api), 
						f -> generateFilterRAttach(f, api)));
	}
	
	private static Attach getFile(AonApiData api) throws Exception {
		try {
			Integer type = JsonUtils.getInteger(api.getData(), IJsonNames.TYPE);
			S3Document document = AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(api.getData().getInt(IJsonNames.ID)), f -> f.getIdProperty().eq(api.getData().getInt(IJsonNames.ID)), type, null, null, null).toList().getFirst();
			byte[] data = null;
			if(type == 0 && document.getS3key() != null) {
				data = S3rDoc.download(document.getS3key(), AON_BUCKET_NAME);
			} else if(type == 1){
				data = AON_SOLUTIONS.getFileS3Document(api.getDomain(), api.getUser(), document.getId());
			}
			Attach attach = new Attach()
					.setDescription(document.getName())
					.setData(data)
					.setMimeType(document.getMimetype());
			return attach;
		} catch(Exception e) {
			throw e;
		}
	}
	
	private static Attach getFileMultiple(AonApiData api, HttpServletResponse resp) throws Exception {
		try {
			String jsonData = api.getData().getString(IJsonNames.DATA);
			String decodedJson = URLDecoder.decode(jsonData, "UTF-8");
			JSONArray array = new JSONArray(decodedJson);
			Integer[] idsRdoc = new Integer[array.length()];
			Integer[] idsRattach = new Integer[array.length()];
			for(int i = 0; i < array.length(); i++) {
				if(array.getJSONObject(i).getInt(IJsonNames.TYPE) == 0)
					idsRdoc[i] = array.getJSONObject(i).getInt(IJsonNames.ID);
				else
					idsRattach[i] = array.getJSONObject(i).getInt(IJsonNames.ID);
			}
			List<S3Document> documents = AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(), f -> f.getIdProperty().in(idsRdoc), f -> f.getIdProperty().in(idsRattach), null, null, null, null).toList();
			List<byte[]> files = new LinkedList<byte[]>();
			List<String> filenames = new LinkedList<String>();
			for(S3Document document: documents) {
				byte[] data = null;
				if(document.getType() == 0 && document.getS3key() != null) {
					data = S3rDoc.download(document.getS3key(), AON_BUCKET_NAME);
				} else if(document.getType() == 1){
					data = AON_SOLUTIONS.getFileS3Document(api.getDomain(), api.getUser(), document.getId());
				}
				files.add(data);
				filenames.add(document.getName());
			}
			byte[] i = createZipFromByteArrays(files, filenames);
			Attach attach = new Attach()
					.setData(i)
					.setDescription("files")
					.setMimeType(MimeType.ZIP);
			return attach;
		} catch(Exception e) {
			throw e;
		}
	}
	
	public static byte[] createZipFromByteArrays(List<byte[]> fileDataList, List<String> fileNames) throws IOException {
        if (fileDataList.size() != fileNames.size()) {
            throw new IllegalArgumentException("La cantidad de archivos y nombres no coincide.");
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (int i = 0; i < fileDataList.size(); i++) {
                byte[] fileData = fileDataList.get(i);
                String fileName = fileNames.get(i);
                ZipEntry entry = new ZipEntry(fileName);
                zos.putNextEntry(entry);
                zos.write(fileData);
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        }
    }
	
	private static Object getAction(AonApiData api) {
		if(api.getData().has(IJsonNames.ID)) {
			return getOne(api);			
		} else {
			return getList(api);	
		}
	}
	
	private static JSONArray getList(AonApiData api) {
		System.out.println("GET LIST METHOD");
		JSONArray jsArray = new JSONArray();
		Optional<Integer> page = Optional.ofNullable(JsonUtils.getInteger(api.getData(), IJsonNames.PAGE));
		Optional<Integer> perPage = Optional.ofNullable(JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE));
		Integer category = JsonUtils.getInteger(api.getData(), IJsonNames.CATEGORY);
		AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(), f -> generateFilter(f, api), f -> generateFilterRAttach(f, api), null, category, page, perPage)
		.forEach(document -> {
			jsArray.put(fullDocumentToJson(document));
		});
		return jsArray;
	}
	
	private static JSONObject getOne(AonApiData api) {
		System.out.println("GET ONE METHOD");
		JSONArray jsArray = new JSONArray();
		Integer type = JsonUtils.getInteger(api.getData(), IJsonNames.TYPE);
		AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(api.getData().getInt(IJsonNames.ID)), f -> f.getIdProperty().eq(api.getData().getInt(IJsonNames.ID)), type, null, null, null)
		.forEach(document -> {
			jsArray.put(fullDocumentToJson(document));
		});
		return jsArray.length() > 0 ? jsArray.getJSONObject(0) : new JSONObject();
	}
	
	private static JSONObject postAction(AonApiData api) {
		System.out.println("POST METHOD");
		Decoder decoder = Base64.getDecoder();
		byte[] bytes = decoder.decode(api.getData().optString(IJsonNames.CONTENT));
		String doc = S3rDoc.uploadObject(bytes, AON_BUCKET_NAME, api.getDomain().getName());
		JSONObject json = api.getData();
		S3Document rdoc = new S3Document()
				.setCategory(json.getInt(IJsonNames.CATEGORY))
				.setDomain(json.getJSONObject(IJsonNames.DOMAIN).getInt(IJsonNames.ID))
				.setRegistry(api.getUser().getRegistry().getId())
				.setSize(JsonUtils.getInteger(json, IJsonNames.SIZE))
				.setDocumentDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setMimetype(MimeType.safeValueFromContenType(json.getString(IJsonNames.CONTENT_TYPE)))
				.setName(json.getString(IJsonNames.NAME))
				.setS3key(doc)
				.setS3bucket(AON_BUCKET_NAME)
				.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
				.setSecurityLevel((byte) 0)
				.setCreationDate(new Date())
				.setCreationUser(api.getUser().getLogin())
				.setModificationDate(new Date())
				.setModificationUser(api.getUser().getLogin())
				;
		S3Document document = AON_SOLUTIONS.insertS3Document(api.getDomain(), api.getUser(), rdoc);
		return fullDocumentToJson(document);
	}
	
	private static JSONObject putAction(AonApiData api) {
		System.out.println("PUT METHOD");
		JSONObject json = api.getData();
		Integer type = JsonUtils.getInteger(api.getData(), IJsonNames.TYPE);
		MimeType mime = JsonUtils.getString(json, IJsonNames.CONTENT_TYPE) != null ? MimeType.safeValueFromContenType(JsonUtils.getString(json, IJsonNames.CONTENT_TYPE)) : null;
		S3Document rdoc = new S3Document()
				.setId(json.getInt(IJsonNames.ID))
				.setCategory(JsonUtils.getInteger(json, IJsonNames.CATEGORY))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
				.setDocumentDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setMimetype(mime)
				.setName(JsonUtils.getString(json, IJsonNames.NAME))
				.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
				.setSecurityLevel(JsonUtils.getByte(json, IJsonNames.SECURITY_LEVEL))
				.setModificationDate(new Date())
				.setModificationUser(api.getUser().getLogin())
				;
		S3Document document = AON_SOLUTIONS.updateS3Document(api.getDomain(), api.getUser(), rdoc, type);
		return fullDocumentToJson(document);
	}
	
	private static JSONObject deleteAction(AonApiData api) {
		System.out.println("DELETE METHOD");
		JSONArray array = JsonUtils.getJSONArray(api.getData(), IJsonNames.DATA);
		Integer[] idsRdoc = new Integer[array.length()];
		Integer[] idsRattach = new Integer[array.length()];
		for(int i = 0; i < array.length(); i++) {
			if(array.getJSONObject(i).getInt(IJsonNames.TYPE) == 0)
				idsRdoc[i] = array.getJSONObject(i).getInt(IJsonNames.ID);
			else
				idsRattach[i] = array.getJSONObject(i).getInt(IJsonNames.ID);
		}
		AON_SOLUTIONS.deleteS3Document(api.getDomain(), api.getUser(), f -> f.getIdProperty().in(idsRdoc), f -> f.getIdProperty().in(idsRattach));	
		return new JSONObject("{ result: OK }");
	}
	
	private static Filter generateFilter(S3DocumentProperties f, AonApiData api) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		JSONObject json = api.getData();
//		if(json.has(IJsonNames.CATEGORY))
//			filter = filter.and(f.getCategoryProperty().eq(json.getInt(IJsonNames.CATEGORY)));
		if(json.has(IJsonNames.SCOPE))
			filter = filter.and(f.getScopeProperty().eq(json.getInt(IJsonNames.SCOPE)));
		if(json.has(IJsonNames.START_DATE))
			filter = filter.and(f.getDocumentDateProperty().ge(JsonUtils.getDate(json, IJsonNames.START_DATE)));
		if(json.has(IJsonNames.END_DATE))
			filter = filter.and(f.getDocumentDateProperty().le(JsonUtils.getDate(json, IJsonNames.END_DATE)));
		if(json.has(IJsonNames.FROM_DATE))
			filter = filter.and(f.getCreationDateProperty().gt(new Timestamp(JsonUtils.getDate(json, IJsonNames.FROM_DATE).getTime())));
		if(json.has(IJsonNames.TO_DATE))
			filter = filter.and(f.getCreationDateProperty().lt(new Timestamp(JsonUtils.getDate(json, IJsonNames.TO_DATE).getTime())));
		if(json.has(IJsonNames.CONTENT_TYPE))
			filter = filter.and(f.getMimeTypeProperty().eq(MimeType.safeValueFromContenType(json.getString(IJsonNames.CONTENT_TYPE)).value()));
		if(json.has(IJsonNames.NAME))
			filter = filter.and(f.getNameProperty().like("%" + json.getString(IJsonNames.NAME) + "%"));
		return filter;
	}
	
	private static Filter generateFilterRAttach(AttachProperties f, AonApiData api) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		JSONObject json = api.getData();
//		if(json.has(IJsonNames.CATEGORY))
//			filter = filter.and(f.getCategoryProperty().eq(json.getInt(IJsonNames.CATEGORY)));
		if(json.has(IJsonNames.SCOPE))
			filter = filter.and(f.getScopeProperty().eq(json.getInt(IJsonNames.SCOPE)));
		if(json.has(IJsonNames.START_DATE))
			filter = filter.and(f.getCreationDateTimeStampProperty().ge(new Timestamp(JsonUtils.getDate(json, IJsonNames.START_DATE).getTime())));
		if(json.has(IJsonNames.END_DATE))
			filter = filter.and(f.getCreationDateTimeStampProperty().le(new Timestamp(JsonUtils.getDate(json, IJsonNames.END_DATE).getTime())));
		if(json.has(IJsonNames.FROM_DATE))
			filter = filter.and(f.getCreationDateTimeStampProperty().gt(new Timestamp(JsonUtils.getDate(json, IJsonNames.FROM_DATE).getTime())));
		if(json.has(IJsonNames.TO_DATE))
			filter = filter.and(f.getCreationDateTimeStampProperty().lt(new Timestamp(JsonUtils.getDate(json, IJsonNames.TO_DATE).getTime())));
		if(json.has(IJsonNames.CONTENT_TYPE))
			filter = filter.and(f.getMimeTypeProperty().eq(MimeType.safeValueFromContenType(json.getString(IJsonNames.CONTENT_TYPE)).value()));
		if(json.has(IJsonNames.NAME))
			filter = filter.and(f.getDescriptionProperty().like("%" + json.getString(IJsonNames.NAME) + "%"));
		return filter;
	}
	
	private static JSONObject fullDocumentToJson(S3Document document) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.ID, document.getId());
		json.put(IJsonNames.DOMAIN, document.getDomain());
		json.put(IJsonNames.CATEGORY, document.getCategory());
		json.put(IJsonNames.NAME, document.getName());
		json.put(IJsonNames.REGISTRY, document.getRegistry());
		json.put(IJsonNames.S3_KEY, document.getS3key());
		json.put(IJsonNames.SCOPE, document.getScope());
		json.put(IJsonNames.SECURITY_LEVEL, document.getSecurityLevel());
		json.put(IJsonNames.CONTENT_TYPE, document.getMimetype().getName());
		json.put(IJsonNames.DATE, document.getDocumentDate());
		json.put(IJsonNames.CREATION_USER, document.getCreationUser());
		json.put(IJsonNames.CREATION_DATE, document.getCreationDate());
		json.put(IJsonNames.MODIFICATION_USER, document.getModificationUser());
		json.put(IJsonNames.MODIFICATION_DATE, document.getModificationDate());
		json.put(IJsonNames.TYPE, document.getType());
		return json;
	}
	
}
