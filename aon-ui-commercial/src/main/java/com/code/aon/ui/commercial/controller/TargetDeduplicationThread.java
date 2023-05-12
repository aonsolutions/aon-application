package com.code.aon.ui.commercial.controller;

import jakarta.servlet.http.HttpServletRequest;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.session.MockHttpServletRequest;
import com.code.aon.ui.util.AonUtil;


public class TargetDeduplicationThread implements Runnable {

	private TargetDeduplicationController controller;
	private TargetController targetController;
	private Criteria criteria;
	private HttpServletRequest httpServletRequest;
	private Thread thread;
    
    public TargetDeduplicationThread(TargetDeduplicationController controller) {
         this.controller = controller;
         AuthPrincipal principal = AonUtil.getAuthPrincipal();
         this.httpServletRequest = new MockHttpServletRequest(principal);
    }

	public void setTargetController(TargetController targetController) {
		this.targetController = targetController;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public void start() {
         thread = new Thread(this);
         thread.start();
    }
    	
	@Override
	public void run() {
        if (thread != null) {
        	this.controller.searchDuplicates(httpServletRequest, targetController, criteria);
        }
	}

}
