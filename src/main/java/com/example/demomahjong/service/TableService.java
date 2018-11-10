package com.example.demomahjong.service;

import com.example.demomahjong.entity.Card;
import com.example.demomahjong.entity.Player;
import com.example.demomahjong.entity.Room;
import com.example.demomahjong.entity.Table;

import java.util.List;

public interface TableService {
    Card getOneCard(Table table);

    void addPointPlayer(Player player, int state, Table table);

    void removeAllPointPlayer(Player player, Table table);

    Player getPointPlayerByIndex(int index, List<Player> playerList);

    int getPointPlayerNum(Table table);

    Player point2Next(Table table, List<Player> playerList);

    Player point2This(Player player, int state, Table table, List<Player> playerList);
}
