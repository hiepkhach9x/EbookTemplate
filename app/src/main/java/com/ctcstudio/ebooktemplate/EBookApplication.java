package com.ctcstudio.ebooktemplate;

import android.app.Application;

import com.ctcstudio.ebooktemplate.entities.SettingBook;

import nl.siegmann.epublib.domain.Book;

/**
 * Created by HungHN on 5/12/2015.
 */
public class EBookApplication extends Application {

    private static EBookApplication instance;

    private SettingBook settingBook;

    private Book eBook;

    private int bookMark;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static EBookApplication get() {
        return instance;
    }

    public Book getBook() {
        return eBook;
    }

    public void setBook(Book eBook) {
        this.eBook = eBook;
    }

    public SettingBook getSettingBook() {
        return settingBook;
    }

    public void setSettingBook(SettingBook settingBook) {
        this.settingBook = settingBook;
    }

    public int getBookMark() {
        return bookMark;
    }

    public void setBookMark(int bookMark) {
        this.bookMark = bookMark;
    }
}
