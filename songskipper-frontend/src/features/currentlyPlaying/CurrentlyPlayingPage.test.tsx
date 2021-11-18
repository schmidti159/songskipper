import { rest } from 'msw';
import { setupServer } from 'msw/node';
import React from 'react';
import { TextDecoder, TextEncoder } from 'util';
import { api } from '../../api/api';
import { store } from '../../app/store';
import { CurrentlyPlayingState, Track } from '../../common/types';
import { fireEvent, render, screen, waitFor, within } from '../../test-utils';
import CurrentlyPlayingPage from './CurrentlyPlayingPage';
import SkipperCard from './SkipperCard';
import TrackCardContent from './TrackCardContent';
import TrackCardMedia from './TrackCardMedia';

global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;

describe('CurrentlyPlayingPage', () => {
  const track: Track = {
    title: 'myTitle',
    url: 'track_url',
    artists: [{
      name: 'artist_1',
      url: 'artist_1_url'
    }, {
      name: 'artist_2',
      url: 'artist_2_url'
    }],
    album: {
      title: 'album',
      url: 'album_url',
      albumArtUrl: 'album_art_url'
    },
    durationMs: 42
  };
  const currentlyPlaying: CurrentlyPlayingState = {
    track: track,
    isPaused: false,
    progressMs: 23
  };
  const server = setupServer(
    rest.get('/api/player/v1/currently-playing-track', (req, res, ctx) => {
      return res(ctx.json(track));
    }),
    rest.get('/api/skipper/v1/active', (req, res, ctx) => {
      return res(ctx.body('true'));
    }),
    rest.get('/api/skipper/v1/start', (req, res, ctx) => {
      return res(ctx.status(200));
    }),
    rest.get('/api/skipper/v1/stop', (req, res, ctx) => {
      return res(ctx.status(200));
    }),
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

  test('shows currently playing card and skipper enabled card', async () => {
    render(<CurrentlyPlayingPage />);

    expect(screen.getByText('--')).toBeInTheDocument(); // no title loaded
    expect(screen.getByText('Next')).toBeInTheDocument();
    expect(screen.getByText(/Skipper enabled/i)).toBeInTheDocument();
  });

  test('SkipperCard shows whether the skipper is active', async () => {
    render(<SkipperCard />);

    expect(screen.getByRole('checkbox')).toBeInTheDocument();
    expect(screen.getByRole('checkbox')).toHaveAttribute('disabled');

    await waitFor(() => expect(screen.getByRole('checkbox').getAttribute('value')).toEqual('true'));
    expect(screen.getByRole('checkbox')).not.toHaveAttribute('disabled');

    fireEvent.click(screen.getByRole('checkbox'));
    await waitFor(() => expect(screen.getByRole('checkbox').getAttribute('value')).toEqual('false'));

    fireEvent.click(screen.getByRole('checkbox'));
    await waitFor(() => expect(screen.getByRole('checkbox').getAttribute('value')).toEqual('true'));
  });

  test('TrackCardMedia shows media', () => {
    render(<TrackCardMedia track={track} />);

    expect(screen.getByRole('link').getAttribute("href")).toEqual('album_url');
    expect(screen.getByAltText("album art").getAttribute("src")).toEqual("album_art_url");
  });

  test('TrackCardMedia shows nothing if there is no track', () => {
    render(<TrackCardMedia track={undefined} />);

    expect(screen.queryByRole('link')).toBeNull();
    expect(screen.queryByAltText("album art")).toBeNull();
  });

  test('TrackCardContent shows all track content', () => {
    render(<TrackCardContent track={track} />);

    const links = screen.queryAllByRole('link');
    expect(links).not.toBeNull();
    expect(links).toHaveLength(4);

    expect(links[0].getAttribute('href')).toEqual('track_url');
    expect(within(links[0]).getByText('myTitle')).toBeInTheDocument();

    expect(links[1].getAttribute('href')).toEqual('artist_1_url');
    expect(within(links[1]).getByText('artist_1')).toBeInTheDocument();

    expect(links[2].getAttribute('href')).toEqual('artist_2_url');
    expect(within(links[2]).getByText('artist_2')).toBeInTheDocument();

    expect(links[3].getAttribute('href')).toEqual('album_url');
    expect(within(links[3]).getByText('album')).toBeInTheDocument();
  });

  test('TrackCardContent is empty if there is no track', () => {
    render(<TrackCardContent track={undefined} />);

    expect(screen.getByText('--')).toBeInTheDocument();
  });
});