
import { CircularProgress } from '@mui/material';
import Grid from '@mui/material/Grid';
import { playlogApi } from '../../api/playlogApi';
import PlayLogCard from './PlayLogCard';


export default function PlayLog() {
  const { data: playLogEntries, isLoading } = playlogApi.useGetPlayLogQuery();
  if (isLoading || playLogEntries === undefined) {
    return <CircularProgress />;
  }
  return (
    <Grid container spacing={3}>
      {playLogEntries.map(logEntry => (
        <Grid item xs={12} md={6} lg={4} key={logEntry.playedOn}>
          <PlayLogCard logEntry={logEntry} />
        </Grid>
      ))}
    </Grid>
  );
}