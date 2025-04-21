package net.aonsolutions.aws.atc.lambda;

import java.util.ArrayList;
import java.util.List;

public class MIModelo420Result {
	
	private String resultado;
	private List<String> errores;
	
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;		
	}
	public List<String> getErrores() {
		if (errores == null) 
			errores = new ArrayList<>();
		return errores;
	}
	public void setErrores(List<String> errores) {
		this.errores = errores;
	}
	
}
