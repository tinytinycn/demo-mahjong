package com.example.demomahjong.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 玩家
 */
public class Player {
    private int state; //状态, 取值: 1摸牌|1出牌|2吃牌|3碰牌|4杠牌|5胡牌
    private String name;//名字
    @JsonIgnore
    private List<Card> handCardList;//手牌
    private String type;//类型, 取值: east|west|south|north
    private boolean banker;//是否为庄家
    @JsonIgnore
    private List<Card> playCardList;//打出的牌
    @JsonIgnore
    private List<Card> drawCardList;//摸到的牌


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getHandCardList() {
        return handCardList;
    }

    public void setHandCardList(List<Card> handCardList) {
        this.handCardList = handCardList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isBanker() {
        return banker;
    }

    public void setBanker(boolean banker) {
        this.banker = banker;
    }

    public List<Card> getPlayCardList() {
        return playCardList;
    }

    public void setPlayCardList(List<Card> playCardList) {
        this.playCardList = playCardList;
    }

    public List<Card> getDrawCardList() {
        return drawCardList;
    }

    public void setDrawCardList(List<Card> drawCardList) {
        this.drawCardList = drawCardList;
    }

    @Override
    public String toString() {
        return name + "|" + type;
    }
}
