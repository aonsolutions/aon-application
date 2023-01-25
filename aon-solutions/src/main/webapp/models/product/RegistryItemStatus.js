export const RegistryItemStatus = {
    ACTIVE: "ACTIVE",
    INTERESTED: "INTERESTED",
    REFUSED: "REFUSED",
    INACTIVE: "INACTIVE",

    getText: function (i) {
        if (i === null) return null;

        switch (i) {
            case RegistryItemStatus.INTERESTED:
                return "INTERESADO";
            case RegistryItemStatus.REFUSED:
                return "RECHAZADO";
            case RegistryItemStatus.INACTIVE:
                return "Inactivo";
            default:
                return "ACTIVO";
        }
    },
};
