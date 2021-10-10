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