import { rest } from 'msw';
import { setupServer } from 'msw/node';
import React from 'react';
import { api } from '../../api/api';
import { store } from '../../app/store';
import { render, screen, waitFor } from '../../test-utils';
import LoginContainer from './LoginContainer';

describe('Login Container checks login and redirects if necessary', () => {
  const server = setupServer(
    rest.get('/api/public/user/v1/id', (req, res, ctx) => {
      return res(ctx.body('user-id'));
    })
  );

  beforeAll(() => {
    server.listen();
  });
  afterEach(() => {
    server.resetHandlers();
    store.dispatch(api.util.resetApiState());
  });
  afterAll(() => {
    server.close();
  });

  test('shows progress until login state is determined', () => {
    render(<LoginContainer >Logged In</LoginContainer>);

    expect(screen.queryByRole('progressbar')).toBeInTheDocument();
    expect(screen.queryByText(/Logged In/)).toBeNull();
    expect(screen.queryByRole('link')).toBeNull();
  });

  test('shows children when logged in', async () => {
    render(<LoginContainer >Logged In</LoginContainer>);

    await waitFor(() => screen.getByText(/Logged In/));

    expect(screen.queryByRole('progressbar')).toBeNull();
    expect(screen.queryByText(/Logged In/)).toBeInTheDocument();
    expect(screen.queryByRole('link')).toBeNull();
  });

  test('redirects to login when not logged in', async () => {
    server.use(
      rest.get('/api/public/user/v1/id', (req, res, ctx) => {
        return res(ctx.body(''));
      }),
    );

    delete global.location;
    global.location = { assign: jest.fn() };

    render(<LoginContainer >Logged In</LoginContainer>);

    await waitFor(() => screen.getByRole('link'));


    expect(screen.queryByRole('progressbar')).toBeNull();
    expect(screen.queryByText(/Logged In/)).toBeNull();
    expect(screen.queryByRole('link'))
      .toContainHTML('<a href="/oauth2/authorization/spotify">click to authenticate</a>');
    expect(global.location.assign).toHaveBeenCalledTimes(1);
    expect(global.location.assign).toHaveBeenCalledWith('oauth2/authorization/spotify');
  });
});