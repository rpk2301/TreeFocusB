import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './timer.reducer';

export const TimerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const timerEntity = useAppSelector(state => state.timer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="timerDetailsHeading">Timer</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{timerEntity.id}</dd>
          <dt>
            <span id="duration">Duration</span>
          </dt>
          <dd>{timerEntity.duration}</dd>
          <dt>
            <span id="expirationTime">Expiration Time</span>
          </dt>
          <dd>
            {timerEntity.expirationTime ? <TextFormat value={timerEntity.expirationTime} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{timerEntity.status}</dd>
          <dt>Assigned To</dt>
          <dd>{timerEntity.assignedTo ? timerEntity.assignedTo.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/timer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/timer/${timerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TimerDetail;
