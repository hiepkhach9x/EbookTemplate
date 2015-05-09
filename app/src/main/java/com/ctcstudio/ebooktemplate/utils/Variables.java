package com.ctcstudio.ebooktemplate.utils;

import java.util.HashMap;

import nl.siegmann.epublib.domain.Book;

/**
 * Created by HungHN on 5/9/2015.
 */
public class Variables {

    public static final String TAG = "HungHN";

    public final static String DATA_BOOK = "DATA_BOOK";

    public final static String DATA_CHAP = "DATA_CHAP";

    public final static HashMap<String, Book> HM_DATA_BOOK = new HashMap<String, Book>();

    public final static String strMarket = "market://search?q=pub:CTC+Studio";
}
