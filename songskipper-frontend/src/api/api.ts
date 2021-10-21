import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const api = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({ baseUrl: '/api/' }),
  endpoints: (builder) => ({
    /* LOGIN */
    isLoggedIn: builder.query<boolean, void>({
      query: () => ({
        url: 'public/user/v1/id',
        responseHandler: 'text'
      }),
      transformResponse: (response: string) => response.length > 0
    }),
    
    /* SKIPPER */
    isSkipperActive: builder.query<boolean, void>({
      query: () => "skipper/v1/active",
    }),
    setSkipperState: builder.mutation<string, boolean>({
      query: (activate) => "skipper/v1/"+(activate ? 'start' : 'stop'),
      async onQueryStarted(activate, { dispatch, queryFulfilled }) {
        try {
          await queryFulfilled
          dispatch(
            api.util.updateQueryData('isSkipperActive', undefined, () => activate)
          )
        } catch {}
      },
    }),
  }),
})
