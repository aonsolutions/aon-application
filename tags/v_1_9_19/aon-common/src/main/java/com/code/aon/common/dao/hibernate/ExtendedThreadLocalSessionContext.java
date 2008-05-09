package com.code.aon.common.dao.hibernate;

import java.util.logging.Logger;

import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.context.ThreadLocalSessionContext;
import org.hibernate.engine.SessionFactoryImplementor;

/**
 * TODO
 * @author Consulting & Development. ecastellano - 22/11/2006
 *
 */
public class ExtendedThreadLocalSessionContext extends
		ThreadLocalSessionContext {

	/**
	 * TODO
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ExtendedThreadLocalSessionContext.class.getName());

	/**
	 * TODO
	 * @param factory
	 */
	public ExtendedThreadLocalSessionContext(SessionFactoryImplementor factory) {
		super(factory);
	}

	// Always set FlushMode.NEVER on any Session
	/* (non-Javadoc)
	 * @see org.hibernate.context.ThreadLocalSessionContext#buildOrObtainSession()
	 */
	protected Session buildOrObtainSession() {
		LOGGER.fine("Opening a new Session");
		Session s = super.buildOrObtainSession();

		LOGGER.fine("Disabling automatic flushing of the Session");
		s.setFlushMode(FlushMode.MANUAL);

		return s;
	}

	// No automatic flushing of the Session at transaction commit, client calls
	// flush()
	/* (non-Javadoc)
	 * @see org.hibernate.context.ThreadLocalSessionContext#isAutoFlushEnabled()
	 */
	protected boolean isAutoFlushEnabled() {
		return false;
	}

	// No automatic closing of the Session at transaction commit, client calls
	// close()
	/* (non-Javadoc)
	 * @see org.hibernate.context.ThreadLocalSessionContext#isAutoCloseEnabled()
	 */
	protected boolean isAutoCloseEnabled() {
		return false;
	}

	// Don't unbind after transaction completion, we expect the client to do
	// this
	// so it can flush() and close() if needed (or continue the conversation).
	/* (non-Javadoc)
	 * @see org.hibernate.context.ThreadLocalSessionContext#buildCleanupSynch()
	 */
	protected CleanupSynch buildCleanupSynch() {
		return new NoCleanupSynch(factory);
	}

	/**
	 * @author Consulting & Development. ecastellano - 22/11/2006
	 *
	 */
	private static class NoCleanupSynch extends
			ThreadLocalSessionContext.CleanupSynch {
		/**
		 * @param factory
		 */
		public NoCleanupSynch(SessionFactory factory) {
			super(factory);
		}

		/* (non-Javadoc)
		 * @see org.hibernate.context.ThreadLocalSessionContext$CleanupSynch#beforeCompletion()
		 */
		public void beforeCompletion() {
		}

		/* (non-Javadoc)
		 * @see org.hibernate.context.ThreadLocalSessionContext$CleanupSynch#afterCompletion(int)
		 */
		public void afterCompletion(int i) {
		}
	}
}