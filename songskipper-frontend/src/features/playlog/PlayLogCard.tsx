import { Card, CardContent, Typography } from '@mui/material';
import { Box } from '@mui/system';
import { PlayLogTrack } from '../../common/types';
import TrackCardContent from '../currentlyPlaying/TrackCardContent';
import TrackCardMedia from '../currentlyPlaying/TrackCardMedia';
import CreateRuleFromTrackButton from '../skipRules/CreateRuleFromTrackButton';

interface PlayLogCardProps {
  logEntry: PlayLogTrack;
}

export default function PlayLogCard(props: PlayLogCardProps) {
  const { track, playedOn, matchingRuleIds } = props.logEntry;
  const wasSkipped = matchingRuleIds.length > 0;
  return (
    <Card sx={{ display: 'flex', alignItems: 'center' }}>
      <TrackCardMedia track={track} maxWidth={100} />
      <CardContent sx={{ width: '100%' }}>
        <CreateRuleFromTrackButton track={track} />
        <Typography variant='caption' component='p'>
          {playedOn}
          {wasSkipped && (
            <>
              {' - '}
              <Box component='span' sx={{ color: wasSkipped ? 'secondary.light' : '' }}>
                Skipped
              </Box>
            </>
          )}
        </Typography>
        <TrackCardContent track={track} />
      </CardContent>
    </Card>
  );
}