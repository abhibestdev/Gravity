package us.blockgame.gravity.util;

import net.md_5.bungee.api.ChatColor;

public class MinecraftStringUtil {

    public static String centerText(String text, int lineLength) {
        char[] chars = text.toCharArray();
        boolean isBold = false;
        double length = 0;
        ChatColor pholder = null;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '&' && chars.length != (i + 1) && (pholder = ChatColor.getByChar(chars[i + 1])) != null) {
                // we don't need to change the length...? it's already where we want it if we ignore the colors
                if (pholder != ChatColor.UNDERLINE && pholder != ChatColor.ITALIC // these 4 don't cancel bold
                        && pholder != ChatColor.STRIKETHROUGH && pholder != ChatColor.MAGIC) {
                    isBold = chars[i + 1] == 'l'; // true if the next is a bold modifier
                }
                i++; // we don't care about the next since it's a color init
            } else {
                length += 1;
                if (isBold) length += 0.1555555555555556;
            }
        }

        double spaces = (lineLength - length) / 2;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            builder.append(' ');
        }
        String copy = builder.toString(); // avoid insertions, it's kinda costly I believe
        builder.append(text).append(copy);

        return builder.toString();
    }
}
