import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './tree.reducer';

export const TreeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const treeEntity = useAppSelector(state => state.tree.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="treeDetailsHeading">Tree</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{treeEntity.id}</dd>
          <dt>
            <span id="trees">Trees</span>
          </dt>
          <dd>{treeEntity.trees}</dd>
          <dt>Assigned To</dt>
          <dd>{treeEntity.assignedTo ? treeEntity.assignedTo.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/tree" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/tree/${treeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TreeDetail;
