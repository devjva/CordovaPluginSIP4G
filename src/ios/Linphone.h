#import <AudioToolbox/AudioToolbox.h>
//#include "linphone/lpconfig.h"
#include "linphone/linphonecore.h"
#include "linphone/linphonecore_utils.h"

//  RCTCalendarModule.h
#import <React/RCTBridgeModule.h>
@interface SipModule : NSObject <RCTBridgeModule>
@property (nonatomic) LinphoneCore *lc;
@property (nonatomic) LinphoneCall *call;
@end

