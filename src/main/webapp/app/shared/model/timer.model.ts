import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { TimerStatus } from 'app/shared/model/enumerations/timer-status.model';

export interface ITimer {
  id?: number;
  duration?: number | null;
  expirationTime?: string | null;
  status?: TimerStatus | null;
  assignedTo?: IUser | null;
}

export const defaultValue: Readonly<ITimer> = {};
