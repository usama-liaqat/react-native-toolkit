import type { Cookie, Cookies } from './NativeCookies';
import NativeCookies from './NativeCookies';
export type { Cookie, Cookies };
export class CookieManager {
  static set(
    url: string,
    cookie: Cookie,
    useWebKit?: boolean
  ): Promise<boolean> {
    return NativeCookies.setValue(url, cookie, useWebKit);
  }
  static setFromResponse(url: string, cookie: string): Promise<boolean> {
    return NativeCookies.setFromResponse(url, cookie);
  }

  static get(url: string, useWebKit?: boolean): Promise<Cookies> {
    return NativeCookies.getValue(url, useWebKit);
  }
  static getFromResponse(url: string): Promise<Cookies> {
    return NativeCookies.getFromResponse(url);
  }

  static clearAll(useWebKit?: boolean): Promise<boolean> {
    return NativeCookies.clearAll(useWebKit);
  }

  // Android only
  static flush(): Promise<void> {
    return NativeCookies.flush();
  }
  static removeSessionCookies(): Promise<boolean> {
    return NativeCookies.removeSessionCookies();
  }

  // iOS only
  static getAll(useWebKit?: boolean): Promise<Cookies> {
    return NativeCookies.getAll(useWebKit);
  }
  static clearByName(
    url: string,
    name: string,
    useWebKit?: boolean
  ): Promise<boolean> {
    return NativeCookies.clearByName(url, name, useWebKit);
  }
}
