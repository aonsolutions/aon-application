import { Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { FormControl, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { SharedService, AonService} from '../../services/services';
import { MatDialog } from '@angular/material';
import { Company } from '../../models/AonModel';
import { TediUtils } from '../../utils/tedi-utils';
import { RootLoader } from '../../utils/loader';

@Component({
  selector: 'aon-login',
  templateUrl: './aon-login.component.html',
  styleUrls: ['./aon-login.component.css']
})
export class AonLoginComponent implements OnInit {
  hide = true;
  logo = '../../../assets/logo.png';
  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  matcher = new MyErrorStateMatcher();
  isError = false;
  errorMsg = ' ';
  buildDate: string;

  @ViewChild('email', {static: false}) public email: any;
  @ViewChild('password', {static: false}) public password: any;
  @HostListener('document:keydown.enter')
    onKeydownHandler() {
      this.login(this.email.nativeElement.value, this.password.nativeElement.value);
    }

  constructor(public service: SharedService, public aonService : AonService,
      public dialog: MatDialog) {}

  ngOnInit() {
    this.aonService.getManifest().subscribe(r => {
      this.buildDate = r.build_date;
    });
  }

  public login(email: string, password: string): void {
    this.service.loading = true;
    this.errorMsg = ' ';
    this.aonService.login(email, password).subscribe(
      result => {
        this.service.loading = false;
        if (result.errorType && result.errorMessage) {
          this.isError = true;
          this.errorMsg =  result.errorMessage;
        } else {
          this.service.isUserLoggedIn = true;
          this.service.setToken(result.session_id);
          this.service.setUserEmail(email);
          this.aonService.getCompanies().subscribe( (companies: Company[]) => {
            this.service.isUserLoggedIn = true;
            localStorage.setItem('aon_domain_id', `${companies[0].id}`);
            localStorage.setItem('aon_domain_name', companies[0].domain);
            if(companies.length === 1){
              RootLoader.rootPanel('<aon-desktop></aon-desktop>');
            } else {
              RootLoader.rootPanel('<aon-company-list></aon-company-list>');
            }
          }, (error: any) => {
            TediUtils.showError(this.dialog, error.error);
            localStorage.clear();
          });

        }
      }, error => {
        this.service.loading = false;
        this.isError = true;
        this.errorMsg = error.response.data.error ? error.response.data.error : 'El usuario no existe.';
      }
    );
  }

  public forgotPassword(): void {

  }

  isMobile(): boolean {
    return this.service.isMobile;
  }
}

/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
