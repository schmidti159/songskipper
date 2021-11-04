import { Box, IconButton, TextField, Typography } from "@mui/material";
import AudiotrackIcon from '@mui/icons-material/Audiotrack';
import PersonIcon from '@mui/icons-material/Person';
import AlbumIcon from '@mui/icons-material/Album';
import RemoveCircleIcon from '@mui/icons-material/RemoveCircle';
import EditIcon from '@mui/icons-material/Edit';
import { Condition } from "./RuleCard";
import UndoIcon from '@mui/icons-material/Undo';
import { KeyboardEventHandler } from "react";

interface RuleConditionProps {
  condition: Condition
  toggleChangeMode: (() => void)
  onChange: ((expression?: string) => void)
  onSave: (() => void)
}

export default function RuleCondition(props: RuleConditionProps) {
  const condition = props.condition
  const type = condition.type
  let icon, description;
  if (type === 'track') {
    icon = <AudiotrackIcon sx={{ m: 1 }} />;
    description = 'Track';
  } else if (type === 'artist') {
    icon = <PersonIcon sx={{ m: 1 }} />;
    description = 'Artist';
  } else if (type === 'album') {
    icon = <AlbumIcon sx={{ m: 1 }} />;
    description = 'Album';
  }
  const text = condition.expression?.split(':').slice(1, -1).join(':')
  const undo = () => {
    props.onChange(condition.initialExpression);
    props.toggleChangeMode();
  }
  const keyHandler: KeyboardEventHandler = (event) => {
    if (event.key === 'Enter') {
      props.onSave();
      event.preventDefault();
    } else if (event.key === 'Escape') {
      undo();
      event.preventDefault();
    }
  }

  const body = (condition.inChangeMode) ? (
    <>
      <TextField id={"condition_" + type} label={description} variant="standard" autoFocus
        value={text}
        onChange={(event) => props.onChange("g:" + event.target.value + ":bi") /*TODO add selectors for the expression flags*/}
        onKeyDown={keyHandler} />

      <IconButton aria-label="edit title" sx={{ marginLeft: 'auto' }}
        onClick={undo}>
        <UndoIcon />
      </IconButton>
    </>
  ) : (
    <>
      <Typography variant="caption" component="p" sx={{ m: 1, marginLeft: 0 }}
        onClick={props.toggleChangeMode}>
        {description}
      </Typography>
      <Typography variant="body1" sx={{ m: 1 }}
        onClick={props.toggleChangeMode}>
        {condition.expression?.split(':').slice(1, -1).join(':')}
      </Typography>
      <IconButton aria-label="edit condition" sx={{ marginLeft: 'auto' }}
        onClick={props.toggleChangeMode}>
        <EditIcon />
      </IconButton>
    </>
  )
  return (
    <Box component="div" sx={{ display: 'flex', alignItems: 'flex-end' }}>
      {icon}
      {body}
      <IconButton aria-label="remove condition">
        <RemoveCircleIcon />
      </IconButton>
    </Box>);
}
