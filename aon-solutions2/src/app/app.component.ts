import { Component } from '@angular/core';
import * as myGlobals from './app.globals';
import { TranslateService } from '@ngx-translate/core';
import { DomSanitizer } from '@angular/platform-browser';
import { MatIconRegistry } from '@angular/material/icon';
//import { SetproductIcons } from 'libraries/setproduct-icons';

@Component({
  selector    : 'app-root',
  templateUrl : './app.component.html',
  styleUrls   : ['./app.component.scss']
})
export class AppComponent {
  constructor(
    private translateService: TranslateService,
    private matIconRegistry : MatIconRegistry,
    private domSanitizer    : DomSanitizer
  ) {
    /*
      Idioma
    */
      if(localStorage.getItem('selectedLanguage') === null){
        // Lenguaje predefinido en el estorage
        localStorage.setItem('selectedLanguage', myGlobals.predefinedLanguage);
      }
      // Cargamos el almacenado en storage
      this.translateService.setDefaultLang(localStorage.getItem('selectedLanguage') as string);
      this.translateService.use(localStorage.getItem('selectedLanguage') as string);
    /*
      FIN idioma
    */
    
    /*
      Add icons
    */
      // Setproduct no va....
//      SetproductIcons.add({"aon-add-note": '<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" clip-rule="evenodd" d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm-1 9h-2v3H8v2h3v3h2v-3h3v-2h-3v-3zm-7 9h12V9h-5V4H6v16z" fill="#292A31"/></svg>'});
//      console.log( SetproductIcons.get(this.shape) );
      console.log( myGlobals );

      Object.entries(myGlobals.aon_add_icon).forEach(([key, value]) => {
        matIconRegistry.addSvgIconLiteral(
          key, domSanitizer.bypassSecurityTrustHtml( value )
        );
      });
    /*
      FIN add icons
    */
  }

  // Modificamos el lenguaje y recargamos
  selectLanguage(language: string) {
    localStorage.setItem('selectedLanguage', language);
    location.reload();
  }
}
