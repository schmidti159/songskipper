import { PlayLogTrack } from '../common/types';
import { api } from './api';

export const playlogApi = api.injectEndpoints({
  endpoints: (builder) => ({
    getPlayLog: builder.query<PlayLogTrack[], void>({
      query: () => "playlog/v1/recent-tracks",
      providesTags: ['PlayLog']
    }),
  }),
});


