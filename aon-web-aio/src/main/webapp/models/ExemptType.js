export const EXEMPT_TYPES = Object.freeze({
    EMPTY: {
        value: 'EMPTY',
        name: '------------'
    },
    NO_SOFTWARE: {
        value: 'NO_SOFTWARE',
        name: 'No utiliza sistema informático de facturación'
    },
    NO_OBLIGATION: {
        value: 'NO_OBLIGATION',
        name: 'Operaciones sin obligación de emitir factura'
    }, 
    REAGP: {
        value: 'REAGP',
        name: 'REAGP sin emisión de factura propia'
    },
    AUTHORIZATION: {
        value: 'AUTHORIZATION',
        name: 'Exenci\u00F3n autorizada por la administración tributaria'
    },
});

export class ExemptType {
    value;
    name;

    constructor(value) { 
        Object.keys(EXEMPT_TYPES).forEach(key => {
            if (EXEMPT_TYPES[key].value === value) {
                this.value = EXEMPT_TYPES[key].value;
                this.name = EXEMPT_TYPES[key].name;
            }
        });
    }
   
}