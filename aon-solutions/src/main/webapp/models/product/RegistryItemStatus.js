export const RegistryItemStatus = {
    ACTIVE: "ACTIVE",
    INTERESTED: "INTERESTED",
    REFUSED: "REFUSED",

    getText: function (i) {
        if (i === null) return null;

        switch (i) {
            case RegistryItemStatus.INTERESTED:
                return "INTERESADO";
            case RegistryItemStatus.REFUSED:
                return "RECHAZADO";
            default:
                return "ACTIVO";
        }
    },
};
