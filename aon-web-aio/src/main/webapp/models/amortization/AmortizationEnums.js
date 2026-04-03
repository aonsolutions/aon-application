export const AmortizationPeriod = Object.freeze({
    YEARLY:         { value: 0, name: 'YEARLY', description: 'Anual', yearFraction: 1 },
    MONTHLY:        { value: 1, name: 'MONTHLY', description: 'Mensual', yearFraction: 12 },
    BI_MONTHLY:     { value: 2, name: 'BI_MONTHLY', description: 'Bimestral', yearFraction: 6 },
    QUARTERLY:      { value: 3, name: 'QUARTERLY', description: 'Trimestral', yearFraction: 4 },
    FOUR_MONTHLY:   { value: 4, name: 'FOUR_MONTHLY', description: 'Cuatrimestral', yearFraction: 3 },
    HALF_YEARLY:    { value: 5, name: 'HALF_YEARLY', description: 'Semestral', yearFraction: 2 },

    safeValueOf(i) {
        if (i == null) return null;
        return Object.values(this).find(v => v?.value == i) ?? null;
    },
    safeValueOfName(name) {
        if(name == null) return null;
        return Object.values(this).find(v => v?.name == name) ?? null;
    },
    safeValueOfDescription(description) {
        if (description == null) return null;
        return Object.values(this).find(v => v?.description == description) ?? null;
    },
    toArray() {
        return Object.values(this);
    }
});