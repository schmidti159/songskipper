import { Card, CardContent, CircularProgress, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material'
import { api } from '../../api/api';
import { PlayLogTrack } from '../../common/types';

interface PlayLogRowProps {
  entry: PlayLogTrack
}

function PlayLogRow(props: PlayLogRowProps) { 
  const {track, playedOn, matchingRuleIds} = props.entry
  return (
  <TableRow key={track.url} sx={{backgroundColor: (matchingRuleIds.length > 0 ? "warning.dark":"")}}>
    <TableCell>{playedOn}</TableCell>
    <TableCell>{track.title}</TableCell>
    <TableCell>{track.artists.map(artist => artist.name).join(", ")}</TableCell>
    <TableCell><img src={track.album.albumArtUrl} alt="album art" width="50" height="50"/></TableCell>
    <TableCell>{track.album.title}</TableCell>
  </TableRow>
  )
}

export default function PlayLogCard() {
  const {data: playLogEntries, isLoading} = api.useGetPlayLogQuery()
  const content = isLoading ? (
    <CircularProgress/>
  ) : (
    <Table size="small">
      <TableHead>
        <TableRow>
          <TableCell>Date</TableCell>
          <TableCell>Track</TableCell>
          <TableCell>Artist(s)</TableCell>
          <TableCell colSpan={2}>Album</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {playLogEntries && playLogEntries.map(
          (entry) => <PlayLogRow entry={entry}/>)}
      </TableBody>
    </Table>
  )
  return (
    <Card sx={{display: 'flex'}}>
      <CardContent sx={{width: '100%'}}>
        <Typography variant="caption" component="p">Play Log</Typography>
        {content}
      </CardContent>
    </Card>
  )
}