
import Orders from '../samples/Orders';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';


export default function SkipRules() {
    return (
      <Grid container spacing={3}>
        {/* Recent Orders */}
        <Grid item xs={12}>
          <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column' }}>
            <Orders />
          </Paper>
        </Grid>
      </Grid>
    );
}