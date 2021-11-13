import { Card, CardContent, Typography } from '@mui/material';
import { PlayLogTrack } from '../../common/types';
import CreateRuleFromTrackButton from '../skipRules/CreateRuleFromTrackButton';
import TrackCardContent from '../currentlyPlaying/TrackCardContent';
import TrackCardMedia from '../currentlyPlaying/TrackCardMedia';

interface PlayLogCardProps {
  logEntry: PlayLogTrack;
}

export default function PlayLogCard(props: PlayLogCardProps) {
  const { track, playedOn, matchingRuleIds } = props.logEntry;
  return (
    <Card sx={{ display: 'flex', alignItems: 'center', backgroundColor: (matchingRuleIds.length > 0 ? "secondary.dark" : "") }}>
      <TrackCardMedia track={track} maxWidth={100} />
      <CardContent sx={{ width: '100%' }}>
        <CreateRuleFromTrackButton track={track} />
        <Typography variant="caption" component="p">
          {playedOn}
        </Typography>
        <TrackCardContent track={track} />
      </CardContent>
    </Card>
  );
}