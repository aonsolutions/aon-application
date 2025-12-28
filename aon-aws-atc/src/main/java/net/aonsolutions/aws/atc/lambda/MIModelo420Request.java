package net.aonsolutions.aws.atc.lambda;

public class MIModelo420Request {
	
    private String declaracion;
    private boolean isBorrador;
	
	public String getDeclaracion() {
		return declaracion;
	}
	public void setDeclaracion(String declaracion) {
		this.declaracion = declaracion;
	}
	
	public boolean isBorrador() {
		return isBorrador;
	}
	public void setBorrador(boolean isBorrador) {
		this.isBorrador = isBorrador;
	}

}
