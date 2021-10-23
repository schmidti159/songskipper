import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { Client } from '@stomp/stompjs/esm6/client'
import SockJS from 'sockjs-client'
import { CurrentlyPlayingState } from '../common/types'

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

    /* CURRENTLY PLAYING */
    updateCurrentlyPlayingState: builder.query<CurrentlyPlayingState, void>({
      query: () => 'currently-playing/v1/currently-playing-track',
      async onCacheEntryAdded(
        arg,
        { updateCachedData, cacheDataLoaded, cacheEntryRemoved }
      ) {
        // create a websocket connection when the cache subscription starts
        const stompClient = new Client({
          webSocketFactory: function () {
              return new SockJS('http://'+document.location.host+'/ws');
          }
        });
        try {
          // wait for the initial query to resolve before proceeding
          await cacheDataLoaded

          // when data is received from the socket connection to the server,
          // if it is a message and for the appropriate channel,
          // update our query result with the received message
          stompClient.onConnect = frame => {
            console.log('Connected: '+frame);
            stompClient.subscribe('/users/queue/messages', message => {
              const currentlyPlaying = JSON.parse(message.body)
              updateCachedData((draft) => {
                return currentlyPlaying
              })
            });
          };
          stompClient.onWebSocketError = error => {
            console.error('websocketError: '+error); 
            console.error(error);}
          stompClient.onStompError =  frame => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
          };
          stompClient.activate();
        } catch {
          // no-op in case `cacheEntryRemoved` resolves before `cacheDataLoaded`,
          // in which case `cacheDataLoaded` will throw
        }
        // cacheEntryRemoved will resolve when the cache subscription is no longer active
        await cacheEntryRemoved
        // perform cleanup steps once the `cacheEntryRemoved` promise resolves
        stompClient.deactivate()
      },
    })
  }),
})
