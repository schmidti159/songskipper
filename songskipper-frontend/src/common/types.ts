export interface Track {
  title: string,
  url?: string,
  artists: {
    name: string,
    url: string
  }[]
  album: {
    title: string,
    url: string,
    albumArtUrl: string
  },
  durationMs: number
}
export interface PlayLogTrack {
  track: Track,
  playedOn: string,
  matchingRuleIds: string[]
}

export interface CurrentlyPlayingState {
  track?: Track,
  progressMs?: number,
  isPaused: boolean
}

export interface Rule {
  id: string,
  title: string,
  titleExpression?: string,
  artistExpression?: string,
  albumExpression?: string
}

export type ConditionType = 'track' | 'artist' | 'album'

export interface Condition {
  type: ConditionType
  initialExpression?: string
  expression?: string
  inChangeMode: boolean
}