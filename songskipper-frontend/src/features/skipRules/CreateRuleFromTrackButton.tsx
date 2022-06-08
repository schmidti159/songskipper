import MoreVertIcon from '@mui/icons-material/MoreVert';
import { IconButton, Menu, MenuItem, Typography } from "@mui/material";
import React from "react";
import { useNavigate } from "react-router-dom";
import { rulesApi } from '../../api/rulesApi';
import { ConditionType, Rule, Track } from '../../common/types';
import { descriptionForType, iconForType } from "./RuleCondition";

interface CreateRuleFromTrackButtonProps {
  track?: Track;
}

function extractValueFromRule(type: ConditionType, track: Track): string {
  switch (type) {
    case 'track': return track.title;
    case 'artist': return track.artists[0].name; // use the first one (any will skip this track)
    case 'album': return track.album.title;
  }
}

function ruleFromType(type: ConditionType, track: Track): Rule {
  const expression = extractValueFromRule(type, track);
  return {
    id: 'new-id',
    title: expression,
    titleExpression: type === 'track' ? `g:${expression}:bi` : undefined,
    artistExpression: type === 'artist' ? `g:${expression}:bi` : undefined,
    albumExpression: type === 'album' ? `g:${expression}:bi` : undefined
  };
};

export default function CreateRuleFromTrackButton(props: CreateRuleFromTrackButtonProps) {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  const [createRule] = rulesApi.useCreateRuleMutation();
  const conditionTypes: ConditionType[] = [
    'track', 'artist', 'album'
  ];
  const navigate = useNavigate();
  if (props.track == null) {
    return <></>;
  }
  const track = props.track;
  return (
    <>
      <IconButton aria-label="option menu" sx={{ float: 'right' }}
        disabled={props.track == null}
        onClick={handleClick}>
        <MoreVertIcon />
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        onClick={handleClose}
        PaperProps={{ elevation: 3 }}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
      >
        {conditionTypes.map(type => (
          <MenuItem key={type} onClick={() => { createRule(ruleFromType(type, track)); navigate('/rules'); }}>
            {iconForType(type)}
            <Typography variant="body1" component="p" sx={{ m: 1, marginLeft: 0 }}>
              Create rule from {descriptionForType(type)}
            </Typography>
          </MenuItem>
        ))}
      </Menu>
    </>);
}
