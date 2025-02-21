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

export const BookingItemStatus = {
    BILLABLE: "BILLABLE",
    NOT_BILLABLE: "NOT_BILLABLE",
    NOT_CONTRACTABLE: "NOT_CONTRACTABLE",
    INACTIVE: "INACTIVE",

    getText: function (i) {
        if (i === null) return null;

        switch (i) {
            case BookingItemStatus.BILLABLE:
                return "Facturable";
            case BookingItemStatus.NOT_BILLABLE:
                return "No facturable";
            case BookingItemStatus.NOT_CONTRACTABLE:
                return "No contratable";
            case BookingItemStatus.INACTIVE:
                return "Inactivo";
            default:
                return "";
        }
    },
};