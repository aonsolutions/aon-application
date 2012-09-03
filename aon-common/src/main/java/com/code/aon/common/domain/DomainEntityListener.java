package com.code.aon.common.domain;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainEntityListener implements PreInsertEventListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DomainEntityListener.class);
	private static final long serialVersionUID = 4075158916830326002L;

	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		Object entity = event.getEntity();
		if (entity instanceof IDomain) {
	        IDomain iDomain =  (IDomain) entity;
			if (iDomain.getDomain() == 0) {
		        Integer currentDomain = DomainManager.getCurrentDomain();
		        LOGGER.debug(" ------------------ DOMAIN ENTITY LISTENER " );
		        LOGGER.debug("Thread ..:" + Thread.currentThread().getId() );
		        LOGGER.debug(""+DomainManager.getDomainProvider());
		        LOGGER.debug("DOMAIN ENTITY LISTENER ..:" + iDomain.getClass().getName());
		        LOGGER.debug("DOMAIN LISTENER ..(domain)..: "+ currentDomain);
		        LOGGER.debug(" -----------------------------------------" );
				iDomain.setDomain(currentDomain);
				String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
				Object[] state = event.getState();
				setValue(state, propertyNames, "domain", currentDomain, entity);
				iDomain.setDomain(currentDomain);
			}
		}
		return false;
	}

	private void setValue(Object[] currentState, String[] propertyNames,String propertyToSet, Object value, Object entity) {
		int index = ArrayUtils.indexOf(propertyNames, propertyToSet);
		if (index >= 0) {
			currentState[index] = value;
		} else {
			LOGGER.error("Field '" + propertyToSet + "' not found on entity '"
					+ entity.getClass().getName() + "'.");
		}
	}
}
