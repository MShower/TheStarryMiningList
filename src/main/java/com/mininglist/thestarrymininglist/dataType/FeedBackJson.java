package com.mininglist.thestarrymininglist.dataType;

/**
 * @param uuid    唯一标识码
 * @param time    启动的时间
 * @param modType 模组的类型
 */
public record FeedBackJson(String uuid, long time, String modType) {
}
