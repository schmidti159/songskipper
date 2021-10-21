import Grid from '@mui/material/Grid';

import { useAppSelector } from '../../app/hooks'
import CurrentlyPlayingCard from './CurrentlyPlayingCard';
import SkipperCard from './SkipperCard';
import { selectCurrentlyPlayingIsPaused, selectCurrentlyPlayingProgress, selectCurrentlyPlayingTrack } from './currentlyPlayingSlice';


export default function CurrentlyPlayingPage() {
  const track = useAppSelector(selectCurrentlyPlayingTrack);
  const progressMs = useAppSelector(selectCurrentlyPlayingProgress);
  const isPaused = useAppSelector(selectCurrentlyPlayingIsPaused);

  return (
    <Grid container spacing={3}>
      <Grid item xs={12} md={8} lg={9}>
        <CurrentlyPlayingCard track={track} progressMs={progressMs} isPaused={isPaused}/>
      </Grid>
      <Grid item xs={6} md={4} lg={3}>
        <SkipperCard/>
      </Grid>
    </Grid>
  );
}