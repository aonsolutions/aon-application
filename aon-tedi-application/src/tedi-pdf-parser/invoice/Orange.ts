import moment = require('moment');
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType, TediString } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |                                                        Titular: XXXXXXXXXXXXXXXXXXXXXXXXX								|
// |                                                        CIF: X99999999     																|
// |     Tu factura                                         XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX								|
// |                                                        99999 XXXXXXXXXXXXXXXXXXXXXXXXXXXX								|
// |                                                        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX								|
// |                                                                           																|
// |    ¡Hola!                                                                 																|
// |    Este mes tu factura es de 999,99 €                                     																|
// |    Nº FACTURA: XXXXXXXXXXXXXXXXXXXXXX                                     																|
// |    Del 01/10/2018 al dd/MM/yyyy                                           																|
// |    Fecha de cierre: dd/MM/yyyy                                                                       		|
// |    Fecha de factura: dd/MM/yyyy                                                                       		|
// |    Fecha de vencimiento: dd/MM/yyyy                                                                      |
// |                                                                           																|
// |    Domiciliación bancaria                                                                       					|
// |    No de cuenta: ******************                                                                 			|
// |                                                                           																|
// |      Resumen de los productos										Líneas		Cuota (€)		Adicional (€)		Total (€)         |
// |                                               999999999                            											|
// |         XXXXXXXXXXXXXXXXXXXXX                 999999999      999,99          999,99      999,99					|
// |                                               999999999                            											|
// |     ____________________________________________________________________________________________					|
// |                                                                           																|
// |         XXXXXXXXXXXXXXXXXXXXX                 999999999      999,99          999,99      999,99					|
// |                                                                           																|
// |     Otros conceptos                                                                      								|
// |                                                                           																|
// |     XXXXXXXXXXXXXXXXXXXXXXXXX                                                            999,99					|
// |                                                                           																|
// |		Tu factura mes a mes                      Desglose fiscal																							|
// |                                              Servicios prestados por Orange															|
// |                                              Base imponible															999,99	        |
// |                                              Impuestos (IVA 21%)                             						|
// |                                              Otros (impuestos incluidos)                 999,99	        |
// |                                                                           																|
// |                                              Compra de dispositivos a plazos 						999,99	        |
// |                                              Total a pagar										 						999,99	        |
// |                                                                           																|
// | _________________________________________________________________________________________________________|

export class Orange {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;

    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;

    let i = 0;
    let match;

    for (i = 0; i < pages.length; i++) {
      const page0: PDFExtractPage = pages[i];

      try {
        // TODO: sort content by x and y coordinates ?
        content0 = page0.content;
        if (content0) {
          TediPDFParserUtils.sort(content0);
          line0 = TediPDFParserUtils.getLine(content0);
          if (line0) {
            do {
              // ORANGE.
              match = line0.str.match(/\s*Madrid\s*,\s*tomo\s*13.183\s*,\s*folio\s*129\s*,\s*hoja\s*M-213468\s*,\s*CIF\s*A-82009812/i);
              if (match) {
                break;
              }
              line0 = TediPDFParserUtils.getLine(content0, line0.index);
            } while (line0);
            if (match) {
              break;
            }
          }
        }
      } catch (e) {
        // tslint:disable-next-line: no-console
        console.error(' Catched!!' + e);

        throw new TediUnknownInvoiceFormatError(e);
      }
    }
    if (i === pages.length || !line0 || !content0) {
      throw new TediUnknownInvoiceFormatError('Not ORANGE invoice');
    }

    for (i = 0; i < pages.length; i++) {
      const page: PDFExtractPage = pages[i];
      try {
        // TODO: sort content by x and y coordinates ?
        content0 = page.content;
        if (content0) {
          TediPDFParserUtils.sort(content0);
          line0 = TediPDFParserUtils.getLine(content0);
          if (line0) {
            do {
              // IBERDROLA CLIENTES, S.A.U.
              match = line0.str.match(/^Titular\s*:\s*(.*)/i);
              if (match) {
                break;
              }
              line0 = TediPDFParserUtils.getLine(content0, line0.index);
            } while (line0);
            if (match) {
              break;
            }
          }
        }
      } catch (e) {
        // tslint:disable-next-line: no-console
        console.error(' Catched!!' + e);
      }
    }
    if (!line0) {
      throw new Error('Error line');
    }

