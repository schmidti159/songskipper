import { Card, CardContent, Switch, Typography } from "@mui/material";
import { api } from "../../api/api";

export default function SkipperCard() {
  let { data: active, isLoading } = api.useIsSkipperActiveQuery()
  if (isLoading) {
    active = false
  }

  const [setSkipper] = api.useSetSkipperStateMutation()

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" component="p">Skipper Enabled</Typography>
        <Switch checked={active} disabled={isLoading} onClick={() => setSkipper(!active)} />
      </CardContent>
    </Card>
  )
}