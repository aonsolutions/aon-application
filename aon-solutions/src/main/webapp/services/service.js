import {clearCompanyService} from './companyService.js';
import { mobileAction, MOBILE_ACTION } from './mobileService.js';
import { clearAuth } from './authService.js';


import './AonStringUtils.js';

import './AonDateUtils.js';

export * from './authService.js';

export * from './invoiceService.js';

export * from './documentalService.js';

export * from './companyService.js';

export * from './userService.js';

export * from './fileService.js';

export * from './comunicaService.js';

export * from './registryService.js';

export * from './contractService.js';

export * from './mobileService.js';

export * from './timeControlService.js';

export * from './taskHolderService.js';

export * from './locationService.js';

export * from './authDeviceService.js';

export * from './request.js';

export * from './laboralService.js';

export * from './notificationService.js';

export * from './taskService.js';

export * from './fiscalService.js';

export * from './accountingService.js';

export * from './productService.js';

export * from './applicationParameterService.js';

export * from './workplaceService.js';

export const clear = () => {
  clearCompanyService();
  clearAuth();
  mobileAction({ action: MOBILE_ACTION.REMOVE_SESSION_BIDOQ });
}
