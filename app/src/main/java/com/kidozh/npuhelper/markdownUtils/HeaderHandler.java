package com.kidozh.npuhelper.markdownUtils;

import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.spans.FontFamilySpan;
import org.htmlcleaner.TagNode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public final class HeaderHandler extends TagNodeHandler {
    private final float size;

    public void beforeChildren(@Nullable TagNode node, @Nullable SpannableStringBuilder builder) {
        this.appendNewLine(builder);
    }

    public void handleTagNode(@NonNull TagNode node, @NonNull SpannableStringBuilder builder, int start, int end) {
        //Intrinsics.checkParameterIsNotNull(node, "node");
        //Intrinsics.checkParameterIsNotNull(builder, "builder");
        builder.setSpan(new RelativeSizeSpan(this.size), start, end, 33);
        FontFamilySpan originalSpan = this.getFontFamilySpan(builder, start, end);
        FontFamilySpan boldSpan;
        if (originalSpan == null) {
            HtmlSpanner var10002 = this.getSpanner();
            //Intrinsics.checkExpressionValueIsNotNull(var10002, "this.spanner");
            boldSpan = new FontFamilySpan(var10002.getDefaultFont());
        } else {
            boldSpan = new FontFamilySpan(originalSpan.getFontFamily());
            boldSpan.setItalic(originalSpan.isItalic());
        }

        boldSpan.setBold(true);
        builder.setSpan(boldSpan, start, end, 33);
        this.appendNewLine(builder);
    }

    public final float getSize() {
        return this.size;
    }

    public HeaderHandler(float size) {
        this.size = size;
    }
}

