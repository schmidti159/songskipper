import { Box, IconButton, TextField, Typography } from "@mui/material";
import AudiotrackIcon from '@mui/icons-material/Audiotrack';
import PersonIcon from '@mui/icons-material/Person';
import AlbumIcon from '@mui/icons-material/Album';
import RemoveCircleIcon from '@mui/icons-material/RemoveCircle';
import EditIcon from '@mui/icons-material/Edit';
import UndoIcon from '@mui/icons-material/Undo';
import { KeyboardEventHandler } from "react";
import { Condition } from "../../common/types";

interface RuleConditionProps {
  condition: Condition
  toggleChangeMode: (() => void)
  onChange: ((expression?: string) => void)
  onDelete: (() => void)
  onSave: (() => void)
}

export function iconForType(type: string) {
  switch (type) {
    case 'track': return <AudiotrackIcon sx={{ m: 1 }} />
    case 'artist': return <PersonIcon sx={{ m: 1 }} />
    case 'album': return <AlbumIcon sx={{ m: 1 }} />
  }
}
export function descriptionForType(type: string) {
  switch (type) {
    case 'track': return 'Title'
    case 'artist': return 'Artist'
    case 'album': return 'Album'
  }
}

export default function RuleCondition(props: RuleConditionProps) {
  const condition = props.condition
  const type = condition.type

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
      <TextField id={"condition_" + type} label={descriptionForType(type)} variant="standard" autoFocus
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
      <Typography variant="body1" component="p" sx={{ m: 1, marginLeft: 0 }}
        onClick={props.toggleChangeMode}>
        {descriptionForType(type)}:
      </Typography>
      <Typography variant="body1" sx={{ m: 1, width: '100%' }}
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
      {iconForType(type)}
      {body}
      <IconButton aria-label="remove condition" onClick={props.onDelete}>
        <RemoveCircleIcon />
      </IconButton>
    </Box>);
}
