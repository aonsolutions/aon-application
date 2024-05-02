package net.aonsolutions.aon.api.servlet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.Certificate.CertificateSecurity;
import com.esferalia.aon.occam.api.model.CertificateInfo;

import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.sepe.Sepe;
import solutions.aon.sepe.exceptions.SepeException;

@SuppressWarnings("serial")
@WebServlet(name = "NewCertificateServlet", urlPatterns = {"/ms/api/certificate/*"})
public class NewCertificateServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(NewCertificateServlet.class.getName());
	
	public static final String CERTIFICATES = "/";
	public static final String CERTIFICATE_ONE = "/one";
	public static final String CERTIFICATE_VERIFY = "/verify";
	
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
				Certificate certificate = AON.getCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");
				InputStream certificateIS = new ByteArrayInputStream(certificate.getData());
				SistemaRED.validateCert(certificateIS, certificate.getPassword(), certificate.getType());
			} catch(SegSocialException e) {
				throw new AonApiException(e.getMessage());
			}
		}
		if(cert.getTags().contains(CertificateType.SEPE)) {
			try {
				Certificate certificate = AON.getCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), "SEPE");
				InputStream certificateIS = new ByteArrayInputStream(certificate.getData());
				Sepe.validateCert(certificateIS, certificate.getPassword(), certificate.getType());	
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
	
	private static JSONObject postAction(AonApiData api) {
		String owner = api.getData().optString(IJsonNames.OWNER);
		String confidential = api.getData().optString(IJsonNames.CONFIDENTIAL);
		String pass = api.getData().optString(IJsonNames.PASSWORD);
		String data = api.getData().optString(IJsonNames.FILE_DATA);
		String filename = api.getData().optString(IJsonNames.FILE_NAME);
		Boolean aeat = api.getData().optBoolean("aeat");
		Boolean sepe = api.getData().optBoolean("sepe");
		Boolean tgss = api.getData().optBoolean("tgss");
		byte[] dataFile = Base64.getDecoder().decode(data);
		List<CertificateType> list = new ArrayList<>();
		if(Boolean.TRUE.equals(aeat)) list.add(CertificateType.AEAT);
		if(Boolean.TRUE.equals(tgss)) list.add(CertificateType.TGSS);
		if(Boolean.TRUE.equals(sepe)) list.add(CertificateType.SEPE);
		Certificate certificate = new Certificate()
				.setDescription(filename)
				.setDomain(api.getDomain().getId())
				.setData(dataFile)
				.setConfidential(CertificateSecurity.valueOf(confidential))
				.setOwner(CertificateOwner.valueOf(owner))
				.setPassword(pass)
				.setTags(list);
		checkCertificateType(api, certificate);
		AON.saveCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), certificate);
		return new JSONObject();
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
		json.put(IJsonNames.NAME, c.getCertificateInfo().getName());
		json.put(IJsonNames.END_DATE, c.getCertificateInfo().getToDate());
		json.put(IJsonNames.REPRESENTATION, c.getCertificateInfo().getEnterprise());
		json.put(IJsonNames.TYPE, c.getConfidential());
		json.put(IJsonNames.ALIAS, c.getDescription());
		json.put(IJsonNames.START_DATE, c.getCertificateInfo().getFromDate());
		json.put(IJsonNames.DOCUMENT, c.getCertificateInfo().getDocument());
		json.put(IJsonNames.OWNER, c.getOwner());
		for(int i = 0; i < c.getTags().size(); i++) {
			if(c.getTags().get(i).name().equals("SEPE")) json.put("sepe", true);
			if(c.getTags().get(i).name().equals("TGSS")) json.put("tgss", true);
			if(c.getTags().get(i).name().equals("AEAT")) json.put("aeat", true);
		}
		return json;
	}
}
