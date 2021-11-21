
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import React from 'react';
import { fireEvent, render, screen, waitFor, within } from '../../test-utils';
import PageFrame from './PageFrame';

describe('The page Frame shows a navigation and the current content', () => {
  const server = setupServer(
    rest.get('/api/public/user/v1/id', (req, res, ctx) => {
      return res(ctx.status(200));
    }),
  );
  beforeAll(() => {
    server.listen();
  });
  afterEach(() => {
    server.resetHandlers();
  });
  afterAll(() => {
    server.close();
  });
  test('Shows navigation and content', () => {
    render(<PageFrame links={[{
      path: "/a",
      title: "Title A",
      icon: <>iconA</>
    }, {
      path: "/b",
      title: "Title B",
      icon: <>iconB</>
    },
    ]} >
      MyContent on the page
    </PageFrame>);

    // validate navigation
    const links = screen.getAllByRole('link');
    expect(links).toHaveLength(3); // two links + copyright
    expect(links[0].getAttribute('href')).toEqual("/a");
    expect(within(links[0]).getByText(/iconA/)).toBeInTheDocument();
    expect(within(links[0]).getByText(/Title A/)).toBeInTheDocument();
    expect(links[1].getAttribute('href')).toEqual("/b");
    expect(within(links[1]).getByText(/iconB/)).toBeInTheDocument();
    expect(within(links[1]).getByText(/Title B/)).toBeInTheDocument();

    // validate content
    expect(screen.getByText(/Songskipper for Spotify/)).toBeInTheDocument();
    expect(screen.queryByRole('progressbar')).toBeInTheDocument(); // login container is loading
    expect(screen.getByText(/Copyright/)).toBeInTheDocument();
  });

  test('Navigation can be opened and closed', async () => {
    render(<PageFrame links={[]} >Content</PageFrame>);


    expect(screen.getByLabelText(/open drawer/)?.getAttribute('aria-hidden')).toEqual('true');
    expect(screen.getByLabelText(/close drawer/)?.getAttribute('aria-hidden')).toEqual('false');

    fireEvent.click(screen.getByLabelText(/close drawer/));
    await waitFor(() => expect(screen.getByLabelText(/open drawer/)?.getAttribute('aria-hidden')).toEqual('false'));

    expect(screen.getByLabelText(/close drawer/)?.getAttribute('aria-hidden')).toEqual('true');

    fireEvent.click(screen.getByLabelText(/open drawer/));
    await waitFor(() => expect(screen.getByLabelText(/close drawer/)?.getAttribute('aria-hidden')).toEqual('false'));

    expect(screen.getByLabelText(/open drawer/)?.getAttribute('aria-hidden')).toEqual('true');
  });
});