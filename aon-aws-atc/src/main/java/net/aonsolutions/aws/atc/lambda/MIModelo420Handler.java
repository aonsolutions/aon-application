package net.aonsolutions.aws.atc.lambda;

import java.util.List;

import org.grecasa.ext.pa.mod420.MIModelo420;

public class MIModelo420Handler {
	MIModelo420 miModelo420;

	/**
	 * Método para obtener el impreso oficial del modelo mostrando los posibles
	 * errores a través de logs.
	 */
	public byte[] obtenerImpresoOficial(String declaracion) {
		if (isEmpty(declaracion))
			return null;
		byte[] resultado = null;
		try {
			resultado = getMIModelo420().getImpreso(declaracion);
			if (resultado == null)
				mostrarMensajes(getMIModelo420().getMensajes());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			/*
			 * TODO: procesar la excepcion ...
			 */
		}
		return resultado;
	}

	/**
	 * Método para obtener el contenido del fichero de presentación telemática,
	 * mostrando los posibles errores a través de logs
	 */
	public String obtenerPresentacion(String declaracion) {
		if (isEmpty(declaracion))
			return null;
		String resultado = null;
		try {
			resultado = getMIModelo420().getFicheroPresentacion(declaracion);
			if (resultado == null)
				mostrarMensajes(getMIModelo420().getMensajes());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			/*
			 * TODO: procesar la excepcion ...
			 */
		}
		return resultado;
	}

	/**
	 * Método que devuelve los mensajes de los métodos del módulo a través de logs.
	 */
	private void mostrarMensajes(List<String> mensajes) {
		if (mensajes != null && mensajes.size() > 0) {
			System.out.println("Nº mensajes error: " + mensajes.size());
			for (String m : mensajes)
				System.out.println("" + m);
		}
	}

	/**
	 * Método que crea una instancia del módulo de impresión
	 */
	private MIModelo420 getMIModelo420() {
		if (this.miModelo420 == null)
			this.miModelo420 = new MIModelo420(getDownloadDir());
		return this.miModelo420;
	}

	private static String getDownloadDir() {
		return System.getProperty("java.io.tmpdir");
	}

	
	private static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static void main(String[] args) {
		System.out.println(new MIModelo420Handler().obtenerPresentacion(args[0]));
	}
	
	

}
