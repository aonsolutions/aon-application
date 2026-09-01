package com.esferalia.aon.occam.api.model.type;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum ExemptType {
	NO_SOFTWARE		("No utiliza sistema inform\u00E1tico de facturaci\u00F3n"),
	NO_OBLIGATION	("Operaciones sin obligaci\u00F3n de emitir factura"),
    REAGP			("REAGP sin emisi\u00F3n de factura propia"),
    AUTHORIZATION	("Exenci\u00F3n autorizada"),
    ;

    private final String description;

    private ExemptType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

	public static Optional<ExemptType> safeValueOf(String cause) {
		if (AonStringUtils.isBlank(cause)) return Optional.empty();
		String c = AonStringUtils.trimToNull(cause);
		return AonCollectionUtils.stream(values())
			.filter(et -> AonStringUtils.equalsIgnoreCase(et.name(), c))
			.findFirst();
	}
}
