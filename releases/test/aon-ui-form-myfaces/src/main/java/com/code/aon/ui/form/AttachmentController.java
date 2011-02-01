package com.code.aon.ui.form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.fileupload.IFileUploadListener;

/**
 * Controller use to handle attachments.
 */
public class AttachmentController extends BasicController implements IFileUploadListener {

	/** Obtains a suitable Logger. */
    private static final Logger LOGGER = Logger.getLogger(AttachmentController.class.getName());

    /** The attachment file. */
    private File attachmentFile;

    /** The current attachment. */
    private IAttachment currentAttachment;

    /** The attachment. */
    private IAttachment attachment;

    /** The upload URL. */
    private String uploadURL;

    /** The return URL. */
    private String returnURL;


    /**
     * Set the return URL.
     * 
     * @param returnURL the return URL
     */
    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    /**
     * Set the upload URL.
     * 
     * @param uploadURL the upload URL
     */
    public void setUploadURL(String uploadURL) {
        this.uploadURL = uploadURL;
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
     */
    @Override
    public void onAccept(ActionEvent event) {
        try {
            PropertyUtils.copyProperties(this.currentAttachment, this.attachment);
        } catch (IllegalAccessException e) {
            LOGGER.severe(e.getMessage());
        } catch (InvocationTargetException e) {
            LOGGER.severe(e.getMessage());
        } catch (NoSuchMethodException e) {
            LOGGER.severe(e.getMessage());
        }
        super.onAccept(event);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.BasicController#setTo(com.code.aon.common.ITransferObject)
     */
    @Override
    protected void setTo(ITransferObject value) {
        this.currentAttachment = (IAttachment) value;
        if (this.attachment != value) {
            IAttachment ia = (IAttachment) value;
            try {
                this.attachment = (IAttachment) ia.clone();
            } catch (CloneNotSupportedException e) {
                LOGGER.severe(e.getMessage());
            }
        }
        super.setTo(this.attachment);
    }

    private IAttachment getAttachment() {
        return (IAttachment) getTo();
    }

    private String getNameWithoutExtension(String value) {
        File file = new File(value);
        String name = file.getName();
        int pos = name.lastIndexOf('.');
        if (pos != -1) {
            name = name.substring(0, pos);
        }
        return name;
    }

    /**
     * Gets the extension.
     * 
     * @param value the value
     * 
     * @return the extension
     */
    protected String getExtension(String value) {
        File file = new File(value);
        String name = file.getName();
        int pos = name.lastIndexOf('.');
        if (pos != -1) {
            return name.substring(pos + 1);
        }
        return null;
    }

    private File createTempFile(IAttachment attachment) {
        File imageFile = null;
        try {
            imageFile = File.createTempFile("attachment", null);
            FileOutputStream out = new FileOutputStream(imageFile);
            out.write(attachment.getData());
            out.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating image temp file", e);
        }
        return imageFile;
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.fileupload.IFileUploadListener#upload(java.lang.String, java.lang.String, byte[])
     */
    public void upload(String name, String mimeType, byte[] data) {
        getAttachment().setDescription(getNameWithoutExtension(name));
        MimeType type = MimeType.get(mimeType);
        if (type == null) {
            type = MimeType.getByExtension(getExtension(name));
        }
        getAttachment().setMimeType(type);
        getAttachment().setData(data);
        getAttachment().setSize(new Integer(data.length));
        this.attachmentFile = createTempFile(getAttachment());
    }

    private String getAttachmentIdAlias() {
        String fullName = this.attachment.getClass().getName();
        String name = fullName.substring(fullName.lastIndexOf('.') + 1);
        return name + "_id";
    }

    /**
     * Gets the attachment parameters.
     * 
     * @return the attachment parameters
     */
    public String getAttachmentParameters() {
        StringBuffer sb = new StringBuffer();
        if (this.attachmentFile != null) {
            sb.append("file=");
            sb.append(this.attachmentFile);
            sb.append("&type=");
            sb.append(attachment.getMimeType().ordinal());
        } else {
            sb.append(getAttachmentIdAlias());
            sb.append('=');
            sb.append(attachment.getId());
        }
        return sb.toString();
    }

    /**
     * Checks if is with attachment.
     * 
     * @return true, if is with attachment
     */
    public boolean isWithAttachment() {
        return getAttachment().getData() != null;
    }

    /**
     * Removes the attachment.
     * 
     * @param e the e
     */
    @SuppressWarnings("unused")
    public void removeAttachment(ActionEvent e) {
        this.attachmentFile = null;
        getAttachment().setData(null);
        getAttachment().setMimeType(null);
        getAttachment().setSize(new Integer(0));
    }

    /**
     * Gets the encoded URL.
     * 
     * @param relativePath the relative path
     * 
     * @return the encoded URL
     */
    protected String getEncodedURL(String relativePath) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String url = ec.getRequestContextPath() + relativePath;
        url = ec.encodeActionURL(url);
        return url;
    }

    /**
     * Gets the encoded upload URL.
     * 
     * @return the encoded upload URL
     */
    public String getEncodedUploadURL() {
        return getEncodedURL(this.uploadURL);
    }
    
    private String getReturnURL() {
    	if ( this.returnURL == null ) {
    		FacesContext context = FacesContext.getCurrentInstance();
    		String id = context.getViewRoot().getViewId();
    		int pos = id.lastIndexOf('.');
    		if ( pos != -1 ) {
    			id = id.substring(0, pos) + ".faces";
    		}
    		return id;
    	}
    	return this.returnURL;
    }

    /**
     * Gets the encoded return URL.
     * 
     * @return the encoded return URL
     */
    public String getEncodedReturnURL() {
        return getEncodedURL(getReturnURL());
    }
}
