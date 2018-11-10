package com.example.demomahjong.service;

import com.example.demomahjong.entity.Card;
import com.example.demomahjong.entity.Player;

import java.util.List;

public interface PlayerService {
    void addCard(Card card, Player player);

    void addCardList(List<Card> cardList, Player player);

    Card getCard(Card card, Player player);

    List<Card> getHandCardList(Player player);

    boolean hasCard(Card card, Player player);

    boolean hasCardList(List<Card> cardList, Player player);

    void removeCard(Card card, Player player);

    void removeCardList(List<Card> cardList, Player player);
}
