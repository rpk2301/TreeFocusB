import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITimer } from 'app/shared/model/timer.model';
import { getEntities } from './timer.reducer';

export const Timer = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const timerList = useAppSelector(state => state.timer.entities);
  const loading = useAppSelector(state => state.timer.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="timer-heading" data-cy="TimerHeading">
        Timers
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/timer/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Timer
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {timerList && timerList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Duration</th>
                <th>Expiration Time</th>
                <th>Status</th>
                <th>Assigned To</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {timerList.map((timer, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/timer/${timer.id}`} color="link" size="sm">
                      {timer.id}
                    </Button>
                  </td>
                  <td>{timer.duration}</td>
                  <td>{timer.expirationTime ? <TextFormat type="date" value={timer.expirationTime} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{timer.status}</td>
                  <td>{timer.assignedTo ? timer.assignedTo.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/timer/${timer.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/timer/${timer.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button tag={Link} to={`/timer/${timer.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Timers found</div>
        )}
      </div>
    </div>
  );
};

export default Timer;
