import { Card, CardActions, CardContent } from '@mui/material';
import { playerApi } from '../../api/playerApi';
import CreateRuleFromTrackButton from '../skipRules/CreateRuleFromTrackButton';
import PlayerControlButtons from './PlayerControlButtons';
import TrackCardContent from './TrackCardContent';
import TrackCardMedia from './TrackCardMedia';

export default function CurrentlyPlayingCard() {
  const { data } = playerApi.useUpdateCurrentlyPlayingStateQuery();
  const noTrack = data?.track == null;

  return (
    <Card sx={{ display: 'flex', alignItems: 'left', flexDirection: { xs: 'column', sm: 'inherit' } }} >
      <TrackCardMedia track={data?.track} />
      <CardContent sx={{ width: '100%' }}>
        <CreateRuleFromTrackButton track={data?.track} />
        <TrackCardContent track={data?.track} isPaused={data?.isPaused} progressMs={data?.progressMs} />
        <CardActions sx={{ padding: 0, paddingTop: 1 }}>
          <PlayerControlButtons noTrack={noTrack} isPaused={data?.isPaused} />
        </CardActions>
      </CardContent>
    </Card>
  );
}

