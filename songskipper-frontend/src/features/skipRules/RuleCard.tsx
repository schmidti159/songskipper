import { Card, CardContent, Grid, Typography } from "@mui/material";
import { Rule } from "../../common/types";

interface RuleCardProps {
  rule: Rule
}

export default function RuleCard(props: RuleCardProps) {
  const rule = props.rule
  return (
    <Grid item xs={12}>
      <Card sx={{ display: 'flex' }}>
        <CardContent sx={{ width: '100%' }}>
          <Typography variant="caption" component="p">{rule.id}</Typography>
          <Typography variant="body1" component="div">
            <p>title: {rule.titleExpression}</p>
            <p>artist: {rule.artistExpression}</p>
            <p>album: {rule.albumExpression}</p>
          </Typography>
        </CardContent>
      </Card>
    </Grid>
  )
}