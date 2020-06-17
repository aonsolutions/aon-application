import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class MakroTest {
  public static checkInvoice(invoice: Invoice, makroInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(makroInvoice.status);
      expect(invoice.date).eql(makroInvoice.date);
      expect(invoice.reference).eq(makroInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && makroInvoice.receiver) {
        expect(invoice.receiver.name).eq(makroInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(makroInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(makroInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && makroInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(makroInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(makroInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(makroInvoice.receiver.address.city);
        }
      }
      // expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && makroInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(makroInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(makroInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(makroInvoice.details[i].base);
          expect(invoice.details[i].price).eq(makroInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && makroInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(makroInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(makroInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(makroInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(makroInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(makroInvoice.total);
       expect(invoice.finances).not.to.be.undefined;
       expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && makroInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(makroInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(makroInvoice.finances[i].amount);
        }
      }
    }
  }
  public static makroTest(): void {
    const makro1: Invoice = {
      status: InvoiceStatus.pending,
      date: new Date(2019, 1, 11, 0, 0),
      reference: '0/0(006)0007/(2019)042254',
      total: 344.78,
      receiver: {
        name: 'CZIPTAK ZSOFIA',
        document_country: 'ES',
        document: 'Y5549126E',
        address: {
          address: 'MEXICO 5',
          postal_code: '38660',
          city: 'ARONA',
        },
      },
      details: [
        {
          price: 5.95,
          quantity: 1,
          base: 5.95,
          discount: 0,
          description: 'TORTILLA WRAPS 30CM MC 18UD',
        },
        {
          price: 6.6,
          quantity: 1,
          base: 6.6,
          discount: 0,
          description: 'TORTILLA PATATA CUAD MC 750G',
        },
        {
          price: 18.13,
          quantity: 1,
          base: 18.13,
          discount: 0,
          description: 'CUADRIL NOVILLO URUGUAY ENFRIA',
        },
        {
          price: 17.19,
          quantity: 1,
          base: 17.19,
          discount: 0,
          description: 'CUADRIL NOVILLO URUGUAY ENFRIA',
        },
        {
          price: 8.93,
          quantity: 1,
          base: 8.93,
          discount: 0,
          description: 'PECHUGA POLLO EXT TIERNA AL VA',
        },
        {
          price: 8.82,
          quantity: 1,
          base: 8.82,
          discount: 0,
          description: 'PECHUGA POLLO EXT TIERNA AL VA',
        },
        {
          price: 9.64,
          quantity: 1,
          base: 9.64,
          discount: 0,
          description: 'PECHUGA POLLO EXT TIERNA AL VA',
        },
        {
          price: 10.16,
          quantity: 1,
          base: 10.16,
          discount: 0,
          description: 'PECHUGA POLLO EXT TIERNA AL VA',
        },
        {
          price: 7.86,
          quantity: 1,
          base: 7.86,
          discount: 0,
          description: 'PECHUGA POLLO EXT TIERNA AL VA',
        },
        {
          price: 9.71,
          quantity: 1,
          base: 9.71,
          discount: 0,
          description: 'PECHUGA POLLO EXT TIERNA AL VA',
        },
        {
          price: 9.6,
          quantity: 1,
          base: 9.6,
          discount: 0,
          description: 'NARANJA ZUMO 6/7 CJ/KG',
        },
        {
          price: 9.54,
          quantity: 1,
          base: 9.54,
          discount: 0,
          description: 'BATATA PZ/KG',
        },
        {
          price: 4.79,
          quantity: 1,
          base: 4.79,
          discount: 0,
          description: 'CEBOLLA MA 5KG',
        },
        {
          price: 7.99,
          quantity: 1,
          base: 7.99,
          discount: 0,
          description: 'NUEZ MONDADA MC 800G',
        },
        {
          price: 5.29,
          quantity: 1,
          base: 5.29,
          discount: 0,
          description: 'PIMTO PIQ TIRAS BAMBO 1900GNE',
        },
        {
          price: 10.9,
          quantity: 1,
          base: 10.9,
          discount: 0,
          description: 'ACT ESP FREIR SABORCHEF PT 10',
        },
        {
          price: 9.35,
          quantity: 1,
          base: 9.35,
          discount: 0,
          description: 'NACHOS JALAPEÑOS HERDEZ 2700G',
        },
        {
          price: 1.59,
          quantity: 3,
          base: 4.77,
          discount: 0,
          description: 'SERVILLETA MPRO 20X2 VERDE',
        },
        {
          price: 14.7,
          quantity: 2,
          base: 29.4,
          discount: 0,
          description: 'RUSTIDERA RECTANGULAR ASA MONO',
        },
        {
          price: 135.0,
          quantity: 1,
          base: 135.0,
          discount: 0,
          description: 'FREIDORA 8L MPRO HDF1108',
        },
        {
          price: 1.75,
          quantity: 1,
          base: 1.75,
          discount: 0,
          description: 'BOLSA ISOTERMICA 25L 52X52CM A',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 0.0,
          base: 78.14,
          percentage: 0.0,
        },
        {
          tax: TaxType.IVA,
          quota: 2.61,
          base: 87.08,
          percentage: 3.0,
        },
        {
          tax: TaxType.IVA,
          quota: 10.8,
          base: 166.15,
          percentage: 6.5,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 1, 11, 0, 0),
          amount: 344.78,
        },
      ],
    };

    const makro2: Invoice = {
      status: InvoiceStatus.pending,
      date: new Date(2019, 2, 16, 0, 0),
      reference: '0/0(058)0002/(2019)075114',
      total: 100.45,
      receiver: {
        name: 'CZIPTAK ZSOFIA',
        document_country: 'ES',
        document: 'Y5549126E',
        address: {
          address: 'MEXICO 5',
          postal_code: '38660',
          city: 'ARONA',
        },
      },
      details: [
        {
          price: 17.46,
          quantity: 1,
          base: 17.46,
          discount: 0,
          description: 'CUADRIL NOVILLO URUGUAY ENFRIA',
        },
        {
          price: 17.44,
          quantity: 1,
          base: 17.44,
          discount: 0,
          description: 'CUADRIL NOVILLO URUGUAY ENFRIA',
        },
        {
          price: 7.55,
          quantity: 1,
          base: 7.55,
          discount: 0,
          description: 'NARANJA ZUMO 6/7 CJ/KG',
        },
        {
          price: 22.95,
          quantity: 1,
          base: 22.95,
          discount: 0,
          description: 'PAPA ESPECIAL FREIR S/C 25KG',
        },
        {
          price: 5.69,
          quantity: 1,
          base: 5.69,
          discount: 0,
          description: 'ACT OLVE OLEOESTEPA 500ML TI',
        },
        {
          price: 1.3,
          quantity: 1,
          base: 1.3,
          discount: 0,
          description: 'AZUCAR MORENA CAÑAVERAL 1KG',
        },
        {
          price: 2.88,
          quantity: 2,
          base: 5.76,
          discount: 0,
          description: 'SIROPE JAR RIOBA CEREZ BT 70CL',
        },
        {
          price: 0.99,
          quantity: 6,
          base: 5.94,
          discount: 0,
          description: 'SERV NATURAL MPRO 20X20 150UD',
        },
        {
          price: 12.5,
          quantity: 1,
          base: 12.5,
          discount: 0,
          description: 'LOTE 3 HERMETICOS 2.8L',
        },
        {
          price: 0.25,
          quantity: 12,
          base: 3.0,
          discount: 0,
          description: 'VASO SIDRA 50CL',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 0.0,
          base: 71.09,
          percentage: 0.0,
        },
        {
          tax: TaxType.IVA,
          quota: 0.86,
          base: 28.5,
          percentage: 3.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 2, 16, 0, 0),
          amount: 100.45,
        },
      ],
    };

    const makro3: Invoice = {
      status: InvoiceStatus.pending,
      date: new Date(2019, 0, 15, 0, 0),
      reference: '0/0(058)0003/(2019)015187',
      total: 134.3,
      receiver: {
        name: 'CZIPTAK ZSOFIA',
        document_country: 'ES',
        document: 'Y5549126E',
        address: {
          address: 'MEXICO 5',
          postal_code: '38660',
          city: 'ARONA',
        },
      },
      details: [
        {
          price: 3.9,
          quantity: 2,
          base: 7.8,
          discount: 0,
          description: 'MANGO DADOS MC 1KG',
        },
        {
          price: 16.44,
          quantity: 1,
          base: 16.44,
          discount: 0,
          description: 'BOLA LOMO ENFRIADO',
        },

        {
          price: 14.11,
          quantity: 1,
          base: 14.11,
          discount: 0,
          description: 'CULATA VACA NAC VC',
        },
        {
          price: 13.85,
          quantity: 1,
          base: 13.85,
          discount: 0,
          description: 'CULATA VACA NAC VC',
        },
        {
          price: 11.16,
          quantity: 1,
          base: 11.16,
          discount: 0,
          description: 'PECHUGA POLLO EXT TIERNA AL VA',
        },

        {
          price: 10.51,
          quantity: 1,
          base: 10.51,
          discount: 0,
          description: 'PECHUGA POLLO EXT TIERNA AL VA',
        },
        {
          price: 11.73,
          quantity: 1,
          base: 11.73,
          discount: 0,
          description: 'PECHUGA POLLO EXT TIERNA AL VA',
        },

        {
          price: 11.37,
          quantity: 1,
          base: 11.37,
          discount: 0,
          description: 'PECHUGA POLLO EXT TIERNA AL VA',
        },

        {
          price: 5.98,
          quantity: 1,
          base: 5.98,
          discount: 0,
          description: 'CHAMPIÑON GRANEL CJ/KG',
        },
        {
          price: 16.25,
          quantity: 1,
          base: 16.25,
          discount: 0,
          description: 'PAPAS WHITES MC SC 25KG',
        },
        {
          price: 0.79,
          quantity: 1,
          base: 0.79,
          discount: 0,
          description: 'RABANITOS 4 GAMA LAVADOS TA 12',
        },

        {
          price: 3.27,
          quantity: 1,
          base: 3.27,
          discount: 0,
          description: 'MAIZ DULCE GRANO MC LT 1775GNE',
        },
        {
          price: 9.6,
          quantity: 1,
          base: 9.6,
          discount: 0,
          description: 'ACT GIRASOL PERCASOL 10L',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 0.0,
          base: 84.82,
          percentage: 0.0,
        },
        {
          tax: TaxType.IVA,
          quota: 1.44,
          base: 48.04,
          percentage: 3.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 0, 15, 0, 0),
          amount: 134.3,
        },
      ],

    };

    const makro4: Invoice = {
      status: InvoiceStatus.pending,
      date: new Date(2019, 2, 26, 0, 0),
      reference: '0/0(058)0051/(2019)085051',
      total: 202.87,
      receiver: {
        name: 'CZIPTAK ZSOFIA',
        document_country: 'ES',
        document: 'Y5549126E',
        address: {
          address: 'MEXICO 5',
          postal_code: '38660',
          city: 'ARONA',
        },
      },
      details: [
        {
          price: 6.74,
          quantity: 1,
          base: 6.74,
          discount: 0,
          description: 'Queso vaca-cabra MAKRO CHEF rulo 1kg',
        },
        {
          price: 78.55,
          quantity: 1,
          base: 78.55,
          discount: 0,
          description: 'Culata vaca nacional precio Kg',
        },
        {
          price: 15.84,
          quantity: 1,
          base: 15.84,
          discount: 0,
          description: 'Paleta de cerdo sin hueso precio kg',
        },
        {
          price: 70.41,
          quantity: 1,
          base: 70.41,
          discount: 0,
          description: 'Pechuga de pollo extra tierna precio Kg',
        },
        {
          price: 8.11,
          quantity: 1,
          base: 8.11,
          discount: 0,
          description: 'Torreznillos fritos ibéricos 800g bolsa',
        },
        {
          price: 3.29,
          quantity: 3,
          base: 9.87,
          discount: 0,
          description: 'Alubia cocida roja MAKRO CHEF lata 1600gne',
        },
        {
          price: 5.82,
          quantity: 1,
          base: 5.82,
          discount: 0,
          description: 'Pimiento del piquillo tiras BAMBOLEO lata 1900gne',
        },
        {
          price: 2.4,
          quantity: 2,
          base: 4.8,
          discount: 0,
          description: 'Salsa barbacoa CHOVÍ botella 950g',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 0.0,
          base: 109.24,
          percentage: 0.0,
        },
        {
          tax: TaxType.IVA,
          quota: 2.73,
          base: 90.9,
          percentage: 3.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 2, 26, 0, 0),
          amount: 202.87,
        },
      ],
    };

    const makro5: Invoice = {
      status: InvoiceStatus.pending,
      date: new Date(2019, 2, 19, 0, 0),
      reference: '0/0(058)0001/(2019)078027',
      total: 66.38,
      receiver: {
        name: 'CZIPTAK ZSOFIA',
        document_country: 'ES',
        document: 'Y5549126E',
        address: {
          address: 'MEXICO 5',
          postal_code: '38660',
          city: 'ARONA',
        },
      },
      details: [
        {
          price: 3.75,
          quantity: 1,
          base: 3.75,
          discount: 0,
          description: 'TORTILLA C/CEBOL FCA BENIS 1KG',
        },
        {
          price: 14.01,
          quantity: 1,
          base: 14.01,
          discount: 0,
          description: 'CUADRIL NOVILLO URUGUAY ENFRIA',
        },
        {
          price: 16.75,
          quantity: 1,
          base: 16.75,
          discount: 0,
          description: 'CUADRIL NOVILLO URUGUAY ENFRIA',
        },
        {
          price: 16.68,
          quantity: 1,
          base: 16.68,
          discount: 0,
          description: 'VACIO AÑOJO VACÍO',
        },
        {
          price: 15.08,
          quantity: 1,
          base: 15.08,
          discount: 0,
          description: 'VACIO AÑOJO VACÍO',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 0.0,
          base: 62.52,
          percentage: 0.0,
        },
        {
          tax: TaxType.IVA,
          quota: 0.11,
          base: 3.75,
          percentage: 3.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 2, 19, 0, 0),
          amount: 66.38,
        },
      ],
      
    };

    describe('TEDI PDF PARSER TEST [MAKRO INVOICES]', () => {
      it('PARSE MAKRO INVOICE [Makro_1.pdf]', done => {
        const filename = 'test/resources/Makro_1.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, makro1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE MAKRO INVOICE [Makro_2.pdf]', done => {
        const filename = 'test/resources/Makro_2.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, makro2);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE MAKRO INVOICE [Makro_3.pdf]', done => {
        const filename = 'test/resources/Makro_3.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, makro3);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE MAKRO INVOICE [Makro_4.pdf]', done => {
        const filename = 'test/resources/Makro_4.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, makro4);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE MAKRO INVOICE [Makro_5.pdf]', done => {
        const filename = 'test/resources/Makro_5.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, makro5);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
    });
  }
}
