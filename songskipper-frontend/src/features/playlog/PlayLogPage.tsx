
import { CircularProgress } from '@mui/material';
import Grid from '@mui/material/Grid';
import { api } from '../../api/api';
import PlayLogCard from './PlayLogCard';


export default function PlayLog() {
  const { data: playLogEntries, isLoading } = api.useGetPlayLogQuery()
  if (isLoading || playLogEntries === undefined) {
    return <CircularProgress />
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