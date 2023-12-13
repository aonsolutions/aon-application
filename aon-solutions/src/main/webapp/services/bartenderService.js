import { API_URL } from "../environments/environments";
import { postXml, post } from "./request";

//Genera un objeto Blob con los datos en un archivo XML
export const generateXml = (printer, tag, datos)  => {
    var texto = [];
    texto.push('<?xml version="1.0" encoding="UTF-8" ?>\n');
    texto.push('<XMLScript Version="2.0" Trusted="true">\n');
    texto.push('\t<Command Name="Print Document">\n');
    texto.push('\t<Print>\n');
    texto.push('\t<PrinterSetup>\n');

    texto.push('\t<Printer>');
    texto.push(writeXml(printer));
    texto.push('</Printer>\n');

    texto.push('\t<IdenticalCopiesOfLabel>');
    texto.push(writeXml("1"));
    texto.push('</IdenticalCopiesOfLabel>\n');
    
    texto.push('</PrinterSetup>\n');

    texto.push('\t<Format>');
    texto.push(writeXml(tag));
    texto.push('</Format>\n');

    for(let key in datos) {
        texto.push(`\t<NamedSubString Name="${key}">\n`);

        texto.push('\t<Value>');
        texto.push(writeXml(datos[key]));
        texto.push('</Value>\n');
    
        texto.push('</NamedSubString>\n');   
    }

    texto.push('</Print>\n');
    texto.push('</Command>\n');
    texto.push('</XMLScript>\n');
    return new Blob(texto, {
        type: 'application/xml'
    });
};


export const writeXml = (cadena) => {
    if (typeof cadena !== 'string') {
        return '';
    };
    cadena = cadena.replace('&', '&amp;')
        .replace('<', '&lt;')
        .replace('>', '&gt;')
        .replace('"', '&quot;');
    return cadena;
};

export const printDeliveryTag = (printer, tag, data) => {
    let xml = generateXml(printer, tag, data);
    let url = 'https://192.168.1.189/Integration/IntegracionWebService/Execute';
    postXml(url, xml);
};


export const sendDeliveryTag = (data) => post(`${API_URL}/bartender/email`, data);
