package com.example.demomahjong.service;

import com.example.demomahjong.entity.Player;
import com.example.demomahjong.entity.Room;
import com.example.demomahjong.entity.TinyMessage;

public interface RoomService {

    void waitGame(TinyMessage message);

    void startGame(TinyMessage message);

    void runGame(TinyMessage message);

    void resumeGame(TinyMessage message);

    void endGame(TinyMessage message);

    /**
     * 广播消息给所有人
     */
    void broadcastMessage(String destination, TinyMessage tinyMessage);

    /**
     * 发送消息给特定一个人
     */
    void broadcastMessage2User(String username, String destination, TinyMessage tinyMessage);

    void addPlayer(Player player, Room room);

    void removePlayer(Player player, Room room);

    Player getPlayerByName(String name, Room room);

    Player getPlayerByIndex(int index, Room room);

    int getPlayerNum(Room room);

}
