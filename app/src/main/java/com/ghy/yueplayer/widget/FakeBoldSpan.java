package com.ghy.yueplayer.widget;

import android.graphics.Paint;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

/**
 * @author HY
 * @date 2018/12/12
 * Desc: 加粗字体--不是很粗(BOLD)的那种
 * 实现--这几个字要加粗，但是不要太粗
 * <p>
 * SpannableStringBuilder spannableString = new SpannableStringBuilder();
 * spannableString.append(s);
 * spannableString.setSpan(new FakeBoldSpan(), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
 * tv.setText(spannableString);
 */
public class FakeBoldSpan extends CharacterStyle {

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setStyle(Paint.Style.FILL_AND_STROKE);
        tp.setStrokeWidth(0.6f);
    }
}
