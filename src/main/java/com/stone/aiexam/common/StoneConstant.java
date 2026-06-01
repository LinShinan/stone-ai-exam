package com.stone.aiexam.common;

public class StoneConstant {

    public static final String HOT_QUESTION_KEY = "question:hot:score";

    /**
     * 试卷状态：草稿
     */
    public static final String PAPER_STATUS_DRAFT="DRAFT";

    /**
     * 试卷状态：发布
     */
    public static final String PAPER_STATUS_PUBLISHED="PUBLISHED";

    /**
     * 试卷状态：停止
     */
    public static final String PAPER_STATUS_STOPPED="STOPPED";

    /**
     * 题目类型：选择题
     */
    public static final String QUESTION_TYPE_CHOICE="CHOICE";

    /**
     * 题目类型：判断题
     */
    public static final String QUESTION_TYPE_JUDGE="JUDGE";

    /**
     * 题目类型：简答题
     */
    public static final String QUESTION_TYPE_TEXT="TEXT";

    /**
     * 考试状态：进行中
     */
    public static final String EXAM_STATUS_PROGRESS="进行中";

    /**
     * 考试状态：已完成
     */
    public static final String EXAM_STATUS_FINISH="已完成";
    /**
     * 考试状态：已批阅
     */
    public static final String EXAM_STATUS_GRADED="已批阅";

    /**
     * 作答状态：错误
     */
    public static final int ANSWER_STATUS_FALSE=0;
    /**
     * 作答状态：完全正确
     */
    public static final int ANSWER_STATUS_TRUE=1;
    /**
     * 作答状态：部分正确
     */
    public static final int ANSWER_STATUS_PARTLY_CORRECT=2;
}
