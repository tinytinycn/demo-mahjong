package com.example.demomahjong.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;


import java.util.List;

/**
 * 牌桌
 */
public class Table {
    @JsonIgnore
    private List<Card> cardList;//一副牌组
    private int step;//出牌数
    private List<Player> pointPlayerList;//出牌玩家列表
    private List<Card> playCardList;//当前打出的牌
    private List<Card> drawCardList;//当前摸到的牌

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

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public List<Player> getPointPlayerList() {
        return pointPlayerList;
    }

    public void setPointPlayerList(List<Player> pointPlayerList) {
        this.pointPlayerList = pointPlayerList;
    }
}
