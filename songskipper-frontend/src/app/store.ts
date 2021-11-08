import { configureStore } from '@reduxjs/toolkit'
import { api } from '../api/api'
import { setupListeners } from '@reduxjs/toolkit/dist/query'
import { rulesSlice } from '../features/skipRules/rulesSlice'

export const store = configureStore({
  reducer: {
    [api.reducerPath]: api.reducer,
    rules: rulesSlice.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(api.middleware)
})

setupListeners(store.dispatch)

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch