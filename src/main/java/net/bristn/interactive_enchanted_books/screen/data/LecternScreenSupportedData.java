package net.bristn.interactive_enchanted_books.screen.data;

import java.util.ArrayList;
import java.util.List;

public record LecternScreenSupportedData(List<LecternScreenSupportedIconData> icons) {

    public static LecternScreenSupportedData EMPTY = new LecternScreenSupportedData(new ArrayList<>());
}
