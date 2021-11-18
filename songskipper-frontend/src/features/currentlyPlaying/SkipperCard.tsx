import { Card, CardContent, Switch, Typography } from "@mui/material";
import { skipperApi } from "../../api/skipperApi";

export default function SkipperCard() {
  let { data: active, isLoading } = skipperApi.useIsSkipperActiveQuery();
  if (isLoading) {
    active = false;
  }

  const [setSkipper] = skipperApi.useSetSkipperStateMutation();

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" component="p">Skipper Enabled</Typography>
        <Switch value={active} checked={active} disabled={isLoading} onClick={() => setSkipper(!active)} />
      </CardContent>
    </Card>
  );
}