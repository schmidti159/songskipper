import { Button, Card, CardActions, CardContent, CardMedia, Link, Typography } from '@mui/material'
import SkipPreviousIcon from '@mui/icons-material/SkipPrevious';
import SkipNextIcon from '@mui/icons-material/SkipNext';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import PauseIcon from '@mui/icons-material/Pause';
import { Fragment } from 'react';
import { api } from '../../api/api';
import PlayingProgressBar from './PlayingProgressBar';

export default function CurrentlyPlayingCard() {
  const { data, isLoading } = api.useUpdateCurrentlyPlayingStateQuery()
  const [previousTrack, { isLoading: previousTrackIsLoading }] = api.usePreviousTrackMutation()
  const [nextTrack, { isLoading: nextTrackIsLoading }] = api.useNextTrackMutation()
  const [startPlayback, { isLoading: startPlaybackIsLoading }] = api.usePlayMutation()
  const [pausePlayback, { isLoading: pausePlaybackIsLoading }] = api.usePauseMutation()

  let cardMedia, cardContent;
  const emptyCard = isLoading || data == null || data.track == null
  if (emptyCard) {
    // empty card
    cardContent = <Typography variant="h5" component="p">--</Typography>;
  } else {
    let { track, isPaused, progressMs } = data
    track = track!
    cardMedia = (
      <Link href={track.album.url} target="_blank" rel="noopener" color="inherit">
        <CardMedia
          component="img"
          sx={{ maxWidth: 100, maxHeight: 100, width: '100%' }}
          image={track.album.albumArtUrl}
          alt="album art" />
      </Link>
    )
    const artists = track.artists.map<React.ReactNode>(
      artist =>
        <Link href={artist.url} target="_blank" rel="noopener" color="inherit" key={artist.url}>
          {artist.name}
        </Link>
    ).reduce((prev, cur) => [prev, ', ', cur]);
    cardContent = (
      <Fragment>
        <Typography variant="h5" component="p">
          <Link href={track.url} target="_blank" rel="noopener" color="inherit">
            {track.title}
          </Link>
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {artists}
          {' - '}
          <Link href={track.album.url} target="_blank" rel="noopener" color="inherit">
            {track.album.title}
          </Link>
        </Typography>
        <PlayingProgressBar isPaused={isPaused} durationMs={track.durationMs} progressMs={progressMs || 0} />
      </Fragment>
    )
  }
  return (
    <Card sx={{ display: 'flex' }}>
      {cardMedia}
      <CardContent sx={{ width: '100%' }}>
        <Typography variant="h6" component="p">Currently Playing {data?.isPaused && ' - Paused'}</Typography>
        {cardContent}
        <CardActions>
          <Button startIcon={<SkipPreviousIcon />} variant="text" sx={{ marginLeft: 'auto' }}
            disabled={emptyCard || previousTrackIsLoading}
            onClick={() => previousTrack()}>Previous</Button>
          {data?.isPaused && <Button startIcon={<PlayArrowIcon />} variant="text"
            disabled={emptyCard || startPlaybackIsLoading || pausePlaybackIsLoading}
            onClick={() => startPlayback()}>Play</Button>
          }
          {data?.isPaused || <Button startIcon={<PauseIcon />} variant="text"
            disabled={emptyCard || startPlaybackIsLoading || pausePlaybackIsLoading}
            onClick={() => pausePlayback()}>Pause</Button>
          }
          <Button startIcon={<SkipNextIcon />} variant="contained"
            disabled={emptyCard || nextTrackIsLoading}
            onClick={() => nextTrack()}>Next</Button>
        </CardActions>
      </CardContent>
    </Card>
  )
}