    const receiverName = match ? match[1] : '';

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'PQUE. EMP. LA FINCA,Pº DEL CLUB DEPORTIVO, 1, EDIF.8',
          postal_code: '28223',
          city: 'POZUELO DE ALARCÓN',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'A82009812',
        name: 'ORANGE ESPAGNE, S.A.U',
      },
    };

    invoice.receiver = {
      name: receiverName,
    };

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;

    let index = line.index;

    // Número de factura 99999999999999999

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/^CIF\s*:\s*(.*)/i);
      if (match) {
        invoice.receiver.document = match[1];
        invoice.receiver.document_country = 'ES';
      }
    }

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      match = line.str.match(/^CIF\s*:\s*(.*)/i);
      if (match) {
        invoice.receiver.document = match[1];
        invoice.receiver.document_country = 'ES';
      }
    }

    let addr = '';
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Hola/i);
        if (match) {
          break;
        }
        if (!line.str.match(/Tu\s*Factura/i)) {
          addr += (addr.length > 0 ? ', ' : '') + line.str;
        }
      }
    } while (line);
    invoice.receiver.address = { address: TediString.polish(addr) };

    invoice.finances = [{}];
    let mainTotal: number = 0;
    do {
      // Este mes tu factura es de 999,99 €
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Este\s*mes\s*tu\s*factura\s*es\s*de\s*(-?\d+(,\d+)?)/i);
        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[1]);
          invoice.total = mainTotal;
          invoice.finances[0].amount = mainTotal;
          break;
        }
      }
    } while (line);

    do {
      // Nº FACTURA: XXXXXXXXXXXXXXXXXXXXXX
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/N.\s*FACTURA\s*:\s*(.*)/i);
        if (match) {
          invoice.reference = match[1];
          break;
        }
      }
    } while (line);

    do {
      // Fecha de factura: dd/MM/yyyy
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Fecha\s*de\s*factura\s*:\s*(\d+\/\d+\/\d+)/i);
        if (match) {
          const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
          invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
          break;
        }
      }
    } while (line);

    do {
      // Fecha de factura: dd/MM/yyyy
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Fecha\s*de\s*vencimiento\s*:\s*(\d+\/\d+\/\d+)/i);
        if (match) {
          const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
          invoice.finances[0].due_date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
          break;
        }
      }
    } while (line);

    do {
      // Fecha de factura: dd/MM/yyyy
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/N.\s*de\s*cuenta\s*:\s*(.*)/i);
        if (match) {
          invoice.finances[0].iban = match[1];
          break;
        }
      }
    } while (line);

    do {
      // Resumen de los productos		Líneas		Cuota (€)		Adicional (€)		Total (€)
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Resum.n\s*de\s*los\s*productos.*/i);
        if (match) {
          break;
        }
      }
    } while (
      line
    ); /* Comment i)) {
          break;
        }
        // 999999999      999,99          999,99      999,99
        match = line.str.match(/(\d+)\s+(-?\d+(,\d+)?)\s+(-?\d+(,\d+)?)\s+(-?\d+(,\d+)?)/i);
        if (match) {
          line = TediPDFParserUtils.getLine(content, index);
          if (line) {
            index = line.index;
            invoice.details.push({
              price: TediPDFParserUtils.string2Number(match[6]),
              quantity: 1,
              base: TediPDFParserUtils.string2Number(match[6]),
              discount: 0,
              description: line.str,
            });
          }
        } else {
          // console.log("Skipped unknown : " + line.str  );
        }
      }
    } while (line);

    // Desglose fiscal
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        if (line.str.match(/Desglose\s*fiscal/i)) {
          break;
        }
        match = line.str.match(/(-?\d+(,\d+)?)/i);
        if (match) {
          line = TediPDFParserUtils.getLine(content, index);
          if (line) {
            index = line.index;
            invoice.details.push({
              price: TediPDFParserUtils.string2Number(match[1]),
              quantity: 1,
              base: TediPDFParserUtils.string2Number(match[1]),
              discount: 0,
              description: line.str,
            });
          }
        }
      }
    } while (line);
    End Details*/

    /* Comment details
    invoice.details = [];

    do {
      // Otros conceptos
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        if (line.str.match(/Otros\s*conceptos.*/ invoice.taxes = [
      {
        tax: TaxType.IVA,
        base: 0,
        percentage: 0,
        quota: 0,
      },
    ];
    // Base imponible 999,99
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Base\s*imponible\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.taxes[0].base = TediPDFParserUtils.string2Number(match[1]);
          break;
        }
      }
    } while (line);

    // Impuestos IVA 999,99
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Impuestos\s*\(\s*IVA\s*(-?\d+(,\d+)?)\s*%\s*\)\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.taxes[0].percentage = TediPDFParserUtils.string2Number(match[1]);
          invoice.taxes[0].quota = TediPDFParserUtils.string2Number(match[3]);
          break;
        }
      }
    } while (line);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
