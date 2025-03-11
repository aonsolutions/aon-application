package com.esferalia.aon.in.payroll.excel.contract;

public enum ContractType {

	BECARIO("000", "Becario", false),
    INDEFINIDO_TC_ORDINARIO("100", "Indefinido, TC/Ordinario", false),
    INDEFINIDO_TC_TRANSF_FOMENTO("109", "Indefinido, TC/Transf. CT-Fomento Contr.", false),
    INDEFINIDO_TC_DISCAPACITADOS("130", "Indefinido, TC/Discapacitados", false),
    INDEFINIDO_TC_TRANSF_DISCAPACITADOS("139", "Indefinido, TC/Transf. CT- Discapacitados", false),
    INDEFINIDO_TC_FOMENTO_INICIAL("150", "Indefinido, TC/Fomento Contr. Emp.Estable inicial", false),
    INDEFINIDO_TC_TRANSF_TEMPORAL("189", "Indefinido, TC/Transf. Cto. Temporal", false),
    INDEFINIDO_TP_ORDINARIO("200", "Indefinido, TP/Ordinario", true),
    INDEFINIDO_TP_TRANSF_FOMENTO("209", "Indefinido, TP/Transf. CT Fomento Contr.", true),
    INDEFINIDO_TP_DISCAPACITADOS("230", "Indefinido, TP/Discapacitados", true),
    INDEFINIDO_TP_TRANSF_DISCAPACITADOS("239", "Indefinido, TP/Transf. CT Discapacitados", true),
    INDEFINIDO_TP_FOMENTO_INICIAL("250", "Indefinido, TP/Fomento Contr. Emp.Estable inicial", true),
    INDEFINIDO_TP_TRANSF_TEMPORAL("289", "Indefinido, TP/Transf. Cto. Temporal", true),
    INDEFINIDO_FIJO_DISCONTINUO("300", "Indefinido/Fijo-Discontinuo", true),
    INDEFINIDO_FIJO_DISCONTINUO_TRANSF("309", "Indefinido/Fijo-Discontinuo Transf. CT", true),
    INDEFINIDO_FIJO_DISCONTINUO_DISCAPACITADOS("330", "Indefinido/Fijo-Discontinuo Discapacitados", true),
    INDEFINIDO_FIJO_DISCONTINUO_DISCAP_TRANSF("339", "Indefinido/Fijo-Discontinuo Discapacitados, Transf.", true),
    INDEFINIDO_FIJO_DISCONTINUO_FOMENTO("350", "Indefinido/Fijo-Disc. Fomento Contr. Empl.Estable inicial", true),
    INDEFINIDO_FIJO_DISCONTINUO_TRANSF2("389", "Indefinido/Fijo-Discontinuo Transf. CT", true),
    TEMPORAL_TC_PRODUCCION("402", "Temporal, TC/Circunstancia de Producción", false),
    TEMPORAL_TC_INSERCION("403", "Temporal, TC/Inserción", false),
    TC_PREDOCTORAL("404", "TC/Predoctoral", false),
    TC_TRANSFORMACION_RESILIENCIA("406", "TC/Transformación y resiliencia", false),
    TEMPORAL_TC_ADMIN("408", "Temporal, TC/Caracter Admin.", false),
    TEMPORAL_TC_INTERINIDAD("410", "Temporal, TC/Interinidad", false),
    TEMPORAL_TC_INTERINIDAD_ADMIN("418", "Temporal, TC/Interinidad C.Admin.", false),
    TEMPORAL_TC_PRACTICAS("420", "Temporal, TC/Prácticas", false),
    TEMPORAL_TC_FORMACION("421", "Temporal, TC/Formación", false),
    TEMPORAL_TC_DISCAPACITADOS("430", "Temporal, TC/Discapacitados", false),
    TEMPORAL_TC_RELEVO("441", "Temporal, TC/Relevo", false),
    TEMPORAL_TC_FOMENTO("450", "Temporal, TC/Fomento Contr. indefinida", false),
    TEMPORAL_TC_DESEMPLEADOS("452", "Temporal, TC/Desempleados en Emp.Inserción", false),
    TEMPORAL_TP_ORDINARIO("500", "Temporal, TP/Ordinario", true),
    TEMPORAL_TP_PRODUCCION("502", "Temporal, TP/Circunstancia de Producción", true),
    TEMPORAL_TP_INSERCION("503", "Temporal, TP/Inserción", true),
    TP_TRANSFORMACION_RESILIENCIA("506", "TP/Transformación y resiliencia", true),
    TEMPORAL_TP_ADMIN("508", "Temporal, TP/Caracter Admin.", true),
    TEMPORAL_TP_INTERINIDAD("510", "Temporal, TP/Interinidad", true),
    TEMPORAL_TP_INTERINIDAD_ADMIN("518", "Temporal, TP/Interinidad C.Admin.", true),
    TEMPORAL_TP_PRACTICAS("520", "Temporal, TP/Prácticas", true),
    TEMPORAL_TP_FORMACION("521", "Temporal, TP/Formación", true),
    TEMPORAL_TP_DISCAPACITADOS("530", "Temporal, TP/Discapacitados", true),
    TEMPORAL_TP_JUBILACION_PARCIAL("540", "Temporal, TP/Jubilación Parcial", true),
    TEMPORAL_TP_RELEVO("541", "Temporal, TP/Relevo", true),
    TEMPORAL_TP_FOMENTO("550", "Temporal, TP/Fomento Contr. indefinida Empl.Estable", true),
    TEMPORAL_TP_DESEMPLEADOS("552", "Temporal, TP/Desempleados en Emp.Inserción", true);

    private final String value;
    private final String name;
    private final boolean partial;

    ContractType(String value, String name, boolean partial) {
        this.value = value;
        this.name = name;
        this.partial = partial;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public boolean isPartial() {
        return partial;
    }

    public static ContractType fromStringCode(String input) {
        if (input == null) {
            return null;
        }

        // Elimina comillas si existen
        String cleanValue = input.replace("\"", "").trim();

        for (ContractType type : values()) {
            if (type.value.equals(cleanValue)) {
                return type;
            }
        }

        throw new IllegalArgumentException("No existe un ContractType con el valor: " + input);
    }
}
