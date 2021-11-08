import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';

interface ConfirmDeletionDialogProps {
  open: boolean
  onConfirm: (() => void)
  onCancel: (() => void)
}

export default function ConfirmDeletionDialog(props: ConfirmDeletionDialogProps) {
  return (
    <Dialog open={props.open} onClose={props.onCancel}>
      <DialogTitle>Confirm Deletion</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Are you sure you want to delete this rule?
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={props.onCancel}>Cancel</Button>
        <Button startIcon={<DeleteIcon />} variant="contained" onClick={props.onConfirm}>Delete</Button>
      </DialogActions>
    </Dialog>
  )
}

