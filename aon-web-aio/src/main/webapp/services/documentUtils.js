export const isPersonaFisica = (document) => {
    if(!document) return false;
    return isValidDni(document) || isValidNie(document) || isValidAssetCommunity(document) || isValidOwnerCommunity(document) || isValidCivilSociety(document);
}

export const isValid = (document, country) => {
    if(!document) return false;
    if(!country || country.toUpperCase() === "ES") {
        return isValidCIF(document) || isValidDni(document) || isValidNie(document);
    } else return true;
}

export const isValidCIF = (cif) =>  {
    if(!cif) return false;
    cif = cif.toUpperCase();
    if (!/^[ABCDEFGHJNPQRSUVW]\d{7}[0-9A-J]$/i.test(cif)) return false;
    const letrasControl = 'JABCDEFGHI';
    const letraInicial = cif[0];
    const numeros = cif.slice(1, 8).split('').map(Number);
    const digitoControl = cif[8];

    const sumaPar = numeros[1] + numeros[3] + numeros[5];
    let sumaImpar = numeros[0] + numeros[2] + numeros[4] + numeros[6];
    sumaImpar *= 2;
    sumaImpar = String(sumaImpar).split('').reduce((a, b) => a + Number(b), 0);
    const total = sumaPar + sumaImpar;
    const control = (10 - (total % 10)) % 10;

    if ('ABEH'.includes(letraInicial)) {
        return digitoControl == control;
    } else if ('KPQS'.includes(letraInicial)) {
        return digitoControl == letrasControl[control];
    } else {
        return digitoControl == control || digitoControl == letrasControl[control];
    }
}

export const isValidDni = (dni) => {
    if (!dni || !/^\d{8}[A-Z]$/i.test(dni)) return false;
    dni = dni.toUpperCase();
    const letras = "TRWAGMYFPDXBNJZSQVHLCKE";
    const numero = parseInt(dni.slice(0, 8), 10);
    const letra = dni[8];
    return letras[numero % 23] === letra;
}

export const isValidNie = (nie) => {
    if(!nie || !/^[XYZ]\d{7}[A-Z]$/i.test(nie)) return false;
    nie = nie.toUpperCase();
    let numInicial = nie[0] === "X" ? "0" : nie[0] === "Y" ? "1" : "2";
    const numero = parseInt(numInicial + nie.slice(1, 8), 10);
    const letra = nie[8];
    const letras = "TRWAGMYFPDXBNJZSQVHLCKE";
    return letras[numero % 23] === letra;
}

export const isValidPassport = (passport) => {
    return passport && /^[A-Z0-9]{5,20}$/i.test(passport);
}

export const isValidCulturalAssociation = (doc) => {
    return doc && /^G\d{7}[0-9A-J]$/i.test(doc) && isValidCIF(doc.toUpperCase());
}

export const isValidCooperative = (doc) => {
    return doc && /^F\d{7}[0-9A-J]$/i.test(doc) && isValidCIF(doc.toUpperCase());
}

export const isValidAssetCommunity = (doc) => {
    return doc && /^E\d{7}[0-9A-J]$/i.test(doc) && isValidCIF(doc.toUpperCase());
}

export const isValidOwnerCommunity = (doc) => {
    return doc && /^H\d{7}[0-9A-J]$/i.test(doc) && isValidCIF(doc.toUpperCase());
}

export const isValidCivilSociety = (doc) => {
    return doc && /^J\d{7}[0-9A-J]$/i.test(doc) && isValidCIF(doc.toUpperCase());
}