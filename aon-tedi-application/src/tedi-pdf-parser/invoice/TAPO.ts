import moment = require('moment');
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType, TediString } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//                                                    ________________________________________________________
//                                                   |   NOMBRE CLIENTE                  	                    |
//                                                   |   DIRECCIÓN                                            |
//                                                   |                                                        |
//                                                   |________________________________________________________|
//  ___________________________________________________________________________________________________________
// |                                                                         														   		|
// | FACTURA  Nº XXXXXXXX           FECHA        XX-XX-XXXX              CIF/DNI        XXXXXXXXXXXXX         |
// |__________________________________________________________________________________________________________|
// |                                CONCEPTO                                   HONORARIOS      SUPLIDOS       |
// |                                                                                                      		|
// |       XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                                   xxxx,xxx                      |
// |       XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                                   xxxx,xxx                      |
// |                                                                                                          |
// |                                                                                                      		|                                       ||    				     ||									|
// |                                                                            				     									|
// | _________________________________________________________________________________________________________|
// |   T.HONORARIOS      T.SUPLIDOS     %IVA      CUOTA IVA      %RET     CUOTA RET        	TOTAL FACTURA 		|
// |                                                                 				              									  |
// |    XX,XX                           XX,X%      X,XX                     				      			XX,XX € 		  |
// |                                                                  				            									  |
// |__________________________________________________________________________________________________________|

export class TAPO {
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
              // TUASESORIA CLIENTES, S.A.U.
              match = line0.str.match(/@tuasesoriapersonalonline.com/i);
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
      throw new TediUnknownInvoiceFormatError('No Tu Asesoria Personal Online invoice');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'C/ PRINCESA 24',
          postal_code: '28008',
          city: 'MADRID',
          province: 'MADRID',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'B87266656',
        name: 'Tu Asesoría Personal Online',
      },
    };

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = 0;

    // Receiver name
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/(.*)/i);
    }

    const receiverName = match ? match[0] : '';
    invoice.receiver = {
      name: receiverName,
    };

    // Invoice Address
    invoice.receiver.address = {};
    let addr = '';
    let cp = '';
    let ci = '';
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Factura\s*N./i);
        if (match) {
          break;
        }
        addr += (addr.length > 0 ? ', ' : '') + line.str;
        match = line.str.match(/^(\d{5,5})\s*(.*)/i);
        if (match) {
          cp = match[1];
          ci = match[2];
        }
      }
    } while (line);

    invoice.receiver.address = {
      address: TediString.polish(addr),
      postal_code: cp,
      city: ci,
    };

    // Invoice referenc: Nº Factura 18701909
    if (line) {
      index = line.index;
      match = line.str.match(/FACTURA\s*N.\s*(\w+)\s/i);
      if (match) {
        invoice.reference = match[1];
      }
    }

    // Invoice date: Fecha dd/MM/yyyy
    invoice.finances = [{}];
    if (line) {
      index = line.index;
      match = line.str.match(/Fecha\s*(\d+).(\d+).(\d+)/i);
      if (match) {
        const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
        invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
        invoice.finances[0].due_date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
      }
    }

    // CIF/DNI: ESX574567C
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        if (line.str.match(/CONCEPTO/i)) {
          break;
        }
        match = line.str.match(/(.*)/i);

        if (match) {
          invoice.receiver.document = match[1];
          invoice.receiver.document_country = 'ES';
        }
      }
    } while (line);

    // Invoice details: SERVICIO FISCAL CONTABLE        50,00
    invoice.details = [];
    let priceMatch: any;
    let descriptionMatch: any;
    line = TediPDFParserUtils.getLine(content, index);
    while (line && line.str.match(/T.HONORARIOS/i) == null) {
      index = line.index;
      // If match price
      if (line.str.match(/(-?\d+(,\d+)?)/i)) {
        priceMatch = line.str.match(/(-?\d+(,\d+)?)/i);
        // If match description
      } else if (line.str.match(/(.*)/i)) {
        descriptionMatch = line.str.match(/(.*)/i);
      }

      if (descriptionMatch && priceMatch) {
        // COMMMENT INVOICE DETAILS
        // invoice.details.push({
        //   price: TediPDFParserUtils.string2Number(priceMatch[1]),
        //   quantity: 1,
        //   base: TediPDFParserUtils.string2Number(priceMatch[1]),
        //   discount: 0,
        //   description: descriptionMatch[1],
        // });

        // Reset matches for next product
        descriptionMatch = null;
        priceMatch = null;
      }
      line = TediPDFParserUtils.getLine(content, index);
    }

    // Invoice total:
    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*(-?\d+(,\d+)?)\s*/i);
        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[1]);
          invoice.total = mainTotal;
          invoice.finances[0].amount = mainTotal;
          break;
        }
      }
    } while (line);

    invoice.taxes = [
      {
        tax: TaxType.IVA,
        base: 0,
        percentage: 0,
        quota: 0,
      },
    ];

    // Taxes (or base and quota)
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(-?\d+(,\d+)?)\s*(-?\d+(,\d+)?)/i);

        if (match) {
          // Match base and quote 30,00   6,30
          if (match[4]) {
            invoice.taxes[0].base = TediPDFParserUtils.string2Number(match[1]);
            invoice.taxes[0].quota = TediPDFParserUtils.string2Number(match[3]);
            // Match only quote  6,30
          } else {
            invoice.taxes[0].quota = TediPDFParserUtils.string2Number(match[0]);

            // Search base
            do {
              line = TediPDFParserUtils.getLine(content, index);
              if (line) {
                index = line.index;
                match = line.str.match(/\s*(-?\d+(,\d+)?)\s*/i);

                if (match) {
                  invoice.taxes[0].base = TediPDFParserUtils.string2Number(match[1]);
                  break;
                }
              }
            } while (line);
          }
          break;
        }
      }
    } while (line);

    // Tax percentage
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.taxes[0].percentage = TediPDFParserUtils.string2Number(match[1]);
          break;
        }
      }
    } while (line);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
