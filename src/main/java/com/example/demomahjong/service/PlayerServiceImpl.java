package com.example.demomahjong.service;

import com.example.demomahjong.entity.Card;
import com.example.demomahjong.entity.Player;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Override
    public void addCard(Card card, Player player) {
        player.getHandCardList().add(card);
    }

    @Override
    public void addCardList(List<Card> cardList, Player player) {
        player.getHandCardList().addAll(cardList);
    }

    @Override
    public Card getCard(Card card, Player player) {
        List<Card> handCardList = player.getHandCardList();
        for (Card c : handCardList) {
            if ((c.getType().equals(card.getType())) && (c.getValue() == card.getValue())) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Card> getHandCardList(Player player) {
        return player.getHandCardList();
    }

    @Override
    public boolean hasCard(Card card, Player player) {
        List<Card> handCardList = player.getHandCardList();
        for (Card c : handCardList) {
            if ((c.getType().equals(card.getType())) && (c.getValue() == card.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasCardList(List<Card> cardList, Player player) {
        //todo
        return true;
    }

    @Override
    public void removeCard(Card card, Player player) {
        if (!hasCard(card, player)) {
            return;
        }
        player.getHandCardList().remove(card);
    }

    @Override
    public void removeCardList(List<Card> cardList, Player player) {
        if (!hasCardList(cardList, player)) {
            return;
        }
        player.getHandCardList().removeAll(cardList);
    }
}
