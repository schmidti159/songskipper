import { Box, CircularProgress } from '@mui/material';
import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useAppSelector } from '../../app/hooks'
import { AppDispatch } from '../../app/store';
import { loggedInMessage } from './loginSlice';

interface LoginContainerProps {
  children: JSX.Element
}

function checkUserId(dispatch : AppDispatch) {
  fetch("/api/public/user/v1/id")
    .then(response => {
      if(!response.ok) {
        throw Error("Could not fetch from /api/public/user/v1/id: "+response.status+" "+response.statusText)
      }
      return response.text()
    }).then(content => {
      console.log('user-id: '+content);
      dispatch(loggedInMessage({ checkedLogin: true, loggedIn: content.length > 0} ))
    }).catch(error => {
      console.log(error)
    })
}

export default function LoginContainer(props: LoginContainerProps) {
  const loginState = useAppSelector(state => state.login);
  const dispatch = useDispatch();
  useEffect(() => checkUserId(dispatch), [dispatch]);
  if(!loginState.checkedLogin) {
    // wait until the loginState is updated
    return (
      <Box sx={{ display: 'flex' }}>
        <CircularProgress />
      </Box>
    )
  } else if(!loginState.loggedIn) {
    // redirect to trigger the login
    window.location.href = '/oauth2/authorization/spotify';
    return null
  } else {
    // logged in -> show the real content
    return props.children
  }
}