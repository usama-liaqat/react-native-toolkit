import type { Cookie, Cookies } from './NativeCookies';
import NativeCookies from './NativeCookies';
export type { Cookie, Cookies };
export class CookieManager {
  set(url: string, cookie: Cookie, useWebKit?: boolean): Promise<boolean> {
    return NativeCookies.setValue(url, cookie, useWebKit);
  }
  setFromResponse(url: string, cookie: string): Promise<boolean> {
    return NativeCookies.setFromResponse(url, cookie);
  }

  get(url: string, useWebKit?: boolean): Promise<Cookies> {
    return NativeCookies.getValue(url, useWebKit);
  }
  getFromResponse(url: string): Promise<Cookies> {
    return NativeCookies.getFromResponse(url);
  }

  clearAll(useWebKit?: boolean): Promise<boolean> {
    return NativeCookies.clearAll(useWebKit);
  }

  // Android only
  flush(): Promise<void> {
    return NativeCookies.flush();
  }
  removeSessionCookies(): Promise<boolean> {
    return NativeCookies.removeSessionCookies();
  }

  // iOS only
  getAll(useWebKit?: boolean): Promise<Cookies> {
    return NativeCookies.getAll(useWebKit);
  }
  clearByName(
    url: string,
    name: string,
    useWebKit?: boolean
  ): Promise<boolean> {
    return NativeCookies.clearByName(url, name, useWebKit);
  }
}
