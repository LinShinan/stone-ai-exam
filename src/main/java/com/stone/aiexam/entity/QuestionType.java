package com.stone.aiexam.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 题目类型的枚举
 */
public enum QuestionType {

    CHOICE, // 选择题
    JUDGE,  // 判断题
    TEXT; // 简答题


    @JsonCreator
    public static QuestionType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (QuestionType type : QuestionType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }

        return null;
    }
} 