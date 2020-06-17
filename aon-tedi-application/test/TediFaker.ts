import * as faker from 'faker';
import RandExp from 'randexp';
import {
  Address,
  Company,
  Invoice,
  InvoiceDetail,
  InvoiceStatus,
  InvoiceTransaction,
  InvoiceType,
  PayMethod,
  Registry,
  TediCalculator,
  TediMath,
  User,
  InvoiceCategory,
} from '../src/tedi-ewok/TediEwok';

export class TediFaker {
  public static randomDocument(): string {
    const randexp: any = new RandExp(/^(\d|[XYZ])\d{7}[A-Z]$/);
    return randexp.gen();
  }

  public static randomUser(): User {
    return {
      email: faker.internet.email(),
      name: faker.name.firstName(),
      surname: faker.name.lastName(),
      document: TediFaker.randomDocument(),
      password: faker.internet.password(),
      actual_company: TediFaker.randomDocument(),
      users: [], /// ???
      permissions: [], /// ???
    };
  }

  public static randomCompany(): Company {
    return {
      document: new RandExp(/^(\d|[XYZ])\d{7}[A-Z]$/).gen(),
      company: new RandExp(/^(\d|[XYZ])\d{7}[A-Z]$/).gen(),
      name: faker.name.findName(),
      active: faker.random.boolean(),
      address: {
        address: faker.address.streetName(),
        city: faker.address.city(),
        postal_code: faker.address.zipCode(),
        province: faker.address.state(),
        country: faker.address.countryCode(),
      },
      printer_configuration: {
        header: faker.random.number({ min: 0, max: 100 }),
        adjustment: faker.random.boolean(),
        detailed: faker.random.boolean(),
        footer: faker.random.number({ min: 0, max: 100 }),
      },
    };
  }

  public static randomRegistry(): Registry {
    return {
      document: new RandExp(/^(\d|[XYZ])\d{7}[A-Z]$/).gen(),
      document_country: faker.address.countryCode(),
      name: faker.name.findName(),
      address: {
        address: faker.address.streetName(),
        city: faker.address.city(),
        postal_code: faker.address.zipCode(),
        province: faker.address.state(),
        country: faker.address.countryCode(),
      },
    };
  }

  public static getRandomElementOfEnum<E>(e: any): E {
    const keys = Object.keys(e);
    const index = Math.floor(Math.random() * keys.length);
    const k = keys[index];
    return e[k] as any;
  }
  public static randomAddress(): Address {
    return {
      address: faker.address.streetName(),
      city: faker.address.city(),
      postal_code: faker.address.zipCode(),
      province: faker.address.state(),
      country: faker.address.countryCode(),
    };
  }

  public static randomInvoice(withUuid?: boolean): Invoice {
    const invoiceType: InvoiceType = TediFaker.getRandomElementOfEnum<InvoiceType>(InvoiceType);

    const detailArray: InvoiceDetail[] = [];
    const times = faker.random.number({ max: 10 });
    for (let i = 0; i < times; i++) {
      const detail: InvoiceDetail = {
        description: faker.commerce.productName(),
        quantity: faker.random.number({ max: 10 }),
        price: TediMath.round(faker.random.number({ min: 0, max: 9999.99, precision: 0.02 })),
      };
      const hasDiscount: boolean = faker.random.number({ max: 100 }) < 10;
      if (hasDiscount) {
        detail.discount = TediMath.round(faker.random.number({ min: 0, max: 100, precision: 0.01 }));
      }
      const hasVat: boolean = faker.random.number({ max: 100 }) > 5;
      if (hasVat) {
        const index = faker.random.number({ max: 100 });
        if (index < 5) {
          detail.vat = TediFaker.VATS[0];
        } else if (index < 15) {
          detail.vat = TediFaker.VATS[1];
        } else if (index < 30) {
          detail.vat = TediFaker.VATS[2];
        } else {
          detail.vat = TediFaker.VATS[3];
        }
        const hasSurcharge: boolean = faker.random.number({ max: 100 }) < 10;
        if (hasSurcharge) {
          detail.surcharge = TediFaker.SURCHARGES[index];
        }
      }

      detailArray.push(detail);
    }
    const registry: Registry = {
      document: TediFaker.randomDocument(),
      document_country: faker.address.countryCode(),
      name: faker.name.findName(),
      address: TediFaker.randomAddress(),
    };
    let invoice: Invoice = {
      uuid: withUuid ? faker.random.uuid() : undefined,
      company: TediFaker.randomDocument(),
      series: this.isEmitida(invoiceType) ? '2019' : undefined,
      number: this.isEmitida(invoiceType) ? faker.random.number({max:999}) : undefined,
      reference: faker.random.alphaNumeric(10),
      type: invoiceType,
      date: faker.date.recent(),
      transaction: TediFaker.getRandomElementOfEnum<InvoiceTransaction>(InvoiceTransaction),
      category: TediFaker.getRandomElementOfEnum<InvoiceCategory>(InvoiceCategory),
      total: 223.2,
      sender: invoiceType !== InvoiceType.EMITIDA ? registry : undefined,
      receiver: invoiceType === InvoiceType.EMITIDA ? registry : undefined,
      details: detailArray,
      file: {
        url: faker.internet.url(),
        thumb_url: faker.internet.url(),
        content_type: faker.system.mimeType(),
      },
      status: TediFaker.getRandomElementOfEnum<InvoiceStatus>(InvoiceStatus),
    };
    invoice = TediCalculator.calculate(invoice);
    invoice.finances = [
      {
        due_date: invoice.date,
        amount: invoice.total,
        pay_method: TediFaker.getRandomElementOfEnum<PayMethod>(PayMethod),
        iban: faker.finance.iban(),
        pending: faker.random.boolean(),
      },
    ];
    return invoice;
  }

  private static VATS: number[] = [0, 4, 10, 21];
  private static SURCHARGES: number[] = [0, 0.5, 1.4, 5.2];

  private static isEmitida(invoiceType:InvoiceType) : boolean {
    return InvoiceType.EMITIDA === invoiceType;
  }
}
