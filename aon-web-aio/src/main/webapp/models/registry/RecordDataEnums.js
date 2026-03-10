export const RecordDataType = Object.freeze({
    INCORPORATION:          { value: 0, name: 'INCORPORATION', description: 'Constitución' },
    TRANSFER:               { value: 1, name: 'TRANSFER', description: 'Traslado' },
    COMPANY_NAME_CHANGE:    { value: 2, name: 'COMPANY_NAME_CHANGE', description: 'Modificación denominación social' },
    OTHER_REGISTRATIONS:    { value: 3, name: 'OTHER_REGISTRATIONS', description: 'Otras Inscripciones' },

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

export const CommercialRegistryCode = Object.freeze({
    ARABA_ALAVA:                    { value: 0,  code: '01005', description: 'Araba-Alava' },
    ALBACETE:                       { value: 1,  code: '02010', description: 'Albacete' },
    ALACANT_ALICANTE:               { value: 2,  code: '03026', description: 'Alacant-Alicante' },
    ALMERIA:                        { value: 3,  code: '04015', description: 'Almería' },
    AVILA:                          { value: 4,  code: '05003', description: 'Ávila' },
    BADAJOZ:                        { value: 5,  code: '06017', description: 'Badajoz' },
    EIVISSA:                        { value: 6,  code: '07009', description: 'Eivissa' },
    MAO:                            { value: 7,  code: '07013', description: 'Mao' },
    PALMA_DE_MALLORCA:              { value: 8,  code: '07017', description: 'Palma De Mallorca' },
    BARCELONA:                      { value: 9,  code: '08005', description: 'Barcelona' },
    BURGOS:                         { value: 10, code: '09014', description: 'Burgos' },
    CACERES:                        { value: 11, code: '10015', description: 'Cáceres' },
    CADIZ:                          { value: 12, code: '11016', description: 'Cádiz' },
    CASTELLO_CASTELLON:             { value: 13, code: '12011', description: 'Castello-Castellón' },
    CIUDAD_REAL:                    { value: 14, code: '13013', description: 'Ciudad Real' },
    CORDOBA:                        { value: 15, code: '14022', description: 'Córdoba' },
    A_CORUNA:                       { value: 16, code: '15021', description: 'A Coruña' },
    SANTIAGO_DE_COMPOSTELA:         { value: 17, code: '15028', description: 'Santiago De Compostela' },
    CUENCA:                         { value: 18, code: '16003', description: 'Cuenca' },
    GIRONA:                         { value: 19, code: '17010', description: 'Girona' },
    GRANADA:                        { value: 20, code: '18020', description: 'Granada' },
    GUADALAJARA:                    { value: 21, code: '19010', description: 'Guadalajara' },
    GIPUZKOA:                       { value: 22, code: '20014', description: 'Gipuzkoa' },
    HUELVA:                         { value: 23, code: '21007', description: 'Huelva' },
    HUESCA:                         { value: 24, code: '22010', description: 'Huesca' },
    JAEN:                           { value: 25, code: '23015', description: 'Jaén' },
    LEON:                           { value: 26, code: '24014', description: 'León' },
    LLEIDA:                         { value: 27, code: '25011', description: 'Lleida' },
    LOGRONO:                        { value: 28, code: '26010', description: 'Logroño' },
    LUGO:                           { value: 29, code: '27013', description: 'Lugo' },
    MADRID:                         { value: 30, code: '28065', description: 'Madrid' },
    MALAGA:                         { value: 31, code: '29023', description: 'Málaga' },
    MURCIA:                         { value: 32, code: '30011', description: 'Murcia' },
    PAMPLONA:                       { value: 33, code: '31015', description: 'Pamplona' },
    OURENSE:                        { value: 34, code: '32013', description: 'Ourense' },
    ASTURIAS:                       { value: 35, code: '33029', description: 'Asturias' },
    PALENCIA:                       { value: 36, code: '34009', description: 'Palencia' },
    LAS_PALMAS_DE_GRAN_CANARIA:     { value: 37, code: '35009', description: 'Las Palmas de Gran Canaria' },
    ARRECIFE:                       { value: 38, code: '35016', description: 'Arrecife' },
    PUERTO_DEL_ROSARIO:             { value: 39, code: '35018', description: 'Puerto Del Rosario' },
    PONTEVEDRA:                     { value: 40, code: '36015', description: 'Pontevedra' },
    SALAMANCA:                      { value: 41, code: '37010', description: 'Salamanca' },
    SANTA_CRUZ_DE_LA_PALMA:         { value: 42, code: '38004', description: 'Santa Cruz De La Palma' },
    SANTA_CRUZ_DE_TENERIFE:         { value: 43, code: '38013', description: 'Santa Cruz De Tenerife' },
    SAN_SEBASTIAN_DE_LA_GOMERA:     { value: 44, code: '38018', description: 'San Sebastián De La Gomera' },
    VALVERDE:                       { value: 45, code: '38019', description: 'Valverde' },
    SANTANDER:                      { value: 46, code: '39014', description: 'Santander' },
    SEGOVIA:                        { value: 47, code: '40008', description: 'Segovia' },
    SEVILLA:                        { value: 48, code: '41021', description: 'Sevilla' },
    SORIA:                          { value: 49, code: '42007', description: 'Soria' },
    TARRAGONA:                      { value: 50, code: '43017', description: 'Tarragona' },
    TERUEL:                         { value: 51, code: '44009', description: 'Teruel' },
    TOLEDO:                         { value: 52, code: '45019', description: 'Toledo' },
    VALENCIA:                       { value: 53, code: '46030', description: 'Valencia' },
    VALLADOLID:                     { value: 54, code: '47015', description: 'Valladolid' },
    BIZKAIA:                        { value: 55, code: '48001', description: 'Bizkaia' },
    ZAMORA:                         { value: 56, code: '49010', description: 'Zamora' },
    ZARAGOZA:                       { value: 57, code: '50020', description: 'Zaragoza' },
    CEUTA:                          { value: 58, code: '51001', description: 'Ceuta' },
    MELILLA:                        { value: 59, code: '52001', description: 'Melilla' },

    safeValueOf(i) {
        if (i == null) return null;
        return Object.values(this).find(v => v?.value == i) ?? null;
    },

    safeValueOfCode(code) {
        if (code == null) return null;
        return Object.values(this).find(v => v?.code == code) ?? null;
    },
    toArray() {
        return Object.values(this);
    }
});
