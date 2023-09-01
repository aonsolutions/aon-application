// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  urlRestCountries: "https://restcountries.com/v3.1",
  urlGobApi: "https://datos.gob.es/apidata/nti/territory",
  urlApiAon: "https://aonsolutions.org/ms/api/",
  headerApi: {
    domainName: "a54212356-cau.aonsolutions.org",
    domainId: 545,
    domainLogin: 87811999
  },
  localStorageJwt: {
    accessToken: "session_id",
    selectedEnterprise: "selectedEnterprise"
  }
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
