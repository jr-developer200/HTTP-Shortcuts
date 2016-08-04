package ch.rmy.android.http_shortcuts.utils;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.EditText;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.rmy.android.http_shortcuts.realm.models.Variable;

public class VariableFormatter implements TextWatcher, Destroyable {

    private static final int FORMAT_COLOR = 0xFF3F51B5;

    private static final String REGEX = "\\{\\{[A-Za-z][A-Za-z0-9]*\\}\\}";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private final EditText editText;
    private final List<Variable> variables;

    public static Destroyable bind(EditText editText, List<Variable> variables) {
        VariableFormatter formatter = new VariableFormatter(editText, variables);
        editText.addTextChangedListener(formatter);
        return formatter;
    }

    private VariableFormatter(EditText editText, List<Variable> variables) {
        this.editText = editText;
        this.variables = variables;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        editText.removeTextChangedListener(this);
        clearFormatting(s);
        Matcher matcher = PATTERN.matcher(s);
        int previousEnd = 0;
        while (matcher.find()) {
            if (matcher.start() < previousEnd) {
                continue;
            }
            String variableName = s.subSequence(matcher.start() + 2, matcher.end() - 2).toString();
            if (isValidVariable(variableName)) {
                format(s, matcher.start(), matcher.end());
                previousEnd = matcher.end();
            }
        }
        editText.addTextChangedListener(this);
    }

    private boolean isValidVariable(String variableName) {
        for (Variable variable : variables) {
            if (variable.isValid() && variableName.equals(variable.getKey())) {
                return true;
            }
        }
        return false;
    }

    private void clearFormatting(Editable s) {
        for (Object span : s.getSpans(0, s.length() + 1, ForegroundColorSpan.class)) {
            s.removeSpan(span);
        }
        for (Object span : s.getSpans(0, s.length() + 1, StyleSpan.class)) {
            s.removeSpan(span);
        }
    }

    private void format(Editable s, int start, int end) {
        s.setSpan(new ForegroundColorSpan(FORMAT_COLOR), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void destroy() {
        editText.removeTextChangedListener(this);
    }
}
