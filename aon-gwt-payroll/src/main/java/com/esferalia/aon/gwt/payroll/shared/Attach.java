package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

import com.google.gwt.view.client.ProvidesKey;

public class Attach extends com.esferalia.aon.occam.api.model.attachment.Attach implements Serializable {

	//--------------------- Serial version
	
	private static final long serialVersionUID = 1L;
	
	//--------------------- Key provider
	
    public static final ProvidesKey<Attach> KEY_PROVIDER = item -> item == null ? null : item.getId();

    //--------------------- Constructor
    
    public Attach() {
    	super();
    }
    
    public Attach(com.esferalia.aon.occam.api.model.attachment.Attach occamAttach) {
    	super();
    	this.setAttachType(occamAttach.getAttachType());
    	this.setAttachModule(occamAttach.getAttachModule());
    	this.setAttachURL(occamAttach.getAttachURL());
    	this.setId(occamAttach.getId());
    	this.setDomain(occamAttach.getDomain());
    	this.setMimeType(occamAttach.getMimeType());
    	this.setDescription(occamAttach.getDescription());
    	this.setData(occamAttach.getData());
    	this.setDate(occamAttach.getDate());
    	this.setType(occamAttach.getType());
    	this.setDriveId(occamAttach.getDriveId());
    	this.setScope(occamAttach.getScope());
    	this.setConfidential(occamAttach.getConfidential());
    	this.setCategory(occamAttach.getCategory());
    	this.setDparentId(occamAttach.getDparentId());
    	this.setSourceBatch(occamAttach.getSourceBatch());
    	this.setSourceType(occamAttach.getSourceType());
    	this.setCreationUser(occamAttach.getCreationUser());
    	this.setCreationDate(occamAttach.getCreationDate());
    	this.setModificationUser(occamAttach.getModificationUser());
    	this.setModificationDate(occamAttach.getModificationDate());
    	this.setIcon(occamAttach.getIcon());
    	this.setMd5(occamAttach.getMd5());
    	this.setIsDrive(occamAttach.getIsDrive());
    	this.setFullScope(occamAttach.getFullScope());
    	this.setFullCategory(occamAttach.getFullCategory());
    	this.setTagList(occamAttach.getTagList());
	}

}
