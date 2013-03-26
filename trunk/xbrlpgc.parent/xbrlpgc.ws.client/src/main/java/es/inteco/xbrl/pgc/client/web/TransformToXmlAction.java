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

public class TransformToXmlAction extends Action
{

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
	TransformationToXmlForm myForm = (TransformationToXmlForm) form;

	// Process the FormFile
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
	    System.out.println("Conversion to XML starts: " + hora + ":" + minutos + ":" + segundos);

	    PGCWSTransformatorProxy service = new PGCWSTransformatorProxy();
	    TransformResult xmlResult = service.transformToXml(fileData, myForm.getValidateXbrl());
	    
	    calendario = Calendar.getInstance();
	    hora =calendario.get(Calendar.HOUR_OF_DAY);
	    minutos = calendario.get(Calendar.MINUTE);
	    segundos = calendario.get(Calendar.SECOND);
	    System.out.println("Conversion to XML ends: " + hora + ":" + minutos + ":" + segundos);

	    if (xmlResult.getArrayResult() != null)
	    {
		os.write(xmlResult.getArrayResult());
		myForm.setXmlString(os.toString("UTF-8"));
	    }
	    if (myForm.getValidateXbrl()){
		myForm.setValidateErrors("Successfully validated ");
	    }
	    else
	    {
		myForm.setValidateErrors("Validation not request");
	    }
	    
	    if (xmlResult.getGeneralError()!= null)
	    {
		myForm.setGeneralErrors(xmlResult.getGeneralError());
	    }
	    if (xmlResult.getXbrlValidateError() != null)
	    {
		myForm.setValidateErrors(xmlResult.getXbrlValidateError());
	    }
	    if (xmlResult.getXsdValidateError() != null)
	    {
		myForm.setValidateErrors(xmlResult.getXsdValidateError());
	    }
	} catch (Throwable e)
	{
	    System.out.println("Error transform to XML: " + e.getMessage());
	    myForm.setValidateErrors(e.getMessage());
	}

	return mapping.findForward("responseXml");

    }
}
