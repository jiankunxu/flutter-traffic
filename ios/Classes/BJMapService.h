//
//  BJMapService.h
//  driver
//
//  Created by River on 2020/3/20.
//  Copyright © 2020 zkwl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MapManager/MapManager.h>
NS_ASSUME_NONNULL_BEGIN

@interface BJMapService : MapService
+ (instancetype)sharedManager;
@end

NS_ASSUME_NONNULL_END
