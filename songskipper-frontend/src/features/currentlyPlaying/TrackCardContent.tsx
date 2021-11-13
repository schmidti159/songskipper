import { Link, Typography } from '@mui/material'
import PlayingProgressBar from './PlayingProgressBar';
import { Track } from '../../common/types';

interface TrackCardContentProps {
  track?: Track,
  isPaused?: boolean,
  progressMs?: number
}

export default function TrackCardContent(props: TrackCardContentProps) {
  const emptyCard = props.track == null
  if (emptyCard) {
    // empty card
    return <Typography variant="h5" component="p">--</Typography>;
  } else {
    const track = props.track!
    const artists = track.artists.map<React.ReactNode>(
      artist =>
        <Link href={artist.url} target="_blank" rel="noopener" color="inherit" key={artist.url}>
          {artist.name}
        </Link>
    ).reduce((prev, cur) => [prev, ', ', cur]);
    return <>
      <Typography variant="body1" component="p">
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
      {props.progressMs !== undefined &&
        <PlayingProgressBar isPaused={props.isPaused}
          durationMs={track.durationMs} progressMs={props.progressMs || 0} />}
    </>
  }
}

