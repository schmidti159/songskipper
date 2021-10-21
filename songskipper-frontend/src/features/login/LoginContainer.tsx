import { Box, CircularProgress } from '@mui/material';
import { api } from '../../api/api';

interface LoginContainerProps {
  children: JSX.Element
}

export default function LoginContainer(props: LoginContainerProps) {
  const {data: loggedIn, isLoading} = api.useIsLoggedInQuery()

  if(isLoading) {
    // wait until the loginState is updated
    return (
      <Box sx={{ display: 'flex' }}>
        <CircularProgress />
      </Box>
    )
  } else if(!loggedIn) {
    // redirect to trigger the login
    window.location.href = '/oauth2/authorization/spotify';
    return null
  } else {
    // logged in -> show the real content
    return props.children
  }
}