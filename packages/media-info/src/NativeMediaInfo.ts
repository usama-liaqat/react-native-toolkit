import { TurboModuleRegistry, type TurboModule } from 'react-native';
import type { MediaMetadata, MetadataWriteResult } from './types';

export interface Spec extends TurboModule {
  multiply(a: number, b: number): number;
  extract(uri: string): Promise<MediaMetadata | undefined>;
  inject(
    uri: string,
    tags: MediaMetadata
  ): Promise<MetadataWriteResult | undefined>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('MediaInfo');
