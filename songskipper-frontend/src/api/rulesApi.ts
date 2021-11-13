import { PayloadAction } from '@reduxjs/toolkit';
import { AppDispatch } from '../app/store';
import { Rule } from '../common/types';
import { ruleDeleted, rulesUpdated, ruleUpdated } from '../features/skipRules/rulesSlice';
import { api } from './api';

export const rulesApi = api.injectEndpoints({
  endpoints: (builder) => ({
    getRules: builder.query<Rule[], void>({
      query: () => "rules/v1/",
      onQueryStarted: (id, { dispatch, queryFulfilled }) =>
        handleQueryFulfilled(dispatch, queryFulfilled, (newRules: Rule[]) => rulesUpdated(newRules)),
    }),
    getRuleById: builder.query<Rule, string>({
      query: (id) => `rules/v1/${id}/`,
      onQueryStarted: (id, { dispatch, queryFulfilled }) =>
        handleQueryFulfilled(dispatch, queryFulfilled, (newRule: Rule) => ruleUpdated(newRule)),
    }),
    createRule: builder.mutation<Rule, Rule>({
      query: (rule) => ({
        url: `rules/v1/`,
        method: "POST",
        body: rule
      }),
      onQueryStarted: (id, { dispatch, queryFulfilled }) =>
        handleQueryFulfilled(dispatch, queryFulfilled, (newRule: Rule) => ruleUpdated(newRule)),
      invalidatesTags: ['PlayLog']
    }),
    updateRule: builder.mutation<Rule, Rule>({
      query: (rule) => ({
        url: `rules/v1/${rule.id}/`,
        method: "PUT",
        body: rule
      }),
      onQueryStarted: (id, { dispatch, queryFulfilled }) =>
        handleQueryFulfilled(dispatch, queryFulfilled, (newRule: Rule) => ruleUpdated(newRule)),
      invalidatesTags: ['PlayLog']
    }),
    deleteRuleById: builder.mutation<void, string>({
      query: (id) => ({
        url: `rules/v1/${id}/`,
        method: "DELETE"
      }),
      onQueryStarted: (id, { dispatch, queryFulfilled }) =>
        handleQueryFulfilled(dispatch, queryFulfilled, () => ruleDeleted(id)),
      invalidatesTags: ['PlayLog']
    }),
  }),
});

async function handleQueryFulfilled(dispatch: AppDispatch, queryFulfilled: Promise<any>,
  handleNewData: ((arg: any) => PayloadAction<any>)): Promise<void> {
  try {
    const { data } = await queryFulfilled;
    dispatch(handleNewData(data));
  } catch {
    // do not update state
  }
};

