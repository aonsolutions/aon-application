//Genera un objeto Blob con los datos en un archivo XML
export const generateXml = (datos)  => {
    var texto = [];
    texto.push('<?xml version="1.0" encoding="UTF-8" ?>\n');
    texto.push('<XMLScript Version="2.0">\n');
    texto.push('\t<Command Name="Job1">\n');
    texto.push('\t<Print>\n');
    texto.push('\t<PrinterSetup>\n');

    texto.push('\t<Printer>');
    texto.push(writeXml("EasyCoder PF4i (203 dpi) - DP"));
    texto.push('</Printer>\n');

    texto.push('\t<IdenticalCopiesOfLabel>');
    texto.push(writeXml("1"));
    texto.push('</IdenticalCopiesOfLabel>\n');
    
    texto.push('</PrinterSetup>\n');

    texto.push('\t<Format>');
    texto.push(writeXml("demo.btw"));
    texto.push('</Format>\n');


    // for

    let var1 = 'Lote';
    let value1 = 'DATO VALUE 1';
    texto.push(`\t<NamedSubString Name="${var1}">\n`);

    texto.push('\t<Value>');
    texto.push(writeXml(value1));
    texto.push('</Value>\n');

    texto.push('</NamedSubString>\n');

    // end for

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