package com.kidozh.npuhelper.markdownUtils;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import android.widget.TextView;

import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.handlers.BoldHandler;

public class htmlHelper {

    public static void htmlIntoTextView(@NonNull TextView textView, @NonNull String html, int width) {
        //registerClickEvent(textView);
        textView.setText(initHtml(textView, width).fromHtml(format(html).toString()));
    }

    private static HtmlSpanner initHtml(@NonNull TextView textView, int width) {
        HtmlSpanner mySpanner = new HtmlSpanner();
        mySpanner.setStripExtraWhiteSpace(true);

        //mySpanner.registerHandler("pre", new PreTagHandler(windowBackground, true, theme));
        //mySpanner.registerHandler("code", new PreTagHandler(windowBackground, false, theme));
        //mySpanner.registerHandler("img", new DrawableHandler(textView, width));
        //mySpanner.registerHandler("g-emoji", new EmojiHandler());
        //mySpanner.registerHandler("blockquote", new QuoteHandler(windowBackground));
        mySpanner.registerHandler("b", new BoldHandler());
        mySpanner.registerHandler("strong", new BoldHandler());
        //mySpanner.registerHandler("i", new ItalicHandler());
        mySpanner.registerHandler("em", new ItalicHandler());
        mySpanner.registerHandler("ul", new MarginHandler());
        mySpanner.registerHandler("ol", new MarginHandler());
        //mySpanner.registerHandler("li", new ListsHandler(checked, unchecked));
        mySpanner.registerHandler("u", new UnderlineHandler());
        mySpanner.registerHandler("strike", new StrikethroughHandler());
        mySpanner.registerHandler("ins", new UnderlineHandler());
        mySpanner.registerHandler("del", new StrikethroughHandler());
        mySpanner.registerHandler("sub", new SubScriptHandler());
        mySpanner.registerHandler("sup", new SuperScriptHandler());
        mySpanner.registerHandler("a", new LinkHandler());
        //mySpanner.registerHandler("hr", new HrHandler(windowBackground, width, false));
        //mySpanner.registerHandler("emoji", new EmojiHandler());
        mySpanner.registerHandler("mention", new LinkHandler());
        mySpanner.registerHandler("h1", new HeaderHandler(1.5F));
        mySpanner.registerHandler("h2", new HeaderHandler(1.4F));
        mySpanner.registerHandler("h3", new HeaderHandler(1.3F));
        mySpanner.registerHandler("h4", new HeaderHandler(1.2F));
        mySpanner.registerHandler("h5", new HeaderHandler(1.1F));
        mySpanner.registerHandler("h6", new HeaderHandler(1.0F));
        if (width > 0) {
            TableHandler tableHandler = new TableHandler();
            //tableHandler.setTextColor(ViewHelper.generateTextColor(windowBackground));

            tableHandler.setTableWidth(width);
            mySpanner.registerHandler("table", tableHandler);
        }
        return mySpanner;
    }

    @ColorInt public static int getWindowBackground() {
        return Color.parseColor("#0B162A");

//        if (theme == PrefGetter.AMLOD) {
//            return Color.parseColor("#0B162A");
//        } else if (theme == PrefGetter.BLUISH) {
//            return Color.parseColor("#111C2C");
//        } else if (theme == PrefGetter.DARK) {
//            return Color.parseColor("#22252A");
//        } else {
//            return Color.parseColor("#EEEEEE");
//        }
    }

    private static final String TOGGLE_START = "<span class=\"email-hidden-toggle\">";

    private static final String TOGGLE_END = "</span>";

    private static final String REPLY_START = "<div class=\"email-quoted-reply\">";

    private static final String REPLY_END = "</div>";

    private static final String SIGNATURE_START = "<div class=\"email-signature-reply\">";

    private static final String SIGNATURE_END = "</div>";

    private static final String HIDDEN_REPLY_START = "<div class=\"email-hidden-reply\" style=\" display:none\">";

    private static final String HIDDEN_REPLY_END = "</div>";

    private static final String BREAK = "<br>";

    private static final String PARAGRAPH_START = "<p>";

    private static final String PARAGRAPH_END = "</p>";

    //https://github.com/k0shk0sh/GitHubSdk/blob/master/library/src/main/java/com/meisolsson/githubsdk/core/HtmlUtils.java
    @NonNull public static CharSequence format(final String html) {
        if (html == null || html.length() == 0) return "";
        StringBuilder formatted = new StringBuilder(html);
        strip(formatted, TOGGLE_START, TOGGLE_END);
        strip(formatted, SIGNATURE_START, SIGNATURE_END);
        strip(formatted, REPLY_START, REPLY_END);
        strip(formatted, HIDDEN_REPLY_START, HIDDEN_REPLY_END);
        if (replace(formatted, PARAGRAPH_START, BREAK)) replace(formatted, PARAGRAPH_END, BREAK);
        trim(formatted);
        return formatted;
    }

    private static void strip(final StringBuilder input, final String prefix, final String suffix) {
        int start = input.indexOf(prefix);
        while (start != -1) {
            int end = input.indexOf(suffix, start + prefix.length());
            if (end == -1)
                end = input.length();
            input.delete(start, end + suffix.length());
            start = input.indexOf(prefix, start);
        }
    }

    private static boolean replace(final StringBuilder input, final String from, final String to) {
        int start = input.indexOf(from);
        if (start == -1) return false;
        final int fromLength = from.length();
        final int toLength = to.length();
        while (start != -1) {
            input.replace(start, start + fromLength, to);
            start = input.indexOf(from, start + toLength);
        }
        return true;
    }

    private static void trim(final StringBuilder input) {
        int length = input.length();
        int breakLength = BREAK.length();
        while (length > 0) {
            if (input.indexOf(BREAK) == 0) input.delete(0, breakLength);
            else if (length >= breakLength && input.lastIndexOf(BREAK) == length - breakLength) input.delete(length - breakLength, length);
            else if (Character.isWhitespace(input.charAt(0))) input.deleteCharAt(0);
            else if (Character.isWhitespace(input.charAt(length - 1))) input.deleteCharAt(length - 1);
            else break;
            length = input.length();
        }
    }
}
