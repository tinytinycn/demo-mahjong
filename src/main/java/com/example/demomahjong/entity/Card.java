package com.example.demomahjong.entity;

import java.util.Objects;

/**
 * 牌
 */
public class Card {
    private int id;//牌的唯一id
    private String type;//牌的类型: 萬|饼|条
    private int value;//牌的值: 1-9

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        //类型相同,值相同
        return id == card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        switch (type) {
            case "w":
                return value + "萬";
            case "b":
                return value + "饼";
            case "t":
                return value + "条";
            default:
                return value + "*";
        }
    }
}
