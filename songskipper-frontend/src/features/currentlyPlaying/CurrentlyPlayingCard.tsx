import { Card, CardActions, CardContent } from '@mui/material'
import { api } from '../../api/api';
import PlayerControlButtons from './PlayerControlButtons';
import TrackCardContent from './TrackCardContent';
import TrackCardMedia from './TrackCardMedia';

export default function CurrentlyPlayingCard() {
  const { data } = api.useUpdateCurrentlyPlayingStateQuery()
  const noTrack = data?.track == null

  return (
    <Card sx={{ display: 'flex' }}>
      <TrackCardMedia track={data?.track} />
      <CardContent sx={{ width: '100%' }}>
        <TrackCardContent track={data?.track} isPaused={data?.isPaused} progressMs={data?.progressMs} />
        <CardActions sx={{ padding: 0, paddingTop: 1 }}>
          <PlayerControlButtons noTrack={noTrack} isPaused={data?.isPaused} />
        </CardActions>
      </CardContent>
    </Card>
  )
}

