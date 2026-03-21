package net.bristn.lectern.screen.data;

import java.util.List;

public record LecternScreenPageData(LecternScreenSupportedData supported, String name, String level,
        List<String> descriptions, List<String> exclusive) {
}
