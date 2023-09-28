import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Observable } from 'rxjs';
import { FilterBuilder } from 'libraries/AonSDK/src/aon';
import { TranslateService } from '@ngx-translate/core';
import { DropdownMenuComponent } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { TaxModelService } from 'src/app/core/services/tax-model.service';

@Component({
  selector    : 'app-models-dashboard',
  templateUrl : './models-dashboard.component.html',
  styleUrls   : ['./models-dashboard.component.scss']
})
export class ModelsDashboardComponent implements OnInit {
  @ViewChild('model') dropdownMenuComponent: DropdownMenuComponent = new DropdownMenuComponent;
  models  : any;
  menuItem: MenuItem [] = []
  selected: string    = '';

  constructor(
    private translateService: TranslateService,
    private taxModelService : TaxModelService
  ) {
    this.translateService.get([
      'HOME.1_TRIMESTER', 'HOME.2_TRIMESTER', 'HOME.3_TRIMESTER', 'HOME.4_TRIMESTER'
    ]).subscribe((result) => {
        taxModelService.thisTrimester().then((trimester) => {
          // Seleccionamos el trimestre en el que estamos
          this.ModelsThisTrimester(trimester, result["HOME."+trimester+"_TRIMESTER"])
        });
      
      // Trimestre en el menu
        this.menuItem! = [
          {root: true, text: result["HOME.1_TRIMESTER"], click:() => this.ModelsThisTrimester(1, result["HOME.1_TRIMESTER"])},
          {root: true, text: result["HOME.2_TRIMESTER"], click:() => this.ModelsThisTrimester(2, result["HOME.2_TRIMESTER"])},
          {root: true, text: result["HOME.3_TRIMESTER"], click:() => this.ModelsThisTrimester(3, result["HOME.3_TRIMESTER"])},
          {root: true, text: result["HOME.4_TRIMESTER"], click:() => this.ModelsThisTrimester(4, result["HOME.4_TRIMESTER"])}
        ];
    });
  }

  ngOnInit(): void {

  }
  
  ModelsThisTrimester(trimester : number, name: string){
    // Nombre
    this.selected = name
    // modelos del trimestre escogido
    let filterBuilder = new FilterBuilder();
    filterBuilder.addField('trimester', trimester);
    this.taxModelService.getTaxModelList(filterBuilder.getFilter()).then((models) => {
      this.models = models;
    });
  }

}
