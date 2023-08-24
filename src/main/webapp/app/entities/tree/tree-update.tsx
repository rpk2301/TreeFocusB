import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { ITree } from 'app/shared/model/tree.model';
import { TreeType } from 'app/shared/model/enumerations/tree-type.model';
import { getEntity, updateEntity, createEntity, reset } from './tree.reducer';

export const TreeUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const treeEntity = useAppSelector(state => state.tree.entity);
  const loading = useAppSelector(state => state.tree.loading);
  const updating = useAppSelector(state => state.tree.updating);
  const updateSuccess = useAppSelector(state => state.tree.updateSuccess);
  const treeTypeValues = Object.keys(TreeType);

  const handleClose = () => {
    navigate('/tree');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...treeEntity,
      ...values,
      assignedTo: users.find(it => it.id.toString() === values.assignedTo.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          trees: 'Dogwood',
          ...treeEntity,
          assignedTo: treeEntity?.assignedTo?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="treefocusApp.tree.home.createOrEditLabel" data-cy="TreeCreateUpdateHeading">
            Create or edit a Tree
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="tree-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Trees" id="tree-trees" name="trees" data-cy="trees" type="select">
                {treeTypeValues.map(treeType => (
                  <option value={treeType} key={treeType}>
                    {treeType}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="tree-assignedTo" name="assignedTo" data-cy="assignedTo" label="Assigned To" type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/tree" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TreeUpdate;
