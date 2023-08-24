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
import { ITimer } from 'app/shared/model/timer.model';
import { TimerStatus } from 'app/shared/model/enumerations/timer-status.model';
import { getEntity, updateEntity, createEntity, reset } from './timer.reducer';

export const TimerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const timerEntity = useAppSelector(state => state.timer.entity);
  const loading = useAppSelector(state => state.timer.loading);
  const updating = useAppSelector(state => state.timer.updating);
  const updateSuccess = useAppSelector(state => state.timer.updateSuccess);
  const timerStatusValues = Object.keys(TimerStatus);

  const handleClose = () => {
    navigate('/timer');
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
    values.expirationTime = convertDateTimeToServer(values.expirationTime);

    const entity = {
      ...timerEntity,
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
      ? {
          expirationTime: displayDefaultDateTime(),
        }
      : {
          status: 'Running',
          ...timerEntity,
          expirationTime: convertDateTimeFromServer(timerEntity.expirationTime),
          assignedTo: timerEntity?.assignedTo?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="treefocusApp.timer.home.createOrEditLabel" data-cy="TimerCreateUpdateHeading">
            Create or edit a Timer
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="timer-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Duration" id="timer-duration" name="duration" data-cy="duration" type="text" />
              <ValidatedField
                label="Expiration Time"
                id="timer-expirationTime"
                name="expirationTime"
                data-cy="expirationTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Status" id="timer-status" name="status" data-cy="status" type="select">
                {timerStatusValues.map(timerStatus => (
                  <option value={timerStatus} key={timerStatus}>
                    {timerStatus}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="timer-assignedTo" name="assignedTo" data-cy="assignedTo" label="Assigned To" type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/timer" replace color="info">
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

export default TimerUpdate;
