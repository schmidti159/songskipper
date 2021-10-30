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
  titleExpression?: string,
  artistExpression?: string,
  albumExpression?: string
  
}