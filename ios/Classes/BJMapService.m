//
//  BJMapService.m
//  driver
//
//  Created by River on 2020/3/20.
//  Copyright © 2020 zkwl. All rights reserved.
//

#import "BJMapService.h"

@implementation BJMapService
static BJMapService *instance = nil;

+ (instancetype)sharedManager {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        instance = [[BJMapService alloc]init];
        
    });
    return instance;
}
- (instancetype)init{
    self = [super init];
    if (self) {
    }
    return self;
}
@end
