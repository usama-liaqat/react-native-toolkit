import MediaInfoModule from './NativeMediaInfo';
import type {
  MediaMetadata,
  MetadataWriteResult,
  UndefinableField,
} from './types';

export function multiply(a: number, b: number): number {
  return MediaInfoModule.multiply(a, b);
}

export class MediaInfo {
  extract(uri: string): Promise<UndefinableField<MediaMetadata>> {
    return MediaInfoModule.extract(uri);
  }

  inject(
    uri: string,
    tags: MediaMetadata
  ): Promise<UndefinableField<MetadataWriteResult>> {
    return MediaInfoModule.inject(uri, tags);
  }
}
