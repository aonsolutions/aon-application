package net.aonsolutions.aon.api.servlet.news;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.NewsJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
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
		
		News news = AON_SOLUTIONS.saveNews(api.getDomain(), api.getUser(), NewsJSON.fromJSON(params));
		
		return NewsJSON.toJSON(news);
	}

	private JSONObject deleteNew(AonApiData api) {
		
		News news = NewsJSON.fromJSON(api.getData());
		
		AON_SOLUTIONS.deleteNews(news.getDomain(), api.getUser(), news.getId());
		
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
}
