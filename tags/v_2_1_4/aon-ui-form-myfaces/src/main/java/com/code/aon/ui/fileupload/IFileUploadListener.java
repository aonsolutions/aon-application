package com.code.aon.ui.fileupload;

/**
 * File Upload Listener
 */
public interface IFileUploadListener {

    /**
     * Upload action
     * 
     * @param name
     * @param mimeType
     * @param data
     */
    void upload(String name, String mimeType, byte[] data);

}
