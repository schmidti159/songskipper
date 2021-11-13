import { Button } from '@mui/material';
import SkipPreviousIcon from '@mui/icons-material/SkipPrevious';
import SkipNextIcon from '@mui/icons-material/SkipNext';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import PauseIcon from '@mui/icons-material/Pause';
import { playerApi } from '../../api/playerApi';

interface PlayerControlButtonsProps {
  noTrack: boolean,
  isPaused?: boolean;
}

export default function PlayerControlButtons(props: PlayerControlButtonsProps) {
  const [previousTrack, { isLoading: previousTrackIsLoading }] = playerApi.usePreviousTrackMutation();
  const [nextTrack, { isLoading: nextTrackIsLoading }] = playerApi.useNextTrackMutation();
  const [startPlayback, { isLoading: startPlaybackIsLoading }] = playerApi.usePlayMutation();
  const [pausePlayback, { isLoading: pausePlaybackIsLoading }] = playerApi.usePauseMutation();

  let playPauseButton = (
    <Button startIcon={<PlayArrowIcon />} variant="text"
      disabled={props.noTrack || startPlaybackIsLoading || pausePlaybackIsLoading}
      onClick={() => startPlayback()}>
      Play
    </Button>
  );
  if (!props.isPaused) {
    playPauseButton = (
      <Button startIcon={<PauseIcon />} variant="text"
        disabled={props.noTrack || startPlaybackIsLoading || pausePlaybackIsLoading}
        onClick={() => pausePlayback()}>
        Pause
      </Button>
    );
  }
  return <>
    <Button startIcon={<SkipPreviousIcon />} variant="text"
      disabled={props.noTrack || previousTrackIsLoading}
      onClick={() => previousTrack()}>Previous</Button>
    {playPauseButton}
    <Button startIcon={<SkipNextIcon />} variant="text"
      disabled={props.noTrack || nextTrackIsLoading}
      onClick={() => nextTrack()}>Next</Button>
  </>;
}

