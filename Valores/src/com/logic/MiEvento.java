package com.logic;

import java.util.EventObject;

public class MiEvento extends EventObject {

	private String elMensaje;
	
	public MiEvento(Object source, String mensaje) {
		super(source);
		elMensaje = mensaje;
	}

	public String getElMensaje() {
		return elMensaje;
	}
	
}
