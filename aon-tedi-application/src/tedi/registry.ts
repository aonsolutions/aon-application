import MariaDB from 'mariadb';
import { Registry } from '../tedi-ewok/TediEwok';

export class TediRegistry {
  public static getRegistries(documents: string[]): Promise<Registry[]> {
    // tslint:disable-next-line: no-console
    // console.log(JSON.stringify(documents));
    return TediRegistry.__getConnection().then(connection => TediRegistry.__getRegistries(connection, documents).finally(() => connection.end()));
  }

  private static __getConnection(): Promise<MariaDB.Connection> {
    const connectionConfig: MariaDB.ConnectionConfig = {
      host: process.env.DB_HOST,
      user: process.env.DB_USER,
      password: process.env.DB_PASSWD,
      database: process.env.DB_NAME,
    };
    return MariaDB.createConnection(connectionConfig);
  }

  private static __getRegistries(connection: MariaDB.Connection, documents: string[]): Promise<Registry[]> {
    return connection
      .query(
        {
          sql: `
        SELECT * 
        FROM registry
        LEFT JOIN raddress ON ( registry.id = raddress.registry )
        LEFT JOIN geozone ON ( raddress.geozone = geozone.id )
        WHERE registry.domain IN (0)
        AND document IN (${documents.map(d => '?').join(',')})
        `,
          nestTables: true,
        },
        documents,
      )
      .then((resultSet: any[]) =>
        resultSet.map(row => {
          const registry: Registry = {
            name: row.registry.name,
            document: row.registry.document,
            document_country: row.registry.document_country,
            address: {
              city: row.raddress.city,
              province: row.geozone.name || TediRegistry.getProvince(row.raddress.zip),
              postal_code: row.raddress.zip,
              address: [row.raddress.street_type, row.raddress.address, row.raddress.number, row.raddress.address1, row.raddress.address2]
                .filter(s => s)
                .join(' '),
              country: row.registry.document_country,
            },
          };
          return registry;
        }),
      );
  }

  private static getProvince(zip: string | null | undefined): string | undefined {
    if (!zip) {
      return undefined;
    }
    return {
      '02': 'ALBACETE',
      '03': 'ALICANTE/ALACANT',
      '04': 'ALMERÍA',
      '33': 'ASTURIAS',
      '05': 'ÁVILA',
      '01': 'ARABA/ÁLAVA',
      '06': 'BADAJOZ',
      '07': 'BALEARS, ILLES',
      '08': 'BARCELONA',
      '48': 'BIZKAIA',
      '09': 'BURGOS',
      '10': 'CÁCERES',
      '11': 'CÁDIZ',
      '39': 'CANTABRIA',
      '12': 'CASTELLÓN/CASTELLÓ',
      '13': 'CIUDAD REAL',
      '14': 'CÓRDOBA',
      '15': 'CORUÑA, A',
      '16': 'CUENCA',
      '20': 'GIPUZKOA',
      '17': 'GIRONA',
      '18': 'GRANADA',
      '19': 'GUADALAJARA',
      '21': 'HUELVA',
      '22': 'HUESCA',
      '23': 'JAÉN',
      '24': 'LEÓN',
      '25': 'LLEIDA',
      '27': 'LUGO',
      '28': 'MADRID',
      '29': 'MÁLAGA',
      '30': 'MURCIA',
      '31': 'NAVARRA',
      '32': 'OURENSE',
      '34': 'PALENCIA',
      '35': 'PALMAS, LAS',
      '36': 'PONTEVEDRA',
      '26': 'RIOJA, LA',
      '37': 'SALAMANCA',
      '38': 'SANTA CRUZ DE TENERIFE',
      '40': 'SEGOVIA',
      '41': 'SEVILLA',
      '42': 'SORIA',
      '43': 'TARRAGONA',
      '44': 'TERUEL',
      '45': 'TOLEDO',
      '46': 'VALENCIA/VALÈNCIA',
      '47': 'VALLADOLID',
      '49': 'ZAMORA',
      '50': 'ZARAGOZA',
      '51': 'CEUTA',
      '52': 'MELILLA',
    }[zip.substr(0, 2)];
  }
}
