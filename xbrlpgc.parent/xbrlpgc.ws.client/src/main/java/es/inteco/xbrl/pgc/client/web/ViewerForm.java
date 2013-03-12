package es.inteco.xbrl.pgc.client.web;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;



public class ViewerForm extends ActionForm
{
    
    private static final long serialVersionUID = 1L;
   
    private FormFile theFile;
    private String errors = "";
    private String module = null;
    private String htmlString = null;
    
    
    public final FormFile getTheFile()
    {
        return theFile;
    }
    public final void setTheFile(FormFile theFile)
    {
        this.theFile = theFile;
    }
    public final String getErrors()
    {
        return errors;
    }
    public final void setErrors(String errors)
    {
        this.errors = errors;
    }
    public final String getModule()
    {
        return module;
    }
    public final void setModule(String module)
    {
        this.module = module;
    }

    public final String getHtmlString()
    {
        return htmlString;
    }
    public final void setHtmlString(String htmlString)
    {
        this.htmlString = htmlString;
    }
    
}
