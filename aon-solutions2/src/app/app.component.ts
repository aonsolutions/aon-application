import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'aon-solutions2';

  constructor(private translateService: TranslateService) { }

  selectLanguage(language: string) {
    this.translateService.use(language);
  }
}
