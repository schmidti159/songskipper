import { Card, CardContent, CardMedia, Link, Typography } from '@mui/material'
import { Fragment } from 'react';
import type { Track } from '../../common/types'
import PlayingProgressBar from './PlayingProgressBar';

interface CurrentlyPlayingCardProps {
  track?: Track,
  isPaused: boolean,
  progressMs: number
}

export default function CurrentlyPlayingCard(props: CurrentlyPlayingCardProps) {
  let cardMedia, cardContent;
  if(props.track === undefined || props.track === null) {
    // empty card
    cardContent = <Typography variant="h5" component="p">--</Typography>;
  } else {
    const track = props.track;
    cardMedia = (
      <Link href={track.album.url} target="_blank" rel="noopener">
        <CardMedia
          component="img"
          sx={{ width: 150, maxHeight: 150 }}
          image={track.album.albumArtUrl}
          alt="album art"/>
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
        <PlayingProgressBar isPaused={props.isPaused} durationMs={track.durationMs} progressMs={props.progressMs}/>
      </Fragment>
    )
  }
  return (
    <Card sx={{display: 'flex'}}>
      {cardMedia}
      <CardContent sx={{width: '100%'}}>
        <Typography variant="caption" component="p">Currently Playing {props.isPaused && ' - Paused'}</Typography>
        {cardContent}
      </CardContent>
    </Card>
  )
}

  