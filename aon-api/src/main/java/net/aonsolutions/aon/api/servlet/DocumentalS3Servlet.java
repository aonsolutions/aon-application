package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.common.AonException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.Properties.S3DocumentProperties;
import com.esferalia.aon.occam.api.model.S3Category;
import com.esferalia.aon.occam.api.model.S3Document;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.google.apis.drive.AonDrive;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;
import solutions.aon.aws.s3.S3rDoc;

@SuppressWarnings("serial")
@WebServlet(name = "DocumentalS3Servlet", urlPatterns = {"/ms/api/s3/*"})
public class DocumentalS3Servlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(DocumentalS3Servlet.class.getName());
	
	public static final String DOCUMENT = "/";
	public static final String DOCUMENT_FILE = "/file";
	public static final String DOCUMENT_FILE_MULTIPLE = "/file_multiple";
	public static final String COUNT = "/count";
	public static final String BIDOQ = "/bidoq";
	public static final String CHECK_BIDOQ = "/check_bidoq";
	public static final String BIDOQ_OCR = "/bidoq_ocr";
	public static final String BIDOQ_OCR_COUNT = "/bidoq_ocr_count";
	
	private static final String AON_BUCKET_NAME = "aon-documental-pro-01";
	private static final String BIDOQ_BUCKET_NAME = "ayudat-mispapeles-docs-pro-02";
	private static final String NO_FOLDER = "No_folder";
	private static final String BIDOQ_NO_FOLDER = "Bidoq/No_folder";
	
	
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
				case BIDOQ:
					response(req, resp, getBidoqDocumentsToAon(api));
					break;
				case CHECK_BIDOQ:
					response(req, resp, checkBidoqDocumentsToAon(api));
					break;
				case BIDOQ_OCR:
					response(req, resp, getBidoqDocumentsToOCR(api));
					break;
				case BIDOQ_OCR_COUNT:
					response(req, resp, getBidoqDocumentsToOCRCount(api));
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
						f -> generateFilter(f, api, getScopes(api)), 
						f -> generateFilterRAttach(f, api, getScopes(api)),
						api.getData().has(IJsonNames.CATEGORY) ? api.getData().getInt(IJsonNames.CATEGORY) : null)
				);
	}
	
	private static Attach getFile(AonApiData api) throws Exception {
		Integer type = JsonUtils.getInteger(api.getData(), IJsonNames.TYPE);
		Integer id = api.getData().getInt(IJsonNames.ID);

		S3Document document = AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(),
				f -> f.getIdProperty().eq(id).and(f.getDeleteDateProperty().isNull()),
				f -> f.getIdProperty().eq(id), type, null, null, null)
			.toList().getFirst();

		byte[] data = null;
		if (type != null && type == 0 && document.getS3key() != null) {
			data = S3rDoc.download(document.getS3key(), document.getS3bucket());
		} else if (type != null && type == 1) {
			data = getRattachFile(api, id, document.getCreationUser());
		}

		if (data == null) throw new AonException("Archivo corrupto.");

		return new Attach()
				.setDescription(document.getName())
				.setData(data)
				.setMimeType(document.getMimetype());
	}
	
	private static byte[] getRattachFile(AonApiData api, Integer id, String creationUser) {
		Domain domain = api.getDomain();
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), api.getUser().getLogin(),
				f -> f.getIdProperty().eq(id), AttachType.getAttachType("registry"), true);

		if (attach == null) {
			LOGGER.warning("Attach no encontrado: id=" + id);
			return null;
		}

		if (attach.getData() == null && attach.getDriveId() != null) {
			DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), api.getUser().getLogin());
			Drive drive = AonDrive.getInstace().serviceInitialize(g);
			String[] keys = {"fileId", "aontype", "domain"};
			String[] values = {attach.getId() + "", "registry", attach.getDomain().getName()};
			FileList fl = SearchFiles.searchFilesAppProperties(drive, keys, values);
			if (!fl.getFiles().isEmpty()) {
				if (!fl.getFiles().get(0).getId().equals(attach.getDriveId())) {
					attach.setDriveId(fl.getFiles().get(0).getId());
					AON.updateAttach(domain.getName(), domain.getId(), "", attach);
				}
				if ("0".equals(attach.getDparentId())) {
					attach.setDparentId(fl.getFiles().get(0).getSize().toString());
					AON.updateAttach(domain.getName(), domain.getId(), "", attach);
				}
			}
			attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
		}

		return attach.getData();
	}

	private static Attach getFileMultiple(AonApiData api, HttpServletResponse resp) throws Exception {
		String jsonData = api.getData().getString(IJsonNames.DATA);
		String decodedJson = URLDecoder.decode(jsonData, StandardCharsets.UTF_8);
		JSONArray array = new JSONArray(decodedJson);

		List<Integer> rdocIds = new ArrayList<>();
		List<Integer> rattachIds = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject o = array.getJSONObject(i);
			if (o.getInt(IJsonNames.TYPE) == 0) rdocIds.add(o.getInt(IJsonNames.ID));
			else rattachIds.add(o.getInt(IJsonNames.ID));
		}
		// -1 no casa con nada: evita pasar arrays vacíos o llenos de null al in()
		if (rdocIds.isEmpty()) rdocIds.add(-1);
		if (rattachIds.isEmpty()) rattachIds.add(-1);
		Integer[] idsRdoc = rdocIds.toArray(new Integer[0]);
		Integer[] idsRattach = rattachIds.toArray(new Integer[0]);

		List<S3Document> documents = AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(),
				f -> f.getIdProperty().in(idsRdoc).and(f.getDeleteDateProperty().isNull()),
				f -> f.getIdProperty().in(idsRattach), null, null, null, null).toList();

		List<byte[]> files = new LinkedList<>();
		List<String> filenames = new LinkedList<>();

		for (S3Document document : documents) {
			byte[] data = null;
			if (document.getType() != null && document.getType() == 0 && document.getS3key() != null) {
				data = S3rDoc.download(document.getS3key(), document.getS3bucket());
			} else if (document.getType() != null && document.getType() == 1) {
				data = getRattachFile(api, document.getId(), document.getCreationUser());
			}

			if (data == null) {
				LOGGER.warning("Documento sin datos, se omite del ZIP: id=" + document.getId() + ", type=" + document.getType());
				continue;
			}

			files.add(data);
			filenames.add(buildFileName(document.getName(), document.getMimetype()));
		}

		if (files.isEmpty()) throw new AonException("No se ha podido descargar ninguno de los documentos seleccionados.");

		return new Attach()
				.setData(createZipFromByteArrays(files, filenames))
				.setDescription("files")
				.setMimeType(MimeType.ZIP);
	}
	
	public static byte[] createZipFromByteArrays(List<byte[]> fileDataList, List<String> fileNames) throws IOException {
		if (fileDataList.size() != fileNames.size()) {
			throw new IllegalArgumentException("La cantidad de archivos y nombres no coincide.");
		}
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {

			Set<String> used = new HashSet<>();
			for (int i = 0; i < fileDataList.size(); i++) {
				String fileName = fileNames.get(i);
				int dot = fileName.lastIndexOf('.');
				String base = dot > 0 ? fileName.substring(0, dot) : fileName;
				String ext  = dot > 0 ? fileName.substring(dot) : "";
				int n = 1;
				while (!used.add(fileName)) {
					fileName = base + " (" + (++n) + ")" + ext;
				}
				zos.putNextEntry(new ZipEntry(fileName));
				zos.write(fileDataList.get(i));
				zos.closeEntry();
			}
			zos.finish();
			return baos.toByteArray();
		}
	}

	private static String buildFileName(String name, MimeType mimeType) {
		String fileName = AonStringUtils.isBlank(name) ? "documento" : name.trim();
		fileName = fileName.replaceAll("[/\\\\:*?\"<>|]", "_");

		// si el nombre ya trae una extensión conocida, no añadimos nada
		if (MimeType.guessFromFileName(fileName) != null) return fileName;

		String ext = mimeType != null ? mimeType.getExtension() : null;
		if (AonStringUtils.isNotBlank(ext)) fileName = fileName + "." + ext;
		return fileName;
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

		AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(),
			f -> generateFilter(f, api, getScopes(api)),
			f -> generateFilterRAttach(f, api, getScopes(api)),
			null, category, page, perPage)
		.forEach(document -> {
			JSONObject doc = fullDocumentToJson(document);

			JSONArray tagArray = new JSONArray();
			AON_SOLUTIONS.getDocumentTags(api.getDomain(), api.getUser(), document.getId(), document.getType())
				.forEach(id -> tagArray.put(new JSONObject().put("id", id)));

			doc.put(IJsonNames.TAG, tagArray);

			jsArray.put(doc);
		});
		return jsArray;
	}

	private static JSONObject getOne(AonApiData api) {
		System.out.println("GET ONE METHOD");
		JSONArray jsArray = new JSONArray();
		Integer type = JsonUtils.getInteger(api.getData(), IJsonNames.TYPE);
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(api.getData().getInt(IJsonNames.ID)).and(f.getDeleteDateProperty().isNull()), f -> f.getIdProperty().eq(api.getData().getInt(IJsonNames.ID)), type, null, null, null)
		.forEach(document -> {
			JSONObject doc = fullDocumentToJson(document);
			AON_SOLUTIONS.getDocumentTags(api.getDomain(), api.getUser(), api.getData().getInt(IJsonNames.ID), type).forEach(id -> {
				array.put(new JSONObject().put("id", id));
			});
			doc.put(IJsonNames.TAG, array);
			jsArray.put(doc);
		});
		
		return jsArray.length() > 0 ? jsArray.getJSONObject(0) : new JSONObject();
	}
	
	private static JSONObject postAction(AonApiData api) {
		System.out.println("POST METHOD");
		Decoder decoder = Base64.getDecoder();
		byte[] bytes = decoder.decode(api.getData().optString(IJsonNames.CONTENT));
		String mimetype = MimeType.safeValueFromContenType(api.getData().getString(IJsonNames.CONTENT_TYPE)).getExtension();
		String doc = S3rDoc.uploadObject(bytes, AON_BUCKET_NAME, api.getDomain().getName(), mimetype);
		JSONObject json = api.getData();
		Integer registry = api.getUser().getRegistry().getId(); 
		if(api.getUser().getRegistry().getId() == null) {
//			registry = AON.getEnterpriseData(api.getDomain(), api.getUser(), f -> f.getDomainProperty().eq(api.getDomain().getId())).getEnterprise();
			registry = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId())).getId();
		}
		ArrayList<Integer> tags = new ArrayList<Integer>();
		if(api.getData().has(IJsonNames.TAG)) {			
			JSONArray tagsArray = api.getData().getJSONArray(IJsonNames.TAG);
			if(tagsArray.length() > 0) {
				for(int i = 0; i < tagsArray.length(); i++)
					tags.add(tagsArray.getJSONObject(i).getInt(IJsonNames.ID));
			}
		}
		S3Document rdoc = new S3Document()
				.setCategory(json.getInt(IJsonNames.CATEGORY))
				.setDomain(json.getJSONObject(IJsonNames.DOMAIN).getInt(IJsonNames.ID))
				.setRegistry(registry)
				.setSize(JsonUtils.getInteger(json, IJsonNames.SIZE))
				.setDocumentDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setMimetype(MimeType.safeValueFromContenType(json.getString(IJsonNames.CONTENT_TYPE)))
				.setName(json.getString(IJsonNames.NAME))
				.setS3key(doc)
				.setRegistryType(getRegistryAttachmentType(api))
				.setS3bucket(AON_BUCKET_NAME)
				.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
				.setSecurityLevel((byte) 0)
				.setCreationDate(new Date())
				.setCreationUser(api.getUser().getLogin())
				.setModificationDate(new Date())
				.setModificationUser(api.getUser().getLogin())
				.setTags(tags)
				;
		S3Document document = AON_SOLUTIONS.insertS3Document(api.getDomain(), api.getUser(), rdoc);
		return fullDocumentToJson(document);
	}
	
	private static JSONObject putAction(AonApiData api) {
		System.out.println("PUT METHOD");
		JSONObject json = api.getData();
		Integer type = JsonUtils.getInteger(api.getData(), IJsonNames.TYPE);
		MimeType mime = JsonUtils.getString(json, IJsonNames.CONTENT_TYPE) != null ? MimeType.safeValueFromContenType(JsonUtils.getString(json, IJsonNames.CONTENT_TYPE)) : null;
		ArrayList<Integer> tags = new ArrayList<Integer>();
		if(api.getData().has(IJsonNames.TAG)) {	
			JSONArray tagsArray = api.getData().getJSONArray(IJsonNames.TAG);
			if(tagsArray.length() > 0) {
				for(int i = 0; i < tagsArray.length(); i++)
					tags.add(tagsArray.getJSONObject(i).getInt(IJsonNames.ID));
			}
		}
		S3Document rdoc = new S3Document()
				.setId(json.getInt(IJsonNames.ID))
				.setCategory(JsonUtils.getInteger(json, IJsonNames.CATEGORY))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
				.setDocumentDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setRegistryType(getRegistryAttachmentType(api))
				.setMimetype(mime)
				.setName(JsonUtils.getString(json, IJsonNames.NAME))
				.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
				.setSecurityLevel(JsonUtils.getByte(json, IJsonNames.SECURITY_LEVEL))
				.setModificationDate(new Date())
				.setModificationUser(api.getUser().getLogin())
				.setTags(tags)
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
	
	private static Filter generateFilter(S3DocumentProperties f, AonApiData api, Integer[] scopes) {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId()).and(f.getDeleteDateProperty().isNull());
		JSONObject json = api.getData();
		String type = json.optString("registryType", null) != null ? json.optString("registryType") : "all";
		if(scopes != null)
    		filter = filter.and(f.getScopeProperty().in(scopes).or(f.getScopeProperty().isNull()));
		if(json.has(IJsonNames.START_DATE))
			filter = filter.and(f.getDocumentDateProperty().ge(JsonUtils.getDate(json, IJsonNames.START_DATE)));
		if(json.has(IJsonNames.END_DATE2))
			filter = filter.and(f.getDocumentDateProperty().le(JsonUtils.getDate(json, IJsonNames.END_DATE)));
		if(json.has(IJsonNames.FROM_DATE))
			filter = filter.and(f.getCreationDateProperty().gt(new Timestamp(JsonUtils.getDate(json, IJsonNames.FROM_DATE).getTime())));
		if(json.has(IJsonNames.TO_DATE))
			filter = filter.and(f.getCreationDateProperty().lt(new Timestamp(JsonUtils.getDate(json, IJsonNames.TO_DATE).getTime())));
		if(json.has(IJsonNames.CONTENT_TYPE))
			filter = filter.and(f.getMimeTypeProperty().eq(MimeType.safeValueFromContenType(json.getString(IJsonNames.CONTENT_TYPE)).value()));
		if(json.has(IJsonNames.NAME))
			filter = filter.and(f.getNameProperty().like("%" + json.getString(IJsonNames.NAME) + "%"));
		if(!dur.isConfidential()) {
    		filter = filter.and(f.getSecurityLevelProperty().eq((byte) 0));
    	}
		if(type != null) {
        	if("system".equalsIgnoreCase(type)) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.SYSTEM_MESSAGE.value()));
        	} else if("enterprise".equalsIgnoreCase(type)) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value()));
        	} else if("employee".equalsIgnoreCase(type)) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value()));
        	} else if("asesor".equalsIgnoreCase(type)) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_ASESOR.value()));
        	} else if("all".equalsIgnoreCase(type)) {
        		if(dur.isDocumentalManager()) {
            		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value())
            			.or(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_ASESOR.value()))
            			.or(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value())));
            	} else if(dur.isDocumentalPortal()) {
            		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value())
               			.or(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value())));
            	} else if(dur.isDocumental()) {
            		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value()));    		
            	} else filter = filter.and(f.getTypeProperty().isNull());
        	}
    	}
		if(api.getData().has(IJsonNames.TAG)) {
			JSONArray tags = new JSONArray(api.getData().getString(IJsonNames.TAG));
			if(tags.length() > 0) {
				Integer[] idsTags = new Integer[tags.length()];
				for(int i = 0; i < tags.length(); i++)
					idsTags[i] = tags.getJSONObject(i).getInt(IJsonNames.ID);
				filter = filter.and(f.getTagProperty().in(idsTags));
			}
		}

		return filter;
	}
	
	private static Filter generateFilterRAttach(AttachProperties f, AonApiData api, Integer[] scopes) {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		JSONObject json = api.getData();
		String type = json.optString("registryType", null) != null ? json.optString("registryType") : "all";
		if(scopes != null)
    		filter = filter.and(f.getScopeProperty().in(scopes).or(f.getScopeProperty().isNull()));
		if(json.has(IJsonNames.START_DATE))
			filter = filter.and(f.getCreationDateTimeStampProperty().ge(new Timestamp(JsonUtils.getDate(json, IJsonNames.START_DATE).getTime())));
		if(json.has(IJsonNames.END_DATE2))
			filter = filter.and(f.getCreationDateTimeStampProperty().le(new Timestamp(JsonUtils.getDate(json, IJsonNames.END_DATE).getTime())));
		if(json.has(IJsonNames.FROM_DATE))
			filter = filter.and(f.getCreationDateTimeStampProperty().gt(new Timestamp(JsonUtils.getDate(json, IJsonNames.FROM_DATE).getTime())));
		if(json.has(IJsonNames.TO_DATE))
			filter = filter.and(f.getCreationDateTimeStampProperty().lt(new Timestamp(JsonUtils.getDate(json, IJsonNames.TO_DATE).getTime())));
		if(json.has(IJsonNames.CONTENT_TYPE))
			filter = filter.and(f.getMimeTypeProperty().eq(MimeType.safeValueFromContenType(json.getString(IJsonNames.CONTENT_TYPE)).value()));
		if(json.has(IJsonNames.NAME))
			filter = filter.and(f.getDescriptionProperty().like("%" + json.getString(IJsonNames.NAME) + "%"));
		if(!dur.isConfidential()) {
    		filter = filter.and(f.getSecurityLevelProperty().eq((byte) 0));
    	}
		if(type != null) {
        	if("system".equalsIgnoreCase(type)) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.SYSTEM_MESSAGE.value()));
        	} else if("enterprise".equalsIgnoreCase(type)) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value()));
        	} else if("employee".equalsIgnoreCase(type)) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value()));
        	} else if("asesor".equalsIgnoreCase(type)) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_ASESOR.value()));
        	} else if("all".equalsIgnoreCase(type)) {
        		if(dur.isDocumentalManager()) {
            		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value())
            			.or(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_ASESOR.value()))
            			.or(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value())));
            	} else if(dur.isDocumentalPortal()) {
            		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value())
               			.or(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value())));
            	} else if(dur.isDocumental()) {
            		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value()));    		
            	} else filter = filter.and(f.getTypeProperty().isNull());
        	}
    	}
		if(api.getData().has(IJsonNames.TAG)) {			
			JSONArray tags = new JSONArray(api.getData().getString(IJsonNames.TAG));
			if(tags.length() > 0) {
				Integer[] idsTags = new Integer[tags.length()];
				for(int i = 0; i < tags.length(); i++)
					idsTags[i] = tags.getJSONObject(i).getInt(IJsonNames.ID);
				filter = filter.and(f.getTagProperty().in(idsTags));
			}
		}
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
		json.put("registryType", getRegistryAttachmentType(document));
		json.put(IJsonNames.TYPE, document.getType());
		json.put(IJsonNames.SIZE, document.getSize());
		return json;
	}

	private static Integer[] getScopes(AonApiData api) {
		Integer[] scopes = null;
		try {
			scopes = AON.getUserScopes(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId());
			if(api.getUser().getDomain().getId().equals(api.getDomain().getParentId())) {
				Integer[] scopes2 = AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
						r -> r.getDomainProperty().eq(api.getDomain().getId())).map(r -> r.getId()).toArray(Integer[]::new);
				Integer[] scopes3 = AON.getUserScopes(api.getDomain().getName(), api.getDomain().getParentId(), api.getUser().getLogin(), api.getUser().getId());
				if(scopes == null && scopes3 == null) {
					scopes = scopes2;
				} else if(scopes == null) {
					scopes = new Integer[scopes2.length + scopes3.length];
					for (Integer i = 0; i< scopes2.length; i++) {
						scopes[i] = scopes2[i];
					}
					for (Integer i = 0; i< scopes3.length; i++) {
						scopes[i + scopes2.length] = scopes3[i];
					}
				}
			}
		} catch (Exception e) {
			scopes = null;
		}
		return scopes;
	}

	private static Byte getRegistryAttachmentType(AonApiData api) {
		if(api.getData().optString("registryType").equals("asesor")) {
			return 24;
		} else if(api.getData().optString("registryType").equals("enterprise")) {
			return 3;
		} else if(api.getData().optString("registryType").equals("employee")) {
			return 25;
		}
		return 3;
	}
	
	private static String getRegistryAttachmentType(S3Document document) {
		if(document.getRegistryType() != null)
			if(document.getRegistryType() == 24) {
				return "asesor";
			} else if(document.getRegistryType() == 3) {
				return "enterprise";
			} else if(document.getRegistryType() == 25) {
				return "employee";
			}
		return "enterprise";
	}
	
	private static JSONObject getBidoqDocumentsToAon(AonApiData api) {
		String document = api.getData().getString(IJsonNames.DOCUMENT);
		JSONObject json = callBidoq(document, "transfer_document_aon");
		if(json.has("datos")) {		
			JSONObject folders = json.getJSONObject("datos");
			for(String folder : JSONObject.getNames(folders)) {
				if(!folder.equals("no_folders")) {
					if(!folder.equals("A contabilizar")){
						if(!checkBidoqCategory(api, folders.getJSONObject(folder).getString("nombre")) && folders.getJSONObject(folder).has("docs")) {
							S3Category category = createBidoqCategory(api, folders.getJSONObject(folder).getString("nombre"));
							AON_SOLUTIONS.insertS3Category(api.getDomain(), api.getUser(), category);
						}
						if(folders.getJSONObject(folder).has("docs")) {
							JSONArray array = folders.getJSONObject(folder).getJSONArray("docs");
							for(int i = 0; i < array.length(); i++) {
								if(array.get(i) instanceof JSONObject) {								
									JSONObject doc = (JSONObject) array.get(i); 
									if(!checkBidoqDocument(api, doc.getString("rutaS3"))) {
										S3Document docu = createBidoqDocument(api, doc, folders.getJSONObject(folder).getString("nombre"));
										AON_SOLUTIONS.insertS3Document(api.getDomain(), api.getUser(), docu);
									}
								}
							}
						}
					}
				} else if(folder.equals("no_folders")){
					if(!checkBidoqCategory(api, NO_FOLDER) && folders.getJSONArray(folder).length() > 0) {
						S3Category category = createBidoqCategory(api, NO_FOLDER);
						AON_SOLUTIONS.insertS3Category(api.getDomain(), api.getUser(), category);
						JSONArray array = folders.getJSONArray(folder);
						for(int i = 0; i < array.length(); i++) {
							if(array.get(i) instanceof JSONObject) {
								JSONObject doc = (JSONObject) array.get(i);
								if(!checkBidoqDocument(api, doc.getString("rutaS3"))) {
									S3Document docu = createBidoqDocument(api, doc, BIDOQ_NO_FOLDER);
									AON_SOLUTIONS.insertS3Document(api.getDomain(), api.getUser(), docu);
								}
							}
						}
					}
				}
			}
			return folders;
		}
		return new JSONObject().put("result", "OK");
	}
	
	private static S3Document createBidoqDocument(AonApiData api, JSONObject json, String category) {
		Integer registry = api.getUser().getRegistry().getId(); 
		if(api.getUser().getRegistry().getId() == null) {
			registry = AON.getEnterpriseData(api.getDomain(), api.getUser(), f -> f.getDomainProperty().eq(api.getDomain().getId())).getEnterprise();
		}
		String name = json.getString("nombreAlmacenado");
		String [] split = name.split("\\.");
		MimeType mime = MimeType.safeValueFromExtension(split[split.length-1].toLowerCase());
		Stream<S3Category> list = AON_SOLUTIONS.getS3CategoryStream(api.getDomain(), api.getUser(), f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getNameProperty().eq("Bidoq/" + category)), Optional.ofNullable(null), Optional.ofNullable(null));
		Optional<S3Category> s3category = list.findFirst();
		return new S3Document()
				.setCategory(s3category.isPresent() ? s3category.get().getId() : null)
				.setCreationDate(new Date())
				.setCreationUser(api.getUser().getLogin())
				.setDocumentDate(new Date())
				.setDomain(api.getDomain().getId())
				.setMimetype(mime)
				.setModificationDate(null)
				.setModificationUser(null)
				.setName(clearFileName(json.getString("nombreArchivo")))
				.setRealName(clearFileName(json.getString("nombreAlmacenado")))
				.setRegistry(registry)
				.setRegistryType((byte) 3)
				.setS3bucket(BIDOQ_BUCKET_NAME)
				.setS3key(json.getString("rutaS3"))
				.setScope(null)
				.setSize(json.getInt("tamano"))
				.setSecurityLevel((byte) 0)
				;
	}
	
	private static String clearFileName(String filename) {
		String textoOriginal = filename;
		return Normalizer.normalize(textoOriginal, Normalizer.Form.NFC);
	}

	
	private static S3Category createBidoqCategory(AonApiData api, String name) {
		S3Category category = new S3Category()
				.setDescription("Bidoq/" + name)
				.setName("Bidoq/" + name)
				.setDomain(api.getDomain().getId())
				.setIsDeletable((byte) 1)
				.setIsVisible((byte) 1)
				.setParent(null)
				.setScope(null);
		return category;
	}
	
	private static boolean checkBidoqCategory(AonApiData api, String category) {
		Stream<S3Category> list = AON_SOLUTIONS.getS3CategoryStream(api.getDomain(), api.getUser(), f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getNameProperty().eq("Bidoq/" + category)), Optional.ofNullable(null), Optional.ofNullable(null));
		if(list.count() == 0)
			return false;
		else 
			return true;
	}
	
	private static boolean checkBidoqDocument(AonApiData api, String s3key) {
		Stream<S3Document> list = AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(), f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getS3Property().eq(s3key)).and(f.getS3BucketProperty().eq(BIDOQ_BUCKET_NAME)).and(f.getDeleteDateProperty().isNull()), f -> null, 0, null, Optional.ofNullable(null), Optional.ofNullable(null));
		if(list.count() == 0)
			return false;
		else 
			return true;
	}
	
	private static JSONObject callBidoq(String document, String method) {
        String url = "https://mispapeles.es/api/v2/";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(StandardCharsets.UTF_8);
            builder.addTextBody("method", method);
            builder.addTextBody("operating_system_version", "0.5");
            builder.addTextBody("app_version", "1990");
            builder.addTextBody("device_info", "AON");
            builder.addTextBody("app_code", "1");
            builder.addTextBody("_token", "AZLD6JvKigwVf@BMKPKtvheQ69UCCtv*t75NXX!nr8X*i6!Dvxh9RW9G3SW3L4L");
            builder.addTextBody("enterprise", document);
            HttpEntity multipart = builder.build();
            post.setEntity(multipart);
            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return new JSONObject(responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", "fallï¿½ la llamada");
        }
    }
	
	private static boolean checkBidoqDocumentsToAon(AonApiData api) {
		String document = api.getData().getString(IJsonNames.DOCUMENT);
        JSONObject json = callBidoq(document, "transfer_document_aon_exist_user");
        return json.has("code") && json.getInt("code") == 0 ? true : false;
	}
	
	private static Integer getBidoqDocumentsToOCRCount(AonApiData api) {
		String document = api.getData().getString(IJsonNames.DOCUMENT);
//		JSONObject json = callBidoq("78150882x", "transfer_document_aon");
//		if(!document.equals("93176905H")) {
//			return 0;
//		}
		JSONObject json = callBidoq(document, "transfer_document_aon");
		int count = 0;
		if(json.has("datos")) {
			JSONObject folders = json.getJSONObject("datos");
			for(String folder : JSONObject.getNames(folders)) {
				if(!folder.equals("no_folders") && folders.getJSONObject(folder).has("nombre") && folders.getJSONObject(folder).getString("nombre").equals("A contabilizar")) {
					if(folders.getJSONObject(folder).has("docs")) {
						JSONArray array = folders.getJSONObject(folder).getJSONArray("docs");
						for(int i = 0; i < array.length(); i++) {
							if(array.get(i) instanceof JSONObject) {
								JSONObject doc = (JSONObject) array.get(i);
								Optional<S3Document> optRdoc = AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(), f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getS3Property().eq(doc.getString("rutaS3"))).and(f.getS3BucketProperty().eq(BIDOQ_BUCKET_NAME)), f -> null, 0, null, Optional.ofNullable(null), Optional.ofNullable(null)).findFirst();
								if(!optRdoc.isPresent())
									count++;
								else if(optRdoc.get().getDeleteDate() == null)
									count++;
							}
						}
					}
				}
			}
		}
		return count;
	}
	
	private static JSONObject getBidoqDocumentsToOCR(AonApiData api) {
		String document = api.getData().getString(IJsonNames.DOCUMENT);
		Integer order = api.getData().getInt(IJsonNames.ORDER);
		Integer size = api.getData().getInt(IJsonNames.SIZE);
//		if(!document.equals("93176905H")) {
//			return new JSONObject().put("result", "OK");
//		}
//		JSONObject json = callBidoq("78150882x", "transfer_document_aon");
		JSONObject json = callBidoq(document, "transfer_document_aon");
		int count = getBidoqDocumentsToOCRCount(api);
		int cont = 0;
		if(json.has("datos")) {		
			JSONObject folders = json.getJSONObject("datos");
			for(String folder : JSONObject.getNames(folders)) {
				if(!folder.equals("no_folders") && folders.getJSONObject(folder).has("nombre") && folders.getJSONObject(folder).getString("nombre").equals("A contabilizar")){
					if(folders.getJSONObject(folder).has("docs")) {
						JSONArray array = folders.getJSONObject(folder).getJSONArray("docs");
						for(int i = order; i < array.length(); i++) {
							if(array.get(i) instanceof JSONObject) {
								JSONObject doc = (JSONObject) array.get(i);
								Optional<S3Document> optRdoc = AON_SOLUTIONS.getS3DocumentStream(api.getDomain(), api.getUser(), f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getS3Property().eq(doc.getString("rutaS3"))).and(f.getS3BucketProperty().eq(BIDOQ_BUCKET_NAME)), f -> null, 0, null, Optional.ofNullable(null), Optional.ofNullable(null)).findFirst();
								if(optRdoc.isPresent()) {
									S3Document rdoc = optRdoc.get();
									if(rdoc.getDeleteDate() == null){
										if(uploadDocumentOCR(api, rdoc, document, i)) {
											AON_SOLUTIONS.deleteS3Document(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(rdoc.getId()), null);
											cont++;
										}
									}
								}else{
									S3Document docu = createBidoqDocument(api, doc, folders.getJSONObject(folder).getString("nombre"));
									if(uploadDocumentOCR(api, docu, document, i)) {
										S3Document fac = AON_SOLUTIONS.insertS3Document(api.getDomain(), api.getUser(), docu);
										AON_SOLUTIONS.deleteS3Document(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(fac.getId()), null);
										cont++;
									}
								}
							}
							if(cont >= (order + size < count ? order + size : count)) {
								return new JSONObject().put("result", "OK");
							}
						}
					}
				}
			}
		}
		return new JSONObject().put("result", "OK");
	}
	
	private static boolean uploadDocumentOCR(AonApiData api, S3Document doc, String document, int orden) {
		String url = "https://aon-upload-post.s3.amazonaws.com/";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime now = LocalDateTime.now();
        	byte[] file = S3rDoc.download(doc.getS3key(), doc.getS3bucket());
            HttpPost post = new HttpPost(url);
            String str = Integer.toString(orden);
            if(str.length() == 1) {
            	str = "0" + str;
            }
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(StandardCharsets.UTF_8);
            builder.addTextBody("key", "invoices/" + api.getDomain().getName() + "/" + document + "/" + api.getUser().getLogin() + "/" + now.format(formatter) + "/" + str + "_bidoq_" + Base64.getEncoder().encodeToString(doc.getName().getBytes()) + "." + doc.getMimetype().getExtension());
            builder.addTextBody("success_action_status", "201");
            builder.addTextBody("Content-Type", doc.getMimetype().getName());
            builder.addBinaryBody("file", file);
            HttpEntity multipart = builder.build();
            post.setEntity(multipart);
            try (CloseableHttpResponse response = client.execute(post)) {
                EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}
	
}
