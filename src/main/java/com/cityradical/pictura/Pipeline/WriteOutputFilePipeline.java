package com.cityradical.pictura.Pipeline;

import com.cityradical.pictura.Queue.ImageItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;

public class WriteOutputFilePipeline implements Pipeline<ImageItem, String> {

    private final BlockingQueue<ImageItem> inputQueue;
    private final Path outputFilePath;
    private final long numInputProducers;
    private final long numOutputConsumers;

    public WriteOutputFilePipeline(
            BlockingQueue<ImageItem> inputQueue,
            Path outputFilePath,
            long numInputProducers,
            long numOutputConsumers
    ) {
        this.inputQueue = inputQueue;
        this.outputFilePath = outputFilePath;
        this.numInputProducers = numInputProducers;
        this.numOutputConsumers = numOutputConsumers;
    }

    @Override
    public void initialize() {
        try {
            Files.deleteIfExists(outputFilePath);
            Files.createFile(outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String process(ImageItem input) {
        var sb = new StringBuilder(input.getUrl().toString());
        for (var color : input.getTopColors()) {
            sb.append(String.format(",#%06X", (0xFFFFFF & color.getRGB())));
        }
        sb.append("\n");
        return sb.toString();
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
    public void addToOutput(String output) {
        try {
            Files.writeString(outputFilePath, output, StandardOpenOption.APPEND);
        } catch (IOException e) {
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
    public String getOutputPoisonPill() {
        return "";
    }

    @Override
    public long getNumOutputConsumers() {
        return numOutputConsumers;
    }
}
