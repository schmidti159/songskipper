import { IconButton, TextField, Typography } from "@mui/material";
import UndoIcon from '@mui/icons-material/Undo';
import EditIcon from '@mui/icons-material/Edit';
import { KeyboardEventHandler } from "react";


interface RuleTitleProps {
  title: string
  initialTitle: string
  inChangeMode: boolean
  toggleChangeMode: (() => void)
  onChange: ((title: string) => void)
  onSave: (() => void)
}

export default function RuleTitle(props: RuleTitleProps) {
  const undo = () => {
    props.onChange(props.initialTitle);
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

  if (props.inChangeMode) {
    return <>
      <TextField id="title" label="Title" variant="standard" autoFocus
        value={props.title}
        onChange={(event) => props.onChange(event.target.value)}
        onKeyDown={keyHandler} />

      <IconButton aria-label="edit title" sx={{ marginLeft: 'auto' }}
        onClick={undo}>
        <UndoIcon />
      </IconButton>
    </>
  } else {
    return <>
      <Typography variant="h6" component="p" onClick={props.toggleChangeMode}>{props.title}</Typography>

      <IconButton aria-label="edit title" sx={{ marginLeft: 'auto' }} onClick={props.toggleChangeMode}>
        <EditIcon />
      </IconButton>
    </>
  }
}
