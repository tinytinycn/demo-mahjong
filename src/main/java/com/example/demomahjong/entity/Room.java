package com.example.demomahjong.entity;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 房间
 */
@Component
public class Room {
    private int state;//状态: 0未开始 1准备中 2进行中 3暂停中 4结束
    private Table table;//牌桌
    private List<Player> playerList;//玩家列表

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }
}
