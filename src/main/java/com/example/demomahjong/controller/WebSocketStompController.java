package com.example.demomahjong.controller;

import com.example.demomahjong.entity.TinyMessage;
import com.example.demomahjong.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class WebSocketStompController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketStompController.class);

    private final RoomService roomService;

    @Autowired
    public WebSocketStompController(RoomService roomService) {
        this.roomService = roomService;
    }

    @MessageMapping("/post")
    public void handlePostMessage(TinyMessage message) throws InterruptedException {
        logger.info("[" + message.getUsername() + "] 发来消息: " + message.getContent());
        switch (message.getType()) {
            case 0:
                //进入房间, 发送消息类型: 0 ,处于等待状态
                roomService.waitGame(message);
                break;
            case 1:
                //已入房间, 点击准备按钮, 发送消息类型: 1, 处于准备状态 (若4位玩家全部准备, 则开始游戏)
                roomService.startGame(message);
                break;
            case 2:
                //游戏中
                roomService.runGame(message);
                break;
            case 3:
                //继续游戏
                roomService.resumeGame(message);
                break;
            case 4:
                //结束游戏
                roomService.endGame(message);
                break;
            default:
        }

    }
}
