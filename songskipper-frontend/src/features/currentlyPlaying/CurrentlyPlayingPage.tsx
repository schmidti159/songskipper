import Chart from '../samples/Chart';
import Deposits from '../samples/Deposits';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import { Card, CardContent, Switch, Typography } from '@mui/material';

import { useAppSelector } from '../../app/hooks'
import CurrentlyPlayingCard from './CurrentlyPlayingCard';


export default function CurrentlyPlayingPage() {
  const track = useAppSelector(state => state.currentlyPlaying.track);
  const progressMs = useAppSelector(state => state.currentlyPlaying.progressMs) || 0;
  const isPaused = useAppSelector(state => state.currentlyPlaying.isPaused);

  return (
    
    <Grid container spacing={3}>
      <Grid item xs={12} md={8} lg={9}>
        <CurrentlyPlayingCard track={track} progressMs={progressMs} isPaused={isPaused}/>
      </Grid>
      <Grid item xs={6} md={4} lg={3}>
        <Card>
          <CardContent>
            <Typography variant="caption" component="p">Skipper Enabled</Typography>
            <Switch checked />
          </CardContent>
        </Card>
      </Grid>
    </Grid>);
}

export function CurrentlyPlaying2() {
  return (
    <Grid container spacing={3}>
      {/* Chart */}
      <Grid item xs={12} md={8} lg={9}>
        <Paper
          sx={{
            p: 2,
            display: 'flex',
            flexDirection: 'column',
            height: 240,
          }}
        >
          <Chart />
        </Paper>
      </Grid>
      {/* Recent Deposits */}
      <Grid item xs={12} md={4} lg={3}>
        <Paper
          sx={{
            p: 2,
            display: 'flex',
            flexDirection: 'column',
            height: 240,
          }}
        >
          <Deposits />
        </Paper>
      </Grid>
    </Grid>
  );
}