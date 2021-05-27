import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class AmazonTest {
  public static checkInvoice(invoice: Invoice, amazonInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(amazonInvoice.status);
      expect(invoice.date).eql(amazonInvoice.date);
      expect(invoice.reference).eq(amazonInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && amazonInvoice.receiver) {
        expect(invoice.receiver.name).eq(amazonInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(amazonInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(amazonInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && amazonInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(amazonInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(amazonInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(amazonInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && amazonInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(amazonInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(amazonInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(amazonInvoice.details[i].base);
          expect(invoice.details[i].price).eq(amazonInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && amazonInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(amazonInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(amazonInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(amazonInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(amazonInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(amazonInvoice.total);
     // expect(invoice.finances).not.to.be.undefined;
     // expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && amazonInvoice.finances) {
       for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(amazonInvoice.finances[i].due_date);
         expect(invoice.finances[i].amount).eq(amazonInvoice.finances[i].amount);
       }
      }
    }
  }

  public static amazonTest(): void {
    const amazon1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 0, 10),
      reference: 'W0184081H',
      total: 14.0,
      receiver: {
        name: 'Alejandra Jimenez González',
        document_country: 'ES',
        document: '',
        address: {
          address: 'C/Santísima Trinidad, 30, planta 7,puerta 8',
          postal_code: '28010',
          city: 'Madrid',
        },
      },
      details: [
        {
          price: 14.0,
          quantity: 1,
          base: 11.57,
          discount: 0,
          description: 'Agenda 2019 semana vista apaisada español',
        }
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 2.43,
          base: 11.57,
          percentage: 21.0,
        },
      ],
    };
    const amazon2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 18, 0, 0),
      reference: 'W0184081H',
      total: 58.88,
      receiver: {
        name: 'JORGE MONTES BENITO',
        document_country: 'ES',
        document: '',
        address: {
          address: 'CONVENTO 19',
          postal_code: '37210',
          city: 'SALAMANCA',
        },
      },
      details: [
        {
          price: 32.9,
          quantity: 1,
          base: 27.19,
          discount: 0,
          description: 'TP-Link TL-WA901ND - Punto de acceso inalámbrico/Extensor de red WiFi (N450 Mbps, 3 Antenas, Power over Ethernet, WPS), blanco',
        },
        {
          price: 16.99,
          quantity: 1,
          base: 14.04,
          discount: 0,
          description: 'TP-Link TL-SG105 Switch Gigabit Conmutador de red 5 puertos para sobremesas, 1000Mbps, Acero inoxidable, IGMP Snooping, QoS',
        },
        {
          price: 8.99,
          quantity: 1,
          base: 7.43,
          discount: 0,
          description: 'Memoria Flash USB SanDisk Cruzer Blade 16 GB con USB 2.0 Pack Triple',
        }
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 10.22,
          base: 48.66,
          percentage: 21.0,
        },
      ],
    };
    const amazon3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 3, 26, 0, 0),
      reference: 'W0184081H',
      total: 31.7,
      receiver: {
        name: 'Grupo Tecnologico ARBINOVA SL',
        document_country: 'ES',
        document: 'ESB32440927',
        address: {
          address: 'Calle Valdomiño nº1',
          postal_code: '32890',
          city: 'orense',
        },
      },
      details: [
        {
          price: 31.7,
          quantity: 1,
          base: 26.2,
          discount: 0,
          description: 'triplast 13 x 19 pulgadas – Bolsa de plástico para envíos postales gris (200 unidades)',
        }
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 5.5,
          base: 26.2,
          percentage: 21.0,
        },
        {
          tax: TaxType.IVA,
          base: -31.7,
          percentage: 0,
          quota: 0
        }
      ],
    };
    const amazon4: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 0, 8, 0, 0),
      reference: 'W0184081H',
      total: 259.82,
      receiver: {
        name: 'Santiago Soria Lopez',
        document_country: 'ES',
        document: '22578233F',
        address: {
          address: 'Avenida Doctor Waksman 35, 6º, B',
          postal_code: '46006',
          city: 'Valencia',
        },
      },
      details: [
        {
          price: 70.9,
          quantity: 1,
          base: 58.6,
          discount: 0,
          description: 'Crucial CT2K4G4DFS824A - Kit de Memoria RAM de 8 GB (4 GB x 2, DDR4, 2400 MT/s, PC4-19200, SR x8, DIMM, 288-Pin)',
        },
        {
          price: 188.92,
          quantity: 1,
          base: 156.1,
          discount: 0,
          description: 'Intel Pentium Core i5-6600 - Microprocesador (3.30 GHz 6M Box Skylake) Color Plata',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 45.09,
          base: 214.73,
          percentage: 21.0,
        },
      ],
    };
    const amazon5: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 9, 0, 0),
      reference: 'W0184081H',
      total: 13.45,
      receiver: {
        name: 'SOCRAM SERVICIOS DE LIMPIEZA S.L.',
        document_country: 'ES',
        document: 'B67349274',
        address: {
          address: 'Carrer Sant Joan Bosco 36 Ecochelimpio',
          postal_code: '08830',
          city: 'Barcelona',
        },
      },
      details: [
        {
          price: 13.45,
          quantity: 1,
          base: 11.12,
          discount: 0,
          description: 'GreenBlue - GB104 Temporizador digital, planificador semanal, DIN 16A, Carril DIN, panel de control',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 2.33,
          base: 11.12,
          percentage: 21.0,
        },
      ],
     
    };
    const amazon6: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 11, 28, 0, 0),
      reference: 'W0184081H',
      total: 19.9,
      receiver: {
        name: 'MULTISERVEI ALT CAMP S.L',
        document_country: 'ES',
        document: 'B55719207',
        address: {
          address: 'Calle Crespia 87',
          postal_code: '43811',
          city: 'Tarragona',
        },
      },
      details: [
        {
          price: 19.9,
          quantity: 1,
          base: 16.45,
          discount: 0,
          description: 'Philips Barbero MG3730/15 - Recortador de Barba y Precisión 8 en 1, Cuchillas autoafilables, Incluye Funda de Viaje',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 3.45,
          base: 16.45,
          percentage: 21.0,
        },
      ],
     
    };

    describe('TEDI PDF PARSER TEST [ AMAZON ]', () => {
      it('PARSE AMAZON INVOICE [ AMAZON_1.pdf ]', done => {
        const filename = 'test/resources/AMAZON_1.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, amazon1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE AMAZON INVOICE [ AMAZON_2.pdf ]', done => {
        const filename = 'test/resources/AMAZON_2.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, amazon2);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE AMAZON INVOICE [ AMAZON_3.pdf ]', done => {
        const filename = 'test/resources/AMAZON_3.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, amazon3);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE AMAZON INVOICE [ AMAZON_4.pdf ]', done => {
        const filename = 'test/resources/AMAZON_4.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, amazon4);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE AMAZON INVOICE [ AMAZON_5.pdf ]', done => {
        const filename = 'test/resources/AMAZON_5.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, amazon5);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

      it('PARSE AMAZON INVOICE [ AMAZON_6.pdf ]', done => {
        const filename = 'test/resources/AMAZON_6.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, amazon6);
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
