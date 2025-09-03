package com.code.aon.web.help.servlet;

import static com.esferalia.aon.watson.util.AonStringUtils.containsIgnoreCase;
import static com.esferalia.aon.watson.util.AonStringUtils.containsMatching;
import static com.esferalia.aon.watson.util.AonStringUtils.isNotBlank;
import static com.esferalia.aon.watson.util.AonStringUtils.substringBefore;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.ui.help.HelpData;
import com.code.aon.ui.help.pdf.PdfSearcher;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiHelpServlet", urlPatterns = {"/ms/api/help/*"})
public class HelpSearch extends AonApiHttpServlet {

	public static final String ROOT = "/";
	private static final Logger LOGGER  = Logger.getLogger(HelpSearch.class.getName());
	
	private static List<HelpData> helpDatas;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req, false);
			
			Object object = new AonRouting(api)
				.addRoute(ROOT, HelpSearch::getHelpDatas)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
		
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		doGet(req, resp);
	}
	
	private static JSONArray getHelpDatas(AonApiData api) {
		int limit = api.getData().optInt(IJsonNames.LIMIT, 25);
		String pattern = api.getData().optString(IJsonNames.PATTERN);
		JSONObject[] helpJsonDatas =  getHelpDatas(pattern, limit).stream().map(HelpSearch::toJSON).toArray(JSONObject[]::new);
		return new JSONArray().putAll(helpJsonDatas);
	}

	private static JSONObject toJSON(HelpData helpData) {
		return 
				new JSONObject()
				.put("uri", helpData.getURI())
				.put("title", helpData.getTitle())
				.put("file", Paths.get(substringBefore(helpData.getURI(), "#")).getFileName())
				;
	}
 	
	private static HelpData toHelpData( PDOutlineItem item) {

		HelpData helpData = 
		new HelpData()
		.setTitle(item.getTitle());
		
		try {
			PDActionURI actionURI = (PDActionURI)item.getAction();
			helpData.setURI(actionURI.getURI());
		} catch ( Exception e ) {
		}
		
		return helpData;
	
	}

	private static List<HelpData> getHelpDatas(String pattern, int pageLimit) {
		if (StringUtils.isBlank(pattern))
			return getHelpDatas();

		List<HelpData> filteredList = getHelpDatas().stream().filter(d -> containsIgnoreCase(d.getTitle(), pattern))
				.limit(pageLimit).collect(Collectors.toList());

		// if nothing is here
		if (filteredList.isEmpty()) {
			filteredList.addAll(getHelpDatas().stream().filter(d -> containsMatching(d.getTitle(), pattern.trim()))
					.limit(pageLimit).toList());
		}

		return filteredList;
	}
	
	private static synchronized List<HelpData> getHelpDatas() {
		if ( HelpSearch.helpDatas == null ) {
			
			try  ( InputStream is = PdfSearcher.class.getResourceAsStream("index.pdf");
			   PDDocument document = Loader.loadPDF(is.readAllBytes()) ) {
				
				return 
				PdfSearcher.search(document, "").stream()
				.filter( i -> isNotBlank(i.getTitle()))
				.map(HelpSearch::toHelpData)
				.toList();
						
			} catch (IOException e) {
				HelpSearch.helpDatas = Collections.emptyList();
			}
		}
		
		return HelpSearch.helpDatas;
	}
	


}
