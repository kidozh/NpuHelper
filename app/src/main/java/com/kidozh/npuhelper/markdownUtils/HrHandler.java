package com.kidozh.npuhelper.markdownUtils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.spans.CenterSpan;

import org.htmlcleaner.TagNode;


/**
 * Created by kosh on 30/07/2017.
 */

public class HrHandler extends TagNodeHandler {

    private int color;
    private int width;
    private boolean isHeader;

    @Override public void handleTagNode(TagNode tagNode, SpannableStringBuilder spannableStringBuilder, int i, int i1) {
        spannableStringBuilder.append("\n");
        SpannableStringBuilder builder = new SpannableStringBuilder("$");
        HrSpan hrSpan = new HrSpan(color, width);
        builder.setSpan(hrSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new CenterSpan(), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("\n");
        spannableStringBuilder.append(builder);
    }

}
