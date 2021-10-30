import { createEntityAdapter, createSlice } from "@reduxjs/toolkit";
import { RootState } from "../app/store";
import { Rule } from "../common/types";


const rulesAdapter = createEntityAdapter<Rule>({
  sortComparer: (a, b) => a.id.localeCompare(b.id)
})

export const rulesSlice = createSlice({
  name: 'rules',
  initialState: rulesAdapter.getInitialState(),
  reducers: {
    rulesUpdated: rulesAdapter.setMany,
    ruleUpdated: rulesAdapter.setOne,
    ruleDeleted: rulesAdapter.removeOne,
  },
})


export const { ruleUpdated, ruleDeleted, rulesUpdated } = rulesSlice.actions
const ruleSelectors = rulesAdapter.getSelectors<RootState>(
  (state) => state.rules
)
export const { selectAll: selectAllRules } = ruleSelectors