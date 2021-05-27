package com.esferalia.aon.ui.payroll.file;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class FileUtils implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public static String obtainUtf(String value) {
		if (value != null) {
			value = value.replaceAll("[¡¿]", "A").replaceAll("[…»]", "E")
					.replaceAll("[ÕÃ]", "I").replaceAll("[”“]", "O")
					.replaceAll("[⁄Ÿ]", "U").replaceAll("[·‡]", "a")
					.replaceAll("[ÈË]", "e").replaceAll("[ÌÏ]", "i")
					.replaceAll("[ÛÚ]", "o").replaceAll("[˙˘]", "u")
					.replaceAll("[^-_;:ø?°!@#$&\\(\\)\\s\\.,a-zA-Z0-9]", "?");
		}
		return value;
	}

}
