import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector    : 'app-root',
  templateUrl : './app.component.html',
  styleUrls   : ['./app.component.scss']
})
export class AppComponent {
  predefinedLanguage  = 'es';

  constructor(private translateService: TranslateService) {
    if(localStorage.getItem('selectedLanguage') === null){
      // Lenguaje predefinido en el estorage
      localStorage.setItem('selectedLanguage', this.predefinedLanguage);
    }
    // Cargamos el almacenado en storage
    this.translateService.setDefaultLang(localStorage.getItem('selectedLanguage') as string);
    this.translateService.use(localStorage.getItem('selectedLanguage') as string);
  }

  selectLanguage(language: string) {
    // Modificamos el lenguaje y recargamos
    localStorage.setItem('selectedLanguage', language);
    location.reload();
  }
}
