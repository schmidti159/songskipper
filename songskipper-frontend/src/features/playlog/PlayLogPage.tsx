
import Grid from '@mui/material/Grid';
import PlayLogCard from './PlayLogCard';


export default function PlayLog() {
    return (
      <Grid container spacing={3}>
        {/* Recent Orders */}
        <Grid item xs={12}>
          <PlayLogCard/>
        </Grid>
      </Grid>
    );
}