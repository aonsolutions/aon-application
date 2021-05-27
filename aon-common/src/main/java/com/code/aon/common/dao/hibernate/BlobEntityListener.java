package com.code.aon.common.dao.hibernate;

import static com.code.aon.common.BlobObjectAction.DELETE;
import static com.code.aon.common.BlobObjectAction.INSERT;

import org.apache.commons.lang.ClassUtils;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.IBlobObject;

public class BlobEntityListener implements PostInsertEventListener,
									PostUpdateEventListener, PostDeleteEventListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(BlobEntityListener.class);
	
	@Override
	public void onPostInsert(PostInsertEvent event) {
		Object to = event.getEntity();
		if ( to instanceof IBlobObject ) {
			IBlobObject bo = (IBlobObject) to;
			LOGGER.info("postInsert - {}, {}", ClassUtils.getShortClassName(to.getClass()), bo.getId());
			bo.getManager(INSERT).setBlobs(true, bo);
		}
	}
	
	@Override
	public void onPostDelete(PostDeleteEvent event) {
		Object to = event.getEntity();
		if ( to instanceof IBlobObject ) {
			IBlobObject bo = (IBlobObject) to;
			LOGGER.info("postDelete - {}, {}", ClassUtils.getShortClassName(to.getClass()), bo.getId());
			bo.getManager(DELETE).deleteBlobs(bo);
		}
	}

	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		Object to = event.getEntity();
		if ( to instanceof IBlobObject ) {
			IBlobObject bo = (IBlobObject) to;
			LOGGER.info("postUpdate - {}, {}", ClassUtils.getShortClassName(to.getClass()), bo.getId());
			bo.getManager(INSERT).setBlobs(false, bo);
		}
	} 
	
}