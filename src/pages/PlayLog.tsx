import Chart from '../samples/Chart';
import Orders from '../samples/Orders';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';


export default function PlayLog() {
    return (
      <Grid container spacing={3}>
        {/* Recent Orders */}
        <Grid item xs={12}>
          <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column' }}>
            <Orders />
          </Paper>
        </Grid>
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
      </Grid>
    );
}