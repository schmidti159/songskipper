import { api } from './api';

export const loginApi = api.injectEndpoints({
  endpoints: (builder) => ({
    isLoggedIn: builder.query<boolean, void>({
      query: () => ({
        url: 'public/user/v1/id',
        responseHandler: 'text'
      }),
      transformResponse: (response: string) => response.length > 0
    }),
  })
});


