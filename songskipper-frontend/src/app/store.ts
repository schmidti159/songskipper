import { configureStore } from '@reduxjs/toolkit'
import currentlyPlayingReducer from '../features/currentlyPlaying/currentlyPlayingSlice'
import { api } from '../api/api'
import { setupListeners } from '@reduxjs/toolkit/dist/query'

export const store = configureStore({
  reducer: {
    [api.reducerPath]: api.reducer,
    currentlyPlaying: currentlyPlayingReducer,
  },
  middleware: (getDefaultMiddleware) => 
    getDefaultMiddleware().concat(api.middleware)
})

setupListeners(store.dispatch)

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch