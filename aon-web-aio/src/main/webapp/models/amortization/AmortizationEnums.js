export const AmortizationPeriod = Object.freeze({
    YEARLY:         { value: 0, name: 'INCORPORATION', description: 'Anual', yearFraction: 12 },
    BI_MONTHLY:     { value: 1, name: 'TRANSFER', description: 'Bimestral', yearFraction: 6 },
    QUARTERLY:      { value: 2, name: 'COMPANY_NAME_CHANGE', description: 'Trimestral', yearFraction: 4 },
    FOUR_MONTHLY:   { value: 3, name: 'OTHER_REGISTRATIONS', description: 'Cuatrimestral', yearFraction: 3 },
    HALF_YEARLY:    { value: 4, name: 'HALF_YEARLY', description: 'Semestral', yearFraction: 2 },
    
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