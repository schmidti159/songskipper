import { Client } from '@stomp/stompjs/esm6/client';
import SockJS from 'sockjs-client';
import { AppDispatch } from '../app/store';
import { CurrentlyPlayingState } from '../common/types';
import { api } from './api';

export const playerApi = api.injectEndpoints({
  endpoints: (builder) => ({
    updateCurrentlyPlayingState: builder.query<CurrentlyPlayingState, void>({
      query: () => 'player/v1/currently-playing-track',
      async onCacheEntryAdded(
        arg,
        { updateCachedData, cacheDataLoaded, cacheEntryRemoved }
      ) {
        // create a websocket connection when the cache subscription starts
        const stompClient = new Client({
          webSocketFactory: function () {
            return new SockJS('http://' + document.location.host + '/ws');
          }
        });
        try {
          // wait for the initial query to resolve before proceeding
          await cacheDataLoaded;

          // when data is received from the socket connection to the server,
          // if it is a message and for the appropriate channel,
          // update our query result with the received message
          stompClient.onConnect = frame => {
            stompClient.subscribe('/users/queue/messages', message => {
              const currentlyPlaying = JSON.parse(message.body);
              updateCachedData((draft) => {
                return currentlyPlaying;
              });
            });
          };
          stompClient.onWebSocketError = error => {
            console.error('websocketError: ' + error);
            console.error(error);
          };
          stompClient.onStompError = frame => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
          };
          stompClient.activate();
        } catch {
          // no-op in case `cacheEntryRemoved` resolves before `cacheDataLoaded`,
          // in which case `cacheDataLoaded` will throw
        }
        // cacheEntryRemoved will resolve when the cache subscription is no longer active
        await cacheEntryRemoved;
        // perform cleanup steps once the `cacheEntryRemoved` promise resolves
        stompClient.deactivate();
      },
    }),
    nextTrack: builder.mutation<CurrentlyPlayingState, void>({
      query: () => ({
        url: 'player/v1/next',
        method: "POST"
      }),
      onQueryStarted: (id, { dispatch, queryFulfilled }) =>
        updatePlayingStateOnQueryFulfilled(dispatch, queryFulfilled)
    }),
    previousTrack: builder.mutation<void, void>({
      query: () => ({
        url: 'player/v1/previous',
        method: "POST"
      }),
      onQueryStarted: (id, { dispatch, queryFulfilled }) =>
        updatePlayingStateOnQueryFulfilled(dispatch, queryFulfilled)
    }),
    play: builder.mutation<void, void>({
      query: () => ({
        url: 'player/v1/play',
        method: "POST"
      }),
      onQueryStarted: (id, { dispatch, queryFulfilled }) =>
        updatePlayingStateOnQueryFulfilled(dispatch, queryFulfilled)
    }),
    pause: builder.mutation<void, void>({
      query: () => ({
        url: 'player/v1/pause',
        method: "POST"
      }),
      onQueryStarted: (id, { dispatch, queryFulfilled }) =>
        updatePlayingStateOnQueryFulfilled(dispatch, queryFulfilled)
    }),
  }),
});

async function updatePlayingStateOnQueryFulfilled(dispatch: AppDispatch, queryFulfilled: Promise<any>): Promise<void> {
  try {
    const { data: state } = await queryFulfilled;
    dispatch(playerApi.util.updateQueryData('updateCurrentlyPlayingState', undefined, (draft) => {
      Object.assign(draft, state);
    }));
  } catch {
    // do not update state
  }
};

