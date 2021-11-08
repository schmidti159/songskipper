import { IconButton, Menu, MenuItem, Typography } from "@mui/material";
import AddCircleIcon from '@mui/icons-material/AddCircle';
import React from "react";
import { descriptionForType, iconForType } from "./RuleCondition";
import { ConditionType } from "../../common/types";

interface AddConditionButtonProps {
  possibleTypes: ConditionType[]
  onNewCondition: ((type: ConditionType) => void)
}

export default function AddConditionButton(props: AddConditionButtonProps) {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  return (
    <>
      <IconButton aria-label="add condition" onClick={handleClick} >
        <AddCircleIcon />
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
        {props.possibleTypes.map(type =>
          <MenuItem key={type} onClick={() => props.onNewCondition(type)}>
            {iconForType(type)}
            <Typography variant="body1" component="p" sx={{ m: 1, marginLeft: 0 }}>
              {descriptionForType(type)}
            </Typography>
          </MenuItem>
        )}
      </Menu>
    </>)
}
