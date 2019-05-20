package com.cityradical.pictura.Pipeline;

import com.cityradical.pictura.Queue.ImageItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.BlockingQueue;

public class DownloadImagePipeline implements Pipeline<ImageItem, ImageItem> {

    private final BlockingQueue<ImageItem> inputQueue;
    private final BlockingQueue<ImageItem> outputQueue;
    private final long numInputProducers;
    private final long numOutputConsumers;

    public DownloadImagePipeline(
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
        try {
            var path = Files.createTempFile(input.getId().toString(), ".tmp");
            try (var in = input.getUrl().openStream()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
            return new ImageItem(input.getId(), input.getUrl(), path, null);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
}
