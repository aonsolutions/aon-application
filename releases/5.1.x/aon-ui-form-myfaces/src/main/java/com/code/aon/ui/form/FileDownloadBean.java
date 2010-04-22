package com.code.aon.ui.form;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

/**
 * Bean for downloading files.
 */
public class FileDownloadBean {

    private static final Logger LOGGER = Logger.getLogger(FileDownloadBean.class.getName());

    private String controllerName;

    /**
     * Return name of controller.
     * 
     * @return String
     */
    public String getControllerName() {
        return controllerName;
    }

    /**
     * Set name of controller.
     * 
     * @param controllerName
     */
    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    /**
     * Return the bean of file.
     * 
     * @return FileBean
     */
    private FileBean getFileBean() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{" + this.controllerName + "}");
        return (FileBean) vb.getValue(ctx);
    }

    /**
     * Execute downloading action.
     * 
     * @param event
     */
    @SuppressWarnings("unused")
    public void onDownload(ActionEvent event) {
        try {
            LOGGER.info("controllerName=" + this.controllerName);
            FileBean fileBean = getFileBean();
            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.setContentType(fileBean.getDownloadMimeType());
            byte[] data = fileBean.getData();
            response.setContentLength(data.length);
            response.setHeader("Content-disposition", "inline; filename=\"" + fileBean.getFileName() + "\"");
            OutputStream out = response.getOutputStream();
            out.write(data);
            out.close();
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.responseComplete();
        } catch (Throwable th) {
            LOGGER.log(Level.SEVERE, "Error generation Report", th);
        }
    }

}
