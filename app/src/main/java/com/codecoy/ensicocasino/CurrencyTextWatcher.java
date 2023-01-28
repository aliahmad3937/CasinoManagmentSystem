package com.codecoy.ensicocasino;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyTextWatcher implements TextWatcher {

    private EditText ed;
    private String lastText;
    private boolean bDel = false;
    private boolean bInsert = false;
    private int pos;

    public CurrencyTextWatcher(EditText ed) {
        this.ed = ed;
    }

    public static String getStringWithSeparator(long value) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        String f = formatter.format(value);
        return f;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        bDel = false;
        bInsert = false;
        if (before == 1 && count == 0) {
            bDel = true;
            pos = start;
        } else if (before == 0 && count == 1) {
            bInsert = true;
            pos = start;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        lastText = s.toString();
    }

    @Override
    public void afterTextChanged(Editable s) {
        ed.removeTextChangedListener(this);
        StringBuilder sb = new StringBuilder();
        String text = s.toString();
        for (int i = 0; i < text.length(); i++) {
            if ((text.charAt(i) >= 0x30 && text.charAt(i) <= 0x39) || text.charAt(i) == '.' || text.charAt(i) == ',')
                sb.append(text.charAt(i));
        }
        if (!sb.toString().equals(s.toString())) {
            bDel = bInsert = false;
        }
        String newText = getFormattedString(sb.toString());
        s.clear();
        s.append(newText);
        ed.addTextChangedListener(this);

        if (bDel) {
            int idx = pos;
            if (lastText.length() - 1 > newText.length())
                idx--; // if one , is removed
            if (idx < 0)
                idx = 0;
            ed.setSelection(idx);
        } else if (bInsert) {
            int idx = pos + 1;
            if (lastText.length() + 1 < newText.length())
                idx++; // if one , is added
            if (idx > newText.length())
                idx = newText.length();
            ed.setSelection(idx);
        }
    }

    private String getFormattedString(String text) {
        String res = "";
        try {
            String temp = text.replace(",", "");
            long part1;
            String part2 = "";
            int dotIndex = temp.indexOf(".");
            if (dotIndex >= 0) {
                part1 = Long.parseLong(temp.substring(0, dotIndex));
                if (dotIndex + 1 <= temp.length()) {
                    part2 = temp.substring(dotIndex + 1).trim().replace(".", "").replace(",", "");
                }
            } else
                part1 = Long.parseLong(temp);

            res = getStringWithSeparator(part1);
            if (part2.length() > 0)
                res += "." + part2;
            else if (dotIndex >= 0)
                res += ".";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }
}
