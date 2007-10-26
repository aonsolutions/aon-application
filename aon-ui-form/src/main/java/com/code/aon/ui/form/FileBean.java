package com.code.aon.ui.form;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.mime.Magic;
import com.code.aon.common.mime.MagicException;
import com.code.aon.common.mime.MagicMatch;
import com.code.aon.common.mime.MagicMatchNotFoundException;
import com.code.aon.common.mime.MagicParseException;
import com.code.aon.ui.fileupload.IFileUploadListener;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;

/**
 * Bean for files.
 */
public class FileBean extends AttachmentController implements IFileUploadListener {

    private static final Logger LOGGER = Logger.getLogger(FileBean.class.getName());

    private static final String DEFAULT_UPLOAD_URL = "/facelet/fileUpload/form.faces";

    private static final String DEFAULT_DOWNLOAD_URL = "/facelet/fileUpload/download.faces";

    private String downloadURL;

    private String blobProperty;

    private String fileNameProperty;

    private String blobControllerName;

    private BasicController blobController;

    private IControllerListener controllerListener;

    /**
     * Constructor.
     */
    public FileBean() {
        this.controllerListener = new FileBeanListener();
        setUploadURL(DEFAULT_UPLOAD_URL);
        setDownloadURL(DEFAULT_DOWNLOAD_URL);
    }

    /**
     * Return download URL encoded
     * 
     * @return String
     */
    public String getEncodedDownloadURL() {
        return getEncodedURL(this.downloadURL);
    }

    /**
     * Set download URL
     * 
     * @param downloadURL
     */
    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    /**
     * Set name of blob controller
     * 
     * @param blobControllerName
     */
    public void setBlobControllerName(String blobControllerName) {
        this.blobControllerName = blobControllerName;
    }

    /**
     * Return controllerListener
     * 
     * @return IControllerListener
     */
    public IControllerListener getControllerListener() {
        return controllerListener;
    }

    /**
     * Set blob property
     * 
     * @param property
     */
    public void setBlobProperty(String property) {
        this.blobProperty = property;
    }

    /**
     * Set file name property
     * 
     * @param nameProperty
     */
    public void setFileNameProperty(String nameProperty) {
        this.fileNameProperty = nameProperty;
    }

    /**
     * Get blob controller
     * 
     * @return BasicController
     */
    private BasicController getBlobController() {
        if (this.blobController == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ValueBinding vb = ctx.getApplication().createValueBinding("#{" + blobControllerName + "}");
            this.blobController = (BasicController) vb.getValue(ctx);

        }
        return this.blobController;
    }

    private void setValue(Object bean, String name, Object value) {
        try {
            PropertyUtils.setProperty(bean, name, value);
        } catch (Throwable th) {
            LOGGER.log(Level.SEVERE, "Error setting property " + name + " with value " + value + " from " + bean, th);
        }
    }

