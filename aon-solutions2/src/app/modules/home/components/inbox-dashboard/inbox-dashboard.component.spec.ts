import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboxDashboardComponent } from './inbox-dashboard.component';
//import { Message } from 'src/app/core/models/class/message';
import { Observable, of } from 'rxjs';

describe('InboxDashboardComponent', () => {
  let component: InboxDashboardComponent;
  let fixture: ComponentFixture<InboxDashboardComponent>;
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboxDashboardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboxDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

 // it('should create', () => {
 //   expect(component).toBeTruthy();
 // });

  //it('should set the models when messageList is available', () => {
    //const messageList: Message[] = [
     // new Message('Maria Rico Gómez', 'Asunto 1', 'sunt in culpa qui officia deserunt', new Date("2021-01-16"), 'consulta', 'pendiente', new Date("2021-05-16")),
      //new Message('Jesús Pérez Álvarez', 'Asunto 2', 'sunt in culpa qui officia deserunt', new Date("2022-01-16"), 'tarea', 'nueva', new Date("2022-05-16")),
      //new Message('Juan Carlos Aragón Pérez', 'Asunto 3', 'sunt in culpa qui officia deserunt', new Date("2023-01-16"), 'notificación', 'abierta', new Date("2023-05-16"))
   // ];
    //const messages: Observable<Message[]> = of(messageList);

    //component.messageList = messages;
    //component.ngOnInit();

    //expect(component.messageList).toEqual(messages); 
  //});

  //it('should set models as empty array when messages is undefined', () => {
    //component.ngOnInit();

  //  expect(component.messages).toEqual([]);
//});
});
