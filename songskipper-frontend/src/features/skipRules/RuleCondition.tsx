import { Box, IconButton, Typography } from "@mui/material";
import AudiotrackIcon from '@mui/icons-material/Audiotrack';
import PersonIcon from '@mui/icons-material/Person';
import AlbumIcon from '@mui/icons-material/Album';
import RemoveCircleIcon from '@mui/icons-material/RemoveCircle';
import EditIcon from '@mui/icons-material/Edit';
import { ConditionType } from "./RuleCard";

interface RuleConditionProps {
  type: ConditionType
  expression?: string
  onChange: ((expression: string) => void)
}

export default function RuleCondition(props: RuleConditionProps) {
  let icon, description;
  if (props.type === 'track') {
    icon = <AudiotrackIcon sx={{ m: 1, marginRight: 0 }} />;
    description = 'Track';
  } else if (props.type === 'artist') {
    icon = <PersonIcon sx={{ m: 1, marginRight: 0 }} />;
    description = 'Artist';
  } else if (props.type === 'album') {
    icon = <AlbumIcon sx={{ m: 1, marginRight: 0 }} />;
    description = 'Album';
  }
  return (
    <Box component="p" sx={{ display: 'flex', alignItems: 'flex-end' }}>
      {icon}
      <Typography variant="caption" component="span" sx={{ m: 1 }}>
        {description}
      </Typography>
      <Typography variant="body1" component="span" sx={{ m: 1 }}>
        {props.expression?.split(':').slice(1, -1).join(':')}
      </Typography>
      <IconButton aria-label="edit condition" sx={{ marginLeft: 'auto' }}>
        <EditIcon />
      </IconButton>
      <IconButton aria-label="remove condition">
        <RemoveCircleIcon />
      </IconButton>
    </Box>);
}
