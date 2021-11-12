import { Button } from '@mui/material'
import SkipPreviousIcon from '@mui/icons-material/SkipPrevious';
import SkipNextIcon from '@mui/icons-material/SkipNext';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import PauseIcon from '@mui/icons-material/Pause';
import { api } from '../../api/api';

interface PlayerControlButtonsProps {
  noTrack: boolean,
  isPaused?: boolean
}

export default function PlayerControlButtons(props: PlayerControlButtonsProps) {
  const [previousTrack, { isLoading: previousTrackIsLoading }] = api.usePreviousTrackMutation()
  const [nextTrack, { isLoading: nextTrackIsLoading }] = api.useNextTrackMutation()
  const [startPlayback, { isLoading: startPlaybackIsLoading }] = api.usePlayMutation()
  const [pausePlayback, { isLoading: pausePlaybackIsLoading }] = api.usePauseMutation()

  let playPauseButton = (
    <Button startIcon={<PlayArrowIcon />} variant="text"
      disabled={props.noTrack || startPlaybackIsLoading || pausePlaybackIsLoading}
      onClick={() => startPlayback()}>
      Play
    </Button>
  )
  if (!props.isPaused) {
    playPauseButton = (
      <Button startIcon={<PauseIcon />} variant="text"
        disabled={props.noTrack || startPlaybackIsLoading || pausePlaybackIsLoading}
        onClick={() => pausePlayback()}>
        Pause
      </Button>
    )
  }
  return <>
    <Button startIcon={<SkipPreviousIcon />} variant="text" sx={{ marginLeft: 'auto' }}
      disabled={props.noTrack || previousTrackIsLoading}
      onClick={() => previousTrack()}>Previous</Button>
    {playPauseButton}
    <Button startIcon={<SkipNextIcon />} variant="contained"
      disabled={props.noTrack || nextTrackIsLoading}
      onClick={() => nextTrack()}>Next</Button>
  </>
}

