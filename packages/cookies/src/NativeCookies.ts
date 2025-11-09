import { TurboModuleRegistry, type TurboModule } from 'react-native';

export interface Cookie {
  name: string;
  value: string;
  path?: string;
  domain?: string;
  version?: string;
  expires?: string;
  secure?: boolean;
  httpOnly?: boolean;
}

export interface Cookies {
  [key: string]: Cookie;
}

export interface Spec extends TurboModule {
  setValue(url: string, cookie: Cookie, useWebKit?: boolean): Promise<boolean>;
  setFromResponse(url: string, cookie: string): Promise<boolean>;

  getValue(url: string, useWebKit?: boolean): Promise<Cookies>;
  getFromResponse(url: string): Promise<Cookies>;

  clearAll(useWebKit?: boolean): Promise<boolean>;

  // Android only
  flush(): Promise<void>;
  removeSessionCookies(): Promise<boolean>;

  // iOS only
  getAll(useWebKit?: boolean): Promise<Cookies>;
  clearByName(url: string, name: string, useWebKit?: boolean): Promise<boolean>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Cookies');
