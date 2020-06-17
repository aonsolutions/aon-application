import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType, TediString } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//
//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |  Naturgy Iberia, S.A. | Gas Natural Servicios SDG, S.A                                                   																|
// |  Fecha de emisión: dd.MM.yyyy                                             																|
// |  Nº factura: REXXXXXXXXXXXXXX                                             																|
// |                                                                           																|
// |  Nombre: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                           																|
// |  Dirección suministro: XXXXXXXXXXXXXXXXXXXXXXXX                           																|
// |  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                           																|
// |  Nº Referencia: 99999999                                                  																|
// |  Nº cliente: 99999999                                                     																|
// |  NIF: XX - X99999999                                                      																|
// |  Dirección: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                           																|
// |  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                           																|
// |  Entidad: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                           																|
// |  Datos bancarios: IBAN XX99 9999 9999 9999 9999 ****                      																|
// |  Fecha de cargo: dd.MM.yyyy                                               																|
// |                                                                           																|
// |  Esta factura será cargada en cuenta siguiendo el mandato 000048467932 de fecha dd.MM.yyyy               |
// |                                                                           																|
// |   ¿Son correctos sus datos personales?                                    																|
// |   Puede actualizarlos online en su Área Privada de la web entrando en www.naturgy.es/misdatos     				|
// |                                                                           																|
// |   Total a pagar                                                                        				999,99 €	|
// |           Electricidad PLAN NEGOCIO A MEDIDA Contrato: 166029076 ......................................  |
// |           Del dd.MM.yyyy al dd.MMM.yyyy (99 días = 99,999999 meses)                                      |
// |                                                                           																|
// |           Consumo electricidad punta                                      																|
// |           		  Período de dd.MM.yyyy a dd.MM.yyyy   		999 kWh       999,999999 €/kWh          999,99 €  |
// |           		  Período de dd.MM.yyyy a dd.MM.yyyy   		999 kWh       999,999999 €/kWh          999,99 €  |
// |           Consumo electricidad llano                                      																|
// |           		  Período de dd.MM.yyyy a dd.MM.yyyy   		999 kWh       999,999999 €/kWh          999,99 €  |
// |           		  Período de dd.MM.yyyy a dd.MM.yyyy   		999 kWh       999,999999 €/kWh          999,99 €  |
// |           Consumo electricidad valle                                      																|
// |           		  Período de dd.MM.yyyy a dd.MM.yyyy   		999 kWh       999,999999 €/kWh          999,99 €  |
// |           		  Período de dd.MM.yyyy a dd.MM.yyyy   		999 kWh       999,999999 €/kWh          999,99 €  |
// |           Término potencia punta (99.999 kW)                                      												|
// |           		  Período de dd.MM.yyyy a dd.MM.yyyy   		999 días      999,999999 €/día          999,99 €  |
// |           		  Período de dd.MM.yyyy a dd.MM.yyyy   		999 días      999,999999 €/día          999,99 €  |
// |          ....                                                                 													  |
// |                                                                           																|
// |           Subtotal                                                                							999,99 €  |
// |           Impuesto Electricidad                     		999 kWh       999,999999 €/kWh          999,99 €  |
// |           Otros Conceptos  elictricidad                                     															|
// |           		  Alquiler de contador   		              999 días       999,999999 €/día         999,99 €  |
// |                                                                           																|
// |           Total electricidad                                                                		999,99 €  |
// |                                                                           																|
// |                                                                           																|
// |                                                                           																|
// |          Base Imponible                                                                 		    999,99 €	|
// |          IVA 99%                                                          											999,99 €  |
// |          Total factura                                                                 				999,99 €	|
// |                                                                           																|
// | _________________________________________________________________________________________________________|

export class Naturgy {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;
    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;

    let i = 0;
    for (i = 0; i < pages.length; i++) {
      const page0: PDFExtractPage = pages[i];

      try {
        // TODO: sort content by x and y coordinates ?
        content0 = page0.content;
        if (content0) {
          TediPDFParserUtils.sort(content0);
          line0 = TediPDFParserUtils.getLine(content0);
          if (line0) {
            let match;
            do {
              // NATURGY.
              match = line0.str.match(/\s*www.naturgy.es/i);
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
      throw new TediUnknownInvoiceFormatError('Not Naturgy invoice');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'AVENIDA SAN LUIS, 77',
          postal_code: '28033',
          city: 'MADRID',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'A08431090',
        name: 'NATURGY IBERIA, S.A',
      },
    };
    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;

    let index = 0;

    // Fecha de emisión: dd.MM.yyyy
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      const match = line.str.match(/Fecha\s*de\s*emisión\s*:\s*(\d+).(\d+).(\d+)/i);
      if (match != null) {
        const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
        invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
      }
    }

    // Fecha de emisión: dd.MM.yyyy
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      const match = line.str.match(/Fecha\s*de\s*emisión\s*:\s*(\d+).(\d+).(\d+)/i);
      if (match != null) {
        const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
        invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
      }
    }

    // Nº factura: REXXXXXXXXXXXXXX
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      const match = line.str.match(/N.\s*factura\s*:\s*(\w+)/i);
      if (match != null) {
        invoice.reference = match[1];
      }
    }

