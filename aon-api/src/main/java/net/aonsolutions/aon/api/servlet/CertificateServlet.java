package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CertificateJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.CertificateProperties;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonCertificateServlet", urlPatterns = {"/ms/api/cert/*",
														  "/aon_gwt_aio/ms/api/cert/*",
														  "/aon_gwt_fiscal/ms/api/cert/*"})
public class CertificateServlet extends AonApiHttpServlet {
	
	private static final Logger LOGGER  = Logger.getLogger(CertificateServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			LOGGER.info("[GET] /ms/api/cert/ - Cetificate Servlet");
			AonApiData api = initialize(req, false);
			response(req, resp, getCertificates(api));
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getCertificates(AonApiData api) {
		return CertificateJSON.toJSON(
			AON.getCertificates(api.getDomain(), api.getUser(), f -> certificateFilter(api, f))
		);
	}
	
	public static Filter certificateFilter(AonApiData api, CertificateProperties f) {
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		
		Filter filter;
		if(api.getDomain().getParentId() != null) {
			if(!api.getUser().getDomain().equals(api.getDomain().getParentId())) {
				filter = (f.getDomainProperty().eq(api.getDomain().getId()).or(
						f.getDomainProperty().eq(api.getDomain().getParentId())
						.and(f.getSecurityLevelProperty().eq(SecurityLevel.OFFICIAL.value())))
					);
			} else {
				Integer[] domains = {api.getDomain().getId(), api.getDomain().getParentId()};
				filter = f.getDomainProperty().in(domains);
			}
		} else filter = f.getDomainProperty().eq(api.getDomain().getId());
    	
		if(api.getUser().getRegistry() != null && api.getDomain().getParentId() != null) {
			Company parentCompany = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getParentId(), api.getUser().getLogin());
			Integer[] registries = {api.getUser().getRegistry().getId(), company.getId(), parentCompany.getId()};
			filter = filter.and(f.getRegistryProperty().in(registries));
		} else if(api.getUser().getRegistry() != null) {
			Integer[] registries = {api.getUser().getRegistry().getId(), company.getId()};
			filter = filter.and(f.getRegistryProperty().in(registries));
		} else if(api.getDomain().getParentId() != null) {
			Company parentCompany = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getParentId(), api.getUser().getLogin());
			Integer[] registries = {company.getId(), parentCompany.getId()};
			filter = filter.and(f.getRegistryProperty().in(registries));
		} else filter = filter.and(f.getRegistryProperty().eq(company.getId()));
		
		
		if(api.getData().opt(IJsonNames.TYPE) != null) {
			String type = JsonUtils.getString(api.getData(), IJsonNames.TYPE);
			CertificateType certificateType = CertificateType.safeValueOf(type);
			if(certificateType != null)
				filter = filter.and(f.getTypeProperty().eq(certificateType.name()).or(f.getTypeProperty().isNull()));
		}
		
    	return filter;
    }
	
}
