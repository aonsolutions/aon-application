import { Component, OnInit, ViewChild } from '@angular/core';
import { Enterprise } from 'src/app/core/models/class/enterprise';
import { AuthService } from 'src/app/core/services/auth.service';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';
import { ExampleServiceService } from 'src/app/core/services/example-service.service';
import { ContainerService } from 'src/app/shared/services/container.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor() {
  }

  ngOnInit(): void {
    
  }
  
}
