import { IUser } from 'app/shared/model/user.model';

export interface IBank {
  id?: number;
  treesowned?: number | null;
  assignedTo?: IUser | null;
}

export const defaultValue: Readonly<IBank> = {};
