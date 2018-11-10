package com.example.demomahjong.service;

import com.example.demomahjong.entity.Card;
import com.example.demomahjong.entity.Player;
import com.example.demomahjong.entity.Table;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableServiceImpl implements TableService {
    @Override
    public Card getOneCard(Table table) {
        List<Card> cardList = table.getCardList();
        if (cardList.size() == 0) {
            return null;
        }
        Card card = cardList.remove(cardList.size() - 1);
        return card;
    }

    @Override
    public void addPointPlayer(Player player, int state, Table table) {
        List<Player> pointPlayerList = table.getPointPlayerList();
        player.setState(state);
        pointPlayerList.add(player);
    }

    @Override
    public void removeAllPointPlayer(Player player, Table table) {
        List<Player> pointPlayerList = table.getPointPlayerList();
        player.setState(0);
        pointPlayerList.clear();
    }

    @Override
    public Player getPointPlayerByIndex(int index, List<Player> playerList) {
        return playerList.get(index);
    }


    @Override
    public int getPointPlayerNum(Table table) {
        List<Player> pointPlayerList = table.getPointPlayerList();
        return pointPlayerList.size();
    }

    @Override
    public Player point2Next(Table table, List<Player> playerList) {
        //出牌数加一
        table.setStep(table.getStep() + 1);
        //移除当前出牌玩家
        Player nowPointPlayer = getPointPlayerByIndex(0, playerList);
        removeAllPointPlayer(nowPointPlayer, table);
        //添加下一出牌玩家
        Player nextPointPlayer = getPointPlayerByIndex(table.getStep() % 4, playerList);
        addPointPlayer(nextPointPlayer, 1, table);
        return nextPointPlayer;
    }

    @Override
    public Player point2This(Player player, int state, Table table, List<Player> playerList) {
        //移除当前出牌玩家
        Player nowPointPlayer = getPointPlayerByIndex(0, playerList);
        removeAllPointPlayer(nowPointPlayer, table);
        //添加下一出牌玩家
        addPointPlayer(player, state, table);
        return player;
    }
}
