package com.cityradical.pictura.Queue;

import java.awt.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ImageItem {

    public static ImageItem POISON_PILL = new ImageItem(null, null, null, null);

    private final UUID id;
    private final URL url;
    private final Path path;
    private final List<Color> topColors;

    public ImageItem(UUID id, URL url, Path path, List<Color> topColors) {
        this.id = id;
        this.url = url;
        this.path = path;
        this.topColors = topColors;
    }

    public UUID getId() {
        return id;
    }

    public URL getUrl() {
        return url;
    }

    public Path getPath() {
        return path;
    }

    public List<Color> getTopColors() {
        return topColors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageItem that = (ImageItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "id=" + id +
                ", url=" + url +
                ", path=" + path +
                ", topColors=" + topColors +
                '}';
    }
}
