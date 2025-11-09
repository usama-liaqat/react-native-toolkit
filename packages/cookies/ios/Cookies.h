#import <CookiesSpec/CookiesSpec.h>
#import <WebKit/WebKit.h>

@interface Cookies : NSObject <NativeCookiesSpec>
@property (nonatomic, strong) NSDateFormatter *formatter;
@end
