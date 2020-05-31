// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  logo: '../../../assets/logo.png',
  logoCompany: '../../../assets/logo-company.png',
  logoMobile: '../../../assets/logoMobile.png',
  apiUrl: 'https://europe-west1-tedi-snapshot.cloudfunctions.net',
  firebase: {
    apiKey: 'AIzaSyAHsPikxx02P2_Ad7J_sBXLBwFu-3AFZmg',
    storageBucket: 'tedi-snapshot.appspot.com',
    projectId: 'tedi-snapshot'
  }
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
