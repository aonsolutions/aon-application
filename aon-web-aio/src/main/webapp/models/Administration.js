export const ADMINISTRATIONS = Object.freeze({
    ALAVA: {
        value: 'ALAVA',
        name: 'Araba/Alava'
    },
    BIZKAIA: {
        value: 'BIZKAIA',
        name: 'Bizkaia'
    }, 
    GIPUZKOA: {
        value: 'GIPUZKOA',
        name: 'Gipuzkoa'
    },
    NAVARRA: {
        value: 'NAVARRA',
        name: 'Navarra'
    },
    COMMON_TERRITORY: {
        value: 'COMMON_TERRITORY',
        name: 'Territorio Común'
    },
    CANARIAS: {
        value: 'CANARIAS',
        name: 'A.T. Canaria'
    },
    UNKNOWN: {
        value: 'UNKNOWN',
        name: 'Otro'
    }
});

export class Administration {

    value;
    name;

    constructor(value) { 
        Object.keys(ADMINISTRATIONS).forEach(key => {
            if (ADMINISTRATIONS[key].value === value) {
                this.value = ADMINISTRATIONS[key].value;
                this.name = ADMINISTRATIONS[key].name;
            }
        });
    }

    isAlava = () => {
        return this.value && this.value === ADMINISTRATIONS.ALAVA.value;
    }

    isBizkaia = () => {
        return this.value && this.value === ADMINISTRATIONS.BIZKAIA.value;
    }
    
    isGipuzkoa = () => {
        return this.value && this.value === ADMINISTRATIONS.GIPUZKOA.value;
    }

    isNavarra = () => {
        return this.value && this.value === ADMINISTRATIONS.NAVARRA.value;
    }
    
    isCommonTerritory = () => {
        return this.value && this.value === ADMINISTRATIONS.COMMON_TERRITORY.value;
    }

    isCanarias = () => {
        return this.value && this.value === ADMINISTRATIONS.CANARIAS.value;
    }

    isUnknown = () => {
        return this.value && this.value === ADMINISTRATIONS.UNKNOWN.value;
    }
    
}