    private UIForm getParentForm(UIComponent component) {
        UIComponent parent = component;
        while (parent != null) {
            if (parent.getFamily().equals(UIForm.COMPONENT_FAMILY)) {
                return (UIForm) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    private UIInput getInput(UIComponent component, String property) {
        String pojoName = ClassUtils.getShortClassName(getBlobController().getPojo());
        String alias = pojoName + "_" + property;
        return (UIInput) getParentForm(component).findComponent(alias);
    }

    private Object getValue(Object bean, String name) {
        try {
            return PropertyUtils.getProperty(bean, name);
        } catch (Throwable th) {
            LOGGER.log(Level.SEVERE, "Error getting property " + name + " from " + bean, th);
        }
        return null;
    }

    @Override
    public void upload(String name, String mimeType, byte[] data) {
        ITransferObject to = getBlobController().getTo();
        setValue(to, this.blobProperty, data);
        if (this.fileNameProperty != null) {
            File file = new File(name);
            setValue(to, this.fileNameProperty, file.getName());
        }
    }

    public void onRemove(ActionEvent e) {
        ITransferObject to = getBlobController().getTo();
        setValue(to, this.blobProperty, null);
        if (this.fileNameProperty != null) {
            setValue(to, this.fileNameProperty, null);
            UIInput input = getInput(e.getComponent(), this.fileNameProperty);
            input.setSubmittedValue(null);
        }
    }

    private ITransferObject getCurrentTo() throws ManagerBeanException {
        DataModel model = getBlobController().getModel();
        if (model.isRowAvailable()) {
            return (ITransferObject) model.getRowData();
        }
        return null;
    }

    /**
     * Return description
     * 
     * @return String
     * @throws ManagerBeanException
     */
    public String getDescription() throws ManagerBeanException {
        String result = null;
        ITransferObject to = getCurrentTo();
        if (this.fileNameProperty != null) {
            Object value = getValue(to, this.fileNameProperty);
            if (value != null) {
                result = value.toString();
            }
        }
        if (result == null) {
            byte[] data = (byte[]) getValue(to, this.blobProperty);
            result = (data != null ? data.length : 0) + " bytes";
        }
        return result;
    }

    /**
     * Return data
     * 
     * @return byte[]
     */
    public byte[] getData() {
        ITransferObject to = getBlobController().getTo();
        return (byte[]) getValue(to, this.blobProperty);
    }
    
    private String getFileNameFromTo() {
        ITransferObject to = getBlobController().getTo();
        if (this.fileNameProperty != null) {
            return (String) getValue(to, this.fileNameProperty);
        }
        return null;
    }

    /**
     * Return name of file
     * 
     * @return String
     */
    public String getFileName() {
    	return StringUtils.defaultString(getFileNameFromTo(), calculateFileName());
    }

    /**
     * Return download file name
     * 
     * @return String
     */
    public String getDownloadFileName() {
        String name = getFileName();
        if (name == null) {
            name = this.blobProperty;
        }
        return name;
    }

    private MagicMatch calculateMimeType() {
        MagicMatch match = null;
        try {
            match = Magic.getMagicMatch(getData());
        } catch (MagicParseException e) {
            LOGGER.log(Level.SEVERE, "Error finding mime type", e);
        } catch (MagicMatchNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error finding mime type", e);
        } catch (MagicException e) {
            LOGGER.log(Level.SEVERE, "Error finding mime type", e);
        }
        return match;
    }
    
    private String calculateFileName() {
    	String name = StringUtils.defaultString(getBeanName(), "file");
    	String extension = null;
    	MagicMatch match = calculateMimeType();
    	if ( match != null ) {
    		extension = match.getExtension();
    	}
    	if ( extension == null ) {
    		MimeType type = MimeType.get(match.getMimeType());
    		if ( type != null ) {
    			extension = type.getExtension();
    		}
    	}
    	if ( extension != null ) {
    		name += "." + extension;
    	}
    	return name;
    }

    /**
     * Return download mime type
     * 
     * @return String
     */
    public String getDownloadMimeType() {
        String mimeType = null;
        String name = getFileNameFromTo();
        if (name != null) {
            MimeType type = MimeType.getByExtension(getExtension(name));
            if (type != null) {
                mimeType = type.getName();
            }
        }
        if (mimeType == null) {
        	MagicMatch match = calculateMimeType();
        	if ( match != null ) {
                mimeType = match.getMimeType();        		
        	}
        }
        return mimeType;
    }

    private class FileBeanListener extends ControllerAdapter {

        private String fileName;

        private byte[] data;

        @Override
        public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
            ITransferObject to = event.getController().getTo();
            this.data = (byte[]) getValue(to, blobProperty);
            if (fileNameProperty != null) {
                this.fileName = (String) getValue(to, fileNameProperty);
            }
        }

        @Override
        public void beforeBeanCanceled(ControllerEvent event) throws ControllerListenerException {
            ITransferObject to = event.getController().getTo();
            setValue(to, blobProperty, this.data);
            if (fileNameProperty != null) {
                setValue(to, fileNameProperty, this.fileName);
            }
        }

    }

}
