package com.code.aon.file.format.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the registers collection
 * 
 * @author Consulting & Development. Iñigo GAyarre - 31/01/2007
 * @since 1.0
 *
 */
public class RegisterManager {

	/**
	 * A map of registers
	 */
	private Map<String,Register> regs = new HashMap<String,Register>();

	/**
	 * Adds a register if not included yet
	 * 
	 * @param type register type
	 * @param reg the register to add
	 */
	public void put(String type, Register reg) {
		if (!regs.containsKey(type)) {
			regs.put(type, reg);
		}
	}

	/**
	 * Recovers a Register
	 * 
	 * @param type the type to recover
	 * @return the register recovered
	 */
	public Register get(String type) {
		return regs.get(type);
	}

}
