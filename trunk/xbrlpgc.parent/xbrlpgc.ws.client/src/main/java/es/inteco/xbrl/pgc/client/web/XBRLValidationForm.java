package es.inteco.xbrl.pgc.client.web;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class XBRLValidationForm extends ActionForm
{
  
    private static final long serialVersionUID = 1L;
    private FormFile theFile;
    private String resultString ="Empty";

    
    
    public final String getResultString()
    {
	return resultString;
    }
    
    public final void setResultString(String xbrlString)
    {
	this.resultString = xbrlString;
    }
    
   

    /**
     * @return Returns the theFile.
     */
    public FormFile getTheFile() {
      return theFile;
    }
    /**
     * @param theFile The FormFile to set.
     */
    public void setTheFile(FormFile theFile) {
      this.theFile = theFile;
    }




}
