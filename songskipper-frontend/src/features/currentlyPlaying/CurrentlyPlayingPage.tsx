import Grid from '@mui/material/Grid';
import CurrentlyPlayingCard from './CurrentlyPlayingCard';
import SkipperCard from './SkipperCard';

export default function CurrentlyPlayingPage() {
  return (
    <Grid container spacing={3}>
      <Grid item xs={12} md={8} lg={9}>
        <CurrentlyPlayingCard/>
      </Grid>
      <Grid item xs={12} md={4} lg={3}>
        <SkipperCard/>
      </Grid>
    </Grid>
  );
}