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
export const isValidCIF = (cif) => {
    if(!cif || !/^[ABCDEFGHJKLMNPQRSUVW]\d{7}[0-9A-J]$/i.test(cif)) return false;
    cif = cif.toUpperCase();

    const letrasControl = "JABCDEFGHI";
    const letra = cif[0];
    const cuerpo = cif.slice(1, 8);
    const digitoControl = cif[8];
    let sumaPares = 0;
    let sumaImpares = 0;

    for(let i = 0; i < cuerpo.length; i++) {
        const n = parseInt(cuerpo[i], 10);
        if(i % 2 === 0) {
            let doble = n * 2;
            sumaImpares += Math.floor(doble / 10) + (doble % 10);
        } else {
            sumaPares += n;
        }
    }

    const sumaTotal = sumaPares + sumaImpares;
    const unidad = sumaTotal % 10;
    const complemento = (10 - unidad) % 10;

    if("ABEH".includes(letra)) {
        return digitoControl === complemento.toString();
    } else if("KPQS".includes(letra)) {
        return digitoControl === letrasControl[complemento];
    } else {
        return digitoControl === complemento.toString() || digitoControl === letrasControl[complemento];
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