    // Nombre: XXXXXXXXXXXXXXXXXXXX
    invoice.receiver = {};
    let nameMatch;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        nameMatch = line.str.match(/Nombre\s*:\s*(.+)/i);
      }
    } while (!nameMatch);
    if (nameMatch != null) {
      invoice.receiver.name = nameMatch[1];
    }

    // Dirección suministro: XXXXXXXXXXXXXXXXXXXX
    let addr = '';
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Direcci.n\s*suministro\s*:\s*(.+)/i);
        if (match != null) {
          addr += ' ' + match[1];
          break;
        }
      }
    } while (line);
    invoice.receiver.address = { address: TediString.trim(addr) };

    // NIF: XX - X99999999
    let documentMatch;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        documentMatch = line.str.match(/NIF\s*:\s*(\w+)\s*-\s*(\w+)/i);
        if (documentMatch) {
          invoice.receiver.document_country = documentMatch[1];
          invoice.receiver.document = documentMatch[2];
        }
      }
    } while (!documentMatch);

    // Datos bancarios: IBAN XX99 9999 9999 9999 9999 ****
    invoice.finances = [{}];

    let ibanMatch;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        ibanMatch = line.str.match(/Datos\s*bancarios\s*:\s*IBAN\s*(.*)/i);
        if (ibanMatch) {
          invoice.finances[0] = { iban: ibanMatch[1] };
        }
      }
    } while (!ibanMatch);

    // Fecha de cargo: dd.MM.yyyy
    let dueDateMatch;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        dueDateMatch = line.str.match(/Fecha\s*de\s*cargo\s*:\s*(\d+).(\d+).(\d+)/i);
        if (dueDateMatch) {
          const dateStr = dueDateMatch[1] + '/' + dueDateMatch[2] + '/' + dueDateMatch[3];
          invoice.finances[0].due_date = moment(dateStr, 'DD/MM/YYYY').toDate();
        }
      }
    } while (!dueDateMatch);

    // Total a pagar     999,99 €
    let totalMatch;
    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        totalMatch = line.str.match(/Total\s*a\s*pagar\s*(-?\d+\s*(,\s*\d+)?)/i);
        if (totalMatch) {
          mainTotal = TediPDFParserUtils.string2Number(totalMatch[1]);
          invoice.total = mainTotal;
          invoice.finances[0].amount = mainTotal;
        }
      }
    } while (!totalMatch);
    /* Comment details

    // Consumo electricidad XXXX                   999 kWh       999,999999 €/kWh          999,99 €
    // Término potencia XXXX                       999 días      999,999999 €/kWh día      999,99 €
    invoice.details = [];
    let detailMatch;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        detailMatch = line.str.match(
          /(^\D+(\([^\)]*\))?)\s*(-?\d+\s*(,\s*\d+)?)\s*\D+\s*(-?\d+\s*(,\s*\d+)?)\s*[^0-9-]+\s*(-?\d+\s*(,\s*\d+)?)\s*\D+$/i,
        );
        if (detailMatch) {
          invoice.details.push({
            description: TediString.trim(detailMatch[1]),
            quantity: TediPDFParserUtils.string2Number(detailMatch[3]),
            price: TediPDFParserUtils.string2Number(detailMatch[5]),
            base: TediPDFParserUtils.string2Number(detailMatch[7]),
            discount: 0,
          });
        } else {
          detailMatch = line.str.match(/Subtotal\s*(-?\d+\s*(,\s*\d+)?)/i);
          if (detailMatch) {
            break;
          }
        }
      }
    } while (line);

    // Consumo electricidad punta
    // 			Período de dd.MM.yyyy a dd.MM.yyyy   		999 kWh       999,999999 €/kWh          999,99 €
    // 			Período de dd.MM.yyyy a dd.MM.yyyy   		999 kWh       999,999999 €/kWh          999,99 €

    // Subtotal     999,99 €
    // do {
    // 	line = getLine(content, line.index);
    // 	match = line.str.match(/Subtotal\s*(-?\d+\s*(,\s*\d+)?)/i);
    // } while ( !match );

    if (invoice.details.length === 0 && detailMatch) {
      invoice.details.push({
        description: 'Consumos',
        quantity: 1,
        price: TediPDFParserUtils.string2Number(detailMatch[1]),
        discount: 0,
        base: TediPDFParserUtils.string2Number(detailMatch[1]),
        vat: 21.0,
      });
    }

    let detail2Match;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        detail2Match = line.str.match(/(\D+)(-?\d+\s*(,\s*\d+)?)\D+(-?\d+\s*(,\s*\d+)?)[^0-9-]+(-?\d+\s*(,\s*\d+)?)/i);
        if (detail2Match) {
          invoice.details.push({
            description: TediString.trim(detail2Match[1]),
            price: TediPDFParserUtils.string2Number(detail2Match[5]),
            quantity: TediPDFParserUtils.string2Number(detail2Match[2]),
            base: TediPDFParserUtils.string2Number(detail2Match[6]),
          });
        } else {
          detail2Match = line.str.match(/Total electricidad\s*(-?\d+\s*(,\s*\d+)?)/i);
          if (detail2Match) {
            break;
          }
        }
      }
    } while (line);
    
    End details*/

    // Base imponible     999,99 €
    let tbMatch;
    invoice.taxes = [];
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        tbMatch = line.str.match(/Base\s*imponible\s*(-?\d+\s*(,\s*\d+)?)/i);
      }
      if (tbMatch) {
        break;
      }
    } while (line);
    if (tbMatch) {
      const taxableBase = TediPDFParserUtils.string2Number(tbMatch[1]);
      let taxMatch;
      do {
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          taxMatch = line.str.match(/IVA\s*(-?\d+(,\d+)?)\s*%\s*(-?\d+\s*(,\s*\d+)?)/i);
        }
      } while (!taxMatch);
      const taxPercent = TediPDFParserUtils.string2Number(taxMatch[1]);
      const taxQuota = TediPDFParserUtils.string2Number(taxMatch[3]);
      invoice.taxes = [
        {
          tax: TaxType.IVA,
          quota: taxQuota,
          base: taxableBase,
          percentage: taxPercent,
        },
      ];
      // invoice.details.forEach(detail => (detail.vat = taxPercent));
    }

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);
    return invoice;
  }
}
