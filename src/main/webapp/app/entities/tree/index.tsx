import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Tree from './tree';
import TreeDetail from './tree-detail';
import TreeUpdate from './tree-update';
import TreeDeleteDialog from './tree-delete-dialog';

const TreeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Tree />} />
    <Route path="new" element={<TreeUpdate />} />
    <Route path=":id">
      <Route index element={<TreeDetail />} />
      <Route path="edit" element={<TreeUpdate />} />
      <Route path="delete" element={<TreeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TreeRoutes;
