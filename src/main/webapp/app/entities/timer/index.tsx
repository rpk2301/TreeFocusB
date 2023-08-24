import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Timer from './timer';
import TimerDetail from './timer-detail';
import TimerUpdate from './timer-update';
import TimerDeleteDialog from './timer-delete-dialog';

const TimerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Timer />} />
    <Route path="new" element={<TimerUpdate />} />
    <Route path=":id">
      <Route index element={<TimerDetail />} />
      <Route path="edit" element={<TimerUpdate />} />
      <Route path="delete" element={<TimerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TimerRoutes;
