package es.inteco.xbrl.pgc.client.web;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;


import es.inteco.xbrl.pgc.wsclient.viewer.PGCWSViewerProxy;
import es.inteco.xbrl.pgc.wsclient.viewer.ViewerResult;

public class ViewerAction extends Action
{

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
	ViewerForm myForm = (ViewerForm) form;

	FormFile myFile = myForm.getTheFile();
	
	

	if (myForm.getHtmlString() != null)
	{
	    request.setAttribute("viewerForm", myForm);
	    return mapping.findForward("preView");
	}
	else
	{
	    byte[] fileData = myFile.getFileData();
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    try
	    {
		PGCWSViewerProxy service = new PGCWSViewerProxy();
	ViewerResult htmlResult = null;
		if (myFile.getFileName().toLowerCase().endsWith("xbrl"))
		{
		    //obtengo la hora
		    Calendar calendario = Calendar.getInstance();
		    
		    int hora =calendario.get(Calendar.HOUR_OF_DAY);
		    int minutos = calendario.get(Calendar.MINUTE);
		    int segundos = calendario.get(Calendar.SECOND);
		    System.out.println("XBRL visualization starts: " + hora + ":" + minutos + ":" + segundos);

		    htmlResult = service.view(fileData, myForm.getModule());
		    
		    calendario = Calendar.getInstance();
		    hora =calendario.get(Calendar.HOUR_OF_DAY);
		    minutos = calendario.get(Calendar.MINUTE);
		    segundos = calendario.get(Calendar.SECOND);
		    System.out.println("XBRL visualization ends: " + hora + ":" + minutos + ":" + segundos);

		}
		else
		{
		    //obtengo la hora
		    Calendar calendario = Calendar.getInstance();
		    
		    int hora =calendario.get(Calendar.HOUR_OF_DAY);
		    int minutos = calendario.get(Calendar.MINUTE);
		    int segundos = calendario.get(Calendar.SECOND);
		    System.out.println("XML visualization starts: " + hora + ":" + minutos + ":" + segundos);

		    htmlResult = service.viewXML(fileData, myForm.getModule());
		    
		    calendario = Calendar.getInstance();
		    hora =calendario.get(Calendar.HOUR_OF_DAY);
		    minutos = calendario.get(Calendar.MINUTE);
		    segundos = calendario.get(Calendar.SECOND);
		    System.out.println("XML visualization ends: " + hora + ":" + minutos + ":" + segundos);

		}
	    
		if (htmlResult.getArrayResult() != null)
		{
		    os.write(htmlResult.getArrayResult());
		    myForm.setHtmlString(os.toString("UTF-8"));
		}
		myForm.setErrors(htmlResult.getGeneralError());


	    } catch (Throwable e)
	    {
		myForm.setErrors(e.getMessage());
		System.out.println("Error viewer: " + e.getMessage());

	    }


	    return mapping.findForward("view");
	}

    }
}
