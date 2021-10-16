import { configureStore } from '@reduxjs/toolkit'
import loginReducer from '../features/login/loginSlice'
import skipperReducer from '../features/skipper/skipperSlice'
import currentlyPlayingReducer from '../features/currentlyPlaying/currentlyPlayingSlice'

export const store = configureStore({
  reducer: {
    login: loginReducer,
    skipper: skipperReducer,
    currentlyPlaying: currentlyPlayingReducer,
  }
})

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch