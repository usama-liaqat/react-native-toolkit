import { TurboModuleRegistry, type TurboModule } from 'react-native';
import type {
  MediaMetadata,
  MetadataWriteResult,
  UndefinableField,
} from './types';

export interface Spec extends TurboModule {
  multiply(a: number, b: number): number;
  extract(uri: string): Promise<UndefinableField<MediaMetadata>>;
  inject(
    uri: string,
    tags: MediaMetadata
  ): Promise<UndefinableField<MetadataWriteResult>>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MediaInfo');
