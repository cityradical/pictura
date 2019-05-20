package com.cityradical.pictura.Pipeline;

import com.cityradical.pictura.Queue.ImageItem;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ReadInputFilePipeline implements Pipeline<String, ImageItem> {

    private static final String POISON_STRING = "POISON";

    private final Path inputFilePath;
    private final BlockingQueue<ImageItem> outputQueue;
    private final long numInputProducers;
    private final long numOutputConsumers;

    private BufferedReader bufferedReader;

    public ReadInputFilePipeline(
            Path inputFilePath,
            BlockingQueue<ImageItem> outputQueue,
            long numInputProducers,
            long numOutputConsumers
    ) {
        this.inputFilePath = inputFilePath;
        this.outputQueue = outputQueue;
        this.numInputProducers = numInputProducers;
        this.numOutputConsumers = numOutputConsumers;
        try {
            this.bufferedReader = new BufferedReader(new FileReader(inputFilePath.toFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize() {
        try {
            this.bufferedReader = new BufferedReader(new FileReader(inputFilePath.toFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanup() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ImageItem process(String input) {
        try {
            return new ImageItem(UUID.randomUUID(), new URL(input), null, null);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getFromInput() {
        try {
            var line = bufferedReader.readLine();
            if (line == null) {
                return POISON_STRING;
            }
            return line;
        } catch (IOException e) {
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
    public String getInputPoisonPill() {
        return POISON_STRING;
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
