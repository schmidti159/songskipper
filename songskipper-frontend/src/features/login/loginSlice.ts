import { createSlice, PayloadAction } from '@reduxjs/toolkit'

// Define a type for the slice state
interface LoginState {
  checkedLogin: boolean,
  loggedIn: boolean
}

// Define the initial state using that type
const initialState: LoginState = {
  checkedLogin: false,
  loggedIn: false
}

export const loginSlice = createSlice({
  name: 'login',
  initialState,
  reducers: {
    loggedInMessage: (state, action: PayloadAction<LoginState>) => {
      return action.payload
    }
  }
})

export const { loggedInMessage } = loginSlice.actions

export default loginSlice.reducer