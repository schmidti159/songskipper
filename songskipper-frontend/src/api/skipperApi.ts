import { api } from './api';

export const skipperApi = api.injectEndpoints({
  endpoints: (builder) => ({
    isSkipperActive: builder.query<boolean, void>({
      query: () => "skipper/v1/active",
    }),
    setSkipperState: builder.mutation<string, boolean>({
      query: (activate) => "skipper/v1/" + (activate ? 'start' : 'stop'),
      async onQueryStarted(activate, { dispatch, queryFulfilled }) {
        try {
          await queryFulfilled;
          dispatch(
            skipperApi.util.updateQueryData('isSkipperActive', undefined, () => activate)
          );
        } catch {
          // do not update state
        }
      },
    }),
  }),
});


