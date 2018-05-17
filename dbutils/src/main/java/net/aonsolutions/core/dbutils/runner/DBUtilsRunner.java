/**
 * 
 */
package net.aonsolutions.core.dbutils.runner;


public class DBUtilsRunner implements Runnable {
	private IDBUtilsRunnable runnable;

	public DBUtilsRunner() {
		
	}

	public DBUtilsRunner(IDBUtilsRunnable runnable) {
		this.runnable = runnable;
	}

	public IDBUtilsRunnable getRunnable() {
		return runnable;
	}

	public void setRunnable(IDBUtilsRunnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		if (runnable == null) {
			throw new IllegalStateException("No hay nada que ejecutar");
		}
		runnable.start();
	}
}