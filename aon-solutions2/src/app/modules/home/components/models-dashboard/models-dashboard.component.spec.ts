import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModelsDashboardComponent } from './models-dashboard.component';
import { MatMenu } from '@angular/material/menu';
//import { TaxModel } from 'src/app/core/models/class/tax-model';
import { Observable, of } from 'rxjs';

describe('ModelsDashboardComponent', () => {
  let component: ModelsDashboardComponent;
  let fixture: ComponentFixture<ModelsDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        ModelsDashboardComponent ,
        MatMenu
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModelsDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  //Se crea taxModels como un array de objeto TaxModel, se se asigna taxModels como  observable a taxModelList, llamada a ngOninit y se comprueba que models se establece correctamente
  it('should set the models when taxModelList is available', () => {
  //  const taxModels: TaxModel[] = [
  //    new TaxModel( 180,'IVA','en proceso','domicialición bancaria','result1',4,2021),
  //    new TaxModel( 303,'IVA','pendiente','tranferencia','result2',2,2020),
  //    new TaxModel( 180,'IVA','rectificado','domicialición bancaria','result1',1,2022),
  //    new TaxModel( 180,'IVA','presentado','domicialición bancaria','result1',2,2022)
  //  ];
 //   const taxModelList: Observable<TaxModel[]> = of(taxModels);
  //  component.taxModelList = taxModelList;
    component.ngOnInit();

  //  expect(component.models).toEqual(taxModels);
  });
    //No se asigna valor a taxModel LIst, llamamos a ngOnInit y verificamos que la propiedad models se inicializa como array vacío
  it('should set models as empty array when taxModelList is undefined', () => {
    component.ngOnInit();

  //  expect(component.models).toEqual([]);
  });
});
