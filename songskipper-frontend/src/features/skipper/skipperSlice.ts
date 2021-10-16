import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../app/store'

// Define a type for the slice state
interface SkipperState {
  active: boolean
}

// Define the initial state using that type
const initialState: SkipperState = {
  active: false
}

export const skipperSlice = createSlice({
  name: 'skipper',
  initialState,
  reducers: {
    skipperActive: (state, action: PayloadAction<boolean>) => {
      return {
        ...state, 
        active: action.payload
      }
    }
  }
})

export const { skipperActive } = skipperSlice.actions

export const selectSkipperActive = (state: RootState) => state.skipper.active

export default skipperSlice.reducer