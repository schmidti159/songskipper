import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useAppSelector } from '../../app/hooks'
import { loggedInMessage } from './loginSlice';

interface LoginContainerProps {
  children: JSX.Element
}

export default function LoginContainer(props: LoginContainerProps) {
  const loginState = useAppSelector(state => state.login);
  const dispatch = useDispatch();
  useEffect(() => {
    fetch("/api/public/user/v1/id")
    .then(response => response.text())
    .then(content => {
      console.log('user-id: '+content);
      dispatch(loggedInMessage({ checkedLogin: true, loggedIn: content.length > 0} ))
    });
  }, [dispatch]);
  if(loginState.checkedLogin && !loginState.loggedIn) {
    window.location.href = '/oauth2/authorization/spotify';
    return null
  } else {
    return props.children
  }
}