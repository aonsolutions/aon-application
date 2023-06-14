package com.code.aon.web.help.servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.news.NewsType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ms/news")
public class NewsServlet extends HttpServlet {


    public static final String USER_PARAM = "user"; 
    public static final String TIME_PARAM = "time"; 
    
    public static final String NEWS_URL = "news.url"; 
    public static final String NEWS_USER = "news.user"; 

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	String url = getUrl(req);
	String login = req.getParameter(USER_PARAM);
	Domain domain = getDomain(req.getServerName(), login);
	User user = getUser(domain, login);
	
	Timestamp time = parseTime(AonStringUtils.defaultIfBlank(req.getParameter(TIME_PARAM), "1970-01-01-00:00:00") );
	
	Timestamp now = new Timestamp(new Date().getTime());
	
	News[] news = 
	AON_SOLUTIONS.getNewsStream(domain, user, 
		f -> f.getDomainProperty().eq(domain.getId()) 						// Same domain  
		  .and(f.getCategoryProperty().isNotNull())						// ¿ Category not null ?
		  .and(f.getTypeProperty().eq(NewsType.COMMUNICATION.value()))				// Must be of type 'COMMUNICATION'
		  .and(f.getInitDateProperty().isNotNull().and(f.getInitDateProperty().le(now)))	// Start date not null and lower than today, skip future news
		  .and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(now)))		// End date null or higher than today, skip old news  
		  .and(f.getInitDateProperty().gt(time))
	)
	.map( n -> n.setUrl(url) )
	.toArray(News[]::new);
	
	ByteArrayOutputStream byteArrayOs = new ByteArrayOutputStream();
	try ( ObjectOutputStream os =  new ObjectOutputStream(byteArrayOs) ){
	    os.writeObject(news);
	}
	
	resp.setContentLength(byteArrayOs.size());
	resp.setContentType("application/x-java-serialized-object");
	
	resp.getOutputStream().write(byteArrayOs.toByteArray());
	resp.getOutputStream().flush();
	resp.getOutputStream().close();
	
	
    }
    
    public static News[] getNews() throws IOException, ClassNotFoundException {
	String host = System.getProperty(NEWS_URL, "https://sig.aonsolutions.org");
	String user = System.getProperty(NEWS_USER, "admin");
	return getNews(host, user, new Date(0));
    }

    public static News[] getNews(String host, String user) throws IOException, ClassNotFoundException {
	return getNews(host, user, new Date(0));
    }

    public static News[] getNews(String host, String user, Date time) throws IOException, ClassNotFoundException {
	URL url = new URL(host 
		+ "/ms/news?" 
		+ USER_PARAM + "=" + user 
		+"&" + TIME_PARAM + "=" + formatTime(time)+""
		);

	HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	try ( ObjectInputStream is = new ObjectInputStream(connection.getInputStream())){
	    return (News[]) is.readObject();
	} finally {
	    connection.disconnect();
	}
    }
    
    private static String formatTime(Date date) {
	return TIME_FORMAT.format(date);
    }

    private static Timestamp parseTime(String date) throws ServletException {
	try {
	    return new Timestamp( TIME_FORMAT.parse(date).getTime());
	} catch (ParseException e) {
	    throw new ServletException(e);
	}
    }
    
    private static User getUser(Domain domain, String login)  throws ServletException {
	User user = AON.getUser(domain.getName(), domain.getId(), login);
	if ( user != null )
	    return user;
	throw new ServletException(String.format("User %s.%s not found", domain.getName() , login ));
    }

    private static Domain getDomain(String domainName, String login )  throws ServletException {
	try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, login)) {
	    return new Domain()
		    .setId(aonContext.getDomainId())
		    .setName(aonContext.getDomainName())
		    ;
	} catch ( Exception  e ) {
	    throw new ServletException(String.format("Domain %s not found", domainName));
	}
	
	
    }
    
    private static String getUrl( HttpServletRequest req) {
	String path = req.getServletPath();
	StringBuffer url = req.getRequestURL();
	return url.substring(0, url.lastIndexOf(path));
	
    }
    
    public static void main(String[] args) throws ClassNotFoundException, IOException {
	News[] news = getNews();
	for (int i = 0; i < news.length; i++) {
	    System.out.println(news[i].getCategory().getName() +" = " + news[i].getTitle() );
	    
	}
    }
    
    
    
}
