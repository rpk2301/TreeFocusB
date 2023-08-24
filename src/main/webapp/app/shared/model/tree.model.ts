import { IUser } from 'app/shared/model/user.model';
import { TreeType } from 'app/shared/model/enumerations/tree-type.model';

export interface ITree {
  id?: number;
  trees?: TreeType | null;
  assignedTo?: IUser | null;
}

export const defaultValue: Readonly<ITree> = {};
