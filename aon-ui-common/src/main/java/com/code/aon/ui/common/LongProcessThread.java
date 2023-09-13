package com.code.aon.ui.common;

import static com.code.aon.ui.common.domain.FacesDomainProvider.DOMAIN_SWITCHER_CONTROLLER;

import jakarta.servlet.http.HttpServletRequest;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.domain.IDomainProvider;
import com.code.aon.common.domain.IDomainSwitcher;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.code.aon.ui.common.domain.FacesDomainProvider;
import com.code.aon.ui.common.session.MockHttpServletRequest;
import com.code.aon.ui.util.AonUtil;


public class LongProcessThread implements Runnable {

	private ILongProcess longProcess;
	private HttpServletRequest httpServletRequest;
	private IDomainSwitcher domainSwitcher;
	private Thread thread;
    
    public LongProcessThread(ILongProcess longProcess) {
         this.longProcess = longProcess;
         AuthPrincipal principal = AonUtil.getAuthPrincipal();
         this.httpServletRequest = new MockHttpServletRequest(principal);
         this.domainSwitcher = (IDomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER_CONTROLLER);
    }

	public void start() {
         thread = new Thread(this);
         thread.start();
    }
	
	public void interrupt() {
		if(thread!=null){
			thread.interrupt();
		}
	}
	
	public boolean isTerminated(){
		return thread.getState()==Thread.State.TERMINATED;
	}
	
	private FacesDomainProvider getFacesDomainProvider() {
		IDomainProvider dp = DomainManager.getDomainProvider();
		if ( (dp!=null) && (dp instanceof FacesDomainProvider) ) {
			return (FacesDomainProvider) dp;
		}
		return null;
	}
	
	private void initContext() {
		HttpServletRequestValve.setHttpServletRequest(httpServletRequest);
		FacesDomainProvider fdp = getFacesDomainProvider();
		if ( fdp != null ) {
			fdp.setDomainSwitcher(domainSwitcher);
		}
	}
	
	private void finishContext() {
		HttpServletRequestValve.setHttpServletRequest(null);
		FacesDomainProvider fdp = getFacesDomainProvider();
		if ( fdp != null ) {
			fdp.setDomainSwitcher(null);
		}
	}
    	
	@Override
	public void run() {
        if (thread != null) {
    		try {
    			initContext();
            	longProcess.execute();
    		} finally {
    			finishContext();
    		}
        }
	}

}
