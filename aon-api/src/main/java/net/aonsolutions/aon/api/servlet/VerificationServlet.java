package net.aonsolutions.aon.api.servlet;

import java.io.StringWriter;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@WebServlet(name = "AonVerificationServlet", urlPatterns = {"/ms/api/verification/*"})
public class VerificationServlet extends AonApiHttpServlet {

    private static final Logger LOGGER  = Logger.getLogger(VerificationServlet.class.getName());

    public static final String SEND_VERIFICATION = "/send";
    public static final String CHECK_VERIFICATION = "/check";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        error(req, resp, new AonApiException(AonApiError.METHOD_NOT_SUPPORTED.getMessage()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);
            Object object = new AonRouting(api)
                    .addRoute(SEND_VERIFICATION, VerificationServlet::sendVerification)
                    .addRoute(CHECK_VERIFICATION, VerificationServlet::checkVerification)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        error(req, resp, new AonApiException(AonApiError.METHOD_NOT_SUPPORTED.getMessage()));
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        error(req, resp, new AonApiException(AonApiError.METHOD_NOT_SUPPORTED.getMessage()));
    }
    
    private static JSONObject sendVerification(AonApiData api) {
        Auth auth = AuthJSON.fromJSON(api.getData()); 
        Integer code = new Random().nextInt(999999);

        sendEmail(auth, code.toString());
        JSONObject object = new JSONObject();
        object.put(IJsonNames.CODE, code.toString());
       
        JSONObject response = new JSONObject();
        response.put(IJsonNames.TOKEN, AonToken.build(object, AonDateUtils.addMinutes(new Date(), 15) ));
        return response;
    }
    
    private static JSONObject checkVerification(AonApiData api) {
        String token = JsonUtils.getString(api.getData(), IJsonNames.TOKEN);
        JSONObject json = SECURITY.decodeJWT(token);
        String code = JsonUtils.getString(json, IJsonNames.CODE);
        String value = JsonUtils.getString(api.getData(), IJsonNames.VALUE);
        JSONObject response = new JSONObject();
        response.put(IJsonNames.RESULT, !AonStringUtils.isBlank(code) && !AonStringUtils.isBlank(value) && code.equalsIgnoreCase(value));
        return response;
    }
    
    private static void sendEmail(Auth auth, String code) {
        SESMessage msg = new SESMessage()
            .setTo(auth.getEmail())
            .setSubject("CÓDIGO DE VERIFICACIÓN - AON SOLUTIONS")
            .setBody(getContent(auth, code));
        SES.sendEmail(msg);
    }
    
    private static String getContent(Auth auth, String code) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        
        VelocityContext context = new VelocityContext();
        context.put("name", auth.getName());
        context.put("code", code);
        
        Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/verification.vm");
        
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }
    
}
