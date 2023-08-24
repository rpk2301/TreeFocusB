import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Bank from './bank';
import BankDetail from './bank-detail';
import BankUpdate from './bank-update';
import BankDeleteDialog from './bank-delete-dialog';

const BankRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Bank />} />
    <Route path="new" element={<BankUpdate />} />
    <Route path=":id">
      <Route index element={<BankDetail />} />
      <Route path="edit" element={<BankUpdate />} />
      <Route path="delete" element={<BankDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BankRoutes;
