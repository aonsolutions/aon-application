package net.aonsolutions.aon.api.servlet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.Certificate.CertificateSecurity;
import com.esferalia.aon.occam.api.model.CertificateInfo;

import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.SecondaryUser;
import solutions.aon.sepe.Sepe;
import solutions.aon.sepe.exceptions.SepeException;

@SuppressWarnings("serial")
@WebServlet(name = "NewCertificateServlet", urlPatterns = {"/ms/api/certificate/*"})
public class NewCertificateServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(NewCertificateServlet.class.getName());
	
	public static final String CERTIFICATES = "/";
	public static final String CERTIFICATE_ONE = "/one";
	public static final String CERTIFICATE_VERIFY = "/verify";
	public static final String CERTIFICATE_SECONDARY_USERS = "/secondary-users";
	public static final String CHECKPASS = "/check-password";
	
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
			Object object = new AonRouting(api)
				.addRoute(CERTIFICATES, NewCertificateServlet::getListAction)
				.addRoute(CERTIFICATE_ONE, NewCertificateServlet::getOneAction)
				.addRoute(CERTIFICATE_VERIFY, NewCertificateServlet::getVerifyAction)
				.addRoute(CERTIFICATE_SECONDARY_USERS, NewCertificateServlet::getSecondaryUsers)
				.apply();
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void post(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
				.addRoute(CERTIFICATES, NewCertificateServlet::postAction)
				.addRoute(CHECKPASS, NewCertificateServlet::checkPassword)
				.apply();
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
				.addRoute(CERTIFICATES, NewCertificateServlet::putAction)
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
				.addRoute(CERTIFICATES, NewCertificateServlet::deleteAction)
				.apply();
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	public static JSONObject getOneAction(AonApiData api) {
		Integer id = api.getData().optInt(IJsonNames.ID);
		Certificate cert = AON.getOneCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), id);
		return toJSON(cert);
	}
	
	public static JSONObject getVerifyAction(AonApiData api) throws AonApiException {
		Integer id = api.getData().optInt(IJsonNames.ID);
		Certificate cert = AON.getOneCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), id);
		if(cert.getTags().contains(CertificateType.TGSS)) {
			try {
				InputStream certificateIS = new ByteArrayInputStream(cert.getData());
				SistemaRED.validateCert(certificateIS, cert.getPassword(), cert.getType());
			} catch(SegSocialException e) {
				throw new AonApiException(e.getMessage());
			}
		}
		if(cert.getTags().contains(CertificateType.SEPE)) {
			try {
				InputStream certificateIS = new ByteArrayInputStream(cert.getData());
				Sepe.validateCert(certificateIS, cert.getPassword(), cert.getType());
			} catch(SepeException e) {
				throw new AonApiException(e.getMessage());
			}
		}
		return new JSONObject();
	}
	
	public static JSONArray getListAction(AonApiData api) {
		JSONArray json = new JSONArray();
		List<Certificate> certificates = AON.getCertificates(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId());
		for(int i = 0; i < certificates.size(); i++) {
			json.put(toJSON(certificates.get(i)));
		}
		return json;
	}
	
	public static JSONArray getSecondaryUsers(AonApiData api) {
		JSONArray json = new JSONArray();
		Certificate certificate = AON.getCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");
		InputStream certificateIS = new ByteArrayInputStream(certificate.getData());
		Collection<SecondaryUser> users;
		try {
			users = SistemaRED.getSecondaryUsers(certificateIS, certificate.getPassword(), certificate.getType());
			List<SecondaryUser> list = users.stream().collect(Collectors.toList());
			for(int i = 0; i < list.size(); i++) {
				json.put(sUserToJSON(list.get(i)));
			}
		} catch (SegSocialException e) {
			e.printStackTrace();
		}
		return json; 
	}
	
	private static JSONObject postAction(AonApiData api) {
		String owner = api.getData().optString(IJsonNames.OWNER);
//		String confidential = api.getData().optString(IJsonNames.CONFIDENTIAL);
		String pass = api.getData().optString(IJsonNames.PASSWORD);
		String data = api.getData().optString(IJsonNames.FILE_DATA);
		String filename = api.getData().optString(IJsonNames.FILE_NAME);
		String alias = api.getData().optString(IJsonNames.ALIAS);
		Boolean aeat = api.getData().optBoolean("aeat");
		Boolean sepe = api.getData().optBoolean("sepe");
		Boolean tgss = api.getData().optBoolean("tgss");
		byte[] dataFile = Base64.getDecoder().decode(data);
		try (InputStream certificateInputStream = new ByteArrayInputStream(dataFile)) {
			KeyStore keyStore = KeyStore.getInstance(MimeType.PKCS12.name());
	        keyStore.load(certificateInputStream, pass.toCharArray());
		}catch (Exception e) {
			throw new AonApiException("La contraseña no es correcta.");
		}
		List<CertificateType> list = new ArrayList<>();
		if(Boolean.TRUE.equals(aeat)) list.add(CertificateType.AEAT);
		if(Boolean.TRUE.equals(tgss)) list.add(CertificateType.TGSS);
		if(Boolean.TRUE.equals(sepe)) list.add(CertificateType.SEPE);
		Certificate certificate = new Certificate()
				.setDescription(filename)
				.setDomain(api.getDomain().getId())
				.setData(dataFile)
				.setDescription(alias)
				// De momento vamos a dejar todos los certificados que se suban a través de la api como privados ya que el hecho de que sea publico solo tendría sentido en la suite, 
				// dentro de un portal ninguna empresa querría tener un certificado público que fuera visible desde otra
				.setConfidential(CertificateSecurity.PRIVATE) 
				.setOwner(CertificateOwner.valueOf(owner))
				.setPassword(pass)
				.setTags(list);
		checkCertificateType(api, certificate);
		AON.saveCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), certificate);
		return new JSONObject();
	}
	
	private static boolean checkPassword(AonApiData api) {
		String dataj = api.getData().optString(IJsonNames.FILE_DATA);
		String password = api.getData().optString(IJsonNames.PASSWORD);
		byte[] data = Base64.getDecoder().decode(dataj);
		try (InputStream certificateInputStream = new ByteArrayInputStream(data)) {
					KeyStore keyStore = KeyStore.getInstance(MimeType.PKCS12.name());
			        keyStore.load(certificateInputStream, password.toCharArray());
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private static void checkCertificateType(AonApiData api, Certificate certificate) {
		List<Certificate> certificateList = AON.getCertificates(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId());
		checkCertificateForType(certificate, certificateList, CertificateOwner.USER);
		checkCertificateForType(certificate, certificateList, CertificateOwner.ENTERPRISE);
	}
	
	private static void checkCertificateForType(Certificate certificate, List<Certificate> certificateList, CertificateOwner owner) {
		List<CertificateType> tagList = new ArrayList<>();
		for(int i = 0; i < certificateList.size(); i++)
			if(!Objects.equals(certificate.getId(), certificateList.get(i).getId()) && (certificateList.get(i).getOwner().equals(owner)))
				tagList.addAll(certificateList.get(i).getTags());
		if(certificate.getOwner().equals(owner) && (
			(tagList.contains(CertificateType.AEAT) && certificate.getTags().contains(CertificateType.AEAT)) ||
			(tagList.contains(CertificateType.SEPE) && certificate.getTags().contains(CertificateType.SEPE)) ||
			(tagList.contains(CertificateType.TGSS) && certificate.getTags().contains(CertificateType.TGSS))
		)){
			throw new AonApiException("Error, ya tiene un certificado de este tipo.");
		}
	}
	
	private static JSONObject putAction(AonApiData api) {
		Boolean aeat = api.getData().optBoolean("aeat");
		Boolean sepe = api.getData().optBoolean("sepe");
		Boolean tgss = api.getData().optBoolean("tgss");
		String alias = api.getData().optString(IJsonNames.ALIAS);
		Integer id = api.getData().optInt(IJsonNames.ID);
		String owner = api.getData().optString(IJsonNames.OWNER);
		Optional<Certificate> certOpt = AON.getCertificates(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(id)).findFirst();
		Certificate cert;
		if(certOpt.isPresent()) cert = certOpt.get();
		else throw new AonApiException("No se ha encontrado el certificado.");
		Certificate certPass = AON.getCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(id));
		CertificateInfo certInfo = AON.getCertificateInfo(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(id));
		cert.setCertificateInfo(certInfo);
		List<CertificateType> list = new ArrayList<>();
		if(Boolean.TRUE.equals(aeat)) list.add(CertificateType.AEAT);
		if(Boolean.TRUE.equals(tgss)) list.add(CertificateType.TGSS);
		if(Boolean.TRUE.equals(sepe)) list.add(CertificateType.SEPE);
		cert.setTags(list);
		cert.setDescription(alias);
		cert.setOwner(CertificateOwner.valueOf(owner));
		cert.setData(null);
		cert.setPassword(certPass.getPassword());
		checkCertificateType(api, cert);
		AON.saveCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), cert);
		Certificate returnedCert = AON.getOneCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), id);
		return toJSON(returnedCert);
	}
	
	private static JSONObject deleteAction(AonApiData api) {
		int id = api.getData().optInt(IJsonNames.ID);
		if(id == 0) throw new AonApiException("No se ha recibido ningún identificador.");
		AON.deleteCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), id, f -> f.getIdProperty().eq(id), null);
		return new JSONObject();
	}
	
	private static JSONObject toJSON(Certificate c) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.ID, c.getId());
		json.put(IJsonNames.TYPE, c.getType());
		if (c.getCertificateInfo() != null) {
			json.put(IJsonNames.NAME, c.getCertificateInfo().getName());
			json.put(IJsonNames.DOCUMENT, c.getCertificateInfo().getDocument());
			json.put(IJsonNames.REPRESENTATION, c.getCertificateInfo().getEnterprise());
			if (c.getCertificateInfo().getToDate() != null && c.getCertificateInfo().getFromDate() != null) {
				json.put(IJsonNames.END_DATE, c.getCertificateInfo().getToDate().getTime());
				json.put(IJsonNames.START_DATE, c.getCertificateInfo().getFromDate().getTime());
			}
		}
		json.put(IJsonNames.TYPE, c.getConfidential());
		json.put(IJsonNames.ALIAS, c.getDescription());	
		json.put(IJsonNames.OWNER, c.getOwner());
		for(int i = 0; i < c.getTags().size(); i++) {
			if(c.getTags().get(i).name().equals("SEPE")) json.put("sepe", true);
			if(c.getTags().get(i).name().equals("TGSS")) json.put("tgss", true);
			if(c.getTags().get(i).name().equals("AEAT")) json.put("aeat", true);
		}
		return json;
	}
	
	private static JSONObject sUserToJSON(SecondaryUser su) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.NAME, su.getName());
		json.put(IJsonNames.NUMBER, su.getNaf());
		json.put(IJsonNames.STATUS, su.getSituation());
		json.put(IJsonNames.DATE, su.getSituationDate());
		return json;
	}
}
