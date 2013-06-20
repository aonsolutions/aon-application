package com.code.aon.ui.admin;

import com.code.aon.ui.admin.controller.BackupController;

public class DumpThread implements Runnable {

	private BackupController backup;
	private String domainName;
	private Thread thread;
    
    public DumpThread(BackupController backup, String domainName) {
         this.backup = backup;
         this.domainName = domainName;
    }

    public void start() {
         thread = new Thread(this);
         thread.start();
    }
    	
	@Override
	public void run() {
        if (thread != null) {
        	this.backup.makeBackup(domainName);
        }
	}

}
