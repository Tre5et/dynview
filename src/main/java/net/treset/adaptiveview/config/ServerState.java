package net.treset.adaptiveview.config;

import java.util.List;

public record ServerState(
        int currentViewDistance,
        int currentSimDistance,
        int currentChunkTickingDistance,
        double mspt,
        double memory,
        List<String> players
) {}
