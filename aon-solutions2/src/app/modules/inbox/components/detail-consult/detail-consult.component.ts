import { Component, EventEmitter, Input, OnInit, Output, ChangeDetectorRef, Inject, OnDestroy } from '@angular/core';
import { Message } from 'src/app/core/models/message';
import { InboxService } from '../../services/inbox.service';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RateInboxComponent } from '../rate-inbox/rate-inbox.component';

@Component({
  selector: 'app-detail-consult',
  templateUrl: './detail-consult.component.html',
  styleUrls: ['./detail-consult.component.scss'],
})
export class DetailConsultComponent implements OnInit {
  @Input() message: Message = new Message();
  @Output() closed: any = new EventEmitter<any>();
  showForm: boolean = false;
  allMessages: Message[] = [];
  showItems: boolean[] = [true, true, true];
  private readonly unsubscribeSubject$: Subject<void> = new Subject<void>();

  constructor(inboxService: InboxService, public dialog: MatDialog, public cdr: ChangeDetectorRef) {
    this.allMessages = inboxService.getAllMessagesFromConsult();
  }

  ngOnInit(): void {}

  // Permite mostrar el formulario para crear una nueva consulta
  showFormFunction() {
    this.showForm === true ? (this.showForm = false) : (this.showForm = true);
  }

  // Cierra el componente de detalle del mensaje
  close() {
    this.closed.emit('true');
  }

  // Permite mostrar el contenido del mensaje completo
  showConsult(item: number) {
    if (this.showItems[item]) this.showItems[item] = false;
    else this.showItems[item] = true;
  }

  openDialog(item: any){
    const dialogRef = this.dialog.open(RateInboxComponent, {width: 'auto', data: {subject: item}});
    dialogRef
        .afterClosed()  //
        .pipe(takeUntil(this.unsubscribeSubject$))
        .subscribe((result) => {
          // tslint:disable-next-line: no-unsafe-callback-scope
          // this.animal = result;
          console.log(result);
          this.cdr.detectChanges();
        });
  }

  ngOnDestroy(): void {
    this.unsubscribeSubject$.next();
    this.unsubscribeSubject$.complete();
    return;
  }
}
