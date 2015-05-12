package com.ctcstudio.ebooktemplate.entities;

import java.io.Serializable;

/**
 * Created by HungHN on 5/10/2015.
 */
public class SettingBook implements Serializable {

    private int width;

    private int height;

    private int textSize = 20;

    private int textColor = 0xffA7573E;

    private int pageColor = 0xffFDF8A6;

    private int topPadding = 10, leftPadding = 15;

    private int bottomPadding = 10, rightPadding = 15;

    private float spacingAdd = 10f, spacingMult = 1.0f;

    private String font = "Roboto-Regular.ttf";

    public SettingBook() {

    }

    public SettingBook(int width, int height, int textSize, int textColor, int pageColor, int topPadding, int bottomPadding, int leftPadding, int rightPadding, float spacingAdd, float spacingMult, String font) {

        this.width = width;
        this.height = height;
        this.textSize = textSize;
        this.textColor = textColor;
        this.pageColor = pageColor;
        this.topPadding = topPadding;
        this.bottomPadding = bottomPadding;
        this.leftPadding = leftPadding;
        this.rightPadding = rightPadding;
        this.spacingMult = spacingMult;
        this.spacingAdd = spacingAdd;
        this.font = font;
    }


    public int getLeftPadding() {
        return leftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }

    public int getTopPadding() {
        return topPadding;
    }

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public int getPageColor() {
        return pageColor;
    }

    public void setPageColor(int pageColor) {
        this.pageColor = pageColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getBottomPadding() {
        return bottomPadding;
    }

    public void setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    public int getRightPadding() {
        return rightPadding;
    }

    public void setRightPadding(int rightPadding) {
        this.rightPadding = rightPadding;
    }

    public float getSpacingAdd() {
        return spacingAdd;
    }

    public void setSpacingAdd(float spacingAdd) {
        this.spacingAdd = spacingAdd;
    }

    public float getSpacingMult() {
        return spacingMult;
    }

    public void setSpacingMult(float spacingMult) {
        this.spacingMult = spacingMult;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String toString() {
        String log = width + ":" + height +
                ":" + textSize + ":" + textColor +
                ":" + bottomPadding + ":" + leftPadding +
                ":" + rightPadding + ":" + spacingMult +
                ":" + spacingAdd + ":" + font;
        return log;
    }
}
