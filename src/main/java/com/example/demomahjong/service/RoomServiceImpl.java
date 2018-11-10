package com.example.demomahjong.service;

import com.example.demomahjong.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomServiceImpl implements RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
    private static final String TOPIC_PUBLIC_DESTINATION = "/topic/public";

    private final Room room;
    private final TableService tableService;
    private final PlayerService playerService;
    private final SimpMessageSendingOperations messageSendingOperations;

    @Autowired
    public RoomServiceImpl(Room room,
                           TableService tableService,
                           PlayerService playerService,
                           SimpMessageSendingOperations messageSendingOperations) {
        this.room = room;
        this.tableService = tableService;
        this.playerService = playerService;
        this.messageSendingOperations = messageSendingOperations;
        //设置房间状态: 未开始
        room.setState(0);
        //设置房间牌桌
        room.setTable(new Table());
        //设置房间玩家列表
        room.setPlayerList(new ArrayList<>());
    }


    /**
     * 等待游戏
     * 1. 等待玩家进入房间, 接受0类型消息
     * 2. 添加玩家到房间, 广播一条none类型消息
     * 3. 玩家到达4人, 房间状态设置为1准备中, 广播一条ready类型消息, 通知客户端准备游戏
     */
    @Override
    public void waitGame(TinyMessage message) {
        //校验房间状态
        if (room.getState() != 0) {
            logger.info("处理客户端消息失败, 当前room状态: " + room.getState());
            return;
        }
        //为房间添加玩家
        if (getPlayerNum(room) != 4) {
            Player player = new Player();
            player.setState(0);
            player.setName(message.getUsername());
            addPlayer(player, room);
            //广播
            TinyMessage msg = new TinyMessage();
            msg.setUsername("admin");
            msg.setType(0);
            msg.setAction("none");
            msg.setContent("在线玩家: " + message.getUsername());
            broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
        }
        //房间进入准备中
        if (getPlayerNum(room) == 4) {
            room.setState(1);
            //广播
            TinyMessage msg2 = new TinyMessage();
            msg2.setUsername("admin");
            msg2.setType(1);
            msg2.setAction("ready");
            msg2.setContent("玩家准备!");
            broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg2);
        }
    }

    /**
     * 准备游戏
     * 1. 等待玩家准备完毕, 接受1类型消息
     * 2. 统计玩家准备情况 todo 跳过
     * 3. 初始化牌桌 (必须先与玩家列表初始化)
     * 4. 初始化玩家列表
     * 5. 玩家准备完毕, 房间状态设置为2游戏中 (广播一条start类型消息, 通知客户端开始游戏)
     */
    @Override
    public void startGame(TinyMessage message) {
        if (room.getState() != 1) {
            logger.info("处理客户端消息失败, 当前room状态: " + room.getState());
            return;
        }
        initTable();
        initPlayerList();
        room.setState(2);
        //广播
        TinyMessage msg = new TinyMessage();
        msg.setUsername("admin");
        msg.setType(2);
        msg.setAction("start");
        msg.setContent("开始游戏!");
        broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
    }

    /**
     * 游戏中
     * 1. 等待玩家操作, 接受2类型消息(包括动作:mopai|chupai|pengpai|chipai|gangpai|hupai)
     * 2. 分别处理不同类型的玩家操作
     * 2.1 摸牌, 在余牌中为当前玩家摸一张牌, 广播一条mopai类型消息, 私发一条mopai类型消息
     */
    @Override
    public void runGame(TinyMessage message) {
        //校验房间信息
        if (room.getState() != 2) {
            logger.info("处理客户端消息失败, 当前room状态: " + room.getState());
            return;
        }
        //获取玩家信息
        Player player = getPlayerByName(message.getUsername(), room);
        //校验玩家信息
        if (!verifyPlayer(player)) {
            return;
        }
        //准备消息实体
        TinyMessage msg = new TinyMessage();
        msg.setUsername(message.getUsername());
        msg.setType(2);
        //处理玩家信息
        switch (message.getAction()) {
            case "mopai":
                //广播
                msg.setAction("notice");
                msg.setContent(message.getUsername() + "摸牌!");
                msg.setTable(room.getTable());
                broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
                //为当前玩家摸牌(手牌中添加, 摸到牌中设置)
                List<Card> drawCardList = new ArrayList<>();
                Card card = tableService.getOneCard(room.getTable());
                playerService.addCard(card, player);
                drawCardList.add(card);
                player.setDrawCardList(drawCardList);
                room.getTable().setDrawCardList(drawCardList);
                //私人频道
                msg.setAction("mopai");
                msg.setContent("摸到一张牌: " + card);
                msg.setTable(room.getTable());
                broadcastMessage2User(message.getUsername(), "/fapai", msg);
                break;
            case "chupai":
                //为当前玩家出牌(玩家手牌中移除,玩家打出牌中设置,牌桌打出牌中设置)
                List<Card> playCardList = message.getTable().getPlayCardList();
                playerService.removeCardList(playCardList, player);
                player.setPlayCardList(playCardList);
                room.getTable().setPlayCardList(playCardList);
                //广播
                msg.setAction("chupai");
                msg.setContent(message.getUsername() + "出牌: " + playCardList.toString());
                msg.setTable(room.getTable());
                broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
                //光标切至下手玩家
                Player nextPlayer = tableService.point2Next(room.getTable(), room.getPlayerList());
                //广播
                msg.setAction("point2Player");
                msg.setContent("轮到玩家: " + nextPlayer.getName());
                msg.setTable(room.getTable());
                broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
                break;
            case "pengpai":
                //当前玩家碰牌
                List<Card> playCardList2 = message.getTable().getPlayCardList();
                playerService.removeCardList(playCardList2, player);
                player.setPlayCardList(playCardList2);
                room.getTable().setPlayCardList(playCardList2);
                //广播
                msg.setAction("pengpai");
                msg.setContent(message.getUsername() + "碰: " + playCardList2.toString());
                msg.setTable(room.getTable());
                broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
                //光标切至下手玩家
                Player nextPlayer2 = tableService.point2Next(room.getTable(), room.getPlayerList());
                //广播
                msg.setAction("point2Player");
                msg.setContent("轮到玩家: " + nextPlayer2.getName());
                msg.setTable(room.getTable());
                broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
                break;
            case "chipai":
                msg.setAction("chipai");
                msg.setContent(message.getUsername() + "吃: " + message.getContent());
                broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
                break;
            case "gangpai":
                msg.setAction("gangpai");
                msg.setContent(message.getUsername() + "杠: " + message.getContent());
                broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
                break;
            case "hupai":
                msg.setAction("hupai");
                msg.setContent(message.getUsername() + "胡: " + message.getContent());
                broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
                break;
            case "wait":
                //当前玩家喊牌, 不喊牌视为放弃(客户端等候10s后, 隐藏喊牌按钮)
                String content = message.getContent();
                Player nowPointPlayer = tableService.getPointPlayerByIndex(0, room.getPlayerList());
                switch (content) {
                    case "胡牌":
                        if (nowPointPlayer.getState() != 5) {
                            tableService.point2This(player, 5, room.getTable(), room.getPlayerList());
                        } else {
                            tableService.addPointPlayer(player, 5, room.getTable());
                        }
                        break;
                    case "杠牌":
                        if (nowPointPlayer.getState() != 5) {
                            tableService.point2This(player, 4, room.getTable(), room.getPlayerList());
                        }
                        break;
                    case "碰牌":
                        if (nowPointPlayer.getState() != 5) {
                            tableService.point2This(player, 3, room.getTable(), room.getPlayerList());
                        }
                        break;
                    case "吃牌":
                        if (nowPointPlayer.getState() < 3) {
                            tableService.point2This(player, 2, room.getTable(), room.getPlayerList());
                        }
                        break;
                    default:
                }
                //广播
                msg.setAction("wait");
                msg.setContent(message.getUsername() + "说: 等一哈!!! " + message.getContent());
                broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
                //光标切至优先级高的玩家
                List<String> pointPlayerList6 = new ArrayList<>();
                for (Player p : room.getTable().getPointPlayerList()) {
                    pointPlayerList6.add(p.getName());
                }
                //广播
                msg.setAction("point2Player");
                msg.setContent("轮到玩家: " + pointPlayerList6.toString());
                broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
                break;
            default:
        }

    }

    @Override
    public void resumeGame(TinyMessage message) {

    }

    @Override
    public void endGame(TinyMessage message) {

    }

    @Override
    public void broadcastMessage(String destination, TinyMessage tinyMessage) {
        messageSendingOperations.convertAndSend(destination, tinyMessage);
    }

    @Override
    public void broadcastMessage2User(String username, String destination, TinyMessage tinyMessage) {
        messageSendingOperations.convertAndSendToUser(username, destination, tinyMessage);
    }

    /**
     * 校验发送客户端消息的玩家身份
     */
    private boolean verifyPlayer(Player player) {
        if (player == null) {
            logger.info("处理客户端消息失败, 不存在当前玩家!");
            return false;
        } else if (player.getState() == 0) {
            //是否为出牌玩家
            logger.info("处理客户端消息失败, 当前player状态: " + player.getState());
            return false;
        }
        return true;
    }

    /**
     * 初始化牌桌
     * 1. 设置出牌数为0
     * 2. 设置牌组(洗牌)
     * 3. 设置出牌玩家列表(光标)
     */
    private void initTable() {
        room.getTable().setStep(0);
        initTableCardList();
        initTablePointPlayerList();
        initTablePlayCardList();
        initTableDrawCardList();
    }

    /**
     * 初始化牌桌牌组
     */
    private void initTableCardList() {
        List<Card> cardList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        typeList.add("w");
        typeList.add("b");
        typeList.add("t");
        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) {
                for (String type : typeList) {
                    Card card = new Card();
                    card.setId(UUID.randomUUID().hashCode());
                    card.setType(type);
                    card.setValue(i);
                    cardList.add(card);
                }
            }
        }
        //打乱牌组
        Collections.shuffle(cardList);
        //为牌桌设置牌组
        room.getTable().setCardList(cardList);
    }

    /**
     * 初始化牌组出牌玩家列表
     */
    private void initTablePointPlayerList() {
        room.getTable().setPointPlayerList(new ArrayList<>());
    }

    /**
     * 初始化当前打出的牌组
     */
    private void initTablePlayCardList() {
        room.getTable().setPlayCardList(new ArrayList<>());
    }

    /**
     * 初始化当前摸到的牌组
     */
    private void initTableDrawCardList() {
        room.getTable().setDrawCardList(new ArrayList<>());
    }

    /**
     * 初始化玩家列表
     * 1. 初始化玩家类型和状态(入座, 广播一条sitdown类型消息, 通知客户端安排座位)
     * 2. 初始化玩家手牌(发牌, 广播一条shoupai类型消息, 通知客户端整理手牌)
     * 3. 初始化玩家出牌组
     * 4. 初始化玩家摸牌组
     */
    private void initPlayerList() {
        initPlayerTypeAndState();//广播sitdown
        initPlayerHandCardList();//广播shoupai
        initPlayerPlayCardList();
        initPlayerDrawCardList();
    }

    /**
     * 初始化玩家类型type和状态state(即选座和设置第一个出牌玩家)
     */
    private void initPlayerTypeAndState() {
        LinkedList<String> typeList = new LinkedList<>();
        typeList.add("东");
        typeList.add("西");
        typeList.add("南");
        typeList.add("北");
        //打乱玩家列表
        Collections.shuffle(room.getPlayerList());
        //为房间安排玩家座位, 为牌桌指定出牌玩家
        for (Player player : room.getPlayerList()) {
            String type = typeList.pop();
            player.setType(type);
            if ("东".equals(type)) {
                //设置"东风"玩家状态为1
                tableService.addPointPlayer(player, 1, room.getTable());
            }
        }
        //广播
        TinyMessage msg = new TinyMessage();
        msg.setUsername("admin");
        msg.setType(1);
        msg.setAction("sitdown");
        msg.setContent("玩家入座! " + room.getPlayerList().toString());
        broadcastMessage(TOPIC_PUBLIC_DESTINATION, msg);
    }

    /**
     * 初始化玩家手牌
     */
    private void initPlayerHandCardList() {
        for (Player player : room.getPlayerList()) {
            List<Card> handCardList = new ArrayList<>();
            //13张手牌
            for (int i = 0; i < 13; i++) {
                handCardList.add(tableService.getOneCard(room.getTable()));
            }
            //为玩家派发13张手牌
            player.setHandCardList(handCardList);
            room.getTable().setDrawCardList(handCardList);
            //私人频道
            TinyMessage msg = new TinyMessage();
            msg.setUsername("admin");
            msg.setType(1);
            msg.setAction("shoupai");
            msg.setContent("手牌: " + handCardList.toString());
            msg.setTable(room.getTable());
//            List<Player> privatePlayerList = new ArrayList<>();
//            privatePlayerList.add(player);
//            msg.setPlayerList(privatePlayerList);
            broadcastMessage2User(player.getName(), "/fapai", msg);
        }
    }

    /**
     * 初始化玩家出牌组
     */
    private void initPlayerPlayCardList() {
        for (Player player : room.getPlayerList()) {
            player.setPlayCardList(new ArrayList<>());
        }
    }

    /**
     * 初始化玩家摸牌组
     */
    private void initPlayerDrawCardList() {
        for (Player player : room.getPlayerList()) {
            player.setDrawCardList(new ArrayList<>());
        }
    }

    @Override
    public void addPlayer(Player player, Room room) {
        List<Player> playerList = room.getPlayerList();
        playerList.add(player);
    }

    @Override
    public void removePlayer(Player player, Room room) {
        List<Player> playerList = room.getPlayerList();
        playerList.remove(player);
    }

    @Override
    public Player getPlayerByName(String name, Room room) {
        List<Player> playerList = room.getPlayerList();
        for (Player p : playerList) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public Player getPlayerByIndex(int index, Room room) {
        List<Player> playerList = room.getPlayerList();
        return playerList.get(index);
    }

    @Override
    public int getPlayerNum(Room room) {
        List<Player> playerList = room.getPlayerList();
        return playerList.size();
    }
}
