package org.yang.common.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gxy on 2017/7/10
 */

public class StringUtils {
    public static boolean isMobileNO(String mobiles) {

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

    public static void setHintSize(TextView textView, int textSize) {
        SpannableString ss = new SpannableString(textView.getHint());
        // 新建一个属性对象,设置文字的大小
        int hintTextSize = (int) textView.getContext().getResources().getDimension(textSize);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(hintTextSize);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        textView.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
    }
}
