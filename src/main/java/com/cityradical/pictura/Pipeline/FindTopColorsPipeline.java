package com.cityradical.pictura.Pipeline;

import com.cityradical.pictura.Queue.ImageItem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class FindTopColorsPipeline implements Pipeline<ImageItem, ImageItem> {

    private final BlockingQueue<ImageItem> inputQueue;
    private final BlockingQueue<ImageItem> outputQueue;
    private final long numInputProducers;
    private final long numOutputConsumers;

    public FindTopColorsPipeline(
            BlockingQueue<ImageItem> inputQueue,
            BlockingQueue<ImageItem> outputQueue,
            long numInputProducers,
            long numOutputConsumers
    ) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.numInputProducers = numInputProducers;
        this.numOutputConsumers = numOutputConsumers;
    }

    @Override
    public ImageItem process(ImageItem input) {
        BufferedImage image;
        try {
            image = ImageIO.read(input.getPath().toFile());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        var colorMap = getColorMap(image);
        var topThreeColors = getTopThreeColors(colorMap);
        return new ImageItem(input.getId(), input.getUrl(), input.getPath(), topThreeColors);
    }

    @Override
    public ImageItem getFromInput() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addToOutput(ImageItem output) {
        try {
            outputQueue.put(output);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ImageItem getInputPoisonPill() {
        return ImageItem.POISON_PILL;
    }

    @Override
    public long getNumInputProducers() {
        return numInputProducers;
    }

    @Override
    public ImageItem getOutputPoisonPill() {
        return ImageItem.POISON_PILL;
    }

    @Override
    public long getNumOutputConsumers() {
        return numOutputConsumers;
    }

    private static HashMap<Color, Integer> getColorMap(BufferedImage image) {
        var map = new HashMap<Color, Integer>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                var key = new Color(image.getRGB(x, y));
                map.put(key, map.getOrDefault(key, 0) + 1);
            }
        }
        return map;
    }

    private static List<Color> getTopThreeColors(HashMap<Color, Integer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }
}
