package spigey.asteroide.utils;

import spigey.asteroide.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    Map<String, Object> map;

    public Regex(Map<String, Object> placeholdermap){
        map = placeholdermap;
    }

    public String placeholder(String regex){
        Matcher matcher = Pattern.compile("\\[(.*?)]").matcher(regex);
        String reas = regex;
        while(matcher.find()){
            String[] split = matcher.group(1).split(" ");
            String shit = reas.replaceAll("\\[" + matcher.group(1) + "]", String.valueOf(map.get(split[0])));
            if (split.length == 1) reas = shit;
            else { // chatgpt told me to kms
                String calculatedValue = String.valueOf(Math.round(util.meth(replaceVariablesInExpression(matcher.group(1)))));
                reas = reas.replace("[" + matcher.group(1) + "]", calculatedValue);
            }
        }
        return reas;
    }

    private String replaceVariablesInExpression(String expression) { // so I did
        String[] split = expression.split(" ");
        StringBuilder replacedExpression = new StringBuilder();
        for (String part : split) {
            if (map.containsKey(part)) replacedExpression.append(map.get(part));
            else replacedExpression.append(part);
            replacedExpression.append(" ");
        }
        return replacedExpression.toString().trim();
    }
}
