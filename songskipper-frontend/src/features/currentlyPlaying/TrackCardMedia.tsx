import { CardMedia, Link } from '@mui/material'
import { Track } from '../../common/types';

interface TrackCardMediaProps {
  track?: Track
}

export default function TrackCardMedia(props: TrackCardMediaProps) {
  const emptyCard = props.track == null
  if (emptyCard) {
    return <></>;
  } else {
    return (
      <Link href={props.track?.album.url} target="_blank" rel="noopener" color="inherit">
        <CardMedia
          component="img"
          sx={{ maxHeight: 190, width: '100%' }}
          image={props.track?.album.albumArtUrl}
          alt="album art" />
      </Link>)
  }
}

