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

import es.inteco.xbrl.pgc.wsclient.transform.PGCWSTransformatorProxy;
import es.inteco.xbrl.pgc.wsclient.transform.TransformResult;

public class TransformToXbrlAction extends Action
{

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
	TransformationToXbrlForm myForm = (TransformationToXbrlForm) form;

	FormFile myFile = myForm.getTheFile();
	byte[] fileData = myFile.getFileData();

	ByteArrayOutputStream os = new ByteArrayOutputStream();
	try
	{
	    //obtengo la hora
	    Calendar calendario = Calendar.getInstance();
	    
	    int hora =calendario.get(Calendar.HOUR_OF_DAY);
	    int minutos = calendario.get(Calendar.MINUTE);
	    int segundos = calendario.get(Calendar.SECOND);
	    System.out.println("Conversion to XBRL starts: " + hora + ":" + minutos + ":" + segundos);

	    PGCWSTransformatorProxy service = new PGCWSTransformatorProxy();
	    TransformResult xbrlResult = service.transformToXbrl(fileData, myForm.getValidateXbrl());

	    calendario = Calendar.getInstance();
	    hora =calendario.get(Calendar.HOUR_OF_DAY);
	    minutos = calendario.get(Calendar.MINUTE);
	    segundos = calendario.get(Calendar.SECOND);
	    System.out.println("Conversion to XBRL ends: " + hora + ":" + minutos + ":" + segundos);

	    if (xbrlResult.getArrayResult() != null)
	    {
		os.write(xbrlResult.getArrayResult());
		myForm.setXbrlString(os.toString("UTF-8"));
	    }
	    if (myForm.getValidateXbrl()){
		myForm.setValidateErrors("Successfully validated ");
	    }
	    else
	    {
		myForm.setValidateErrors("Validation not request");
	    }
	    if (xbrlResult.getGeneralError()!= null)
	    {
		myForm.setGeneralErrors(xbrlResult.getGeneralError());
	    }
	    if (xbrlResult.getXbrlValidateError() != null)
	    {
		myForm.setValidateErrors(xbrlResult.getXbrlValidateError());
	    }
	    if (xbrlResult.getXsdValidateError() != null)
	    {
		myForm.setValidateErrors(xbrlResult.getXsdValidateError());
	    }
	    
	} catch (Throwable e)
	{
	    myForm.setGeneralErrors(e.getMessage());
	    System.out.println("Error transform to XBRL: " + e.getMessage());
	    
	}

	return mapping.findForward("responseXbrl");

    }
}
