import { Card, CardContent, CardMedia, Link, Typography } from '@mui/material'
import { Fragment } from 'react';
import { api } from '../../api/api';
import PlayingProgressBar from './PlayingProgressBar';

export default function CurrentlyPlayingCard() {
  const { data, isLoading } = api.useUpdateCurrentlyPlayingStateQuery()

  let cardMedia, cardContent;
  if (isLoading || data == null || data.track == null) {
    // empty card
    cardContent = <Typography variant="h5" component="p">--</Typography>;
  } else {
    let { track, isPaused, progressMs } = data
    cardMedia = (
      <Link href={track.album.url} target="_blank" rel="noopener">
        <CardMedia
          component="img"
          sx={{ width: 150, maxHeight: 150 }}
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
      </CardContent>
    </Card>
  )
}

