
import { ComponentFixture, TestBed } from '@angular/core/testing';
//import { ChartDashboardComponent, ChartItem } from './chart-dashboard.component';
import { ReportingService } from '../../../../core/services/reporting.service';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { MatMenu } from '@angular/material/menu';
import { Observable, of } from 'rxjs';

/*
describe('ChartDashboardComponent', () => {
  let component: ChartDashboardComponent;
  let fixture: ComponentFixture<ChartDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
         ChartDashboardComponent,
         MatMenu
        ],
        schemas :[CUSTOM_ELEMENTS_SCHEMA,NO_ERRORS_SCHEMA],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChartDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('toggleChartType() should check that chartType has been changed to bar', () => {
    component.chartType = 'line';
    component.toggleChartType();

    expect(component.chartType).toEqual('bar');
  });

  it('toggleChartType() should check that chartType has been changed to line', () => {
    component.chartType = 'bar';
    component.toggleChartType();

    expect(component.chartType).toEqual('line');
  });

  it('getToggleIcon() should return bar_chart when chartType is line', () => {
    component.chartType = 'line';
    component.getToggleIcon();
    const spyGetToggleIcon = spyOn(component,'getToggleIcon').and.returnValue('bar_chart');
    
    expect(spyGetToggleIcon).toBeTruthy();
  });
  //comprueba que la prpiedad chartItems del component se inicialice correctamente con los datos de chartItemList
  it('should initialize chartItems on ngOnInit', () => {
    const chartItems: ChartItem[] = [
      {
        shape: 'Shape 1',
        name: 'Name 1',
        chartLabels: ['Label 1', 'Label 2'],
        chartData: [[10, 20]],
        chartType: 'bar',
        colors: ['exampleColor', 'exampleColor2'],
      },
    ];
    const chartItemList: Observable<ChartItem[]> = of(chartItems);

    component.chartItemList = chartItemList;
    component.ngOnInit();

    expect(component.chartItemList).toEqual(chartItemList);
  });

  it('should set models as empty array when chartItems is undefined', () => {
    component.ngOnInit();

    expect(component.chartItems).toEqual([]);
});

});
*/