package com.cityradical.pictura;

import com.cityradical.pictura.Pipeline.*;
import com.cityradical.pictura.Queue.ImageItem;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Parameters;

@Command(
        description = "Finds the top pixel color values in a list of images.",
        name = "pictura",
        mixinStandardHelpOptions = true)
public class Main implements Callable<Void> {

    @Parameters(index = "0", description = "A path to a file containing a list of image URLs.")
    private Path inputFilePath;

    @Parameters(index = "1", description = "A path to the output file to write the color data.")
    private Path outputFilePath;

    public static void main(String[] args) {
        CommandLine.call(new Main(), args);
    }

    @Override
    public Void call() throws Exception {
        var imageUrlQueue = new LinkedBlockingQueue<ImageItem>(4);
        var imageFileQueue = new LinkedBlockingQueue<ImageItem>(4);
        var imageResultQueue = new LinkedBlockingQueue<ImageItem>(4);

        var pipelines = List.of(
                new ReadInputFilePipeline(inputFilePath, imageUrlQueue, 1, 1),
                new DownloadImagePipeline(imageUrlQueue, imageFileQueue, 1, 1),
                new FindTopColorsPipeline(imageFileQueue, imageResultQueue, 1, 1),
                new WriteOutputFilePipeline(imageResultQueue, outputFilePath, 1, 1)
        );

        var service = Executors.newFixedThreadPool(5);

        pipelines.stream()
                .map(p -> new LoggingPipeline(p))
                .map(p -> new PipelineRunner(p))
                .forEach(p -> service.submit(p));

        service.shutdown();

        return null;
    }
}
