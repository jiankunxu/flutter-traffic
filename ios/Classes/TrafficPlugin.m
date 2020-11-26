#import "TrafficPlugin.h"
#import "BJMapService.h"
@implementation TrafficPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"traffic"
            binaryMessenger:[registrar messenger]];
  TrafficPlugin* instance = [[TrafficPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if([@"initSDK" isEqualToString:call.method]){
      
      [[BJMapService sharedManager] openServiceWithAppId:call.arguments[@"appId"] appSecurity:call.arguments[@"appSecurity"] enterpriseSenderCode:call.arguments[@"enterpriseSenderCode"] environment:call.arguments[@"type"] listener:^(id  _Nonnull model, NSError * _Nonnull error) {
          result(@"success");
          return;
      }];
      
  }else if([@"startSDK" isEqualToString:call.method]){
      NSDictionary *shippingChilder = @{
          @"shippingNoteNumber":call.arguments[@"billNum"],
          @"serialNumber":@"0000",
          @"startCountrySubdivisionCode":call.arguments[@"loadingAreaCode"],
          @"endCountrySubdivisionCode":call.arguments[@"unloadingAreaCode"],
      };
      NSArray *ShippingNoteInfo=@[shippingChilder];
      [[BJMapService sharedManager] startLocationWithShippingNoteInfos:ShippingNoteInfo listener:^(id  _Nonnull model, NSError * _Nonnull error) {
          result(@"success");
          return;
      }];
      
  }else if([@"stopSDK" isEqualToString:call.method]){
      NSDictionary *shippingChilder = @{
          @"shippingNoteNumber":call.arguments[@"billNum"],
          @"serialNumber":@"0000",
          @"startCountrySubdivisionCode":call.arguments[@"loadingAreaCode"],
          @"endCountrySubdivisionCode":call.arguments[@"unloadingAreaCode"],
      };
      NSArray *ShippingNoteInfo2=@[shippingChilder];
      [[BJMapService sharedManager] stopLocationWithShippingNoteInfos:ShippingNoteInfo2 listener:^(id  _Nonnull model, NSError * _Nonnull error) {
          result(@"success");
          return;
      }];
      
  }else {
    result(FlutterMethodNotImplemented);
  }
}

@end
