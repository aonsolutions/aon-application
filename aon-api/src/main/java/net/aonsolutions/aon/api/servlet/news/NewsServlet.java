package net.aonsolutions.aon.api.servlet.news;

import java.util.Optional;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.AttachJSON;
import com.esferalia.aon.occam.api.json.NewsJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.security.User;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "NewsServlet", urlPatterns = {"/ms/api/news/*"})
public class NewsServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(NewsServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON NEW SERVLET GET");
		try {		
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
					response(req, resp, getNews(api));
					break;
				case "/one":
					response(req, resp, getNew(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("AON NEW SERVLET POST");
		try {		
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
					response(req, resp, saveNew(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API NEW SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteNew(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getNews(AonApiData api) {
		JSONObject params = api.getData();
		Domain domain     = api.getDomain();
		User user         = api.getUser();
		
		Integer page      = params.optInt(IJsonNames.PAGE);
		Integer perPage   = params.optInt(IJsonNames.PER_PAGE);
		
		return NewsJSON.toJSON(
				AON_SOLUTIONS.getNewsStream(domain, user, 
					f->NewsFilter.filter(api, f, domain),
					page, perPage
				)
		);
	}

	private JSONObject getNew(AonApiData api) {
		JSONObject params = api.getData();
		Domain domain     = api.getDomain();
		User user         = api.getUser();

		
		Integer id        = params.optInt(IJsonNames.ID);

		return NewsJSON.toJSON(
			AON_SOLUTIONS.getNews(domain, user, 
				f->f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq(id))
			)
		);
	}

	
	private JSONObject saveNew(AonApiData api) {
		JSONObject params = api.getData();
		
		validateSave(params);
		
		News news = NewsJSON.fromJSON(params);
		
		Integer rattachId = saveAttach(api, news.getRattach());

		news.setRattach(rattachId);

		return NewsJSON.toJSON(
			AON_SOLUTIONS.saveNews(api.getDomain(), api.getUser(), news)
		);
	}

	private JSONObject deleteNew(AonApiData api) {
		
		News news = NewsJSON.fromJSON(api.getData());
		
		AON_SOLUTIONS.deleteNews(news.getDomain(), api.getUser(), news);
		
		return new JSONObject();
	}
	
	
	private void validateSave(JSONObject params) {
		if(params.isNull(IJsonNames.SCOPE)) {
			throw new AonApiException("Ambito requerido");
		} else if(params.isNull(IJsonNames.TYPE)) {
			throw new AonApiException("Tipo requerido");
		} else if(params.isNull(IJsonNames.TITLE)) {
			throw new AonApiException("T\u00edtulo requerido");
		}
	}
	
	private Integer saveAttach(AonApiData api, Optional<Integer> rattach) {
		Domain domain = api.getDomain();
		String login  = api.getUser().getLogin();
		JSONObject params = api.getData();
		
		JSONObject rattachJson = params.optJSONObject(IJsonNames.ATTACH);
		
		Integer rattachId = rattach.isPresent() ? rattach.get() : null;
		
		if(params.isNull(IJsonNames.ATTACH) && rattachId!=null) {
			rattachId = null;
		} else if(rattachJson!= null && !rattachJson.isNull(IJsonNames.CONTENT)) {
			Attach attach = AttachJSON.fromJSON(rattachJson);
			if(attach!=null && !attach.isEmpty()) {
				Company company = AON.getCompany(domain.getName(), domain.getId(), login, f->f.getDomainProperty().eq(domain.getId()));
				if(company.getId()!=null) {
					attach.setAttachModule(company.getId());
					
					String description = attach.getDescription();
					if(description.length()>64) {
						description = description.substring(0,64);
					}
							
					attach.setDescription(description);
					
					rattachId = AON.insertAttach(domain.getName(), domain.getId(), login, attach);
				}
			}
		}
		
		return rattachId;
	}
}
