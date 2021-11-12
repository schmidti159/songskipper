import { Card, CardContent, Typography } from '@mui/material'
import { PlayLogTrack } from '../../common/types';
import TrackCardContent from '../currentlyPlaying/TrackCardContent';
import TrackCardMedia from '../currentlyPlaying/TrackCardMedia';

interface PlayLogCardProps {
  logEntry: PlayLogTrack
}

export default function PlayLogCard(props: PlayLogCardProps) {
  const { track, playedOn, matchingRuleIds } = props.logEntry;
  return (
    <Card sx={{ display: 'flex', backgroundColor: (matchingRuleIds.length > 0 ? "secondary.dark" : "") }}>
      <TrackCardMedia track={track} />
      <CardContent sx={{ width: '100%' }}>
        <Typography variant="caption" component="p">
          {playedOn}
        </Typography>
        <TrackCardContent track={track} />
      </CardContent>
    </Card>
  )
}