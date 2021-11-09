import { Button, Card, CardActions, CardContent, CardMedia, Link, Typography } from '@mui/material'
import SkipPreviousIcon from '@mui/icons-material/SkipPrevious';
import SkipNextIcon from '@mui/icons-material/SkipNext';
import { Fragment } from 'react';
import { api } from '../../api/api';
import PlayingProgressBar from './PlayingProgressBar';

export default function CurrentlyPlayingCard() {
  const { data, isLoading } = api.useUpdateCurrentlyPlayingStateQuery()
  const [previousTrack, { isLoading: previousTrackIsLoading }] = api.usePreviousTrackMutation()
  const [nextTrack, { isLoading: nextTrackIsLoading }] = api.useNextTrackMutation()

  let cardMedia, cardContent;
  const emptyCard = isLoading || data == null || data.track == null
  if (emptyCard) {
    // empty card
    cardContent = <Typography variant="h5" component="p">--</Typography>;
  } else {
    let { track, isPaused, progressMs } = data
    track = track!
    cardMedia = (
      <Link href={track.album.url} target="_blank" rel="noopener">
        <CardMedia
          component="img"
          sx={{ maxWidth: 100, maxHeight: 100, width: '100%' }}
          image={track.album.albumArtUrl}
          alt="album art" />
      </Link>
    )
    const artists = track.artists.map<React.ReactNode>(
      artist =>
        <Link href={artist.url} target="_blank" rel="noopener" key={artist.url}>
          {artist.name}
        </Link>
    ).reduce((prev, cur) => [prev, ', ', cur]);
    cardContent = (
      <Fragment>
        <Typography variant="h5" component="p">
          <Link href={track.url} target="_blank" rel="noopener">
            {track.title}
          </Link>
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {artists}
          {' - '}
          <Link href={track.album.url} target="_blank" rel="noopener">
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
          <Button startIcon={<SkipNextIcon />} variant="contained"
            disabled={emptyCard || nextTrackIsLoading}
            onClick={() => nextTrack()}>Next</Button>
        </CardActions>
      </CardContent>
    </Card>
  )
}

