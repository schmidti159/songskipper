import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import type { RootState } from '../../app/store'
import type { Track } from '../../common/types'

// Define a type for the slice state
interface CurrentlyPlayingState {
  track?: Track,
  progressMs?: number,
  isPaused: boolean
}

// Define the initial state using that type
const initialState: CurrentlyPlayingState = {
  // test data
  progressMs: 0,
  isPaused: true
}

export const currentlyPlayingSlice = createSlice({
  name: 'currentlyPlaying',
  initialState,
  reducers: {
    currentlyPlayingMessage: (state, action: PayloadAction<CurrentlyPlayingState>) => {
      console.log("reducer currentlyPlayingMessage called with "+action.payload)
      return action.payload
    }
  }
})

export const { currentlyPlayingMessage } = currentlyPlayingSlice.actions

export const selectCurrentlyPlayingTrack = (state: RootState) => state.currentlyPlaying.track
export const selectCurrentlyPlayingProgress = (state: RootState) => state.currentlyPlaying.progressMs || 0
export const selectCurrentlyPlayingIsPaused = (state: RootState) => state.currentlyPlaying.isPaused

export default currentlyPlayingSlice.reducer