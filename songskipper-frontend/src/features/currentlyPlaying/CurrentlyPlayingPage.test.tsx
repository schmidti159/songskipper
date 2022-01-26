import { rest } from 'msw';
import { setupServer } from 'msw/node';
import { act } from 'react-dom/test-utils';
import { api } from '../../api/api';
import { store } from '../../app/store';
import { Track } from '../../common/types';
import { fireEvent, render, screen, waitFor, within } from '../../test-utils';
import CurrentlyPlayingPage from './CurrentlyPlayingPage';
import PlayerControlButtons from './PlayerControlButtons';
import SkipperCard from './SkipperCard';
import TrackCardContent from './TrackCardContent';
import TrackCardMedia from './TrackCardMedia';

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
    durationMs: 42000
  };

  const server = setupServer(
    rest.get('/api/player/v1/currently-playing-track', (req, res, ctx) => {
      return res(ctx.status(200));
    }),
    rest.post('/api/player/v1/previous', (req, res, ctx) => {
      return res(ctx.status(200));
    }),
    rest.post('/api/player/v1/next', (req, res, ctx) => {
      return res(ctx.status(200));
    }),
    rest.post('/api/player/v1/play', (req, res, ctx) => {
      return res(ctx.status(200));
    }),
    rest.post('/api/player/v1/pause', (req, res, ctx) => {
      return res(ctx.status(200));
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

  describe('SkipperCard', () => {
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
  });

  describe('TrackCardMedia', () => {
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
  });

  describe('TrackCardContent', () => {
    test('TrackCardContent shows all track content', () => {
      jest.useFakeTimers();

      render(<TrackCardContent track={track} isPaused={false} progressMs={23000} />);

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

      let progressbar = screen.getByRole('progressbar');
      expect(screen.getByText(/0:23\s*\/\s*0:42/)).toBeInTheDocument();
      expect(progressbar.getAttribute('aria-valuemax')).toEqual('100');
      expect(progressbar.getAttribute('aria-valuemin')).toEqual('0');
      expect(progressbar.getAttribute('aria-valuenow')).toEqual('55');

      act(() => {
        jest.advanceTimersByTime(2000);
      });

      progressbar = screen.getByRole('progressbar');
      expect(screen.getByText(/0:25\s*\/\s*0:42/)).toBeInTheDocument();
      expect(progressbar.getAttribute('aria-valuemax')).toEqual('100');
      expect(progressbar.getAttribute('aria-valuemin')).toEqual('0');
      expect(progressbar.getAttribute('aria-valuenow')).toEqual('60');
      jest.useRealTimers();
    });

    test('TrackCardContent is empty if there is no track', () => {
      render(<TrackCardContent track={undefined} />);
      expect(screen.getByText('--')).toBeInTheDocument();
    });
  });
  describe('PlayerControlButtons', () => {
    test('PlayerControlButtons can skip forward, backward and pause if a track is playing', async () => {
      render(<PlayerControlButtons noTrack={false} isPaused={false} />);

      const buttons = screen.getAllByRole('button');
      expect(buttons).toHaveLength(3);
      expect(within(buttons[0]).getByText(/Previous/)).toBeInTheDocument();
      expect(within(buttons[1]).getByText(/Pause/)).toBeInTheDocument();
      expect(within(buttons[2]).getByText(/Next/)).toBeInTheDocument();

      // buttons are disabled until the result is available
      fireEvent.click(screen.getAllByRole('button')[0]);
      await waitFor(() => expect(screen.getAllByRole('button')[0]).toHaveAttribute('disabled'));
      await waitFor(() => expect(screen.getAllByRole('button')[0]).not.toHaveAttribute('disabled'));
      fireEvent.click(screen.getAllByRole('button')[1]);
      await waitFor(() => expect(screen.getAllByRole('button')[1]).toHaveAttribute('disabled'));
      await waitFor(() => expect(screen.getAllByRole('button')[1]).not.toHaveAttribute('disabled'));
      fireEvent.click(screen.getAllByRole('button')[2]);
      await waitFor(() => expect(screen.getAllByRole('button')[2]).toHaveAttribute('disabled'));
      await waitFor(() => expect(screen.getAllByRole('button')[2]).not.toHaveAttribute('disabled'));
    });

    test('PlayerControlButtons can skip forward, backward and resume if a track is paused', async () => {
      render(<PlayerControlButtons noTrack={false} isPaused={true} />);

      const buttons = screen.getAllByRole('button');
      expect(buttons).toHaveLength(3);
      expect(within(buttons[0]).getByText(/Previous/)).toBeInTheDocument();
      expect(within(buttons[1]).getByText(/Play/)).toBeInTheDocument();
      expect(within(buttons[2]).getByText(/Next/)).toBeInTheDocument();

      fireEvent.click(screen.getAllByRole('button')[1]);
      await waitFor(() => expect(screen.getAllByRole('button')[1]).toHaveAttribute('disabled'));
      await waitFor(() => expect(screen.getAllByRole('button')[1]).not.toHaveAttribute('disabled'));
    });

    test('PlayerControlButtons is disabled if there is no track', async () => {
      render(<PlayerControlButtons noTrack={true} isPaused={false} />);

      const buttons = screen.getAllByRole('button');
      expect(buttons).toHaveLength(3);
      expect(within(buttons[0]).getByText(/Previous/)).toBeInTheDocument();
      expect(buttons[0]).toHaveAttribute('disabled');
      expect(within(buttons[1]).getByText(/Pause/)).toBeInTheDocument();
      expect(buttons[1]).toHaveAttribute('disabled');
      expect(within(buttons[2]).getByText(/Next/)).toBeInTheDocument();
      expect(buttons[2]).toHaveAttribute('disabled');
    });
  });
});