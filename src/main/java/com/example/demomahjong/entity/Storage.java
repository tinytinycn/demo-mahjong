package com.example.demomahjong.entity;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 内存中存储玩家信息
 */
@Component
public class Storage {
    private List<Player> playerList;
}
