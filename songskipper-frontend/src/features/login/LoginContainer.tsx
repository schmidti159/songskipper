import { Box, CircularProgress } from '@mui/material';
import { Fragment } from 'react';
import { loginApi } from '../../api/loginApi';

interface LoginContainerProps {
  children: React.ReactNode;
}

export default function LoginContainer(props: LoginContainerProps) {
  const { data: loggedIn, isLoading } = loginApi.useIsLoggedInQuery();
  if (isLoading) {
    // wait until the loginState is updated
    return (
      <Box sx={{ display: 'flex' }}>
        <CircularProgress />
      </Box>
    );
  } else if (!loggedIn) {
    // redirect to trigger the login
    window.location.assign('oauth2/authorization/spotify');
    return <a href="/oauth2/authorization/spotify">click to authenticate</a>;
  } else {
    // logged in -> show the real content
    return <Fragment>{props.children}</Fragment>;

  }
}