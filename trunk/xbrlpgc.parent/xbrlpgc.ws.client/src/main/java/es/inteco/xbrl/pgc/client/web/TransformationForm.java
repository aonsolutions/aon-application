package es.inteco.xbrl.pgc.client.web;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class TransformationForm extends ActionForm
{
    private FormFile theFile;
    private String validateErrors = "";
    private String generalErrors = "";
    private boolean validateXbrl = false;
    
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
    
    
    public final String getValidateErrors()
    {
        return validateErrors;
    }

    public final void setValidateErrors(String validateErrors)
    {
        this.validateErrors = validateErrors;
    }
    
    public final boolean getValidateXbrl()
    {
        return validateXbrl;
    }

    public final void setValidateXbrl(boolean validateXbrl)
    {
        this.validateXbrl = validateXbrl;
    }
    public final String getGeneralErrors()
    {
        return generalErrors;
    }
    public final void setGeneralErrors(String generalErrors)
    {
        this.generalErrors = generalErrors;
    }
    
}
