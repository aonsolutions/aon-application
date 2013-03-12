package es.inteco.xbrl.pgc.client.web;



import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import es.inteco.xbrl.pgc.wsclient.validator.ValidateResult;
import es.inteco.xbrl.pgc.wsclient.validator.XBRLWSValidatorProxy;

public class XBRLValidationAction extends Action
{

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
	XBRLValidationForm myForm = (XBRLValidationForm) form;

	// Process the FormFile
	FormFile myFile = myForm.getTheFile();
	byte[] fileData = myFile.getFileData();

	try
	{
	    //obtengo la hora
	    Calendar calendario = Calendar.getInstance();
	    
	    int hora =calendario.get(Calendar.HOUR_OF_DAY);
	    int minutos = calendario.get(Calendar.MINUTE);
	    int segundos = calendario.get(Calendar.SECOND);
	    System.out.println("XBRL validation starts: " + hora + ":" + minutos + ":" + segundos);

	    XBRLWSValidatorProxy service = new XBRLWSValidatorProxy();
	    ValidateResult result = service.validate(fileData );

	    calendario = Calendar.getInstance();
	    hora =calendario.get(Calendar.HOUR_OF_DAY);
	    minutos = calendario.get(Calendar.MINUTE);
	    segundos = calendario.get(Calendar.SECOND);
	    System.out.println("xBRL validation ends: " + hora + ":" + minutos + ":" + segundos);

	    if (result != null)
	    {
		myForm.setResultString(result.getErrors());
	    }

	} catch (Throwable e)
	{
	    System.out.println("Error validation XBRL: " + e.getMessage());
	    myForm.setResultString("Error validation XBRL: " + e.getMessage());
	    e.printStackTrace();	    
	}

	return mapping.findForward("responseXbrlValidation");

    }
}
